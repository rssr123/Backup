import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { environment } from 'src/environments/environment';
import { NgbPaginationConfig } from '@ng-bootstrap/ng-bootstrap';
import { Router } from '@angular/router';
import { DataService } from 'src/app/core/services/data.service';
import { AuthService } from 'src/app/core/services/auth.service';
import { DatePipe, formatDate } from '@angular/common';
import { saveAs } from "file-saver";
import { NgForm } from '@angular/forms';
import { perm } from 'src/permissions/perm';
import { UnmatchTransMonth, ExportUnmatchTransMonth } from '../../../../core/models/unmatchtrans';

import { fadeInOut } from 'src/app/shared/animation';


@Component({
  selector: 'app-ut-listing-transactions',
  templateUrl: './ut-listing-transactions.component.html',
  styleUrls: ['./ut-listing-transactions.component.scss'],
  animations: [fadeInOut]
})
export class UtListingTransactionsComponent implements OnInit {

  reportName = 'un_trans_lst_report';
  dlUrl = `${environment.apiUrl}/api/rrareport/v1/${this.reportName}`;

  showResultAlert = false;
  settDate: string | null = null;
  ornNo: string | null = null;
  subCriteria: any;
  rcptNo: string | null = null;
  pgTxnId: string | null = null;
  stmtNo: string | null = null;
  checkDup: any;

  periodKey: Date | null = null;

  totalRecords: number = 0;

  totalMFTRecords: number = 0;
  isDisplay: boolean = false;
  isLoadingReport: boolean = false;
  errorMessagesAccessDenied: string[] = [];
  errorAccessDenied: boolean = false;
  invalidInputFrom: boolean = false;
  invalidInputTo: boolean = false;

  utlm: UnmatchTransMonth[] = [];

  // Configuring Permissions for User and roles variables
  permReport = perm.Reporting_and_Analysis_Unmatched_Transaction_Listing

  permReportAllow = ""; // variable to store allowed permission for the user
  permListAllow: number = 0; // if 0 then not allow to view listing page, else allow



  fileType: any =
    [{ value: 'pdf', label: 'PDF' },
    { value: 'csv', label: 'CSV' },
    { value: 'xlsx', label: 'XLSX' }]
  selectedFileType: string = 'pdf'; // Default value

  pgReconciliationStatus: any =
    [{ value: 0, label: 'All' },//All
    { value: 1, label: 'Matched' }, //SAM
    { value: 2, label: 'Unmatched' }]//SNM,NFR,TXF

  selectedPgReconciliationStatus: string = 'SNM,NFR,TXF'; // Default value



  showDuplicatedTransactions: any =
    [{ value: 1, label: 'Yes' },
    { value: 0, label: 'No' }]



  

  constructor(private http: HttpClient, config: NgbPaginationConfig, private router: Router, private dataService: DataService, private authService: AuthService, public datepipe: DatePipe) {
    config.maxSize = 3;
    config.boundaryLinks = true;
  }

  ngOnInit() {
    this.resetWithDefaults();
    this.loadPermission();
    this.settDate = history.state.dummydate;

    // this.periodKey = new Date();
    const dummydate = history.state.dummydate;

    if (dummydate) {
      this.periodKey = new Date(dummydate);
      this.periodKey.setHours(0, 0, 0, 0);
    }


    console.log("periodKey is " + this.periodKey);

    this.loadPermission();

  }

  loadData() {

    this.isLoadingReport = true;
    const url = environment.apiUrl + '/api/utl/v1/getunmatchtransday';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json'
    });


    const Body: any = {

    };

    if (this.periodKey) {
      Body.i_period_key = formatDate(this.periodKey, 'YYYY-MM-dd', 'en');
    }



    

    this.http.post(url, Body, { headers }).subscribe(
      (response: any) => {

        if (response.data.length === 0) {
          this.totalRecords = 0;
          this.isDisplay = false;
          this.isLoadingReport = false;
        } else {
          this.utlm = response.data;
          this.totalRecords = response.data[0].total;
          this.isLoadingReport = false;
          this.isDisplay = true;
        }
      },
      (error) => {
        console.error(error);
        this.isDisplay = false;
        this.isLoadingReport = false;
      }
    );

  }

  loadPermission() {
    this.authService.checkUserRole(this.authService.username, this.permReport)
      .subscribe(
        (response: any) => {
          this.permReportAllow = response.data;
          this.permListAllow = this.permReportAllow.includes(perm.Reporting_and_Analysis_Unmatched_Transaction_Listing) ? 1 : 0;
          if (this.permListAllow === 0) {
            console.log("access-denied");
            this.router.navigate(['/access-denied']);
            return; // Exit the function to prevent further execution
          }
        }
      );
  }



  refreshPage(): void {
    window.location.reload();
  }

  get today() {
    return new Date();
  }

  apply(): void {
    this.loadData();
    this.refreshPage;
  }

  dlReport() {



    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json'
    });

    // Create the request body with your form data
    var requestBody: { [k: string]: any } = {
      i_settlement_date: this.periodKey ? formatDate(this.periodKey, 'YYYY-MM-dd', 'en') : null,
      i_orn_no: this.ornNo,
      i_sub_criteria: this.subCriteria || null,
      i_rcpt_no: this.rcptNo,
      i_pg_txn_id: this.pgTxnId,
      i_stmt_no: this.stmtNo,

      i_check_duplicate: this.checkDup || null,

      i_report_name: this.reportName,
      i_report_format: this.selectedFileType


    };

    console.log(requestBody);



    var fileTypeMime = this.selectedFileType == 'pdf' ? 'application/pdf' : this.selectedFileType == 'xlsx' ? 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' : this.selectedFileType == 'csv' ? 'text/csv' :
      'application/octet-stream';

    this.http.post(this.dlUrl, requestBody, { observe: 'response', responseType: 'blob', headers: headers })
      .subscribe(response => {
        var blob = new Blob([response.body as Blob], { type: fileTypeMime });
        saveAs(blob, response.headers.get('content-disposition')!.split('filename=')[1]);
      });
  }

  resetWithDefaults() {
    // Currently empty, intended for resetting form to default state
  }

  reset(): void {

    this.ornNo = null;
    this.subCriteria = null;
    this.rcptNo = null;
    this.pgTxnId = null;
    this.stmtNo = null;

    //need to add more later
  }



  //form handle before submit start
  async handleFormSubmit(form: NgForm) {
    let formValidation: boolean | null = false;
    formValidation = form.valid;

    if (formValidation) {
      this.dlReport();
    } else {
      Object.values(form.controls).forEach((control) => {
        control.markAsTouched();
      });
    }
  }


}
