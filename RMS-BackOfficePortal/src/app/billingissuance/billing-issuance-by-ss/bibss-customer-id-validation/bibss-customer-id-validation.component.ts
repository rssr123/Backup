import { Component, OnInit, ViewChild, ElementRef, ChangeDetectorRef, ViewEncapsulation, AfterViewInit } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { environment } from 'src/environments/environment';
import { FormBuilder, FormControl, FormGroup, NgForm, NgModel, Validators } from '@angular/forms';
import { Systemstatus } from '../../../shared/enums/systemstatus';
import { ActivatedRoute, Router } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { GlobalService } from 'src/app/shared/global.service';
import { AuthService } from 'src/app/core/services/auth.service';
import { perm } from 'src/permissions/perm';
import { NgbPaginationConfig } from '@ng-bootstrap/ng-bootstrap';
import { MatDialog } from '@angular/material/dialog';
import { ParamService } from '../../../core/services/param.service';
import { OTCReceiptCancellationBalStatusDetails, OTCReceiptCancellationHistoryDetails, OTCReceiptCancellationOrderInfoDetails, OTCReceiptCancellationPaymentInfoDetails, OTCReceiptCancellationPaymentItemsDetails, OTCReceiptCancellationRecepitInfoDetails } from 'src/app/core/models/otc-receipt-cancellation.interface';
import { BillingIssuanceBySSBillingChildDetails, BillingIssuanceBySSBillingIssuance, BillingIssuanceBySSBillingItemDetails, BillingIssuanceBySSHistory, BillingTypeCode, ornDetails, paymentItemDetails } from 'src/app/core/models/biiling-issuance-by-ss.interface';
import moment from 'moment';
import { PostCodeData } from 'src/app/core/models/postcode.interface';

// import { BillingItem } from 'src/app/core/models/BillingItem.interface';
// import { HistModel } from 'src/app/core/models/HistModel.interface';
// import { ParamData } from 'src/app/core/models/param.interface';
import saveAs from 'file-saver';
import { file } from 'ngx-bootstrap-icons';
import { ParamData } from 'src/app/core/models/param.interface';
import { NgSelectComponent } from '@ng-select/ng-select';

@Component({
  selector: 'app-bibss-customer-id-validation',
  templateUrl: './bibss-customer-id-validation.component.html',
  styleUrls: ['./bibss-customer-id-validation.component.scss']
})
export class BibssCustomerIdValidationComponent implements OnInit {

  //note-source code system and billing item is from same sp with billing type code
  //when insert billing cust table, rms bil will also inserted via the bilcust sp

  @ViewChild('cityRef') cityRef!: NgModel;
  @ViewChild('stateRef') stateRef!: NgModel;
  @ViewChild('postcodeRef') postcodeRef!: NgModel;

  page = environment.DefaultPage;
  itemsPerPage = environment.ItemPerPage;
  dropDownSize = environment.DropDownSize;
  customerName: string | null = null;
  // bt_code: string = '';
  billingTypeCodeId: number | null = null;
  btc_list: string[] = [];

  entity_type: string = '';
  // entity_types: ParamData[] = []; //later
  entity_types: any[] = [];

  entity_no_tmp: string = '';
  entity_no: string = '';
  cust_exist_btn_first_click: boolean = false;
  cust_id: string = '';
  cus_name: string = '';
  cus_email: string = '';
  cus_phno: string = '';
  cus_add1: string | null = null;
  cus_add2: string | null = null;
  cus_add3: string | null = null;
  postcode: string | null = null;
  city: string | null = null;
  state: string | null = null;
  shortformstate: string | null = null;
  // states: ParamData[] = [];
  states: any[] = [];
  entity_name: string = '';

  sscode: string = '';
  ssname: string = '';
  billing_no: string = '';
  billing_methods: ParamData[] = [];
  billing_method: string = 'O';
  billing_desc: string = '';

  requester_name: string = '';
  requester_email: string = '';


  uniqueBillingTypeOptions: any[] = []; // Unique options for ng-select
  billingTypeCodeOptions: BillingTypeCode[] = [];
  billingItemsDetailsTemp: any[] = []; //different from billingItemsDetails because got extra desc column
  billingItemsDetails: BillingIssuanceBySSBillingItemDetails[] = [];
  billingChildDetails: BillingIssuanceBySSBillingChildDetails[] = [];
  bllingChildDetailsTemp: any[] = [];
  billingHistory: BillingIssuanceBySSHistory[] = [];
  postCodes: PostCodeData[] = [];

  billing_items_total_cost: number = 0;
  paymentItemDetails: paymentItemDetails[] = [];
  ornDetails: ornDetails | null = null;
  sscdFromCallback: string | null = null;
  callbackurl: string = '';
  // histModel: HistModel[] = [
  //   {
  //     action:'-',
  //     dt_action: null,
  //     performed_by: '-',
  //     assigned_to: '-',
  //     remark:'-'
  //   }];

  maxFileSize = 10 * 1024 * 1024; // 10MB


  //files
  selectedSupportingFiles: File[] = [];
  selectedSupportingFilesSize: number = 0;
  isDisplayFileRequired: boolean = false;
  bilIdFromInsert: number | null = null;
  i_file_content: any;

  selectedLoaFile: File | any = null;
  selectedLoaFileSize: number = 0;
  errorFile: boolean = false;
  errorFileMessages: string[] = [];
  errorFileSizeLimit: boolean = false;
  errorFileSizeLimitMessages: string[] = [];
  errorFileDuplicate: boolean = false;
  errorFileDuplicateMessages: string[] = [];

  remarks: string = '';
  showInsertAlert: boolean = false;

  btcMapData: Map<string, any> = new Map<string, any>();

  //date_range: Date[] = [new Date((new Date()).getFullYear() + '-01-01'), new Date()];
  //date_range: Date[] = [new Date(), new Date(new Date().getFullYear(), 11, 31)];
  date_range: Date[] = [new Date(), new Date(new Date().setMonth(new Date().getMonth() + 3))];
  isuance_counter: number = this.monthDiff(this.date_range[0], this.date_range[1]);
  isuance_count: number = 0;
  maxDate = new Date(new Date().setMonth(new Date().getMonth() + 3));

  BFreqT: any[] = [];
  billing_frequency: number = 1;
  billing_day: number = 1;

  //billing_issuance_list: any = [];
  loa_ref_no: string = '';
  agmt_ref_no: string = '';
  runnoThatReserved: string = '';
  orderSummary: string = '';
  isLoadingHistory: boolean = false;
  isSearchingCustomerId: boolean = false;
  failFindCustId: boolean = false;
  custIdUnapproved: boolean = false;

  // isLoading: boolean = false;
  totalRecords: number = 0;
  totalHistoryRecords: number = 0;
  totalPostCodeRecords: number = 0;
  error: boolean = false;
  errorMessages: string[] = [];
  uniqueCities: string[] = [];
  uniqueStates: string[] = [];


