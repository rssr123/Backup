import { AfterViewInit, ChangeDetectorRef, Component, Input, OnInit, ViewChild, ViewChildren } from '@angular/core';
import { FeeGroup } from '../../core/models/fee-group';
import { MFTWFDoc, User } from '../../core/models/entity';
import { ActivatedRoute } from '@angular/router';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { environment } from 'src/environments/environment';
import { MFT, MFTWF, MFTWFHist } from '../../core/models/entity';
import { EMPTY, Observable, map } from 'rxjs';
import { forkJoin, of } from 'rxjs';
import { lastValueFrom } from 'rxjs';
import { DataService } from '../../core/services/data.service';
import { Systemstatus } from '../../shared/enums/systemstatus';
import { NgForm } from '@angular/forms';
import { Router } from '@angular/router';
import { MasterTaskListComponent } from '../../mastercomponent/master-task-list/master-task-list.component';
import { fadeInOut } from '../../shared/animation';
import { TranslateService } from '@ngx-translate/core';
import { GlobalService } from 'src/app/shared/global.service';
import { AuthService } from 'src/app/core/services/auth.service';
import { perm } from 'src/permissions/perm';
import { MatDialog } from '@angular/material/dialog';
import { MftCancelTaskComponent } from '../mft-cancel-task/mft-cancel-task.component';


@Component({
  selector: 'app-task-details',
  templateUrl: './task-details.component.html',
  styleUrls: ['./task-details.component.scss'],
  animations: [fadeInOut]
})
export class TaskDetailsComponent implements OnInit {

  username = this.authService.username;
  roles = this.authService.roles;

  isDisplaySupDoc: boolean = false;
  isLoadingSupDoc: boolean = false;
  isDisplayHist: boolean = false;
  isLoadingHist: boolean = false;

  assignTo: string | null = null;
  currentAssignTo: string | null = null;
  page = environment.DefaultPage;
  mftwfs: MFTWF[] = [];
  mftwfHis: MFTWFHist[] = [];
  users: User[] = [];
  mftwfSupDocs: MFTWFDoc[] = [];
  mfts: MFT[] = [];
  emailMftwfs: MFTWF[] = [];
  checkboxOptions: string[] | undefined = undefined;
  ssCdWithSpace: string | null = null;
  errorMessages: string[] = [];
  error: boolean = false;
  isLoading: boolean = false;
  totalRecords: number = 0;
  totalMFTRecords: number = 0;
  allowCancel: boolean = true;
  showActionTable: boolean = true;
  file_content = "";
  errorMessagesTaskClosedCanceled: string[] = [];
  errorTaskClosedCanceled: boolean = false;
  alertMessageTaskClosedCanceled: string | undefined = undefined;
  fromView: boolean = false;
  navigateNotRefresh: boolean = false;

  //email
  emailWfId: string | number | null = null;
  emailAssignTo: string | null = null;
  emailFeeDetailPk: string | number | null = null;
  emailfeeDetailId: string | null = null;
  emailTaskId: string | null = null;
  emailAction: string | null = null;


  //Supporting Document Table setting
  pageSupDoc = environment.DefaultPage;
  itemsPerPageSupDoc = environment.ItemPerPage;
  totalRecordsSupDoc: number = 0;

  //History Table setting
  pageHist = environment.DefaultPage;
  itemsPerPageHist = environment.ItemPerPage;
  totalRecordsHist: number = 0;

  navigateToAssignedTask = false;
  wfId: number | null = null;
  // displayFeeDetailId: string | null = null;
  displayRessignmentAction: boolean = true;
  pendingStatus: string | null = null;
  userRoleToReassign: string | null = null;
  displayUserRoleToReassign: string | null = null;
  editMode: boolean = false;
  showRequesterTable: boolean = false;
  submitMode: boolean = false;
  currentMFTWFDetail: MFTWF[] = [];
  currentMFTWFStatus: string | null = null;
  currentMFTWFAssignTo: string | null = null;

  requesterOrFA: string | null = null;
  requesterOrFAName: string | null = null;
  requesterHOD: string | null = null;
  rFeeDetNm: string | null = null;
  rFeeAmt: number | null = null;
  rSsCd: string | null = null;
  rPromoStartDt: Date | null = null;
  rPromoEndDt: Date | null = null;
  rPromoFee: number | null = null;
  rLlRequired: number = 0;
  rAddNotes: string | null = null;
  feeDetailPk: number | null = null;
  feeDetailId: string | null = null;
  feeGroupId: string | null = null;
  feeGrpNmEn: string | null = null;
  feeGrpNmBm: string | null = null;
  feeDetailNmE: string | null = null;
  feeDetailNmB: string | null = null;
  feeAmt: number | null = null;
  promoStartDate: Date | null = null;
  promoEndDate: Date | null = null;
  promoFee: number | null = null;
  taxCd: string | null = null;
  allowOTC: number | null = null;
  llParentId: string | null = null;
  llStartDay: number | null = null;
  llStartMth: number | null = null;
  llEndDay: number | null = null;
  llEndMth: number | null = null;
  ledgerCd: string | null = null;
  ssCd: string | null = null;
  dtCreated: Date | null = null;
  dtModified: Date | null = null;
  createdBy: string | null = null;
  createdByNm: string | null = null;
  modifiedBy: string | null = null;
  modifiedByNm: string | null = null;
  status: string | null = null;
  statusEn: string | null = null;
  effectiveDate: Date | null = null;
  textRemarks: string | null = null;
  ssm4uuserrefno: string | null = null;
  mftStatus: string | null = null;
  taskId: string | null = null;
  myTaskAssignTo: string | null = null;
  displayRequestedValue: boolean = false;
  displayCurrentValue: boolean = false;
  checkboxRssCd: string[] | undefined = undefined;
  rSsCdWithSpace: string | null = null;
  isPublic: number | null = null;

