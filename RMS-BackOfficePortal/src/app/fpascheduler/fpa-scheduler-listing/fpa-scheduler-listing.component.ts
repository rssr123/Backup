import { HttpClient, HttpHeaders } from '@angular/common/http';
import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { NgbPaginationConfig } from '@ng-bootstrap/ng-bootstrap';
import { TranslateService } from '@ngx-translate/core';
import { FPAScheduler } from 'src/app/core/models/fpa-scheduler.interface';
import { AuthService } from 'src/app/core/services/auth.service';
import { PerformanceService } from 'src/app/core/services/performance.service';
import { GlobalService } from 'src/app/shared/global.service';
import { environment } from 'src/environments/environment';
import { perm } from 'src/permissions/perm';
import { ParamData } from 'src/app/core/models/param.interface';
import { ParamService } from '../../core/services/param.service';
import { fadeInOut } from '../../shared/animation';
import { Systemstatus } from '../../shared/enums/systemstatus';
// import { FPASchedulerUpdateComponent } from '../fpa-scheduler-update/fpa-scheduler-update.component';

@Component({
  selector: 'app-fpa-scheduler-listing',
  templateUrl: './fpa-scheduler-listing.component.html',
  styleUrls: ['./fpa-scheduler-listing.component.scss'],
  animations: [fadeInOut],
})
export class FPASchedulerListingComponent implements OnInit {
  page = environment.DefaultPage;
  itemsPerPage = environment.ItemPerPage;
  model: FPAScheduler[] = [];
  totalRecords: number = 0;

  rowNumber: number | null = null;
  jobName: String | null = null;
  // modifiedBy: String | null = null;

  // Configuring Permissions for User and roles
  // rmb chg
  permFPAScheduluer = perm.View_Financial_Post_Accounting_Schedulers_Page + "," + perm.Retrying_Scheduler;

  permFPAScheduluerAllow = "";

  // chg bck to 0
  permListAllow: number = 0;
  permRetryAllow: number = 0;
  // end configuration

  // chg back to false
  isDisplay: boolean = false;
  isLoading: boolean = false;

  //date range picker
  selected: Date[] | null = null; //{ start?: moment.Moment; end?: moment.Moment };
  bsValue = new Date();
  tempDate!: Date;
  minDate = new Date();
  //date range picker

  //toogle start
  rightSectionCollapsed: boolean = true;

  //default pagination
  selectedValue = environment.dropdownOptions[0];
  dropdownOptions = environment.dropdownOptions;

  states: ParamData[] = [];
  selectedState: string = Systemstatus.Active;

  checkResult: number = 0;

  LoadData(singleItem: number) {
    this.selectedValue = singleItem;
    this.itemsPerPage = this.selectedValue;
    this.loadData();
  }

  toggleRightSection() {
    this.rightSectionCollapsed = !this.rightSectionCollapsed;
  }
  //toogle end

  //for alert box start
  showUpdateAlert = false;

  showUpdateAlertBox() {
    this.showUpdateAlert = true;
    setTimeout(() => (this.showUpdateAlert = false), 2000);
  }

  showResultAlert = false;

  showResultAlertBox() {
    this.showResultAlert = true;
    setTimeout(() => (this.showResultAlert = false), 2000);
  }

  showDeactiveAlert = false;

  showDeactiveAlertBox() {
    this.showDeactiveAlert = true;
    setTimeout(() => (this.showDeactiveAlert = false), 2000);
  }

  showGenericAlert = false;

  showGenericAlertBox() {
    this.showGenericAlert = true;
    setTimeout(() => (this.showGenericAlert = false), 2000);
  }
  //for alert box end

  constructor(
    private http: HttpClient,
    config: NgbPaginationConfig,
    private router: Router,
    public dialog: MatDialog,
    private ParamService: ParamService,
    private cd: ChangeDetectorRef,
    private translate: TranslateService,
    private globalService: GlobalService,
    private performanceService: PerformanceService,
    private authService: AuthService
  ) {
    config.maxSize = environment.PaginationMaxSize;
    config.boundaryLinks = true;
    this.translate.setDefaultLang(this.globalService.getGlobalValue()); // default language
    this.translate.use(this.globalService.getGlobalValue());
  }

  ngOnInit(): void {
    //this.selected = new Date();
    this.bsValue = new Date();
    this.minDate.setMonth(this.bsValue.getMonth() - 1);
    this.selected = null;

    this.performanceService.measurePerformanceAsync(
      () => this.loadStates(),
      'loadStates'
    );
    //load data must put at last
    this.performanceService.measurePerformanceAsync(
      () => this.loadData(),
      'loadData'
    );
  }

