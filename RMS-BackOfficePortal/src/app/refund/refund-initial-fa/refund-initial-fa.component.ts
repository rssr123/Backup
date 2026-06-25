import { HttpClient, HttpHeaders } from '@angular/common/http';
import { ChangeDetectorRef, Component } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { Router, ActivatedRoute } from '@angular/router';
import { NgbPaginationConfig } from '@ng-bootstrap/ng-bootstrap';
import { TranslateService } from '@ngx-translate/core';
import { AuthService } from 'src/app/core/services/auth.service';
import { GlobalService } from 'src/app/shared/global.service';
import { environment } from 'src/environments/environment';
import { ParamService } from 'src/app/core/services/param.service';
import { RefundPTTOrderDetails, RefundPTTPaymentItemDetails, RefundPTTOnlinePaymentInfos, PGRcpt, RefundInfo, RefundHist } from 'src/app/core/models/refundptt-interface';
import { OTCCollectionReceiptingBankDraft, OTCCollectionReceiptingCheque, OTCCollectionReceiptingMoneyOrder, OTCCollectionReceiptingPymtItem, OTCHist, OTCPaymentModel, OTCPaymentDetails, OTCPaymentHeader, OTCRcpt, OTCEMV } from 'src/app/core/models/otc-collection-receipting.interface';
import { OTCBank } from 'src/app/core/models/otc-collection-returned-cheque.interface';
import { Location } from '@angular/common';
import { ActionMappingService } from 'src/app/core/services/action-mapping.service';

@Component({
  selector: 'app-refund-initial-fa',
  templateUrl: './refund-initial-fa.component.html',
  styleUrls: ['./refund-initial-fa.component.scss']
})
export class RefundInitialFAComponent {
  actionMapping!: { [key: string]: string };
  page = environment.DefaultPage;
  itemsPerPage = environment.ItemPerPage;
  orn_no: String | null = null;
  txn_id: String | null = null;
  mtt_id: number | null = null;
  rms_type: String | null = null;
  rtt_status: String | null = null;
  modelData: any;
  orderInfo: RefundPTTOrderDetails[] = [];
  paymentItems: RefundPTTPaymentItemDetails[] = [];
  onlinePaymentInfos: RefundPTTOnlinePaymentInfos[] = [];
  bankmodel: OTCBank[] = [];
  selectedChequeDt: Date[] | null = null;
  selectedBankDraftDt: Date[] | null = null;
  selectedMoneyOrderDt: Date[] | null = null;
  selectedPaymentMode: string = ''; // Tracks the selected payment mode
  chequeModel: OTCCollectionReceiptingCheque[] = [];
  isAddCheque: boolean = true;
  bankDraftModel: OTCCollectionReceiptingBankDraft[] = [];
  isAddBankDraft: boolean = true;
  moneyOrderModel: OTCCollectionReceiptingMoneyOrder[] = [];
  isAddMoneyOrder: boolean = true;
  otcRcptModel: OTCRcpt[] = [];
  otcEMVModel: OTCEMV[] = [];
  otcPaymentDetails: OTCPaymentDetails[] = [];
  otcPaymentHeader: OTCPaymentHeader[] = [];
  otcPayerEmail: String | null = null;
  otcPymtMode: String | null = null;
  onlinePayerEmail: String | null = null;
  pgRCPTModel: PGRcpt[] = [];
  refundInfoModel: RefundInfo[] = [];
  refundHistModel: RefundHist[] = [];

  cashPayments: OTCPaymentDetails[] = [];
  chequePayments: OTCPaymentDetails[] = [];
  moneyOrderPayments: OTCPaymentDetails[] = [];
  bankDraftPayments: OTCPaymentDetails[] = [];
  totalGrossAmount: number = 0; // Variable to hold the total sum of gross amounts
  totalPGAmounts: number = 0;
  totalChequeAmount: number = 0;
  totalBDAmount: number = 0;
  totalMOAmount: number = 0;
  file_content: any;
  showInsertAlert: boolean = false;
  alertMessage: string = '';
  alertClass: string = '';

