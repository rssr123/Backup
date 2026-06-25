import { ChangeDetectorRef, Component } from '@angular/core';
import { environment } from 'src/environments/environment';
import { Router } from '@angular/router';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { MatDialog } from '@angular/material/dialog';
import { NgbPaginationConfig } from '@ng-bootstrap/ng-bootstrap';
import { TranslateService } from '@ngx-translate/core';
import { AuthService } from 'src/app/services/auth.service';
import { GlobalService } from 'src/app/shared/global.service';
import { ParamService } from 'src/app/core/services/param.service';
import { billinglisting } from 'src/app/core/models/billinglisting.interface';
import { ParamData } from 'src/app/core/models/param.interface';

@Component({
  selector: 'app-billing-cancellation-search',
  templateUrl: './billing-cancellation-search.component.html',
  styleUrls: ['./billing-cancellation-search.component.scss']
})
export class BillingCancellationSearchComponent {
  page = environment.DefaultPage;
  itemsPerPage = environment.ItemPerPage;
  model: billinglisting[] = [];
  totalRecords: number = 0;
  isDisplay: boolean = true;
  currentUser: String | null = null;
  currentName:String | null = null;
  isUser: Boolean = false;

  //default pagination
  selectedValue = environment.dropdownOptions[0];
  dropdownOptions = environment.dropdownOptions;
  dropDownSize = environment.DropDownSize;

  customerID: String | null = null;
  billingNo: String | null = null;
  orderrefNo: String | null = null;
  entTy: String | null = null;
  entNo: String | null = null;

  isLoading: boolean = true;

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
      if(this.currentUser == null || this.currentUser == 'undefined')
          this.currentUser = 'Anonymous';
      this.authService.getName().subscribe(response =>{
        this.currentName = response;
        if(this.currentName == null || this.currentName == 'undefined' || this.currentName == '' || this.currentName == 'Anonymous')
            this.currentName = 'Anonymous';
      });
    });

    this.loadStatusTypes();
    this.loadBSParam();
    this.loadData();
  }

  LoadData(singleItem: number) {
    this.selectedValue = singleItem;
    this.itemsPerPage = this.selectedValue;
    this.loadData();
  }

  loadData() {
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    this.isDisplay = true;
    this.isLoading = true;
    const url = environment.apiUrl + '/api/billing/v1/getbillcanadjlist';

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
      i_billing_no: '',
      i_cust_id: '',
      i_orn_no: '',
      i_ent_ty: '',
      i_user_only: 1
    };

    if (this.customerID && this.customerID.trim()) Body.i_cust_id = this.customerID;
    if (this.billingNo && this.billingNo.trim()) Body.i_billing_no = this.billingNo;
    if (this.orderrefNo && this.orderrefNo.trim()) Body.i_orn_no = this.orderrefNo;
    if (this.entTy && this.entTy.trim()) Body.i_ent_ty = this.entTy;
    if (this.entNo && this.entNo.trim()) Body.i_ent_no = this.entNo;

    this.http.post(url, Body, { headers }).subscribe(
      (response: any) => {
        if (response.data.length != 0) {
          this.model = response.data.billing_list;
          this.totalRecords = response.data.total;
          console.log(this.model);
        }
        this.isLoading = false;
      },
      (error) => {
        console.error(error);
        this.isLoading = false;
      }
    );
  }

  clearFields(): void {
    this.customerID = null;
    this.billingNo = null;
    this.orderrefNo = null;
    this.entTy = null;
    this.entNo = null;
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
      if((this.isUser && (action == 'Query Requester' || item.bil_wf_status.includes(' Q'))))
        this.router.navigate(['/billing-cancellation-review'], { queryParams: { billing_no }});
      else
        this.router.navigate(['/billing-details'], { queryParams: { billing_no }});
    }
    else if(this.isUser && this.isCancelAllowed(bil_wf_status, item.unpaid))
      this.router.navigate(['/billing-cancellation'], { queryParams: { billing_no }});
    else
      this.router.navigate(['/billing-details'], { queryParams: { billing_no }});
  }

  bilWfStatuses: ParamData[] = [];
  billingStatusOptions: ParamData[] = [];

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

  loadBSParam(){
      this.ParamService.getStates(environment.DefaultPage.toString(), environment.ItemPerPage.toString(), '', 'Billing-Status').subscribe((response: any) => {
        if (response.data.length >= 0) {
          this.billingStatusOptions = response.data as ParamData[];
          this.billingStatusOptions.sort((a, b) => a.nm_en.localeCompare(b.nm_en)); // sorting
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
        return i.nm_en + (isQuery ? '-Query' : '');
    return paramCd;
  }

  getBillingStatusName(bil_status: string | null): string {
    if (!bil_status) {
      return ''; // Return a default value if stateCode is null
    }
    const billingStatus = this.billingStatusOptions.find((option) => option.param_cd === bil_status);
    return billingStatus ? billingStatus.nm_en : bil_status; // Return the name if found, otherwise return the code
  }
}
