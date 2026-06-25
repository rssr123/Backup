import { HttpClient, HttpHeaders } from '@angular/common/http';
import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { Router, ActivatedRoute } from '@angular/router';
import { NgbPaginationConfig } from '@ng-bootstrap/ng-bootstrap';
import { TranslateService } from '@ngx-translate/core';
import { AuthService } from 'src/app/services/auth.service';
import { GlobalService } from 'src/app/shared/global.service';
import { environment } from 'src/environments/environment';
import { ParamService } from 'src/app/core/services/param.service';
import { ParamData } from 'src/app/core/models/param.interface';
import saveAs from 'file-saver';

@Component({
  selector: 'app-billing-registration',
  templateUrl: './billing-registration.component.html',
  styleUrls: ['./billing-registration.component.scss']
})
export class BillRegistrationComponent implements OnInit {
  page = environment.DefaultPage;
  itemsPerPage = environment.ItemPerPage;

  bt_code: string = '';
  btc_list: string[] = [];

  entity_type: string = '';
  entity_types: ParamData[] = [];

  entity_no: string = '';
  cust_exist_btn_first_click: boolean = false;
  cust_id: string = '';
  cus_name: string = '';
  cus_email: string = '';
  cus_phno: string = '';
  cus_add1: string = '';
  cus_add2: string = '';
  cus_add3: string = '';
  postcode: string = '';
  city: string = '';
  state: string | null = null;
  states: ParamData[] = [];
  entity_name: string = '';

  sscode: string = '';
  billing_no: string = '';
  billing_methods: ParamData[] = [];
  billing_method: string = 'O';
  billing_desc: string = '';
  billingStatusOptions: ParamData[] = [];

  requester_name: string = '';
  requester_email: string = '';


  billing_items: any[] = [
    {
      desc:'-', //Stuct of desc is 'fee_detail_id - fee_detail_nm_e'
      price:0,
      qty:0,
      tax:0,
      taxc:0,
      total:0,
      mftPk:0
    }];

  billing_items_total_cost: number = 0;

  histModel: any[] = [];
  /*[
    {
      action:'-',
      dt_action: null,
      performed_by: '-',
      assigned_to: '-',
      remark:'-'
    }];*/

  maxFileSize = 10 * 1024 * 1024; // 10MB
  selectedFiles: File[] = [];
  selectedFilesSize: number = 0;

  remarks: string = '';

  btcMapData: Map<string, any> = new Map<string, any>();

  //date_range: Date[] = [new Date((new Date()).getFullYear() + '-01-01'), new Date()];
  date_range: Date[] = [new Date(), new Date(new Date().getFullYear(), 11, 31)];
  isuance_counter: number = this.monthDiff(this.date_range[0], this.date_range[1]);
  isuance_count: number = 0;
  
  BFreqT: any[] = [];
  billing_frequency: number = 1;
  billing_day: number = 1;

  billing_issuance_list: any[] = [];

  loa_ref_no: string = '';
  cfm_loa_ref_no: string = '';
  agmt_ref_no: string = '';

  loa_exists: boolean = true;
  loa_registered: boolean = false;
  has_checked_loa_exists: boolean = false;
  has_checked_loa_registered: boolean = false;
  showLoaAlert: boolean = false;
  loaAlertString: string = '';
  checkingLOA: boolean = false;

  selectedLoaFile: File | any = null;
  selectedLoaFileSize: number = 0;

  maxIssuanceForLOA: number = 3;

