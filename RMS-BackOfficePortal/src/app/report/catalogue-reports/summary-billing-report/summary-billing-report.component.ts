import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { environment } from 'src/environments/environment';
import { NgbPaginationConfig } from '@ng-bootstrap/ng-bootstrap';
import { Router } from '@angular/router';
import { DataService } from 'src/app/core/services/data.service';
import { AuthService } from 'src/app/core/services/auth.service';
import { DatePipe } from '@angular/common';
import { saveAs } from "file-saver";
import { perm } from 'src/permissions/perm';
import { BillingClassId } from 'src/app/core/models/biiling-issuance-by-ss.interface';
import { Systemstatus } from 'src/app/shared/enums/systemstatus';
import { ParamService } from '../../../core/services/param.service';

@Component({
  selector: 'app-summary-billing-report',
  templateUrl: './summary-billing-report.component.html',
  styleUrls: ['./summary-billing-report.component.scss']
})
export class SummaryBillingReportComponent {

  dlUrl = environment.apiUrl + '/api/blr/v1/summaryBillingReport';

  selectedFileType: String | null = 'pdf';
  fileType: any = [
    { value: 'csv', label: 'CSV' },
    { value: 'pdf', label: 'PDF' },
    { value: 'xlsx', label: 'XLSX' }
  ];

  summaryBillingDateRange!: Date[] | null;
  classIdSelection: string | null = null;
  entityCustomerId: string | null = null;
  billingStatusSelection: string | null = null;

  // classIdSelections = [
  //   { value: 'A', label: 'A - Rental' },
  //   { value: 'B', label: 'B - Supply Info Receivable' },
  //   { value: 'C', label: 'C - Corporate Program' },
  //   { value: 'M', label: 'M - Miscellaneous' },
  //   { value: 'O', label: 'O - Parking Operator' },
  //   { value: 'I', label: 'I - Integration' },
  //   { value: 'G', label: 'G - Agent Receivable' },
  //   { value: 'S', label: 'S - Staff Receivable' }
  // ];
  

  // billingStatusOptions = [
  //   { value: 'P', label: 'Paid' },
  //   { value: 'U', label: 'Unpaid' },
  //   { value: 'C', label: 'Cancelled' }
  // ];

  billingStatusOptions: any[] = [];

  // Configuring Permissions for User and roles variables
  permList = perm.Reporting_and_Analysis_Summary_Billing_by_Class_ID 
  permListResponse = "";
  permViewAllow: number = 0; // if 0 then not allow to view listing page, else allow
  permActionAllow: number = 0;
  authorization() {
    this.authService.checkUserRole(this.authService.username, this.permList)
    .subscribe(
      (response: any) => {
        this.permListResponse = response.data;
        this.permViewAllow = this.permListResponse.includes(perm.Reporting_and_Analysis_Summary_Billing_by_Class_ID) ? 1 : 0;
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
    private ParamService: ParamService) {
    config.maxSize = 3;
    config.boundaryLinks = true;
  }

  ngOnInit() {
    this.resetWithDefaults();
    this.authorization();
    this.populateclassid();
    this.loadBillingStatus(); 
  }

  resetWithDefaults(){
    this.reset();
  }

  datePreset(){
    this.summaryBillingDateRange = null;
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

    this.summaryBillingDateRange = [start, end];
  }

  reset() {
		this.datePreset();
    this.classIdSelection = null;
    this.entityCustomerId= null;
    this.billingStatusSelection = null;
	}

  // validateBillingDateRange(): boolean {
  //   if (!this.summaryBillingDateRange || this.summaryBillingDateRange.length !== 2) {
  //     return false;
  //   }
  //   const start = new Date(this.summaryBillingDateRange[0]);
  //   const end = new Date(this.summaryBillingDateRange[1]);
  //   return (end.getTime() - start.getTime()) <= 31536000000; // 12 months in milliseconds
  // }

  dlbuttonPerms(): boolean {
    return !this.summaryBillingDateRange || this.summaryBillingDateRange.length !== 2 || this.dateCheck() || !this.tempClassId || !this.billingStatusSelection;
  }

  dateCheck() {
    if (this.summaryBillingDateRange?.length === 2) {
      const startDate = new Date(this.summaryBillingDateRange[0]);
      const endDate = new Date(this.summaryBillingDateRange[1]);
      
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

  downloadReport() {
    // Validate Class ID Selection
    if (!this.tempClassId) {
      alert("Please select a Class ID before downloading the report.");
      return; // Prevent further execution
    }
  
    // Validate Billing Status Selection
    if (!this.billingStatusSelection) {
      alert("Please select a Billing Status before downloading the report.");
      return; // Prevent further execution
    }
    const selectedItem = this.classid.find(item => item.blcm_id === this.tempClassId);
    

    var requestBody: {[k: string]: any} = {
      i_date_range_start: this.datepipe.transform(this.summaryBillingDateRange?.[0] ?? new Date(), 'yyyy-MM-dd') + ' 00:00:00',
      i_date_range_end: this.datepipe.transform(this.summaryBillingDateRange?.[1] ?? new Date(), 'yyyy-MM-dd') + ' 23:59:59',
        i_class_id_selection: selectedItem?.classId,
        i_entity_customer_id: this.entityCustomerId,
        i_report_format: this.selectedFileType,
        i_billing_status: this.billingStatusSelection
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

  resetFilters() {
    this.summaryBillingDateRange = null;
    this.classIdSelection = null;
    this.entityCustomerId = null;
    this.selectedFileType = 'pdf';
  }

  onKeyDown(event: KeyboardEvent): void {
    if (event.key === 'Backspace') {
      return;
    }
    event.preventDefault();
  }

  classid: BillingClassId[] = [];
  
    tempClassId: number | null = null;
  
      page = environment.DefaultPage;
      itemsPerPage = environment.ItemPerPage;
      dropDownSize = environment.DropDownSize;
  
    async populateclassid(): Promise<void> {
    
        const url = environment.apiUrl + '/api/blc/v1/getBillingClass';
    
        // Set your authorization header
        const headers = new HttpHeaders({
          Authorization: environment.authKey,
          'Content-Type': 'application/json',
        });
    
        const Body: any = {
          i_page: this.page,
          i_size: this.dropDownSize,
          i_status: Systemstatus.Active
        };
    
        try {
          const response: any = await this.http.post(url, Body, { headers }).toPromise();
          if (response.header.statusCode === '00') {
            this.classid = response.data;
            
          } else {
            console.error('Invalid master fee table response format:', response);
          }
        } catch (error) {
          console.error('There was an error retrieving the master fee table:', error);
        }
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
        this.billingStatusOptions = (response.data as { param_cd: string, nm_en: string }[])
          .filter((item) => ['U', 'P'].includes(item.param_cd))
          .sort((a, b) => a.nm_en.localeCompare(b.nm_en));
      } else {
        console.error('Invalid response format:', response);
      }
    },
    (error) => {
      console.error('There was an error retrieving the status:', error);
    }
  );
}
  
}
