import { Component, OnInit, ViewChild, ElementRef, ChangeDetectorRef, ViewEncapsulation, AfterViewInit } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { environment } from 'src/environments/environment';
import { FeeGroup } from '../../core/models/fee-group';
import { DatePipe, DecimalPipe } from '@angular/common';
import { MFTWF, MFTWFDoc, MFTWFHist, Param, SourceSystemCode, TaxCode, User } from '../../core/models/entity';
import { forkJoin, lastValueFrom, of } from 'rxjs';
import { concatMap, delay, map } from 'rxjs/operators';
import { FormBuilder, FormControl, FormGroup, NgForm, NgModel, Validators } from '@angular/forms';
import { Systemstatus } from '../../shared/enums/systemstatus';
import { ActivatedRoute, Router } from '@angular/router';
import { DataService } from '../../core/services/data.service';
import { trigger, state, style, transition, animate } from '@angular/animations';
import { fadeInOut } from '../../shared/animation';
import { TranslateService } from '@ngx-translate/core';
import { GlobalService } from 'src/app/shared/global.service';
import { AuthService } from 'src/app/core/services/auth.service';
import { perm } from 'src/permissions/perm';
import { MatDialog } from '@angular/material/dialog';
import { ConfirmSubmitComponent } from '../confirm-submit/confirm-submit.component';



@Component({
  selector: 'app-mft-req-form-add',
  templateUrl: './mft-req-form-add.component.html',
  styleUrls: ['./mft-req-form-add.component.scss'],
  animations: [fadeInOut]
})
export class MftReqFormAddComponent implements OnInit {

  username = this.authService.username;
  roles = this.authService.roles;

  userHigherOfficialRole: string | null = null;
  feeGroups: FeeGroup[] = [];
  inputDetails: boolean = true;
  errorMessages: string[] = [];
  error: boolean = false;
  isLoading: boolean = false;
  page = environment.DefaultPage;
  itemsPerPage = environment.ItemPerPage;
  dropDownSize = environment.DropDownSize;
  totalRecords: number = 0;
  status: Param[] = [];
  checkboxRssCd: string[] | undefined = undefined;
  rSourceSystemCodes: SourceSystemCode[] = [];
  rSelectedSourceSystemCodes: any[] = [];
  queryFromStatus: string | null = null;
  queryBackTo: string | null = null;
  actionType: string | null = null;
  wfId: number | null = null;
  taskId: string | null = null;
  users: User[] = [];
  taxCode: TaxCode[] = [];
  mftwfHis: MFTWFHist[] = [];
  mftwfs: MFTWF[] = [];
  emailMftwfs: MFTWF[] = [];
  mftwfSupDocs: MFTWFDoc[] = [];
  rLlRequiredOptions: any[] = [
    { value: 0, label: 'No' },
    { value: 1, label: 'Yes' },
  ]
  file_content = "";
  statusFromAssigned: string | null = null;
  currentMFTWFDetail: MFTWF[] = [];
  currentMFTWFStatus: string | null = null;
  currentMFTWFAssignTo: string | null = null;
  errorMessagesTaskNotUpdate: string[] = [];
  errorTaskNotUpdate: boolean = false;
  errorFile: boolean = false;
  errorFileMessages: string[] = [];
  errorFileSizeLimit: boolean = false;
  errorFileSizeLimitMessages: string[] = [];
  errorFileDuplicate: boolean = false;
  errorFileDuplicateMessages: string[] = [];

  //insert
  rFeeDetNm: string | null = null;
  rFeeAmt: string | null = null;
  rScCd: string | null = null;
  rPromoStartDt: string | null = null;
  rPromoEndDt: string | null = null;
  rPromoFee: string | null = null;
  rLlRequired: string | number | null = null;
  rAddNotes: string | null = null;
  feeDetailId: string | null = null;
  feeGroupId: string | number | null = null;
  feeDetailNmE: string | null = null;
  feeDetailNmB: string | null = null;
  feeAmt: string | null = null;
  promoStartDate: string | null = null;
  promoEndDate: string | null = null;
  promoFee: string | null = null;
  taxCdId: string | number | null = null;
  allowOTC: string | number | null = null;
  llParentId: string | null = null;
  llStartDay: string | null = null;
  llStartMth: string | null = null;
  llEndDay: string | null = null;
  llEndMth: string | null = null;
  ledgerCd: string | null = null;
  createdBy: string | null = null
  createdByNm: string | null = null
  modifiedBy: string | null = null
  modifiedByNm: string | null = null
  dtCreated: string | null = null;
  dtModified: string | null = null;
  effectiveDate: string | null = null;
  textRemarks: string | null = null;
  ssm4uuserrefno: string | null = null;
  mftStatus: string | null = null;
  tempSsCd: string | null = null;
  isPublic: number|null = null;
  initialNumber: any[] = [
    { value: 0, label: 'No' },
    { value: 1, label: 'Yes' },
  ]

  //display
  /*displayRFeeDetNm: string | null = null;
  displayRFeeAmt: number | null = null;
  displayRScCd: string | null = null;
  displayRPromoStartDt: Date | null = null;
  displayRPromoEndDt: Date | null = null;
  displayRLlRequired: number | null = null;
  displayRAddNotes: string | null = null;
  displayEffectiveDate: Date | null = null;*/

  isDisplaySupDoc: boolean = false;
  isLoadingSupDoc: boolean = false;
  isDisplayHist: boolean = false;
  isLoadingHist: boolean = false;

  //promotion start date and end date validation
  currentDate: Date = new Date();
  promoStartDateAsDate: Date | null = null;
  promoEndDateAsDate: Date | null = null;
  effectiveDateAsDate: Date | null = null;
  passDateValidation: boolean = false;
  promotionStartDateNotSelected: boolean = true;
  promotionEndDateNotSelected: boolean = true;
  promotionStartDateInvalid: boolean = true;
  promotionEndDateInvalid: boolean = true;
  promotionEndDateLessThanStartDate: boolean = true;
  effectiveDateInvalid: boolean = true;
  minEffDate: Date = new Date();
  minPromoStartDate: Date = new Date();
  minPromoEndDate: Date = new Date();

