import { Component, ViewChild } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { AuthService } from 'src/app/services/auth.service';
import { environment } from 'src/environments/environment';
import { ParamService } from '../services/param.service';
import { StatesDropdown } from '../models/state';
import { NgModel } from '@angular/forms';
import { PostCodeData } from '../models/postcode.model';

interface PaymentItemDetail {
  fee_detail_id: string;
  item_ref_no: string;
  item_desc: string;
  line_no: number;
  qty: number;
  unit_fee: number;
  gross_amt: number;
  tax_pct: number;
  tax_amt: number;
  net_amt: number;
  disc_amt: number;
  entity_type: string;
  entity_no: string;
  entity_nm: string;
}

@Component({
  selector: 'app-catalogue-json',
  templateUrl: './catalogue-json.component.html',
  styleUrls: ['./catalogue-json.component.scss']
})
export class CatalogueJsonComponent {
  @ViewChild('cityRef') cityRef!: NgModel;
  @ViewChild('stateRef') stateRef!: NgModel;

  apiURL = environment.apiUrl;
  catrunno: string = ''; // To hold the catrunno value
  selectedItem: any = null; // To hold the selected item details
  states: any[] = [];
  totalPostCodeRecords: number = 0;

  constructor(private http: HttpClient, private authService: AuthService, private ParamService: ParamService,) {
    this.submittedJson = JSON.stringify(this.paymentData, null, 2);
  }
  paymentData = {
    ss_cd: 'RMS',
    pymt_method: 'Online',
    orn_no: '',
    orn_dt: new Date().toISOString(), // Current date/time
    cust_nm: '',
    cust_addr_1: '',
    cust_addr_2: '',
    cust_addr_3: '',
    cust_postcode: '',
    cust_city: '',
    cust_state: '',
    cust_email: '',
    cust_phone: '',
    total_amt: 0.00,
    ss_return_url: environment.url + '/catalogue-product-service', // Hardcoded
    payment_item_details: [] as PaymentItemDetail[],
  };

  customerFields = [
    { id: 'cust_nm', key: 'cust_nm', label: 'Customer Name', placeholder: 'Enter customer name' },
    { id: 'cust_addr_1', key: 'cust_addr_1', label: 'Address Line 1', placeholder: 'Enter address line 1' },
    { id: 'cust_addr_2', key: 'cust_addr_2', label: 'Address Line 2', placeholder: 'Enter address line 2' },
    { id: 'cust_addr_3', key: 'cust_addr_3', label: 'Address Line 3', placeholder: 'Enter address line 3' },
    { id: 'cust_postcode', key: 'cust_postcode', label: 'Postcode', placeholder: 'Enter postcode' },
    { id: 'cust_city', key: 'cust_city', label: 'City', placeholder: 'Enter city' },
    { id: 'cust_state', key: 'cust_state', label: 'State', placeholder: 'Enter state' },
    { id: 'cust_email', key: 'cust_email', label: 'Email', placeholder: 'Enter email' },
    { id: 'cust_phone', key: 'cust_phone', label: 'Phone', placeholder: 'Enter phone' }
  ];

  entityType: StatesDropdown[] = []; // To store the API response
  uniqueCities: string[] = [];
  uniqueStates: string[] = [];
  postCodes: PostCodeData[] = [];

  recalculateAmounts(item: any) {
    // Calculate Gross Amount (Unit Fee * Quantity)
    item.gross_amt = item.unit_fee * item.qty;

    // Calculate Tax Amount (Gross Amount * Tax Percentage / 100)
    item.tax_amt = (item.gross_amt * item.tax_pct) / 100;

    // Calculate Net Amount (Gross Amount + Tax Amount)
    item.net_amt = item.gross_amt + item.tax_amt;
  }

  preventTyping(event: KeyboardEvent): void {
    // Prevent typing any key other than navigation keys
    const allowedKeys = ['Backspace', 'ArrowLeft', 'ArrowRight', 'Delete', 'Tab']; // Allow navigation and delete keys
    if (!allowedKeys.includes(event.key)) {
      event.preventDefault();
    }
  }

