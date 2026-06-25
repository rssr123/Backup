import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { rilt } from '../../core/models/rilt.interface';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Router } from '@angular/router';
import { DatePipe, formatDate } from '@angular/common';
import { NgbPaginationConfig } from '@ng-bootstrap/ng-bootstrap';
import { environment } from 'src/environments/environment';
import moment from 'moment';
import { MatDialog } from '@angular/material/dialog';
import { Systemstatus } from '../../shared/enums/systemstatus';
import { fadeInOut } from '../../shared/animation';
import { ParamData } from '../../core/models/param.interface';
import { ParamService } from '../../core/services/param.service';
import { TranslateService } from '@ngx-translate/core';
import { GlobalService } from 'src/app/shared/global.service';
import { perm } from 'src/permissions/perm';
import { AuthService } from 'src/app/core/services/auth.service';

import { ParamList } from 'src/app/core/models/param-list.interface';

@Component({
  selector: 'app-rilt-listing',
  templateUrl: './rilt-listing.component.html',
  styleUrls: ['./rilt-listing.component.scss']
})
export class RiltListingComponent {
  page = environment.DefaultPage;
    itemsPerPage = environment.ItemPerPage;
    totalRecords: number = 0;
    isReadOnly = false;

    isDisplay: boolean = false;
    isLoading: boolean = false;
    rightSectionCollapsed: boolean = true;
    selectedValue = environment.dropdownOptions[0];
    dropdownOptions = environment.dropdownOptions;

    model: rilt[] = [];

    entityTypes: ParamList[] = [];
    statuses: ParamList[] = [];

    iLitigationNo: String | null = null;
    iLitigationItemRef: String | null = null;
    iEntityType: String | null = null;
    iEntityNo: String | null = null;
    iLitigationAmount: number | null = null;
    iStatus: String | null = null;
    iDateDue: Date | null = null;
    iDateCreated: Date | null = null;

    permList = perm.Accrual_Listing_Deferred_Income_RI_Periodic_Lodgement_RI_Compound_View_Listing_Page + "," + perm.Accrual_Listing_Deferred_Income_RI_Periodic_Lodgement_RI_Compound_View_Details_Page
    permListResponse = "";
    permViewAllow: number = 0; // if 0 then not allow to view listing page, else allow
    permActionAllow: number = 0;
  
    toggleRightSection() {
      this.rightSectionCollapsed = !this.rightSectionCollapsed;
    }
  
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
      private authService: AuthService,
      public datepipe: DatePipe
    ) {
      config.maxSize = environment.PaginationMaxSize;
      config.boundaryLinks = true;
      this.translate.setDefaultLang(this.globalService.getGlobalValue());
      this.translate.use(this.globalService.getGlobalValue());
    }
  
    ngOnInit(): void {
      this.authorization();
      this.loadData();
      this.loadEntityTypes();
      this.loadStatuses();
    }

    authorization() {
      this.authService.checkUserRole(this.authService.username, this.permList)
      .subscribe(
        (response: any) => {
          this.permListResponse = response.data;
          this.permViewAllow = this.permListResponse.includes(perm.Accrual_Listing_Deferred_Income_RI_Periodic_Lodgement_RI_Compound_View_Listing_Page) ? 1 : 0;
          this.permActionAllow = this.permListResponse.includes(perm.Accrual_Listing_Deferred_Income_RI_Periodic_Lodgement_RI_Compound_View_Details_Page) ? 1 : 0;
  
          console.log("AuthList: " + this.permViewAllow, this.permActionAllow);
  
          if (this.permViewAllow === 0) {
            console.log("access-denied");
            this.router.navigate(['/access-denied']);
            return;
          }
  
          console.log("AuthResp: " + this.permViewAllow, this.permActionAllow);
        },
        (error: any) => {
          console.log(error);
        }
      );
    }

    loadData() {
      const headers = new HttpHeaders({
        Authorization: environment.authKey,
        'Content-Type': 'application/json',
      });
  
      this.isDisplay = true;
      this.isLoading = true;
      const url = environment.apiUrl + '/api/rilt/v1/getRILT';
  
      const Body: any = {
        i_page: this.page.toString(),
        i_size: this.itemsPerPage.toString(),
      };

      if (this.iLitigationNo && this.iLitigationNo.trim()) {
        Body.i_lit_no = this.iLitigationNo;
      }
  
      if (this.iLitigationItemRef && this.iLitigationItemRef.trim()) {
        Body.i_lit_item_ref = this.iLitigationItemRef;
      }
  
      if (this.iEntityType && this.iEntityType.trim()) {
        Body.i_entity_type = this.iEntityType;
      }
  
      if (this.iEntityNo && this.iEntityNo.trim()) {
        Body.i_entity_no = this.iEntityNo;
      }
  
      if (this.iLitigationAmount || this.iLitigationAmount === 0) {
        Body.i_lit_amount = this.iLitigationAmount;
      }
  
      if (this.iStatus && this.iStatus.trim()) {
        Body.i_status = this.iStatus;
      }

      if (this.iDateDue) {
        Body.i_dt_due_from = this.datepipe.transform(this.iDateDue, 'yyyy-MM-dd') + ' 00:00:00',
        Body.i_dt_due_to = this.datepipe.transform(this.iDateDue, 'yyyy-MM-dd') + ' 23:59:59'
      }
  
      if (this.iDateCreated) {
        Body.i_dt_created_fr = this.datepipe.transform(this.iDateCreated, 'yyyy-MM-dd') + ' 00:00:00',
        Body.i_dt_created_to = this.datepipe.transform(this.iDateCreated, 'yyyy-MM-dd') + ' 23:59:59'
      }

      console.log(Body);
  
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

    loadEntityTypes() {
      const headers = new HttpHeaders({
        Authorization: environment.authKey,
        'Content-Type': 'application/json',
      });
  
      const url3 = environment.apiUrl + '/api/helper/v1/getparamlist';
  
      const body3 = {
        i_status: null,
        i_param_grp_nm: "EntityType"
      }
  
      this.http.post(url3, body3, { headers }).subscribe(
        (response: any) => {
          this.entityTypes = response.data;
        },
        (error) => {
          console.error(error);
        }
      )
    }

    loadStatuses() {
      const headers = new HttpHeaders({
        Authorization: environment.authKey,
        'Content-Type': 'application/json',
      });
  
      const url3 = environment.apiUrl + '/api/helper/v1/getparamlist';
  
      const body3 = {
        i_status: null,
        i_param_grp_nm: "RILT-Status"
      }
  
      this.http.post(url3, body3, { headers }).subscribe(
        (response: any) => {
          this.statuses = response.data;
        },
        (error) => {
          console.error(error);
        }
      )
    }

    LoadData(singleItem: number) {
      this.selectedValue = singleItem;
      this.itemsPerPage = this.selectedValue;
      this.loadData();
    }

    apply(): void {
      this.loadData();
      this.rightSectionCollapsed = true;
    }
  
    reset(): void {
      this.iLitigationNo = null;
      this.iLitigationItemRef = null;
      this.iEntityType = null;
      this.iEntityNo = null;
      this.iLitigationAmount = null;
      this.iStatus = null;
      this.iDateDue = null;
      this.iDateCreated = null;
    }
  
    refreshMainPage(): void {
      this.page = 1;
      this.loadData();
    }

    navigateToIndividual(item: any) {
      const rilt_id = item.ricp_id
      this.router.navigate(['/rilt-details'], { state: { rilt_id } });
    }

}
