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
import { FormBuilder, FormControl, FormGroup, NgForm, NgModel, Validators, FormArray } from '@angular/forms';
import { Location } from '@angular/common';
import { ActionMappingService } from 'src/app/core/services/action-mapping.service';

import { RefundApprovalTaskInfo, RefundRTTItems } from 'src/app/core/models/refundapproval-interface';
@Component({
  selector: 'app-refund-approval-fa',
  templateUrl: './refund-approval-fa.component.html',
  styleUrls: ['./refund-approval-fa.component.scss']
})
export class RefundApprovalFaComponent {
  headerLabel = 'labels.approval';  // default
  actionMapping!: { [key: string]: string };
  page = environment.DefaultPage;
  itemsPerPage = environment.ItemPerPage;
  orn_no: String | null = null;
  txn_id: String | null = null;
  mtt_id: number | null = null;
  rms_type: String | null = null;
  rtt_app_no: String | null = null;
  task_id: String | null = null;
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
  modelData: any;
  orderInfo: RefundPTTOrderDetails[] = [];
  rttItems: RefundRTTItems[] = [];
  rttwfid: number | null = null; // Start as null to indicate no value yet
  onlinePaymentInfos: RefundPTTOnlinePaymentInfos[] = [];
  bankmodel: OTCBank[] = [];
  file_content: string | null = null;
  returnTaskbutton: boolean = false;

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
  refundApprovalTaskInfo: RefundApprovalTaskInfo[] = [];
  refundCdModel: any[] = [];

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
  AssignToBYM: boolean = false;

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

  refundTypeMapping = [
    { type: 'RS01', label: 'Refund Slip 01' },
    { type: 'RS02', label: 'Refund Slip 02' },
    { type: 'DA', label: 'Direct Refund Application' },
    { type: 'CB', label: 'Charge Back' },
    { type: 'RF', label: 'Refund Form' },
  ];
  refundcdsection: boolean = false;
  smesection: boolean = false;
  noremarksection: boolean = false;
  isLoading: boolean = false;
  totalRecords: number = 0;
  onlinepaymentinfosection: boolean = false;
  otcpaymentinfosection: boolean = false;
  decisiongroup: any[] = [];
  previousHistRTTStatus: string = '';
  previousSMEUsername: string = '';
  previousSMEAssignTo: string = '';
  previousFAUsername: string = '';
  previousPGUsername: string = '';
  previousBYMUsername: string = '';
  previousHistPickupBy: string = '';
  refund_reason: string = '';
  pickup_by: String | null = null;
  approved_by: String | null = null;


  //refund form one 
  RefundInfoForm!: FormGroup;

  // Receipt & Supporting Documents
  uploadedFiles: Array<{
    fileName: string;
    file: File | null;
    fileContent?: string; // Add the optional fileContent property
  }> = [
      { fileName: '', file: null, fileContent: '' } // Initialize with default values
    ];
  fileTypeList = ['Invoice', 'Receipt', 'Supporting Document'];


  // Payee Bank Information
  payeeBank = {
    bankName: '',
    accountNo: '',
    accountHolderName: ''
  };
  bankList = ['Maybank', 'CIMB', 'Public Bank', 'RHB', 'Hong Leong Bank'];

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
    public authService: AuthService,
    private location: Location,
    private actionMappingService: ActionMappingService,
    private fb: FormBuilder,
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
    // this.hardcodeRefundCd();
    this.fetchRefundCd();
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
  //   this.rtt_wf_id = 472;
  //   this.task_id = 'T20250110000002';
  //   console.log('rtt_wf_id:', this.rtt_wf_id);
  // }

