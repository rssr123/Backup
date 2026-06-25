import { Component, OnInit, ViewChild, ElementRef, ChangeDetectorRef, ViewEncapsulation, AfterViewInit } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { environment } from 'src/environments/environment';
import { FeeGroup } from '../../core/models/fee-group';
import { DatePipe, DecimalPipe } from '@angular/common';
import { MFT, MFTWF, MFTWFDoc, MFTWFHist, Param, SourceSystemCode, TaxCode, User } from '../../core/models/entity';
import { forkJoin, of, Subject } from 'rxjs';
import { concatMap, debounceTime, delay, distinctUntilChanged, map, startWith, switchMap, tap } from 'rxjs/operators';
import { FormBuilder, FormControl, FormGroup, NgForm, NgModel, Validators } from '@angular/forms';
import { Systemstatus } from '../../shared/enums/systemstatus';
import { ActivatedRoute, Router } from '@angular/router';
import { fadeInOut } from '../../shared/animation';
import { TranslateService } from '@ngx-translate/core';
import { GlobalService } from 'src/app/shared/global.service';
import { perm } from 'src/permissions/perm';
import { AuthService } from 'src/app/core/services/auth.service';

@Component({
  selector: 'app-mft-fa-fa-rqt-add',
  templateUrl: './mft-fa-fa-rqt-add.component.html',
  styleUrls: ['./mft-fa-fa-rqt-add.component.scss'],
  animations: [fadeInOut],

})
export class MftFaFaRqtAddComponent implements OnInit {

  username = this.authService.username;
  roles = this.authService.roles;


  userHigherOfficialRole: string | null = null;
  financeHOD: string | null = null;
  feeGroups: FeeGroup[] = [];
  errorMessages: string[] = [];
  error: boolean = false;
  isLoading: boolean = false;
  page = environment.DefaultPage;
  itemsPerPage = environment.ItemPerPage;
  dropDownSize = environment.DropDownSize;
  totalRecords: number = 0;
  status: Param[] = [];
  sourceSystemCodes: SourceSystemCode[] = [];
  selectedSourceSystemCodes: any[] = [];
  initialNumber: any[] = [
    { value: 0, label: 'No' },
    { value: 1, label: 'Yes' },
  ]
  mftStatusOptions: any[] = [
    { value: 'A', label: 'Active' },
    { value: 'D', label: 'Inactive' },
  ];
  inputDetails: boolean = true;
  wfId: number | null = null;
  taskId: string | null = null;
  //assignTo: string | null = null;
  actionType: string | null = null;
  users: User[] = [];
  taxCode: TaxCode[] = [];
  mftwfHis: MFTWFHist[] = [];
  mftwfs: MFTWF[] = [];
  emailMftwfs: MFTWF[] = [];
  mftsForLlPID: MFT[] = [];
  mftwfSupDocs: MFTWFDoc[] = [];
  checkboxSsCd: string[] | undefined = undefined;
  file_content = "";
  statusFromAssigned: string | null = null;
  currentMFTWFDetail: MFTWF[] = [];
  currentMFTWFStatus: string | null = null;
  currentMFTWFAssignTo: string | null = null;
  rolesContainFA: boolean = false;
  errorMessagesTaskNotUpdate: string[] = [];
  errorTaskNotUpdate: boolean = false;
  errorFile: boolean = false;
  errorFileMessages: string[] = [];
  errorFileSizeLimit: boolean = false;
  errorFileSizeLimitMessages: string[] = [];
  errorFileDuplicate: boolean = false;
  errorFileDuplicateMessages: string[] = [];
  isPublic: number | null = null;


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

  //files
  selectedFiles: File[] = [];
  selectedFilesSize: number = 0;
  isDisplayFileRequired: boolean = false;
  wfIdFromInsert: number | null = null;
  i_file_content: any;

  //email
  emailWfId: string | number | null = null;
  emailAssignTo: string | null = null;
  emailFeeDetailPk: string | number | null = null;
  emailfeeDetailId: string | null = null;
  emailTaskId: string | null = null;
  emailAction: string | null = null;

  //to insert into master fee table workflow
  previousInsertFeeDetailId: string | null = null;
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
  statusEn: string | null = null;
  effectiveDate: string | null = null;
  textRemarks: string | null = null;
  ssm4uuserrefno: string | null = null;
  mftStatus: string | null = null;
  minEffDate: Date = new Date();
  minPromoStartDate: Date = new Date();
  minPromoEndDate: Date = new Date();

  isDisplaySupDoc: boolean = false;
  isLoadingSupDoc: boolean = false;
  isDisplayHist: boolean = false;
  isLoadingHist: boolean = false;

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

    //for parentid 
    llParentSearch$ = new Subject<string>();
    loadingLlParent = false;
    llParentPage = 1;
    llParentPageSize = 20;
    currentLlParentTerm = '';
    dropDownSize100 = 100;

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
  permRequestAddMFTFA = perm.Master_Fee_Table_Request_Add_MFT_FA; // all the perm_cd for this module seperated with comma
  permRequestAddMFTFAAllow = ""; // variable to store allowed permission for the user
  permListAllow: number = 0; // if 0 then not allow to view listing page, else allow
  // end configuration