  otcPaymentDetailsCashAmt: number | null = 0;
  paymentModel: OTCPaymentModel = {
    payer_email: '',
    pymt_mode: '',
    cash_amt: 0,
    // Initialize other fields here
  };
  searchEntityNo: string = '';
  isLoading: boolean = false;
  totalRecords: number = 0;
  onlinepaymentinfosection: boolean = false;
  otcpaymentinfosection: boolean = false;
  isRefundable: boolean = false;

  constructor(
    private http: HttpClient,
    config: NgbPaginationConfig,
    private router: Router,
    public dialog: MatDialog,
    private ParamService: ParamService,
    private cd: ChangeDetectorRef,
    private route: ActivatedRoute,
    private translate: TranslateService,
    private globalService: GlobalService,
    private authService: AuthService,
    private location: Location,
    private actionMappingService: ActionMappingService,
  ) {
    config.maxSize = environment.PaginationMaxSize;
    config.boundaryLinks = true;
    this.translate.setDefaultLang(this.globalService.getGlobalValue()); // default language
    this.translate.use(this.globalService.getGlobalValue());
    this.route.queryParams.subscribe(params => {
      this.orn_no = params['orn_no'];
    });

    const navigation = this.router.getCurrentNavigation();

  }

  ngOnInit() {
    this.actionMapping = this.actionMappingService.getMapping();
    this.stateHistory();
    this.fetchOrderInfo();
    this.fetchPaymentItems();
    this.fetchPaymentHeader();
    this.checkfetchRcpt();
    this.fetchOTCRcpt();
    this.fetchOnlineRcpt();
    this.fetchRefundInformation();
    this.fetchRefundHist();

  }

  stateHistory() {
    this.mtt_id = history.state.mtt_id;
    this.orn_no = history.state.orn_no;
    this.txn_id = history.state.txn_id;
    this.rms_type = history.state.rms_type;
    this.rtt_status = history.state.rtt_status;
    console.log(this.mtt_id);
    console.log(this.orn_no);
    console.log(this.txn_id);
    console.log(this.rms_type);
    console.log(this.rtt_status);
    if (this.rtt_status == null || this.rtt_status == undefined || this.rtt_status == '' || this.rtt_status == 'RR') {
      this.isRefundable = true;
    }
  }

  fetchOrderInfo(): void {
    this.isLoading = true;

    // Common headers for the request
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    // Declare the URL variable outside the conditional blocks
    let url: string;

    // Determine the API endpoint based on rms_type
    if (this.rms_type === 'Online') {
      url = environment.apiUrl + '/api/refundl/v1/getrefundoionline'; // API endpoint
    } else if (this.rms_type === 'OTC') {
      url = environment.apiUrl + '/api/refundl/v1/getrefundoiotc'; // API endpoint
    } else {
      console.log('rms_type not found');
      this.isLoading = false; // Stop loading if rms_type is invalid
      return; // Exit the method early
    }

    // Request body
    const requestBody = {
      i_mtt_id: this.mtt_id,
    };

    // Make the HTTP POST request
    this.http.post(url, requestBody, { headers }).subscribe(
      (response: any) => {
        this.orderInfo = response?.data || []; // Store the received data
        console.log(this.orderInfo);
        this.isLoading = false;
      },
      (error) => {
        console.error('Error fetching order info:', error);
        this.isLoading = false;
      }
    );
  }


  fetchPaymentItems(): void {
    this.isLoading = true;
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    const url = environment.apiUrl + '/api/refundl/v1/getpymtitems'; // API endpoint
    const requestBody = {
      i_mtt_id: this.mtt_id
    };

    this.http.post(url, requestBody, { headers }).subscribe(
      (response: any) => {
        this.paymentItems = response?.data || []; // Store the received data
        console.log(this.paymentItems);
        this.calculateTotalGrossAmount();
        this.isLoading = false;

      },
      (error) => {
        console.error('Error fetching payment items:', error);
        this.isLoading = false;
      }
    );
  }