  //files
  selectedFiles: File[] = [];
  selectedFilesSize: number = 0;
  isDisplayFileRequired: boolean = false;
  wfIdFromInsert: number | null = null;
  i_file_content: any;

  //email
  emailWfId: string | number | null = null;
  emailAssignTo: string | null = null;
  emailStatus: string | null = null;
  emailStatusEn: string | null = null;
  emailfeeDetailId: string | null = null;
  emailrfeeDetNm: string | null = null;
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

  // Configuring Permissions for User and roles variables
  permAddMFTRequesterFormR = perm.Master_Fee_Table_Add_MFT_Requester_Form_R; // all the perm_cd for this module seperated with comma
  permAddMFTRequesterFormRAllow = ""; // variable to store allowed permission for the user
  permListAllow: number = 0; // if 0 then not allow to view listing page, else allow
  // end configuration

  constructor(
    private http: HttpClient,
    private datePipe: DatePipe,
    private router: Router,
    private cdref: ChangeDetectorRef,
    private route: ActivatedRoute,
    private translate: TranslateService,
    public globalService: GlobalService,
    private authService: AuthService,
    private translateService: TranslateService,
    public dialog: MatDialog,

  ) {
    this.translate.setDefaultLang(this.globalService.getGlobalValue()); // default language
    this.translate.use(this.globalService.getGlobalValue());
  }


  async ngOnInit() {

    const queryParams = this.route.snapshot.queryParamMap;

    const tempWf_id = queryParams.get('wf_id');
    if (tempWf_id !== null && tempWf_id !== 'null') {
      this.wfId = +tempWf_id; //+ mean convert string to number
      //  console.log("Wfid from queryparam" + this.wfId)
    }
    else {
      this.wfId = history.state.wf_id;
    }

    const tempTask_id = queryParams.get('task_id');
    if (tempTask_id !== null && tempTask_id !== 'null') {
      this.taskId = tempTask_id; //+ mean convert string to number
    }
    else {
      this.taskId = history.state.task_id;
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
    this.userHigherOfficialRole = "REQUESTERHOD";

    this.minEffDate.setDate(this.minEffDate.getDate() + 1); // Set minDate to tomorrow
    this.minPromoStartDate.setDate(this.minPromoStartDate.getDate() + 1);
    this.minPromoEndDate.setDate(this.minPromoEndDate.getDate() + 2);

    this.isLoading = true;
    this.checkPermission();
    if (this.wfId !== undefined) {
      await this.checkStatusAndAssignTo(false);
    }
    await this.populateSourceSystemCode(); //check permission here
    if (this.wfId !== undefined) { //means query to requster

      this.inputDetails = false;
      this.actionType = "Add-Reply";
      this.loadDataSupDoc();
      this.loadDataHist();
      await this.populateForm();

      const tempQuery = await this.getworkflowhistory_status("Q-R");
      this.queryBackTo = tempQuery.assign_to;
      this.queryFromStatus = tempQuery.status;
      this.isLoading = false;
    }
    else {
      this.inputDetails = true;
      this.actionType = "Add";
      this.populateAppover();

      //this.isLoading = false;
    }
  }

  async insertMFTWF() {

    if(!this.rFeeAmt || this.rSelectedSourceSystemCodes.length === 0 || !this.rPromoStartDt || !this.rPromoEndDt
      || !this.rPromoFee  || this.rLlRequired === null || this.rLlRequired === undefined || this.rLlRequired === '' || !this.rAddNotes){
      const dialogRef = this.dialog.open(ConfirmSubmitComponent, {
        width: '20%',
      });
  
      // Wait for dialogRef.afterClosed() to finish
      const result = await lastValueFrom(dialogRef.afterClosed());
  
      if (result === 'no' || result === undefined) {
        return; // Stop execution if "No" is clicked
      }
    }

    this.isLoading = true;
    this.defaultSetting();
    // const isValid = await this.validation();

    let invalidUploadFile: boolean = false;
    const invalidInsert = await this.insertMasterFeeTableWorkFlow();

    if (this.selectedFiles.length !== 0) {
      invalidUploadFile = await this.readFileAsync(); //must put below insertMasterFeeTableWorkFlow();
    }

    if (invalidInsert === false && invalidUploadFile === false) {
      this.sendEmail();
      this.isLoading = false;
      const alert_msg = "submittedForApproval";
      this.router.navigate(['/master-fee-table'], { state: { alert_msg } });
    }

    this.isLoading = false;
  }


  //default setting start
  defaultSetting(): void {
    this.error = false;
    // this.errorMessage="";
    this.errorMessages = [];
  }

  async insertMasterFeeTableWorkFlow(): Promise<boolean> {
    const insertURL = environment.apiUrl + '/api/mftwf/v1/insertmasterfeetableworkflow';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json'
    });

    // let rFormattedStartDate = this.datePipe.transform(this.rPromoStartDt, 'dd/MM/yyyy') || null;
    // let rFormattedEndDate = this.datePipe.transform(this.rPromoEndDt, 'dd/MM/yyyy') || null;
    // let formattedEffectiveDate = this.datePipe.transform(this.effectiveDate, 'dd/MM/yyyy') || null;

    let resultString = this.rSelectedSourceSystemCodes.join(',');

    const Body: any = {
      i_fee_detail_pk: null,
      i_fee_detail_id: null,
      i_fee_grp_id: null,
      i_fee_detail_nm_e: null,
      i_fee_detail_nm_b: null,
      i_fee_amt: null,
      i_promo_startdt: null,
      i_promo_enddt: null,
      i_promo_fee: null,
      i_tax_cd_id: null,
      i_allow_otc: null,
      i_ll_parent_id: null,
      i_ll_start_day: null,
      i_ll_start_mth: null,
      i_ll_end_day: null,
      i_ll_end_mth: null,
      i_ledger_cd: null,
      i_ss_cd: null,
      i_created_by: this.username,
      i_modified_by: this.username,
      i_status: "P-RHOD",
      i_effective_date: this.effectiveDate,
      i_remark: this.textRemarks,
      i_assign_to: this.ssm4uuserrefno,
      i_action: "Request Add"
    };