  constructor(
    private http: HttpClient,
    private datePipe: DatePipe,
    private router: Router,
    private cdref: ChangeDetectorRef,
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
      console.log("Wfid from queryparam" + this.wfId)
    }
    else {
      this.wfId = history.state.wf_id;
      console.log("Wfid from state" + this.wfId)
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


    //this.wfId = history.state.wf_id;
    //this.taskId = history.state.task_id;
    this.userHigherOfficialRole = "FINANCEHOD";

    this.minEffDate.setDate(this.minEffDate.getDate() + 1); // Set minDate to tomorrow
    this.minPromoStartDate.setDate(this.minPromoStartDate.getDate() + 1);
    this.minPromoEndDate.setDate(this.minPromoEndDate.getDate() + 2);

    //check if roles contain super admin

    this.currentDate.setHours(0, 0, 0, 0);

    this.isLoading = true;
    this.checkPermission();
    if (this.wfId !== undefined) {
      await this.checkStatusAndAssignTo(false);
    }
    await this.populateFeeGroupId(); //check permission here
    await this.populateTaxCode();
    await this.populateSourceSystemCode();
   // await this.populateLateLodgementID(); //load fee detail id from master fee table
    this.llParentSearch$  //for parentid
      .pipe(
        startWith(''), 
        debounceTime(300),
        distinctUntilChanged(),
        tap(term => {
          this.currentLlParentTerm = term ?? '';
          this.llParentPage = 1;
          this.mftsForLlPID = [];
          this.loadingLlParent = true;
        }),
        switchMap(term => this.loadLlParent(term))
          )
          .subscribe({
            next: data => {
              this.mftsForLlPID = data;
              this.loadingLlParent = false;
            },
            error: () => {
              this.loadingLlParent = false;
            }
          });

    if (this.wfId !== undefined) {
      this.inputDetails = false; //this page is navigated from query to finance admin
      this.actionType = "Add-Reply";
      this.loadDataSupDoc();
      this.loadDataHist();
      await this.populateForm();
      const tempQuery = await this.getworkflowhistory_status('Q-FA');
      if (tempQuery.status === "P-FHOD") {
        this.financeHOD = tempQuery.assign_to;
      }
      this.isLoading = false;
    }
    else { //this page is navigated from add button from master fee table

      this.inputDetails = true;
      this.actionType = "Add"
      this.populateAppover();
      // this.isLoading = false;
    }
  }

  //insert master workflow start
  async insertMFTWF() {
    this.isLoading = true;
    this.defaultSetting();

    let invalidUploadFile: boolean = false;
    const isValid = await this.validation();

    //false means no error, validation passed, can insert
    if (!isValid) {
      const invalidInsert = await this.insertMasterFeeTableWorkFlow();
      // const invalidInsert = false;
      if (this.selectedFiles.length !== 0) {
        invalidUploadFile = await this.readFileAsync(); //must put below insertMasterFeeTableWorkFlow();
      }

      if (invalidInsert === false && invalidUploadFile === false) {
        this.sendEmail();
        this.isLoading = false;
        const alert_msg = "submittedForApproval";
        this.router.navigate(['/master-fee-table'], { state: { alert_msg } });
      }
      else {
        this.isLoading = false;
      }
    }
    console.log("Validation failed at insert mftwf")
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

    //let formattedStartDate = this.datePipe.transform(this.promoStartDate, 'dd/MM/yyyy') || "";
    // let formattedEndDate = this.datePipe.transform(this.promoEndDate, 'dd/MM/yyyy') || "";
    // let formattedEffectiveDate = this.datePipe.transform(this.effectiveDate, 'dd/MM/yyyy') || "";

    let resultString = this.selectedSourceSystemCodes.join(',');

    console.log(resultString);
    const body: any = {
      i_fee_detail_pk: null
      // i_fee_detail_id: this.feeDetailId,
      //i_fee_grp_id: this.feeGroupId,
      //i_fee_detail_nm_e: this.feeDetailNmE,
      //i_fee_detail_nm_b: this.feeDetailNmB,
      //i_fee_amt: this.feeAmt,
    };

    if (this.feeDetailId && this.feeDetailId.trim()) {
      body.i_fee_detail_id = this.feeDetailId;
    }

    if (this.feeGroupId) {
      body.i_fee_grp_id = this.feeGroupId;
    }

    if (this.feeDetailNmE && this.feeDetailNmE.trim()) {
      body.i_fee_detail_nm_e = this.feeDetailNmE;
    }

    if (this.feeDetailNmB && this.feeDetailNmB.trim()) {
      body.i_fee_detail_nm_b = this.feeDetailNmB;
    }

    if (this.feeAmt && this.feeAmt.trim()) {
      body.i_fee_amt = this.feeAmt;
    }

    if (this.promoStartDate) {
      body.i_promo_startdt = this.promoStartDate;
    }

    if (this.promoEndDate) {
      body.i_promo_enddt = this.promoEndDate;
    }

    if (this.promoFee && this.promoFee.trim()) {
      body.i_promo_fee = this.promoFee;
    }

    body.i_tax_cd_id = this.taxCdId;
    body.i_allow_otc = this.allowOTC;

    if (this.llParentId && this.llParentId.trim()) {
      body.i_ll_parent_id = this.llParentId;
    }

    if (this.llStartDay && this.llStartDay.trim()) {
      body.i_ll_start_day = this.llStartDay;
    }

    if (this.llStartMth && this.llStartMth.trim()) {
      body.i_ll_start_mth = this.llStartMth;
    }

    if (this.llEndDay && this.llEndDay.trim()) {
      body.i_ll_end_day = this.llEndDay;
    }

    if (this.llEndMth && this.llEndMth.trim()) {
      body.i_ll_end_mth = this.llEndMth;
    }

    if (this.ledgerCd && this.ledgerCd.trim()) {
      body.i_ledger_cd = this.ledgerCd;
    }

    body.i_ss_cd = resultString;
    body.i_status = 'P-FHOD';

    if (this.effectiveDate) {
      body.i_effective_date = this.effectiveDate;
    }

    if (this.textRemarks) {
      body.i_remark = this.textRemarks;
    }

    body.i_assign_to = this.ssm4uuserrefno;
    body.i_action = "Request Add-FIN";
    body.i_r_fee_det_nm = null;
    body.i_r_fee_amt = null;
    body.i_r_ss_cd = null;
    body.i_r_promo_startdt = null;
    body.i_r_promo_enddt = null;
    body.i_r_ll_required = null;
    body.i_r_add_notes = null;
    body.i_r_add_notes = null;

    if (this.mftStatus && this.mftStatus.trim()) {
      body.i_mft_status = this.mftStatus;
    }

    body.i_ispub = this.isPublic;

    body.i_r_promo_fee = null;

    try {
      const response: any = await this.http.post(insertURL, body, { headers }).toPromise();
      if (response.header.statusCode === '00') {
        this.wfIdFromInsert = response.data;
        // console.log("this wfid = " +response.data)
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

  //validation start
  async validation(): Promise<boolean> {

    let invalidMTF = true;
    let invalidMTFWF = true;

    if (!this.feeDetailId) {
      // Form is not valid, you can handle this case or simply return
      return true;
    }

    invalidMTF = await this.checkDuplicateMFT();
    invalidMTFWF = await this.checkDuplicateMFTWF();

    if (invalidMTF === false && invalidMTFWF === false) {
      return false;
    }
    else {
      return true;
    }
  }

  async checkDuplicateMFT() {

    console.log("check duplicate mft start")

    const url = environment.apiUrl + '/api/mft/v1/checkmasterfeetableexist';

    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    const body: any = {
      i_page: '1',
      i_size: '1',
      i_fee_detail_id: this.feeDetailId,
      i_status: Systemstatus.Active
    };

    try {
      const response: any = await this.http
        .post(url, body, { headers })
        .toPromise();
      // console.log('bng' + response.header.statusCode)
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
      console.error("Check duplicate mft error :" + error);
      return true;
    }

  }

  async checkDuplicateMFTWF() {

    console.log("check duplicate mftwf start")

    const url = environment.apiUrl + '/api/mftwf/v1/checkmasterfeetableworkflowexist';

    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    const body: any = {
      i_page: '1',
      i_size: '1',
      i_fee_detail_id: this.feeDetailId
    };

    try {
      const response: any = await this.http
        .post(url, body, { headers })
        .toPromise();
      // console.log('bng' + response.header.statusCode)
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
      console.error("Check duplicate mftwf error :" + error);
      return true;
    }

  }
  //validation end
  //insert master workflow end

  async populateFeeGroupId(): Promise<void> {

    const url = environment.apiUrl + '/api/fg/v1/getfeegroup';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json'
    });

    const Body: any = {
      i_page: this.page,
      i_size: this.dropDownSize,
      i_fee_grp_id: null,
      i_fee_grp_nm_en: null,
      i_fee_grp_nm_bm: null,
      i_modified_by: null,
      i_dt_modified_fr: null,
      i_dt_modified_to: null,
      i_status: Systemstatus.Active

    };

    try {
      const response: any = await this.http.post(url, Body, { headers }).toPromise();
      if (response.header.statusCode === '00') {
        this.feeGroups = response.data;
      } else {
        console.error('Invalid fee group id response format:', response);
      }
    } catch (error) {
      console.error('There was an error retrieving the fee group id:', error);
    }

  }


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

      for (let i = 0; i < files.length; i++) {
        //currnetFilesTotalSize += files[i].size;
        console.log('This file size is ' + files[i].size)
        console.log('recent selected file size is ' + this.selectedFilesSize)
        // Check if the file with the same name already exists in selectedFiles

        const fileName = files[i].name;
        const fileExtension = fileName.split('.').pop().toLowerCase(); // Extract file extension

        // **Check if the file extension is allowed**
        if (!allowedExtensions.includes(fileExtension)) {
          this.errorFile = true;
          this.errorFileMessages.push(fileName);
          continue; // Skip this file and check the next one
        }

        currnetFilesTotalSize += files[i].size;




        const isDuplicate = this.selectedFiles.some((file) => file.name === files[i].name);
        console.log('Sum of file size is ' + currnetFilesTotalSize + this.selectedFilesSize)
        if (!isDuplicate && (currnetFilesTotalSize + this.selectedFilesSize) <= 5 * 1024 * 1024) {
          this.selectedFiles.push(files[i]);
          this.selectedFilesSize += files[i].size;
        } else if (isDuplicate) {
          //alert(`File "${files[i].name}" already selected. Please choose a different file.`);
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
          // this.users = response.data;
          this.users = response.data.filter((user: User) => user.ssm4uuserrefno !== this.username);
          this.isLoading = false;
        }
      },
      (error) => {
        console.error('There was an error retrieving the approver:', error);
        this.isLoading = false;
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
    // console.log('sizes'+this.selectedFiles)
  }

  populateStatus() {
    const url = environment.apiUrl + '/api/rms/v1/getparam';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json'
    });


