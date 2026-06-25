import { HttpClient, HttpHeaders } from '@angular/common/http';
import { ChangeDetectorRef, Component } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { Router, ActivatedRoute } from '@angular/router';
import { NgbPaginationConfig } from '@ng-bootstrap/ng-bootstrap';
import { TranslateService } from '@ngx-translate/core';
import { AuthService } from 'src/app/core/services/auth.service';
import { GlobalService } from 'src/app/shared/global.service';
import { environment } from 'src/environments/environment';
import { ParamService } from 'src/app/core/services/param.service';
import { DatePipe } from '@angular/common';
import { ParamData } from 'src/app/core/models/param.interface';
import { perm } from 'src/permissions/perm';

@Component({
  selector: 'app-billing-cancellation-approval',
  templateUrl: './billing-cancellation-approval.component.html',
  styleUrls: ['./billing-cancellation-approval.component.scss']
})
export class BillCancellationApprovalComponent {
  page = environment.DefaultPage;
  itemsPerPage = environment.ItemPerPage;

  currentUser:String | null = null;
  currentName:String | null = null;
  isUser: Boolean = false;
  roles: String | null = 'ANONYMOUS';
  billNo: String | null = null;
  billInfo: any = null;
  billing_mthd: String | null = '-';
  b_mthd: String | null = null;
  dateRangeString: String | null = '-';
  billing_day: String | null = '-';
  cust_id: String | null = '-';
  cust_nm: String | null = '-';
  cust_email: String | null = '-';
  cust_phone: String | null = '-';
  cust_addr1: String | null = '-';
  cust_addr2: String | null = '-';
  cust_addr3: String | null = '-';
  cust_postcode: String | null = '-';
  cust_city: String | null = '-';
  cust_state: String | null = '-';
  ent_nm: String | null = '-';
  ent_ty: String | null = '-';
  ent_no: String | null = '-';
  ss_cd: String | null = '-';
  billing_no: String | null = '-';
  loa_id: String | null = '-';
  agm_id: String | null = '-';
  billing_desc: String | null = '-';
  billing_cnt: String | null = '-';
  billing_freq: String | null = '-';
  req_name: String | null = '-';
  req_email: String | null = '-';
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
  queryOnly: boolean = false;

  // default pagination
  selectedValue = environment.dropdownOptions[0];
  dropdownOptions = environment.dropdownOptions;
  dropDownSize = environment.DropDownSize;
  isLoadingHistory: boolean = true;
  isApprover: boolean = false;
  isLoading: boolean = true;
  isLoadingBill: boolean = true;
  isLoadingPerms: boolean = true;
  isBillUnauth: boolean = false;

