import { Component } from '@angular/core';
import { AuthService } from 'src/app/core/services/auth.service';
import { perm } from 'src/permissions/perm';

@Component({
  selector: 'app-catalogue-reports-listing',
  templateUrl: './catalogue-reports-listing.component.html',
  styleUrls: ['./catalogue-reports-listing.component.scss']
})
export class CatalogueReportsListingComponent {

  permReport =
      // perm.Reporting_and_Analysis_Bank_In_Slip + "," +
      // perm.Reporting_and_Analysis_OTC_Collection_By_Fee_Detail_ID + "," +
      // perm.Reporting_and_Analysis_OTC_Collection + "," +
      // perm.Reporting_and_Analysis_OTC_Receipt_Cancellation + "," +
  
      // perm.Reporting_and_Analysis_Master_Balancing + "," +
      // perm.Reporting_and_Analysis_Daily_Balancing + "," +
      // perm.Reporting_and_Analysis_OTC_Returned_Cheque + "," +
      // perm.Reporting_and_Analysis_Counter_Collection;
    // all the perm_cd for this module seperated with comma

    perm.Reporting_and_Analysis_Billing + "," +
    perm.Reporting_and_Analysis_Catalogue_Product_Service + "," +
    perm.Reporting_and_Analysis_Detailed_Billing_by_Billing_Type + "," +
    perm.Reporting_and_Analysis_Detailed_Billing_by_Class_ID  + "," +
    perm.Reporting_and_Analysis_Summary_Billing_by_Class_ID  
    ;
    

  
    permReportAllow = ""; // variable to store allowed permission for the user
    permViewBillingReport: number = 0;
    permViewCatalogueProductServiceReport: number = 0;
    permViewDetailedBillingBTReport: number = 0;
    permViewDetailedBillingCIDReport: number = 0;
    permViewSummaryBillingCIDReport: number = 0;
  
  
  
  
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
            this.permViewBillingReport = this.permReportAllow.includes(perm.Reporting_and_Analysis_Billing) ? 1 : 0;
            this.permViewCatalogueProductServiceReport = this.permReportAllow.includes(perm.Reporting_and_Analysis_Catalogue_Product_Service) ? 1 : 0;
            this.permViewDetailedBillingBTReport = this.permReportAllow.includes(perm.Reporting_and_Analysis_Detailed_Billing_by_Billing_Type) ? 1 : 0;
            this.permViewDetailedBillingCIDReport = this.permReportAllow.includes(perm.Reporting_and_Analysis_Detailed_Billing_by_Class_ID) ? 1 : 0;
            this.permViewSummaryBillingCIDReport = this.permReportAllow.includes(perm.Reporting_and_Analysis_Summary_Billing_by_Class_ID) ? 1 : 0;
  
            this.reportList = [
              { permission: this.permViewBillingReport, label: 'labels.billingreport', route: '/billing-report' },
              { permission: this.permViewCatalogueProductServiceReport, label: 'labels.catalogueproductservicereport', route: '/catalogue-product-service-report' },
              { permission: this.permViewDetailedBillingBTReport, label: 'labels.detailedbillingreportbybillingtype', route: '/detailed-billing-bt-report' },
              { permission: this.permViewDetailedBillingCIDReport, label: 'labels.detailedbillingreportbyclassid', route: '/detailed-billing-report' },
              { permission: this.permViewSummaryBillingCIDReport, label: 'labels.summarybillingreportbyclassid', route: '/summary-billing-report' }
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
