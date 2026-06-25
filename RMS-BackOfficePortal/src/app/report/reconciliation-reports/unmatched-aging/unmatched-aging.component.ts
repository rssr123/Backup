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
import { UmAging } from 'src/app/core/models/umaging.interface';
import { fadeInOut } from 'src/app/shared/animation';
import { perm } from 'src/permissions/perm';

@Component({
  selector: 'app-unmatched-aging',
  templateUrl: './unmatched-aging.component.html',
  styleUrls: ['./unmatched-aging.component.scss'],
  animations: [fadeInOut]
})
export class UnmatchedAgingComponent implements OnInit {
  months: String[] = ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"];
  
  receiptDate: string | null = null;
  reconStatus: string | null = null;
  receiptNo: string | null = null;
  pgTxnId: string | null = null;
  stmtNo: string | null = null;
  stmtDate!: Date[];
  stmtStartDateString: string | null = null;
  stmtEndDateString: string | null = null;
  showDupTrns: number | null = 0;
  emailNotification: number | null = 0;
  isDisplayReport: boolean = false;
  isLoadingReport: boolean = false;
  pageReport = environment.DefaultPage;
  itemsPerPageReport = environment.ItemPerPage;
  totalRecordsReport: number = 0;
  umagings: UmAging[] = [];

  selectedFileType: string = 'pdf'; // Default value

  //default pagination
  selectedValue = environment.dropdownOptions[0];
  dropdownOptions = environment.dropdownOptions;

  LoadData(singleItem: number) {
    this.selectedValue = singleItem;
    this.itemsPerPageReport = this.selectedValue;
    this.loadData();
  }

  fileType: any =
    [{ value: 'csv', label: 'CSV' },
    { value: 'pdf', label: 'PDF' },
    { value: 'xlsx', label: 'XLSX' }]

  reconStatusOption: any =
    [
      { value: 'matched', label: 'Matched' },
      { value: 'unmatched', label: 'Unmatched' },
      { value: 'all', label: 'All' }
      ]

  emailOption: any =
    [
      { value: 0, label: 'No' },
      { value: 1, label: 'Yes' }
    ]

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
  permReport = perm.Reporting_and_Analysis_Unmatched_Aging

  permReportAllow = ""; // variable to store allowed permission for the user
  permListAllow: number = 0; // if 0 then not allow to view listing page, else allow

  alphaNumberOnly (e : any) {
    var regex = new RegExp("^[a-zA-Z0-9]+$");
    var str = String.fromCharCode(!e.charCode ? e.which : e.charCode);
    if (regex.test(str)) {
        return true;
    }

    e.preventDefault();
    return false;
  }

  constructor(private http: HttpClient, config: NgbPaginationConfig, private router: Router, private dataService: DataService, private authService: AuthService, public datepipe: DatePipe) {
    config.maxSize = 3;
    config.boundaryLinks = true;
  }

  ngOnInit() {
    this.waitForElm('.btnAdd').then((elm) => {
      this.loadData();
    });
    this.resetWithDefaults();
    this.loadPermission();
  }

  formatDate(date: Date): string {
    const day = ('0' + date.getDate()).slice(-2);
    const month = date.toLocaleString('default', { month: 'short' });
    const year = date.getFullYear();
    return `${day} ${month} ${year}`;
  }

  resetWithDefaults() {
    this.receiptDate = this.formatDate(new Date());
    this.reconStatus = 'unmatched';
    this.receiptNo = null;
    this.pgTxnId = null;
    this.stmtNo = null;
    this.stmtDate = [];
    this.stmtStartDateString = null;
    this.stmtEndDateString = null;
    this.showDupTrns = 0;

    this.emailNotification = 0;
    this.selectedFileType = 'pdf';
  }

  onKeyDown(event: KeyboardEvent): void {
    if (event.key === 'Backspace') {
      return;
    }
    // Prevent manual key entry
    event.preventDefault();
  }

  //form handle before submit start
  async handleFormSubmit(form: NgForm) {

    if(this.selectedFileType == null || this.selectedFileType.length < 1)
      return;

    if(this.umagings !== null && this.umagings.length > 0){
          if(this.umagings[0].status !== 'P')
            await this.insertUmAging()
    }
    else
      await this.insertUmAging()

    /*
    let formValidation: boolean | null = false;

    formValidation = form.valid;  //PG Settlement Date Range html tag causing issue
    //formValidation = true;

    alert(formValidation);
    if (formValidation) {
      await this.insertUmAging()
    } else {
      Object.values(form.controls).forEach((control) => {
        control.markAsTouched();
      });
    }
    */
  }

  convertDateString(dateS: Date): string{
    var date = new Date(dateS.toString());
    return ("0" + date.getDate()).slice(-2) + ' ' + this.months[date.getMonth()] + ' ' + date.getFullYear() + ' ' +
     ("0" + date.getHours()).slice(-2)  + ':' + ("0" + date.getMinutes()).slice(-2) + ':' + ("0" + date.getSeconds()).slice(-2);
  }