  calculateTotalGrossAmount(): void {
    this.totalGrossAmount = this.paymentItems.reduce((sum, item) => sum + item.net_amt, 0);
  }

  toggleAllCheckboxes(event: Event): void {
    const isChecked = (event.target as HTMLInputElement).checked;
    this.paymentItems.forEach((item) => {
      item.isSelected = isChecked;
    });
  }

  checkIfAllSelected(): void {
    // If any item is not selected, uncheck the header checkbox
    const allSelected = this.paymentItems.every((item) => item.isSelected);
    if (!allSelected) {
      this.uncheckHeaderCheckbox();
    }
  }

  areAllSelected(): boolean {
    // Returns true if all checkboxes are selected
    return this.paymentItems.every((item) => item.isSelected);
  }

  private uncheckHeaderCheckbox(): void {
    const headerCheckbox = document.querySelector('thead input[type="checkbox"]') as HTMLInputElement;
    if (headerCheckbox) {
      headerCheckbox.checked = false;
    }
  }

  handleRequestRefund(): void {
    const selectedItems = this.paymentItems.filter((item) => item.isSelected);

    if (selectedItems.length > 0) {
      const rms_type = this.rms_type; // Assuming `rms_type` is available in your component
      const mtt_id = this.mtt_id; // Assuming `mtt_id` is available in your component
      const orn_no = this.orn_no; // Assuming `orn_no` is available in your component
      const txn_id = this.txn_id; // Assuming `txn_id` is available in your component
      const orderInfo = this.orderInfo; // Assuming `orderInfo` is available in your component
      let payeremail: string | null = null; // Initialize payeremail

      if (this.rms_type === 'Online') {
        payeremail = this.onlinePaymentInfos.length > 0
          ? this.onlinePaymentInfos[0].cust_email
          : null; // Assign cust_email from onlinePaymentInfos if available
      } else if (this.rms_type === 'OTC') {
        payeremail = this.otcPaymentHeader.length > 0
          ? this.otcPaymentHeader[0].payer_email
          : null; // Assign payer_email from otcPaymentHeader if available
      }

      // Navigate to the new page with all required data
      this.router.navigate(['/refund-request-select-sme'], {
        state: {
          selectedItems,
          mtt_id,
          orn_no,
          txn_id,
          rms_type,
          orderInfo,
          payeremail,
        },
      });
    } else {
      this.alertMessage = 'Please select at least one item for a refund.';
      this.alertClass = 'alert alert-danger PA-alert-box';
      this.showInsertAlert = true;

      // auto-hide after 3 seconds
      setTimeout(() => {
        this.showInsertAlert = false;
      }, 3000);
    }
  }


  fetchPaymentHeader(): void {
    this.isLoading = true;
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    if (this.rms_type === 'Online') {

      this.onlinepaymentinfosection = true;
      const url = environment.apiUrl + '/api/refundl/v1/getPGPaymentInfo'; // API endpoint
      const requestBody = {
        i_mtt_id: this.mtt_id,
        i_txn_id: this.txn_id,
      };
      this.http.post(url, requestBody, { headers }).subscribe(
        (response: any) => {
          this.onlinePaymentInfos = response?.data || []; // Store the received data
          this.onlinePayerEmail = this.onlinePaymentInfos.length > 0 ? this.onlinePaymentInfos[0].cust_email : '';
          this.isLoading = false;
          // console.log(this.onlinePaymentInfos);
          // console.log(this.onlinePayerEmail);
          this.calculateTotalPGAmount();
        },
        (error) => {
          console.error('Error fetching OTC Payment Details:', error);
          this.isLoading = false;
        }
      );


    } else if (this.rms_type === 'OTC') {

      this.otcpaymentinfosection = true;
      const url = environment.apiUrl + '/api/OTCCR/v1/getOTCPaymentHeader'; // API endpoint
      const requestBody = {
        i_mtt_id: this.mtt_id,
      };

      this.http.post(url, requestBody, { headers }).subscribe(
        (response: any) => {
          this.otcPaymentHeader = response?.data || []; // Store the received data
          this.otcPayerEmail = this.otcPaymentHeader.length > 0 ? this.otcPaymentHeader[0].payer_email : '';
          this.otcPymtMode = this.otcPaymentHeader.length > 0 ? this.otcPaymentHeader[0].otc_pymt_mode : '';

          this.isLoading = false;
        },
        (error) => {
          console.error('Error fetching OTC Payment Details:', error);
          this.isLoading = false;
        }
      );

      this.fetchOTCPaymentDetails();

      this.fetchOTCEMV();

    }

  }

