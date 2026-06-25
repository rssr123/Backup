import { HttpClient, HttpHeaders } from '@angular/common/http';
import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
// import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { NgbPaginationConfig } from '@ng-bootstrap/ng-bootstrap';
import { environment } from 'src/environments/environment';
import { RIPL } from '../../core/models/ri-pl.interface';
import { ParamData } from 'src/app/core/models/param.interface';
import { Systemstatus } from '../../shared/enums/systemstatus';
import { formatDate } from '@angular/common';
import { ParamService } from '../../core/services/param.service';
import { AuthService } from 'src/app/core/services/auth.service';
import { perm } from 'src/permissions/perm';

@Component({
  selector: 'app-ri-pl-listing',
  templateUrl: './ri-pl-listing.component.html',
  styleUrls: ['./ri-pl-listing.component.scss']
})
export class RIPLComponent implements OnInit {

  page = environment.DefaultPage;
  itemsPerPage = environment.ItemPerPage;
  model: RIPL[] = [];
  totalRecords: number = 0;
  isReadOnly = false;
  isEmptyResult = false;

  transactionType: String | null = null;
  entityType: String | null = null;
  entityNo: String | null = null;
  calendarYear: Date | null = null;
  dueDate: Date | null = null;
  companyType: String | null = null;
  riplStatus: String | null = null;

  // Configuring Permissions for User and roles
  permRIPL = perm.Accrual_Listing_Deferred_Income_RI_Periodic_Lodgement_RI_Compound_View_Details_Page + "," + perm.Accrual_Listing_Deferred_Income_RI_Periodic_Lodgement_RI_Compound_View_Listing_Page ;
  permRIPLAllow = "";
  permRIPLListAllow: number = 1;
  permRIPLDetailsAllow: number = 0;
  // end configuration

  isDisplay: boolean = false;

  isLoading: boolean = false;

  //toogle start
  rightSectionCollapsed: boolean = true;

  //default pagination
  selectedValue = environment.dropdownOptions[0];
  dropdownOptions = environment.dropdownOptions;

  status: ParamData[] = [];
  txnType: ParamData[] = [];
  entType: ParamData[] = [];
  selectedStatus: string = Systemstatus.Active;

  checkResult: number = 0;
  selected: any;
  getparam: any;
  item: any;

  LoadData(singleItem: number) {
    this.selectedValue = singleItem;
    this.itemsPerPage = this.selectedValue;
    this.loadData();
  }

  toggleRightSection() {
    this.rightSectionCollapsed = !this.rightSectionCollapsed;
  }
  //toogle end

  showResultAlert = false;

  showResultAlertBox() {
    this.showResultAlert = true;
    setTimeout(() => (this.showResultAlert = false), 2000);
  }

  constructor(
    private http: HttpClient,
    config: NgbPaginationConfig,
    private router: Router,
    private cd: ChangeDetectorRef,
    private ParamService: ParamService,
    private authService: AuthService
  ) {
    config.maxSize = environment.PaginationMaxSize;
    config.boundaryLinks = true;
  }

  viewSelected(item: any) {

    const ripl_id = item.i_ripl_id
    //const ripl_id = item.ripl_id
    this.router.navigate(['/ri-pl-detail'], { state: { ripl_id } });

  }

  ngOnInit(): void {
    // this.minDate.setMonth(this.minDate.getMonth() - 1);
    // this.selected = [this.minDate, this.bsValue];

    this.loadRIPLStatus();
    this.loadTxnType();
    this.loadEntType();
    //load data must be place at last
    this.loadData();
  }

