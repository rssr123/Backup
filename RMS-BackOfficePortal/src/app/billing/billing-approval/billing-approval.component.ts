import { HttpClient, HttpHeaders } from '@angular/common/http';
import { ChangeDetectorRef, ViewChild, Component } from '@angular/core';
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
import { PostCodeData } from 'src/app/core/models/postcode.interface';
import moment from 'moment';
import { FormBuilder, FormControl, FormGroup, NgForm, NgModel, Validators } from '@angular/forms';

@Component({
  selector: 'app-billing-approval',
  templateUrl: './billing-approval.component.html',
  styleUrls: ['./billing-approval.component.scss']
})
export class BillApprovalComponent {
  @ViewChild('cityRef') cityRef!: NgModel;
  @ViewChild('stateRef') stateRef!: NgModel;
  @ViewChild('postcodeRef') postcodeRef!: NgModel;

  postCodes: PostCodeData[] = [];
  uniqueCities: string[] = [];
  uniqueStates: string[] = [];
  totalPostCodeRecords: number = 0;
  shortformstate: string | null = null;

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
  ent_no: String | null = '-';
  ss_cd: String | null = '-';
  billing_no: String | null = '-';
  billing_cnt: String | null = '-';
  billing_freq: String | null = '-';
  req_name: String | null = '-';
  req_email: String | null = '-';
  billing_items: any[] = [];
  old_billing_items: any[] = [];
  billing_list: any[] = [];
  old_billing_list: any[] = [];
  documents_list: any[] = [];
  history: any[] = [];
  billingStatusOptions: ParamData[] = [];
  ent_ty_list: ParamData[] = [];
  states: ParamData[] = [];

  cust_nm: String | null = '-';
  cust_email: String | null = '-';
  cust_phone: String | null = '-';
  cust_addr1: String | null = '-';
  cust_addr2: String | null = '-';
  cust_addr3: String | null = '-';
  postcode: String | null = '-';
  city: String | null = '-';
  state: String | null = '-';
  ent_nm: String | null = '-';
  ent_ty: String | null = '-';
  loa_id: string = '-';
  agm_id: String | null = '-';
  billing_desc: String | null = '-';
  billing_mthd_string: String | null = '-';
  ent_ty_string: String | null = '-';
  cust_state_string: String | null = '-';

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

  permCheck = perm.Billing_Registration + ',' + perm.Billing_Registration_Approval;
  permCheckReturnString = ""; // variable to store allowed permission for the user
  permAllowQuery: number = 0;
  permAllowApp: number = 0;
  showInsertAlert: boolean = false;

