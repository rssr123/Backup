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
import { AgTxn } from '../core/models/ag-txn.interface';


@Component({
  selector: 'app-ag-bank-txn-listing',
  templateUrl: './ag-bank-txn-listing.component.html',
  styleUrls: ['./ag-bank-txn-listing.component.scss']
})
export class AgBankTxnListingComponent {
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
  acctno: string | null = null;
  brnchn: string | null = null;

  //Model
  model: AgTxn[] = [];
  task_no: string | null = null;
  txn_ref: string | null = null;
  acct_no: string | null = null;
  brn_chn: string | null = null;

  receivedModelData: any;

  constructor(
    private http: HttpClient,
    config: NgbPaginationConfig,
    private router: Router,
    public dialog: MatDialog,
    private ParamService: ParamService,
    private translateService: TranslateService,
    private globalService: GlobalService,
    public dialogRef: MatDialogRef<AgBankTxnListingComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any // Receive data from dialog
  ) {
    config.maxSize = environment.PaginationMaxSize;
    config.boundaryLinks = true;    
    this.translateService.setDefaultLang(this.globalService.getGlobalValue());
    this.translateService.use(this.globalService.getGlobalValue());
    this.receivedModelData = data.modelData; // Store received data
    console.log("Received Data:", this.receivedModelData); // Debugging
  }

  ngOnInit(): void {
    this.loadData();
  }

  reset() {
    this.txnref = null;
    this.acctno = null;
    this.brnchn = null;
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
    const url = environment.apiUrl + '/api/RMSNR/v1/getagbanktxn';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    const Body: any = {
      i_page: this.page.toString(),
      i_size: this.itemsPerPage.toString(),
      i_stmt_no: this.receivedModelData.stmt_no,
      // i_ag_sale_id: this.receivedModelData.ag_sale_id
    };

    // if (this.task_no && this.task_no.trim()) {
    //   Body.task_no = this.task_no;
    // }

    if (this.txnref && this.txnref.trim()) {
      Body.i_txn_ref = this.txnref;
    }

    if (this.acctno && this.acctno.trim()) {
      Body.i_acct_no = this.acctno;
    }

    if (this.brnchn && this.brnchn.trim()) {
      Body.i_brn_chn = this.brnchn;
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

          // this.model = response.data.map((item:any) => ({
          //   bank_txn_ref : item.txn_ref,
          //   bank_acct_no : item.acct_no,
          //   bank_brn_chn : item.brn_chn,
          //   bank_dt_posting : item.dt_posting,
          //   bank_tm_posting : item.dt_posting,
          //   bank_credit : item.credit,
          // }));
          
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
}
