import { Component, OnInit, ViewChild, ElementRef, ChangeDetectorRef, ViewEncapsulation, AfterViewInit } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { environment } from 'src/environments/environment';
import { FeeGroup } from '../../core/models/fee-group';
import { DatePipe, DecimalPipe } from '@angular/common';
import { MFT, MFTWF, MFTWFDoc, MFTWFHist, Param, SourceSystemCode, TaxCode, User } from '../../core/models/entity';
import { forkJoin, of } from 'rxjs';
import { concatMap, delay, map } from 'rxjs/operators';
import { FormBuilder, FormControl, FormGroup, NgForm, NgModel, Validators } from '@angular/forms';
import { Systemstatus } from '../../shared/enums/systemstatus';
import { ActivatedRoute, Router } from '@angular/router';
import { DataService } from '../../core/services/data.service';
import { trigger, state, style, transition, animate } from '@angular/animations';
import { fadeInOut } from '../../shared/animation';
import { TranslateService } from '@ngx-translate/core';
import { GlobalService } from 'src/app/shared/global.service';
import { perm } from 'src/permissions/perm';
import { AuthService } from 'src/app/core/services/auth.service';


@Component({
  selector: 'app-mft-fhod-appr-add',
  templateUrl: './mft-fhod-appr-add.component.html',
  styleUrls: ['./mft-fhod-appr-add.component.scss'],
  animations: [fadeInOut]
})
export class MftFhodApprAddComponent implements OnInit {

  username = this.authService.username;
  userHigherOfficialRole = "FINANCEHOD";

  feeGroups: FeeGroup[] = [];
  //feeGroupId:string="" //selected value from fee group id drop down
  initialNumber = [
    { value: 0, label: 'No' },
    { value: 1, label: 'Yes' },
  ]
  errorMessages: string[] = [];
  error: boolean = false;
  isLoading: boolean = false;
  page = environment.DefaultPage;
  itemsPerPage = environment.ItemPerPage;
  dropDownSize = environment.DropDownSize;
  totalRecords: number = 0;
  totalMFTRecords: number = 0;
  status: Param[] = [];
  sourceSystemCodes: SourceSystemCode[] = [];
  selectedSourceSystemCodes: any[] = [];
  decisionOption: any[] = [];
  decision: string = "";
  wfId: number | null = null;
  taskId: string | null = null;
  assignTo: string | null = null;
  actionType: string | null = null;
  alertMessage: string | undefined = undefined;
  users: User[] = [];
  taxCode: TaxCode[] = [];
  editMode: boolean = false;
  file_content = "";
  statusFromAssigned: string | null = null;
  currentMFTWFDetail: MFTWF[] = [];
  currentMFTWFStatus: string | null = null;
  currentMFTWFAssignTo: string | null = null;
  errorMessagesTaskNotUpdate: string[] = [];
  errorTaskNotUpdate: boolean = false;

  feeDetailPk: number | null = null;
  feeDetailId: string | null = null;
  feeGroupId: string | null = null;
  feeGrpNmEn: string | null = null;
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
  createdBy: string | null = null;
  createdByNm: string | null = null;
  modifiedBy: string | null = null;
  modifiedByNm: string | null = null;
  dtCreated: Date | null = null;
  dtModified: Date | null = null;
  ssCd: string | null = null;
  statusEn: string | null = null;
  effectiveDate: Date | null = null;
  textRemarks: string | null = null;
  ssm4uuserrefno: string | null = null;
  mftStatus: string | null = null;
  mftwfHis: MFTWFHist[] = [];
  mftwfs: MFTWF[] = [];
  mfts: MFT[] = [];
  emailMftwfs: MFTWF[] = [];
  mftwfSupDocs: MFTWFDoc[] = [];
  checkboxRSsCd: string[] | undefined = undefined;
  rSsCdWithSpace: string | null = null;
  checkboxSsCd: string[] | undefined = undefined;
  ssCdWithSpace: string | null = null;
  isPublic: number| null = null;

  //display for first column mft value
  currentFeeDetailId: string | null = null;
  currentFeeGroupNmEn: string | null = null;
  currentFeeDetailNmEn: string | null = null;
  currentFeeDetailNmBm: string | null = null;
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
  currentIsPublic: number | null = null;

