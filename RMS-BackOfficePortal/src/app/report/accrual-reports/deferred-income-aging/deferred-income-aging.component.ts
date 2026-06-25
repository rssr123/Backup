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
import { DIAging } from 'src/app/core/models/diaging.interface';
import { fadeInOut } from 'src/app/shared/animation';
import { perm } from 'src/permissions/perm';


@Component({
  selector: 'app-deferred-income-aging',
  templateUrl: './deferred-income-aging.component.html',
  styleUrls: ['./deferred-income-aging.component.scss'],
  animations: [fadeInOut]
})
export class DeferredIncomeAgingComponent implements OnInit {

  receiptDate: string | null = null;
  incTermStatus: string | number | null = 0;
  entityType: string[] = [];
  entityName: string | null = null;
  DIType: string[] = [];
  DIStatus: string[] = [];
  emailNotification: number | null = 0;
  effectiveInValid: boolean = true;
  receiptDateEndString: string | null = null;
  effDateFromString: string | null = null;
  effDateToString: string | null = null;
  expDateFromString: string | null = null;
  expDateToString: string | null = null;
  appDateFromString: string | null = null;
  appDateToString: string | null = null;
  termDateFromString: string | null = null;
  termDateToString: string | null = null;
  isDisplayReport: boolean = false;
  isLoadingReport: boolean = false;
  pageReport = environment.DefaultPage;
  itemsPerPageReport = environment.ItemPerPage;
  totalRecordsReport: number = 0;
  diagings: DIAging[] = [];
  batchno: string | null = null;
  refno: string | null = null;
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
  effDate!: Date[] | null;
  effStartDateString: string | null = null;
  effEndDateString: string | null = null;

  expDate!: Date[] | null;
  expStartDateString: string | null = null;
  expEndDateString: string | null = null;

  appDate !: Date[] | null;
  appStartDateString: string | null = null;
  appEndDateString: string | null = null;

  termDate !: Date[] | null;
  termStartDateString: string | null = null;
  termEndDateString: string | null = null;
  //date range picker 

  fileType: any =
    [{ value: 'csv', label: 'CSV' },
    { value: 'pdf', label: 'PDF' },
    { value: 'xlsx', label: 'XLSX' }]

  inclusiveTerminationOption: any =
    [
      { value: 0, label: 'No' },
      { value: 1, label: 'Yes' }
    ]

  entityTypeOption: any =
    [
      { value: 'B', label: 'Business' },
      { value: 'C', label: 'Company' },
      { value: 'I', label: 'Individual' },
      { value: 'L', label: 'LLP' },
      { value: 'all', label: 'All' }
    ]

  DITypeOption: any =
    [
      { value: 'CT-PP', label: 'COMTRAC Pre-Payment' },
      { value: 'MPSI-FS', label: 'MPSI Financing Statement' },
      { value: 'ROC-RPC', label: 'Renewal of Practicing Certificate' },
      { value: 'ROB-NB', label: 'ROB New Business' },
      { value: 'ROB-RB', label: 'ROB Renewal of Business' },
      { value: 'all', label: 'All' }
    ]

  DIStatusOption: any =
    [
      { value: 'A', label: 'Amortized' },
      { value: 'C', label: 'Completed' },
      { value: 'R', label: 'Recognized' },
      { value: 'T', label: 'Terminated' },
      { value: 'all', label: 'All' }
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
  permReport = perm.Reporting_and_Analysis_Deferred_Income_Aging

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

    this.receiptDate = this.formatDate(new Date());
    this.incTermStatus = 0;
    this.entityType = [];
    this.entityName = null;
    this.DIType = [];
    this.DIStatus = [];
    this.emailNotification = 0;
    this.effDate = [sixMonthsAgo, today];
    this.expDate = null;
    this.appDate = null;
    this.termDate = null;
    this.batchno = null;
    this.refno = null;
    this.selectedFileType = 'pdf';
  }

  onKeyDown(event: KeyboardEvent): void {
    if (event.key === 'Backspace') {
      return;
    }
    // Prevent manual key entry
    event.preventDefault();
  }

  isEffectiveDateInvalid(): boolean {

    if (!this.effDate || this.effDate.length !== 2) {
      return false; // No value or incomplete range, so it's not invalid
    }

    const startDate = new Date(this.effDate[0]);
    const endDate = new Date(this.effDate[1]);

    startDate.setHours(0, 0, 0, 0);
    endDate.setHours(0, 0, 0, 0);
    const startYear = startDate.getFullYear();
    const startMonth = startDate.getMonth();
    const endYear = endDate.getFullYear();
    const endMonth = endDate.getMonth();

    const differenceMonths = (endYear - startYear) * 12 + (endMonth - startMonth);

    if (differenceMonths > 5) {
      this.effectiveInValid = true;
    }
    else {
      this.effectiveInValid = false;
    }
    return this.effectiveInValid;
  }

