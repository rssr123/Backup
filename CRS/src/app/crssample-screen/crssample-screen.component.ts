// import { formatDate } from '@angular/common';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Component } from '@angular/core';
import { Router } from '@angular/router';
// import { ICRSPayment } from '../interfaces/icrspayment';
// import { Irmspayment } from '../interfaces/irmspayment';
import { environment } from 'src/environments/environment';

@Component({
  selector: 'app-crssample-screen',
  templateUrl: './crssample-screen.component.html',
  styleUrls: ['./crssample-screen.component.scss']
})
export class CRSSampleScreenComponent {
  idNo: number = 3;
  apiURL = environment.apiUrl;
  postURL:String="";

  ngOnInit() {
    this.fetchFeeDetails();
    // this.initializeOrnNo();
    // this.changePaymentData();
  }

  constructor(private http: HttpClient, private router: Router) {
    console.log('Constructor before called');
    // this.basePaymentData = this.initializePaymentDetailsByTable();
    console.log('Constructor called');
    // const jsonData = JSON.stringify(this.basePaymentData);
    // this.jsonData = JSON.stringify(this.basePaymentData);
    // console.log(jsonData);
  }

  payOnline(): void {
    // const apiEndpoint = 'https://appsdev.ssm4u.com.my/rmsrest/api/onlinepayment/v1/rms_paymentPage';
    // const apiEndpoint = 'https://52.163.62.7:1443/rmsrest/api/onlinepayment/v1/rms_paymentPage';
    // const apiEndpoint = 'https://rmsdev.ssm4u.com.my/rmsrest/api/onlinepayment/v1/rms_paymentPage';
    // const apiEndpoint = 'https://52.187.113.151:8443/rmsrest/api/onlinepayment/v1/rms_paymentPage';
    const apiEndpoint = environment.apiUrl+ '/api/onlinepayment/v1/rms_paymentPage';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: 'Basic cm95OnBhc3M='
      // 'Content-Type': 'application/json'

    });
    const formData = new FormData();
    formData.append('jsonData', JSON.stringify(this.paymentData, null, 2));

    console.log('Value of forrmdate : ' + formData);
    // this.http.post(apiEndpoint, formData, { headers, responseType: 'text' }).subscribe(
    this.http.post(apiEndpoint, formData, { headers }).subscribe(
      response => {
        // Since the response is a URL, navigate to it
        // window.location.href = response;
      },
      error => {
        console.error('Payment error:', error);
      }
    );
  }

  paymentData = {
    ss_cd: 'CRS',
    pymt_method: 'Online',
    orn_no: 'CRS' + new Date().toISOString().replace(/[^0-9]/g, '').substring(0, 8) + Math.floor(Math.random() * 1000000),
    // orn_dt: '2023-10-23T10:55:30.123Z',
    orn_dt: new Date().toISOString(),
    cust_nm: 'Muhammad Ali',
    cust_addr_1: '1',
    cust_addr_2: 'Jalan Merdeka',
    cust_addr_3: 'Taman Merdeka',
    cust_postcode: '56100',
    cust_city: 'Cheras',
    cust_state: 'KUL',
    cust_email: 'roy.low@mtechnologies.com.my',
    cust_phone: '0123456789',
    total_amt: 0.00,
    // ss_return_url: 'https://52.163.62.7:1443/mockcrs/crsstatus',
    // ss_return_url: 'https://52.187.113.151:8443/mockcrs/crsstatus',
    ss_return_url: environment.env+'/crsstatus',
    ss_callback_url: environment.env+'/crsstatus',
    email_flag: '0',
    collection_slip:'',
    payment_item_details: [
      {
        fee_detail_id: '',
        item_ref_no: 'REF001',
        item_desc: '',
        line_no: 1,
        qty: 1,
        unit_fee: 0.00,
        gross_amt: 0.00,
        grant_cd: null,
        disc_amt: 0.00,
        tax_pct: 0.00,
        tax_amt: 0.00,
        net_amt: 0.00,
        entity_type: 'I',
        entity_no: 'ENT001',
        entity_nm: 'MUHAMMAD ALI',
        cp_no: '',
        cp_tier: 0,
        cp_tier_amt: 0.00,
        cp_tier_disc_pct: 0.00,
        dps_id: '',
        dps_task: '',
        pymt_case: '',
        location: '',
        lit_item_ref: '',
        txn_type: '',
        calendar_yr: '2025'
      }
    ]
  };

  addNewItem() {
    this.paymentData.payment_item_details.push({
      fee_detail_id: '',
      item_ref_no: '',
      item_desc: '',
      line_no: 0,
      qty: 1,
      unit_fee: 0.00,
      gross_amt: 0.00,
      grant_cd: null,
      disc_amt: 0.00,
      tax_pct: 0.0,
      tax_amt: 0.0,
      net_amt: 0.00,
      entity_type: '',
      entity_no: '',
      entity_nm: '',
      cp_no: '',
      cp_tier: 0,
      cp_tier_amt: 0.00,
      cp_tier_disc_pct: 0.00,
      dps_id: '',
      dps_task: '',
      pymt_case: '',
      location: '',
      lit_item_ref: '',
      txn_type: '',
      calendar_yr: '2025',
  
    });
    this.updateLineNumbers();
  }
  
  removeItem(index: number) {
    this.paymentData.payment_item_details.splice(index, 1);
    this.updateLineNumbers();
    this.updateTotalAmount();
  }
  
  updateLineNumbers() {
    this.paymentData.payment_item_details.forEach((item, index) => {
      item.line_no = index + 1; // Assign line_no dynamically
    });
  }
  
  recalculateAmounts(index: number): void {
    const item = this.paymentData.payment_item_details[index];
  
    // Calculate Gross Amount (Unit Fee * Quantity)
    item.gross_amt = item.unit_fee * item.qty;
  
    // Calculate Tax Amount (Gross Amount * Tax Percentage / 100)
    item.tax_amt = (item.gross_amt * item.tax_pct) / 100;
  
    // Calculate Net Amount (Gross Amount + Tax Amount)
    item.net_amt = item.gross_amt + item.tax_amt - item.disc_amt;
  
    // Update Total Amount (sum of all net amounts)
    this.updateTotalAmount();
  }
  
  // Update Total Amount for all items
  updateTotalAmount(): void {
    this.paymentData.total_amt = this.paymentData.payment_item_details.reduce(
      (sum, item) => sum + item.net_amt,
      0
    );
  }
  
  preventTyping(event: KeyboardEvent): void {
    // Prevent typing any key other than navigation keys
    const allowedKeys = ['Backspace', 'ArrowLeft', 'ArrowRight', 'Delete', 'Tab']; // Allow navigation and delete keys
    if (!allowedKeys.includes(event.key)) {
        event.preventDefault();
    }
  }
  
  submittedJson: string | null = null;
  checkJson() {
    // Convert the paymentData object to a JSON string
    this.submittedJson = JSON.stringify(this.paymentData, null, 2);
  }

  submitForm(){
    const url = environment.apiUrl + '/api/onlinepayment/v1/otcPayment';
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    this.http.post(url, this.submittedJson, { headers }).subscribe(
      (response: any) => {
        this.router.navigate(['/otc',response.header.message]);
      },
      (error) => {
        console.error('Error fetching fee details:', error);
      }
    );


  }
  
  feeDetailOptions: any[] = []; // Stores fee details from API
  
  fetchFeeDetails() {
    const url = environment.apiUrl + '/api/mft/v1/getfeedetailitems';
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });
  
    const requestBody = {
      fee_detail_id: null,
      fee_grp_id: null,
      ss_cd: 'CRS',
      last_sync_dt: null,
      exclude_deleted: 1
    };
  
    this.http.post(url, requestBody, { headers }).subscribe(
      (response: any) => {
        this.feeDetailOptions = response.data;
              // Set the first fee_detail_id as default
              if (this.feeDetailOptions.length > 0) {
                this.paymentData.payment_item_details[0].fee_detail_id = this.feeDetailOptions[0].fee_detail_id;
                
                // Trigger change event to auto-populate fields
                this.onFeeDetailChange({ target: { value: this.feeDetailOptions[0].fee_detail_id } }, this.paymentData.payment_item_details[0]);
              }
      },
      (error) => {
        console.error('Error fetching fee details:', error);
      }
    );
  }
  
  onFeeDetailChange(event: any, item: any) {
    const selectedFeeDetailId = event.target.value;
    
    // Find the selected fee detail object from feeDetailOptions
    const selectedFeeDetail = this.feeDetailOptions.find(fee => fee.fee_detail_id === selectedFeeDetailId);
  
    if (selectedFeeDetail) {
      // Auto-populate fields in the current row
      item.unit_fee = selectedFeeDetail.unit_fee || 0;
      item.tax_pct = selectedFeeDetail.tax_pct || 0;
      item.item_desc = selectedFeeDetail.fee_detail_nm_en || '';
      // item.grant_cd = selectedFeeDetail.ledger_cd || '';
      
      // If promo fee is available, override unit_fee
      // if (selectedFeeDetail.promo_fee !== null) {
      //   item.unit_fee = selectedFeeDetail.promo_fee;
      // }
  
      // Recalculate amounts after fee detail is selected
      this.recalculateAmounts(this.paymentData.payment_item_details.indexOf(item));
    }
  }
  
  isFormValid(): boolean {
    // Check if all paymentData fields are filled (excluding total_amt)
    const isMainDataValid = [
      this.paymentData.ss_cd,
      // this.paymentData.pymt_method,
      this.paymentData.orn_no,
      // this.paymentData.cust_nm,
      // this.paymentData.cust_addr_1,
      // this.paymentData.cust_addr_2,
      // // this.paymentData.cust_addr_3,
      // this.paymentData.cust_postcode,
      // this.paymentData.cust_city,
      // this.paymentData.cust_state,
      // this.paymentData.cust_email,
      // this.paymentData.cust_phone
    ].every(field => field && field.trim().length > 0);
  
    // Check if all fields in payment_item_details are filled
    const areItemsValid = this.paymentData.payment_item_details.every(item =>
      [
        item.fee_detail_id,
        item.item_ref_no,
        item.item_desc,
        item.qty > 0, // Quantity should be greater than 0
        item.unit_fee > 0 // Unit fee should be greater than 0
      ].every(field => field)
    );

    this.submittedJson = JSON.stringify(this.paymentData, null, 2);
  
    // Return true if both main data and items are valid
    return isMainDataValid && areItemsValid;
  }

  test(idNo: number) {
    this.idNo = idNo;
    // this.changePaymentData();
  }

  onPaymentMethodChange(): void {
    if (this.paymentData.pymt_method === 'OTC') {
      const today = new Date();
      const yyyyMMdd = today.toISOString().split('T')[0].replace(/-/g, '');
      const randomSixDigits = Math.floor(100000 + Math.random() * 900000);
      this.paymentData.collection_slip = `COL${yyyyMMdd}${randomSixDigits}`;
    } else {
      // Optional: clear the field if Online is selected
      this.paymentData.collection_slip = '';
    }
  }

