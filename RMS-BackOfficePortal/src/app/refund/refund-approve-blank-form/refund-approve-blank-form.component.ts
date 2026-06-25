import { HttpClient, HttpHeaders } from '@angular/common/http';
import { ChangeDetectorRef, Component } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { Router, ActivatedRoute } from '@angular/router';
import { NgbPaginationConfig } from '@ng-bootstrap/ng-bootstrap';
import { TranslateService } from '@ngx-translate/core';
import { AuthService } from 'src/app/core/services/auth.service';
import { ActionMappingService } from 'src/app/core/services/action-mapping.service';
import { GlobalService } from 'src/app/shared/global.service';
import { environment } from 'src/environments/environment';
import { ParamService } from 'src/app/core/services/param.service';
import { RefundPTTOrderDetails, RefundPTTPaymentItemDetails, RefundPTTOnlinePaymentInfos, PGRcpt, RefundInfo, RefundHist, RefundForm } from 'src/app/core/models/refundptt-interface';
import { OTCCollectionReceiptingBankDraft, OTCCollectionReceiptingCheque, OTCCollectionReceiptingMoneyOrder, OTCPaymentModel, OTCPaymentDetails, OTCPaymentHeader, OTCRcpt, OTCEMV } from 'src/app/core/models/otc-collection-receipting.interface';
import { OTCBank } from 'src/app/core/models/otc-collection-returned-cheque.interface';
import { Roles, UserRole } from 'src/app/core/models/entity';
import { AbstractControl, ValidationErrors, FormBuilder, FormControl, FormGroup, NgForm, NgModel, Validators, FormArray } from '@angular/forms';
import { RefundApprovalTaskInfo, RefundRTTItems } from 'src/app/core/models/refundapproval-interface';
import { Location } from '@angular/common';
import { PostCodeData } from 'src/app/core/models/postcode.interface';
import { identity } from 'rxjs';

@Component({
  selector: 'app-refund-approve-blank-form',
  templateUrl: './refund-approve-blank-form.component.html',
  styleUrls: ['./refund-approve-blank-form.component.scss']
})
export class RefundApproveBlankFormComponent {
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
  sme_nm: String | null = null;
  refund_cd: String | null = null;
  refund_ty: String | null = null;
  status_param_nm: String | null = null;
  remarks_msg: String | null = null;
  appeal_cnt: number | null = null;
  payeremail: String | null = null;
  rtt_wf_id: number | null = null;
  rtt_wf_hist_id: number | null = null;
  task_id: string | null = null;
  modelData: any;
  orderInfo: RefundPTTOrderDetails[] = [];
  rttItems: RefundRTTItems[] = [];
  bankmodel: OTCBank[] = [];
  onlinePayerEmail: String | null = null;
  pgRCPTModel: PGRcpt[] = [];
  refundInfoModel: RefundInfo[] = [];
  refundHistModel: RefundHist[] = [];
  smeUserModel: Roles[] = [];
  refundApprovalTaskInfo: RefundApprovalTaskInfo[] = [];
  refundCdModel: any[] = [];
  refundformModel: RefundForm[] = [];
  fileList: any[] = []; // Array to store existing supporting documents
  selectedItems: any[] = [];
  states: any[] = [];
  totalGrossAmount: number = 0; // Variable to hold the total sum of gross amounts
  totalPGAmounts: number = 0;

  otcPaymentDetailsCashAmt: number | null = 0;
  paymentModel: OTCPaymentModel = {
    payer_email: '',
    pymt_mode: '',
    cash_amt: 0,
    // Initialize other fields here
  };

  refundTypeMapping = [
    { type: 'RS01', label: 'Refund Slip 01' },
    { type: 'RS02', label: 'Refund Slip 02' },
    { type: 'DA', label: 'Direct Refund Application' },
    { type: 'CB', label: 'Charge Back' },
    { type: 'RF', label: 'Refund Form' },
  ];

  rtt_status: string = '';
  refundcdsection: boolean = false;
  smesection: boolean = false;
  noremarksection: boolean = false;
  disableForm: boolean = false;
  isLoading: boolean = false;
  totalRecords: number = 0;
  onlinepaymentinfosection: boolean = false;
  otcpaymentinfosection: boolean = false;
  decisiongroup: any[] = [];
  previousHistRTTStatus: string = '';
  previousHistPickupBy: string = '';
  prrviousHistAssignTo: string = '';
  refund_reason: string = '';
  pickup_by: string = '';
  approved_by: String | null = null;

  previousSMEUsername: string = '';
  previousSMEAssignTo: string = '';
  previousFAUsername: string = '';
  previousPGUsername: string = '';
  previousBYMUsername: string = '';
  AssignToBYM: boolean = false;
  //refund form one 
  RefundInfoForm!: FormGroup;
  postcode: string | null = null;
  city: string | null = null;
  state: string | null = null;
  totalPostCodeRecords: number = 0;
  postCodes: PostCodeData[] = [];
  uniqueCities: string[] = [];
  uniqueStates: string[] = [];

  // Receipt & Supporting Documents
  uploadedFiles: Array<{
    fileName: string;
    file: File | null;
    fileContent?: string; // Add the optional fileContent property
    fileSize?: number;
  }> = [
      { fileName: '', file: null, fileContent: '', fileSize: 0 } // Initialize with default values
    ];
  fileTypeList = ['Invoice', 'Receipt', 'Supporting Document'];


  // Payee Bank Information
  payeeBank = {
    bankName: '',
    accountNo: '',
    accountHolderName: ''
  };

  showInsertAlert: boolean = false;
  alertMessage: string = '';
  alertClass: string = '';

  //for file document
  existingFilesTotalSize: number = 0;
  readonly MAX_TOTAL_SIZE = 10 * 1024 * 1024; // 10 MB in bytes
  // A string snapshot of the form’s original value
  originalFormValue: string = '';

  // Custom flag to indicate if the form has any net changes
  isFormChanged: boolean = false;
  returnTaskbutton: boolean = false;
  RttForm: any;

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
    private fb: FormBuilder,
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

    //this.isLoading = true;
    this.fetchSMEUser();
    this.fetchBanks();
    this.loadStates();
    this.initializeRefundInfoForm();
    this.fetchRefundCd();
    //this.hardcodeRefundCd();
    //this.hardcodeAfterClickViewInMytasklist();
    this.stateHistory();

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


  // hardcodeAfterClickViewInMytasklist() {
  //   this.rtt_wf_id = 475;
  //   this.orn_no = 'tea111';
  //   this.task_id = 'T20250110000002';
  //   console.log('rtt_wf_id:', this.rtt_wf_id);
  // }

  fetchblankform(): void {
    // Fetch SME users after getting the refund history
    this.fetchDocuments()
      .then(() => {

        this.fetchRefundHist();

        this.fetchRTTItems();
      })
      .catch((error) => {
        console.error("Error fetching documents:", error);
      });

  }

  // Method to get the label based on refund_ty
  getRefundLabel(refund_ty: string): string {
    const match = this.refundTypeMapping.find(item => item.type === refund_ty);
    return match ? match.label : 'Unknown Refund Type';
  }

