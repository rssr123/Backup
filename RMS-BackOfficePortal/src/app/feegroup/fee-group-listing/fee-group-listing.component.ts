import { ChangeDetectorRef, Component, OnInit, ViewChild } from '@angular/core';
import { FeeGroup } from '../../core/models/fee-group.interface';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Router } from '@angular/router';
import { NgbPaginationConfig } from '@ng-bootstrap/ng-bootstrap';
import { environment } from 'src/environments/environment';
import { TranslateService } from '@ngx-translate/core';
import { GlobalService } from 'src/app/shared/global.service';
import { MatDialog } from '@angular/material/dialog';
import { FeeGroupAddComponent } from '../fee-group-add/fee-group-add.component';
import { FeeGroupDeleteComponent } from '../fee-group-delete/fee-group-delete.component';
import { FeeGroupUpdateComponent } from '../fee-group-update/fee-group-update.component';
import { Systemstatus } from '../../shared/enums/systemstatus';
import { fadeInOut } from '../../shared/animation';
import { ParamData } from 'src/app/core/models/param.interface';
import { ParamService } from '../../core/services/param.service';
import { formatDate } from '@angular/common';
import { PerformanceService } from 'src/app/core/services/performance.service';
import { perm } from 'src/permissions/perm';
import { AuthService } from 'src/app/core/services/auth.service';
import { SourceSystemCode } from 'src/app/core/models/entity';

@Component({
  selector: 'app-fee-group-listing',
  templateUrl: './fee-group-listing.component.html',
  styleUrls: ['./fee-group-listing.component.scss'],
  animations: [fadeInOut],
})
export class FeeGroupListingComponent implements OnInit {
  page = environment.DefaultPage;
  itemsPerPage = environment.ItemPerPage;
  model: FeeGroup[] = [];
  totalRecords: number = 0;

  feeGroupId: String | null = null;
  ssCd: any[] = [];
  ssFeeGroupId: String | null = null;
  feeGroupNameEN: String | null = null;
  feeGroupNameBM: String | null = null;
  modifiedBy: String | null = null;

  // Configuring Permissions for User and roles
  permFG = perm.Fee_Group_Maintenance_View_Page + "," + perm.Fee_Group_Maintenance_Add_Item + "," + perm.Fee_Group_Maintenance_Edit;
  permFGAllow = "";
  permListAllow: number = 0;
  permAddAllow: number = 0;
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
  addBox: boolean = false;
  deleteBox: boolean = false;

  //toogle start
  rightSectionCollapsed: boolean = true;

  //default pagination
  selectedValue = environment.dropdownOptions[0];
  dropdownOptions = environment.dropdownOptions;

  states: ParamData[] = [];
  selectedState: string = Systemstatus.Active;