  submittedJson: string | null = null;
  submitForm() {
    // Convert the paymentData object to a JSON string
    this.submittedJson = JSON.stringify(this.paymentData, null, 2);
  }

  // printJson() {
  //   console.log(this.submittedJson)
  // }

  // isFormValid(): boolean {
  //   // Check if all paymentData fields are filled (excluding total_amt)
  //   const isMainDataValid = [
  //     this.paymentData.ss_cd,
  //     this.paymentData.pymt_method,
  //     this.paymentData.orn_no,
  //     this.paymentData.cust_nm,
  //     this.paymentData.cust_addr_1,
  //     this.paymentData.cust_addr_2,
  //     this.paymentData.cust_addr_3,
  //     this.paymentData.cust_postcode,
  //     this.paymentData.cust_city,
  //     this.paymentData.cust_state,
  //     this.paymentData.cust_email,
  //     this.paymentData.cust_phone
  //   ].every(field => field && field.trim().length > 0);

  //   // Check if all fields in payment_item_details are filled
  //   const areItemsValid = this.paymentData.payment_item_details.every(item =>
  //     [
  //       item.fee_detail_id,
  //       item.item_ref_no,
  //       item.item_desc,
  //       item.qty > 0, // Quantity should be greater than 0
  //       item.unit_fee > 0 // Unit fee should be greater than 0
  //     ].every(field => field)
  //   );

  //   this.submittedJson = JSON.stringify(this.paymentData, null, 2);
  //   console.log("check json:"+this.submittedJson);

  //   // Return true if both main data and items are valid
  //   return isMainDataValid && areItemsValid;
  // }

  ngOnInit() {
    this.populateUserDetails();
    this.fetchStates();
    this.loadEntityType();
    this.fetchCatNo();
    this.loadPostcode(); // Load postcode data on initialization
    // Retrieve the selectedItem from the router state
    if (history.state.selectedItem) {
      this.selectedItem = history.state.selectedItem;
      // Optionally, populate form fields with selected item details
      this.populateItemDetails(this.selectedItem);
    }
  }

  populateItemDetails(item: any) {
    // Calculate the gross amount based on the unit fee and quantity
    const grossAmount = item.unitFee * 1;  // Assuming quantity is 1 initially

    // Calculate tax amount
    const taxAmount = (grossAmount * item.taxPct) / 100;

    // Calculate net amount (gross + tax)
    const netAmount = grossAmount + taxAmount;

    // Now, push the item to the payment_item_details array with pre-calculated amounts
    this.paymentData.payment_item_details.push({
      fee_detail_id: item.feeDetailId,
      item_ref_no: 'REF' + Math.random().toString(36).substring(2, 10).toUpperCase(),
      item_desc: item.feeDetailNmE,
      line_no: 1,
      qty: 1,  // Assuming the quantity is 1 when the item is first added
      unit_fee: item.unitFee,
      gross_amt: grossAmount,
      disc_amt: 0,  // No discount
      tax_pct: item.taxPct,
      tax_amt: taxAmount,
      net_amt: netAmount,
      entity_type: '',
      entity_no: '',
      entity_nm: ''
    });
  }

  updateTotalAmount(): void {
    let total = 0;
    for (const item of this.paymentData.payment_item_details) {
      total += item.net_amt || 0;
    }
    this.paymentData.total_amt = parseFloat(total.toFixed(2)); // round to 2 decimal places
  }


  fetchCatNo(): void {
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    const url = environment.apiUrl + '/api/catalogue/v1/getcatno';

    const Body: any = {
    };

    this.http.post(url, Body, { headers }).subscribe(
      (response: any) => {
        this.catrunno = response.data;
      },
      (error) => {
        console.error(error);
      }
    );
  }

