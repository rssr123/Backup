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
import { OTCReceiptCancellationMyTaskListing } from 'src/app/core/models/otc-receipt-cancellation.interface';
import { RefundMyTaskListing } from 'src/app/core/models/refund.interface';
import { BillingMyTaskListing } from 'src/app/core/models/billing.interface';

@Component({
  selector: 'app-my-task-created-task',
  templateUrl: './my-task-created-task.component.html',
  styleUrls: ['./my-task-created-task.component.scss'],
  animations: [fadeInOut]
})
export class MyTaskCreatedTaskComponent implements OnInit {

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

  //for drop down
  dropDownDisplayRecords = 1000;

  refundTaskDescriptionOption: Param[] = [];
  refundStatusOption: Param[] = [];
  billingApprovalStatusOption: Param[] = [];

  //variable
  statusOptions: Param[] = [];
  otcRCStatusOptions: Param[] = [];
  initialNumber = [
    { value: 0, label: 'No' },
    { value: 1, label: 'Yes' },
  ]

  mftwfs: MFTWF[] = [];
  forCheckingMFTWFS: MFTWF[] = [];
  currentMFTWFStatus: string | null = null;

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
  currentTaskMode: string = 'C'; // Default to Assigned

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

  querySubmittedBox: boolean = false;
  cancelledBox: boolean = false;

  //date range picker
  selected!: Date[];//{ start?: moment.Moment; end?: moment.Moment };
  bsValue = new Date();
  tempDate !: Date;
  minDate = new Date();
  //date range picker

  status: string | null = null;
  alertMessage: string | undefined = undefined;
  showTaskNotUpdateAlert: boolean | undefined = undefined;

  permCT = perm.Created_Task_Listing ;// all the perm_cd for this module seperated with comma
  permCTAllow = ""; // variable to store allowed permission for the user
  permListAllow: number = 0; // if 0 then not allow to view listing page, else allow
  // permUploadAllow: number = 0; 
  // permDownloadAllow: number = 0; 
  // permCancelViewAllow: number = 0; 
  // permReconViewAllow: number = 0;

  otcReceiptCancellationTaskDescription: number | null = null; //number because it is rctype
  counterID: string | null = null;
  otcRcAssignedTo: string | null = null;
  otcRCStatus: string | null = null;
  refundTaskDescription: string | null = null;
  refundStatus: string | null = null;
  refundApplicationNo: string | null = null;
  refundAssignedTo: string | null = null;
  refundDateRequested: Date | null = null;
  billingTaskDescription: string | null = null;
  billingApprovalStatus: string | null = null;
  billingDateRequested: Date | null = null;
  billingAssignedTo: string | null = null;
  otcRCRequestedBy: string | null = null;
  otcRCDateRequested: Date | null = null;
  ccTaskStatus: string | null = null;
  ccPymtStatus: string | null = null;
  ccTxnType: string | null = null;
  ccCaseNo: string | null = null;

  //alert start
  showResultAlert = false;
  showResultAlertBox() {
    this.showResultAlert = true;
    setTimeout(() => this.showResultAlert = false, 2000);
  }

  showQuerySubmittedAlert = false;
  showQuerySubmittedAlertBox() {
    this.showQuerySubmittedAlert = true;
    setTimeout(() => this.showQuerySubmittedAlert = false, 2000);
  }

  showCancelledAlert = false;
  showCancelledAlertBox() {
    this.showCancelledAlert = true;
    setTimeout(() => this.showCancelledAlert = false, 2000);
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
    this.cancelledBox = false;
    this.querySubmittedBox = false;
  }

  AlertBoxInitialize() {
    if (this.cancelledBox) {
      this.showCancelledAlertBox();
    } else if (this.querySubmittedBox) {
      this.showQuerySubmittedAlertBox();
    }
  }

