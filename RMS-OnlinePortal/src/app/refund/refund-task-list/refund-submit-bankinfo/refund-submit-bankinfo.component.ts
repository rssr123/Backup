import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Location } from '@angular/common';
import { ChangeDetectorRef, Component } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { Router, ActivatedRoute } from '@angular/router';
import { NgbPaginationConfig } from '@ng-bootstrap/ng-bootstrap';
import { TranslateService } from '@ngx-translate/core';
import { AuthService } from 'src/app/services/auth.service';
import { GlobalService } from 'src/app/shared/global.service';
import { environment } from 'src/environments/environment';
import { RefundPTTOrderDetails, RefundPTTPaymentItemDetails, RefundPTTOnlinePaymentInfos, PGRcpt, RefundInfo, RefundHist, RefundForm } from 'src/app/models/refundptt-interface';
import { OTCCollectionReceiptingBankDraft, OTCCollectionReceiptingCheque, OTCCollectionReceiptingMoneyOrder, OTCCollectionReceiptingPymtItem, OTCHist, OTCPaymentModel, OTCPaymentDetails, OTCPaymentHeader, OTCRcpt, OTCEMV } from 'src/app/models/otc-collection-receipting.interface';
import { OTCBank } from 'src/app/models/otc-collection-returned-cheque.interface';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { t } from 'i18next';
import { ParamService } from 'src/app/core/services/param.service';
import { ActionMappingService } from 'src/app/services/action-mapping.service';
import { PostCodeData } from 'src/app/models/postcode.interface';



@Component({
  selector: 'app-refund-submit-bankinfo',
  templateUrl: './refund-submit-bankinfo.component.html',
  styleUrls: ['./refund-submit-bankinfo.component.scss']
})
export class RefundSubmitBankinfoComponent {
  actionMapping!: { [key: string]: string };
  page = environment.DefaultPage;
  itemsPerPage = environment.ItemPerPage;
  orn_no: String | null = null;
  txn_id: String | null = null;
  mtt_id: number | null = null;
  rms_type: String | null = null;
  orderstatus: String | null = null;
  remarks_msg: String | null = null;
  refund_cd: String | null = null;
  refund_slip_no: string = '';

  modelData: any;
  orderInfo: RefundPTTOrderDetails[] = [];
  paymentItems: RefundPTTPaymentItemDetails[] = [];
  onlinePaymentInfos: RefundPTTOnlinePaymentInfos[] = [];
  bankmodel: OTCBank[] = [];
  selectedChequeDt: Date[] | null = null;
  selectedBankDraftDt: Date[] | null = null;
  selectedMoneyOrderDt: Date[] | null = null;
  selectedPaymentMode: string = ''; // Tracks the selected payment mode
  chequeModel: OTCCollectionReceiptingCheque[] = [];
  isAddCheque: boolean = true;
  bankDraftModel: OTCCollectionReceiptingBankDraft[] = [];
  isAddBankDraft: boolean = true;
  moneyOrderModel: OTCCollectionReceiptingMoneyOrder[] = [];
  isAddMoneyOrder: boolean = true;
  otcRcptModel: OTCRcpt[] = [];
  otcEMVModel: OTCEMV[] = [];
  otcPaymentDetails: OTCPaymentDetails[] = [];
  otcPaymentHeader: OTCPaymentHeader[] = [];
  otcPayerEmail: String | null = null;
  otcPymtMode: String | null = null;
  onlinePayerEmail: String | null = null;
  pgRCPTModel: PGRcpt[] = [];
  refundInfoModel: RefundInfo[] = [];
  refundHistModel: RefundHist[] = [];
  refundformModel: RefundForm[] = [];

  states: any[] = [];
  cashPayments: OTCPaymentDetails[] = [];
  chequePayments: OTCPaymentDetails[] = [];
  moneyOrderPayments: OTCPaymentDetails[] = [];
  bankDraftPayments: OTCPaymentDetails[] = [];
  totalGrossAmount: number = 0; // Variable to hold the total sum of gross amounts
  totalPGAmounts: number = 0;
  totalChequeAmount: number = 0;
  totalBDAmount: number = 0;
  totalMOAmount: number = 0;

