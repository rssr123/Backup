import { Component, OnInit } from '@angular/core';
import { MFTWF, Param } from '../../core/models/entity';
import { environment } from 'src/environments/environment';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Router } from '@angular/router';
import { NgbPaginationConfig } from '@ng-bootstrap/ng-bootstrap';
import { Injectable } from '@angular/core';
import { DataService } from '../../core/services/data.service';
import { DateAdapter, MAT_DATE_FORMATS } from '@angular/material/core';
import { DatePipe } from '@angular/common';
import { MatDatepickerInputEvent } from '@angular/material/datepicker';
import moment from 'moment';
import { trigger, state, style, transition, animate } from '@angular/animations';
import { fadeInOut } from '../../shared/animation';
import { formatDate } from '@angular/common';
import { TranslateService } from '@ngx-translate/core';
import { GlobalService } from 'src/app/shared/global.service';
import { AuthService } from 'src/app/core/services/auth.service';
import { perm } from 'src/permissions/perm';
import { OTCReceiptCancellationMyTaskListing } from 'src/app/core/models/otc-receipt-cancellation.interface'; // Wilson's
import { RefundMyTaskListing } from 'src/app/core/models/refund.interface'; // Wilson's
import { BillingMyTaskListing } from 'src/app/core/models/billing.interface'; // Wilson's
import { CounterCheckInStatus } from 'src/app/core/services/otc-counter-status.service';

@Component({
  selector: 'app-my-task-assigned-tasks',
  templateUrl: './my-task-assigned-tasks.component.html',
  styleUrls: ['./my-task-assigned-tasks.component.scss'],
  providers: [DatePipe],
  animations: [fadeInOut]

})

export class MyTaskAssignedTasksComponent implements OnInit {

  username = this.authService.username;
  roles = this.authService.roles;

  // MFT
  isDisplay: boolean = false;
  isLoading: boolean = false;

  // OTC Receipt Cancellation
  isDisplayOtcRC: boolean = false;

  // For OTC Receipt Cancellation Table
  otcRCPage = environment.DefaultPage;
  otcRCItemsPerPage = environment.ItemPerPage;
  otcRCTotalRecords: number = 0;

  // Refund
  isDisplayRefund: boolean = false;

  // For Refund Table
  refundPage = environment.DefaultPage;
  refundItemsPerPage = environment.ItemPerPage;
  refundTotalRecords: number = 0;

  // Billing
  isDisplayBilling: boolean = false;

  // For Billing Table
  billingPage = environment.DefaultPage;
  billingItemsPerPage = environment.ItemPerPage;
  billingTotalRecords: number = 0;

  // For Credit Control Case Table
  ccPage = environment.DefaultPage;
  ccItemsPerPage = environment.ItemPerPage;
  ccTotalRecords: number = 0;
  ccTaskStatusOption: Param[] = [];
  ccTaskPymtTypeOption: Param[] = [];
  ccTaskTxnTypeOption: Param[] = [];
  isLoadingCCData: boolean = false;

  //date
  formattedStartDate: string = "";
  formattedEndDate: string = "";
  date_modified_from: string = "";
  convertedDate_modified_from = "";
  date_modified_to: string = "";
  convertedDate_modified_to = "";

  //for MFT table
  page = environment.DefaultPage;
  itemsPerPage = environment.ItemPerPage;
  totalRecords: number = 0;

  statusOptions: Param[] = [];
  otcRCStatusOptions: Param[] = [];

  // Wilson's
  refundTaskDescriptionOption: Param[] = [];
  refundStatusOption: Param[] = [];
  billingTaskDescriptionOption: Param[] = [];
  billingApprovalStatusOption: Param[] = [];

  // KS's 
  refund_ty: string = "";

  initialNumber = [
    { value: 0, label: 'No' },
    { value: 1, label: 'Yes' },
  ]

  mftwfs: MFTWF[] = [];
  forCheckingMFTWFS: MFTWF[] = [];
  forCheckingOTCRCS: OTCReceiptCancellationMyTaskListing[] = [];
  currentMFTWFStatus: string | null = null;
  currentOTCRCStatus: string | null = null;
  OTCCheckedIn: number = 0;

  currentTab: string = 'mft'; // Default to Master Fee Table tab
  onTabChange(tab: string): void {
    this.currentTab = tab;
    switch (this.currentTab) {
      case 'mft':
        this.loadMftData();
        if(this.statusOptions.length == 0)
          this.populateMftStatus();
        break;
      case 'refund':
        this.loadRefundData();
        if(this.refundStatusOption.length == 0)
        this.populateRefundStatus();
        if(this.refundTaskDescriptionOption.length == 0)
          this.populateRefundType();
        break;
      case 'billing':
        this.loadBillingData();
        if(this.billingApprovalStatusOption.length == 0)
          this.populateBillingStatus();
        break;
      case 'otc':
        switch (this.currentOTCMode) {
          case 'otc-rc':
            console.log('otc-rc');
            this.loadOtcRCData();
            if(this.otcRCStatusOptions.length == 0)
              this.populateOtcRCStatus();
            break;
          default:
            break;
        }
        break;
      case 'ccc':
        this.loadCCCData();     
        if(this.ccTaskStatusOption.length == 0)
          this.populateParam('cc-case');
        if(this.ccTaskPymtTypeOption.length == 0)
          this.populateParam('cc-pymt-status');
        if(this.ccTaskTxnTypeOption.length == 0)
          this.populateParam('cc-txn-type');   
        break;   
      default:
        break;
    }
  }

