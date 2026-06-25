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
import { RefundPTTOrderDetails, RefundPTTPaymentItemDetails, RefundPTTOnlinePaymentInfos, PGRcpt, RefundInfo, RefundHist, RefundForm } from 'src/app/core/models/refundptt-interface';
import { OTCCollectionReceiptingBankDraft, OTCCollectionReceiptingCheque, OTCCollectionReceiptingMoneyOrder, OTCPaymentModel, OTCPaymentDetails, OTCPaymentHeader, OTCRcpt, OTCEMV } from 'src/app/core/models/otc-collection-receipting.interface';
import { OTCBank } from 'src/app/core/models/otc-collection-returned-cheque.interface';
import { Roles, UserRole } from 'src/app/core/models/entity';
import { FormBuilder, FormControl, FormGroup, NgForm, NgModel, Validators, FormArray } from '@angular/forms';
import { RefundApprovalTaskInfo, RefundRTTItems } from 'src/app/core/models/refundapproval-interface';
import { Location } from '@angular/common';
import { ActionMappingService } from 'src/app/core/services/action-mapping.service';
import { PostCodeData } from 'src/app/core/models/postcode.interface';

@Component({
  selector: 'app-refund-listing-info-rf',
  templateUrl: './refund-listing-info-rf.component.html',
  styleUrls: ['./refund-listing-info-rf.component.scss']
})
export class RefundListingInfoRfComponent {
  actionMapping!: { [key: string]: string };
  page = environment.DefaultPage;
  itemsPerPage = environment.ItemPerPage;
  orn_no: String | null = null;
  txn_id: String | null = null;
  mtt_id: number | null = null;
  rms_type: String | null = null;
  rtt_app_no: String | null = null;
  decision_status: String | null = null;
  sme_email: String | null = null;
  sme_nm: String | null = null;
  refund_cd: String | null = null;
  refund_ty: String | null = null;
  status_param_nm: String | null = null;
  remarks_msg: String | null = null;
  appeal_cnt: number | null = null;
  payeremail: String | null = null;
  rtt_wf_id: number | null = null;
  rtt_wf_hist_id: number | null = null;
  task_id: string | null = null;
  modelData: any;
  orderInfo: RefundPTTOrderDetails[] = [];
  rttItems: RefundRTTItems[] = [];
  bankmodel: OTCBank[] = [];
  onlinePayerEmail: String | null = null;
  pgRCPTModel: PGRcpt[] = [];
  refundInfoModel: RefundInfo[] = [];
  refundHistModel: RefundHist[] = [];
  smeUserModel: Roles[] = [];
  refundApprovalTaskInfo: RefundApprovalTaskInfo[] = [];
  refundCdModel: any[] = [];
  refundformModel: RefundForm[] = [];
  fileList: any[] = []; // Array to store existing supporting documents
  selectedItems: any[] = [];
  states: any[] = [];
  totalGrossAmount: number = 0; // Variable to hold the total sum of gross amounts
  totalPGAmounts: number = 0;

  otcPaymentDetailsCashAmt: number | null = 0;
  paymentModel: OTCPaymentModel = {
    payer_email: '',
    pymt_mode: '',
    cash_amt: 0,
    // Initialize other fields here
  };

  refundTypeMapping = [
    { type: 'RS01', label: 'Refund Slip 01' },
    { type: 'RS02', label: 'Refund Slip 02' },
    { type: 'DA', label: 'Direct Refund Application' },
    { type: 'CB', label: 'Charge Back' },
    { type: 'RF', label: 'Refund Form' },
  ];

  rtt_status: string = '';
  refundcdsection: boolean = false;
  smesection: boolean = false;
  noremarksection: boolean = false;
  disableForm: boolean = false;
  isLoading: boolean = false;
  totalRecords: number = 0;
  onlinepaymentinfosection: boolean = false;
  otcpaymentinfosection: boolean = false;
  decisiongroup: any[] = [];
  previousHistRTTStatus: string = '';
  previousHistPickupBy: string = '';
  prrviousHistAssignTo: string = '';
  refund_reason: string = '';
  pickup_by: string = '';
  approved_by: String | null = null;
  postcode: string | null = null;
  city: string | null = null;
  state: string | null = null;
  totalPostCodeRecords: number = 0;
  postCodes: PostCodeData[] = [];
  uniqueCities: string[] = [];
  uniqueStates: string[] = [];