//   paymentData: ICRSPayment[] = [];
//   totalAmount: number = 0;
//   email: string = "";
//   cp_no: string = "";
//   cp_tier: string = "";
//   cp_tier_amt: string = "";
//   cp_tier_disc_pct: string = "";

//   /*paymentData: ICRSPayment[] = [
//     { id: 1, description: 'LLP Reservation of Name / Name Search', quantity: 1, amount: 30.00, tax: 1.80, discount: 0.00, grossAmount: 31.8 },
//     { id: 2, description: 'Company Registration', quantity: 1, amount: 30.00, tax: 1.80, discount: 0.00, grossAmount: 31.8 },
//     { id: 3, description: 'Business Information Search', quantity: 1, amount: 30.00, tax: 1.80, discount: 0.00, grossAmount: 31.8 },
//   ];*/

//   changePaymentData() {
//     if (this.idNo === 1) {
//       this.paymentData = [
//         { id: 1, description: 'Fee for (30 Days) Name Reservation', quantity: 1, amount: 50, tax: 0, discount: 0.00, grossAmount: 50 }
//       ];
//     }
//     else if (this.idNo === 2) {
//       this.paymentData = [
//         { id: 2, description: 'Fee for Incorporation from Reserved Name', quantity: 1, amount: 1001.80, tax: 1.80, discount: 0.00, grossAmount: 1000 }
//       ];
//     }
//     else{
//       this.paymentData = [
//         { id: 1, description: 'Fee for (30 Days) Name Reservation', quantity: 1, amount: 50, tax: 0, discount: 0.00, grossAmount: 50 },
//         { id: 2, description: 'Fee for Incorporation from Reserved Name', quantity: 1, amount: 1001.80, tax: 1.80, discount: 0.00, grossAmount: 1000 },
//         { id: 3, description: 'Fee for Renewal of Practising Certificate (1 year)', quantity: 3, amount: 301.80, tax: 1.80, discount: 0.00, grossAmount: 300 },
//       ];
//     }

