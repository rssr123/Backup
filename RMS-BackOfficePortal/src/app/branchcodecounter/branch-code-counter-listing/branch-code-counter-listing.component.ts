import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Router } from '@angular/router';
import { NgbPaginationConfig } from '@ng-bootstrap/ng-bootstrap';
import { environment } from 'src/environments/environment';
import { TranslateService } from '@ngx-translate/core';
import { GlobalService } from 'src/app/shared/global.service';
import { MatDialog } from '@angular/material/dialog';
import { BranchCodeCounterAddComponent } from '../branch-code-counter-add/branch-code-counter-add.component';
import { BranchCodeCounterUpdateComponent } from '../branch-code-counter-update/branch-code-counter-update.component';
import { BranchCodeCounterDeleteComponent } from '../branch-code-counter-delete/branch-code-counter-delete.component';
import { Systemstatus } from '../../shared/enums/systemstatus';
import { ParamData } from 'src/app/core/models/param.interface';
import { ParamService } from '../../core/services/param.service';
import { formatDate } from '@angular/common';
import { AuthService } from 'src/app/core/services/auth.service';
import { perm } from 'src/permissions/perm';
import { BranchCodeCounter } from 'src/app/core/models/branch-code-counter.interface';
import { BranchCodeCounterList } from 'src/app/core/models/branch-code-counter-list.interface';

@Component({
  selector: 'app-branch-code-counter-listing',
  templateUrl: './branch-code-counter-listing.component.html',
  styleUrls: ['./branch-code-counter-listing.component.scss']
})
export class BranchCodeCounterListingComponent {
  page = environment.DefaultPage;
  itemsPerPage = environment.ItemPerPage;
  model: BranchCodeCounter[] = [];
  model2: BranchCodeCounterList[] = [];
  totalRecords: number = 0;

  // Configuring Permissions for User and roles variables
  permBCC = perm.Branch_Code_Maintenance_View_Page + "," + perm.Branch_Code_Maintenance_Add_Item + "," + perm.Branch_Code_Maintenance_Edit; // all the perm_cd for this module seperated with comma
  permBCCAllow = ""; // variable to store allowed permission for the user
  permListAllow: number = 0; // if 0 then not allow to view listing page, else allow
  permAddAllow: number = 0; // if 0 then not allow to add, else allow
  permEditAllow: number = 0; // if 0 then not allow to edit, else allow
  // end configuration

  counterID: String | null = null;
  terminalID: String | null = null;
  counterIP: String | null = null;
  branchID: Number | null = null;
  modifiedBy: String | null = null;

  isDisplay: boolean = false;
  isLoading: boolean = false;

