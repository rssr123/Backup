import { ChangeDetectorRef, Component, NgZone, OnInit } from '@angular/core';
import { fadeInOut } from '../../shared/animation';
import { environment } from 'src/environments/environment';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { OTCCtrBalInfo } from 'src/app/core/models/otc-ctr-bal-info';
import { NgbPaginationConfig } from '@ng-bootstrap/ng-bootstrap';
import { Router } from '@angular/router';
import { Location } from '@angular/common';
import { MatDialog } from '@angular/material/dialog';
import { ParamService } from '../../core/services/param.service';
import { TranslateService } from '@ngx-translate/core';
import { GlobalService } from 'src/app/shared/global.service';
import { AuthService } from 'src/app/core/services/auth.service';
import { OTCRMSInfo } from 'src/app/core/models/otc-ctr-bal-rms';
import { OTCEMVInfo } from 'src/app/core/models/otc-ctr-bal-emv';
import { OTCPHYInfo } from 'src/app/core/models/otc-ctr-bal-phy';
import { OTCBalCash } from 'src/app/core/models/otc-bal-cash';
import { CounterBalancingEditComponent } from '../counter-balancing-edit/counter-balancing-edit.component';
import { utils, writeFile, WorkSheet } from 'xlsx';
import { perm } from 'src/permissions/perm';
import { OTCCollectionReceipting } from 'src/app/core/models/otc-collection-receipting.interface';
import { CounterCheckInStatus } from 'src/app/core/services/otc-counter-status.service';

import { jsPDF } from "jspdf";
import "jspdf-autotable";
import { trophy } from 'ngx-bootstrap-icons';

@Component({
  selector: 'app-counter-balancing-listing',
  templateUrl: './counter-balancing-listing.component.html',
  styleUrls: ['./counter-balancing-listing.component.scss'],
  animations: [fadeInOut]
})
export class CounterBalancingListingComponent implements OnInit{
  //Parameter
  i_counter_id: any = null;
  i_otc_counter_id: any = null;
  i_flag: boolean = false;

  modelCtrBalInfo: OTCCtrBalInfo[] = [];
  modelRMSInfo: OTCRMSInfo[] = [];
  modelEMVInfo: OTCEMVInfo[] = [];
  modelPHYInfo: OTCPHYInfo[] = [];
  modelBalCash: OTCBalCash[] = [];
  modelOTCCR: OTCCollectionReceipting[] = [];

  filterByCash: OTCPHYInfo[] = [];
  filterByCheque: OTCPHYInfo[] = [];
  filterByMO: OTCPHYInfo[] = [];
  filterByBD: OTCPHYInfo[] = [];

  page = environment.DefaultPage;
  itemsPerPage = environment.ItemPerPage;
  
  grandTotal = 0;
  grandCashTotal: number = 0;
  totalEMVAmount: number = 0;
  totalChequeAmount: number = 0;
  totalBDAmount: number = 0;
  totalMOAmount: number = 0;

  moneyMap = new Map<string, any>();   // Optimized data structure

  isDisplay: boolean = false;
  isLoading: boolean = false;
  showResultAlert = false;
  isEmptyResult = false;
  isEmptyResultRMS = false;
  isEmptyResultEMV = false;
  isEmptyResultChe = false;
  isEmptyResultBD = false;
  isEmptyResultMO = false;
  totalRecordsEMV: number = 0;
  totalRecordsRMS: number = 0;

  changePaymentModeBox: boolean = false;

  //default pagination
  selectedValue = environment.dropdownOptions[0];
  dropdownOptions = environment.dropdownOptions;

  //Permissions
  permOTCCBal = perm.OTC_COUNTER_BALANCING_Detail_Page;
  permOTCCBalAllow = "";
  permOTCCBalDetailAllow = 0;
  
  constructor(
    private http: HttpClient,
    config: NgbPaginationConfig,
    private router: Router,
    public dialog: MatDialog,
    private ParamService: ParamService,
    private cdr: ChangeDetectorRef,
    private translateService: TranslateService,
    private globalService: GlobalService,
    private authService: AuthService,
    private location: Location,
    public counterCheckInStatus: CounterCheckInStatus,
    private zone: NgZone,
  ){
    counterCheckInStatus.counterIdChanged.subscribe(counterId=>this.updatedCounterId(counterId));
  }

