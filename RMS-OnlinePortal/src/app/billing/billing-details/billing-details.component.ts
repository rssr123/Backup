import { formatDate } from '@angular/common';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { Router, ActivatedRoute } from '@angular/router';
import { NgbPaginationConfig } from '@ng-bootstrap/ng-bootstrap';
import { Systemstatus } from 'src/app/shared/enums/systemstatus';
import { environment } from 'src/environments/environment';
import { ParamService } from 'src/app/core/services/param.service';
import { GlobalService } from 'src/app/shared/global.service';
import { TranslateService } from '@ngx-translate/core';
import { AuthService } from 'src/app/services/auth.service';
import { ParamData } from 'src/app/core/models/param.interface';

@Component({
  selector: 'app-billing-details',
  templateUrl: './billing-details.component.html',
  styleUrls: ['./billing-details.component.scss']
})
export class BillingDetailsComponent implements OnInit {
  billingMethod: string | null = null;
  billingMethodF: string | null = null;

  custID: string | null = null;
  custName: string | null = null;
  custEmail: string | null = null;
  custPhoneNo: string | null = null;
  add1: string | null = null;
  add2: string | null = null;
  add3: string | null = null;
  postcode: string | null = null;
  city: string | null = null;
  state: string | null = null;
  entityName: string | null = null;
  entityType: string | null = null;
  entityNo: string | null = null;
  ss: string | null = null;
  billNo: string | null = null;
  b_mthd: string | null = null;
  bilDesc: string | null = null;
  reqName: string | null = null;
  reqEmail: string | null = null;
  loaRefNo: string | null = null;
  agmtRefNo: string | null = null;
  loaStartDate: Date | null = null;
  loaEndDate: Date | null = null;
  agmtStartDate: Date | null = null;
  agmtEndDate: Date | null = null;
  bilCount: number | null = null;
  bilFrequency: string | null = null;
  dayOfBilIssued: string | null = null;
  status: string | null = null;

  billInfo: any = null;
  billing_items: any[] = [];
  billing_list: any[] = [];
  documents_list: any[] = [];
  history: any[] = [];
  dateRangeString: String | null = null;
  total_amount: number = 0; 

  file_content = "";
  billingStatusOptions: ParamData[] = [];

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

  totalHistoryRecords: number = 0;
  totalRecordsListOfIssuance: number = 0;
  totalRecordsListOfDoc: number = 0;
  totalRecordsListOfItem: number = 0;

  // default pagination
  selectedValue = environment.dropdownOptions[0];
  dropdownOptions = environment.dropdownOptions;
  isLoadingHistory: boolean = true;
  isLoading: boolean = true;
  isBillUnauth: boolean = false;

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
  }


  async ngOnInit() {
    const queryParams = this.route.snapshot.queryParamMap;
    const tempBillNo = queryParams.get('billing_no');
    if (tempBillNo !== null && tempBillNo !== 'null')
      this.billNo = tempBillNo;

    if(this.billNo != null){
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
      i_bil_details_flag: true,
      i_user_only: 1
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
        else
          this.populate();
        
      },
      (error) => {
        console.error(error);
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
}
