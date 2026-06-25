import { Component, OnInit } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { environment } from 'src/environments/environment';
import { NgbPaginationConfig } from '@ng-bootstrap/ng-bootstrap';
import { ParamData } from 'src/app/core/models/param.interface';
import { Router } from '@angular/router';
import { ParamService } from '../../core/services/param.service';
import { MatDialog } from '@angular/material/dialog';
import { Systemstatus } from '../../shared/enums/systemstatus';
import { bankReconDetails } from 'src/app/core/models/entity';

import { TranslateService } from '@ngx-translate/core';
import { GlobalService } from 'src/app/shared/global.service';

@Component({
  selector: 'app-bank-recon-pg-file-txn-listing',
  templateUrl: './bank-recon-pg-file-txn-listing.component.html',
  styleUrls: ['./bank-recon-pg-file-txn-listing.component.scss']
})
export class BankReconPgFileTxnListingComponent implements OnInit{

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

  constructor(
    private http: HttpClient,
    config: NgbPaginationConfig,
    private router: Router,
    public dialog: MatDialog,
    private ParamService: ParamService,
    private translateService: TranslateService,
    private globalService: GlobalService
  ) {
    config.maxSize = environment.PaginationMaxSize;
    config.boundaryLinks = true;    
    this.translateService.setDefaultLang(this.globalService.getGlobalValue());
    this.translateService.use(this.globalService.getGlobalValue());
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
    const url = environment.apiUrl + '/api/brdc/v1/getpgfilerelatedtxn';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    const Body: any = {
      i_page: this.page.toString(),
      i_size: this.itemsPerPage.toString(),
    };

    if (this.task_no && this.task_no.trim()) {
      Body.task_no = this.task_no;
    }

    if (this.txnref && this.txnref.trim()) {
      Body.txn_ref = this.txnref;
    }

    if (this.credit && this.credit) {
      Body.credit_ = this.credit;
    }

    if (this.dtposting && this.dtposting) {
      Body.dt_posting = this.dtposting;
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

}