  //display for first column mft value
  currentFeeDetailId: string | null = null;
  currentFeeGroupNmEn: string | null = null;
  currentFeeDetailNmE: string | null = null;
  currentFeeDetailNmB: string | null = null;
  currentUnitFee: number | null = null;
  currentPromoStartDate: Date | null = null;
  currentPromoEndDate: Date | null = null;
  currentPromoFee: number | null = null;
  currentTaxCd: string | null = null;
  currentAllowOTC: number | null = null;
  currentLlParentId: string | null = null;
  currentLlStartDay: number | null = null;
  currentLlStartMth: number | null = null;
  currentLlEndDay: number | null = null;
  currentLlEndMth: number | null = null;
  currentLedgerCd: string | null = null;
  currentEffectiveDate: Date | null = null;
  currentCreatedBy: string | null = null;
  currentCreatedByNm: string | null = null;
  currentModifiedBy: string | null = null;
  currentModifiedByNm: string | null = null;
  currentDtCreated: Date | null = null;
  currentDtModified: Date | null = null;
  currentCheckboxSsCd: string[] | undefined = undefined;
  currentSsCdWithSpace: string | null = null;
  currentSsCd: string | null = null;
  currentStatus: string | null = null;
  cc: string | null = null;
  sendTo: string | null = null;
  currentisPublic: number| null = null;

  //default pagination for history
  selectedValueHist = environment.dropdownOptions[0];
  dropdownOptionsHist = environment.dropdownOptions;

  LoadDataHist(singleItem: number) {
    this.selectedValueHist = singleItem;
    this.itemsPerPageHist = this.selectedValueHist;
    this.loadDataHist();
  }

  //default pagination for supporting document
  selectedValueSupDoc = environment.dropdownOptions[0];
  dropdownOptionsSupDoc = environment.dropdownOptions;

  LoadDataSupDoc(singleItem: number) {
    this.selectedValueSupDoc = singleItem;
    this.itemsPerPageSupDoc = this.selectedValueHist;
    this.loadDataSupDoc();
  }

  permTaskDetails = perm.Master_Fee_Table_View_Task_Details + "," + perm.Master_Fee_Table_Cancel_Task; // all the perm_cd for this module seperated with comma
  permTaskDetailsAllow = ""; // variable to store allowed permission for the user
  permListAllow: number = 0; // if 0 then not allow to view listing page, else allow
  permCancelAllow: number = 0;

  constructor(
    private route: ActivatedRoute,
    private http: HttpClient,
    private router: Router,
    private cdref: ChangeDetectorRef,
    private translate: TranslateService,
    private globalService: GlobalService,
    public dialog: MatDialog,
    private authService: AuthService) {
    this.translate.setDefaultLang(this.globalService.getGlobalValue()); // default language
    this.translate.use(this.globalService.getGlobalValue());
  }

