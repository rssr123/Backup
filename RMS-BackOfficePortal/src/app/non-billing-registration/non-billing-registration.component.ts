import { HttpClient, HttpHeaders } from '@angular/common/http';
import { ChangeDetectorRef, Component, ViewChild, ElementRef } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { NgbPaginationConfig } from '@ng-bootstrap/ng-bootstrap';
import { TranslateService } from '@ngx-translate/core';
import { environment } from 'src/environments/environment';
import { AuthService } from '../core/services/auth.service';
import { GlobalService } from '../shared/global.service';
import { ParamService } from 'src/app/core/services/param.service';
import { NBLTC, NBLTCItem, NonBillingListing, OTCBank } from '../core/models/otc-collection-returned-cheque.interface';
import { CounterCheckInStatus } from 'src/app/core/services/otc-counter-status.service';
import { ParamData } from '../core/models/param.interface';
import { NgModel } from '@angular/forms';
import { PostCodeData } from '../core/models/postcode.interface';

@Component({
  selector: 'app-non-billing-registration',
  templateUrl: './non-billing-registration.component.html',
  styleUrls: ['./non-billing-registration.component.scss']
})
export class NonBillingRegistrationComponent {
  @ViewChild('supportingFilesInput') supportingFilesInput!: ElementRef;
  @ViewChild('rcaFileInput') rcaFileInput!: ElementRef;
  @ViewChild('cityRef') cityRef!: NgModel;
  @ViewChild('stateRef') stateRef!: NgModel;
  @ViewChild('postcodeRef') postcodeRef!: NgModel;
  
  isLoading: boolean = false;
  modelData: any;
  otcBodyID: number;
  chequeID: string;
  customerExist = false;
  chequeAmt: number = 0;
  totalRecords: number = 0;
  nbltcModel: NBLTC[] = [];
  nbltcItemModel: NBLTCItem[] = [];
  rctypeModel: OTCBank[] = [];
  mockCustId: string | null = '';
  custName: string = '';
  custEmail: string = '';
  custPhoneNumber: string = '';
  custAddr1: string = '';
  custAddr2: string = '';
  custAddr3: string = '';
  postcode: string = '';
  city: string | null = null;
  state: string | null = null;
  uniqueCities: string[] = [];
  uniqueStates: string[] = [];
  entityName: string = '';
  requestorName: string = '';
  requestorEmail: string = '';
  payerName: string = '';
  payerEmail: string = '';
  nbdesc: string = '';
  quantities: number[] = []; // Dynamic quantities for each row
  taxAmounts: number[] = []; // Tax amounts for each row
  rowTotals: number[] = []; // Totals for each row
  grandTotal: number = 0; // Grand total
  chequeNo: string | null = null;
  nbrunno: string | null = null;
  remarks: string = '';
  selectedReason: string = ""; // Variable to hold selected reason
  model: NonBillingListing[] = [];
  inputDetails: boolean = false;
  supportingDocs: File[] = [];
  rcaDocs: File[] = [];
  maxFileSize = 10 * 1024 * 1024; // 10MB
  selectedFiles: File[] = [];
  selectedFilesSize: number = 0;
  isDisplayFileRequired: boolean = false;
  i_file_content: any;
  errorMessages: string[] = [];
  error: boolean = false;
  selectedRcaFile: File | null = null; // For single RCA file
  selectedRcaFileSize: number = 0;
  isInvalidPostcode: boolean = false;
  states: ParamData[] = []; // To store the API response
  entityTypeDropdown: ParamData[] = []; // To store the API response

  // Fields to filled in
  billingTypeCode: string = '';
  entityType: string = '';
  entityNumber: string = '';
  validationMessage: string | null = null;
  messageColor: string = 'red'; // Default to red for errors
  OTCCheckedIn: number = 0;
  postCodes: PostCodeData[] = [];
  totalPostCodeRecords: number = 0;