  constructor(
    private http: HttpClient, 
    config: NgbPaginationConfig, 
    private router: Router,
    private authService: AuthService
  ) {
    config.maxSize = 3;
    config.boundaryLinks = true;
    var tmp = window.location.href.split('/');
    if(tmp[tmp.length-1] != 'my-task-created-task')
      this.currentTab = tmp[tmp.length-1];
  }

  ngOnInit(): void {
   this.bsValue = new Date();
   this.minDate.setMonth(this.bsValue.getMonth() - 1);

    //put default box before alert message
    this.DefaultBox()
    this.alertMessage = history.state.alert_msg;
    this.showTaskNotUpdateAlert = history.state.showTaskNotUpdateAlert;

    if (this.alertMessage !== undefined) {

      if (this.alertMessage === "cancelled") {
        this.cancelledBox = true;
      }
      else if (this.alertMessage === "submitted") {
        this.querySubmittedBox = true;
      }
    }

    if(this.showTaskNotUpdateAlert === true){
      this.showTaskNotExistAlertBox();
    }

    // Reset alert_msg in history state so if refresh page, alert message will not persist
    history.replaceState({ ...history.state, alert_msg: undefined, showTaskNotUpdateAlert: undefined }, '');

    this.populateMftStatus();
    this.checkPermissions();
  }

  checkPermissions(){
    this.authService.checkUserRole(this.authService.username, this.permCT)
   .subscribe(
     (response: any) => {
        this.permCTAllow = response.data;
        this.permListAllow = this.permCTAllow.includes(perm.Created_Task_Listing) ? 1 : 0;
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
    let edit_Mode : boolean = false;
    let show_requester_table : boolean = false;
    const from_route = 1;

    const validResponse = await this.checkCurrentMFTWFStatus(wf_id);
    if (!validResponse) {
      return;
    }

    //console.log("currentMFTWFStatus" + this.currentMFTWFStatus);
    if(status__From_Assigned !== this.currentMFTWFStatus){
      this.showTaskNotExistAlertBox();
      this.loadMftData();
      return;
    }

    if(item.action === "Request Add" || item.action === "Request Add-FIN"){
      edit_Mode=false;
    }
    else{
      edit_Mode=true;
    }

    if(item.action === "Request Add-FIN" || item.action === "Request Edit-FIN"){
      show_requester_table=false;
    }
    else{
      show_requester_table=true;
    }
 
    if(item.status === "C" || item.status === "EFT" || item.status === "RJ-RHOD" || item.status === "RJ-FA" || item.status === "RJ-FHOD"){//route to there because got cancel button that route back to started page
      this.router.navigate(['/my-task-created-task/mft-item-task-list'], { state: {  wf_id, status__From_Assigned, task_id, fee_detail_pk, edit_Mode, show_requester_table, from_route } });
    }
    else{
      this.router.navigate(['/my-task-created-task/created-task-details'], { state: {  wf_id, status__From_Assigned, task_id, fee_detail_pk, edit_Mode, show_requester_table } });
   }
  }

  viewSeletedOtcRC(item: any) {

    const mtt_id = item.mtt_id;
    const otc_id = item.otc_id;
    const counter_id = item.counter_id;
    const otc_pymt_mode = item.otc_pymt_mode

    this.router.navigate(['/my-task-created-task/otc-rcpt-dets'], { state: { mtt_id, otc_id, counter_id, otc_pymt_mode } });
  }

  viewSeletedRefund(item: any) {
    const url = environment.apiUrl + '/api/refundl/v1/getrefundlisting';

    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });
  
    const Body: any = {
      i_page: 1,
      i_size: 1,
      i_rtt_app_no: item.rtt_app_no,
    };

    this.http.post(url, Body, { headers }).subscribe(
      (response: any) => {
        console.log(response);
        if (response.data && response.data.length > 0) {
          const refundData = response.data[0];
          const { mtt_id, orn_no, txn_id, rms_type, rtt_wf_id, refund_ty, rtt_status, rtt_app_no } = refundData;
  
          if (refund_ty !== 'RF') {
            this.router.navigate(['/refund-created-task-details'], { state: { mtt_id, orn_no, txn_id, rms_type, rtt_wf_id, refund_ty, rtt_status } });
          } else {
            this.router.navigate(['/refund-created-task-rf-details'], { state: { refund_ty, rtt_app_no } });
          }
        } else {
          console.error('No data found for the selected refund.');
        }
      },
      (error) => {
        console.error('Error fetching refund details:', error);
      }
    );
  }

