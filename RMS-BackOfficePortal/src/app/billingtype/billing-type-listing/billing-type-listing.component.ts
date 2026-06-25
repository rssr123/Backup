import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Router } from '@angular/router';
import { NgbPaginationConfig } from '@ng-bootstrap/ng-bootstrap';
import { environment } from 'src/environments/environment';
import { TranslateService } from '@ngx-translate/core';
import { GlobalService } from 'src/app/shared/global.service';
import { MatDialog } from '@angular/material/dialog';
import { BillingTypeAddComponent } from 'src/app/billingtype/billing-type-add/billing-type-add.component';
import { BillingTypeUpdateComponent } from 'src/app/billingtype/billing-type-update/billing-type-update.component';
import { Systemstatus } from '../../shared/enums/systemstatus';
import { fadeInOut } from '../../shared/animation';
import { ParamData } from '../../core/models/param.interface';
import { ParamService } from '../../core/services/param.service';
import { formatDate } from '@angular/common';
import { AuthService } from 'src/app/core/services/auth.service';
import { perm } from 'src/permissions/perm';

@Component({
  selector: 'app-billing-type-listing',
  templateUrl: './billing-type-listing.component.html',
  styleUrls: ['./billing-type-listing.component.scss'],
  animations: [fadeInOut],
})
export class BillingTypeListingComponent implements OnInit {
  page = environment.DefaultPage;
  itemsPerPage = environment.ItemPerPage;
  model: any[] = [];
  totalRecords: number = 0;

  // Configuring Permissions
  permBT = perm.Billing_Type_Maintenance_View_Page + "," + perm.Billing_Type_Maintenance_Add_Item + "," + perm.Billing_Type_Maintenance_Edit;
  permBTAllow = '';
  permListAllow: number = 0;
  permAddAllow: number = 0;
  permEditAllow: number = 0;

  btCd: string | null = null;
  btTy: string | null = null;
  btDesc: string | null = null;
  classId: string | null = null;
  ssCd: string | null = null;
  mftId: string | null = null;
  dpsMftId: string | null = null;
  modifiedBy: string | null = null;

  isDisplay: boolean = false;
  isLoading: boolean = false;

  // Date range picker
  selected: Date[] | null = null;
  bsValue = new Date();
  tempDate!: Date;
  minDate = new Date();

  editBox: boolean = false;
  addBox: boolean = false;
  deleteBox: boolean = false;

  // Toggle section
  rightSectionCollapsed: boolean = true;

  // Default pagination
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
    private paramService: ParamService,
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

  refreshMainPage(): void {
    this.page = 1;
    this.loadData();
  }

  loadData(): void {
    this.authService.checkUserRole(this.authService.username, this.permBT).subscribe(
      (response: any) => {
        this.permBTAllow = response.data;
        this.permListAllow = this.permBTAllow.includes(perm.Billing_Type_Maintenance_View_Page) ? 1 : 0;
        this.permAddAllow = this.permBTAllow.includes(perm.Billing_Type_Maintenance_Add_Item) ? 1 : 0;
        this.permEditAllow = this.permBTAllow.includes(perm.Billing_Type_Maintenance_Edit) ? 1 : 0;

        if (this.permListAllow === 0) {
          console.log("Access denied");
          this.router.navigate(['/access-denied']);
          return;
        }

        this.isLoading = true;

        const url = environment.apiUrl + '/api/bltc/v1/getBillingType';
        const headers = new HttpHeaders({
          Authorization: environment.authKey,
          'Content-Type': 'application/json',
        });

        const Body: any = {
          i_page: this.page.toString(),
          i_size: this.itemsPerPage.toString(),
          i_bt_cd: this.btCd?.trim() || null,
          i_bt_desc: this.btDesc?.trim() || null,
          i_class_id: this.classId?.trim() || null,
          i_ss_cd: this.ssCd?.trim() || null,
          i_mft_id: this.mftId?.trim() || null,
          i_dps_mft_id: this.dpsMftId?.trim() || null,
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
    const dialogRef = this.dialog.open(BillingTypeAddComponent, {
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
    const dialogRef = this.dialog.open(BillingTypeUpdateComponent, {
      width: '50%',
      data: {
        item
      },
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result === 'updated') {
        //this.loadData();
        this.editBox = true;
      }
      this.refreshMainPage();
    });
  }

  


  showCantActivateAlert = false;
  uploadErrorMessage: string = '';

  showUploadErrorAlertBox(message: string) {
    this.uploadErrorMessage = message; // Store the message
    this.showCantActivateAlert = true;
    setTimeout(() => (this.showCantActivateAlert = false), 10000);
  }

  toggleActivation(item: any): void {
    const url = `${environment.apiUrl}/api/bltc/v1/delBillingType`;
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    const body = {
      i_bltc_id: item.bltc_id,
      i_bt_cd: item.btCd,
      i_status: item.status === Systemstatus.Active ? Systemstatus.Delete : Systemstatus.Active,
    };

    this.http.post(url, body, { headers }).subscribe(
      () => {
        this.loadData();
      },
      (error) => {
        console.error('Error toggling activation:', error);
        this.uploadErrorMessage = this.translate.instant("Unable to activate or deactivate the selected item. Another item with the same billing type code is already active.");
        this.showUploadErrorAlertBox(this.uploadErrorMessage);
        this.loadData();
      }
    );
  }

  apply(): void {
    this.loadData();
    this.rightSectionCollapsed = true;
  }

  reset(): void {
    this.btCd = null;
    this.btDesc = null;
    this.modifiedBy = null;
    this.selected = null;
    this.selectedState = '';
  }

  loadStates(): void {
    this.paramService.getStates('1', '100', '', 'Status').subscribe(
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