  async ngOnInit() {

    this.wfId = history.state.wf_id;
    this.pendingStatus = history.state.status__From_Assigned;
    this.taskId = history.state.task_id;
    // this.currentAssignTo = history.state.assign_to;
    this.feeDetailPk = history.state.fee_detail_pk;
    this.editMode = history.state.edit_Mode;
    this.showRequesterTable = history.state.show_requester_table;
    this.fromView = history.state.from_view;
    this.navigateNotRefresh = history.state.navigate_not_refresh;
    // this.alertMessageTaskClosedCanceled = history.state.alert_msg_task_canceled_closed;

    if (this.wfId !== undefined) {
      this.isLoading = true;

      // if (this.alertMessageTaskClosedCanceled !== undefined) {
      //   this.errorTaskClosedCanceled = true;
      //   this.errorMessagesTaskClosedCanceled.push(this.alertMessageTaskClosedCanceled);
      // }

      // history.replaceState({ ...history.state, alert_msg_task_canceled_closed: undefined }, '');

      await this.checkPermission(); //need to use await here because nned to assign permCancelAllow, if subscribe cannot capture correct value at allowcancel
      await this.checkStatusAndAssignTo(false)
      await this.populateForm();//await to load createdBy
      this.populateReassignmentUser();
      this.loadDataSupDoc();
      this.loadDataHist();
      this.toDisplayReassignmentAction();//need to put below populateForm to get status
      if (this.editMode === true) {
        await this.populateCurrentValueForm();
      }

      //check if roles contain super admin
      // let rolesContainSA = false;
      // if (this.roles !== null) {
      //   const rolesArray = this.roles.split(',');
      //   rolesContainSA = rolesArray.includes('SUPERADMIN');
      // }
      //console.log("Roles contain SA is " + rolesContainSA)

      // if (rolesContainSA === true) {
      if (this.status === 'RJ-RHOD' || this.status === 'RJ-FA' || this.status === 'RJ-FHOD' || this.status === 'C' || this.status === 'EFT') {
        this.allowCancel = false;
        this.showActionTable = false;
      }
      else {
        console.log("permCancelAllow is " + this.permCancelAllow);
        if (((this.editMode === true && this.totalMFTRecords > 0 && this.totalRecords > 0) ||
          (this.editMode === false && this.totalRecords > 0)) && this.permCancelAllow === 1) {
          this.allowCancel = true;
        }
        else {
          this.allowCancel = false;
        }
      }
      console.log("allow cancel is " + this.allowCancel);
      // }
      //   else {
      //     this.allowCancel = false;
      //  }

      let wfHistStatus = "";
      if (this.showRequesterTable === true) { //showRequesterTable = true means this form is created by requester elase finance admin
        wfHistStatus = 'Q-R';
      }
      else {
        wfHistStatus = 'Q-FA';
      }

      const tempRequester = await this.getworkflowhistory_ast(wfHistStatus)
      if (tempRequester.assign_to !== null && tempRequester.assign_to_nm !== null) { //means requester/FA has been reassigned by super admin
        this.requesterOrFA = tempRequester.assign_to;
        this.requesterOrFAName = tempRequester.assign_to_nm;
      }
      else { //means requester/FA has not been reassigned by super admin
        this.requesterOrFA = this.createdBy;
        this.requesterOrFAName = this.createdByNm;
      }

    }
  }

  async submit() {
    this.isLoading = true;
    this.defaultSetting();

    const invalidUpdate = await this.updateMasterTaskList(this.pendingStatus, this.ssm4uuserrefno);

    if (invalidUpdate === false) { //success update
      // this.dataService.setShowUpdateAlert(true)
      // this.router.navigate(['/master-task-list']);
      this.sendEmail('submit')
      const alert_msg = "reassigned";
      this.router.navigate(['/master-task-list'], { state: { alert_msg } });
    }
    else {
      console.log("Reassign not successful");
    }
    this.isLoading = false;
  }