  cfm_loa_ref_no: string = '';
  loa_exists: boolean = true;
  loa_registered: boolean = false;
  has_checked_loa_exists: boolean = false;
  has_checked_loa_registered: boolean = false;
  showLoaAlert: boolean = false;
  showLoaRegisteredAlert: boolean = false;
  loaAlertString: string = '';
  checkingLOA: boolean = false;
  isBITotalTouched: boolean = false;
  checkLOAExistsRequired: boolean = false;
  isValidLoaAgreement: boolean = false;
  isLOAAgreementRequired: boolean = false;
  validLOABeforeSubmitMethodLoa: boolean = false;
  validLOABeforeSubmitMethodAgr: boolean = false;
  combineSSCode: string = '';

  // Configuring Permissions for User and roles variables
  permBilReg = perm.Billing_Issuance_By_Source_System_Billing_Registration
  permBilRegAllow = ""; // variable to store allowed permission for the user
  permListAllow: number = 0; // if 0 then not allow to view listing page, else allow

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

    const queryParams = this.route.snapshot.queryParamMap;

    const tempsscdFromCallback = queryParams.get('ss_cd');
    if (tempsscdFromCallback !== null && tempsscdFromCallback !== 'null') {
      this.sscdFromCallback = tempsscdFromCallback; //original
    }
     
    const tempcallbackurl = queryParams.get('callbackurl');
    if (tempcallbackurl !== null && tempcallbackurl !== 'null') {
      this.callbackurl = tempcallbackurl;
    }
    console.log('sscdFromCallback: ' + this.sscdFromCallback);
    console.log('callbackurl: ' + this.callbackurl);

    this.loadPermissions();

