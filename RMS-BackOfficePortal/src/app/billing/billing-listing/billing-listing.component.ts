import { ChangeDetectorRef, Component } from '@angular/core';
import { environment } from 'src/environments/environment';
import { Router } from '@angular/router';
import { billinglisting } from 'src/app/core/models/billinglisting.interface';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { MatDialog } from '@angular/material/dialog';
import { NgbPaginationConfig } from '@ng-bootstrap/ng-bootstrap';
import { TranslateService } from '@ngx-translate/core';
import { AuthService } from 'src/app/core/services/auth.service';
import { GlobalService } from 'src/app/shared/global.service';
import { ParamService } from 'src/app/core/services/param.service';
import { ParamData } from 'src/app/core/models/param.interface';
import { saveAs } from 'file-saver';
import { perm } from 'src/permissions/perm';

@Component({
  selector: 'app-billing-listing',
  templateUrl: './billing-listing_v2.component.html',
  styleUrls: ['./billing-listing_v2.component.scss']
})
export class BillListingComponent {
  page = environment.DefaultPage;
  itemsPerPage = environment.ItemPerPage;
  model: billinglisting[] = [];
  hiddenModel: billinglisting[] = [];
  bilWfStatuses: ParamData[] = [];
  bilType: ParamData[] = [];
  bilMthds: ParamData[] = [];
  sourceSysList1: String[] = [];
  sourceSysList2: Map<String, String> = new Map<String, String>();

  totalRecords: number = 0;
  totalRecordsHidden: number = 0;
  isDisplay: boolean = true;

  //default pagination
  selectedValue = environment.dropdownOptions[0];
  dropdownOptions = environment.dropdownOptions;
  dropDownSize = environment.DropDownSize;

  entNm: String | null = null;
  entNo: String | null = null;
  sourceSys: String | null = null;
  receiptNo: String | null = null;
  dateRange: Date[] | null = null;
  billingMthd: String | null = null;
  billingType: String | null = null;
  billingNo: String | null = null;
  billStatus: String | null = null;
  currentUser: String | null = null;
  roles: String | null = 'ANONYMOUS';
  csvData: any[] = [];

  filter: boolean = false;

  isLoading: boolean = true;
  isLoadingPerm: boolean = true;
  isLoadingList: boolean = true;
  permCheck = perm.Billing_Listing_Details + ',' + perm.Billing_Registration_Approval;
  permCheckReturnString = ""; // variable to store allowed permission for the user
  permAllow: number = 0;
  permAllowApp: number = 0;

  isBreadcrumbLoaded: boolean = false;
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
    this.authService.getUsername().subscribe(response =>{
      this.currentUser = response;
      if(this.currentUser == null || this.currentUser == 'undefined' || this.currentUser == '' || this.currentUser == 'Anonymous')
        this.currentUser = 'Anonymous';
      else
        this.roles = this.authService.roles;
    });

    this.waitForElm('.breadcrumb').then((elm) => {
      this.isBreadcrumbLoaded = true;
    });

