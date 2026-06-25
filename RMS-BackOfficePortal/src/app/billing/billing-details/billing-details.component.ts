import { formatDate } from '@angular/common';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { Router, ActivatedRoute, RoutesRecognized } from '@angular/router';
import { NgbPaginationConfig } from '@ng-bootstrap/ng-bootstrap';
import { Systemstatus } from 'src/app/shared/enums/systemstatus';
import { environment } from 'src/environments/environment';
import { ParamService } from '../../core/services/param.service';
import { GlobalService } from 'src/app/shared/global.service';
import { TranslateService } from '@ngx-translate/core';
import { perm } from 'src/permissions/perm';
import { AuthService } from 'src/app/core/services/auth.service';
import { ParamData } from 'src/app/core/models/param.interface';
import { filter, pairwise } from 'rxjs/operators';

@Component({
  selector: 'app-billing-details',
  templateUrl: './billing-details.component.html',
  styleUrls: ['./billing-details.component.scss']
})
export class BillingDetailsComponent implements OnInit {
  billingMethod: string | null = null;
  billingMethodF: string | null = '-';

  custID: string | null = '-';
  custName: string | null = '-';
  custEmail: string | null = '-';
  custPhoneNo: string | null = '-';
  add1: string | null = '-';
  add2: string | null = '-';
  add3: string | null = '-';
  postcode: string | null = '-';
  city: string | null = '-';
  state: string | null = '-';
  entityName: string | null = '-';
  entityType: string | null = '-';
  entityNo: string | null = '-';
  ss: string | null = '-';
  billNo: string | null = '-';
  b_mthd: string | null = '-';
  bilDesc: string | null = '-';
  reqName: string | null = '-';
  reqEmail: string | null = '-';
  loaRefNo: string | null = '-';
  agmtRefNo: string | null = '-';
  loaStartDate: Date | null = null;
  loaEndDate: Date | null = null;
  agmtStartDate: Date | null = null;
  agmtEndDate: Date | null = null;
  bilCount: number | null = null;
  bilFrequency: string | null = '-';
  dayOfBilIssued: string | null = '-';
  status: string | null = '-';

  billInfo: any = null;
  billing_items: any[] = [];
  billing_list: any[] = [];
  documents_list: any[] = [];
  history: any[] = [];
  dateRangeString: String | null = '-';
  total_amount: number = 0; 

  file_content = "";
  /*
  billingDetails: BillingIssuanceBySSBillingDetails[] = [];
  bilingListOfItems: BillingIssuanceBySSListofBilItems[] = [];
  billingListOfIssuance: BillingIssuanceBySSListOfIssuance[] = [];
  billingListOfDoc: BillingIssuanceBySBillingDoc[] = [];
  billingHistory: BillingIssuanceBySSHistory[] = [];
  statesOptions: any[] = [];
  entityTypesOptions: any[] = [];
  sourceSystemCodesOptions: any[] = [];
  */
  billingStatusOptions: ParamData[] = [];

  alertMessage: string | undefined = undefined;


  listOfItemPage = environment.DefaultPage;
  itemsPerPageListOfItem = environment.ItemPerPage;
  listOfBillingIssuancePage = environment.DefaultPage;
  itemsPerPageListOfBillingIssuance = environment.ItemPerPage;
  listOfBillingDocPage = environment.DefaultPage;
  itemsPerPageListOfBillingDoc = environment.ItemPerPage;
  page = environment.DefaultPage;
  itemsPerPage = environment.ItemPerPage;
  dropDownSize = environment.DropDownSize;

  pageReceiptInfo = environment.DefaultPage;
  itemsPerPageReceiptInfo = environment.ItemPerPage;
  pageHistory = environment.DefaultPage;
  itemsPerPageHistory = environment.ItemPerPage;

  // totalReceiptInfoRecords: number = 0;
  totalHistoryRecords: number = 0;
  totalRecordsListOfIssuance: number = 0;
  totalRecordsListOfDoc: number = 0;
  totalRecordsListOfItem: number = 0;
  // isDisplayListOfIssuance: boolean = false;
  // isDisplayReceiptInfo: boolean = false;
  //isDisplayHist: boolean = false;

