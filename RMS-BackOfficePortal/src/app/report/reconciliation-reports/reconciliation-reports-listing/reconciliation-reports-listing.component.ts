import { Component } from '@angular/core';
import { AuthService } from 'src/app/core/services/auth.service';
import { perm } from 'src/permissions/perm';


@Component({
  selector: 'app-reconciliation-reports-listing',
  templateUrl: './reconciliation-reports-listing.component.html',
  styleUrls: ['./reconciliation-reports-listing.component.scss']
})
export class ReconciliationReportsListingComponent {


    permReport =
      // perm.Reporting_and_Analysis_Bank_In_Slip + "," +
      // perm.Reporting_and_Analysis_OTC_Collection_By_Fee_Detail_ID + "," +
      // perm.Reporting_and_Analysis_OTC_Collection + "," +
      // perm.Reporting_and_Analysis_OTC_Receipt_Cancellation + "," +
  
      // perm.Reporting_and_Analysis_Master_Balancing + "," +
      // perm.Reporting_and_Analysis_Daily_Balancing + "," +
      // perm.Reporting_and_Analysis_OTC_Returned_Cheque + "," +
      // perm.Reporting_and_Analysis_Counter_Collection;

      perm.Reporting_and_Analysis_Matched_Transaction_Listing + "," +
      perm.Reporting_and_Analysis_PG_Settlement_Disbursement_Listing + "," +
      perm.Reporting_and_Analysis_Unmatched_Aging+ "," +
      perm.Reporting_and_Analysis_Unmatched_Transaction_Listing

      ;

    // all the perm_cd for this module seperated with comma
  
    permReportAllow = ""; // variable to store allowed permission for the user
    permViewUnmatchedTransactionReport: number = 0;
    permViewMatchedTransactionReport: number = 0;
    permViewPGSettlementDisbursementReport: number = 0;
    permViewUnmatchedAgingReport: number = 0;
    
  
  
  
  
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
            this.permViewUnmatchedTransactionReport = this.permReportAllow.includes(perm.Reporting_and_Analysis_Matched_Transaction_Listing) ? 1 : 0;
            this.permViewMatchedTransactionReport = this.permReportAllow.includes(perm.Reporting_and_Analysis_PG_Settlement_Disbursement_Listing) ? 1 : 0;
            this.permViewPGSettlementDisbursementReport = this.permReportAllow.includes(perm.Reporting_and_Analysis_Unmatched_Aging) ? 1 : 0;
            this.permViewUnmatchedAgingReport = this.permReportAllow.includes(perm.Reporting_and_Analysis_Unmatched_Transaction_Listing) ? 1 : 0;
  
            this.reportList = [
              { permission: this.permViewUnmatchedTransactionReport, label: 'labels.unmatchedtransactionlisting', route: '/ut-listing-months' },
              { permission: this.permViewMatchedTransactionReport, label: 'labels.mactchedtransactionlisting', route: '/matched-transaction-listing' },
              { permission: this.permViewPGSettlementDisbursementReport, label: 'labels.pgsettlementdisbursementlisting', route: '/pg-settlement-disbursement-listing' },
              { permission: this.permViewUnmatchedAgingReport, label: 'labels.unmatchedaging', route: '/unmatched-aging' }
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