  async updateMasterTaskList(status: string | null, assignTo: string | null): Promise<boolean> {

    const updmftwfStatusUrl = environment.apiUrl + '/api/mftwf/v1/updatemasterfeetableworkflow_status';

    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json'
    });

    const Body: any = {
      i_wf_id: this.wfId,
      i_assign_to: assignTo,
      i_status: status,
    };

    if (this.textRemarks && this.textRemarks.trim()) {
      Body.i_remark = this.textRemarks;
    }

    Body.i_modified_by = this.username;

    //console.log('Action taken is '+this.decision)
    try {
      const response: any = await this.http.post(updmftwfStatusUrl, Body, { headers }).toPromise();
      if (response.header.statusCode === '00') {
        return false; // Update success
      } else {
        this.error = true;
        this.errorMessages.push('Update not successful');
        return true; // update failed
      }
    } catch (error) {
      this.error = true;
      this.errorMessages.push('Internal Server Error.');
      console.error(error);
      return true; // Error occurred
    }
  }

  //default setting start
  defaultSetting(): void {
    this.error = false;
    // this.errorMessage="";
    this.errorMessages = [];
  }



  loadDataSupDoc() {
    // this.isDisplaySupDoc = true;
    this.isLoadingSupDoc = true;

    const urlMftWFHis = environment.apiUrl + '/api/mftwfdoc/v1/getmasterfeetableworkflowdocument';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json'
    });


    // Create the request body with your form data
    const requestBody: any = {

      i_page: this.pageSupDoc,
      i_size: this.itemsPerPageSupDoc,
      i_wf_id: this.wfId,
      i_status: Systemstatus.Active,

    };

    this.http.post(urlMftWFHis, requestBody, { headers }).subscribe(
      (response: any) => {

        if (response.data.length === 0) {
          this.isDisplaySupDoc = false;
          this.isLoadingSupDoc = false;
          this.totalRecordsSupDoc = 0;
          console.error('Invalid master fee table work flow document response format:', response);
        }
        else {
          this.mftwfSupDocs = response.data;
          this.isDisplaySupDoc = true;
          this.isLoadingSupDoc = false;
          this.totalRecordsSupDoc = response.data[0].total;
        }
        //  console.log("MFTWF is "+this.mftwf[0].fee_detail_id);
        //  console.log(this.totalRecords);

      },
      (error) => {
        console.error('There was an error retrieving the master fee table work flow document:', error);
        this.isLoadingSupDoc = false;
        // Handle errors here
      }
    );
  }

  loadDataHist() {
    // this.isDisplayHist = true;
    this.isLoadingHist = true;

    const urlMftWFHis = environment.apiUrl + '/api/mftwfh/v1/getmasterfeetableworkflowhistory';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json'
    });


    // Create the request body with your form data
    const requestBody: any = {

      i_page: this.pageHist,
      i_size: this.itemsPerPageHist,
      i_wf_id: this.wfId,
      i_status: null,

    };

    this.http.post(urlMftWFHis, requestBody, { headers }).subscribe(
      (response: any) => {

        if (response.data.length === 0) {
          this.isDisplayHist = false;
          this.totalRecordsHist = 0;
          this.isLoadingHist = false;
          console.error('Invalid master fee table work flow history response format:', response);
        }
        else {
          this.mftwfHis = response.data;
          this.isDisplayHist = true;
          this.isLoadingHist = false;
          this.totalRecordsHist = response.data[0].total;
        }
        //  console.log("MFTWF is "+this.mftwf[0].fee_detail_id);
        //  console.log(this.totalRecords);

      },
      (error) => {
        console.error('There was an error retrieving the master fee table work flow history:', error);
        this.isLoadingHist = false;
      }
    );

  }


  //form handle before submit start
  async handleFormSubmit(form: NgForm) {

    this.submitMode = true;
    this.cdref.detectChanges();

    if (form.valid) {
      const validProceedToSubmit = await this.checkStatusAndAssignTo(true); //ensure the mftwf status is updated before reassign
      if (validProceedToSubmit === true) { //only when update require to checkStatusAndAssignTo
        this.submit();
      }
      else {
        console.log("Invalid proceed to submit")
      }
      // this.submit();
    } else {
      Object.values(form.controls).forEach((control) => {
        control.markAsTouched();
      });
    }
  }
  //form handle before submit end


  cancel() {
    this.router.navigate(['/master-task-list']);
  }


  async populateForm(): Promise<void> {

    const urlMftWF = environment.apiUrl + '/api/mftwf/v1/getmasterfeetableworkflow';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json'
    });


    // Create the request body with your form data
    const Body: any = {

      i_page: environment.DefaultPage,
      i_size: "1",
      i_wf_id: this.wfId,
      i_wf_is_in_prg: "f"
    };

    try {
      const response: any = await this.http.post(urlMftWF, Body, { headers }).toPromise();
      // console.log("Ast is "+ response.header.statusCode)
      if (response.header.statusCode === '00') {
        this.totalRecords = response.data[0].total;
        this.mftwfs = response.data;
        this.rFeeDetNm = this.mftwfs[0].r_fee_det_nm;
        this.rFeeAmt = this.mftwfs[0].r_fee_amt;
        this.rSsCd = this.mftwfs[0].r_ss_cd;
        this.rPromoStartDt = this.mftwfs[0].r_promo_startdt;
        this.rPromoEndDt = this.mftwfs[0].r_promo_enddt;
        this.rPromoFee = this.mftwfs[0].r_promo_fee;
        this.rLlRequired = this.mftwfs[0].r_ll_required;
        this.rAddNotes = this.mftwfs[0].r_add_notes;
        this.feeDetailId = this.mftwfs[0].fee_detail_id;
        this.feeGrpNmEn = this.mftwfs[0].fee_grp_nm_en;
        this.feeDetailNmE = this.mftwfs[0].fee_detail_nm_e;
        this.feeDetailNmB = this.mftwfs[0].fee_detail_nm_b;
        this.feeAmt = this.mftwfs[0].fee_amt;
        this.promoStartDate = this.mftwfs[0].promo_startdt;
        this.promoEndDate = this.mftwfs[0].promo_enddt;
        this.promoFee = this.mftwfs[0].promo_fee;
        this.taxCd = this.mftwfs[0].tax_cd;
        this.allowOTC = this.mftwfs[0].allow_otc;
        this.llParentId = this.mftwfs[0].ll_parent_id;
        this.llStartDay = this.mftwfs[0].ll_start_day;
        this.llStartMth = this.mftwfs[0].ll_start_mth;
        this.llEndDay = this.mftwfs[0].ll_end_day;
        this.llEndMth = this.mftwfs[0].ll_end_mth;
        this.ledgerCd = this.mftwfs[0].ledger_cd;
        this.ssCd = this.mftwfs[0].ss_cd;
        this.effectiveDate = this.mftwfs[0].effective_date;
        this.dtCreated = this.mftwfs[0].dt_created;
        this.createdBy = this.mftwfs[0].created_by;
        this.createdByNm = this.mftwfs[0].created_by_nm;
        this.dtModified = this.mftwfs[0].dt_modified;
        this.modifiedBy = this.mftwfs[0].modified_by;
        this.modifiedByNm = this.mftwfs[0].modified_by_nm;
        this.status = this.mftwfs[0].status;
        this.statusEn = this.mftwfs[0].status_en;
        this.mftStatus = this.mftwfs[0].mft_status;
        this.currentAssignTo = this.mftwfs[0].assign_to;
        this.isPublic = this.mftwfs[0].is_pub;

        if (this.rSsCd !== null) {
          //  this.checkboxRssCd = this.rSsCd.split(',');
          this.rSsCdWithSpace = this.rSsCd.split(',').join(', '); //for display
        }

        if (this.ssCd !== null) {
          //  this.checkboxOptions = this.ssCd.split(',');
          this.ssCdWithSpace = this.ssCd.split(',').join(', '); //for display
        }
        //  this.checkboxRssCd = this.rSsCd?.split(',');
        //this.checkboxOptions = this.ssCd?.split(',');
        this.permCancelAllow = this.permTaskDetailsAllow.includes(perm.Master_Fee_Table_Cancel_Task) ? 1 : 0;
      } else {
        this.totalRecords = 0;
        console.error('Invalid master fee table work flow history response format:', response);
      }
    } catch (error) {
      console.error('There was an error retrieving the master fee table work flow history:', error);
    }

  }

  toDisplayReassignmentAction() {
    if (this.status === 'APV' || this.status === 'RJ-RHOD' || this.status === 'RJ-FA' ||
      this.status === 'RJ-FHOD' || this.status === 'C' || this.status === 'EFT') {
      this.displayRessignmentAction = false;
    }
    else {
      this.displayRessignmentAction = true;
    }
  }

  populateReassignmentUser(): void {

    if (this.pendingStatus === 'Q-R') {
      this.userRoleToReassign = 'REQUESTER';
      this.displayUserRoleToReassign = 'Requester';
    }
    else if (this.pendingStatus === 'P-RHOD' || this.pendingStatus === 'Q-RHOD') {
      this.userRoleToReassign = 'REQUESTERHOD';
      this.displayUserRoleToReassign = 'Requester HOD';
    }
    else if (this.pendingStatus === 'P-FA' || this.pendingStatus === 'Q-FA') {
      this.userRoleToReassign = 'FINANCEADMIN';
      this.displayUserRoleToReassign = 'Finance Admin';
    }
    else if (this.pendingStatus === 'P-FHOD') {
      this.userRoleToReassign = 'FINANCEHOD';
      this.displayUserRoleToReassign = 'Finance HOD';
    }
    else {
      this.userRoleToReassign = null
    }
    //console.log('Users is roles: '+this.userRoleToReassign )
    const url = environment.apiUrl + '/api/mft/v1/getuserbyrole';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json'
    });

    // Create the request body with your form data
    const requestBody: any = {
      i_page: '1',
      i_size: environment.DropDownSize,
      i_role_nm_en: this.userRoleToReassign,
      i_role_nm_bm: null,
      i_status: Systemstatus.Active
    };

    this.http.post(url, requestBody, { headers }).subscribe(
      (response: any) => {
        if (response.data.length === 0) {
          console.error('Invalid user response format:', response);
        }
        else {
          this.users = response.data.filter((user: User) => user.ssm4uuserrefno !== this.currentAssignTo);
          //  this.users= response.data
        }

        //  console.log('Rolees is : '+this.roles )
      },
      (error) => {
        console.error('There was an error retrieving the user:', error);
        // Handle errors here
      }
    );
  }

  async cancelTask(form: NgForm) {

    const dialogRef = this.dialog.open(MftCancelTaskComponent, {
      width: '20%',
    });

    // Wait for dialogRef.afterClosed() to finish
    const result = await lastValueFrom(dialogRef.afterClosed());

    if (result === 'no' || result === undefined) {
      return; // Stop execution if "No" is clicked
    }

    this.submitMode = false
    this.cdref.detectChanges(); //angular sometimes cannot detect this changes therefore manually detect changes

    this.isLoading = true;
    this.defaultSetting();

    if (form.valid) {
      const validProceedToSubmit = await this.checkStatusAndAssignTo(true); //ensure the mftwf status is updated before reassign
      if (validProceedToSubmit === true) { //only when update require to checkStatusAndAssignTo
        const successCancelTask = await this.updateMasterTaskList('C', null);

        if (successCancelTask === false) {
          this.sendEmail('cancel')
          this.isLoading = false;
          const alert_msg = "cancelled";
          this.router.navigate(['/master-task-list'], { state: { alert_msg } });
        }
        else {
          this.isLoading = false;
        }
      }
      else {
        console.log("Invalid proceed to submit")
      }
    }
    else {
      Object.values(form.controls).forEach((control) => {
        control.markAsTouched();
        this.isLoading = false;
      });
    }
    this.isLoading = false;
  }

  //download file start
  downloadFile(file_nm: string, wfdoc_id: number): void {

    const url = environment.apiUrl + '/api/mftwfdoc/v1/getmftwfdocfilecontent';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    const Body: any = {
      i_wfdoc_id: wfdoc_id
    };

    console.log(Body);

    this.http.post(url, Body, { headers }).subscribe(
      (response: any) => {
        // console.log(response.data);
        this.file_content = response.data;
        this.downloadFileContent(file_nm, this.file_content);

        if (response.data.length == 0) {
          this.isLoadingSupDoc = false;
          console.error('Invalid master fee table work flow document response format:', response);
        } else {
          this.isLoadingSupDoc = false;
          console.log('Successful download file ' + file_nm);
        }
      },
      (error) => {
        this.isLoadingSupDoc = false;
        console.error('There was an error downloading the master fee table work flow document:', error);;
      }
    );
  }

  downloadFileContent(fileName: string, fileContent: string): void {
    // event.preventDefault(); // Prevent the default behavior of the anchor element
    this.isLoadingSupDoc = true;

    // Check if file_content exists
    if (fileContent) {
      const contentType = 'application/octet-stream';
      const blob = this.base64ToBlob(fileContent, contentType);
      const blobUrl = URL.createObjectURL(blob);

      // Create an anchor element and trigger the download
      const link = document.createElement('a');
      link.href = blobUrl;
      link.download = fileName;
      link.click();

      // Cleanup
      URL.revokeObjectURL(blobUrl);
    }
  }

  base64ToBlob(base64: string, contentType: string): Blob {
    const byteCharacters = atob(base64);
    const byteNumbers = new Array(byteCharacters.length);

    for (let i = 0; i < byteCharacters.length; i++) {
      byteNumbers[i] = byteCharacters.charCodeAt(i);
    }

    const byteArray = new Uint8Array(byteNumbers);
    return new Blob([byteArray], { type: contentType });
  }
  //download file end

  async populateCurrentValueForm(): Promise<void> {

    const url = environment.apiUrl + '/api/mft/v1/getmasterfeetable';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    const Body: any = {
      i_page: this.page.toString(),
      i_size: 1,
      i_fee_detail_pk: this.feeDetailPk
    };


    try {
      const response: any = await this.http.post(url, Body, { headers }).toPromise();
      // console.log("Ast is "+ response.header.statusCode)
      if (response.header.statusCode === '00') {
        this.totalMFTRecords = response.data[0].total;
        this.mfts = response.data;
        this.currentFeeDetailId = this.mfts[0].fee_detail_id;
        this.currentFeeGroupNmEn = this.mfts[0].fee_grp_nm_en;
        this.currentFeeDetailNmE = this.mfts[0].fee_detail_nm_e;
        this.currentFeeDetailNmB = this.mfts[0].fee_detail_nm_b;
        this.currentUnitFee = this.mfts[0].unit_fee;
        this.currentPromoStartDate = this.mfts[0].promo_startdt;
        this.currentPromoEndDate = this.mfts[0].promo_enddt;
        this.currentPromoFee = this.mfts[0].promo_fee;
        this.currentTaxCd = this.mfts[0].tax_cd;
        this.currentAllowOTC = this.mfts[0].allow_otc;
        this.currentLlParentId = this.mfts[0].ll_parent_id;
        this.currentLlStartDay = this.mfts[0].ll_start_day;
        this.currentLlStartMth = this.mfts[0].ll_start_mth;
        this.currentLlEndDay = this.mfts[0].ll_end_day;
        this.currentLlEndMth = this.mfts[0].ll_end_mth;
        this.currentLedgerCd = this.mfts[0].ledger_cd;
        this.currentSsCd = this.mfts[0].ss_cd;
        this.currentDtCreated = this.mfts[0].dt_created;
        this.currentCreatedBy = this.mfts[0].created_by;
        this.currentCreatedByNm = this.mfts[0].created_by_nm;
        this.currentDtModified = this.mfts[0].dt_modified;
        this.currentModifiedBy = this.mfts[0].modified_by;
        this.currentModifiedByNm = this.mfts[0].modified_by_nm;
        this.currentStatus = this.mfts[0].status;
        this.currentisPublic = this.mfts[0].isPub;

        if (this.currentSsCd !== null) {
          //  this.currentCheckboxSsCd = this.currentSsCd.split(',');
          this.currentSsCdWithSpace = this.currentSsCd.split(',').join(', '); //for display
        }
      } else {
        this.totalMFTRecords = 0;
        console.error('Invalid master fee table response format:', response);
      }
    } catch (error) {
      console.error('There was an error retrieving the master fee table:', error);
    }
  }

  async getworkflowhistory_ast(status: string): Promise<{ assign_to: string | null, assign_to_nm: string | null }> {

    const url = environment.apiUrl + '/api/mftwfh/v1/getworkflowhistory_ast';

    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json'
    });

    const body: any = {
      i_task_id: this.taskId,
      i_status: status,
    };

    try {
      const response: any = await this.http.post(url, body, { headers }).toPromise();

      if (response.header.statusCode === '00') {
        this.isLoading = false;
        return {
          assign_to: response.data[0].assign_to,
          assign_to_nm: response.data[0].assign_to_nm
        }; // Data found
      } else {
        // this.error = true;
        // this.errorMessages.push('Data not found');
        console.error('Invalid master fee table work flow history assign to response format:', response);
        this.isLoading = false;
        return { assign_to: null, assign_to_nm: null }; // Data not found
      }
    } catch (error) {
      //  this.error = true;
      //  this.errorMessages.push('Internal Server Error.');
      this.isLoading = false;
      console.error('There was an error retrieving the master fee table work flow history assign to:', error);
      return { assign_to: null, assign_to_nm: null }; // Error occurred
    }
  }

  //email start

  async sendEmail(status: string) {

    const url = environment.apiUrl + '/api/mftemail/v1/backend';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });


    let toSendEmailStatus: string | null = null;
    let extractedValue = "";
    
    if (this.wfId !== undefined) { //query from finance hod
      this.emailWfId = this.wfId;
      // this.emailAssignTo = this.requesterOrFA;
    }

    //console.log("emailAssignTo is " + this.emailAssignTo);
    await this.getworkflowDetailAfterInsert();
    console.log("emailWfId is " + this.emailWfId);
    console.log("emailfeeDetailId is " + this.emailfeeDetailId);
    console.log("emailTaskId is " + this.emailTaskId);
    console.log("current assign to is " + this.currentAssignTo);

    if (status === 'submit') {
      toSendEmailStatus = this.currentMFTWFStatus;
      this.sendTo = this.ssm4uuserrefno; //send to user who being selected
      this.cc = "";
    }
    else {//means cancel button press
      toSendEmailStatus = 'C';
      this.sendTo = this.createdBy; //send to task creator
      this.cc = this.currentAssignTo; //send to current task assign to
    }

    console.log("send to is " + this.sendTo);

    if (this.emailAction === "Request Add-FIN" || this.emailAction === "Request Add") {// for request add, since emailfeeDetailId will be null herefore extracted value will be ""
      if (this.emailfeeDetailId !== undefined && this.emailfeeDetailId !== null) {
        extractedValue = this.emailfeeDetailId;
      }
    }
    else {//edit

      if (this.emailfeeDetailId !== undefined && this.emailfeeDetailId !== null) {
        extractedValue = this.emailfeeDetailId;
      }
      else {
        if (this.rFeeDetNm !== undefined && this.rFeeDetNm !== null) {
          extractedValue = this.rFeeDetNm.split(" : ")[0];
        }
      }
    }

    const Body: any = {

      i_wf_id: this.emailWfId,
      i_task_id: this.emailTaskId,
      i_fee_detail_pk: this.emailFeeDetailPk,
      // i_fee_detail_id: this.emailfeeDetailId,
      i_fee_detail_id: extractedValue,
      i_status: toSendEmailStatus,
      i_action: this.emailAction,
      i_send_to: this.sendTo,
      i_cc: this.cc,
      i_bcc: ""
    };

    this.http.post(url, Body, { headers }).subscribe(
      (response: any) => {
        console.log('Email awaiting scheduler to send email.')
      },
      (error) => {
        console.error('There was an error sending the email:', error);
      }
    );
  }


  async getworkflowDetailAfterInsert(): Promise<void> {

    const urlMftWF = environment.apiUrl + '/api/mftwf/v1/getmasterfeetableworkflow';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json'
    });


    // Create the request body with your form data
    const Body: any = {

      i_page: "1",
      i_size: "1",
      i_wf_id: this.emailWfId,
      i_wf_is_in_prg: "f"
    };
    console.log("emailWfId is " + this.emailWfId);
    try {
      const response: any = await this.http.post(urlMftWF, Body, { headers }).toPromise();

      if (response.header.statusCode === '00') {
        this.emailMftwfs = response.data;
        this.emailFeeDetailPk = this.emailMftwfs[0].fee_detail_pk;
        this.emailfeeDetailId = this.emailMftwfs[0].fee_detail_id;
        this.emailTaskId = this.emailMftwfs[0].task_id;
        this.emailAction = this.emailMftwfs[0].action;
        console.log("emailFeeDetailPk is " + this.emailFeeDetailPk);
        console.log("emailAction is " + this.emailAction);
      } else {
        //this.error = true;
        //this.errorMessages.push('Data not found');
        console.error('Invalid master work work flow response format:', response);
        //  return ''; //  Data not found
      }
    } catch (error) {
      // this.error = true;
      // this.errorMessages.push('Internal Server Error.');
      console.error('There was an error retrieving the master fee table work flow:', error);
      //return ''; // Error occurred
    }
  }

  //email end

  async checkPermission(): Promise<void> {

    const permUrl = environment.apiUrl + '/api/RPC/v1/checkuserrole';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json'
    });

    const bodyPermTCDList: any = {
      i_username: this.username,
      i_perm_cd: this.permTaskDetails,
    };

    try {
      const response: any = await this.http.post(permUrl, bodyPermTCDList, { headers }).toPromise();
      this.permTaskDetailsAllow = response.data;
      this.permListAllow = this.permTaskDetailsAllow.includes(perm.Master_Fee_Table_View_Task_Details) ? 1 : 0;
      this.permCancelAllow = this.permTaskDetailsAllow.includes(perm.Master_Fee_Table_Cancel_Task) ? 1 : 0;

      if (this.permListAllow === 0) {
        this.router.navigate(['/access-denied']);
        return; // Exit the function to prevent further execution
      }
    } catch (error) {
      // this.error = true;
      // this.errorMessages.push('Internal Server Error.');
      console.error('There was an error retrieving the permission:', error);
      //return ''; // Error occurred
    }
  }

  async checkStatusAndAssignTo(reassignOrCancel: boolean): Promise<boolean> {

    const urlMftWF = environment.apiUrl + '/api/mftwf/v1/getmasterfeetableworkflow';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json'
    });

    const Body: any = {

      i_page: "1",
      i_size: "1",
      i_wf_id: this.wfId,
      i_wf_is_in_prg: "f"
    };

    try {
      const response: any = await this.http.post(urlMftWF, Body, { headers }).toPromise();

      if (response.header.statusCode === '00') {
        this.currentMFTWFDetail = response.data;
        this.currentMFTWFStatus = this.currentMFTWFDetail[0].status;
        this.currentMFTWFAssignTo = this.currentMFTWFDetail[0].assign_to;

        // console.log("currentMFTWFAssignTo is " + this.currentMFTWFAssignTo);
        // console.log("username is " + this.username);
        console.log("currentMFTWFStatus is " + this.currentMFTWFStatus);
        console.log("pendingStatus is " + this.pendingStatus);
        console.log("reassignOrCancel is " + reassignOrCancel);
        console.log("fromView is " + this.fromView);
        console.log("navigateNotRefresh is " + this.navigateNotRefresh);

        //need to check if cancel, eft or else in case when this page is open then document is cancel from created task there

        if (reassignOrCancel === false) {//from navigation button pressed
          if (this.currentMFTWFStatus === "EFT"
            || this.currentMFTWFStatus === "RJ-RHOD" || this.currentMFTWFStatus === "RJ-FA" || this.currentMFTWFStatus === "RJ-FHOD") {
            const showTaskNotUpdateAlert = true;
            this.router.navigate(['/master-task-list'], { state: { showTaskNotUpdateAlert } });
            return false;
            // if (this.fromView === true) {//means come from master task list view, APV status is ignore, (lastest at right)this part can ignore since new requirement where there are task status checking in view selected before come here
            //   if (this.currentMFTWFStatus === "C" || this.currentMFTWFStatus === "EFT"
            //     || this.currentMFTWFStatus === "RJ-RHOD" || this.currentMFTWFStatus === "RJ-FA" || this.currentMFTWFStatus === "RJ-FHOD") {
            //     if (this.navigateNotRefresh === true) {
            //       // this.errorTaskClosedCanceled = true;
            //       // this.errorMessagesTaskClosedCanceled.push("This task has been canceled or closed and it is no longer active.");
            //       const showTaskNotUpdateAlert = true;
            //       this.router.navigate(['/master-task-list'], { state: { showTaskNotUpdateAlert } });
            //     }
            //     history.replaceState({ ...history.state, navigate_not_refresh: false }, '');
            //     console.log("Navigate from view and status is no longer active : " + this.currentMFTWFStatus)
            //     return false;
            //   }
            // }
            // else {  //from mft-fa-fa-rqt or created-tasks, APV is required here because  other task like mft-fa-fa-rqt can approved and send to here
            //   if (this.currentMFTWFStatus === "C" || this.currentMFTWFStatus === "APV" || this.currentMFTWFStatus === "EFT"
            //     || this.currentMFTWFStatus === "RJ-RHOD" || this.currentMFTWFStatus === "RJ-FA" || this.currentMFTWFStatus === "RJ-FHOD") {
            //     if (this.navigateNotRefresh === true) {
            //       // this.errorTaskClosedCanceled = true;
            //       // this.errorMessagesTaskClosedCanceled.push("This task has been canceled or closed and it is no longer active.");
            //       const showTaskNotUpdateAlert = true;
            //       this.router.navigate(['/master-task-list'], { state: { showTaskNotUpdateAlert } });
            //     }
            //     history.replaceState({ ...history.state, navigate_not_refresh: false }, '');
            //     console.log("Navigate from not view and status is no longer active. : " + this.currentMFTWFStatus)
            //     return false;
            //   }
          }
        }
        else {//submit or cancel button pressed
          if (this.currentMFTWFStatus === "C" || this.currentMFTWFStatus === "EFT"
            || this.currentMFTWFStatus === "RJ-RHOD" || this.currentMFTWFStatus === "RJ-FA" || this.currentMFTWFStatus === "RJ-FHOD") {
            const showTaskNotUpdateAlert = true;
            this.router.navigate(['/master-task-list'], { state: { showTaskNotUpdateAlert } });
            return false;
          }

          if (this.currentAssignTo !== this.currentMFTWFAssignTo) {//means this task already assigned but u are not updated
            console.log("currentAssignTo !== currentMFTWFAssignTo")
            const showTaskNotUpdateAlert = true;
            this.router.navigate(['/master-task-list'], { state: { showTaskNotUpdateAlert } });
            return false;
          }
        }

        if (this.currentMFTWFStatus !== this.pendingStatus) {
          console.log("currentMFTWFStatus !== this.pendingStatus")
          const showTaskNotUpdateAlert = true;
          this.router.navigate(['/master-task-list'], { state: { showTaskNotUpdateAlert } });
          return false;
        }
        else {
          return true;
        }
      } else {
        console.error('Invalid master work work flow response format:', response);
        return false;
      }
    } catch (error) {
      console.error('There was an error retrieving the master fee table work flow:', error);
      return false;
    }
  }

}
