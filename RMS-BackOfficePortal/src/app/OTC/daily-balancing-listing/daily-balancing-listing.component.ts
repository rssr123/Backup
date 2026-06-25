import { Component, OnInit } from '@angular/core';
import { environment } from 'src/environments/environment';
import { DailyBalancingWarningComponent } from '../daily-balancing-warning/daily-balancing-warning.component';
import { ActivatedRoute ,Router } from '@angular/router';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { MatDialog } from '@angular/material/dialog';
import { TranslateService } from '@ngx-translate/core';
import { GlobalService } from 'src/app/shared/global.service';
import { AuthService } from 'src/app/core/services/auth.service';
import { OTCDailyBal } from 'src/app/core/models/otc-daily-bal';
import { perm } from 'src/permissions/perm';

@Component({
  selector: 'app-daily-balancing-listing',
  templateUrl: './daily-balancing-listing.component.html',
  styleUrls: ['./daily-balancing-listing.component.scss']
})
export class DailyBalancingListingComponent implements OnInit{
  constructor(
    private http: HttpClient,
    private router: Router,
    private route: ActivatedRoute,
    public dialog: MatDialog,
    private translateService: TranslateService,
    private globalService: GlobalService,
    private authService: AuthService,
  ) {
    this.translateService.setDefaultLang(this.globalService.getGlobalValue());
    this.translateService.use(this.globalService.getGlobalValue());
  }

  modelParam: OTCDailyBal[] = [];
  modelList: OTCDailyBal[] = [];
  modelBal: OTCDailyBal[] = [];
  rightSectionCollapsed: boolean = true;
  showResultAlert: boolean = false;
  totalRecords: number = 0;
  page = environment.DefaultPage;
  itemsPerPage = environment.ItemPerPage;

  isDisplay: boolean = false;
  isLoading: boolean = false;
  isEmptyResult = false;

  warningBox: boolean = false;
  successBox: boolean = false;
  errorBox: boolean = false;

  balancingDate: Date = new Date();
  selectedBranchCode: any = null;

  balStatus: string = "";
  balType: string = "";
  showSubmit: boolean = false;
  showView: boolean = false;
  showNotification: boolean = false;

  //Permissions
  permOTCDBal = perm.OTC_DAILY_BALANCING_View_Listing;
  permOTCDBalAllow = "";
  permOTCDBalListingAllow = 0;

  ngOnInit(): void {
    this.loadPermission();
    this.balancingDate = new Date();
    this.loadBranchCode();

    if(history.state.success != null && history.state.success === true){
      this.successBox = true;
      this.balancingDate = history.state.balancingDate;
      this.selectedBranchCode.branch_code = history.state.branch_code;
    }
    else{
      this.successBox = false;
    }
  }

  toggleRightSection() {
    this.rightSectionCollapsed = !this.rightSectionCollapsed;
  }

  viewCtrBal(item: any):void{
    const otc_counter_id = item.otc_counter_id;
    const counter_id = item.counter_id;
    this.router.navigate(['/counter-balancing-listing'], { state: { counter_id, otc_counter_id, flag: true} });
  }

  showResultAlertBox() {
    this.showResultAlert = true;
    setTimeout(() => (this.showResultAlert = false), 2000);
  }

  DefaultBox() {
    this.warningBox = false;
  }

  clear(){
    this.selectedBranchCode = null;
    this.balancingDate = new Date();
    this.isEmptyResult = true;
    this.modelList = [];
    this.totalRecords = 0;
  }

  loadPermission(){
    this.authService.checkUserRole(this.authService.username, this.permOTCDBal)
    .subscribe(
      (response: any) => {
        this.permOTCDBalAllow = response.data;
        this.permOTCDBalListingAllow = this.permOTCDBalAllow.includes(perm.OTC_DAILY_BALANCING_View_Listing) ? 1 : 0;
        if(this.permOTCDBalListingAllow === 0){
          this.router.navigate(['/access-denied']);
          return;
        }
      }
    )
  }

