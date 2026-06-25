import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Component, Inject, NgModule, OnInit, ViewChild } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { environment } from '../../environments/environment';
import { GlobalService } from '../shared/global.service';

import { ApiServiceService } from '../services/api-service.service';
import { ApiResponse } from '../models/api-response.model';
import { ApiRequest, MTT, MTTItem, ornDetails, paymentItemDetails, EmailExpiryData } from '../models/payment-info.model';
import { Router } from '@angular/router';
import { DOCUMENT, DatePipe } from '@angular/common';
import { StatesDropdown } from '../models/state';
import { GHLPayment } from '../models/ghl-request.model';
import { FormGroup, FormsModule, NgForm, NgModel } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';


@Component({
  selector: 'app-payment-page',
  templateUrl: './payment-page.component.html',
  styleUrls: ['./payment-page.component.scss'],

})


export class PaymentPageComponent implements OnInit {
  @ViewChild('billingName') billingNameControl!: NgModel;
  @ViewChild('cust_addr_1') cust_addr_1Control!: NgModel;
  @ViewChild('cust_addr_2') cust_addr_2Control!: NgModel;
  @ViewChild('cust_addr_3') cust_addr_3Control!: NgModel;
  @ViewChild('cust_postcode') cust_postcodeControl!: NgModel;
  @ViewChild('cust_city') cust_cityControl!: NgModel;
  @ViewChild('cust_state') cust_stateControl!: NgModel;

  //orn_no = String
  //emailExpiryData: string = ''; // Store the expiry date (string or Date, depending on your API)
  isExpired: boolean = false; // Flag to determine if the link is expired
  isExpiredBilling: boolean = false; // Flag to determine if the link is expired

  emailExpiryData: EmailExpiryData[] = [];// Store the expiry date (string or Date, depending on your API)
  linkExpiredMessage: string = '';

  constructor(private translate: TranslateService, private http: HttpClient,
    private globalService: GlobalService, private apiService: ApiServiceService, private router: Router,
    @Inject(DOCUMENT) private document: Document, private route: ActivatedRoute) {


    this.translate.setDefaultLang(this.globalService.getGlobalValue()); // default language
    this.translate.use(this.globalService.getGlobalValue());

  }

  warning_status!: boolean;
  waiting_status!: boolean;
  billing_status!: boolean;
  pending_status!: boolean;
  api_error: boolean = false;
  api_error_message!: string;
  paid!: boolean;
  RICPCheckError!: boolean;
  RICPCheckExistError!: boolean;
  RICPCheckStatusError!: boolean;
  RIPLCheckError!: boolean;
  RIPLCheckExistError!: boolean;
  RIPLCheckStatusError!: boolean;
  RILTCheckError!: boolean;
  RILTCheckExistError!: boolean;
  RILTCheckStatusError!: boolean;
  hidePaymentDetails!: boolean;
  unitfeeError!: boolean;
  unitfeeErrorMessage!: string;
  status!: string;
  pay_button: boolean = true;
  orn_no!: string;
  payValidation = {
    unitFee: true,
    accrual: true,
    expired: true,
    orn: true
  };
  OrnDetails1!: ornDetails;
  states: StatesDropdown[] = [];

  ghlrequest!: GHLPayment;

  paymentForm!: FormGroup;
  editMode: boolean = false;

  paymentType: string | null = null;
  billInfo: any;
  fromCatalogueProductService = false;
  apiUrl: string = '';
  requestBody!: { orn_no: string; total_amt: number };
  public environment = environment;