  currentOTCMode: string = 'otc-rc'; // Default to OTC Receipt Cancellation
  onOTCModeChange(mode: string): void {
    this.currentOTCMode = mode;
  }

  currentTaskMode: string = 'A'; // Default to Assigned

  // OTC Receipt Cancellation
  otcReceiptCancellationTaskDescriptionOption = [
    { value: 1, label: 'Task 1 - Receipt Cancellation for payment via EMV' },
    { value: 2, label: 'Task 2 - Receipt Cancellation for payment via Physical' },
    { value: 3, label: 'Task 3 - Task for Cash Return to Customer' }
  ]

  OTCReceiptCancellationMyTaskListing: OTCReceiptCancellationMyTaskListing[] = [];

  // Refund
  RefundMyTaskListing: RefundMyTaskListing[] = [];

  // Billing
  BillingMyTaskListing: BillingMyTaskListing[] = [];

  //Credit Control
  creditControlCaseListing: any[] = [];

  //ngmodel
  taskId: string | null = null;
  feeDetailId: string | null = null;
  modifiedByNm: string | null = null;
  extractedNumber: number | null = null;

  //date range picker
  selected!: Date[];//{ start?: moment.Moment; end?: moment.Moment };
  bsValue = new Date();
  tempDate !: Date;
  minDate = new Date();
  //date range picker

  status: string | null = null;
  alertMessage: string | undefined = undefined;
  showTaskNotUpdateAlert: boolean | undefined = undefined;
  

  taskApprovedBox: boolean = false;
  querySubmittedBox: boolean = false;
  rejectedBox: boolean = false;
  submittedForApprovalBox: boolean = false;
  taskSubmittedBox: boolean = false;

  permAT = perm.Assigned_Task_Listing;// all the perm_cd for this module seperated with comma
  permATAllow = ""; // variable to store allowed permission for the user
  permListAllow: number = 0; // if 0 then not allow to view listing page, else allow

  otcReceiptCancellationTaskDescription: number | null = null; //number because it is rctype
  counterID: string | null = null;
  requestedBy: string | null = null;
  refundRequestedBy: string | null = null;
  refundTaskDescription: string | null = null;
  refundApplicationNo: string | null = null;
  refundStatus: string | null = null;
  refundDateRequested: Date | null = null;
  refundDatePickup: Date | null = null;
  billingTaskDescription: string | null = null;
  billingApprovalStatus: string | null = null;
  billingDateRequested: Date | null = null;
  billingRequestedBy: string | null = null;
  otcRCRequestedBy: string | null = null;
  otcRCStatus: string | null = null;
  otcRCDateRequested: Date | null = null;
  ccTaskStatus: string | null = null;
  ccPymtStatus: string | null = null;
  ccTxnType: string | null = null;
  ccCaseNo: string | null = null;

  //alert start
  showResultAlert = false;
  showResultAlertBox() {
    this.showResultAlert = true;
    setTimeout(() => this.showResultAlert = false, 7000);
  }

  showTaskApprovedAlert = false;
  showTaskApprovedAlertBox() {
    this.showTaskApprovedAlert = true;
    setTimeout(() => this.showTaskApprovedAlert = false, 7000);
  }

  showQuerySubmittedAlert = false;
  showQuerySubmittedAlertBox() {
    this.showQuerySubmittedAlert = true;
    setTimeout(() => this.showQuerySubmittedAlert = false, 7000);
  }

  showSubmittedForApprovalAlert = false;
  showSubmittedForApprovalAlertBox() {
    this.showSubmittedForApprovalAlert = true;
    setTimeout(() => this.showSubmittedForApprovalAlert = false, 7000);
  }

  showTaskSubmittedAlert = false;
  showTaskSubmittedAlertBox() {
    this.showTaskSubmittedAlert = true;
    setTimeout(() => this.showTaskSubmittedAlert = false, 7000);
  }

  showRejectedAlert = false;
  showRejectedAlertBox() {
    this.showRejectedAlert = true;
    setTimeout(() => this.showRejectedAlert = false, 7000);
  }

  showGenericAlert = false;
  showGenericAlertBox() {
    this.showGenericAlert = true;
    setTimeout(() => (this.showGenericAlert = false), 2000);
  }

  showTaskNotExist = false;
  showTaskNotExistAlertBox() {
    this.showTaskNotExist = true;
    setTimeout(() => (this.showTaskNotExist = false), 10000);
  }

  //alert end

  //toogle start
  rightSectionCollapsed: boolean = true;

  //default pagination
  selectedValue = environment.dropdownOptions[0];
  dropdownOptions = environment.dropdownOptions;

  LoadData(singleItem: number) {
    this.selectedValue = singleItem;
    this.itemsPerPage = this.selectedValue;
    // Wilson's
    switch (this.currentTab) {
      case 'mft':
        this.loadMftData();
        break;
      case 'refund':
        this.loadRefundData();
        break;
      case 'billing':
        this.loadBillingData();
        break;
      case 'otc':
        switch (this.currentOTCMode) {
          case 'otc-rc':
            this.loadOtcRCData();
            break;
          default:
            break;
        }
        break;
      case 'ccc':
        this.loadCCCData();
        break;
      default:
        break;
    }
  }

  toggleRightSection() {
    this.rightSectionCollapsed = !this.rightSectionCollapsed;
  }
  //toogle end

  DefaultBox() {
    this.taskApprovedBox = false;
    this.querySubmittedBox = false;
    this.rejectedBox = false;
    this.submittedForApprovalBox = false;
  }

