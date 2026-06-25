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
import { UserRole } from 'src/app/core/models/entity';
import { FormBuilder, FormControl, FormGroup, NgForm, NgModel, Validators } from '@angular/forms';
import { RefundApprovalTaskInfo, RefundRTTItems } from 'src/app/core/models/refundapproval-interface';
import { Location } from '@angular/common';
import { ActionMappingService } from 'src/app/core/services/action-mapping.service';



@Component({
  selector: 'app-refund-approval-userrole',
  templateUrl: './refund-approval-userrole.component.html',
  styleUrls: ['./refund-approval-userrole.component.scss']
})
export class RefundApprovalUserroleComponent {
  actionMapping!: { [key: string]: string };
  page = environment.DefaultPage;
  itemsPerPage = environment.ItemPerPage;
  orn_no: String | null = null;
  txn_id: String | null = null;
  mtt_id: number | null = null;
  rms_type: String | null = null;
  rtt_app_no: String | null = null;
  decision_status: String | null = null;
  sme_email: String | null = null;
  refund_cd: String | null = null;
  remarks_msg: String | null = null;
  appeal_cnt: number | null = null;
  payeremail: String | null = null;
  refund_ty: String | null = null;
  status_param_nm: String | null = null;
  rtt_wf_id: number | null = null;
  task_id: string | null = null;
  modelData: any;
  orderInfo: RefundPTTOrderDetails[] = [];
  rttItems: RefundRTTItems[] = [];
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
  smeUserModel: UserRole[] = [];
  refundApprovalTaskInfo: RefundApprovalTaskInfo[] = [];
  refundCdModel: any[] = [];
  file_content: any;

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
  returnTaskbutton: boolean = false;


  otcPaymentDetailsCashAmt: number | null = 0;
  paymentModel: OTCPaymentModel = {
    payer_email: '',
    pymt_mode: '',
    cash_amt: 0,
    // Initialize other fields here
  };

  refundcdsection: boolean = false;
  isLoading: boolean = false;
  totalRecords: number = 0;
  onlinepaymentinfosection: boolean = false;
  otcpaymentinfosection: boolean = false;
  decisiongroup: any[] = [];
  taskinfoRemarks: String | null = null;
  pickup_by: String | null = null;
  approved_by: String | null = null;

  //refund form one 
  RttForm: any;


  refundTypeMapping = [
    { type: 'RS01', label: 'Refund Slip 01' },
    { type: 'RS02', label: 'Refund Slip 02' },
    { type: 'DA', label: 'Direct Refund Application' },
    { type: 'CB', label: 'Charge Back' },
    { type: 'RF', label: 'Refund Form' },
  ];