  async ngOnInit(): Promise<void> {
    this.editMode = false;
    // this.pay_button = true;

    await this.populateState();

    this.route.queryParams.subscribe(params => {

      localStorage.setItem('pID', params['pr']);
      this.orn_no = params['pr'];

      if (this.orn_no.startsWith('BIL') || this.orn_no.startsWith('NB') || this.orn_no.startsWith('CTL') || this.orn_no.startsWith('AGB')) {
        this.hidePaymentDetails = true;
        this.paymentType = 'non-direct-payment';
      }

      if (params['from'] === 'cat') {
        this.fromCatalogueProductService = true;
      }

      // this.paymentType = params['payment-type'];
      // this.orn_no = this.getOrnNumberFromUrl() || '';

    });

    await this.getPaymentDetail(this.orn_no);
    //this.tempHardCode();

    console.log(this.OrnDetails1);

    if (this.OrnDetails1.email_flag == 1){
      console.log('Email flag is set to 1');
      this.hidePaymentDetails = true;
      this.paymentType = 'non-direct-payment';
    }

    if (this.OrnDetails1.cust_nm != null) {
      await this.checkORN();
    }
    

  }

  async getPaymentDetail(orn_no: any): Promise<void> {
    // Construct the URL with orn_no as a query parameter
    const url = `${environment.apiUrl}/api/onlinepayment/v1/GetPaymentRequestDetailByID?param1=${orn_no}`;

    try {
      // Make the HTTP GET request without specifying headers
      const response = await this.http.get<ApiResponse<ornDetails>>(url).toPromise();
      if (response) {
        this.OrnDetails1 = response.data;
        this.validateUnitFees(); // Call the validation function after fetching the data
      }
    } catch (error) {
      //console.error(error);
      // Handle errors here
    }
  }

  async validateUnitFees(): Promise<void> {
    this.payValidation.unitFee = true;

    if (!this.OrnDetails1?.payment_item_details || this.OrnDetails1.payment_item_details.length === 0) {
      return;
    }

    for (const item of this.OrnDetails1.payment_item_details) {
      const requestBody = { fee_detail_pk: item.fee_detail_pk };
      const headers = new HttpHeaders({
        Authorization: environment.authKey,
        'Content-Type': 'application/json'
      });
      try {
        const response = await this.http.post<ApiResponse<number>>(
          `${environment.apiUrl}/api/onlinepayment/v1/getrmsfee`,
          requestBody, { headers }
        ).toPromise();

        const rmsUnitFee = response?.data;

        // ✅ Skip validation if RMS fee is 0 (use submitted value directly)
        if (rmsUnitFee === 0) {
          continue;
        }

        if (rmsUnitFee != null && Number(rmsUnitFee) !== Number(item.unit_fee)) {
          this.unitfeeErrorMessage = this.translate.instant('labels.unmatchedUnitFeeMessage', {
            item: item.item_desc,
            expected: rmsUnitFee,
            submitted: item.unit_fee
          });
          this.unitfeeError = true;
          this.payValidation.unitFee = false;
        }

      } catch (error) {
        console.error('Error validating unit fee:', error);
        this.payValidation.unitFee = false;
        this.unitfeeErrorMessage = 'Unable to validate fee. Please try again later.';
        break;
      }
    }
  }

  // tempHardCode(): void{
  //  // Sample payment item
  // const PaymentItem: paymentItemDetails = {
  //   fee_detail_id: 'FD0001',
  //   item_ref_no: 'item123',
  //   line_no: 2,
  //   item_desc: 'Sample Item',
  //   qty: 2,
  //   unit_fee: 1,
  //   tax_amt: 0,
  //   disc_amt: 0.00,
  //   gross_amt: 2.00,
  //   grant_cd: 'USD',
  //   tax_pct: 0,
  //   net_amt: 2.00,
  //   entity_type: 'C',
  //   entity_no: '12345678',
  //   entity_nm: 'Entity Name',
  //   cp_no:'',
  //   cp_tier: 0,
  //   cp_tier_amt: 0,
  //   cp_tier_disc_pct: 0,
  //   dps_id: '',
  //   dps_task: '',
  //   pymt_case: '',
  //   location: '',
  //   lit_item_ref: '',
  //   txn_type: '',
  //   calendar_yr: '',
  // };