//     this.totalAmount = 0;

//     for(let value of this.paymentData){
//       this.totalAmount += value.grossAmount;
//     }

//     console.log(this.paymentData);

//    console.log('Calculated total is ' +this.totalAmount);
//    this.updateBasePaymentData();

//   }

//   latestOrn: string | null = null;

//   // Declare and initialize basePaymentData
//   basePaymentData: Irmspayment = {
//     ss_cd: 'crs',
//     orn_no: '',
//     orn_dt: new Date().toISOString(),
//     cust_nm: 'Rica Tan',
//     cust_addr_1: '122, Jalan 1/23, Taman Bukit Cheras',
//     cust_addr_2: 'Taman Bukit Cheras',
//     cust_addr_3: 'Cheras',
//     cust_postcode: '56000',
//     cust_city: 'KL',
//     cust_state: 'KUL',

//    // cust_email: 'sytan@persys-tech.com',
//     cust_email: '',
//     cust_phone: '01123456781',
//     //total_amt: '95.4',
//     total_amt: '0',
//     // ss_return_url: 'https://appsdev.ssm4u.com.my/mockcrs/crsstatus',
//     ss_return_url: 'https://52.163.62.7:1443/mockcrs/crsstatus',
//     payment_item_details: []  // Empty initially, we will populate it below
//   };
//   jsonData: string;

