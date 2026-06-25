import { Component, OnInit } from '@angular/core';
import { environment } from 'src/environments/environment';
import { Router } from '@angular/router';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { MatDialog } from '@angular/material/dialog';
import { TranslateService } from '@ngx-translate/core';
import { GlobalService } from 'src/app/shared/global.service';
import { AuthService } from 'src/app/core/services/auth.service';
import { OTCMasterBal } from 'src/app/core/models/otc-master-bal';
import { OTCDailyBal } from 'src/app/core/models/otc-daily-bal';
import { perm } from 'src/permissions/perm';

@Component({
  selector: 'app-master-balancing-listing',
  templateUrl: './master-balancing-listing.component.html',
  styleUrls: ['./master-balancing-listing.component.scss']
})
export class MasterBalancingListingComponent  implements OnInit{
  constructor(
    private http: HttpClient,
    private router: Router,
    public dialog: MatDialog,
    private translateService: TranslateService,
    private globalService: GlobalService,
    private authService: AuthService,
  ) {
    this.translateService.setDefaultLang(this.globalService.getGlobalValue());
    this.translateService.use(this.globalService.getGlobalValue());
  }

  modelParam: OTCMasterBal[] = [];
  modelList: OTCMasterBal[] = [];
  modelBal: OTCDailyBal[] = [];
  modelValidation: OTCDailyBal[] = [];
  rightSectionCollapsed: boolean = true;
  showResultAlert: boolean = false;
  totalRecords: number = 0;
  page = environment.DefaultPage;
  itemsPerPage = environment.ItemPerPage;

  isDisplay: boolean = false;
  isLoading: boolean = false;
  isEmptyResult = false;

  successBox: boolean = false;
  errorBox: boolean = false;
  errorBox1: boolean = false;
  flag: boolean = false;

  status: String = '';

  balStatus: string = "";
  balType: string = "";
  showSubmit: boolean = false;
  disableSubmit : boolean = false;
  showView: boolean = false;

  balancingDate: Date = new Date();
  selectedBranchCode: any = null;

  //Permissions
  permOTCMBal = perm.OTC_MASTER_BALANCING_View_Listing;
  permOTCMBalAllow = "";
  permOTCMBalListingAllow = 0;

  ngOnInit(): void {
    this.balancingDate = new Date();
    this.loadBranchCode();
    this.loadPermission();

    if(history.state.flag != null && history.state.flag != undefined){
      this.flag = history.state.flag;

      if(this.flag === true){
        this.successBox = true;
        this.errorBox = false;
        this.errorBox1 = false;
      }
      else{
        this.successBox = false;
        this.errorBox = false;
        this.errorBox1 = true;
      }
    }
  }

  toggleRightSection() {
    this.rightSectionCollapsed = !this.rightSectionCollapsed;
  }

  showResultAlertBox() {
    this.showResultAlert = true;
    setTimeout(() => (this.showResultAlert = false), 2000);
  }

  clear(){
    this.selectedBranchCode = null;
    this.balancingDate = new Date();
    this.isEmptyResult = true;
    this.modelList = [];
    this.totalRecords = 0;

  }

  search(){
    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });
    this.successBox = false;
    this.showView = false;
    this.errorBox = false;
    
    this.isDisplay = true;
    this.isLoading = true;
    const url = environment.apiUrl + '/api/otcmasterbal/v1/getotcmasterballist';

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
        } else {
          this.errorBox = false;
          this.errorBox1 = false;
          this.isEmptyResult = false;
          this.isLoading = false;
          
          if(this.modelList[0].total === 0 || this.modelList[0].total === null){
            this.isEmptyResult = true;
          }
          else{
            this.balStatusValidation();
          }

          this.status = this.modelList[0].daily_bal_status;

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

  viewCtrBal(item: any){
    this.router.navigate(['/daily-balancing-detail'],
      { state: 
        { 
          branch_code: item.branch_code,
          bal_date: this.balancingDate,
          flag: true
        } });
  }

  loadPermission(){
    this.authService.checkUserRole(this.authService.username, this.permOTCMBal)
    .subscribe(
      (response: any) => {
        this.permOTCMBalAllow = response.data;
        this.permOTCMBalListingAllow = this.permOTCMBalAllow.includes(perm.OTC_MASTER_BALANCING_View_Listing) ? 1 : 0;
        if(this.permOTCMBalListingAllow === 0){
          this.router.navigate(['/access-denied']);
          return;
        }
      }
    )
  }

  submit(){
    if(this.status === null || this.status === ''){
      this.errorBox = true;
    }
    else if(this.modelList[0].daily_bal_status == 'Completed'){
      this.isLoading = true;
      this.errorBox = false;
      this.validateStatus();
    }
    else{
      this.errorBox = true;
    }
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
  
            const invalidStatusesForMaster = ["IP", "N", "P"];
            const completedStatusForMaster = ["C"];
            const completedStatusForDaily = ["C"];
        
            // Determine whether the submit button should be **shown**
            this.showSubmit = 
                (this.balType === "BM" && invalidStatusesForMaster.includes(this.balStatus)) 
                ||
                (this.balType === "D");

            // Determine whether the submit button should be **disabled**
            this.disableSubmit = this.balType === "D" && !completedStatusForDaily.includes(this.balStatus);

            // Determine whether the view button should be **shown**
            this.showView = this.balType === "BM" && completedStatusForMaster.includes(this.balStatus);
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

  validateStatus(): void{
    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    this.isDisplay = true;
    this.isLoading = true;
    const url = environment.apiUrl + '/api/otcdailybal/v1/checkotcdailybalval';

    const Body: any = {
      branch_code: this.selectedBranchCode.branch_code,
      bal_date: this.balancingDate,
    };

    this.http.post(url, Body, { headers }).subscribe(
      (response: any) => {
        if (response.data.length == 0) {
          this.isDisplay = true;
          this.isLoading = false;
        } else {
          this.isEmptyResult = false;
          this.isLoading = false;

          if(response.data == 0){
            this.errorBox = false;
            this.errorBox1 = false;
            this.updStatus();
          }
          else{
            this.errorBox1 = true;
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

  viewMasterBal() :void{
    this.router.navigate(['/master-balancing-detail'], 
      { state: 
        { 
          branch_code: this.selectedBranchCode.branch_code,
          bal_date: this.balancingDate,
          flag: true,
      }
    });
  }

  updStatus(){
    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });
    this.isDisplay = true;
    this.isLoading = true;
    const url = environment.apiUrl + '/api/otcmasterbal/v1/updotcmasterbalstatus';

    const Body: any = {
      branch_code: this.selectedBranchCode.branch_code,
      bal_date: this.balancingDate,
      bal_status: 'IP',
      bal_type: 'BM',
    };

    this.http.post(url, Body, { headers }).subscribe(
      (response: any) => {
        if (response.data.length == 0) {
          this.isDisplay = true;
          this.isEmptyResult = true;
          this.showResultAlertBox();
          this.isLoading = false;
        } else {
          this.isEmptyResult = false;
          this.isLoading = false;
          this.router.navigate(['/master-balancing-detail'], 
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
}