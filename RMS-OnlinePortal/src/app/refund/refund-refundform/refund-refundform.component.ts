import { HttpClient, HttpHeaders } from '@angular/common/http';
import { ChangeDetectorRef, Component, OnInit, ViewChild } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { AbstractControl, ValidationErrors, FormBuilder, FormGroup, FormArray, Validators, NgModel } from '@angular/forms';
import { TranslateService } from '@ngx-translate/core';
import { NgbPaginationConfig } from '@ng-bootstrap/ng-bootstrap';
import { environment } from 'src/environments/environment';
import { GlobalService } from 'src/app/shared/global.service';
import { AuthService } from 'src/app/services/auth.service';
import { Location } from '@angular/common';
import { upload } from 'ngx-bootstrap-icons';
import { ParamService } from 'src/app/core/services/param.service';
import { OTCBank } from 'src/app/models/otc-collection-returned-cheque.interface';
import { PostCodeData } from 'src/app/models/postcode.interface';


@Component({
  selector: 'app-refund-refundform',
  templateUrl: './refund-refundform.component.html',
  styleUrls: ['./refund-refundform.component.scss']
})
export class RefundRefundformComponent implements OnInit {
  isLoading: boolean = false;
  RefundInfoForm!: FormGroup;
  totalGrossAmount: number = 0;
  totalRecords: number = 0;
  submitted = false;
  //fetch bank information
  bankmodel: OTCBank[] = [];
  //fetch state information
  states: any[] = [];



  // Receipt & Supporting Documents
  uploadedFiles: Array<{
    fileName: string;
    file: File | null;
    fileContent?: string; // Add the optional fileContent property
    fileSize?: number;
  }> = [
      { fileName: '', file: null, fileContent: '', fileSize: 0 } // Initialize with default values
    ];
  fileTypeList = ['Invoice', 'Receipt', 'Supporting Document'];


  // Payee Bank Information
  payeeBank = {
    bankName: '',
    accountNo: '',
    accountHolderName: ''
  };
  bankList = ['Maybank', 'CIMB', 'Public Bank', 'RHB', 'Hong Leong Bank'];

  showInsertAlert: boolean = false;
  alertMessage: string = '';
  alertClass: string = '';
  postcode: string | null = null;
  city: string | null = null;
  state: string | null = null;
  totalPostCodeRecords: number = 0;
  postCodes: PostCodeData[] = [];
  uniqueCities: string[] = [];
  uniqueStates: string[] = [];


  constructor(
    private http: HttpClient,
    private router: Router,
    private route: ActivatedRoute,
    private fb: FormBuilder,
    private translate: TranslateService,
    private globalService: GlobalService,
    private authService: AuthService,
    private cd: ChangeDetectorRef,
    config: NgbPaginationConfig,
    private ParamService: ParamService,
    private location: Location,
  ) {
    config.maxSize = environment.PaginationMaxSize;
    config.boundaryLinks = true;

    // Set the default language for translation
    this.translate.setDefaultLang(this.globalService.getGlobalValue());
    this.translate.use(this.globalService.getGlobalValue());
  }

  ngOnInit(): void {

    // Initialize the refund info form
    this.loadStates();
    this.fetchBanks();

    this.initializeRefundInfoForm();
    this.paymentItems.valueChanges.subscribe(() => {
      this.calculateTotalGrossAmount();
    });



  }