  AlertBoxInitialize() {
    if (this.taskApprovedBox) {
      this.showTaskApprovedAlertBox();
    } else if (this.querySubmittedBox) {
      this.showQuerySubmittedAlertBox();
    } else if (this.rejectedBox) {
      this.showRejectedAlertBox();
    } else if (this.submittedForApprovalBox) {
      this.showSubmittedForApprovalAlertBox();
    } else if (this.taskSubmittedBox){
      this.showTaskSubmittedAlertBox();
    }
  }

  constructor(
    private http: HttpClient,
    config: NgbPaginationConfig,
    private router: Router,
    private authService: AuthService,
    private translate: TranslateService,
    private globalService: GlobalService,
    public counterCheckInStatus: CounterCheckInStatus,
  ) {
    config.maxSize = 3;
    config.boundaryLinks = true;
    this.translate.setDefaultLang(this.globalService.getGlobalValue()); // default language
    this.translate.use(this.globalService.getGlobalValue());

    var tmp = window.location.href.split('/');
    if(tmp[tmp.length-1] != 'my-task-assigned-tasks')
      this.currentTab = tmp[tmp.length-1];
  }

  ngOnInit(): void {
    this.bsValue = new Date();
    this.minDate.setMonth(this.bsValue.getMonth() - 1);
    // this.selected = [this.minDate, this.bsValue];

    //put default box before alert message
    this.DefaultBox()
    this.alertMessage = history.state.alert_msg;
    this.showTaskNotUpdateAlert = history.state.showTaskNotUpdateAlert;
    if (this.alertMessage !== undefined) {

      if (this.alertMessage === "approved") {
        this.taskApprovedBox = true;
      }
      else if (this.alertMessage === "submitted") {
        this.querySubmittedBox = true;
      }
      else if (this.alertMessage === "rejected") {
        this.rejectedBox = true;
      }
      else if (this.alertMessage === "submittedForApproval") {
        this.submittedForApprovalBox = true;
      }
      else if (this.alertMessage === "tasksubmitted") {
        this.taskSubmittedBox = true;
      }
    }

    if(this.showTaskNotUpdateAlert === true){
      this.showTaskNotExistAlertBox();
    }
    
    if(this.showTaskNotUpdateAlert === true){
      this.showTaskNotExistAlertBox();
    }

    // Reset alert_msg in history state so if refresh page, alert message will not persist
    history.replaceState({ ...history.state, alert_msg: undefined, showTaskNotUpdateAlert: undefined }, '');
    // Default to MFT data load
    this.populateMftStatus();
    this.checkPermissions();
  }

  checkPermissions(){
    this.authService.checkUserRole(this.authService.username, this.permAT)
   .subscribe(
     (response: any) => {
        this.permATAllow = response.data;
        this.permListAllow = this.permATAllow.includes(perm.Assigned_Task_Listing) ? 1 : 0;
        if (this.permListAllow === 0) {
          this.router.navigate(['/access-denied']);
          return; // Exit the function to prevent further execution
        }
        this.LoadData(environment.dropdownOptions[0]);
    });
  }

  async viewSeletedMft(item: any) {

    const wf_id = item.wf_id;
    const task_id = item.task_id;
    const status__From_Assigned = item.status;
    const fee_detail_pk = item.fee_detail_pk
    let edit_Mode: boolean = false;

    const validResponse = await this.checkCurrentMFTWFStatus(wf_id);
    if (!validResponse) {
      return;
    }

    console.log("currentMFTWFStatus" + this.currentMFTWFStatus);
    if(status__From_Assigned !== this.currentMFTWFStatus){
      this.showTaskNotExistAlertBox();
      this.loadMftData();
      return;
    }

    //start from requester
    if (item.action === "Request Add" && item.status === "P-RHOD") { //pending Requester HOD approval
      this.router.navigate(['/mft-reqhod-appr-add'], { state: { wf_id, task_id, status__From_Assigned } });;
    }
    else if (item.action === "Request Add" && item.status === "P-FA") {  //pending Finance Admin approval
      this.router.navigate(['/mft-fa-appr-add'], { state: { wf_id, task_id, status__From_Assigned } });
    }
    else if (item.action === "Request Add" && item.status === "P-FHOD") { //pending Finance HOD approval
      edit_Mode = false;
      this.router.navigate(['/mft-fhod-appr-add'], { state: { wf_id, task_id, edit_Mode, status__From_Assigned } });
    }
    else if (item.action === "Request Add" && item.status === "Q-R") {  //query to Requester 
      this.router.navigate(['/mft-req-form-add'], { state: { wf_id, task_id, status__From_Assigned } });
    }
    else if (item.action === "Request Add" && item.status === "Q-RHOD") {  // query to Requester HOD 
      this.router.navigate(['/mft-reqhod-appr-add'], { state: { wf_id, task_id, status__From_Assigned } });
    }
    else if (item.action === "Request Add" && item.status === "Q-FA") { //query to Finance Admin
      this.router.navigate(['/mft-fa-appr-add'], { state: { wf_id, task_id, status__From_Assigned } });
    }
    else if (item.action === "Request Edit" && item.status === "P-RHOD") { //pending Requester HOD approval
      this.router.navigate(['/mft-reqhod-appr-edit'], { state: { wf_id, task_id, status__From_Assigned } });
    }
    else if (item.action === "Request Edit" && item.status === "P-FA") {  //pending Finance Admin approval
      this.router.navigate(['/mft-fa-appr-edit'], { state: { wf_id, task_id, status__From_Assigned, fee_detail_pk } });
    }
    else if (item.action === "Request Edit" && item.status === "P-FHOD") { //pending Finance HOD approval
      edit_Mode = true;
      this.router.navigate(['/mft-fhod-appr-add'], { state: { wf_id, task_id, edit_Mode, fee_detail_pk, status__From_Assigned } });
    }
    else if (item.action === "Request Edit" && item.status === "Q-R") {  //query to Requester 
      this.router.navigate(['/mft-req-form-edit'], { state: { wf_id, task_id, status__From_Assigned } });
    }
    else if (item.action === "Request Edit" && item.status === "Q-RHOD") {  // query to Requester HOD 
      this.router.navigate(['/mft-reqhod-appr-edit'], { state: { wf_id, task_id, status__From_Assigned } });
    }
    else if (item.action === "Request Edit" && item.status === "Q-FA") { //query to Finance Admin
      this.router.navigate(['/mft-fa-appr-edit'], { state: { wf_id, task_id, status__From_Assigned, fee_detail_pk } });
    }
    //start from finance admin
    else if (item.action === "Request Add-FIN" && item.status === "P-FHOD") {  //pending Finance HOD approval
      edit_Mode = false;
      this.router.navigate(['/mft-fa-fhod-appr-add'], { state: { wf_id, task_id, edit_Mode, status__From_Assigned } });
    }
    else if (item.action === "Request Add-FIN" && item.status === "Q-FA") {  //query to Finance Admin
      this.router.navigate(['/mft-fa-fa-rqt-add'], { state: { wf_id, task_id, status__From_Assigned } });
    }
    else if (item.action === "Request Edit-FIN" && item.status === "P-FHOD") {  //pending Finance HOD approval
      edit_Mode = true;
      this.router.navigate(['/mft-fa-fhod-appr-add'], { state: { wf_id, task_id, edit_Mode, fee_detail_pk, status__From_Assigned } });
    }
    else if (item.action === "Request Edit-FIN" && item.status === "Q-FA") {  //query to Finance Admin
      this.router.navigate(['/mft-fa-fa-rqt-edit'], { state: { wf_id, task_id, fee_detail_pk, status__From_Assigned } });
    }


    //const queryParams = {
    //  wf_id: wf_id,
    //  task_id: task_id,
    //  edit_Mode: edit_Mode
    //}

    // this.router.navigate(['/mft-fa-fhod-appr-add'], { queryParams })
  }