  otcPaymentDetailsCashAmt: number | null = 0;
  paymentModel: OTCPaymentModel = {
    payer_email: '',
    pymt_mode: '',
    cash_amt: 0,
    // Initialize other fields here
  };
  rttwfInfo: any[] = [];
  file_content: any;
  showInsertAlert: boolean = false;
  alertMessage: string = '';
  alertClass: string = '';


  isLoading: boolean = false;
  totalRecords: number = 0;
  onlinepaymentinfosection: boolean = false;
  otcpaymentinfosection: boolean = false
  submitbutton: boolean = true;
  rtt_status: string | null = null;
  rtt_app_no: string | null = null;
  date_expiry: Date | null = null;
  isDateExpired: boolean = false;
  BankInfoForm!: FormGroup;
  disabled: any;
  rtt_wf_id: number | null = null;
  postcode: string | null = null;
  city: string | null = null;
  state: string | null = null;
  totalPostCodeRecords: number = 0;
  postCodes: PostCodeData[] = [];
  uniqueCities: string[] = [];
  uniqueStates: string[] = [];


  constructor(
    private http: HttpClient,
    config: NgbPaginationConfig,
    private router: Router,
    public dialog: MatDialog,
    private cd: ChangeDetectorRef,
    private route: ActivatedRoute,
    private translate: TranslateService,
    private globalService: GlobalService,
    private fb: FormBuilder,
    private location: Location,
    private ParamService: ParamService,
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
    this.bankInfoForm();
    this.fetchBanks();
    this.loadStates();
    this.stateHistory();
    this.fetchRttform();
    this.fetchOrderInfo();

    this.fetchPaymentHeader();
    this.checkfetchRcpt();
    this.fetchOTCRcpt();
    this.fetchOnlineRcpt();
    this.fetchRefundInformation();
    this.fetchRefundHist();

  }

  bankInfoForm() {
    this.BankInfoForm = this.fb.group({
      refund_slip_no: [{ value: '', disabled: true }],
      identityType: ['', Validators.required],
      identityNumber: ['', [Validators.required, Validators.pattern(/^[a-zA-Z0-9]+$/)]], // Only alphanumeric
      bankAccountNo: ['', [Validators.required, Validators.pattern(/^\d+$/)]], // Only digits
      bankAccountName: ['', Validators.required],
      bankAccountType: [{ value: 'Current/Savings', disabled: true }, Validators.required],
      bankHolderName: ['', Validators.required],
      billingAddress1: ['', Validators.required],
      billingAddress2: [''],
      billingAddress3: [''],
      postcode: ['', [Validators.required, Validators.pattern(/^\d{5}$/)]], // 5 digits
      city: ['', Validators.required],
      state: ['', Validators.required],
      recEmail: ['', [Validators.required, Validators.email]],
    });
    this.loadPostcode();
  }


  stateHistory() {
    this.mtt_id = history.state.mtt_id;
    this.orn_no = history.state.orn_no;
    this.txn_id = history.state.txn_id;
    this.rms_type = history.state.rms_type;
    this.orderstatus = history.state.order_status;
    this.refund_slip_no = history.state.refund_slip_no;
    this.rtt_status = history.state.rtt_status;
    this.rtt_app_no = history.state.rtt_app_no;
    // get the wf_id from api
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

    this.http.post(url, requestBody, { headers }).subscribe(
      (response: any) => {

        this.rttwfInfo = response?.data || []; // Store the received data
        this.rtt_wf_id = this.rttwfInfo[0].rtt_wf_id;
        this.fetchPaymentItems();
      },
      (error) => {
        console.error('Error fetching rttwfid:', error);
        this.isLoading = false;
      }
    );


    this.date_expiry = history.state.date_expiry;
    //this.date_expiry = new Date('2025-12-31');
    console.log(this.rtt_app_no);
    console.log(this.date_expiry);
    console.log(this.rtt_status);
    this.setRefundSlipNo(this.refund_slip_no);

    this.checkRefundDateExpiry();


    // Set the value in the form

  }