  // const PaymentItem2: paymentItemDetails = {
  //   fee_detail_id: 'FD0001',
  //   item_ref_no: 'item123',
  //   line_no: 1,
  //   item_desc: 'Sample Item',
  //   qty: 2,
  //   unit_fee: 1,
  //   tax_amt: 0,
  //   disc_amt: 0.00,
  //   gross_amt: 2.00,
  //   grant_cd: 'USD',
  //   tax_pct: 0,
  //   net_amt: 2.00,
  //   entity_type: 'I',
  //   entity_no: 'entity123',
  //   entity_nm: 'Entity Name',
  //   cp_no:'',
  //   cp_tier: 0,
  //   cp_tier_amt: 0,
  //   cp_tier_disc_pct: 0,
  //   dps_id: '',
  //   dps_task: '',
  //   pymt_case: '',
  //   location: '',
  //   lit_item_ref: '',
  //   txn_type: '',
  //   calendar_yr: '2025',
  // };

  // // Sample ornDetails with paymentItemDetails included
  // const OrnDetails: ornDetails = {
  //   ss_cd: 'CRS',
  //   pymt_method:'Online',
  //   orn_no: 'BIL20250120000106',
  //   orn_dt: new Date(),
  //   cust_nm: 'Vicky',
  //   cust_addr_1: '123 Main Street',
  //   cust_addr_2: 'Apt 4B',
  //   cust_addr_3: 'Building C',
  //   cust_postcode: '12345',
  //   cust_city: 'Cityville',
  //   cust_state: 'KUL',
  //   cust_email: 'sytan@persys-tech.com',
  //   cust_phone: '0123456789',
  //   total_amt: 2.00,
  //   ss_return_url: 'https://ssmrms.eastasia.cloudapp.azure.com:4300',
  //   email_flag: 0,
  //   // payment_item_details: [PaymentItem,PaymentItem2], // Add the paymentItem to the array
  //   payment_item_details: [PaymentItem], // Add the paymentItem to the array
  // };

  // this.OrnDetails1=OrnDetails;
  // this.OrnDetails1.payment_item_details.sort((a, b) => a.line_no - b.line_no);
  // }

