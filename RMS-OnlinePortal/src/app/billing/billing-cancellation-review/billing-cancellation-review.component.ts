import { HttpClient, HttpHeaders } from '@angular/common/http';
import { ChangeDetectorRef, Component } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { Router, ActivatedRoute } from '@angular/router';
import { NgbPaginationConfig } from '@ng-bootstrap/ng-bootstrap';
import { TranslateService } from '@ngx-translate/core';
import { AuthService } from 'src/app/services/auth.service';
import { GlobalService } from 'src/app/shared/global.service';
import { environment } from 'src/environments/environment';
import { ParamService } from 'src/app/core/services/param.service';
import { DatePipe } from '@angular/common';
import { ParamData } from 'src/app/core/models/param.interface';

@Component({
  selector: 'app-billing-cancellation-review',
  templateUrl: './billing-cancellation-review.component.html',
  styleUrls: ['./billing-cancellation-review.component.scss']
})
export class BillCancellationReviewComponent {
  page = environment.DefaultPage;
  itemsPerPage = environment.ItemPerPage;

  currentUser:String | null = null;
  currentName:String | null = null;
  billNo: String | null = null;
  billInfo: any = null;
  billing_mthd: String | null = null;
  b_mthd: String | null = null;
  dateRangeString: String | null = null;
  billing_day: String | null = null;
  cust_id: String | null = null;
  cust_nm: String | null = null;
  cust_email: String | null = null;
  cust_phone: String | null = null;
  cust_addr1: String | null = null;
  cust_addr2: String | null = null;
  cust_addr3: String | null = null;
  cust_postcode: String | null = null;
  cust_city: String | null = null;
  cust_state: String | null = null;
  ent_nm: String | null = null;
  ent_ty: String | null = null;
  ent_no: String | null = null;
  ss_cd: String | null = null;
  billing_no: String | null = null;
  loa_id: String | null = null;
  agm_id: String | null = null;
  billing_desc: String | null = null;
  billing_cnt: String | null = null;
  billing_freq: String | null = null;
  req_name: String | null = null;
  req_email: String | null = null;
  billing_items: any[] = [];
  billing_list: any[] = [];
  documents_list: any[] = [];
  history: any[] = [];
  billingStatusOptions: ParamData[] = [];

  total_amount: number = 0;
  
  selectedDecision: String | null = null;
  remarks: String | null = null;
  file_content = "";
  
  totalHistoryRecords: number = 0;
  totalRecordsListOfIssuance: number = 0;
  totalRecordsListOfDoc: number = 0;
  totalRecordsListOfItem: number = 0;

  hideActions: boolean = true;

  // default pagination
  selectedValue = environment.dropdownOptions[0];
  dropdownOptions = environment.dropdownOptions;
  dropDownSize = environment.DropDownSize;
  isLoadingHistory: boolean = true;
  isLoading: boolean = true;
  isBillUnauth: boolean = false;
  isUser: boolean = false;

  constructor(
    private http: HttpClient,
    config: NgbPaginationConfig,
    private router: Router,
    public dialog: MatDialog,
    private ParamService: ParamService,
    private cd: ChangeDetectorRef,
    private route: ActivatedRoute,
    private translate: TranslateService,
    private globalService: GlobalService,
    private authService: AuthService,
    public datepipe: DatePipe
  ) {
    config.maxSize = environment.PaginationMaxSize;
    config.boundaryLinks = true;
    this.translate.setDefaultLang(this.globalService.getGlobalValue()); // default language
    this.translate.use(this.globalService.getGlobalValue());
  }

  async ngOnInit() {
    this.freezeScreen();
    const queryParams = this.route.snapshot.queryParamMap;
    const tempBillNo = queryParams.get('billing_no');
    if (tempBillNo !== null && tempBillNo !== 'null')
      this.billNo = tempBillNo;

    this.authService.getUsername().subscribe(response =>{
      this.currentUser = response;
      if(this.currentUser == null || this.currentUser == 'undefined' || this.currentUser == '' || this.currentUser == 'Anonymous')
          this.currentUser = 'Anonymous';
      this.authService.getName().subscribe(response =>{
        this.currentName = response;
        if(this.currentName == null || this.currentName == 'undefined' || this.currentName == '' || this.currentName == 'Anonymous')
            this.currentName = 'Anonymous';
        });
    });
    if(this.billNo != null)
      this.fetchBillInfo();
    
  }

