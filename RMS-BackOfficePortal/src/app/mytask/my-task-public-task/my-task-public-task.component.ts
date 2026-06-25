import { Component, ViewChild } from '@angular/core';
import { Param } from '../../core/models/entity';
import { environment } from 'src/environments/environment';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Router, ActivatedRoute  } from '@angular/router';
import { NgbPaginationConfig } from '@ng-bootstrap/ng-bootstrap';
import { DatePipe } from '@angular/common';
import { fadeInOut } from '../../shared/animation';
import { formatDate } from '@angular/common';
import { TranslateService } from '@ngx-translate/core';
import { GlobalService } from 'src/app/shared/global.service';
import { AuthService } from 'src/app/core/services/auth.service';
import { perm } from 'src/permissions/perm';
import { MyTasksPublicTasks } from 'src/app/core/models/my-tasks-public-tasks.interface';
import { NotificationService } from 'src/app/core/services/notification.service';
import { Observable, of, combineLatest, map } from 'rxjs';
import { TriggerNotificationUpdateService } from 'src/app/core/services/TriggerNotificationUpdateService.service';

@Component({
  selector: 'app-my-task-public-task',
  templateUrl: './my-task-public-task_v2.component.html',
  styleUrls: ['./my-task-public-task_v2.component.scss'],
  providers: [DatePipe],
  animations: [fadeInOut]
})

export class MyTaskPublicTaskComponent {

  username = this.authService.username;
  name = this.authService.name;
  roles: any = this.authService.roles;
  pool: string = '';

  isDisplay: boolean = false;
  isLoading: boolean = true;

  //date
  formattedStartDate: string = "";
  formattedEndDate: string = "";
  date_modified_from: string = "";
  convertedDate_modified_from = "";
  date_modified_to: string = "";
  convertedDate_modified_to = "";

  page = environment.DefaultPage;
  itemsPerPage = environment.ItemPerPage;
  totalRecords: number = 0;

  statusOptions: Param[] = [];

  initialNumber = [
    { value: 0, label: 'No' },
    { value: 1, label: 'Yes' },
  ]

  PublicTasks: MyTasksPublicTasks[] = [];

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
  alertMessage: string | undefined = undefined

  taskApprovedBox: boolean = false;
  querySubmittedBox: boolean = false;
  rejectedBox: boolean = false;
  submittedForApprovalBox: boolean = false;

  permPTLAllow = "";
  permPublicTaskListAllow: number = 0;

  requestedBy: string | null = null;
  taskDescriptionS: string | null = null;
  dateRequested: Date | null = null;

  userName = this.authService.username;

  userNameToCheck = "";
  permToCheck = "";
  userRole: string = '';

  alertTime: number = 10000;

  //alert start
  showResultAlert = false;
  firstResultAlert = true;

  showResultAlertBox() {
    if(this.firstResultAlert)
      this.firstResultAlert = false;
    else
      this.showResultAlert = true;

    setTimeout(() => this.showResultAlert = false, this.alertTime);
  }

  showTaskApprovedAlert = false;
  showTaskApprovedAlertBox() {
    this.showTaskApprovedAlert = true;
    setTimeout(() => this.showTaskApprovedAlert = false, this.alertTime);
  }

  showQuerySubmittedAlert = false;
  showQuerySubmittedAlertBox() {
    this.showQuerySubmittedAlert = true;
    setTimeout(() => this.showQuerySubmittedAlert = false, this.alertTime);
  }

  showSubmittedForApprovalAlert = false;
  showSubmittedForApprovalAlertBox() {
    this.showSubmittedForApprovalAlert = true;
    setTimeout(() => this.showSubmittedForApprovalAlert = false, this.alertTime);
  }

  showRejectedAlert = false;
  showRejectedAlertBox() {
    this.showRejectedAlert = true;
    setTimeout(() => this.showRejectedAlert = false, this.alertTime);
  }

