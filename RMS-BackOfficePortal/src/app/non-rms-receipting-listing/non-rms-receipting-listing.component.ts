import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Router } from '@angular/router';
import { NgbPaginationConfig } from '@ng-bootstrap/ng-bootstrap';
import { environment } from 'src/environments/environment';
import { MatDialog } from '@angular/material/dialog';
import { ParamService } from '../core/services/param.service';
import { ParamData } from 'src/app/core/models/param.interface';
import { TranslateService } from '@ngx-translate/core';
import { GlobalService } from '../shared/global.service';
import { NonRMSReceipting } from '../core/models/non-rms-receipting.interface';
import { perm } from 'src/permissions/perm';
import { AuthService } from '../core/services/auth.service';

@Component({
  selector: 'app-non-rms-receipting-listing',
  templateUrl: './non-rms-receipting-listing.component.html',
  styleUrls: ['./non-rms-receipting-listing.component.scss']
})
export class NonRmsReceiptingListingComponent {
  page = environment.DefaultPage;
  itemsPerPage = environment.ItemPerPage;
  dropDownSize = environment.DropDownSize;
  model: NonRMSReceipting[] = [];
  totalRecords: number = 0;

  task_id: String | null = null;
  settlement_date: Date | null = null;
  merchant_id: String | null = null;
  task_status: String | null = null;
  settlement_status_option: String | null = null;
  date_upload: Date | null = null;

  isDisplay: boolean = false;
  isLoading: boolean = false;

  settlement_status: ParamData[] = [];
  taskstatus: ParamData[] = [];

  // toggle start
  rightSectionCollapsed: boolean = true;

  // default pagination
  selectedValue = environment.dropdownOptions[0];
  dropdownOptions = environment.dropdownOptions;

  permNRMS = perm.Non_RMS_Receipting_View_Listing_Page + "," + perm.Non_RMS_Receipting_Reconcile + "," + perm.Non_RMS_Receipting_View; // all the perm_cd for this module seperated with comma
  permNRMSAllow = ""; // variable to store allowed permission for the user
  permViewAllow: number = 0; // if 0 then not allow to view listing page, else allow
  permViewListingAllow: number = 0; // if 0 then not allow to view listing page, else allow
  permReconcileAllow: number = 0;

  showResultAlert = false;

  showResultAlertBox() {
    this.showResultAlert = true;
    setTimeout(() => this.showResultAlert = false, 2000);
  }

  showGenericAlert = false;
  showGenericAlertBox() {
    this.showGenericAlert = true;
    setTimeout(() => (this.showGenericAlert = false), 2000);
  }

  toggleRightSection() {
    this.rightSectionCollapsed = !this.rightSectionCollapsed;
  }

  constructor(
    private http: HttpClient,
    config: NgbPaginationConfig,
    private router: Router,
    public dialog: MatDialog,
    private ParamService: ParamService,
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
    this.loadSettlementStatus();
    this.loadTaskStatus();
    this.loadData();
  }

  LoadData(singleItem: number) {
    this.selectedValue = singleItem;
    this.itemsPerPage = this.selectedValue;
    this.loadData();
  }

  //loadData Start
  loadData() {
    this.authService.checkUserRole(this.authService.username, this.permNRMS)
      .subscribe(
        (response: any) => {
          this.permNRMSAllow = response.data;
          this.permViewListingAllow = this.permNRMSAllow.includes(perm.Non_RMS_Receipting_View_Listing_Page) ? 1 : 0;
          this.permReconcileAllow = this.permNRMSAllow.includes(perm.Non_RMS_Receipting_Reconcile) ? 1 : 0;
          this.permViewAllow = this.permNRMSAllow.includes(perm.Non_RMS_Receipting_View) ? 1 : 0;


          if (this.permViewListingAllow === 0) {
            console.log("access-denied");
            this.router.navigate(['/access-denied']);
            return; // Exit the function to prevent further execution
          }

          this.isDisplay = true;
          this.isLoading = true;
          const url = environment.apiUrl + '/api/RMSNR/v1/getrmsnonreceipting';

          // Set your authorization header
          const headers = new HttpHeaders({
            Authorization: environment.authKey,
            'Content-Type': 'application/json',
          });

          const Body: any = {
            i_page: this.page.toString(),
            i_size: this.itemsPerPage.toString(),
          };

          if (this.task_id && this.task_id.trim()) {
            Body.i_task_id = this.task_id;
          }

          if (this.settlement_date) {
            Body.i_settlement_date = this.formatDateForSP(this.settlement_date);
          }

          if (this.merchant_id && this.merchant_id.trim()) {
            Body.i_merchant_id = this.merchant_id;
          }

          if (this.task_status && this.task_status.trim()) {
            Body.i_task_status = this.task_status;
          }

          if (this.date_upload) {
            Body.i_date_uploaded = this.formatDateForSP(this.date_upload);
          }

          if (this.settlement_status_option && this.settlement_status_option.trim()) {
            Body.i_settle_status = this.settlement_status_option;
          }

          console.log(Body);


          this.http.post(url, Body, { headers }).subscribe(
            (response: any) => {
              this.model = response.data;
              if (response.data.length == 0) {
                this.totalRecords = 0;
                // this.showResultAlertBox();
                this.isLoading = false;
              }
              else {
                this.isLoading = true;
                this.totalRecords = response.data[0].total;
                this.isLoading = false;
              }
              console.log(response.data);
              console.log(this.totalRecords);
            },
            (error) => {
              console.error(error);
              this.isLoading = false;
            }
          );
        },
        (error) => {
          console.error('Error fetching user role permissions', error);
        }
      );
  }

  formatDateForSP(date: Date): string {
    if (!date) return '';

    const formattedDate = new Date(date);
    formattedDate.setHours(0, 0, 0, 0); // Set time to 00:00:00

    const year = formattedDate.getFullYear();
    const month = ('0' + (formattedDate.getMonth() + 1)).slice(-2); // Ensure two digits
    const day = ('0' + formattedDate.getDate()).slice(-2);

    return `${year}-${month}-${day} 00:00:00`;
  }

  //loadData End
  apply(): void {
    this.isLoading = true;
    this.loadData();
    this.rightSectionCollapsed = true;
  }

  reset(): void {
    this.task_id = null;
    this.settlement_date = null;
    this.merchant_id = null;
    this.task_status = null;
    this.settlement_status_option = null;
    this.date_upload = null;
  }

  refreshMainPage(): void {
    this.page = 1;
    this.loadData();
  }

  loadSettlementStatus() {
    this.ParamService.getStates('1', '100', '', 'ag-settlement-status').subscribe(
      (response: any) => {
        if (response && response.data && Array.isArray(response.data)) {
          this.settlement_status = response.data as ParamData[];
          //this.states.push({ param_cd: '', nm_en: 'All', nm_bm: 'All', total: 5 }); //add 'All' options
          // this.states.push(response.data);
          // this.states = [...this.states, ...response.data];
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

  loadTaskStatus() {
    this.ParamService.getStates('1', '100', '', 'ag-sale').subscribe(
      (response: any) => {
        if (response && response.data && Array.isArray(response.data)) {
          this.taskstatus = response.data as ParamData[];
          //this.states.push({ param_cd: '', nm_en: 'All', nm_bm: 'All', total: 5 }); //add 'All' options
          // this.states.push(response.data);
          // this.states = [...this.states, ...response.data];
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

  navigateToDetail(item: any): void {
    this.router.navigate(['/non-rms-receipting-details'], { state: { item } });
  }
}