  moneys = [
    { param_cd: 'cd1', name: 'RM 100.00', price: 100, quantity: 0 },
    { param_cd: 'cd2', name: 'RM 50.00', price: 50, quantity: 0 },
    { param_cd: 'cd3', name: 'RM 20.00', price: 20, quantity: 0 },
    { param_cd: 'cd4', name: 'RM 10.00', price: 10, quantity: 0 },
    { param_cd: 'cd5', name: 'RM 5.00', price: 5, quantity: 0 },
    { param_cd: 'cd6', name: 'RM 1.00', price: 1, quantity: 0 },
    { param_cd: 'cd7', name: 'RM 0.50 (50 sen)', price: 0.5, quantity: 0 },
    { param_cd: 'cd8', name: 'RM 0.20 (20 sen)', price: 0.2, quantity: 0 },
    { param_cd: 'cd9', name: 'RM 0.10 (10 sen)', price: 0.1, quantity: 0 },
    { param_cd: 'cd10', name: 'RM 0.05 (5 sen)', price: 0.05, quantity: 0 }
  ];

  async updatedCounterId(counterId: any): Promise<void>{
    this.isLoading = true;
    this.loadPermission();

    const { flag, counter_id, otc_counter_id } = history.state || {};
    this.i_flag = !!flag;

    if(!this.i_flag){
      if(this.counterCheckInStatus.counterId != null){
        if(this.counterCheckInStatus.counterId.counter_id.length > 0){
          this.i_counter_id = this.counterCheckInStatus.counterId.counter_id;
          this.i_otc_counter_id = this.counterCheckInStatus.counterId.otc_counter_id;

          await this.loadEverything(flag);
        }
      }
    }
    else{
      this.i_counter_id = history.state.counter_id;
      this.i_otc_counter_id = history.state.otc_counter_id;

      await this.loadEverything(flag);
    }
  }

  async ngOnInit(): Promise<void> {
    this.isLoading = true;
    this.loadPermission();

    const alreadyReloaded = sessionStorage.getItem('alreadyReloaded');

    var tmp = <HTMLElement>document.getElementsByTagName('app-root')[0];
    tmp.style.pointerEvents = 'all';

    const { flag, counter_id, otc_counter_id } = history.state || {};
    this.i_flag = !!flag;
    if (!this.i_flag) {
      if(this.counterCheckInStatus.counterId != null){
        if(this.counterCheckInStatus.counterId.counter_id.length > 0){
          this.i_counter_id = this.counterCheckInStatus.counterId.counter_id;
          this.i_otc_counter_id = this.counterCheckInStatus.counterId.otc_counter_id;
  
          await this.loadEverything(flag);
        };
      }
    } else {
      if (!alreadyReloaded) {
        sessionStorage.setItem('alreadyReloaded', 'true');
        window.location.reload();
        return;
      }
      
      this.i_counter_id = counter_id;
      this.i_otc_counter_id = otc_counter_id;
      await this.loadEverything(flag);
    }
  }

  private async loadEverything(flag: boolean): Promise<void> {
    try {
      await Promise.all([
        this.loadCtrInfo(),
        this.loadRMSInfo(),
        flag ? this.loadCashInfo() : Promise.resolve(),
        this.loadEMVInfo()
      ]);
      await this.loadPHYInfo();
      this.isLoading = false;
    } catch (err) {
      console.error("Error during loading data:", err);
      this.isLoading = false;
    }
    finally{
      this.isLoading = false;
    }
  }

  calculateTotal() {
    this.grandCashTotal = this.moneys.reduce((sum, money) => sum + money.price * money.quantity, 0);
    this.calculateGrandTotal();
  }

  calculateGrandTotal(){
    if(this.i_flag == false){
      this.grandTotal = this.totalEMVAmount + this.grandCashTotal + this.totalChequeAmount + this.totalBDAmount + this.totalMOAmount;
    }
    else{
      const balCashTotal = this.modelBalCash?.[0]?.total || 0;
      this.grandTotal = this.totalEMVAmount + this.totalChequeAmount + this.totalBDAmount + this.totalMOAmount + balCashTotal;
    }
 }