  // Initialize the form group with validation
  initializeRefundInfoForm() {
    this.RefundInfoForm = this.fb.group({
      // Customer Information
      customerName: ['', Validators.required],
      customerEmail: ['', [Validators.required, Validators.email]],
      customerPhoneNumber: ['', [
        Validators.required,
        Validators.minLength(10),
        Validators.maxLength(15),
        Validators.pattern('^[0-9]+$')
      ]],
      customerAddress1: ['', Validators.required],
      customerAddress2: [''],
      customerAddress3: [''],
      postcode: ['', [Validators.required, Validators.pattern(/^\d{5}$/)]], // 5 digits
      city: ['', Validators.required],
      state: ['', Validators.required],

      // Receipt Information
      receiptNo: ['', Validators.required],
      receiptAmount: ['', [Validators.required, Validators.pattern("^[0-9]+(\.[0-9]{1,2})?$")]],
      orderReferenceNo: ['', Validators.required],
      transactionId: [''],
      entityName: ['', Validators.required],
      entityType: ['', Validators.required],
      entityNo: ['', [
        Validators.required,
        Validators.minLength(5),
        Validators.maxLength(30)
      ]],

      // Payment Items
      paymentItems: this.fb.array(
        [this.createPaymentItem()],
        minValidItems(1)
      ),
      // Dynamically added rows for payment items
      uploadedFiles: this.fb.array(
        [this.createFileGroup()],        // start with 1 row
        minValidItems(1)                        // require ≥1
      ),

      // Payee Bank Information
      bankName: ['', Validators.required],
      accountNo: ['', [Validators.required, Validators.pattern(/^\d+$/)]], // Only digits
      accountHolderName: ['', Validators.required],
      identityNumber: ['', [Validators.required, Validators.pattern(/^[a-zA-Z0-9]+$/)]],
      identityType: ['', Validators.required], // e.g., BRN, Passport, Old NRIC

      // Other Information
      remarks: ['', Validators.required]
    });

    // Add one payment item row by default
    //this.addCheque();
    this.loadPostcode();
  }


private createPaymentItem(): FormGroup {
    const fg = this.fb.group({
      itemDescription: ['', Validators.required],
      quantity: [1, [Validators.required, Validators.min(1)]],
      amount: [1, [Validators.required, Validators.min(0.01), Validators.pattern(/^\d+(\.\d{1,2})?$/)]],
      tax: [0, [Validators.min(0), Validators.pattern(/^\d+(\.\d{1,2})?$/)]], // Add min validator
      discount: [0, [Validators.min(0), Validators.pattern(/^\d+(\.\d{1,2})?$/)]], // Add min validator
      incentiveCode: [''],
      grossAmount: [1],
      netAmount: [1]
    });

    fg.valueChanges.subscribe(vals => {
      // Sanitize values - convert invalid inputs to 0
      const quantity = Number(vals.quantity) || 0;
      const amount = Number(vals.amount) || 0;
      const tax = Number(vals.tax) || 0;
      const discount = Number(vals.discount) || 0;

      const gross = quantity * amount;
      const FixedGross = parseFloat(gross.toFixed(2));

      const net = gross + tax - discount;
      const FixedNet = parseFloat(net.toFixed(2));

      fg.patchValue({ grossAmount: FixedGross, netAmount: FixedNet }, { emitEvent: false });
      this.calculateTotalGrossAmount();
    });

    return fg;
  }

  // helper to build one file-upload row
  private createFileGroup(): FormGroup {
    return this.fb.group({
      file: [null, Validators.required],
      fileName: ['', Validators.required],
      fileContent: ['']
    });
  }


  // Getter for payment items FormArray
  get paymentItems(): FormArray {
    return this.RefundInfoForm.get('paymentItems') as FormArray;
  }

  get uploadedFilesArray() {
    return this.RefundInfoForm.get('uploadedFiles') as FormArray;
  }