    // Create the request body with your form data
    const requestBody = {
      page: this.page,
      size: this.dropDownSize,
      param_cd: Systemstatus.Active,
      param_grp_nm: 'Status-MFT'
    };

    // Send an HTTP POST request to the API
    this.http.post(url, requestBody, { headers }).subscribe(
      (response: any) => {
        if (response.data.length == 0) {
          console.error('Invalid status response format:', response);
        }
        else {
          this.status = response.data;
        }
        // Handle a successful response (e.g., show a success message)
      },
      (error) => {
        console.error('There was an error retrieving the status:', error);
        // Handle API errors (e.g., show an error message)
      }
    );

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
        this.sourceSystemCodes = response.data
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

  async populateTaxCode(): Promise<void> {

    const url = environment.apiUrl + '/api/tc/v1/gettaxcode';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json'
    });


    // Create the request body with your form data
    const Body = {

      i_page: this.page,
      i_size: this.dropDownSize,
      i_tax_cd_id: null,
      i_tax_cd: null,
      i_tax_cd_nm_en: null,
      i_tax_cd_nm_bm: null,
      i_modified_by: null,
      i_dt_modified_fr: null,
      i_dt_modified_to: null,
      i_status: Systemstatus.Active
    };

    try {
      const response: any = await this.http.post(url, Body, { headers }).toPromise();
      if (response.header.statusCode === '00') {
        this.taxCode = response.data
        // return false; // Insert success
      } else {
        // this.error = true;
        // this.errorMessages.push('Insert not successful');
        console.error('Invalid tax code response format:', response);
        // return true; // Insert failed
      }
    } catch (error) {
      // this.error = true;
      // this.errorMessages.push('Internal Server Error.');
      console.error('There was an error retrieving the tax code:', error);
      // return true; // Error occurred
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

      i_page: "1",
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
        this.previousInsertFeeDetailId = this.mftwfs[0].fee_detail_id === null ? null : this.mftwfs[0].fee_detail_id.toString();

        if (this.mftwfs[0].fee_grp_id === null || this.mftwfs[0].fee_grp_id === undefined) {
          this.feeGroupId = null;
        }
        else {
          if (this.feeGroups.some(feeGroup => feeGroup.fee_grp_id === this.mftwfs[0].fee_grp_id)) {
            this.feeGroupId = this.mftwfs[0].fee_grp_id;
          }
          else {
            // this.error = true;
            // this.errorMessages.push('Fee Group ' + this.mftwfs[0].fee_grp_nm_en + ' is inactive.');
            this.feeGroupId = null;
          }
        }

        this.feeDetailNmE = this.mftwfs[0].fee_detail_nm_e === null ? null : this.mftwfs[0].fee_detail_nm_e.toString();
        this.feeDetailNmB = this.mftwfs[0].fee_detail_nm_b === null ? null : this.mftwfs[0].fee_detail_nm_b.toString();
        this.feeAmt = this.mftwfs[0].fee_amt === null ? null : this.mftwfs[0].fee_amt.toFixed(2).toString();
        this.promoStartDate = this.mftwfs[0].promo_startdt === null ? null : this.mftwfs[0].promo_startdt.toString();
        this.promoEndDate = this.mftwfs[0].promo_enddt === null ? null : this.mftwfs[0].promo_enddt.toString();
        this.promoFee = this.mftwfs[0].promo_fee === null ? null : this.mftwfs[0].promo_fee.toFixed(2).toString();

        if (this.mftwfs[0].tax_cd_id === null || this.mftwfs[0].tax_cd_id === undefined) {
          this.taxCdId = null;
        }
        else {
          if (this.taxCode.some(taxCode => taxCode.tax_cd_id === this.mftwfs[0].tax_cd_id)) {
            this.taxCdId = this.mftwfs[0].tax_cd_id;
          }
          else {
            //  this.error = true;
            // this.errorMessages.push('Tax code ' + this.mftwfs[0].tax_cd + ' is inactive.');
            this.taxCdId = null;
          }
        }

        this.allowOTC = this.mftwfs[0].allow_otc === null ? null : this.mftwfs[0].allow_otc;

        // if (this.mftwfs[0].ll_parent_id === null || this.mftwfs[0].ll_parent_id === undefined) {
        //   this.llParentId = null;
        // }
        // else {
        //   if (this.mftsForLlPID.some(mftsForLlPID => mftsForLlPID.fee_detail_id.includes(this.mftwfs[0].ll_parent_id))) {
        //     this.llParentId = this.mftwfs[0].ll_parent_id.toString();
        //   }
        //   else {
        //     //  this.error = true;
        //     // this.errorMessages.push('Late Lodgement Parent ID ' + this.mftwfs[0].ll_parent_id.toString() + ' is inactive.');
        //     this.llParentId = null;
        //   }
        // }

            const llParentId = this.mftwfs[0].ll_parent_id;

        if (llParentId) {
          const selectedLlParent = await this.loadLlParentById(llParentId);

          if (selectedLlParent) {
            // Ensure dropdown contains it
            if (!this.mftsForLlPID.some(m => m.fee_detail_id === llParentId)) {
              this.mftsForLlPID = [selectedLlParent, ...this.mftsForLlPID];
            }

            // Select it
            this.llParentId = llParentId;
          } else {
            this.llParentId = null;
          }
        } else {
          this.llParentId = null;
        }

        this.llStartDay = this.mftwfs[0].ll_start_day === null ? null : this.mftwfs[0].ll_start_day.toString();
        this.llStartMth = this.mftwfs[0].ll_start_mth === null ? null : this.mftwfs[0].ll_start_mth.toString();
        this.llEndDay = this.mftwfs[0].ll_end_day === null ? null : this.mftwfs[0].ll_end_day.toString();
        this.llEndMth = this.mftwfs[0].ll_end_mth === null ? null : this.mftwfs[0].ll_end_mth.toString();
        this.ledgerCd = this.mftwfs[0].ledger_cd === null ? null : this.mftwfs[0].ledger_cd.toString();

        if (this.mftwfs[0].ss_cd === null || this.mftwfs[0].ss_cd === undefined) {
          this.selectedSourceSystemCodes = [];
        }
        else {

          let tempSelectedSourceSystemCodes = this.mftwfs[0].ss_cd.toString().split(',');
          let tempinActiveSsCd: string[] = [];
          this.selectedSourceSystemCodes = []; //when perform array binding to ngmodel, need to declare again

          for (let i = 0; i < tempSelectedSourceSystemCodes.length; i++) {
            if (this.sourceSystemCodes.some(sourceSystemCode => sourceSystemCode.ss_cd.includes(tempSelectedSourceSystemCodes[i]))) {
              this.selectedSourceSystemCodes.push(tempSelectedSourceSystemCodes[i]);
            }
            else {
              tempinActiveSsCd.push(tempSelectedSourceSystemCodes[i]);
            }
          }
          // if (tempinActiveSsCd.length > 0) {
          //  this.error = true;
          // this.errorMessages.push('Source system code ' + tempinActiveSsCd.join(', ') + ' is inactive.');
          // }
        }

        this.dtCreated = this.mftwfs[0].dt_created === null ? null : this.mftwfs[0].dt_created.toString();
        this.createdBy = this.mftwfs[0].created_by === null ? null : this.mftwfs[0].created_by.toString(); //not in use
        this.createdByNm = this.mftwfs[0].created_by_nm === null ? null : this.mftwfs[0].created_by_nm.toString();
        this.dtModified = this.mftwfs[0].dt_modified === null ? null : this.mftwfs[0].dt_modified.toString();
        this.modifiedBy = this.mftwfs[0].modified_by === null ? null : this.mftwfs[0].modified_by.toString(); //not in use
        this.modifiedByNm = this.mftwfs[0].modified_by_nm === null ? null : this.mftwfs[0].modified_by_nm.toString();
        this.mftStatus = this.mftwfs[0].mft_status === null ? null : this.mftwfs[0].mft_status.toString();
        this.isPublic = this.mftwfs[0].is_pub === null ? null : this.mftwfs[0].is_pub;
        this.effectiveDate = this.mftwfs[0].effective_date === null ? null : this.mftwfs[0].effective_date.toString();

      } else {
        this.totalRecords = 0;
        console.error('Invalid master work work flow response format:', response);
      }
    } catch (error) {
      console.error('There was an error retrieving the master fee table work flow:', error);
    }


    /* this.http.post(urlMftWF, Body, { headers }).subscribe(
       (response: any) => {
         console.log(response);
         // You can process the response data here
         if (response.data.length === 0) {
           console.error('Invalid master work work flow response format:', response);
         }
         else {
           //  this.totalRecords = response.data[0].total;
           this.mftwfs = response.data;
           this.displayFeeDetailId = this.mftwfs[0].fee_detail_id;
           this.displayFeeGroupNmEn = this.mftwfs[0].fee_grp_nm_en;
           this.displayFeeDetailNmE = this.mftwfs[0].fee_detail_nm_e;
           this.displayFeeDetailNmB = this.mftwfs[0].fee_detail_nm_b;
           this.displayFeeAmt = this.mftwfs[0].fee_amt;
           this.displayPromoStartDate = this.mftwfs[0].promo_startdt;
           this.displayPromoEndDate = this.mftwfs[0].promo_enddt;
           this.displayPromoFee = this.mftwfs[0].promo_fee;
           this.displayTaxCd = this.mftwfs[0].tax_cd;
           this.displayAllowOTC = this.mftwfs[0].allow_otc;
           this.displayLlParentId = this.mftwfs[0].ll_parent_id;
           this.displayLlStartDay = this.mftwfs[0].ll_start_day;
           this.displayLlStartMth = this.mftwfs[0].ll_start_mth;
           this.displayLlEndDay = this.mftwfs[0].ll_end_day;
           this.displayLlEndMth = this.mftwfs[0].ll_end_mth;
           this.displayLedgerCd = this.mftwfs[0].ledger_cd;
           this.displayEffectiveDate = this.mftwfs[0].effective_date;
           this.displaySsCd = this.mftwfs[0].ss_cd;
           this.displayDtCreated = this.mftwfs[0].dt_created;
           this.displayCreatedBy = this.mftwfs[0].created_by;
           this.displayCreatedByNm = this.mftwfs[0].created_by_nm;
           this.displayDtModified = this.mftwfs[0].dt_modified;
           this.displayModifiedBy = this.mftwfs[0].modified_by;
           this.displayModifiedByNm = this.mftwfs[0].modified_by_nm;
           this.displayStatusEn = this.mftwfs[0].status_en;
 
           if(this.displaySsCd !== null){
             this.checkboxSsCd = this.displaySsCd.split(',');
           }
         //  this.checkboxSsCd = this.displaySsCd.split(',');
 
 
         }
         //console.log(response.data);
         // console.log(this.totalRecords);
 
       },
       (error) => {
         console.error('There was an error retrieving the master work work flow:', error);
 
         // Handle errors here
       }
     );*/
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


  //form handle before submit start
  async handleFormSubmit(form: NgForm) {

    console.log("Submit button pressed")

    console.log("'startday is : " + this.llStartDay)

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
        console.log("file is invalid")
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

      if (this.wfId !== undefined) { //means come from assgined pages, query to FA
        const validProceedToSubmit = await this.checkStatusAndAssignTo(true); //ensure the mftwf status is updated before submit
        if (validProceedToSubmit === true) { //only when update require to checkStatusAndAssignTo
          this.updateMFTWF();
          console.log("update mftwf triggered")
        }
        else {
          console.log("Invalid proceed to submit")
        }
      }
      else {
        this.insertMFTWF();
        console.log("Insert mftwf triggered")
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
    // form.resetForm();
    if (this.wfId !== undefined) {
      this.router.navigate(['/my-task-assigned-tasks']);
    }
    else {
      this.router.navigate(['/master-fee-table']);
    }

  }

  //update master fee table workflow start
  async updateMFTWF() {

    this.isLoading = true;
    this.defaultSetting();

    let invalidUploadFile: boolean = false;
    let isValid: boolean = false;

    if (this.previousInsertFeeDetailId !== this.feeDetailId) {//if previous fee detail id is same as current fee detail id, means no change
      isValid = await this.validation();
    }

    //false means no error, validation passed, can insert
    if (!isValid) {

      const invalidUpdate = await this.updateMasterFeeTableWorkFlow()

      if (this.selectedFiles.length !== 0) {
        invalidUploadFile = await this.readFileAsync(); //must put below insertMasterFeeTableWorkFlow();
      }

      if (invalidUpdate === false && invalidUploadFile === false) {
        this.sendEmail();
        this.isLoading = false;
        const alert_msg = "submittedForApproval"
        this.router.navigate(['/my-task-assigned-tasks'], { state: { alert_msg } });
      }
      else {
        this.isLoading = false;
      }
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
    let formattedEffectiveDate = this.datePipe.transform(this.effectiveDate, 'yyyy-MM-dd') || "";
    let resultString = this.selectedSourceSystemCodes.join(',');

    //console.log(resultString);

    const Body: any = {
      i_wf_id: this.wfId,
      i_fee_detail_pk: null
    };

    if (this.feeDetailId && this.feeDetailId.trim()) {
      Body.i_fee_detail_id = this.feeDetailId;
    }

    if (this.feeGroupId) {
      Body.i_fee_grp_id = this.feeGroupId;
    }

    if (this.feeDetailNmE && this.feeDetailNmE.trim()) {
      Body.i_fee_detail_nm_e = this.feeDetailNmE;
    }

    if (this.feeDetailNmB && this.feeDetailNmB.trim()) {
      Body.i_fee_detail_nm_b = this.feeDetailNmB;
    }

    if (this.feeAmt && this.feeAmt.trim()) {
      Body.i_fee_amt = this.feeAmt;
    }

    if (formattedPromoStartDate) {
      Body.i_promo_startdt = formattedPromoStartDate;
    }

    if (formattedPromoEndDate) {
      Body.i_promo_enddt = formattedPromoEndDate;
    }

    if (this.promoFee && this.promoFee.trim()) {
      Body.i_promo_fee = this.promoFee;
    }

    if (this.taxCdId) {
      Body.i_tax_cd_id = this.taxCdId;
    }

    //if (this.allowOTC) {
    Body.i_allow_otc = this.allowOTC;
    // }

    if (this.llParentId && this.llParentId.trim()) {
      Body.i_ll_parent_id = this.llParentId;
    }

    if (this.llStartDay && this.llStartDay.trim()) {
      Body.i_ll_start_day = this.llStartDay;
    }

    if (this.llStartMth && this.llStartMth.trim()) {
      Body.i_ll_start_mth = this.llStartMth;
    }

    if (this.llEndDay && this.llEndDay.trim()) {
      Body.i_ll_end_day = this.llEndDay;
    }

    if (this.llEndMth && this.llEndMth.trim()) {
      Body.i_ll_end_mth = this.llEndMth;
    }

    if (this.ledgerCd && this.ledgerCd.trim()) {
      Body.i_ledger_cd = this.ledgerCd;
    }

    if (resultString && resultString.trim()) {
      Body.i_ss_cd = resultString;
    }

    if (formattedEffectiveDate) {
      Body.i_effective_date = formattedEffectiveDate;
    }

    Body.i_modified_by = this.username;
    Body.i_status = "P-FHOD";
    Body.i_assign_to = this.financeHOD;
    Body.i_remark = this.textRemarks;
    Body.i_action = "Request Add-FIN";
    Body.i_r_fee_det_nm = null;
    Body.i_r_fee_amt = null;
    Body.i_r_ss_cd = null;
    Body.i_r_promo_startdt = null;
    Body.i_r_promo_enddt = null;
    Body.i_r_ll_required = null;
    Body.i_r_add_notes = null;

    if (this.mftStatus && this.mftStatus.trim()) {
      Body.i_mft_status = this.mftStatus;
    }

    Body.i_ispub = this.isPublic;

    Body.i_r_promo_fee = null;


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
  //update master fee table workflow end

  //input checking start
  //fee amt checking start
  formatInput(feeAmtRef: any): void {
    let value = parseFloat(feeAmtRef.value);
    if (!isNaN(value) && value >= 0) {
      this.feeAmt = value.toFixed(2); // Convert number to string with 2 decimal places
    } else if (value < 0) {
      feeAmtRef.control.setErrors({ negative: true }); // Set a custom 'negative' error
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

  //promo fee checking start
  formatInputPromoFee(promoFeeRef: any): void {

    let value = parseFloat(promoFeeRef.value);
    if (!isNaN(value) && value >= 0) {
      this.promoFee = value.toFixed(2); // Convert number to string with 2 decimal places
    } else if (value < 0) {
      promoFeeRef.control.setErrors({ negative: true }); // Set a custom 'negative' error
    }
  }
  //promo fee  checking end

  onClosed(formControl: any) {
    formControl.control.markAsTouched();
  }

  // llStartDay checking start
  // formatInputllStartDay(llStartDayRef: any): void {
  //   let value = llStartDayRef.value;

  //   if (value === '') {
  //     // Allow empty value
  //     this.llStartDay = value;
  //   } else {
  //     let intValue = parseInt(value, 10);
  //     if (!isNaN(intValue) && intValue >= 0) {
  //       this.llStartDay = value; // Positive integer
  //     } else {
  //       llStartDayRef.control.setErrors({ pattern: true }); // Set a custom 'pattern' error
  //     }
  //   }
  // }

  formatInputllStartDay(llStartDayRef: any): void {
    
    let value = (llStartDayRef.value ?? '').toString().trim();

    if (value === '') {
      //  Allow empty or null — clear errors and model
      this.llStartDay = '';
      llStartDayRef.control.setErrors(null);
      return;
    }

    //  Check if it’s a valid positive integer
    const intValue = parseInt(value, 10);
    if (!isNaN(intValue) && /^\d+$/.test(value) && intValue >= 0) {
      this.llStartDay = value;
      llStartDayRef.control.setErrors(null);
    } else {
      //  Invalid: non-numeric, negative, or malformed
      llStartDayRef.control.setErrors({ pattern: true });
    }
  }

  validateLateLodgementInput(ref: any): void {
    let value = ref.value;

    if (value !== '' && !/^\d+$/.test(value)) {
      ref.control.setErrors({ pattern: true }); // Set a custom 'pattern' error for non-integer values
    }
  }
  // llStartDay checking end;


  // llStartMth checking start;
    formatInputllStartMth(llStartMthRef: any): void {
    
    let value = (llStartMthRef.value ?? '').toString().trim();

    if (value === '') {
      //  Allow empty or null — clear errors and model
      this.llStartMth = '';
      llStartMthRef.control.setErrors(null);
      return;
    }

    //  Check if it’s a valid positive integer
    const intValue = parseInt(value, 10);
    if (!isNaN(intValue) && /^\d+$/.test(value) && intValue >= 0) {
      this.llStartMth = value;
      llStartMthRef.control.setErrors(null);
    } else {
      //  Invalid: non-numeric, negative, or malformed
      llStartMthRef.control.setErrors({ pattern: true });
    }
  }
  // formatInputllStartMth(llStartMthRef: any): void {
  //   let value = llStartMthRef.value;

  //   if (value === '') {
  //     // Allow empty value
  //     this.llStartMth = value;
  //   } else {
  //     let intValue = parseInt(value, 10);
  //     if (!isNaN(intValue) && intValue >= 0) {
  //       this.llStartMth = value; // Positive integer
  //     } else {
  //       llStartMthRef.control.setErrors({ pattern: true }); // Set a custom 'pattern' error
  //     }
  //   }
  // }
  // llStartMthRef checking end;


  // llEndDay checking start;
  formatInputllEndDay(llEndDayRef: any): void {
    
    let value = (llEndDayRef.value ?? '').toString().trim();

    if (value === '') {
      //  Allow empty or null — clear errors and model
      this.llEndDay = '';
      llEndDayRef.control.setErrors(null);
      return;
    }

    //  Check if it’s a valid positive integer
    const intValue = parseInt(value, 10);
    if (!isNaN(intValue) && /^\d+$/.test(value) && intValue >= 0) {
      this.llEndDay = value;
      llEndDayRef.control.setErrors(null);
    } else {
      //  Invalid: non-numeric, negative, or malformed
      llEndDayRef.control.setErrors({ pattern: true });
    }
  }
  // formatInputllEndDay(llEndDayRef: any): void {
  //   let value = llEndDayRef.value;

  //   if (value === '') {
  //     // Allow empty value
  //     this.llEndDay = value;
  //   } else {
  //     let intValue = parseInt(value, 10);
  //     if (!isNaN(intValue) && intValue >= 0) {
  //       this.llEndDay = value; // Positive integer
  //     } else {
  //       llEndDayRef.control.setErrors({ pattern: true }); // Set a custom 'pattern' error
  //     }
  //   }
  // }
  // llEndDay checking end;


  // llEndMth checking start;
  formatInputllEndMth(llEndMthRef: any): void {
    
    let value = (llEndMthRef.value ?? '').toString().trim();

    if (value === '') {
      //  Allow empty or null — clear errors and model
      this.llEndMth = '';
      llEndMthRef.control.setErrors(null);
      return;
    }

    //  Check if it’s a valid positive integer
    const intValue = parseInt(value, 10);
    if (!isNaN(intValue) && /^\d+$/.test(value) && intValue >= 0) {
      this.llEndMth = value;
      llEndMthRef.control.setErrors(null);
    } else {
      //  Invalid: non-numeric, negative, or malformed
      llEndMthRef.control.setErrors({ pattern: true });
    }
  }
  // formatInputllEndMth(llEndMthRef: any): void {
  //   let value = llEndMthRef.value;

  //   if (value === '') {
  //     // Allow empty value
  //     this.llEndMth = value;
  //   } else {
  //     let intValue = parseInt(value, 10);
  //     if (!isNaN(intValue) && intValue >= 0) {
  //       this.llEndMth = value; // Positive integer
  //     } else {
  //       llEndMthRef.control.setErrors({ pattern: true }); // Set a custom 'pattern' error
  //     }
  //   }
  // }
  // llEndMth checking end;
  //input checking end

  //prevent manual key for date
  clearEndDate(event: KeyboardEvent) {
    if (event.key === 'Backspace' && this.promoEndDate) {
      this.promoEndDate = null; // Clears the date
      event.preventDefault(); // Prevent default backspace behavior
    } else {
      event.preventDefault(); // Disable all other keys
    }
  }

  clearStartDate(event: KeyboardEvent) {
    if (event.key === 'Backspace' && this.promoStartDate) {
      this.promoStartDate = null; // Clears the date
      event.preventDefault(); // Prevent default backspace behavior
    } else {
      event.preventDefault(); // Disable all other keys
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
        //this.error = true;
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
    if (this.promoStartDate === undefined) { //this is because after select date and when backspace to remove, this become undefined
      this.promoStartDate = null;
      this.cdref.detectChanges();
    }
    if (this.promoEndDate === undefined) { //this is because after select date and when backspace to remove, this become undefined
      this.promoEndDate = null;
      this.cdref.detectChanges();
    }

    if (this.promoStartDate !== null) {
      this.promoStartDateAsDate = new Date(this.promoStartDate);
      this.promoStartDateAsDate.setHours(0, 0, 0, 0);
    }
    else {
      this.promoStartDateAsDate = null;
    }

    if (this.promoEndDate !== null) {
      this.promoEndDateAsDate = new Date(this.promoEndDate);
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

  async populateLateLodgementID(): Promise<void> {

    const url = environment.apiUrl + '/api/mft/v1/getmasterfeetable';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    const Body: any = {
      i_page: this.page.toString(),
      i_size: this.dropDownSize.toString(),
      i_status: Systemstatus.Active
    };

    try {
      const response: any = await this.http.post(url, Body, { headers }).toPromise();
      if (response.header.statusCode === '00') {
        this.mftsForLlPID = response.data;
        // return false; // Insert success
      } else {
        // this.error = true;
        // this.errorMessages.push('Insert not successful');
        console.error('Invalid master fee table response format:', response);
        // return true; // Insert failed
      }
    } catch (error) {
      //this.error = true;
      // this.errorMessages.push('Internal Server Error.');
      console.error('There was an error retrieving the master fee table:', error);
      // return true; // Error occurred
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

    if (this.wfId !== undefined) { //query from finance hod
      this.emailWfId = this.wfId;
      this.emailAssignTo = this.financeHOD;
    }
    else { //create new work flow
      this.emailWfId = this.wfIdFromInsert;
      this.emailAssignTo = this.ssm4uuserrefno;
    }


    await this.getworkflowDetailAfterInsert();
    console.log("emailWfId is " + this.emailWfId);
    console.log("emailfeeDetailId is " + this.emailfeeDetailId);
    console.log("emailTaskId is " + this.emailTaskId);
    const Body: any = {

      i_wf_id: this.emailWfId,
      i_task_id: this.emailTaskId,
      i_fee_detail_id: this.emailfeeDetailId,
      i_status: "P-FHOD",
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
        this.emailAction = this.emailMftwfs[0].action;
        // this.isLoading = false;
      } else {
        //this.error = true;
        //this.errorMessages.push('Data not found');
        //  this.isLoading = false;
        console.error('Invalid master work work flow response format:', response);
        //  return ''; //  Data not found
      }
    } catch (error) {
      // this.error = true;
      // this.errorMessages.push('Internal Server Error.');
      // this.isLoading = false;
      console.error('There was an error retrieving the master fee table work flow:', error);
      //return ''; // Error occurred
    }
  }

  //email end

  checkPermission() {
    this.authService.checkUserRole(this.authService.username, this.permRequestAddMFTFA)
      .subscribe(
        (response: any) => {
          this.permRequestAddMFTFAAllow = response.data;
          console.log("this.permRequestAddMFTFAAllow " + this.permRequestAddMFTFAAllow);
          this.permListAllow = this.permRequestAddMFTFAAllow.includes(perm.Master_Fee_Table_Request_Add_MFT_FA) ? 1 : 0;
          if (this.permListAllow === 0) {
            this.router.navigate(['/access-denied']);
            return; // Exit t he function to prevent further execution

          }
        }
      );
  }

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
        //   const alert_msg_access_denied = "This task is assigned or reassigned to another user."
        //   this.router.navigate(['/master-fee-table'], { state: { alert_msg_access_denied } });
        //   return false; // Exit t he function to prevent further execution
        // }

        if (this.currentMFTWFAssignTo !== this.username && this.currentMFTWFAssignTo !== null) {
          // const alert_msg_access_denied = "This task is assigned or reassigned to another user."
          // this.router.navigate(['/master-fee-table'], { state: { alert_msg_access_denied } });
          const showTaskNotUpdateAlert = true;
          this.router.navigate(['/my-task-assigned-tasks'], { state: { showTaskNotUpdateAlert } });
          return false;
        }

        if (clicked === false) {
          if (this.currentMFTWFStatus === "C" || this.currentMFTWFStatus === "APV" || this.currentMFTWFStatus === "EFT"
            || this.currentMFTWFStatus === "RJ-RHOD" || this.currentMFTWFStatus === "RJ-FA" || this.currentMFTWFStatus === "RJ-FHOD") { //below parameter are different for each page
            // const alert_msg_task_canceled_closed = "This task has been canceled or closed and it is no longer active."
            // const wf_id = this.wfId;
            // const status__From_Assigned = this.currentMFTWFStatus;
            // const task_id = this.taskId;
            // const assign_to = this.currentMFTWFAssignTo;
            // const fee_detail_pk = null;
            // const edit_Mode = false;
            // const show_requester_table = false;
            // const from_view = false;
            // const navigate_not_refresh = true
            // this.router.navigate(['/task-details'], { state: { wf_id, status__From_Assigned, task_id, assign_to, fee_detail_pk, edit_Mode, show_requester_table, from_view, navigate_not_refresh } });
            const showTaskNotUpdateAlert = true;
            this.router.navigate(['/my-task-assigned-tasks'], { state: { showTaskNotUpdateAlert } });
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

  // Update minPromoEndDate dynamically
  // onStartDateChange(selectedStartDate: Date) {
  //   if (selectedStartDate) {
  //     const newMinEndDate = new Date(selectedStartDate);
  //     newMinEndDate.setDate(newMinEndDate.getDate() + 1); // Ensure it's at least 1 day later
  //     this.minPromoEndDate = newMinEndDate;
  //   }
  // }

  onStartDateChange(selectedStartDate: Date) {
    if (selectedStartDate) {
      // Ensure minPromoEndDate is at least 1 day after the selected start date
      const newMinEndDate = new Date(selectedStartDate);
      newMinEndDate.setDate(newMinEndDate.getDate() + 1);
      this.minPromoEndDate = newMinEndDate;

      let tempStartDate: Date | null = null;
      let tempEndDate: Date | null = null;

      // Convert rPromoStartDt (string) to Date
      if (this.promoStartDate) {
        tempStartDate = new Date(this.promoStartDate);
        tempStartDate.setHours(0, 0, 0, 0);
      }

      // Convert rPromoEndDt (string) to Date
      if (this.promoEndDate) {
        tempEndDate = new Date(this.promoEndDate);
        tempEndDate.setHours(0, 0, 0, 0);
      }

      // If end date is invalid (earlier than or equal to start date), adjust it
      if (tempStartDate && tempEndDate && tempEndDate <= tempStartDate) {
        this.promoEndDate = null;
      }
    }
  }


  //load parentld
  async loadLlParent(searchTerm: string = ''): Promise<any[]> {
  const url = environment.apiUrl + '/api/mft/v1/getmasterfeetable_typesearch';

  const headers = new HttpHeaders({
    Authorization: environment.authKey,
    'Content-Type': 'application/json',
  });

  const Body = {
    i_page: this.llParentPage,
    i_size: this.dropDownSize100,
    i_status: Systemstatus.Active,
    i_ss_cd: null,
    i_searchTerm: searchTerm
  };

  const response: any = await this.http.post(url, Body, { headers }).toPromise();

  if (response.header.statusCode === '00') {
    return response.data ?? [];
  }

  return [];
}

loadMoreLlParent(): void {
  if (this.loadingLlParent) return;

  this.llParentPage++;
  this.loadingLlParent = true;

  this.loadLlParent(this.currentLlParentTerm)
    .then(data => {
      this.mftsForLlPID = [...this.mftsForLlPID, ...data];
      this.loadingLlParent = false;
    })
    .catch(() => {
      this.loadingLlParent = false;
    });
}

//below is use to populate parentlld after user query back to requester using feeDetailId
async loadLlParentById(feeDetailId: string): Promise<any | null> {
  const url = environment.apiUrl + '/api/mft/v1/getmasterfeetable';

  const headers = new HttpHeaders({
    Authorization: environment.authKey,
    'Content-Type': 'application/json',
  });

  const Body = {
    i_page: 1,
    i_size: 1,
    i_fee_detail_id: feeDetailId,
    i_status: Systemstatus.Active
  };

  try {
    const response: any = await this.http.post(url, Body, { headers }).toPromise();
    if (response.header.statusCode === '00' && response.data?.length) {
      return response.data[0];
    }
  } catch (e) {
    console.error('loadLlParentById failed', e);
  }

  return null;
}

}
