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
  selector: 'app-matched-transaction-listing',
  templateUrl: './matched-transaction-listing.component.html',
  styleUrls: ['./matched-transaction-listing.component.scss']
})
export class MatchedTransactionListingComponent implements OnInit {

  reportName = 'mat_trans_lst_report';
  dlUrl = `${environment.apiUrl}/api/rrareport/v1/${this.reportName}`;

  showResultAlert = false;
  ornNo: string | null = null;
  pgPaymentID: string | null = null;
  pgTransID: string | null = null;
  receiptNo: string | null = null;
  statementNo: string | null = null;
  paymentInValid: boolean = true;

  //date range picker
  pymtSubmitDt!: Date[] | null;
  startDateString: string | null = null;
  endDateString: string | null = null;
  //date range picker 

  fileType: any =
    [{ value: 'csv', label: 'CSV' },
    { value: 'pdf', label: 'PDF' },
    { value: 'xlsx', label: 'XLSX' }]

  selectedFileType: string = 'pdf'; // Default value

  // Configuring Permissions for User and roles variables
  permReport = perm.Reporting_and_Analysis_Matched_Transaction_Listing

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

    if (this.pymtSubmitDt && this.pymtSubmitDt.length > 0) {
      this.startDateString = this.datepipe.transform(this.pymtSubmitDt[0], 'yyyy-MM-dd');
      this.endDateString = this.datepipe.transform(this.pymtSubmitDt[1], 'yyyy-MM-dd');
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
      i_orn_no: this.ornNo,
      i_pg_pymt_id: this.pgPaymentID,
      i_pg_txn_id: this.pgTransID,
      i_rcpt_no: this.receiptNo,
      i_stmt_no: this.statementNo,
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
    const twoDaysAgo = new Date();
    twoDaysAgo.setDate(today.getDate() - 1);

    this.pymtSubmitDt = [twoDaysAgo, today];
    this.ornNo = null;
    this.pgPaymentID = null;
    this.pgTransID = null;
    this.receiptNo = null;
    this.statementNo = null;
    this.selectedFileType = 'pdf';
  }

  isPaymentSubmmitedDateInvalid(): boolean {

    if (!this.pymtSubmitDt || this.pymtSubmitDt.length !== 2) {
      return false; // No value or incomplete range, so it's not invalid
    }

    const startDate = new Date(this.pymtSubmitDt[0]);
    const endDate = new Date(this.pymtSubmitDt[1]);

    startDate.setHours(0, 0, 0, 0);
    endDate.setHours(0, 0, 0, 0);
    const differenceMs: number = +endDate - +startDate;
    const differenceDays: number = differenceMs / (1000 * 60 * 60 * 24);

    if (differenceDays > 1) {
      this.paymentInValid = true;
    }
    else {
      this.paymentInValid = false;
    }
    return this.paymentInValid;
  }



  //form handle before submit start
  async handleFormSubmit(form: NgForm) {

    let formValidation: boolean | null = false;

    formValidation = form.valid
    // && !this.paymentInValid

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
          this.permListAllow = this.permReportAllow.includes(perm.Reporting_and_Analysis_Matched_Transaction_Listing) ? 1 : 0;
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

  validatePymtSubmitDt() {

    if (this.pymtSubmitDt &&  this.pymtSubmitDt.length>0 && (this.pymtSubmitDt[0] === undefined || this.pymtSubmitDt[1] === undefined)) {  // is undefined when backspace to clear the date
      this.pymtSubmitDt = null;
    }
  }


}
