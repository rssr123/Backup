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
  selector: 'app-billing-review',
  templateUrl: './billing-review.component.html',
  styleUrls: ['./billing-review.component.scss']
})
export class BillReviewComponent {
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
  ent_no: String | null = null;
  ss_cd: String | null = null;
  billing_no: String | null = null;
  billing_cnt: String | null = null;
  billing_freq: String | null = null;
  req_name: String | null = null;
  req_email: String | null = null;
  billing_items: any[] = [];
  old_billing_items: any[] = [];
  billing_list: any[] = [];
  old_billing_list: any[] = [];
  documents_list: any[] = [];
  history: any[] = [];
  billingStatusOptions: ParamData[] = [];
  ent_ty_list: ParamData[] = [];
  cust_state_list: ParamData[] = [];

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
  loa_id: string = '';
  agm_id: String | null = null;
  billing_desc: String | null = null;
  billing_mthd_string: String | null = null;
  ent_ty_string: String | null = null;
  cust_state_string: String | null = null;

  old_cust_nm: String | null = null;
  old_cust_email: String | null = null;
  old_cust_phone: String | null = null;
  old_cust_addr1: String | null = null;
  old_cust_addr2: String | null = null;
  old_cust_addr3: String | null = null;
  old_cust_postcode: String | null = null;
  old_cust_city: String | null = null;
  old_cust_state: String | null = null;
  old_ent_nm: String | null = null;
  old_ent_ty: String | null = null;
  old_req_name: String | null = null;
  old_req_email: String | null = null;
  old_billing_desc: String | null = null;
  old_loa_id: String | null = null;
  old_agm_id: String | null = null;

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
  canEdit: boolean = false;
  isItemUploadable: boolean = false;
  isUser: boolean = false;

  // default pagination
  selectedValue = environment.dropdownOptions[0];
  dropdownOptions = environment.dropdownOptions;
  dropDownSize = environment.DropDownSize;
  isLoadingHistory: boolean = true;
  isApprover: boolean = false;
  isLoading: boolean = true;
  isBillUnauth: boolean = false;

  cfm_loa_ref_no: string = '';
  loa_exists: boolean = false;
  loa_registered: boolean = true;
  has_checked_loa_exists: boolean = true;
  has_checked_loa_registered: boolean = true;
  showLoaAlert: boolean = false;
  loaAlertString: string = '';
  checkingLOA: boolean = false;

  loaCheckFlag1: boolean = true;
  loaCheckFlag2: boolean = true;

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

    this.loadCSParam();
    this.loadETParam();
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
        /*
        if(this.currentUser == this.billInfo.created_by && this.billInfo.history[0].msg_type == 'QR' && this.billInfo.bil_wf_status == 'WF-N'){ //Owner of the bill to return FA query / update data
            this.hideActions = false;
            this.canEdit = true;
          }
        */
        this.isUser = this.currentUser == this.billInfo.created_by || this.currentName == this.billInfo.created_by;

        if(this.billInfo.bil_wf_status.includes('WF-N'))
            this.hideActions = false;

        if(this.billInfo.history[0].msg_type == 'QR' || this.billInfo.has_query > 0)
          this.queryOnly = true;

        if(this.isUser && this.queryOnly)
          this.canEdit = true;

        if(this.queryOnly && !this.isUser)
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
    this.old_billing_items = this.billInfo.billing_items;
    this.billing_items = JSON.parse(JSON.stringify(this.billInfo.billing_items));
    this.billing_list = this.billInfo.billing_list;
    this.documents_list = this.billInfo.documents_list;
    this.history = this.billInfo.history;
    this.b_mthd = this.billInfo.billing_mthd;
    this.totalHistoryRecords = this.billInfo.history_size;
    this.totalRecordsListOfIssuance = this.billInfo.issuance_size;
    this.totalRecordsListOfDoc = this.billInfo.documents_size;
    this.totalRecordsListOfItem = this.billInfo.items_size;

