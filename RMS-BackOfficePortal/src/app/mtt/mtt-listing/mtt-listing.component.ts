import { formatDate } from '@angular/common';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { NgbPaginationConfig } from '@ng-bootstrap/ng-bootstrap';
import { TranslateService } from '@ngx-translate/core';
import { Param, SourceSystemCode } from 'src/app/core/models/entity';
import { MTT } from 'src/app/core/models/mtt-interface';
import { ParamData } from 'src/app/core/models/param.interface';
import { Systemstatus } from 'src/app/shared/enums/systemstatus';
import { GlobalService } from 'src/app/shared/global.service';
import { environment } from 'src/environments/environment';
import { perm } from 'src/permissions/perm';
import { AuthService } from 'src/app/core/services/auth.service';

@Component({
  selector: 'app-mtt-listing',
  templateUrl: './mtt-listing.component.html',
  styleUrls: ['./mtt-listing.component.scss']
})
export class MttListingComponent implements OnInit {


  getStatusInfo(): string {
    return 'An FMS Code can only be activated if the configured Ledger Count is greater than or equal to the number of MFT items. (Ledger Count ≥ MFT Items)';
  }

  page = environment.DefaultPage;
  itemsPerPage = environment.ItemPerPage;
  model: MTT[] = [];
  totalRecords: number = 0;

  ss_cd: String | null = null;
  orn_no: String | null = null;
  orn_dt: String | null = null;
  total_amt: String | null = null;
  rcpt_no: String | null = null;
  rcpt_dt: String | null = null;
  mtt_id: number | null = null;
  total: String | null = null;
  status: string | null = null;
  nm: string | null = null;
  selectedSourceSystemCodes: any[] = [];
  statusOptions: Param[] = [];
  sourceSystemCodeOptions: SourceSystemCode[] = [];
  rms_type: string | null = null;
  selectedRMSType: string | null = null;

  // Configuring Permissions for User and roles
  permMTT = perm.MTT_Listing_View_Listing_Page + "," + perm.MTT_Listing_View_Details_Page;
  permMTTAllow = "";
  permMTTListAllow: number = 1;
  permMTTDetailsAllow: number = 0;
  // end configuration

  invalidInput: boolean = false;

  isDisplay: boolean = false;

  isLoading: boolean = false;
  //date range picker
  selected!: Date[];
  bsValue = new Date();
  tempDate!: Date;
  minDate = new Date();
  //date range picker

  //date range picker 2
  selected2!: Date[];
  bsValue2 = new Date();
  tempDate2!: Date;
  minDate2 = new Date();
  //date rangee picker 2 

  editBox: boolean = false;
  addBox: boolean = false;
  deleteBox: boolean = false;
  viewBox: boolean = false;

  selectedValue = environment.dropdownOptions[0];
  dropdownOptions = environment.dropdownOptions;

  states: ParamData[] = [];
  selectedState: string | null = null;