  setRefundSlipNo(value: string | null | undefined): void {
    this.BankInfoForm.patchValue({
      refund_slip_no: value && value.trim() !== '' ? value : '-'  // if empty/null → "-"
    });
  }

  fetchOrderInfo(): void {
    this.isLoading = true;
    const today = new Date();

    // Common headers for the request
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    // Declare the URL variable outside the conditional blocks
    let url: string;

    // Determine the API endpoint based on rms_type
    if (this.rms_type === 'Online') {
      url = environment.apiUrl + '/api/refundl/v1/getrefundoionline'; // API endpoint
    } else if (this.rms_type === 'OTC') {
      url = environment.apiUrl + '/api/refundl/v1/getrefundoiotc'; // API endpoint
    } else {
      console.log('rms_type not found');
      this.isLoading = false; // Stop loading if rms_type is invalid
      return; // Exit the method early
    }

    // Request body
    const requestBody = {
      i_mtt_id: this.mtt_id,
    };

    // Make the HTTP POST request
    this.http.post(url, requestBody, { headers }).subscribe(
      (response: any) => {
        this.orderInfo = response?.data || []; // Store the received data
        console.log(this.orderInfo);
        this.isLoading = false;
      },
      (error) => {
        console.error('Error fetching order info:', error);
        this.isLoading = false;
      }
    );
  }

  fetchPaymentItems(): void {
    this.isLoading = true;
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    const url = environment.apiUrl + '/api/refundapproval/v1/getrttitems'; // API endpoint
    const requestBody = {
      i_rtt_wf_id: this.rtt_wf_id,
    };


    this.http.post(url, requestBody, { headers }).subscribe(
      (response: any) => {
        this.paymentItems = response?.data || []; // Store the received data
        console.log(this.paymentItems);
        this.calculateTotalGrossAmount();
        this.isLoading = false;

      },
      (error) => {
        console.error('Error fetching payment items:', error);
        this.isLoading = false;
      }
    );
  }

  calculateTotalGrossAmount(): void {
    this.totalGrossAmount = this.paymentItems.reduce((sum, item) => sum + item.net_amt, 0);

  }


  checkRefundDateExpiry(): void {
    // Get today's date
    const today = new Date();
    today.setHours(0, 0, 0, 0); // Set the time to midnight for accurate comparison

    // Example variable date
    let variableDate = this.date_expiry; // Replace with your variable date

    if (variableDate) {
      if (typeof variableDate === 'string') {
        // Parse the string into a Date object
        variableDate = new Date(variableDate);
      }

      if (variableDate instanceof Date && !isNaN(variableDate.getTime())) {
        // Ensure variableDate is a valid Date object
        variableDate.setHours(0, 0, 0, 0); // Set the time to midnight for accurate comparison

        // Compare dates
        if (today.getTime() === variableDate.getTime()) {
          console.log("The dates are the same.");
        } else if (today.getTime() > variableDate.getTime()) {
          console.log("Today is after the variable date.");
          this.isDateExpired = true;
        } else {
          console.log("Today is before the variable date.");
        }
      } else {
        console.log("Variable date is not a valid Date object.");
      }
    } else {
      console.log("Variable date is null.");
    }
  }