    const clonedBillInfo = JSON.parse(JSON.stringify(this.billInfo));
    this.old_cust_nm = clonedBillInfo.cust_nm;
    this.old_cust_email = clonedBillInfo.cust_email;
    this.old_cust_phone = clonedBillInfo.cust_phone;
    this.old_cust_addr1 = clonedBillInfo.cust_addr1;
    this.old_cust_addr2 = clonedBillInfo.cust_addr2;
    this.old_cust_addr3 = clonedBillInfo.cust_addr3;
    this.old_cust_postcode = clonedBillInfo.cust_postcode;
    this.old_cust_city = clonedBillInfo.cust_city;
    this.old_cust_state = clonedBillInfo.cust_state;
    this.old_ent_nm = clonedBillInfo.ent_nm;
    this.old_ent_ty = clonedBillInfo.ent_ty;
    this.old_loa_id = clonedBillInfo.loa_id;
    this.old_agm_id = clonedBillInfo.agm_id;
    this.old_billing_desc = clonedBillInfo.billing_desc;
    this.old_req_name = clonedBillInfo.req_name;
    this.old_req_email = clonedBillInfo.req_email;

    this.cfm_loa_ref_no = JSON.parse(JSON.stringify(this.old_loa_id));

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
    else if(this.agm_id == null && this.loa_id != null){
      console.log(this.billInfo.dt_loa_start)
      this.dateRangeString = this.datepipe.transform(new Date(this.billInfo.dt_loa_start), 'dd MMM yyyy') + ' - ' + this.datepipe.transform(new Date(this.billInfo.dt_loa_end), 'dd MMM yyyy');
    }

    this.loadSourceSytemData();