  // Add a new payment item row (Cheque)
  addCheque(): void {
    const paymentItem = this.fb.group({
      itemDescription: ['', Validators.required],
      quantity: [1, [Validators.required, Validators.min(1)]],
      amount: [1, [Validators.required, Validators.min(0.01), Validators.pattern(/^\d+(\.\d{1,2})?$/)]],
      tax: [0], // Default tax to 0
      incentiveCode: [''],
      discount: [0],
      grossAmount: [1, Validators.required], // Remove 'disabled' and make it a regular form control
      netAmount: [1, Validators.required] // Add netAmount field
    });

    // Calculate grossAmount dynamically
    paymentItem.valueChanges.subscribe(values => {
      const { quantity, amount, tax, discount } = values;
      const calculatedGrossAmount =
        (quantity || 0) * (amount || 0);
      const FixedCalculatedGrossAmount = parseFloat(calculatedGrossAmount.toFixed(2)); // Ensure 2 decimal places
      paymentItem.get('grossAmount')?.setValue(FixedCalculatedGrossAmount, { emitEvent: false });

      const calculatedNetAmount =
        (quantity || 0) * (amount || 0) + (tax || 0) - (discount || 0);
      const FixedCalculatedNetAmount = parseFloat(calculatedNetAmount.toFixed(2)); // Ensure 2 decimal places
      console.log('Calculated Net Amount:', FixedCalculatedNetAmount);
      paymentItem.get('netAmount')?.setValue(FixedCalculatedNetAmount, { emitEvent: false });
    });

    this.paymentItems.push(paymentItem);

    // Update total gross amount whenever paymentItems change
    this.paymentItems.valueChanges.subscribe(() => {
      this.calculateTotalGrossAmount();
    });
  }

  calculateTotalGrossAmount(): void {
    this.totalGrossAmount = this.paymentItems.controls.reduce((total, control) => {
      const netAmount = parseFloat(control.get('netAmount')?.value || 0);
      return total + netAmount;
    }, 0);
  }

  // Remove a payment item row
  removeItem(index: number): void {
    this.paymentItems.removeAt(index);
  }

  clampDiscount(index: number) {
    const fg = this.paymentItems.at(index);
    const gross = fg.get('grossAmount')!.value || 0;
    const ctrl = fg.get('discount')!;

    if (ctrl.value > gross) {
      // <-- drop the emitEvent:false so your valueChanges re-runs
      ctrl.setValue(gross);
    }
  }


  // Add a new file row
  addFileRow(): void {
    this.uploadedFiles.push({ fileName: '', file: null });
    this.uploadedFilesArray.push(this.createFileGroup());
  }

  // Handle file selection and encode to Base64
  // In your component…
  readonly MAX_SINGLE_FILE_SIZE = 5 * 1024 * 1024;   // 5 MB
  readonly MAX_TOTAL_FILE_SIZE = 10 * 1024 * 1024;  // e.g. total of 10 MB
  SUPPORTED_EXTS = '.pdf,.doc,.docx,.jpeg,.jpg,.png';

  // onFileSelect(event: Event, index: number): void {
  //   const input = event.target as HTMLInputElement;
  //   if (!input.files?.length) { return; }

  //   const file = input.files[0];


  //   if (file.size > this.MAX_SINGLE_FILE_SIZE) {
  //     this.alertMessage = `"${file.name}" exceeds the 5 MB per-file limit.`;
  //     this.alertClass = 'alert alert-danger PA-alert-box';
  //     this.showInsertAlert = true;

  //     // auto-hide after 3 seconds
  //     setTimeout(() => {
  //       this.showInsertAlert = false;
  //     }, 3000);

  //     return;
  //   }

  //   // 2) Your existing type check…
  //   const allowedTypes = [
  //     'application/pdf',
  //     'application/msword',
  //     'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
  //     'image/jpeg',
  //     'image/png'
  //   ];
  //   if (!allowedTypes.includes(file.type)) {
  //     this.alertMessage = 'This file type is not allowed. Please upload a PDF, DOC, DOCX, JPEG, or PNG file.';
  //     this.alertClass = 'alert alert-danger PA-alert-box';
  //     this.showInsertAlert = true;

  //     // auto-hide after 3 seconds
  //     setTimeout(() => {
  //       this.showInsertAlert = false;
  //     }, 3000);

  //     return;
  //   }