    if (this.rFeeDetNm && this.rFeeDetNm.trim()) {
      Body.i_r_fee_det_nm = this.rFeeDetNm;
    }

    if (this.rFeeAmt && this.rFeeAmt.trim()) {
      Body.i_r_fee_amt = this.rFeeAmt;
    }

    if (resultString && resultString.trim()) {
      Body.i_r_ss_cd = resultString;
    }

    if (this.rPromoStartDt) {
      Body.i_r_promo_startdt = this.rPromoStartDt;
    }

    if (this.rPromoEndDt) {
      Body.i_r_promo_enddt = this.rPromoEndDt;
    }

    if (this.rPromoFee && this.rPromoFee.trim()) {
      Body.i_r_promo_fee = this.rPromoFee;
    }

    Body.i_r_ll_required = this.rLlRequired;

    Body.i_ispub = this.isPublic;

    if (this.rAddNotes && this.rAddNotes.trim()) {
      Body.i_r_add_notes = this.rAddNotes;
    }

    Body.i_mft_status = null;

    try {
      const response: any = await this.http.post(insertURL, Body, { headers }).toPromise();
      if (response.header.statusCode === '00') {
        this.wfIdFromInsert = response.data;
        return false; // Insert success
      } else {
        this.error = true;
        this.errorMessages.push('Submit not successful');
        return true; // Insert failed
      }
    } catch (error) {
      this.error = true;
      this.errorMessages.push('Internal Server Error.');
      console.error(error);
      return true; // Error occurred
    }
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

