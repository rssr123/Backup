import { Component, OnInit, ViewChild, ElementRef, ChangeDetectorRef, ViewEncapsulation, AfterViewInit } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { environment } from 'src/environments/environment';
import { FeeGroup } from '../../core/models/fee-group';
import { DatePipe, DecimalPipe } from '@angular/common';
import { MFTWF, MFTWFDoc, MFTWFHist, Param, SourceSystemCode, TaxCode, User } from '../../core/models/entity';
import { FormBuilder, FormControl, FormGroup, NgForm, NgModel, Validators } from '@angular/forms';
import { Systemstatus } from '../../shared/enums/systemstatus';
import { ActivatedRoute, Router } from '@angular/router';
import { fadeInOut } from '../../shared/animation';
import { TranslateService } from '@ngx-translate/core';
import { GlobalService } from 'src/app/shared/global.service';
import { AuthService } from 'src/app/core/services/auth.service';
import { perm } from 'src/permissions/perm';

@Component({
  selector: 'app-mft-reqhod-appr-edit',
  templateUrl: './mft-reqhod-appr-edit.component.html',
  styleUrls: ['./mft-reqhod-appr-edit.component.scss'],
  animations: [fadeInOut]
})
export class MftReqhodApprEditComponent implements OnInit {

  username = this.authService.username;
  roles = this.authService.roles;
  userHigherOfficialRole = "FINANCEADMIN";

  isLoading: boolean = false;
  initialNumber = [
    { value: 0, label: 'No' },
    { value: 1, label: 'Yes' },
  ]
  selectedFiles: File[] = [];
  errorMessages: string[] = [];
  error: boolean = false;
  page = environment.DefaultPage;
  itemsPerPage = environment.ItemPerPage;
  dropDownSize = environment.DropDownSize;
  totalRecords: number = 0;
  statusFromAssigned: string | null = null;
  status: Param[] = [];
  decisionOption: any[] = [];
  alertMessage: string | undefined = undefined;
  checkboxOptions: string[] | undefined = undefined;
  rSsCdWithSpace: string | null = null;
  disableApprover: boolean = true;
  decision: string | null = null;
  mftwfs: MFTWF[] = [];
  mftwfSupDocs: MFTWFDoc[] = [];
  mftwfHis: MFTWFHist[] = [];
  emailMftwfs: MFTWF[] = [];
  queryFromStatus: string | null = null;
  queryBackTo: string | null = null;
  assignTo: string | null = null;
  actionType: string | null = null;
  users: User[] = [];
  taxCode: TaxCode[] = [];
  decisionDetails: boolean = false;
  wfId: number | null = null;
  taskId: string | null = null;
  requester: string | null = null;
  file_content = "";
  currentMFTWFDetail: MFTWF[] = [];
  currentMFTWFStatus: string | null = null;
  currentMFTWFAssignTo: string | null = null;
  errorMessagesTaskNotUpdate: string[] = [];
  errorTaskNotUpdate: boolean = false;
  isPublic: number| null = null;

  //display
  rFeeDetNm: string | null = null;
  rFeeAmt: number | null = null;
  rScCd: string | null = null;
  rPromoStartDt: Date | null = null;
  rPromoEndDt: Date | null = null;
  rPromoFee: number | null = null;
  rLlRequired: number = 0;
  rAddNotes: string | null = null;
  createdBy: string | null = null;
  sourceSystemCode: string | null = null;
  effectiveDate: Date | null = null;
  textRemarks: string | null = null;
  ssm4uuserrefno: string | null = null;

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
  permEditMFTRequesterFormRHOD = perm.Master_Fee_Table_Approve_Edit_MFT_Requester_Form_RHOD; // all the perm_cd for this module seperated with comma
  permEditMFTRequesterFormRHODAllow = ""; // variable to store allowed permission for the user
  permListAllow: number = 0; // if 0 then not allow to view listing page, else allow
  // end configuration

  constructor(
    private http: HttpClient,
    private router: Router,
    private route: ActivatedRoute,
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
      this.taskId = tempTask_id; //+ mean convert string to number
    }
    else {
      this.taskId = history.state.task_id;
    }

