import { formatDate } from '@angular/common';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { NgbPaginationConfig } from '@ng-bootstrap/ng-bootstrap';
import { PGReconList } from 'src/app/core/models/pg-recon';
import { ParamData } from 'src/app/core/models/param.interface';
import { Systemstatus } from 'src/app/shared/enums/systemstatus';
import { environment } from 'src/environments/environment';
import { ParamService } from '../../../core/services/param.service';
import { GlobalService } from 'src/app/shared/global.service';
import { TranslateService } from '@ngx-translate/core';
import { perm } from 'src/permissions/perm';
import { AuthService } from 'src/app/core/services/auth.service';
import { OTCReceiptCancellationListing } from 'src/app/core/models/otc-receipt-cancellation.interface';
import { CounterCheckInStatus } from 'src/app/core/services/otc-counter-status.service';

@Component({
  selector: 'app-receipt-no-validation',
  templateUrl: './receipt-no-validation.component.html',
  styleUrls: ['./receipt-no-validation.component.scss']
})
export class ReceiptNoValidationComponent implements OnInit {

  receiptNo: string | null = null;
  oderRefNo: string | null = null;
  custName: string | null = null;
  // entityType: string | null = null;
  // entityNo: string | null = null;

  entityTypeOptions: any =
    [{ value: 'B', label: 'Business' },
    { value: 'C', label: 'Company' },
    { value: 'I', label: 'Individual' },
    { value: 'L', label: 'LLP' }]

  page = environment.DefaultPage;
  itemsPerPage = environment.ItemPerPage;
  totalRecords: number = 0;
  model: OTCReceiptCancellationListing[] = [];
  isReadOnly = false;
  isEmptyResult = false;
  totalCount = 0;
  errorMessage: string | null = null;
  addBox: boolean = false;
  viewBox: boolean = false;

  taskId: String | null = null;
  settlementDate: Date | null = null;
  merchantId: String | null = null;
  taskStatus: String | null = null;
  reconStatus: String | null = null;
  fileName: string | null = null;
  file_content = "";
  alertMessage: string | undefined = undefined;

  isDisplay: boolean = true;
  isLoading: boolean = false;

  //toogle start
  rightSectionCollapsed: boolean = true;

  //default pagination
  selectedValue = environment.dropdownOptions[0];
  dropdownOptions = environment.dropdownOptions;

  fileInput: File | null = null;
  dt_settlement: Date | null = null;//= new Date();
  formatError: boolean = false;
  duplicateError: boolean = false;
  serverError: boolean = false;

  disableButton: boolean = true;

  //otc
  counterId: string | null = null;
  otcCounterId: number | null = null;
  OTCCheckedIn: number = 0;

  LoadData(singleItem: number) {
    this.selectedValue = singleItem;
    this.itemsPerPage = this.selectedValue;
    this.loadData();
  }

  toggleRightSection() {
    this.rightSectionCollapsed = !this.rightSectionCollapsed;
  }
  //toogle end

  showResultAlert = false;

  showResultAlertBox() {
    this.showResultAlert = true;
    setTimeout(() => (this.showResultAlert = false), 10000);
  }

  DefaultBox() {
    this.addBox = false;
  }

  AlertBoxInitialize() {
    if (this.addBox) {
      // this.showInsertAlertBox();
    }
  }

  // Configuring Permissions for User and roles variables
  permOTC = perm.OTC_Receipt_Cancellation_View_Listing_Page
  permOTCAllow = ""; // variable to store allowed permission for the user
  permListAllow: number = 0; // if 0 then not allow to view listing page, else allow

  constructor(
    private http: HttpClient,
    config: NgbPaginationConfig,
    private router: Router,
    public dialog: MatDialog,
    private ParamService: ParamService,
    private translateService: TranslateService,
    private globalService: GlobalService,
    private authService: AuthService,
    public counterCheckInStatus: CounterCheckInStatus,
  ) {
    config.maxSize = environment.PaginationMaxSize;
    config.boundaryLinks = true;
    this.translateService.setDefaultLang(this.globalService.getGlobalValue());
    this.translateService.use(this.globalService.getGlobalValue());
  }

  async ngOnInit() {
    this.alertMessage = history.state.alert_msg;
    if (this.alertMessage !== undefined) {
      this.showResultAlertBox();
    }
    history.replaceState({ ...history.state, alert_msg: undefined }, '');
    this.clear();
    await this.loadCounterInfo();
    this.loadData(); // must last
  }