  //   // 3) Total size check (still 10 MB across all files)
  //   const currentTotal = this.uploadedFiles
  //     .reduce((sum, f) => sum + (f.file?.size || 0), 0);
  //   if (currentTotal + file.size > this.MAX_TOTAL_FILE_SIZE) {
  //     alert("Total of all uploaded files cannot exceed 10 MB.");
  //     return;
  //   }

  //   // 4) If you get here, it’s valid—proceed with your FileReader
  //   this.uploadedFiles[index].file = file;
  //   this.uploadedFiles[index].fileName = file.name;
  //   this.uploadedFiles[index].fileSize = file.size;

  //   const reader = new FileReader();
  //   reader.onload = () => {
  //     const base64 = (reader.result as string).split(',')[1];
  //     this.uploadedFilesArray.at(index).patchValue({
  //       file, fileName: file.name, fileContent: base64
  //     });
  //     this.uploadedFiles[index].fileContent = base64;
  //     input.value = '';
  //   };
  //   reader.onerror = err => console.error("Error reading file:", err);
  //   reader.readAsDataURL(file);
  // }

  onFileSelect(event: Event, index: number): void {
    const input = event.target as HTMLInputElement;
    if (!input.files?.length) { return; }

    const file = input.files[0];

    // ✅ Sanitize and validate filename
    const sanitizedFileName = this.sanitizeFileName(file.name, 100);
    if (!sanitizedFileName) {
      this.alertMessage = `The file name is too long. Maximum length is 100 characters.`;
      this.alertClass = 'alert alert-danger PA-alert-box';
      this.showInsertAlert = true;
      setTimeout(() => this.showInsertAlert = false, 3000);
      return;
    }

    // ✅ Extract extension
    const fileExt = sanitizedFileName.split('.').pop()?.toLowerCase();
    const allowedExts = ['pdf', 'doc', 'docx', 'jpg', 'jpeg', 'png'];
    const allowedTypes = [
      'application/pdf',
      'application/msword',
      'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
      'image/jpeg',
      'image/png'
    ];

    // 1) Single file size check
    if (file.size > this.MAX_SINGLE_FILE_SIZE) {
      this.alertMessage = `"${sanitizedFileName}" exceeds the 5 MB per-file limit.`;
      this.alertClass = 'alert alert-danger PA-alert-box';
      this.showInsertAlert = true;
      setTimeout(() => this.showInsertAlert = false, 3000);
      return;
    }

    // 2) Extension check
    if (!fileExt || !allowedExts.includes(fileExt)) {
      this.alertMessage = `Invalid file extension ".${fileExt}". Allowed: ${allowedExts.join(', ')}`;
      this.alertClass = 'alert alert-danger PA-alert-box';
      this.showInsertAlert = true;
      setTimeout(() => this.showInsertAlert = false, 3000);
      return;
    }

    // 3) MIME type check
    if (!allowedTypes.includes(file.type)) {
      this.alertMessage = 'This file type is not allowed. Please upload a PDF, DOC, DOCX, JPEG, or PNG file.';
      this.alertClass = 'alert alert-danger PA-alert-box';
      this.showInsertAlert = true;
      setTimeout(() => this.showInsertAlert = false, 3000);
      return;
    }

    // 4) Total size check (10 MB across all files)
    const currentTotal = this.uploadedFiles
      .reduce((sum, f) => sum + (f.file?.size || 0), 0);
    if (currentTotal + file.size > this.MAX_TOTAL_FILE_SIZE) {
      alert("Total of all uploaded files cannot exceed 10 MB.");
      return;
    }

    // ✅ Safe to proceed
    this.uploadedFiles[index].file = file;
    this.uploadedFiles[index].fileName = sanitizedFileName;
    this.uploadedFiles[index].fileSize = file.size;

    const reader = new FileReader();
    reader.onload = () => {
      const base64 = (reader.result as string).split(',')[1];
      this.uploadedFilesArray.at(index).patchValue({
        file,
        fileName: sanitizedFileName,
        fileContent: base64
      });
      this.uploadedFiles[index].fileContent = base64;
      input.value = '';
    };
    reader.onerror = err => console.error("Error reading file:", err);
    reader.readAsDataURL(file);
  }