  async handleSubmitBankInfo(): Promise<void> {
    this.isLoading = true;
    const formData = this.BankInfoForm.getRawValue();
    const body: any = {
      orn_no: this.orn_no,
      identity_type: formData.identityType,
      identity_number: formData.identityNumber,
      bank_account_no: formData.bankAccountNo,
      bank_account_name: formData.bankAccountName,
      bank_account_type: formData.bankAccountType,
      bank_holder_name: formData.bankHolderName,
      billing_address_1: formData.billingAddress1,
      billing_address_2: formData.billingAddress2,
      billing_address_3: formData.billingAddress3,
      city: formData.city,
      postcode: formData.postcode,
      state: formData.state,
      rec_email: formData.recEmail
    };

    console.log('Request body:', body);
    //    return;

    // this.isLoading = true;
    if (this.isDateExpired === true) {

      const url = environment.apiUrl + '/api/refundl/v1/updaterefunddateexpiry';
      const headers = new HttpHeaders({
        Authorization: environment.authKey,
        'Content-Type': 'application/json',
      });

      const body: any = {
        i_rtt_app_no: this.rtt_app_no,
      };
      try {
        const response = await this.http.post(url, body, { headers }).toPromise();
        console.log('Success response:', response);
        this.isLoading = false;

        // Instead of alerting, set the properties to display the message in a div.
        this.alertMessage = 'Refund request expired. This refund request no longer valid.';
        // You can use a warning style or any style you prefer.
        this.alertClass = 'alert alert-warning PA-alert-box';
        this.showInsertAlert = true;

        // Hide the alert and then navigate back after 5 seconds.
        setTimeout(() => {
          this.showInsertAlert = false;
          this.location.back();
        }, 3000);
      } catch (error) {
        console.error('Error:', error);
        this.isLoading = false;

        // Set error alert properties.
        this.alertMessage = 'An error occurred while submitting the refund bank info. Please try again.';
        this.alertClass = 'alert alert-danger PA-alert-box';
        this.showInsertAlert = true;

        // Hide the alert after 10 seconds.
        setTimeout(() => {
          this.showInsertAlert = false;
        }, 10000);
      }
    } else {
      if (this.BankInfoForm.valid) {
        const orn_no = this.orn_no; // Assuming `orn_no` is available in your component
        const txn_id = this.txn_id; // Assuming `txn_id` is available in your component

        const url = environment.apiUrl + '/api/refundl/v1/addrttform_rs02';
        const headers = new HttpHeaders({
          Authorization: environment.authKey,
          'Content-Type': 'application/json',
        });
        // Extract values from BankInfoForm
        const formData = this.BankInfoForm.getRawValue();
        const body: any = {
          orn_no: orn_no,
          identity_type: formData.identityType,
          identity_number: formData.identityNumber,
          bank_account_no: formData.bankAccountNo,
          bank_account_name: formData.bankAccountName,
          bank_account_type: formData.bankAccountType,
          bank_holder_name: formData.bankHolderName,
          billing_address_1: formData.billingAddress1,
          billing_address_2: formData.billingAddress2,
          billing_address_3: formData.billingAddress3,
          city: formData.city,
          postcode: formData.postcode,
          state: formData.state,
          rec_email: formData.recEmail
        };


        // ============================================
        // 1. Call FMS API to insert data into FMS DB
        // ============================================
        const fmsUrl = environment.apiUrl + '/api/fmsapia/insertfmsapia';
        const fmsBody: any = {

          // For the Vendor 
          ext_sys: 'RMS',
          vendor_id: null,
          vendor_nm: formData.bankHolderName, // from the BankInfoForm
          id_ty: formData.identityType,              // from the BankInfoForm
          id_no: formData.identityNumber,            // from the BankInfoForm
          pm: this.rms_type,
          p_desc: 'Beneficiary Account No',
          p_id: '1',
          p_bankname: formData.bankAccountName, // from the BankInfoForm
          p_value: formData.bankAccountNo, // from the BankInfoForm
          addr1: formData.billingAddress1, // from the BankInfoForm
          addr2: formData.billingAddress2, // from the BankInfoForm
          addr3: formData.billingAddress3, // from the BankInfoForm
          city: formData.city,             // from the BankInfoForm
          country: 'MY',
          state: formData.state,           // from the BankInfoForm
          postcode: formData.postcode,     // from the BankInfoForm
          email: formData.recEmail,        // from the BankInfoForm
          phone_no: formData.phone_no,  // from the BankInfoForm

          //for the IH
          rtt_app_no: this.rtt_app_no,       // assumed available from orderInfo           
          refund_slip_no: formData.refund_slip_no, // assumed available from orderInfo
          refund_total_amt: this.totalGrossAmount, // assumed available from orderInfo

          //for the IH_details
          payment_item_details: this.paymentItems

        };
        console.log('FMS API request body:', fmsBody);
        try {
          // Make the API call
          const fmsResponse = await this.http.post<number>(fmsUrl, fmsBody, { headers }).toPromise();
          console.log('FMS API response:', fmsResponse);


          // Check the response value
          if (typeof fmsResponse !== 'undefined' && fmsResponse > 0) {
            console.log('FMS API insertion successful.');

            // =====================================================
            // 2. Call Refund API to submit the refund request
            // =====================================================


            try {
              const response = await this.http.post(url, body, { headers }).toPromise();
              console.log('Success response:', response);

              // Set the alert properties for a successful submission
              this.alertMessage = 'Refund Bank Info submitted successfully.';
              this.alertClass = 'alert alert-success PA-alert-box';
              this.showInsertAlert = true;

              // Optionally, redirect after a short delay or immediately
              // For example, to hide the alert after 3 seconds:
              setTimeout(() => {
                this.showInsertAlert = false;
                this.redirectToPaidTransactions();
              }, 3000);

            } catch (error) {
              console.error('Error:', error);

              // Set the alert properties for an error message
              this.alertMessage = 'An error occurred while submitting the refund request. Please try again.';
              this.alertClass = 'alert alert-danger PA-alert-box';
              this.showInsertAlert = true;

              // Optionally, hide the alert after a few seconds:
              setTimeout(() => {
                this.showInsertAlert = false;
              }, 10000);
            }
            this.isLoading = false;

          } else if (typeof fmsResponse !== 'undefined' && fmsResponse < 0) {
            console.error('FMS API returned an error ');
            // Optionally, alert the user or stop further processing:
            alert('An error occurred while inserting data into FMS. Please try again.');
            // Optionally, you might want to return or throw an error to stop the process:
            // return;
          } else {
            console.warn('Unexpected FMS API response:', fmsResponse);
            // Handle unexpected responses if necessary
          }
        } catch (fmsError) {
          console.error('FMS API error:', fmsError);
          // Handle network or other errors here
        }


      } else {
        alert('Please fill in the bank info');
      }
    }
  }

