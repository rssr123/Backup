import { Component, OnInit, Output, EventEmitter } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { environment } from 'src/environments/environment';
import { NgbPaginationConfig } from '@ng-bootstrap/ng-bootstrap';
import { MFT, MFTWF, MasterTaskList, Param, SourceSystemCode, User } from '../../../core/models/entity';
import { Router } from '@angular/router';
import { trigger, state, style, transition, animate } from '@angular/animations';
import { Systemstatus } from '../../../shared/enums/systemstatus';
import moment from 'moment';
import { DataService } from '../../../core/services/data.service';
import { fadeInOut } from '../../../shared/animation';
import { DatePipe, formatDate } from '@angular/common';
import { saveAs } from "file-saver";
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { catchError } from 'rxjs/operators';
import { perm } from 'src/permissions/perm';
import { AuthService } from 'src/app/core/services/auth.service';

import { BranchCodeCounterList } from 'src/app/core/models/branch-code-counter-list.interface';
import { ParamList } from 'src/app/core/models/param-list.interface';

@Component({
  selector: 'app-otc-returned-cheque-report',
  templateUrl: './otc-returned-cheque-report.component.html',
  styleUrls: ['./otc-returned-cheque-report.component.scss']
})
export class OtcReturnedChequeReportComponent {
  dlUrl = environment.apiUrl + '/api/otcreport/v1/otcreturnedcheque';
  
  selectedFileType: String | null = 'pdf';
  fileType: any = [
    { value: 'csv', label: 'CSV' },
    { value: 'pdf', label: 'PDF' },
    { value: 'xlsx', label: 'XLSX' }
  ];

  branches: BranchCodeCounterList[] = [];
  nonBillingStatuses: ParamList[] = [];

  dateRange: Date[] | null = null;
  branchCode: String | null = null;
  receiptNo: String | null = null;
  chequeNo: String | null = null;
  customerName: String | null = null;
  entityNo: String | null = null;
  nonBillingNo: String | null = null;
  nonBillingStatus: String | null = null;

  showResultAlert = false;

  permList = perm.Reporting_and_Analysis_OTC_Returned_Cheque
  permListResponse = "";
  permViewAllow: number = 0; // if 0 then not allow to view listing page, else allow
  permActionAllow: number = 0;

  authorization() {
    this.authService.checkUserRole(this.authService.username, this.permList)
    .subscribe(
      (response: any) => {
        this.permListResponse = response.data;
        this.permViewAllow = this.permListResponse.includes(perm.Reporting_and_Analysis_OTC_Returned_Cheque) ? 1 : 0;

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
  
    showResultAlertBox() {
      this.showResultAlert = true;
      setTimeout(() => this.showResultAlert = false, 2000);
    }
  
    constructor(private http: HttpClient, config: NgbPaginationConfig, private router: Router, private dataService: DataService, private authService: AuthService, public datepipe: DatePipe) {
      config.maxSize = 3;
      config.boundaryLinks = true;
    }
  
    ngOnInit() {
      this.resetWithDefaults();
      this.authorization();
      this.loadBranches();
      this.loadNonBillingStatuses();
    }
  
    resetWithDefaults(){
      this.reset();
    }
  
    datePreset(){
      this.dateRange = null;
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
  
      this.dateRange = [start, end];
    }
  
    reset() {
      this.datePreset();
      this.branchCode = null;
      this.receiptNo = null;
      this.chequeNo = null;
      this.customerName = null;
      this.entityNo = null;
      this.nonBillingNo = null;
      this.nonBillingStatus = null;
    }
  
    dlbuttonPerms(){
      if (
        (this.dateRange === null)
        || (this.dateCheck())
      )
          return true;
      return false;
    }
  
    // date range maximum 31 days
    dateCheck() {
      if (this.dateRange?.length === 2) {
        const startDate = new Date(this.dateRange[0]);
        const endDate = new Date(this.dateRange[1]);
        const diffTime = Math.abs(endDate.getTime() - startDate.getTime());
        const diffDays = diffTime / (1000 * 3600 * 24);
        if (diffDays > 31) {
          return true;
        }
      }
      return false;
    }
  
    dlReport() {
  
      var requestBody: {[k: string]: any} = {
        i_date_from: this.datepipe.transform(this.dateRange?.[0] ?? new Date(), 'yyyy-MM-dd') + ' 00:00:00',
        i_date_to: this.datepipe.transform(this.dateRange?.[1] ?? new Date(), 'yyyy-MM-dd') + ' 23:59:59',
        i_branch_code: this.branchCode,
        i_receipt_no: this.receiptNo,
        i_cheque_no: this.chequeNo,
        i_customer_name: this.customerName,
        i_entity_no: this.entityNo,
        i_non_billing_no: this.nonBillingNo,
        i_non_billing_status: this.nonBillingStatus,
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
  
    loadBranches() {
      const headers = new HttpHeaders({
        Authorization: environment.authKey,
        'Content-Type': 'application/json',
      });
  
      const url2 = environment.apiUrl + '/api/helper/v1/getbranchcodecounterlist';
  
      const body2 = {
        i_status: null
      }
  
      this.http.post(url2, body2, { headers }).subscribe(
        (response: any) => {
          this.branches = response.data;
        },
        (error) => {
          console.error(error);
        }
      )
    }
  
    loadNonBillingStatuses() {
      const headers = new HttpHeaders({
        Authorization: environment.authKey,
        'Content-Type': 'application/json',
      });
  
      const url3 = environment.apiUrl + '/api/helper/v1/getparamlist';
  
      const body3 = {
        i_status: null,
        i_param_grp_nm: "Non-BillingStatus"
      }
  
      this.http.post(url3, body3, { headers }).subscribe(
        (response: any) => {
          this.nonBillingStatuses = response.data.filter((item: { param_cd: string; }) => item.param_cd !== 'N');
        },
        (error) => {
          console.error(error);
        }
      )
    }
}
