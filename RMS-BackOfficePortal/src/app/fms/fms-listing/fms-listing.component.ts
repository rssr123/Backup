import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import moment from 'moment';
import { environment } from 'src/environments/environment';
import { FMS } from '../../core/models/fms.interface';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import {NgbPaginationConfig} from '@ng-bootstrap/ng-bootstrap';
import { Router } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import { Systemstatus } from '../../shared/enums/systemstatus';
import { FmsUpdateComponent } from '../fms-update/fms-update.component';
import { fadeInOut } from '../../shared/animation';
import { ParamData } from 'src/app/core/models/param.interface';
import { formatDate } from '@angular/common';
import { FmsAddComponent } from '../fms-add/fms-add.component';
import { TranslateService } from '@ngx-translate/core';
import { GlobalService } from 'src/app/shared/global.service';
import { perm } from 'src/permissions/perm';
import { AuthService } from 'src/app/core/services/auth.service';

@Component({
  selector: 'app-fms-listing',
  templateUrl: './fms-listing.component.html',
  styleUrls: ['./fms-listing.component.scss'],
  animations:[fadeInOut],
})
export class FmsListingComponent implements OnInit {

getStatusInfo(): string {
  if (this.globalService.getGlobalValue()=='en'){
  return 'An FMS Code can only be activated if the configured Ledger Count is greater than or equal to the number of MFT items. (Ledger Count ≥ MFT Items)';
  }
  else{
    return 'Kod FMS hanya boleh diaktifkan jika Kiraan Lejar yang dikonfigurasi lebih besar daripada atau sama dengan bilangan item MFT. (Kiraan lejar ≥ item MFT)';
  }
}

  page = environment.DefaultPage;
  itemsPerPage = environment.ItemPerPage;
  model: FMS[] = [];
  totalRecords: number = 0;

  fmsId: String | null = null;
  fmsCd: String | null = null;
  congLedCnt: String | null = null;
  modifiedBy: String | null = null;
  isDisplay: boolean = false;

  isLoading: boolean = false;
  //date range picker
  selected!: Date[];
  bsValue = new Date();
  tempDate!: Date;
  minDate = new Date();
  //date range picker

  editBox: boolean = false;
  addBox: boolean = false;
  deleteBox: boolean = false;
  viewBox: boolean = false;

  selectedValue = environment.dropdownOptions[0];
  dropdownOptions = environment.dropdownOptions;

  states: ParamData[] = [];
  selectedState: string = Systemstatus.Active;

  checkResult: number = 0;

  permFMS = perm.FMS_Ledger_Code_View_FMS_Code_Listing_Page + "," + perm.FMS_Ledger_Code_Add_FMS_Code + "," + perm.FMS_Ledger_Code_Edit_FMS_Code + ","+ perm.FMS_Ledger_Code_Activate_FMS_Code + "," + perm.FMS_Ledger_Code_View_FMS_Ledger_Code_Details; // all the perm_cd for this module seperated with comma
  permFMSAllow = "";
  permListAllow: number = 0;
  permAddAllow: number = 0;
  permEditAllow: number = 0;
  permActivateAllow: number = 0;
  permFMSLedgerAllow: number = 0;

  LoadData(singleItem: number) {
    this.selectedValue = singleItem;
    this.itemsPerPage = this.selectedValue;
    this.loadData();
  }

//   toogle start
  rightSectionCollapsed: boolean = true;

//   toggleRightSection() {
//     this.rightSectionCollapsed = !this.rightSectionCollapsed;
//   }

  DefaultBox() {
    this.editBox = false;
    this.addBox = false;
    this.viewBox = false;
  }

  AlertBoxInitialize() {
    if (this.editBox) {
      this.showUpdateAlertBox();
    } else if (this.addBox) {
      this.showInsertAlertBox();
    } 
    // else if (this.deleteBox) {
    //   this.showDeleteAlertBox();}
    // } else if (this.viewBox){
    //     this.showViewAlertBox();
    // }
  }

