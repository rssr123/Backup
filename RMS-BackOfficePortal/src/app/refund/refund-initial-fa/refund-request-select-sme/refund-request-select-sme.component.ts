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
import { OTCCollectionReceiptingBankDraft, OTCCollectionReceiptingCheque, OTCCollectionReceiptingMoneyOrder, OTCPaymentModel, OTCPaymentDetails, OTCPaymentHeader, OTCRcpt, OTCEMV } from 'src/app/core/models/otc-collection-receipting.interface';
import { OTCBank } from 'src/app/core/models/otc-collection-returned-cheque.interface';
import { Roles, UserRole } from 'src/app/core/models/entity';
import { Location } from '@angular/common';
import { FormBuilder, FormControl, FormGroup, NgForm, NgModel, Validators } from '@angular/forms';
import { ActionMappingService } from 'src/app/core/services/action-mapping.service';

@Component({
  selector: 'app-refund-request-select-sme',
  templateUrl: './refund-request-select-sme.component.html',
  styleUrls: ['./refund-request-select-sme.component.scss']
})
export class RefundRequestSelectSMEComponent {
  actionMapping!: { [key: string]: string };
  page = environment.DefaultPage;
  itemsPerPage = environment.ItemPerPage;
  orn_no: String | null = null;
  txn_id: String | null = null;
  mtt_id: number | null = null;
  rms_type: String | null = null;
  sme_nm: String | null = null;
  sme_email: String | null = null;
  refund_cd: String | null = null;
  remarks_msg: String | null = null;
  appeal_cnt: number | null = null;
  payeremail: String | null = null;
  modelData: any;
  showInsertAlert: boolean = false;
  alertMessage: string = '';
  alertClass: string = '';
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
  smeUserModel: Roles[] = [];
  refundCdModel: any[] = [];
  selectedName: string = '';
  selectedItems: any[] = [];
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
  refund_reason: string = '';
  file_content: any;
  rcpt_no: String | null = null;
  rcpt_date: Date | null = null;

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
    private location: Location,
    private authService: AuthService,
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
    //this.hardcodeRefundCd();
    this.fetchRefundCd();
    this.stateHistory();
    //this.fetchOrderInfo();
    this.fetchPaymentItems();
    this.fetchPaymentHeader();
    this.checkfetchRcpt();
    //this.fetchOTCRcpt();
    //this.fetchOnlineRcpt();
    this.fetchRefundInformation();
    this.fetchRefundHist();
    this.fetchSMEUser();

  }

  // hardcode for the refund code(will be remove once can get the refund code from the API)
  // hardcodeRefundCd() {
  //   this.refundCdModel = [
  //     { acc_cd: '001', name: 'Refund Code 1' },
  //     { acc_cd: '002', name: 'Refund Code 2' },
  //     { acc_cd: '003', name: 'Refund Code 3' },
  //   ];
  // }


  fetchRefundCd(): void {

    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    const url = environment.apiUrl + '/api/rac/v1/getrefundaccountcode';
    const Body: any = {
      i_page: "1",
      i_size: "1000",
    }
    this.http.post(url, Body, { headers }).subscribe(
      (response: any) => {
        if (response && response.data) { // Check if `data` exists in the response
          // Filter roles where `roleNmEn` contains "SME"
          const filteredCd = response.data.filter((refundcode: any) =>
            refundcode.acc_cd && refundcode.status_en === 'Active' && refundcode.acc_desc && refundcode.acc_desc.trim() !== ''
          );
          this.refundCdModel = filteredCd;
          console.log('refund code:', this.refundCdModel);
        } else {
          console.error('Unexpected response format:', response);
        }
      },
      error => {
        console.error('Error fetching roles:', error);
      }
    );
  }

  stateHistory() {
    this.selectedItems = history.state.selectedItems || [];
    this.mtt_id = history.state.mtt_id;
    this.orn_no = history.state.orn_no;
    this.txn_id = history.state.txn_id;
    this.rms_type = history.state.rms_type;
    this.orderInfo = history.state.orderInfo;
    this.payeremail = history.state.payeremail;
    console.log(this.selectedItems);
    console.log(this.mtt_id);
    // console.log(this.orn_no);
    // console.log(this.txn_id);
    // console.log(this.rms_type);
    console.log(this.payeremail);
  }





  async handleFormSubmit(form: NgForm) {

    if (form.invalid) {


      this.alertMessage = 'Unable to submit refund request. Please fill in all required fields.';
      this.alertClass = 'alert alert-danger PA-alert-box';
      this.showInsertAlert = true; ``

      // auto-hide after 3 seconds
      setTimeout(() => {
        this.showInsertAlert = false;
      }, 3000);

      return;

    } else {
      this.isLoading = true; // Show loading screen
      await this.submitRefundRequest();
      this.isLoading = false;
    }

  }


  async submitRefundRequest(): Promise<void> {

    const formattedRcptDate: string | null = this.rcpt_date
      ? new Date(this.rcpt_date)
        .toISOString()
        .slice(0, 19)
        .replace('T', ' ')
      : null;

    const url = environment.apiUrl + '/api/refundl/v1/addrttwf';
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    // define the refund type and the next rtt status here so param can set the i_assign_action

    const paymentItemDetails = this.selectedItems.map((item: any) => ({
      unit_fee: item.unit_fee,
      qty: item.qty,
      item_ref_no: item.item_ref_no,
      item_desc: item.item_desc,
      tax_pct: item.tax_pct,
      tax_amt: item.tax_amt,
      grant_cd: item.grant_cd,
      disc_amt: item.disc_amt,
      gross_amt: item.gross_amt,
      net_amt: item.net_amt,
      entity_type: item.entity_type,
      entity_no: item.entity_no,
      entity_nm: item.entity_nm,
    }));

    const body: any = {
      rcpt_no: this.rcpt_no,
      rcpt_date: formattedRcptDate,
      orn_no: this.orn_no, //'testin123',
      txn_id: this.txn_id,
      refund_amt: this.totalGrossAmount,
      ent_no: this.orderInfo.length > 0 ? this.orderInfo[0].ent_no : null,
      ent_nm: this.orderInfo.length > 0 ? this.orderInfo[0].ent_nm : null,
      cust_email: this.payeremail,
      sme_email: this.sme_email,     //this is for SME user role assign
      assign_to: this.sme_nm,        //this is for SME user role assign
      rtt_status: 'PSME',             // set for rtt_status
      refund_ty: 'RS02',              // set for the refund type
      msg: this.remarks_msg,
      refund_cd: this.refund_cd,
      payment_item_details: paymentItemDetails, // Attach payment items
      refund_reason: this.refund_reason,
    };
    console.log('Request body:', body);

    try {
        const response: any = await this.http.post(url, body, { headers }).toPromise();

      // Check the "data" field
      if (response.data === -1) {
        console.log('Success:', response);

        this.alertMessage = "Reference No. cannot be null or empty";
        this.alertClass = "alert alert-danger PA-alert-box";
        this.showInsertAlert = true;
        this.isLoading = false;

        // Optionally, hide the alert after a few seconds
        setTimeout(() => {
          this.showInsertAlert = false;
        }, 10000);
        return;
      }
       if (response.data === -2) {
        console.log('Success:', response);

        this.alertMessage = "Remarks are required for appeal refund requests.";
        this.alertClass = "alert alert-danger PA-alert-box";
        this.showInsertAlert = true;
        this.isLoading = false;

        // Optionally, hide the alert after a few seconds
        setTimeout(() => {
          this.showInsertAlert = false;
        }, 10000);
        return;
      }


      if (response.data === -3) {
        console.log('Success:', response);

        this.alertMessage = "This Reference No. cannot be submitted — it is either still processing or has already reached the maximum of 3 appeals.";
        this.alertClass = "alert alert-danger PA-alert-box";
        this.showInsertAlert = true;
        this.isLoading = false;

        // Optionally, hide the alert after a few seconds
        setTimeout(() => {
          this.showInsertAlert = false;
        }, 10000);
        return;
      }

       if (response.data > 0) {
      // Set the alert properties for a successful submission
      this.alertMessage = 'Refund request submitted successfully.';
      this.alertClass = 'alert alert-success PA-alert-box';
      this.showInsertAlert = true;
       }
      // Optionally, redirect after a short delay or immediately
      // For example, to hide the alert after 3 seconds:
      setTimeout(() => {
        this.showInsertAlert = false;
        this.redirectToPaidTransactions();
      }, 3000);

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
  }


  redirectToPaidTransactions() {
    this.router.navigate(['/paid-transaction-table']);
  }

  onSelectionChange(event: Event) {
    const selectedValue = (event.target as HTMLSelectElement).value;
    if (selectedValue) {
      const selectedItem = this.smeUserModel.find(item => item.roleNmEn === this.sme_nm);
      if (selectedItem) {
        this.sme_nm = selectedItem.roleNmEn; // Assign the name
        this.sme_email = ''; // Assign the email
        console.log('Assigned To:', this.sme_nm, 'SME Email:', this.sme_email);
      }
    }
  }
  onSelectionChange_refundcd(event: Event): void {
    const selectedValue = (event.target as HTMLSelectElement).value;
    this.refund_cd = selectedValue;
    console.log('Selected value:', selectedValue);
  }

  onSelectionChange_refundreason(event: Event): void {
    const selectedValue = (event.target as HTMLSelectElement).value;
    this.refund_reason = selectedValue;
    console.log('Selected value:', selectedValue);
  }

  fetchPaymentItems(): void {
    if (this.selectedItems && this.selectedItems.length > 0) {
      // Use the passed selectedItems instead of making an API call
      this.paymentItems = this.selectedItems;

      console.log('Payment Items from passed data:', this.paymentItems);
      this.calculateTotalGrossAmount();
      this.isLoading = false;
    } else {
      console.log('No selected items passed. Consider handling this case.');
      this.isLoading = false;
    }
  }

  calculateTotalGrossAmount(): void {
    this.totalGrossAmount = this.paymentItems.reduce((sum, item) => sum + item.net_amt, 0);
  }


  filterSelectedItems() {
    // Ensure you compare using a unique property (e.g., `id` or `txn_id`)
    const selectedItemIds = this.selectedItems.map(item => item.mtt_item_id);

    // Filter paymentItems to match selectedItems
    const filteredItems = this.paymentItems.filter(item => selectedItemIds.includes(item.mtt_item_id));

    console.log('Filtered Items:', filteredItems);

    // Use the filtered items as needed
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




  fetchSMEUser(): void {
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    const url = environment.apiUrl + '/api/rms/v1/getroles';
    const Body: any = {
      i_page: "1",
      i_size: "1000",
    }
    this.http.post(url, Body, { headers }).subscribe(
      (response: any) => {
        if (response && response.data) { // Check if `data` exists in the response
          // Filter roles where `roleNmEn` contains "SME"
          const filteredRoles = response.data.filter((role: any) =>
            role.roleNmEn && role.roleNmEn.includes("SME")
          );
          this.smeUserModel = filteredRoles;
        } else {
          console.error('Unexpected response format:', response);
        }
      },
      error => {
        console.error('Error fetching roles:', error);
      }
    );
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

  cancel(): void {
    this.location.back();
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

    const url = environment.apiUrl + '/api/refundl/v1/getRefundInfo';
    const requestBody = {
      i_txn_id: this.txn_id,
      i_orn_no: this.orn_no,
    };

    this.http.post(url, requestBody, { headers }).subscribe(
      (response: any) => {
        // 1) Populate refundInfoModel (or use a stub if empty)
        if (response?.data && Array.isArray(response.data) && response.data.length > 0) {
          this.refundInfoModel = response.data;
        } else {
          this.refundInfoModel = [{
            refund_slip_no: '-',
            requested_by: '-',
            dt_process: null,
            rtt_id: null,
            appeal_cnt: null,
            rtt_status: '-'
          }];
        }

        let maxAppealCnt: number | null = null;

        this.refundInfoModel.forEach((item: any, idx: number) => {
          console.log(`Item ${idx + 1} raw appeal_cnt =`, item.appeal_cnt);

          if (item.appeal_cnt != null) {
            const asNumber = Number(item.appeal_cnt);
            if (!isNaN(asNumber)) {
              // If we haven’t set max yet, or this is bigger than current max, update
              if (maxAppealCnt === null || asNumber > maxAppealCnt) {
                maxAppealCnt = asNumber;
              }
            }
          }
        });

        // Now maxAppealCnt is either null (no numeric seen) or 0 / 1 / 2 / …
        this.appeal_cnt = maxAppealCnt;
        console.log('Computed this.appeal_cnt =', this.appeal_cnt);
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
            modified_by_nm: '-', // Add the missing property
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
        console.log(this.otcPaymentDetailsCashAmt)

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
          this.rcpt_no = this.pgRCPTModel.length > 0 ? this.pgRCPTModel[0].rcptNo : null;
          this.rcpt_date = this.pgRCPTModel.length > 0 ? this.pgRCPTModel[0].rcpt_dt : null;

          // console.log(this.pgRCPTModel);
          console.log(this.rcpt_no);
          console.log(this.rcpt_date);
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
          this.rcpt_no = this.otcRcptModel.length > 0 ? this.otcRcptModel[0].rcptNo : null;
          this.rcpt_date = this.otcRcptModel.length > 0 ? this.otcRcptModel[0].rcpt_dt : null;
          //          console.log(this.otcRcptModel);
          console.log(this.rcpt_no);
          console.log(this.rcpt_date);
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