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
import { ParamData } from 'src/app/core/models/param.interface';
import saveAs from 'file-saver';
import { perm } from 'src/permissions/perm';
import { FormBuilder, FormControl, FormGroup, NgForm, NgModel, Validators } from '@angular/forms';
import moment from 'moment';
import { PostCodeData } from 'src/app/core/models/postcode.interface';

@Component({
  selector: 'app-billing-registration',
  templateUrl: './billing-registration.component.html',
  styleUrls: ['./billing-registration.component.scss']
})
export class BillRegistrationComponent {
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

  bt_code: string | null = null;
  btc_list: string[] = [];

  entity_type: string | null = null;
  entity_types: ParamData[] = [];

  entity_no_tmp: string = '';
  entity_no: string = '';
  cust_exist_btn_first_click: boolean = false;
  cust_id: string = '';
  cus_name: string = '';
  cus_email: string = '';
  cus_phno: string = '';
  cus_add1: string = '';
  cus_add2: string = '';
  cus_add3: string = '';
  postcode: string | null = null;
  city: string | null = null;
  state: string | null = null;
  states: ParamData[] = [];
  entity_name: string = '';

  sscode: string = '-';
  billing_no: string = '';
  billing_methods: ParamData[] = [];
  billing_method: string = 'O';
  billing_desc: string = '';
  billingStatusOptions: ParamData[] = [];

  requester_name: string = '';
  requester_email: string = '';
  
  showInsertAlert: boolean = false;

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
  today: Date = new Date();
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
  custIdUnapproved: boolean = false;

  isLoadingPerms: boolean = true;
  permCheck = perm.Billing_Registration;
  permCheckReturnString = ""; // variable to store allowed permission for the user
  permAllow: number = 0;

  isDisplayFileRequired: boolean = false;
  isBITotalTouched: boolean = false;
  errorFile: boolean = false;
  errorFileMessages: string[] = [];
  errorFileSizeLimit: boolean = false;
  errorFileSizeLimitMessages: string[] = [];
  errorFileDuplicate: boolean = false;
  errorFileDuplicateMessages: string[] = [];
  error: boolean = false;
  errorMessages: string[] = [];

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
    this.loadPermission();
    this.loadBFreq();
    this.loadBillingMethods();
    this.loadETypes();
    this.loadStates();
    this.loadBSParam();
    this.fetchBillingRegListingData();
    this.loadRunningBillingNo();
    this.loadPostcode();
    
    this.authService.getName().subscribe(response =>{
      this.requester_name = response;
    });

    this.authService.getEmail().subscribe(response =>{
      this.requester_email = response;
    });