  // default pagination
  selectedValue = environment.dropdownOptions[0];
  dropdownOptions = environment.dropdownOptions;
  isLoadingHistory: boolean = true;
  isLoading: boolean = true;
  isLoadingBill: boolean = true;
  isLoadingPerms: boolean = true;
  isBillUnauth: boolean = false;

  permCheck = perm.Billing_Listing_Details;
  permCheckReturnString = ""; // variable to store allowed permission for the user
  permAllow: number = 0;

  constructor(
    private http: HttpClient,
    config: NgbPaginationConfig,
    private router: Router,
    private route: ActivatedRoute,
    public dialog: MatDialog,
    private ParamService: ParamService,
    private translateService: TranslateService,
    private globalService: GlobalService,
    private authService: AuthService
  ) {
    config.maxSize = environment.PaginationMaxSize;
    config.boundaryLinks = true;
    this.translateService.setDefaultLang(this.globalService.getGlobalValue());
    this.translateService.use(this.globalService.getGlobalValue());

    this.route.data.subscribe(d => {
      console.log('BCrumbs:');
      console.log(d);
      console.log(document.referrer);
    });

    this.router.events
    .pipe(filter((evt: any) => evt instanceof RoutesRecognized), pairwise())
    .subscribe((events: RoutesRecognized[]) => {
      console.log('previous url', events[0].urlAfterRedirects);
      console.log('current url', events[1].urlAfterRedirects);
    });
  }


  async ngOnInit() {
    /*
    this.bilId = history.state.bil_id;
    this.billingMethod = history.state.billing_method;
    
    // this.mttId = history.state.mtt_id;
    // this.otcId = history.state.otc_id;
    // this.otcCounterId = history.state.otc_counter_id;
    // this.counterId = history.state.counter_id;
    // this.OTCPaymentMode = history.state.otc_pymt_mode;
    
    this.loadBillingDetails();
    this.loadBillingListOfItems();
    this.loadBillingListOfIssuance();
    this.loadBillingListOfDoc();
    this.loadStates('State');// to replace state with state name
    this.loadStates('EntityType');
    this.populateSourceSystemCode();
    this.loadStates('Billing-Status');
    this.loadHistory();
    */
    const queryParams = this.route.snapshot.queryParamMap;
    const tempBillNo = queryParams.get('billing_no');
    if (tempBillNo !== null && tempBillNo !== 'null')
      this.billNo = tempBillNo;

    if(this.billNo != null){
      this.loadPermission();
      this.fetchBillInfo();
    }
  }