  preventInvalidInput(event: KeyboardEvent): void {
    const invalidChars = ['-', '.', 'e', 'E'];

    if (invalidChars.includes(event.key)) {
        event.preventDefault();
    }
  }

  showResultAlertBox() {
    this.showResultAlert = true;
    setTimeout(() => (this.showResultAlert = false), 2000);
  }

  changePaymentMode(item: any): void {
    this.DefaultBox();
    const dialogRef = this.dialog.open(CounterBalancingEditComponent, { 
      height: '80%',
      width: '60%',
      data: {item: JSON.parse(JSON.stringify(item))}
    });

    dialogRef.afterClosed().subscribe(async result => {
      if (result) {
        // Commit the changes only if the user submitted
        Object.assign(item, result);
        await this.loadCtrInfo();
        await this.loadRMSInfo();
        await this.loadPHYInfo();

        // Force view to update
        //window.location.reload();
        //this.cdr.detectChanges();
      }
    });

    this.changePaymentModeBox = true;
  }

  DefaultBox() {
    this.changePaymentModeBox = false;
  }

  LoadRMSData(singleItem: number) {
    this.selectedValue = singleItem;
    this.itemsPerPage = this.selectedValue;
    this.loadRMSInfo();
  }

  LoadEMVData(singleItem: number) {
    this.selectedValue = singleItem;
    this.itemsPerPage = this.selectedValue;
    this.loadEMVInfo();
  }

  loadPermission(){
    this.authService.checkUserRole(this.authService.username, this.permOTCCBal)
    .subscribe(
      (response: any) => {
        this.permOTCCBalAllow = response.data;
        this.permOTCCBalDetailAllow = this.permOTCCBalAllow.includes(perm.OTC_COUNTER_BALANCING_Detail_Page) ? 1 : 0;
        if(this.permOTCCBalDetailAllow === 0){
          this.router.navigate(['/access-denied']);
          return;
        }
      }
    )
  }

  async loadCtrInfo(): Promise<void>{
    return new Promise((resolve, reject) => {
      // Set your authorization header
      const headers = new HttpHeaders({
        Authorization: environment.authKey,
        'Content-Type': 'application/json',
      });

      this.isDisplay = true;
      //this.isLoading = true;
      const url = environment.apiUrl + '/api/otcctrbal/v1/getotcbalctrinfo';

      const Body: any = {
        counter_id: this.i_counter_id,
        otc_counter_id: this.i_otc_counter_id,
      };

      this.http.post(url, Body, { headers }).subscribe(
        (response: any) => {
          this.modelCtrBalInfo = response.data;
          if (response.data.length == 0) {
            this.isDisplay = true;
            this.showResultAlertBox();
            //this.isLoading = false;
          } else {
            //this.isLoading = false;
            this.totalEMVAmount = this.modelCtrBalInfo[0].total_emv;
          }

          // Resolve the Promise when the operation is successful
          resolve(); 
        },
        (error) => {
          console.error(error);
          //this.isLoading = false;
          // Handle errors here
          reject(error); // Reject the Promise in case of an error
        }
      );
    });
  }

  async loadRMSInfo(): Promise<void>{
    return new Promise((resolve, reject) => {
      // Set your authorization header
      const headers = new HttpHeaders({
        Authorization: environment.authKey,
        'Content-Type': 'application/json',
      });

      this.isDisplay = true;
      //this.isLoading = true;
      const url = environment.apiUrl + '/api/otcctrbal/v1/getotcrmscol';

      const Body: any = {
        i_page : 1,
        i_size : 20,
        counter_id: this.i_counter_id,
        otc_counter_id: this.i_otc_counter_id,
      };

      this.http.post(url, Body, { headers }).subscribe(
        (response: any) => {
          this.modelRMSInfo = response.data;
          if (response.data.length == 0) {
            this.totalRecordsRMS = 0;
            this.isDisplay = true;
            this.isEmptyResultRMS = true;
            this.showResultAlertBox();
            //this.isLoading = false;
          } else {
            this.isEmptyResultRMS = false;
            //this.isLoading = false;
            this.totalRecordsRMS = this.modelRMSInfo[0].total;
          }

          // Resolve the Promise when the operation is successful
          resolve(); 
        },
        (error) => {
          console.error(error);
          //this.isLoading = false;
          // Handle errors here
          reject(error); // Reject the Promise in case of an error
        }
      );
    });
  }

