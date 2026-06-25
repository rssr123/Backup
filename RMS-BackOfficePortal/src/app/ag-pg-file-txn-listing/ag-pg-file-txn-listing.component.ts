import { Component, Inject, OnInit } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { environment } from 'src/environments/environment';
import { NgbPaginationConfig } from '@ng-bootstrap/ng-bootstrap';
import { ParamData } from 'src/app/core/models/param.interface';
import { Router } from '@angular/router';
import { ParamService } from '../core/services/param.service';
import { MAT_DIALOG_DATA, MatDialog, MatDialogRef } from '@angular/material/dialog';
import { Systemstatus } from '../shared/enums/systemstatus';
import { bankReconDetails } from 'src/app/core/models/entity';

import { TranslateService } from '@ngx-translate/core';
import { GlobalService } from 'src/app/shared/global.service';

@Component({
  selector: 'app-ag-pg-file-txn-listing',
  templateUrl: './ag-pg-file-txn-listing.component.html',
  styleUrls: ['./ag-pg-file-txn-listing.component.scss']
})
export class AgPgFileTxnListingComponent {
  page = environment.DefaultPage;
  itemsPerPage = environment.ItemPerPage;
  errorMessage: string | null = null;
  isDisplay: boolean = false;
  isLoading: boolean = false;
  isReadOnly = false;
  isEmptyResult = false;
  totalRecords: number = 0;

  //default pagination
  selectedValue = environment.dropdownOptions[0];
  dropdownOptions = environment.dropdownOptions;

  txnref: string | null = null;
  credit: number | null = null;
  dtposting: Date | null = null;
  

  //Model
  model: bankReconDetails[] = [];
  task_no: string | null = null;
  txn_ref: string | null = null;
  credit_: number | null = null;
  dt_posting: Date | null = null;

  receivedModelData: any;

  constructor(
    private http: HttpClient,
    config: NgbPaginationConfig,
    private router: Router,
    public dialog: MatDialog,
    private ParamService: ParamService,
    private translateService: TranslateService,
    private globalService: GlobalService,
     public dialogRef: MatDialogRef<AgPgFileTxnListingComponent>,
        @Inject(MAT_DIALOG_DATA) public data: any // Receive data from dialog
  ) {
    config.maxSize = environment.PaginationMaxSize;
    config.boundaryLinks = true;    
    this.translateService.setDefaultLang(this.globalService.getGlobalValue());
    this.translateService.use(this.globalService.getGlobalValue());
    this.receivedModelData = data.modelData; // Store received data
  }

  ngOnInit(): void {
    this.loadData();
  }

  reset() {
    this.txnref = null;
    this.credit = null;
    this.dtposting = null;
  }

  apply(){
    this.loadData();
  }

  LoadData(singleItem: number) {
    this.selectedValue = singleItem;
    this.itemsPerPage = this.selectedValue;
    this.loadData();
  }

  //loadData Start
  loadData() {
    this.task_no = history.state.task_id;

    this.isDisplay = true;
    this.isLoading = true;
    const url = environment.apiUrl + '/api/RMSNR/v1/getagbanktxnpg';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    const Body: any = {
      i_page: this.page.toString(),
      i_size: this.itemsPerPage.toString(),
      // i_ag_sale_id: this.receivedModelData.ag_sale_id,
      i_stmt_no: this.receivedModelData.stmt_no
    };

    // if (this.task_no && this.task_no.trim()) {
    //   Body.task_no = this.task_no;
    // }

    if (this.txnref && this.txnref.trim()) {
      Body.i_txn_ref = this.txnref;
    }

    if (this.credit && this.credit) {
      Body.i_credit = this.credit;
    }

    // if (this.dtposting && this.dtposting) {
    //   Body.i_posting_date = this.dtposting;
    // }

    if (this.dtposting) {
      Body.i_posting_date = this.formatDateForSP(this.dtposting);
    }

    this.http.post(url, Body, { headers }).subscribe(
      (response: any) => {
        console.log(response.data);
        this.model = response.data;
        if (response.data.length == 0) {
          this.totalRecords = 0;
          this.isEmptyResult = true;
          //this.isDisplay = false;
          //this.showResultAlert; // Remove parentheses here
          this.isLoading = false;
        } else {
          this.totalRecords = response.data[0].total;
          this.isEmptyResult = false;
          this.isLoading = false;
          // this.AlertBoxInitialize();

          this.model = response.data.map((item:any) => ({
            bank_txn_ref : item.txn_ref,
            bank_acct_no : item.acct_no,
            bank_brn_chn : item.brn_chn,
            bank_dt_posting : item.dt_posting,
            bank_tm_posting : item.dt_posting,
            bank_credit : item.credit,
          }));
          
          this.isLoading = false;
        }
        // console.log(response.data);
        //  console.log(this.totalRecords);
      },
      (error: any) => {
        console.error(error);
        this.isLoading = false;
        // Handle errors here
      }
    );
  }

  formatDateForSP(date: Date): string {
    if (!date) return '';
  
    const formattedDate = new Date(date);
    formattedDate.setHours(0, 0, 0, 0); // Set time to 00:00:00
  
    const year = formattedDate.getFullYear();
    const month = ('0' + (formattedDate.getMonth() + 1)).slice(-2); // Ensure two digits
    const day = ('0' + formattedDate.getDate()).slice(-2);
  
    return `${year}-${month}-${day} 00:00:00`;
  }
}