  //form handle before submit start
  async handleFormSubmit(form: NgForm) {

    let formValidation: boolean | null = false;

    formValidation = form.valid
    //&& !this.effectiveInValid

    if (formValidation) {
      await this.insertDIAging()
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
          this.permListAllow = this.permReportAllow.includes(perm.Reporting_and_Analysis_Deferred_Income_Aging) ? 1 : 0;
          if (this.permListAllow === 0) {
            console.log("access-denied");
            this.router.navigate(['/access-denied']);
            return; // Exit the function to prevent further execution
          }
        }
      );
  }

  async insertDIAging(): Promise<void> {

    // let invalidDIAging = await this.checkPendingAndInProgessDIAging();

    // if (!invalidDIAging) {

    const insertURL = environment.apiUrl + '/api/rrareport/v1/insertdiagingrpt';
    // const insertURL = environment.apiUrl + '/api/rrareport/v1/fds';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json'
    });

    this.receiptDateEndString = this.datepipe.transform(this.receiptDate, 'yyyy-MM-dd') || null;

    if (this.effDate && this.effDate.length > 0) {
      this.effDateFromString = this.datepipe.transform(this.effDate[0], 'yyyy-MM-dd');
      this.effDateToString = this.datepipe.transform(this.effDate[1], 'yyyy-MM-dd');
    }

    if (this.expDate && this.expDate.length > 0) {
      this.expDateFromString = this.datepipe.transform(this.expDate[0], 'yyyy-MM-dd');
      this.expDateToString = this.datepipe.transform(this.expDate[1], 'yyyy-MM-dd');
    }

    if (this.appDate && this.appDate.length > 0) {
      this.appDateFromString = this.datepipe.transform(this.appDate[0], 'yyyy-MM-dd');
      this.appDateToString = this.datepipe.transform(this.appDate[1], 'yyyy-MM-dd');
    }

    if (this.termDate && this.termDate.length > 0) {
      this.termDateFromString = this.datepipe.transform(this.termDate[0], 'yyyy-MM-dd');
      this.termDateToString = this.datepipe.transform(this.termDate[1], 'yyyy-MM-dd');
    }

    const body: any = {
      i_p_tmn_status: this.incTermStatus
    };

    if (this.receiptDate) {
      body.i_p_dt_req = this.receiptDateEndString;
    }


    if (this.entityType !== null && this.entityType.length > 0) {
      body.i_p_ent_ty = this.entityType; //only ng select dropdown need this
    }


    if (this.entityName && this.entityName.trim()) {
      body.i_p_ent_nm = this.entityName;
    }


    if (this.DIType !== null && this.DIType.length > 0) {
      body.i_p_txn_ty = this.DIType; //only ng select dropdown need this
    }

    if (this.DIStatus !== null && this.DIStatus.length > 0) {
      body.i_p_status = this.DIStatus; //only ng select dropdown need this
    }

    if (this.expDateFromString) {
      body.i_p_dt_exp_fr = this.expDateFromString;
    }

    if (this.expDateToString) {
      body.i_p_dt_exp_to = this.expDateToString;
    }

    if (this.effDateFromString) {
      body.i_p_dt_eff_fr = this.effDateFromString;
    }

    if (this.effDateToString) {
      body.i_p_dt_eff_to = this.effDateToString;
    }

    if (this.appDateFromString) {
      body.i_p_dt_app_fr = this.appDateFromString;
    }

    if (this.appDateToString) {
      body.i_p_dt_app_to = this.appDateToString;
    }

    if (this.termDateFromString) {
      body.i_p_dt_tmn_fr = this.termDateFromString;
    }

    if (this.termDateToString) {
      body.i_p_dt_tmn_to = this.termDateToString;
    }

    body.i_status = 'P'
    body.i_p_file_type = this.selectedFileType;
    body.i_p_file_size = null;
    body.p_file_nm = null

    if (this.batchno && this.batchno.trim()) {
      body.i_p_batch_no = this.batchno;
    }

    if (this.refno && this.refno.trim()) {
      body.i_p_fms_ref_no = this.refno;
    }

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
    // }
  }


  loadData() {

    this.isDisplayReport = true;
    this.isLoadingReport = true;

    const url = environment.apiUrl + '/api/rrareport/v1/getdiaginglistingrpt';

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
          this.diagings = response.data;
          this.isLoadingReport = false;
          this.totalRecordsReport = response.data[0].total;
        }

        if (this.diagings !== null && this.diagings.length > 0) {
          if (this.diagings[0].status === 'P')
            this.disableSubmitButton = true;
          else
            this.disableSubmitButton = false;
        }
        else
          this.disableSubmitButton = false;
      },
      (error) => {
        console.error('There was an error retrieving the di aging:', error);
        this.isLoadingReport = false;
      }
    );
  }

  onDIStatusChange(newStatus: string) {
    if (newStatus === 'T') {
      this.incTermStatus = 1;
    }
  }

  formatDateForReportName(dateString: string): string {
    const formattedDate = this.datepipe.transform(dateString, 'yyyyMMdd');
    return formattedDate ? formattedDate : '';
  }

  // downloadFile(rpt_di_age_id: number) {

  //   const generateURL = environment.apiUrl + '/api/rrareport/v1/generatediagingrpt';

  //   // Set your authorization header
  //   const headers = new HttpHeaders({
  //     Authorization: environment.authKey,
  //     'Content-Type': 'application/json'
  //   });

  //   // Create the request body with your form data
  //   var requestBody: { [k: string]: any } = {
  //     i_rpt_di_age_id: rpt_di_age_id
  //   };

  //   var fileTypeMime = this.selectedFileType == 'pdf' ? 'application/pdf' : this.selectedFileType == 'xlsx' ? 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' : this.selectedFileType == 'csv' ? 'text/csv' :
  //     'application/octet-stream';

  //   this.http.post(generateURL, requestBody, { observe: 'response', responseType: 'blob', headers: headers })
  //     .subscribe(response => {
  //       var blob = new Blob([response.body as Blob], { type: fileTypeMime });
  //       saveAs(blob, response.headers.get('content-disposition')!.split('filename=')[1]);
  //     });
  // }

  downloadFile(file_nm: string, file_ext: string) {

    const generateURL = environment.apiUrl + '/api/rrareport/v1/downloaddiagingrpt';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json'
    });

    // Create the request body with your form data
    var requestBody: { [k: string]: any } = {
      i_file_name: file_nm,
      i_report_format: file_ext
    };

    var fileTypeMime = file_ext == 'pdf' ? 'application/pdf' : file_ext == 'xlsx' ? 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' : file_ext == 'csv' ? 'text/csv' :
      'application/octet-stream';

    this.http.post(generateURL, requestBody, { observe: 'response', responseType: 'blob', headers: headers })
      .subscribe(response => {
        var blob = new Blob([response.body as Blob], { type: fileTypeMime });
        saveAs(blob, response.headers.get('content-disposition')!.split('filename=')[1]);
      });
  }


  async cancelFile(rpt_di_age_id: number): Promise<void> {

    // let invalidCancelDIAging = await this.checkIsStillPendingById(rpt_di_age_id);

    // if (!invalidCancelDIAging) {

    const updURL = environment.apiUrl + '/api/rrareport/v1/updatediagingrpt';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json'
    });

    // Create the request body with your form data
    var requestBody: { [k: string]: any } = {
      i_rpt_di_age_id: rpt_di_age_id,
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
    // }
  }

  async checkPendingAndInProgessDIAging(): Promise<boolean> {

    const url = environment.apiUrl + '/api/rrareport/v1/getdiagequeuerpt';

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
  async checkIsStillPendingById(rpt_di_age_id: number): Promise<boolean> {

    const url = environment.apiUrl + '/api/rrareport/v1/getpendingdiagingrptbyid';

    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    const body: any = {
      i_rpt_di_age_id: rpt_di_age_id
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

  validateEffDate() {

    if (this.effDate && this.effDate.length > 0 && (this.effDate[0] === undefined || this.effDate[1] === undefined)) { // is undefined when backspace to clear the date
      this.effDate = null;
    }
  }

  validateExpDate() {
    //  console.log("exp date is " + this.expDate)
    //  if(this.expDate !== null && this.expDate !== undefined){
    //   console.log("exp date 0 is " + this.expDate[0])
    //   console.log("exp date 1 is " + this.expDate[1])
    //   console.log("exp date lenght is " + this.expDate.length)
    //  }

    if (this.expDate && this.expDate.length > 0 && (this.expDate[0] === undefined || this.expDate[1] === undefined)) { // is undefined when backspace to clear the date
      this.expDate = null;
    }
  }

  validateAppDate() {

    if (this.appDate && this.appDate.length > 0 && (this.appDate[0] === undefined || this.appDate[1] === undefined)) { // is undefined when backspace to clear the date
      this.appDate = null;
    }
  }

  validateTermDate() {

    if (this.termDate && this.termDate.length > 0 && (this.termDate[0] === undefined || this.termDate[1] === undefined)) { // is undefined when backspace to clear the date
      this.termDate = null;
    }
  }









}