  loadBranchCode(){
    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    this.isDisplay = true;
    this.isLoading = true;
    const url = environment.apiUrl + '/api/otcdailybal/v1/getotcbranchcode';

    this.http.post(url, {}, { headers }).subscribe(
      (response: any) => {
        this.modelParam = response.data;
        if (response.data.length == 0) {
          this.isDisplay = true;
          this.showResultAlertBox();
          this.isLoading = false;
          this.isEmptyResult = true;
        } else {
          this.isEmptyResult = true;
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

search(): void{
  // Set your authorization header
  const headers = new HttpHeaders({
    Authorization: environment.authKey,
    'Content-Type': 'application/json',
  });

  this.isDisplay = true;
  this.isLoading = true;
  const url = environment.apiUrl + '/api/otcdailybal/v1/getotcdailyballisting';

  const Body: any = {
    branch_code: this.selectedBranchCode.branch_code,
    bal_date: this.balancingDate,
  };

  this.http.post(url, Body, { headers }).subscribe(
    (response: any) => {
      this.modelList = response.data;
      if (response.data.length == 0) {
        this.isDisplay = true;
        this.isEmptyResult = true;
        this.showResultAlertBox();
        this.isLoading = false;
        this.successBox = false;
        this.errorBox = false;
      } else {
        this.isEmptyResult = false;
        this.isLoading = false;
        this.successBox = false;
        this.errorBox = false;

        if(this.modelList[0].total === 0){
          this.isEmptyResult = true;
        }
        else{
          this.balStatusValidation();
        }
        
        this.totalRecords = this.modelList[0].total;        
      }
    },
    (error) => {
      console.error(error);
      this.isLoading = false;
      // Handle errors here
    }
  );    
}

balStatusValidation(): void{
  // Set your authorization header
  const headers = new HttpHeaders({
    Authorization: environment.authKey,
    'Content-Type': 'application/json',
  });

  this.isDisplay = true;
  this.isLoading = true;
  const url = environment.apiUrl + '/api/otcdailybal/v1/checkotcbalstatus';

  const Body: any = {
    branch_code: this.selectedBranchCode.branch_code,
    bal_date: this.balancingDate,
  };

  this.http.post(url, Body, { headers }).subscribe(
    (response: any) => {
      this.modelBal = response.data;
      if (response.data.length == 0) {
        this.isDisplay = true;
        this.isEmptyResult = true;
        this.showResultAlertBox();
        this.isLoading = false;
      } else {
        this.isEmptyResult = false;
        this.isLoading = false;
       
        if(this.modelBal.length > 0){
          this.balStatus = this.modelBal[0].bal_status;
          this.balType = this.modelBal[0].bal_type;

          const submitStatusesForC = ["IP", "N", "C", "P"];
          const submitStatusesForD = ["IP", "N", "P"];
          const viewStatusesForD = ["C"];
      
          // Determine whether the submit button should be **shown**
          this.showSubmit = 
              (this.balType === "C" && submitStatusesForC.includes(this.balStatus)) ||
              (this.balType === "D" && submitStatusesForD.includes(this.balStatus));

          // Determine whether the view button should be **shown**
          this.showView = (this.balType === "D" && viewStatusesForD.includes(this.balStatus) || 
          (this.balType !== "C" && this.balType !== "D"));

          // Determine whether the success box sohuld be **shown**
          this.showNotification = (this.balType === "D" && viewStatusesForD.includes(this.balStatus) || 
          (this.balType !== "C" && this.balType !== "D"));
        }
      }
    },
    (error) => {
      console.error(error);
      this.isLoading = false;
      // Handle errors here
    }
  ); 
}

viewDailyBal(): void{
  this.router.navigate(['/daily-balancing-detail'],
    { state: 
      { 
        branch_code: this.selectedBranchCode.branch_code,
        bal_date: this.balancingDate,
        flag: true
      } });
}

updStatus(): void{
  // Set your authorization header
  const headers = new HttpHeaders({
    Authorization: environment.authKey,
    'Content-Type': 'application/json',
  });

  this.isDisplay = true;
  this.isLoading = true;
  const url = environment.apiUrl + '/api/otcdailybal/v1/updotcdailybalstatus';

  const Body: any = {
    branch_code: this.selectedBranchCode.branch_code,
    bal_date: this.balancingDate,
    bal_status: 'IP',
    bal_type: 'D',
  };

  this.http.post(url, Body, { headers }).subscribe(
    (response: any) => {
      this.modelList = response.data;
      if (response.data.length == 0) {
        this.isDisplay = true;
        this.isEmptyResult = true;
        this.showResultAlertBox();
        this.isLoading = false;
      } else {
        this.isEmptyResult = false;
        this.isLoading = false;
        this.router.navigate(['/daily-balancing-detail'], 
          { state: 
            {
            branch_code: this.selectedBranchCode.branch_code,
            bal_date: this.balancingDate,
            flag: false,
          }
        });
      }
    },
    (error) => {
      console.error(error);
      this.isLoading = false;
      // Handle errors here
    }
  );        
}

submit(): void{
    this.DefaultBox();
    const dialogRef = this.dialog.open(DailyBalancingWarningComponent, { 
      height: '40%',
      width: '50%',
      data:{
        bc: this.selectedBranchCode.branch_code,
        bd: this.balancingDate
      },
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result?.notification) {
        this.errorBox = true;
      } else {
        this.errorBox = false;
        this.updStatus();
      }
    });

    this.warningBox = true;      
  }
}