  async loadEMVInfo(): Promise<void>{
    return new Promise((resolve, reject) => {
      // Set your authorization header
      const headers = new HttpHeaders({
        Authorization: environment.authKey,
        'Content-Type': 'application/json',
      });

      this.isDisplay = true;
      this.isLoading = true;
      const url = environment.apiUrl + '/api/otcctrbal/v1/getotcctrcol';

      const Body: any = {
        i_page : 1,
        i_size : 20,
        counter_id: this.i_counter_id,
        otc_counter_id: this.i_otc_counter_id,
      };

      this.http.post(url, Body, { headers }).subscribe(
        (response: any) => {
          this.modelEMVInfo = response.data;
          if (response.data.length == 0) {
            this.totalRecordsEMV = 0;
            this.isDisplay = true;
            this.isEmptyResultEMV = true;
            this.showResultAlertBox();
            this.isLoading = false;
          } else {
            this.isEmptyResultEMV = false;
            this.isLoading = false;
            this.totalRecordsEMV = this.modelEMVInfo[0].total;
            //this.totalEMVAmount = this.modelEMVInfo.reduce((sum,emv) => sum + emv.amount,0);
          }

          // Resolve the Promise when the operation is successful
          resolve(); 
        },
        (error) => {
          console.error(error);
          this.isLoading = false;
          // Handle errors here
          reject(error); // Reject the Promise in case of an error
        }
      );
    });
  }

  async loadPHYInfo(): Promise<void>{
    return new Promise((resolve, reject) => {
      // Set your authorization header
      const headers = new HttpHeaders({
        Authorization: environment.authKey,
        'Content-Type': 'application/json',
      });

      this.isDisplay = true;
      //this.isLoading = true;
      const url = environment.apiUrl + '/api/otcctrbal/v1/getotcphyinfo';

      const Body: any = {
        counter_id : this.i_counter_id,
        otc_counter_id: this.i_otc_counter_id,
      };

      this.http.post(url, Body, { headers }).subscribe(
        (response: any) => {
          this.zone.run(() => {
            this.modelPHYInfo = response.data;
      
            if (response.data.length == 0) {
              this.isDisplay = true;
              this.isEmptyResultChe = true;
              this.isEmptyResultBD = true;
              this.isEmptyResultMO = true;
              this.showResultAlertBox();
              this.calculateGrandTotal();
            } else {
              this.filterByCheque = this.modelPHYInfo.filter(item=> item.detail_type.trim().toLowerCase() === 'cheque');
              this.filterByBD = this.modelPHYInfo.filter(item=> item.detail_type.trim().toLowerCase() === 'bank draft');
              this.filterByMO = this.modelPHYInfo.filter(item=> item.detail_type.trim().toLowerCase() === 'money order');
              this.filterByCash = this.modelPHYInfo.filter(item=> item.detail_type.trim().toLowerCase() === 'cash');
      
              this.isEmptyResultChe = this.filterByCheque.length == 0;
              this.isEmptyResultBD = this.filterByBD.length == 0;
              this.isEmptyResultMO = this.filterByMO.length == 0;
      
              this.totalChequeAmount = this.filterByCheque.reduce((sum, che) => sum + che.che_amt, 0);
              this.totalBDAmount = this.filterByBD.reduce((sum, bd) => sum + bd.bd_amt, 0);
              this.totalMOAmount = this.filterByMO.reduce((sum, mo) => sum + mo.mo_amt, 0);
      
              this.calculateGrandTotal();
            }
      
            this.cdr.detectChanges(); // optional, just to be safe
            resolve();
          });
        },
        (error) => {
          console.error(error);
          reject(error);
        }
      );
    });
  }