  showGenericAlert = false;
  showGenericAlertBox() {
    this.showGenericAlert = true;
    setTimeout(() => (this.showGenericAlert = false), this.alertTime);
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
    // this.taskApprovedBox = false;
    this.querySubmittedBox = false;
    // this.rejectedBox = false;
    // this.submittedForApprovalBox = false;
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
    }
  }

  constructor(
    private http: HttpClient,
    config: NgbPaginationConfig,
    private router: Router,
    private authService: AuthService,
    private translate: TranslateService,
    private globalService: GlobalService,
    private route: ActivatedRoute,
    private tnu: TriggerNotificationUpdateService
  ) {
    config.maxSize = 3;
    config.boundaryLinks = true;
    this.translate.setDefaultLang(this.globalService.getGlobalValue()); // default language
    this.translate.use(this.globalService.getGlobalValue());
    this.route.params.subscribe(val => {
      this.isLoading = true;
      var tmp = window.location.href.split('/');
      if(tmp[tmp.length-1] != 'my-task-public-task'){
        if(this.pool != tmp[tmp.length-1])
          this.firstResultAlert = true;
        this.pool = tmp[tmp.length-1];
      }
      this.initializeUser();
    });
  }

  ngOnInit(): void {
    var tmp = window.location.href.split('/');
    if(tmp[tmp.length-1] != 'my-task-public-task'){
      if(this.pool != tmp[tmp.length-1])
        this.firstResultAlert = true;
      this.pool = tmp[tmp.length-1];
    }

    this.bsValue = new Date();
    this.minDate.setMonth(this.bsValue.getMonth() - 1);
    // this.selected = [this.minDate, this.bsValue];

    //put default box before alert message
    this.DefaultBox()
    this.alertMessage = history.state.alert_msg;
    if (this.alertMessage !== undefined) {

      if (this.alertMessage === "submitted") {
        this.querySubmittedBox = true;
      }
    }
    // Reset alert_msg in history state so if refresh page, alert message will not persist
    history.replaceState({ ...history.state, alert_msg: undefined }, '');
    //this.initializeUser();
    this.populateStatus();
  }

  initializeUser() {
    this.userNameToCheck = this.authService.username;
    if(this.pool == 'fa'){
        //console.log("Matched Case: FINANCEADMIN");
        this.permToCheck = perm.Finance_Admin_Task_Listing;
        this.userRole = 'FINANCEADMIN';
    }
    else if(this.pool == 'bym'){
        //console.log("Matched Case: FINANCESENIORMANAGER / FINANCEHOD / DEPUTYCEO / CEO");
        this.permToCheck = perm.BYM_Task_Listing;
        //if(this.roles.includes('FINANCESENIORMANAGER'))
          this.userRole = 'FINANCESENIORMANAGER';
        //if(this.roles.includes('FINANCEHOD'))
          this.userRole = this.userRole == '' ? 'FINANCEHOD' : this.userRole + ',' + 'FINANCEHOD';
        //if(this.roles.includes('DEPUTYCEO'))
          this.userRole = this.userRole == '' ? 'DEPUTYCEO' : this.userRole + ',' + 'DEPUTYCEO';
        //if(this.roles.includes('CEO'))
          this.userRole = this.userRole == '' ? 'CEO' : this.userRole + ',' + 'CEO';
    }
    else if(this.pool == 'pg'){
        //console.log("Matched Case: PGPERSONNEL");
        this.permToCheck = perm.PG_Task_Listing;
        this.userRole = 'PGPERSONNEL';
    }
    else if(this.pool == 'os'){
        //console.log("Matched Case: OTCSTUFF");
        this.permToCheck = perm.OTC_Staff_Task_Listing;
        this.userRole = 'OTCSTAFF';
    }
    else if(this.pool == 'osp'){
        //console.log("Matched Case: OTCSUPERVISOR");
        this.permToCheck = perm.OTC_Supervisor_Task_Listing;
        this.userRole = 'OTCSUPERVISOR';
    }
    else if(this.pool == 'obm'){
        //console.log("Matched Case: OTCBRANCHMANAGER");
        this.permToCheck = perm.OTC_Branch_Manager_Task_Listing;
        this.userRole = 'OTCBRANCHMANAGER';
    }
    else if(this.pool == 'sme'){
        //console.log("Matched Case: CRS SME Roles");
        this.permToCheck = perm.SME_Task_Listing;
        this.userRole = 'SME';
        //this.userRole = 'CRS SME';
        //if(this.roles.includes('CRS SME Compound'))
          //this.userRole = 'CRS SME Compound';
        //if(this.roles.includes('CRS SME RIPL'))
          //this.userRole = this.userRole == '' ? 'CRS SME RIPL' : this.userRole + ',' + 'CRS SME RIPL';
        //if(this.roles.includes('CRS SME Litigation'))
          //this.userRole = this.userRole == '' ? 'CRS SME Litigation' : this.userRole + ',' + 'CRS SME Litigation';
    }
    else if(this.pool == 'lgl'){
        //console.log("Matched Case: LEGAL");
        this.permToCheck = perm.LGL_Task_Listing;
        //if(this.roles.includes('LEGAL'))
        this.userRole = 'LEGAL';
    }
    else{
        //console.log("Matched Case: Default - No Specific Role Found");
        this.permToCheck = perm.Public_Task_Listing;
      }

    //console.log("Assigned Permissions:" + this.permToCheck);

    this.authService.checkUserRole(this.userNameToCheck, this.permToCheck)
      .subscribe(
        (response: any) => {
          this.permPTLAllow = response.data;
          this.permPublicTaskListAllow = this.permPTLAllow.includes(this.permToCheck) ? 1 : 0;
          if (this.permPublicTaskListAllow === 0) {
            //console.log('Access denied due to ' + this.permPTLAllow);
            this.router.navigate(['/access-denied']);
            return;
          }
          else
            this.loadData();
    });
  }

  loadData() {
    this.username = this.authService.username;
    if(this.username == null || this.username == ''){
      setTimeout(() => {this.loadData();}, 1000);
      return;
    }

    if(this.userRole.length < 1){
      this.PublicTasks = [];
      this.totalRecords = 0;
      this.isDisplay = false;
      this.showResultAlertBox();
      this.isLoading = false;
      return;
    }

    this.isDisplay = true;
    this.isLoading = true;
    const url = environment.apiUrl + '/api/mytasks/v1/getpublictasks';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    // Create the request body with your form data
    const Body: any = {
      i_page: this.page.toString(),
      i_size: this.itemsPerPage.toString(),
      i_username: this.username,
      i_userrole: this.userRole,
    };
    //console.log(Body);

    Body.i_task_id = this.taskId;

    if (this.taskDescriptionS && this.taskDescriptionS.trim())
      Body.i_task_desc = this.taskDescriptionS;

    if (this.requestedBy && this.requestedBy.trim())
      Body.i_requested_by = this.requestedBy;

    Body.i_dt_requested = this.dateRequested ? formatDate(this.dateRequested, 'YYYY-MM-dd', 'en') : null;

    if (this.status && this.status.trim()) 
      Body.i_status = this.status;

    this.http.post(url, Body, { headers }).subscribe(
      (response: any) => {
        //console.log(response);
        // You can process the response data here

        if (response.data.length === 0) {
          this.PublicTasks = [];
          this.totalRecords = 0;
          this.isDisplay = false;
          this.showResultAlertBox();
          this.isLoading = false;
        }
        else {
          this.PublicTasks = [];
          this.PublicTasks = response.data;
          this.totalRecords = response.data[0].total;
          //console.log(response.data);
          this.AlertBoxInitialize();
          this.DefaultBox();
          this.isDisplay = true;
          this.isLoading = false;
        }

      },
      (error: any) => {
        console.error(error);
        this.isLoading = false;
        this.showGenericAlertBox();
        // Handle errors here
      });
  };

  populateStatus() {
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
          //console.log(this.statusOptions);
        }

      },
      (error) => {
        console.error('There was an error retrieving the status:', error);
        // Handle API errors (e.g., show an error message)
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
    this.modifiedByNm = null;
    this.bsValue = new Date();
    this.minDate.setMonth(this.bsValue.getMonth() - 1);
    this.selected = [this.minDate, this.bsValue];
    this.selected = [];
    this.status = null;
    this.requestedBy = null;
    this.taskDescriptionS = null;
    this.dateRequested = null;
  }

  showInsertAlert: boolean = false;
  pickUp() {
    this.isLoading = true;
    // selected tasks are those being ticked
    const selectedTasks = this.PublicTasks.filter(task => task.isSelected);
    //console.log(selectedTasks);

    if (selectedTasks.length === 0) {
      alert("No tasks selected!");
      return;
    }

    // Prepare data for backend
    const selectedRecordsToUpdate = []

    for (let i = 0; i < selectedTasks.length; i++) {
      selectedRecordsToUpdate.push({
        pk: selectedTasks[i].pk,
        pickup_person: this.username,
        origin_table: selectedTasks[i].origin_table
      });
    }

    const url = environment.apiUrl + '/api/mytasks/v1/pickuptasks';

    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    const requestBody = selectedRecordsToUpdate.map(record => ({
      i_pk: record.pk,
      i_pickup_person: record.pickup_person,
      i_origin_table: record.origin_table
    }));

    this.http.post(url, requestBody, { headers }).subscribe(
      (response: any) => {
        //console.log(response);
        //this.showResultAlertBox();
        //this.isLoading = false;

        this.showInsertAlert = true;
        setTimeout(() => {
          this.showInsertAlert = false;        
              
          this.tnu.emitEvent('trigger');
          this.loadData();
          return;
        }, 2500);
      },
      (error: any) => {
        console.error(error);
        this.isLoading = false;
      });
  }

  disableCheckbox(item: any){
    if(this.username == item.pickup_by || this.name == item.pickup_by)
      return false;
    if(this.pool == 'fa' && item.task_status.includes(' Query'))
      return false;
    return true;
  }
}
