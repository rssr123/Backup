import { Component, OnInit } from '@angular/core';
import { FeeGroup } from '../../core/models/fee-group';
import { MFTWFDoc, SourceSystemCode, User } from '../../core/models/entity';
import { ActivatedRoute } from '@angular/router';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { environment } from 'src/environments/environment';
import { MFT, MFTWF, MFTWFHist } from '../../core/models/entity';
import { EMPTY, Observable, map } from 'rxjs';
import { forkJoin, of } from 'rxjs';
import { DataService } from '../../core/services/data.service';
import { Systemstatus } from '../../shared/enums/systemstatus';
import { NgForm } from '@angular/forms';
import { Router } from '@angular/router';
import { fadeInOut } from '../../shared/animation';
import { TranslateService } from '@ngx-translate/core';
import { GlobalService } from 'src/app/shared/global.service';
import { AuthService } from 'src/app/core/services/auth.service';
import { perm } from 'src/permissions/perm';

@Component({
  selector: 'app-mft-fa-fhod-appr-add',
  templateUrl: './mft-fa-fhod-appr-add.component.html',
  styleUrls: ['./mft-fa-fhod-appr-add.component.scss'],
  animations: [fadeInOut]
})
export class MftFaFhodApprAddComponent implements OnInit {

  username = this.authService.username;
  roles = this.authService.roles;


  isDisplaySupDoc: boolean = false;
  isLoadingSupDoc: boolean = false;
  isDisplayHist: boolean = false;
  isLoadingHist: boolean = false;

  assignTo: string | null = null;
  financeAdmin: string | null = null;
  mftwfs: MFTWF[] = [];
  emailMftwfs: MFTWF[] = [];
  mftwfSupDocs: MFTWFDoc[] = [];
  page = environment.DefaultPage;
  dropDownSize = environment.DropDownSize;
  decision: string = "";
  decisionOption: any[] = [];
  actionType: string | null = null;
  disableApprover: boolean = true;
  mftwfHis: MFTWFHist[] = [];
  users: User[] = [];
  mftwfHisAsts: MFTWFHist[] = [];
  errorMessages: string[] = [];
  error: boolean = false;
  isLoading: boolean = false;
  editMode: boolean = false;
  totalRecords: number = 0;
  totalMFTRecords: number = 0;
  sourceSystemCodeOptions: SourceSystemCode[] = [];
  mfts: MFT[] = [];
  checkboxSsCd: string[] | undefined = undefined;
  ssCdWithSpace: string | null = null;
  downloadUrl: string = '';
  file_content = "";
  statusFromAssigned: string | null = null;
  currentMFTWFDetail: MFTWF[] = [];
  currentMFTWFStatus: string | null = null;
  currentMFTWFAssignTo: string | null = null;
  rolesContainFAHOD: boolean = false;
  errorMessagesTaskNotUpdate: string[] = [];
  errorTaskNotUpdate: boolean = false;
  isPublic: number| null = null;

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

  //display
  wfId: number | null = null;
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
  statusEn: string | null = null;
  effectiveDate: Date | null = null;
  textRemarks: string | null = null;
  ssm4uuserrefno: string | null = null;
  mftStatus: string | null = null;
  taskId: string | null = null;
  myTaskAssignTo: string | null = null;
  alertMessage: string | undefined = undefined;

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
  permApproveAddAndEditMFTFHOD = perm.Master_Fee_Table_Approve_Add_MFT_FHOD + "," + perm.Master_Fee_Table_Approve_Edit_MFT_FHOD; // all the perm_cd for this module seperated with comma
  permApproveAddAndEditMFTFHODAllow = ""; // variable to store allowed permission for the user
  permApproveAdd: number = 0;
  permApproveEdit: number = 0;
  permListAllow: number = 0; // if 0 then not allow to view listing page, else allow
  // end configuration

  constructor(
    private route: ActivatedRoute,
    private http: HttpClient, private router: Router,
    private translate: TranslateService,
    private globalService: GlobalService,
    private authService: AuthService) {
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

    }
    console.log("Fee detail pk is " + this.feeDetailPk)
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


    //this.wfId = history.state.wf_id;
    //  this.taskId = history.state.task_id;
    // this.feeDetailPk = history.state.fee_detail_pk;
    //  this.editMode = history.state.edit_Mode;