  permCheck = perm.Billing_Cancellation_Request + ',' + perm.Billing_Cancellation_Approval;
  permCheckReturnString = ""; // variable to store allowed permission for the user
  permAllowQuery: number = 0;
  permAllowApp: number = 0;
  showInsertAlert: boolean = false;
  
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
      else
        this.roles = this.authService.roles;
    });
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
      i_page: this.page,
      i_size: this.itemsPerPage,
      i_bil_item_status: 'A',
      i_bil_details_flag: true
    };
    
    this.http.post(url, Body, { headers }).subscribe(
      (response: any) => {
        this.billInfo = response.data;
        //console.log(response.data);
        if(response.data.status == 'UNAUTHORIZED'){
          this.isBillUnauth = true;
          this.unfreezeScreen();
          this.isLoadingHistory = false;
        }
        else if(!this.isLoadingPerms){
          this.isLoadingBill = false;
          this.populate();
        }
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
    /*if(((this.currentUser != this.billInfo.created_by && this.roles!.includes('FINANCEADMIN')) || this.roles!.includes('FINANCEADMIN'))
      && this.billInfo.bil_wf_status == 'WF-CN' && this.billInfo.history[0].msg_type != 'QCR') { //FA (not registrar!)
        this.hideActions = false;
        this.queryOnly = false;
        this.isApprover = true;
    }
    else if(this.currentUser == this.billInfo.created_by && this.billInfo.history[0].msg_type == 'QCR' && this.billInfo.bil_wf_status == 'WF-CN') //Owner of the bill to return FA query
        this.hideActions = false;*/

    var billing_no = this.billNo;
    this.isUser = this.currentUser == this.billInfo.created_by || this.currentName == this.billInfo.created_by;
    if(this.billInfo.bil_wf_status.includes('WF-CN'))
        this.hideActions = false;

    if(this.permAllowApp > 0) //if(this.roles!.includes('FINANCEADMIN') || this.permAllowApp > 0)
        this.isApprover = true;
    
    if((this.billInfo.history.length > 0 && this.billInfo.history[0].msg_type == 'QCR') || this.billInfo.has_query > 0){
      this.queryOnly = true;
      if(!this.isUser)
        this.hideActions = true;
    }
    else{
      if(!this.isApprover)
        this.hideActions = true;
    }

    if(environment.production && this.hideActions) //User not supposed to be in this screen! Reroute to details page
      this.router.navigate(['/billing-details'], { queryParams: { billing_no }});
        

    if (this.permAllowQuery === 0 && this.permAllowApp === 0) {
      if(environment.production)
        this.router.navigate(['/billing-details'], { queryParams: { billing_no }});
      alert('bad permission: ' + this.permCheckReturnString);  
    }

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
    /*
    this.loadBMParam();
    this.loadETParam();
    this.loadCSParam();
    */
    this.loadBSParam();
    this.loadParam(this.b_mthd as string,'Billing-Method');
    this.loadParam(this.ent_ty as string,'EntityType');
    this.loadParam(this.cust_state as string,'State');
    this.loadParam(this.billing_freq as string,'Billing-FreqType');

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
          else if (paramGrpNm == 'Billing-FreqType')
            this.billing_freq = response.data[0].nm_en;
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
    if(this.showInsertAlert == true)
      return true;
    if(this.cust_id == null || this.cust_id == '' || this.cust_id == '-')
      return true;
    if((this.remarks != null && this.remarks.length > 0) && this.selectedDecision == 'query')
      return false;
    else if(this.selectedDecision == 'aprv' || this.selectedDecision == 'rjct')
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
    //var tmp = <HTMLElement>document.getElementsByTagName('app-root')[0];
    //tmp.style.pointerEvents = 'none';
    //document.getElementById("mainBodyContainer")!.style.pointerEvents = 'none';
    //document.getElementById("f_overlay")!.style.display = 'block';
  }

  unfreezeScreen(){
    this.isLoading = false;
    //var tmp = <HTMLElement>document.getElementsByTagName('app-root')[0];
    //tmp.style.pointerEvents = 'all';
    //document.getElementById("mainBodyContainer")!.style.pointerEvents = 'all';
    //document.getElementById("f_overlay")!.style.display = 'none';
  }

  cancelForm(){
    location.href = 'billing-listing';
  }

  submitForm(){
    var url = environment.apiUrl;

    if(this.selectedDecision == 'aprv')
      url += '/api/billing/v1/aprovebillcan';
    else if(this.selectedDecision == 'rjct')
      url += '/api/billing/v1/rejectbillcan';
    else if (this.selectedDecision == 'query')
      url += '/api/billing/v1/querybillcan';

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

        this.showInsertAlert = true;
        this.unfreezeScreen();

        setTimeout(() => {
          this.showInsertAlert = false;
          this.router.navigate(['/billing-cancellation-listing']);
          location.href = 'billing-cancellation-listing';
          return;
        }, 5000);
      }
    },
      (error: any) => {
        console.log('fail update bill!');
        console.error(error);
        this.unfreezeScreen();
    });
  }

  loadPermission() {
    this.authService.checkUserRole(this.currentUser as string, this.permCheck)
      .subscribe((response: any) => {
          this.permCheckReturnString = response.data;
          this.permAllowQuery = this.permCheckReturnString.includes(perm.Billing_Cancellation_Request) ? 1 : 0;
          this.permAllowApp = this.permCheckReturnString.includes(perm.Billing_Cancellation_Approval) ? 1 : 0;
          this.isLoadingPerms = false;
          if(this.permAllowQuery == 0 && this.permAllowApp == 0)
            console.log(response.data);
          
          if(this.isBillUnauth){
            this.unfreezeScreen();
            this.isLoadingHistory = false;
          }
          else if(!this.isLoadingBill)
            this.populate();
        },
        (error: any) => {
          var billing_no = this.billNo;
          if(environment.production)
            this.router.navigate(['/billing-details'], { queryParams: { billing_no }});
          console.log(error);
          alert('permission load failed');
          this.isLoadingPerms = false;
          if(!this.isLoadingBill)
            this.populate();
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
  back(){
    window.history.back();
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
}