  fetchRttform(): void {
    this.isLoading = true;
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    const url = environment.apiUrl + '/api/refundapproval/v1/getrttform'; // API endpoint
    const requestBody = {
      i_orn_no: this.orn_no,
    };

    console.log(requestBody);
    this.http.post(url, requestBody, { headers }).subscribe(
      (response: any) => {
        if (response?.data && Array.isArray(response.data) && response.data.length > 0) {
          // Assign the API response to refundformModel
          this.refundformModel = response.data;

          // Check if all bank info fields are not null
          const bankInfo = this.refundformModel[0];
          const allBankInfoPresent = bankInfo.bankAccountNo != null &&
            bankInfo.bankHolderName != null &&
            bankInfo.bankAccountType != null &&
            bankInfo.identityType != null &&
            bankInfo.identityNumber != null &&
            bankInfo.bankAccountName != null;

          if (allBankInfoPresent) {
            // Patch all fields except city/state
            this.BankInfoForm.patchValue({
              identityType: bankInfo.identityType,
              identityNumber: bankInfo.identityNumber,
              bankAccountNo: bankInfo.bankAccountNo,
              bankAccountName: bankInfo.bankAccountName,
              bankHolderName: bankInfo.bankHolderName,
              bankAccountType: bankInfo.bankAccountType,
              billingAddress1: bankInfo.billingAddress1,
              billingAddress2: bankInfo.billingAddress2,
              billingAddress3: bankInfo.billingAddress3,
              postcode: bankInfo.custPostcode,
              recEmail: bankInfo.recEmail,
            });
            // Store city/state for later patching
            this.pendingCity = bankInfo.custCity;
            this.pendingState = bankInfo.custState;
            // If options are already loaded, patch now
            if (this.uniqueCities.length && this.uniqueStates.length) {
              this.patchPendingCityState();
            }
            this.checkRefundDateExpiry();
            if (this.rtt_status === 'REG' || this.rtt_status === 'BE') {
              this.submitbutton = true;
            } else {
              // Disable the form to prevent user modification
              this.BankInfoForm.disable();
              this.submitbutton = false;
            }

          }
        }
        console.log(this.refundformModel);
        this.isLoading = false;
      },
      (error) => {
        console.error('Error fetching refund info', error);
        this.isLoading = false;
      }
    );
  }