  calculateTotalPGAmount(): void {
    this.totalPGAmounts = this.onlinePaymentInfos.reduce((sum, item) => sum + item.pg_payment_amt, 0);
  }





  fetchBanks(): void {
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    const url = environment.apiUrl + '/api/rms/v1/getbanks';

    const Body: any = {
    };

    this.http.post(url, Body, { headers }).subscribe(
      (response: any) => {
        this.bankmodel = response.data;
        this.totalRecords = response.data.length > 0 ? response.data[0].total : 0;
        this.isLoading = false;
      },
      (error) => {
        console.error(error);
        this.isLoading = false;
      }
    );

  }

  fetchOTCEMV(): void {
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    const url = environment.apiUrl + '/api/OTCCR/v1/getotcemv';

    const Body: any = {
      i_mtt_id: this.mtt_id,
    };

    this.http.post(url, Body, { headers }).subscribe(
      (response: any) => {
        // Ensure response.data is treated as an array
        if (response.data) {
          this.otcEMVModel = Array.isArray(response.data) ? response.data : [response.data];
        } else {
          this.otcEMVModel = [];
        }

        console.log(this.otcEMVModel);
        console.log(this.otcEMVModel.length);
        // this.totalRecords = this.otcEMVModel.length > 0 ? this.otcEMVModel[0].total || 0 : 0;
        this.isLoading = false;
      },
      (error) => {
        console.error('Error fetching EMV data:', error);
        this.isLoading = false;
      }
    );
  }


  addCheque() {
    this.chequeModel.push({
      otc_body_id: 0,
      che_bank_nm: '',
      che_no: '',
      che_payer_nm: '',
      che_date: new Date(),
      che_ba_acct_no: '',
      che_amt: 0,
      che_id: '',
      che_status: '',
      isEditable: true,
      isNew: true
    });
    this.isAddCheque = false;
  }

  saveChequeRow(index: number) {
    const row = this.chequeModel[index];
    if (
      !row.che_bank_nm ||
      !row.che_payer_nm ||
      !row.che_no ||
      !row.che_date ||
      !row.che_ba_acct_no ||
      !row.che_amt ||
      row.che_amt <= 0
    ) {
      alert("Please ensure all fields are filled and the amount is greater than 0.");
      return; // Exit without saving
    }

    this.chequeModel[index].isEditable = false;
    this.chequeModel[index].isNew = false;
    this.isAddCheque = true;
  }

  editChequeRow(index: number) {
    this.chequeModel[index].isEditable = true;
  }

  removeChequeNewRow(index: number) {
    this.chequeModel.splice(index, 1);
    this.isAddCheque = true;
  }

  deleteChequeRow(index: number) {
    this.chequeModel.splice(index, 1); // Remove the row from the array
    if (this.chequeModel.length === 0) {
      this.isAddCheque = true; // If all rows are deleted, show Add button
    }
  }

  addBankDraft() {
    this.bankDraftModel.push({
      bd_bank_nm: '',
      bd_no: '',
      bd_date: new Date(),
      bd_amt: 0,
      isEditable: true,
      isNew: true
    });
    this.isAddBankDraft = false;
  }

