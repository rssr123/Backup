import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { DeferredIncome } from '../../core/models/deferred-income.interface';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Router } from '@angular/router';
import { NgbPaginationConfig } from '@ng-bootstrap/ng-bootstrap';
import { environment } from 'src/environments/environment';
import moment from 'moment';
import { MatDialog } from '@angular/material/dialog';
import { Systemstatus } from '../../shared/enums/systemstatus';
import { fadeInOut } from '../../shared/animation';
import { ParamData } from 'src/app/core/models/param.interface';
import { ParamService } from '../../core/services/param.service';
import { MFT, MFTWF, MasterTaskList, Param, SourceSystemCode, User } from '../../core/models/entity';
import { formatDate } from '@angular/common';
import { perm } from 'src/permissions/perm';
import { AuthService } from 'src/app/core/services/auth.service';


@Component({
  selector: 'app-deferred-income-listing',
  templateUrl: './deferred-income-listing.component.html',
  styleUrls: ['./deferred-income-listing.component.scss'],
  animations: [fadeInOut]
})
export class DeferredIncomeListingComponent implements OnInit {
  page = environment.DefaultPage;
  itemsPerPage = environment.ItemPerPage;
  dropDownSize = environment.DropDownSize;
  model: DeferredIncome[] = [];
  // statusOptions: Param[] = [];
  totalRecords: number = 0;
  isReadOnly = false;

  diId: BigInteger | null = null;
  feeDetailId: String | null = null;
  transactionType: String | null = null;
  entityType: String | null = null;
  entityNo: String | null = null;
  effectiveDate: Date | null = null;
  expiryDate: Date | null = null;
  approvalStatus: String | null = null;
  approvalDate: Date | null = null;
  itemReferanceNo: String | null = null;
  diStatus: String | null = null;


  // Configuring Permissions for User and roles
  permDI = perm.Accrual_Listing_Deferred_Income_RI_Periodic_Lodgement_RI_Compound_View_Details_Page + "," + perm.Accrual_Listing_Deferred_Income_RI_Periodic_Lodgement_RI_Compound_View_Listing_Page;
  permDIAllow = "";
  permDIListAllow: number = 1;
  permDIDetailsAllow: number = 0;
  // end configuration

  isDisplay: boolean = false;

  isLoading: boolean = false;

  //toogle start
  rightSectionCollapsed: boolean = true;

  //default pagination
  selectedValue = environment.dropdownOptions[0];
  dropdownOptions = environment.dropdownOptions;

  status: ParamData[] = [];
  enType: ParamData[] = [];
  txnType: ParamData[] = [];
  aprStatus: ParamData[] = [];
  distatus: ParamData[] = [];
  selectedState: string = Systemstatus.Active;

  checkResult: number = 0;