  constructor(
    private http: HttpClient,
    config: NgbPaginationConfig,
    private router: Router,
    public dialog: MatDialog,
    private ParamService: ParamService,
    private translate: TranslateService,
    private globalService: GlobalService,
    private cd: ChangeDetectorRef,
    private authService: AuthService,
    public counterCheckInStatus: CounterCheckInStatus,
  ) {
    config.maxSize = environment.PaginationMaxSize;
    config.boundaryLinks = true;
    this.translate.setDefaultLang(this.globalService.getGlobalValue());
    this.translate.use(this.globalService.getGlobalValue());
    const navigation = this.router.getCurrentNavigation();
    this.chequeNo = navigation?.extras.state?.['chequeNo'] || null;
    this.chequeAmt = navigation?.extras.state?.['chequeAmount'] || 0;
    this.modelData = navigation?.extras.state?.['modelData'] || null;
    this.otcBodyID = navigation?.extras.state?.['otcBodyID'] || null;
    this.chequeID = navigation?.extras.state?.['chequeID'] || null;

    console.log(this.otcBodyID, this.chequeID);
  }

  ngOnInit(): void {
    this.fetchNBLTC();
    this.fetchRCType();
    this.fetchNBNo();
    this.loadCounterInfo();
    this.loadStates();
    this.loadPostcode();
    this.loadEntityType();
    // this.loadData();
  }

  showDuplicateBox2 = false;
  duplicateMessage = ""; // Store the dynamic message
  showDuplicateBoxAlert(message: string) {
    this.duplicateMessage = message; // Store the message
    this.showDuplicateBox2 = true;
    setTimeout(() => (this.showDuplicateBox2 = false), 10000);
  }


  fetchNBLTC(): void {
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    const url = environment.apiUrl + '/api/OTCRC/v1/getnbltc';

    const Body: any = {
    };

    this.http.post(url, Body, { headers }).subscribe(
      (response: any) => {
        this.nbltcModel = response.data;
        console.log(this.nbltcModel);
        this.totalRecords = response.data.length > 0 ? response.data[0].total : 0;
        this.isLoading = false;
      },
      (error) => {
        console.error(error);
        this.isLoading = false;
      }
    );
  }

  fetchNBNo(): void {
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    const url = environment.apiUrl + '/api/OTCRC/v1/getnbno';

    const Body: any = {
    };

    this.http.post(url, Body, { headers }).subscribe(
      (response: any) => {
        this.nbrunno = response.data;
        this.isLoading = false;
      },
      (error) => {
        console.error(error);
        this.isLoading = false;
      }
    );
  }

  fetchRCType(): void {
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    const url = environment.apiUrl + '/api/rms/v1/getrctype';

    const Body: any = {
    };

    this.http.post(url, Body, { headers }).subscribe(
      (response: any) => {
        this.rctypeModel = response.data;
        this.totalRecords = response.data.length > 0 ? response.data[0].total : 0;
        this.isLoading = false;
      },
      (error) => {
        console.error(error);
        this.isLoading = false;
      }
    );
  }