    if (this.sscdFromCallback !== undefined && this.sscdFromCallback !== null) {
      this.loadBillingTypeCode();
      this.loadETypes();
      this.loadStates();
      this.loadBillingMethods();
      this.loadBFreq();
      // this.fetchBillingRegListingData();
      this.loadBillingRunningNo();
      this.loadPostcode();
      // this.loadHistory(); when register there are no any history yet
      this.authService.getName().subscribe(response => {
        this.requester_name = response;
      });

      this.authService.getEmail().subscribe(response => {
        this.requester_email = response;
      });
    }
  }

  searchCustomerId(entity_no_tmp: string, billingTypeCodeIdRef: any,
    entity_typeRef: any,
    entity_noRef: any) {
    // Validate billingTypeCodeId
    if (billingTypeCodeIdRef.invalid) {
      billingTypeCodeIdRef.control.markAsTouched(); // Mark the field as touched to show validation error
    }

    // Validate entity_type
    if (entity_typeRef.invalid) {
      entity_typeRef.control.markAsTouched(); // Mark the field as touched to show validation error
    }

    // Validate entity_no
    if (!entity_no_tmp || entity_no_tmp.trim() === '') {
      entity_noRef.control.markAsTouched(); // Mark the field as touched to show validation error
      return; // Stop execution if input is empty
    }

    // ✅ Length check (Must be >= 12 characters)
    if (entity_no_tmp.length < 5 || entity_no_tmp.length > 30) {
      // entity_noRef.control.setErrors({ minlength: true }); // Manually trigger minlength error
      // entity_noRef.control.markAsTouched();
      return; // Stop execution if length is invalid
    }

    // Stop if any field is invalid
    if (billingTypeCodeIdRef.invalid || entity_typeRef.invalid || !entity_no_tmp || entity_no_tmp.trim() === '' || entity_no_tmp.length < 5 || entity_no_tmp.length > 30) {
      return;
    }

    var headerValue = '';
    for (var i = 0; i < this.uniqueBillingTypeOptions.length; i++) {
      if(this.uniqueBillingTypeOptions[i].bltc_id == this.billingTypeCodeId){
        headerValue = this.uniqueBillingTypeOptions[i].class_id;
        break;
      }
    }

    if(headerValue == null || headerValue === '' || headerValue == '-' || headerValue.length == 0){
      return;
    }
   
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      'X-IBM-Client-Id': environment.xibmclientid
    });

    //  const headers = new HttpHeaders({
    //   Authorization: environment.authKey,
    //   'Content-Type': 'application/json'
    // });


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
    console.log(body);
    this.isSearchingCustomerId = true;
    this.http.post(url, body, { headers }).subscribe(
      (response: any) => {
        if (response) {
          if (response.Status != '200' || response.CustomerID == null) {
            this.failFindCustId = true;
            this.custIdUnapproved = true;
          }
          else {
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

  // updateBTC(bt_code: string) {
  //   this.billing_items_total_cost = 0;
  //   // this.sscode = this.btcMapData.get(bt_code)[0];
  //   this.billing_items = [];
  //   if (this.btcMapData.get(bt_code)[1].length > 0) {
  //     for (var i = 0; i < this.btcMapData.get(bt_code)[1].length; i++) {
  //       this.billing_items.push({
  //         desc: this.translate.store.currentLang === 'en' ?
  //           this.btcMapData.get(bt_code)[1][i][0] : this.btcMapData.get(bt_code)[1][i][4],
  //         price: parseFloat(this.btcMapData.get(bt_code)[1][i][1]),
  //         qty: 0,
  //         tax: parseFloat(this.btcMapData.get(bt_code)[1][i][2]),
  //         taxc: 0,
  //         total: 0
  //       });
  //       this.recalcBICost(this.billing_items[this.billing_items.length - 1]);
  //     }
  //   }
  //   else {
  //     this.billing_items.push({
  //       desc: '-',
  //       price: 0,
  //       qty: 0,
  //       tax: 0,
  //       taxc: 0,
  //       total: 0
  //     });
  //     this.recalcBICost(this.billing_items[this.billing_items.length - 1]);
  //   }
  // }

  //recalcBICost(item: BillingItem){ laetr
  recalcBICost(item: any) {
    this.isBITotalTouched = true;

    item.tax_amt = parseFloat((((item.unit_fee * item.qty) * ((item.tax_pct + 100) / 100)) - (item.unit_fee * item.qty)).toFixed(2));
    this.billing_items_total_cost -= item.final_amt; //remove old total
    item.final_amt = (item.unit_fee * item.qty) + item.tax_amt;
    this.billing_items_total_cost += item.final_amt;
  }

  // updateIssuanceList() {
  //   this.bllingChildDetailsTemp = [];
  //   this.isuance_count = 0;
  //   this.isuance_counter = this.monthDiff(this.date_range[0], this.date_range[1]);
  //   if (this.isuance_counter == 0)
  //     this.isuance_counter = 1;

  //   const dateTracker = new Date(this.date_range[0]);
  //   if (this.billing_day == 0){
  //     dateTracker.setMonth(dateTracker.getMonth() + 1);
  //   }
  //   dateTracker.setDate(this.billing_day);
  //   var running_billing_no = parseFloat(this.billing_no.split('BIL')[1]);

  //   console.log('dateTracker: ' + dateTracker);
  //   console.log('new Date(dateTracker) ia : ' + new Date(dateTracker));

  //   for (var i = 0; i < this.isuance_counter; i++) {
  //     if (i % this.billing_frequency == 0) { //if billing frequency is 1, then every month, if 2, then every 2 months
  //       this.bllingChildDetailsTemp.push({
  //         ['billing_date']: new Date(dateTracker),
  //         ['billing_no']: (dateTracker.getTime() > new Date().getTime()) ? 'BIL' + running_billing_no : 'N/A',
  //         ['valid']: dateTracker.getTime() > new Date().getTime(),
  //         ['issuance']: dateTracker.getTime() > new Date().getTime()
  //       });

  //       if (dateTracker.getTime() > new Date().getTime()) {
  //         this.isuance_count += 1;
  //         running_billing_no += 1;
  //       }
  //     }
  //     dateTracker.setDate(1);
  //     dateTracker.setMonth(dateTracker.getMonth() + (this.billing_day == 0 ? 2 : 1));
  //     dateTracker.setDate(this.billing_day);
  //   }
  // }

  updateIssuanceList() {
    this.bllingChildDetailsTemp = [];
    this.isuance_count = 0;
    this.loa_ref_no = '';


    // Calculate the total number of months in the range
    this.isuance_counter = this.monthDiff(this.date_range[0], this.date_range[1]);
    if (this.isuance_counter === 0) {
      this.isuance_counter = 1;
    }

    const dateTracker = new Date(this.date_range[0]);
    const endDate = new Date(this.date_range[1]); // End date from the range
    endDate.setDate(endDate.getDate() + 1); // Add 1 day to ensure inclusion of the last month

    let running_billing_no = parseFloat(this.billing_no.split('BIL')[1]);

    while (dateTracker <= endDate) {
      // Only process if the iteration aligns with the billing frequency
      if ((dateTracker.getMonth() - new Date(this.date_range[0]).getMonth()) % this.billing_frequency === 0) {
        // Calculate the last valid day of the current month
        const maxDay = new Date(dateTracker.getFullYear(), dateTracker.getMonth() + 1, 0).getDate();

        // Set the day to the billing_day or last day of the month
        const adjustedDay = this.billing_day > 0 ? Math.min(this.billing_day, maxDay) : maxDay;
        dateTracker.setDate(adjustedDay);

        // Ensure the date does not exceed the original endDate
        if (dateTracker > endDate) {
          break;
        }

        // Push billing details to the array
        this.bllingChildDetailsTemp.push({
          ['billing_date']: new Date(dateTracker),
          ['billing_no']: dateTracker.getTime() > new Date().getTime() ? 'BIL' + running_billing_no : 'N/A',
          ['valid']: dateTracker.getTime() > new Date().getTime(),
          ['issuance']: dateTracker.getTime() > new Date().getTime(),
        });

        if (dateTracker.getTime() > new Date().getTime()) {
          this.isuance_count++;
          running_billing_no++;
        }
      }

      // Move to the first day of the next billing frequency cycle
      dateTracker.setDate(1);
      dateTracker.setMonth(dateTracker.getMonth() + this.billing_frequency);
    }
  }

  unissueBillingItem(item: any) {
    var running_billing_no = parseFloat(this.billing_no.split('BIL')[1]);
    this.isuance_count = 0;
    item.issuance = !item.issuance;
    for (var i = 0; i < this.isuance_counter; i++) {
      if (this.bllingChildDetailsTemp[i].issuance) {
        this.bllingChildDetailsTemp[i].billing_no = 'BIL' + running_billing_no;
        running_billing_no += 1;
        this.isuance_count += 1;
      }
      else
        this.bllingChildDetailsTemp[i].billing_no = 'N/A';
    }
  }

  // fetchBillingRegListingData() {
  //   const url = environment.apiUrl + '/api/billing/v1/bcdlistreg';

  //   // Set your authorization header
  //   const headers = new HttpHeaders({
  //     Authorization: environment.authKey,
  //     'Content-Type': 'application/x-www-form-urlencoded'
  //   });

  //   // Make the HTTP GET request
  //   this.http.get(url, { headers: headers }).subscribe(
  //     (response: any) => {
  //       for (var prop in response.data) {
  //         this.btc_list.push(prop);
  //         this.btcMapData.set(prop, response.data[prop]);
  //       }
  //       this.btc_list.sort((a, b) => a.localeCompare(b)); // sorting
  //     },
  //     (error: any) => {
  //       console.error(error);
  //     }
  //   );
  // }

  getEntity(entity_type: string) {
    for (const param of this.entity_types)
      if (param.param_cd == entity_type)
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
    return months <= 0 ? 0 : months + 1;
  }

  nthNumber(number: number): string {
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
    if (this.selectedSupportingFiles.length > 0) {//added condition because if not, when user didn't  
      this.selectedSupportingFiles = [];
      this.selectedSupportingFilesSize = 0;
    }

    this.errorFile = false;
    this.errorFileMessages = [];
    this.errorFileSizeLimit = false;
    this.errorFileSizeLimitMessages = [];
    this.errorFileDuplicate = false;
    this.errorFileDuplicateMessages = [];

    // Reset file input to allow re-selection of the same file
    const fileInput = document.getElementById('supportingFilesInput') as HTMLInputElement;
    if (fileInput) {
      fileInput.value = ''; // Clears the file input field
    }
  }

  //upload file start
  //multiple file start
  async onSupportingFileSelected(event: any) {

    if (event.target.files) {
      let files = event.target.files;
      let currnetFilesTotalSize = 0;
      const allowedExtensions = ['png', 'jpeg', 'jpg', 'pdf', 'doc', 'docx'];

      this.errorFile = false;
      this.errorFileMessages = [];
      this.errorFileSizeLimit = false;
      this.errorFileSizeLimitMessages = [];
      this.errorFileDuplicate = false;
      this.errorFileDuplicateMessages = [];

      for (let i = 0; i < files.length; i++) {
        // currnetFilesTotalSize += files[i].size;
        console.log('This file size is ' + files[i].size)
        console.log('recent selected file size is ' + this.selectedSupportingFilesSize)

        const fileName = files[i].name;
        const fileExtension = fileName.split('.').pop().toLowerCase(); // Extract file extension

        // **Check if the file extension is allowed**
        if (!allowedExtensions.includes(fileExtension)) {
          this.errorFile = true;
          this.errorFileMessages.push(fileName);
          continue; // Skip this file and check the next one
        }

        currnetFilesTotalSize += files[i].size;

        // Check if the file with the same name already exists in selectedFiles
        const isDuplicate = this.selectedSupportingFiles.some((file) => file.name === files[i].name);
        console.log('Sum of file size is ' + currnetFilesTotalSize + this.selectedSupportingFilesSize)

        if (!isDuplicate && (currnetFilesTotalSize + this.selectedSupportingFilesSize) <= 10 * 1024 * 1024) {
          this.selectedSupportingFiles.push(files[i]);
          this.selectedSupportingFilesSize += files[i].size;
        } else if (isDuplicate) {
          //alert(`File "${files[i].name}" already selected. Please choose a different file.`);
          this.errorFileDuplicate = true;
          this.errorFileDuplicateMessages.push(files[i].name);
        } else {
          //alert('Total file size exceeds 10MB. Please select smaller files.');
          this.errorFileSizeLimit = true;
          this.errorFileSizeLimitMessages.push('Total file size exceeds 10MB. Please select smaller files.');
        }
      }
    }
    console.log('total size' + this.selectedSupportingFilesSize);
  }

  async readMultipleFileAsync(): Promise<boolean> {
    let result: boolean = false;
    let fileCategory = 'S';

    for (const file of this.selectedSupportingFiles) {

      this.i_file_content = await new Promise((resolve, reject) => {
        const reader = new FileReader();

        reader.onload = (e: any) => {
          resolve(e.target.result);
        };

        reader.onerror = reject;

        reader.readAsDataURL(file);
      });

      result = await this.uploadFile(file, fileCategory);
    }
    return result;
  }
  //multiple file end


  async uploadFile(file: File, fileCategory: string): Promise<boolean> {
    // const formData: FormData = new FormData();
    // formData.append('file', file, file.name);

    const url = environment.apiUrl + '/api/bibss/v1/addbillingissuancebyssdocument';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: 'Basic cm95OnBhc3M=',
      'Content-Type': 'application/json'
    });


    const Body: any = {

      i_bil_id: this.bilIdFromInsert,
      i_file_nm: file.name,
      i_file_content: this.i_file_content,
      i_file_type: file.type,
      i_file_size: file.size.toString(),
      i_file_category: fileCategory,
      i_created_by: null,
      i_modified_by: null,
      i_status: Systemstatus.Active

    };

    try {
      const response: any = await this.http.post(url, Body, { headers }).toPromise();
      if (response.header.statusCode === '00') {
        return false; // Insert success
      } else {
        console.error('Error uploading file:', response);
        return true; // Insert failed
      }
    } catch (error) {
      this.error = true;
      this.errorMessages.push('Internal Server Error.');
      console.error(error);
      return true; // Error occurred
    }
  }

  //single file start
  async onLoaFileSelected(event: any) {
    let files = event.target.files;
    const selectedFile = files[0]; // Single file selection
    const currentFileSize = selectedFile.size;
    const allowedExtensions = ['png', 'jpeg', 'jpg', 'pdf', 'doc', 'docx'];

    this.errorFile = false;
    this.errorFileMessages = [];
    this.errorFileSizeLimit = false;
    this.errorFileSizeLimitMessages = [];
    this.errorFileDuplicate = false;
    this.errorFileDuplicateMessages = [];

    // Extract file extension
    const fileExtension = selectedFile.name.split('.').pop().toLowerCase();

    if (!allowedExtensions.includes(fileExtension)) {
      this.errorFile = true;
      this.errorFileMessages.push(selectedFile.name);
      //this.selectedLoaFile = null; // Clear the file input
      return;
    }

    console.log("currentFileSize is " + currentFileSize);

    if (currentFileSize <= 10 * 1024 * 1024) {
      this.selectedLoaFile = selectedFile;
      this.selectedLoaFileSize = currentFileSize;
    }
    else {
      //alert('File size exceeds 10MB. Please select a smaller file.');
      this.errorFileSizeLimit = true;
      this.errorFileSizeLimitMessages.push('Total file size exceeds 10MB. Please select smaller files.');
      //this.selectedLoaFile = null; // Clear the selected file if it exceeds the limit
      // this.selectedLoaFileSize = 0;
    }

    if (this.billing_method != 'O') {
      if (this.selectedLoaFileSize === 0) {
        this.isDisplayFileRequired = true;
      }
      else {
        this.isDisplayFileRequired = false;
      }
    }

    console.log('total size' + this.selectedLoaFileSize);
  }

  async readSingleFileAsync(): Promise<boolean> {
    let result: boolean = false;
    let fileCategory: string = '';
    if (this.billing_method === 'L') {
      fileCategory = 'L';
    }
    else {
      fileCategory = 'A';
    }

    if (!this.selectedLoaFile) {
      console.error('No file selected.');
      return false;
    }

    // Read the single file asynchronously
    this.i_file_content = await new Promise((resolve, reject) => {
      const reader = new FileReader();

      reader.onload = (e: any) => {
        resolve(e.target.result);
      };

      reader.onerror = reject;

      reader.readAsDataURL(this.selectedLoaFile);
    });

    // Upload the file after reading
    result = await this.uploadFile(this.selectedLoaFile, fileCategory);

    return result;
  }

  //single file end

  //upload file end

  /*
      // File upload logic with readFileAsync
    async readFileAsync(): Promise<boolean> {
      let result = true;
    
      // Upload Supporting Documents
      for (const file of this.selectedFiles) {
        const success = await this.uploadFile(file, 'Supporting Documents');
        if (!success) result = false;
      }
      // Upload LOA/Agreement File
      if (this.selectedLoaFile) {
        const success = await this.uploadFile(this.selectedLoaFile, this.billing_method == 'L' ? 'LOA' : 'Agreement');
        if (!success) result = false;
      }
    
      return result;
    }
    
    // File upload method
    async uploadFile(file: File, fileCategory: string): Promise<boolean> {
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
        i_file_category: fileCategory // Supporting Documents or LOA/Agreement
      };
    
      const headers = new HttpHeaders({
        Authorization: environment.authKey,
        'Content-Type': 'application/json',
      });
    
      try {
        await this.http.post(environment.apiUrl + '/api/OTCRC/v1/insnonbildoc', fileBody, { headers }).toPromise();
        console.log(`File ${file.name} uploaded successfully.`);
        return true;
      } catch (error) {
        console.error(`Error uploading file ${file.name}:`, error);
        return false;
      }
    }
  */

  // onSupportingFilesSelected(event: any): void {
  //   const files: FileList = event.target.files;
  //   let currentFilesTotalSize = this.selectedFilesSize;

  //   for (let i = 0; i < files.length; i++) {
  //     const file = files[i];

  //     if (currentFilesTotalSize + file.size > 10 * 1024 * 1024) {
  //       alert('Total file size exceeds 10MB. Please select smaller files.');
  //       continue;
  //     }

  //     if (!this.selectedFiles.some((f) => f.name === file.name)) {
  //       this.selectedFiles.push(file);
  //       currentFilesTotalSize += file.size;
  //     } else {
  //       alert(`File "${file.name}" already selected.`);
  //     }
  //   }

  //   this.selectedFilesSize = currentFilesTotalSize;
  // }

  // onLoaFileSelected(event: any): void {
  //   const file: File = event.target.files[0];

  //   if (file) {
  //     if (file.size > 10 * 1024 * 1024) {
  //       alert('File size exceeds 10MB. Please select a smaller file.');
  //       return;
  //     }

  //     this.selectedLoaFile = file;
  //     this.selectedLoaFileSize = file.size;
  //   }
  // }

  clearLoaFile(): void {

    if (this.selectedLoaFileSize > 0) {//added condition because if not, when user didn't  
      this.selectedLoaFile = null;
      this.selectedLoaFileSize = 0;
      this.isDisplayFileRequired = true;
    }

    this.errorFile = false;
    this.errorFileMessages = [];
    this.errorFileSizeLimit = false;
    this.errorFileSizeLimitMessages = [];
    this.errorFileDuplicate = false;
    this.errorFileDuplicateMessages = [];

    // Reset file input to allow re-selection of the same file
    const fileInput = document.getElementById('loaFileInput') as HTMLInputElement;
    if (fileInput) {
      fileInput.value = ''; // Clears the file input field
    }
  }

  loadBillingRunningNo() {
    this.billing_no = '';
    const url = environment.apiUrl + '/api/bibss/v1/getbillingissuancebyssrunno';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json'
    });

    const requestBody = {
    };


    this.http.post(url, requestBody, { headers }).subscribe(
      (response: any) => {
        if (response.data.length === 0) {
          console.error('Invalid response format:', response);
        }
        else {
          this.billing_no = response.data;
          this.updateIssuanceList();
        }
      },
      (error) => {
        //this.error = true;
        //this.errorMessages.push('Error in retrieving Billing Running No.');
        console.error('There was an error retrieving the billing running no:', error);

      }
    );

    // // Make the HTTP GET request
    // this.http.post(url, { headers: headers }).subscribe(
    //   (response: any) => {
    //     if (response.data.length > 0)
    //       this.billing_no = response.data;
    //     this.updateIssuanceList();
    //   },
    //   (error: any) => {
    //     console.error(error);
    //   }
    // );
  }

  // loadHistory() {

  //   this.isLoadingHistory = true;

  //   const urlMftWFHis = environment.apiUrl + '/api/bibss/v1/getbillingissuancebysshistory';

  //   // Set your authorization header
  //   const headers = new HttpHeaders({
  //     Authorization: environment.authKey,
  //     'Content-Type': 'application/json'
  //   });


  //   // Create the request body with your form data
  //   const requestBody: any = {
  //     i_bil_id: null
  //   };

  //   this.http.post(urlMftWFHis, requestBody, { headers }).subscribe(
  //     (response: any) => {

  //       if (response.data.length === 0) {
  //         // this.isDisplayHist = false;
  //         this.isLoadingHistory = false;
  //         this.totalHistoryRecords = 0;
  //         console.error('Invalid otc receipt cancellation history table details response format:', response);
  //       }
  //       else {
  //         this.totalHistoryRecords = response.data[0].total;
  //         this.billingHistory = response.data;
  //         // this.isDisplayHist = true;
  //         this.isLoadingHistory = false;
  //       }
  //     },
  //     (error) => {
  //       console.error('There was an error retrieving the history table:', error);
  //       this.isLoadingHistory = false;
  //     }
  //   );
  // }

  loadETypes() {
    this.ParamService.getStates(environment.DefaultPage.toString(), environment.ItemPerPage.toString(), '', 'EntityType').subscribe((response: any) => {
      if (response.data.length >= 0) {
        //this.entity_types = response.data as ParamData[]; later
        this.entity_types = response.data as any[];
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
        // this.states = response.data as ParamData[]; later
        this.states = response.data as any[];
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
        for (var data of response.data) {
          delete data.total;
          if (data.param_cd == 'M')
            data.value = 1;
          else if (data.param_cd == 'Q')
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

  // //if true will disable submit button
  // submitCheck() {
  //   /*
  //   if(!this.bt_code.length || !this.entity_type.length || !this.entity_no.length || !this.cust_id.length
  //     || !this.cus_name.length || !this.cus_email.length || !this.cus_phno.length || !this.cus_add1.length
  //     || !this.cus_add2.length || !this.cus_add3.length || !this.postcode.length || !this.city.length
  //     || this.state == null || !this.entity_name.length || !this.sscode.length || !this.billing_no.length
  //     || !this.requester_name.length || !this.requester_email.length || this.billing_items_total_cost == 0
  //     || !this.billing_desc.length)
  //     return true;

  //   var emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  //   if(!emailRegex.test(this.cus_email) || !emailRegex.test(this.requester_email))
  //     return true;

  //   if(this.billing_method != 'O'){
  //     if(!this.loa_ref_no.length  || this.isuance_count == 0 
  //       || this.selectedLoaFile == null || this.selectedLoaFileSize == 0)
  //       return true;
  //     if(this.billing_method == 'A' && !this.agmt_ref_no.length)
  //         return true;
  //   }
  //   */
  //   return false;
  // }

  freezeScreen() {
    var tmp = <HTMLElement>document.getElementsByTagName('app-root')[0];
    tmp.style.pointerEvents = 'none';
    document.getElementById("mainBodyContainer")!.style.pointerEvents = 'none';
    document.getElementById("f_overlay")!.style.display = 'block';
  }

  unfreezeScreen() {
    var tmp = <HTMLElement>document.getElementsByTagName('app-root')[0];
    tmp.style.pointerEvents = 'all';
    document.getElementById("mainBodyContainer")!.style.pointerEvents = 'all';
    document.getElementById("f_overlay")!.style.display = 'none';
  }

  cancelForm() {
    this.router.navigate(['/billing-listing']);
  }


  async submitForm() {
    this.freezeScreen();
    // this.isLoading = true;
    // this.defaultSetting();
    if (this.cfm_loa_ref_no != this.loa_ref_no) {
      this.has_checked_loa_registered = false;
      this.has_checked_loa_exists = false;
    }

    if (this.billing_method == 'A' && !this.has_checked_loa_registered)
      await this.checkLOARegistered();
    else if (this.billing_method == 'L' && !this.has_checked_loa_exists)
      await this.checkLOAExists();

    if (this.billing_method == 'A' && !this.loa_registered) {
      this.unfreezeScreen();
      return;
    }
    if (this.billing_method == 'L' && this.loa_exists) {
      this.unfreezeScreen();
      return;
    }


    let invalidUploadSupportingFile: boolean = false;
    let invalidUploadLoaFile: boolean = false;
    const invalidInsBilling = await this.insbilling();

    if (this.selectedSupportingFiles.length !== 0 && invalidInsBilling === false) {
      invalidUploadSupportingFile = await this.readMultipleFileAsync(); //must put below insbilling();
    }

    if (this.selectedLoaFile && this.selectedLoaFile.length !== 0 && invalidInsBilling === false) {
      invalidUploadLoaFile = await this.readSingleFileAsync();
    }

    if (invalidInsBilling === false && invalidUploadSupportingFile === false && invalidUploadLoaFile === false) {
      // this.sendEmail();
      // this.isLoading = false;
      this.unfreezeScreen();
      console.log('Billing issuance submitted successfully');

      var tmp = <HTMLElement>document.getElementsByTagName('app-root')[0];
      tmp.style.pointerEvents = 'none';
      document.getElementById("mainBodyContainer")!.style.pointerEvents = 'none';
      document.getElementById("f_overlay")!.style.display = 'block';

      this.showInsertAlert = true;
      tmp.style.pointerEvents = 'all';
      document.getElementById("mainBodyContainer")!.style.pointerEvents = 'none';
      document.getElementById("f_overlay")!.style.display = 'none';
      setTimeout(() => {
        this.showInsertAlert = false;
        this.toCallBackURl();
        return;
      }, 5000);

      // const alert_msg = "submittedForApproval";
      // this.router.navigate(['/my-task-assigned-tasks'], { state: { alert_msg } });
    }
    else {
      this.unfreezeScreen();
      console.error('Error submitting billing issuance');
    }

    //    this.isLoading = false;
  }

  async insbilling(): Promise<boolean> {

    const url = environment.apiUrl + '/api/bibss/v1/insertbillingissuancebyssbillingcustomer';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json'
    });

    this.updateShortformState(); //get the state shortform
    console.log(this.shortformstate);

    console.log(this.bllingChildDetailsTemp);

    // Transform billingItemsDetailsTemp to exclude 'desc' and populate billingItemsDetails
    this.billingItemsDetails = this.billingItemsDetailsTemp.map(item => ({
      mft_pk: item.mft_pk,
      unit_fee: item.unit_fee,
      qty: item.qty,
      tax_pct: item.tax_pct,
      tax_amt: item.tax_amt,
      final_amt: item.final_amt,
      status: item.status
    }));


    let invalidRunno = await this.getAndReserveLastestRunno();
    if (invalidRunno) return true; //return true if invalid runno

    this.billingChildDetails = []; //clear child details first before insert

    if (this.billing_method == 'O') { //method is O(one-time) means insert one row

      this.billingChildDetails.push({
        bil_child_date: new Date(),
        bil_child_status: 'A', //brian said to put A
        status: 'A',
        bil_wf_id: null,
        bil_no: this.runnoThatReserved,
        bil_status: 'WF-A'
      });
    }
    else {//if method is L/A (load or agreement) means insert multiple row
      // Initialize the base run number from the reserved value
      let currentRunno = this.runnoThatReserved;
      let runnoCounter = parseInt(currentRunno.slice(-6), 10); // Extract numeric part of run number
      this.bllingChildDetailsTemp.forEach((item, index) => {
        console.log(`Object at index ${index}:`, item);
      });
      // Transform billingItemsDetailsTemp to exclude 'desc' (use in UI display) and populate billingChildDetails
      this.billingChildDetails = this.bllingChildDetailsTemp
        .filter(item => item.issuance === true) // Filter items with issuance === true
        .map(item => {
          const bilNo = currentRunno.slice(0, -6) + runnoCounter.toString().padStart(6, '0');
          runnoCounter++; // Increment the counter for the next bil_no

          return {
            bil_child_date: item.billing_date,
            bil_child_status: 'A', // Set status to 'A' for issuance === true
            status: 'A',
            bil_wf_id: null,
            bil_no: bilNo,
            bil_status: 'WF-A',
          };
        });
    }

    // //mtt and mtt item start
    // //map to paymentItemDetails, must put after getAndReserveLastestRunno to get billing_no for orn_no
    // this.paymentItemDetails = this.billingItemsDetailsTemp.map((item, index) => ({
    //   fee_detail_id: item.mft_id,
    //   item_ref_no: (index + 1).toString(), // Convert index + 1 to a string
    //   line_no: index + 1, // Map index to line_no, starting from 1,
    //   item_desc: item.fee_detail_nm_e,
    //   qty: item.qty,
    //   unit_fee: item.unit_fee,
    //   tax_amt: item.tax_amt,
    //   disc_amt: 0,
    //   gross_amt: item.final_amt,
    //   grant_cd: null,
    //   tax_pct: item.tax_pct,
    //   net_amt: item.final_amt,
    //   entity_type: this.entity_type,
    //   entity_no: this.entity_no,
    //   entity_nm: this.entity_name
    // }));

    // this.ornDetails = {
    //   ss_cd: this.sscode,
    //   orn_no: this.runnoThatReserved,
    //   orn_dt: new Date(),
    //   cust_nm: this.cus_name,
    //   cust_addr_1: this.cus_add1,
    //   cust_addr_2: this.cus_add2,
    //   cust_addr_3: this.cus_add3,
    //   cust_postcode: this.postcode,
    //   cust_city: this.city,
    //   cust_state: this.state,
    //   cust_email: this.cus_email,
    //   cust_phone: this.cus_phno,
    //   total_amt: this.billing_items_total_cost,
    //   ss_return_url: '',
    //   payment_item_details: this.paymentItemDetails
    // };

    // //mtt and mtt item end

    console.log("This is child details " + this.billingChildDetails);

    // Create the request body with your form data
    var requestBody: { [k: string]: any } = {
      i_cust_id: this.cust_id,
      i_cust_nm: this.cus_name,
      i_cust_email: this.cus_email,
      i_cust_phone: this.cus_phno,
      i_cust_addr1: this.cus_add1,
      i_cust_addr2: this.cus_add2,
      i_cust_addr3: this.cus_add3,
      i_cust_postcode: this.postcode,
      i_cust_city: this.city,
      //i_cust_state: this.state,
      i_cust_state: this.shortformstate,
      i_ent_nm: this.entity_name,
      i_ent_no: this.entity_no,
      i_ent_ty: this.entity_type,
      // i_created_by
      // i_modified_by
      i_status: 'A',
      i_bltc_id: this.billingTypeCodeId,
      i_req_name: this.requester_name,
      i_req_email: this.requester_email,
      i_ss_cd: this.sscode,
      i_billing_no: this.runnoThatReserved,
      i_billing_desc: this.billing_desc,
      i_action: 'Billing Registration',
      i_dps_amt: null,
      i_billing_cnt: this.billing_method == 'O' ? 1 : this.isuance_count,
      i_billing_freq: this.billing_method == 'O' ? 'D' : this.billing_frequency == 1 ? 'M' : 'Q',
      i_bil_wf_status: 'WF-A',
      i_pickup_by: null,
      i_dt_pick: null,
      i_billing_mthd: this.billing_method,
      i_msg: this.remarks,
      i_msg_type: 'R',
      // i_order_summary: this.orderSummary,
      i_billingItemDetails: this.billingItemsDetails,
      i_billingChildDetails: this.billingChildDetails,
      // i_paymentRequest: this.ornDetails //use i_paymentrequest because java use paymentRequest
    };

    if (this.billing_method !== 'O') {
      requestBody['i_loa_id'] = this.loa_ref_no;
    }


    if (this.billing_method === 'A') {
      requestBody['i_agm_id'] = this.agmt_ref_no;
      requestBody['i_dt_agm_start'] = new Date(this.date_range[0]);
      requestBody['i_dt_agm_end'] = new Date(this.date_range[1])
    }
    else if (this.billing_method === 'L') {
      requestBody['i_dt_loa_start'] = new Date(this.date_range[0]);
      requestBody['i_dt_loa_end'] = new Date(this.date_range[1])
    }
    else {
      console.log('Billing method selected is:', this.billing_method);
    }

    //   requestBody['i_loa_document'] = fileBody;

    //   if(this.billing_method == 'A')  
    //     requestBody['i_agmt_ref_no'] = this.agmt_ref_no;
    // }

    console.log(requestBody);
    try {
      const response: any = await this.http.post(url, requestBody, { headers }).toPromise();
      if (response.header.statusCode === '00') {
        this.bilIdFromInsert = response.data;
        return false; // Update success
      } else {
        return true; // update failed
      }
    } catch (error) {
      this.error = true;
      this.errorMessages.push('Internal Server Error.');
      console.error(error);
      return true; // Error occurred
    }
  }


  loadBillingTypeCode() {


    const url = environment.apiUrl + '/api/bibss/v1/getbillingissuancebyssbillingtypecode';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json'
    });


    // Create the request body with your form data
    const requestBody = {
      i_page: this.page,
      i_size: this.dropDownSize, //dont use item per page here because it is for table
      i_bt_ty: 'B',
      i_class_id: null,
      i_ss_cd: this.sscdFromCallback
    };

    // Send an HTTP POST request to the API
    this.http.post(url, requestBody, { headers }).subscribe(
      (response: any) => {
        if (response.data.length === 0) {
          this.billingTypeCodeOptions.length = 0;
          console.error('Invalid response format:', response);
        }
        else {
          this.billingTypeCodeOptions = response.data;
          this.combineSSCode = this.billingTypeCodeOptions[0].ss_nm + ' (' + this.billingTypeCodeOptions[0].ss_cd + ')';
          this.updateUniqueBillingOptions(); // Update unique options for dropdown
        }
      },
      (error) => {
        console.error('There was an error retrieving the billing type code:', error);

      }
    );

  }

  updateUniqueBillingOptions(): void {
    const uniqueOptions = new Map();
    this.billingTypeCodeOptions.forEach((item) => {
      if (!uniqueOptions.has(item.bltc_id)) {
        uniqueOptions.set(item.bltc_id, item);
      }
    });
    this.uniqueBillingTypeOptions = Array.from(uniqueOptions.values());
  }

  updateBillingTypeCode(selectedBltcId: number | null): void {
    this.billing_items_total_cost = 0; // Reset the total cost
    if (selectedBltcId === null) {
      this.billingItemsDetailsTemp = []; // Clear the list if no selection
      this.ssname = '';
      return;
    }

    // Filter the billingTypeCodeOptions for the selected bltc_id
    const filteredItems = this.billingTypeCodeOptions.filter(item => item.bltc_id === selectedBltcId);
    this.sscode = filteredItems[0].ss_cd;
    this.ssname = filteredItems[0].ss_nm;
    // this.orderSummary = filteredItems[0].bt_cd + ' - ' + filteredItems[0].bt_desc;
    // Map the filtered items to match the structure of billing_items
    this.billingItemsDetailsTemp = filteredItems.map(item => ({
      desc: `${item.mft_id} - ${item.fee_detail_nm_e}`,
      mft_id: item.mft_id,
      fee_detail_nm_e: item.fee_detail_nm_e,
      mft_pk: item.mft_pk,
      unit_fee: item.unit_fee !== undefined ? item.unit_fee.toFixed(2) : '0.00', // Keep as string
      qty: 0,
      tax_pct: item.tax_pct,
      tax_amt: 0,
      final_amt: 0,
      status: 'A'
    }));
    console.log("unit fee " + this.billingItemsDetailsTemp[0].unit_fee);
  }

  async handleFormSubmit(form: NgForm) {

    this.isBITotalTouched = true; //true because if user didn't change the value and press submit, it will not trigger the error
    //let inValidSubmit = this.submitCheck(); //return false mean can submit, true mean cannot 
    let formValidation: boolean | null = false;


    if (this.billing_method != 'O') {
      if (this.selectedLoaFileSize === 0) {
        this.isDisplayFileRequired = true;
      }
      else {
        this.isDisplayFileRequired = false;
      }
    }

    if (this.billing_method === 'O') {
      formValidation = form.valid && (this.billing_items_total_cost > 0);
    }
    else if (this.billing_method === 'L') {
      if (this.loa_exists)
        this.validLOABeforeSubmitMethodLoa = true;

      // console.log('total loa size' + this.selectedLoaFileSize);
      // console.log('isuance_count is ' + this.isuance_count);
      // console.log('billing_items_total_cost is ' + this.billing_items_total_cost);
      // console.log('loa exists is ' + this.loa_exists);
      formValidation = form.valid && (this.selectedLoaFileSize !== 0) && (this.isuance_count !== 0) && (this.billing_items_total_cost > 0) && !this.loa_exists;
    }
    else if (this.billing_method === 'A') {
      if (!this.loa_registered)
        this.validLOABeforeSubmitMethodAgr = true;

      // console.log('total loa size' + this.selectedLoaFileSize);
      // console.log('isuance_count is ' + this.isuance_count);
      // console.log('billing_items_total_cost is ' + this.billing_items_total_cost);
      // console.log('loa registered is ' + this.loa_registered);
      formValidation = form.valid && (this.selectedLoaFileSize !== 0) && (this.isuance_count !== 0) && (this.billing_items_total_cost > 0) && this.loa_registered;
    }


    //formValidation = form.valid && !inValidSubmit;
    console.log('Form validation:', formValidation);
    if (formValidation) {
      //console.log('Form is valid');
      this.submitForm();
    } else {
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

  //if true will disable submit button
  // submitCheck() {
  //   if (this.checkingLOA)
  //     return true;
  //   if ((this.billing_method == 'L' && this.loa_exists) || (this.billing_method == 'A' && !this.loa_registered))
  //     return true;

  //   if (this.billingTypeCodeId === null || !this.entity_type.length || !this.entity_no.length || !this.cust_id.length
  //     || !this.sscode.length || !this.billing_no.length || this.billing_items_total_cost == 0)
  //     return true;

  //   if (this.billingTypeCodeId === null || !this.entity_type.length || !this.entity_no.length || !this.cust_id.length
  //     || !this.cus_name.length || !this.cus_email.length || !this.cus_phno.length || !this.cus_add1.length
  //     || !this.cus_add2.length || !this.cus_add3.length || !this.postcode.length || !this.city.length
  //     || this.state == null || !this.entity_name.length || !this.sscode.length || !this.billing_no.length
  //     || !this.requester_name.length || !this.requester_email.length || this.billing_items_total_cost == 0
  //     || !this.billing_desc.length)
  //     return true;

  //   var emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  //   if (!emailRegex.test(this.cus_email) || !emailRegex.test(this.requester_email))
  //     return true;

  //   if (this.billing_method != 'O') {
  //     if (!this.loa_ref_no.length || this.isuance_count == 0
  //       || this.selectedLoaFile == null || this.selectedLoaFileSize == 0)
  //       return true;
  //     if (this.billing_method == 'A' && !this.agmt_ref_no.length)
  //       return true;
  //   }

  //   return false;
  // }

  cancel() {
    this.router.navigate(['/bibss-listing']);
  }


  async getAndReserveLastestRunno(): Promise<boolean> {

    // const urlMftWF = environment.apiUrl + '/api/bibss/v1/getandreservebillrunno';
    const urlMftWF = environment.apiUrl + '/api/bibss/v1/getandreservebillrunno';
    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json'
    });

    const Body: any = {
      i_number_to_reserve: this.billing_method == 'O' ? 1 : this.isuance_count,
    };

    try {
      const response: any = await this.http.post(urlMftWF, Body, { headers }).toPromise();

      if (response.header.statusCode === '00') {
        this.runnoThatReserved = response.data;
        return false;
      } else {
        console.error('Invalid billing issuance by source system getandreservebillrunno response format:', response);
        return true;
      }
    } catch (error) {
      this.error = true;
      this.errorMessages.push('Internal Server Error.');
      console.error('There was an error retrieving the getandreservebillrunno:', error);
      return true;
    }
  }

  formatInputPromoFee(item: any): void {
    if (item && item.unit_fee !== null && item.unit_fee !== undefined) {
      // Format the value to two decimal places
      item.unit_fee = parseFloat(item.unit_fee).toFixed(2);
    }
  }


  toCallBackURl() {
    document.location.href = this.callbackurl + "?billing_no=" + this.runnoThatReserved;
  }

  resetCheckLOAExists(): void {
    // Reset the state when user modifies the input
    this.loa_exists = true;
    this.has_checked_loa_exists = false;
    this.loaAlertString = '';
    this.showLoaAlert = false;
    this.validLOABeforeSubmitMethodLoa = false;
  }

  async onCheckLOAExists(control: NgModel) {

    this.validLOABeforeSubmitMethodLoa = false;

    // Manually trigger validation
    control.control.markAsTouched();

    if (!this.loa_ref_no) {
      console.log('LOA Reference Number is required.');
      return; // Stop if invalid
    }

    await this.checkLOAExists();
  }

  async checkLOAExists() {
    if (this.loa_ref_no == null || this.loa_ref_no == '') {
      return;
    }

    const url = environment.apiUrl + '/api/billing/v1/getexistsloa';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json'
    });

    // Create the request body with your form data
    var requestBody: { [k: string]: any } = {
      i_loa_ref_no: this.loa_ref_no
    };

    this.checkingLOA = true;
    //console.log(requestBody);
    this.http.post(url, requestBody, { headers }).subscribe((response: any) => {
      if (response.data && response.data.length > 0) {
        //console.log('grab loa exists:' + response.data);
        if (parseFloat(response.data) == 0) {
          this.loa_exists = false;
          this.loaAlertString = '';
          this.showLoaAlert = false;
          this.cfm_loa_ref_no = JSON.parse(JSON.stringify(this.loa_ref_no));
        }
        else {
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


  resetCheckLOARegistered(): void {
    // Reset the state when user modifies the input
    this.loa_registered = false;
    this.has_checked_loa_registered = false;
    this.loaAlertString = '';
    this.showLoaRegisteredAlert = false;
    this.validLOABeforeSubmitMethodAgr = false;
  }

  async oncheckLOARegistered(control: NgModel) {

    this.validLOABeforeSubmitMethodAgr = false;

    // Manually trigger validation
    control.control.markAsTouched();

    if (!this.loa_ref_no) {
      console.log('LOA Reference Number is required.');
      return; // Stop if invalid
    }

    await this.checkLOARegistered();
  }

  async checkLOARegistered() {
    if (this.loa_ref_no == null || this.loa_ref_no == '') {
      this.isLOAAgreementRequired = true;
      return;
    }

    const url = environment.apiUrl + '/api/billing/v1/getregisteredloa';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json'
    });

    // Create the request body with your form data
    var requestBody: { [k: string]: any } = {
      i_loa_ref_no: this.loa_ref_no
    };

    this.checkingLOA = true;
    //console.log(requestBody);
    this.http.post(url, requestBody, { headers }).subscribe((response: any) => {
      if (response.data && response.data.length > 0) {
        //console.log('grab loa registered: ' + response.data);
        if (parseFloat(response.data) > 0) {
          this.loa_registered = true;
          this.loaAlertString = '';
          this.showLoaRegisteredAlert = false;
          this.cfm_loa_ref_no = JSON.parse(JSON.stringify(this.loa_ref_no));
        }
        else {
          this.loa_registered = false;
          this.loaAlertString = 'LOA Reference not registered!';
          this.showLoaRegisteredAlert = true;
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

  loadPermissions() {

    this.authService.checkUserRole(this.authService.username, this.permBilReg)
      .subscribe(
        (response: any) => {
          this.permBilRegAllow = response.data;
          this.permListAllow = this.permBilRegAllow.includes(perm.Billing_Issuance_By_Source_System_Billing_Registration) ? 1 : 0;

          console.log(this.permListAllow,);
          if (this.permListAllow === 0) {
            console.log("access-denied");
            this.router.navigate(['/access-denied']);
            return; // Exit the function to prevent further execution
          }
        });
  }

  loadPostcode() {

    const url = environment.apiUrl + '/api/rms/v1/getpostcode';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    // const Body: any = {
    // };

    this.http.post(url, {}, { headers }).subscribe(
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

  //postcode start
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

  upperCity = (term: string): string | null => {
    if (!term) return null;
    const trimmed = term.trim().toUpperCase();
    return trimmed.length > 50 ? trimmed.substring(0, 50) : trimmed; //allow maximum 50 characters
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
    const match = this.states.find(s => s.nm_en === this.state);
    this.shortformstate = match ? match.param_cd : null;
  }

  isPhoneInvalid: boolean = false;

  checkPhoneLength(): void {
    const phoneLength = this.cus_phno?.length || 0;
    this.isPhoneInvalid = phoneLength < 10 || phoneLength > 15;
  }

  allowOnlyNumbers(event: KeyboardEvent): void {
    const pattern = /^[0-9]$/;
    const inputChar = String.fromCharCode(event.charCode);

    if (!pattern.test(inputChar)) {
      event.preventDefault(); // blocks the input
    }
  }

  isEntityNoTooLong: boolean = false;

  checkEntityNoLength(): void {
    const entityNumberLength = this.entity_no?.length || 0;
    this.isEntityNoTooLong = entityNumberLength > 20;
  }










}
