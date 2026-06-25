import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { NgbPaginationConfig } from '@ng-bootstrap/ng-bootstrap';
import { ParamData } from 'src/app/core/models/param.interface';
import { environment } from 'src/environments/environment';
import { ParamService } from '../../core/services/param.service';
import { formatDate } from '@angular/common';
import { PGDetailListing } from 'src/app/core/models/pg-recon';

@Component({
  selector: 'app-pg-detail-listing',
  templateUrl: './pg-detail-listing.component.html',
  styleUrls: ['./pg-detail-listing.component.scss']
})
export class PgDetailListingComponent {

  taskNo: any;
  txnDate: Date = new Date();
  txnType: string = '';
  txnID: string = '';
  txnCode: string = '';
  model: PGDetailListing[]=[];
  foundInRMS: boolean = false;
  subCriteria:any;
  subCri :ParamData[] = [];

  isLoading: boolean = false;
  isDisplay: boolean = false;
  isEmptyResult: boolean = false;
  showResultAlert: boolean = false;

  page = environment.DefaultPage;
  itemsPerPage = environment.ItemPerPage;
  totalRecords: number = 0;
  selectedValue = environment.dropdownOptions[0];
  dropdownOptions = environment.dropdownOptions;

  constructor(
    private http: HttpClient,
    config: NgbPaginationConfig,
    private router: Router,
    public dialog: MatDialog,
    private ParamService: ParamService,
    @Inject(MAT_DIALOG_DATA) public data: any,
  ) {
    config.maxSize = environment.PaginationMaxSize;
    config.boundaryLinks = true;
  }

  ngOnInit(): void {

    this.loadSubCriteria();

    this.txnDate= this.data.txnDate;
    this.txnType=this.data.txnType;
    this.txnID=this.data.txnID;
    this.txnCode = this.data.txnCode;
    this.foundInRMS = this.data.found;
    this.subCriteria = this.data.subCri;
    this.taskNo = this.data.taskNo;
    console.log(this.data);
    
    this.loadData(); // must last
    
  }

  loadSubCriteria() {
    this.ParamService.getStates('1', '100', '', 'PGRecon-PGSubCri').subscribe(
      (response: any) => {
        if (response && response.data && Array.isArray(response.data)) {
          this.subCri.push({
            param_cd: '',
            nm_en: 'All',
            nm_bm: 'All',
            total: 5
          }); //add 'All' options
          this.subCri = [...this.subCri, ...response.data];
          // Set the default selected state to 'All'
          this.subCriteria = this.subCri[0].param_cd; // Convert string to Date object
          //this.states.sort((a, b) => a.nm_en.localeCompare(b.nm_en)); // sorting
        } else {
          console.error('Invalid response format:', response);
        }
      },
      (error: any) => {
        console.error('There was an error retrieving the status:', error);
      }
    );
  }

  LoadData(singleItem: number) {
    this.selectedValue = singleItem;
    this.itemsPerPage = this.selectedValue;
    this.loadData();
  }

  apply(){
    this.loadData();
  }

  reset(){
    this.loadSubCriteria();
    this.txnDate= this.data.txnDate;
    this.txnType=this.data.txnType;
    this.txnID=this.data.txnID;
    this.txnCode = this.data.txnCode;
    this.foundInRMS = this.data.found;
    this.subCriteria = this.data.subCri;
    this.taskNo = this.data.taskNo;

  }

  loadData(){
    this.isDisplay = true;
    this.isLoading = true;
    const url = environment.apiUrl + '/api/pgrecon/v1/sp_getPGDetailListing'; 

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    const Body: any = {
      i_page: this.page.toString(),
      i_size: this.itemsPerPage.toString(),
    };

    if (this.txnDate) {
      Body.i_txn_date = formatDate(this.txnDate, 'YYYY-MM-dd', 'en');
    }

    if (this.txnType && this.txnType.trim()) {
      Body.i_txn_type = this.txnType;
    }

      Body.i_found_in_rms = this.foundInRMS;
    

    if(this.txnID){
      Body.i_txn_id = this.txnID;
    }

    if(this.txnCode){
      Body.i_txn_code = this.txnCode;
    }

    if(this.subCriteria){
      Body.i_sub_cri = this.subCriteria;
    }

    if(this.taskNo){
      Body.i_task_no=this.taskNo;
    }
    console.log(Body);

    this.http.post(url, Body, { headers }).subscribe(
      (response: any) => {
        console.log(response.data);
        this.model = response.data;
        if (response.data.length == 0) {
          this.totalRecords = 0;
          this.isEmptyResult = true;
          //this.isDisplay = false;
          this.showResultAlert; // Remove parentheses here
          this.isLoading = false;
        } else {
          this.totalRecords = response.data[0].total;
          this.isEmptyResult = false;
          // this.AlertBoxInitialize();
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