//   onOrnNoChange(updatedValue: string): void {
//     // Update the orn_no property in basePaymentData
//     this.basePaymentData.orn_no = updatedValue;

//     // Then update the jsonData property
//     this.updateJsonData();
//   }
//   updateJsonData(): void {
//     this.jsonData = JSON.stringify(this.basePaymentData);
//   }

//   initializeBasePaymentData(): Irmspayment {
//     for (let i = 1; i <= 2; i++) {
//       this.basePaymentData.payment_item_details.push({
//         fee_detail_id: 'FD0001', // You'll need logic to set this if it varies
//         item_ref_no: i.toString(),
//         item_desc: `LLP Reservation of Name / Name Search${i}`,
//         line_no: i.toString(),
//         qty: '1',
//         unit_fee: '30.00',
//         gross_amt: '31.8',
//         grant_cd: '1', // Static value as per your example, adjust if needed
//         disc_amt: '0.00',
//         tax_pct: '0', // Assuming 0% tax, adjust if needed
//         tax_amt: '1.80',
//         net_amt: '0', // Assuming 0 net amount, adjust if needed
//         entity_type: 'I', // Static value, adjust if needed
//         entity_no: '990123104567', // Static value, adjust if needed
//         entity_nm: '1', // Static value, adjust if needed
//         cp_no: 'CP0022', // Static value, adjust if needed
//         cp_tier: '1', // Static value, adjust if needed
//         cp_tier_amt: '1', // Static value, adjust if needed
//         cp_tier_disc_pct: '0', // Static value, adjust if needed
//       });
//     }