  async viewSeletedOtcRC(item: any) {

    const mtt_id = item.mtt_id;
    const otc_rc_id = item.otc_rc_id;
    const rc_type = item.rc_type;
    const status__From_Assigned = item.rc_status;

    const validResponse = await this.checkCurrentOTCRCStatus(item.otc_rc_id);
    if (!validResponse) {
      return;
    }

    // console.log("status__From_Assigned" + status__From_Assigned);
    // console.log("currentOTCRCStatus" + this.currentOTCRCStatus);
    if(status__From_Assigned !== this.currentOTCRCStatus){
      this.showTaskNotExistAlertBox();
      this.loadOtcRCData();
      return;
    }

    if (item.rc_status === 'PS') { //pending otc supervisor
      this.router.navigate(['/rcpt-ccl-app-and-just'], { state: { mtt_id, otc_rc_id, rc_type } });
    }
    if (item.rc_status === 'PEMV' || item.rc_status === 'PCR' || item.rc_status === 'VF') { //pending emv or cash or failed
      this.router.navigate(['/update-task-status'], { state: { mtt_id, otc_rc_id, rc_type } });
    }
  }

  viewSeletedRefund(item: any) {
    // TO-DO
    const rtt_app_no = item.rtt_app_no;
    const task_id = item.task_id;

    // Define HTTP headers
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    // Define API endpoint & request body
    const url = `${environment.apiUrl}/api/refundl/v1/getrttwfid`;
    const requestBody = {
      i_rtt_app_no: rtt_app_no,
    };

    this.http.post(url, requestBody, { headers }).subscribe(
      (response: any) => {
        // You can process the response data here
        this.refund_ty = response.data[0].refund_ty;

        // use the refund_ty to determine which form to navigate to
        if (item.rtt_status === 'Pending Finance Admin' || item.rtt_status === 'Pending SME' || item.rtt_status ==='Pending PG' || item.rtt_status === 'Preview Refund Submission') { //pending finance admin
          if(this.refund_ty === 'RF'){
            console.log("refund-approval-blank-form");
            this.router.navigate(['/refund-approval-blank-form'], { state: { rtt_app_no, task_id, refund_ty: this.refund_ty } });
          }else{
            this.router.navigate(['/refund-approval-fa'], { state: { rtt_app_no, task_id } });
    
          }
        } else if (item.rtt_status === 'Pending FSM/FHOD' || item.rtt_status === 'Pending DCEO' || item.rtt_status === 'Pending CEO') {
          if(this.refund_ty === 'RF'){
            console.log("refund-approval-blank-form");
            this.router.navigate(['/refund-approval-blank-form'], { state: { rtt_app_no, task_id, refund_ty: this.refund_ty } });
          }else{
            this.router.navigate(['/refund-approval-userrole'], { state: { rtt_app_no, task_id } });    
          }                   
        }

      },
      (error) => {
        console.error(error);
        this.isLoading = false;
        this.showGenericAlertBox();
        // Handle errors here
      }
    );
  }

