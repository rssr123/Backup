import { Component } from '@angular/core';
import { AuthService } from 'src/app/core/services/auth.service';
import { perm } from 'src/permissions/perm';

@Component({
  selector: 'app-refund-reports-listing',
  templateUrl: './refund-reports-listing.component.html',
  styleUrls: ['./refund-reports-listing.component.scss']
})
export class RefundReportsListingComponent {
  permReport =
      

      perm.Reporting_and_Analysis_Refund_Aging + "," +
      perm.Reporting_and_Analysis_Refund_Summary_Status + "," +
      perm.Reporting_and_Analysis_Refund_Status_Detailed
      ;
  
    permReportAllow = ""; // variable to store allowed permission for the user
    permViewRefundAgingReport: number = 0;
    permViewRefundSummaryStatusReport: number = 0;
    permViewRefundStatusDetailedReport: number = 0;
  
    constructor(private authService: AuthService) {
  
    }
  
    ngOnInit() {
      this.loadPermission();
    }
  
    reportList: any[] = [];
  
  
    loadPermission() {
      this.authService.checkUserRole(this.authService.username, this.permReport)
        .subscribe(
          (response: any) => {
            this.permReportAllow = response.data;
            this.permViewRefundAgingReport = this.permReportAllow.includes(perm.Reporting_and_Analysis_Refund_Aging) ? 1 : 0;
            this.permViewRefundSummaryStatusReport = this.permReportAllow.includes(perm.Reporting_and_Analysis_Refund_Summary_Status) ? 1 : 0;
            this.permViewRefundStatusDetailedReport = this.permReportAllow.includes(perm.Reporting_and_Analysis_Refund_Status_Detailed) ? 1 : 0;
  
            this.reportList = [
              { permission: this.permViewRefundAgingReport, label: 'labels.refundagingreport', route: '/refund-aging' },
              { permission: this.permViewRefundSummaryStatusReport, label: 'labels.refundsummarystatusreport', route: '/refund-summary-status' },
              { permission: this.permViewRefundStatusDetailedReport, label: 'labels.refundstatusdetailedreport', route: '/refund-status-detailed' },
            ];
            this.reportList = this.reportList.filter(report => report.permission === 1);
  
          }
        );
    }
  
  
    counter: number = 0;
  
    getCounter() {
      return ++this.counter;
    }
  
    ngAfterViewChecked() {
      this.counter = 0; // Reset the counter after each change detection cycle
    }
}