  async checkORN(): Promise<void> {
    const url = environment.apiUrl + '/api/onlinepayment/v1/sp_checkLatestOrderStatus';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json'
    });

    if (this.paymentType == 'non-direct-payment') {
      this.requestBody = { orn_no: this.orn_no, total_amt: 0 };
    }
    else {
      this.requestBody = { orn_no: this.OrnDetails1.orn_no, total_amt: this.OrnDetails1.total_amt };
    }

    try {

      const response = await this.http.post<ApiResponse<string>>(url, this.requestBody, { headers }).toPromise();
      if (response) {
        // --0:no block;
        // --1:block due to within 5min
        // --2:block due to status wrong
        // --3:block due to paid
        // --4:no block, but possible double payment
        if (response.data == "1") {
          this.waiting_status = true;
          this.pay_button = false;
          this.payValidation.orn = false;
        }

        if (response.data == "2") {
          this.billing_status = true;
          this.pay_button = false;
          this.payValidation.orn = false;
        }

        if (response.data == "3") {
          this.paid = true;
          this.pay_button = false;
          this.payValidation.orn = false;
        }

        if (response.data == "4") {
          if(this.OrnDetails1.email_flag == 1){
            await this.getMttDtEmailExpiry();
          }
          this.pending_status = true;
          this.pay_button = true;
          this.payValidation.orn = true;
        }

        if (response.data == "0") {
          // this.pay_button = false;
          await this.checkAccrual();
        }
      }

    } catch (error) {
      this.pay_button = true;
      this.payValidation.orn = true;
      //console.error(error);
    }

  }

  async populateState(): Promise<void> {
    const url = environment.apiUrl + '/api/rms/v1/getparam';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json'
    });

    const requestBody = {
      i_page: 1,
      i_size: 100,
      i_param_cd: "",
      i_param_grp_nm: "State"
    };

    this.http.post(url, requestBody, { headers }).subscribe(
      (response: any) => {
        //console.log(response);
        // You can process the response data here
        this.states = response.data;
        //console.log(this.states)
      },
      (error) => {
        console.error(error);
        // Handle errors here
      }
    );
  }

  PayOnline(): void {

    this.pay_button = true;
    this.payValidation.orn = true;

    if (this.OrnDetails1.email_flag == 1 && this.paymentType == 'non-direct-payment' && this.OrnDetails1.order_status != 'ES'  && this.OrnDetails1.order_status != 'PP'  && this.OrnDetails1.order_status != 'PIP' && this.OrnDetails1.order_status != 'F') {
      this.apiUrl = environment.apiUrl + '/api/onlinepayment/v1/sp_insertPaymentEmail';
    } else {
      this.apiUrl = environment.apiUrl + '/api/onlinepayment/v1/sp_insertPayment';
    }

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json'
    });

    // Create the request body with your form data
    this.OrnDetails1.pymt_method = 'Online';
    const requestBody = {
      ornDetails: this.OrnDetails1
    };

    // Send an HTTP POST request to the API
    this.http.post<ApiResponse<GHLPayment>>(this.apiUrl, this.OrnDetails1, { headers }).subscribe(
      (response) => {
        if (response.header.statusCode == '01') {
          //console.error('API error:', response.header.message);
          this.api_error_message = response.header.message;
          this.api_error = true;

        } else {
          this.ghlrequest = response.data;

          if (this.OrnDetails1.total_amt == 0) {
            this.router.navigate(['/payment-response'], {
              queryParams: {
                orn_no: this.orn_no,
                pymt_status: this.ghlrequest.order_status,
                rcpt_no: this.ghlrequest.rcpt_no,
                rcpt_dt: this.ghlrequest.rcpt_dt,
                ss_return_url: this.ghlrequest.ss_return_url
              }
            });
            //this.router.navigate(['/payment-response?orn_no='+this.orn_no+'&pymt_status='+this.ghlrequest.order_status+'&rcpt_no='+this.ghlrequest.rcpt_no+'&rcpt_dt='+this.ghlrequest.rcpt_dt+'']);
            //this.OrnDetails1.ss_return_url+'?orn_no='+this.orn_no+'&pymt_status='+this.OrnDetails1.order_status+'&rcpt_no=&rcpt_dt=';
          // } else if (this.OrnDetails1.email_flag == 1 && this.paymentType == 'non-direct-payment' && this.OrnDetails1.order_status != 'ES'  && this.OrnDetails1.order_status != 'F') {
          } else if (this.OrnDetails1.email_flag == 1 && this.paymentType == 'non-direct-payment' && this.OrnDetails1.order_status == 'EP') {
            // document.location.href = this.OrnDetails1.ss_return_url+'?orn_no='+this.orn_no+'&pymt_status='+this.OrnDetails1.order_status+'&rcpt_no=&rcpt_dt=';
            document.location.href = this.OrnDetails1.ss_return_url + '?orn_no=' + this.orn_no + '&pymt_status=EP&rcpt_no=&rcpt_dt=';
          }
          else {
            //this.ghlrequest=response.data;
            this.router.navigate(['/ghl', { ghlrequest: JSON.stringify(this.ghlrequest) }]);
          }

        }

      },
      (error) => {
        console.error('API error:', error);
        this.api_error_message = error.header.message; //to display error message
        this.api_error = true;
        // Handle API errors (e.g., show an error message)
      }
    );

    this.pay_button = false;
    this.payValidation.orn = false;

  }

  Cancel(): void {
    this.document.location.href = this.OrnDetails1.ss_return_url;
  }

  //form handle before submit start
  handleFormSubmit(form: NgForm) {

    if (form.valid || form.status == "DISABLED") {
      this.PayOnline();
    } else {
      Object.values(form.controls).forEach((control) => {
        control.markAsTouched();
      });
    }
  }
  //form handle before submit end

  async checkAccrual(): Promise<void> {
    const url = environment.apiUrl + '/api/onlinepayment/v1/sp_checkAccrual';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json'
    });

    const requestBody = { payment_item_details: this.OrnDetails1.payment_item_details };

    try {

      const response = await this.http.post<ApiResponse<string>>(url, requestBody, { headers }).toPromise();
      if (response) {
        if (response.header.statusCode == "00") {
          if (response.data == "1") {
            this.RICPCheckExistError = true;
            this.RICPCheckError = true;
            this.pay_button = false;
            this.payValidation.accrual = false;
          }

          if (response.data == "2") {
            this.RICPCheckStatusError = true;
            this.RICPCheckError = true;
            this.pay_button = false;
            this.payValidation.accrual = false;
          }

          if (response.data == "3") {
            this.RIPLCheckExistError = true;
            this.RIPLCheckError = true;
            this.pay_button = false;
            this.payValidation.accrual = false;
          }

          if (response.data == "4") {
            this.RIPLCheckStatusError = true;
            this.RIPLCheckError = true;
            this.pay_button = false;
            this.payValidation.accrual = false;
          }

          if (response.data == "5") {
            this.RILTCheckExistError = true;
            this.RILTCheckError = true;
            this.pay_button = false;
            this.payValidation.accrual = false;
          }
          if (response.data == "6") {
            this.RILTCheckStatusError = true;
            this.RILTCheckError = true;
            this.pay_button = false;
            this.payValidation.accrual = false;
          }

          if (response.data == "0") {
            console.log(this.paymentType, 'paymentType');
            console.log(this.fromCatalogueProductService, 'fromCatalogueProductService');
            // this.pay_button = false;
            if (this.paymentType === 'non-direct-payment') {
              if (!this.fromCatalogueProductService) {
                await this.getMttDtEmailExpiry(); // only call this if NOT from catalogue-product-service
              }
            } else {
              this.pay_button = true;
              this.payValidation.accrual = true;
            }
          }

        } else {
          this.api_error = true;
          this.api_error_message = response.header.message;
          this.pay_button = true;
          this.payValidation.accrual = true;
        }

      }

    } catch (error) {
      //console.error(error);
    }
  }

  // link expired
  async getMttDtEmailExpiry(): Promise<void> {
    const url = environment.apiUrl + '/api/OTCRC/v1/mttemaildtexpiry';
    try {
      const headers = new HttpHeaders({
        Authorization: environment.authKey,
        'Content-Type': 'application/json',
      });

      const requestBody = {
        i_orn_no: this.orn_no // Use the orn_no from the component
      };

      const response = await this.http.post<ApiResponse<any>>(url, requestBody, { headers }).toPromise();
      if (response) {
        this.emailExpiryData = response.data; // Store the response data
        console.log('Email Expiry Data:', this.emailExpiryData);
        this.checkIfExpired(this.emailExpiryData[0].dt_email_expiry);
      }
    } catch (error) {
      console.error('Error fetching email expiry data:', error);
    }
  }

  checkIfExpired(expiryDate: string | null | undefined): void {
    if (expiryDate == null || expiryDate === '') {
      // If expiry date is null/undefined/empty, skip checking, allow payment
      this.payValidation.expired = true;
      return;
    }

    const currentDate = new Date();
    const expiry = new Date(expiryDate);

    if (this.orn_no.startsWith('BIL') || this.orn_no.startsWith('NB')) {
      this.isExpiredBilling = currentDate > expiry;
    }
    else {
      this.isExpired = currentDate >= expiry; // Check if the current date is greater than or equal to the expiry date
    }

    if(this.isExpired || this.isExpiredBilling){
      this.payValidation.expired = false;
    }
  }

  get isPayButtonEnabled(): boolean {
    return this.payValidation.unitFee && this.payValidation.accrual && this.payValidation.expired && this.payValidation.orn && this.OrnDetails1.payment_item_details !=null
            && !this.hasInvalidBillingInfo();
  }

  hasInvalidBillingInfo(): boolean {
      return this.isFieldInvalid(this.OrnDetails1?.cust_addr_1) ||
            this.isFieldInvalid(this.OrnDetails1?.cust_addr_2) ||
            this.isFieldInvalid(this.OrnDetails1?.cust_city) ||
            this.isFieldInvalid(this.OrnDetails1?.cust_postcode) ||
            this.isFieldInvalid(this.OrnDetails1?.cust_state) ||
            this.isFieldInvalid(this.OrnDetails1?.cust_nm);
  }

  isFieldInvalid(value: string | null | undefined): boolean {
      return value === 'N/A' || value === '' || value == null;
  }

}