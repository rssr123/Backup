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
  selector: 'app-refund-chargeback',
  templateUrl: './refund-chargeback.component.html',
  styleUrls: ['./refund-chargeback.component.scss']
})

export class RefundChargebackComponent implements OnInit {

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
  selectedOption: string = ''; // Holds the selected option
  inputName: string = ''; // Dynamically updated input name
  checkResult: number = 0;
  isSearchOptionSelected = false;

  


  constructor(private fb: FormBuilder, private http: HttpClient, private config: NgbPaginationConfig, private router: Router) { }

  ngOnInit(): void {
    this.searchForm = this.fb.group({
      inputName: ['', [Validators.required]] // define your form control with validators
    });
    this.searchBy();
    this.loadData();
  }


   initializeSearchForm() {
      this.searchForm = this.fb.group({
        searchOption: ['', [Validators.required]],
        searchInput: ['', [Validators.required]]
      }); 
    }

  
  searchBy(){
    this.searchModel=[
      {nm:'Order Reference No.', value:'i_orn_no'},
      {nm:'Receipt No.', value:'i_rcpt_no'},
      {nm:'Transaction ID', value:'i_txn_id'},

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
      i_order_status: 'P',
    };
  
    if (this.inputName && this.inputName.trim()) {
      // Add the dynamic property to Body
      Body[this.selectedOption] = this.inputName;
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
    this.searchForm.reset(); // Resets all formCRS20250224252732 controls
  }

  // navigateToPaymentScreen(item: any): void {
  //   const coll_slip_no = item.coll_slip_no;
  //   const orn_no = item.orn_no;

  //   this.router.navigate(['/otc-payment-screen', coll_slip_no], { queryParams: { orn_no }, state: { item } });
  // }



  navigateToRefundInfo(item: any): void {

      this.nevigatetoChargeBack(item);
    
  }
  

  nevigatetoChargeBack(item: any): void {

    const rms_type = item.rms_type;
    const mtt_id = item.mtt_id;
    const txn_id = item.txn_id;
    const orn_no = item.orn_no;
    const order_status = item.order_status;
    // console.log('order_status', order_status);  
    // console.log(txn_id)
    this.router.navigate(['/refund-chargeback-info'],
      { state: { mtt_id, orn_no, txn_id, rms_type, order_status}});
  }

}



