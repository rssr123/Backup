import { ChangeDetectorRef, Component, OnInit, ViewChild } from '@angular/core';
import { FMSAccount } from '../../core/models/fms-account.interface';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Router } from '@angular/router';
import { NgbPaginationConfig } from '@ng-bootstrap/ng-bootstrap';
import { environment } from 'src/environments/environment';
import { TranslateService } from '@ngx-translate/core';
import { GlobalService } from 'src/app/shared/global.service';
import { MatDialog } from '@angular/material/dialog';
import { Systemstatus } from '../../shared/enums/systemstatus';
import { fadeInOut } from '../../shared/animation';
import { ParamData } from 'src/app/core/models/param.interface';
import { ParamService } from '../../core/services/param.service';
import { formatDate } from '@angular/common';
import { PerformanceService } from 'src/app/core/services/performance.service';
import { perm } from 'src/permissions/perm';
import { AuthService } from 'src/app/core/services/auth.service';
import { FMSAccountUpdateComponent } from '../fms-account-update/fms-account-update.component';

@Component({
  selector: 'app-fms-account-listing',
  templateUrl: './fms-account-listing.component.html',
  styleUrls: ['./fms-account-listing.component.scss'],
  animations: [fadeInOut],
})
export class FMSAccountListingComponent implements OnInit {
  page = environment.DefaultPage;
  itemsPerPage = environment.ItemPerPage;
  model: FMSAccount[] = [];
  totalRecords: number = 0;

  fmsAcctId: number | null = null;
  acctName: String | null = null;
  acctType: String | null = null;
  acctCode: String | null = null;
  modifiedBy: String | null = null;

  // Configuring Permissions for User and roles
  permFG = perm.FMS_Account_Code_View_Page + "," + perm.FMS_Account_Code_Edit;

  permFGAllow = "";

  permListAllow: number = 0;
  permEditAllow: number = 0;
  // end configuration

  isDisplay: boolean = false;

  isLoading: boolean = false;
  //date range picker
  selected: Date[] | null = null; //{ start?: moment.Moment; end?: moment.Moment };
  bsValue = new Date();
  tempDate!: Date;
  minDate = new Date();
  //date range picker

  editBox: boolean = false;

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

  DefaultBox() {
    this.editBox = false;
  }

  AlertBoxInitialize() {
    if (this.editBox) {
      this.showUpdateAlertBox();
    }
  }

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

    this.authService.checkUserRole(this.authService.username, this.permFG)
      .subscribe(
        (response: any) => {
          this.permFGAllow = response.data;
          this.permListAllow = this.permFGAllow.includes(perm.FMS_Account_Code_View_Page) ? 1 : 0;
          this.permEditAllow = this.permFGAllow.includes(perm.FMS_Account_Code_Edit) ? 1 : 0;
          console.log(this.permListAllow, this.permEditAllow);
          if (this.permListAllow === 0) {
            console.log("access-denied");
            this.router.navigate(['/access-denied']);
            return; // Exit the function to prevent further execution
          }
          console.log(this.permListAllow, this.permEditAllow);
          this.isLoading = true;
          const url = environment.apiUrl + '/api/fmsaccount/v1/getfmsaccount';

          // Set your authorization header
          const headers = new HttpHeaders({
            Authorization: environment.authKey,
            'Content-Type': 'application/json',
          });

          const Body: any = {
            i_page: this.page.toString(),
            i_size: this.itemsPerPage.toString(),
            i_fms_acct_id: this.fmsAcctId || undefined,
            i_acct_nm: this.acctName?.trim() || undefined,
            i_acct_type: this.acctType?.trim() || undefined,
            i_acct_cd: this.acctCode?.trim() || undefined,
            i_modified_by: this.modifiedBy?.trim() || undefined,
            // i_dt_modified: this.selected ? formatDate(this.selected[0], 'YYYY-MM-dd', 'en') : undefined,
            i_status: (this.selectedState === Systemstatus.Active || this.selectedState === Systemstatus.Inactive) ? this.selectedState : undefined,
          };

          if (this.selected) {
            if (this.selected.length === 1) {
              Body.i_dt_modified = formatDate(this.selected[0], 'YYYY-MM-dd', 'en');
            } else if (this.selected.length === 2) {
              Body.i_dt_modified_fr = formatDate(this.selected[0], 'YYYY-MM-dd', 'en');
              this.selected[1].setDate(this.selected[1].getDate() + 1);
              Body.i_dt_modified_to = formatDate(this.selected[1], 'YYYY-MM-dd', 'en');
            }
          }

          console.log('Request Body:', Body);

          this.http.post(url, Body, { headers }).subscribe(
            (response: any) => {
              // console.log("API Response:", response.data);

              this.model = response.data;

              if (response.data.length == 0) {
                this.totalRecords = 0;
                this.isDisplay = true;
                // this.showResultAlertBox();
                this.isLoading = false;
              } else {
                this.totalRecords = response.data[0].total;
                this.AlertBoxInitialize();
                this.DefaultBox();
                this.isLoading = false;
              }
              console.log(response.data);
              //  console.log(this.totalRecords);
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

  //editSelected Start
  editSelected(item: any): void {
    this.DefaultBox();
    const dialogRef = this.dialog.open(FMSAccountUpdateComponent, {
      width: '50%',
      data: {
        id: item.fms_acct_id,
        name: item.acct_nm,
        type: item.acct_type,
        code: item.acct_cd,
        status: item.status,
      },
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result === 'updated') {
        this.editBox = true;
        this.isLoading = true;
        this.refreshMainPage();
      }
    });
  }
  //editSelected End

  apply(): void {
    this.loadData();
    this.rightSectionCollapsed = true;
  }

  reset(): void {
    this.fmsAcctId = null;
    this.acctName = null;
    this.acctType = null;
    this.acctCode = null;
    this.modifiedBy = null;
    this.bsValue = new Date();
    this.minDate.setMonth(this.bsValue.getMonth() - 1);
    this.selected = null;

    // this.loadData();
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

