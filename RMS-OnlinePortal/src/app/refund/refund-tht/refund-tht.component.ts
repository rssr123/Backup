import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { formatDate } from '@angular/common';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { NgbPaginationConfig } from '@ng-bootstrap/ng-bootstrap';
import { TranslateService } from '@ngx-translate/core';
import { GlobalService } from 'src/app/shared/global.service';
import { environment } from 'src/environments/environment';
import { AuthService } from 'src/app/services/auth.service';
import { RefundPTT } from 'src/app/models/refundptt-interface';
@Component({
  selector: 'app-refund-tht',
  templateUrl: './refund-tht.component.html',
  styleUrls: ['./refund-tht.component.scss']
})
export class RefundTHTComponent implements OnInit {

  page = environment.DefaultPage;
  itemsPerPage = environment.ItemPerPage;

  searchForm!: FormGroup;
  inputValue: string = '';

   //default pagination
   selectedValue = environment.dropdownOptions[0];
   dropdownOptions = environment.dropdownOptions;
   dropDownSize = environment.DropDownSize;

  model: RefundPTT[] = [];
  searchModel: any[] = [];
  totalRecords: number = 0;

  orn_no: String | null = null;
  orn_dt: String | null = null;
  total_amt: String | null = null;
  rcpt_no: String | null = null;
  rcpt_dt: String | null = null;
  mtt_id: number | null = null;
  rms_type: string | null = null;
  total: String | null = null;
  status: string | null = null;
  nm: string | null = null;
  ent_no: string | null = null;
  ent_nm: string | null = null;
  txn_id: string | null = null;
  invalidInput: boolean = false;
  isDisplay: boolean = false;
  isLoading: boolean = false;
  selectedState: string | null = null;
  selectedOption: string | null = null; // Holds the selected option
  inputName: string = ''; // Dynamically updated input name
  checkResult: number = 0;
  isSearchOptionSelected = false;

  constructor(private fb: FormBuilder, private http: HttpClient, private config: NgbPaginationConfig, private router: Router) { }

  ngOnInit(): void {
    this.searchForm = this.fb.group({
      searchOption: ['', Validators.required],
      inputName: ['', Validators.required]
    });
    this.searchForm.get('searchOption')?.setValue(null);
   
    this.searchBy();
    this.loadData();
  }

  searchBy(){
    this.searchModel=[
      {nm:'Order Reference No.', value:'i_orn_no'},
      {nm:'Receipt No.', value:'i_rcpt_no'},
      {nm:'Transaction ID', value:'i_txn_id'},
      {nm:'Refund Slip No.', value:'i_refund_slip_no'},
      {nm:'Refund Application No.', value:'i_rtt_app_no'},
    ];
    console.log(this.searchModel);
  }

  onSelectionChange(event: Event): void {
    const selectedValue = (event.target as HTMLSelectElement).value;
    this.selectedOption = selectedValue;
    this.isSearchOptionSelected = !!selectedValue; // Show the input field if a value is selected
     console.log('Selected value:', selectedValue);
  }




  LoadData(singleItem: number) {
    this.selectedValue = singleItem;
    this.itemsPerPage = this.selectedValue;
    this.loadData();
  }


  handleFormSubmit(): void {
    if (this.searchForm.valid) {
      this.inputName = this.searchForm.value.inputName;
      this.loadData();
    } else {
      this.invalidInput = true;
    }
  }

  loadData() {
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });
  
    this.isDisplay = true;
    this.isLoading = true;
    const url = environment.apiUrl + '/api/refundl/v1/getrefundtht';
  
    // Initialize the Body object
    let Body: any = {
      i_page: this.page.toString(),
      i_size: this.itemsPerPage.toString(),
      i_platform_call: 'pp',
    };
  
    if (this.inputName && this.inputName.trim()) {
      // Add the dynamic property to Body
      if(this.selectedOption !== null && this.selectedOption != undefined){
      Body[this.selectedOption] = this.inputName;
      }
    }
  
    console.log('Body:', Body);
  
    // Perform the POST request
    this.http.post(url, Body, { headers }).subscribe(
      (response: any) => {
        this.model = response.data;
        console.log(response.data);
        this.totalRecords = response.data.length > 0 ? response.data[0].total : 0;
        this.isLoading = false;
      },
      (error) => {
        console.error(error);
        this.isLoading = false;
      }
    );
  }
  

  clearFields() {
    this.searchForm.reset();
    this.selectedOption = null;
    
    this.isSearchOptionSelected = false;
    this.loadData();
  }
  
  // navigateToPaymentScreen(item: any): void {
  //   const coll_slip_no = item.coll_slip_no;
  //   const orn_no = item.orn_no;

  //   this.router.navigate(['/otc-payment-screen', coll_slip_no], { queryParams: { orn_no }, state: { item } });
  // }



navigateToRefundInfo(item: any): void {
  const status = (item?.rtt_status ?? '').toUpperCase().trim();    // e.g. BE
  const slip = (item?.refund_slip_no ?? '').trim();

  // DA + BE => go to Enter Bank Info even if slip is empty
  if (status === 'BE') {
    this.navigateToRefundEnterBankInfo(item);
    return;
  }

  // No slip => New Submit Info
  if (!slip || slip === '' || slip === 'N/A') {
    this.navigateToRefundNewsubmitInfo(item);
    return;
  }

  // Otherwise => Enter Bank Info
  this.navigateToRefundEnterBankInfo(item);
}
  

  navigateToRefundNewsubmitInfo(item: any): void {
    console.log('Navigating to Direct Application with item:', item);
    const rms_type = item.rms_type;
    const mtt_id = item.mtt_id;
    const txn_id = item.txn_id;
    const orn_no = item.orn_no;
    const order_status = item.order_status;
    const rtt_status = item.rtt_status;
    const rtt_app_no = item.rtt_app_no;
    const date_expiry = item.date_expiry;
    console.log('order_status', order_status);  
    this.router.navigate(['/refund-direct-application'],
      { state: { mtt_id, orn_no, txn_id, rms_type, order_status, rtt_status, rtt_app_no, date_expiry }});
  }
  navigateToRefundEnterBankInfo(item: any): void {
    console.log('Navigating to Enter Bank Info with item:', item);
    const rms_type = item.rms_type;
    const mtt_id = item.mtt_id;
    const txn_id = item.txn_id;
    const orn_no = item.orn_no;
    const order_status = item.order_status;
    const refund_slip_no = item.refund_slip_no;
    const rtt_status = item.rtt_status;
    const rtt_app_no = item.rtt_app_no;
    const date_expiry = item.date_expiry;
    console.log('order_status', order_status);  
    this.router.navigate(['/refund-submit-bankinfo'],
      { state: { mtt_id, orn_no, txn_id, rms_type, order_status, refund_slip_no, rtt_status, rtt_app_no, date_expiry }});
  }

}