  async fetchBillInfo()  {
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });
    const url = environment.apiUrl + '/api/billing/v1/getbill';

    const Body: any = {
      i_billing_no: this.billNo,
      i_page: this.page,
      i_size: this.itemsPerPage,
      i_bil_item_status: 'A',
      i_bil_details_flag: true,
      i_user_only: 1
    };
    
    this.http.post(url, Body, { headers }).subscribe(
      (response: any) => {
        this.billInfo = response.data;
        var billing_no = this.billNo;
        /*if(this.currentUser == this.billInfo.created_by && this.billInfo.history[0].msg_type == 'QCR' && this.billInfo.bil_wf_status == 'WF-CN') //Owner of the bill to return FA query
            this.hideActions = false;*/
        this.isUser = this.currentUser == this.billInfo.created_by || this.currentName == this.billInfo.created_by;

        if(this.billInfo.bil_wf_status == 'WF-CN' && this.billInfo.history[0].msg_type == 'QCR')
            this.hideActions = false;
        if(!this.isUser)
            this.hideActions = true;

        if(environment.production) //User not supposed to be in this screen! Reroute to details page
          this.router.navigate(['/billing-details'], { queryParams: { billing_no }});
        
        console.log(response.data);
        if(response.data.status == 'UNAUTHORIZED'){
          this.isBillUnauth = true;
          this.unfreezeScreen();
          this.isLoadingHistory = false;
        }
        else
          this.populate();
      },
      (error) => {
        console.error(error);
        this.unfreezeScreen();
      }
    );
  }
  
  hasBilDoc(bilChildDate: string | Date): boolean {
    const currentDate = new Date();
    const childDate = new Date(bilChildDate);
    return childDate > currentDate ? true : false;
  }

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
        this.file_content = response.data;
        this.downloadFileContent(item.file_nm, this.file_content);

        if (response.data.length == 0) {
          console.error('Invalid billing workflow document response format:', response);
        } else {
          console.log('Successful download file ' + item.file_nm);
        }
        item.isDownloadingFile = false;
      },
      (error) => {
        console.error('There was an error downloading the master billing workflow document:', error);
        item.isDownloadingFile = false;
      }
    );
  }

  downloadFileContent(fileName: string, fileContent: string): void {
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

  populate(){
    this.cust_id = this.billInfo.cust_id;
    this.cust_nm = this.billInfo.cust_nm;
    this.cust_email = this.billInfo.cust_email;
    this.cust_phone = this.billInfo.cust_phone;
    this.cust_addr1 = this.billInfo.cust_addr1;
    this.cust_addr2 = this.billInfo.cust_addr2;
    this.cust_addr3 = this.billInfo.cust_addr3;
    this.cust_postcode = this.billInfo.cust_postcode;
    this.cust_city = this.billInfo.cust_city;
    this.cust_state = this.billInfo.cust_state;
    this.ent_nm = this.billInfo.ent_nm;
    this.ent_ty = this.billInfo.ent_ty;
    this.ent_no = this.billInfo.ent_no;
    this.ss_cd = this.billInfo.ss_cd;
    this.billing_no = this.billInfo.billing_no;
    this.loa_id = this.billInfo.loa_id;
    this.agm_id = this.billInfo.agm_id;
    this.billing_desc = this.billInfo.billing_desc;
    this.billing_cnt = this.billInfo.billing_cnt;
    this.billing_freq = this.billInfo.billing_freq;
    this.req_name = this.billInfo.req_name;
    this.req_email = this.billInfo.req_email;
    this.billing_items = this.billInfo.billing_items;
    this.billing_list = this.billInfo.billing_list;
    this.documents_list = this.billInfo.documents_list;
    this.history = this.billInfo.history;
    this.b_mthd = this.billInfo.billing_mthd;
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
      this.billing_day = billing_day_number.toString() + this.nthNumber(billing_day_number) + ' day';
    }

    if(this.agm_id != null)
      this.dateRangeString = this.datepipe.transform(new Date(this.billInfo.dt_agm_start), 'dd MMM yyyy') + ' - ' + this.datepipe.transform(new Date(this.billInfo.dt_agm_end), 'dd MMM yyyy');
    else if(this.agm_id == null && this.loa_id != null)
      this.dateRangeString = this.datepipe.transform(new Date(this.billInfo.dt_loa_start), 'dd MMM yyyy') + ' - ' + this.datepipe.transform(new Date(this.billInfo.dt_loa_end), 'dd MMM yyyy');

    this.loadSourceSytemData();
    this.loadBSParam();
    this.loadParam(this.b_mthd as string,'Billing-Method');
    this.loadParam(this.ent_ty as string,'EntityType');
    this.loadParam(this.cust_state as string,'State');

    this.unfreezeScreen();
    this.isLoadingHistory = false;
  }

  nthNumber(number: number): string{
    if (number > 3 && number < 21) return "th";
    switch (number % 10) {
      case 1: return "st";
      case 2: return "nd";
      case 3: return "rd";
      default: return "th";
    }
  }

  formatDate(sdate: Date): string{
    const cdate = new Date(sdate);
    return cdate.getDate() + this.nthNumber(cdate.getDate()) + ' ' + cdate.toLocaleString('default', { month: 'long' }) + ' ' + cdate.getFullYear();
  }

  loadParam(paramCd: string, paramGrpNm: string){
      this.ParamService.getStates(environment.DefaultPage.toString(), environment.ItemPerPage.toString(), paramCd, paramGrpNm).subscribe((response: any) => {
        if (response.data.length >= 0) {
          if(paramGrpNm == 'EntityType')
            this.ent_ty = response.data[0].nm_en;
          else if (paramGrpNm == 'State'){    
            var state = response.data[0].nm_en;
            state = state.charAt(0) + state.substring(1).toLowerCase();
            this.cust_state = state;
          }
          else if (paramGrpNm == 'Billing-Method')
            this.billing_mthd = response.data[0].nm_en;
        } 
        else
          console.error('Invalid response format:', response);
      },
      (error) => {
        console.error('There was an error retrieving the status:', error);
      }
    );
  } 

  loadETParam(){
      this.ParamService.getStates(environment.DefaultPage.toString(), environment.ItemPerPage.toString(), this.ent_ty as string,'EntityType').subscribe((response: any) => {
        if (response.data.length >= 0) {
          this.ent_ty = response.data[0].nm_en;
        } 
        else
          console.error('Invalid response format:', response);
      },
      (error) => {
        console.error('There was an error retrieving the status:', error);
      }
    );
  } 

  loadCSParam(){
      this.ParamService.getStates(environment.DefaultPage.toString(), environment.ItemPerPage.toString(), this.cust_state as string, 'State').subscribe((response: any) => {
        if (response.data.length >= 0) {
          var state = response.data[0].nm_en;
          state = state.charAt(0) + state.substring(1).toLowerCase();
          this.cust_state = state;
        } 
        else
          console.error('Invalid response format:', response);
      },
      (error) => {
        console.error('There was an error retrieving the status:', error);
      }
    );
  }  

  loadBMParam(){
      this.ParamService.getStates(environment.DefaultPage.toString(), environment.ItemPerPage.toString(), this.b_mthd as string, 'Billing-Method').subscribe((response: any) => {
        if (response.data.length >= 0) {
          this.billing_mthd = response.data[0].nm_en;
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
      i_ss_cd: this.ss_cd, 
      i_ss_nm: null, 
      i_modified_by: null,
      i_dt_modified_fr: null, 
      i_dt_modified_to: null, 
      i_status: 'A'
    };
    
    this.http.post(url, Body, { headers }).subscribe(
      (response: any) => {
        this.ss_cd = response.data[0].ss_nm;
      },
      (error) => {
        console.error(error);
      }
    );
  }

  submitCheck(){
    if((this.remarks != null && this.remarks.length > 0) && this.selectedDecision == 'query')
      return false;
    return true;
  }

  isValidDate(bilChildDate: string | Date): string {
    const currentDate = new Date();
    const childDate = new Date(bilChildDate);
    return childDate > currentDate ? 'Valid' : 'Invalid';
  }

  getBillingStatusName(bil_status: string | null): string {
    if (!bil_status) {
      return ''; // Return a default value if stateCode is null
    }
    const billingStatus = this.billingStatusOptions.find((option) => option.param_cd === bil_status);
    return billingStatus ? billingStatus.nm_en : bil_status; // Return the name if found, otherwise return the code
  }

  freezeScreen(){
    this.isLoading = true;
  }

  unfreezeScreen(){
    this.isLoading = false;
  }

  cancelForm(){
    location.href = 'billing-listing';
  }

  submitForm(){
    var url = environment.apiUrl + '/api/billing/v1/querybillcan';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json'
    });

    // Create the request body with your form data
    var requestBody: {[k: string]: any} = {
        i_billing_no: this.billing_no,
        i_remark: this.remarks,
    };

    console.log(requestBody);
    this.freezeScreen();
    this.http.post(url, requestBody, { headers }).subscribe((response: any) => {
      if (response.data && response.data.length > 0) {
        console.log('success update bill!');
        console.log(response.data);

        this.unfreezeScreen();
        location.href = 'billing-cancellation-listing';
      }
    },
      (error: any) => {
        console.log('fail update bill!');
        console.error(error);
        this.unfreezeScreen();
    });
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
}