  //  Sanitizer function
  private sanitizeFileName(fileName: string, maxLength: number = 100): string | null {
    // Step 1: Allow only safe characters
    let safeName = fileName.replace(/[^a-zA-Z0-9._-]/g, '_');

    // Step 2: Replace multiple dots with a single dot
    safeName = safeName.replace(/\.{2,}/g, '.');

    // Step 3: Remove leading and trailing dots
    safeName = safeName.replace(/^\.+|\.+$/g, '');

    // Step 4: Enforce max length
    if (safeName.length > maxLength) {
      return null; // return null so caller can handle validation error
    }

    return safeName;
  }



  formatBytes(bytes: number, decimals = 2): string {
    const k = 1024;
    const sizes = ['B', 'KB', 'MB', 'GB', 'TB'];
    if (bytes === 0) {
      // force “0.00 MB” – or pick whichever unit you prefer
      return (0).toFixed(decimals) + ' ' + sizes[2];
    }
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return parseFloat((bytes / Math.pow(k, i)).toFixed(decimals)) + ' ' + sizes[i];
  }

  // how much total you’ve used so far
  get totalUsed(): number {
    return this.uploadedFiles
      .reduce((sum, f) => sum + (f.fileSize || 0), 0);
  }

  // remaining space under your TOTAL cap
  get remainingSpace(): number {
    return Math.max(this.MAX_TOTAL_FILE_SIZE - this.totalUsed, 0);
  }



  // Remove a file row
  removeFile(index: number): void {
    this.uploadedFilesArray.removeAt(index);
    this.uploadedFiles.splice(index, 1);
  }

  // Validate the form before submission
  isFormValid(): boolean {
    const isRefundInfoValid = this.RefundInfoForm?.valid ?? false;
    console.log('Refund Info Form Valid:', isRefundInfoValid, this.RefundInfoForm.errors);

    const areFilesValid = this.uploadedFiles?.length > 0 &&
      this.uploadedFiles.every(file => Boolean(file.file && file.fileName?.trim()));
    console.log('Uploaded Files Valid:', areFilesValid);



    return isRefundInfoValid && areFilesValid;
  }


  get paymentItemsArray(): FormArray {
    return this.RefundInfoForm.get('paymentItems') as FormArray;
  }