  checkResult: number = 0;
  dropDownSize = environment.DropDownSize;

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
    setTimeout(() => (this.showInsertAlert = false), 2000);
  }

  showUpdateAlert = false;

  showUpdateAlertBox() {
    this.showUpdateAlert = true;
    setTimeout(() => (this.showUpdateAlert = false), 2000);
  }

  // showDeleteAlert = false;

  // showDeleteAlertBox() {
  //   this.showDeleteAlert = true;
  //   setTimeout(() => (this.showDeleteAlert = false), 2000);
  // }

  showResultAlert = false;

  showResultAlertBox() {
    this.showResultAlert = true;
    setTimeout(() => (this.showResultAlert = false), 2000);
  }

  showGenericAlert = false;

  showGenericAlertBox() {
    this.showGenericAlert = true;
    setTimeout(() => (this.showGenericAlert = false), 2000);
  }

  showDeactiveAlert = false;

  showDeactiveAlertBox() {
    this.showDeactiveAlert = true;
    setTimeout(() => (this.showDeactiveAlert = false), 2000);
  }

  showActivateAlert = false;

  showActivateAlertBox() {
    this.showActivateAlert = true;
    setTimeout(() => (this.showActivateAlert = false), 2000);
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
    // this.selected = [this.minDate, this.bsValue];

    this.bsValue2 = new Date();
    this.minDate2.setMonth(this.bsValue.getMonth() - 1);
    // this.selected2 = [this.minDate, this.bsValue];
    //this.selected[1].setDate(this.selected[1].getDate() + 1);
    // {
    //   start: moment().subtract(1, 'month'),
    //   end: moment(),
    // };

    const today = new Date();
    const yesterday = new Date(today);
    yesterday.setDate(today.getDate() - 1);

    // (optionally normalize to midnight if you care about time)
    const y = new Date(yesterday.getFullYear(), yesterday.getMonth(), yesterday.getDate());
    const t = new Date(today.getFullYear(), today.getMonth(), today.getDate());

    this.selected2 = [y, t];

    //this.loadStates();
    //load data must be place at last
    this.populateStatus();
    this.populateSourceSystemCode();
    this.loadData();
  }


  toggleRightSection() {
    this.rightSectionCollapsed = !this.rightSectionCollapsed;
  }

  validateInput(event: any): void {
    const input = event.target.value;

    if (!/^[0-9]*(\.[0-9]+)?$/.test(input)) {
      this.invalidInput = true;
    } else {
      this.invalidInput = false;
    }
  }

  //loadData Start
  loadData() {

    this.authService.checkUserRole(this.authService.username, this.permMTT)
      .subscribe(
        (response: any) => {
          this.permMTTAllow = response.data;
          this.permMTTListAllow = this.permMTTAllow.includes(perm.MTT_Listing_View_Listing_Page) ? 1 : 0;
          this.permMTTDetailsAllow = this.permMTTAllow.includes(perm.MTT_Listing_View_Details_Page) ? 1 : 0;
          console.log(this.permMTTListAllow, this.permMTTDetailsAllow);
          if (this.permMTTListAllow === 0) {
            console.log("access-denied");
            this.router.navigate(['/access-denied']);
            return; // Exit the function to prevent further execution
          }
          // console.log(this.permListAllow, this.permAddAllow, this.permEditAllow);

          // Set your authorization header
          const headers = new HttpHeaders({
            Authorization: environment.authKey,
            'Content-Type': 'application/json',
          });

          this.isDisplay = true;
          this.isLoading = true;
          const url = environment.apiUrl + '/api/mttl/v1/getmttlisting';

          const Body: any = {
            i_page: this.page.toString(),
            i_size: this.itemsPerPage.toString(),
          };

          // if (this.ss_cd && this.ss_cd.trim()) {
          //   Body.i_ss_cd = this.ss_cd;
          // }

          if (this.orn_no && this.orn_no.trim()) {
            Body.i_orn_no = this.orn_no;
          }

          if (!this.invalidInput) {
            Body.i_total_amt = this.total_amt;
          }

          if (this.rcpt_no && this.rcpt_no.trim()) {
            Body.i_rcpt_no = this.rcpt_no;
          }

          // if (this.modifiedBy && this.modifiedBy.trim()) {
          //     Body.i_modified_by = this.modifiedBy;
          //   }

          if (this.selected && this.selected.length > 0) {
            //Body.i_dt_modified_fr =
            this.selected[0].setHours(0, 0, 0, 0);
            Body.i_rcpt_dt_fr = formatDate(this.selected[0], 'YYYY-MM-dd HH:mm:ss', 'en'); //.format('YYYY-MM-DD');
            // this.selected[1].setDate(this.selected[1].getDate() + 1);
            this.selected[1].setHours(23, 59, 59, 999);
            Body.i_rcpt_dt_to = formatDate(this.selected[1], 'YYYY-MM-dd HH:mm:ss', 'en');
          }

          if (this.selected2 && this.selected2.length > 0) {
            //Body.i_dt_modified_fr =
            this.selected2[0].setHours(0, 0, 0, 0);
            Body.i_orn_dt_fr = formatDate(this.selected2[0], 'YYYY-MM-dd HH:mm:ss', 'en'); //.format('YYYY-MM-DD');
            // this.selected2[1].setDate(this.selected2[1].getDate() + 1);
            this.selected2[1].setHours(23, 59, 59, 999);
            Body.i_orn_dt_to = formatDate(this.selected2[1], 'YYYY-MM-dd HH:mm:ss', 'en');
          }

          if (this.selectedSourceSystemCodes.length > 0) {
            Body.i_ss_cd = this.selectedSourceSystemCodes.toString();
          }

          if (this.selectedState != null) {
            Body.i_order_status = this.selectedState;
          }

          if (this.selectedRMSType) {
            Body.i_rms_type = this.selectedRMSType;
          }

          // let resultString = this.selectedSourceSystemCodes.join(',');

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
              // Handle errors here
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

  populateStatus() {
    const url = environment.apiUrl + '/api/rms/v1/getparam';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json'
    });


    // Create the request body with your form data
    const requestBody = {
      i_page: this.page,
      i_size: this.dropDownSize, //dont use item per page here because it is for table
      i_param_cd: null,
      i_param_grp_nm: 'OrderStatus'
    };

    // Send an HTTP POST request to the API
    this.http.post(url, requestBody, { headers }).subscribe(
      (response: any) => {
        if (response.data.length === 0) {
          console.error('Invalid response format:', response);
        }
        else {
          this.statusOptions = response.data;
        }
      },
      (error) => {
        console.error('There was an error retrieving the status:', error);

      }
    );
  }

  populateSourceSystemCode() {
    const url = environment.apiUrl + '/api/rms/v1/getsourcesystem';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json'
    });


    // Create the request body with your form data
    const requestBody = {
      i_page: this.page,
      i_size: this.dropDownSize,
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
          this.sourceSystemCodeOptions = response.data
          // this.sourceSystemCodeOptions=this.sourceSystemCodeOptions.concat(response.data)
          // Handle a successful response (e.g., show a success message)
        }
      },
      (error) => {
        console.error('There was an error retrieving the source code system:', error);
        // Handle API errors (e.g., show an error message)
      }
    );

  }

  viewSelected(item: any): void {
    const ss_cd = item.ss_cd;
    const orn_no = item.orn_no;
    const total_amt = item.total_amt;
    const order_status = item.order_status;
    const rcpt_no = item.rcpt_no;
    const mtt_id = item.mtt_id;
    const rms_type = item.rms_type;

    this.router.navigate(['/mtt-details'], { state: { ss_cd, orn_no, total_amt, order_status, rcpt_no, mtt_id, rms_type } });
    // this.router.navigate(['/fms-ledger-listing', fmsId]);
  }

  // addSelected(): void {
  //   this.DefaultBox();
  //   const dialogRef = this.dialog.open(FmsAddComponent, {
  //     width: '50%',
  //   });

  //   dialogRef.afterClosed().subscribe((result) => {
  //     if (result === 'inserted') {
  //       this.addBox = true;
  //     }
  //     this.refreshMainPage();
  //   });
  // }

  apply(): void {
    this.loadData();
    this.rightSectionCollapsed = true;
  }

  reset(): void {
    this.ss_cd = null;
    this.orn_no = null;
    this.total_amt = null;
    this.rcpt_no = null;
    this.selectedState = null;
    this.selectedSourceSystemCodes = [];
    // this.selected = {
    //   start: moment().subtract(1, 'month'),
    //   end: moment(),
    // };
    this.bsValue = new Date();
    this.minDate.setMonth(this.bsValue.getMonth() - 1);
    this.selected = [];
    // this.selected = [this.minDate, this.bsValue];
    // this.selected[1].setDate(this.selected[1].getDate() + 1);

    this.bsValue2 = new Date();
    this.minDate2.setMonth(this.bsValue.getMonth() - 1);
    this.selected2 = [];
    this.selectedRMSType = null;
    // this.selected2 = [this.minDate, this.bsValue];
    // this.selected2[1].setDate(this.selected[1].getDate() + 1);
  }

  refreshMainPage(): void {
    this.page = 1;
    this.loadData();
  }

  isTransactionDateValid(): boolean {
  // Check if any filter is filled (not null, not empty string, not empty array)
  const anyFilterApplied =
    (this.selectedSourceSystemCodes && this.selectedSourceSystemCodes.length > 0) ||
    // (this.orn_no && this.orn_no.trim() !== '') ||
    (this.total_amt !== null && this.total_amt !== undefined && this.total_amt !== '') ||
    (this.selectedState && this.selectedState !== '') ||
    // (this.rcpt_no && this.rcpt_no.trim() !== '') ||
    (this.selectedRMSType && this.selectedRMSType !== '') ||
    (this.selected && this.selected.length === 2); // receipt date

  // If any filter is applied, transaction date must be filled
  if (anyFilterApplied) {
    return this.selected2 && this.selected2.length === 2;
  }

  // If no filter is applied, allow apply without transaction date
  return true;
}

  // async checkRecordInUse(fms_id: any): Promise<any> {
  //   const url = environment.apiUrl + '/api/fms/v1/checkfmsexist';

  //   const headers = new HttpHeaders({
  //     Authorization: environment.authKey,
  //     'Content-Type': 'application/json',
  //   });

  //   const body = {
  //     i_fms_id: fms_id,
  //   };
  //   const response: any = await this.http
  //     .post(url, body, { headers })
  //     .toPromise();
  //   try {
  //     this.checkResult = response.data;
  //     console.log('check: ' + response.data);
  //     return response.data;
  //   } catch (error) {
  //     console.error(error);
  //     return error;
  //   }
  // }

  // async toggleActivation(item: any) {
  //   this.isDisplay = true;
  //   this.isLoading = true;
  //   this.checkResult = 0;
  //   // Toggle the local status for a responsive UI
  //   if (item.isActive === false) {
  //     item.isActive = 0;
  //   } else {
  //     item.isActive = 1;
  //   }

  //   if (item.isActive == 1) {
  //     //CHECK RECORD IN USE
  //     this.checkResult = await this.checkRecordInUse(item.fmsId);
  //     console.log('checkResult: ' + this.checkResult);
  //   }

  //   if (this.checkResult == 1) {

  //     const url = environment.apiUrl + '/api/fms/v1/updatefms';

  //     const headers = new HttpHeaders({
  //       Authorization: environment.authKey,
  //       'Content-Type': 'application/json',
  //     });

  //     const body = {
  //       i_fms_id: item.fmsId,
  //       i_fms_cd: item.fmsCd,
  //       i_is_active: item.isActive,
  //     };

  //     console.log('this is the body', body);

  //     this.http.post(url, body, { headers }).subscribe(
  //       (response: any) => {
  //         // Handle the response

  //         this.loadData();
  //         this.cd.detectChanges();
  //       },
  //       (error) => {
  //         // In case of error, revert the status change in the UI
  //         item.isActive =
  //           item.isActive === 1
  //             ? 0
  //             : 1;
  //         console.error('Error toggling status:', error);
  //         this.loadData();
  //         this.cd.detectChanges();
  //     }
  //     );

  //       const url2 = environment.apiUrl + '/api/fms/v1/fmsactivated';

  //       const body2 = {
  //         i_fms_id: item.fmsId,
  //       };

  //       console.log('this is the body', body2);

  //       this.http.post(url2, body2, { headers }).subscribe(
  //         (response: any) => {
  //           // Handle the response
  //           this.cd.detectChanges();
  //         },
  //         (error) => {
  //           // In case of error, revert the status change in the UI
  //           item.isActive =
  //             item.isActive === 1
  //               ? 0
  //               : 1;
  //           console.error('Error toggling status:', error);
  //           this.loadData();
  //           this.cd.detectChanges();
  //       }
  //       );
  //     }


  //     else if(item.isActive == 0){
  //     const url = environment.apiUrl + '/api/fms/v1/updatefms';

  //     const headers = new HttpHeaders({
  //       Authorization: environment.authKey,
  //       'Content-Type': 'application/json',
  //     });

  //     const body = {
  //       i_fms_id: item.fmsId,
  //       i_fms_cd: item.fmsCd,
  //       i_is_active: item.isActive,
  //     };

  //     console.log('this is the body', body);

  //     this.http.post(url, body, { headers }).subscribe(
  //       (response: any) => {
  //         // Handle the response

  //         this.loadData();
  //         this.cd.detectChanges();
  //       },
  //       (error) => {
  //         // In case of error, revert the status change in the UI
  //         item.isActive =
  //           item.isActive === 1
  //             ? 0
  //             : 1;
  //         console.error('Error toggling status:', error);
  //         this.loadData();
  //         this.cd.detectChanges();
  //     }
  //     );
  //     if(item.isActive ==0){
  //       this.showDeactiveAlertBox();
  //       }
  //    }

  //    else{
  //     if(item.isActive ==0){
  //     this.showDeactiveAlertBox();
  //     }
  //     if(item.isActive ==1){
  //       this.showActivateAlertBox();
  //       }
  //     item.isActive =
  //       item.isActive === 1
  //         ? 1
  //         : 0;
  //     this.loadData();
  //     this.cd.detectChanges();
  //   }
  // }

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


