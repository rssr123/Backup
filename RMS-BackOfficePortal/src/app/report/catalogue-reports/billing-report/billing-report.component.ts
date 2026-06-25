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
import { ParamService } from '../../../core/services/param.service';

@Component({
  selector: 'app-billing-report',
  templateUrl: './billing-report.component.html',
  styleUrls: ['./billing-report.component.scss']
})
export class BillingReportComponent implements OnInit{

  dlUrl = environment.apiUrl + '/api/blr/v1/billingReport';

  selectedFileType: String | null = 'pdf';
  fileType: any = [
    { value: 'csv', label: 'CSV' },
    { value: 'pdf', label: 'PDF' },
    { value: 'xlsx', label: 'XLSX' }
  ];

  billingDateRange!: Date[] | null;
  billingCategory: string | null = null;
  // emailStatus: string | null = null;
  paymentStatus: string | null = null;
  billingNo: string | null = null;
  entityCustomerID: string | null = null;
  billingStatuses: any[] = [];
  billingStatus: string | null = null;


  //billingCategories = ['Billing', 'Non-Billing'];
  billingCategories = [
    { label: 'Billing', value: 'B' },
    { label: 'Non-Billing', value: 'NB' },
  ];
  // emailStatuses = ['Email Sent', 'Email Expired', 'Email Failed to Send'];
  // paymentStatuses = ['Payment in Process', 'Pending Payment', 'Paid', 'Fail'];
  //paymentStatuses = ['Payment in Process', 'Pending Payment', 'Paid', 'Fail','Email Sent', 'Email Expired', 'Email Failed to Send'];

  // paymentStatuses = [
  //   { label: 'Payment in Process', value: 'PIP' },
  //   { label: 'Pending Payment', value: 'PP' },
  //   { label: 'Paid', value: 'P' },
  //   { label: 'Fail', value: 'F' },
  //   { label: 'Email Sent', value: 'ES' },
  //   { label: 'Email Pending', value: 'EP' },
  //   { label: 'Email Expired', value: 'EE' },
  //   { label: 'Email Failed to Send', value: 'EFS' }
  // ];

  paymentStatuses: any[] = [];

    // Configuring Permissions for User and roles variables
    permList = perm.Reporting_and_Analysis_Billing 
    permListResponse = "";
    permViewAllow: number = 0; // if 0 then not allow to view listing page, else allow
    permActionAllow: number = 0;
    authorization() {
      this.authService.checkUserRole(this.authService.username, this.permList)
      .subscribe(
        (response: any) => {
          this.permListResponse = response.data;
          this.permViewAllow = this.permListResponse.includes(perm.Reporting_and_Analysis_Billing) ? 1 : 0;
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

  constructor(private http: HttpClient, config: NgbPaginationConfig, private router: Router, 
    private dataService: DataService, private authService: AuthService, public datepipe: DatePipe,
    private ParamService: ParamService,) {
    config.maxSize = 3;
    config.boundaryLinks = true;
  }

  ngOnInit() {
    this.resetWithDefaults();
    this.authorization();
    //this.loadPaymentStatus();
    this.loadBillingStatus()
  }

  resetWithDefaults(){
    this.reset();
  }

  datePreset(){
    this.billingDateRange = null;
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

    this.billingDateRange = [start, end];
  }

  reset() {
		this.datePreset();
    this.billingCategory = null;
   // this.paymentStatus= null;
    this.billingNo = null;
    this.entityCustomerID = null;
    this.billingStatus = null;
	}

  dlbuttonPerms(): boolean {
    return !this.billingDateRange || this.billingDateRange.length !== 2 || this.dateCheck();
  }

  dateCheck() {
    if (this.billingDateRange?.length === 2) {
      const startDate = new Date(this.billingDateRange[0]);
      const endDate = new Date(this.billingDateRange[1]);
      
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

  // validateBillingDateRange(): boolean {
  //   if (!this.billingDateRange || this.billingDateRange.length !== 2) {
  //     return false;
  //   }
  //   const start = new Date(this.billingDateRange[0]);
  //   const end = new Date(this.billingDateRange[1]);
  //   return (end.getTime() - start.getTime()) <= 31536000000; // 12 months in milliseconds
  // }

  // downloadReport() {
  //   var requestBody: {[k: string]: any} = {
  //     i_date_range_start: this.datepipe.transform(this.billingDateRange?.[0] ?? new Date(), 'yyyy-MM-dd') + ' 00:00:00',
  //     i_date_range_end: this.datepipe.transform(this.billingDateRange?.[1] ?? new Date(), 'yyyy-MM-dd') + ' 23:59:59',
  //     i_billing_category: this.billingCategory.value,
  //     // i_email_status: this.emailStatus,
  //     i_payment_status: this.paymentStatus,
  //     i_billing_no: this.billingNo,
  //     i_entity_customer_id: this.entityCustomerID,
  //     i_report_format: this.selectedFileType
  //   };

    downloadReport() {

      var requestBody: {[k: string]: any} = {
      i_date_range_start: this.datepipe.transform(this.billingDateRange?.[0] ?? new Date(), 'yyyy-MM-dd') + ' 00:00:00',
      i_date_range_end: this.datepipe.transform(this.billingDateRange?.[1] ?? new Date(), 'yyyy-MM-dd') + ' 23:59:59',
      i_billing_category: this.billingCategory,
      //i_payment_status: this.paymentStatus,
      i_payment_status: this.billingStatus,
      i_billing_no: this.billingNo,
      i_entity_customer_id: this.entityCustomerID,
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


   loadPaymentStatus() {
    this.ParamService.getStates(environment.DefaultPage.toString(), environment.ItemPerPage.toString(), '', 'OrderStatus').subscribe((response: any) => {
      if (response.data.length >= 0) {
        // this.states = response.data as ParamData[]; later
        this.paymentStatuses = response.data as any[];
        this.paymentStatuses.sort((a, b) => a.nm_en.localeCompare(b.nm_en)); // sorting
      }
      else
        console.error('Invalid response format:', response);
    },
      (error) => {
        console.error('There was an error retrieving the status:', error);
      }
    );
  }

  loadBillingStatus() {
    this.ParamService.getStates(
      environment.DefaultPage.toString(),
      environment.ItemPerPage.toString(),
      '',
      'Billing-Status'
    ).subscribe(
      (response: any) => {
        if (response.data && Array.isArray(response.data)) {
          this.billingStatuses = (response.data as { param_cd: string, nm_en: string }[])
            .filter((item) => ['U', 'P'].includes(item.param_cd))
            .sort((a, b) => a.nm_en.localeCompare(b.nm_en));
        } else {
          console.error('Invalid response billing format:', response);
        }
      },
      (error) => {
        console.error('There was an error retrieving the billing status:', error);
      }
    );
  }
}