  // Handle form submission
  async onSubmit(): Promise<void> {

    this.isLoading = true;
    this.submitted = true;

    // Mark all fields as touched to show errors
    this.RefundInfoForm.markAllAsTouched();
    
    // Check payment items specifically
    this.paymentItems.controls.forEach(control => {
      control.markAllAsTouched();
    });


    if (!this.isFormValid()) {
      this.alertMessage = 'Form is invalid. Please check the form fields.';
      this.alertClass = 'alert alert-danger PA-alert-box';
      this.showInsertAlert = true;

      // auto-hide after 3 seconds
      setTimeout(() => {
        this.showInsertAlert = false;
      }, 3000);
      
      this.isLoading = false;
      return;
    }

    // Check total file size of all uploaded files
    const MAX_TOTAL_SIZE = 10 * 1024 * 1024; // 10 MB in bytes
    const totalFileSize = this.uploadedFiles.reduce((total, fileItem) => {
      return total + (fileItem.file ? fileItem.file.size : 0);
    }, 0);

    if (totalFileSize > MAX_TOTAL_SIZE) {
      alert("Total file size cannot exceed 10 MB.");
      this.isLoading = false;
      return;
    }

    // Map the uploaded files into the desired format
    const formattedFiles = this.uploadedFiles.map(fileItem => {
      const file = fileItem.file; // Access the File object
      return {
        file_nm: fileItem.fileName, // File name
        file_content: fileItem.fileContent, // Base64 string
        file_type: file ? file.type : '', // MIME type of the file
        file_size_kb: file ? Math.ceil(file.size / 1024) : 0 // Convert size from bytes to kilobytes
      };
    });

    // Prepare form data
    const formData = {
      ...this.RefundInfoForm.value,
      uploadedFiles: formattedFiles, // Use the formatted files
    };

    // Extract values from BankInfoForm
    const RefundformData = this.RefundInfoForm.value;

    const paymentItemDetails = (this.RefundInfoForm.get('paymentItems') as FormArray).value.map((item: any) => {
      const unitFee = parseFloat(item.amount); // Ensure unit_fee is a number
      const taxAmt = parseFloat(item.tax); // Ensure tax_amt is a number
      const taxPct = unitFee ? (taxAmt / unitFee) * 100 : 0; // Calculate tax_pct, avoid division by zero

      return {
        unit_fee: unitFee,
        qty: item.quantity,
        item_ref_no: 'NON-RMS',
        item_desc: item.itemDescription,
        tax_pct: taxPct, // Add the calculated tax_pct
        tax_amt: taxAmt,
        grant_cd: item.incentiveCode,
        disc_amt: item.discount,
        gross_amt: item.grossAmount,
        net_amt: item.netAmount,
      };
    });


    console.log('Mapped Payment Item Details:', paymentItemDetails);

    const body: any = {
      rcpt_no: RefundformData.receiptNo,
      rcpt_amt: RefundformData.receiptAmount,
      rcpt_date: null,  //empty
      orn_no: RefundformData.orderReferenceNo,
      txn_id: RefundformData.transactionId,
      refund_amt: this.totalGrossAmount,
      ent_no: RefundformData.entityNo,
      ent_nm: RefundformData.entityName,
      ent_ty: RefundformData.entityType,
      cust_email: RefundformData.customerEmail,
      cust_nm: RefundformData.customerName,
      cust_phone: RefundformData.customerPhoneNumber,
      msg: RefundformData.remarks,
      sme_email: null,                //this is for SME user role assign
      assign_to: null,                  //this is for SME user role assign
      rtt_status: 'PFA',              // set for rtt_status
      refund_ty: 'RF',                // refund type
      refund_reason: null,            // set at Finance Admin side
      // Attach payment items

      payment_item_details: paymentItemDetails,
      uploadedFiles: formattedFiles, // Use the formatted files

      // Add bank information
      identity_type: RefundformData.identityType,
      identity_number: RefundformData.identityNumber,
      bank_account_no: RefundformData.accountNo,
      bank_account_name: RefundformData.bankName,
      bank_account_type: null, //empty
      bank_holder_name: RefundformData.accountHolderName,
      billing_address_1: RefundformData.customerAddress1,
      billing_address_2: RefundformData.customerAddress2,
      billing_address_3: RefundformData.customerAddress3,
      city: RefundformData.city,
      postcode: RefundformData.postcode,
      state: RefundformData.state,
      rec_email: RefundformData.customerEmail,

    };

    console.log(body);

    //return;
    // Prepare headers for the API call
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    // Define the API endpoint
    const url = `${environment.apiUrl}/api/refundl/v1/addrttwf_rf`;

    // Make the API call
    try {
      const response: any = await this.http.post(url, body, { headers }).toPromise();

      // Check the "data" field
      if (response.data === -1) {
        console.log('Success:', response);

        this.alertMessage = "Reference No. cannot be null or empty";
        this.alertClass = "alert alert-danger PA-alert-box";
        this.showInsertAlert = true;
        this.isLoading = false;

        // Optionally, hide the alert after a few seconds
        setTimeout(() => {
          this.showInsertAlert = false;
        }, 10000);
        return;
      }

      if (response.data === -3) {
        console.log('Success:', response);

        this.alertMessage = "This Reference No. cannot be submitted — it is either still processing or has already reached the maximum of 3 appeals.";
        this.alertClass = "alert alert-danger PA-alert-box";
        this.showInsertAlert = true;
        this.isLoading = false;

        // Optionally, hide the alert after a few seconds
        setTimeout(() => {
          this.showInsertAlert = false;
        }, 10000);
        return;
      }

      if (response.data > 0) {
        console.log('Success:', response);
        // Set the alert properties for a successful submission
        this.alertMessage = 'Refund request submitted successfully.';
        this.alertClass = 'alert alert-success PA-alert-box';
        this.showInsertAlert = true;
        this.isLoading = false;
      }


      // Optionally, redirect after a short delay
      setTimeout(() => {
        this.showInsertAlert = false;
        this.location.back();
      }, 3000);

    } catch (error: any) {
      console.error('Error:', error);
      this.isLoading = false;

      // Default error message in case no custom message is returned
      let errMsg = 'An error occurred while submitting the refund request. Please try again.';

      // Check if error contains your custom error message from backend
      // Depending on your implementation, the error response might be in error.error
      if (error?.error?.header?.message) {
        errMsg = error.error.header.message;
      }

      // Set the alert properties with the (custom) error message
      this.alertMessage = errMsg;
      this.alertClass = 'alert alert-danger PA-alert-box';
      this.showInsertAlert = true;

      // Optionally, hide the alert after a few seconds
      setTimeout(() => {
        this.showInsertAlert = false;
      }, 10000);
    }

        // catch (error: any) {
    //   this.isLoading = false;

    //   let errMsg = 'An error occurred while submitting the refund request. Please try again.';
    //   if (error?.error?.header?.message) {
    //     errMsg = error.error.header.message;
    //   }


    //   // ✅ If unauthorized, show alert for 3 seconds, then redirect
    //   if (error.status === 401 || errMsg === 'Unauthorized') {

    //     // Set alert first
    //     this.alertMessage = "Session has expired. Redirecting to home page in 5 seconds...";
    //     this.alertClass = 'alert alert-danger PA-alert-box';
    //     this.showInsertAlert = true;

    //     setTimeout(() => {
    //       this.showInsertAlert = false;
    //       this.router.navigate(['/home']); // or ['/login']
    //     }, 5000);

    //     return; // stop further execution
    //   }


    //   // Set alert first
    //   this.alertMessage = errMsg;
    //   this.alertClass = 'alert alert-danger PA-alert-box';
    //   this.showInsertAlert = true;

    //   // For all other errors: hide alert after 10 seconds
    //   setTimeout(() => {
    //     this.showInsertAlert = false;
    //   }, 10000);
    // }

  }

