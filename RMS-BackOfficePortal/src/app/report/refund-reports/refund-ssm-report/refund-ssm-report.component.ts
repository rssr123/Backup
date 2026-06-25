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
  selector: 'app-refund-ssm-report',
  templateUrl: './refund-ssm-report.component.html',
  styleUrls: ['./refund-ssm-report.component.scss']
})
export class RefundSsmReportComponent implements OnInit{

  dlUrl = environment.apiUrl + '/api/rs/v1/refundSummaryStatus';

  selectedFileType: String | null = 'pdf';
  fileType: any = [
    { value: 'csv', label: 'CSV' },
    { value: 'pdf', label: 'PDF' },
    { value: 'xlsx', label: 'XLSX' }
  ];

  refundDateRange!: Date[] | null;
  refundStatus: string | null = null;
  refundType: string | null = null;
  division: string | null = null;

  refundStatuses = [
    { value: 'PFA', label: 'Pending Finance Admin' },
    { value: 'PBYM', label: 'Pending BYM' },
    { value: 'RS', label: 'Refund Submitted' },
    { value: 'RR', label: 'Refund Rejected' },
    { value: 'BE', label: 'Bank Error' },
    { value: 'RE', label: 'Refund Expired' },
    { value: 'AE', label: 'Attempts Exceeded' },
    { value: 'RG', label: 'Refund Slip Generated' },
    { value: 'PPG', label: 'Pending PG' },
    { value: 'PSME', label: 'Pending SME' }
];

  refundTypes = [
    { value: 'RS01', label: 'Refund Slip 01' },
    { value: 'RS02', label: 'Refund Slip 02' },
    { value: 'DA', label: 'Direct Appeal' },
    { value: 'CB', label: 'Chargeback' }
  ];

  divisions = ['Division 1', 'Division 2', 'Division 3', 'Division 4'];

    // Configuring Permissions for User and roles variables
    permList = perm.Reporting_and_Analysis_Refund_Summary_Status 
    permListResponse = "";
    permViewAllow: number = 0; // if 0 then not allow to view listing page, else allow
    permActionAllow: number = 0;
    authorization() {
      this.authService.checkUserRole(this.authService.username, this.permList)
      .subscribe(
        (response: any) => {
          this.permListResponse = response.data;
          this.permViewAllow = this.permListResponse.includes(perm.Reporting_and_Analysis_Refund_Summary_Status) ? 1 : 0;
          console.log("AuthList: " + this.permViewAllow);
          if (this.permViewAllow === 0) {
            console.log("access-denied");
            this.router.navigate(['/access-denied']);
            return;
          }
          console.log("AuthResp: " + this.permViewAllow);
        },
        (error: any) => {
          console.log(error);
        }
      );
    }

	constructor(private http: HttpClient, config: NgbPaginationConfig, private router: Router, private dataService: DataService, private authService: AuthService, public datepipe: DatePipe) {
		config.maxSize = 3;
		config.boundaryLinks = true;
	}

  ngOnInit() {
		this.resetWithDefaults();
    this.authorization();
  }

  resetWithDefaults(){
		this.reset();
	}

  datePreset(){
    this.refundDateRange = null;
    const today = new Date();

    const start = new Date(today.getFullYear(), today.getMonth() - 1, today.getDate(), 0, 0, 0);
    if (start.getMonth() < 0) {
      start.setFullYear(start.getFullYear() - 1);
      start.setMonth(11);
    }
    if (!this.dateCheck()) {
      start.setDate(start.getDate() + 1);
    }
    const end = new Date(today.getFullYear(), today.getMonth(), today.getDate(), 23, 59, 59);

    this.refundDateRange = [start, end];
  }

  reset() {
		this.datePreset();
    this.refundStatus = null;
    this.refundType = null;
    this.division = null;
	}

  dlbuttonPerms(): boolean {
    return !this.refundDateRange || this.refundDateRange.length !== 2 || this.dateCheck();
  }

  dateCheck() {
    if (this.refundDateRange?.length === 2) {
      const startDate = new Date(this.refundDateRange[0]);
      const endDate = new Date(this.refundDateRange[1]);
      
      // Calculate the difference in time
      const diffTime = endDate.getTime() - startDate.getTime();
      // Convert the difference in time to days
      const diffDays = diffTime / (1000 * 3600 * 24);
      
      // Check if the difference in days is more than 365 (1 year)
      if (diffDays > 365) {
        return true;
      }
    }
    return false;
  }

  // validateRefundDateRange() {
  //   if (!this.refundDateRange || this.refundDateRange.length !== 2) {
  //     return false;
  //   }
  //   const start = new Date(this.refundDateRange[0]);
  //   const end = new Date(this.refundDateRange[1]);
  //   return (end.getTime() - start.getTime()) <= 31536000000; // 12 months in milliseconds
  // }

  downloadReport() {

      var requestBody: {[k: string]: any} = {
        i_date_range_start: this.datepipe.transform(this.refundDateRange?.[0] ?? new Date(), 'yyyy-MM-dd') + ' 00:00:00',
        i_date_range_end: this.datepipe.transform(this.refundDateRange?.[1] ?? new Date(), 'yyyy-MM-dd') + ' 23:59:59',
        i_refund_status: this.refundStatus,
        i_refund_ty: this.refundType,
        i_division: this.division,
        i_report_format: this.selectedFileType
      };
  
      console.log(requestBody);
  
      var fileTypeMime = this.selectedFileType == 'pdf' ? 'application/pdf' : this.selectedFileType == 'xlsx' ? 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' : this.selectedFileType == 'csv' ? 'text/csv' : 
        'application/octet-stream';

      const headers = new HttpHeaders({
        Authorization: environment.authKey,
        'Content-Type': 'application/json'
      });

      this.http.post(this.dlUrl, requestBody, { observe: 'response', responseType: 'blob', headers: headers })
        .subscribe(response => {
          var blob = new Blob([response.body as Blob], { type: fileTypeMime });
          saveAs(blob, response.headers.get('content-disposition')!.split('filename=')[1]);
        });
    }

  onKeyDown(event: KeyboardEvent): void {
    if (event.key === 'Backspace') {
      return;
    }
    event.preventDefault();
  }
}


