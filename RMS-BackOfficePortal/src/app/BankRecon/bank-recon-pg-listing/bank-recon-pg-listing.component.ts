import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { bankReconDetail } from '../../core/models/bank-recon-details.interface';

import { environment } from 'src/environments/environment';
import { NgbPaginationConfig } from '@ng-bootstrap/ng-bootstrap';
import { formatDate } from '@angular/common';
import { fadeInOut } from '../../shared/animation';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Router } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import { ParamService } from '../../core/services/param.service';

import { TranslateService } from '@ngx-translate/core';
import { GlobalService } from 'src/app/shared/global.service';
import { AuthService } from 'src/app/core/services/auth.service';
import { perm } from 'src/permissions/perm';

@Component({
  selector: 'app-bank-recon-pg-listing',
  templateUrl: './bank-recon-pg-listing.component.html',
  styleUrls: ['./bank-recon-pg-listing.component.scss'],
  animations: [fadeInOut],
})

export class BankReconPgListingComponent implements OnInit {
  page = environment.DefaultPage;
  itemsPerPage = environment.ItemPerPage;
  errorMessage: string | null = null;
  isDisplay: boolean = false;
  isLoading: boolean = false;
  isReadOnly = false;
  isEmptyResult = false;
  totalRecords: number = 0;


  //Model
  model: bankReconDetail[] = [];

  txn_date: Date | null = null;
  txn_type: String | null = null;
  found_in_rms: String | null = null;
  txn_id: String | null = null;
  txn_code: String | null = null;

  //default pagination
  selectedValue = environment.dropdownOptions[0];
  dropdownOptions = environment.dropdownOptions;

  //date range picker
  selected!: Date[];
  bsValue = new Date();
  tempDate !: Date;
  minDate = new Date();

  constructor(
    private http: HttpClient,
    config: NgbPaginationConfig,
    private router: Router,
    public dialog: MatDialog,
    private ParamService: ParamService,
    private translateService: TranslateService,
    private globalService: GlobalService,
    private authService: AuthService
  ) {
    config.maxSize = environment.PaginationMaxSize;
    config.boundaryLinks = true;
    this.translateService.setDefaultLang(this.globalService.getGlobalValue());
    this.translateService.use(this.globalService.getGlobalValue());
  }

  ngOnInit(): void {
    this.minDate.setMonth(this.minDate.getMonth() - 1);
    this.selected = [this.minDate, this.bsValue];

    //load data must be place at last
    this.loadData();
  }

  apply(): void {
    this.loadData();
  }

  reset(): void {
    //this.pg_txn_date = null;
    this.txn_type = null;
    this.found_in_rms = null;
    this.txn_id = null;
    this.txn_code = null;
    // this.selected = {
    //   start: moment().subtract(1, 'month'),
    //   end: moment(),
    // };
    this.minDate.setDate(this.minDate.getMonth() - 1);
    this.selected = [this.minDate, this.bsValue];
  }

  //Load Data
  LoadData(singleItem: number) {
    this.selectedValue = singleItem;
    this.itemsPerPage = this.selectedValue;
    this.loadData();
  }

  //loadData Start
  loadData() {

          this.isDisplay = true;
          this.isLoading = true;
          const url = environment.apiUrl + '/api/brdc/v1/getbankpgtxnlisting';

          // Set your authorization header
          const headers = new HttpHeaders({
            Authorization: environment.authKey,
            'Content-Type': 'application/json',
          });

          const Body: any = {
            i_page: this.page.toString(),
            i_size: this.itemsPerPage.toString(),
            task_no: history.state.task_id,
            i_task_no: history.state.task_id,
          };

          if (this.txn_date) {
            //Body.dt_txn = this.txn_date;
            Body.i_dt_txn = this.txn_date;
          }

          if (this.txn_type && this.txn_type.trim()) {
            //Body.txn_type = this.txn_type;
            Body.i_txn_type = this.txn_type;
          }

          if (this.found_in_rms) {
            //Body.found_in_pg = this.found_in_rms;
            if(this.found_in_rms.toLowerCase() == "yes"){
              Body.i_found_in_rms = 1;
            }
            else if(this.found_in_rms.toLowerCase() == "no"){
              Body.i_found_in_rms = 0;
            }
          }

          if (this.txn_id) {
            //Body.txn_id = this.txn_id;
            Body.i_txn_id = this.txn_id;
          }

          if (this.txn_code && this.txn_code.trim()) {
            //Body.txn_cd = this.txn_code;
            Body.i_txn_code = this.txn_code;
          }

          this.http.post(url, Body, { headers }).subscribe(
            (response: any) => {
              this.model = response.data;
              if (response.data.length == 0) {
                this.totalRecords = 0;
                this.isDisplay = false;
                //this.showResultAlertBox();
                this.isLoading = false;
                this.isEmptyResult = true;
              } else {
                this.totalRecords = response.data[0].total;
                this.txn_date = response.data[0].dt_txn;
                this.isEmptyResult = false;

                this.model = response.data.map((item: any) => ({
                  pg_txn_date: item.dt_txn,
                  pg_txn_id: item.txn_id,
                  pg_txn_type: item.txn_type,
                  pg_txn_code: item.txn_cd,
                  pg_found_in_rms: item.found_in_pg,
                  pg_txn_amt: item.txn_amt,
                  pg_mdr: item.mdr_amt,
                  pg_sst: item.sst_amt,
                  pg_net_amt: item.net_amt,
                }));

                //this.AlertBoxInitialize();
                this.isLoading = false;
              }
            },
            (error) => {
              console.error(error);
              this.isLoading = false;
              // Handle errors here
            }
          );
  }
}