  async loadCashInfo(): Promise<void>{
    return new Promise((resolve, reject) => {
      // Set your authorization header
      const headers = new HttpHeaders({
        Authorization: environment.authKey,
        'Content-Type': 'application/json',
      });

      this.isDisplay = true;
      this.isLoading = true;
      const url = environment.apiUrl + '/api/otcctrbal/v1/getotccashinfo';

      const Body: any = {
        otc_counter_id: this.i_otc_counter_id,
      };

      this.http.post(url, Body, { headers }).subscribe(
        (response: any) => {
          this.modelBalCash = response.data;
          if (response.data.length == 0) {
            this.isDisplay = true;
            this.isEmptyResult = true;
            this.showResultAlertBox();
            this.isLoading = false;
          } else {
            this.isEmptyResult = false;
            this.isLoading = false;

            // Clear and populate the moneyMap for fast lookup
            this.moneyMap.clear();
            this.modelBalCash.forEach((item) => {
              this.moneyMap.set(item.param_cd, item);
            });
          }
          // Resolve the Promise when the operation is successful
          resolve();
        },
        (error) => {
          console.error(error);
          this.isLoading = false;
          // Handle errors here
          reject(error); // Reject the Promise in case of an error
        }
      );
    });
  }

  fetchCollectionInfo(item: any): void { 
    const url = environment.apiUrl + '/api/OTCCR/v1/getcollectioninfo';
 
    const body = {
      i_page: '1',
      i_size: '10',
      i_coll_slip_no: item.col_slip_no,
      i_orn_no: item.orn_no,
      i_cust_nm: null,
      i_cust_phone: null,
      i_mtt_id: null
    };
 
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });
 
    this.http.post(url, body, { headers }).subscribe(
      (response: any) => {
        if (response.data && response.data.length > 0) {
          //const item = response.data[0];
          this.modelOTCCR = response.data[0];
          this.router.navigate(['/otc-receipt-screen', item.col_slip_no], {
            queryParams: { orn_no: item.orn_no, curr_page: "counter-balancing-listing" },
            state: { item: this.modelOTCCR }
          });
        } else {
          console.error("No data returned from API");
        }
      },
      (error) => {
        console.error("Error fetching collection info", error);
      }
    );
  }

  mappingDomination(param_cd: string): string {
    return this.moneyMap.get(param_cd)?.quantity || 'N/A';
  }

  isDataMatching(): boolean {

    if(!this.modelCtrBalInfo?.length) return false;
    return (
        Number((this.modelCtrBalInfo[0]?.total_emv ?? 0).toFixed(2)) === Number(this.totalEMVAmount.toFixed(2)) &&
        Number((this.modelCtrBalInfo[0]?.total_col ?? 0).toFixed(2)) === Number(this.grandCashTotal.toFixed(2)) &&
        Number((this.modelCtrBalInfo[0]?.total_che ?? 0).toFixed(2)) ===  Number(this.totalChequeAmount.toFixed(2)) &&
        Number((this.modelCtrBalInfo[0]?.total_bd ?? 0).toFixed(2)) ===  Number(this.totalBDAmount.toFixed(2)) &&
        Number((this.modelCtrBalInfo[0]?.total_mo ?? 0).toFixed(2)) ===  Number(this.totalMOAmount.toFixed(2)) && 
        Number((this.modelCtrBalInfo[0]?.total ?? 0).toFixed(2)) ===  Number(this.grandTotal.toFixed(2))
    );
  }

  submit():void{
    if(this.modelCtrBalInfo[0]?.total_col != 0 && this.grandCashTotal != 0){
      this.submitCash();
    }
    
    this.submitStatus();
  }

  submitStatus():void{
    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    this.isDisplay = true;
    this.isLoading = true;
    const url = environment.apiUrl + '/api/otcbalancingreq/v1/updotcbalstatus';

    const Body: any = {
      counter_id: this.i_counter_id,
      bal_status: 'C',
      bal_type: 'C',
      total_emv_amt: this.totalEMVAmount,
      total_phy_amt: this.grandCashTotal + this.totalChequeAmount + this.totalBDAmount + this.totalMOAmount,
      total_collection: this.grandTotal,
    };

    this.http.post(url, Body, { headers }).subscribe(
      (response: any) => {
        if (response.data.length == 0) {
          this.isDisplay = true;
          this.showResultAlertBox();
          this.isLoading = false;     
          console.log("submitStatus()");
          this.location.back();
        } else {
          this.isLoading = false;
          this.router.navigateByUrl('/home')
            .then(() => {
              this.router.navigate(['/counter-balancing-listing'], {
                state: {
                  counter_id: this.i_counter_id,
                  otc_counter_id: response.data,
                  flag: true
              }
              })
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

  submitCash():void{

    this.moneys = this.moneys.map(item => ({
      ...item,
      id: this.filterByCash[0].id  
    }));

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    this.isDisplay = true;
    this.isLoading = true;
    const url = environment.apiUrl + '/api/otcbalancingreq/v1/insotcbalcash';

    this.http.post(url, this.moneys, { headers }).subscribe(
      (response: any) => {
        
        if (response.data.length == 0) {
          this.isDisplay = true;
          this.showResultAlertBox();
          this.isLoading = false;
        } else {
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

  cancel():void{
    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    this.isDisplay = true;
    this.isLoading = true;
    const url = environment.apiUrl + '/api/otcbalancingreq/v1/updotcbalstatus';

    const Body: any = {
      counter_id: this.i_counter_id,
      bal_status: 'P',
      bal_type: 'C',
    };

    this.http.post(url, Body, { headers }).subscribe(
      (response: any) => {

        if (response.data.length == 0) {
          this.isDisplay = true;
          this.showResultAlertBox();
          this.isLoading = false;
          console.log("cancel() updotcbalstatus response.data.length == 0");
          //this.location.back();
          this.router.navigateByUrl('/home')
        } else {
          console.log("cancel() updotcbalstatus response.data.length != 0");
          this.isLoading = false;
          // this.location.back();
          this.router.navigateByUrl('/home')
        }
      },
      (error) => {
        console.error(error);
        this.isLoading = false;
        // Handle errors here
      }
    );
  }

  exportToPDF(): void {
    const doc = new jsPDF({ orientation: 'landscape', unit: 'mm', format: 'a4' });
    let isFirstPage = true; // Track if it's the first page to avoid unnecessary page breaks

    const dataToExport = [
        { 
            title: "Counter Info", 
            data: this.modelCtrBalInfo.length > 0 ? this.modelCtrBalInfo.map(item => ({
                "Counter ID": item.counter_id,
                "Check In Time": item.check_in,
                "User ID": item.user_id,
                "Branch Code": item.branch_cd,
                "No. of Orders Paid": item.orders_paid,
                "Total EMV (RM)": (item.total_emv ?? 0).toFixed(2),
                "Total Physical (RM)": (item.total_phy ?? 0).toFixed(2),
                "Total Cash (RM)": (item.total_col ?? 0).toFixed(2),
                "Total Cheque (RM)": (item.total_che ?? 0).toFixed(2),
                "Total Money Order (RM)": (item.total_mo ?? 0).toFixed(2),
                "Total Bank Draft (RM)": (item.total_bd ?? 0).toFixed(2),
                "Total Amount (RM)": (item.total ?? 0).toFixed(2),
            })) : [{ 
              "Counter ID": "",
              "Check In Time": "", 
              "User ID": "",
              "Branch Code": "", 
              "No. of Orders Paid": "", 
              "Total EMV (RM)": "", 
              "Total Physical (RM)": "", 
              "Total Cash (RM)": "", 
              "Total Cheque (RM)": "", 
              "Total Money Order (RM)": "", 
              "Total Bank Draft (RM)": "", 
              "Total Amount (RM)": "" }]
        },
        { 
            title: "RMS Collection Summary", 
            data: this.modelRMSInfo.length > 0 ? this.modelRMSInfo.map(item => ({
                "Collection Slip No": item.col_slip_no,
                "Order Reference No.": item.orn_no,
                "Payment Method": item.otc_pymt_mode,
                "EMV Amount (RM)": (item.emv_amt ?? 0).toFixed(2),
                "Cash Amount (RM)": (item.cash_amt ?? 0).toFixed(2),
                "Cheque Amount (RM)": (item.che_amt ?? 0).toFixed(2),
                "Bank Draft Amount (RM)": (item.bd_amt ?? 0).toFixed(2),
                "Money Order Amount (RM)": (item.mo_amt ?? 0).toFixed(2),
                "Total Amount (RM)": (item.gtotal ?? 0).toFixed(2),
            })) : [{ 
              "Collection Slip No": "", 
              "Order Reference No.": "", 
              "Payment Method": "", 
              "EMV Amount (RM)": "", 
              "Cash Amount (RM)": "", 
              "Cheque Amount (RM)": "", 
              "Bank Draft Amount (RM)": "", 
              "Money Order Amount (RM)": "", 
              "Total Amount (RM)": "" }]
        },
        {
          title: 'Counter EMV Collection', 
          data: this.modelEMVInfo.length > 0 ? this.modelEMVInfo.map(item => 
            ({
              'Collection Slip No': item.col_slip_no,
              'Order Reference No.': item.orn_no,
              'Transaction Trace': item.trans_trace,
              'Batch Number': item.batch_no,
              'Host Number': item.host_no,
              'Terminal ID': item.t_id,
              'Total Amount (RM)': (item.amount ?? 0).toFixed(2)
            })) : [{ 
              'Collection Slip No': '',
              'Order Reference No.': '',
              'Transaction Trace': '',
              'Batch Number': '',
              'Host Number': '',
              'Terminal ID': '',
              'Total Amount (RM)': ''
          }]
        },
        {
          title: 'Cash', 
          data: this.modelBalCash.length > 0 ? this.modelBalCash.map(item => 
            {
              const money = this.moneys.find(m => m.param_cd === item.param_cd);

              return {
                'Denomination': item.denomination,
                'Quantity': item.quantity,
                'Amount (RM)': money ? (money.price * item.quantity).toFixed(2) : '0.00'
              }
            }) : [{ 
              'Denomination': '',
              'Quantity': '',
              'Amount (RM)': ''
          }]
        },
        {
          title: 'Cheque', 
          data: this.filterByCheque.length > 0 ? this.filterByCheque.map(item => 
            ({
              'Collection Slip No': item.col_slip_no,
              'Order Reference No.': item.orn_no,
              'Bank Name': item.che_bank_nm,
              'Bank Account No.': item.che_ba_acct_no,
              'Cheque No.': item.che_no,
              'Payer Name': item.che_payer_nm,
              'Cheque Date': item.che_date,
              'Amount (RM)': (item.che_amt ?? 0).toFixed(2)
            })) : [{ 
              'Collection Slip No': '',
              'Order Reference No.': '',
              'Bank Name': '',
              'Bank Account No.': '',
              'Cheque No.': '',
              'Payer Name': '',
              'Cheque Date': '',
              'Amount (RM)': ''
          }]
        },
        {
          title: 'Bank Draft', 
          data: this.filterByBD.length > 0 ? this.filterByBD.map(item => 
            ({
              'Collection Slip No': item.col_slip_no,
              'Order Reference No.': item.orn_no,
              'Bank Name': item.bd_bank_nm,
              'Bank Drat No..': item.bd_no,
              'Bank Draft Date': item.bd_date,
              'Amount (RM)': (item.bd_amt ?? 0).toFixed(2)
            })) : [{ 
              'Collection Slip No': '',
              'Order Reference No.': '',
              'Bank Name': '',
              'Bank Draft No.': '',
              'Bank Draft Date': '',
              'Amount (RM)': ''
          }]
        },
        {
          title: 'Money Order', 
          data: this.filterByMO.length > 0 ? this.filterByMO.map(item => 
            ({
              'Collection Slip No': item.col_slip_no,
              'Order Reference No.': item.orn_no,
              'Remit No.': item.mo_rm_no,
              'Money Order Date': item.mo_date,
              'Payer Name': item.mo_payer_nm,
              'ID No.': item.mo_id_no,
              'Contact No.': item.mo_contact_no,
              'Amount (RM)': (item.mo_amt ?? 0).toFixed(2)
            })) : [{ 
              'Collection Slip No': '',
              'Order Reference No.': '',
              'Remit No.': '',
              'Money Order Date': '',
              'Payer Name': '',
              'ID No.': '',
              'Contact No.': '',
              'Amount (RM)': ''
          }]
        },
        {
          title: 'RMS Total VS Counter Total', 
          data: this.modelCtrBalInfo.length > 0 ? [ 
            {
                '': 'RMS Collection', // Second row: RMS Collection
                'EMV Amount (RM)': (typeof this.modelCtrBalInfo[0].total_emv === 'number' ? this.modelCtrBalInfo[0].total_emv : 0).toFixed(2),
                'Cash Amount (RM)': (typeof this.modelCtrBalInfo[0].total_col === 'number' ? this.modelCtrBalInfo[0].total_col : 0).toFixed(2),
                'Cheque Amount (RM)': (typeof this.modelCtrBalInfo[0].total_che === 'number' ? this.modelCtrBalInfo[0].total_che : 0).toFixed(2),
                'Bank Draft Amount (RM)': (typeof this.modelCtrBalInfo[0].total_bd === 'number' ? this.modelCtrBalInfo[0].total_bd : 0).toFixed(2),
                'Money Order Amount (RM)': (typeof this.modelCtrBalInfo[0].total_mo === 'number' ? this.modelCtrBalInfo[0].total_mo : 0).toFixed(2),
                'Total Collected Amount (RM)': (typeof this.modelCtrBalInfo[0].total === 'number' ? this.modelCtrBalInfo[0].total : 0).toFixed(2)
            },
            {
                '': 'Counter Collection', // Third row: Counter Collection
                'EMV Amount (RM)': (typeof this.totalEMVAmount === 'number' ? this.totalEMVAmount : 0).toFixed(2),
                'Cash Amount (RM)': (typeof this.modelCtrBalInfo[0].total_col === 'number' ? this.modelCtrBalInfo[0].total_col : 0).toFixed(2),
                'Cheque Amount (RM)': (typeof this.totalChequeAmount === 'number' ? this.totalChequeAmount : 0).toFixed(2),
                'Bank Draft Amount (RM)': (typeof this.totalBDAmount === 'number' ? this.totalBDAmount : 0).toFixed(2),
                'Money Order Amount (RM)': (typeof this.totalMOAmount === 'number' ? this.totalMOAmount : 0).toFixed(2),
                'Total Collected Amount (RM)': (typeof this.grandTotal === 'number' ? this.grandTotal : 0).toFixed(2)
            }
        ]:[
            {
              '': 'RMS Collection',
              'EMV Amount (RM)': '',
              'Cash Amount (RM)': '',
              'Cheque Amount (RM)': '',
              'Bank Draft Amount (RM)': '',
              'Money Order Amount (RM)': '',
              'Total Collected Amount (RM)': ''
          },
          {
              '': 'Counter Collection',
              'EMV Amount (RM)': '',
              'Cash Amount (RM)': '',
              'Cheque Amount (RM)': '',
              'Bank Draft Amount (RM)': '',
              'Money Order Amount (RM)': '',
              'Total Collected Amount (RM)': ''
          }
        ]
        }
    ];

    dataToExport.forEach((section, index) => {
        if (!isFirstPage) {
            doc.addPage('a4', 'landscape'); // Create a new page for each section
        }
        isFirstPage = false;

        // Add title
        doc.setFontSize(14);
        doc.text(section.title, 15, 20);

        // Convert data to table format
        const tableData = section.data.map(row => Object.values(row));
        const tableHeaders = [Object.keys(section.data[0])];

        // Add autoTable
        (doc as any).autoTable({
            head: tableHeaders,
            body: tableData,
            startY: 30, // Position after title
            theme: "grid",
            styles: { fontSize: 10 ,
                      lineWidth: 0.1, // Increase thickness
                      lineColor: [0, 0, 0] // Black color for borders
            },
            headStyles: { fillColor: [175,175,175], textColor: [0, 0, 0] }, // Header styling
        });
    });

    // Get current date in 'yyyyMMDD' format
    const currentDate = new Date();
    const yyyy = currentDate.getFullYear();
    const MM = ('0' + (currentDate.getMonth() + 1)).slice(-2);
    const DD = ('0' + currentDate.getDate()).slice(-2);
    const dates = yyyy + MM + DD;

    // Generate filename
    const branchCode = this.modelCtrBalInfo[0]?.branch_cd ?? "Unknown";
    const counterId = this.modelCtrBalInfo[0]?.counter_id ?? "Unknown";
    const filename = `CB_${counterId}_${branchCode}_${dates}.pdf`;

    // Save the PDF
    doc.save(filename);
}

  back(): void{
    console.log("back()");
    this.location.back();
  }

}
