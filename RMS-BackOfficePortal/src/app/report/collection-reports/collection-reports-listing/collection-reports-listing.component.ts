import { Component } from '@angular/core';
import { AuthService } from 'src/app/core/services/auth.service';
import { perm } from 'src/permissions/perm';

@Component({
  selector: 'app-collection-reports-listing',
  templateUrl: './collection-reports-listing.component.html',
  styleUrls: ['./collection-reports-listing.component.scss']
})
export class CollectionReportsListingComponent {
  permReport =
    // perm.Reporting_and_Analysis_Bank_In_Slip + "," +
    // perm.Reporting_and_Analysis_OTC_Collection_By_Fee_Detail_ID + "," +
    // perm.Reporting_and_Analysis_OTC_Collection + "," +
    // perm.Reporting_and_Analysis_OTC_Receipt_Cancellation + "," +

    // perm.Reporting_and_Analysis_Master_Balancing + "," +
    // perm.Reporting_and_Analysis_Daily_Balancing + "," +
    // perm.Reporting_and_Analysis_OTC_Returned_Cheque + "," +
    // perm.Reporting_and_Analysis_Counter_Collection
    
    perm.Reporting_and_Analysis_Daily_Collection_Listing+ "," +
    perm.Reporting_and_Analysis_View_Payment_Collection_Report_Fee_Detail_ID+ "," +
    perm.Reporting_and_Analysis_View_Payment_Collection_Report_Payment_Mode+ "," +
    perm.Reporting_and_Analysis_View_Payment_Collection_Report_Source_System
    
    
    ;
  // all the perm_cd for this module seperated with comma

  permReportAllow = ""; // variable to store allowed permission for the user
  permViewDailyCollectionReport: number = 0;
  permViewPaymentCollectionFD: number = 0;
  permViewPaymentCollectionPM: number = 0;
  permViewPaymentCollectionSS: number = 0;




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
          this.permViewDailyCollectionReport = this.permReportAllow.includes(perm.Reporting_and_Analysis_Daily_Collection_Listing) ? 1 : 0;
          this.permViewPaymentCollectionFD = this.permReportAllow.includes(perm.Reporting_and_Analysis_View_Payment_Collection_Report_Fee_Detail_ID) ? 1 : 0;
          this.permViewPaymentCollectionPM = this.permReportAllow.includes(perm.Reporting_and_Analysis_View_Payment_Collection_Report_Payment_Mode) ? 1 : 0;
          this.permViewPaymentCollectionSS = this.permReportAllow.includes(perm.Reporting_and_Analysis_View_Payment_Collection_Report_Source_System) ? 1 : 0;

          this.reportList = [
            { permission: this.permViewDailyCollectionReport, label: 'labels.dailycollectionlisting', route: '/daily-collection-listing' },
            { permission: this.permViewPaymentCollectionFD, label: 'labels.paymentcollection(groupbyfeedetailid)', route: '/payment-collection-fee-dt-id' },
            { permission: this.permViewPaymentCollectionPM, label: 'labels.paymentcollection(groupbypaymentmode)', route: '/payment-collection-pymt-md' },
            { permission: this.permViewPaymentCollectionSS, label: 'labels.paymentcollection(groupbysourcesystem)', route: '/payment-collection-s-s' }
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