  fetchStates(): void {
    this.http.post<any>(environment.apiUrl + '/api/state/v1/getState', {
      i_page: 1,
      i_size: 20,
      i_param_id: null,
      i_param_cd: null,
      i_nm_en: null,
      i_nm_bm: null,
      i_param_grp_nm: null,
      i_seq: null,
      i_status: null
    }).subscribe(response => {
      // console.log('API Response:', response);  // Log the full API response to verify structure

      // Map the API response to extract paramCd and nmEn
      this.states = Array.isArray(response.data) ?
        response.data.map((state: any) => ({
          shortForm: state.paramCd, // paramCd for short form
          fullName: state.nmEn     // nmEn for full name
        })) : [];

      // console.log('Mapped States:', this.states); // Log the mapped states for verification
    }, error => {
      console.error('Error fetching states:', error); // Log any errors
    });
  }

  populateUserDetails() {
    this.authService.getName().subscribe({
      next: (userName) => {
        this.paymentData.cust_nm = userName;
      },
      error: (err) => console.error('Error fetching username:', err)
    });
  }

  isFormValid(): boolean {
    // Check if all paymentData fields are filled (excluding total_amt)
    const isMainDataValid = [
      this.paymentData.ss_cd,
      this.paymentData.pymt_method,
      this.paymentData.orn_no,
      this.paymentData.cust_nm,
      this.paymentData.cust_addr_1,
      this.paymentData.cust_addr_2,
      // this.paymentData.cust_addr_3,
      this.paymentData.cust_postcode,
      this.paymentData.cust_city,
      this.paymentData.cust_state,
      this.paymentData.cust_email,
      this.paymentData.cust_phone
    ].every(field => field && field.trim().length > 0);

    // Check if all fields in payment_item_details are filled correctly
    const areItemsValid = this.paymentData.payment_item_details.every(item =>
      item.fee_detail_id &&
      item.item_ref_no &&
      item.item_desc &&
      item.qty > 0 && // Ensure qty is a positive number
      item.unit_fee > 0 && // Ensure unit_fee is a positive number
      item.net_amt > 0 &&
      item.entity_nm &&
      item.entity_no &&
      item.entity_type
    );

    this.submittedJson = JSON.stringify(this.paymentData, null, 2);
    this.paymentData.orn_no = 'CTL'; // Update the catrunno
    this.updateTotalAmount(); // Update the total amount
    // console.log("JSON:", this.submittedJson);
    return isMainDataValid && areItemsValid && !this.isInvalidPostcode && !this.isPhoneInvalid && !this.isEntityNoTooLong;
  }

  isInvalidPostcode: boolean = false;

  validatePostcode(): void {
    // Check if the postcode length is exactly 5 and is numeric
    this.isInvalidPostcode = !(this.paymentData.cust_postcode.length === 5 && /^[0-9]+$/.test(this.paymentData.cust_postcode));
  }

  loadEntityType() {
    this.ParamService.getStates('1', '100', '', 'EntityType').subscribe(
      (response: any) => {
        if (response && response.data && Array.isArray(response.data)) {
          this.entityType = response.data as StatesDropdown[];
        } else {
          console.error('Invalid response format:', response);
        }
      },
      (error) => {
        console.error('There was an error retrieving the status:', error);
      }
    );
  }

  isPhoneInvalid: boolean = false;

  // checkPhoneLength(): void {
  //   this.paymentData.cust_phone?.length > 15 ? this.isPhoneTooLong = true : this.isPhoneTooLong = false;
  // }

  checkPhoneLength(): void {
    const phoneLength = this.paymentData.cust_phone?.length || 0;
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
    this.paymentData.payment_item_details.forEach(item => {
      const length = item.entity_no?.length || 0;
      this.isEntityNoTooLong = length < 5 || length > 30;
    });
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

  onPostcodeChange(selectedPostcode: string | null) {
    if (!selectedPostcode) {
      this.paymentData.cust_city = '';
      this.paymentData.cust_state = '';

      if (this.cityRef) this.cityRef.control.markAsTouched();
      if (this.stateRef) this.stateRef.control.markAsTouched();

      return;
    }

    const match = this.postCodes.find(p => String(p.postcode) === selectedPostcode);
    this.paymentData.cust_city = match ? match.city : '';
    this.paymentData.cust_state = match ? match.state : '';
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

  // Postcode End
}