  saveBDRow(index: number) {
    const row = this.bankDraftModel[index];
    if (
      !row.bd_bank_nm ||
      !row.bd_no ||
      !row.bd_date ||
      !row.bd_amt ||
      row.bd_amt <= 0
    ) {
      alert("Please ensure all fields are filled and the amount is greater than 0.");
      return; // Exit without saving
    }
    this.bankDraftModel[index].isEditable = false;
    this.bankDraftModel[index].isNew = false;
    this.isAddBankDraft = true;
  }

  editBDRow(index: number) {
    this.bankDraftModel[index].isEditable = true;
  }

  removeBDNewRow(index: number) {
    this.bankDraftModel.splice(index, 1);
    this.isAddBankDraft = true;
  }

  deleteBDRow(index: number) {
    this.bankDraftModel.splice(index, 1); // Remove the row from the array
    if (this.bankDraftModel.length === 0) {
      this.isAddBankDraft = true; // If all rows are deleted, show Add button
    }
  }

  addMoneyOrder() {
    this.moneyOrderModel.push({
      mo_rm_no: '',
      mo_payer_nm: '',
      mo_id_no: '',
      mo_contact_no: '',
      mo_amt: 0,
      mo_date: new Date(),
      isEditable: true,
      isNew: true
    });
    this.isAddMoneyOrder = false;
  }

  saveMORow(index: number) {
    const row = this.moneyOrderModel[index];
    if (
      !row.mo_rm_no ||
      !row.mo_date ||
      !row.mo_payer_nm ||
      !row.mo_id_no ||
      !row.mo_contact_no ||
      !row.mo_amt ||
      row.mo_amt <= 0
    ) {
      alert("Please ensure all fields are filled and the amount is greater than 0.");
      return; // Exit without saving
    }
    this.moneyOrderModel[index].isEditable = false;
    this.moneyOrderModel[index].isNew = false;
    this.isAddMoneyOrder = true;
  }

  editMORow(index: number) {
    this.moneyOrderModel[index].isEditable = true;
  }

  removeMONewRow(index: number) {
    this.moneyOrderModel.splice(index, 1);
    this.isAddMoneyOrder = true;
  }

  deleteMORow(index: number) {
    this.moneyOrderModel.splice(index, 1); // Remove the row from the array
    if (this.moneyOrderModel.length === 0) {
      this.isAddMoneyOrder = true; // If all rows are deleted, show Add button
    }
  }


  fetchRefundInformation(): void {
    this.isLoading = true;
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    const url = environment.apiUrl + '/api/refundl/v1/getRefundInfo'; // API endpoint
    const requestBody = {
      i_txn_id: this.txn_id,
      i_orn_no: this.orn_no,
    };


    this.http.post(url, requestBody, { headers }).subscribe(
      (response: any) => {
        if (response?.data && Array.isArray(response.data) && response.data.length > 0) {
          // If response.data exists and is not empty
          this.refundInfoModel = response.data;
        } else {
          // If response.data is null or empty
          this.refundInfoModel = [{
            refund_slip_no: '-',
            requested_by: '-',
            dt_process: null,
            rtt_id: null,
            appeal_cnt: null,
            rtt_status: '-'
          }]; // Replace with appropriate fields
        }
        console.log(this.refundInfoModel);
        this.isLoading = false;
      },
      (error) => {
        console.error('Error fetching refund info', error);
        this.isLoading = false;
      }
    );
  }

  fetchRefundHist(): void {
    this.isLoading = true;
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    const url = environment.apiUrl + '/api/refundl/v1/getRefundHist'; // API endpoint
    const requestBody = {
      i_txn_id: this.txn_id,
      i_orn_no: this.orn_no,
    };

    this.http.post(url, requestBody, { headers }).subscribe(
      (response: any) => {
        if (response?.data && Array.isArray(response.data) && response.data.length > 0) {
          this.refundHistModel = response.data.map((item: RefundHist) => {
            return {
              ...item,
              action: this.mapAction(item.action), // Map the action to descriptive name
            };
          });
        } else {
          // If response.data is null or empty
          this.refundHistModel = [{
            action: '-',
            rtt_status: '-',
            dt_action: null,
            requested_by: '-',
            modified_by: '-',
            msg: '-',
            total: null,
            rtt_wf_hist_id: null,
            assign_to: '-',
            pickup_by: '-',
            modified_by_nm: '-',// Add the missing property
          }]; // Replace with appropriate fields
        }
        console.log(this.refundInfoModel);
        this.isLoading = false;
      },
      (error) => {
        console.error('Error fetching refund info', error);
        this.isLoading = false;
      }
    );
  }


