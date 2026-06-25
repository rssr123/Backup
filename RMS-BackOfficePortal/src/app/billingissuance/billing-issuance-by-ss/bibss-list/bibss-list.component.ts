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

@Component({
  selector: 'app-bibss-list',
  templateUrl: './bibss-list.component.html',
  styleUrls: ['./bibss-list.component.scss']
})
export class BibssListComponent {
  page = environment.DefaultPage;
  itemsPerPage = environment.ItemPerPage;
  model: billinglisting[] = [];
  bilWfStatuses: ParamData[] = [];
  bilType: ParamData[] = [];
  bilMthds: ParamData[] = [];
  sourceSysList1: String[] = [];
  sourceSysList2: Map<String, String> = new Map<String, String>();

  totalRecords: number = 0;
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

  filter: boolean = false;
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

    this.authService.getUsername().subscribe(response =>{
      this.currentUser = response;
      if(this.currentUser == null || this.currentUser == 'undefined')
          this.currentUser = 'Anonymous';
    });

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
    
    this.http.post(url, Body, { headers }).subscribe(
      (response: any) => {
        this.model = response.data[0].billing_list;
        this.totalRecords = response.data[0].total;
        this.isLoading = false;
      },
      (error) => {
        console.error(error);
        console.log(Body);
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

  navigateToBillingAppScreen(billing_no: string): void {
    this.router.navigate(['/billing-approval'], { queryParams: { billing_no }});
  }

  navigateToBillingDetScreen(bil_id: number, billing_method: string): void {
    //this.router.navigate(['/billing-details'], { queryParams: { billing_no }});
   //const billing_no = item.billing_no;
    //const bil_id = bil_id;

    //let billingMethod = '';
    if (billing_method === 'agmt' || billing_method === 'A') {
      billing_method = 'A';
    }
    else if (billing_method === 'loa' || billing_method === 'L') {
      billing_method = 'L';
    }
    else {
      billing_method = 'O';
    }

    // const billing_method = item.billing_method === 'agmt' ? item.billing_method : 'agmt';
    this.router.navigate(['/bibss-details'], { state: { bil_id, billing_method } });
    // this.router.navigate(['/billing-approval'], { queryParams: { billing_no }});

  }

  getParamDescWF(paramCd : String){
    for(const i of this.bilWfStatuses)
      if(i.param_cd == paramCd)
        return i.nm_en;
    return paramCd;
  }

  getParamDescBM(paramCd : String){
    for(const i of this.bilMthds)
      if(i.param_cd == paramCd)
        return i.nm_en;
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

  checkButtonToUse(status: string, action: string, modifiedBy: string){
    if(status == 'WF-N'){
      if(modifiedBy == this.currentUser){
        if(action == 'Query Requester')
          return true;
        else
          return false;
      }
      return true;
    }
    return false;
  }
}