  //date range picker
  selected: Date[] | null = null;
  bsValue = new Date();
  tempDate !: Date;
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
    private translate: TranslateService,
    private globalService: GlobalService,
    private cd: ChangeDetectorRef,
    private authService: AuthService
  ) {
    config.maxSize = environment.PaginationMaxSize;
    config.boundaryLinks = true;
    this.translate.setDefaultLang(this.globalService.getGlobalValue()); // default language
    this.translate.use(this.globalService.getGlobalValue());
  }

  ngOnInit(): void {
    this.minDate.setMonth(this.minDate.getMonth() - 1);
    this.selected = null;
    this.loadStates();
    this.loadData();
    this.loadCodes();
  }

  //loadData Start
  loadData() {
    this.authService.checkUserRole(this.authService.username, this.permBCC)
      .subscribe(
        (response: any) => {
          this.permBCCAllow = response.data;
          this.permListAllow = this.permBCCAllow.includes(perm.Branch_Code_Maintenance_View_Page) ? 1 : 0;
          this.permAddAllow = this.permBCCAllow.includes(perm.Branch_Code_Maintenance_Add_Item) ? 1 : 0;
          this.permEditAllow = this.permBCCAllow.includes(perm.Branch_Code_Maintenance_Edit) ? 1 : 0;
          console.log(this.permListAllow, this.permAddAllow, this.permEditAllow);
          if (this.permListAllow === 0) {
            console.log("access-denied");
            this.router.navigate(['/access-denied']);
            return; // Exit the function to prevent further execution
          }
          console.log(this.permListAllow, this.permAddAllow, this.permEditAllow);

          // Set your authorization header
          const headers = new HttpHeaders({
            Authorization: environment.authKey,
            'Content-Type': 'application/json',
          });


          this.isDisplay = true;
          this.isLoading = true;
          const url = environment.apiUrl + '/api/bcc/v1/getbranchcodecounter';

          const Body: any = {
            i_page: this.page.toString(),
            i_size: this.itemsPerPage.toString(),
          };

          if (this.counterID && this.counterID.trim()) {
            Body.i_counter_id = this.counterID;
          }

          if (this.terminalID && this.terminalID.trim()) {
            Body.i_terminal_id = this.terminalID;
          }

          if (this.counterIP && this.counterIP.trim()) {
            Body.i_counter_ip = this.counterIP;
          }

          if (this.branchID)
          {
            Body.i_bcm_id = this.branchID;
          }

          if (this.modifiedBy && this.modifiedBy.trim()) {
            Body.i_modified_by = this.modifiedBy;
          }

          if (this.selected) {
            Body.i_dt_modified_fr = formatDate(this.selected[0], 'YYYY-MM-dd', 'en');
            this.selected[1].setDate(this.selected[1].getDate() + 1);
            Body.i_dt_modified_to = formatDate(this.selected[1], 'YYYY-MM-dd', 'en');
          }

          let temp = "";

          if (this.selectedState.length > 0 && (this.selectedState == Systemstatus.Active || this.selectedState == Systemstatus.Inactive)) {
            temp = this.selectedState;
          }

          if (temp == Systemstatus.Active || temp == Systemstatus.Inactive) {
            Body.i_status = temp;
          }

          console.log(Body);

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
                this.AlertBoxInitialize();
                this.DefaultBox();
                this.isLoading = false;
              }
              
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

  //loadCodes Start
  loadCodes() {
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    const url2 = environment.apiUrl + '/api/helper/v1/getbranchcodecounterlist';

    const body2 = {
      i_status: null
    }

    this.http.post(url2, body2, { headers }).subscribe(
      (response: any) => {
        this.model2 = response.data;
      },
      (error) => {
        console.error(error);
      }
    )
  }
  //loadCodes End

  // onBranchCodeChange(event: any) {
  //   const selectedBranchCode = event.target.value;
  //   this.branchID = selectedBranchCode;
  // }
  
  //editSelected Start
  editSelected(item: any): void {
    this.DefaultBox();
    const dialogRef = this.dialog.open(BranchCodeCounterUpdateComponent, {
      width: '50%',
      data: {
        id: item.bcc_id,
        counter_id: item.counter_id,
        terminal_id: item.terminal_id,
        counter_ip: item.counter_ip,
        bcm_id: item.bcm_id,
        bcm_code: item.bcm_code
      },
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result === 'updated') {
        this.editBox = true;
        this.showUpdateAlertBox();
        this.refreshMainPage();
      }
      // this.refreshMainPage();
    });
  }
  //editSelected End

  //deleteSelected Start
  deleteSelected(item: any): void {
    this.DefaultBox();
    const dialogRef = this.dialog.open(BranchCodeCounterDeleteComponent, {
      width: '50%',
      data: { id: item.i_bcc_id },
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result === 'deleted') {
        this.deleteBox = true;
        this.refreshMainPage();
      }
      // this.refreshMainPage();
    });
  }
  //deleteSelected End

  //addSelected Start
  addSelected(): void {
    this.DefaultBox();
    const dialogRef = this.dialog.open(BranchCodeCounterAddComponent, {
      width: '50%',
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result === 'inserted') {
        this.addBox = true;
        this.showInsertAlertBox();
        this.refreshMainPage();
      }
      // this.refreshMainPage();
    });
  }
  //addSelected End

  apply(): void {
    this.loadData();
    this.rightSectionCollapsed = true;
  }

  reset(): void {
    this.counterID = null;
    this.terminalID = null;
    this.counterIP = null;
    this.branchID = null;
    this.modifiedBy = null;
    this.minDate.setDate(this.minDate.getMonth() - 1);
    this.selected = null;
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
          this.states.push({ param_cd: '', nm_en: 'All', nm_bm: 'All', total: 5 }); //add 'All' options
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

  async checkRecordInUse(bcc_id: any): Promise<any> {
    const url = environment.apiUrl + '/api/bcc/v1/checkbccmap';

    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    const body = {
      i_bcc_id: bcc_id,
    };

    const response: any = await this.http.post(url, body, { headers }).toPromise();
    try {
      this.checkResult = response.data;
      console.log("check: " + response.data);
      return response.data;
    }
    catch (error) {
      console.error(error);
      return error;
    }
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

    // if (item.status == 'D') {
    //   //CHECK RECORD IN USE
    //   this.checkResult -= await this.checkRecordInUse(item.bcc_id);
    // }

    if (this.checkResult == 0) {

      const url = environment.apiUrl + '/api/bcc/v1/delbranchcodecounter';

      const headers = new HttpHeaders({
        Authorization: environment.authKey,
        'Content-Type': 'application/json',
      });

      const body = {
        i_bcc_id: item.bcc_id,
        i_modified_by: null,
        i_status: item.status
      };

      this.http.post(url, body, { headers }).subscribe(
        (response: any) => {
          this.loadData();
          this.cd.detectChanges();
        },
        (error) => {
          item.status = item.status === Systemstatus.Active ? Systemstatus.Inactive : Systemstatus.Active;
          console.error('Error toggling status:', error);
          this.loadData();
          this.cd.detectChanges();
        }
      );
    }
    else {
      this.showDeactiveAlertBox();
      item.status = item.status === Systemstatus.Active ? Systemstatus.Inactive : Systemstatus.Active;
      this.loadData();
      this.cd.detectChanges();
    }
  }
}