  // //loadData Start
  loadData() {
    this.authService.checkUserRole(this.authService.username, this.permRIPL)
    .subscribe(
      (response: any) => {
        this.permRIPLAllow = response.data;
        this.permRIPLListAllow = this.permRIPLAllow.includes(perm.Accrual_Listing_Deferred_Income_RI_Periodic_Lodgement_RI_Compound_View_Listing_Page) ? 1 : 0;
        this.permRIPLDetailsAllow = this.permRIPLAllow.includes(perm.Accrual_Listing_Deferred_Income_RI_Periodic_Lodgement_RI_Compound_View_Details_Page) ? 1 : 0;
        console.log(this.permRIPLListAllow, this.permRIPLDetailsAllow);
        if (this.permRIPLListAllow === 0) {
          console.log("access-denied");
          this.router.navigate(['/access-denied']);
          return; // Exit the function to prevent further execution
        }
        // console.log(this.permListAllow, this.permAddAllow, this.permEditAllow);

        // Set your authorization header
        const headers = new HttpHeaders({
          Authorization: environment.authKey,
          'Content-Type': 'application/json',
        });

        
        this.isDisplay = true;
        this.isLoading = true;
        const url = environment.apiUrl + '/api/ripl/v1/sp_getRIPL';
    
        const Body: any = {
          i_page: this.page.toString(),
          i_size: this.itemsPerPage.toString(),
        };
    
        if (this.transactionType && this.transactionType.trim()) {
          Body.i_txn_type = this.transactionType;
        }
    
        if (this.entityType && this.entityType.trim()) {
          Body.i_entity_type = this.entityType;
        }
    
        if (this.entityNo && this.entityNo.trim()) {
          Body.i_entity_no = this.entityNo;
        }
    
        if (this.calendarYear) {
          Body.i_calendar_yr = formatDate(this.calendarYear, 'YYYY', 'en');
        }
        
        if (this.dueDate) {
          Body.i_dt_due = formatDate(this.dueDate, 'YYYY-MM-dd', 'en');
          
        }
    
        if (this.companyType && this.companyType.trim()) {
          Body.i_ripl_ctype = this.companyType;
        }
    
    
        if (this.riplStatus && this.riplStatus.trim()) {
          Body.i_status = this.riplStatus;
        }
    
        this.http.post(url, Body, { headers }).subscribe(
          (response: any) => {
            this.model = response.data;
            if (response.data.length == 0) {
              this.totalRecords = 0;
              this.isDisplay = true;
              this.isEmptyResult = true;
              this.showResultAlertBox();
              this.isLoading = false;
            } else {
              this.totalRecords = response.data[0].total;
              this.isEmptyResult = false;
    
              this.isLoading = false;
            }
            // console.log(response.data);
            //  console.log(this.totalRecords);
          },
          (error) => {
            console.error(error);
            this.isLoading = false;
            // Handle errors here
    
    
          }
        );
      },
      (error) => {
        console.error(error);
      }
    );

    
  }
  //loadData End

  apply(): void {
    this.loadData();
    this.rightSectionCollapsed = true;
  }

  reset(): void {
    this.transactionType = null;
    this.entityType = null;
    this.entityNo = null;
    this.calendarYear = null;
    this.dueDate = null;
    this.companyType = null;
    this.riplStatus = null;
    // this.selected = {
    //   start: moment().subtract(1, 'month'),
    //   end: moment(),
    // };
    
  }

  loadEntType() {
    this.ParamService.getStates('1', '100', '', 'EntityType').subscribe(
      (response: any) => {
        if (response && response.data && Array.isArray(response.data)) {
          //this.states = response.data as ParamData[];
          this.entType.push({ 
            param_cd: '', 
            nm_en: 'All', 
            nm_bm: 'All', 
            total: 5 
          }); //add 'All' options
          //this.states.push(response.data);
          this.entType = [...this.entType, ...response.data];
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

  loadTxnType() {
    this.ParamService.getStates('1', '100', '', 'RIPL-TxnType').subscribe(
      (response: any) => {
        if (response && response.data && Array.isArray(response.data)) {
          //this.states = response.data as ParamData[];
          this.txnType.push({ 
            param_cd: '', 
            nm_en: 'All', 
            nm_bm: 'All', 
            total: 5 
          }); //add 'All' options
          //this.states.push(response.data);
          this.txnType = [...this.txnType, ...response.data];
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

  loadRIPLStatus() {
    this.ParamService.getStates('1', '100', '', 'RIPL-Status').subscribe(
      (response: any) => {
        if (response && response.data && Array.isArray(response.data)) {
          //this.states = response.data as ParamData[];
          this.status.push({ 
            param_cd: '', 
            nm_en: 'All', 
            nm_bm: 'All', 
            total: 5 
          }); //add 'All' options
          //this.states.push(response.data);
          this.status = [...this.status, ...response.data];
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

}