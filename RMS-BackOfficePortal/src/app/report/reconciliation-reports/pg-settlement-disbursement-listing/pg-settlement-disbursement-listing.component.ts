import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { environment } from 'src/environments/environment';
import { NgbPaginationConfig } from '@ng-bootstrap/ng-bootstrap';
import { Router } from '@angular/router';
import { DataService } from 'src/app/core/services/data.service';
import { AuthService } from 'src/app/core/services/auth.service';
import { DatePipe } from '@angular/common';
import { saveAs } from "file-saver";
import { NgForm } from '@angular/forms';
import { perm } from 'src/permissions/perm';

@Component({
  selector: 'app-pg-settlement-disbursement-listing',
  templateUrl: './pg-settlement-disbursement-listing.component.html',
  styleUrls: ['./pg-settlement-disbursement-listing.component.scss']
})
export class PgSettlementDisbursementListingComponent implements OnInit {


  reportName = 'pg_set_dis_lst_report';
  dlUrl = `${environment.apiUrl}/api/rrareport/v1/${this.reportName}`;

  showResultAlert = false;
  statementNo: string | null = null;
  transDesc: string | null = null;

  //date range picker
  dateStatement!: Date[] | null;
  pgSettlementInValid: boolean = true;
  startDateString: string | null = null;
  endDateString: string | null = null;
  //date range picker 

  selectedFileType: string = 'pdf'; // Default value

  fileType: any =
    [{ value: 'csv', label: 'CSV' },
    { value: 'pdf', label: 'PDF' },
    { value: 'xlsx', label: 'XLSX' }]

  // Configuring Permissions for User and roles variables
  permReport = perm.Reporting_and_Analysis_PG_Settlement_Disbursement_Listing

  permReportAllow = ""; // variable to store allowed permission for the user
  permListAllow: number = 0; // if 0 then not allow to view listing page, else allow


  constructor(private http: HttpClient, config: NgbPaginationConfig, private router: Router, private dataService: DataService, private authService: AuthService, public datepipe: DatePipe) {
    config.maxSize = 3;
    config.boundaryLinks = true;
  }

  ngOnInit() {
    this.resetWithDefaults();
    this.loadPermission();
  }

  dlReport() {

    if (this.dateStatement && this.dateStatement.length > 0) {
      this.startDateString = this.datepipe.transform(this.dateStatement[0], 'yyyy-MM-dd');
      this.endDateString = this.datepipe.transform(this.dateStatement[1], 'yyyy-MM-dd');
    }

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json'
    });

    // Create the request body with your form data
    var requestBody: { [k: string]: any } = {
      i_start_date: this.startDateString,
      i_end_date: this.endDateString,
      i_stmt_no: this.statementNo,
      i_txn_desc: this.transDesc,
      i_report_name: this.reportName,
      i_report_format: this.selectedFileType
    };

    var fileTypeMime = this.selectedFileType == 'pdf' ? 'application/pdf' : this.selectedFileType == 'xlsx' ? 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' : this.selectedFileType == 'csv' ? 'text/csv' :
      'application/octet-stream';

    this.http.post(this.dlUrl, requestBody, { observe: 'response', responseType: 'blob', headers: headers })
      .subscribe(response => {
        var blob = new Blob([response.body as Blob], { type: fileTypeMime });
        saveAs(blob, response.headers.get('content-disposition')!.split('filename=')[1]);
      });
  }



  resetWithDefaults() {

    const today = new Date();
    const sevenDaysAgo = new Date();
    sevenDaysAgo.setDate(today.getDate() - 6);

    this.dateStatement = [sevenDaysAgo, today];
    this.statementNo = null;
    this.transDesc = null;
    this.selectedFileType = 'pdf';
  }

  isPGSettlementStatementDateInvalid(): boolean {

    if (!this.dateStatement || this.dateStatement.length !== 2) {
      return false; // No value or incomplete range, so it's not invalid
    }

    const startDate = new Date(this.dateStatement[0]);
    const endDate = new Date(this.dateStatement[1]);

    startDate.setHours(0, 0, 0, 0);
    endDate.setHours(0, 0, 0, 0);
    const differenceMs: number = +endDate - +startDate;
    const differenceDays: number = differenceMs / (1000 * 60 * 60 * 24);

    if (differenceDays > 6) {
      this.pgSettlementInValid = true;
    }
    else {
      this.pgSettlementInValid = false;
    }
    return this.pgSettlementInValid;
  }

  //form handle before submit start
  async handleFormSubmit(form: NgForm) {

    let formValidation: boolean | null = false;

    formValidation = form.valid
    // && !this.pgSettlementInValid

    if (formValidation) {
      this.dlReport()
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
          this.permListAllow = this.permReportAllow.includes(perm.Reporting_and_Analysis_PG_Settlement_Disbursement_Listing) ? 1 : 0;
          if (this.permListAllow === 0) {
            console.log("access-denied");
            this.router.navigate(['/access-denied']);
            return; // Exit the function to prevent further execution
          }
        }
      );
  }

  onKeyDown(event: KeyboardEvent): void {
    if (event.key === 'Backspace') {
      return;
    }
    // Prevent manual key entry
    event.preventDefault();
  }


  validateDateStatement() {

    if (this.dateStatement &&  this.dateStatement.length>0 && (this.dateStatement[0] === undefined || this.dateStatement[1] === undefined)) { // is undefined when backspace to clear the date
      this.dateStatement = null;
    }
  }

}
