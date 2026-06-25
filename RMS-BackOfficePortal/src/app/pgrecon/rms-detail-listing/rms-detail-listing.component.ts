import { formatDate } from '@angular/common';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { NgbPaginationConfig } from '@ng-bootstrap/ng-bootstrap';
import { ParamData } from 'src/app/core/models/param.interface';
import { environment } from 'src/environments/environment';
import { ParamService } from '../../core/services/param.service';
import { RMSDetailListing } from 'src/app/core/models/pg-recon';

@Component({
  selector: 'app-rms-detail-listing',
  templateUrl: './rms-detail-listing.component.html',
  styleUrls: ['./rms-detail-listing.component.scss']
})
export class RmsDetailListingComponent {

  txnDate: Date = new Date();
  custName: string = '';
  ornNo: string = '';
  txnType: string = '';
  txnID: string = '';
  txnCode: string = '';
  orderStatus: string = '';
  model: RMSDetailListing[]=[];
  foundInPG: boolean = false;
  subCriteria:any;
  subCri :ParamData[] = [];
  ordStatus: ParamData[] = [];

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

  async ngOnInit(): Promise<void> {

    this.isDisplay = true;
    this.isLoading = true;

    this.txnDate= this.data.pymtDate;
    this.custName=this.data.custNm;
    this.foundInPG=this.data.foundInPg;
    this.txnID=this.data.txnID2;
    this.ornNo=this.data.ornNo;

    await this.loadSubCriteria();
    await this.loadOrderStatus();

    this.subCriteria=this.data.subCri2;
    this.orderStatus=this.data.orderStatus;

    this.loadData(); // must last
    
  }

  async loadOrderStatus(){
    try {
      const response: any = await this.ParamService.getStates('1', '100', '', 'OrderStatus').toPromise();
      if (response && response.data && Array.isArray(response.data)) {
        this.ordStatus.push({
          param_cd: 'null',
          nm_en: 'All',
          nm_bm: 'All',
          total: 5
        }); //add 'All' options
        this.ordStatus = [...this.ordStatus, ...response.data];
        // Set the default selected state to 'All'
        this.orderStatus = this.ordStatus[0].param_cd; // Convert string to Date object
        //this.states.sort((a, b) => a.nm_en.localeCompare(b.nm_en)); // sorting
      } else {
        console.error('Invalid response format:', response);
      }
    } catch (error) {
      console.error('There was an error retrieving the status:', error);
    }
    // this.ParamService.getStates('1', '100', '', 'OrderStatus').subscribe(
    //   (response: any) => {
    //     if (response && response.data && Array.isArray(response.data)) {
    //       this.ordStatus.push({
    //         param_cd: '',
    //         nm_en: 'All',
    //         nm_bm: 'All',
    //         total: 5
    //       }); //add 'All' options
    //       this.ordStatus = [...this.ordStatus, ...response.data];
    //       // Set the default selected state to 'All'
    //       this.orderStatus = this.ordStatus[0].param_cd; // Convert string to Date object
    //       //this.states.sort((a, b) => a.nm_en.localeCompare(b.nm_en)); // sorting
    //     } else {
    //       console.error('Invalid response format:', response);
    //     }
    //   },
    //   (error: any) => {
    //     console.error('There was an error retrieving the status:', error);
    //   }
    // );
  }

  async loadSubCriteria() {
      try {
        const response: any = await this.ParamService.getStates('1', '100', '', 'PGRecon-RMSSubCri').toPromise();
        if (response && response.data && Array.isArray(response.data)) {
          this.subCri.push({
            param_cd: 'null',
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
      } catch (error) {
        console.error('There was an error retrieving the status:', error);
      }
    // this.ParamService.getStates('1', '100', '', 'PGRecon-RMSSubCri').subscribe(
    //   (response: any) => {
    //     if (response && response.data && Array.isArray(response.data)) {
    //       this.subCri.push({
    //         param_cd: '',
    //         nm_en: 'All',
    //         nm_bm: 'All',
    //         total: 5
    //       }); //add 'All' options
    //       this.subCri = [...this.subCri, ...response.data];
    //       // Set the default selected state to 'All'
    //       this.subCriteria = this.subCri[0].param_cd; // Convert string to Date object
    //       //this.states.sort((a, b) => a.nm_en.localeCompare(b.nm_en)); // sorting
    //     } else {
    //       console.error('Invalid response format:', response);
    //     }
    //   },
    //   (error: any) => {
    //     console.error('There was an error retrieving the status:', error);
    //   }
    // );
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

    this.txnDate= this.data.pymtDate;
    this.custName=this.data.custNm;
    this.foundInPG=this.data.foundInPg;
    this.txnID=this.data.txnID2;
    this.ornNo=this.data.ornNo;
    this.subCriteria=this.data.subCri2;
    this.orderStatus=this.data.orderStatus;

  }

  loadData(){
    this.isDisplay = true;
    this.isLoading = true;
    const url = environment.apiUrl + '/api/pgrecon/v1/sp_getRMSDetailListing'; 

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
      Body.i_dt_pymt = formatDate(this.txnDate, 'YYYY-MM-dd', 'en');
    }

    if (this.custName) {
      Body.i_cust_nm = this.custName;
    }

    if (this.foundInPG) {
      Body.i_fnd_in_pg = this.foundInPG;
    }

    if(this.txnID){
      Body.i_txn_id = this.txnID;
    }

    if(this.ornNo){
      Body.i_orn_no = this.ornNo;
    }

    if(this.subCriteria){
      Body.i_sub_cri = this.subCriteria;
    }

    if(this.orderStatus){
      Body.i_order_status = this.orderStatus;
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