  viewSeletedBilling(item: any) {
    const billing_no = item.task_id;
    if(!(this.authService.name ==  item.assigned_to || this.authService.username == item.assigned_to || item.assigned_to == null)){
      this.router.navigate(['/my-task-created-task/billing-details'], { queryParams: { billing_no }});
      return;
    }
    switch(item.bil_wf_status.trim()){
      case 'New':
        this.router.navigate(['/my-task-created-task/billing-approval'], { queryParams: { billing_no }});
        break;
      case 'New Cancellation':
        this.router.navigate(['/my-task-created-task/billing-cancellation-approval'], { queryParams: { billing_no }});
        break;
      case 'New Adjustment':
        this.router.navigate(['/my-task-created-task/billing-adjustment-approval'], { queryParams: { billing_no }});
        break;
      case 'WF-N':
        this.router.navigate(['/my-task-created-task/billing-approval'], { queryParams: { billing_no }});
        break;
      case 'WF-CN':
        this.router.navigate(['/my-task-created-task/billing-cancellation-approval'], { queryParams: { billing_no }});
        break;
      case 'WF-AN':
        this.router.navigate(['/my-task-created-task/billing-adjustment-approval'], { queryParams: { billing_no }});
        break;
      default:
        this.router.navigate(['/my-task-created-task/billing-details'], { queryParams: { billing_no }});
        break;
    }
  }

  viewSelectedCCC(item: any){
    const task_no = item.taskId;
    this.router.navigate(['/my-task-created-task/credit-control-case-view'], { queryParams: { task_no }});
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
      i_wf_is_in_prg: 't',

    };

   if (this.taskId !== null) {
    // use a regular expression to extract the number from the string
    const match = this.taskId.match(/\d+/);

    // check if there is a match and convert it to a number
    this.extractedNumber = match ? +match[0] : null;
  }
  else{
    this.extractedNumber = null;
  }

    Body.i_wf_id = this.extractedNumber;     //is call taskId here because search filter use task id

    if (this.feeDetailId && this.feeDetailId.trim()) {
      Body.i_fee_detail_id = this.feeDetailId;
    }

    Body.i_assign_to = null;

    if (this.status && this.status.trim()) {
      Body.i_status = this.status;
    }