  viewSeletedBilling(item: any) {
    const billing_no = item.task_id;

    const wf_id = item.wf_id;
    const task_id = item.task_id;
    const status__From_Assigned = item.status;
    const fee_detail_pk = item.fee_detail_pk

    switch(item.bil_wf_status){
      case 'New':
        this.router.navigate(['/my-task-assigned-tasks/billing-approval'], { queryParams: { billing_no }});
        break;
      case 'New Cancellation':
        this.router.navigate(['/my-task-assigned-tasks/billing-cancellation-approval'], { queryParams: { billing_no }});
        break;
      case 'New Adjustment':
        this.router.navigate(['/my-task-assigned-tasks/billing-adjustment-approval'], { queryParams: { billing_no }});
        break;
      case 'WF-N':
        this.router.navigate(['/my-task-assigned-tasks/billing-approval'], { queryParams: { billing_no }});
        break;
      case 'WF-CN':
        this.router.navigate(['/my-task-assigned-tasks/billing-cancellation-approval'], { queryParams: { billing_no }});
        break;
      case 'WF-AN':
        this.router.navigate(['/my-task-assigned-tasks/billing-adjustment-approval'], { queryParams: { billing_no }});
        break;
      default:
        this.router.navigate(['/my-task-assigned-tasks/billing-details'], { queryParams: { billing_no }});
        break;
    }
  }

  viewSelectedCCC(item: any){
    const task_no = item.taskId;
    this.router.navigate(['/my-task-assigned-tasks/credit-control-case'], { queryParams: { task_no }});
  }

  loadMftData() {
    const urlMftWF = environment.apiUrl + '/api/mftwf/v1/getmasterfeetableworkflow';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    // Create the request body with your form data
    const Body: any = {

      i_page: this.page.toString(),
      i_size: this.itemsPerPage.toString(),

    };

    if (this.taskId !== null) {
      // use a regular expression to extract the number from the string
      const match = this.taskId.match(/\d+/);

      // check if there is a match and convert it to a number
      this.extractedNumber = match ? +match[0] : null;
    }
    else {
      this.extractedNumber = null;
    }

    Body.i_wf_id = this.extractedNumber;     //is call taskId here because search filter use task id

    if (this.feeDetailId && this.feeDetailId.trim()) {
      Body.i_fee_detail_id = this.feeDetailId;
    }

    Body.i_assign_to = this.username;

    if (this.status && this.status.trim()) {
      Body.i_status = this.status;
    }

    Body.i_created_by = null;
    Body.i_modified_by = null;

    if (this.modifiedByNm && this.modifiedByNm.trim()) {
      Body.i_modified_by_nm = this.modifiedByNm;
    }

    if (this.selected && this.selected.length > 0) {
      Body.i_dt_modified_fr = formatDate(this.selected[0], 'YYYY-MM-dd', 'en');//.format('YYYY-MM-DD');
      this.selected[1].setDate(this.selected[1].getDate() + 1,);
      Body.i_dt_modified_to = formatDate(this.selected[1], 'YYYY-MM-dd', 'en');
    }

    Body.i_dt_created_fr = null;
    Body.i_dt_created_to = null;
    Body.i_dt_effective_fr = null;
    Body.i_dt_effective_to = null;
    Body.i_ss_cd = null;
    Body.i_wf_is_in_prg = "t";

    this.isDisplay = true;
    this.isLoading = true;
    this.http.post(urlMftWF, Body, { headers }).subscribe(
      (response: any) => {
        // You can process the response data here

        if (response.data.length === 0) {
          this.totalRecords = 0;
          this.isDisplay = false;
          //this.showResultAlertBox();
          this.isLoading = false;
        }
        else {
          this.mftwfs = response.data;
          this.totalRecords = response.data[0].total;
          this.AlertBoxInitialize();
          this.DefaultBox();
          this.isDisplay = true;
          this.isLoading = false;
        }

      },
      (error) => {
        console.error(error);
        this.isLoading = false;
        this.showGenericAlertBox();
        // Handle errors here
      });
  }

  loadOtcRCData() {
    const url = environment.apiUrl + '/api/otcrcptccl/v1/getotcreceiptcancellationmytasklisting';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    // Create the request body with your form data
    const Body: any = {

      i_page: this.otcRCPage.toString(),
      i_size: this.otcRCItemsPerPage.toString(),

    };

    if (this.taskId !== null) {
      // use a regular expression to extract the number from the string
      const match = this.taskId.match(/\d+/);

      // check if there is a match and convert it to a number
      this.extractedNumber = match ? +match[0] : null;
    }
    else {
      this.extractedNumber = null;
    }

    Body.i_task_id = this.extractedNumber;     //is call taskId here because search filter use task id

    Body.i_rc_type = this.otcReceiptCancellationTaskDescription; //use number

    if (this.counterID && this.counterID.trim()) {
      Body.i_counter_id = this.counterID;
    }


    if (this.otcRCRequestedBy && this.otcRCRequestedBy.trim()) {
      Body.i_requsted_by_nm = this.otcRCRequestedBy; //use i_requsted_by_nm because search name
    }

    if (this.status && this.status.trim()) {
      Body.i_rc_status = this.status;
    }

    Body.i_assigned_to = this.username;

    this.isDisplayOtcRC = true;
    this.isLoading = true;
    this.http.post(url, Body, { headers }).subscribe(
      (response: any) => {
        // console.log(response);
        // You can process the response data here

        if (response.data.length === 0) {
          this.otcRCTotalRecords = 0;
          this.isDisplayOtcRC = false;
          //this.showResultAlertBox();
          this.isLoading = false;
        }
        else {
          this.OTCReceiptCancellationMyTaskListing = response.data;
          this.otcRCTotalRecords = response.data[0].total;
          this.AlertBoxInitialize();
          this.DefaultBox();
          this.isDisplayOtcRC = true;
          this.isLoading = false;
        }

      },
      (error) => {
        console.error(error);
        this.isLoading = false;
        this.showGenericAlertBox();
        // Handle errors here
      });
  }