  //for alert box start
  showInsertAlert = false;

  showInsertAlertBox() {
    this.showInsertAlert = true;
    setTimeout(() => (this.showInsertAlert = false), 10000);
  }

  showUpdateAlert = false;

  showUpdateAlertBox() {
    this.showUpdateAlert = true;
    setTimeout(() => (this.showUpdateAlert = false), 10000);
  }

  // showDeleteAlert = false;

  // showDeleteAlertBox() {
  //   this.showDeleteAlert = true;
  //   setTimeout(() => (this.showDeleteAlert = false), 10000);
  // }

  showResultAlert = false;

  showResultAlertBox() {
    this.showResultAlert = true;
    setTimeout(() => (this.showResultAlert = false), 10000);
  }

  showGenericAlert = false;

  showGenericAlertBox() {
    this.showGenericAlert = true;
    setTimeout(() => (this.showGenericAlert = false), 10000);
  }

  showDeactiveAlert = false;

  showDeactiveAlertBox() {
    this.showDeactiveAlert = true;
    setTimeout(() => (this.showDeactiveAlert = false), 10000);
  }

  showActivateAlert = false;

  showActivateAlertBox() {
    this.showActivateAlert = true;
    setTimeout(() => (this.showActivateAlert = false), 10000);
  }
  //for alert box end

  constructor(
    private http: HttpClient,
    config: NgbPaginationConfig,
    private router: Router,
    public dialog: MatDialog,
    private cd: ChangeDetectorRef,
    private translate: TranslateService,
    private globalService: GlobalService,
    private authService: AuthService
  ) {
    config.maxSize = environment.PaginationMaxSize;
    config.boundaryLinks = true;
    this.translate.setDefaultLang(this.globalService.getGlobalValue()); // default language
    this.translate.use(this.globalService.getGlobalValue());
  }


  ngOnInit(): void {
    this.bsValue = new Date();
    this.minDate.setMonth(this.bsValue.getMonth() - 1);
    this.selected = [this.minDate, this.bsValue];
    //this.selected[1].setDate(this.selected[1].getDate() + 1);
    // {
    //   start: moment().subtract(1, 'month'),
    //   end: moment(),
    // };

    //this.loadStates();
    //load data must be place at last
    this.loadData();
  }