    this.loadBSParam();
    this.ent_ty_string = this.getParamDescET(this.ent_ty as string);
    this.cust_state_string = this.getParamDescS(this.cust_state as string);
    this.loadParam(this.b_mthd as string,'Billing-Method');
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
          if (paramGrpNm == 'Billing-Method')
            this.billing_mthd_string = response.data[0].nm_en;
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
      this.ParamService.getStates(environment.DefaultPage.toString(), environment.ItemPerPage.toString(), '','EntityType').subscribe((response: any) => {
        if (response.data.length >= 0) {
          this.ent_ty_list = response.data as ParamData[];
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
      this.ParamService.getStates(environment.DefaultPage.toString(), environment.ItemPerPage.toString(), '', 'State').subscribe((response: any) => {
        if (response.data.length >= 0) {
          this.cust_state_list = response.data as ParamData[];
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

  getParamDescET(paramCd : String){
    for(const i of this.ent_ty_list)
      if(i.param_cd == paramCd)
        return i.nm_en;
    return paramCd;
  }

  getParamDescS(paramCd : String){
    for(const i of this.cust_state_list)
      if(i.param_cd == paramCd)
        return i.nm_en.charAt(0) + i.nm_en.substring(1).toLowerCase();;
    return paramCd;
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
    if(this.selectedDecision == 'cancel')
      return false;
    if(this.checkForErrorCustData())
      return true;
    if((this.b_mthd == 'L' && this.loa_exists) || (this.b_mthd == 'A' && !this.loa_registered))
      return true;
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

  async submitForm(){
    this.freezeScreen();

    if(this.cfm_loa_ref_no != this.loa_id){
      this.has_checked_loa_registered = false;
      this.has_checked_loa_exists = false;
    }

    if(this.b_mthd == 'A' && !this.has_checked_loa_registered)
      await this.checkLOARegistered();
    else if(this.b_mthd == 'L' && !this.has_checked_loa_exists)
      await this.checkLOAExists();

    this.submitFullForm();
  }

  async submitFullForm(){
    if(this.b_mthd == 'A' && !this.loa_registered){
      this.unfreezeScreen();
      return;
    }
    if(this.b_mthd == 'L' && this.loa_exists){
      this.unfreezeScreen();
      return;
    }

    var url = environment.apiUrl;

    if (this.selectedDecision == 'query')
      url += '/api/billing/v1/querybillwf';
    else if (this.selectedDecision == 'cancel')
      url += '/api/billing/v1/cancelbillwf';
    else
      return;

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

    if(this.selectedDecision == 'query'){
      if(!this.checkIfSameItemData())
        requestBody['i_billing_items'] = this.billing_items;
      if(!this.checkIfSameCustData()){
        var data: any = {
          i_cust_nm: this.cust_nm,
          i_cust_email: this.cust_email,
          i_cust_phone: this.cust_phone,
          i_cust_addr1: this.cust_addr1,
          i_cust_addr2: this.cust_addr2,
          i_cust_addr3: this.cust_addr3,
          i_cust_postcode: this.cust_postcode,
          i_cust_city: this.cust_city,
          i_cust_state: this.cust_state,
          i_ent_nm: this.ent_nm,
          i_ent_ty: this.ent_ty,
          i_loa_id: this.loa_id,
          i_agm_id: this.agm_id,
          i_billing_desc: this.billing_desc,
          i_req_name: this.req_name,
          i_req_email: this.req_email
        };
        requestBody['i_billing_info'] = data;
      }
    }

    console.log(requestBody);
    
    this.http.post(url, requestBody, { headers }).subscribe((response: any) => {
      if (response.data && response.data.length > 0) {
        console.log('success update bill!');
        console.log(response.data);

        this.unfreezeScreen();
        location.href = 'billing-listing';
      }
    },
      (error: any) => {
        console.log('fail update bill!');
        console.error(error);
        this.unfreezeScreen();
    });
  }

  recalcBICost(item: any){
    item.unit_fee = ~~parseFloat(item.unit_fee);
    item.qty = ~~parseFloat(item.qty);
    item.tax_amt = parseFloat((((item.unit_fee*item.qty)*((item.tax_pct+100)/100))-(item.unit_fee*item.qty)).toFixed(2));
    this.total_amount -= item.final_amt; //remove old total
    item.final_amt = parseFloat(((item.unit_fee*item.qty) + item.tax_amt).toFixed(2));
    this.total_amount += item.final_amt;
  }

  checkIfSameItemData(){
    for(const item of this.billing_items){
        const old = this.old_billing_items.find((option) => option.mft_pk === item.mft_pk);
        if(old.qty != item.qty || old.unit_fee != item.unit_fee)
          return false;
    }
    return true;
  }
  
  checkIfSameCustData(){
    if(this.old_cust_nm != this.cust_nm || this.old_cust_email != this.cust_email ||
        this.old_cust_phone != this.cust_phone || this.old_cust_addr1 != this.cust_addr1 ||
        this.old_cust_addr2 != this.cust_addr2 || this.old_cust_addr3 != this.cust_addr3 ||
        this.old_cust_postcode != this.cust_postcode || this.old_cust_city != this.cust_city ||
        this.old_cust_state != this.cust_state || this.old_ent_nm != this.ent_nm ||
        this.old_ent_ty != this.ent_ty || this.old_loa_id != this.loa_id ||
        this.old_agm_id != this.agm_id || this.old_billing_desc != this.billing_desc ||
        this.old_req_name != this.req_name || this.old_req_email != this.req_email){

      var emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
      if(!emailRegex.test(this.cust_email as string) || !emailRegex.test(this.req_email as string))
        return true;

      if(this.checkForErrorCustData())
        return true;

      return false;
    }
    return true;
  }

  checkForErrorCustData(){
    if(this.b_mthd == 'A' && (this.agm_id == '' || this.loa_id == '' || this.agm_id == null || this.loa_id == null))
      return true;
    if(this.b_mthd == 'L' && (this.loa_id == '' || this.loa_id == null))
      return true;
    if(this.cust_nm == '' || this.cust_email == '' || this.cust_phone == '' || this.cust_addr1 == '' || this.cust_addr2 == '' || this.cust_postcode == '' || this.cust_city == '' || this.cust_state == '' || this.ent_nm == '' || this.ent_ty == '' || this.billing_desc == '' || this.req_name == '' || this.req_email == '' || this.cust_nm == null || this.cust_email == null || this.cust_phone == null || this.cust_addr1 == null || this.cust_addr2 == null || this.cust_postcode == null || this.cust_city == null || this.cust_state == null || this.ent_nm == null || this.ent_ty == null || this.billing_desc == null || this.req_name == null || this.req_email == null)
        return true;
      return false;
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

    async checkLOAExists(){
    if(this.old_loa_id == this.loa_id){
      this.loa_exists = false;
      this.loaAlertString = '';
      this.showLoaAlert = false;
      this.cfm_loa_ref_no = JSON.parse(JSON.stringify(this.loa_id));
      this.has_checked_loa_exists = true;
      return;
    }

    if(this.loa_id == null || this.loa_id == '')
      return;
    const url = environment.apiUrl + '/api/billing/v1/getexistsloa';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json'
    });

    // Create the request body with your form data
    var requestBody: {[k: string]: any} = {
      i_loa_ref_no: this.loa_id
    };

    this.checkingLOA = true;
    console.log(requestBody);
    this.http.post(url, requestBody, { headers }).subscribe((response: any) => {
      if (response.data && response.data.length > 0) {
        console.log('grab loa exists:' + response.data);
        if(parseFloat(response.data) == 0){
          this.loa_exists = false;
          this.loaAlertString = '';
          this.showLoaAlert = false;
          this.cfm_loa_ref_no = JSON.parse(JSON.stringify(this.loa_id));
        }
        else{
          this.loa_exists = true;
          this.loaAlertString = 'LOA Reference already exists!';
          this.showLoaAlert = true;
        }
        
        this.has_checked_loa_exists = true;
        this.checkingLOA = false;
      }
    },
      (error: any) => {
        console.log('fail check loa exists!');
        console.error(error);
    });
  }

  async checkLOARegistered(){
    if(this.old_loa_id == this.loa_id){
      this.loa_registered = true;
      this.loaAlertString = '';
      this.showLoaAlert = false;
      this.cfm_loa_ref_no = JSON.parse(JSON.stringify(this.loa_id));
      this.has_checked_loa_registered = true;
      return;
    }
    if(this.loa_id == null || this.loa_id == '')
      return;
    const url = environment.apiUrl + '/api/billing/v1/getregisteredloa';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json'
    });

    // Create the request body with your form data
    var requestBody: {[k: string]: any} = {
      i_loa_ref_no: this.loa_id
    };

    this.checkingLOA = true;
    console.log(requestBody);
    this.http.post(url, requestBody, { headers }).subscribe((response: any) => {
      if (response.data && response.data.length > 0) {
        console.log('grab loa registered: ' + response.data);
        if(parseFloat(response.data) > 0){
          this.loa_registered = true;
          this.loaAlertString = '';
          this.showLoaAlert = false;
          this.cfm_loa_ref_no = JSON.parse(JSON.stringify(this.loa_id));
        }
        else{
          this.loa_registered = false;
          this.loaAlertString = 'LOA Reference not registered!';
          this.showLoaAlert = true;
        }

        this.has_checked_loa_registered = true;
        this.checkingLOA = false;
      }
    },
      (error: any) => {
        console.log('fail check loa registered!');
        console.error(error);
    });
  }

  resetLOAFlag1(){
    if(!this.loaCheckFlag1)
      return;
    this.loa_exists = true;
    this.has_checked_loa_exists = false;
    this.loaCheckFlag1 = false;
  }

  resetLOAFlag2(){
    if(!this.loaCheckFlag2)
      return;
    this.loa_registered =false;
    this.has_checked_loa_registered = false;
    this.loaCheckFlag2 = false;
  }
}