  // Method to map actions to descriptive names
  mapAction(action: string | null): string {
    return action && this.actionMapping[action] ? this.actionMapping[action] : 'Unknown';
  }

  calculateTotalChequeAmount(): void {
    this.totalChequeAmount = this.chequePayments.reduce((sum, item) => sum + item.che_amt, 0);
  }

  calculateTotalBDAmount(): void {
    this.totalBDAmount = this.bankDraftPayments.reduce((sum, item) => sum + item.bd_amt, 0);
  }

  calculateTotalMOAmount(): void {
    this.totalMOAmount = this.moneyOrderPayments.reduce((sum, item) => sum + item.mo_amt, 0);
  }

  fetchOTCPaymentDetails(): void {
    this.isLoading = true;
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    const url = environment.apiUrl + '/api/OTCCR/v1/getOTCPaymentDetails'; // API endpoint
    const requestBody = {
      i_mtt_id: this.mtt_id,
    };

    this.http.post(url, requestBody, { headers }).subscribe(
      (response: any) => {
        this.otcPaymentDetails = response?.data || []; // Store the received data
        this.cashPayments = this.otcPaymentDetails.filter(item => item.cash_amt !== null);
        this.otcPaymentDetailsCashAmt = this.cashPayments.length > 0 ? this.cashPayments[0].cash_amt : 0;

        this.chequePayments = this.otcPaymentDetails.filter(item => item.che_amt !== null);
        this.moneyOrderPayments = this.otcPaymentDetails.filter(item => item.mo_amt !== null);
        this.bankDraftPayments = this.otcPaymentDetails.filter(item => item.bd_amt !== null);

        this.calculateTotalChequeAmount();
        this.calculateTotalBDAmount();
        this.calculateTotalMOAmount();
        this.isLoading = false;
      },
      (error) => {
        console.error('Error fetching OTC Payment Details:', error);
        this.isLoading = false;
      }
    );
  }

  back() {
    this.location.back();
  }

  checkfetchRcpt(): void {
    if (this.rms_type === 'OTC') {
      this.fetchOTCRcpt();
    } else if (this.rms_type === 'Online') {
      this.fetchOnlineRcpt();
    }
  }

  fetchOnlineRcpt(): void {
    if (this.rms_type === 'Online') {
      this.isLoading = true;
      const headers = new HttpHeaders({
        Authorization: environment.authKey,
        'Content-Type': 'application/json',
      });

      const url = environment.apiUrl + '/api/refundl/v1/getRefundPGRcpt'; // API endpoint
      const requestBody = {
        i_mtt_id: this.mtt_id,
      };

      this.http.post(url, requestBody, { headers }).subscribe(
        (response: any) => {
          this.pgRCPTModel = response?.data || []; // Store the received data
          console.log(this.pgRCPTModel);
          this.isLoading = false;
        },
        (error) => {
          console.error('Error fetching PG Rcpt:', error);
          this.isLoading = false;
        }
      );
    }
  }


  fetchOTCRcpt(): void {
    if (this.rms_type === 'OTC') {
      this.isLoading = true;
      const headers = new HttpHeaders({
        Authorization: environment.authKey,
        'Content-Type': 'application/json',
      });

      const url = environment.apiUrl + '/api/OTCCR/v1/getotccrrcpt'; // API endpoint
      const requestBody = {
        i_mtt_id: this.mtt_id,
      };

      this.http.post(url, requestBody, { headers }).subscribe(
        (response: any) => {
          this.otcRcptModel = response?.data || []; // Store the received data
          console.log(this.otcRcptModel);
          this.isLoading = false;
        },
        (error) => {
          console.error('Error fetching OTC Rcpt:', error);
          this.isLoading = false;
        }
      );
    }
  }

