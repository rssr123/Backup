import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Router } from '@angular/router';
import { NgbPaginationConfig } from '@ng-bootstrap/ng-bootstrap';
import { environment } from 'src/environments/environment';
import { MatDialog } from '@angular/material/dialog';
import { ParamService } from 'src/app/core/services/param.service';
import { ParamData } from 'src/app/core/models/param.interface';
import { TranslateService } from '@ngx-translate/core';
import { GlobalService } from 'src/app/shared/global.service';
import { formatDate } from '@angular/common';
import { billinglisting } from 'src/app/core/models/billinglisting.interface';
import { AuthService } from 'src/app/core/services/auth.service';
import { perm } from 'src/permissions/perm';

@Component({
  selector: 'app-billing-cancellation-listing',
  templateUrl: './billing-cancellation-listing.component.html',
  styleUrls: ['./billing-cancellation-listing.component.scss']
})
export class BillingCancellationListingComponent {
  page = environment.DefaultPage;
  itemsPerPage = environment.ItemPerPage;
  dropDownSize = environment.DropDownSize;
  model: billinglisting[] = [];
  hiddenModel: billinglisting[] = [];

  totalRecordsHidden: number = 0;
  totalRecords: number = 0;
  billingNo: String | null = null;
  customerID: String | null = null;
  billingStatusStr: String | null = null;
  selected: Date[] | null = null;

  isDisplay: boolean = false;
  //billingStatus: OTCBank[] = [];
  currentUser: String | null = null;
  currentName:String | null = null;
  isUser: Boolean = false;
  roles: String | null = 'ANONYMOUS';

  states: ParamData[] = [];

  // toogle start
  rightSectionCollapsed: boolean = true;

  // default pagination
  selectedValue = environment.dropdownOptions[0];
  dropdownOptions = environment.dropdownOptions;

  showResultAlert = false;

  isLoading: boolean = true;
  isLoadingPerm: boolean = true;
  isLoadingList: boolean = true;
  permCheck = perm.Billing_Cancellation_Listing;
  permCheckApp = perm.Billing_Cancellation_Approval;
  permCheckString = this.permCheck + ',' + this.permCheckApp;
  permCheckReturnString = ""; // variable to store allowed permission for the user
  permAllow: number = 0;
  permAllowApp: number = 0;

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
    this.authService.getUsername().subscribe(response =>{
      this.currentUser = response;
      if(this.currentUser == null || this.currentUser == 'undefined' || this.currentUser == '' || this.currentUser == 'Anonymous')
          this.currentUser = 'Anonymous';
      else
        this.roles = this.authService.roles;
      this.authService.getName().subscribe(response =>{
        this.currentName = response;
        if(this.currentName == null || this.currentName == 'undefined' || this.currentName == '' || this.currentName == 'Anonymous')
            this.currentName = 'Anonymous';
      });
    });

