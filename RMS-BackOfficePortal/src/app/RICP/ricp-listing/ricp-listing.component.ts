import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { ricp } from '../../core/models/ricp.interface';
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
import { formatDate } from '@angular/common';
import {TranslateService} from '@ngx-translate/core';
import { GlobalService } from 'src/app/shared/global.service';
import { perm } from 'src/permissions/perm';
import { AuthService } from 'src/app/core/services/auth.service';


@Component({
  selector: 'app-ricp-listing',
  templateUrl: './ricp-listing.component.html',
  styleUrls: ['./ricp-listing.component.scss'],
  animations: [fadeInOut]
})
export class RicpListingComponent implements OnInit{
  page = environment.DefaultPage;
  itemsPerPage = environment.ItemPerPage;
  model: ricp[] = [];
  enType: ParamData[] = [];
  ricpstatus: ParamData[] = [];
  totalRecords: number = 0;
  isReadOnly = false;

  

  ricpId: BigInteger | null = null;
  entityType: String | null = null;
  entityNo: String | null = null;
  cpNo: String | null = null;
  issuanceDate: Date | null = null;
  expiryDate: Date | null = null;
  cpAmount: number | null = null;
  accrualAmount: number | null = null;
  cpTier: number | null = null;
  cpTierAmount: number | null = null;
  ricpStatus: String | null = null;

  // Configuring Permissions for User and roles
  permRICP = perm.Accrual_Listing_Deferred_Income_RI_Periodic_Lodgement_RI_Compound_View_Details_Page + "," + perm.Accrual_Listing_Deferred_Income_RI_Periodic_Lodgement_RI_Compound_View_Listing_Page ;
  permRICPAllow = "";
  permRICPListAllow: number = 1;
  permRICPDetailsAllow: number = 0;
  // end configuration

  isDisplay: boolean = false;

  isLoading: boolean = false;

  // //toogle start
  rightSectionCollapsed: boolean = true;

  // //default pagination
  selectedValue = environment.dropdownOptions[0];
  dropdownOptions = environment.dropdownOptions;

  status: ParamData[] = [];
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
  // //toogle end

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
    private translate: TranslateService,
    private globalService: GlobalService,
    private authService: AuthService
  ) {
    config.maxSize = environment.PaginationMaxSize;
    config.boundaryLinks = true;
    this.translate.setDefaultLang(this.globalService.getGlobalValue());
    this.translate.use(this.globalService.getGlobalValue());
  }

  ngOnInit(): void {
    // this.minDate.setMonth(this.minDate.getMonth() - 1);
    // this.selected = [this.minDate, this.bsValue];

    this.loadStates();
    this.loadEntType();
    this.loadricpStatus();
    //load data must be place at last
    this.loadData();

  }



  viewSelected(item: any) {

    const ricp_id = item.ricp_id
    this.router.navigate(['/ricp-details'], { state: { ricp_id } });

  }

  // //loadData Start
  loadData() {
    this.authService.checkUserRole(this.authService.username, this.permRICP)
    .subscribe(
      (response: any) => {
        this.permRICPAllow = response.data;
        this.permRICPListAllow = this.permRICPAllow.includes(perm.Accrual_Listing_Deferred_Income_RI_Periodic_Lodgement_RI_Compound_View_Listing_Page) ? 1 : 0;
        this.permRICPDetailsAllow = this.permRICPAllow.includes(perm.Accrual_Listing_Deferred_Income_RI_Periodic_Lodgement_RI_Compound_View_Details_Page) ? 1 : 0;
        console.log(this.permRICPListAllow, this.permRICPDetailsAllow);
        if (this.permRICPListAllow === 0) {
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
        const url = environment.apiUrl + '/api/ricp/v1/getricp';
    
        const Body: any = {
          i_page: this.page.toString(),
          i_size: this.itemsPerPage.toString(),
        };
    
    
        if (this.entityType && this.entityType.trim()) {
          Body.i_entity_type = this.entityType;
        }
        
    
        if (this.entityNo && this.entityNo.trim()) {
          Body.i_entity_no = this.entityNo;
        }
    
        if (this.cpNo && this.cpNo.trim()) {
          Body.i_cp_no = this.cpNo;
        }
    
        if (this.issuanceDate) {
          Body.i_dt_issuance = formatDate(this.issuanceDate, 'YYYY-MM-dd', 'en');
        }
    
        if (this.expiryDate) {
          Body.i_dt_expiry = formatDate(this.expiryDate, 'YYYY-MM-dd', 'en');
        }
    
        //number
        if (this.cpAmount && this.cpAmount) {
          Body.i_cp_amt = this.cpAmount;
        }
    
        if (this.accrualAmount && this.accrualAmount) {
          Body.i_accr_amt = this.accrualAmount;
        }
    
        if (this.cpTier && this.cpTier) {
          Body.i_cp_tier = this.cpTier;
        }
    
        if (this.cpTierAmount && this.cpTierAmount) {
          Body.i_cp_tier_amt = this.cpTierAmount;
        }
        //number end
    
    
        if (this.ricpStatus && this.ricpStatus.trim()) {
          Body.i_status = this.ricpStatus;
        }
    
    
    
        console.log(Body);
    
        this.http.post(url, Body, { headers }).subscribe(
          (response: any) => {
            this.model = response.data.ricpList;
            if (response.data.total == 0) {
              this.totalRecords = 0;
              this.isDisplay = true;
              this.isEmptyResult = true;
              this.showResultAlertBox();
              this.isLoading = false;
            } else {
              this.totalRecords = response.data.total;
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
    this.entityType = null;
    this.entityNo = null;
    this.cpNo = null;
    this.issuanceDate = null;
    this.expiryDate = null;
    this.cpAmount = null;
    this.accrualAmount = null;
    this.cpTier = null;
    this.cpTierAmount = null;
    this.ricpStatus = null;

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

  loadricpStatus() {
    this.ParamService.getStates('1', '100', '', 'RICP-Status').subscribe(
      (response: any) => {
        if (response && response.data && Array.isArray(response.data)) {
          //this.states = response.data as ParamData[];
          this.ricpstatus.push({
            param_cd: '',
            nm_en: 'All',
            nm_bm: 'ALL',
            total: 5
          }); //add 'All' options
          //this.states.push(response.data);
          this.ricpstatus = [...this.ricpstatus, ...response.data];
          //this.states.sort((a, b) => a.nm_en.localeCompare(b.nm_en)); // sorting
          this.ricpStatus = this.ricpstatus[0].param_cd;
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