//     return this.basePaymentData;


//   }

//   getFeeDetailId(description: string): string {
//     switch (description) {
//       case 'Fee for (30 Days) Name Reservation':
//         return 'FD1-1';
//       case 'Fee for Incorporation from Reserved Name':
//         return 'FD2-3';
//       case 'Fee for Renewal of Practising Certificate (1 year)':
//         return 'FD4-5';
//       default:
//         return 'FD0001'; // Default value if none of the cases match
//     }
//   }

//   // Method to initialize basePaymentData based on paymentData
//   initializePaymentDetailsByTable(): Irmspayment {
//     // Clear the existing payment_item_details array
//     this.basePaymentData.payment_item_details = [];

//     // Loop through paymentData and push items to payment_item_details
//     console.log(this.paymentData);

//     let counter = 1;

//     this.paymentData.forEach((paymentItem, index) => {
//       const timestamp = new Date().getTime(); // Get current timestamp in milliseconds

//       // Append the counter to the timestamp to ensure uniqueness and convert to string
//       const uniqueItemRefNo = `${timestamp}${counter}`;

//       this.basePaymentData.payment_item_details.push({
//         fee_detail_id: this.getFeeDetailId(paymentItem.description), // Assuming 'id' can be used for 'fee_detail_id'
//         item_ref_no: uniqueItemRefNo,
//         item_desc: paymentItem.description,
//         line_no: (index + 1).toString(), // Line number could just be the index + 1
//         qty: paymentItem.quantity.toString(),
//         unit_fee: paymentItem.amount.toString(),
//         gross_amt: paymentItem.grossAmount.toString(),
//         grant_cd: '1', // Static value as per your example, adjust if needed
//         disc_amt: paymentItem.discount.toString(),
//         tax_pct: '0', // Assuming 0% tax, adjust if needed
//         tax_amt: paymentItem.tax.toString(),
//         net_amt: (paymentItem.amount - paymentItem.discount + paymentItem.tax).toString(), // Assuming this is how net_amt is calculated
//         entity_type: 'I', // Static value, adjust if needed
//         entity_no: '990123104567', // Static value, adjust if needed
//         entity_nm: '1', // Static value, adjust if needed
//         cp_no: this.cp_no,
//         cp_tier: this.cp_tier, // Static value, adjust if needed
//         cp_tier_amt: this.cp_tier_amt, // Static value, adjust if needed
//         cp_tier_disc_pct: this.cp_tier_disc_pct, // Static value, adjust if needed
//       });

//       counter++;
//     });

//     console.log(this.basePaymentData);

//     return this.basePaymentData;
//   }

//   get totalGrossAmount(): number {
//     return this.paymentData.reduce((acc, item) => acc + item.grossAmount, 0);
// }
 



//   showOrn = false; // Property to track visibility of ORN

//   toggleOrnDisplay() {
//     this.showOrn = !this.showOrn; // Toggle the visibility
//   }

//   onOrnNoChange2(updatedValue: string): void {


//     // Update the orn_no property in basePaymentData
//     this.basePaymentData.orn_no = updatedValue;

//     // Update the cookie with the new orn_no value
//     const today = new Date();
//     const dateString = formatDate(today, 'yyyyMMdd', 'en-US');

//     this.saveToLocalStorage('lastOrnNo', `${dateString}:${updatedValue}`);
//     // this.setCookie('lastOrnNo', `${dateString}:${updatedValue}`, 1);