  // Method to get the label based on refund_ty
  getRefundLabel(refund_ty: string): string {
    const match = this.refundTypeMapping.find(item => item.type === refund_ty);
    return match ? match.label : 'Unknown Refund Type';
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
    console.log('rtt_wf_id:', this.rtt_wf_id);
    this.http.post(url, Body, { headers }).subscribe(
      (response: any) => {

        this.refundApprovalTaskInfo = response?.data || []; // Store the received data
        console.log(this.refundApprovalTaskInfo);
        this.mtt_id = this.refundApprovalTaskInfo.length > 0 ? this.refundApprovalTaskInfo[0].mtt_id : null;
        this.rms_type = this.refundApprovalTaskInfo.length > 0 ? this.refundApprovalTaskInfo[0].rms_tpye : '';
        this.orn_no = this.refundApprovalTaskInfo.length > 0 ? this.refundApprovalTaskInfo[0].orn_no : '';
        this.refund_cd = this.refundApprovalTaskInfo.length > 0 ? this.refundApprovalTaskInfo[0].refund_cd : '';
        this.refund_ty = this.refundApprovalTaskInfo.length > 0 ? this.refundApprovalTaskInfo[0].refund_ty : '';
        this.status_param_nm = this.refundApprovalTaskInfo.length > 0 ? this.refundApprovalTaskInfo[0].status_param_nm : '';
        console.log('approval task info: ', this.refund_cd);
        // execute here because for get the mtt_id, rms_type, orn_no

        this.fetchRefundHist();
        this.fetchOrderInfo();
        this.fetchRTTItems();
        this.checkfetchRcpt();
        this.fetchOTCRcpt();
        this.fetchOnlineRcpt();
        this.fetchRefundInformation();
        this.fetchSMEUser();
        this.isLoading = false;
      },
      (error) => {
        console.error(error);
        this.isLoading = false;
      }
    );
  }


