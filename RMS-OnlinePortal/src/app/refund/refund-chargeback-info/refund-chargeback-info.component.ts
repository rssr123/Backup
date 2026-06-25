import { HttpClient, HttpHeaders } from '@angular/common/http';
import { ChangeDetectorRef, Component } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { Router, ActivatedRoute } from '@angular/router';
import { NgbPaginationConfig } from '@ng-bootstrap/ng-bootstrap';
import { TranslateService } from '@ngx-translate/core';
import { AuthService } from 'src/app/services/auth.service';
import { GlobalService } from 'src/app/shared/global.service';
import { environment } from 'src/environments/environment';
import { RefundPTTOrderDetails, RefundPTTPaymentItemDetails, RefundPTTOnlinePaymentInfos, PGRcpt, RefundInfo, RefundHist, RefundForm } from 'src/app/models/refundptt-interface';
import { OTCCollectionReceiptingBankDraft, OTCCollectionReceiptingCheque, OTCCollectionReceiptingMoneyOrder, OTCCollectionReceiptingPymtItem, OTCHist, OTCPaymentModel, OTCPaymentDetails, OTCPaymentHeader, OTCRcpt, OTCEMV } from 'src/app/models/otc-collection-receipting.interface';
import { OTCBank } from 'src/app/models/otc-collection-returned-cheque.interface';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Location } from '@angular/common';

@Component({
  selector: 'app-refund-chargeback-info',
  templateUrl: './refund-chargeback-info.component.html',
  styleUrls: ['./refund-chargeback-info.component.scss']
})
export class RefundChargebackInfoComponent {
  page = environment.DefaultPage;
  itemsPerPage = environment.ItemPerPage;
  orn_no: String | null = null;
  txn_id: String | null = null;
  mtt_id: number | null = null;
  rms_type: String | null = null;
  orderstatus: String | null = null;
  remarks_msg: String | null = null;
  refund_cd: String | null = null;


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
  refundformModel: RefundForm[] = [];

  cashPayments: OTCPaymentDetails[] = [];
  chequePayments: OTCPaymentDetails[] = [];
  moneyOrderPayments: OTCPaymentDetails[] = [];
  bankDraftPayments: OTCPaymentDetails[] = [];
  totalGrossAmount: number = 0; // Variable to hold the total sum of gross amounts
  totalPGAmounts: number = 0;
  totalChequeAmount: number = 0;
  totalBDAmount: number = 0;
  totalMOAmount: number = 0;

  otcPaymentDetailsCashAmt: number | null = 0;
  paymentModel: OTCPaymentModel = {
    payer_email: '',
    pymt_mode: '',
    cash_amt: 0,
    // Initialize other fields here
  };

  isLoading: boolean = false;
  totalRecords: number = 0;
  onlinepaymentinfosection: boolean = false;
  otcpaymentinfosection: boolean = false

  BankInfoForm!: FormGroup;
  file_content: any;

  actionMapping: { [key: string]: string } = {
    "Pending Finance Admin": "Assign / Query to Finance Admin",
    "Pending SME": "Assign / Query to SME",
    "Pending BYM": "Approved by Finance Admin / SME",
    "Pending FSM/FHOD": "Approval by FSM/FHOD",
    "Pending DCEO": "Approval by DCEO",
    "Pending CEO": "Approval by CEO",
    "Refund Request": "Refund Request",
    "THRESHOLD ASSIGN": "Threshold Assign",
    "Pending RG": "Pending Refund Slip Generated ",
    "Job Pick Up": "Job Pick Up",
    "Pending Refund Slip Generated": "Approval by Threshold User Role",
    "Refund Slip Generated": "Refund Slip Generated",
    "Refund Email Sent": "Refund Email Sent",
    "Refund Bank Info Submitted": "Refund Bank Info Submitted",
    "Refund Rejected": "Refund Rejected",
    "Pending PG": "Assign / Query to PG",
    "Pending Refund Submitted": "Assign to PG approval",
    "Refund Submitted": "Refund Submitted",
    "Refund Info Update": "Refund Info Update",
    // Add more mappings as needed
  };

  showInsertAlert: boolean = false;
  alertMessage: string = '';
  alertClass: string = '';

  constructor(
    private http: HttpClient,
    config: NgbPaginationConfig,
    private router: Router,
    public dialog: MatDialog,
    private cd: ChangeDetectorRef,
    private route: ActivatedRoute,
    private translate: TranslateService,
    private globalService: GlobalService,
    private authService: AuthService,
    private fb: FormBuilder,
    private location: Location
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

    this.stateHistory();
    this.fetchRttform();
    this.fetchOrderInfo();
    this.fetchPaymentItems();
    this.fetchPaymentHeader();
    this.checkfetchRcpt();
    this.fetchOTCRcpt();
    this.fetchOnlineRcpt();
    this.fetchRefundInformation();
    this.fetchRefundHist();

  }