  loadPermission() {
    this.authService.checkUserRole(this.authService.username, this.permReport)
      .subscribe(
        (response: any) => {
          this.permReportAllow = response.data;
          this.permListAllow = this.permReportAllow.includes(perm.Reporting_and_Analysis_Unmatched_Aging) ? 1 : 0;
          if (this.permListAllow === 0) {
            console.log("access-denied");
            this.router.navigate(['/access-denied']);
            return; // Exit the function to prevent further execution
          }
        }
      );
  }

  async insertUmAging(): Promise<void> {

    const insertURL = environment.apiUrl + '/api/report/v1/unmatched_aging/req_report';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json'
    });

    this.receiptDate = this.datepipe.transform(this.receiptDate, 'yyyy-MM-dd') || null;

    if (this.stmtDate && this.stmtDate.length > 0) {
      this.stmtStartDateString = this.datepipe.transform(this.stmtDate[0], 'yyyy-MM-dd');
      this.stmtEndDateString = this.datepipe.transform(this.stmtDate[1], 'yyyy-MM-dd');
    }

    const body: any = {
      i_req_date: this.receiptDate,
      i_recon_status: this.reconStatus,
      i_dup_flag: this.showDupTrns,
      i_email: this.emailNotification,
      i_file_type: this.selectedFileType,
      i_rcpt_no: '',
      i_pg_txn_id: '',
      i_rc_pg_stmt_no: '',
      i_stmt_dt_fr: '',
      i_stmt_dt_to: ''
    };

    if (this.receiptNo !== null && this.receiptNo.trim() !== '') {
      body.i_rcpt_no = this.receiptNo;
    }

    if (this.pgTxnId !== null && this.pgTxnId.trim() !== '') {
      body.i_pg_txn_id = this.pgTxnId;
    }

    if (this.stmtNo !== null && this.stmtNo.trim() !== '') {
      body.i_rc_pg_stmt_no = this.stmtNo;
    }

    if (this.stmtStartDateString !== null && this.stmtStartDateString.trim() !== ''){
      body.i_stmt_dt_fr = this.stmtStartDateString;
    }

    if (this.stmtEndDateString !== null && this.stmtEndDateString.trim() !== '') {
      body.i_stmt_dt_to = this.stmtEndDateString;
    }

    try {
      (<HTMLButtonElement>document.getElementById('submit'))!.disabled = true;
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

    const urlMftWF = environment.apiUrl + '/api/report/v1/unmatched_aging/listing';

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


    this.http.post(urlMftWF, Body, { headers }).subscribe(
      (response: any) => {
        if (response.data.length === 0) {
          this.totalRecordsReport = 0;
          this.isDisplayReport = false
          this.isLoadingReport = false;
        }
        else {
          this.umagings = response.data.data;
          this.isLoadingReport = false;
          this.totalRecordsReport = response.data.total;
        }

        if(this.umagings !== null && this.umagings.length > 0){
          if(this.umagings[0].status === 'P')
            (<HTMLButtonElement>document.getElementById('submit'))!.disabled = true;
          else
            (<HTMLButtonElement>document.getElementById('submit'))!.disabled = false;
        }
        else
            (<HTMLButtonElement>document.getElementById('submit'))!.disabled = false;
      },
      (error) => {
        console.error('There was an error retrieving the Unmatched aging flow:', error);
        this.isLoadingReport = false;
        // Handle errors here
      }
    );
  }

  formatDateForReportName(dateString: string): string {
    const formattedDate = this.datepipe.transform(dateString, 'yyyyMMdd');
    return formattedDate ? formattedDate : '';
  }

  getReportName(item: any): string {
    //const receiptDate = this.formatDateForReportName(item.p_dt_req);
    //return `Um_Aging_${receiptDate}.${item.p_file_type}`;
    //return `Um_Aging_${receiptDate}`;
    //return item.p_file_nm.split('.')[0];
    return item.p_file_nm;
  }


  downloadFile(file_nm: string, file_ext: string) {

    const generateURL = environment.apiUrl + '/api/report/v1/aging/download_report';

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


  async cancelFile(rpt_um_age_id: number, event: any): Promise<void>{
    event.target.disabled = true;
    const updURL = environment.apiUrl + '/api/report/v1/unmatched_aging/cancel';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json'
    });

    // Create the request body with your form data
    var requestBody: { [k: string]: any } = {
      i_req_id: rpt_um_age_id
    };

    try {
      const response: any = await this.http.post(updURL, requestBody, { headers }).toPromise();
      if (response.header.statusCode === '00') {
        this.showCancelAlertBox();
        this.loadData();
      } else {
        console.log("error")
      }
    } catch (error) {
      console.error(error);
    }
  }

  waitForElm(selector: any) {
    return new Promise(resolve => {
        if (document.querySelector(selector)) {
            return resolve(document.querySelector(selector));
        }

        const observer = new MutationObserver(mutations => {
            if (document.querySelector(selector)) {
                observer.disconnect();
                resolve(document.querySelector(selector));
            }
        });

        // If you get "parameter 1 is not of type 'Node'" error, see https://stackoverflow.com/a/77855838/492336
        observer.observe(document.body, {
            childList: true,
            subtree: true
        });
    });
  }
}