  //refund form one 
  RefundInfoForm!: FormGroup;

  // Receipt & Supporting Documents
  uploadedFiles: Array<{
    fileName: string;
    file: File | null;
    fileContent?: string; // Add the optional fileContent property
  }> = [
      { fileName: '', file: null, fileContent: '' } // Initialize with default values
    ];
  fileTypeList = ['Invoice', 'Receipt', 'Supporting Document'];


  // Payee Bank Information
  payeeBank = {
    bankName: '',
    accountNo: '',
    accountHolderName: ''
  };

  showInsertAlert: boolean = false;
  alertMessage: string = '';
  alertClass: string = '';

  //for file document
  existingFilesTotalSize: number = 0;
  readonly MAX_TOTAL_SIZE = 10 * 1024 * 1024; // 10 MB in bytes
  // A string snapshot of the form’s original value
  originalFormValue: string = '';

  // Custom flag to indicate if the form has any net changes
  isFormChanged: boolean = false;

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
    private location: Location,
    private fb: FormBuilder,
    private actionMappingService: ActionMappingService,
  ) {
    config.maxSize = environment.PaginationMaxSize;
    config.boundaryLinks = true;
    this.translate.setDefaultLang(this.globalService.getGlobalValue()); // default language
    this.translate.use(this.globalService.getGlobalValue());
    this.route.queryParams.subscribe(params => {
      this.orn_no = params['orn_no'];
    });

    const navigation = this.router.getCurrentNavigation();

  }

  ngOnInit() {
    this.actionMapping = this.actionMappingService.getMapping();
    this.isLoading = true;
    this.initializeRefundInfoForm();
    this.stateHistory();
    this.fetchBanks();
    this.loadStates();

  }

  fetchblankform(): void {
    this.fetchRefundHist();
  }

  // Method to get the label based on refund_ty
  getRefundLabel(refund_ty: string): string {
    const match = this.refundTypeMapping.find(item => item.type === refund_ty);
    return match ? match.label : 'Unknown Refund Type';
  }

  // Fetch customer and bank information
  fetchRttform(): void {
    this.isLoading = true;
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    const url = environment.apiUrl + '/api/refundapproval/v1/getrttform';
    const requestBody = {
      i_orn_no: this.orn_no,
    };

    this.http.post(url, requestBody, { headers }).subscribe(
      (response: any) => {
        if (response?.data && Array.isArray(response.data) && response.data.length > 0) {
          const bankInfo = response.data[0]; // Assuming the first object is the desired one

          console.log('Bank information:', bankInfo);
          // Patch the form with bank and customer information
          this.RefundInfoForm.patchValue({
            // Customer Information
            customerName: bankInfo.custNm,
            customerEmail: bankInfo.custEmail,
            customerPhoneNumber: bankInfo.custPhone,
            customerAddress1: bankInfo.billingAddress1,
            customerAddress2: bankInfo.billingAddress2,
            customerAddress3: bankInfo.billingAddress3,
            postcode: bankInfo.custPostcode,
            city: bankInfo.custCity,
            state: bankInfo.custState,

            // Receipt Information
            receiptNo: bankInfo.rcptNo,
            receiptAmount: bankInfo.rcptAmt,
            orderReferenceNo: bankInfo.ornNo,
            transactionId: bankInfo.txnId,
            entityName: bankInfo.entityNm,
            entityType: bankInfo.entityTy,
            entityNo: bankInfo.entityNo,

            // Payee Bank Information
            bankName: bankInfo.bankAccountName,
            accountNo: bankInfo.bankAccountNo,
            accountHolderName: bankInfo.bankHolderName,
            identityType: bankInfo.identityType,
            identityNumber: bankInfo.identityNumber,

            // Other Information
            //remarks: '', // Default or API-provided value
          });
        }
        this.isLoading = false;
        // Store the original form value as a JSON string
        this.originalFormValue = JSON.stringify(this.RefundInfoForm.value);

        // console.log('Original form value:', this.originalFormValue);

        // Subscribe to form value changes to detect net changes
        this.RefundInfoForm.valueChanges.subscribe(currentValue => {
          const currentValueString = JSON.stringify(currentValue);

          //  console.log('Current form value:', currentValueString);

          if (currentValueString === this.originalFormValue) {
            // If current form value equals the original, mark as unchanged.
            this.isFormChanged = false;
            // Optionally mark as pristine.
            this.RefundInfoForm.markAsPristine();
          } else {
            // There is a net change in the form.
            this.isFormChanged = true;
          }
        });

      },
      (error) => {
        console.error('Error fetching refund info:', error);
        this.isLoading = false;
      }
    );
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

  async fetchDocuments(): Promise<void> {
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    const url = environment.apiUrl + '/api/refundapproval/v1/getrttdoc';
    const requestBody = {
      i_rtt_wf_id: this.rtt_wf_id,
    };

    this.http.post(url, requestBody, { headers }).subscribe(
      (response: any) => {
        if (Array.isArray(response)) {
          this.fileList = response; // Fetched files remain here.
          console.log('Fetched documents:', this.fileList);

          this.existingFilesTotalSize = this.fileList.reduce((total, file) => {
            return total + (file.fileSize || 0);
          }, 0);
          console.log('Existing files total size (bytes):', this.existingFilesTotalSize);
        } else {
          console.error('Unexpected response format:', response);
          this.alertMessage = 'Unexpected response format.';
          this.alertClass = 'alert alert-danger PA-alert-box';
          this.showInsertAlert = true;

          // auto-hide after 5 seconds
          setTimeout(() => {
            this.showInsertAlert = false;
          }, 5000);
        }
      },
      (error) => {
        this.alertMessage = 'An error occurred while fetching documents.';
        this.alertClass = 'alert alert-danger PA-alert-box';
        this.showInsertAlert = true;

        // auto-hide after 5 seconds
        setTimeout(() => {
          this.showInsertAlert = false;
         // window.history.back();
        }, 10000);
      }
    );
  }

  getDownloadFileName(file: any): string {
    let fileName = file.file_nm;

    // Check if fileName already has an extension
    if (fileName.lastIndexOf('.') === -1) {
      // Map MIME type to file extension
      let extension = '';
      switch (file.file_type) {
        case 'application/pdf':
          extension = '.pdf';
          break;
        case 'application/msword':
          extension = '.doc';
          break;
        case 'application/vnd.openxmlformats-officedocument.wordprocessingml.document':
          extension = '.docx';
          break;
        case 'application/vnd.ms-excel':
          extension = '.xls';
          break;
        case 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet':
          extension = '.xlsx';
          break;
        // Add more cases as needed for other file types
        default:
          // Optionally, you can set a default extension or leave it blank.
          extension = '';
          break;
      }
      fileName += extension;
    }
    return fileName;
  }

  stateHistory() {
    this.refund_ty = history.state.refund_type;
    this.rtt_app_no = history.state.rtt_app_no;

    // Set loading state
    this.isLoading = true;

    // Define HTTP headers
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    // Define API endpoint & request body
    const url = `${environment.apiUrl}/api/refundl/v1/getrttwfid`;
    const requestBody = {
      i_rtt_app_no: this.rtt_app_no,
    };

    // Make HTTP POST request
    this.http.post(url, requestBody, { headers }).subscribe(
      (response: any) => {
        this.rtt_wf_id = response.data[0].rtt_wf_id;
        this.orn_no = response.data[0].orn_no;
        this.rtt_status = response.data[0].rtt_status;
        this.refund_cd = response.data[0].refund_cd;
        this.RefundInfoForm.disable(); // Disable entire form

        this.disableForm = true;
        this.fetchblankform();
      },
      (error) => {
        console.error('Error fetching rttwfid:', error);
        this.isLoading = false;
      }
    );

  }

  // Fetch RTT Items
  async fetchRTTItems(): Promise<void> {
    this.isLoading = true;
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    const url = environment.apiUrl + '/api/refundapproval/v1/getrttitems';
    const requestBody = {
      i_rtt_wf_id: this.rtt_wf_id,
    };

    this.http.post(url, requestBody, { headers }).subscribe(
      (response: any) => {
        this.rttItems = response?.data || [];
        console.log(this.rttItems); // Log the API response

        const paymentItemsArray = this.RefundInfoForm.get('paymentItems') as FormArray;
        paymentItemsArray.clear(); // Clear existing items

        // Populate payment items dynamically with subscription for recalculation
        this.rttItems.forEach((item: any) => {
          const group = this.fb.group({
            itemDescription: [item.item_desc || '', Validators.required],
            quantity: [item.qty || 0, Validators.required],
            amount: [item.unit_fee || 0, Validators.required],
            tax: [item.tax_amt || 0],
            incentiveCode: [item.grant_cd || ''],
            discount: [item.disc_amt || 0],
            grossAmount: [item.refund_amt || 0, Validators.required],
            rtt_item_id: [item.rtt_item_id || 0],
            netAmount: [item.net_amt || 0],
          });

          // Subscribe to changes on this group to recalculate grossAmount
          group.valueChanges.subscribe(values => {
            const quantity = Number(values.quantity) || 0;
            const amount = Number(values.amount) || 0;
            const tax = Number(values.tax) || 0;
            const discount = Number(values.discount) || 0;
            // Example calculation: (quantity * amount) + tax - discount
            const calculatedGross = (quantity * amount) + tax - discount;
            group.get('netAmount')?.setValue(calculatedGross, { emitEvent: false });
            this.calculateTotalGrossAmount();
          });

          paymentItemsArray.push(group);
        });

        // Calculate total refund amount (as an initial value)
        this.totalGrossAmount = this.rttItems.reduce(
          (total, item) => total + (item.net_amt || 0),
          0
        );

        this.isLoading = false;
        this.fetchRttform();
      },
      (error) => {
        console.error('Error fetching payment items:', error);
        this.isLoading = false;
      }
    );
  }



  // Method to map actions to descriptive names
  mapAction(action: string | null): string {
    return action && this.actionMapping[action] ? this.actionMapping[action] : 'Unknown';
  }

  downloadFile(fileName: string, verId: string, sourceSysDocRefID: string) {
    const requestBody = {
      refNo1: fileName,
      verID: verId,
      sourceSysDocRefID: sourceSysDocRefID,
    };

    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    const url = `${environment.apiUrl}/api/OTCCR/v1/downloadOTCRcpt`;

    this.http
      .post(url, requestBody, {
        observe: 'response',
        responseType: 'blob', // Expect binary content
        headers: headers,
      })
      .subscribe(
        (response) => {
          const contentDisposition = response.headers.get('content-disposition');
          let fileNameFromHeader = 'SSM-Receipt-' + fileName + '.pdf'; // Fallback name
          if (contentDisposition) {
            const match = contentDisposition.match(/filename="?(.+)"?/);
            if (match && match[1]) {
              fileNameFromHeader = match[1];
            }
          }

          const blob = new Blob([response.body as Blob], { type: 'application/pdf' });

          // Create a link to download the file
          const link = document.createElement('a');
          const objectUrl = URL.createObjectURL(blob);
          link.href = objectUrl;
          link.download = fileNameFromHeader || fileName;
          link.click();
          URL.revokeObjectURL(objectUrl);
        },
        (error) => {
          console.error('Error downloading file:', error);
        }
      );
  }

  // for refund form 
  initializeRefundInfoForm() {
    this.RefundInfoForm = this.fb.group({
      // Customer Information
      customerName: ['', Validators.required],
      customerEmail: [''],
      customerPhoneNumber: [''],
      customerAddress1: ['', Validators.required],
      customerAddress2: [''],
      customerAddress3: [''],
      postcode: [''],
      city: ['', Validators.required],
      state: ['', Validators.required],

      // Receipt Information
      receiptNo: ['', Validators.required],
      receiptAmount: [''],
      orderReferenceNo: ['', Validators.required],
      transactionId: ['', Validators.required],
      entityName: ['', Validators.required],
      entityType: ['', Validators.required],
      entityNo: ['', Validators.required],

      // Payment Items
      paymentItems: this.fb.array([]), // Dynamically added rows for payment items
      uploadedFiles: this.fb.array([]), // Dynamically added rows for uploaded files

      // Payee Bank Information
      bankName: ['', Validators.required],
      accountNo: ['', Validators.required],
      accountHolderName: ['', Validators.required],
      identityNumber: ['', [Validators.required, Validators.pattern(/^[a-zA-Z0-9]+$/)]],
      identityType: ['', Validators.required], // e.g., BRN, Passport, Old NRIC

      // Other Information
      remarks: ['', Validators.required]
    });

    this.loadPostcode();

    // Add one payment item row by default
    // this.addCheque();
  }

  // Getter for payment items FormArray
  get paymentItems(): FormArray {
    return this.RefundInfoForm.get('paymentItems') as FormArray;
  }

  //getter for uploaded files FormArray
  get uploadedFilesFormArray(): FormArray {
    return this.RefundInfoForm.get('uploadedFiles') as FormArray;
  }

  // Remove a payment item row
  removeItem(index: number): void {
    this.paymentItems.removeAt(index);
  }

  // Handle file selection and encode to Base64
  onFileSelect(event: Event, index: number): void {
    const input = event.target as HTMLInputElement;
    if (input.files?.length) {
      const file = input.files[0];

      // Allowed file types array (modify as needed)
      const allowedFileTypes = [
        'application/pdf',
        'application/msword',
        'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
        'image/jpeg',
        'image/png'
      ];

      // Check if the selected file's MIME type is allowed
      if (!allowedFileTypes.includes(file.type)) {
        this.alertMessage = 'This file type is not allowed. Please upload a PDF, DOC, DOCX, JPEG, or PNG file.';
        this.alertClass = 'alert alert-danger PA-alert-box';
        this.showInsertAlert = true;

        // auto-hide after 3 seconds
        setTimeout(() => {
          this.showInsertAlert = false;

        }, 3000);
      }

      // Calculate the size of the new file (in bytes)
      const newFileSize = file.size;

      // Calculate the current total size of new files already selected
      const currentUploadedFilesSize = this.uploadedFiles.reduce((total, fileItem) => {
        return total + (fileItem.file ? fileItem.file.size : 0);
      }, 0);

      // Calculate the combined size: fetched files + new uploads already selected
      const currentTotalSize = this.existingFilesTotalSize + currentUploadedFilesSize;

      // Calculate what the total size would be if this file is added
      const newTotalSize = currentTotalSize + newFileSize;

      console.log(
        "Current total size (fetched + new uploads):",
        currentTotalSize,
        "bytes (", (currentTotalSize / (1024 * 1024)).toFixed(2), "MB)"
      );
      console.log(
        "New total size after adding file:",
        newTotalSize,
        "bytes (", (newTotalSize / (1024 * 1024)).toFixed(2), "MB)"
      );

      // Check if adding this file exceeds the maximum allowed total size
      if (newTotalSize > this.MAX_TOTAL_SIZE) {
        const remainingSize = this.MAX_TOTAL_SIZE - currentTotalSize;
        this.alertMessage = `You can only upload files up to ${(remainingSize / (1024 * 1024)).toFixed(2)} MB more.`;
        this.alertClass = 'alert alert-danger PA-alert-box';
        this.showInsertAlert = true;

        // auto-hide after 3 seconds
        setTimeout(() => {
          this.showInsertAlert = false;
        }, 3000);
        return; // Prevent adding the file
      }

      const reader = new FileReader();
      reader.onload = () => {
        // Update the form control for the given index
        this.uploadedFilesFormArray.at(index).patchValue({
          fileName: file.name,
          fileContent: reader.result?.toString().split(',')[1] || ''
        });
        // Also update the local array:
        this.uploadedFiles[index].file = file;
        this.uploadedFiles[index].fileName = file.name;
        this.uploadedFiles[index].fileContent = reader.result?.toString().split(',')[1] || '';
      };
      reader.readAsDataURL(file);

    }
  }

  // Add a new file row to the FormArray (user uploads only)
  addFileRow(): void {
    this.uploadedFilesFormArray.push(this.fb.group({
      fileName: ['', Validators.required],
      fileContent: ['']
    }));
  }

  // Remove a file row from the FormArray (user uploads only)
  removeFile(index: number): void {
    if (this.uploadedFilesFormArray.length > index) {
      this.uploadedFilesFormArray.removeAt(index);
    }
  }

  addCheque(): void {
    const paymentItem = this.fb.group({
      itemDescription: ['', Validators.required],
      quantity: [1, [Validators.required, Validators.min(1)]],
      amount: [0, [Validators.required, Validators.min(0)]],
      tax: [0], // Default tax to 0
      incentiveCode: [''],
      discount: [0],
      grossAmount: [0, Validators.required] // Remove 'disabled' and make it a regular form control
    });

    // Calculate grossAmount dynamically
    paymentItem.valueChanges.subscribe(values => {
      const { quantity, amount, tax, discount } = values;
      const calculatedGrossAmount =
        (quantity || 0) * (amount || 0) + (tax || 0) - (discount || 0);
      paymentItem.get('grossAmount')?.setValue(calculatedGrossAmount, { emitEvent: false });
    });

    this.paymentItems.push(paymentItem);

    // Update total gross amount whenever paymentItems change
    this.paymentItems.valueChanges.subscribe(() => {
      this.calculateTotalGrossAmount();
    });
  }

  calculateTotalGrossAmount(): void {
    const controls = this.paymentItems.controls as FormGroup[];
    this.totalGrossAmount = controls.reduce((total: number, group: FormGroup) => {
      const value = group.get('grossAmount')?.value;
      return total + (parseFloat(value) || 0);
    }, 0);
  }

  fetchRefundHist(): void {
    this.isLoading = true;
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    const url = environment.apiUrl + '/api/refundl/v1/getRefundHist'; // API endpoint
    const requestBody = {
      i_orn_no: this.orn_no,
    };

    this.http.post(url, requestBody, { headers }).subscribe(
      (response: any) => {
        if (response?.data && Array.isArray(response.data) && response.data.length > 0) {
          this.refundHistModel = response.data.map((item: RefundHist) => {
            return {
              ...item,
              action: this.mapAction(item.action), // Map the action to descriptive name
            };
          });
          console.log('Refund history:', this.refundHistModel);

          // Set the last item's msg as the initial value for the 'remarks' FormControl
          const lastMsg = this.refundHistModel[this.refundHistModel.length - 1]?.msg || '';
          this.RefundInfoForm.controls['remarks'].setValue(lastMsg);
          this.rtt_wf_hist_id = this.refundHistModel[this.refundHistModel.length - 1]?.rtt_wf_hist_id;
          console.log(lastMsg);


          // Function to find the first non-matching `modified_by` value
          function findFirstDifferentModifiedByWithRTT(data: any[]): any {
            if (!data || data.length === 0) return null;

            // Filter out records with modified_by = 'SYSTEM'
            const validRecords = data.filter(record => record.modified_by !== 'SYSTEM');

            if (validRecords.length === 0) return null;

            // Group records by contiguous RTT status
            interface Group {
              rtt_status: string;
              records: any[];
            }
            const groups: Group[] = [];
            let currentGroup: Group = { rtt_status: validRecords[0].rtt_status, records: [validRecords[0]] };

            for (let i = 1; i < validRecords.length; i++) {
              const record = validRecords[i];
              // If the RTT status is the same as the current group, add the record to the group.
              if (record.rtt_status === currentGroup.rtt_status) {
                currentGroup.records.push(record);
              } else {
                // RTT status changed: push the current group and start a new one.
                groups.push(currentGroup);
                currentGroup = { rtt_status: record.rtt_status, records: [record] };
              }
            }
            // Push the final group
            groups.push(currentGroup);

            // If there's only one group, then RTT status never changed.
            if (groups.length < 2) return null;

            // The previous (or initial) group's in‑charge is taken as the modified_by of the first record in the first group.
            let previousIncharge = groups[0].records[0].modified_by;

            // Now check each subsequent group.
            for (let g = 1; g < groups.length; g++) {
              // Look through all records in the current group.
              for (const record of groups[g].records) {
                // If we find a modified_by different from the previous group's in‑charge, return it.
                if (record.modified_by !== previousIncharge) {
                  return record.modified_by;
                }
              }
              // If no record in this group had a different modified_by,
              // update the previousIncharge to the new group's first record (baseline) and continue.
              previousIncharge = groups[g].records[0].modified_by;
            }

            // If no change was detected across groups, return null.
            return null;
          }


          // Call the function with the response data
          const firstDifferentModifiedBy = findFirstDifferentModifiedByWithRTT(this.refundHistModel);

          this.pickup_by = firstDifferentModifiedBy;
          console.log('First different modified_by:', this.pickup_by);

          // Function to find the first non-matching `rtt_status` value (ignoring 'SYSTEM' and empty values)
          function findFirstDifferentRTTStatus(data: any[]): any {
            if (!data || data.length === 0) return null; // Handle empty or invalid data

            // Start with the first rtt_status value
            let reference = data[0].rtt_status;
            // If the first value is 'SYSTEM' or falsy, ignore it for comparison
            if (reference === 'SYSTEM' || !reference) {
              reference = null;
            }

            // Loop through the data starting from the second element
            for (let i = 1; i < data.length; i++) {
              const current = data[i].rtt_status;

              // Skip if current is 'SYSTEM' or empty
              if (current === 'SYSTEM' || !current) {
                continue;
              }

              // If reference is null (because it was 'SYSTEM' or missing), assign the first valid value
              if (reference === null) {
                reference = current;
                continue;
              }

              // If the current value does not match the reference, return it
              if (current !== reference) {
                return current; // First non-matching rtt_status found
              }
            }

            // If all valid rtt_status values are the same, return null
            return null;
          }

          // Example usage:
          const firstDifferentRTTStatus = findFirstDifferentRTTStatus(this.refundHistModel);
          this.previousHistRTTStatus = firstDifferentRTTStatus;
          console.log('First different rtt_status:', this.previousHistRTTStatus);

        } else {
          // Handle the case where the response is empty or null
          this.refundHistModel = [];
          this.RefundInfoForm.controls['remarks'].setValue('');
        }

        // Fetch SME users after getting the refund history
        this.fetchDocuments()
          .then(() => {
            this.fetchRTTItems();
          })
          .catch((error) => {
            console.error("Error fetching documents:", error);
          });

        this.isLoading = false;
      },
      (error) => {
        console.error('Error fetching refund history', error);
        this.isLoading = false;
      }
    );
  }

  downloadDocument(file: any): void {
    if (!file.file_content) {
      this.alertMessage = 'No file content available';
      this.alertClass = 'alert alert-danger PA-alert-box';
      this.showInsertAlert = true;

      // auto-hide after 3 seconds
      setTimeout(() => {
        this.showInsertAlert = false;
      }, 3000);
    }

    // 1) Decode Base64
    const binaryString = atob(file.file_content);
    const len = binaryString.length;
    const bytes = new Uint8Array(len);
    for (let i = 0; i < len; i++) {
      bytes[i] = binaryString.charCodeAt(i);
    }

    // 2) Build a Blob and a temporary URL
    const blob = new Blob([bytes], { type: file.file_type });
    const blobUrl = URL.createObjectURL(blob);

    // 3) Create an <a> and “click” it to download
    const a = document.createElement('a');
    a.href = blobUrl;
    a.download = this.getDownloadFileName(file);
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);

    // 4) Clean up
    URL.revokeObjectURL(blobUrl);
  }


  //postcode start

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

  // Method to handle postcode change and update city and state
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

  filterToDigits(event: Event) {
    const input = event.target as HTMLInputElement;
    // strip out any non‑digit characters
    const digits = input.value.replace(/\D/g, '');
    // update both the input’s displayed value and the form control
    input.value = digits;
    this.RefundInfoForm.get('customerPhoneNumber')!.setValue(digits);
  }

  back() {
    this.location.back();
  }
}