  fetchRttform(): void {
    this.isLoading = true;
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    const url = environment.apiUrl + '/api/refundapproval/v1/getrttform'; // API endpoint
    const requestBody = {
      i_orn_no: this.orn_no,
    };

    console.log(requestBody);
    this.http.post(url, requestBody, { headers }).subscribe(
      (response: any) => {
        if (response?.data && Array.isArray(response.data) && response.data.length > 0) {
          // Assign the API response to refundformModel
          this.refundformModel = response.data;

          // Check if all bank info fields are not null
          const bankInfo = this.refundformModel[0];
          const allBankInfoPresent = bankInfo.bankAccountNo != null &&
            bankInfo.bankHolderName != null &&
            bankInfo.bankAccountType != null &&
            bankInfo.identityType != null &&
            bankInfo.identityNumber != null;

          if (allBankInfoPresent) {
            // Patch only when all bank info is present
            this.BankInfoForm.patchValue({
              identityType: bankInfo.identityType,
              identityNumber: bankInfo.identityNumber,
              bankAccountNo: bankInfo.bankAccountNo,
              bankAccountName: bankInfo.bankHolderName,
              bankAccountType: bankInfo.bankAccountType,
              bankHolderName: bankInfo.bankHolderName,
              billingAddress1: bankInfo.billingAddress1,
              billingAddress2: bankInfo.billingAddress2,
              billingAddress3: bankInfo.billingAddress3,
              postcode: bankInfo.custPostcode,
              city: bankInfo.custCity,
              state: bankInfo.custState,
              recEmail: bankInfo.recEmail,
            });
            // Disable the form to prevent user modification
            this.BankInfoForm.disable();
          }
        }
        console.log(this.refundformModel);
        this.isLoading = false;
      },
      (error) => {
        console.error('Error fetching refund info', error);
        this.isLoading = false;
      }
    );
  }


  stateHistory() {
    this.mtt_id = history.state.mtt_id;
    this.orn_no = history.state.orn_no;
    this.txn_id = history.state.txn_id;
    this.rms_type = history.state.rms_type;
    this.orderstatus = history.state.order_status;
    // console.log(this.mtt_id);
    // console.log(this.orn_no);
    console.log(this.txn_id);
    // console.log(this.rms_type);
    // console.log(this.orderstatus);
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
      i_mtt_id: this.mtt_id,
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
    this.totalGrossAmount = this.paymentItems.reduce((sum, item) => sum + item.gross_amt, 0);
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

  async handleRequestRefund(): Promise<void> {
    this.isLoading = true;
    const selectedItems = this.paymentItems; // Grab all payment items directly

    if (selectedItems.length > 0) {

      const orn_no = this.orn_no; // Assuming `orn_no` is available in your component
      const txn_id = this.txn_id; // Assuming `txn_id` is available in your component
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

      const rcptDate: string | null = this.pgRCPTModel.length > 0 && this.pgRCPTModel[0].rcpt_dt
        ? new Date(this.pgRCPTModel[0].rcpt_dt).toISOString().slice(0, 19).replace('T', ' ')
        : null;

      const url = environment.apiUrl + '/api/refundl/v1/addrttwf';
      const headers = new HttpHeaders({
        Authorization: environment.authKey,
        'Content-Type': 'application/json',
      });



      const paymentItemDetails = selectedItems.map((item: any) => ({
        unit_fee: item.unit_fee,
        qty: item.qty,
        item_ref_no: item.item_ref_no,
        item_desc: item.item_desc,
        tax_pct: item.tax_pct,
        tax_amt: item.tax_amt,
        grant_cd: item.grant_cd,
        disc_amt: item.disc_amt,
        gross_amt: item.gross_amt,
      }));

      const body: any = {
        rcpt_no: this.pgRCPTModel.length > 0 ? this.pgRCPTModel[0].rcptNo : null,
        rcpt_date: rcptDate,
        orn_no: orn_no,
        txn_id: txn_id,
        refund_amt: this.totalGrossAmount,
        ent_no: this.orderInfo.length > 0 ? this.orderInfo[0].ent_no : null,
        ent_nm: this.orderInfo.length > 0 ? this.orderInfo[0].ent_nm : null,
        cust_email: payeremail,
        msg: null,
        sme_email: null,                //this is for SME user role assign
        assign_to: null,                  //this is for SME user role assign
        rtt_status: 'PFA',              // set for rtt_status
        refund_ty: 'CB',                // refund type
        refund_reason: null,            // set at Finance Admin side
        payment_item_details: paymentItemDetails,
      };

      console.log(body);

      try {
        const response = await this.http.post(url, body, { headers }).toPromise();
        console.log('Success response:', response);

        // Set the alert properties for a successful submission
        this.alertMessage = 'Refund request submitted successfully.';
        this.alertClass = 'alert alert-success PA-alert-box';
        this.showInsertAlert = true;

        // Optionally, redirect after a short delay or immediately
        // For example, to hide the alert after 3 seconds:
        setTimeout(() => {
          this.showInsertAlert = false;
          this.redirectToPaidTransactions();
        }, 5000);

      } catch (error) {
        console.error('Error:', error);

        // Set the alert properties for an error message
        this.alertMessage = 'An error occurred while submitting the refund request. Please try again.';
        this.alertClass = 'alert alert-danger PA-alert-box';
        this.showInsertAlert = true;

        // Optionally, hide the alert after a few seconds:
        setTimeout(() => {
          this.showInsertAlert = false;
        }, 10000);
      }
      this.isLoading = false;


    } else {
      alert('Please select at least one item for a refund or Fill in the bank info');
    }
  }

  redirectToPaidTransactions() {
    this.location.back();
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
          // If response.data exists and is not empty
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
            pickup_by: '-',
            msg: '-',
            total: null,
            rtt_wf_hist_id: null,
            assign_to: '-',
            modified_by: '-',
            modified_by_nm: '-',
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