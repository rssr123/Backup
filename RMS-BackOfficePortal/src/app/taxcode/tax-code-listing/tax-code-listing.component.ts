import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { TaxCode } from '../../core/models/tax-code.interface';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Router } from '@angular/router';
import { NgbPaginationConfig } from '@ng-bootstrap/ng-bootstrap';
import { environment } from 'src/environments/environment';
import { TranslateService } from '@ngx-translate/core';
import { GlobalService } from 'src/app/shared/global.service';
import { MatDialog } from '@angular/material/dialog';
import { TaxCodeAddComponent } from '../tax-code-add/tax-code-add.component';
import { TaxCodeDeleteComponent } from '../tax-code-delete/tax-code-delete.component';
import { TaxCodeUpdateComponent } from '../tax-code-update/tax-code-update.component';
import { Systemstatus } from '../../shared/enums/systemstatus';
import { fadeInOut } from '../../shared/animation';
import { ParamData } from 'src/app/core/models/param.interface';
import { ParamService } from '../../core/services/param.service';
import { formatDate } from '@angular/common';
import { AuthService } from 'src/app/core/services/auth.service';
import { perm } from 'src/permissions/perm';
import { Observable, finalize, switchMap } from 'rxjs';

@Component({
  selector: 'app-tax-code-listing',
  templateUrl: './tax-code-listing.component.html',
  styleUrls: ['./tax-code-listing.component.scss'],
  animations: [fadeInOut],
})
export class TaxCodeListingComponent implements OnInit {
  page = environment.DefaultPage;
  itemsPerPage = environment.ItemPerPage;
  model: TaxCode[] = [];
  totalRecords: number = 0;

  // Configuring Permissions for User and roles variables
  permTCD = perm.Tax_Code_Maintenance_View_Page + "," + perm.Tax_Code_Maintenance_Add_Item + "," + perm.Tax_Code_Maintenance_Edit; // all the perm_cd for this module seperated with comma
  permTCDAllow = ""; // variable to store allowed permission for the user
  permListAllow: number = 0; // if 0 then not allow to view listing page, else allow
  permAddAllow: number = 0; // if 0 then not allow to add tax code, else allow
  permEditAllow: number = 0; // if 0 then not allow to edit tax code, else allow
  // end configuration

  taxCode: String | null = null;
  taxCodeNameEN: String | null = null;
  taxCodeNameBM: String | null = null;
  taxPercentage: String | null = null;
  modifiedBy: String | null = null;

  isDisplay: boolean = false;