  downloadFile(fileName: string, verId: string, sourceSysDocRefID: string) {
    const requestBody = {
      refNo1: fileName,
      verID: verId,
      sourceSysDocRefID: sourceSysDocRefID,
    };

    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    const url = `${environment.apiUrl}/api/OTCCR/v1/downloadOTCRcpt`;

    this.http
      .post(url, requestBody, {
        observe: 'response',
        responseType: 'blob', // Expect binary content
        headers: headers,
      })
      .subscribe(
        (response) => {
          const contentDisposition = response.headers.get('content-disposition');
          let fileNameFromHeader = 'SSM-Receipt-' + fileName + '.pdf'; // Fallback name
          if (contentDisposition) {
            const match = contentDisposition.match(/filename="?(.+)"?/);
            if (match && match[1]) {
              fileNameFromHeader = match[1];
            }
          }

          const blob = new Blob([response.body as Blob], { type: 'application/pdf' });

          // Create a link to download the file
          const link = document.createElement('a');
          const objectUrl = URL.createObjectURL(blob);
          link.href = objectUrl;
          link.download = fileNameFromHeader || fileName;
          link.click();
          URL.revokeObjectURL(objectUrl);
        },
        (error) => {
          console.error('Error downloading file:', error);
        }
      );
  }

  dlRcpt(orn_no: any) {
    if (orn_no == null)
      return;

    const generateURL = environment.apiUrl + '/api/receipt/v1/dl_rcpt';
    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json'
    });

    // Create the request body with your form data
    var requestBody: { [k: string]: any } = {
      i_orn_no: this.pgRCPTModel[0].rcptNo,
      i_mtt_id: this.mtt_id
    };
    console.log(requestBody);

    // this.http.post(generateURL, requestBody, { observe: 'response', responseType: 'blob', headers: headers })
    //   .subscribe(response => {
    //     var blob = new Blob([response.body as Blob], { type: 'pdf' });
    //     saveAs(blob, response.headers.get('content-disposition')!.split('filename=')[1]);
    //   });

    this.http.post(generateURL, requestBody, { headers }).subscribe(
      (response: any) => {
        // console.log(response.data);
        this.file_content = response.data;
        if (this.file_content != null) {
          this.downloadFileContent(this.file_content, orn_no);
        }
        if (response.data.length == 0) {
          this.totalRecords = 0;
          //this.showResultAlertBox();
          this.isLoading = false;
        } else {
          this.totalRecords = response.data[0].total;
          //   this.DefaultBox();
          this.isLoading = false;
          // this.isDisplay = true;
        }
        // console.log(response.data);
        //  console.log(this.totalRecords);
      },
      (error) => {
        console.error(error);
        this.isLoading = false;
        //this.showGenericAlertBox();
      }
    );
    const filename = 'SSM-Receipt-' + orn_no + '.pdf';
  }

  downloadFileContent(fileContent: string, orn_no: string): void {
    this.isLoading = true;
    const binaryString = window.atob(fileContent);
    const len = binaryString.length;
    const uint8Array = new Uint8Array(len);
    for (let i = 0; i < len; i++) {
      uint8Array[i] = binaryString.charCodeAt(i);
    }
    const blob = new Blob([uint8Array], { type: 'application/pdf' });
    const url = URL.createObjectURL(blob);
    const anchor = document.createElement('a');
    anchor.href = url;
    const filename = 'SSM-Receipt-' + orn_no + '.pdf';
    anchor.download = filename;
    document.body.appendChild(anchor);
    anchor.click();
    document.body.removeChild(anchor);
    URL.revokeObjectURL(url);
  }


}