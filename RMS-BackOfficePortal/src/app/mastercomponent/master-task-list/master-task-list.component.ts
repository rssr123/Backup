import { Component, OnInit, Output, EventEmitter } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { environment } from 'src/environments/environment';
import { NgbPaginationConfig } from '@ng-bootstrap/ng-bootstrap';
import { MFT, MFTWF, MasterTaskList, Param, SourceSystemCode, User } from '../../core/models/entity';
import { Router } from '@angular/router';
import { trigger, state, style, transition, animate } from '@angular/animations';
import { Systemstatus } from '../../shared/enums/systemstatus';
import moment from 'moment';
import { DataService } from '../../core/services/data.service';
import { fadeInOut } from '../../shared/animation';
import { formatDate } from '@angular/common';
import { TranslateService } from '@ngx-translate/core';
import { GlobalService } from 'src/app/shared/global.service';
import { AuthService } from 'src/app/core/services/auth.service';
import { perm } from 'src/permissions/perm';

@Component({
  selector: 'app-master-task-list',
  templateUrl: './master-task-list.component.html',
  styleUrls: ['./master-task-list.component.scss'],
  animations: [fadeInOut]
})
export class MasterTaskListComponent implements OnInit {


  isDisplay: boolean = false;
  isLoading: boolean = false;
  errorMessages: string[] = [];
  error: boolean = false;
  mftwfs: MFTWF[] = []
  statusOptions: Param[] = [];
  sourceSystemCodeOptions: SourceSystemCode[] = [];
  users: User[] = [];
  page = environment.DefaultPage;;
  itemsPerPage = environment.ItemPerPage;
  dropDownSize = environment.DropDownSize;
  totalRecords: number = 0;
  currentMFTWFStatus: string | null = null;
  forCheckingMFTWFS: MFTWF[] = [];
  

  taskId: string | null = null;
  feeDetailId: string | null = null;
  modifiedByNm: string | null = null;
  alertMessage: string | undefined = undefined;
  showTaskNotUpdateAlert: boolean | undefined = undefined;
  extractedNumber: number | null = null;
  //date range picker
  selectedEffectiveDate!: Date[];
  effectiveDateBsValue = new Date();
  effectiveDateTempDate !: Date;
  effectiveDateMinDate = new Date();
  effectiveDateMaxDate = new Date();


  selectedRequestedDate!: Date[];
  requestedDateBsValue = new Date();
  requestedDateTempDate !: Date;
  requestedDateMinDate = new Date();
  //date range picker

  status: string | null = null;
  nm: string | null = null;
  sourceSystemCode: String | null = null;
  wfId: number | null = null;

  cancelBox: boolean = false;
  reassignBox: boolean = false;
  


  //alert start
  showResultAlert = false;
  showResultAlertBox() {
    this.showResultAlert = true;
    setTimeout(() => this.showResultAlert = false, 10000);
  }

  showCancelAlert = false;
  showCancelAlertBox() {
    this.showCancelAlert = true;
    setTimeout(() => this.showCancelAlert = false, 10000);
  }

  showReassignAlert = false;
  showReassignAlertBox() {
    this.showReassignAlert = true;
    setTimeout(() => this.showReassignAlert = false, 10000);
  }

  showGenericAlert = false;
  showGenericAlertBox() {
    this.showGenericAlert = true;
    setTimeout(() => (this.showGenericAlert = false), 10000);
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
    this.loadData();
  }


  toggleRightSection() {
    this.rightSectionCollapsed = !this.rightSectionCollapsed;
  }
  //toogle end

  DefaultBox() {
    this.cancelBox = false;
    this.reassignBox = false;
  }

  AlertBoxInitialize() {
    if (this.cancelBox) {
      this.showCancelAlertBox();
    } else if (this.reassignBox) {
      this.showReassignAlertBox();
    }
  }