//     console.log(this.basePaymentData.orn_no);

//     // Then update the jsonData property
//     this.updateJsonData();
//   }


//   // Method to initialize or retrieve the running number from cookies
//   initializeOrnNo(): void {
//     const today = new Date();
//     const dateString = formatDate(today, 'yyyyMMdd', 'en-US');
//     let runningNumber = 1;

//     // const cookieValue = this.getCookie('lastOrnNo');

//     const cookieValue = this.getFromLocalStorage('lastOrnNo');


//     console.log("getvalue" + cookieValue);
//     if (cookieValue) {
//       const prefixLength = 3; // Length of 'CRS'
//       const dateLength = 8; // Length of the date part (e.g., '20240116')
//       const storedDate = cookieValue.substring(prefixLength, prefixLength + dateLength); // Extract date part
//       const storedNumber = cookieValue.substring(prefixLength + dateLength); // Extract running number part

//       if (storedDate === dateString) {
//         runningNumber = parseInt(storedNumber) + 1;
//       }
//     }


//     this.saveToLocalStorage('lastOrnNo', `${dateString}:${runningNumber}`);

//     // Update the cookie
//     //this.setCookie('lastOrnNo', `${dateString}:${runningNumber}`, 1); // Expires in 1 day

//     // Set orn_no in basePaymentData
//     this.latestOrn = `CRS${dateString}${runningNumber}`;
//     this.basePaymentData.orn_no = this.latestOrn;
//     this.updateJsonData();
//   }

//   // Helper methods to set and get cookies
//   setCookie(name: string, value: string, days: number): void {
//     const d = new Date();
//     d.setTime(d.getTime() + (days * 24 * 60 * 60 * 1000));
//     let expires = "expires=" + d.toUTCString();
//     document.cookie = name + "=" + value + ";" + expires + ";path=/";
//   }

//   getCookie(name: string): string | null {
//     let ca = document.cookie.split(';');
//     let caLen = ca.length;
//     let cookieName = `${name}=`;
//     let c: string;

//     for (let i = 0; i < caLen; i += 1) {
//       c = ca[i].replace(/^\s+/g, '');
//       if (c.indexOf(cookieName) === 0) {
//         return c.substring(cookieName.length, c.length);
//       }
//     }
//     return null;
//   }

//   // Method to save the orn_no to localStorage
//   saveToLocalStorage(key: string, value: string): void {
//     localStorage.setItem(key, value);
//   }

//   // Method to retrieve the orn_no from localStorage
//   getFromLocalStorage(key: string): string | null {
//     return localStorage.getItem(key);
//   }



//   updateBasePaymentData(){
//     this.basePaymentData.cust_email = this.email;
//     this.basePaymentData.total_amt = this.totalAmount.toString();
//     this.basePaymentData = this.initializePaymentDetailsByTable();
//     this.jsonData = JSON.stringify(this.basePaymentData);
//     this.updateJsonData();
//   }

//   onEmailChange(){
//     this.updateBasePaymentData();
//   }

//   updateCPValues(): void {
//     const targetDescription = 'Fee for Incorporation from Reserved Name';
  
//     // Find the item with the specific description
//     const targetItem = this.basePaymentData.payment_item_details.find(item => item.item_desc === targetDescription);
  
//     if (targetItem) {
//       // Update the CP values only for the target item
//       targetItem.cp_no = this.cp_no;
//       targetItem.cp_tier = this.cp_tier;
//       targetItem.cp_tier_amt = this.cp_tier_amt;
//       targetItem.cp_tier_disc_pct = this.cp_tier_disc_pct;
  
//       // Update jsonData after making changes
//       this.updateJsonData();
  
//       console.log('Updated jsonData:', this.jsonData);
//     } else {
//       console.warn(`Item with description '${targetDescription}' not found.`);
//     }
//   }
  

//   onCPChange(){
//     this.updateCPValues();
//   }

}