  redirectToPaidTransactions() {
    this.location.back();
  }
  onCancel() {
    this.location.back();
  }

  fetchPaymentHeader(): void {
    this.isLoading = true;
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    if (this.rms_type === 'Online') {

      this.onlinepaymentinfosection = true;
      const url = environment.apiUrl + '/api/refundl/v1/getPGPaymentInfo'; // API endpoint
      const requestBody = {
        i_mtt_id: this.mtt_id,
        i_txn_id: this.txn_id,
      };
      this.http.post(url, requestBody, { headers }).subscribe(
        (response: any) => {
          this.onlinePaymentInfos = response?.data || []; // Store the received data
          this.onlinePayerEmail = this.onlinePaymentInfos.length > 0 ? this.onlinePaymentInfos[0].cust_email : '';
          this.isLoading = false;
          // console.log(this.onlinePaymentInfos);
          // console.log(this.onlinePayerEmail);
          this.calculateTotalPGAmount();
        },
        (error) => {
          console.error('Error fetching OTC Payment Details:', error);
          this.isLoading = false;
        }
      );


    } else if (this.rms_type === 'OTC') {

      this.otcpaymentinfosection = true;
      const url = environment.apiUrl + '/api/OTCCR/v1/getOTCPaymentHeader'; // API endpoint
      const requestBody = {
        i_mtt_id: this.mtt_id,
      };

      this.http.post(url, requestBody, { headers }).subscribe(
        (response: any) => {
          this.otcPaymentHeader = response?.data || []; // Store the received data
          this.otcPayerEmail = this.otcPaymentHeader.length > 0 ? this.otcPaymentHeader[0].payer_email : '';
          this.otcPymtMode = this.otcPaymentHeader.length > 0 ? this.otcPaymentHeader[0].otc_pymt_mode : '';

          this.isLoading = false;
        },
        (error) => {
          console.error('Error fetching OTC Payment Details:', error);
          this.isLoading = false;
        }
      );

      this.fetchOTCPaymentDetails();

      this.fetchOTCEMV();

    }

  }

