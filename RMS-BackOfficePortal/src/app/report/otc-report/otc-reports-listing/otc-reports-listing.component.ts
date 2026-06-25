import { Component } from '@angular/core';
import { AuthService } from 'src/app/core/services/auth.service';
import { perm } from 'src/permissions/perm';

@Component({
  selector: 'app-otc-reports-listing',
  templateUrl: './otc-reports-listing.component.html',
  styleUrls: ['./otc-reports-listing.component.scss']
})
export class OtcReportsListingComponent {


  permReport =
    perm.Reporting_and_Analysis_Bank_In_Slip + "," +
    perm.Reporting_and_Analysis_OTC_Collection_By_Fee_Detail_ID + "," +
    perm.Reporting_and_Analysis_OTC_Collection + "," +
    perm.Reporting_and_Analysis_OTC_Receipt_Cancellation + "," +

    perm.Reporting_and_Analysis_Master_Balancing + "," +
    perm.Reporting_and_Analysis_Daily_Balancing + "," +
    perm.Reporting_and_Analysis_OTC_Returned_Cheque + "," +
    perm.Reporting_and_Analysis_Counter_Collection;
  // all the perm_cd for this module seperated with comma

  permReportAllow = ""; // variable to store allowed permission for the user
  permViewBankInSlipReport: number = 0;
  permViewCounterCollectionReport: number = 0;
  permViewDailyBalancingReport: number = 0;
  permViewMasterBalancingReport: number = 0;
  permViewOTCCollectionReport: number = 0;
  permViewOTCCollectionPlusReport: number = 0;
  permViewOTCReceiptCancellationReport: number = 0;
  permViewOTCReturnedChequeReport: number = 0;




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
          this.permViewBankInSlipReport = this.permReportAllow.includes(perm.Reporting_and_Analysis_Bank_In_Slip) ? 1 : 0;
          this.permViewCounterCollectionReport = this.permReportAllow.includes(perm.Reporting_and_Analysis_OTC_Collection_By_Fee_Detail_ID) ? 1 : 0;
          this.permViewDailyBalancingReport = this.permReportAllow.includes(perm.Reporting_and_Analysis_Daily_Balancing) ? 1 : 0;
          this.permViewMasterBalancingReport = this.permReportAllow.includes(perm.Reporting_and_Analysis_Master_Balancing) ? 1 : 0;
          this.permViewOTCCollectionReport = this.permReportAllow.includes(perm.Reporting_and_Analysis_OTC_Collection) ? 1 : 0;
          this.permViewOTCCollectionPlusReport = this.permReportAllow.includes(perm.Reporting_and_Analysis_OTC_Collection_By_Fee_Detail_ID) ? 1 : 0;
          this.permViewOTCReceiptCancellationReport = this.permReportAllow.includes(perm.Reporting_and_Analysis_OTC_Receipt_Cancellation) ? 1 : 0;
          this.permViewOTCReturnedChequeReport = this.permReportAllow.includes(perm.Reporting_and_Analysis_OTC_Returned_Cheque) ? 1 : 0;

          this.reportList = [
            { permission: this.permViewCounterCollectionReport, label: 'labels.otccollectionreportbypaymentmode', route: '/otc-collection-report' },
            { permission: this.permViewOTCCollectionPlusReport, label: 'labels.otccollectionreportbyfeedetailid', route: '/otc-collection-plus-report' },
            { permission: this.permViewOTCReturnedChequeReport, label: 'labels.otcreturnedchequereport', route: '/otc-returned-cheque-report' },
            { permission: this.permViewCounterCollectionReport, label: 'labels.countercollectionreport', route: '/counter-collection-report' },
            { permission: this.permViewDailyBalancingReport, label: 'labels.dailybalancingreport', route: '/daily-balancing-report' },
            { permission: this.permViewMasterBalancingReport, label: 'labels.masterbalancingreport', route: '/master-balancing-report' },
            { permission: this.permViewBankInSlipReport, label: 'labels.bankinslipreport', route: '/bank-in-slip-report' },
            { permission: this.permViewOTCReceiptCancellationReport, label: 'labels.otcreceiptcancellationreport', route: '/otc-receipt-cancellation-report' }
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
