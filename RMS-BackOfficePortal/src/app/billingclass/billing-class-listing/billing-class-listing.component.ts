import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Router } from '@angular/router';
import { NgbPaginationConfig } from '@ng-bootstrap/ng-bootstrap';
import { environment } from 'src/environments/environment';
import { TranslateService } from '@ngx-translate/core';
import { GlobalService } from 'src/app/shared/global.service';
import { MatDialog } from '@angular/material/dialog';
import { BillingClassAddComponent } from 'src/app/billingclass/billing-class-add/billing-class-add.component';
import { BillingClassUpdateComponent } from 'src/app/billingclass/billing-class-update/billing-class-update.component';
import { Systemstatus } from '../../shared/enums/systemstatus';
import { fadeInOut } from '../../shared/animation';
import { ParamData } from '../../core/models/param.interface';
import { ParamService } from '../../core/services/param.service';
import { formatDate } from '@angular/common';
import { AuthService } from 'src/app/core/services/auth.service';
import { perm } from 'src/permissions/perm';

@Component({
  selector: 'app-billing-class-listing',
  templateUrl: './billing-class-listing.component.html',
  styleUrls: ['./billing-class-listing.component.scss'],
  animations: [fadeInOut],
})
export class BillingClassListingComponent implements OnInit {
  page = environment.DefaultPage;
  itemsPerPage = environment.ItemPerPage;
  model: any[] = [];
  totalRecords: number = 0;

  // Configuring Permissions
  permBC = perm.Billing_Class_Maintenance_View_Page + "," + perm.Billing_Class_Maintenance_Add_Item + "," + perm.Billing_Class_Maintenance_Edit;
  permBCAllow = '';
  permListAllow: number = 0; 
  permAddAllow: number = 0; 
  permEditAllow: number = 0;

  classId: string | null = null;
  classDesc: string | null = null;
  modifiedBy: string | null = null;

  isDisplay: boolean = false;
  isLoading: boolean = false;

  //date range picker
  selected: Date[] | null = null;
  bsValue = new Date();
  tempDate !: Date;
  minDate = new Date();

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
    this.translate.setDefaultLang(this.globalService.getGlobalValue());
    this.translate.use(this.globalService.getGlobalValue());
  }

  ngOnInit(): void {
    this.minDate.setMonth(this.minDate.getMonth() - 1);
    this.selected = null;
    this.loadStates();
    this.loadData();
  }

  loadData(): void {
    this.authService.checkUserRole(this.authService.username, this.permBC).subscribe(
      (response: any) => {
        this.permBCAllow = response.data;
        this.permListAllow = this.permBCAllow.includes(perm.Billing_Class_Maintenance_View_Page) ? 1 : 0;
        this.permAddAllow = this.permBCAllow.includes(perm.Billing_Class_Maintenance_Add_Item) ? 1 : 0;
        this.permEditAllow = this.permBCAllow.includes(perm.Billing_Class_Maintenance_Edit) ? 1 : 0;

        if (this.permListAllow === 0) {
          console.log("Access denied");
          this.router.navigate(['/access-denied']);
          return;
        }

        this.isLoading = true;

        const url = environment.apiUrl + '/api/blc/v1/getBillingClass';
        const headers = new HttpHeaders({
          Authorization: environment.authKey,
          'Content-Type': 'application/json',
        });

        const Body: any = {
          i_page: this.page.toString(),
          i_size: this.itemsPerPage.toString(),
          i_class_id: this.classId?.trim() || null,
          i_class_desc: this.classDesc?.trim() || null,
          i_modified_by: this.modifiedBy?.trim() || null,
          i_dt_modified_fr: this.selected ? formatDate(this.selected[0], 'yyyy-MM-dd', 'en') : null,
          i_dt_modified_to: this.selected ? formatDate(this.selected[1], 'yyyy-MM-dd', 'en') : null,
          // i_status: this.selectedState || Systemstatus.Active,
          
        };

        let temp = "";

        if (this.selectedState.length > 0 && (this.selectedState == Systemstatus.Active || this.selectedState == Systemstatus.Inactive)) {
          temp = this.selectedState;
        }

        if (temp == Systemstatus.Active || temp == Systemstatus.Inactive) {
          Body.i_status = temp;
        }

        this.http.post(url, Body, { headers }).subscribe(
          (response: any) => {
            this.model = response.data || [];
            this.totalRecords = response.data?.length > 0 ? response.data[0].total : 0;
            // this.AlertBoxInitialize();
            this.isDisplay = true;
            this.isLoading = false;
          },
          (error) => {
            console.error(error);
            this.isDisplay = true;
            this.isLoading = false;
          }
        );
      },
      (error) => {
        console.error(error);
      }
    );
  }

  addSelected(): void {
    const dialogRef = this.dialog.open(BillingClassAddComponent, {
      width: '50%',
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result === 'inserted') {
        //this.loadData();
        this.addBox = true;
      }
      this.refreshMainPage();
    });
  }

  editSelected(item: any): void {
    const dialogRef = this.dialog.open(BillingClassUpdateComponent, {
      width: '50%',
      data: {
        blcm_id: item.blcm_id,
        classId: item.classId,
        classDesc: item.classDesc,
      },
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result === 'updated') {
        this.editBox = true;
        //this.loadData();
      }
      this.refreshMainPage();
    });
  }

  toggleActivation(item: any): void {
    const url = `${environment.apiUrl}/api/blc/v1/delBillingClass`;
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    const body = {
      i_blcm_id: item.blcm_id,
      i_class_id: item.classId,
      i_modified_by: null,
      i_status: item.status === Systemstatus.Active ? Systemstatus.Delete : Systemstatus.Active,
    };

    this.http.post(url, body, { headers }).subscribe(
      () => {
        //this.loadData();
        this.refreshMainPage();
      },
      (error) => {
        console.error('Error toggling activation:', error);
        this.showUploadErrorAlertBox(this.translate.instant("Unable to activate or deactivate the selected item. Another item with the same Class ID is already active."));
        //this.loadData();
        this.refreshMainPage();
      }
    );
  }

  showCantActivateAlert = false;
  uploadErrorMessage: string = '';

  showUploadErrorAlertBox(message: string) {
    this.uploadErrorMessage = message; // Store the message
    this.showCantActivateAlert = true;
    setTimeout(() => (this.showCantActivateAlert = false), 10000);
  }

  apply(): void {
    this.loadData();
    this.rightSectionCollapsed = true;
  }

  reset(): void {
    this.classId = null;
    this.classDesc = null;
    this.modifiedBy = null;
    this.selected = null;

    this.selectedState = '';
  }

  refreshMainPage(): void {
    this.page = 1;
    this.loadData();
  }

  loadStates(): void {
    this.ParamService.getStates('1', '100', '', 'Status').subscribe(
      (response: any) => {
        if (response && response.data && Array.isArray(response.data)) {
          this.states = [{ param_cd: '', nm_en: 'All', nm_bm: 'All', total: 0 }, ...response.data];
        }
      },
      (error) => {
        console.error('Error loading states:', error);
      }
    );
  }
}