  calculateTotalPGAmount(): void {
    this.totalPGAmounts = this.onlinePaymentInfos.reduce((sum, item) => sum + item.pg_payment_amt, 0);
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

  fetchOTCEMV(): void {
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    const url = environment.apiUrl + '/api/OTCCR/v1/getotcemv';

    const Body: any = {
      i_mtt_id: this.mtt_id,
    };

    this.http.post(url, Body, { headers }).subscribe(
      (response: any) => {
        // Ensure response.data is treated as an array
        if (response.data) {
          this.otcEMVModel = Array.isArray(response.data) ? response.data : [response.data];
        } else {
          this.otcEMVModel = [];
        }

        console.log(this.otcEMVModel);
        console.log(this.otcEMVModel.length);
        // this.totalRecords = this.otcEMVModel.length > 0 ? this.otcEMVModel[0].total || 0 : 0;
        this.isLoading = false;
      },
      (error) => {
        console.error('Error fetching EMV data:', error);
        this.isLoading = false;
      }
    );
  }

  fetchRefundInformation(): void {
    this.isLoading = true;
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    const url = environment.apiUrl + '/api/refundl/v1/getRefundInfo'; // API endpoint
    const requestBody = {
      i_txn_id: this.txn_id,
      i_orn_no: this.orn_no,
    };

    this.http.post(url, requestBody, { headers }).subscribe(
      (response: any) => {
        if (response?.data && Array.isArray(response.data) && response.data.length > 0) {
          // If response.data exists and is not empty
          this.refundInfoModel = response.data;
        } else {
          // If response.data is null or empty
          this.refundInfoModel = [{
            refund_slip_no: '-',
            requested_by: '-',
            dt_process: null,
            rtt_id: null,
            appeal_cnt: null,
            rtt_status: '-'
          }]; // Replace with appropriate fields
        }
        console.log(this.refundInfoModel);
        this.isLoading = false;
      },
      (error) => {
        console.error('Error fetching refund info', error);
        this.isLoading = false;
      }
    );
  }

  fetchRefundHist(): void {
    this.isLoading = true;
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    const url = environment.apiUrl + '/api/refundl/v1/getRefundHist'; // API endpoint
    const requestBody = {
      i_txn_id: this.txn_id,
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
          console.log(this.refundHistModel);
        } else {
          // If response.data is null or empty
          this.refundHistModel = [{
            action: '-',
            rtt_status: '-',
            dt_action: null,
            requested_by: '-',
            pickup_by: '-',
            msg: '-',
            total: null,
            rtt_wf_hist_id: null,
            assign_to: "-",
            modified_by: "-",
            modified_by_nm: '-',
          }]; // Replace with appropriate fields
        }
        console.log(this.refundInfoModel);
        this.isLoading = false;
      },
      (error) => {
        console.error('Error fetching refund info', error);
        this.isLoading = false;
      }
    );
  }

  // Method to map actions to descriptive names
  mapAction(action: string | null): string {
    return action && this.actionMapping[action] ? this.actionMapping[action] : 'Unknown';
  }

  calculateTotalChequeAmount(): void {
    this.totalChequeAmount = this.chequePayments.reduce((sum, item) => sum + item.che_amt, 0);
  }

  calculateTotalBDAmount(): void {
    this.totalBDAmount = this.bankDraftPayments.reduce((sum, item) => sum + item.bd_amt, 0);
  }

  calculateTotalMOAmount(): void {
    this.totalMOAmount = this.moneyOrderPayments.reduce((sum, item) => sum + item.mo_amt, 0);
  }

  fetchOTCPaymentDetails(): void {
    this.isLoading = true;
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    const url = environment.apiUrl + '/api/OTCCR/v1/getOTCPaymentDetails'; // API endpoint
    const requestBody = {
      i_mtt_id: this.mtt_id,
    };

    this.http.post(url, requestBody, { headers }).subscribe(
      (response: any) => {
        this.otcPaymentDetails = response?.data || []; // Store the received data
        this.cashPayments = this.otcPaymentDetails.filter(item => item.cash_amt !== null);
        this.otcPaymentDetailsCashAmt = this.cashPayments.length > 0 ? this.cashPayments[0].cash_amt : 0;

        this.chequePayments = this.otcPaymentDetails.filter(item => item.che_amt !== null);
        this.moneyOrderPayments = this.otcPaymentDetails.filter(item => item.mo_amt !== null);
        this.bankDraftPayments = this.otcPaymentDetails.filter(item => item.bd_amt !== null);

        this.calculateTotalChequeAmount();
        this.calculateTotalBDAmount();
        this.calculateTotalMOAmount();
        this.isLoading = false;
      },
      (error) => {
        console.error('Error fetching OTC Payment Details:', error);
        this.isLoading = false;
      }
    );
  }

  checkfetchRcpt(): void {
    if (this.rms_type === 'OTC') {
      this.fetchOTCRcpt();
    } else if (this.rms_type === 'Online') {
      this.fetchOnlineRcpt();
    }
  }

  fetchOnlineRcpt(): void {
    if (this.rms_type === 'Online') {
      this.isLoading = true;
      const headers = new HttpHeaders({
        Authorization: environment.authKey,
        'Content-Type': 'application/json',
      });

      const url = environment.apiUrl + '/api/refundl/v1/getRefundPGRcpt'; // API endpoint
      const requestBody = {
        i_mtt_id: this.mtt_id,
      };

      this.http.post(url, requestBody, { headers }).subscribe(
        (response: any) => {
          this.pgRCPTModel = response?.data || []; // Store the received data
          console.log(this.pgRCPTModel);
          this.isLoading = false;
        },
        (error) => {
          console.error('Error fetching PG Rcpt:', error);
          this.isLoading = false;
        }
      );
    }
  }


  fetchOTCRcpt(): void {
    if (this.rms_type === 'OTC') {
      this.isLoading = true;
      const headers = new HttpHeaders({
        Authorization: environment.authKey,
        'Content-Type': 'application/json',
      });

      const url = environment.apiUrl + '/api/OTCCR/v1/getotccrrcpt'; // API endpoint
      const requestBody = {
        i_mtt_id: this.mtt_id,
      };

      this.http.post(url, requestBody, { headers }).subscribe(
        (response: any) => {
          this.otcRcptModel = response?.data || []; // Store the received data
          console.log(this.otcRcptModel);
          this.isLoading = false;
        },
        (error) => {
          console.error('Error fetching OTC Rcpt:', error);
          this.isLoading = false;
        }
      );
    }
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

  dlRcpt(orn_no: any) {
    if (orn_no == null)
      return;

    const generateURL = environment.apiUrl + '/api/receipt/v1/dl_rcpt';
    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json'
    });

    // Create the request body with your form data
    var requestBody: { [k: string]: any } = {
      i_orn_no: this.pgRCPTModel[0].rcptNo,
      i_mtt_id: this.mtt_id
    };
    console.log(requestBody);

    // this.http.post(generateURL, requestBody, { observe: 'response', responseType: 'blob', headers: headers })
    //   .subscribe(response => {
    //     var blob = new Blob([response.body as Blob], { type: 'pdf' });
    //     saveAs(blob, response.headers.get('content-disposition')!.split('filename=')[1]);
    //   });

    this.http.post(generateURL, requestBody, { headers }).subscribe(
      (response: any) => {
        // console.log(response.data);
        this.file_content = response.data;
        if (this.file_content != null) {
          this.downloadFileContent(this.file_content, orn_no);
        }
        if (response.data.length == 0) {
          this.totalRecords = 0;
          //this.showResultAlertBox();
          this.isLoading = false;
        } else {
          this.totalRecords = response.data[0].total;
          //   this.DefaultBox();
          this.isLoading = false;
          // this.isDisplay = true;
        }
        // console.log(response.data);
        //  console.log(this.totalRecords);
      },
      (error) => {
        console.error(error);
        this.isLoading = false;
        //this.showGenericAlertBox();
      }
    );
    const filename = 'SSM-Receipt-' + orn_no + '.pdf';
  }

  downloadFileContent(fileContent: string, orn_no: string): void {
    this.isLoading = true;
    const binaryString = window.atob(fileContent);
    const len = binaryString.length;
    const uint8Array = new Uint8Array(len);
    for (let i = 0; i < len; i++) {
      uint8Array[i] = binaryString.charCodeAt(i);
    }
    const blob = new Blob([uint8Array], { type: 'application/pdf' });
    const url = URL.createObjectURL(blob);
    const anchor = document.createElement('a');
    anchor.href = url;
    const filename = 'SSM-Receipt-' + orn_no + '.pdf';
    anchor.download = filename;
    document.body.appendChild(anchor);
    anchor.click();
    document.body.removeChild(anchor);
    URL.revokeObjectURL(url);
  }
  //postcode start

  loadPostcode() {
    // Subscribe to changes on the postcode control to auto-populate city and state.
    this.BankInfoForm.get('postcode')?.valueChanges.subscribe(selectedPostcode => {
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

  onPostcodeChange(selectedPostcode: string | null) {
    if (!selectedPostcode) {
      // Reset city and state when postcode is cleared.
      this.BankInfoForm.patchValue({ city: null, state: null });
      return;
    }

    const match = this.postCodes.find(p => String(p.postcode) === selectedPostcode);
    this.BankInfoForm.patchValue({
      city: match ? match.city : null,
      state: match ? match.state : null
    });
  }

  extractUniqueCitiesAndStates() {
    this.uniqueCities = [...new Set(this.postCodes.map(p => p.city))].sort();
    this.uniqueStates = [...new Set(this.postCodes.map(p => p.state))].sort();

    this.patchPendingCityState();
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
  // In your component
  private pendingCity: string | null = null;
  private pendingState: string | null = null;

  private patchPendingCityState() {
    if (this.pendingCity) {
      this.BankInfoForm.patchValue({ city: this.pendingCity });
      this.pendingCity = null;
    }
    if (this.pendingState) {
      this.BankInfoForm.patchValue({ state: this.pendingState });
      this.pendingState = null;
    }
    console.log('City and State patched:', this.BankInfoForm.value.city, this.BankInfoForm.value.state);
  }

}