      i_page: this.page.toString(),
      i_size: "1",
      i_wf_id: this.wfId,
      i_wf_is_in_prg: "f"
    };

    try {
      const response: any = await this.http.post(urlMftWF, Body, { headers }).toPromise();
      if (response.header.statusCode === '00') {
        this.totalRecords = response.data[0].total;
        this.mftwfs = response.data;
        this.feeDetailId = this.mftwfs[0].fee_detail_id === null ? null : this.mftwfs[0].fee_detail_id.toString();
        this.feeGroupId = this.mftwfs[0].fee_grp_id === null ? null : this.mftwfs[0].fee_grp_id.toString();
        this.feeDetailNmE = this.mftwfs[0].fee_detail_nm_e === null ? null : this.mftwfs[0].fee_detail_nm_e.toString();
        this.feeDetailNmB = this.mftwfs[0].fee_detail_nm_b === null ? null : this.mftwfs[0].fee_detail_nm_b.toString();
        this.feeAmt = this.mftwfs[0].fee_amt === null ? null : this.mftwfs[0].fee_amt.toFixed(2).toString();
        this.promoStartDate = this.mftwfs[0].promo_startdt === null ? null : this.mftwfs[0].promo_startdt.toString();
        this.promoEndDate = this.mftwfs[0].promo_enddt === null ? null : this.mftwfs[0].promo_enddt.toString();
        this.promoFee = this.mftwfs[0].promo_fee === null ? null : this.mftwfs[0].promo_fee.toFixed(2).toString();
        this.taxCdId = this.mftwfs[0].tax_cd_id === null ? null : this.mftwfs[0].tax_cd_id.toString();
        this.allowOTC = this.mftwfs[0].allow_otc === null ? null : this.mftwfs[0].allow_otc.toString();
        this.llParentId = this.mftwfs[0].ll_parent_id === null ? null : this.mftwfs[0].ll_parent_id.toString();
        this.llStartDay = this.mftwfs[0].ll_start_day === null ? null : this.mftwfs[0].ll_start_day.toString();
        this.llEndDay = this.mftwfs[0].ll_end_day === null ? null : this.mftwfs[0].ll_end_day.toString();
        this.llStartMth = this.mftwfs[0].ll_start_mth === null ? null : this.mftwfs[0].ll_start_mth.toString();
        this.llEndMth = this.mftwfs[0].ll_end_mth === null ? null : this.mftwfs[0].ll_end_mth.toString();
        this.ledgerCd = this.mftwfs[0].ledger_cd === null ? null : this.mftwfs[0].ledger_cd.toString();

        this.tempSsCd = this.mftwfs[0].ss_cd === null ? null : this.mftwfs[0].ss_cd.toString();

        this.effectiveDate = this.mftwfs[0].effective_date === null ? null : this.mftwfs[0].effective_date.toString();

        this.mftStatus = this.mftwfs[0].mft_status === null ? null : this.mftwfs[0].mft_status.toString();
        this.rFeeDetNm = this.mftwfs[0].r_fee_det_nm === null ? null : this.mftwfs[0].r_fee_det_nm.toString();
        this.rFeeAmt = this.mftwfs[0].r_fee_amt === null ? null : this.mftwfs[0].r_fee_amt.toFixed(2).toString();

        // this.displayRScCd = this.mftwfs[0].r_ss_cd;
        if (this.mftwfs[0].r_ss_cd === null || this.mftwfs[0].r_ss_cd === undefined) {
          this.rSelectedSourceSystemCodes = [];
        }
        else {

          let temprSelectedSourceSystemCodes = this.mftwfs[0].r_ss_cd.toString().split(',');
          this.rSelectedSourceSystemCodes = []; //when perform array binding to ngmodel, need to declare again

          for (let i = 0; i < temprSelectedSourceSystemCodes.length; i++) {
            if (this.rSourceSystemCodes.some(rSourceSystemCode => rSourceSystemCode.ss_cd.includes(temprSelectedSourceSystemCodes[i]))) {
              this.rSelectedSourceSystemCodes.push(temprSelectedSourceSystemCodes[i]);
            }
          }
        }

        this.rPromoStartDt = this.mftwfs[0].r_promo_startdt === null ? null : this.mftwfs[0].r_promo_startdt.toString();
        this.rPromoEndDt = this.mftwfs[0].r_promo_enddt === null ? null : this.mftwfs[0].r_promo_enddt.toString();
        this.rPromoFee = this.mftwfs[0].r_promo_fee === null ? null : this.mftwfs[0].r_promo_fee.toFixed(2).toString();
        this.rLlRequired = this.mftwfs[0].r_ll_required === null ? null : this.mftwfs[0].r_ll_required;
        this.isPublic = this.mftwfs[0].is_pub === null ? null : this.mftwfs[0].is_pub;
        this.rAddNotes = this.mftwfs[0].r_add_notes === null ? null : this.mftwfs[0].r_add_notes.toString();
      } else {
        this.totalRecords = 0;
        console.error('Invalid master fee table work flow response format:', response);
      }
    } catch (error) {
      console.error('There was an error retrieving the master fee table work flow:', error);
      // return true; // Error occurred
    }
  }


  populateAppover() {

    const url = environment.apiUrl + '/api/mft/v1/getuserbyrole';

    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json'
    });


    const requestBody = {
      i_page: this.page,
      i_size: this.dropDownSize,
      i_role_nm_en: this.userHigherOfficialRole,
      i_role_nm_bm: this.userHigherOfficialRole,
      i_status: Systemstatus.Active
    };

    this.http.post(url, requestBody, { headers }).subscribe(
      (response: any) => {
        if (response.data.length == 0) {
          this.isLoading = false;
          console.error('Invalid approver response format:', response);
        }
        else {
          this.isLoading = false;
          this.users = response.data.filter((user: User) => user.ssm4uuserrefno !== this.username);
        }
      },
      (error) => {
        this.isLoading = false;
        console.error('There was an error retrieving the approver:', error);
        // Handle errors here
      }
    );
  }

  clearFiles() {
    if (this.selectedFiles.length > 0) {//added condition because if not, when user didn't  
      this.selectedFiles = [];           //select any file and click clear file will prompt error which is incorrect
      this.selectedFilesSize = 0;
      this.isDisplayFileRequired = true;
    }
    this.errorFile = false;
    this.errorFileMessages = [];
    this.errorFileSizeLimit = false;
    this.errorFileSizeLimitMessages = [];
    this.errorFileDuplicate = false;
    this.errorFileDuplicateMessages = [];

    // Reset file input to allow re-selection of the same file
    const fileInput = document.getElementById('fileInput') as HTMLInputElement;
    if (fileInput) {
      fileInput.value = ''; // Clears the file input field
    }

  }

  async populateSourceSystemCode(): Promise<void> {


    const url = environment.apiUrl + '/api/rms/v1/getsourcesystem';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json'
    });


    // Create the request body with your form data
    const Body = {
      i_page: this.page,
      i_size: this.dropDownSize,
      i_ss_id: null,
      i_ss_cd: null,
      i_ss_nm: null,
      i_modified_by: null,
      i_dt_modified_fr: null,
      i_dt_modified_to: null,
      i_status: Systemstatus.Active
    };

    try {
      const response: any = await this.http.post(url, Body, { headers }).toPromise();
      if (response.header.statusCode === '00') {
        this.rSourceSystemCodes = response.data
        // return false; // Insert success
      } else {
        // this.error = true;
        // this.errorMessages.push('Insert not successful');
        console.error('Invalid source system code response format:', response);
        // return true; // Insert failed
      }
    } catch (error) {
      //this.error = true;
      // this.errorMessages.push('Internal Server Error.');
      console.error('There was an error retrieving the source system code:', error);
      // return true; // Error occurred
    }

  }

  /* async validation(): Promise<boolean> {
 
     let invalidMTF = true
     let invalidMTFWF = true
 
     if (!this.feeDetailId) {
       // Form is not valid, you can handle this case or simply return
       return true;
     }
 
 
     invalidMTF = await this.checkDuplicateMFT()
     invalidMTFWF = await this.checkDuplicateMFTWF()
 
     if (invalidMTF === false && invalidMTFWF === false) {
       return false
     }
     else {
       return true
     }
 
   }
 
   async checkDuplicateMFT() {
 
     const url = environment.apiUrl + '/api/mft/v1/getmasterfeetable';
 
     const headers = new HttpHeaders({
       Authorization: environment.authKey,
       'Content-Type': 'application/json',
     });
 
     const body: any = {
       i_page: '1',
       i_size: '1',
       i_fee_detail_id: this.feeDetailId,
 
     };
 
     try {
       const response: any = await this.http
         .post(url, body, { headers })
         .toPromise();
       console.log('bng' + response.header.statusCode)
       if (response.header.statusCode === '01') {
         return false;
       } else {
         this.error = true;
         this.errorMessages.push('Fee Detail ID is duplicate in Master Fee Table.');
         return true;
       }
     } catch (error) {
       this.error = true;
       this.errorMessages.push('Internal Server Error.');
       console.error(error);
       return true;
     }
 
   }
 
   async checkDuplicateMFTWF() {
 
     const url = environment.apiUrl + '/api/mftwf/v1/getmasterfeetableworkflow';
 
     const headers = new HttpHeaders({
       Authorization: environment.authKey,
       'Content-Type': 'application/json',
     });
 
     const body: any = {
       i_page: '1',
       i_size: '1',
       i_fee_detail_id: this.feeDetailId,
 
     };
 
     try {
       const response: any = await this.http
         .post(url, body, { headers })
         .toPromise();
       console.log('bng' + response.header.statusCode)
       if (response.header.statusCode === '01') {
         return false;
       } else {
         this.error = true;
         this.errorMessages.push('Fee Detail ID is duplicate Master Fee Table work flow.');
         return true;
       }
     } catch (error) {
       this.error = true;
       this.errorMessages.push('Internal Server Error.');
       console.error(error);
       return true;
     }
 
   }
 */
  //validation end

  //form handle before submit start
  async handleFormSubmit(form: NgForm) {

    let formValidation: boolean | null = false;

    if (!this.promotionStartDateNotSelected && !this.promotionEndDateNotSelected && !this.promotionStartDateInvalid && !this.promotionEndDateInvalid && !this.promotionEndDateLessThanStartDate) {
      this.passDateValidation = true;
    }
    else {
      this.passDateValidation = false;
    }

    if (this.inputDetails === true) {
      //file upload display check
      if (this.selectedFiles.length === 0) {
        this.isDisplayFileRequired = true;
      }
      else {
        this.isDisplayFileRequired = false;
      }

      formValidation = form.valid && (this.selectedFiles.length !== 0) && this.passDateValidation && !this.effectiveDateInvalid;
      // console.log("form valid is " + form.valid);
      //  console.log("selected files is " + this.selectedFiles.length);
      // console.log("pass date validation is " + this.passDateValidation);
      // console.log("effective date valid is " + this.effectiveDateInvalid);

    }
    else {
      formValidation = form.valid && this.passDateValidation && !this.effectiveDateInvalid;
      // formValidation = form.valid;
      //  console.log("form valid is " + form.valid);
      //  console.log("effective date valid is " + this.effectiveDateInvalid);
    }

    if (formValidation) {
      // if (this.wfId !== undefined) {
      //   this.updateMFTWF();
      // }
      // else {
      //   this.insertMFTWF();
      // }
      if (this.wfId !== undefined) { //means come from assgined pages, query to FA
        const validProceedToSubmit = await this.checkStatusAndAssignTo(true); //ensure the mftwf status is updated before submit
        if (validProceedToSubmit === true) { //only when update require to checkStatusAndAssignTo
          this.updateMFTWF();
        }
        else {
          console.log("Invalid proceed to submit")
        }
      }
      else {
        this.insertMFTWF();
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

  //update master fee table workflow status start
  async updateMFTWF() {
    this.isLoading = true;
    this.defaultSetting();

    let invalidUploadFile: boolean = false;
    const invalidUpdate = await this.updateMasterFeeTableWorkFlow();

    if (this.selectedFiles.length !== 0) {
      invalidUploadFile = await this.readFileAsync(); //must put below insertMasterFeeTableWorkFlow();
    }

    if (invalidUpdate === false && invalidUploadFile === false) {
      this.sendEmail();
      this.isLoading = false;
      const alert_msg = "submittedForApproval";
      this.router.navigate(['/my-task-assigned-tasks'], { state: { alert_msg } });
    }

    this.isLoading = false;
  }

  async updateMasterFeeTableWorkFlow(): Promise<boolean> {

    const updmftwfUrl = environment.apiUrl + '/api/mftwf/v1/updatemasterfeetableworkflow';

    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json'
    });


    //change date format from dd MMM yyyy to yyyy-MM-dd
    let formattedPromoStartDate = this.datePipe.transform(this.promoStartDate, 'yyyy-MM-dd') || "";
    let formattedPromoEndDate = this.datePipe.transform(this.promoEndDate, 'yyyy-MM-dd') || "";
    let formattedRPromoStartDt = this.datePipe.transform(this.rPromoStartDt, 'yyyy-MM-dd') || "";
    let formattedRPromoEndDt = this.datePipe.transform(this.rPromoEndDt, 'yyyy-MM-dd') || "";
    let formattedEffectiveDate = this.datePipe.transform(this.effectiveDate, 'yyyy-MM-dd') || "";

    let resultString = this.rSelectedSourceSystemCodes.join(',');
    //console.log(resultString);

    const Body: any = {
      i_wf_id: this.wfId,
      i_fee_detail_pk: null,
      i_fee_detail_id: this.feeDetailId,
      i_fee_grp_id: this.feeGroupId,
      i_fee_detail_nm_e: this.feeDetailNmE,
      i_fee_detail_nm_b: this.feeDetailNmB,
      i_fee_amt: this.feeAmt,
      i_promo_startdt: formattedPromoStartDate,
      i_promo_enddt: formattedPromoEndDate,
      i_promo_fee: this.promoFee,
      i_tax_cd_id: this.taxCdId,
      i_allow_otc: this.allowOTC,
      i_ll_parent_id: this.llParentId,
      i_ll_start_day: this.llStartDay,
      i_ll_end_day: this.llEndDay,
      i_ll_start_mth: this.llStartMth,
      i_ll_end_mth: this.llEndMth,
      i_ledger_cd: this.ledgerCd,
      i_ss_cd: this.tempSsCd
    };


    if (this.effectiveDate) {
      Body.i_effective_date = formattedEffectiveDate;
    }

    Body.i_modified_by = this.username;
    Body.i_status = this.queryFromStatus;
    Body.i_assign_to = this.queryBackTo;
    Body.i_remark = this.textRemarks;
    Body.i_action = "Request Add";

    if (this.rFeeDetNm && this.rFeeDetNm.trim()) {
      Body.i_r_fee_det_nm = this.rFeeDetNm;
    }

    if (this.rFeeAmt && this.rFeeAmt.trim()) {
      Body.i_r_fee_amt = this.rFeeAmt;
    }

    if (resultString && resultString.trim()) {
      Body.i_r_ss_cd = resultString;
    }

    if (this.rPromoStartDt) {
      Body.i_r_promo_startdt = formattedRPromoStartDt;
    }

    if (this.rPromoEndDt) {
      Body.i_r_promo_enddt = formattedRPromoEndDt;
    }

    if (this.rPromoFee && this.rPromoFee.trim()) {
      Body.i_r_promo_fee = this.rPromoFee;
    }

    Body.i_r_ll_required = this.rLlRequired;

    Body.i_ispub = this.isPublic;

    if (this.rAddNotes && this.rAddNotes.trim()) {
      Body.i_r_add_notes = this.rAddNotes;
    }

    Body.i_mft_status = this.mftStatus;

    try {
      const response: any = await this.http.post(updmftwfUrl, Body, { headers }).toPromise();
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
  //update master fee table workflow status end

  cancel() {
    // this.dataService.setShowInsertAlert(false)
    if (this.wfId !== undefined){
      this.router.navigate(['/my-task-assigned-tasks']);
    }
    else{
      this.router.navigate(['/home']); 
    }
  }

  /*OnlyNumberAllowed(event:any):boolean{
  
    const charCode = (event.which)?event.which : event.keyCode;
  
      if(charCode>31 && (charCode <48 || charCode>57)){
        return false;
      }
      return true;
  }*/


  //decimalInput: string = ''; // Changed to string type to directly bind to input value
  //fee amt checking start
  formatInput(rFeeAmtRef: any): void {
    let value = parseFloat(rFeeAmtRef.value);
    if (!isNaN(value) && value >= 0) {
      this.rFeeAmt = value.toFixed(2); // Convert number to string with 2 decimal places
    } else if (value < 0) {
      rFeeAmtRef.control.setErrors({ negative: true }); // Set a custom 'negative' error
    }
  }

  validateInput(ref: any): void {
    let value = ref.value;
    // Check for more than one dot or more than two decimal places
    if ((value.match(/\./g) || []).length > 1 || (value.includes('.') && value.split('.')[1].length > 2)) {
      ref.control.setErrors({ pattern: true }); // Set a custom 'pattern' error
    }
  }
  //fee amt checking end

  //rPromoFee checking start
  formatInputRPromoFee(rPromoFeeRef: any): void {
    let value = parseFloat(rPromoFeeRef.value);
    if (!isNaN(value) && value >= 0) {
      this.rPromoFee = value.toFixed(2); // Convert number to string with 2 decimal places
    } else if (value < 0) {
      rPromoFeeRef.control.setErrors({ negative: true }); // Set a custom 'negative' error
    }
  }
  //rPromoFee checking end

  onClosed(formControl: any) {
    formControl.control.markAsTouched();
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


  onKeyDownBackspace(event: KeyboardEvent): void {
    if (event.key === 'Backspace') {
      event.preventDefault(); // Prevents backspace from working
    }
  }

  onKeyDown(event: KeyboardEvent): void {
    if (event.key === 'Backspace') {
      this.promoStartDate = null; // Clear the date
      this.promotionStartDateNotSelected = true; // Show error

      // Manually trigger Angular change detection if needed
      this.isPromoStartDateNotSelected();

      event.preventDefault(); // Stop further manual editing
    }
  }

  clearEndDate(event: KeyboardEvent) {
    if (event.key === 'Backspace' && this.rPromoEndDt) {
      this.rPromoEndDt = null; // Clears the date
      event.preventDefault(); // Prevent default backspace behavior
    } else {
      event.preventDefault(); // Disable all other keys
    }
  }

  clearStartDate(event: KeyboardEvent) {
    if (event.key === 'Backspace' && this.rPromoStartDt) {
      this.rPromoStartDt = null; // Clears the date
      event.preventDefault(); // Prevent default backspace behavior
    } else {
      event.preventDefault(); // Disable all other keys
    }
  }



  /* isCheck(checked: boolean) {
     if (checked) {
       this.rLlRequired = "1";
     }
     else {
       this.rLlRequired = "0";
     }
     //console.log("The status of checkbox is : "+this.llodgement)
   }*/

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



  async getworkflowhistory_status(status: string): Promise<{ assign_to: string | null, assign_to_nm: string | null, status: string | null }> {

    const url = environment.apiUrl + '/api/mftwfh/v1/getworkflowhistory_status';

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
          assign_to_nm: response.data[0].assign_to_nm,
          status: response.data[0].status
        }; // Data found
      } else {
        // this.error = true;
        // this.errorMessages.push('Data not found');
        console.error('Invalid master fee table work flow history assign to by status response format:', response);
        return { assign_to: null, assign_to_nm: null, status: null }; // Data not found
      }
    } catch (error) {
      //this.error = true;
      // this.errorMessages.push('Internal Server Error.');
      console.error('There was an error retrieving the master fee table work flow history assign to by status:', error);
      return { assign_to: null, assign_to_nm: null, status: null }; // Error occurred
    }
  }

  //promomotion start, end and effective date start
  promotionDateConvertion() {
    if (this.rPromoStartDt === undefined) { //this is because after select date and when backspace to remove, this become undefined
      this.rPromoStartDt = null;
      this.cdref.detectChanges();
    }
    if (this.rPromoEndDt === undefined) { //this is because after select date and when backspace to remove, this become undefined
      this.rPromoEndDt = null;
      this.cdref.detectChanges();
    }

    if (this.rPromoStartDt !== null) {
      this.promoStartDateAsDate = new Date(this.rPromoStartDt);
      this.promoStartDateAsDate.setHours(0, 0, 0, 0);
    }
    else {
      this.promoStartDateAsDate = null;
    }

    if (this.rPromoEndDt !== null) {
      this.promoEndDateAsDate = new Date(this.rPromoEndDt);
      this.promoEndDateAsDate.setHours(0, 0, 0, 0);
    }
    else {
      this.promoEndDateAsDate = null;
    }
  }

  isPromoStartDateInvalid(): boolean {

    this.promotionDateConvertion();

    // Check if promoStartDateAsDate is not null
    if (this.promoStartDateAsDate !== null) {

      // Check if promoStartDateAsDate is less than or equal to today
      if (this.promoStartDateAsDate <= this.currentDate) {
        this.promotionStartDateInvalid = true;
        return true;
      }
      else {
        this.promotionStartDateInvalid = false;
      }
    }
    this.promotionStartDateInvalid = false;
    return false;
  }

  isPromoEndDateInvalid(): boolean {

    this.promotionDateConvertion();

    // Check if promoEndDateAsDate is not null
    if (this.promoEndDateAsDate !== null) {

      // Check if promoEndDateAsDate is less than or equal to today
      if (this.promoEndDateAsDate <= this.currentDate) {
        this.promotionEndDateInvalid = true;
        return true;
      }
      else {
        this.promotionEndDateInvalid = false;
      }
    }
    this.promotionEndDateInvalid = false;
    return false;
  }

  isPromoStartDateNotSelected(): boolean {
    this.promotionDateConvertion();

    if ((this.promoEndDateAsDate !== null) && this.promoStartDateAsDate === null) {
      this.promotionStartDateNotSelected = true;
      return true;
    }
    else {
      this.promotionStartDateNotSelected = false;
      return false;
    }
  }

  isPromoEndDateNotSelected(): boolean {
    this.promotionDateConvertion();

    if ((this.promoStartDateAsDate !== null) && this.promoEndDateAsDate === null) {
      this.promotionEndDateNotSelected = true;
      return true;
    }
    else {
      this.promotionEndDateNotSelected = false;
      return false;
    }
  }


  isPromoEndDateLessThanPromoEndDate(): boolean {

    if (this.promoEndDateAsDate !== null && this.promoStartDateAsDate !== null) {
      if (this.promoEndDateAsDate <= this.promoStartDateAsDate) {
        this.promotionEndDateLessThanStartDate = true;
        return true;
      }
      else {
        this.promotionEndDateLessThanStartDate = false;
        return false;
      }
    }
    this.promotionEndDateLessThanStartDate = false;
    return false;

  }

  isEffectiveDateInvalid(): boolean {

    if (this.effectiveDate === undefined) { //this is because after select date and when backspace to remove, this become undefined
      this.effectiveDate = null;
      this.cdref.detectChanges();
    }

    if (this.effectiveDate !== null) {
      this.effectiveDateAsDate = new Date(this.effectiveDate);
      this.effectiveDateAsDate.setHours(0, 0, 0, 0);
    }
    else {
      this.effectiveDateAsDate = null;
    }

    // Check if effectiveDateAsDate is not null
    if (this.effectiveDateAsDate !== null) {

      // Check if effectiveDateAsDate is less than or equal to today
      if (this.effectiveDateAsDate <= this.currentDate) {
        this.effectiveDateInvalid = true;
        return true;
      }
      else {
        this.effectiveDateInvalid = false;
      }
    }
    this.effectiveDateInvalid = false;
    return false;

  }
  //promomotion start, end and effective date end

  //upload file start
  onFileButtonClicked() {
    if (this.inputDetails === true) {
      if (this.selectedFiles.length === 0) {
        this.isDisplayFileRequired = true;
      }
      else {
        this.isDisplayFileRequired = false;
      }
    }

  }


  async onFileSelected(event: any) {

    if (event.target.files) {
      let files = event.target.files;
      let currnetFilesTotalSize = 0;
      const allowedExtensions = ['png', 'jpeg', 'jpg', 'pdf', 'doc', 'docx'];
      this.errorFile = false;
      this.errorFileMessages = [];
      this.errorFileSizeLimit = false;
      this.errorFileSizeLimitMessages = [];
      this.errorFileDuplicate = false;
      this.errorFileDuplicateMessages = [];

      // // **Check if any file is invalid**
      // for (let i = 0; i < files.length; i++) {
      //   const fileName = files[i].name;
      //   const fileExtension = fileName.split('.').pop().toLowerCase(); // Extract file extension

      //   if (!allowedExtensions.includes(fileExtension)) {
      //     //   alert(`Invalid file format detected: "${fileName}". All files have been removed. Please select only valid formats: .png, .jpeg, .jpg, .pdf, .doc, .docx`);
      //     this.translateService.get('invalidformatmessage').subscribe((translation: string) => {
      //       this.errorFileMessage = translation;
      //     });

      //     this.selectedFiles = [];
      //     this.selectedFilesSize = 0;
      //     const fileInput = document.getElementById('fileInput') as HTMLInputElement;
      //     if (fileInput) {
      //       fileInput.value = ''; // Clears the file input field
      //     }
      //     return; // **Exit function**
      //   }
      // }


      for (let i = 0; i < files.length; i++) {
        //currnetFilesTotalSize += files[i].size;
        console.log('This file size is ' + files[i].size)
        console.log('recent selected file size is ' + this.selectedFilesSize)

        const fileName = files[i].name;
        const fileExtension = fileName.split('.').pop().toLowerCase(); // Extract file extension

        // **Check if the file extension is allowed**
        if (!allowedExtensions.includes(fileExtension)) {
          this.errorFile = true;
          this.errorFileMessages.push(fileName);
          continue; // Skip this file and check the next one
        }

        currnetFilesTotalSize += files[i].size;

        // Check if the file with the same name already exists in selectedFiles
        const isDuplicate = this.selectedFiles.some((file) => file.name === files[i].name);
        console.log('Sum of file size is ' + currnetFilesTotalSize + this.selectedFilesSize)
        if (!isDuplicate && (currnetFilesTotalSize + this.selectedFilesSize) <= 5 * 1024 * 1024) {
          this.selectedFiles.push(files[i]);
          this.selectedFilesSize += files[i].size;
        } else if (isDuplicate) {
          // alert(`File "${files[i].name}" already selected. Please choose a different file.`);
          this.errorFileDuplicate = true;
          this.errorFileDuplicateMessages.push(files[i].name);
        } else {
          //alert('Total file size exceeds 5MB. Please select smaller files.');
          this.errorFileSizeLimit = true;
          this.errorFileSizeLimitMessages.push('Total file size exceeds 5MB. Please select smaller files.');
        }
      }
      console.log('total size' + this.selectedFilesSize);

      if (this.inputDetails === true) {
        if (this.selectedFiles.length === 0) {
          this.isDisplayFileRequired = true;
        }
        else {
          this.isDisplayFileRequired = false;
        }
      }
    }

  }

  async readFileAsync(): Promise<boolean> {
    let result: boolean = false;
    for (const file of this.selectedFiles) {

      this.i_file_content = await new Promise((resolve, reject) => {
        const reader = new FileReader();

        reader.onload = (e: any) => {
          resolve(e.target.result);
        };

        reader.onerror = reject;

        reader.readAsDataURL(file);
      });

      result = await this.uploadFile(file);
    }
    return result;
  }


  async uploadFile(file: File): Promise<boolean> {
    // const formData: FormData = new FormData();
    // formData.append('file', file, file.name);

    const url = environment.apiUrl + '/api/mftwfdoc/v1/addmasterfeetableworkflowdocument';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: 'Basic cm95OnBhc3M=',
      'Content-Type': 'application/json'
    });

    let tempWfId: number | null
    if (this.wfId !== undefined) {
      tempWfId = this.wfId;
    }
    else {
      tempWfId = this.wfIdFromInsert;
    }

    const Body: any = {
      i_wf_id: tempWfId,
      i_file_nm: file.name,
      i_file_content: this.i_file_content,
      i_file_type: file.type,
      i_file_size_kb: file.size.toString(),
      i_created_by: null,
      i_modified_by: null,
      i_status: Systemstatus.Active

    };

    try {
      const response: any = await this.http.post(url, Body, { headers }).toPromise();
      if (response.header.statusCode === '00') {
        return false; // Insert success
      } else {
        this.error = true;
        this.errorMessages.push('Upload files not successful');
        return true; // Insert failed
      }
    } catch (error) {
      this.error = true;
      this.errorMessages.push('Internal Server Error.');
      console.error(error);
      return true; // Error occurred
    }
  }

  //upload file end

  //email start

  async sendEmail() {

    const url = environment.apiUrl + '/api/mftemail/v1/backend';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    if (this.wfId !== undefined) { //query from others
      this.emailWfId = this.wfId;
      this.emailAssignTo = this.queryBackTo;
      this.emailStatus = this.queryFromStatus;
    }
    else { //create new work flow
      this.emailWfId = this.wfIdFromInsert;
      this.emailAssignTo = this.ssm4uuserrefno;
      this.emailStatus = "P-RHOD";
    }



    await this.getworkflowDetailAfterInsert(); //use wfid to find fee detail id and task id
    console.log("emailWfId is " + this.emailWfId);
    console.log("emailfeeDetailId is " + this.emailfeeDetailId);
    console.log("emailTaskId is " + this.emailTaskId);
    console.log("emailrfeeDetNm is " + this.emailrfeeDetNm);
    const Body: any = {

      i_wf_id: this.emailWfId,
      i_task_id: this.emailTaskId,
      i_fee_detail_id: this.emailfeeDetailId,
      i_r_fee_det_nm: this.emailrfeeDetNm,
      i_status: this.emailStatus,
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
    this.authService.checkUserRole(this.authService.username, this.permAddMFTRequesterFormR)
      .subscribe(
        (response: any) => {
          this.permAddMFTRequesterFormRAllow = response.data;
          console.log("this.permAddMFTRequesterFormRAllow " + this.permAddMFTRequesterFormRAllow);
          this.permListAllow = this.permAddMFTRequesterFormRAllow.includes(perm.Master_Fee_Table_Add_MFT_Requester_Form_R) ? 1 : 0;
          if (this.permListAllow === 0) {
            this.router.navigate(['/access-denied']);
            return; // Exit t he function to prevent further execution

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
  //         this.router.navigate(['/access-denied']);
  //         return; // Exit t he function to prevent further execution
  //       }
  //     }
  //   );
  //}

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

        // if (this.currentMFTWFAssignTo !== this.username || this.currentMFTWFStatus !== this.statusFromAssigned) {
        //           this.router.navigate(['/access-denied']);
        //           return false; // Exit t he function to prevent further execution
        //         }
        if (this.currentMFTWFAssignTo !== this.username && this.currentMFTWFAssignTo !== null) {
          // this.router.navigate(['/access-denied']);
          const showTaskNotUpdateAlert = true;
          this.router.navigate(['/my-task-assigned-tasks'], { state: { showTaskNotUpdateAlert } });
          return false;
        }

        if (clicked === false) {
          if (this.currentMFTWFStatus === "C" || this.currentMFTWFStatus === "APV" || this.currentMFTWFStatus === "EFT"
            || this.currentMFTWFStatus === "RJ-RHOD" || this.currentMFTWFStatus === "RJ-FA" || this.currentMFTWFStatus === "RJ-FHOD") { //below parameter are different for each page
            //this.router.navigate(['/access-denied']);
            const showTaskNotUpdateAlert = true;
            this.router.navigate(['/my-task-assigned-tasks'], { state: { showTaskNotUpdateAlert } });
            return false;
          }
        }
        else {
          if (this.currentMFTWFStatus !== this.statusFromAssigned) {
            // this.errorTaskNotUpdate = true;
            //this.errorMessagesTaskNotUpdate.push("The task status has been updated. Please refresh the page to reflect the latest changes.");
            const showTaskNotUpdateAlert = true;
            this.router.navigate(['/my-task-assigned-tasks'], { state: { showTaskNotUpdateAlert } });
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

  // Update minPromoEndDate dynamically using
  onStartDateChange(selectedStartDate: Date) {
    if (selectedStartDate) {
      // Ensure minPromoEndDate is at least 1 day after the selected start date
      const newMinEndDate = new Date(selectedStartDate);
      newMinEndDate.setDate(newMinEndDate.getDate() + 1);
      this.minPromoEndDate = newMinEndDate;

      let tempStartDate: Date | null = null;
      let tempEndDate: Date | null = null;

      // Convert rPromoStartDt (string) to Date
      if (this.rPromoStartDt) {
        tempStartDate = new Date(this.rPromoStartDt);
        tempStartDate.setHours(0, 0, 0, 0);
      }

      // Convert rPromoEndDt (string) to Date
      if (this.rPromoEndDt) {
        tempEndDate = new Date(this.rPromoEndDt);
        tempEndDate.setHours(0, 0, 0, 0);
      }

      // If end date is invalid (earlier than or equal to start date), adjust it
      if (tempStartDate && tempEndDate && tempEndDate <= tempStartDate) {
        this.rPromoEndDt = null;
      }
    }
  }


}