    const tempStatus__From_Assigned = queryParams.get('status__From_Assigned');
    if (tempStatus__From_Assigned !== null && tempStatus__From_Assigned !== 'null') {
      this.statusFromAssigned = tempStatus__From_Assigned;
      //  console.log("status__From_Assigned from queryparam" + this.statusFromAssigned)
    }
    else {
      this.statusFromAssigned = history.state.status__From_Assigned;
      //  console.log("status__From_Assigned from state" + this.statusFromAssigned)
    }

    // this.wfId = history.state.wf_id;
    // this.taskId = history.state.task_id;
    // this.statusFromAssigned = history.state.status__From_Assigned;

    if (this.wfId !== undefined) {

      this.decisionOption = [
        { label: 'Query to Requester', value: 'Q-R' },//Query Pending Requester
        { label: 'Approve', value: 'P-FA' }, //Pending Finance Admin Approval
        { label: 'Reject', value: 'RJ-RHOD' } //Rejected by Requester HOD
      ]

      this.isLoading = true;
      this.checkPermission();
      await this.checkStatusAndAssignTo(false);
      this.populateAppover();
      this.loadDataSupDoc();
      this.loadDataHist();
      await this.populateForm(); //await for load value createdby and check permission here

      if (this.statusFromAssigned === "Q-RHOD") {
        this.decisionDetails = false;
        this.actionType = "Edit-Reply";

        const tempQuery = await this.getworkflowhistory_status("Q-RHOD");
        this.queryBackTo = tempQuery.assign_to;
        this.queryFromStatus = tempQuery.status;
      }
      else {   // this.statusFromAssigned === "P-RHOD"
        this.decisionDetails = true;
        this.actionType = "Edit";
      }

      // await this.populateForm(); //await for load value createdby
      // this.populateAppover();
      //this.loadDataSupDoc();
      // this.loadDataHist();

      //find most recent requester where either created the form or assign to
      const tempRequester = await this.getworkflowhistory_ast('Q-R');
      if (tempRequester.assign_to !== null && tempRequester.assign_to_nm !== null) { //means requester has been reassigned by super admin
        this.requester = tempRequester.assign_to;
      }
      else { //means requester has not been reassigned by super admin
        this.requester = this.createdBy;
      }

      this.isLoading = false;
    }
  }

  async submit() {

    this.isLoading = true;
    this.defaultSetting();

    if (this.statusFromAssigned === "Q-RHOD") {
      this.assignTo = this.queryBackTo;
      this.decision = this.queryFromStatus;
      this.emailAssignTo = this.queryBackTo;
      this.alertMessage = "submittedForApproval";
    }
    else {   //this.statusFromAssigned === "P-RHOD"
      if (this.decision === 'Q-R') {
        this.assignTo = this.requester;
        this.emailAssignTo = this.requester;
      }
      else if (this.decision === 'P-FA') {
        this.assignTo = this.ssm4uuserrefno;
        this.emailAssignTo = this.ssm4uuserrefno;
      }
      else if (this.decision === 'RJ-RHOD') {
        this.assignTo = null;
        this.emailAssignTo = this.createdBy;
      }
      else {
        this.assignTo = null;
        this.emailAssignTo = this.requester
      }
    }

    console.log("Assign to is " + this.assignTo);
    const invalidUpdate = await this.updateMasterFeeTableWorkFlowStatus();
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
          console.error('Invalid approver response format:', response);
        }
        else {
          this.users = response.data.filter((user: User) => user.ssm4uuserrefno !== this.username);
        }
      },
      (error) => {
        console.error('There was an error retrieving the approver:', error);
        // Handle errors here
      }
    );
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
    this.router.navigate(['/my-task-assigned-tasks']);
  }

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


  onKeyDown(event: KeyboardEvent): void {
    // if (event.key === 'Backspace') {
    //       return;
    //}
    // Prevent manual key entry
    event.preventDefault();
  }

  onDecisionChange() {

    if (this.decision === 'P-FA') {
      this.alertMessage = 'approved';
      this.disableApprover = false;
    }
    else if (this.decision === 'Q-R') {
      this.alertMessage = 'submitted';
      this.disableApprover = true;
    }
    else if (this.decision === 'RJ-RHOD') {
      this.alertMessage = 'rejected';
      this.disableApprover = true;
    }
    else {
      this.alertMessage = undefined;
      this.disableApprover = true;
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
      // console.log("Ast is "+ response.header.statusCode)
      if (response.header.statusCode === '00') {
        this.totalRecords = response.data[0].total;
        this.mftwfs = response.data;
        this.rFeeDetNm = this.mftwfs[0].r_fee_det_nm;
        this.rFeeAmt = this.mftwfs[0].r_fee_amt;
        this.rScCd = this.mftwfs[0].r_ss_cd;
        this.rPromoStartDt = this.mftwfs[0].r_promo_startdt;
        this.rPromoEndDt = this.mftwfs[0].r_promo_enddt;
        this.rPromoFee = this.mftwfs[0].r_promo_fee;
        this.rLlRequired = this.mftwfs[0].r_ll_required;
        this.rAddNotes = this.mftwfs[0].r_add_notes;
        this.effectiveDate = this.mftwfs[0].effective_date;
        this.createdBy = this.mftwfs[0].created_by; //use for query to requester
        this.isPublic = this.mftwfs[0].is_pub;

        if (this.rScCd !== null) {
          //this.checkboxOptions = this.rScCd.split(','); //for checkbox
          this.rSsCdWithSpace = this.rScCd.split(',').join(', '); //for display
        }
        //this.checkboxOptions = this.rScCd?.split(',');

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
        // this.errorMessages.push('Data not found');
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
          console.error('Invalid master fee table work flow document response format:', response);
        } else {
          console.log('Successful download document');
        }
      },
      (error) => {
        console.error('There was an error downloading the master fee table work flow document:', error);;
        this.isLoading = false;
      }
    );
  }

  downloadFileContent(fileName: string, fileContent: string): void {
    // event.preventDefault(); // Prevent the default behavior of the anchor element

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
        // this.error = true;
        // this.errorMessages.push('Data not found');
        console.error('Invalid master fee table work flow history assign to by status response format:', response);
        return { assign_to: null, assign_to_nm: null, status: null }; // Data not found
      }
    } catch (error) {
      // this.error = true;
      //this.errorMessages.push('Internal Server Error.');
      console.error('There was an error retrieving the master fee table work flow history assign to by status:', error);
      return { assign_to: null, assign_to_nm: null, status: null }; // Error occurred
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
    let extractedValue = "";

    if(this.emailfeeDetailId !== undefined && this.emailfeeDetailId !== null){
      extractedValue =this.emailfeeDetailId
    }
    else{
      if(this.emailrfeeDetNm !== undefined && this.emailrfeeDetNm !== null){
        extractedValue = this.emailrfeeDetNm.split(" : ")[0];
     }
    }
    
   

    const Body: any = {

      i_wf_id: this.emailWfId,
      i_task_id: this.emailTaskId,
      i_fee_detail_pk: this.emailFeeDetailPk,
      // i_fee_detail_id: this.emailfeeDetailId,
      i_fee_detail_id: extractedValue,
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
    this.authService.checkUserRole(this.authService.username, this.permEditMFTRequesterFormRHOD)
      .subscribe(
        (response: any) => {
          this.permEditMFTRequesterFormRHODAllow = response.data;
          console.log("this.permEditMFTRequesterFormRHODAllow " + this.permEditMFTRequesterFormRHODAllow);
          this.permListAllow = this.permEditMFTRequesterFormRHODAllow.includes(perm.Master_Fee_Table_Approve_Edit_MFT_Requester_Form_RHOD) ? 1 : 0;
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
            // this.router.navigate(['/access-denied']);
            const showTaskNotUpdateAlert = true;
            this.router.navigate(['/my-task-assigned-tasks'], { state: { showTaskNotUpdateAlert } });
            return false;
          }
        }
        else {
          if (this.currentMFTWFStatus !== this.statusFromAssigned) {
            // this.router.navigate(['/access-denied']);
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
}