  checkResult: number = 0;
  sourceSystemCodeOptions: SourceSystemCode[] = [];

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
    this.addBox = false;
    this.deleteBox = false;
  }

  AlertBoxInitialize() {
    if (this.editBox) {
      this.showUpdateAlertBox();
    } else if (this.addBox) {
      this.showInsertAlertBox();
    } else if (this.deleteBox) {
      this.showDeleteAlertBox();
    }
  }

  //for alert box start
  showInsertAlert = false;

  showInsertAlertBox() {
    this.showInsertAlert = true;
    setTimeout(() => (this.showInsertAlert = false), 2000);
  }

  showUpdateAlert = false;

  showUpdateAlertBox() {
    this.showUpdateAlert = true;
    setTimeout(() => (this.showUpdateAlert = false), 2000);
  }

  showDeleteAlert = false;

  showDeleteAlertBox() {
    this.showDeleteAlert = true;
    setTimeout(() => (this.showDeleteAlert = false), 2000);
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
    private performanceService : PerformanceService,
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
    //this.selected[1].setDate(this.selected[1].getDate() + 1);
    // {
    //   start: moment().subtract(1, 'month'),
    //   end: moment(),
    // };

    this.performanceService.measurePerformanceAsync(
      () => this.loadStates(),
      'loadStates'
    );
    this.performanceService.measurePerformanceAsync(
      () => this.loadSourceSystem(),
      'loadSourceSystem'
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
        this.permListAllow = this.permFGAllow.includes(perm.Fee_Group_Maintenance_View_Page) ? 1 : 0;
        this.permAddAllow = this.permFGAllow.includes(perm.Fee_Group_Maintenance_Add_Item) ? 1 : 0;
        this.permEditAllow = this.permFGAllow.includes(perm.Fee_Group_Maintenance_Edit) ? 1 : 0;
        console.log(this.permListAllow, this.permAddAllow, this.permEditAllow);
        if (this.permListAllow === 0) {
          console.log("access-denied");
          this.router.navigate(['/access-denied']);
          return; // Exit the function to prevent further execution
        }
        console.log(this.permListAllow, this.permAddAllow, this.permEditAllow);
        this.isLoading = true;
        const url = environment.apiUrl + '/api/fg/v1/getfeegroup';
    
        // Set your authorization header
        const headers = new HttpHeaders({
          Authorization: environment.authKey,
          'Content-Type': 'application/json',
        });
    
        const Body: any = {
          i_page: this.page.toString(),
          i_size: this.itemsPerPage.toString(),
        };
    
        // if (this.feeGroupId && this.feeGroupId.trim()) {
        //   Body.i_fee_grp_id = this.feeGroupId;
        // }

        if (this.ssCd.length>0) {
          Body.i_ss_cd = this.ssCd.toString();
        }
        if (this.ssFeeGroupId && this.ssFeeGroupId.trim()) {
          Body.i_ss_fee_grp_id = this.ssFeeGroupId;
        }
    
        if (this.feeGroupNameEN && this.feeGroupNameEN.trim()) {
          Body.i_fee_grp_nm_en = this.feeGroupNameEN;
        }
    
        if (this.feeGroupNameBM && this.feeGroupNameBM.trim()) {
          Body.i_fee_grp_nm_bm = this.feeGroupNameBM;
        }
    
        if (this.modifiedBy && this.modifiedBy.trim()) {
          Body.i_modified_by = this.modifiedBy;
        }
    
        if (this.selected) {
          this.selected[0].setHours(0, 0, 0, 0);
          Body.i_dt_modified_fr = formatDate(this.selected[0], 'YYYY-MM-dd HH:mm:ss', 'en');
          this.selected[1].setHours(23, 59, 59, 999);
          Body.i_dt_modified_to = formatDate(this.selected[1], 'YYYY-MM-dd HH:mm:ss', 'en');
        }
    
        // if (this.selected && this.selected.start && this.selected.end) {
        //   Body.i_dt_modified_fr = this.selected.start.format('YYYY-MM-DD');
        //   Body.i_dt_modified_to = this.selected.end
        //     .add(1, 'day')
        //     .format('YYYY-MM-DD');
        // }
    
        let temp = '';
    
        if (
          this.selectedState.length > 0 &&
          (this.selectedState == Systemstatus.Active ||
            this.selectedState == Systemstatus.Inactive)
        ) {
          temp = this.selectedState;
        }
    
        if (temp == Systemstatus.Active || temp == Systemstatus.Inactive) {
          Body.i_status = temp;
        }
    
       // console.log(Body);
    
        this.http.post(url, Body, { headers }).subscribe(
          (response: any) => {
    
    // console.log("original data");
    // console.log(response.data);
    
    
            this.model = response.data;
          
            if (response.data.length == 0) {
              this.totalRecords = 0;
              this.isDisplay = true;
              this.showResultAlertBox();
              this.isLoading = false;
            } else {
              this.totalRecords = response.data[0].total;
              this.AlertBoxInitialize();
              this.DefaultBox();
              this.isLoading = false;
            }
            // console.log(response.data);
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
    const dialogRef = this.dialog.open(FeeGroupUpdateComponent, {
      width: '50%',
      data: {
        id: item.fee_grp_id,
        en: item.fee_grp_nm_en,
        bm: item.fee_grp_nm_bm,
        ss_cd: item.ss_cd,
        ss_fee_grp_id: item.ss_fee_grp_id,
        status: item.status,
      },
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result === 'updated') {
        this.editBox = true;
      }
      this.refreshMainPage();
    });
  }
  //editSelected End

  //deleteSelected Start
  deleteSelected(item: any): void {
    this.DefaultBox();
    const dialogRef = this.dialog.open(FeeGroupDeleteComponent, {
      width: '50%',
      data: { id: item.fee_grp_id },
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result === 'deleted') {
        this.deleteBox = true;
      }
      this.refreshMainPage();
    });
  }
  //deleteSelected End

  addSelected(): void {
    this.DefaultBox();
    const dialogRef = this.dialog.open(FeeGroupAddComponent, {
      width: '50%',
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result === 'inserted') {
        this.addBox = true;
      }
      this.refreshMainPage();
    });
  }

  apply(): void {
    this.loadData();
    this.rightSectionCollapsed = true;
  }

  reset(): void {
    this.feeGroupId = null;
    this.feeGroupNameEN = null;
    this.feeGroupNameBM = null;
    this.modifiedBy = null;
    // this.selected = {
    //   start: moment().subtract(1, 'month'),
    //   end: moment(),
    // };
    this.bsValue = new Date();
    this.minDate.setMonth(this.bsValue.getMonth() - 1);
    this.selected = null;
    // this.selected[1].setDate(this.selected[1].getDate() + 1);
    this.ssCd = [];
    this.ssFeeGroupId = null;
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

  loadSourceSystem() {
    const url = environment.apiUrl + '/api/rms/v1/getsourcesystem';
    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json'
    });
    // Create the request body with your form data
    const requestBody = {
      i_page: 1,
      i_size: this.itemsPerPage,
      i_ss_id: null,
      i_ss_cd: null,
      i_ss_nm: null,
      i_modified_by: null,
      i_dt_modified_fr: null,
      i_dt_modified_to: null,
      i_status: Systemstatus.Active
    };
    // Send an HTTP POST request to the API
    this.http.post(url, requestBody, { headers }).subscribe(
      (response: any) => {
        if (response.data.length === 0) {
          console.error('Invalid response format:', response);
        }
        else {
          this.sourceSystemCodeOptions = response.data;
          // this.sourceSystemCodeOptions=this.sourceSystemCodeOptions.concat(response.data)
          // Handle a successful response (e.g., show a success message)
        }
      },
      (error) => {
        console.error('There was an error retrieving the source system code:', error);
        // Handle API errors (e.g., show an error message)
      }
    );
  }

  async checkRecordInUse(fee_grp_id: any): Promise<any> {
    const url = environment.apiUrl + '/api/fg/v1/checkfeegroupexist';

    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    const body = {
      i_fee_grp_id: fee_grp_id,
    };
    const response: any = await this.http
      .post(url, body, { headers })
      .toPromise();
    try {
      this.checkResult = response.data;
      console.log('check: ' + response.data);
      return response.data;
    } catch (error) {
      console.error(error);
      return error;
    }

    // await this.http.post(url, body, { headers }).subscribe(
    //   (response: any) => {
    //     // Handle the response
    //     this.checkResult=response.data;
    //     //let e = response.data;
    //     console.log("check: "+response.data);
    //     return response.data;
    //     //this.loadData();
    //     //this.cd.detectChanges();
    //   },
    //   (error) => {
    //     console.log(error);
    //     return error;
    //     // In case of error, revert the status change in the UI
    //     // item.status = item.status === Systemstatus.Active ? Systemstatus.Inactive : Systemstatus.Active;
    //     // console.error('Error toggling status:', error);
    //     //this.loadData();
    //     //this.cd.detectChanges();
    //   }
    // );
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

    if (item.status == 'D') {
      //CHECK RECORD IN USE
      this.checkResult = await this.checkRecordInUse(item.fee_grp_id);
      console.log('checkResult: ' + this.checkResult);
    }

    if (this.checkResult == 0) {
      const url = environment.apiUrl + '/api/fg/v1/updatefeegroup';

      const headers = new HttpHeaders({
        Authorization: environment.authKey,
        'Content-Type': 'application/json',
      });

      const body = {
        i_fee_grp_id: item.fee_grp_id,
        i_fee_grp_nm_en: item.fee_grp_nm_en,
        i_fee_grp_nm_bm: item.fee_grp_nm_bm,
        i_status: item.status,
      };

      console.log('this is the body', body);

      this.http.post(url, body, { headers }).subscribe(
        (response: any) => {
          // Handle the response

          this.loadData();
          this.cd.detectChanges();
        },
        (error) => {
          // In case of error, revert the status change in the UI
          item.status =
            item.status === Systemstatus.Active
              ? Systemstatus.Inactive
              : Systemstatus.Active;
          console.error('Error toggling status:', error);
          this.loadData();
          this.cd.detectChanges();
        }
      );
    } else {
      this.showDeactiveAlertBox();
      item.status =
        item.status === Systemstatus.Active
          ? Systemstatus.Inactive
          : Systemstatus.Active;
      this.loadData();
      this.cd.detectChanges();
    }
  }
  //toggle action
}
