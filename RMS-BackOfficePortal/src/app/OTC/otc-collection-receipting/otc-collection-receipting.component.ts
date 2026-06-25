
import { ChangeDetectorRef, Component } from '@angular/core';
import { environment } from 'src/environments/environment';
import { Router } from '@angular/router';
import { OTCCollectionReceipting } from 'src/app/core/models/otc-collection-receipting.interface';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { MatDialog } from '@angular/material/dialog';
import { NgbPaginationConfig } from '@ng-bootstrap/ng-bootstrap';
import { TranslateService } from '@ngx-translate/core';
import { AuthService } from 'src/app/core/services/auth.service';
import { GlobalService } from 'src/app/shared/global.service';
import { ParamService } from 'src/app/core/services/param.service';
import { perm } from 'src/permissions/perm';

@Component({
  selector: 'app-otc-collection-receipting',
  templateUrl: './otc-collection-receipting.component.html',
  styleUrls: ['./otc-collection-receipting.component.scss']
})
export class OtcCollectionReceiptingComponent {
  page = environment.DefaultPage;
  itemsPerPage = environment.ItemPerPage;
  model: OTCCollectionReceipting[] = [];
  totalRecords: number = 0;
  isDisplay: boolean = true;

  permOTC = perm.OTC_Collection_Receipting_View_Listing_Page + "," + perm.OTC_Collection_Receipting_Pay_Now_Button + "," + perm.OTC_Collection_Receipting_View_Button; // all the perm_cd for this module seperated with comma
  permOTCAllow = ""; // variable to store allowed permission for the user
  permListAllow: number = 0; // if 0 then not allow to view listing page, else allow
  permPayAllow: number = 0; // if 0 then not allow to add tax code, else allow
  permViewAllow: number = 0; // if 0 then not allow to edit tax code, else allow

  //default pagination
  selectedValue = environment.dropdownOptions[0];
  dropdownOptions = environment.dropdownOptions;
  dropDownSize = environment.DropDownSize;

  collectionSlipNo: String | null = null;
  orderReferenceNo: String | null = null;
  customerName: String | null = null;
  customerPhone: String | null = null;
  isLoading: boolean = false;

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
    // this.selected = null;
    // this.loadStates();
    console.log(this.authService.username);
    this.loadData();
  }

  LoadData(singleItem: number) {
    this.selectedValue = singleItem;
    this.itemsPerPage = this.selectedValue;
    this.loadData();
  }

  loadData() {
    this.authService.checkUserRole(this.authService.username, this.permOTC)
      .subscribe(
        (response: any) => {
          this.permOTCAllow = response.data;
          this.permListAllow = this.permOTCAllow.includes(perm.OTC_Collection_Receipting_View_Listing_Page) ? 1 : 0;
          this.permPayAllow = this.permOTCAllow.includes(perm.OTC_Collection_Receipting_Pay_Now_Button) ? 1 : 0;
          this.permViewAllow = this.permOTCAllow.includes(perm.OTC_Collection_Receipting_View_Button) ? 1 : 0;
  
          console.log(this.permListAllow, this.permPayAllow, this.permViewAllow);
  
          if (this.permListAllow === 0) {
            console.log("access-denied");
            this.router.navigate(['/access-denied']);
            return; // Exit the function to prevent further execution
          }
  
          console.log(this.permListAllow, this.permPayAllow, this.permViewAllow);
  
          const headers = new HttpHeaders({
            Authorization: environment.authKey,
            'Content-Type': 'application/json',
          });
  
          this.isDisplay = true;
          this.isLoading = true;
          const url = environment.apiUrl + '/api/OTCCR/v1/getcollectioninfo';
  
          const Body: any = {
            i_page: this.page.toString(),
            i_size: this.itemsPerPage.toString(),
          };
  
          if (this.collectionSlipNo && this.collectionSlipNo.trim()) {
            Body.i_coll_slip_no = this.collectionSlipNo;
          }
          if (this.orderReferenceNo && this.orderReferenceNo.trim()) {
            Body.i_orn_no = this.orderReferenceNo;
          }
          if (this.customerName && this.customerName.trim()) {
            Body.i_cust_nm = this.customerName;
          }
          if (this.customerPhone && this.customerPhone.trim()) {
            Body.i_cust_phone = this.customerPhone;
          }
  
          this.http.post(url, Body, { headers }).subscribe(
            (response: any) => {
              this.model = response.data;
              console.log(response.data);
              this.totalRecords = response.data.length > 0 ? response.data[0].total : 0;
              this.isLoading = false;
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
  
  clearFields(): void {
    this.collectionSlipNo = null;
    this.orderReferenceNo = null;
    this.customerName = null;
    this.customerPhone = null;
  }

  navigateToPaymentScreen(item: any): void {
    const coll_slip_no = item.coll_slip_no;
    const orn_no = item.orn_no;
    
    this.router.navigate(['/otc-payment-screen', coll_slip_no], { queryParams: { orn_no }, state: { item } });
  }

  navigateToReceiptScreen(item: any): void {
    const coll_slip_no = item.coll_slip_no;
    const orn_no = item.orn_no;
    const curr_page = "otc-collection-receipting";
    this.router.navigate(['/otc-receipt-screen', coll_slip_no], { queryParams: { orn_no, curr_page }, state: { item } });
  }

}