    this.waitForElm('.breadcrumb').then((elm) => {
      (document.getElementsByClassName('breadcrumb')[0] as HTMLElement).style.marginLeft = '2%';
    });
  }

  searchCustomerId(entity_no_tmp: string, entity_noRef: any, billingTypeCodeIdRef: any, entity_typeRef: any){
    var headerValue = '';
    if(this.bt_code == null || this.bt_code == ''){
      if (!entity_no_tmp || entity_no_tmp.trim() === '' || entity_no_tmp.length == 0) {
        entity_noRef.control.setErrors({required: true});
        entity_noRef.control.markAsTouched(); // Mark the field as touched to show validation error
      }
      if(this.entity_type == null || this.entity_type === ''){
        entity_typeRef.control.setErrors({required: true});
        entity_typeRef.control.markAsTouched();
      }
      billingTypeCodeIdRef.control.setErrors({required: true});
      billingTypeCodeIdRef.control.markAsTouched();
      return;
    }
    headerValue = this.btcMapData.get(this.bt_code as string)[1][0][5];
    if(headerValue == null || headerValue === '' || headerValue == '-' || headerValue.length == 0){
      if (!entity_no_tmp || entity_no_tmp.trim() === '' || entity_no_tmp.length == 0) {
        entity_noRef.control.setErrors({required: true});
        entity_noRef.control.markAsTouched(); // Mark the field as touched to show validation error
      }
      if(this.entity_type == null || this.entity_type === ''){
        entity_typeRef.control.setErrors({required: true});
        entity_typeRef.control.markAsTouched();
      }
      billingTypeCodeIdRef.control.setErrors({required: true});
      billingTypeCodeIdRef.control.markAsTouched();
      return;
    }

    if(this.entity_type == null || this.entity_type === ''){
      if (!entity_no_tmp || entity_no_tmp.trim() === '' || entity_no_tmp.length == 0) {
        entity_noRef.control.setErrors({required: true});
        entity_noRef.control.markAsTouched(); // Mark the field as touched to show validation error
      }
      entity_typeRef.control.setErrors({required: true});
      entity_typeRef.control.markAsTouched();
      return;
    }
   
    // Validate entity_no
    if (!entity_no_tmp || entity_no_tmp.trim() === '' || entity_no_tmp.length == 0) {
      entity_noRef.control.setErrors({required: true});
      entity_noRef.control.markAsTouched(); // Mark the field as touched to show validation error
      return; // Stop execution if input is empty
    }

    // ✅ Length check (Must be > 5 characters)
    if (entity_no_tmp.length < 5) {
      entity_noRef.control.setErrors({ minlength: true }); // Manually trigger minlength error
      entity_noRef.control.markAsTouched();
      return; // Stop execution if length is invalid
    }

        // ✅ Length check (Must be < 31 characters)
    if (entity_no_tmp.length > 30) {
      entity_noRef.control.setErrors({ maxlength: true }); // Manually trigger minlength error
      entity_noRef.control.markAsTouched();
      return; // Stop execution if length is invalid
    }

    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      'X-IBM-Client-Id': environment.xibmclientid
    });
    const url = environment.fmsCheckCustIdApi;

    var header: any = {
        value: headerValue
    };
    var nbrDataType = 'CustomStringField';
    var nbrData: any = {
      type: nbrDataType,
      value: entity_no_tmp
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
    //console.log(body);
    this.isSearchingCustomerId = true;
    this.http.post(url, body, { headers }).subscribe(
      (response: any) => {
        if(response){
          console.log(response);
          if(response.Status != '200' || response.CustomerID == null){
            this.failFindCustId = true;
            this.custIdUnapproved = true;
          }
          else{
            this.failFindCustId = false;
            this.cust_id = response.CustomerID.trimEnd();
            this.entity_no = entity_no_tmp;
            if(response.CustomerName != null)
              this.cus_name = response.CustomerName.trimEnd();
            if(response.Email != null)
              this.cus_email = response.Email.trimEnd();
            if(response.AddressLine1 != null)
              this.cus_add1 = response.AddressLine1.toUpperCase().trimEnd();
            if(response.AddressLine2 != null)
              this.cus_add2 = response.AddressLine2.toUpperCase().trimEnd();
            if(response.AddressLine3 != null)
              this.cus_add3 = response.AddressLine3.toUpperCase().trimEnd();
            if(response.Phone != null)
              this.cus_phno = response.Phone.trimEnd();
            // if(response.PostalCode != null){
            //   this.postcode = response.PostalCode.trimEnd();
            //   this.onPostcodeChange(this.postcode);
            // }
            // if(response.City != null && this.uniqueCities.includes(response.City.trimEnd()))
            //   this.city = response.City.trimEnd();
            // if(response.StateName != null && this.uniqueStates.includes(response.StateName.trimEnd()))
            //   this.state = response.StateName.trimEnd();

            if (response.PostalCode != null) {
              this.postcode = response.PostalCode.trimEnd();
              // this.onPostcodeChange(this.postcode);
            }
            if (response.City != null) {
              this.city = this.uniqueCities.some(c => c.toUpperCase() === response.City.toUpperCase())
                ? response.City.toUpperCase()
                : null;
            }
            if (response.StateName != null) {
              this.state = this.uniqueStates.some(s => s.toUpperCase() === response.StateName.toUpperCase())
                ? response.StateName.toUpperCase()
                : null;
            }

            if(response.ContactName != null) //UsrIdentityNbr != null)
              this.entity_name = response.ContactName.trimEnd();
          }
          this.isSearchingCustomerId = false;
          if(this.cust_id == null || this.cust_id == '')
            this.failFindCustId = true;
        }
		    if (!this.cust_exist_btn_first_click)
		    	this.cust_exist_btn_first_click = true;
      },
      (error) => {
        console.error(error);
        this.isSearchingCustomerId = false;
        this.cust_exist_btn_first_click = true;
        this.failFindCustId = true;
        this.custIdUnapproved = false;
        this.cust_id = '';
      }
    );
  }

  updateBTC(bt_code: string | null){
    if(bt_code == null)
      return;
    this.billing_items_total_cost = 0;
    this.sscode = this.btcMapData.get(bt_code as string)[0];
    this.billing_items = [];
    //console.log(this.btcMapData);
    if(this.btcMapData.get(bt_code as string)[1].length > 0){
      for(var i = 0; i < this.btcMapData.get(bt_code as string)[1].length; i++){
        this.billing_items.push({              
          desc:this.translate.store.currentLang === 'en' ? 
            this.btcMapData.get(bt_code as string)[1][i][0] : this.btcMapData.get(bt_code as string)[1][i][3],
          price:parseFloat(this.btcMapData.get(bt_code as string)[1][i][1]),
          qty:0,
          tax:parseFloat(this.btcMapData.get(bt_code as string)[1][i][2]),
          taxc:0,
          total:0,
          mftPk:parseFloat(this.btcMapData.get(bt_code as string)[1][i][4])
        });
        //this.recalcBICost(this.billing_items[this.billing_items.length-1]);
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
        //this.recalcBICost(this.billing_items[this.billing_items.length-1]);
    }
    //console.log(this.billing_items);
  }

  recalcBICost(item: any){
    this.isBITotalTouched = true;
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
        //console.log(response.data)
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

  getEntity(entity_type: string | null){
    if(entity_type == null)
      return '-';
    for(const param of this.entity_types)
      if(param.param_cd == entity_type)
        return param.nm_en;
    return '-';
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

  onLoaFileSelected(event: any): void {
    const file: File = event.target.files[0];

    const allowedExtensions = ['png', 'jpeg', 'jpg', 'pdf', 'doc', 'docx'];
    this.errorFile = false;
    this.errorFileMessages = [];
    this.errorFileSizeLimit = false;
    this.errorFileSizeLimitMessages = [];
    this.errorFileDuplicate = false;
    this.errorFileDuplicateMessages = [];
  
    if (file) {
      if (file.size > 10 * 1024 * 1024) {
        //alert('File size exceeds 10MB. Please select a smaller file.');
        this.errorFileSizeLimit = true;
        this.errorFileSizeLimitMessages.push(this.translate.instant('labels.filesize10MBlimitmessage'));
        return;
      }
  
      this.selectedLoaFile = file;
      this.selectedLoaFileSize = file.size;
    }
  }

  clearLoaFile(): void {
    this.selectedLoaFile = null;
    this.selectedLoaFileSize = 0;
    this.errorFile = false;
    this.errorFileMessages = [];
    this.errorFileSizeLimit = false;
    this.errorFileSizeLimitMessages = [];
    this.errorFileDuplicate = false;
    this.errorFileDuplicateMessages = [];
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
        this.error = true;
        this.errorMessages.push(this.translate.instant('retrieverunningbillnoerr'));
        console.error('There was an error retrieving the billing running no:', error);
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
    if(this.bt_code == null || this.entity_type == null || !this.entity_no.length || !this.cust_id.length
      || !this.cus_name.length || !this.cus_email.length || !this.cus_phno.length || !this.cus_add1.length
      || !this.cus_add2.length || this.postcode == null || this.city == null
      || this.state == null || !this.entity_name.length || this.sscode == '-' || !this.billing_no.length
      || !this.requester_name.length || !this.requester_email.length || this.billing_items_total_cost == 0
      || !this.billing_desc.length) //|| !this.cus_add3.length
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
    var tmp = <HTMLElement>document.getElementsByTagName('app-root')[0];
    tmp.style.pointerEvents = 'all';
    document.getElementById("mainBodyContainer")!.style.pointerEvents = 'all';
    document.getElementById("f_overlay")!.style.display = 'none';
  }

  async handleFormSubmit(form: NgForm) {

    this.isBITotalTouched = true; //true because if user didn't change the value and press submit, it will not trigger the error
    let inValidSubmit = this.submitCheck(); //return false mean can submit, true mean cannot 
    let formValidation: boolean | null = false;

    if (this.billing_method != 'O') {
      if (this.selectedLoaFileSize === 0) 
        this.isDisplayFileRequired = true;
      else 
        this.isDisplayFileRequired = false;
      
    }

    if (this.billing_method == 'O') {
      form.controls['loa_ref_no_l'].clearValidators();
      form.controls['loa_ref_no_a'].clearValidators();
      form.controls['agmt_ref_no_a'].clearValidators();
    }
    else if(this.billing_method == 'L'){
      form.controls['loa_ref_no_l'].setValidators([Validators.required]);
      form.controls['loa_ref_no_a'].clearValidators();
      form.controls['agmt_ref_no_a'].clearValidators();
    }
    else if(this.billing_method == 'A'){
      form.controls['loa_ref_no_l'].clearValidators();
      form.controls['loa_ref_no_a'].setValidators([Validators.required]);
      form.controls['agmt_ref_no_a'].setValidators([Validators.required]);
    }
    form.controls['loa_ref_no_l'].updateValueAndValidity();
    form.controls['loa_ref_no_a'].updateValueAndValidity();
    form.controls['agmt_ref_no_a'].updateValueAndValidity();

    formValidation = form.valid && !inValidSubmit;
    console.log('Form validation:', formValidation);
    if (formValidation) {
      console.log('Form is valid');
      this.submitForm(); //temporary comment
    } 
    else {
      Object.keys(form.controls).forEach((field) => {
        const control = form.controls[field]; // Access the control directly

        // Mark the control as touched to trigger validation messages
        control?.markAsTouched();

        // Log details about the invalid field
        if (control?.invalid) {
          console.log(`Field "${field}" is invalid.`);
          console.log('Errors:', control.errors);
        }
      });
    }

  }

  cancelForm(){
    location.href = 'billing-listing';
  }

  async submitForm(){
    //this.freezeScreen();

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

    this.updateShortformState();
    const url = environment.apiUrl + '/api/billing/v1/newbill';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json'
    });

    // Create the request body with your form data
    var requestBody: {[k: string]: any} = {
        i_bt_code: (this.bt_code as string).split(' - ')[0],
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
        i_state: this.shortformstate != null ? this.shortformstate : this.state,
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

    //this.freezeScreen();
    var tmp = <HTMLElement>document.getElementsByTagName('app-root')[0];
    tmp.style.pointerEvents = 'none';
    document.getElementById("mainBodyContainer")!.style.pointerEvents = 'none';
    document.getElementById("f_overlay")!.style.display = 'block';

    this.http.post(url, requestBody, { headers }).subscribe((response: any) => {
      if (response.data && response.data.length > 0) {
        var debugFlag = localStorage.getItem('debug') == 'true' ? true : false;
        if(debugFlag){
          console.log('body:');
          console.log(requestBody);
          console.log(response.data);
          alert('Billing Registration Successful!');
          this.unfreezeScreen();
        }
        else{
          this.showInsertAlert = true;
          tmp.style.pointerEvents = 'all';
          document.getElementById("mainBodyContainer")!.style.pointerEvents = 'none';
          document.getElementById("f_overlay")!.style.display = 'none';
          setTimeout(() => {
            this.showInsertAlert = false;
            this.router.navigate(['/billing-listing']);
            location.href = 'billing-listing';
            return;
          }, 5000);
        }
      }
    },
      (error: any) => {
        this.error = true;
        this.errorMessages.push(this.translate.instant('internalServerErr'));
        console.log('fail insert new bill!');
        console.error(error);
        this.updateIssuanceList();
        this.unfreezeScreen();
    });
  }

  loadPermission() {
    this.freezeScreen();
    this.authService.checkUserRole(this.authService.username, this.permCheck)
      .subscribe((response: any) => {
          this.permCheckReturnString = response.data;
          this.permAllow = this.permCheckReturnString.includes(perm.Billing_Registration) ? 1 : 0;
          this.isLoadingPerms = false;
          if(this.permAllow === 0){
            if(environment.production)
              this.router.navigate(['/access-denied']);
            console.log(response.data);
            alert('bad permission: ' + this.permCheckReturnString);  
          }
          this.unfreezeScreen();
        },
        (error: any) => {
          if(environment.production)
            this.router.navigate(['/access-denied']);
          console.log(error);
          alert('permission load failed');
          this.isLoadingPerms = false;
          this.unfreezeScreen();
        }
      );
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
    //console.log(requestBody);
    this.http.post(url, requestBody, { headers }).subscribe((response: any) => {
      if (response.data && response.data.length > 0) {
        //console.log('grab loa exists:' + response.data);
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
    //console.log(requestBody);
    this.http.post(url, requestBody, { headers }).subscribe((response: any) => {
      if (response.data && response.data.length > 0) {
        //console.log('grab loa registered: ' + response.data);
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
    const match = this.states.find(s => s.nm_en === this.state!.toUpperCase());
    this.shortformstate = match ? match.param_cd : null;
  }

}