    this.loadPermission();
    this.loadStatusTypes();
    this.loadSourceSytemData();
    this.loadBillingMethods();
    this.loadBillingTypes();
    this.loadData();
  }

  LoadData(singleItem: number) {
    this.selectedValue = singleItem;
    this.itemsPerPage = this.selectedValue;
    this.loadData();
  }

  filterButton(){
    this.filter = true;
    this.loadData();
  }

  loadData() {
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    this.isDisplay = true;
    this.isLoading = true;
    const url = environment.apiUrl + '/api/billing/v1/getbillist';

    const Body: any = {
      i_page: this.page,
      i_size: this.itemsPerPage,
      i_ent_nm: '', 
      i_ent_no: '', 
      i_ss_cd: '', 
      i_receipt_no: '',
      i_billing_mthd: '', 
      i_bil_wf_status: '', 
      i_dt_start: null, 
      i_dt_end: null, 
      i_b_type: '',
      i_billing_no: ''
    };
    if(this.filter){
      if (this.entNm && this.entNm.trim()) Body.i_ent_nm = this.entNm;
      if (this.entNo && this.entNo.trim()) Body.i_ent_no = this.entNo;
      if (this.sourceSys && this.sourceSys.trim()) Body.i_ss_cd = this.sourceSys;
      if (this.receiptNo && this.receiptNo.trim()) Body.i_receipt_no = this.receiptNo;
      if (this.dateRange && this.dateRange.length !=0) {
        Body.i_dt_start = this.dateRange[0]; 
        Body.i_dt_end = this.dateRange[1];
      }
      if (this.billingMthd && this.billingMthd.trim()) Body.i_billing_mthd = this.billingMthd;
      if (this.billingType && this.billingType.trim()) Body.i_b_type = this.billingType;
      if (this.billingNo && this.billingNo.trim()) Body.i_billing_no = this.billingNo;
      if (this.billStatus && this.billStatus.trim()) Body.i_bil_wf_status = this.billStatus;
    }    

    if(!this.rightSectionCollapsed)
      this.rightSectionCollapsed = true;

    this.http.post(url, Body, { headers }).subscribe(
      (response: any) => {
        if (response.data.length != 0) {
          this.hiddenModel = response.data.billing_list;
          this.totalRecordsHidden = response.data.total;
          this.isLoadingList = false;

          var debugFlag = localStorage.getItem('debug') == 'true' ? true : false;
          if(debugFlag){
            console.log('Debug');
            console.log(this.currentUser);
            console.log(response.data);
          }
          
          if(!this.isLoadingPerm){
            this.model = this.hiddenModel;
            this.totalRecords = this.totalRecordsHidden;
            this.isLoading = false;
          }
        }
        else
          this.isLoading = false;
      },
      (error) => {
        console.error(error);
        this.isLoading = false;
      }
    );
  }

  clearFields(): void {
    this.filter = false;
    this.entNm = null;
    this.entNo = null;
    this.sourceSys = null;
    this.receiptNo = null;
    this.dateRange = null;
    this.billingMthd = null;
    this.billingType = null;
    this.billingNo = null;
    this.billStatus = null;
  }

  navigateScreen(item: any): void{
    const status = item.bil_wf_status;
    const action = item.action;
    const owner = item.created_by;
    const billing_no = item.billing_no;
    if(status.includes('WF-N')){
      if(owner == this.currentUser && (action == 'Query Requester' || status.includes(' Q')))
        this.router.navigate(['/billing-listing/billing-approval'], { queryParams: { billing_no }});
      else if(this.roles!.includes('FINANCEADMIN') || this.permAllowApp > 0)
        this.router.navigate(['/billing-listing/billing-approval'], { queryParams: { billing_no }});
      else
        this.router.navigate(['/billing-listing/billing-details'], { queryParams: { billing_no }});
    }
    else
      this.router.navigate(['/billing-listing/billing-details'], { queryParams: { billing_no }});
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

  getParamDescBM(paramCd : String){
    for(const i of this.bilMthds)
      if(i.param_cd == paramCd){
        return i.nm_en;
      }
    return paramCd;
  }

  loadStatusTypes(){
      this.ParamService.getStates(environment.DefaultPage.toString(), environment.ItemPerPage.toString(), '', 'Billing-Status').subscribe((response: any) => {
        if (response.data.length >= 0) {
          this.bilWfStatuses = response.data as ParamData[];
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

  loadBillingTypes(){
      this.ParamService.getStates(environment.DefaultPage.toString(), environment.ItemPerPage.toString(), '', 'bltc-type').subscribe((response: any) => {
        if (response.data.length >= 0) {
          this.bilType = response.data as ParamData[];
          this.bilType.sort((a, b) => a.nm_en.localeCompare(b.nm_en)); // sorting
        } 
        else
          console.error('Invalid response format:', response);
      },
      (error) => {
        console.error('There was an error retrieving the status:', error);
      }
    );
  }

  loadBillingMethods(){
      this.ParamService.getStates(environment.DefaultPage.toString(), environment.ItemPerPage.toString(), '', 'Billing-Method').subscribe((response: any) => {
        if (response.data.length >= 0) {
          this.bilMthds = response.data as ParamData[];
          this.bilMthds.sort((a, b) => a.nm_en.localeCompare(b.nm_en)); // sorting
        } 
        else
          console.error('Invalid response format:', response);
      },
      (error) => {
        console.error('There was an error retrieving the status:', error);
      }
    );
  }

  loadSourceSytemData() {
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    this.isDisplay = true;
    this.isLoading = true;
    const url = environment.apiUrl + '/api/rms/v1/getsourcesystem';

    const Body: any = {
      i_page: 1,
      i_size: 10000,
      i_ss_id: null, 
      i_ss_cd: null, 
      i_ss_nm: null, 
      i_modified_by: null,
      i_dt_modified_fr: null, 
      i_dt_modified_to: null, 
      i_status: 'A'
    };
    
    this.http.post(url, Body, { headers }).subscribe(
      (response: any) => {
        for(const i of response.data){
          this.sourceSysList1.push(i.ss_nm);
          this.sourceSysList2.set(i.ss_nm, i.ss_cd);
        }
      },
      (error) => {
        console.error(error);
        this.isLoading = false;
      }
    );
  }

  exportData(data: any) {
    const replacer = (key: any, value: any) => value === null ? '-' : value; // specify how you want to handle null values here
    const header = Object.keys(data[0]);
    let csv = data.map((row: any) => header.map(fieldName => JSON.stringify(row[fieldName], replacer)).join(','));
    csv.unshift(header.join(','));
    let csvArray = csv.join('\r\n');
   
    var blob = new Blob([csvArray.split('action\r\n',2)[1]], {type: 'text/csv' })
    saveAs(blob, 'Billing-Listing-'+ new Date().toISOString().split('T')[0].replaceAll('-','') +'.csv');
  }

  exportButton(){
    this.csvData = [];
    this.csvData.push({
      cust_id : 'Customer ID',
      ent_nm: 'Entity Name',
      ent_no: 'Entity No',
      billing_no: 'Billing No',
      amount: 'Amount (RM)',
      billing_method: 'Billing Method',
      bil_wf_status: 'Billing Status',
      receipt_no: 'Receipt No',
      req_name: 'Requester Name',
      issuance: 'Issuance:',
      action: 'Action:'
    });
    this.isLoading = true;
      
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    const url = environment.apiUrl + '/api/billing/v1/getbillist';

    const Body: any = {
      i_page: 1,
      i_size: this.totalRecords,
      i_ent_nm: '', 
      i_ent_no: '', 
      i_ss_cd: '', 
      i_receipt_no: '',
      i_billing_mthd: '', 
      i_bil_wf_status: '', 
      i_dt_start: null, 
      i_dt_end: null, 
      i_b_type: '',
      i_billing_no: ''
    };
    if(this.filter){
      if (this.entNm && this.entNm.trim()) Body.i_ent_nm = this.entNm;
      if (this.entNo && this.entNo.trim()) Body.i_ent_no = this.entNo;
      if (this.sourceSys && this.sourceSys.trim()) Body.i_ss_cd = this.sourceSys;
      if (this.receiptNo && this.receiptNo.trim()) Body.i_receipt_no = this.receiptNo;
      if (this.dateRange && this.dateRange.length !=0) {
        Body.i_dt_start = this.dateRange[0]; 
        Body.i_dt_end = this.dateRange[1];
      }
      if (this.billingMthd && this.billingMthd.trim()) Body.i_billing_mthd = this.billingMthd;
      if (this.billingType && this.billingType.trim()) Body.i_b_type = this.billingType;
      if (this.billingNo && this.billingNo.trim()) Body.i_billing_no = this.billingNo;
      if (this.billStatus && this.billStatus.trim()) Body.i_bil_wf_status = this.billStatus;
    }    

    this.http.post(url, Body, { headers }).subscribe(
      (response: any) => {
        for(const item of response.data.billing_list){
          this.csvData.push({
            cust_id: item.cust_id,
            ent_nm: item.ent_nm,
            ent_no: item.ent_no,
            billing_no: item.billing_no,
            amount: item.amount,
            billing_method: this.getParamDescBM(item.billing_method.trim()),
            bil_wf_status: this.getParamDescWF(item.bil_wf_status),
            receipt_no: item.receipt_no,
            req_name: item.req_name,
            issuance: item.issuance,
            action: item.action
          });
        }
        this.exportData(this.csvData);
        this.isLoading = false;
      },
      (error) => {
        console.error(error);
        this.isLoading = false;
      }
    );
  }

  loadPermission() {
    this.authService.checkUserRole(this.currentUser as string, this.permCheck)
        .subscribe((response: any) => {
        this.permCheckReturnString = response.data;
        this.permAllow = this.permCheckReturnString.includes(perm.Billing_Listing_Details) ? 1 : 0;
        this.permAllowApp = this.permCheckReturnString.includes(perm.Billing_Registration_Approval) ? 1 : 0;
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
        alert('permission load failed for user: ' + this.currentUser as string);
        this.isLoadingPerm = false;
        this.model = this.hiddenModel;
        this.totalRecords = this.totalRecordsHidden;
        this.isLoading = false;
      }
    );
  }
  
  // toogle start
  rightSectionCollapsed: boolean = true;

  toggleRightSection() {
    this.rightSectionCollapsed = !this.rightSectionCollapsed;
  }

  waitForElm(selector: any) {
    return new Promise(resolve => {
        if (document.querySelector(selector)) {
            return resolve(document.querySelector(selector));
        }

        const observer = new MutationObserver(mutations => {
            if (document.querySelector(selector)) {
                observer.disconnect();
                resolve(document.querySelector(selector));
            }
        });

        // If you get "parameter 1 is not of type 'Node'" error, see https://stackoverflow.com/a/77855838/492336
        observer.observe(document.body, {
            childList: true,
            subtree: true
        });
    });
  }
}
