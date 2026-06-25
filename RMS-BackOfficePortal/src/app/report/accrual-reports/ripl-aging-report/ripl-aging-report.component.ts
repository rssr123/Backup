import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { environment } from 'src/environments/environment';
import { NgbPaginationConfig } from '@ng-bootstrap/ng-bootstrap';
import { Router } from '@angular/router';
import { DataService } from 'src/app/core/services/data.service';
import { AuthService } from 'src/app/core/services/auth.service';
import { DatePipe } from '@angular/common';
import { saveAs } from "file-saver";
import { empty } from 'rxjs';
import { NgForm } from '@angular/forms';
import { RIPLAging } from 'src/app/core/models/riplaging.interface';
import { fadeInOut } from 'src/app/shared/animation';
import { perm } from 'src/permissions/perm';

@Component({
  selector: 'app-ripl-aging-report',
  templateUrl: './ripl-aging-report.component.html',
  styleUrls: ['./ripl-aging-report.component.scss'],
  animations: [fadeInOut]
})
export class RIPLAgingReportComponent implements OnInit {


  createdDate: string | null = null;
  impairStatus: string | number | null = 0;
  expiredStatus: string | number | null = 0;
  entityType: string[] = [];
  entityName: string | null = null;
  RIPLType: string[] = [];
  RIPLStatus: string[] = [];
  emailNotification: number | null = 0;
  dueInValid: boolean = true;

  createdDateString: string | null = null;
  dueDateFromString: string | null = null;
  dueDateToString: string | null = null;
  receiptDateFromString: string | null = null;
  receiptDateToString: string | null = null;
  impairDateFromString: string | null = null;
  impairDateToString: string | null = null;
  writeoffDateFromString: string | null = null;
  writeoffDateToString: string | null = null;

  isDisplayReport: boolean = false;
  isLoadingReport: boolean = false;
  pageReport = environment.DefaultPage;
  itemsPerPageReport = environment.ItemPerPage;
  totalRecordsReport: number = 0;
  riplagings: RIPLAging[] = [];

  disableSubmitButton = false;

  //default pagination
  selectedValue = environment.dropdownOptions[0];
  dropdownOptions = environment.dropdownOptions;

  LoadData(singleItem: number) {
    this.selectedValue = singleItem;
    this.itemsPerPageReport = this.selectedValue;
    this.loadData();
  }

  //date range picker
  dueDate!: Date[] | null;
  dueStartDateString: string | null = null;
  dueEndDateString: string | null = null;

  rcptDate!: Date[] | null;
  rcptStartDateString: string | null = null;
  rcptEndDateString: string | null = null;

  impDate!: Date[] | null;
  impStartDateString: string | null = null;
  impEndDateString: string | null = null;

  woDate!: Date[] | null;
  woStartDateString: string | null = null;
  woEndDateString: string | null = null;
  //date range picker 

  fileType: any =
    [{ value: 'csv', label: 'CSV' },
    { value: 'pdf', label: 'PDF' },
    { value: 'xlsx', label: 'XLSX' }]

  inclusiveImpairedOption: any =
    [
      { value: 0, label: 'No' },
      { value: 1, label: 'Yes' }
    ]

  inclusiveExpiredOption: any =
    [
      { value: 0, label: 'No' },
      { value: 1, label: 'Yes' }
    ]

  entityTypeOption: any =
    [
      { value: null, label: 'All' },
      { value: 'B', label: 'Business' },
      { value: 'C', label: 'Company' },
      { value: 'I', label: 'Individual' },
      { value: 'L', label: 'LLP' }
    ]

  RIPLTypeOption: any =
    [
      { value: null, label: 'All' },
      { value: 'AR', label: 'Annual Return' },
      { value: 'FS', label: 'Financing Statement' }
    ]

  RIPLStatusOption: any =
    [
      { value: null, label: 'All' },
      { value: 'CA', label: 'Collectable' },
      { value: 'CE', label: 'Collected' },
      { value: 'IP', label: 'Impair' },
      { value: 'WO', label: 'Write Off' },
    ]

  emailOption: any =
    [
      { value: 0, label: 'No' },
      { value: 1, label: 'Yes' }
    ]

  selectedFileType: string = 'pdf'; // Default value

