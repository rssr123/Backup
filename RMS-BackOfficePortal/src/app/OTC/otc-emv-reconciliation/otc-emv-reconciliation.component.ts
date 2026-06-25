import { ChangeDetectorRef, Component } from '@angular/core';
import { environment } from 'src/environments/environment';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { DatePipe, formatDate } from '@angular/common';
import { NgbPaginationConfig } from '@ng-bootstrap/ng-bootstrap';
import { TranslateService } from '@ngx-translate/core';
import { AuthService } from 'src/app/core/services/auth.service';
import { GlobalService } from 'src/app/shared/global.service';
import { ParamService } from 'src/app/core/services/param.service';
import { perm } from 'src/permissions/perm';

import { OTCEMVReconciliation, OTCEMVReconciliationCheck, OTCEMVReconciliationStatus } from 'src/app/core/models/otc-emv-reconciliation.interface';

@Component({
  selector: 'app-otc-emv-reconciliation',
  templateUrl: './otc-emv-reconciliation.component.html',
  styleUrls: ['./otc-emv-reconciliation.component.scss']
})
export class OtcEmvReconciliationComponent {
  isLoading: boolean = false;
  totalRecords: number = 0;

  page = environment.DefaultPage;
  itemsPerPage = environment.ItemPerPage;
  selectedValue = environment.dropdownOptions[0];
  dropdownOptions = environment.dropdownOptions;
  dropDownSize = environment.DropDownSize;
  isDisplay: boolean = true;

  permList = perm.OTC_EMV_Reconciliation_View_Listing_Page
  permListResponse = "";
  permViewAllow: number = 0; // if 0 then not allow to view listing page, else allow
  permActionAllow: number = 0;

  model: OTCEMVReconciliation[] = [];
  model2: OTCEMVReconciliationCheck[] = [];
  rcEmvStatus: OTCEMVReconciliationStatus[] = [];

  rcEmvStatusId: number | null = null;
  rcEmvStatusStatus: string | null = null;

  balancingDate: Date | null = null;
  selectedBalancingDate: Date | null = null;

  constructor(
    private http: HttpClient,
    config: NgbPaginationConfig,
    private router: Router,
    public dialog: MatDialog,
    private ParamService: ParamService,
    private translate: TranslateService,
    private globalService: GlobalService,
    private cd: ChangeDetectorRef,
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
    this.getCurrentDate();
    this.loadData();
  }

  authorization() {
    this.authService.checkUserRole(this.authService.username, this.permList)
    .subscribe(
      (response: any) => {
        this.permListResponse = response.data;
        this.permViewAllow = this.permListResponse.includes(perm.OTC_EMV_Reconciliation_View_Listing_Page) ? 1 : 0;

        console.log("AuthList: " + this.permViewAllow);

        if (this.permViewAllow === 0) {
          console.log("access-denied");
          this.router.navigate(['/access-denied']);
          return;
        }

        console.log("AuthResp: " + this.permViewAllow);
      },
      (error: any) => {
        console.log(error);
      }
    );
  }

  search() {
    this.selectedBalancingDate = this.balancingDate;
    this.loadData();
    this.checkData();
    if (this.balancingDate) {
      this.getRcEmvStatus(0);
    };
  }