  async fetchBillInfo()  {
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });
    const url = environment.apiUrl + '/api/billing/v1/getbill';

    const Body: any = {
      i_billing_no: this.billNo,
      i_page: environment.DefaultPage,
      i_size: environment.ItemPerPage,
      i_bil_item_status: 'A',
      i_bil_details_flag: true
    };
    
    this.http.post(url, Body, { headers }).subscribe(
      (response: any) => {
        this.billInfo = response.data;

        console.log(response.data);
        if(response.data.status == 'UNAUTHORIZED'){
          this.isBillUnauth = true;
          this.isLoading = false;
          this.isLoadingHistory = false;
        }
        else if(!this.isLoadingPerms){
          this.isLoadingBill = false;
          this.populate();
        }
      },
      (error) => {
        console.error(error);
        this.isLoadingBill = false;
        if(!this.isLoadingPerms)
          this.isLoading = false;
      }
    );
  }

  populate(){
    this.custID = this.billInfo.cust_id;
    this.custName = this.billInfo.cust_nm;
    this.custEmail = this.billInfo.cust_email;
    this.custPhoneNo = this.billInfo.cust_phone;
    this.add1 = this.billInfo.cust_addr1;
    this.add2 = this.billInfo.cust_addr2;
    this.add3 = this.billInfo.cust_addr3;
    this.postcode = this.billInfo.cust_postcode;
    this.city = this.billInfo.cust_city;
    this.state = this.billInfo.cust_state;
    this.entityName = this.billInfo.ent_nm;
    this.entityType = this.billInfo.ent_ty;
    this.entityNo = this.billInfo.ent_no;
    this.ss = this.billInfo.ss_cd;
    this.loaRefNo = this.billInfo.loa_id;
    this.agmtRefNo = this.billInfo.agm_id;
    this.bilDesc = this.billInfo.billing_desc;
    this.bilCount = this.billInfo.billing_cnt;
    this.bilFrequency = this.billInfo.billing_freq;
    this.reqName = this.billInfo.req_name;
    this.reqEmail = this.billInfo.req_email;
    this.history = this.billInfo.history;
    this.billingMethod = this.billInfo.billing_mthd;
    this.billing_items = this.billInfo.billing_items;
    this.billing_list = this.billInfo.billing_list;
    this.documents_list = this.billInfo.documents_list;
    this.status = this.billInfo.bil_wf_status;
    this.totalHistoryRecords = this.billInfo.history_size;
    this.totalRecordsListOfIssuance = this.billInfo.issuance_size;
    this.totalRecordsListOfDoc = this.billInfo.documents_size;
    this.totalRecordsListOfItem = this.billInfo.items_size;

    if(this.billing_items != null && this.billing_items.length > 0)
      for(const item of this.billing_items)
        this.total_amount += item.final_amt;

    if(this.billing_list != null && this.billing_list.length > 0){
      var billing_day_number = new Date(this.billing_list[this.billing_list.length-1].bil_child_date).getDate();
      if(billing_day_number == 28 && this.billing_list.length > 1)
        billing_day_number = new Date(this.billing_list[this.billing_list.length-2].bil_child_date).getDate();
      this.dayOfBilIssued = billing_day_number.toString() + this.getOrdinalSuffix(billing_day_number) + ' day';
    }
    
    this.loaStartDate = this.billInfo.dt_loa_start;
    this.loaEndDate = this.billInfo.dt_loa_end;
    this.agmtStartDate = this.billInfo.dt_agm_start;
    this.agmtEndDate = this.billInfo.dt_agm_end;

    this.loadBSParam();
    this.loadSourceSytemData();

    this.loadParam(this.billingMethod as string,'Billing-Method');
    this.loadParam(this.entityType as string,'EntityType');
    this.loadParam(this.state as string,'State');
    this.loadParam(this.bilFrequency as string,'Billing-FreqType');

    this.isLoading = false;
    this.isLoadingHistory = false;
  }

  loadSourceSytemData(){
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    const url = environment.apiUrl + '/api/rms/v1/getsourcesystem';

    const Body: any = {
      i_page: 1,
      i_size: 1,
      i_ss_id: null, 
      i_ss_cd: this.ss, 
      i_ss_nm: null, 
      i_modified_by: null,
      i_dt_modified_fr: null, 
      i_dt_modified_to: null, 
      i_status: 'A'
    };
    
    this.http.post(url, Body, { headers }).subscribe(
      (response: any) => {
        this.ss = response.data[0].ss_nm;
      },
      (error) => {
        console.error(error);
      }
    );
  }

  loadParam(paramCd: string, paramGrpNm: string){
      this.ParamService.getStates(environment.DefaultPage.toString(), environment.ItemPerPage.toString(), paramCd, paramGrpNm).subscribe((response: any) => {
        if (response.data.length >= 0) {
          if(paramGrpNm == 'EntityType'){
            this.entityType = response.data[0].nm_en;
          }
          else if (paramGrpNm == 'State'){    
            var stateTmp = response.data[0].nm_en;
            stateTmp= stateTmp.charAt(0) + stateTmp.substring(1).toLowerCase();
            this.state = stateTmp;
          }
          else if (paramGrpNm == 'Billing-Method')
            this.billingMethodF = response.data[0].nm_en;
          else if (paramGrpNm == 'Billing-FreqType')
            this.bilFrequency = response.data[0].nm_en;
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
    for(const i of this.billingStatusOptions)
      if(i.param_cd == paramCd)
        return i.nm_en;
    return paramCd;
  }

  /*
  loadBillingDetails() {

    this.isLoadingBillingListOfItems = true;

    const urlMftWFHis = environment.apiUrl + '/api/bibss/v1/getbillingissuancebyssbillingdetails';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json'
    });


    // Create the request body with your form data
    const requestBody: any = {
      i_bil_id: this.bilId
    };

    this.http.post(urlMftWFHis, requestBody, { headers }).subscribe(
      (response: any) => {

        if (response.data.length === 0) {
          // this.isDisplayHist = false;
          this.isLoadingBillingListOfItems = false;
          // this.totalRecordsHist = 0;
          console.error('Invalid billing details response format:', response);
        }
        else {
          this.billingDetails = response.data;
          this.custID = this.billingDetails[0].cust_id;
          this.custName = this.billingDetails[0].cust_nm;
          this.custEmail = this.billingDetails[0].cust_email;
          this.custPhoneNo = this.billingDetails[0].cust_phone;
          this.add1 = this.billingDetails[0].cust_addr1;
          this.add2 = this.billingDetails[0].cust_addr2;
          this.add3 = this.billingDetails[0].cust_addr3;
          this.postcode = this.billingDetails[0].cust_postcode;
          this.city = this.billingDetails[0].cust_city;
          this.state = this.billingDetails[0].cust_state;
          this.entityName = this.billingDetails[0].ent_nm;
          this.entityType = this.billingDetails[0].ent_ty;
          this.entityNo = this.billingDetails[0].ent_no;
          this.ss = this.billingDetails[0].ss_cd;
          this.bilNo = this.billingDetails[0].billing_no;
          this.bilDesc = this.billingDetails[0].billing_desc;
          this.reqName = this.billingDetails[0].req_name;
          this.reqEmail = this.billingDetails[0].req_email;
          this.loaRefNo = this.billingDetails[0].loa_id;
          this.agmtRefNo = this.billingDetails[0].agm_id;
          this.loaStartDate = this.billingDetails[0].dt_loa_start;
          this.loaEndDate = this.billingDetails[0].dt_loa_end;
          this.agmtStartDate = this.billingDetails[0].dt_agm_start;
          this.agmtEndDate = this.billingDetails[0].dt_agm_end;
          this.bilCount = this.billingDetails[0].billing_cnt;
          this.bilFrequency = this.billingDetails[0].billing_freq;

          // this.isDisplayHist = true;
          this.isLoadingBillingListOfItems = false;
          // this.isOrderInfoFinishLoading = true;
          //this.totalRecordsHist = response.data[0].total;
        }


      },
      (error) => {
        console.error('There was an error retrieving billing details:', error);
        this.isLoadingBillingListOfItems = false;
      }
    );
  }

  loadBillingListOfItems() {

    this.isLoadingBillingListOfItems = true;

    const urlMftWFHis = environment.apiUrl + '/api/bibss/v1/getbillingissuancebysslistofbillingitems';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json'
    });


    // Create the request body with your form data
    const requestBody: any = {
      i_bil_id: this.bilId
    };

    this.http.post(urlMftWFHis, requestBody, { headers }).subscribe(
      (response: any) => {

        if (response.data.length === 0) {
          // this.isDisplayHist = false;
          this.isLoadingBillingListOfItems = false;
          this.totalRecordsListOfItem = 0;
          console.error('Invalid billing items response format:', response);
        }
        else {
          this.bilingListOfItems = response.data;
          this.isLoadingBillingListOfItems = false;
          // this.isOrderInfoFinishLoading = true;
          this.totalRecordsListOfItem = response.data[0].total;
        }


      },
      (error) => {
        console.error('There was an error retrieving billing items:', error);
        this.isLoadingBillingDetails = false;
      }
    );
  }

  loadBillingListOfIssuance() {

    this.isLoadingBillingListOfIssuance = true;

    const urlMftWFHis = environment.apiUrl + '/api/bibss/v1/getbillingissuancebysslistofbillingissuance';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json'
    });


    // Create the request body with your form data
    const requestBody: any = {
      i_bil_id: this.bilId
    };

    this.http.post(urlMftWFHis, requestBody, { headers }).subscribe(
      (response: any) => {

        if (response.data.length === 0) {
          // this.isDisplayHist = false;
          this.isLoadingBillingListOfIssuance = false;
          this.totalRecordsListOfIssuance = 0;
          console.error('Invalid billing issuance response format:', response);
        }
        else {
          this.billingListOfIssuance = response.data;
          this.isLoadingBillingListOfIssuance = false;
          // this.isOrderInfoFinishLoading = true;
          this.totalRecordsListOfIssuance = response.data[0].total;

          // Determine the status with priority: C > U > P
          for (const item of this.billingListOfIssuance) {
            if (item.bil_status === 'C') {
              this.status = 'C';
              break; // Stop checking further once C is found
            }
            if (item.bil_status === 'U') {
              this.status = 'U'; // Continue checking in case C appears later
            }
          }

          // If no C or U was found, check if all are P
          if (!this.status) {
            const allP = this.billingListOfIssuance.every(item => item.bil_status === 'P');
            this.status = allP ? 'P' : ''; // Default status if none of the conditions are met
          }
        }
      },
      (error) => {
        console.error('There was an error retrieving billing issuance:', error);
        this.isLoadingBillingListOfIssuance = false;
      }
    );
  }

  loadBillingListOfDoc() {

    this.isLoadingBillingListOfDoc = true;

    const urlMftWFHis = environment.apiUrl + '/api/bibssdoc/v1/getbillingissuancebyssdocument';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json'
    });


    // Create the request body with your form data
    const requestBody: any = {
      i_bil_id: this.bilId
    };

    this.http.post(urlMftWFHis, requestBody, { headers }).subscribe(
      (response: any) => {

        if (response.data.length === 0) {
          // this.isDisplayHist = false;
          this.isLoadingBillingListOfDoc = false;
          this.totalRecordsListOfDoc = 0;
          console.error('Invalid billing docs response format:', response);
        }
        else {
          this.billingListOfDoc = response.data;


          this.isLoadingBillingListOfDoc = false;
          // this.isOrderInfoFinishLoading = true;
          this.totalRecordsListOfDoc = response.data[0].total;
        }
      },
      (error) => {
        console.error('There was an error retrieving billing docs:', error);
        this.isLoadingBillingListOfDoc = false;
      }
    );
  }

  loadHistory() {

    this.isLoadingHistory = true;

    const urlMftWFHis = environment.apiUrl + '/api/bibss/v1/getbillingissuancebysshistory';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json'
    });


    // Create the request body with your form data
    const requestBody: any = {
      i_bil_id: this.bilId
    };

    this.http.post(urlMftWFHis, requestBody, { headers }).subscribe(
      (response: any) => {

        if (response.data.length === 0) {
          this.isDisplayHist = false;
          this.isLoadingHistory = false;
          this.totalHistoryRecords = 0;
          console.error('Invalid otc receipt cancellation history table details response format:', response);
        }
        else {
          this.totalHistoryRecords = response.data[0].total;
          this.billingHistory = response.data;
          // this.isDisplayHist = true;
          this.isLoadingHistory = false;
        }
      },
      (error) => {
        console.error('There was an error retrieving the history table:', error);
        this.isLoadingHistory = false;
      }
    );
  }

  loadStates(paramGrpNm: string) {
    this.ParamService.getStates(environment.DefaultPage.toString(), environment.ItemPerPage.toString(), '', paramGrpNm).subscribe((response: any) => {
      if (response.data.length >= 0) {
        // this.states = response.data as ParamData[]; later
        if (paramGrpNm === 'State') {
          this.statesOptions = response.data as any[];
          this.statesOptions.sort((a, b) => a.nm_en.localeCompare(b.nm_en)); // sorting
        }
        else if (paramGrpNm === 'EntityType') {
          this.entityTypesOptions = response.data as any[];
          this.entityTypesOptions.sort((a, b) => a.nm_en.localeCompare(b.nm_en));
        }
        else if (paramGrpNm === 'Billing-Status') {
          this.billingStatusOptions = response.data as any[];
          this.billingStatusOptions.sort((a, b) => a.nm_en.localeCompare(b.nm_en));
        }
      }
      else
        console.error('Invalid response format:', response);
    },
      (error) => {
        console.error('There was an error retrieving the status:', error);
      }
    );
  }

  getStateName(stateCode: string | null): string {
    if (!stateCode) {
      return ''; // Return a default value if stateCode is null
    }
    const state = this.statesOptions.find((option) => option.param_cd === stateCode);
    return state ? state.nm_en : stateCode; // Return the name if found, otherwise return the code
  }

  getEntityTypeName(entityCode: string | null): string {
    if (!entityCode) {
      return ''; // Return a default value if stateCode is null
    }
    const entity = this.entityTypesOptions.find((option) => option.param_cd === entityCode);
    return entity ? entity.nm_en : entityCode; // Return the name if found, otherwise return the code
  }

  getSourceSystemName(ssCode: string | null): string {
    if (!ssCode) {
      return ''; // Return a default value if stateCode is null
    }
    const sourceSystem = this.sourceSystemCodesOptions.find((option) => option.ss_cd === ssCode);
    return sourceSystem ? sourceSystem.ss_nm : ssCode; // Return the name if found, otherwise return the code
  }

  populateSourceSystemCode() {

    const url = environment.apiUrl + '/api/rms/v1/getsourcesystem';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json'
    });


    // Create the request body with your form data
    const Body = {
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

    this.http.post(url, Body, { headers }).subscribe(
      (response: any) => {

        if (response.data.length === 0) {
          console.error('Invalid source system response format:', response);
        }
        else {
          this.sourceSystemCodesOptions = response.data;
        }
      },
      (error) => {
        console.error('There was an error retrieving the source system:', error);
      }
    );
  }
  */
  getBillingStatusName(bil_status: string | null): string {
    if (!bil_status) {
      return ''; // Return a default value if stateCode is null
    }
    const billingStatus = this.billingStatusOptions.find((option) => option.param_cd === bil_status);
    return billingStatus ? billingStatus.nm_en : bil_status; // Return the name if found, otherwise return the code
  }

  getTotalGrossAmountListOfItems(): number {
    if (!this.billing_items.length) {
      return 0;
    }
    return this.billing_items.reduce((sum, item) => sum + (item.final_amt || 0), 0);
  }

  back() {
    this.router.navigate(['/billing-listing']);
  }

  formatOrdinalDate(dateInput: string | Date): string {
    if (!dateInput) return '';

    const date = new Date(dateInput);
    const day = date.getDate();
    const month = date.toLocaleString('default', { month: 'long' }); // Full month name
    const year = date.getFullYear();

    const suffix = this.getOrdinalSuffix(day);

    return `${day}${suffix} ${month} ${year}`;
  }

  private getOrdinalSuffix(day: number): string {
    if (day > 3 && day < 21) return 'th'; // 4th to 20th always have 'th'
    switch (day % 10) {
      case 1: return 'st';
      case 2: return 'nd';
      case 3: return 'rd';
      default: return 'th';
    }
  }

  isValidDate(bilChildDate: string | Date): string {
    const currentDate = new Date();
    const childDate = new Date(bilChildDate);
    return childDate > currentDate ? 'Valid' : 'Invalid';
  }

  hasBilDoc(bilChildDate: string | Date): boolean {
    const currentDate = new Date();
    const childDate = new Date(bilChildDate);
    return childDate > currentDate ? true : false;
  }

  //download file start
  downloadFile(item: any): void {
    if(item.isDownloadingFile)
      return;
    item.isDownloadingFile = true;
    const url = environment.apiUrl + '/api/billing/v1/getbildocblob';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    const Body: any = {
      i_doc_id: item.bil_doc_id
    };

    this.http.post(url, Body, { headers }).subscribe(
      (response: any) => {
        // console.log(response.data);
        this.file_content = response.data;
        this.downloadFileContent(item.file_nm, this.file_content);

        if (response.data.length == 0) {
          console.error('Invalid billing document response format:', response);
        } else {
          console.log('Successful download file ' + item.file_nm);
        }
        item.isDownloadingFile = false;
      },
      (error) => {
        console.error('There was an error downloading the billing document:', error);
        item.isDownloadingFile = false;
      }
    );
  }

  downloadFileContent(fileName: string, fileContent: string): void {
    // event.preventDefault(); // Prevent the default behavior of the anchor element

    // Check if file_content exists
    if (fileContent) {
      const contentType = 'application/octet-stream';
      const blob = this.base64ToBlob(fileContent, contentType);
      const blobUrl = URL.createObjectURL(blob);

      // Create an anchor element and trigger the download
      const link = document.createElement('a');
      link.href = blobUrl;
      link.download = fileName;
      link.click();

      // Cleanup
      URL.revokeObjectURL(blobUrl);
    }
  }

  base64ToBlob(base64: string, contentType: string): Blob {
    const byteCharacters = atob(base64);
    const byteNumbers = new Array(byteCharacters.length);

    for (let i = 0; i < byteCharacters.length; i++) {
      byteNumbers[i] = byteCharacters.charCodeAt(i);
    }

    const byteArray = new Uint8Array(byteNumbers);
    return new Blob([byteArray], { type: contentType });
  }
  //download file end

  loadPermission() {
    this.authService.checkUserRole(this.authService.username, this.permCheck)
        .subscribe((response: any) => {
          this.permCheckReturnString = response.data;
          this.permAllow = this.permCheckReturnString.includes(perm.Billing_Listing_Details) ? 1 : 0;
          if (this.permAllow === 0) {
            if(environment.production)
              this.router.navigate(['/access-denied']);
            console.log(response.data);
            alert('bad permission: ' + this.permCheckReturnString);  
          }
          this.isLoadingPerms = false;

          if(this.isBillUnauth){
            this.isLoading = false;
            this.isLoadingHistory = false;
          }
          else if(!this.isLoadingBill)
            this.populate();
        },
        (error: any) => {
          if(environment.production)
            this.router.navigate(['/access-denied']);
          console.log(error);
          alert('permission load failed');
          this.isLoadingPerms = false;
          if(!this.isLoadingBill && this.billInfo != null)
            this.populate();
          else if(!this.isLoadingBill)
            this.isLoadingBill = false;
        }
      );
  }

  LoadHistory(singleItem: number) {
    this.selectedValue = singleItem;
    this.itemsPerPage = this.selectedValue;
    this.loadHistory();
  }

  loadHistory() {
    this.isLoadingHistory = true;
    const url = environment.apiUrl + '/api/billing/v1/getbilhist';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json'
    });


    // Create the request body with your form data
    const requestBody: any = {      
      i_page: this.page,
      i_size: this.itemsPerPage,
      i_billing_no: this.billNo
    };

    this.http.post(url, requestBody, { headers })
    .subscribe((response: any) => {
        if (response.data.length === 0) {
          this.isLoadingHistory = false;
          console.error('Invalid history table details response format:', response);
        }
        else {
          this.history = response.data;
          this.isLoadingHistory = false;
        }
      },
      (error) => {
        console.error('There was an error retrieving the history table:', error);
        this.isLoadingHistory = false;
      }
    );
  }

  downloadImg(item: any): void {
    if(item.isDownloadingFile)
      return;
    item.isDownloadingFile = true;
    const url = environment.apiUrl + '/api/billing/v1/getbilchildimgblob';
    
    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });
    
    const Body: any = {
      i_child_id: item.bil_child_id,
    };
    
    this.http.post(url, Body, { headers }).subscribe(
      (response: any) => {
        const fileContent = response.data;
        const mimeType = response.mimeType || 'application/octet-stream'; // Fallback MIME type
        this.downloadFileContent(item.bil_no + "_img.pdf", fileContent);
      
        if (!fileContent || fileContent.length === 0) {
            item.isDownloadingFile = false;
        } else {
            item.isDownloadingFile = false;
        }
        },
        (error) => {
        console.error(error);
            item.isDownloadingFile = false;
        }
      );
  }

  async enablePayment(item: any){
        const url = environment.apiUrl + '/api/billing/v1/refreshbillingpayment';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json'
    });

    // Create the request body with your form data
    var requestBody: {[k: string]: any} = {
      i_billing_no: item.bil_no
    };

    item.isEnablingPayment = true;
    this.http.post(url, requestBody, { headers }).subscribe((response: any) => {
      if (response.data && response.data.length > 0) {
        if(parseFloat(response.data) < 1){
          var debugFlag = localStorage.getItem('debug') == 'true' ? true : false;
          if(debugFlag){
            console.log('Failed, bad status code: ' + response.data);
          }
        }
        else{
          item.is_expired = 0;
          this.selectedValue = environment.dropdownOptions[0];
          this.itemsPerPage = environment.ItemPerPage;
          this.page = environment.DefaultPage;
          this.loadHistory();
        }
        
        item.isEnablingPayment = false;
      }
    },
      (error: any) => {
        item.isEnablingPayment = false;
        console.log('Fail to re-enable expired bill! Error in Posting.');
        console.error(error);
        this.selectedValue = environment.dropdownOptions[0];
        this.itemsPerPage = environment.ItemPerPage;
        this.page = environment.DefaultPage;
        this.loadHistory();
    });

  }
}