    Body.i_created_by = this.username;
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
      }
    );
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

    Body.i_rc_type = this.otcReceiptCancellationTaskDescription;

    if (this.counterID && this.counterID.trim()) {
      Body.i_counter_id = this.counterID;
    }


    if (this.otcRcAssignedTo && this.otcRcAssignedTo.trim()) {
      Body.i_assigned_to_nm = this.otcRcAssignedTo; //use i_assigned_to_nm because search name
    }

    if (this.status && this.status.trim()) {
      Body.i_rc_status = this.status;
    }

    Body.i_requested_by = this.username;


    this.isDisplay = true;
    this.isLoading = true;
    this.http.post(url, Body, { headers }).subscribe(
      (response: any) => {
        // You can process the response data here

    if (response.data.length === 0) {
      this.otcRCTotalRecords = 0;
      //this.showResultAlertBox();
      this.isDisplayOtcRC = false;
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
    //console.log(response.data);
    // console.log(this.totalRecords);

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
       i_my_task_mode: "C",

     };

     if (this.taskId !== null) {
      // use a regular expression to extract the number from the string
      const match = this.taskId.match(/\d+/);

      // check if there is a match and convert it to a number
      this.extractedNumber = match ? +match[0] : null;
    }
    else{
      this.extractedNumber = null;
    }

      Body.i_task_id = this.extractedNumber;     //is call taskId here because search filter use task id

     if (this.refundTaskDescription && this.refundTaskDescription.trim()) {
       Body.i_task_desc = this.refundTaskDescription;
     }

     if (this.refundApplicationNo && this.refundApplicationNo.trim()) {
       Body.i_rtt_app_no = this.refundApplicationNo;
     }

     if (this.refundAssignedTo && this.refundAssignedTo.trim()) {
      Body.i_assigned_to = this.refundAssignedTo;
    }

      Body.i_dt_requested = this.refundDateRequested ? formatDate(this.refundDateRequested, 'YYYY-MM-dd', 'en') : null;

      if (this.refundStatus && this.refundStatus.trim()) {
        Body.i_rtt_status = this.refundStatus;
      }
 
     this.isDisplay = true;
     this.isLoading = true;
     this.http.post(url, Body, { headers }).subscribe(
     (response: any) => {
        console.log(response);
       // You can process the response data here

       if (response.data.length === 0) {
         this.refundTotalRecords = 0;
         this.isDisplayRefund = false;
         //this.showResultAlertBox();
         this.isLoading = false;
       }
       else {
         this.RefundMyTaskListing = response.data;
         this.refundTotalRecords = response.data[0].total;
         this.AlertBoxInitialize();
         this.DefaultBox();
         this.isDisplayRefund = true;
         this.isLoading = false;
       }
       //console.log(response.data);
       // console.log(this.totalRecords);

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
       i_my_task_mode: "C",

     };
 
     if (this.taskId !== null) {
       // use a regular expression to extract the number from the string
       const match = this.taskId.match(/\d+/);
 
       // check if there is a match and convert it to a number
       this.extractedNumber = match ? +match[0] : null;
     }
     else{
       this.extractedNumber = null;
     }

       Body.i_task_id = this.extractedNumber;     //is call taskId here because search filter use task id

        if (this.billingTaskDescription && this.billingTaskDescription.trim()) {
          Body.i_task_desc = this.billingTaskDescription;
        }

        if (this.billingApprovalStatus && this.billingApprovalStatus.trim()) {
          Body.i_approval_status = this.billingApprovalStatus;
        }

        if (this.billingAssignedTo && this.billingAssignedTo.trim()) {
          Body.i_assigned_to = this.billingAssignedTo;
        }

        Body.i_dt_requested = this.billingDateRequested ? formatDate(this.billingDateRequested, 'YYYY-MM-dd', 'en') : null;
     this.isDisplay = true;
     this.isLoading = true;
     this.http.post(url, Body, { headers }).subscribe(
     (response: any) => {
       // console.log(response);
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
         //console.log(this.BillingMyTaskListing);
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
      i_task_mode: 'C',
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
        //console.log(response);
       //// You can process the response data here

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
      i_page: this.page,
      i_size: this.dropDownDisplayRecords, //dont use item per page here because it is for table
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
      i_page: this.page,
      i_size: this.dropDownDisplayRecords, //dont use item per page here because it is for table
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
      i_page: this.page,
      i_size: this.dropDownDisplayRecords, //dont use item per page here because it is for table
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
    this.selected = [];
    this.status = null;
    this.otcReceiptCancellationTaskDescription = null;
    this.counterID = null;
    this.otcRcAssignedTo = null;
    this.refundTaskDescription = null;
    this.refundStatus = null;
    this.refundApplicationNo = null;
    this.refundAssignedTo = null;
    this.refundDateRequested = null;  
    this.billingTaskDescription = null;
    this.billingApprovalStatus = null;
    this.billingDateRequested = null;
    this.billingAssignedTo = null;
    this.otcRCRequestedBy = null;
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
}