  //loadData Start
  loadData() {
    this.authService.checkUserRole(this.authService.username, this.permFMS)
    .subscribe(
      (response: any) => {
        this.permFMSAllow = response.data;
        this.permListAllow = this.permFMSAllow.includes(perm.FMS_Ledger_Code_View_FMS_Code_Listing_Page) ? 1 : 0;
        this.permAddAllow = this.permFMSAllow.includes(perm.FMS_Ledger_Code_Add_FMS_Code) ? 1 : 0;
        this.permEditAllow = this.permFMSAllow.includes(perm.FMS_Ledger_Code_Edit_FMS_Code) ? 1 : 0;
        this.permActivateAllow = this.permFMSAllow.includes(perm.FMS_Ledger_Code_Activate_FMS_Code) ? 1 : 0;
        this.permFMSLedgerAllow = this.permFMSAllow.includes(perm.FMS_Ledger_Code_View_FMS_Ledger_Code_Details) ? 1 : 0;
        console.log(this.permListAllow, this.permAddAllow, this.permEditAllow, this.permActivateAllow, this.permFMSLedgerAllow);
        if (this.permListAllow === 0) {
          console.log("access-denied");
          this.router.navigate(['/access-denied']);
          return; // Exit the function to prevent further execution
        }
      });

    setTimeout(() => {

    this.isDisplay = true;
    this.isLoading = true;
    const url = environment.apiUrl + '/api/fms/v1/getfms';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    const Body: any = {
      i_page: this.page.toString(),
      i_size: this.itemsPerPage.toString(),
    };

    // if (this.fmsId && this.fmsId.trim()) {
    //   Body.i_fms_ledger_id = this.fmsId;
    // }

    // if (this.fmsCd && this.fmsCd.trim()) {
    //   Body.i_fms_ledger_cd = this.fmsCd;
    // }

    // if (this.congLedCnt && this.congLedCnt.trim()) {
    //   Body.i_fms_ledger_count = this.congLedCnt;
    // }

    // if (this.modifiedBy && this.modifiedBy.trim()) {
    //   Body.i_modified_by = this.modifiedBy;
    // }

    // if (this.modifiedBy && this.modifiedBy.trim()) {
    //     Body.i_modified_by = this.modifiedBy;
    //   }

    // if (this.selected) {
    //   //Body.i_dt_modified_fr =
    //   Body.i_dt_modified_fr = formatDate(this.selected[0], 'YYYY-MM-dd', 'en'); //.format('YYYY-MM-DD');
    //   this.selected[1].setDate(this.selected[1].getDate() + 1);
    //   Body.i_dt_modified_to = formatDate(this.selected[1], 'YYYY-MM-dd', 'en');
    // }

    // if (this.selected && this.selected.start && this.selected.end) {
    //   Body.i_dt_modified_fr = this.selected.start.format('YYYY-MM-DD');
    //   Body.i_dt_modified_to = this.selected.end
    //     .add(1, 'day')
    //     .format('YYYY-MM-DD');
    // }

    // let temp = '';

    // if (
    //   this.selectedState.length > 0 &&
    //   (this.selectedState == Systemstatus.Active ||
    //     this.selectedState == Systemstatus.Inactive)
    // ) {
    //   temp = this.selectedState;
    // }

    // if (temp == Systemstatus.Active || temp == Systemstatus.Inactive) {
    //   Body.i_status = temp;
    // }

    console.log(Body);

    this.http.post(url, Body, { headers }).subscribe(
      (response: any) => {

        console.log("original data");
        console.log(response.data);


        this.model = response.data;
      
        if (response.data.length == 0) {
          this.totalRecords = 0;
          this.isDisplay = false;
          // this.showResultAlertBox();
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
  }, 1000); // 1-second delay (1000 milliseconds)
  }
  //loadData End

  viewInfo(item: any): void {
    const fmsId = item.fmsId;
    const fmsCd = item.fmsCd;
    this.router.navigate(['/fms-ledger-listing', fmsId], { queryParams: { fmsCd } });
    }

  //editSelected Start
  editSelected(item: any): void {
    this.DefaultBox();
    const dialogRef = this.dialog.open(FmsUpdateComponent, {
      width: '50%',
      data: {
        id: item.fmsId,
        code: item.fmsCd,
        active: item.isActive,
      },
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result === 'updated') {
        this.editBox = true;
        this.refreshMainPage();
      }
      // this.refreshMainPage();
    });
  }
  //editSelected End

  //deleteSelected Start
//   deleteSelected(item: any): void {
//     this.DefaultBox();
//     const dialogRef = this.dialog.open(FmsDeleteComponent, {
//       width: '50%',
//       data: { fmsCd: item.fmsCd },
//     });

//     dialogRef.afterClosed().subscribe((result) => {
//       if (result === 'deleted') {
//         this.deleteBox = true;
//       }
//       this.refreshMainPage();
//     });
//   }
  //deleteSelected End

  addSelected(): void {
    this.DefaultBox();
    const dialogRef = this.dialog.open(FmsAddComponent, {
      width: '50%',
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result === 'inserted') {
        this.addBox = true;
        this.refreshMainPage();
      }
      // this.refreshMainPage();
    });
  }

  apply(): void {
    this.loadData();
    this.rightSectionCollapsed = true;
  }

  reset(): void {
    this.fmsId=null;
    this.fmsCd = null;
    this.congLedCnt =  null;
    this.modifiedBy = null;
    // this.selected = {
    //   start: moment().subtract(1, 'month'),
    //   end: moment(),
    // };
    this.bsValue = new Date();
    this.minDate.setMonth(this.bsValue.getMonth() - 1);
    this.selected = [this.minDate, this.bsValue];
    this.selected[1].setDate(this.selected[1].getDate() + 1);
  }

  refreshMainPage(): void {
    this.page = 1;
    this.loadData();
  }

  async checkRecordInUse(fms_id: any): Promise<any> {
    const url = environment.apiUrl + '/api/fms/v1/checkfmsexist';

    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    const body = {
      i_fms_id: fms_id,
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
  }

  async toggleActivation(item: any) {
    this.isDisplay = true;
    this.isLoading = true;
    this.checkResult = 0;
    // Toggle the local status for a responsive UI
    if (item.isActive === false) {
      item.isActive = 0;
    } else {
      item.isActive = 1;
    }

    if (item.isActive == 1) {
      //CHECK RECORD IN USE
      this.checkResult = await this.checkRecordInUse(item.fmsId);
      console.log('checkResult: ' + this.checkResult);
    }

    if (this.checkResult == 1) {

      const url = environment.apiUrl + '/api/fms/v1/updatefms';

      const headers = new HttpHeaders({
        Authorization: environment.authKey,
        'Content-Type': 'application/json',
      });

      const body = {
        i_fms_id: item.fmsId,
        i_fms_cd: item.fmsCd,
        i_is_active: item.isActive,
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
          item.isActive =
            item.isActive === 1
              ? 0
              : 1;
          console.error('Error toggling status:', error);
          this.loadData();
          this.cd.detectChanges();
      }
      );

        const url2 = environment.apiUrl + '/api/fms/v1/fmsactivated';
  
        const body2 = {
          i_fms_id: item.fmsId,
        };
  
        console.log('this is the body', body2);
  
        this.http.post(url2, body2, { headers }).subscribe(
          (response: any) => {
            // Handle the response
            this.cd.detectChanges();
          },
          (error) => {
            // In case of error, revert the status change in the UI
            item.isActive =
              item.isActive === 1
                ? 0
                : 1;
            console.error('Error toggling status:', error);
            this.loadData();
            this.cd.detectChanges();
        }
        );
      }
      

      else if(item.isActive == 0){
      const url = environment.apiUrl + '/api/fms/v1/updatefms';

      const headers = new HttpHeaders({
        Authorization: environment.authKey,
        'Content-Type': 'application/json',
      });

      const body = {
        i_fms_id: item.fmsId,
        i_fms_cd: item.fmsCd,
        i_is_active: item.isActive,
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
          item.isActive =
            item.isActive === 1
              ? 0
              : 1;
          console.error('Error toggling status:', error);
          this.loadData();
          this.cd.detectChanges();
      }
      );
      if(item.isActive ==0){
        this.showDeactiveAlertBox();
        }
     }

     else{
      if(item.isActive ==0){
      this.showDeactiveAlertBox();
      }
      if(item.isActive ==1){
        this.showActivateAlertBox();
        }
      item.isActive =
        item.isActive === 1
          ? 1
          : 0;
      this.loadData();
      this.cd.detectChanges();
    }
  }

//   toggleActivation(item: any) {
//     this.isDisplay = true;
//     this.isLoading = true;
//     // Toggle the local status for a responsive UI

//     item.isActive = item.isActive === 1 ? 0 : 1;

//     const url = environment.apiUrl + '/api/fms/v1/fms_activation'; // Replace with your actual endpoint
//     const headers = new HttpHeaders({
//       Authorization: environment.authKey,
//       'Content-Type': 'application/json',
//     });

//     const body = {
//       i_fms_cd: item.fmsCd,
//       i_status: item.isActive, // Send the updated status
//     };

//     this.http.post(url, body, { headers }).subscribe(
//       (response: any) => {
//         // Handle the response

//         this.loadData();
//         this.cd.detectChanges();
//       },
//       (error) => {
//         // In case of error, revert the status change in the UI
//         item.isActive = item.isActive === 1 ? 0 : 1;
//         console.error('Error toggling status:', error);
//         this.loadData();
//         this.cd.detectChanges();
//       }
//     );
//   }
  
}
