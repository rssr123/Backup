import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Router } from '@angular/router';
import { NgbPaginationConfig } from '@ng-bootstrap/ng-bootstrap';
import { environment } from 'src/environments/environment';
import { MatDialog } from '@angular/material/dialog';
import { ParamService } from '../core/services/param.service';
import { ParamData } from '../core/models/param.interface';
import { TranslateService } from '@ngx-translate/core';
import { GlobalService } from '../shared/global.service';
import { NonBillingListing, OTCBank } from 'src/app/core/models/otc-collection-returned-cheque.interface';
import { perm } from 'src/permissions/perm';
import { AuthService } from '../core/services/auth.service';

@Component({
  selector: 'app-non-billing-listing',
  templateUrl: './non-billing-listing_v2.component.html',
  styleUrls: ['./non-billing-listing_v2.component.scss']
})
export class NonBillingListingComponent {
  page = environment.DefaultPage;
  itemsPerPage = environment.ItemPerPage;
  dropDownSize = environment.DropDownSize;
  model: NonBillingListing[] = [];

  totalRecords: number = 0;
  billingNo: String | null = null;
  customerID: String | null = null;
  entityName: String | null = null;
  entityNo: String | null = null;
  chequeid: String | null = null;
  chequeno: String | null = null;
  billingStatusStr: String | null = null;

  isDisplay: boolean = false;
  isLoading: boolean = false;
  isFirstLoad: boolean = true;
  billingStatus: ParamData[] = [];

  states: ParamData[] = [];
  statusMapping: { [key: string]: string } = {};

  // toogle start
  rightSectionCollapsed: boolean = true;

  // default pagination
  selectedValue = environment.dropdownOptions[0];
  dropdownOptions = environment.dropdownOptions;

  permNB = perm.Non_Billing_Listing_View; // all the perm_cd for this module seperated with comma
  permNBAllow = ""; // variable to store allowed permission for the user
  permNBListingAllow: number = 0; // if 0 then not allow to view listing page, else allow

  showResultAlert = false;

  showResultAlertBox() {
    if(this.isFirstLoad)
      return;
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
    // this.fetchBillingStatus();
    this.loadNonBillingStatus();
    this.loadStates();
    // load data must be place at last
    this.loadData();
  }

  LoadData(singleItem: number) {
    this.selectedValue = singleItem;
    this.itemsPerPage = this.selectedValue;
    this.loadData();
  }

  //loadData Start
  loadData() {

    this.authService.checkUserRole(this.authService.username, this.permNB)
      .subscribe(
        (response: any) => {
          this.permNBAllow = response.data;
          this.permNBListingAllow = this.permNBAllow.includes(perm.Non_Billing_Listing_View) ? 1 : 0;
          if (this.permNBListingAllow === 0) {
            console.log("access-denied");
            this.router.navigate(['/access-denied']);
            return; // Exit the function to prevent further execution
          }
          this.isDisplay = true;
          this.isLoading = true;
          const url = environment.apiUrl + '/api/OTCRC/v1/getnonbillinglisting';

          // Set your authorization header
          const headers = new HttpHeaders({
            Authorization: environment.authKey,
            'Content-Type': 'application/json',
          });

          const Body: any = {
            i_page: this.page.toString(),
            i_size: this.itemsPerPage.toString(),
          };

          if (this.entityName && this.entityName.trim()) {
            Body.i_ent_nm = this.entityName;
          }

          if (this.entityNo && this.entityNo.trim()) {
            Body.i_ent_no = this.entityNo;
          }

          if (this.billingNo && this.billingNo.trim()) {
            Body.i_non_bil_no = this.billingNo;
          }

          if (this.customerID && this.customerID.trim()) {
            Body.i_cust_id = this.customerID;
          }

          if (this.billingStatusStr) {
            Body.i_bil_status = this.billingStatusStr;
          }

          if (this.chequeid && this.chequeid.trim()) {
            Body.i_che_id = this.chequeid;
          }

          if (this.chequeno && this.chequeno.trim()) {
            Body.i_che_no = this.chequeno;
          }

          this.http.post(url, Body, { headers }).subscribe(
            (response: any) => {
              this.model = response.data;
              if (response.data.length == 0) {
                this.totalRecords = 0;
                // this.showResultAlertBox();
                this.isLoading = false;
                this.isFirstLoad = false;
              }
              else {
                this.totalRecords = response.data[0].total;
                this.isLoading = false;
                this.isFirstLoad = false;
              }
              console.log(response.data);
              console.log(this.totalRecords);
            },
            (error) => {
              console.error(error);
              this.isLoading = false;
            }
          );
        }, (error) => {
          console.error('Error fetching user role permissions', error);
        }
      );
  }

  // loadData End
  apply(): void {
    this.isLoading = true;
    this.loadData();
    this.rightSectionCollapsed = true;
  }

  reset(): void {
    this.entityName = null;
    this.entityNo = null;
    this.customerID = null;
    this.billingNo = null;
    this.billingStatusStr = null;
  }

  refreshMainPage(): void {
    this.page = 1;
    this.loadData();
  }

  loadStates() {
    this.ParamService.getStates('1', '100', '', 'Status').subscribe(
      (response: any) => {
        if (response && response.data && Array.isArray(response.data)) {
          this.states = response.data as ParamData[];
          //this.states.push({ param_cd: '', nm_en: 'All', nm_bm: 'All', total: 5 }); //add 'All' options
          this.states.push(response.data);
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

  loadNonBillingStatus() {
    this.ParamService.getStates('1', '100', '', 'Non-BillingStatus').subscribe(
      (response: any) => {
        if (response && response.data && Array.isArray(response.data)) {
          this.billingStatus = response.data as ParamData[];
          //this.states.push({ param_cd: '', nm_en: 'All', nm_bm: 'All', total: 5 }); //add 'All' options
          // this.billingStatus.push(response.data);
          // this.billingStatus = [...this.billingStatus, ...response.data];
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

  // fetchBillingStatus(): void {
  //   const headers = new HttpHeaders({
  //     Authorization: environment.authKey,
  //     'Content-Type': 'application/json',
  //   });
  //   const url = environment.apiUrl + '/api/rms/v1/getbillingstatus';
  //   const Body: any = {
  //   };
  //   this.http.post(url, Body, { headers }).subscribe(
  //     (response: any) => {
  //       this.billingStatus = response.data;
  //       // Dynamically populate statusMapping from API response
  //       this.billingStatus.forEach((status: { param_cd: string; nm_en: string }) => {
  //         this.statusMapping[status.param_cd] = status.nm_en;
  //       });
  //       this.isLoading = false;
  //     },
  //     (error) => {
  //       console.error(error);
  //       this.isLoading = false;
  //     }
  //   );
  // }

  navigateToDetailsScreen(item: any): void {
    const bill_no = item.non_bil_no;
    const bill_status = item.bil_status;
    this.router.navigate(['/non-billing-details', bill_no], { queryParams: { bill_status }, state: { item } });

  }
}