  resetFileInput(event: MouseEvent): void {
    const input = event.target as HTMLInputElement;
    // Clear the old value so that selecting the same file again will trigger `change`
    input.value = '';
  }


  onCancel() {
    this.location.back();
  }
  // Reset the form
  resetForm(): void {
    this.RefundInfoForm.reset();
    // Clear the payment items array
    while (this.paymentItems.length > 0) {
      this.paymentItems.removeAt(0);
    }
    // Add one default payment item row
    this.addCheque();
  }

  // Patch the form with existing data
  patchForm(data: any): void {
    this.RefundInfoForm.patchValue({
      customerName: data.customerName || '',
      customerEmail: data.customerEmail || '',
      customerPhoneNumber: data.customerPhoneNumber || '',
      customerAddress1: data.customerAddress1 || '',
      customerAddress2: data.customerAddress2 || '',
      customerAddress3: data.customerAddress3 || '',
      postcode: data.postcode != null ? data.postcode : null,
      city: data.city != null ? data.city : null,
      state: data.state != null ? data.state : null,
      receiptNo: data.receiptNo || '',
      receiptAmount: data.receiptAmount || '',
      orderReferenceNo: data.orderReferenceNo || '',
      transactionId: data.transactionId || '',
      entityName: data.entityName || '',
      entityType: data.entityType || '',
      entityNo: data.entityNo || ''
    });

    // Clear existing payment items and patch new ones
    while (this.paymentItems.length > 0) {
      this.paymentItems.removeAt(0);
    }

    if (data.paymentItems && data.paymentItems.length > 0) {
      data.paymentItems.forEach((item: any) => {
        this.paymentItems.push(
          this.fb.group({
            itemDescription: item.itemDescription || '',
            quantity: item.quantity || '',
            amount: item.amount || '',
            tax: item.tax || '',
            incentiveCode: item.incentiveCode || '',
            discount: item.discount || '',
            grossAmount: item.grossAmount || ''
          })
        );
      });
    }
  }