  decisionGroup() {
    // Reset the decisiongroup array before updating
    this.decisiongroup = [];

    // Check if refundApprovalTaskInfo has at least one item with refund_cd not empty or null
    const hasValidRefundCd = this.refundApprovalTaskInfo.some(
      (task) => task.refund_cd && task.refund_cd.trim() !== ''
    );
    const currentRttStatus = this.refundApprovalTaskInfo[0].rtt_status;

    const mapStatus = (status: string): string => {
      const statusMapping: { [key: string]: string } = {
        "Pending BYM": "PBYM",
        "Pending SME": "PSME",
        "Pending Finance Admin": "PFA",
        "Pending FSM/FHOD": 'T1',
        "Pending DCEO": 'T2',
        "Pending CEO": 'T3',
        "Pending PG": 'PPG',
        "Preview Refund Submission": 'PRS',

      };
      return statusMapping[status] || status; // Use the mapped value or keep the original if no mapping exists
    };
    const code = mapStatus(currentRttStatus);
    if (code === 'PRS') {
      // if it’s already “Submitted” then show Completed
      this.headerLabel = 'labels.completed';
      this.decisiongroup.push({
        value: this.headerLabel,              // or a code like 'COMPLETE'
        name: this.translate.instant(this.headerLabel)
      });
    } else {
      // otherwise you’re still in an approval flow
      this.headerLabel = 'labels.approval';
      this.decisiongroup.push(
        {
          value: 'APPROVE',
          name: this.translate.instant('labels.approve')
        },
        {
          value: 'REJECT',
          name: this.translate.instant('labels.reject')
        }
      );
    }

    // Map the status before assigning it
    const mappedStatus = mapStatus(this.previousHistRTTStatus);
    console.log('mapped status:', mappedStatus);

    if (this.refund_ty === 'RS02') {
      if (hasValidRefundCd) {
        // Create a mapping function for statuses with explicit typing

        if (currentRttStatus === 'PSME') {
          this.decisiongroup = [
            { nm: 'Query to Finance Admin', status: 'PFA' }, // Directly using 'PFA'
            { nm: 'Approve', status: 'PBYM' },
            { nm: 'Reject', status: 'RR' },
          ];
        } else {
          this.decisiongroup = [
            { nm: 'Query to Requester', status: mappedStatus }, // Use mapped status
          ];
        }
        // If refund_cd is not empty or null, allow "Approve" and "Reject" options

        this.refundcdsection = false;

      } else {
        // If refund_cd is empty or null, allow "Select All" (query to requester)

        if (currentRttStatus === 'PBYM') {
          this.decisiongroup = [
            { nm: 'Approve', status: 'PBYM' },
            { nm: 'Reject', status: 'RR' },
          ];
          this.refundcdsection = true;

        } else if (currentRttStatus === 'PSME') {
          this.decisiongroup = [
            { nm: 'Query to Finance Admin', status: 'PFA' }, // Directly using 'PFA'
            { nm: 'Approve', status: 'PBYM' },
            { nm: 'Reject', status: 'RR' },
          ];
        }
      }

    } else if (this.refund_ty === 'DA') {
      //console.log('Refund Type:', this.refund_ty);
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
            { nm: 'Query to Requester', status: mappedStatus }, // Use mapped status
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
      }
    } else if (this.refund_ty === 'RS01') {

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
            { nm: 'Query to Requester', status: mappedStatus }, // Use mapped status
          ];
        }
        console.log('refund sction:', this.refundcdsection);
        this.refundcdsection = false;

      } else {    // refund_cd not added
        if (currentRttStatus === 'PFA') {  // FA take action to update the refund_cd
          this.decisiongroup = [
            { nm: 'Query to Requester', status: 'PSME' },
            { nm: 'Approve', status: 'PBYM' },
            { nm: 'Reject', status: 'RR' },
          ];
          this.refundcdsection = true;

        } else if (currentRttStatus === 'PSME') {
          this.decisiongroup = [
            { nm: 'Query to Finance Admin', status: 'PFA' }, // Directly using 'PFA'
          ];
        }
      }

    } else if (this.refund_ty === 'CB') {

      if (hasValidRefundCd) { // mean refund_cd added

        if (currentRttStatus === 'PRS') { // FA take action to reply SME / HOD / DCEO / CEO
          this.decisiongroup = [
            { nm: 'Completed - This task will be removed from Assigned Task', status: 'RS' } // Use mapped status
          ];
          this.noremarksection = true;
          this.refundcdsection = false;
          this.smesection = false;
        }

      } else {    // refund_cd not added
        if (currentRttStatus === 'PFA') {  // FA take action to update the refund_cd
          this.decisiongroup = [
            { nm: 'Query to Requester', status: 'PPG' },
            { nm: 'Approve', status: 'PRS' },
            { nm: 'Reject', status: 'RR' },
          ];
          this.refundcdsection = true;

        }

        if (currentRttStatus === 'PPG') {
          this.decisiongroup = [
            { nm: 'Query to Finance Admin', status: 'PFA' },
          ];
        }
      }

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

  async postRefundRequest(
    url: string,
    body: any,
    headers: HttpHeaders,
    successMessage: string = 'Refund approval submit successfully.',
    errorMessage: string = 'An error occurred while submitting the refund approval. Please try again.'
  ): Promise<void> {
    try {
      const response = await this.http.post(url, body, { headers }).toPromise();
      console.log('Success response:', response);

      // Set alert properties for a successful submission
      this.alertMessage = successMessage;
      this.alertClass = 'alert alert-success PA-alert-box';
      this.showInsertAlert = true;

      // Hide the alert and optionally redirect after 3 seconds
      setTimeout(() => {
        this.showInsertAlert = false;
        this.redirectToPaidTransactions();
      }, 3000);
    } catch (error) {
      console.error('Error:', error);

      // Set alert properties for an error message
      this.alertMessage = errorMessage;
      this.alertClass = 'alert alert-danger PA-alert-box';
      this.showInsertAlert = true;

      // Hide the alert after 10 seconds
      setTimeout(() => {
        this.showInsertAlert = false;
      }, 10000);
    }
  }



  async handleFormSubmit(form: NgForm) {

    if (form.invalid) {

      this.alertMessage = 'Unable to complete refund approval. Please fill in all required fields.';
      this.alertClass = 'alert alert-danger PA-alert-box';
      this.showInsertAlert = true;

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
    // Format receipt date if available
    const rcptDate: string | null =
      this.pgRCPTModel.length > 0 && this.pgRCPTModel[0].rcpt_dt
        ? new Date(this.pgRCPTModel[0].rcpt_dt)
          .toISOString()
          .slice(0, 19)
          .replace('T', ' ')
        : null;

    const url = environment.apiUrl + '/api/refundapproval/v1/updaterttwfstatus';
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json'
    });

    let body: any; // Declare `body` here

    if (this.sme_nm == null || this.sme_nm === '') {
      this.sme_nm = this.previousSMEAssignTo
    }

    // Build the request body based on the refund type
    if (this.refund_ty === 'RS02') {
      if (
        this.decision_status === 'PFA') {
        body = {
          i_rtt_wf_id: this.rtt_wf_id,
          i_rtt_status: this.decision_status,
          i_msg: this.remarks_msg,
          i_refund_cd: this.refund_cd,
          i_assign_to: null, // Explicitly set to null
          i_pickup_by: this.previousFAUsername,
        };
      } else if (this.decision_status === 'PSME') {
        // If the decision status is "PSME", we need to check the refundHistModel array
        body = {
          i_rtt_wf_id: this.rtt_wf_id,
          i_rtt_status: this.decision_status,
          i_msg: this.remarks_msg,
          i_refund_cd: this.refund_cd,
          i_assign_to: this.sme_nm,
          i_pickup_by: this.previousSMEUsername
        };
      }
      else if (this.decision_status === 'PBYM' || this.decision_status === 'T1' || this.decision_status === 'T2' || this.decision_status === 'T3') {
        // If the decision status is "PSME", we need to check the refundHistModel array
        body = {
          i_rtt_wf_id: this.rtt_wf_id,
          i_rtt_status: this.decision_status,
          i_msg: this.remarks_msg,
          i_refund_cd: this.refund_cd,
          i_assign_to: null,
          i_pickup_by: this.previousBYMUsername
        };
      }
      else {
        body = {
          i_rtt_wf_id: this.rtt_wf_id,
          i_rtt_status: this.decision_status,
          i_msg: this.remarks_msg,
          i_refund_cd: this.refund_cd,
          i_assign_to: null, // Explicitly set to null
          i_pickup_by: null,
          i_refund_amt: this.totalGrossAmount,
          i_refund_type: this.refund_ty,
        };
      }
    } else if (this.refund_ty === 'DA') {

      if (this.decision_status === 'PSME') {
        body = {
          i_rtt_wf_id: this.rtt_wf_id,
          i_rtt_status: this.decision_status,
          i_msg: this.remarks_msg,
          i_refund_cd: this.refund_cd,
          i_refund_reason: this.refund_reason,
          i_assign_to: this.sme_nm,
          i_pickup_by: this.previousSMEUsername
        };
      } else if (this.decision_status === 'PFA') {
        body = {
          i_rtt_wf_id: this.rtt_wf_id,
          i_rtt_status: this.decision_status,
          i_msg: this.remarks_msg,
          i_refund_cd: this.refund_cd,
          i_refund_reason: this.refund_reason,
          i_assign_to: null,
          i_pickup_by: this.previousFAUsername
        };
      }
      else if (this.decision_status === 'PBYM' || this.decision_status === 'T1' || this.decision_status === 'T2' || this.decision_status === 'T3') {
        body = {
          i_rtt_wf_id: this.rtt_wf_id,
          i_rtt_status: this.decision_status,
          i_msg: this.remarks_msg,
          i_refund_cd: this.refund_cd,
          i_assign_to: null,
          i_refund_reason: this.refund_reason,
          i_pickup_by: this.previousBYMUsername
        };

      }
      else {
        body = {
          i_rtt_wf_id: this.rtt_wf_id,
          i_rtt_status: this.decision_status,
          i_msg: this.remarks_msg,
          i_refund_cd: this.refund_cd,
          i_assign_to: null,
          i_refund_reason: this.refund_reason,
          i_pickup_by: null,
          i_refund_amt: this.totalGrossAmount,
          i_refund_type: this.refund_ty,
        };
      }
    } else if (this.refund_ty === 'RS01') {
      if (this.sme_nm == null || this.sme_nm === '') {
        this.sme_nm = this.refundHistModel[1]?.assign_to;
      }
      if (this.decision_status === 'PSME') {
        body = {
          i_rtt_wf_id: this.rtt_wf_id,
          i_rtt_status: this.decision_status,
          i_msg: this.remarks_msg,
          i_refund_cd: null,
          i_refund_reason: null,
          i_assign_to: this.sme_nm,
          i_pickup_by: this.previousSMEUsername
        };
      } else if (this.decision_status === 'PFA') {
        body = {
          i_rtt_wf_id: this.rtt_wf_id,
          i_rtt_status: this.decision_status,
          i_msg: this.remarks_msg,
          i_refund_cd: this.refund_cd,
          i_assign_to: null,
          i_refund_reason: this.refund_reason,
          i_pickup_by: this.previousFAUsername,
        };
      } else if (this.decision_status === 'PBYM' || this.decision_status === 'T1' || this.decision_status === 'T2' || this.decision_status === 'T3') {
        body = {
          i_rtt_wf_id: this.rtt_wf_id,
          i_rtt_status: this.decision_status,
          i_msg: this.remarks_msg,
          i_refund_cd: this.refund_cd,
          i_assign_to: null,
          i_refund_reason: this.refund_reason,
          i_pickup_by: this.previousBYMUsername
        };

      } else {
        body = {
          i_rtt_wf_id: this.rtt_wf_id,
          i_rtt_status: this.decision_status,
          i_msg: this.remarks_msg,
          i_refund_cd: this.refund_cd,
          i_assign_to: null,
          i_refund_reason: this.refund_reason,
          i_pickup_by: null,
          i_refund_amt: this.totalGrossAmount,
          i_refund_type: this.refund_ty,
        };
      }
    } else if (this.refund_ty === 'CB') {
      if (this.decision_status === 'PFA') {
        body = {
          i_rtt_wf_id: this.rtt_wf_id,
          i_rtt_status: this.decision_status,
          i_msg: this.remarks_msg,
          i_refund_cd: this.refund_cd,
          i_refund_reason: this.refund_reason,
          i_assign_to: null,
          i_pickup_by: this.previousFAUsername
        };
      } else if (this.decision_status === 'PPG') {
        body = {
          i_rtt_wf_id: this.rtt_wf_id,
          i_rtt_status: this.decision_status,
          i_msg: this.remarks_msg,
          i_refund_cd: this.refund_cd,
          i_refund_reason: this.refund_reason,
          i_assign_to: null,
          i_pickup_by: this.previousPGUsername
        };
      } else if (this.decision_status === 'PRS') {
        body = {
          i_rtt_wf_id: this.rtt_wf_id,
          i_rtt_status: this.decision_status,
          i_msg: this.remarks_msg,
          i_refund_cd: this.refund_cd,
          i_refund_reason: this.refund_reason,
          i_assign_to: null,
          i_pickup_by: this.previousPGUsername,
          i_refund_amt: this.totalGrossAmount,
          i_refund_type: this.refund_ty,
        };
      } else
        body = {
          i_rtt_wf_id: this.rtt_wf_id,
          i_rtt_status: this.decision_status,
          i_msg: this.remarks_msg,
          i_refund_cd: this.refund_cd,
          i_refund_reason: this.refund_reason,
          i_assign_to: null,
          i_pickup_by: null,
          i_refund_amt: this.totalGrossAmount,
          i_refund_type: this.refund_ty,
        };

    }

    // Log the constructed request body for debugging
    console.log('Request body:', body);


    // return;
    // Use the helper function to send the POST request

    await this.postRefundRequest(url, body, headers);
  }


  redirectToPaidTransactions() {
    this.location.back();
  }

  onSelectionChange(event: Event): void {

    const hasValidRefundCd = this.refundApprovalTaskInfo.some(
      (task) => task.refund_cd && task.refund_cd.trim() !== ''
    );

    const selectedValue = (event.target as HTMLSelectElement).value;
    this.sme_email = selectedValue;

    console.log('Selected value:', selectedValue);


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

      if (selectedValue === 'RS') {
        this.refundcdsection = false;
        this.smesection = false;
      }

    } else if (this.refund_ty === 'DA') {    // this for DA

      if (selectedValue === 'PSME' && !hasValidRefundCd) {
        this.refundcdsection = true;
        this.smesection = true;
      }

      if (selectedValue === 'PFA') {
        this.refundcdsection = false;
        this.smesection = false;
      }

    } else if (this.refund_ty === 'RS02') {    // this for RS02
      //curently not in used

    }
  }

  onSelectionChange_refundcd(event: Event): void {
    const selectedValue = (event.target as HTMLSelectElement).value;
    this.refund_cd = selectedValue;
    // console.log('Selected value:', selectedValue);
  }

  onSelectionChange_refundreason(event: Event): void {
    const selectedValue = (event.target as HTMLSelectElement).value;
    this.refund_reason = selectedValue;
    console.log('Selected value:', selectedValue);
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
          console.log(this.onlinePaymentInfos);
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
            refund_slip_no: 'N/A',
            requested_by: 'N/A',
            dt_process: null,
            rtt_id: null,
            appeal_cnt: null,
            rtt_status: 'N/A'
          }]; // Replace with appropriate fields
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

          if (this.refundHistModel[0].action === 'Job Pick Up') {
            this.returnTaskbutton = true;
            console.log('returnTaskbutton:', this.returnTaskbutton);
          }

          // Function to find the first non-matching `modified_by` value
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

          const findSMEUsername = (data: any[]): any => {
            if (!data || data.length === 0) return null; // Handle empty or invalid data
            if (this.refund_ty === 'RS01') {

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
            if (this.refund_ty === 'RS02') {

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
            // Check refund_ty = 'DA'
            if (this.refund_ty === 'DA') {
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
            if (this.refund_ty === 'RS01' || this.refund_ty === 'CB' || this.refund_ty === 'DA') {

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
          }

          const findPGUsername = (data: any[]): any => {
            if (!data || data.length === 0) return null; // Handle empty or invalid data
            if (this.refund_ty === 'CB') {

              for (let i = 0; i < data.length; i++) {
                // Check if the current record has the desired action
                if (data[i].action === 'Refund Request' && data[i].rtt_status === 'Pending Finance Admin') {
                  const current = data[i].modified_by;
                  return current;
                }
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
              if (this.refund_ty === 'RS01' || this.refund_ty === 'DA' || this.refund_ty === 'CB') {
                if (data[i].action === 'Refund Request' && data[i].rtt_status === 'Pending Finance Admin') {
                  return null;
                }

              } else if (this.refund_ty === 'RS02') {
                if (data[i].action === 'Refund Request' && data[i].rtt_status === 'Pending SME') {
                  return null;
                }
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

          const firstPGUsername = findPGUsername(this.refundHistModel);
          this.previousPGUsername = firstPGUsername;
          console.log('First PG modified_by:', this.previousPGUsername);


          const firstBYMUsername = findBYMUsername(this.refundHistModel);
          this.previousBYMUsername = firstBYMUsername;
          console.log('First BYM modified_by:', this.previousBYMUsername);


          // Example usage:
          const firstDifferentRTTStatus = findFirstDifferentRTTStatus(this.refundHistModel);
          this.previousHistRTTStatus = firstDifferentRTTStatus;
          console.log('First different rtt_status:', this.previousHistRTTStatus);

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
            modified_by_nm: '-',
          }]; // Replace with appropriate fields
        }
        this.isLoading = false;
        this.decisionGroup();
      },
      (error) => {
        console.error('Error fetching refund info', error);
        this.isLoading = false;
      }
    );
    console.log('Previous Hist RTT Status:', this.previousHistRTTStatus);
  }

  // Method to map actions to descriptive names
  mapAction(action: string | null): string {
    return action && this.actionMapping[action] ? this.actionMapping[action] : 'Unknown';
  }

  cancel(): void {
    this.location.back();
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