  loadRefundData() {
    const url = environment.apiUrl + '/api/rtt/v1/getrefundlisting';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    // Create the request body with your form data
    const Body: any = {

      i_page: this.refundPage.toString(),
      i_size: this.refundItemsPerPage.toString(),
      i_username: this.username,
      i_user_role: this.roles,
      i_my_task_mode: "A",

    };
    console.log(Body);

    if (this.taskId !== null) {
      // use a regular expression to extract the number from the string
      const match = this.taskId.match(/\d+/);

      // check if there is a match and convert it to a number
      this.extractedNumber = match ? +match[0] : null;
    }
    else {
      this.extractedNumber = null;
    }

    Body.i_task_id = this.extractedNumber;     //is call taskId here because search filter use task id

    if (this.refundTaskDescription && this.refundTaskDescription.trim()) {
      Body.i_task_desc = this.refundTaskDescription;
    }

    if (this.refundApplicationNo && this.refundApplicationNo.trim()) {
      Body.i_rtt_app_no = this.refundApplicationNo;
    }

    if (this.refundRequestedBy && this.refundRequestedBy.trim()) {
      Body.i_requested_by = this.refundRequestedBy;
    }

    Body.i_dt_requested = this.refundDateRequested ? formatDate(this.refundDateRequested, 'YYYY-MM-dd', 'en') : null;
    Body.i_dt_pick = this.refundDatePickup ? formatDate(this.refundDatePickup, 'YYYY-MM-dd', 'en') : null;

    if (this.refundStatus && this.refundStatus.trim()) {
      Body.i_rtt_status = this.refundStatus;
    }
    this.isDisplayRefund = true;
    this.isLoading = true;

    this.http.post(url, Body, { headers }).subscribe(
      (response: any) => {
        // You can process the response data here

        if (response.data.length === 0) {
          this.refundTotalRecords = 0;
          this.isDisplayRefund = false;
          //this.showResultAlertBox();
          this.isLoading = false;
        }
        else {
          this.RefundMyTaskListing = response.data;
          console.log(this.RefundMyTaskListing);
          this.refundTotalRecords = response.data[0].total;
          this.AlertBoxInitialize();
          this.DefaultBox();
          this.isDisplayRefund = true;
          this.isLoading = false;
        }

      },
      (error) => {
        console.error(error);
        this.isLoading = false;
        this.showGenericAlertBox();
        // Handle errors here
      });
  }

  loadBillingData() {
    const url = environment.apiUrl + '/api/bil/v1/getbillinglisting';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    // Create the request body with your form data
    const Body: any = {

      i_page: this.billingPage.toString(),
      i_size: this.billingItemsPerPage.toString(),
      i_username: this.username,
      i_my_task_mode: "A",

    };

    if (this.taskId !== null) {
      // use a regular expression to extract the number from the string
      const match = this.taskId.match(/\d+/);

      // check if there is a match and convert it to a number
      this.extractedNumber = match ? +match[0] : null;
    }
    else {
      this.extractedNumber = null;
    }

    Body.i_task_id = this.extractedNumber;     //is call taskId here because search filter use task id

    if (this.billingTaskDescription && this.billingTaskDescription.trim()) {
      Body.i_task_desc = this.billingTaskDescription;
    }

    if (this.billingApprovalStatus && this.billingApprovalStatus.trim()) {
      Body.i_approval_status = this.billingApprovalStatus;
    }

    if (this.billingRequestedBy && this.billingRequestedBy.trim()) {
      Body.i_requested_by = this.billingRequestedBy;
    }

    Body.i_dt_requested = this.billingDateRequested ? formatDate(this.billingDateRequested, 'YYYY-MM-dd', 'en') : null;

    this.isDisplayBilling = true;
    this.isLoading = true;
    this.http.post(url, Body, { headers }).subscribe(
      (response: any) => {
        // You can process the response data here

        if (response.data.length === 0) {
          this.billingTotalRecords = 0;
          this.isDisplayBilling = false;
          //this.showResultAlertBox();
          this.isLoading = false;
        }
        else {
          this.BillingMyTaskListing = response.data;
          this.billingTotalRecords = response.data[0].total;
          this.AlertBoxInitialize();
          this.DefaultBox();
          this.isDisplayBilling = true;
          this.isLoading = false;
        }

      },
      (error) => {
        console.error(error);
        this.isLoading = false;
        this.showGenericAlertBox();
        // Handle errors here
      });
  }