  isEmptyResult = false;
  totalCount = 0;


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
    public dialog: MatDialog,
    private ParamService: ParamService,
    private cd: ChangeDetectorRef,
    private authService: AuthService
  ) {
    config.maxSize = environment.PaginationMaxSize;
    config.boundaryLinks = true;
  }

  viewSelected(item: any) {

    const di_id = item.di_id
    this.router.navigate(['/deferred-income-details'], { state: { di_id } });

  }

  ngOnInit(): void {

    this.loadEntType();
    this.loadTxnType();
    this.loadApprStatus();
    this.loadDIStatus();

    this.loadStates();
    //load data must be place at last
    this.loadData();

  }






  //loadData Start
  loadData() {
    this.authService.checkUserRole(this.authService.username, this.permDI)
    .subscribe(
      (response: any) => {
        this.permDIAllow = response.data;
        this.permDIListAllow = this.permDIAllow.includes(perm.Accrual_Listing_Deferred_Income_RI_Periodic_Lodgement_RI_Compound_View_Listing_Page) ? 1 : 0;
        this.permDIDetailsAllow = this.permDIAllow.includes(perm.Accrual_Listing_Deferred_Income_RI_Periodic_Lodgement_RI_Compound_View_Details_Page) ? 1 : 0;
        console.log(this.permDIListAllow, this.permDIDetailsAllow);
        if (this.permDIListAllow === 0) {
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
        const url = environment.apiUrl + '/api/di/v1/getdeferredincome';
    
        const Body: any = {
          i_page: this.page.toString(),
          i_size: this.itemsPerPage.toString(),
        };
    
    
        if (this.feeDetailId && this.feeDetailId.trim()) {
          Body.i_fee_detail_id = this.feeDetailId;
        }
    
        if (this.transactionType && this.transactionType.trim()) {
          Body.i_txn_type = this.transactionType;
        }
    
        if (this.entityType && this.entityType.trim()) {
          Body.i_entity_type = this.entityType;
        }
    
        if (this.entityNo && this.entityNo.trim()) {
          Body.i_entity_no = this.entityNo;
        }
    
        if (this.effectiveDate) {
          Body.i_dt_effective = formatDate(this.effectiveDate, 'YYYY-MM-dd', 'en');
        }
    
        if (this.expiryDate) {
          Body.i_dt_expiry = formatDate(this.expiryDate, 'YYYY-MM-dd', 'en');
        }
    
        if (this.approvalStatus && this.approvalStatus.trim()) {
          Body.i_approval_status = this.approvalStatus;
        }
    
    
        if (this.approvalDate) {
          Body.i_dt_approval = formatDate(this.approvalDate, 'YYYY-MM-dd', 'en');
        }
    
        if (this.itemReferanceNo && this.itemReferanceNo.trim()) {
          Body.i_item_ref_no = this.itemReferanceNo;
        }
    
        if (this.diStatus && this.diStatus.trim()) {
          Body.i_status = this.diStatus;
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
    // this.loadEntType();
    this.loadData();
    this.rightSectionCollapsed = true;
  }

  reset(): void {
    this.feeDetailId = null;
    this.transactionType = null;
    this.entityType = null;
    this.entityNo = null;
    this.effectiveDate = null;
    this.expiryDate = null;
    this.approvalStatus = null;
    this.approvalDate = null;
    this.itemReferanceNo = null;
    this.diStatus = null;

  }

  loadEntType() {
    this.ParamService.getStates('1', '100', '', 'EntityType').subscribe(
      (response: any) => {
        if (response && response.data && Array.isArray(response.data)) {
          //this.states = response.data as ParamData[];
          this.enType.push({
            param_cd: '',
            nm_en: 'All',
            nm_bm: 'All',
            total: 5
          }); //add 'All' options
          //this.states.push(response.data);
          this.enType = [...this.enType, ...response.data];
          //this.states.sort((a, b) => a.nm_en.localeCompare(b.nm_en)); // sorting
          this.entityType = this.enType[0].param_cd;
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
    this.ParamService.getStates('1', '100', '', 'DI-TxnType').subscribe(
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
          this.transactionType = this.txnType[0].param_cd;
        } else {
          console.error('Invalid response format:', response);
        }
      },
      (error: any) => {
        console.error('There was an error retrieving the status:', error);
      }
    );
  }

  loadApprStatus() {
    this.ParamService.getStates('1', '100', '', 'DI-ApprovalStatus').subscribe(
      (response: any) => {
        if (response && response.data && Array.isArray(response.data)) {
          //this.states = response.data as ParamData[];
          this.aprStatus.push({
            param_cd: '',
            nm_en: 'All',
            nm_bm: 'All',
            total: 5
          }); //add 'All' options
          //this.states.push(response.data);
          this.aprStatus = [...this.aprStatus, ...response.data];
          //this.states.sort((a, b) => a.nm_en.localeCompare(b.nm_en)); // sorting
          this.approvalStatus = this.aprStatus[0].param_cd;
        } else {
          console.error('Invalid response format:', response);
        }
      },
      (error: any) => {
        console.error('There was an error retrieving the status:', error);
      }
    );
  }

  loadDIStatus() {
    this.ParamService.getStates('1', '100', '', 'DI-Status').subscribe(
      (response: any) => {
        if (response && response.data && Array.isArray(response.data)) {
          //this.states = response.data as ParamData[];
          this.distatus.push({
            param_cd: '',
            nm_en: 'All',
            nm_bm: 'All',
            total: 5
          }); //add 'All' options
          //this.states.push(response.data);
          this.distatus = [...this.distatus, ...response.data];
          //this.states.sort((a, b) => a.nm_en.localeCompare(b.nm_en)); // sorting
          this.diStatus = this.distatus[0].param_cd;
        } else {
          console.error('Invalid response format:', response);
        }
      },
      (error: any) => {
        console.error('There was an error retrieving the status:', error);
      }
    );
  }

  refreshMainPage(): void {
    this.page = 1;
    this.loadData();
  }

  loadStates() {
    this.ParamService.getStates('1', '100', '', 'Status').subscribe(
      (response: any) => {
        if (response && response.data && Array.isArray(response.data)) {
          //this.states = response.data as ParamData[];
          this.status.push({ param_cd: '', nm_en: 'All', nm_bm: 'All', total: 5 }); //add 'All' options
          //this.states.push(response.data);
          this.status = [...this.status, ...response.data];
          //this.states.sort((a, b) => a.nm_en.localeCompare(b.nm_en)); // sorting
        } else {
          console.error('Invalid response format:', response);
        }
      },
      (error) => {
        console.error('There was an error retrieving the status:', error);
      }
    );
  }
}