  fetchBanks(): void {
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    const url = environment.apiUrl + '/api/rms/v1/getbanks';

    const Body: any = {
    };

    this.http.post(url, Body, { headers }).subscribe(
      (response: any) => {
        this.bankmodel = response.data;
        this.totalRecords = response.data.length > 0 ? response.data[0].total : 0;
        this.isLoading = false;
        console.log(this.bankmodel);
      },
      (error) => {
        console.error(error);
        this.isLoading = false;
      }
    );

  }


  loadStates() {
    this.ParamService.getStates('1', '100', '', 'State').subscribe(
      (response: any) => {
        if (response && response.data && Array.isArray(response.data)) {
          this.states = response.data as any[];
          console.log(this.states);
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

  loadPostcode() {
    // Subscribe to changes on the postcode control to auto-populate city and state.
    this.RefundInfoForm.get('postcode')?.valueChanges.subscribe(selectedPostcode => {
      this.onPostcodeChange(selectedPostcode);
    });

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
        //console.log(this.postCodes);
      },
      (error) => {
        console.error('There was an error retrieving the postcode:', error);
      }
    );

  }

  //postcode start
  onPostcodeChange(selectedPostcode: string | null) {
    if (!selectedPostcode) {
      // Reset city and state when postcode is cleared.
      this.RefundInfoForm.patchValue({ city: null, state: null });
      return;
    }

    const match = this.postCodes.find(p => String(p.postcode) === selectedPostcode);
    this.RefundInfoForm.patchValue({
      city: match ? match.city : null,
      state: match ? match.state : null
    });
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


  checkTag = (term: string): string | null => {
    if (/^\d{1,5}$/.test(term)) {
      return term; // ensure 1–5 digit number
    }

    return null;
  };

  compareByString(a: any, b: any): boolean {
    return String(a) === String(b);
  }

  filterToDigits(event: Event) {
    const input = event.target as HTMLInputElement;
    // strip out any non‑digit characters
    const digits = input.value.replace(/\D/g, '');
    // update both the input’s displayed value and the form control
    input.value = digits;
    this.RefundInfoForm.get('customerPhoneNumber')!.setValue(digits);
  }


sanitizeNumericInput(event: Event, index: number, fieldName: string): void {
  const input = event.target as HTMLInputElement;
  const value = input.value;
  
  // Remove any non-numeric characters except decimal point
  const sanitized = value.replace(/[^\d.]/g, '');
  
  // Ensure only one decimal point
  const parts = sanitized.split('.');
  const cleaned = parts.length > 2 
    ? parts[0] + '.' + parts.slice(1).join('') 
    : sanitized;
  
  // Update the form control
  const control = this.paymentItems.at(index).get(fieldName);
  control?.setValue(cleaned ? parseFloat(cleaned) : 0, { emitEvent: true });
}

}

export function minValidItems(min: number) {
  return (control: AbstractControl): ValidationErrors | null => {
    if (!(control instanceof FormArray)) {
      return null;
    }
    // Count how many child groups are valid
    const validCount = control.controls.filter(c => c.valid).length;
    return validCount >= min
      ? null
      : { minValidItems: { required: min, actual: validCount } };
  };
}