    this.loadPermission();
    this.loadStatusTypes();
    this.loadData();
  }

  LoadData(singleItem: number) {
    this.selectedValue = singleItem;
    this.itemsPerPage = this.selectedValue;
    this.loadData();
  }

  //loadData Start
  loadData() {
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    this.isDisplay = true;
    this.isLoading = true;
    const url = environment.apiUrl + '/api/billing/v1/getcancelbillist';

    const Body: any = {
      i_page: this.page,
      i_size: this.itemsPerPage,
      i_billing_no: '',
      i_cust_id: '',
      i_bil_wf_status: '', 
      i_dt_start: null, 
      i_dt_end: null
    };

    if (this.billingNo && this.billingNo.trim()) {
      Body.i_billing_no = this.billingNo;
    }

    if (this.customerID && this.customerID.trim()) {
      Body.i_cust_id = this.customerID;
    }
    /*
    if (this.selected) {
      Body.i_dt_start = formatDate(this.selected[0], 'YYYY-MM-dd', 'en');
      this.selected[1].setDate(this.selected[1].getDate() + 1);
      Body.i_dt_end = formatDate(this.selected[1], 'YYYY-MM-dd', 'en');
    }
    */
    if (this.selected && this.selected.length == 2) {
      Body.i_dt_start = this.selected[0]; 
      Body.i_dt_end = this.selected[1];
    }

    if (this.billingStatusStr) {
      Body.i_bil_wf_status = this.billingStatusStr;
    }

    console.log(Body);

    if(!this.rightSectionCollapsed)
      this.rightSectionCollapsed= false;
    
    this.http.post(url, Body, { headers }).subscribe(
      (response: any) => {
        if (response.data.length != 0) {
          this.hiddenModel = response.data.billing_list;
          this.totalRecordsHidden = response.data.total;
          this.isLoadingList = false;

          if(!this.isLoadingPerm){
            this.model = this.hiddenModel;
            this.totalRecords = this.totalRecordsHidden;
            this.isLoading = false;
          }
        }
        else{
          this.showResultAlertBox();
          this.isLoading = false;
        }
      },
      (error) => {
        console.error(error);
        this.isLoading = false;
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
    this.customerID = null;
    this.billingNo = null;
    this.billingStatusStr = null;
    this.selected = null;
  }

  refreshMainPage(): void {
    this.page = 1;
    this.loadData();
  }

  isCancelAllowed(bil_wf_status: string, unpaid: number){
    if(bil_wf_status == 'WF-A' || bil_wf_status == 'U' || bil_wf_status == 'WF-CR' || bil_wf_status == 'WF-AA'
        || bil_wf_status == 'WF-AR' || (bil_wf_status == 'P' && unpaid > 0))
      return true;
    return false;
  }

  navigateScreen(item: billinglisting){
    const billing_no = item.billing_no;
    const owner = item.created_by;
    const action = item.action;
    const bil_wf_status = item.bil_wf_status.trim();

    this.isUser = this.currentUser == owner || this.currentName == owner;
    if(bil_wf_status.includes('WF-CN')){
      //if((this.isUser && (action == 'Query Requester' || item.bil_wf_status.includes(' Q'))) || this.roles!.includes('FINANCEADMIN'))
      if((this.isUser && (action == 'Query Requester' || item.bil_wf_status.includes(' Q'))) || this.permAllowApp > 0)
        this.router.navigate(['/billing-cancellation-listing/billing-cancellation-approval'], { queryParams: { billing_no }});
      else
        this.router.navigate(['/billing-cancellation-listing/billing-details'], { queryParams: { billing_no }});
    }
    //else if((this.isUser || this.roles!.includes('FINANCEADMIN')) && this.isCancelAllowed(bil_wf_status, item.unpaid))
    else if((this.isUser || this.permAllowApp > 0) && this.isCancelAllowed(bil_wf_status, item.unpaid))
      this.router.navigate(['/billing-cancellation-listing/billing-cancellation'], { queryParams: { billing_no }});
    else
      this.router.navigate(['/billing-cancellation-listing/billing-details'], { queryParams: { billing_no }});    
  }

  bilWfStatuses: ParamData[] = [];
  billingStatusOptions: ParamData[] = [];

  loadStatusTypes(){
      this.ParamService.getStates(environment.DefaultPage.toString(), environment.ItemPerPage.toString(), '', 'Billing-Status').subscribe((response: any) => {
        if (response.data.length >= 0) {
          this.bilWfStatuses = response.data as ParamData[];

          //Remove all statuses that are not cancel
          var i = this.bilWfStatuses.length;
          while(i--)
            if(!this.bilWfStatuses[i].nm_en.includes('Cancel'))
              this.bilWfStatuses.splice(i,1);

          this.bilWfStatuses.sort((a, b) => a.nm_en.localeCompare(b.nm_en)); // sorting
        } 
        else
          console.error('Invalid response format:', response);
      },
      (error) => {
        console.error('There was an error retrieving the status:', error);
      }
    );
  }

  getParamDescWF(paramCd : String){
    var searchCd = paramCd.trim();
    var isQuery = false;
    if(paramCd.includes(' Q')){
      searchCd = paramCd.split(' Q')[0];
      isQuery = true;
    }
    for(const i of this.bilWfStatuses)
      if(i.param_cd == searchCd)
        return i.nm_en + (isQuery ? ' Query' : '');
    return paramCd;
  }

  getBillingStatusName(bil_status: string | null): string {
    if (!bil_status) {
      return ''; // Return a default value if stateCode is null
    }
    const billingStatus = this.bilWfStatuses.find((option) => option.param_cd === bil_status);
    return billingStatus ? billingStatus.nm_en : bil_status; // Return the name if found, otherwise return the code
  }

  loadPermission() {
    this.authService.checkUserRole(this.currentUser as string, this.permCheckString)
        .subscribe((response: any) => {
        this.permCheckReturnString = response.data;
        this.permAllow = this.permCheckReturnString.includes(perm.Billing_Cancellation_Listing) ? 1 : 0;
        this.permAllowApp = this.permCheckReturnString.includes(perm.Billing_Cancellation_Approval) ? 1 : 0;
        if (this.permAllow === 0) {
          if(environment.production)
            this.router.navigate(['/access-denied']);
          console.log(response.data);
          alert('bad permission: ' + this.permCheckReturnString);  
        }
        this.isLoadingPerm = false;

        if(!this.isLoadingList){
          this.model = this.hiddenModel;
          this.totalRecords = this.totalRecordsHidden;
          this.isLoading = false;
        }
      },
      (error: any) => {
        if(environment.production)
          this.router.navigate(['/access-denied']);
        console.log(error);
        alert('permission load failed');
        this.isLoadingPerm = false;
        this.model = this.hiddenModel;
        this.totalRecords = this.totalRecordsHidden;
        this.isLoading = false;
      }
    );
  }
}