  isLoading: boolean = false;
  isSearchingCustomerId: boolean = false;
  failFindCustId: boolean = false;

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
    private authService: AuthService
  ) {
    config.maxSize = environment.PaginationMaxSize;
    config.boundaryLinks = true;
    this.translate.setDefaultLang(this.globalService.getGlobalValue()); // default language
    this.translate.use(this.globalService.getGlobalValue());
    const navigation = this.router.getCurrentNavigation();
  }

  ngOnInit() {
    this.loadBFreq();
    this.loadBillingMethods();
    this.loadETypes();
    this.loadStates();
    this.loadBSParam();
    this.fetchBillingRegListingData();
    this.loadRunningBillingNo();
    
    this.authService.getName().subscribe(response =>{
      this.requester_name = response;
    });

    this.authService.getEmail().subscribe(response =>{
      this.requester_email = response;
    });
  }

  searchCustomerId(entity_no: string){
    const headers = new HttpHeaders({
      'Content-Type': 'application/json'
    });
    const url = environment.fmsCheckCustIdApi;

    var headerValue = 'LOCAL';

    if(!environment.production){  //debug only
      if(this.entity_type == 'I' || this.entity_type == 'C')
        headerValue = 'LOCAL1';
    }


    var header: any = {
        value: headerValue
    };
    var nbrDataType = 'CustomStringField';
    var nbrData: any = {
      type: nbrDataType,
      value: entity_no
    };
    var ccData: any = {
      UsrIdentityNbr: nbrData
    };
    var data: any = {
      CurrentCustomer: ccData
    };
    var body: any = {
      CustomerClass: header,
      custom: data
    };

    this.isSearchingCustomerId = true;
    this.http.post(url, body, { headers }).subscribe(
      (response: any) => {
        if(response){
          this.cust_id = response.CustomerID;
          this.isSearchingCustomerId = false;
          this.failFindCustId = false;
        }
		    if (!this.cust_exist_btn_first_click)
		    	this.cust_exist_btn_first_click = true;
      },
      (error) => {
        console.error(error);
        this.isSearchingCustomerId = false;
        this.cust_exist_btn_first_click = true;
        this.failFindCustId = true;
      }
    );
  }

  updateBTC(bt_code: string){
    this.billing_items_total_cost = 0;
    this.sscode = this.btcMapData.get(bt_code)[0];
    this.billing_items = [];
    console.log(this.btcMapData);
    if(this.btcMapData.get(bt_code)[1].length > 0){
      for(var i = 0; i < this.btcMapData.get(bt_code)[1].length; i++){
        this.billing_items.push({              
          desc:this.translate.store.currentLang === 'en' ? 
            this.btcMapData.get(bt_code)[1][i][0] : this.btcMapData.get(bt_code)[1][i][3],
          price:parseFloat(this.btcMapData.get(bt_code)[1][i][1]),
          qty:0,
          tax:parseFloat(this.btcMapData.get(bt_code)[1][i][2]),
          taxc:0,
          total:0,
          mftPk:parseFloat(this.btcMapData.get(bt_code)[1][i][4])
        });
        this.recalcBICost(this.billing_items[this.billing_items.length-1]);
      }
    }
    else{
      this.billing_items.push({              
          desc:'-',
          price:0,
          qty:0,
          tax:0,
          taxc:0,
          total:0,
          mftPk:0
        });
        this.recalcBICost(this.billing_items[this.billing_items.length-1]);
    }
  }

  recalcBICost(item: any){
    item.unit_fee = ~~parseFloat(item.unit_fee);
    item.qty = ~~parseFloat(item.qty);
    item.taxc = parseFloat((((item.price*item.qty)*((item.tax+100)/100))-(item.price*item.qty)).toFixed(2));
    this.billing_items_total_cost -= item.total; //remove old total
    item.total = (item.price*item.qty) + item.taxc;
    this.billing_items_total_cost += item.total;
  }

  updateIssuanceList(){
    if(document.getElementById('loadaterange')!.className.includes('untouched') && this.billing_method == 'L')
      this.date_range = [new Date(), new Date(new Date(this.date_range[0]).setMonth(this.date_range[0].getMonth()+3+(this.date_range[0] > new Date() ? 1 : 0)))];
    else if(document.getElementById('loadaterange')!.className.includes('untouched') && this.billing_method == 'A')
      this.date_range = [new Date(), new Date(new Date().getFullYear(), 11, 31)];

    this.billing_issuance_list = [];
    this.isuance_count = 0;
    this.isuance_counter = this.monthDiff(this.date_range[0], this.date_range[1]);
    if(this.isuance_counter == 0)
      this.isuance_counter = 1;
    if(this.billing_day == 0)
      this.billing_day = 1;
    
    const dateTracker = new Date(this.date_range[0]);

    if(this.billing_day > 28){
      var testDate = new Date(dateTracker);
      var day = this.billing_day;
      if(new Date(testDate.setDate(this.billing_day)).getMonth() != dateTracker.getMonth()){
        dateTracker.setMonth(dateTracker.getMonth()+1);
        day = 0;
      }
      dateTracker.setDate(day);
    }
    else
      dateTracker.setDate(this.billing_day);
    
    var running_billing_no = parseFloat(this.billing_no.split('BIL')[1]);

    for(var i = 0; i < this.isuance_counter; i++){
      if(i%this.billing_frequency == 0){
        this.billing_issuance_list.push({['billing_date']: new Date(dateTracker),
                                        ['billing_no'] : (dateTracker.getTime() > new Date().getTime()) ? (!(this.billing_method == 'L' && this.isuance_count > this.maxIssuanceForLOA-1) ? 'BIL' + running_billing_no : 'N/A') : 'N/A',
                                        ['valid'] : dateTracker.getTime() > new Date().getTime(),
                                        ['issuance'] : dateTracker.getTime() > new Date().getTime() ? (this.billing_method == 'L' ? this.isuance_count < this.maxIssuanceForLOA : dateTracker.getTime() > new Date().getTime()) : false});
        if(this.billing_method == 'L' && this.isuance_count > this.maxIssuanceForLOA-1)
          continue;
        if(dateTracker.getTime() > new Date().getTime()){
          this.isuance_count += 1;
          running_billing_no +=1;
        }
      }
      dateTracker.setDate(1);
      dateTracker.setMonth(dateTracker.getMonth()+1);
      if(this.billing_day > 28){
        var testDate = new Date(dateTracker);
        var day = this.billing_day;
        if(new Date(testDate.setDate(this.billing_day)).getMonth() != dateTracker.getMonth()){
          dateTracker.setMonth(dateTracker.getMonth()+1);
          day = 0;
        }
        dateTracker.setDate(day);
      }
      else
        dateTracker.setDate(this.billing_day);
    }
  }

  unissueBillingItem(item: any){
    var running_billing_no = parseFloat(this.billing_no.split('BIL')[1]);
    this.isuance_count = 0;
    item.issuance = !item.issuance;
    for(var i = 0; i < this.isuance_counter; i++){
      if(this.billing_issuance_list[i].issuance){
        this.billing_issuance_list[i].billing_no = 'BIL' + running_billing_no;
        running_billing_no +=1;
        this.isuance_count += 1;
      }
      else          
        this.billing_issuance_list[i].billing_no = 'N/A';
    }
  }

  fetchBillingRegListingData(){
    const url = environment.apiUrl + '/api/billing/v1/bcdlistreg';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/x-www-form-urlencoded'
    });

    // Make the HTTP GET request
    this.http.get(url, {headers : headers}).subscribe(
      (response:any) => {
        console.log(response.data)
        for(var prop in response.data){
          this.btc_list.push(prop);
          this.btcMapData.set(prop, response.data[prop]);
        }
        this.btc_list.sort((a, b) => a.localeCompare(b)); // sorting
      },
      (error: any) => {
        console.error(error);
      }
    );
  }

  getEntity(entity_type: string){
    for(const param of this.entity_types)
      if(param.param_cd == entity_type)
        return param.nm_en;
    return '';
  }

  roundTo(n: number, digits: number) {
      var negative = false;
      if (digits === undefined) {
          digits = 0;
      }
      if (n < 0) {
          negative = true;
          n = n * -1;
      }
      var multiplicator = Math.pow(10, digits);
      n = parseFloat((n * multiplicator).toFixed(11));
      n = parseFloat((Math.round(n) / multiplicator).toFixed(digits));
      if (negative) {
          n = parseFloat((n * -1).toFixed(digits));
      }
      return n;
  }

  monthDiff(d1: Date, d2: Date) {
      var months;
      months = (d2.getFullYear() - d1.getFullYear()) * 12;
      months -= d1.getMonth();
      months += d2.getMonth();
      return months <= 0 ? 0 : months+1;
  }

  nthNumber(number: number): string{
    if (number > 3 && number < 21) return "th";
    switch (number % 10) {
      case 1:
        return "st";
      case 2:
        return "nd";
      case 3:
        return "rd";
      default:
        return "th";
    }
  }

  clearSupportingFiles(): void {
    this.selectedFiles = [];
    this.selectedFilesSize = 0;

  }

  onSupportingFilesSelected(event: any): void {
    const files: FileList = event.target.files;
    let currentFilesTotalSize = this.selectedFilesSize;
  
    for (let i = 0; i < files.length; i++) {
      const file = files[i];
  
      if (currentFilesTotalSize + file.size > 10 * 1024 * 1024) {
        alert('Total file size exceeds 10MB. Please select smaller files.');
        continue;
      }
  
      if (!this.selectedFiles.some((f) => f.name === file.name)) {
        this.selectedFiles.push(file);
        currentFilesTotalSize += file.size;
      } else {
        alert(`File "${file.name}" already selected.`);
      }
    }
  
    this.selectedFilesSize = currentFilesTotalSize;
  }

  onLoaFileSelected(event: any): void {
    const file: File = event.target.files[0];
  
    if (file) {
      if (file.size > 10 * 1024 * 1024) {
        alert('File size exceeds 10MB. Please select a smaller file.');
        return;
      }
  
      this.selectedLoaFile = file;
      this.selectedLoaFileSize = file.size;
    }
  }

  clearLoaFile(): void {
    this.selectedLoaFile = null;
    this.selectedLoaFileSize = 0;
  }

  loadRunningBillingNo(){
    this.billing_no = '';
    const url = environment.apiUrl + '/api/billing/v1/bcdrunnoreg';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/x-www-form-urlencoded'
    });

    // Make the HTTP GET request
    this.http.get(url, {headers : headers}).subscribe(
      (response:any) => {
        if(response.data.length > 0)  
          this.billing_no = response.data;
        this.updateIssuanceList();
      },
      (error: any) => {
        console.error(error);
      }
    );
  }

  loadETypes(){
      this.ParamService.getStates(environment.DefaultPage.toString(), environment.ItemPerPage.toString(), '', 'EntityType').subscribe((response: any) => {
        if (response.data.length >= 0) {
          this.entity_types = response.data as ParamData[];
          this.entity_types.sort((a, b) => a.nm_en.localeCompare(b.nm_en)); // sorting
        } 
        else
          console.error('Invalid response format:', response);
      },
      (error) => {
        console.error('There was an error retrieving the status:', error);
      }
    );
  }

  loadStates() {
      this.ParamService.getStates(environment.DefaultPage.toString(), environment.ItemPerPage.toString(), '', 'State').subscribe((response: any) => {
        if (response.data.length >= 0) {
          this.states = response.data as ParamData[];
          this.states.sort((a, b) => a.nm_en.localeCompare(b.nm_en)); // sorting
        } 
        else
          console.error('Invalid response format:', response);
      },
      (error) => {
        console.error('There was an error retrieving the status:', error);
      }
    );
  }

  loadBillingMethods() {
      this.ParamService.getStates(environment.DefaultPage.toString(), environment.ItemPerPage.toString(), '', 'Billing-Method').subscribe((response: any) => {
        if (response.data.length >= 0) {
          this.billing_methods = response.data as ParamData[];
          this.billing_methods.sort((a, b) => a.nm_en.localeCompare(b.nm_en)); // sorting
        } 
        else
          console.error('Invalid response format:', response);
      },
      (error) => {
        console.error('There was an error retrieving the status:', error);
      }
    );
  }

  loadBFreq() {
      this.ParamService.getStates(environment.DefaultPage.toString(), environment.ItemPerPage.toString(), '', 'Billing-FreqType').subscribe((response: any) => {
        if (response.data.length >= 0) {
          for(var data of response.data){
            delete data.total;
            if(data.param_cd == 'M')
              data.value = 1;
            else if(data.param_cd == 'Q')
              data.value = 3;
            this.BFreqT.push(data);
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

  //if true will disable submit button
  submitCheck(){
    if(this.checkingLOA)
      return true;
    if((this.billing_method == 'L' && this.loa_exists) || (this.billing_method == 'A' && !this.loa_registered))
      return true;
    if(!this.bt_code.length || !this.entity_type.length || !this.entity_no.length || !this.cust_id.length
      || !this.cus_name.length || !this.cus_email.length || !this.cus_phno.length || !this.cus_add1.length
      || !this.cus_add2.length || !this.cus_add3.length || !this.postcode.length || !this.city.length
      || this.state == null || !this.entity_name.length || !this.sscode.length || !this.billing_no.length
      || !this.requester_name.length || !this.requester_email.length || this.billing_items_total_cost == 0
      || !this.billing_desc.length)
      return true;

    var emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if(!emailRegex.test(this.cus_email) || !emailRegex.test(this.requester_email))
      return true;

    if(this.billing_method != 'O'){
      if(!this.loa_ref_no.length  || this.isuance_count == 0 
        || this.selectedLoaFile == null || this.selectedLoaFileSize == 0)
        return true;
      if(this.billing_method == 'A' && !this.agmt_ref_no.length)
          return true;
    }
    
    return false;
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

  async submitForm(){
    this.freezeScreen();

    if(this.cfm_loa_ref_no != this.loa_ref_no){
      this.has_checked_loa_registered = false;
      this.has_checked_loa_exists = false;
    }

    if(this.billing_method == 'A' && !this.has_checked_loa_registered)
      await this.checkLOARegistered();
    else if(this.billing_method == 'L' && !this.has_checked_loa_exists)
      await this.checkLOAExists();

    this.submitFullForm();
  }

  async submitFullForm(){ 
    if(this.billing_method == 'A' && !this.loa_registered){
      this.unfreezeScreen();
      return;
    }
    if(this.billing_method == 'L' && this.loa_exists){
      this.unfreezeScreen();
      return;
    }

    const url = environment.apiUrl + '/api/billing/v1/newbill';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json'
    });

    // Create the request body with your form data
    var requestBody: {[k: string]: any} = {
        i_bt_code: this.bt_code.split(' - ')[0],
        i_entity_type: this.entity_type,
        i_entity_no: this.entity_no,
        i_cust_id: this.cust_id,
        i_cus_name: this.cus_name,
        i_cus_email: this.cus_email,
        i_cus_phno: this.cus_phno,
        i_cus_add1: this.cus_add1,
        i_cus_add2: this.cus_add2,
        i_cus_add3: this.cus_add3,
        i_postcode: this.postcode,
        i_city: this.city,
        i_state: this.state,
        i_entity_name: this.entity_name,
        i_sscode: this.sscode.split('(')[1].replace(')',''),
        i_billing_no: this.billing_no,
        i_billing_method: this.billing_method,
        i_billing_desc: this.billing_desc,
        i_requester_name: this.requester_name,
        i_requester_email: this.requester_email,
        i_billing_items_total_cost: this.billing_items_total_cost,
        i_remarks: this.remarks,
        i_billing_cnt: this.billing_method == 'O' ? 1 : this.isuance_count,
        i_billing_freq: this.billing_method == 'O' ? 'D' :
                        this.billing_frequency == 1 ? 'M' : 'Q'
    };
    /*
    var bItemData: any[] = [];
    for(const item of this.billing_items)
      if(item.qty != 0)
        bItemData.push(item);
    requestBody['i_billing_items'] = bItemData;
    */
    requestBody['i_billing_items'] = this.billing_items;

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

    if(this.billing_method != 'O'){
      requestBody['i_loa_ref_no'] = this.loa_ref_no;
      requestBody['i_date_range'] = this.date_range;

      var bIssL: any[] = [];
      for(const item of this.billing_issuance_list){
        if(item.issuance){
          delete item.valid;
          delete item.issuance;
          bIssL.push(item);
        }
      }

      requestBody['i_billing_issuance_list'] = bIssL;

      const base64Content = await new Promise<string>((resolve, reject) => {
          const reader = new FileReader();
          reader.onload = (e: any) => resolve(e.target.result);
          reader.onerror = reject;
          reader.readAsDataURL(this.selectedLoaFile);
        });
    
        const fileBody = {
          i_file_nm: this.selectedLoaFile!.name,
          i_file_content: base64Content,
          i_file_type: this.selectedLoaFile!.type,
          i_file_size: this.selectedLoaFile!.size,
          i_file_category: this.billing_method == 'L' ? 'L' : 'A'
        };

      requestBody['i_loa_document'] = fileBody;

      if(this.billing_method == 'A')  
        requestBody['i_agmt_ref_no'] = this.agmt_ref_no;
    }

    console.log(requestBody);
    this.freezeScreen();
    this.http.post(url, requestBody, { headers }).subscribe((response: any) => {
      if (response.data && response.data.length > 0) {
        console.log('success insert new bill!');
        console.log(response.data);

        this.unfreezeScreen();
        alert('Billing Registration Successful!');
        location.href = 'billing-listing';
      }
    },
      (error: any) => {
        console.log('fail insert new bill!');
        console.error(error);
        this.updateIssuanceList();
        this.unfreezeScreen();
    });
  }

  async checkLOAExists(){
    if(this.loa_ref_no == null || this.loa_ref_no == '')
      return;
    const url = environment.apiUrl + '/api/billing/v1/getexistsloa';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json'
    });

    // Create the request body with your form data
    var requestBody: {[k: string]: any} = {
      i_loa_ref_no: this.loa_ref_no
    };

    this.checkingLOA = true;
    console.log(requestBody);
    this.http.post(url, requestBody, { headers }).subscribe((response: any) => {
      if (response.data && response.data.length > 0) {
        if(parseFloat(response.data) == 0){
          this.loa_exists = false;
          this.loaAlertString = '';
          this.showLoaAlert = false;
          this.cfm_loa_ref_no = JSON.parse(JSON.stringify(this.loa_ref_no));
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
    if(this.loa_ref_no == null || this.loa_ref_no == '')
      return;
    const url = environment.apiUrl + '/api/billing/v1/getregisteredloa';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json'
    });

    // Create the request body with your form data
    var requestBody: {[k: string]: any} = {
      i_loa_ref_no: this.loa_ref_no
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
          this.cfm_loa_ref_no = JSON.parse(JSON.stringify(this.loa_ref_no));
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
}