  showResultAlert = false;
  showResultAlertBox() {
    this.showResultAlert = true;
    setTimeout(() => this.showResultAlert = false, 2000);
  }

  showCancelAlert = false;
  showCancelAlertBox() {
    this.showCancelAlert = true;
    setTimeout(() => this.showCancelAlert = false, 2000);
  }

  showPendingAlert = false;
  showPendingAlertBox() {
    this.showPendingAlert = true;
    setTimeout(() => this.showPendingAlert = false, 2000);
  }

  // Configuring Permissions for User and roles variables
  permReport = perm.Reporting_and_Analysis_RIPL_Aging

  permReportAllow = ""; // variable to store allowed permission for the user
  permListAllow: number = 0; // if 0 then not allow to view listing page, else allow


  constructor(private http: HttpClient, config: NgbPaginationConfig, private router: Router, private dataService: DataService, private authService: AuthService, public datepipe: DatePipe) {
    config.maxSize = 3;
    config.boundaryLinks = true;
  }

  ngOnInit() {
    this.resetWithDefaults();
    this.loadData();
    this.loadPermission();
  }


  formatDate(date: Date): string {
    const day = ('0' + date.getDate()).slice(-2);
    const month = date.toLocaleString('default', { month: 'short' });
    const year = date.getFullYear();
    return `${day} ${month} ${year}`;
  }



  resetWithDefaults() {

    const today = new Date();
    const sixMonthsAgo = new Date();
    sixMonthsAgo.setMonth(today.getMonth() - 6);

    this.createdDate = this.formatDate(new Date());
    this.impairStatus = 0;
    this.expiredStatus = 0;
    this.entityType = [];
    this.entityName = null;
    this.RIPLType = [];
    this.RIPLStatus = [];
    this.emailNotification = 0;
    this.dueDate = [sixMonthsAgo, today];
    this.rcptDate = null;
    this.impDate = null;
    this.woDate = null;
    this.selectedFileType = 'pdf';
  }

  onKeyDown(event: KeyboardEvent): void {
    if (event.key === 'Backspace') {
      return;
    }
    // Prevent manual key entry
    event.preventDefault();
  }

  isDueDateInvalid(): boolean {

    if (!this.dueDate || this.dueDate.length !== 2) {
      return false; // No value or incomplete range, so it's not invalid
    }

    const startDate = new Date(this.dueDate[0]);
    const endDate = new Date(this.dueDate[1]);

    startDate.setHours(0, 0, 0, 0);
    endDate.setHours(0, 0, 0, 0);
    const startYear = startDate.getFullYear();
    const startMonth = startDate.getMonth();
    const endYear = endDate.getFullYear();
    const endMonth = endDate.getMonth();

    const differenceMonths = (endYear - startYear) * 12 + (endMonth - startMonth);

    if (differenceMonths > 5) {
      this.dueInValid = true;
    }
    else {
      this.dueInValid = false;
    }
    return this.dueInValid;
  }

  //form handle before submit start
  async handleFormSubmit(form: NgForm) {

    let formValidation: boolean | null = false;

    formValidation = form.valid
    //&& !this.dueValid

    if (formValidation) {
      await this.insertRIPLAging()
    } else {
      Object.values(form.controls).forEach((control) => {
        control.markAsTouched();
      });
    }
  }

  loadPermission() {
    this.authService.checkUserRole(this.authService.username, this.permReport)
      .subscribe(
        (response: any) => {
          this.permReportAllow = response.data;
          this.permListAllow = this.permReportAllow.includes(perm.Reporting_and_Analysis_RIPL_Aging) ? 1 : 0;
          if (this.permListAllow === 0) {
            console.log("access-denied");
            this.router.navigate(['/access-denied']);
            return; // Exit the function to prevent further execution
          }
        }
      );

  }