  loadData() {
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    this.isDisplay = true;
    this.isLoading = true;
    const url = environment.apiUrl + '/api/OTCEMVR/v1/getotcemvreconciliation';

    const Body: any = {
      i_page: this.page.toString(),
      i_size: this.itemsPerPage.toString(),
    };

    if (this.balancingDate) {
      const formattedDate = this.datepipe.transform(this.balancingDate, 'yyyy-MM-dd');
      if (formattedDate) {
        Body.i_date_from = formattedDate + ' 00:00:00';
        Body.i_date_to = formattedDate + ' 23:59:59';
      } else {
        Body.i_date_from = null;
        Body.i_date_to = null;
      }
    } else {
      Body.i_date_from = null;
      Body.i_date_to = null;
    }    

    console.log(Body);

    Body.i_date = this.balancingDate;

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

  checkData() {
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    this.isDisplay = true;
    this.isLoading = true;
    const url = environment.apiUrl + '/api/OTCEMVR/v1/getotcemvreconciliationcheck';

    const Body: any = {
    };

    if (this.balancingDate) {
      const formattedDate = this.datepipe.transform(this.balancingDate, 'yyyy-MM-dd');
      if (formattedDate) {
        Body.i_date_from = formattedDate + ' 00:00:00';
        Body.i_date_to = formattedDate + ' 23:59:59';
      } else {
        Body.i_date_from = null;
        Body.i_date_to = null;
      }
    } else {
      Body.i_date_from = null;
      Body.i_date_to = null;
    }    

    console.log(Body);

    Body.i_date = this.balancingDate;

    this.http.post(url, Body, { headers }).subscribe(
      (response: any) => {
        this.model2 = response.data;
        console.log(response.data);
        this.isLoading = false;
      },
      (error) => {
        console.error(error);
        this.isLoading = false;
      }
    );
  }

  LoadData(singleItem: number) {
    this.selectedValue = singleItem;
    this.itemsPerPage = this.selectedValue;
    this.loadData();
  }

  checkValid() {
    let ret = false;
    if (!this.balancingDate) {
      return ret;
    }
    for (const item of this.model2) {
      if (item.flag) {
        ret = true;
        break;
      }
    }
    return ret;
  }

  checkValidLite() {
    let ret = false;
    if (this.balancingDate) {
      for (const item of this.model2) {
        if (item.flag) {
          ret = true;
          break;
        }
        else{
          ret = false;
          break;
        }
      }
    }
    else {
      ret = true;
    }
    return ret;
  }

  clearFields(): void {
    this.getCurrentDate();
  }

  getCurrentDate() {
    this.balancingDate = null;
    const today = new Date();
    const d = new Date(today.getFullYear(), today.getMonth(), today.getDate(), 0, 0, 0);
    this.balancingDate = d;
  }

  navigateToIndividual(item: any): void {
    const balDate = this.datepipe.transform(this.balancingDate, 'yyyy-MM-dd');
    const branchCode = item.branch_cd;
    this.router.navigate(['/daily-balancing-detail'], 
      { state: 
        {
        branch_code: branchCode,
        bal_date: balDate,
        flag: false,
      }
    })
  }

  navigateToDetails(): void {
    const balancingdate = this.datepipe.transform(this.selectedBalancingDate, 'yyyy-MM-dd');
    this.router.navigate(['/otc-emv-reconciliation-details'], { state: { balancingdate } });
  }

  begin() {
    this.updateRcEmvStatus('IP');
  }

  beginlite() {
    this.navigateToDetails();
  }

  getRcEmvStatus(recur: number) {
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    this.isDisplay = true;
    this.isLoading = true;
    const url = environment.apiUrl + '/api/OTCEMVR/v1/getrcemv';

    const Body: any = {
      i_date_from: this.datepipe.transform(this.balancingDate, 'yyyy-MM-dd') + ' 00:00:00',
      i_date_to: this.datepipe.transform(this.balancingDate, 'yyyy-MM-dd') + ' 23:59:59',
    };

    console.log(Body);

    this.http.post(url, Body, { headers }).subscribe(
      (response: any) => {
        this.rcEmvStatus = response.data;
        if (recur == 0 && this.rcEmvStatus.length == 0) {
          this.insertRcEmvStatus();
        }
        this.extractStatus();
        // console.log("rcEmvStatus: ", this.rcEmvStatus);
        this.isLoading = false;
      },
      (error) => {
        console.error(error);
        this.isLoading = false;
      }
    );
  }

  extractStatus(): void {
    this.rcEmvStatusId = null;
    this.rcEmvStatusStatus = null;
    this.rcEmvStatusId = this.rcEmvStatus[0].rc_emv_id;
    this.rcEmvStatusStatus = this.rcEmvStatus[0].rc_emv_status;
  }

  insertRcEmvStatus() {
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    this.isDisplay = true;
    this.isLoading = true;
    const url = environment.apiUrl + '/api/OTCEMVR/v1/insrcemv';

    const Body: any = {
      i_dt_balancing: this.datepipe.transform(this.balancingDate, 'yyyy-MM-dd') + ' 12:00:00',
      i_rc_emv_status: 'P'
    };

    console.log(Body);

    this.http.post(url, Body, { headers }).subscribe(
      (response: any) => {
        // console.log("insertRcEmvStatus: ", response);
        if (response.data.length > 0) {
          this.getRcEmvStatus(1);
        }
        this.isLoading = false;
      },
      (error) => {
        console.error(error);
        this.isLoading = false;
      }
    );
  }

  updateRcEmvStatus(status: string) {
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    this.isDisplay = true;
    this.isLoading = true;
    const url = environment.apiUrl + '/api/OTCEMVR/v1/updrcemv';

    const Body: any = {
      i_dt_balancing: this.datepipe.transform(this.selectedBalancingDate, 'yyyy-MM-dd') + ' 12:00:00',
      i_rc_emv_status: status
    };

    console.log(Body);

    this.http.post(url, Body, { headers }).subscribe(
      (response: any) => {
        // console.log("updateRcEmvStatus: ", response);
        this.navigateToDetails();
      },
      (error) => {
        console.error(error);
        this.isLoading = false;
      }
    );
  }
  
}