  maxFileSize = 10 * 1024 * 1024; // 10MB
  selectedFiles: File[] = [];
  selectedFilesSize: number = 0;
  errorFile: boolean = false;
  errorFileMessages: string[] = [];
  errorFileSizeLimit: boolean = false;
  errorFileSizeLimitMessages: string[] = [];
  errorFileDuplicate: boolean = false;
  errorFileDuplicateMessages: string[] = [];

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
      this.authService.getName().subscribe(response =>{
        this.currentName = response;
        if(this.currentName == null || this.currentName == 'undefined' || this.currentName == '' || this.currentName == 'Anonymous')
            this.currentName = 'Anonymous';
      });
    });

    this.loadCSParam();
    this.loadETParam();
    if(this.billNo != null){
      this.loadPermission();
      this.loadPostcode();
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

        if(this.billInfo.bil_wf_status.includes('WF-N'))
            this.hideActions = false;

        this.isLoadingBill = false;

        //console.log(response.data);
        if(response.data.status == 'UNAUTHORIZED'){
          this.isBillUnauth = true;
          this.unfreezeScreen();
          this.isLoadingHistory = false;
        }
        else if(!this.isLoadingPerms)
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
    var billing_no = this.billNo;
    this.isUser = this.currentUser == this.billInfo.created_by || this.currentName == this.billInfo.created_by;
    if(this.permAllowApp > 0)   //if(this.roles!.includes('FINANCEADMIN') || this.permAllowApp > 0)
        this.isApprover = true;
    //console.log(this.billInfo)
    if((this.billInfo.history.length > 0 && this.billInfo.history[0].msg_type == 'QR') || this.billInfo.has_query > 0){
      this.queryOnly = true;
      if(this.isUser)
        this.canEdit = true;
      else
        this.hideActions = true;
    }
    else{
      if(!this.isApprover)
        this.hideActions = true;
    }

    if(environment.production && this.hideActions) //User not supposed to be in this screen! Reroute to details page
      this.router.navigate(['/billing-details'], { queryParams: { billing_no }});
        
    var billing_no = this.billNo;
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
    this.postcode = this.billInfo.cust_postcode;
    this.city = this.billInfo.cust_city;
    this.state = this.billInfo.cust_state;
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
    else if(this.agm_id == null && this.loa_id != null)
      this.dateRangeString = this.datepipe.transform(new Date(this.billInfo.dt_loa_start), 'dd MMM yyyy') + ' - ' + this.datepipe.transform(new Date(this.billInfo.dt_loa_end), 'dd MMM yyyy');

    this.loadSourceSytemData();

    this.loadBSParam();
    this.ent_ty_string = this.getParamDescET(this.ent_ty as string);
    this.cust_state_string = this.getParamDescS(this.state as string);
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
          this.states = response.data as ParamData[];
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
    for(const i of this.states)
      if(i.param_cd == paramCd)
        return i.nm_en; //i.nm_en.charAt(0) + i.nm_en.substring(1).toLowerCase();
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
    if(this.showInsertAlert == true)
      return true;
    if(this.selectedDecision == 'aprv' || this.selectedDecision == 'rjct' || this.selectedDecision == 'cancel')
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
    //var tmp = <HTMLElement>document.getElementsByTagName('app-root')[0];
    //tmp.style.pointerEvents = 'none';
    //document.getElementById("mainBodyContainer")!.style.pointerEvents = 'none';
    //document.getElementById("f_overlay")!.style.display = 'block';
  }

  unfreezeScreen(){
    this.isLoading = false;
    this.isLoadingHistory = false;
    //var tmp = <HTMLElement>document.getElementsByTagName('app-root')[0];
    //tmp.style.pointerEvents = 'all';
    //document.getElementById("mainBodyContainer")!.style.pointerEvents = 'all';
    //document.getElementById("f_overlay")!.style.display = 'none';
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

    if(this.selectedDecision == 'aprv')
      url += '/api/billing/v1/aprovebillwf';
    else if(this.selectedDecision == 'rjct')
      url += '/api/billing/v1/rejectbillwf';
    else if (this.selectedDecision == 'query')
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
        i_remark: this.remarks
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
          i_cust_postcode: this.postcode,
          i_cust_city: this.city,
          i_cust_state: this.shortformstate,
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
      if(this.selectedFilesSize != 0){
        var fileArray: any[] = [];
        for(const file of this.selectedFiles){
          const base64Content = await new Promise<string>((resolve, reject) => {
            const reader = new FileReader();
            reader.onload = (e: any) => resolve(e.target.result);
            reader.onerror = reject;
            reader.readAsDataURL(file);
          });
      
          const fileBody = {
            i_file_nm: file.name,
            i_file_content: base64Content,
            i_file_type: file.type,
            i_file_size: file.size,
            i_file_category: 'S' // Supporting Documents or LOA/Agreement
          };
          fileArray.push(fileBody);
        }
        requestBody['i_supporting_documents'] = fileArray;
      }
    }

    //console.log(requestBody);
    this.freezeScreen();
    
    this.http.post(url, requestBody, { headers }).subscribe((response: any) => {
      if (response.data && response.data.length > 0) {
        console.log('success update bill!');
        console.log(response.data);

        this.showInsertAlert = true;
        this.unfreezeScreen();

        setTimeout(() => {
          this.showInsertAlert = false;
          this.router.navigate(['/billing-listing']);
          location.href = 'billing-listing';
          return;
        }, 5000);
      }
    },
      (error: any) => {
        console.log('fail update bill!');
        console.error(error);
        this.unfreezeScreen();
        if(error.error.data != null){
          if(error.error.data.ari_failed_code < -5){
            console.log('due to missing fms billing image!');
            if(!(localStorage.getItem('debug') == 'true')){
              this.showInsertAlert = true;
              setTimeout(() => {
                this.showInsertAlert = false;
                this.router.navigate(['/billing-listing']);
                location.href = 'billing-listing';
                return;
              }, 5000);
            }
          }
        }
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
    this.updateShortformState();
    if(this.old_cust_nm != this.cust_nm || this.old_cust_email != this.cust_email ||
        this.old_cust_phone != this.cust_phone || this.old_cust_addr1 != this.cust_addr1 ||
        this.old_cust_addr2 != this.cust_addr2 || this.old_cust_addr3 != this.cust_addr3 ||
        this.old_cust_postcode != this.postcode || this.old_cust_city != this.city ||
        this.old_cust_state != this.shortformstate || this.old_ent_nm != this.ent_nm ||
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
    if(this.b_mthd == 'A' && (this.agm_id == '-' || this.loa_id == '-' || this.agm_id == null || this.loa_id == null))
      return true;
    if(this.b_mthd == 'L' && (this.loa_id == '-' || this.loa_id == null))
      return true;
    if(this.cust_nm == '' || this.cust_email == '' || this.cust_phone == '' || this.cust_addr1 == '' || this.cust_addr2 == '' || this.postcode == '' || this.city == '' || this.state == '' || this.ent_nm == '' || this.ent_ty == '' || this.billing_desc == '' || this.req_name == '' || this.req_email == '' || this.cust_nm == '-' || this.cust_email == '-' || this.cust_phone == '-' || this.cust_addr1 == '-' || this.cust_addr2 == '-' || this.postcode == '-' || this.city == '-' || this.state == '-' || this.ent_nm == '-' || this.ent_ty == '-' || this.billing_desc == '-' || this.req_name == '-' || this.req_email == '-' || this.cust_nm == null || this.cust_email == null || this.cust_phone == null || this.cust_addr1 == null || this.cust_addr2 == null || this.postcode == null || this.city == null || this.state == null || this.ent_nm == null || this.ent_ty == null || this.billing_desc == null || this.req_name == null || this.req_email == null)
        return true;
      return false;
  }

  loadPermission() {
    this.authService.checkUserRole(this.currentUser as string, this.permCheck)
      .subscribe((response: any) => {
          this.permCheckReturnString = response.data;
          this.permAllowQuery = this.permCheckReturnString.includes(perm.Billing_Registration) ? 1 : 0;
          this.permAllowApp = this.permCheckReturnString.includes(perm.Billing_Registration_Approval) ? 1 : 0;
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
        //console.log('grab loa exists:' + response.data);
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

  //postcode start
  loadPostcode() {

    const url = environment.apiUrl + '/api/rms/v1/getpostcode';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    // const Body: any = {
    // };

    this.http.post(url,{}, { headers }).subscribe(
      (response: any) => {

        if (response.data.length === 0) {
          this.totalPostCodeRecords = 0;
        } else {
          this.postCodes = response.data;
          this.totalPostCodeRecords = response.data[0].total;
          this.extractUniqueCitiesAndStates();
        }
      },
      (error) => {
        console.error('There was an error retrieving the postcode:', error);
      }
    );

  }

  onPostcodeChange(selectedPostcode: string | null) {
    if (!selectedPostcode) {
      this.city = null;
      this.state = null;
  
      if (this.cityRef) this.cityRef.control.markAsTouched();
      if (this.stateRef) this.stateRef.control.markAsTouched();
  
      return;
    }

    const match = this.postCodes.find(p => String(p.postcode) === selectedPostcode);
    this.city = match ? match.city : null;
    this.state = match ? match.state : null;
  }


  extractUniqueCitiesAndStates() {
    this.uniqueCities = [...new Set(this.postCodes.map(p => p.city))].sort((a, b) =>
      a.localeCompare(b)
    );

    this.uniqueStates = [...new Set(this.postCodes.map(p => p.state))].sort((a, b) =>
      a.localeCompare(b)
    );
  }

  upperCity = (term: string): string => {
    return (term ?? '').toUpperCase(); 
  };


  checkTag = (term: string): string => {
    const trimmed = (term ?? '').trim();
  
    // Allow any numeric tag up to 5 digits, but trigger error if not exactly 5 digits
    if (/^\d{1,5}$/.test(trimmed)) {
      if (trimmed.length < 5) {
        this.postcodeRef?.control.setErrors({ minlength: true });
      } else {
        this.postcodeRef?.control.setErrors(null);
      }
      return trimmed;
    }
  
    return trimmed;
  };

  allow5Numbers(event: KeyboardEvent): void {
    const input = event.target as HTMLInputElement;
    const pattern = /^[0-9]$/;
    const inputChar = String.fromCharCode(event.charCode);
  
    // Block non-numeric characters
    if (!pattern.test(inputChar)) {
      event.preventDefault();
      return;
    }
  
    // Limit to 5 digits only
    if (input.value.length >= 5) {
      event.preventDefault();
    }
  }

  //postcode end

  //get param_cd of state
  updateShortformState(): void {
    const match = this.states.find(s => s.nm_en === this.cust_state_string!.toUpperCase());
    this.shortformstate = match ? match.param_cd : null;
  }
  back(){
    window.history.back();
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

  clearSupportingFiles(): void {
    this.selectedFiles = [];
    this.selectedFilesSize = 0;

    this.errorFile = false;
    this.errorFileMessages = [];
    this.errorFileSizeLimit = false;
    this.errorFileSizeLimitMessages = [];
    this.errorFileDuplicate = false;
    this.errorFileDuplicateMessages = [];
  }

  async onSupportingFilesSelected(event: any){
    const files: FileList = event.target.files;
    let currentFilesTotalSize = this.selectedFilesSize;

    const allowedExtensions = ['png', 'jpeg', 'jpg', 'pdf', 'doc', 'docx'];
    this.errorFile = false;
    this.errorFileMessages = [];
    this.errorFileSizeLimit = false;
    this.errorFileSizeLimitMessages = [];
    this.errorFileDuplicate = false;
    this.errorFileDuplicateMessages = [];
  
    for (let i = 0; i < files.length; i++) {
      const file = files[i];
  
      if (currentFilesTotalSize + file.size > 10 * 1024 * 1024) {
        //alert('Total file size exceeds 10MB. Please select smaller files.');
        this.errorFileSizeLimit = true;
        this.errorFileSizeLimitMessages.push(this.translate.instant('labels.filesize10MBlimitmessage'));
        continue;
      }        

      const fileName = files[i].name;
      const fileExtension = fileName.split('.').pop()?.toLowerCase(); // Extract file extension
      if (!allowedExtensions.includes(fileExtension as string)) {
          this.errorFile = true;
          this.errorFileMessages.push(fileName);
          continue; // Skip this file and check the next one
      }
  
      if (!this.selectedFiles.some((f) => f.name === file.name)) {
        this.selectedFiles.push(file);
        currentFilesTotalSize += file.size;
      } else {
        //alert(`File "${file.name}" already selected.`);          
        this.errorFileDuplicate = true;
        this.errorFileDuplicateMessages.push(this.translate.instant('labels.File') + ' ' + files[i].name +  ' ' + this.translate.instant('labels.alreadyselected'));
      }
    }
    this.selectedFilesSize = currentFilesTotalSize;
  }
}