  loadCCCData(){
    const url = environment.apiUrl + '/api/cc/v1/tasklist'; 

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });
 
    const Body: any = {
      i_page: this.ccPage,
      i_size: this.ccItemsPerPage,
      i_task_mode: 'A',
    };
 
    if(this.taskId != null) 
      Body.i_task_id = this.taskId;
    if(this.ccTaskStatus != null)
      Body.i_task_status = this.ccTaskStatus;
    if(this.ccPymtStatus != null)
      Body.i_payment_status = this.ccPymtStatus;
    if(this.ccTxnType != null)
      Body.i_txn_type = this.ccTxnType;
    if(this.ccCaseNo != null)
      Body.i_case_no = this.ccCaseNo;
    
    this.isDisplay = true;
    this.isLoading = true;
    this.isLoadingCCData = true;
    this.http.post(url, Body, { headers }).subscribe(
     (response: any) => {
        console.log(response);
       // You can process the response data here

       if (response.data.length === 0) {
         this.ccTotalRecords = 0;
         this.isDisplay = false;
         //this.showResultAlertBox();
         this.isLoading = false;
       }
       else {
         this.creditControlCaseListing = response.data;
         this.ccTotalRecords = response.data[0].total;
         this.isDisplay = true;
         this.AlertBoxInitialize();
         this.DefaultBox();
         this.isLoading = false;
       }
      this.isLoadingCCData = false;
     },
     (error) => {
       console.error(error);
       this.isLoading = false;
       this.showGenericAlertBox();
        this.isLoadingCCData = false;
       // Handle errors here
     });
  }

  populateParam(i_param_grp_nm: string){
    const url = environment.apiUrl + '/api/rms/v1/getparam';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json'
    });

    // Create the request body with your form data
    const requestBody = {
      i_page: environment.DefaultPage,
      i_size: environment.DropDownSize,
      i_param_cd: null,
      i_param_grp_nm: i_param_grp_nm
    };
    // Send an HTTP POST request to the API
    this.http.post(url, requestBody, { headers }).subscribe(
      (response: any) => {
        if (response.data.length === 0) {
          console.error('Invalid response format:', response);
          return;
        }
        else if(i_param_grp_nm == 'cc-case')
          this.ccTaskStatusOption = response.data;
        else if(i_param_grp_nm == 'cc-pymt-status')
          this.ccTaskPymtTypeOption = response.data;
        else if(i_param_grp_nm == 'cc-txn-type')
          this.ccTaskTxnTypeOption = response.data;
      },
      (error) => {
        console.error('There was an error retrieving the params:', error);
      });
  }

  populateMftStatus() {
    const url = environment.apiUrl + '/api/rms/v1/getparam';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json'
    });

    // Create the request body with your form data
    const requestBody = {
      i_page: environment.DefaultPage,
      i_size: environment.DropDownSize,
      i_param_cd: null,
      i_param_grp_nm: 'Status-MFT'
    };

    // Send an HTTP POST request to the API
    this.http.post(url, requestBody, { headers }).subscribe(
      (response: any) => {
        if (response.data.length === 0) {
          console.error('Invalid response format:', response);
        }
        else {
          this.statusOptions = response.data;
        }

      },
      (error) => {
        console.error('There was an error retrieving the status:', error);
        // Handle API errors (e.g., show an error message)
      }
    );
  }

  populateOtcRCStatus() {
    const url = environment.apiUrl + '/api/rms/v1/getparam';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json'
    });

    // Create the request body with your form data
    const requestBody = {
      i_page: environment.DefaultPage,
      i_size: environment.DropDownSize,
      i_param_cd: null,
      i_param_grp_nm: 'OTC-RC'
    };

    // Send an HTTP POST request to the API
    this.http.post(url, requestBody, { headers }).subscribe(
      (response: any) => {
        if (response.data.length === 0) {
          console.error('Invalid response format:', response);
        }
        else {
          this.otcRCStatusOptions = response.data;
        }

      },
      (error) => {
        console.error('There was an error retrieving the status:', error);
        // Handle API errors (e.g., show an error message)
      }
    );
  }

  populateRefundStatus() {
    const url = environment.apiUrl + '/api/rms/v1/getparam';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json'
    });

    // Create the request body with your form data
    const requestBody = {
      i_page: environment.DefaultPage,
      i_size: environment.DropDownSize,
      i_param_cd: null,
      i_param_grp_nm: 'RTT-Status'
    };

    // Send an HTTP POST request to the API
    this.http.post(url, requestBody, { headers }).subscribe(
      (response: any) => {
        if (response.data.length === 0) {
          console.error('Invalid response format:', response);
        }
        else {
          this.refundStatusOption = response.data;
        }

      },
      (error) => {
        console.error('There was an error retrieving the status:', error);
        // Handle API errors (e.g., show an error message)
      }
    );
  }

  populateRefundType() {
    const url = environment.apiUrl + '/api/rms/v1/getparam';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json'
    });

    // Create the request body with your form data
    const requestBody = {
      i_page: environment.DefaultPage,
      i_size: environment.DropDownSize,
      i_param_cd: null,
      i_param_grp_nm: 'RTT-RefundType'
    };

    // Send an HTTP POST request to the API
    this.http.post(url, requestBody, { headers }).subscribe(
      (response: any) => {
        if (response.data.length === 0) {
          console.error('Invalid response format:', response);
        }
        else {
          this.refundTaskDescriptionOption = response.data;
        }

      },
      (error) => {
        console.error('There was an error retrieving the status:', error);
        // Handle API errors (e.g., show an error message)
      }
    );
  }

  populateBillingStatus() {
    const url = environment.apiUrl + '/api/rms/v1/getparam';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json'
    });

    // Create the request body with your form data
    const requestBody = {
      i_page: environment.DefaultPage,
      i_size: environment.DropDownSize,
      i_param_cd: null,
      i_param_grp_nm: 'Billing-Status'
    };

    // Send an HTTP POST request to the API
    this.http.post(url, requestBody, { headers }).subscribe(
      (response: any) => {
        if (response.data.length === 0) {
          console.error('Invalid response format:', response);
        }
        else {
          this.billingApprovalStatusOption = response.data;
        }

      },
      (error) => {
        console.error('There was an error retrieving the status:', error);
        // Handle API errors (e.g., show an error message)
      }
    );
  }

  getCCTaskStatus(entityCode: string | null): string {
    if (!entityCode) {
      return '';
    }
    const entity = this.ccTaskStatusOption.find((option) => option.param_cd === entityCode);
    return entity ? entity.nm_en : entityCode;
  }

  getCCTaskPymtStatus(entityCode: string | null): string {
    if (!entityCode) {
      return '';
    }
    const entity = this.ccTaskPymtTypeOption.find((option) => option.param_cd === entityCode);
    return entity ? entity.nm_en : entityCode;
  }
  
  getCCTaskTxnType(entityCode: string | null): string {
    if (!entityCode) {
      return '';
    }
    const entity = this.ccTaskTxnTypeOption.find((option) => option.param_cd === entityCode);
    return entity ? entity.nm_en : entityCode;
  }
  
  apply() {
    this.rightSectionCollapsed = true;
    // Wilson's
    switch (this.currentTab) {
      case 'mft':
        this.loadMftData();
        break;
      case 'refund':
        this.loadRefundData();
        break;
      case 'billing':
        this.loadBillingData();
        break;
      case 'otc':
        switch (this.currentOTCMode) {
          case 'otc-rc':
            this.loadOtcRCData();
            break;
          default:
            break;
        }
        break;
      case 'ccc':
        this.loadCCCData();
        break;
      default:
        break;
    }
  }

  reset() {
    this.taskId = null;
    this.feeDetailId = null;
    this.modifiedByNm = null;
    this.bsValue = new Date();
    this.minDate.setMonth(this.bsValue.getMonth() - 1);
    this.selected = [this.minDate, this.bsValue];
    this.selected = [];
    this.status = null;
    this.otcReceiptCancellationTaskDescription = null;
    this.counterID = null;
    this.requestedBy = null;
    this.refundTaskDescription = null;
    this.refundApplicationNo = null;
    this.refundStatus = null;
    this.refundDateRequested = null;
    this.refundDatePickup = null;
    this.refundRequestedBy = null;
    this.billingTaskDescription = null;
    this.billingApprovalStatus = null;
    this.billingDateRequested = null;
    this.billingRequestedBy = null;
    this.otcRCRequestedBy = null;
    this.otcRCStatus = null;
    this.otcRCDateRequested = null;
    this.ccTaskStatus = null;
    this.ccPymtStatus = null;
    this.ccTxnType = null;
    this.ccCaseNo = null;
  }

  async checkCurrentMFTWFStatus(wfid:number): Promise<boolean> {

    const urlMftWF = environment.apiUrl + '/api/mftwf/v1/getmasterfeetableworkflow';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json'
    });


    // Create the request body with your form data
    const Body: any = {

      i_page: this.page.toString(),
      i_size: "1",
      i_wf_id: wfid,
      i_wf_is_in_prg: "t"
    };

    try {
      const response: any = await this.http.post(urlMftWF, Body, { headers }).toPromise();
      // console.log("Ast is "+ response.header.statusCode)
      if (response.header.statusCode === '00') {
        // this.totalRecords = response.data[0].total;
        this.forCheckingMFTWFS = response.data;
        this.currentMFTWFStatus = this.forCheckingMFTWFS[0].status;
        return true;
      } else {
        // this.totalRecords = 0;
        console.error('Invalid master fee table work flow history response format:', response);
        return false;
      }
    } catch (error) {
      console.error('There was an error retrieving the master fee table work flow history:', error);
      return false;
    }
  }

  async loadCounterInfo() {
      const permUrl = environment.apiUrl + '/api/otc/v1/checkinstatus';
      // Make the HTTP GET request
      const headers = new HttpHeaders({
        Authorization: environment.authKey,
        'Content-Type': 'application/json'
      });
      var requestBody: { [k: string]: any } = {
        i_session_id: localStorage.getItem('otcSession')
      };
      this.http.post(permUrl, requestBody, { headers }).subscribe(
        (response: any) => {
          this.counterCheckInStatus.data = response.data;
          if (this.counterCheckInStatus.data.counter_id.length > 0) {
            //this.counterTitle = 'Counter ID: ' + this.counterCheckInStatus.data.counter_id + ' | ';
            // this.otcCounterId = this.counterCheckInStatus.data.otc_counter_id;
            // this.counterID = this.counterCheckInStatus.data.counter_id;
            this.OTCCheckedIn = 1;
            console.log('Counter ID: ' + this.counterCheckInStatus.data.counter_id);
            console.log('OTC Counter ID: ' + this.counterCheckInStatus.data.otc_counter_id);
          }
        },
        (error) => {
          console.log(error);
          this.counterCheckInStatus.data = ''; //still update something to push the observer
          this.OTCCheckedIn = 0;
        }
      );
  }

  async checkCurrentOTCRCStatus(otcrcid:number): Promise<boolean> {

    const url = environment.apiUrl + '/api/otcrcptccl/v1/getotcreceiptcancellationmytasklisting';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json'
    });


    // Create the request body with your form data
    const Body: any = {

      i_page: this.page.toString(),
      i_size: "1",
      i_otc_rc_id: otcrcid
    };

    try {
      const response: any = await this.http.post(url, Body, { headers }).toPromise();
      // console.log("Ast is "+ response.header.statusCode)
      if (response.header.statusCode === '00') {
        // this.totalRecords = response.data[0].total;
        this.forCheckingOTCRCS = response.data;
        this.currentOTCRCStatus = this.forCheckingOTCRCS[0].rc_status;
        return true;
      } else {
        // this.totalRecords = 0;
        console.error('Invalid otc receipt cancellation work flow history response format:', response);
        return false;
      }
    } catch (error) {
      console.error('There was an error retrieving the otc receipt cancellation flow history:', error);
      return false;
    }
  }

}