  //loadData Start
  loadData() {
    this.isDisplay = true;

    const permUrl = environment.apiUrl + '/api/RPC/v1/checkuserrole';

    this.authService.checkUserRole(this.authService.username, this.permFPAScheduluer)
      .subscribe(
        (response: any) => {
          this.permFPAScheduluerAllow = response.data;

          this.permListAllow = this.permFPAScheduluerAllow.includes(perm.View_Financial_Post_Accounting_Schedulers_Page) ? 1 : 0;
          this.permRetryAllow = this.permFPAScheduluerAllow.includes(perm.Retrying_Scheduler) ? 1 : 0;
          if (this.permListAllow === 0) {
            console.log("access-denied");
            this.router.navigate(['/access-denied']);
            return; // Exit the function to prevent further execution
          }
          this.isLoading = true;
          const url = environment.apiUrl + '/api/fpascheduler/v1/getfpascheduler';

          // Set your authorization header
          const headers = new HttpHeaders({
            Authorization: environment.authKey,
            'Content-Type': 'application/json',
          });

          const Body: any = {
            i_job_name: this.jobName || undefined
          };

          console.log('Request Body:', Body);

          this.http.post(url, Body, { headers }).subscribe(
            (response: any) => {
              this.model = response.data;

              if (response.data.length == 0) {
                this.totalRecords = 0;
                this.isDisplay = true;
                // this.showResultAlertBox();
                this.isLoading = false;
              } else {
                this.totalRecords = response.data[0].total;
                this.isLoading = false;
              }
              console.log(response.data);
            },
            (error) => {
              console.error(error);
              this.isLoading = false;

              this.showGenericAlertBox();
            }
          );
        },
        (error) => {
          console.error(error);
        }
      );
  }
  //loadData End

  // retrySelected Start
  retrySelected(jobName: string): void {
    const headers = new HttpHeaders({
      'Content-Type': 'application/json'
    });
    const url = environment.apiUrl + '/api/scheduler/trigger';
    const Body: any = {
      job_name: jobName || undefined
    };
    console.log("RETRY payload", Body);
    console.log("RETRY url", url);
    try {
      this.http.post(url, Body, { headers });
      console.log("RETRY SUCCESS")
    } catch (error) {
      console.log("RETRY ERROR", error)
    }

    this.authService.checkUserRole(this.authService.username, this.permFPAScheduluer)
      .subscribe(
        (response: any) => {
          this.permFPAScheduluerAllow = response.data;
          // rmb chg
          this.permListAllow = this.permFPAScheduluerAllow.includes(perm.FMS_Account_Code_View_Page) ? 1 : 0;
          if (this.permListAllow === 0) {
            console.log("access-denied");
            this.router.navigate(['/access-denied']);
            return; // Exit the function to prevent further execution
          }
          const url = environment.apiUrl + '/api/scheduler/trigger';

          const headers = new HttpHeaders({
            Authorization: environment.authKey,
            'Content-Type': 'application/json',
          });

          const Body: any = {
            job_name: jobName || undefined
          };

          this.isLoading = true;

          console.log('Request Body:', Body);

          this.http.post(url, Body, { headers }).subscribe(
            (response: any) => {
              console.log(response.data);
              this.isLoading = false;
            },
            (error) => {
              console.error(error);
              this.isLoading = false;

              this.showGenericAlertBox();
            }
          );
        },
        (error) => {
          console.error(error);
        }
      );
  }
  // retrySelected End

  apply(): void {
    this.loadData();
    this.rightSectionCollapsed = true;
  }

  reset(): void {
    this.jobName = null;
    this.loadData();
  }

  refreshMainPage(): void {
    this.page = 1;
    this.loadData();
  }

  loadStates() {
    this.ParamService.getStates('1', '100', '', 'Status').subscribe(
      (response: any) => {
        if (response && response.data && Array.isArray(response.data)) {
          //this.states = response.data as ParamData[];
          this.states.push({
            param_cd: '',
            nm_en: 'All',
            nm_bm: 'All',
            total: 5,
          }); //add 'All' options
          //this.states.push(response.data);
          this.states = [...this.states, ...response.data];
          //this.states.sort((a, b) => a.nm_en.localeCompare(b.nm_en)); // sorting
        } else {
          console.error('Invalid response format:', response);
        }
      },
      (error) => {
        console.error('There was an error retrieving the status:', error);
      }
    );
  }

  //toggle action
  async toggleActivation(item: any) {
    this.isDisplay = true;
    this.isLoading = true;
    this.checkResult = 0;
    // Toggle the local status for a responsive UI
    if (item.status === Systemstatus.Active) {
      item.status = Systemstatus.Delete;
    } else {
      item.status = Systemstatus.Active;
    }
  }
  //toggle action
}