  clear() {
    this.receiptNo = null;
    this.oderRefNo = null;
    this.custName = null;
  }

  viewSelected(item: any) {

    const mtt_id = item.mtt_id;
    const otc_id = item.otc_id;
    const otc_counter_id = item.otc_counter_id;
    const counter_id = item.counter_id;
    const otc_pymt_mode = item.otc_pymt_mode;
    this.router.navigate(['/otc-rcpt-dets'], { state: { mtt_id, otc_id, otc_counter_id, counter_id, otc_pymt_mode } });

  }

  //loadData Start
  loadData() {
    this.authService.checkUserRole(this.authService.username, this.permOTC)
      .subscribe(
        (response: any) => {
          this.permOTCAllow = response.data;
          this.permListAllow = this.permOTCAllow.includes(perm.OTC_Receipt_Cancellation_View_Listing_Page) ? 1 : 0;

          console.log(this.permListAllow,);
          if (this.permListAllow === 0) {
            console.log("access-denied");
            this.router.navigate(['/access-denied']);
            return; // Exit the function to prevent further execution
          }

          if(this.otcCounterId == null || this.otcCounterId == undefined){
            console.log("OTC Counter ID is null or undefined in loadData()");
            return;
          }


          this.isDisplay = true;
          this.isLoading = true;
          const url = environment.apiUrl + '/api/otcrcptccl/v1/getotcreceiptcancellationlisting';

          // Set your authorization header
          const headers = new HttpHeaders({
            Authorization: environment.authKey,
            'Content-Type': 'application/json',
          });

          const Body: any = {
            i_page: this.page.toString(),
            i_size: this.itemsPerPage.toString(),
          };

          if (this.receiptNo && this.receiptNo.trim()) {
            Body.i_rcpt_no = this.receiptNo;
          }

          if (this.oderRefNo && this.oderRefNo.trim()) {
            Body.i_orn_no = this.oderRefNo;
          }

          if (this.custName && this.custName.trim()) {
            Body.i_cust_nm = this.custName;
          }

          Body.i_otc_counter_id = this.otcCounterId;

            this.http.post(url, Body, { headers }).subscribe(
              (response: any) => {
                console.log(response.data);
                this.model = response.data;
                if (response.data.length == 0) {
                  this.totalRecords = 0;
                  //this.isEmptyResult = true;
                  //this.isDisplay = false;
                  this.showResultAlert; // Remove parentheses here
                  this.isLoading = false;
                } else {
                  this.totalRecords = response.data[0].total;
                 // this.isEmptyResult = false;
                  // this.AlertBoxInitialize();
                  this.isLoading = false;
                }
                // console.log(response.data);
                //  console.log(this.totalRecords);
              },
              (error: any) => {
                console.error(error);
                this.isLoading = false;
                // Handle errors here
              }
            );
          });
  }
  //loadData End

  apply(): void {
    this.loadData();
    this.rightSectionCollapsed = true;
  }


  //prevent manual key for date
  onKeyDown(event: KeyboardEvent): void {
    if (event.key === 'Backspace') {
      return;
    }
    // Prevent manual key entry
    event.preventDefault();
  }

  async loadCounterInfo() {
    const permUrl = environment.apiUrl + '/api/otc/v1/checkinstatus';
    // Make the HTTP GET request
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json'
    });
    var requestBody: { [k: string]: any } = {
      i_session_id: localStorage.getItem('otcSession')
    };
    try {
      const response: any = await this.http.post(permUrl, requestBody, { headers : headers }).toPromise();
      this.counterCheckInStatus.data = response.data;

      if (this.counterCheckInStatus.data.counter_id?.length > 0) {
        this.otcCounterId = this.counterCheckInStatus.data.otc_counter_id;
        this.counterId = this.counterCheckInStatus.data.counter_id;
        this.OTCCheckedIn = 1;
        console.log('Counter ID: ' + this.counterCheckInStatus.data.counter_id);
        console.log('OTC Counter ID: ' + this.counterCheckInStatus.data.otc_counter_id);
      }
    } catch (error) {
      console.log(error);
      this.counterCheckInStatus.data = ''; // still notify observer
      this.OTCCheckedIn = 0;
    }
  }

}