  //email
  emailWfId: string | number | null = null;
  emailAssignTo: string | null = null;
  emailStatus: string | null = null;
  emailFeeDetailPk: string | number | null = null;
  emailfeeDetailId: string | null = null;
  emailrfeeDetNm: string | null = null;
  emailTaskId: string | null = null;
  emailAction: string | null = null;

  isDisplaySupDoc: boolean = false;
  isLoadingSupDoc: boolean = false;
  isDisplayHist: boolean = false;
  isLoadingHist: boolean = false;


  //to display all the read only field
  requester: string | null = null;
  requesterName: string | null = null;
  requesterHOD: string | null = null;
  financeAdmin: string | null = null;
  rFeeDetNm: string | null = null;
  rFeeAmt: number | null = null;
  rSsCd: string | null = null;
  rPromoStartDt: Date | null = null;
  rPromoEndDt: Date | null = null;
  rPromoFee: number | null = null;
  rLlRequired: number = 0;
  rAddNotes: string | null = null;

  //Supporting Document Table setting
  pageSupDoc = environment.DefaultPage;
  itemsPerPageSupDoc = environment.ItemPerPage;
  totalRecordsSupDoc: number = 0;

  //History Table setting
  pageHist = environment.DefaultPage;
  itemsPerPageHist = environment.ItemPerPage;
  totalRecordsHist: number = 0;

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
    this.itemsPerPageSupDoc = this.selectedValueSupDoc;
    this.loadDataSupDoc();
  }

  // Configuring Permissions for User and roles variables
  permApproveAddAndEditMFTwithRequesterFormFHOD = perm.Master_Fee_Table_Approve_Add_MFT_with_Requester_Form_FHOD + "," + perm.Master_Fee_Table_Approve_Edit_MFT_with_Requester_Form_FHOD; // all the perm_cd for this module seperated with comma
  permApproveAddAndEditMFTwithRequesterFormFHODAllow = ""; // variable to store allowed permission for the user
  permApproveAdd: number = 0;
  permApproveEdit: number = 0;
  permListAllow: number = 0; // if 0 then not allow to view listing page, else allow
  // end configuration

  constructor(
    private http: HttpClient,
    private datePipe: DatePipe,
    private router: Router,
    private route: ActivatedRoute,
    private translate: TranslateService,
    private globalService: GlobalService,
    private authService: AuthService
  ) {
    this.translate.setDefaultLang(this.globalService.getGlobalValue()); // default language
    this.translate.use(this.globalService.getGlobalValue());
  }


  async ngOnInit() {

    const queryParams = this.route.snapshot.queryParamMap;

    const tempWf_id = queryParams.get('wf_id');
    if (tempWf_id !== null && tempWf_id !== 'null') {
      this.wfId = +tempWf_id; //+ mean convert string to number
    }
    else {
      this.wfId = history.state.wf_id;
    }

    const tempTask_id = queryParams.get('task_id');
    if (tempTask_id !== null && tempTask_id !== 'null') {
      this.taskId = tempTask_id;
    }
    else {
      this.taskId = history.state.task_id;
    }

    const tempFee_detail_pk = queryParams.get('fee_detail_pk');
    if (tempFee_detail_pk !== null && tempFee_detail_pk !== 'null') {
      this.feeDetailPk = +tempFee_detail_pk;
    }
    else {
      this.feeDetailPk = history.state.fee_detail_pk;
      //console.log("Fee detail pk is "+this.feeDetailPk)
    }

    const tempedit_Mode = queryParams.get('edit_Mode');
    if (tempedit_Mode !== null && tempedit_Mode !== 'null') {
      if (tempedit_Mode === 'true') {
        this.editMode = true;
      } else if (tempedit_Mode === 'false') {
        this.editMode = false;
      }
    }
    else {
      this.editMode = history.state.edit_Mode;
    }

    const tempStatus__From_Assigned = queryParams.get('status__From_Assigned');
    if (tempStatus__From_Assigned !== null && tempStatus__From_Assigned !== 'null') {
      this.statusFromAssigned = tempStatus__From_Assigned;
      console.log("status__From_Assigned from queryparam" + this.statusFromAssigned)
    }
    else {
      this.statusFromAssigned = history.state.status__From_Assigned;
      console.log("status__From_Assigned from state" + this.statusFromAssigned)
    }

    // this.wfId = history.state.wf_id;
    // this.taskId = history.state.task_id;
    // this.feeDetailPk = history.state.fee_detail_pk;
    // this.editMode = history.state.edit_Mode;

    if (this.wfId !== undefined) {
      this.isLoading = true;
      this.decisionOption = [
        { label: 'Query to Requester', value: 'Q-R' },//Query Pending Requester
        { label: 'Query to Requester HOD', value: 'Q-RHOD' }, //Query Pending Requester HOD
        { label: 'Query to Finance Admin', value: 'Q-FA' }, //Query Pending Finance Admin
        { label: 'Approve', value: 'APV' }, //Approved
        { label: 'Reject', value: 'RJ-FHOD' } //Rejected by Finance HOD
      ]
      this.checkPermission();
      await this.checkStatusAndAssignTo(false);
      this.loadDataSupDoc();
      this.loadDataHist();
      await this.populateForm(); // await for load value createdby and check permission here

      if (this.editMode === false) {
        this.actionType = "Add";
      }
      else {
        this.actionType = "Edit";
        await this.populateCurrentValueForm();
      }

      const tempRequester = await this.getworkflowhistory_ast('Q-R');
      if (tempRequester.assign_to !== null && tempRequester.assign_to_nm !== null) { //means requester has been reassigned by super admin
        this.requester = tempRequester.assign_to;
        this.requesterName = tempRequester.assign_to_nm;
      }
      else { //means requester has not been reassigned by super admin
        this.requester = this.createdBy;
        this.requesterName = this.createdByNm;
      }

      const tempRequesterHOD = await this.getworkflowhistory_ast('P-RHOD');
      this.requesterHOD = tempRequesterHOD.assign_to;

      const tempfinanceAdmin = await this.getworkflowhistory_ast('P-FA');
      this.financeAdmin = tempfinanceAdmin.assign_to;
      console.log("Requester is " + this.requester);
      console.log("Requester HOD is " + this.requesterHOD);
      console.log("Finance Admin is " + this.financeAdmin);
      this.isLoading = false;
    }

  }

  async submit() {
    this.isLoading = true;
    this.defaultSetting();

    if (this.decision === 'Q-R') //Query Pending Requester
    {
      this.assignTo = this.requester;
      this.emailAssignTo = this.requester;
    }
    else if (this.decision === 'Q-RHOD') //Query Pending Requester HOD
    {
      this.assignTo = this.requesterHOD;
      this.emailAssignTo = this.requesterHOD;
    }
    else if (this.decision === 'Q-FA') { //Query Pending Finance Admin
      this.assignTo = this.financeAdmin;
      this.emailAssignTo = this.financeAdmin;
    }
    else if (this.decision === 'RJ-FHOD' || this.decision === 'APV') {  //Rejected by Finance HOD or Approved
      this.assignTo = null;
      this.emailAssignTo = this.createdBy;
    }
    else {
      this.assignTo = null;
    }

    const invalidUpdate = await this.updateMasterFeeTableWorkFlow()
    if (invalidUpdate === false) {
      this.sendEmail();
      this.isLoading = false;
      const alert_msg = this.alertMessage;
      this.router.navigate(['/my-task-assigned-tasks'], { state: { alert_msg } });
    }

    this.isLoading = false;

  }

  async updateMasterFeeTableWorkFlow(): Promise<boolean> {

    const updmftwfStatusUrl = environment.apiUrl + '/api/mftwf/v1/updatemasterfeetableworkflow_status';

    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json'
    });

    const Body: any = {
      i_wf_id: this.wfId,
      i_assign_to: this.assignTo,
      i_status: this.decision,
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
        this.errorMessages.push('Submit not successful');
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


  //form handle before submit start
  async handleFormSubmit(form: NgForm) {
    if (form.valid) {
      // this.submit();
      const validProceedToSubmit = await this.checkStatusAndAssignTo(true); //ensure the mftwf status is updated before submit
      if (validProceedToSubmit === true) {
        this.submit();
      }
      else {
        console.log("Invalid proceed to submit")
      }

   } else {
      Object.entries(form.controls).forEach(([fieldName, control]) => {
        control.markAsTouched();
        if (control.invalid) {
          console.error(`Field "${fieldName}" is invalid`, control.errors);
        }
      });
    }
  }
  //form handle before submit end

  cancel() {
    // this.dataService.setShowInsertAlert(false)
    this.router.navigate(['/master-fee-table']);
  }

  onKeyDown(event: KeyboardEvent): void {
    if (event.key === 'Backspace') {
      return;
    }
    // Prevent manual key entry
    event.preventDefault();
  }

  onDecisionChange() {

    if (this.decision === 'APV') {
      this.alertMessage = 'approved';
    }
    else if (this.decision === 'Q-R' || this.decision === 'Q-RHOD' || this.decision === 'Q-FA') {
      this.alertMessage = 'submitted';
    }
    else if (this.decision === 'RJ-FHOD') {
      this.alertMessage = 'rejected';
    }
    else {
      this.alertMessage = undefined;
    }
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
          this.isLoadingHist = false;
          this.totalRecordsHist = 0;
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

  async populateForm(): Promise<void> {

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
      i_wf_id: this.wfId,
      i_wf_is_in_prg: "f"
    };

    try {
      const response: any = await this.http.post(urlMftWF, Body, { headers }).toPromise();
      // console.log("Ast is "+ response.header.statusCode)
      if (response.header.statusCode === '00') {
        this.totalRecords = response.data[0].total
        this.mftwfs = response.data;
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
        this.effectiveDate = this.mftwfs[0].effective_date;
        this.ssCd = this.mftwfs[0].ss_cd;
        this.dtCreated = this.mftwfs[0].dt_created;
        this.createdBy = this.mftwfs[0].created_by;
        this.createdByNm = this.mftwfs[0].created_by_nm;
        this.dtModified = this.mftwfs[0].dt_modified;
        this.modifiedBy = this.mftwfs[0].modified_by;
        this.modifiedByNm = this.mftwfs[0].modified_by_nm;
        this.statusEn = this.mftwfs[0].status_en;
        this.mftStatus = this.mftwfs[0].mft_status;
        this.rFeeDetNm = this.mftwfs[0].r_fee_det_nm;
        this.rFeeAmt = this.mftwfs[0].r_fee_amt;
        this.rSsCd = this.mftwfs[0].r_ss_cd;
        this.rPromoStartDt = this.mftwfs[0].r_promo_startdt;
        this.rPromoEndDt = this.mftwfs[0].r_promo_enddt;
        this.rPromoFee = this.mftwfs[0].r_promo_fee;
        this.rLlRequired = this.mftwfs[0].r_ll_required;
        this.isPublic = this.mftwfs[0].is_pub;
        this.rAddNotes = this.mftwfs[0].r_add_notes;

        if (this.rSsCd !== null) {
          //this.checkboxRSsCd = this.rSsCd.split(',');
          this.rSsCdWithSpace = this.rSsCd.split(',').join(', '); //for display
        }

        if (this.ssCd !== null) {
          // this.checkboxSsCd = this.ssCd.split(',');
          this.ssCdWithSpace = this.ssCd.split(',').join(', '); //for display
        }

        /*   const tempAction = this.mftwfs[0].action;
           if (tempAction === "Request Edit") {
             this.editMode = true;
           }
           else {
             this.editMode = false;
           }*/

        //   this.checkboxRSsCd = this.rSsCd.split(',');
        //  this.checkboxSsCd = this.ssCd.split(',');
        //  return ''
      } else {
        this.totalRecords = 0;
        console.error('Invalid master fee table work flow history response format:', response);
      }
    } catch (error) {
      console.error('There was an error retrieving the master fee table work flow history:', error);
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
        return {
          assign_to: response.data[0].assign_to,
          assign_to_nm: response.data[0].assign_to_nm
        }; // Data found
      } else {
        // this.error = true;
        //this.errorMessages.push('Data not found');
        console.error('Invalid master fee table work flow history assign to response format:', response);
        return { assign_to: null, assign_to_nm: null }; // Data not found
      }
    } catch (error) {
      // this.error = true;
      //this.errorMessages.push('Internal Server Error.');
      console.error('There was an error retrieving the master fee table work flow history assign to:', error);
      return { assign_to: null, assign_to_nm: null }; // Error occurred
    }

  }

  //download start
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
  //download end

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
      i_fee_detail_pk: this.feeDetailPk,
    };

    try {
      const response: any = await this.http.post(url, Body, { headers }).toPromise();
      if (response.header.statusCode === '00') {
        this.totalMFTRecords = response.data[0].total;
        this.mfts = response.data;
        this.currentFeeDetailId = this.mfts[0].fee_detail_id;
        this.currentFeeGroupNmEn = this.mfts[0].fee_grp_nm_en;
        this.currentFeeDetailNmEn = this.mfts[0].fee_detail_nm_e;
        this.currentFeeDetailNmBm = this.mfts[0].fee_detail_nm_b;
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
        this.currentIsPublic = this.mfts[0].isPub;

        if (this.currentSsCd !== null) {
          //this.currentCheckboxSsCd = this.currentSsCd.split(',');
          this.currentSsCdWithSpace = this.currentSsCd.split(',').join(', '); //for display
        }
        //  this.currentCheckboxSsCd = this.currentSsCd.split(',');

      } else {
        this.totalMFTRecords = 0;
        console.error('Invalid master fee table response format:', response);
      }
    } catch (error) {
      console.error('There was an error retrieving the master fee table:', error);
    }
  }

  //email start

  async sendEmail() {

    const url = environment.apiUrl + '/api/mftemail/v1/backend';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    this.emailWfId = this.wfId;

    await this.getworkflowDetailAfterInsert(); //use wfid to find fee detail id and task id
    console.log("emailWfId is " + this.emailWfId);
    console.log("emailfeeDetailId is " + this.emailfeeDetailId);
    console.log("emailTaskId is " + this.emailTaskId);
    console.log("emailrfeeDetNm is " + this.emailrfeeDetNm);
    const Body: any = {

      i_wf_id: this.emailWfId,
      i_task_id: this.emailTaskId,
      i_fee_detail_pk: this.emailFeeDetailPk,
      i_fee_detail_id: this.emailfeeDetailId,
      i_r_fee_det_nm: this.emailrfeeDetNm,
      i_status: this.decision,
      i_action: this.emailAction,
      i_send_to: this.emailAssignTo,
      i_cc: "",
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

    try {
      const response: any = await this.http.post(urlMftWF, Body, { headers }).toPromise();

      if (response.header.statusCode === '00') {
        this.emailMftwfs = response.data;
        this.emailFeeDetailPk = this.emailMftwfs[0].fee_detail_pk;
        this.emailfeeDetailId = this.emailMftwfs[0].fee_detail_id;
        this.emailTaskId = this.emailMftwfs[0].task_id;
        this.emailrfeeDetNm = this.emailMftwfs[0].r_fee_det_nm;
        this.emailAction = this.emailMftwfs[0].action;

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

  checkPermission() {
    this.authService.checkUserRole(this.authService.username, this.permApproveAddAndEditMFTwithRequesterFormFHOD)
      .subscribe(
        (response: any) => {
          this.permApproveAddAndEditMFTwithRequesterFormFHODAllow = response.data;
          console.log("permApproveAddAndEditMFTwithRequesterFormFHODAllow is " + this.permApproveAddAndEditMFTwithRequesterFormFHODAllow);
          this.permApproveAdd = this.permApproveAddAndEditMFTwithRequesterFormFHODAllow.includes(perm.Master_Fee_Table_Approve_Add_MFT_with_Requester_Form_FHOD) ? 1 : 0;
          this.permApproveEdit = this.permApproveAddAndEditMFTwithRequesterFormFHODAllow.includes(perm.Master_Fee_Table_Approve_Edit_MFT_with_Requester_Form_FHOD) ? 1 : 0;
          if (this.permApproveAdd === 0 && this.editMode === false) {
            this.permListAllow = 0;
            this.router.navigate(['/access-denied']);
            return; // Exit the function to prevent further execution

          }
          else if (this.permApproveEdit === 0 && this.editMode === true) {
            this.permListAllow = 0;
            this.router.navigate(['/access-denied']);
            return; // Exit the function to prevent further execution
          }
          else {
            this.permListAllow = 1;
          }
        }
      );
  }

  // checkStatusAndAssignTo() {

  //   const urlMftWF = environment.apiUrl + '/api/mftwf/v1/getmasterfeetableworkflow';

  //   // Set your authorization header
  //   const headers = new HttpHeaders({
  //     Authorization: environment.authKey,
  //     'Content-Type': 'application/json'
  //   });

  //   const Body: any = {

  //     i_page: "1",
  //     i_size: "1",
  //     i_wf_id: this.wfId,
  //     i_wf_is_in_prg: "f"
  //   };

  //   this.http.post(urlMftWF, Body, { headers }).subscribe(
  //     (response: any) => {
  //       this.currentMFTWFDetail = response.data;
  //       this.currentMFTWFStatus = this.currentMFTWFDetail[0].status;
  //       this.currentMFTWFAssignTo = this.currentMFTWFDetail[0].assign_to;

  //       if (this.currentMFTWFAssignTo !== this.username || this.currentMFTWFStatus !== this.statusFromAssigned) {
  //         const alert_msg_access_denied = "This task is assigned or reassigned to another user."
  //         this.router.navigate(['/master-fee-table'], { state: { alert_msg_access_denied } });
  //         return; // Exit t he function to prevent further execution
  //       }
  //     }
  //   );
  // }

  async checkStatusAndAssignTo(clicked: boolean): Promise<boolean> {

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

        console.log("currentMFTWFAssignTo is " + this.currentMFTWFAssignTo);
        console.log("username is " + this.username);
        console.log("currentMFTWFStatus is " + this.currentMFTWFStatus);
        console.log("statusFromAssigned is " + this.statusFromAssigned);

        //  if (this.currentMFTWFAssignTo !== this.username || this.currentMFTWFStatus !== this.statusFromAssigned) {
        //   const alert_msg_access_denied = "This task is assigned or reassigned to another user."
        //   this.router.navigate(['/master-fee-table'], { state: { alert_msg_access_denied } });
        //   return false; // Exit t he function to prevent further execution
        // }

        if (this.currentMFTWFAssignTo !== this.username && this.currentMFTWFAssignTo !== null) {
          // const alert_msg_access_denied = "This task is assigned or reassigned to another user."
          // this.router.navigate(['/master-fee-table'], { state: { alert_msg_access_denied } });
          const showTaskNotUpdateAlert = true;
            this.router.navigate(['/my-task-assigned-tasks'], { state: { showTaskNotUpdateAlert} });
          return false;
        }

        if (clicked === false) {
          if (this.currentMFTWFStatus === "C" || this.currentMFTWFStatus === "APV" || this.currentMFTWFStatus === "EFT"
            || this.currentMFTWFStatus === "RJ-RHOD" || this.currentMFTWFStatus === "RJ-FA" || this.currentMFTWFStatus === "RJ-FHOD") {  //below parameter are different for each page
            // const alert_msg_task_canceled_closed = "This task has been canceled or closed and it is no longer active."
            // const wf_id = this.wfId;
            // const status__From_Assigned = this.currentMFTWFStatus;
            // const task_id = this.taskId;
            // const assign_to = this.currentMFTWFAssignTo;
            // const fee_detail_pk = this.feeDetailPk;
            // const edit_Mode = this.editMode;
            // const show_requester_table = true;
            // const from_view = false;
            // const navigate_not_refresh = true
            // this.router.navigate(['/task-details'], { state: { wf_id, status__From_Assigned, task_id, assign_to, fee_detail_pk, edit_Mode, show_requester_table, from_view, navigate_not_refresh } });
            const showTaskNotUpdateAlert = true;
            this.router.navigate(['/my-task-assigned-tasks'], { state: { showTaskNotUpdateAlert} });
            return false;
          }
        }
        else {
          if (this.currentMFTWFStatus !== this.statusFromAssigned) {
            // const alert_msg_access_denied = "This task is assigned or reassigned to another user."
            // this.router.navigate(['/master-fee-table'], { state: { alert_msg_access_denied } });
            // this.errorTaskNotUpdate = true;
            // this.errorMessagesTaskNotUpdate.push("The task status has been updated. Please refresh the page to reflect the latest changes.");
            const showTaskNotUpdateAlert = true;
            this.router.navigate(['/my-task-assigned-tasks'], { state: { showTaskNotUpdateAlert} });
            return false;
          }
        }

        return true;

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