  showInsertAlert: boolean = false;
  alertMessage: string = '';
  alertClass: string = '';

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
    // this.hardcodeRefundCd();  this not needed
    //this.hardcodeAfterClickViewInMytasklist();
    this.stateHistory();
    this.fetchSMEUser();

  }

  // hardcode for the refund code(will be remove once can get the refund code from the API)
  hardcodeRefundCd() {
    this.refundCdModel = [
      { acc_cd: '001', name: 'Refund Code 1' },
      { acc_cd: '002', name: 'Refund Code 2' },
      { acc_cd: '003', name: 'Refund Code 3' },
    ];
  }

  hardcodeAfterClickViewInMytasklist() {
    this.rtt_wf_id = 468;
    this.task_id = 'T20250110000001';
  }

  // Method to get the label based on refund_ty
  getRefundLabel(refund_ty: string): string {
    const match = this.refundTypeMapping.find(item => item.type === refund_ty);
    return match ? match.label : 'Unknown Refund Type';
  }


  decisionGroup() {
    if (this.refund_ty === 'RS01' || this.refund_ty === 'RS02') {
      this.decisiongroup = [
        { nm: 'Query to Finance Admin', status: 'PFA' },
        { nm: 'Approve', status: 'PRG' },
        { nm: 'Reject', status: 'RR' },
      ];
    } else if (this.refund_ty === 'DA' || this.refund_ty === 'CB') {
      this.decisiongroup = [
        { nm: 'Query to Finance Admin', status: 'PFA' },
        { nm: 'Approve', status: 'RS' },
        { nm: 'Reject', status: 'RR' },
      ];
    }


  }

  stateHistory() {
    this.rtt_app_no = history.state.rtt_app_no;
    this.task_id = history.state.task_id;

    // Set loading state
    this.isLoading = true;

    // Define HTTP headers
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    // Define API endpoint & request body
    const url = `${environment.apiUrl}/api/refundl/v1/getrttwfid`;
    const requestBody = {
      i_rtt_app_no: this.rtt_app_no,
    };

    // Make HTTP POST request
    this.http.post(url, requestBody, { headers }).subscribe(
      (response: any) => {
        this.rtt_wf_id = response.data[0].rtt_wf_id;
        this.fetchTaskInfo();
      },

      (error) => {
        console.error('Error fetching rttwfid:', error);
        this.isLoading = false;
      }
    );
  }





  async handleFormSubmit(form: NgForm) {

    if (form.invalid) {

      this.alertMessage = 'Unable to complete refund approval. Please fill in all required fields.';
      this.alertClass = 'alert alert-danger PA-alert-box';
      this.showInsertAlert = true;

      // auto-hide after 3 seconds
      setTimeout(() => {
        this.showInsertAlert = false;
      }, 1000);
      return;
    } else {
      this.isLoading = true; // Show loading screen
      await this.submitRefundRequest();
      this.isLoading = false;
    }

  }


  async submitRefundRequest(): Promise<void> {
    this.isLoading = true;

    const url = environment.apiUrl + '/api/refundapproval/v1/updaterttwfstatus';
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    if (this.decision_status !== 'PFA') {
      this.pickup_by = null; // Reset pickup_by if decision_status is not PFA
    }

    const body: any = {
      i_rtt_wf_id: this.rtt_wf_id,
      i_rtt_status: this.decision_status,
      i_msg: this.remarks_msg,
      i_refund_cd: this.refund_cd,
      i_pickup_by: this.pickup_by,
      i_refund_amt: this.totalGrossAmount,
      i_refund_type: this.refund_ty,
    };

    console.log('Submitting refund request with body:', body);

    if (this.refund_ty === 'DA' && this.decision_status === 'RS') {
      const formData = this.RttForm; // Assuming RttForm is already populated with the necessary data
      console.log('RttForm data:', formData);
      // ============================================
      // 1. Call FMS API to insert data into FMS DB
      // ============================================
      const fmsUrl = environment.apiUrl + '/api/fmsapia/insertfmsapia';
      const fmsBody: any = {

        // For the Vendor 
        ext_sys: 'RMS',
        vendor_id: null,
        vendor_nm: formData.bankHolderName, // from the BankInfoForm
        id_ty: formData.identityType,              // from the BankInfoForm
        id_no: formData.identityNumber,            // from the BankInfoForm
        pm: this.rms_type,
        p_desc: 'Beneficiary Account No',
        p_id: '1',
        p_bankname: formData.bankAccountName, // from the BankInfoForm
        p_value: formData.bankAccountNo, // from the BankInfoForm
        addr1: formData.billingAddress1, // from the BankInfoForm
        addr2: formData.billingAddress2, // from the BankInfoForm
        addr3: formData.billingAddress3, // from the BankInfoForm
        city: formData.custCity,             // from the BankInfoForm
        country: 'MY',
        state: formData.custState,           // from the BankInfoForm
        postcode: formData.custPostcode,     // from the BankInfoForm
        email: formData.recEmail,        // from the BankInfoForm
        phone_no: formData.phone_no,  // from the BankInfoForm

        //for the IH
        rtt_app_no: this.rtt_app_no,       // assumed available from orderInfo           
        refund_slip_no: formData.refund_slip_no, // assumed available from orderInfo
        refund_total_amt: this.totalGrossAmount, // assumed available from orderInfo

        //for the IH_details
        payment_item_details: this.rttItems

      };
      console.log('FMS API request body:', fmsBody);
      // return;
      try {

        // Make the API call
        const fmsResponse = await this.http
          .post<number>(fmsUrl, fmsBody, { headers })
          .toPromise();
        console.log('FMS API response:', fmsResponse);

        // Check the response value
        if (typeof fmsResponse === 'number' && fmsResponse > 0) {
          console.log('FMS API insertion successful.');
        } else {
          console.error('FMS API returned an error:', fmsResponse);
          alert('An error occurred while inserting data into FMS. Please try again.');
          this.isLoading = false;   // stop loading spinner
          return;                   // ← bail out here
        }

      } catch (fmsError) {
        console.error('FMS API error:', fmsError);
        alert('Network error during FMS insert. Please check your connection.');
        this.isLoading = false;     // stop loading spinner
        return;                     // ← and here
      }

    }



    // 1️⃣ Call the first API and subscribe to its response
    this.http.post(url, body, { headers }).subscribe({
      next: (response: any) => {
        console.log('Success response from UpdateRTTStatus:', response);

        // 2️⃣ Check if the update was successful and status is PRG
        if (response?.data === 1 && this.decision_status === 'PRG' &&
          (this.refund_ty === 'RS01' || this.refund_ty === 'RS02')) {

          console.log("RTT status successfully updated to PRG. Triggering PDFSlipGenerator...");

          // 3️⃣ Call the PDF API after the first API completes
          this.triggerPDFSlipGenerator();
        } else {
          console.warn("RTT status is not PRG or refund_ty is not RS01/RS02. Skipping PDFSlipGenerator API call.");
        }

        // 4️⃣ Show success alert & redirect
        this.alertMessage = 'Refund approval submit successfully.';
        this.alertClass = 'alert alert-success PA-alert-box';
        this.showInsertAlert = true;

        setTimeout(() => {
          this.showInsertAlert = false;
          this.redirectToPaidTransactions();
        }, 3000);
      },
      error: (error) => {
        console.error('Error submitting refund approval:', error);

        // Set error alert
        this.alertMessage = 'An error occurred while submitting the refund approval. Please try again.';
        this.alertClass = 'alert alert-danger PA-alert-box';
        this.showInsertAlert = true;

        setTimeout(() => {
          this.showInsertAlert = false;
        }, 10000);
      }
    });
  }



  /**
     * Triggers the PDF Slip Generator API if conditions are met.
     */
  async triggerPDFSlipGenerator(): Promise<void> {
    const pdfUrl = environment.apiUrl + '/api/refund/v1/PDFSlipGenerator';
    const pdfHeaders = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    const pdfBody = {
      rttWfId: this.rtt_wf_id,
    };

    console.log('Triggering PDFSlipGenerator with:', pdfBody);

    try {
      const pdfResponse = await this.http.post(pdfUrl, pdfBody, { headers: pdfHeaders }).toPromise();
      console.log('PDFSlipGenerator response:', pdfResponse);
    } catch (pdfError) {
      console.error('Error triggering PDFSlipGenerator:', pdfError);
    }
  }


  redirectToPaidTransactions() {
    this.location.back();
  }

  onSelectionChange(event: Event): void {
    const selectedValue = (event.target as HTMLSelectElement).value;
    this.decision_status = selectedValue;

  }

  onSelectionChange_refundcd(event: Event): void {
    const selectedValue = (event.target as HTMLSelectElement).value;
    this.refund_cd = selectedValue;
    // console.log('Selected value:', selectedValue);
  }

  fetchRTTItems(): void {
    this.isLoading = true;
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    const url = environment.apiUrl + '/api/refundapproval/v1/getrttitems'; // API endpoint
    const requestBody = {
      i_rtt_wf_id: this.rtt_wf_id,
    };

    this.http.post(url, requestBody, { headers }).subscribe(
      (response: any) => {
        this.rttItems = response?.data || []; // Store the received data
        console.log(this.rttItems);
        this.isLoading = false;
        this.totalGrossAmount = this.rttItems.length > 0 ? this.rttItems[0].total_refund_amt : 0;
      },
      (error) => {
        console.error('Error fetching payment items:', error);
        this.isLoading = false;
      }
    );

    this.calculateTotalGrossAmount();
  }

  calculateTotalGrossAmount(): void {
    this.totalGrossAmount = this.rttItems.reduce((sum, item) => sum + item.refund_amt, 0);

  }

  fetchTaskInfo(): void {
    this.isLoading = true;
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    const url = environment.apiUrl + '/api/refundapproval/v1/getrefundapprovalinfo';
    const Body: any = {
      i_rtt_wf_id: this.rtt_wf_id,
    }
    this.http.post(url, Body, { headers }).subscribe(
      (response: any) => {

        this.refundApprovalTaskInfo = response?.data || []; // Store the received data
        this.mtt_id = this.refundApprovalTaskInfo.length > 0 ? this.refundApprovalTaskInfo[0].mtt_id : null;
        this.rms_type = this.refundApprovalTaskInfo.length > 0 ? this.refundApprovalTaskInfo[0].rms_tpye : '';
        this.orn_no = this.refundApprovalTaskInfo.length > 0 ? this.refundApprovalTaskInfo[0].orn_no : '';
        this.refund_cd = this.refundApprovalTaskInfo.length > 0 ? this.refundApprovalTaskInfo[0].refund_cd : '';
        this.refund_ty = this.refundApprovalTaskInfo.length > 0 ? this.refundApprovalTaskInfo[0].refund_ty : '';
        this.status_param_nm = this.refundApprovalTaskInfo.length > 0 ? this.refundApprovalTaskInfo[0].status_param_nm : '';
        // console.log('approval task info: ', this.refundApprovalTaskInfo);
        // console.log('orn_no: ', this.orn_no);
        // execute here because for get the mtt_id, rms_type, orn_no
        this.fetchRefundHist();
        this.decisionGroup();
        this.fetchOrderInfo();
        this.fetchRTTItems();
        this.checkfetchRcpt();
        this.fetchOTCRcpt();
        this.fetchOnlineRcpt();
        this.fetchRefundInformation();
        this.isLoading = false;
      },
      (error) => {
        console.error(error);
        this.isLoading = false;
      }
    );
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
      //this.isLoading = false; // Stop loading if rms_type is invalid
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
        this.txn_id = this.orderInfo.length > 0 ? this.orderInfo[0].txn_id : '';
        // Wait for txn_id to have a value before calling fetchPaymentHeader
        if (this.txn_id) {
          this.fetchPaymentHeader();
        } else {
          // Poll until txn_id is set, then call fetchPaymentHeader
          const interval = setInterval(() => {
            if (this.txn_id) {
              clearInterval(interval);
              this.fetchPaymentHeader();
            }
          }, 100); // check every 100ms
        }
        this.isLoading = false;
      },
      (error) => {
        console.error('Error fetching order info:', error);
        this.isLoading = false;
      }
    );
  }


  fetchSMEUser(): void {
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    const url = environment.apiUrl + '/api/UR/v1/getuserrole';
    const Body: any = {
      i_page: "1",
      i_size: "1000",
      i_user: "SME"
    }
    this.http.post(url, Body, { headers }).subscribe(
      (response: any) => {
        this.smeUserModel = response.data;
        console.log(this.smeUserModel);
        // this.isLoading = false;
      },
      (error) => {
        console.error(error);
        this.isLoading = false;
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
          //  this.isLoading = false;
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

          //this.isLoading = false;
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


  // Fetch customer and bank information
  fetchRttform(): void {
    this.isLoading = true;
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    const url = environment.apiUrl + '/api/refundapproval/v1/getrttform';
    console.log('Fetching RTT form with ORN:', this.orn_no);
    const requestBody = {
      i_orn_no: this.orn_no,
    };

    this.http.post(url, requestBody, { headers }).subscribe(
      (response: any) => {
        if (response?.data && Array.isArray(response.data) && response.data.length > 0) {
          this.RttForm = response.data[0]; // Assuming the first object is the desired one
          console.log('Bank information:', this.RttForm);
        }
      },
      (error) => {
        console.error('Error fetching refund info:', error);
        this.isLoading = false;
      }
    );
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
        // this.isLoading = false;
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
          console.log(this.refundInfoModel);
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

        if (this.refund_ty === 'DA') {
          this.fetchRttform();
        }

        console.log(this.refundInfoModel);
        // Check and process appeal_cnt values
        this.refundInfoModel.forEach((item: any, index: number) => {
          const appealCntDisplay = item.appeal_cnt === null ? 'null' : item.appeal_cnt;
          console.log(`Item ${index + 1} - appeal_cnt:`, appealCntDisplay);
        });

        // Find the max appeal_cnt (ignoring nulls)
        const maxAppealCnt = this.refundInfoModel
          .filter((item: any) => item.appeal_cnt !== null) // Exclude null values for max calculation
          .reduce((max: number, item: any) => {
            return item.appeal_cnt > max ? item.appeal_cnt : max;
          }, 0); // Start with 0 as the initial max value

        this.appeal_cnt = maxAppealCnt === 0 ? null : maxAppealCnt;
        console.log(this.appeal_cnt);

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

          console.log(this.refundHistModel);
          // Find the `modified_by` value where action is "Approved"
          const approvedActions = this.refundHistModel.filter(
            (item) => item.action === 'Approved by Finance Admin / SME'
          );

          // Find the latest 'Remarks' field in the refundHistModel
          for (let i = 0; i < this.refundHistModel.length; i++) {
            const msg = this.refundHistModel[i].msg;
            if (!msg || msg === '-') {
              // If msg is null, empty, or '-', skip this iteration
              continue;
            }
            // If a valid message is found, assign it and break the loop.
            this.taskinfoRemarks = msg;
            break;
          }

          if (approvedActions.length > 0) {
            // Get the first `modified_by` value from the approved actions
            const modifiedByForApproved = approvedActions[0].modified_by_nm;

            // Store the result in a variable for use elsewhere
            this.approved_by = modifiedByForApproved;
            console.log('Modified by for Approved Action:', this.approved_by); ``

          } else {
            console.log('No Approved actions found in the history.');
          }


          // Function to find the first non-matching `modified_by` value
          const findPFAUsername = (data: any[]): any => {
            if (!data || data.length === 0) return null; // Handle empty or invalid data

            // Store the PFA username in modified_by for RS02 

            if (this.refund_ty === 'RS02') {
              for (let i = 0; i < data.length; i++) {
                // Check if the current record has the desired action
                if (data[i].action === 'Job Pick Up' && data[i].rtt_status === 'Pending Finance Admin') {
                  const current = data[i].modified_by;
                  // Found the Refund Request row, return the modified_by value
                  return current;
                }

                if (data[i].action === 'Refund Request' && data[i].rtt_status === 'Pending SME') {
                  const current = data[i].modified_by;
                  // Found the Refund Request row, return the modified_by value
                  return current;
                }
              }
            }

            console.log(data);

            // Store the PFA username in modified_by for RS01 || DA || CB || RF
            if (this.refund_ty === 'RS01' || this.refund_ty === 'DA' || this.refund_ty === 'CB') {
              for (let i = 0; i < data.length; i++) {
                // Check if the current record has the desired action
                if (data[i].action == 'Job Pick Up' && data[i].rtt_status === 'Pending Finance Admin') {
                  const current = data[i].modified_by;
                  // Found the Refund Request row, return the modified_by value
                  return current;
                }
                continue; // Continue to the next iteration if the condition is not met
              }
            }
          }

          // Call the function with the response data
          const PFAUsername = findPFAUsername(this.refundHistModel);

          this.pickup_by = PFAUsername;
          console.log('PFA - pick up username', this.pickup_by);

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
        console.log(this.taskinfoRemarks);
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
        // this.isLoading = false;
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
          //this.isLoading = false;
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
          //   this.isLoading = false;
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

  returnTask(): void {
    this.isLoading = true;

    const url = environment.apiUrl + '/api/refundapproval/v1/updaterttwfreturntask';
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json'
    });

    const currentRttStatus = this.refundApprovalTaskInfo[0].rtt_status;
    let navigateURL: any;
    if (currentRttStatus === 'PFA') {
      navigateURL = '/my-task-public-task/fa';
    } else if (currentRttStatus === 'T1' || currentRttStatus === 'T2' || currentRttStatus === 'T3') {
      navigateURL = '/my-task-public-task/bym';
    } else if (currentRttStatus === 'PSME') {
      navigateURL = '/my-task-public-task/sme';
    }

    const body = {
      i_rtt_wf_id: this.rtt_wf_id,
    };

    this.http.post(url, body, { headers }).subscribe(
      (response: any) => {
        console.log('Response:', response);
        this.isLoading = false;
        // Handle success response here

        this.router.navigateByUrl(navigateURL); // Navigate to the desired URL after successful return

        // For example, you can show a success message or redirect the user
      },
      (error) => {
        console.error('Error returning task:', error);
        this.isLoading = false;
        // Handle error response here
      }
    );
  }
}