  permMTL = perm.Master_Fee_Table_View_Master_Task_List; // all the perm_cd for this module seperated with comma
  permMTLAllow = ""; // variable to store allowed permission for the user
  permListAllow: number = 0; // if 0 then not allow to view listing page, else allow

  constructor(
    private http: HttpClient,
    config: NgbPaginationConfig,
    private router: Router,
    private translate: TranslateService,
    private globalService: GlobalService,
    private authService: AuthService) {
    config.maxSize = 3;
    config.boundaryLinks = true;
    this.translate.setDefaultLang(this.globalService.getGlobalValue()); // default language
    this.translate.use(this.globalService.getGlobalValue());
  }

  ngOnInit() {

    this.effectiveDateBsValue = new Date();
    this.effectiveDateMinDate.setMonth(this.effectiveDateBsValue.getMonth() - 1);
   // this.effectiveDateMaxDate.setMonth(this.effectiveDateBsValue.getMonth() + 1); //different from other because want add 1 more month
   // this.selectedEffectiveDate = [this.effectiveDateMinDate, this.effectiveDateMaxDate];


    this.requestedDateBsValue = new Date();
    this.requestedDateMinDate.setMonth(this.requestedDateBsValue.getMonth() - 1);
   // this.selectedRequestedDate = [this.requestedDateMinDate, this.requestedDateBsValue];

    // this.effectiveDateMinDate.setMonth(this.effectiveDateMinDate.getMonth() - 1);
    // this.selectedEffectiveDate = [this.effectiveDateMinDate, this.effectiveDateBsValue];

    //this.requestedDateMinDate.setMonth(this.requestedDateMinDate.getMonth() - 1);
    //this.selectedRequestedDate = [this.requestedDateMinDate, this.requestedDateBsValue];

    //put default box above alert message
    this.DefaultBox()
    this.alertMessage = history.state.alert_msg;
    this.showTaskNotUpdateAlert = history.state.showTaskNotUpdateAlert;
    
    if (this.alertMessage !== undefined) {
      if (this.alertMessage === "reassigned") {
        this.reassignBox = true;
      }
      else if (this.alertMessage === "cancelled") {
        this.cancelBox = true;
      }
    }

    if(this.showTaskNotUpdateAlert === true){
      this.showTaskNotExistAlertBox();
    }

    // Reset alert_msg in history state so if refresh page, alert message will not persist
    history.replaceState({ ...history.state, alert_msg: undefined, showTaskNotUpdateAlert: undefined }, '');

    this.populateStatus();
    this.populateSourceSystemCode();
    this.populateRequester();
    this.loadData();
  }