    if (this.wfId !== undefined) {
      this.isLoading = true;
      this.decisionOption = [
        { label: 'Query to Finance Admin', value: 'Q-FA' }, //Query Pending Finance Admin
        { label: 'Approve', value: 'APV' }, //Approved
        { label: 'Reject', value: 'RJ-FHOD' } //Rejected by Finance HOD
      ]
      this.checkPermission();
      await this.checkStatusAndAssignTo(false);
      this.loadDataSupDoc();
      this.loadDataHist();
      await this.populateDisplayRequestedValueForm(); // await because need load value for createdBy

      if (this.editMode === false) {
        this.actionType = "Add";
      }
      else {
        this.actionType = "Edit";
        await this.populateCurrentValueForm();
      }
      console.log('Edit mode is ' + this.editMode)
      const tempfinanceAdmin = await this.getworkflowhistory_ast('Q-FA');
      if (tempfinanceAdmin.assign_to !== null && tempfinanceAdmin.assign_to_nm !== null) { //means finance admin has been reassigned by super admin
        this.financeAdmin = tempfinanceAdmin.assign_to;
      }
      else { //means finance admin has not been reassigned by super admin
        this.financeAdmin = this.createdBy;
      }
      //  console.log("FA is "+this.financeAdmin)
      this.isLoading = false;
    }
  }

  async submit() {

    this.isLoading = true;
    this.defaultSetting();

    if (this.decision === 'Q-FA') //Query Pending Finance Admin
    {
      this.assignTo = this.financeAdmin;
      this.emailAssignTo = this.financeAdmin;
    }
    else if (this.decision === 'APV' || this.decision === 'RJ-FHOD') {//need change later
      this.assignTo = null;
      this.emailAssignTo = this.createdBy;
    }
    else {
      this.assignTo = null;
      this.emailAssignTo = this.financeAdmin;
    }

    const invalidUpdate = await this.updateMasterFeeTableWorkFlowStatus()
    if (invalidUpdate === false) {
      this.sendEmail();
      this.isLoading = false;
      const alert_msg = this.alertMessage;
      this.router.navigate(['/my-task-assigned-tasks'], { state: { alert_msg } });
    }

    this.isLoading = false;
  }

  async updateMasterFeeTableWorkFlowStatus(): Promise<boolean> {

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

  async populateDisplayRequestedValueForm(): Promise<void> {

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
        this.isPublic = this.mftwfs[0].is_pub;
        // console.log('this sscd is '+this.ssCd)
        if (this.ssCd !== null) {
          // this.checkboxSsCd = this.ssCd.split(','); //for checkbox
          this.ssCdWithSpace = this.ssCd.split(',').join(', '); //for display
        }

      } else {
        this.totalRecords = 0
        console.error('Invalid master fee table work flow  response format:', response);
      }
    } catch (error) {
      console.error('There was an error retrieving the master fee table work flow :', error);
    }
  }

  onDecisionChange() {

    if (this.decision === 'APV') {
      this.alertMessage = 'approved';
      //  this.emailDecisionDescription = "Approved";
    }
    else if (this.decision === 'Q-FA') {
      this.alertMessage = 'submitted';
      //  this.emailDecisionDescription = "Query Pending Finance Admin";
    }
    else if (this.decision === 'RJ-FHOD') {
      this.alertMessage = 'rejected';
      //  this.emailDecisionDescription = "Rejected by Finance HOD";
    }
    else {
      this.alertMessage = undefined;
    }
  }


  //form handle before submit start
  async handleFormSubmit(form: NgForm) {
    if (form.valid) {
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
    // this.dataService.setShowUpdateAlert(false)
    this.router.navigate(['/my-task-assigned-tasks']);
  }

  populateSourceSystemCode() {
    const url = environment.apiUrl + '/api/rms/v1/getsourcesystem';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json'
    });


    // Create the request body with your form data
    const requestBody = {
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


    // Send an HTTP POST request to the API
    this.http.post(url, requestBody, { headers }).subscribe(
      (response: any) => {
        if (response.data.length === 0) {
          console.error('Invalid source system code response format:', response);
        }
        else {
          this.sourceSystemCodeOptions = response.data;
          // this.sourceSystemCodeOptions=this.sourceSystemCodeOptions.concat(response.data)
          // Handle a successful response (e.g., show a success message)
        }
      },
      (error) => {
        console.error('There was an error retrieving the source system code:', error);
        // Handle API errors (e.g., show an error message)
      }
    );

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
      //this.error = true;
      // this.errorMessages.push('Internal Server Error.');
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
          //  this.totalRecordsSupDoc = 0;
          this.isLoadingSupDoc = false;
          console.error('Invalid master fee table work flow document response format:', response);
        } else {
          //    this.totalRecordsSupDoc = response.data[0].total;
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
          // this.currentCheckboxSsCd = this.currentSsCd.split(',');
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
    }

    console.log("emailAssignTo is " + this.emailAssignTo);
    await this.getworkflowDetailAfterInsert();
    console.log("emailWfId is " + this.emailWfId);
    console.log("emailfeeDetailId is " + this.emailfeeDetailId);
    console.log("emailTaskId is " + this.emailTaskId);
    const Body: any = {

      i_wf_id: this.emailWfId,
      i_task_id: this.emailTaskId,
      i_fee_detail_pk: this.emailFeeDetailPk,
      i_fee_detail_id: this.emailfeeDetailId,
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

  checkPermission() {
    this.authService.checkUserRole(this.authService.username, this.permApproveAddAndEditMFTFHOD)
      .subscribe(
        (response: any) => {
          this.permApproveAddAndEditMFTFHODAllow = response.data;
          console.log("this.permApproveAddAndEditMFTFHODAllow " + this.permApproveAddAndEditMFTFHODAllow);
          this.permApproveAdd = this.permApproveAddAndEditMFTFHODAllow.includes(perm.Master_Fee_Table_Approve_Add_MFT_FHOD) ? 1 : 0;
          this.permApproveEdit = this.permApproveAddAndEditMFTFHODAllow.includes(perm.Master_Fee_Table_Approve_Edit_MFT_FHOD) ? 1 : 0;
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
            // const show_requester_table = false;
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

  // this.http.post(urlMftWF, Body, { headers }).subscribe(
  //   (response: any) => {
  //     this.currentMFTWFDetail = response.data;
  //     this.currentMFTWFStatus = this.currentMFTWFDetail[0].status;
  //     this.currentMFTWFAssignTo = this.currentMFTWFDetail[0].assign_to;

  //     if (this.currentMFTWFAssignTo !== this.username || this.currentMFTWFStatus !== this.statusFromAssigned) {
  //       const alert_msg_access_denied = "This task is assigned or reassigned to another user."
  //       this.router.navigate(['/master-fee-table'], { state: { alert_msg_access_denied } });
  //       return; // Exit t he function to prevent further execution
  //     }
  //   }
  // );

}