  // Fetch customer and bank information
  fetchRttform(): void {
    this.isLoading = true;
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    const url = environment.apiUrl + '/api/refundapproval/v1/getrttform';
    const requestBody = {
      i_orn_no: this.orn_no,
    };

    this.http.post(url, requestBody, { headers }).subscribe(
      (response: any) => {
        if (response?.data && Array.isArray(response.data) && response.data.length > 0) {
          const bankInfo = response.data[0]; // Assuming the first object is the desired one

          console.log('Bank information:', bankInfo);
          this.RttForm = bankInfo; // Store the bank information in RttForm
          // Patch the form with bank and customer information
          this.RefundInfoForm.patchValue({
            // Customer Information
            customerName: bankInfo.custNm,
            customerEmail: bankInfo.custEmail,
            customerPhoneNumber: bankInfo.custPhone,
            customerAddress1: bankInfo.billingAddress1,
            customerAddress2: bankInfo.billingAddress2,
            customerAddress3: bankInfo.billingAddress3,
            postcode: bankInfo.custPostcode,
            city: bankInfo.custCity,
            state: bankInfo.custState,

            // Receipt Information
            receiptNo: bankInfo.rcptNo,
            receiptAmount: bankInfo.rcptAmt,
            orderReferenceNo: bankInfo.ornNo,
            transactionId: bankInfo.txnId,
            entityName: bankInfo.entityNm,
            entityType: bankInfo.entityTy,
            entityNo: bankInfo.entityNo,

            // Payee Bank Information
            bankName: bankInfo.bankAccountName,
            accountNo: bankInfo.bankAccountNo,
            accountHolderName: bankInfo.bankHolderName,
            identityType: bankInfo.identityType,
            identityNumber: bankInfo.identityNumber,

            // Other Information
            //remarks: '', // Default or API-provided value
          });
        }
        console.log('RttForm data:', this.RttForm);
        this.isLoading = false;
        // Store the original form value as a JSON string
        this.originalFormValue = JSON.stringify(this.RefundInfoForm.value);

        // console.log('Original form value:', this.originalFormValue);

        // Subscribe to form value changes to detect net changes
        this.RefundInfoForm.valueChanges.subscribe(currentValue => {
          const currentValueString = JSON.stringify(currentValue);

          //  console.log('Current form value:', currentValueString);

          if (currentValueString === this.originalFormValue) {
            // If current form value equals the original, mark as unchanged.
            this.isFormChanged = false;
            // Optionally mark as pristine.
            this.RefundInfoForm.markAsPristine();
          } else {
            // There is a net change in the form.
            this.isFormChanged = true;
          }
        });

      },
      (error) => {
        console.error('Error fetching refund info:', error);
        this.isLoading = false;
      }
    );
  }