  isLoading: boolean = false;
  //date range picker
  selected: Date[] | null = null;
  bsValue = new Date();
  tempDate !: Date;
  minDate = new Date();
  // selected!: { start?: moment.Moment; end?: moment.Moment };
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
  }

  //loadData Start
  loadData() {
    this.authService.checkUserRole(this.authService.username, this.permTCD)
      .subscribe(
        (response: any) => {
          this.permTCDAllow = response.data;
          this.permListAllow = this.permTCDAllow.includes(perm.Tax_Code_Maintenance_View_Page) ? 1 : 0;
          this.permAddAllow = this.permTCDAllow.includes(perm.Tax_Code_Maintenance_Add_Item) ? 1 : 0;
          this.permEditAllow = this.permTCDAllow.includes(perm.Tax_Code_Maintenance_Edit) ? 1 : 0;
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
          const url = environment.apiUrl + '/api/tc/v1/gettaxcode';

          const Body: any = {
            i_page: this.page.toString(),
            i_size: this.itemsPerPage.toString(),
          };

          if (this.taxCode && this.taxCode.trim()) {
            Body.i_tax_cd = this.taxCode;
          }

          if (this.taxCodeNameEN && this.taxCodeNameEN.trim()) {
            Body.i_tax_cd_nm_en = this.taxCodeNameEN;
          }

          if (this.taxCodeNameBM && this.taxCodeNameBM.trim()) {
            Body.i_tax_cd_nm_bm = this.taxCodeNameBM;
          }

          if (this.taxPercentage && this.taxPercentage.trim()) {
            Body.i_tax_pct = this.taxPercentage;
          }

          if (this.modifiedBy && this.modifiedBy.trim()) {

            Body.i_modified_by = this.modifiedBy;
          }

          // if (this.selected && this.selected.start && this.selected.end) {
          //   Body.i_dt_modified_fr = this.selected.start.format('YYYY-MM-DD');
          //   Body.i_dt_modified_to = this.selected.end
          //     .add(1, 'day')
          //     .format('YYYY-MM-DD');
          // }
          if (this.selected) {
            this.selected[0].setHours(0, 0, 0, 0);
            Body.i_dt_modified_fr = formatDate(this.selected[0], 'YYYY-MM-dd HH:mm:ss', 'en');
            this.selected[1].setHours(23, 59, 59, 999);
            Body.i_dt_modified_to = formatDate(this.selected[1], 'YYYY-MM-dd HH:mm:ss', 'en');
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

    // this.isDisplay = true;
    // // Set your authorization header
    // const headers = new HttpHeaders({
    //   Authorization: environment.authKey,
    //   'Content-Type': 'application/json',
    // });

    // this.isLoading = true;
    // const url = environment.apiUrl + '/api/tc/v1/gettaxcode';

    // const Body: any = {
    //   i_page: this.page.toString(),
    //   i_size: this.itemsPerPage.toString(),
    // };

    // if (this.taxCode && this.taxCode.trim()) {
    //   Body.i_tax_cd = this.taxCode;
    // }

    // if (this.taxCodeNameEN && this.taxCodeNameEN.trim()) {
    //   Body.i_tax_cd_nm_en = this.taxCodeNameEN;
    // }

    // if (this.taxCodeNameBM && this.taxCodeNameBM.trim()) {
    //   Body.i_tax_cd_nm_bm = this.taxCodeNameBM;
    // }

    // if (this.taxPercentage && this.taxPercentage.trim()) {
    //   Body.i_tax_pct = this.taxPercentage;
    // }

    // if (this.modifiedBy && this.modifiedBy.trim()) {
    //   1
    //   Body.i_modified_by = this.modifiedBy;
    // }

    // // if (this.selected && this.selected.start && this.selected.end) {
    // //   Body.i_dt_modified_fr = this.selected.start.format('YYYY-MM-DD');
    // //   Body.i_dt_modified_to = this.selected.end
    // //     .add(1, 'day')
    // //     .format('YYYY-MM-DD');
    // // }
    // if (this.selected) {
    //   Body.i_dt_modified_fr = formatDate(this.selected[0], 'YYYY-MM-dd', 'en');
    //   this.selected[1].setDate(this.selected[1].getDate() + 1);
    //   Body.i_dt_modified_to = formatDate(this.selected[1], 'YYYY-MM-dd', 'en');
    // }


    // let temp = "";

    // if (this.selectedState.length > 0 && (this.selectedState == Systemstatus.Active || this.selectedState == Systemstatus.Inactive)) {
    //   temp = this.selectedState;
    // }

    // if (temp == Systemstatus.Active || temp == Systemstatus.Inactive) {
    //   Body.i_status = temp;
    // }


    // console.log(Body);

    // this.http.post(url, Body, { headers }).subscribe(
    //   (response: any) => {
    //     this.model = response.data;
    //     if (response.data.length == 0) {
    //       this.totalRecords = 0;
    //       this.isDisplay = false;
    //       this.showResultAlertBox();
    //       this.isLoading = false;
    //     } else {
    //       this.totalRecords = response.data[0].total;
    //       this.AlertBoxInitialize();
    //       this.isLoading = false;
    //     }
    //     console.log(response.data);
    //     console.log(this.totalRecords);
    //   },
    //   (error) => {
    //     console.error(error);
    //     this.isLoading = false;
    //     // Handle errors here
    //   }
    // );
  }
  //loadData End

  //editSelected Start
  editSelected(item: any): void {
    this.DefaultBox();
    const dialogRef = this.dialog.open(TaxCodeUpdateComponent, {
      width: '50%',
      data: {
        taxCodeId: item.tax_cd_id,
        id: item.tax_cd,
        en: item.tax_cd_nm_en,
        bm: item.tax_cd_nm_bm,
        pct: item.tax_pct
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
    const dialogRef = this.dialog.open(TaxCodeDeleteComponent, {
      width: '50%',
      data: { id: item.tax_cd },
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
    const dialogRef = this.dialog.open(TaxCodeAddComponent, {
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
    this.taxCode = null;
    this.taxCodeNameEN = null;
    this.taxCodeNameBM = null;
    this.modifiedBy = null;
    // this.selected = {
    //   start: moment().subtract(1, 'month'),
    //   end: moment(),
    // };
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

  async checkRecordInUse(tax_cd_id: any): Promise<any> {
    const url = environment.apiUrl + '/api/tc/v1/checktaxcodeexist';

    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    const body = {
      i_tax_cd_id: tax_cd_id,
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

    // await this.http.post(url, body, { headers }).subscribe(
    //   (response: any) => {
    //     // Handle the response
    //     //this.checkResult=response.data;
    //     let e = response.data;
    //     console.log("check: " + response.data);
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
      this.checkResult -= await this.checkRecordInUse(item.tax_cd_id);
    }

    if (this.checkResult == 0) {

      const url = environment.apiUrl + '/api/tc/v1/deltaxcode';

      const headers = new HttpHeaders({
        Authorization: environment.authKey,
        'Content-Type': 'application/json',
      });

      const body = {
        i_tax_cd: item.tax_cd,
        i_modified_by: null,
        i_status: item.status
      };

      console.log("this is the body", body);

      this.http.post(url, body, { headers }).subscribe(
        (response: any) => {
          // Handle the response

          this.loadData();
          this.cd.detectChanges();
        },
        (error) => {
          // In case of error, revert the status change in the UI
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
  //toggle action
}

