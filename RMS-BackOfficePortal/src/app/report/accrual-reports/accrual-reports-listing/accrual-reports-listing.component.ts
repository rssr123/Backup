import { Component } from '@angular/core';
import { AuthService } from 'src/app/core/services/auth.service';
import { perm } from 'src/permissions/perm';

@Component({
  selector: 'app-accrual-reports-listing',
  templateUrl: './accrual-reports-listing.component.html',
  styleUrls: ['./accrual-reports-listing.component.scss']
})
export class AccrualReportsListingComponent {

  permReport =
    perm.Reporting_and_Analysis_Deferred_Income_Aging + "," +
    perm.Reporting_and_Analysis_RICP_Aging + "," +
    perm.Reporting_and_Analysis_RIPL_Aging

    ;

  permReportAllow = ""; // variable to store allowed permission for the user
  permViewDIAgingReport: number = 0;
  permViewRICPAgingReport: number = 0;
  permViewRIPLAgingReport: number = 0;

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
          this.permViewDIAgingReport = this.permReportAllow.includes(perm.Reporting_and_Analysis_Deferred_Income_Aging) ? 1 : 0;
          this.permViewRICPAgingReport = this.permReportAllow.includes(perm.Reporting_and_Analysis_RICP_Aging) ? 1 : 0;
          this.permViewRIPLAgingReport = this.permReportAllow.includes(perm.Reporting_and_Analysis_RIPL_Aging) ? 1 : 0;

          this.reportList = [
            { permission: this.permViewDIAgingReport, label: 'menu.deferredincomeaging', route: '/deferred-income-aging' },
            { permission: this.permViewRICPAgingReport, label: 'labels.receivableincomeperiodiclodgmentaging', route: '/ripl-aging-report' },
            { permission: this.permViewRIPLAgingReport, label: 'labels.receivableincomecompoundaging', route: '/ricp-aging' },
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