  async fetchDocuments(): Promise<void> {
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json'
    });

    const url = environment.apiUrl + '/api/refundapproval/v1/getrttdoc';
    const requestBody = {
      i_rtt_wf_id: this.rtt_wf_id,
    };

    const retries = 3;
    let success = false;
    let lastError: any = null;

    for (let attempt = 1; attempt <= retries; attempt++) {
      try {
        const response: any = await this.http.post(url, requestBody, { headers }).toPromise();
        if (Array.isArray(response)) {
          this.fileList = response;
          console.log('Fetched documents:', this.fileList);

          this.existingFilesTotalSize = this.fileList.reduce(
            (total, file) => total + (file.fileSize || 0),
            0
          );
          console.log('Existing files total size (bytes):', this.existingFilesTotalSize);

          success = true;
          break; // Exit the loop on success.
        } else {
          throw new Error('Unexpected response format');
        }
      } catch (error) {
        lastError = error;
        console.error(`Attempt ${attempt} failed:`, error);
        // Delay before retrying (e.g., 1 second delay)
        await new Promise((resolve) => setTimeout(resolve, 1000));
      }
    }

    if (!success) {
      this.alertMessage = 'An error occurred while fetching documents.';
      this.alertClass = 'alert alert-danger PA-alert-box';
      this.showInsertAlert = true;

      // auto-hide after 3 seconds
      setTimeout(() => {
        this.showInsertAlert = false;
       /// window.history.back();
      }, 10000);
    }
  }

  viewDocument(file: any): void {
    if (file.file_content) {
      // Decode the Base64 content
      const binaryString = atob(file.file_content);
      const binaryLen = binaryString.length;
      const bytes = new Uint8Array(binaryLen);

      for (let i = 0; i < binaryLen; i++) {
        bytes[i] = binaryString.charCodeAt(i);
      }

      // Create a blob from the decoded content
      const blob = new Blob([bytes], { type: file.file_type });
      const fileURL = URL.createObjectURL(blob);

      // Open the file in a new browser tab or window
      window.open(fileURL);
    } else {
      this.alertMessage = 'No file content available for preview';
      this.alertClass = 'alert alert-danger PA-alert-box';
      this.showInsertAlert = true;

      // auto-hide after 3 seconds
      setTimeout(() => {
        this.showInsertAlert = false;
      }, 3000);
      this.isLoading = false;
    }
  }

  downloadDocument(file: any): void {
    if (!file.file_content) {
      // Set the alert message and class to display the custom alert
      this.alertMessage = 'No file content available';
      this.alertClass = 'alert alert-danger PA-alert-box';  // You can customize the class based on your styles
      this.showInsertAlert = true;  // Show the alert

      // Auto-hide after 3 seconds
      setTimeout(() => {
        this.showInsertAlert = false;  // Hide the alert
      }, 3000);

      // Optional: Stop loading if needed
      this.isLoading = false;  // Assuming this is part of your flow
    }

    // 1) Decode Base64
    const binaryString = atob(file.file_content);
    const len = binaryString.length;
    const bytes = new Uint8Array(len);
    for (let i = 0; i < len; i++) {
      bytes[i] = binaryString.charCodeAt(i);
    }

    // 2) Build a Blob and a temporary URL
    const blob = new Blob([bytes], { type: file.file_type });
    const blobUrl = URL.createObjectURL(blob);

    // 3) Create an <a> and “click” it to download
    const a = document.createElement('a');
    a.href = blobUrl;
    a.download = this.getDownloadFileName(file);
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);

    // 4) Clean up
    URL.revokeObjectURL(blobUrl);
  }

  getDownloadFileName(file: any): string {
    let fileName = file.file_nm;

    // Check if fileName already has an extension
    if (fileName.lastIndexOf('.') === -1) {
      // Map MIME type to file extension
      let extension = '';
      switch (file.file_type) {
        case 'application/pdf':
          extension = '.pdf';
          break;
        case 'application/msword':
          extension = '.doc';
          break;
        case 'application/vnd.openxmlformats-officedocument.wordprocessingml.document':
          extension = '.docx';
          break;
        case 'application/vnd.ms-excel':
          extension = '.xls';
          break;
        case 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet':
          extension = '.xlsx';
          break;
        // Add more cases as needed for other file types
        default:
          // Optionally, you can set a default extension or leave it blank.
          extension = '';
          break;
      }
      fileName += extension;
    }
    return fileName;
  }


  decisionGroup(): void {
    console.log('calling decision');
    // Reset the decisiongroup array before updating
    this.decisiongroup = [];

    // Check if refundApprovalTaskInfo has at least one item with refund_cd not empty or null
    const hasValidRefundCd = this.refund_cd && this.refund_cd.trim() !== '';
    console.log('hasValidRefundCd:', hasValidRefundCd);
    const currentRttStatus = this.rtt_status;
    console.log('currentRttStatus:', currentRttStatus);
    const mapStatus = (status: string): string => {
      const statusMapping: { [key: string]: string } = {
        "Pending BYM": "PBYM",
        "Pending SME": "PSME",
        "Pending Finance Admin": "PFA",
        "Pending FSM/FHOD": 'T1',
        "Pending DCEO": 'T2',
        "Pending CEO": 'T3',
        "Pending PG": 'PPG',

      };
      return statusMapping[status] || status; // Use the mapped value or keep the original if no mapping exists
    };

    // Map the status before assigning it
    const mappedStatus = mapStatus(this.previousHistRTTStatus);

    if (hasValidRefundCd) { // mean refund_cd added

      if (currentRttStatus === 'PSME') { // SME take action
        this.decisiongroup = [
          { nm: 'Query to Finance Admin', status: 'PFA' }, // Directly using 'PFA'
          { nm: 'Approve', status: 'PBYM' },
          { nm: 'Reject', status: 'RR' },
        ];
      }

      if (currentRttStatus === 'PFA') { // FA take action to reply SME / HOD / DCEO / CEO
        this.decisiongroup = [
          { nm: 'Query to Requester', status: mappedStatus, assign_to: this.prrviousHistAssignTo }, // Use mapped status
        ];
        console.log('decisiongroup:', this.decisiongroup);
      }
      if (currentRttStatus === 'T1' || currentRttStatus === 'T2' || currentRttStatus === 'T3') { // FSM/FHOD / DCEO / CEO take action
        this.decisiongroup = [
          { nm: 'Query to Finance Admin', status: 'PFA' },
          { nm: 'Approve', status: 'RS' },
          { nm: 'Reject', status: 'RR' },
        ];
      }

      console.log('refund sction:', this.refundcdsection);
      this.refundcdsection = false;

    } else {    // refund_cd not added
      if (currentRttStatus === 'PFA') {  // FA take action to update the refund_cd
        this.decisiongroup = [
          { nm: 'Approve', status: 'PSME' },
          { nm: 'Reject', status: 'RR' },
        ];
        this.refundcdsection = true;
        this.smesection = true;
      }
      if (currentRttStatus === 'PSME') {  // SME take action to update the refund_cd
        this.decisiongroup = [
          { nm: 'Query Finance Admin', status: 'PFA' },
          { nm: 'Approve', status: 'PBYM' },
          { nm: 'Reject', status: 'RR' },
        ];
        this.refundcdsection = false;
        this.smesection = false;
      }
    }
  }

  stateHistory() {
    this.refund_ty = history.state.refund_ty;
    this.rtt_app_no = history.state.rtt_app_no;
    this.task_id = history.state.task_id;

    console.log('refund_ty:', this.refund_ty, 'rtt_app_no:', this.rtt_app_no, 'task_id:', this.task_id);
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
        this.orn_no = response.data[0].orn_no;
        this.rtt_status = response.data[0].rtt_status;
        this.refund_cd = response.data[0].refund_cd;
        console.log('response:', this.rtt_wf_id, this.orn_no, this.rtt_status, this.refund_cd);

        // for the refund form status is T1 / T2/ T3
        if (this.rtt_status === 'T1' || this.rtt_status === 'T2' || this.rtt_status === 'T3') {
          this.RefundInfoForm.disable(); // Disable entire form
          this.disableForm = true;
        }

        this.fetchblankform();
      },
      (error) => {
        console.error('Error fetching rttwfid:', error);
        this.isLoading = false;
      }
    );

  }

  clampDiscount(index: number) {
    const fg = this.paymentItems.at(index);
    const gross = fg.get('grossAmount')!.value || 0;
    const ctrl = fg.get('discount')!;

    if (ctrl.value > gross) {
      // <-- drop the emitEvent:false so your valueChanges re-runs
      ctrl.setValue(gross);
    }
  }




  async handleFormSubmit(form: NgForm) {

    if (form.invalid) {
      this.alertMessage = 'Please fill in all required approval action fields.';
      this.alertClass = 'alert alert-danger PA-alert-box';
      this.showInsertAlert = true;

      // auto-hide after 3 seconds
      setTimeout(() => {
        this.showInsertAlert = false;
      }, 3000);
      return;
    }

    this.isLoading = true; // Show loading screen
    await this.submitRefundRequest();
    this.isLoading = false;
  }

  async submitRefundRequest(): Promise<void> {
    const rcptDate: string | null = this.pgRCPTModel.length > 0 && this.pgRCPTModel[0].rcpt_dt
      ? new Date(this.pgRCPTModel[0].rcpt_dt).toISOString().slice(0, 19).replace('T', ' ')
      : null;

    const url = environment.apiUrl + '/api/refundapproval/v1/updaterttwfstatus';
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    let body: any; // Declare `body` in a higher scope

    if (this.sme_nm == null || this.sme_nm === '') {
      this.sme_nm = this.previousSMEAssignTo
    }
    if (this.decision_status === 'PSME') {
      body = {
        i_rtt_wf_id: this.rtt_wf_id,
        i_rtt_status: this.decision_status,
        i_msg: this.remarks_msg,
        i_refund_cd: this.refund_cd,
        i_refund_reason: this.refund_reason,
        i_assign_to: this.sme_nm, // Use optional chaining to avoid errors
        i_pickup_by: this.previousSMEUsername
      };
    } else if (this.decision_status === 'PFA') {
      body = {
        i_rtt_wf_id: this.rtt_wf_id,
        i_rtt_status: this.decision_status,
        i_msg: this.remarks_msg,
        i_refund_cd: this.refund_cd,
        i_assign_to: null, // Explicitly set to null
        i_refund_reason: this.refund_reason,
        i_pickup_by: this.previousFAUsername
      };
    } else if (this.decision_status === 'PBYM' || this.decision_status === 'T1' || this.decision_status === 'T2' || this.decision_status === 'T3') {
      body = {
        i_rtt_wf_id: this.rtt_wf_id,
        i_rtt_status: this.decision_status,
        i_msg: this.remarks_msg,
        i_refund_cd: this.refund_cd,
        i_assign_to: null, // Explicitly set to null
        i_refund_reason: this.refund_reason,
        i_pickup_by: this.previousBYMUsername,
      };
    } else if (this.decision_status === 'RS') {
      body = {
        i_rtt_wf_id: this.rtt_wf_id,
        i_rtt_status: this.decision_status,
        i_msg: this.remarks_msg,
        i_refund_cd: this.refund_cd,
        i_assign_to: null, // Explicitly set to null
        i_refund_reason: this.refund_reason,
        i_pickup_by: null,
        i_refund_type: this.refund_ty,
        i_refund_amt: this.totalGrossAmount,
      };
    } else if (this.decision_status === 'RR') {
      body = {
        i_rtt_wf_id: this.rtt_wf_id,
        i_rtt_status: this.decision_status,
        i_msg: this.remarks_msg,
        i_refund_cd: this.refund_cd,
        i_assign_to: null, // Explicitly set to null
        i_refund_reason: this.refund_reason,
        i_pickup_by: null,
        i_refund_type: this.refund_ty,
        i_refund_amt: this.totalGrossAmount,
      };
    }

    console.log('Request body:', body);

    //return

    if (this.refund_ty === 'RF' && this.decision_status === 'RS') {
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
        vendor_nm: formData.bankHolderName,
        id_ty: formData.identityType,              // from the BankInfoForm
        id_no: formData.identityNumber,            // from the BankInfoForm
        pm: 'Online', // Current setting, can be changed later
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
          this.alertMessage = 'An error occurred while inserting data into FMS. Please try again.';
          this.alertClass = 'alert alert-danger PA-alert-box';
          this.showInsertAlert = true;

          // Auto-hide after 3 seconds
          setTimeout(() => {
            this.showInsertAlert = false;
          }, 3000);
          this.isLoading = false;   // stop loading spinner
          return;                   // ← bail out here
        }

      } catch (fmsError) {
        this.alertMessage = 'Network error during FMS insert. Please check your connection.';
        this.alertClass = 'alert alert-danger PA-alert-box';
        this.showInsertAlert = true;

        // Auto-hide after 3 seconds
        setTimeout(() => {
          this.showInsertAlert = false;
        }, 3000);
        this.isLoading = false;     // stop loading spinner
        return;                     // ← and here
      }

    }


    try {
      const response = await this.http.post(url, body, { headers }).toPromise();
      console.log('Success response:', response);

      // Set the alert properties for a successful submission
      this.alertMessage = 'Refund approval submit successfully.';
      this.alertClass = 'alert alert-success PA-alert-box';
      this.showInsertAlert = true;

      // Optionally hide the alert and redirect after 5 seconds
      setTimeout(() => {
        this.showInsertAlert = false;
        this.redirectToPaidTransactions();
      }, 3000);
    } catch (error) {
      console.error('Error:', error);

      // Set the alert properties for an error message
      this.alertMessage = 'An error occurred while submitting the refund aprroval. Please try again.';
      this.alertClass = 'alert alert-danger PA-alert-box';
      this.showInsertAlert = true;

      // Optionally hide the alert after 10 seconds
      setTimeout(() => {
        this.showInsertAlert = false;
      }, 10000);
    }


  }

  redirectToPaidTransactions() {
    this.location.back();
  }

  onSelectionChange(event: Event): void {
    const selectedValue = (event.target as HTMLSelectElement).value;
    this.sme_email = selectedValue;

    console.log('Selected value:', selectedValue);
    console.log('refund_ty:', this.refund_ty);


    if (selectedValue === 'RR') {
      this.smesection = false;
      this.refundcdsection = false;
    }


    if (this.refund_ty === 'RS01') {     // this for RS01

      if (selectedValue === 'PBYM') {
        this.refundcdsection = true;
        this.smesection = false;
      }
      if (selectedValue === 'PSME') {
        this.smesection = true;
        this.refundcdsection = false;
      }

    } else if (this.refund_ty === 'CB') {    // this for CB

      if (selectedValue === 'PPG') {
        this.smesection = false;
        this.refundcdsection = false;
      }

      if (selectedValue === 'PRS') {
        this.refundcdsection = true;
        this.smesection = false;
      }

    } else if (this.refund_ty === 'DA') {    // this for DA
      if (selectedValue === 'PSME') {
        this.refundcdsection = true;
        this.smesection = true;
      }

      if (selectedValue === 'PFA') {
        this.refundcdsection = false;
        this.smesection = false;
      }


    } else if (this.refund_ty === 'RS02') {    // this for RS02
      // currently not in used

    } else if (this.refund_ty === 'RF') {    // this for RF

      if (selectedValue === 'PSME') {
        if (this.previousSMEUsername === '' || this.previousSMEUsername === null) {
          this.refundcdsection = true;
          this.smesection = true;
        } else {
          this.refundcdsection = false;
          this.smesection = false;
        }

      }

      if (selectedValue === 'PFA') {
        this.refundcdsection = false;
        this.smesection = false;
      }

    }
  }

  onSelectionChange_refundcd(event: Event): void {
    const selectedValue = (event.target as HTMLSelectElement).value;
    this.refund_cd = selectedValue;
    // console.log('Selected value:', selectedValue);
  }

  // Fetch RTT Items
  async fetchRTTItems(): Promise<void> {
    this.isLoading = true;
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    const url = environment.apiUrl + '/api/refundapproval/v1/getrttitems';
    const requestBody = {
      i_rtt_wf_id: this.rtt_wf_id,
    };

    this.http.post(url, requestBody, { headers }).subscribe(
      (response: any) => {
        this.rttItems = response?.data || [];
        console.log(this.rttItems); // Log the API response

        const paymentItemsArray = this.RefundInfoForm.get('paymentItems') as FormArray;
        paymentItemsArray.clear(); // Clear existing items

        // Populate payment items dynamically with subscription for recalculation
        this.rttItems.forEach((item: any) => {
          const group = this.fb.group({
            itemDescription: [item.item_desc || '', Validators.required],
            quantity: [item.qty || 0, Validators.required],
            amount: [item.unit_fee || 0, Validators.required],
            tax: [item.tax_amt || 0],
            incentiveCode: [item.grant_cd || ''],
            discount: [item.disc_amt || 0],
            grossAmount: [item.refund_amt || 0, Validators.required],
            rtt_item_id: [item.rtt_item_id || 0],
            netAmount: [item.net_amt || 0],
          });

          // Subscribe to changes on this group to recalculate grossAmount
          group.valueChanges.subscribe(values => {
            const quantity = Number(values.quantity) || 0;
            const amount = Number(values.amount) || 0;
            const tax = Number(values.tax) || 0;
            const discount = Number(values.discount) || 0;
            // Example calculation: (quantity * amount) + tax - discount
            const calculatedGross = (quantity * amount)
            const FixedGross = parseFloat(calculatedGross.toFixed(2)); // Ensure 2 decimal places
            group.get('grossAmount')?.setValue(FixedGross, { emitEvent: false });

            const calculatedNet = (quantity * amount) + tax - discount;
            const FixedNet = parseFloat(calculatedNet.toFixed(2)); // Ensure 2 decimal places

            group.get('netAmount')?.setValue(FixedNet, { emitEvent: false });
            this.calculateTotalGrossAmount();
          });

          paymentItemsArray.push(group);
        });

        // Calculate total refund amount (as an initial value)
        this.totalGrossAmount = this.rttItems.reduce(
          (total, item) => total + (item.net_amt || 0),
          0
        );

        this.isLoading = false;
        this.fetchRttform();
      },
      (error) => {
        console.error('Error fetching payment items:', error);
        this.isLoading = false;
      }
    );
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
      },
      (error) => {
        console.error(error);
        this.isLoading = false;
      }
    );

  }

  loadStates() {
    this.ParamService.getStates('1', '100', '', 'State').subscribe(
      (response: any) => {
        if (response && response.data && Array.isArray(response.data)) {
          this.states = response.data as any[];
          console.log(this.states);
          //this.states.push({ param_cd: '', nm_en: 'All', nm_bm: 'All', total: 5 }); //add 'All' options
          // this.states.push(response.data);
          // this.states = [...this.states, ...response.data];
          //this.states.sort((a, b) => a.nm_en.localeCompare(b.nm_en)); // sorting
        } else {
          console.error('Invalid response format:', response);
        }
      },
      (error) => {
        console.error('There was an error retrieving the status:', error);
      }
    );
  }

  // Method to map actions to descriptive names
  mapAction(action: string | null): string {
    return action && this.actionMapping[action] ? this.actionMapping[action] : 'Unknown';
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

  // for refund form 
  initializeRefundInfoForm() {
    this.RefundInfoForm = this.fb.group({
      // Customer Information
      customerName: ['', Validators.required],
      customerEmail: ['', [Validators.required, Validators.email]],
      customerPhoneNumber: ['', [
        Validators.required,
        Validators.minLength(10),
        Validators.maxLength(15),
        Validators.pattern('^[0-9]+$')
      ]],
      customerAddress1: ['', Validators.required],
      customerAddress2: [''],
      customerAddress3: [''],
      postcode: ['', [Validators.required, Validators.pattern(/^\d{5}$/)]], // 5 digits
      city: ['', Validators.required],
      state: ['', Validators.required],

      // Receipt Information
      receiptNo: ['', Validators.required],
      receiptAmount: ['', [Validators.required, Validators.pattern("^[0-9]+(\.[0-9]{1,2})?$")]],
      orderReferenceNo: ['', Validators.required],
      transactionId: [''],
      entityName: ['', Validators.required],
      entityType: ['', Validators.required],
      entityNo: ['', [
        Validators.required,
        Validators.minLength(5),
        Validators.maxLength(30)
      ]],

      // Payment Items
      paymentItems: this.fb.array(
        [],
      ),
      // Dynamically added rows for payment items
      uploadedFiles: this.fb.array(
        [],
      ),// Dynamically added rows for uploaded files

      // Payee Bank Information
      bankName: ['', Validators.required],
      accountNo: ['', Validators.required],
      accountHolderName: ['', Validators.required],
      identityNumber: ['', [Validators.required, Validators.pattern(/^[a-zA-Z0-9]+$/)]],
      identityType: ['', Validators.required], // e.g., BRN, Passport, Old NRIC

      // Other Information
      remarks: ['', Validators.required]
    });

    this.loadPostcode(); // Load postcodes when the form initializes
    // Add one payment item row by default
    // this.addCheque();
  }

  // Getter for payment items FormArray
  get paymentItems(): FormArray {
    return this.RefundInfoForm.get('paymentItems') as FormArray;
  }

  //getter for uploaded files FormArray
  get uploadedFilesFormArray(): FormArray {
    return this.RefundInfoForm.get('uploadedFiles') as FormArray;
  }


  // Remove a payment item row
  removeItem(index: number): void {
    this.paymentItems.removeAt(index);
  }


  private createPaymentItem(): FormGroup {
    const fg = this.fb.group({
      itemDescription: ['', Validators.required],
      quantity: [1, [Validators.required, Validators.min(1)]],
      amount: [0, [Validators.required, Validators.min(0)]],  // ← unit fee
      tax: [0],
      discount: [0],
      grossAmount: [0],
      netAmount: [0]
    });

    // recalc gross/net…
    fg.valueChanges.subscribe(vals => {
      const gross = (vals.quantity || 0) * (vals.amount || 0);
      const net = gross + (vals.tax || 0) - (vals.discount || 0);
      fg.patchValue({ grossAmount: gross, netAmount: net }, { emitEvent: false });
      this.calculateTotalGrossAmount();
    });

    return fg;
  }

  // helper to build one file-upload row
  private createFileGroup(): FormGroup {
    return this.fb.group({
      file: [null, Validators.required],
      fileName: ['', Validators.required],
      fileContent: ['']
    });
  }

  // 2) In your onFileSelect method:
  onFileSelect(event: Event, index: number): void {
    const input = event.target as HTMLInputElement;
    if (!input.files?.length) { return; }

    const file = input.files[0];

    // 1) check total including already-fetched files
    const usedSoFar = this.existingFilesTotalSize
      + this.uploadedFiles.reduce((sum, f) => sum + (f.file?.size || 0), 0);

    if (usedSoFar + file.size > this.MAX_TOTAL_FILE_SIZE) {
      this.alertMessage = `Total of all uploaded files cannot exceed 10 MB.`;
      this.alertClass = 'alert alert-danger PA-alert-box';
      this.showInsertAlert = true;

      // auto-hide after 3 seconds
      setTimeout(() => {
        this.showInsertAlert = false;
      }, 3000);
      return;
    }


    if (file.size > this.MAX_SINGLE_FILE_SIZE) {
      this.alertMessage = `"${file.name}" exceeds the 5 MB per-file limit.`;
      this.alertClass = 'alert alert-danger PA-alert-box';
      this.showInsertAlert = true;

      // auto-hide after 3 seconds
      setTimeout(() => {
        this.showInsertAlert = false;
      }, 3000);

      return;
    }

    // 2) Your existing type check…
    const allowedTypes = [
      'application/pdf',
      'application/msword',
      'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
      'image/jpeg',
      'image/png'
    ];
    if (!allowedTypes.includes(file.type)) {
      this.alertMessage = 'This file type is not allowed. Please upload a PDF, DOC, DOCX, JPEG, or PNG file.';
      this.alertClass = 'alert alert-danger PA-alert-box';
      this.showInsertAlert = true;

      // auto-hide after 3 seconds
      setTimeout(() => {
        this.showInsertAlert = false;
      }, 3000);

      return;
    }

    // 3) Total size check (still 10 MB across all files)
    const currentTotal = this.uploadedFiles
      .reduce((sum, f) => sum + (f.file?.size || 0), 0);
    if (currentTotal + file.size > this.MAX_TOTAL_FILE_SIZE) {

      console.log('currentTotal:', currentTotal, 'file.size:', file.size, 'MAX_TOTAL_FILE_SIZE:', this.MAX_TOTAL_FILE_SIZE);
      this.alertMessage = "Total of all uploaded files cannot exceed 10 MB.";
      this.alertClass = 'alert alert-danger PA-alert-box';
      this.showInsertAlert = true;

      // auto-hide after 3 seconds
      setTimeout(() => {
        this.showInsertAlert = false;
      }, 3000);

      return;
    }

    // 4) If you get here, it’s valid—proceed with your FileReader
    this.uploadedFiles[index].file = file;
    this.uploadedFiles[index].fileName = file.name;
    this.uploadedFiles[index].fileSize = file.size;

    const reader = new FileReader();
    reader.onload = () => {
      const base64 = (reader.result as string).split(',')[1];
      this.uploadedFilesArray.at(index).patchValue({
        file, fileName: file.name, fileContent: base64
      });
      this.uploadedFiles[index].fileContent = base64;
      input.value = '';
    };
    reader.onerror = err => console.error("Error reading file:", err);
    reader.readAsDataURL(file);
  }




  // Add a new file row to the FormArray (user uploads only)
  addFileRow(): void {
    // 1) push a new FormGroup into the FormArray
    this.uploadedFilesFormArray.push(this.createFileGroup());

    // 2) also push a matching “blank” entry into your data array
    this.uploadedFiles.push({
      fileName: '',
      file: null,
      fileContent: '',
      fileSize: 0
    });
  }

  // Remove a file row from the FormArray (user uploads only)
  removeFile(index: number): void {
    if (this.uploadedFilesFormArray.length > index) {
      // 1) remove the FormArray group
      this.uploadedFilesFormArray.removeAt(index);
      // 2) also remove the corresponding entry in your data array
      this.uploadedFiles.splice(index, 1);
    }
  }

  // addCheque(): void {
  //   const paymentItem = this.fb.group({
  //     itemDescription: ['', Validators.required],
  //     quantity: [1, [Validators.required, Validators.min(1)]],
  //     amount: [0, [Validators.required, Validators.min(0)]],
  //     tax: [0], // Default tax to 0
  //     incentiveCode: [''],
  //     discount: [0],
  //     grossAmount: [0, Validators.required] // Remove 'disabled' and make it a regular form control
  //   });

  //   // Calculate grossAmount dynamically
  //   paymentItem.valueChanges.subscribe(values => {
  //     const { quantity, amount, tax, discount } = values;
  //     const calculatedGrossAmount =
  //       (quantity || 0) * (amount || 0) + (tax || 0) - (discount || 0);
  //     paymentItem.get('grossAmount')?.setValue(calculatedGrossAmount, { emitEvent: false });
  //   });

  //   this.paymentItems.push(paymentItem);

  //   // Update total gross amount whenever paymentItems change
  //   this.paymentItems.valueChanges.subscribe(() => {
  //     this.calculateTotalGrossAmount();
  //   });
  // }

  calculateTotalGrossAmount(): void {
    const controls = this.paymentItems.controls as FormGroup[];
    this.totalGrossAmount = controls.reduce((total: number, group: FormGroup) => {
      const value = group.get('netAmount')?.value;
      return total + (parseFloat(value) || 0);
    }, 0);
  }


  fetchRefundHist(): void {
    this.isLoading = true;
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    console.log('call refund hist api');
    console.log(this.orn_no);
    const url = environment.apiUrl + '/api/refundl/v1/getRefundHist'; // API endpoint
    const requestBody = {
      i_orn_no: this.orn_no,
    };

    this.http.post(url, requestBody, { headers }).subscribe(
      (response: any) => {
        console.log('API response:', response);
        if (response?.data && Array.isArray(response.data) && response.data.length > 0) {
          this.refundHistModel = response.data.map((item: RefundHist) => {
            return {
              ...item,
              action: this.mapAction(item.action), // Map the action to descriptive name
            };
          });

          console.log('Refund history:', this.refundHistModel);

          // Set the last item's msg as the initial value for the 'remarks' FormControl
          const lastMsg = this.refundHistModel[this.refundHistModel.length - 1]?.msg || '';
          this.RefundInfoForm.controls['remarks'].setValue(lastMsg);
          this.rtt_wf_hist_id = this.refundHistModel[this.refundHistModel.length - 1]?.rtt_wf_hist_id;
          console.log(lastMsg);


          // Function to find the first non-matching `modified_by` value
          function findFirstDifferentModifiedByWithRTT(data: any[]): any {
            if (!data || data.length === 0) return null;

            // Filter out records with modified_by = 'SYSTEM'
            const validRecords = data.filter(record => record.modified_by !== 'SYSTEM');

            if (validRecords.length === 0) return null;

            // Group records by contiguous RTT status
            interface Group {
              rtt_status: string;
              records: any[];
            }
            const groups: Group[] = [];
            let currentGroup: Group = { rtt_status: validRecords[0].rtt_status, records: [validRecords[0]] };

            for (let i = 1; i < validRecords.length; i++) {
              const record = validRecords[i];
              // If the RTT status is the same as the current group, add the record to the group.
              if (record.rtt_status === currentGroup.rtt_status) {
                currentGroup.records.push(record);
              } else {
                // RTT status changed: push the current group and start a new one.
                groups.push(currentGroup);
                currentGroup = { rtt_status: record.rtt_status, records: [record] };
              }
            }
            // Push the final group
            groups.push(currentGroup);

            // If there's only one group, then RTT status never changed.
            if (groups.length < 2) return null;

            // The previous (or initial) group's in‑charge is taken as the modified_by of the first record in the first group.
            let previousIncharge = groups[0].records[0].modified_by;

            // Now check each subsequent group.
            for (let g = 1; g < groups.length; g++) {
              // Look through all records in the current group.
              for (const record of groups[g].records) {
                // If we find a modified_by different from the previous group's in‑charge, return it.
                if (record.modified_by !== previousIncharge) {
                  return record.modified_by;
                }
              }
              // If no record in this group had a different modified_by,
              // update the previousIncharge to the new group's first record (baseline) and continue.
              previousIncharge = groups[g].records[0].modified_by;
            }

            // If no change was detected across groups, return null.
            return null;
          }


          // Call the function with the response data
          const firstDifferentModifiedBy = findFirstDifferentModifiedByWithRTT(this.refundHistModel);

          this.pickup_by = firstDifferentModifiedBy;
          console.log('First different modified_by:', this.pickup_by);

          // Function to find the first non-matching `rtt_status` value (ignoring 'SYSTEM' and empty values)
          function findFirstDifferentRTTStatus(data: any[]): any {
            if (!data || data.length === 0) return null; // Handle empty or invalid data

            // Start with the first rtt_status value
            let reference = data[0].rtt_status;
            // If the first value is 'SYSTEM' or falsy, ignore it for comparison
            if (reference === 'SYSTEM' || !reference) {
              reference = null;
            }

            // Loop through the data starting from the second element
            for (let i = 1; i < data.length; i++) {
              const current = data[i].rtt_status;

              // Skip if current is 'SYSTEM' or empty
              if (current === 'SYSTEM' || !current) {
                continue;
              }

              // If reference is null (because it was 'SYSTEM' or missing), assign the first valid value
              if (reference === null) {
                reference = current;
                continue;
              }

              // If the current value does not match the reference, return it
              if (current !== reference) {
                return current; // First non-matching rtt_status found
              }
            }

            // If all valid rtt_status values are the same, return null
            return null;
          }

          // Example usage:
          const firstDifferentRTTStatus = findFirstDifferentRTTStatus(this.refundHistModel);
          this.previousHistRTTStatus = firstDifferentRTTStatus;
          console.log('First different rtt_status:', this.previousHistRTTStatus);


          // add the similar function here 


          function findFirstDifferentModifiedBy(data: any[], loginUserName: string): any {
            if (!data || data.length === 0) return null;

            // If the first element's action is "Job Pick Up" and there is a second element,
            // return the second element's pickup_by.
            if (data[0].action === 'Job Pick Up' && data.length > 1) {
              console.log('Job Pick Up action found, returning pickup_by from second element:', data[1]?.modified_by);
              return data[1]?.modified_by || null;
            }

            // Get the first element's modified_by value.
            const firstModified = data[0].modified_by;

            // Immediately return the first element's modified_by if it is not SYSTEM and not equal to loginUserName.
            if (
              firstModified &&
              firstModified.toUpperCase() !== 'SYSTEM' &&
              firstModified !== loginUserName
            ) {
              return firstModified;
            }
          }


          const findSMEUsername = (data: any[]): any => {
            if (!data || data.length === 0) return null; // Handle empty or invalid data

            for (let i = 0; i < data.length; i++) {
              // Check if the current record has the desired action
              if (data[i].action === 'Job Pick Up' && data[i].rtt_status === 'Pending SME') {
                const current = data[i].modified_by;
                // Found the Refund Request row, return the modified_by value
                return current;
              }
              if (data[i].action === 'Refund Request' && data[i].rtt_status === 'Pending Finance Admin') {
                return null;
              }
            }

          }

          const findSMEAssignTo = (data: any[]): any => {
            if (!data || data.length === 0) return null; // Handle empty or invalid data

            for (let i = 0; i < data.length; i++) {
              // Check if the current record has the desired action
              if (data[i].action === 'Job Pick Up' && data[i].rtt_status === 'Pending SME') {
                const current = data[i].assign_to;
                // Found the Refund Request row, return the modified_by value
                return current;
              }
              if (data[i].action === 'Refund Request' && data[i].rtt_status === 'Pending Finance Admin') {
                return null;
              }
            }

          }

          const findFAUsername = (data: any[]): any => {
            if (!data || data.length === 0) return null; // Handle empty or invalid data

            for (let i = 0; i < data.length; i++) {
              // Check if the current record has the desired action
              if (data[i].action === 'Job Pick Up' && data[i].rtt_status === 'Pending Finance Admin') {
                const current = data[i].modified_by;
                // Found the Refund Request row, return the modified_by value
                return current;
              }
              if (data[i].action === 'Refund Request' && data[i].rtt_status === 'Pending Finance Admin') {
                return null;
              }
            }
          }

          const findBYMUsername = (data: any[]): any => {
            if (!data || data.length === 0) return null; // Handle empty or invalid data

            for (let i = 0; i < data.length; i++) {
              // Check if the current record has the desired action
              if (
                data[i].action === 'Job Pick Up' &&
                (
                  data[i].rtt_status === 'Pending FSM/FHOD' ||
                  data[i].rtt_status === 'Pending DCEO' ||
                  data[i].rtt_status === 'Pending CEO'
                )
              ) {
                const current = data[i].modified_by;
                // Found the Refund Request row, return the modified_by value
                return current;
              }
              if (data[i].action === 'Refund Request' && data[i].rtt_status === 'Pending Finance Admin') {
                return null;
              }
            }
          }

          const firstSMEUsername = findSMEUsername(this.refundHistModel);
          this.previousSMEUsername = firstSMEUsername;
          console.log('First SME modified_by:', this.previousSMEUsername);

          const firstSMEAssignTo = findSMEAssignTo(this.refundHistModel);
          this.previousSMEAssignTo = firstSMEAssignTo;
          console.log('First SME Assign To:', this.previousSMEAssignTo);

          const firstFAUsername = findFAUsername(this.refundHistModel);
          this.previousFAUsername = firstFAUsername;
          console.log('First FA modified_by:', this.previousFAUsername);

          const firstBYMUsername = findBYMUsername(this.refundHistModel);
          this.previousBYMUsername = firstBYMUsername;
          console.log('First BYM modified_by:', this.previousBYMUsername);

        } else {
          // Handle the case where the response is empty or null
          this.refundHistModel = [];
          this.RefundInfoForm.controls['remarks'].setValue('');
        }

        this.decisionGroup();
        this.isLoading = false;
      },
      (error) => {
        console.error('Error fetching refund history', error);
        this.isLoading = false;
      }
    );
  }

  onSelectionChange_refundreason(event: Event): void {
    const selectedValue = (event.target as HTMLSelectElement).value;
    this.refund_reason = selectedValue;
    console.log('Selected value:', selectedValue);
  }


  cancel(): void {
    this.location.back();
  }

  private logValidationErrors(group: FormGroup | FormArray, path: string = ''): void {
    Object.keys(group.controls).forEach(key => {
      const control = group.get(key)!;
      const currentPath = path ? `${path}.${key}` : key;

      if (control instanceof FormGroup || control instanceof FormArray) {
        // dive into nested groups/arrays
        this.logValidationErrors(control, currentPath);
      }
      else {
        // a FormControl
        if (control.invalid) {
          console.log(`❌ [${currentPath}] errors:`, control.errors);
        }
      }
    });
  }

  // This method handles the update action when the form is modified
  updateChanges(): void {
    if (this.RefundInfoForm.invalid) {
      this.alertMessage = 'Refund form is invalid. Please check the fields.';
      this.alertClass = 'alert alert-danger PA-alert-box';
      this.showInsertAlert = true;

      // auto-hide after 3 seconds
      setTimeout(() => {
        this.showInsertAlert = false;
      }, 3000);
      this.logValidationErrors(this.RefundInfoForm);

      return;
    }

    // For example, call a method to send the updated form data
    this.onUpdate();
  }

  // Handle form update
  async onUpdate(): Promise<void> {
    this.isLoading = true;

    // Check total file size of all uploaded files (same as insert)
    const MAX_TOTAL_SIZE = 10 * 1024 * 1024; // 10 MB in bytes
    const totalFileSize = this.uploadedFiles.reduce((total, fileItem) => {
      return total + (fileItem.file ? fileItem.file.size : 0);
    }, 0);

    if (totalFileSize > MAX_TOTAL_SIZE) {
      this.alertMessage = 'Total of all uploaded files cannot exceed 10 MB.';
      this.alertClass = 'alert alert-danger PA-alert-box';
      this.showInsertAlert = true;

      // auto-hide after 3 seconds
      setTimeout(() => {
        this.showInsertAlert = false;
      }, 3000);

      this.isLoading = false;
      return;
    }

    // Map the uploaded files into the desired format
    const formattedFiles = this.uploadedFiles.map(fileItem => {
      const file = fileItem.file; // Access the File object
      return {
        file_nm: fileItem.fileName,            // File name
        file_content: fileItem.fileContent,      // Base64 string
        file_type: file ? file.type : '',         // MIME type of the file
        file_size_kb: file ? Math.ceil(file.size / 1024) : 0 // File size in KB
      };
    });

    // Extract values from the form as you did before
    const RefundformData = this.RefundInfoForm.value;

    const paymentItemDetails = (this.RefundInfoForm.get('paymentItems') as FormArray).value.map((item: any) => {
      const unitFee = parseFloat(item.amount); // Ensure unit_fee is a number
      const taxAmt = parseFloat(item.tax);       // Ensure tax_amt is a number
      const taxPct = unitFee ? (taxAmt / unitFee) * 100 : 0; // Calculate tax_pct

      return {
        unit_fee: unitFee,
        qty: item.quantity,
        item_ref_no: 'NON-RMS',
        item_desc: item.itemDescription,
        tax_pct: taxPct,
        tax_amt: taxAmt,
        grant_cd: item.incentiveCode,
        disc_amt: item.discount,
        gross_amt: item.grossAmount,
        net_amt: item.netAmount,
        rtt_item_id: item.rtt_item_id
      };
    });

    // Prepare the update payload. You may need to include an identifier (e.g. record id) 
    // if your API requires it. Here it is assumed that this.recordId holds the ID.
    const body: any = {
      // Include the record identifier if necessary (can be in the URL or the body)
      rtt_wf_id: this.rtt_wf_id, // ensure this is set earlier in your component logic
      rtt_wf_hist_id: this.rtt_wf_hist_id,
      rcpt_no: RefundformData.receiptNo,
      rcpt_amt: RefundformData.receiptAmount,
      rcpt_date: null,  // as per your current setup
      orn_no: RefundformData.orderReferenceNo,
      txn_id: RefundformData.transactionId,
      refund_amt: this.totalGrossAmount,
      ent_no: RefundformData.entityNo,
      ent_nm: RefundformData.entityName,
      ent_ty: RefundformData.entityType,
      cust_email: RefundformData.customerEmail,
      cust_nm: RefundformData.customerName,
      cust_phone: RefundformData.customerPhoneNumber,
      msg: RefundformData.remarks,
      sme_email: null,
      assign_to: null,
      rtt_status: this.rtt_status,  // set accordingly
      refund_ty: 'RF',
      refund_reason: null,
      payment_item_details: paymentItemDetails,
      uploadedFiles: formattedFiles,
      identity_type: RefundformData.identityType,
      identity_number: RefundformData.identityNumber,
      bank_account_no: RefundformData.accountNo,
      bank_account_name: RefundformData.bankName,
      bank_account_type: RefundformData.accountType,
      bank_holder_name: RefundformData.accountHolderName,
      billing_address_1: RefundformData.customerAddress1,
      billing_address_2: RefundformData.customerAddress2,
      billing_address_3: RefundformData.customerAddress3,
      city: RefundformData.city,
      postcode: RefundformData.postcode,
      state: RefundformData.state,
      rec_email: RefundformData.customerEmail,
    };

    console.log('Update Payload:', body);
    //return;

    // Set up HTTP headers
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    // Define the update API endpoint. Notice that we include the record ID here.
    const url = `${environment.apiUrl}/api/refundl/v1/updrttwf_rf`;

    // Execute the HTTP PUT (or PATCH) request for update
    this.http.put(url, body, { headers }).subscribe(
      (response: any) => {
        this.isLoading = false;

        this.alertMessage = 'Refund form updated successfully.';
        this.alertClass = 'alert alert-success PA-alert-box';
        this.showInsertAlert = true;

        // auto-hide after 3 seconds
        setTimeout(() => {
          this.showInsertAlert = false;
          window.location.reload();
        }, 3000);

      },
      (error) => {
        this.alertMessage = 'An error occurred while updating the refund form. Please try again.';
        this.alertClass = 'alert alert-danger PA-alert-box';
        this.showInsertAlert = true;

        // auto-hide after 3 seconds
        setTimeout(() => {
          this.showInsertAlert = false;
        }, 3000);
        this.isLoading = false;
      }
    );
  }

  loadPostcode() {
    // Subscribe to changes on the postcode control to auto-populate city and state.
    this.RefundInfoForm.get('postcode')?.valueChanges.subscribe(selectedPostcode => {
      this.onPostcodeChange(selectedPostcode);
    });

    const url = environment.apiUrl + '/api/rms/v1/getpostcode';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    // const Body: any = {
    // };

    this.http.post(url, {}, { headers }).subscribe(
      (response: any) => {

        if (response.data.length === 0) {
          this.totalPostCodeRecords = 0;
        } else {
          this.postCodes = response.data;
          this.totalPostCodeRecords = response.data[0].total;
          this.extractUniqueCitiesAndStates();
        }
        //console.log(this.postCodes);
      },
      (error) => {
        console.error('There was an error retrieving the postcode:', error);
      }
    );

  }

  //postcode start
  onPostcodeChange(selectedPostcode: string | null) {
    if (!selectedPostcode) {
      // Reset city and state when postcode is cleared.
      this.RefundInfoForm.patchValue({ city: null, state: null });
      return;
    }

    const match = this.postCodes.find(p => String(p.postcode) === selectedPostcode);
    this.RefundInfoForm.patchValue({
      city: match ? match.city : null,
      state: match ? match.state : null
    });
  }

  extractUniqueCitiesAndStates() {
    this.uniqueCities = [...new Set(this.postCodes.map(p => p.city))].sort((a, b) =>
      a.localeCompare(b)
    );
    this.uniqueStates = [...new Set(this.postCodes.map(p => p.state))].sort((a, b) =>
      a.localeCompare(b)
    );
  }

  upperCity = (term: string): string => {
    return (term ?? '').toUpperCase();
  };


  checkTag = (term: string): string | null => {
    if (/^\d{1,5}$/.test(term)) {
      return term; // ensure 1–5 digit number
    }

    return null;
  };

  filterToDigits(event: Event) {
    const input = event.target as HTMLInputElement;
    // strip out any non‑digit characters
    const digits = input.value.replace(/\D/g, '');
    // update both the input’s displayed value and the form control
    input.value = digits;
    this.RefundInfoForm.get('customerPhoneNumber')!.setValue(digits);
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

  resetFileInput(event: MouseEvent): void {
    const input = event.target as HTMLInputElement;
    // Clear the old value so that selecting the same file again will trigger `change`
    input.value = '';
  }

  formatBytes(bytes: number, decimals = 2): string {
    const k = 1024;
    const sizes = ['B', 'KB', 'MB', 'GB', 'TB'];
    if (bytes === 0) {
      // force “0.00 MB” – or pick whichever unit you prefer
      return (0).toFixed(decimals) + ' ' + sizes[2];
    }
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return parseFloat((bytes / Math.pow(k, i)).toFixed(decimals)) + ' ' + sizes[i];
  }


  readonly MAX_SINGLE_FILE_SIZE = 5 * 1024 * 1024;   // 5 MB
  readonly MAX_TOTAL_FILE_SIZE = 10 * 1024 * 1024;  // e.g. total of 10 MB
  SUPPORTED_EXTS = '.pdf,.doc,.docx,.jpeg,.jpg,.png';

  get uploadedFilesArray() {
    return this.RefundInfoForm.get('uploadedFiles') as FormArray;
  }

  get uploadedFilesSize(): number {
    return this.uploadedFiles
      .reduce((sum, f) => sum + (f.fileSize || 0), 0);
  }

  // how much total you’ve used so far
  get totalUsed(): number {
    return this.existingFilesTotalSize + this.uploadedFilesSize;
  }

  // remaining space under your TOTAL cap
  get remainingSpace(): number {
    return Math.max(this.MAX_TOTAL_FILE_SIZE - this.totalUsed, 0);
  }

  get paymentItemsArray(): FormArray {
    return this.RefundInfoForm.get('paymentItems') as FormArray;
  }

}