  async insertRIPLAging(): Promise<void> {

    //let invalidRIPLAging = await this.checkPendingAndInProgessRIPLAging();



    const insertURL = environment.apiUrl + '/api/rrareport/v1/insertriplagingrpt';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json'
    });

    this.createdDateString = this.datepipe.transform(this.createdDate, 'yyyy-MM-dd') || null;

    if (this.dueDate && this.dueDate.length > 0) {
      this.dueDateFromString = this.datepipe.transform(this.dueDate[0], 'yyyy-MM-dd');
      this.dueDateToString = this.datepipe.transform(this.dueDate[1], 'yyyy-MM-dd');
    }

    if (this.rcptDate && this.rcptDate.length > 0) {
      this.receiptDateFromString = this.datepipe.transform(this.rcptDate[0], 'yyyy-MM-dd');
      this.receiptDateToString = this.datepipe.transform(this.rcptDate[1], 'yyyy-MM-dd');
    }

    if (this.impDate && this.impDate.length > 0) {
      this.impairDateFromString = this.datepipe.transform(this.impDate[0], 'yyyy-MM-dd');
      this.impairDateToString = this.datepipe.transform(this.impDate[1], 'yyyy-MM-dd');
    }

    if (this.woDate && this.woDate.length > 0) {
      this.writeoffDateFromString = this.datepipe.transform(this.woDate[0], 'yyyy-MM-dd');
      this.writeoffDateToString = this.datepipe.transform(this.woDate[1], 'yyyy-MM-dd');
    }

    const body: any = {
      i_p_imp_status: this.impairStatus,
      i_p_exp_status: this.expiredStatus
    };


    if (this.createdDate) {
      body.i_p_dt_req = this.createdDateString;
    }


    if (this.entityType !== null && this.entityType.length > 0) {
      body.i_p_ent_ty = this.entityType;
    }


    if (this.entityName && this.entityName.trim()) {
      body.i_p_ent_nm = this.entityName;
    }



    if (this.receiptDateFromString) {
      body.i_p_dt_rcpt_fr = this.receiptDateFromString;
    }

    if (this.receiptDateToString) {
      body.i_p_dt_rcpt_to = this.receiptDateToString;
    }

    if (this.dueDateFromString) {
      body.i_p_dt_due_fr = this.dueDateFromString;
    }

    if (this.dueDateToString) {
      body.i_p_dt_due_to = this.dueDateToString;
    }

    if (this.impairDateFromString) {
      body.i_p_dt_imp_fr = this.impairDateFromString;
    }

    if (this.impairDateToString) {
      body.i_p_dt_imp_to = this.impairDateToString;
    }

    if (this.writeoffDateFromString) {
      body.i_p_dt_wo_fr = this.writeoffDateFromString;
    }

    if (this.writeoffDateToString) {
      body.i_p_dt_wo_to = this.writeoffDateToString;
    }

    body.i_status = 'P'
    body.i_p_file_type = this.selectedFileType;
    body.i_p_file_size = null;
    body.p_file_nm = null;

    body.i_email_ntfn = this.emailNotification; //normal dropdown no need if condition

    try {
      const response: any = await this.http.post(insertURL, body, { headers }).toPromise();
      if (response.header.statusCode === '00') {
        this.showResultAlertBox();
        this.loadData();
      } else {
        console.log("error")
      }
    } catch (error) {
      console.error(error);
    }

  }

  loadData() {

    this.isDisplayReport = true;
    this.isLoadingReport = true;

    const url = environment.apiUrl + '/api/rrareport/v1/getriplaginglistingrpt';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json'
    });


    // Create the request body with your form data
    const Body: any = {
      i_page: this.pageReport,
      i_size: this.itemsPerPageReport
    };


    this.http.post(url, Body, { headers }).subscribe(
      (response: any) => {
        if (response.data.length === 0) {
          this.totalRecordsReport = 0;
          this.isDisplayReport = false
          this.isLoadingReport = false;
        }
        else {
          this.riplagings = response.data;
          this.isLoadingReport = false;
          this.totalRecordsReport = response.data[0].total;
        }

        if (this.riplagings !== null && this.riplagings.length > 0) {
          if (this.riplagings[0].status === 'P')
            this.disableSubmitButton = true;
          else
            this.disableSubmitButton = false;
        }
        else
          this.disableSubmitButton = false;
      },
      (error) => {
        console.error('There was an error retrieving the ripl aging:', error);
        this.isLoadingReport = false;
      }
    );
  }

  // onRIPLStatusChange(newStatus: string) {
  //   if (newStatus === 'T') {
  //     this.incTermStatus = 1;
  //   }
  // }

  formatDateForReportName(dateString: string): string {
    const formattedDate = this.datepipe.transform(dateString, 'yyyyMMdd');
    return formattedDate ? formattedDate : '';
  }




  downloadFile(rpt_ripl_age_id: number) {

    const generateURL = environment.apiUrl + '/api/rrareport/v1/generateriplagingrpt';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json'
    });

    // Create the request body with your form data
    var requestBody: { [k: string]: any } = {
      i_rpt_ripl_age_id: rpt_ripl_age_id
    };

    var fileTypeMime = this.selectedFileType == 'pdf' ? 'application/pdf' : this.selectedFileType == 'xlsx' ? 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' : this.selectedFileType == 'csv' ? 'text/csv' :
      'application/octet-stream';

    this.http.post(generateURL, requestBody, { observe: 'response', responseType: 'blob', headers: headers })
      .subscribe(response => {
        var blob = new Blob([response.body as Blob], { type: fileTypeMime });
        saveAs(blob, response.headers.get('content-disposition')!.split('filename=')[1]);
      });

  }


  async cancelFile(rpt_ripl_age_id: number): Promise<void> {

    //let invalidCancelRIPLAging = await this.checkIsStillPendingById(rpt_ripl_age_id);

    const updURL = environment.apiUrl + '/api/rrareport/v1/updateriplagingrpt';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json'
    });

    // Create the request body with your form data
    var requestBody: { [k: string]: any } = {
      i_rpt_ripl_age_id: rpt_ripl_age_id,
      i_status: 'C'
    };


    try {
      const response: any = await this.http.post(updURL, requestBody, { headers }).toPromise();
      if (response.header.statusCode === '00') {
        this.showCancelAlertBox();
        this.loadData();
      } else {
        console.log("Cancel not successful")
      }
    } catch (error) {
      console.error(error);
    }
  }

  async checkPendingAndInProgessRIPLAging(): Promise<boolean> {

    const url = environment.apiUrl + '/api/rrareport/v1/getriplagequeuerpt';

    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    const body: any = {
    };

    try {
      const response: any = await this.http.post(url, body, { headers }).toPromise();
      if (response.header.statusCode === '00') {
        const totalPendingOrInProgress = response.data
        if (totalPendingOrInProgress > 0) {
          this.showPendingAlertBox();
          return true;
        }
        else {
          return false;
        }
      } else {
        console.log("error")
        return true;
      }
    } catch (error) {
      console.error(error);
      return true;
    }
  }

  //before cancel, check if the record is still pending
  async checkIsStillPendingById(rpt_ripl_age_id: number): Promise<boolean> {

    const url = environment.apiUrl + '/api/rrareport/v1/getpendingriplagingrptbyid';

    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    const body: any = {
      i_rpt_ripl_age_id: rpt_ripl_age_id
    };

    try {
      const response: any = await this.http.post(url, body, { headers }).toPromise();
      if (response.header.statusCode === '00') {
        const stillPending = response.data
        if (stillPending <= 0) { //0 means not pending
          return true;
        }
        else {
          return false;
        }
      } else {
        console.log("error")
        return true;
      }
    } catch (error) {
      console.error(error);
      return true;
    }
  }

  // isAnyItemPendingOrInProgress(): boolean {
  //   return this.riplagings.some(item => item.status === 'P' || item.status === 'I');
  // }

  validateDueDate() {

    if (this.dueDate && this.dueDate.length > 0 && (this.dueDate[0] === undefined || this.dueDate[1] === undefined)) { // is undefined when backspace to clear the date
      this.dueDate = null;
    }
  }

  validateWoDate() {


    if (this.woDate && this.woDate.length > 0 && (this.woDate[0] === undefined || this.woDate[1] === undefined)) { // is undefined when backspace to clear the date
      this.woDate = null;
    }
  }

  validateImpDate() {

    if (this.impDate && this.impDate.length > 0 && (this.impDate[0] === undefined || this.impDate[1] === undefined)) { // is undefined when backspace to clear the date
      this.impDate = null;
    }
  }

  validateRcptDate() {

    if (this.rcptDate && this.rcptDate.length > 0 && (this.rcptDate[0] === undefined || this.rcptDate[1] === undefined)) { // is undefined when backspace to clear the date
      this.rcptDate = null;
    }
  }





}