  loadData() {

    this.isLoading = true;

    this.authService.checkUserRole(this.authService.username, this.permMTL)
      .subscribe(
        (response: any) => {
          this.permMTLAllow = response.data;
          this.permListAllow = this.permMTLAllow.includes(perm.Master_Fee_Table_View_Master_Task_List) ? 1 : 0;
          if (this.permListAllow === 0) {
            this.router.navigate(['/access-denied']);
            return; // Exit the function to prevent further execution
          }

          const urlMftWF = environment.apiUrl + '/api/mftwf/v1/getmasterfeetableworkflow';

          // Set your authorization header
          const headers = new HttpHeaders({
            Authorization: environment.authKey,
            'Content-Type': 'application/json'
          });


          // Create the request body with your form data
          const Body: any = {

            i_page: this.page,
            i_size: this.itemsPerPage,

          };

          // if (this.taskId && this.taskId.trim()) {
          //  Body.i_wf_id = this.taskId; //is call taskId here because search filter use task id
          //}

          if (this.taskId !== null) {
            // use a regular expression to extract the number from the string
            const match = this.taskId.match(/\d+/);

            // check if there is a match and convert it to a number
            this.extractedNumber = match ? +match[0] : null;
          }
          else {
            this.extractedNumber = null;
          }

          // if (this.extractedNumber) {
          Body.i_wf_id = this.extractedNumber;     //is call taskId here because search filter use task id
          //  }

          if (this.feeDetailId && this.feeDetailId.trim()) {
            Body.i_fee_detail_id = this.feeDetailId;
          }

          Body.i_assign_to = null;

          if (this.status && this.status.trim()) {
            Body.i_status = this.status;
          }

          Body.i_created_by = null;

          if (this.nm && this.nm.trim()) {
            Body.i_created_by_nm = this.nm;
          }

          Body.i_modified_by = null;
          Body.i_modified_by_nm = null;
          Body.i_dt_modified_fr = null;
          Body.i_dt_modified_to = null;

          /* if (this.selectedRequestedDate && this.selectedRequestedDate.start && this.selectedRequestedDate.end) {
             Body.i_dt_created_fr = this.selectedRequestedDate.start.format('YYYY-MM-DD');
             Body.i_dt_created_to = this.selectedRequestedDate.end
               .add(1, 'day')
               .format('YYYY-MM-DD');
           }*/

          if (this.selectedRequestedDate && this.selectedRequestedDate.length>0) {
            //Body.i_dt_modified_fr = 
            Body.i_dt_created_fr = formatDate(this.selectedRequestedDate[0], 'YYYY-MM-dd', 'en');//.format('YYYY-MM-DD');
            this.selectedRequestedDate[1].setDate(this.selectedRequestedDate[1].getDate() + 1,);
            Body.i_dt_created_to = formatDate(this.selectedRequestedDate[1], 'YYYY-MM-dd', 'en');
          }

          if (this.selectedEffectiveDate && this.selectedEffectiveDate.length>0) {
            //Body.i_dt_modified_fr = 
            Body.i_dt_effective_fr = formatDate(this.selectedEffectiveDate[0], 'YYYY-MM-dd', 'en');//.format('YYYY-MM-DD');
            this.selectedEffectiveDate[1].setDate(this.selectedEffectiveDate[1].getDate() ,);
            Body.i_dt_effective_to = formatDate(this.selectedEffectiveDate[1], 'YYYY-MM-dd', 'en');
          }

          /* if (this.selectedEffectiveDate && this.selectedEffectiveDate.start && this.selectedEffectiveDate.end) {
             Body.i_dt_effective_fr = this.selectedEffectiveDate.start.format('YYYY-MM-DD');
             Body.i_dt_effective_to = this.selectedEffectiveDate.end
               .add(1, 'day')
               .format('YYYY-MM-DD');
           }*/

          if (this.sourceSystemCode && this.sourceSystemCode.trim()) {
            Body.i_ss_cd = this.sourceSystemCode;
          }

          Body.i_wf_is_in_prg = "lt";

          this.http.post(urlMftWF, Body, { headers }).subscribe(
            (response: any) => {
              console.log("MFT is " + response.data);
              // You can process the response data here

              if (response.data.length === 0) {
                this.totalRecords = 0;
                // this.showResultAlertBox();
                this.isDisplay = false;
                this.isLoading = false;
              }
              else {
                this.totalRecords = response.data[0].total;
                this.mftwfs = response.data;
                this.AlertBoxInitialize();
                this.DefaultBox();
                this.isDisplay = true;
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
            }
          );
        }
      );

  }

  async viewSeleted(item: any) {

    const wf_id = item.wf_id;
    const status__From_Assigned = item.status;
    const task_id = item.task_id;
    const assign_to = item.assign_to;
    const fee_detail_pk = item.fee_detail_pk
    let edit_Mode: boolean = false;
    let show_requester_table: boolean = false;
    const from_view = true;
    const from_route = 3;
    const navigate_not_refresh = true

    //if(item.action==="Request Add" && item.status==="P-RHOD"){
    //  this.router.navigate(['/my-component']);
    // }

    const validResponse = await this.checkCurrentMFTWFStatus(wf_id);
    if (!validResponse) {
      return;
    }

    console.log("currentMFTWFStatus" + this.currentMFTWFStatus);
    if(status__From_Assigned !== this.currentMFTWFStatus){
      this.showTaskNotExistAlertBox();
      this.loadData();
      return;
    }

    if (item.action === "Request Add" || item.action === "Request Add-FIN") {
      edit_Mode = false;
    }
    else {
      edit_Mode = true;
    }

    if (item.action === "Request Add-FIN" || item.action === "Request Edit-FIN") {
      show_requester_table = false;
    }
    else {
      show_requester_table = true;
    }

    if(this.currentMFTWFStatus === 'C'){
      this.router.navigate(['/mft-item-task-list'], { state: {  wf_id, status__From_Assigned, task_id, fee_detail_pk, edit_Mode, show_requester_table, from_route } });
    }
    else{
      this.router.navigate(['/task-details'], { state: { wf_id, status__From_Assigned, task_id, assign_to, fee_detail_pk, edit_Mode, show_requester_table, from_view, navigate_not_refresh } });
    }

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
      i_page: this.page,
      i_size: this.dropDownSize, //dont use item per page here because it is for table
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

      }
    );
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
          console.error('Invalid response format:', response);
        }
        else {
          this.sourceSystemCodeOptions = response.data
          // this.sourceSystemCodeOptions=this.sourceSystemCodeOptions.concat(response.data)
          // Handle a successful response (e.g., show a success message)
        }
      },
      (error) => {
        console.error('There was an error retrieving the source code system:', error);
        // Handle API errors (e.g., show an error message)
      }
    );

  }

  populateRequester() {

    const url = environment.apiUrl + '/api/mft/v1/getuserbyrole';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json'
    });

    // Create the request body with your form data
    const requestBody: any = {
      i_page: this.page,
      i_size: this.dropDownSize,
      i_role_nm_en: 'REQUESTER',
      i_role_nm_bm: null,
      i_status: Systemstatus.Active
    };

    this.http.post(url, requestBody, { headers }).subscribe(
      (response: any) => {
        if (response.data.length === 0) {
          console.error('Invalid response format:', response);
        }
        else {
          this.users = response.data;
        }

      },
      (error) => {
        console.error('There was an error retrieving the requester:', error);
        // Handle errors here
      }
    );

  }

  apply() {
    this.loadData();
    this.rightSectionCollapsed = true;
  }

  reset() {

    this.taskId = null;
    this.feeDetailId = null;
    this.sourceSystemCode = null;
    this.status = null;
    this.nm = null;

    // this.effectiveDateMinDate.setDate(this.effectiveDateMinDate.getMonth() - 1);
    //this.selectedEffectiveDate = [this.effectiveDateMinDate, this.effectiveDateBsValue];

    //this.requestedDateMinDate.setDate(this.requestedDateMinDate.getMonth() - 1);
    //this.selectedRequestedDate = [this.requestedDateMinDate, this.requestedDateBsValue];

    this.effectiveDateBsValue = new Date();
    this.effectiveDateMinDate.setMonth(this.effectiveDateBsValue.getMonth() - 1);
    this.selectedEffectiveDate = [];
    // this.selectedEffectiveDate = [this.effectiveDateMinDate, this.effectiveDateBsValue];
    // this.selectedEffectiveDate[1].setDate(this.selectedEffectiveDate[1].getDate() + 1);

    this.requestedDateBsValue = new Date();
    this.requestedDateMinDate.setMonth(this.requestedDateBsValue.getMonth() - 1);
    this.selectedRequestedDate = [];
    // this.selectedRequestedDate = [this.requestedDateMinDate, this.requestedDateBsValue];
    // this.selectedRequestedDate[1].setDate(this.selectedRequestedDate[1].getDate() + 1);
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
      i_wf_is_in_prg: "f"
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