  callCustomerAPI(): void {
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
      'X-IBM-Client-Id': environment.xibmclientid  // ✅ added header
    });
    
    const url = environment.fmsCheckCustIdApi;

    const selectedNbltc: any = this.nbltcModel.find(nbltc => nbltc.bt_cd === this.billingTypeCode);
    console.log(selectedNbltc.class_id);

    const body: any = {
      "CustomerClass": {
        "value": selectedNbltc.class_id
      },
      "custom": {
        "CurrentCustomer": {
          "UsrIdentityNbr": {
            "type": "CustomStringField",
            "value": this.entityNumber
          }
        }
      }
    };

    this.http.post(url, body, { headers }).subscribe(
      (response: any) => {
        // Handle the response status
        if (response.Status === "400" || response.Status === "500") {
          this.validationMessage = 'Customer ID does not exist';
          this.messageColor = 'red';
          this.mockCustId = null;
        } else {
          this.validationMessage = 'Customer ID exists';
          this.messageColor = 'green';
          this.mockCustId = response.CustomerID;

          this.mockCustId = response.CustomerID;
          if(response.CustomerName != null)
            this.custName = response.CustomerName.trimEnd();
          if(response.Email != null)
            this.custEmail = response.Email.trimEnd();
          if(response.AddressLine1 != null)
            this.custAddr1 = response.AddressLine1.toUpperCase().trimEnd();
          if(response.AddressLine2 != null)
            this.custAddr2 = response.AddressLine2.toUpperCase().trimEnd();
          if(response.AddressLine3 != null)
            this.custAddr3 = response.AddressLine3.toUpperCase().trimEnd();
          if(response.Phone != null)
            this.custPhoneNumber = response.Phone.trimEnd();
          if (response.PostalCode != null) {
            this.postcode = response.PostalCode.trimEnd();
            // this.onPostcodeChange(this.postcode);
          }
          if (response.City != null ) {
            this.city = this.uniqueCities.some(c => c.toUpperCase() === response.City.toUpperCase())
              ? response.City.toUpperCase()
              : null;
          }
          if (response.StateName != null ) {
            this.state = this.uniqueStates.some(s => s.toUpperCase() === response.StateName.toUpperCase())
              ? response.StateName.toUpperCase()
              : null;
          }

          if(response.ContactName != null) //UsrIdentityNbr != null)
            this.entityName = response.ContactName.trimEnd();

          console.log(this.mockCustId);
          console.log(response.data);
          this.customerExist = true;

          // Ensure nbltcItemModel is defined before proceeding
          if (this.nbltcItemModel && this.nbltcItemModel.length > 0) {
            this.customerExist = true;

            // Initialize related arrays based on nbltcItemModel length
            this.quantities = Array(this.nbltcItemModel.length).fill(0);
            this.taxAmounts = Array(this.nbltcItemModel.length).fill(0);
            this.rowTotals = Array(this.nbltcItemModel.length).fill(0);
          } else {
            console.warn("nbltcItemModel is empty or undefined.");
          }

          // Fetch additional items
          this.fetchNBLTCItem();
        }
      },
      (error) => {
        console.error("Error in callCustomerAPI:", error);
        this.isLoading = false;
        this.validationMessage = 'Customer ID does not exist';
        this.mockCustId = null;
        this.messageColor = 'red';
      }
    );
  }

  validatePostcode(): void {
    // Check if the postcode length is exactly 5 and is numeric
    this.isInvalidPostcode = !(this.postcode.length === 5 && /^[0-9]+$/.test(this.postcode));
  }


  // validateAndFetch(): void {
  //   if (this.entityType === 'A' && this.entityNumber === '1') {
  //     this.validationMessage = 'Customer ID does not exist';
  //     this.messageColor = 'red';
  //   } else if (this.entityType === 'B' && this.entityNumber === '2') {
  //     this.validationMessage = 'Customer ID exists';
  //     this.messageColor = 'green';
  //     this.mockCustId = 'CUST0001'
  //     this.fetchNBLTCItem();
  //     this.customerExist = true;
  //     this.quantities = Array(this.nbltcItemModel.length).fill(0);
  //     this.taxAmounts = Array(this.nbltcItemModel.length).fill(0);
  //     this.rowTotals = Array(this.nbltcItemModel.length).fill(0);
  //   } else {
  //     this.validationMessage = null;
  //   }
  // }

  fetchNBLTCItem(): void {
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    const url = environment.apiUrl + '/api/OTCRC/v1/getnblitem';

    const Body: any = {
      i_bt_cd: this.billingTypeCode
    };

    this.http.post(url, Body, { headers }).subscribe(
      (response: any) => {
        this.nbltcItemModel = response.data;
        this.totalRecords = response.data.length > 0 ? response.data[0].total : 0;
        this.isLoading = false;
      },
      (error) => {
        console.error(error);
        this.isLoading = false;
      }
    );
  }

  calculateRowTotal(index: number): void {
    const item = this.nbltcItemModel[index];
    const quantity = this.quantities[index] || 0;
    const unitFee = item.unit_fee || 0;
    const taxPct = item.tax_pct || 0;

    // Calculate tax and total
    const taxAmount = (unitFee * quantity * taxPct) / 100;
    const total = unitFee * quantity + taxAmount;

    // Update arrays
    this.taxAmounts[index] = taxAmount;
    this.rowTotals[index] = total;

    // Recalculate grand total
    this.calculateGrandTotal();
  }

  calculateGrandTotal(): void {
    this.grandTotal = this.rowTotals.reduce((sum, total) => sum + total, 0);
  }

  clearFiles() {
    this.selectedFiles = [];
    this.selectedFilesSize = 0;
    this.isDisplayFileRequired = true;
    // console.log('sizes'+this.selectedFiles)
  }

  //upload file start
  onFileButtonClicked() {
    if (this.inputDetails === true) {
      if (this.selectedFiles.length === 0) {
        this.isDisplayFileRequired = true;
      }
      else {
        this.isDisplayFileRequired = false;
      }
    }
  }

  async onFileSelected(event: any) {
    if (event.target.files) {
      let files = event.target.files;
      let currnetFilesTotalSize = 0;

      for (let i = 0; i < files.length; i++) {
        currnetFilesTotalSize += files[i].size;
        console.log('This file size is ' + files[i].size)
        console.log('recent selected file size is ' + this.selectedFilesSize)
        // Check if the file with the same name already exists in selectedFiles
        const isDuplicate = this.selectedFiles.some((file) => file.name === files[i].name);
        console.log('Sum of file size is ' + currnetFilesTotalSize + this.selectedFilesSize)
        if (!isDuplicate && (currnetFilesTotalSize + this.selectedFilesSize) <= 10 * 1024 * 1024) {
          this.selectedFiles.push(files[i]);
          this.selectedFilesSize += files[i].size;
        } else if (isDuplicate) {
          this.showDuplicateBoxAlert('File "' + files[i].name + '" already selected. Please choose a different file.');
          // alert(`File "${files[i].name}" already selected. Please choose a different file.`);
        } else {
          this.showDuplicateBoxAlert('Total file size exceeds 10MB. Please select smaller files.');
          // alert('Total file size exceeds 10MB. Please select smaller files.');
        }
      }
      console.log('total size' + this.selectedFilesSize);

      if (this.inputDetails === true) {
        if (this.selectedFiles.length === 0) {
          this.isDisplayFileRequired = true;
        }
        else {
          this.isDisplayFileRequired = false;
        }
      }
    }
  }

  loadCounterInfo() {
   
    const permUrl = environment.apiUrl + '/api/otc/v1/checkinstatus';
    // Make the HTTP GET request
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json'
    });
    var requestBody: { [k: string]: any } = {
      i_session_id: localStorage.getItem('otcSession')
    };
    this.http.post(permUrl, requestBody, { headers }).subscribe(
      (response: any) => {
        this.counterCheckInStatus.data = response.data;
        if (this.counterCheckInStatus.data.counter_id.length > 0) {
          // this.counterTitle = 'Counter ID: ' + this.counterCheckInStatus.data.counter_id + ' | ';
          this.OTCCheckedIn = 1;
        }
      },
      (error) => {
        console.log(error);
        this.counterCheckInStatus.data = ''; //still update something to push the observer
        this.OTCCheckedIn = 0;
      }
    );
    
  }

  non_bil_no: string | null = null;
  async submitNonBilling(): Promise<void> {
    this.loadCounterInfo();
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    // Prepare non-billing body
    const Body: any = {
      i_cust_id: this.mockCustId,
      i_cust_nm: this.custName,
      i_cust_email: this.custEmail,
      i_cust_phone: this.custPhoneNumber,
      i_cust_addr_1: this.custAddr1,
      i_cust_addr_2: this.custAddr2,
      i_cust_addr_3: this.custAddr3,
      i_ent_nm: this.entityName,
      i_ent_no: this.entityNumber,
      i_ent_ty: this.entityType,
      i_req_name: this.requestorName,
      i_req_email: this.requestorEmail,
      i_non_bil_no: this.nbrunno,
      i_non_bil_desc: this.nbdesc,
      i_ret_che_no: this.chequeNo,
      i_total_bil_amt: this.grandTotal,
      i_remark: this.remarks,
      i_bil_status: 'U',
      // i_fms_admin_email: 'admin.fms@example.com',
      // i_fms_admin_nm: 'FMS Admin',
      i_fms_admin_email: null,
      i_fms_admin_nm: null,
      i_cust_postcode: this.postcode,
      i_cust_city: this.city,
      i_cust_state: this.state,
      i_void_reason: this.selectedReason,
      i_otc_body_id: this.otcBodyID,
      i_counter_id: this.counterCheckInStatus.data.counter_id,
      // i_counter_id: this.counterCheckInStatus?.data || null,
      i_che_amt: this.chequeAmt,
      i_bt_cd: this.billingTypeCode,
      i_che_id: this.chequeID,
      i_payer_nm: this.payerName,
      i_payer_email: this.payerEmail,
    };

    console.log(this.counterCheckInStatus.counterId);

    try {

      this.isLoading = true;
      // Step 1: Submit non-billing data
      console.log('Submitting non-billing data...');
      const nonBillingResponse: any = await this.http.post(environment.apiUrl + '/api/OTCRC/v1/insnonbil', Body, { headers }).toPromise();
      console.log('Non-billing data submitted successfully.', nonBillingResponse);
      const i_mtt_id = nonBillingResponse?.data[0].mtt_id;
      const i_non_bil = nonBillingResponse?.data[0].non_bil_id;
      this.non_bil_no = nonBillingResponse?.data[0].non_biling_no;

      console.log(i_mtt_id, i_non_bil, this.non_bil_no);

      // Prepare item body
      const itemBody = this.nbltcItemModel.map((item, index) => ({
        i_mft_pk: item.mft_pk,
        i_unit_fee: item.unit_fee,
        i_quantity: this.quantities[index],
        i_tax_pct: item.tax_pct,
        i_tax_amt: this.taxAmounts[index],
        i_item_total_amt: this.rowTotals[index],
        // i_non_bil_no: i_non_bil,
        i_non_bil_id: i_non_bil,
        i_mtt_id: i_mtt_id
      }));

      // Step 2: Submit non-billing item data
      console.log('Submitting non-billing item data...');
      await this.http.post(environment.apiUrl + '/api/OTCRC/v1/insnonbilitem', itemBody, { headers }).toPromise();
      console.log('Non-billing item data submitted successfully.');

      // Step 3: Upload files using readFileAsync()
      console.log('Uploading files...');
      const fileUploadSuccess = await this.readFileAsync();
      if (!fileUploadSuccess) {
        this.showDuplicateBoxAlert('Some files failed to upload. Please check and try again.');
        // alert('Some files failed to upload. Please check and try again.');
        return;
      }
      this.isLoading = false;
      this.routeToNonBillingDetails();
      // this.router.navigate(['/otc-returned-cheque']);
    } catch (error) {
      console.error('Error during submission:', error);
      this.isLoading = false;
      this.showDuplicateBoxAlert('Error occurred while submitting data. Please try again.');
      // alert('Error occurred while submitting data. Please try again.');
    }
  }

  // File upload logic with readFileAsync
  async readFileAsync(): Promise<boolean> {
    let result = true;

    // Upload Supporting Documents
    for (const file of this.selectedFiles) {
      const success = await this.uploadFile(file, 'Supporting Documents');
      if (!success) result = false;
    }

    // Upload RCA File
    if (this.selectedRcaFile) {
      const success = await this.uploadFile(this.selectedRcaFile, 'RCA');
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
      i_file_category: fileCategory, // Supporting Documents or RCA
      i_non_bil_no: this.non_bil_no
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

  onSupportingFilesSelected(event: any): void {
    const files: FileList = event.target.files;
    let currentFilesTotalSize = this.selectedFilesSize;

    for (let i = 0; i < files.length; i++) {
      const file = files[i];

      if (currentFilesTotalSize + file.size > 10 * 1024 * 1024) {
        this.showDuplicateBoxAlert('Total file size exceeds 10MB. Please select smaller files.');
        // alert('Total file size exceeds 10MB. Please select smaller files.');
        continue;
      }

      if (!this.selectedFiles.some((f) => f.name === file.name)) {
        this.selectedFiles.push(file);
        currentFilesTotalSize += file.size;
      } else {
        this.showDuplicateBoxAlert(`File "${file.name}" already selected.`);
        // alert(`File "${file.name}" already selected.`);
      }
    }

    this.selectedFilesSize = currentFilesTotalSize;
  }

  onRcaFileSelected(event: any): void {
    const file: File = event.target.files[0];

    if (file) {
      if (file.size > 10 * 1024 * 1024) {
        this.showDuplicateBoxAlert('File size exceeds 10MB. Please select a smaller file.');
        // alert('File size exceeds 10MB. Please select a smaller file.');
        return;
      }

      this.selectedRcaFile = file;
      this.selectedRcaFileSize = file.size;
    }
  }

  clearSupportingFiles(): void {
    this.selectedFiles = [];
    this.selectedFilesSize = 0;
    if (this.supportingFilesInput) {
      this.supportingFilesInput.nativeElement.value = '';
    }
  }

  clearRcaFile(): void {
    this.selectedRcaFile = null;
    this.selectedRcaFileSize = 0;
    // Reset the RCA file input element
    if (this.rcaFileInput) {
      this.rcaFileInput.nativeElement.value = '';
    }
  }

  isFormValid(): boolean {
    // Check all required fields
    const isCustomerEntityInformationValid  = 
    !!this.mockCustId &&
    !!this.custName &&
    !!this.custEmail &&
    !!this.custPhoneNumber &&
    !!this.custAddr1 &&
    !!this.custAddr2 &&
    !!this.postcode &&
    !!this.city &&
    !!this.state &&
    !!this.entityType &&
    !!this.entityNumber &&
    !!this.entityName

    const isNonBillingInfoValid =
      !!this.chequeNo &&
      !!this.chequeAmt &&
      !!this.nbrunno &&
      !!this.nbdesc;

    const isOthersValid =
      //!!this.requestorName &&
      //!!this.requestorEmail &&
      !!this.payerName &&
      !!this.payerEmail;

    const isListValid =
      this.nbltcItemModel.length > 0 &&
      this.nbltcItemModel.every((item, index) => item.unit_fee && this.quantities[index] >= 0);

    const isFilesValid = !!this.selectedRcaFile;

    const isReasonValid = !!this.selectedReason;

    return (
      isCustomerEntityInformationValid &&
      isNonBillingInfoValid &&
      isOthersValid &&
      isListValid &&
      isFilesValid &&
      isReasonValid
    );
  }

  cancel(): void {
    this.router.navigate(['/otc-returned-cheque']);
  }

  preventTyping(event: KeyboardEvent): void {
    // Prevent typing any key other than navigation keys
    const allowedKeys = ['Backspace', 'ArrowLeft', 'ArrowRight', 'Delete', 'Tab']; // Allow navigation and delete keys
    if (!allowedKeys.includes(event.key)) {
      event.preventDefault();
    }
  }

  loadStates() {
    this.ParamService.getStates('1', '100', '', 'State').subscribe(
      (response: any) => {
        if (response && response.data && Array.isArray(response.data)) {
          this.states = response.data as ParamData[];
          //this.states.push({ param_cd: '', nm_en: 'All', nm_bm: 'All', total: 5 }); //add 'All' options
          // this.states.push(response.data);
          // this.states = [...this.states, ...response.data];
          //this.states.sort((a, b) => a.nm_en.localeCompare(b.nm_en)); // sorting
        } else {
          console.error('Invalid response format:', response);
        }
      },
      (error) => {
        console.error('There was an error retrieving the status:', error);
      }
    );
  }

  loadEntityType() {
    this.ParamService.getStates('1', '100', '', 'EntityType').subscribe(
      (response: any) => {
        if (response && response.data && Array.isArray(response.data)) {
          this.entityTypeDropdown = response.data as ParamData[];
        } else {
          console.error('Invalid response format:', response);
        }
      },
      (error) => {
        console.error('There was an error retrieving the status:', error);
      }
    );
  }

  routeToNonBillingDetails() {
    this.isLoading = true;
    const url = environment.apiUrl + '/api/OTCRC/v1/getnonbillinglisting';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    const Body: any = {
      i_page: '1',
      i_size: '10',
      i_ent_nm: this.entityName,
      i_ent_no: this.entityNumber,
      i_cust_id: this.mockCustId,
      i_non_bil_no: this.non_bil_no,
      i_bil_status: 'U'
    };

    console.log(Body);

    this.http.post(url, Body, { headers }).subscribe(
      (response: any) => {
        this.model = response.data;
        if (response.data && response.data.length > 0) {
          const item = response.data[0];

          this.router.navigate(['/non-billing-details', this.nbrunno], {
            queryParams: { bill_status: "Unpaid" },
            state: { item }
          });
        } else {
          console.error("No data returned from API");
        }
        console.log(response.data);
        console.log(this.totalRecords);
      },
      (error) => {
        console.error(error);
        this.isLoading = false;
      }
    );
  }

   // Postcode Start
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

// Postcode End

  isPhoneInvalid: boolean = false;

  checkPhoneLength(): void {
    const phoneLength = this.custPhoneNumber?.length || 0;
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
    const entityNumberLength = this.entityNumber?.length || 0;
    this.isEntityNoTooLong = entityNumberLength < 5 || entityNumberLength > 30;
  }

  validateChequeAndTotal() {
    if (this.chequeAmt !== this.grandTotal) {
      return false;
    }
    return true;
  }
}