import { ChangeDetectorRef, Component, HostListener, NgZone,OnInit } from '@angular/core';
import { environment } from 'src/environments/environment';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { NgbPaginationConfig } from '@ng-bootstrap/ng-bootstrap';
import { NavigationStart, Router } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import { ParamService } from '../../core/services/param.service';
import { TranslateService } from '@ngx-translate/core';
import { GlobalService } from 'src/app/shared/global.service';
import { AuthService } from 'src/app/core/services/auth.service';
import { Location, DatePipe } from '@angular/common';
import { OTCEMVInfo } from 'src/app/core/models/otc-ctr-bal-emv';
import { OTCPHYInfo } from 'src/app/core/models/otc-ctr-bal-phy';
import { CounterBalancingEditComponent } from '../counter-balancing-edit/counter-balancing-edit.component';
import { OTCBalInfo } from 'src/app/core/models/otc-bal-info';
import { OTCBalRC } from 'src/app/core/models/otc-bal-rc';
import { OTCBalCash } from 'src/app/core/models/otc-bal-cash';
import { OTCDailyBal } from 'src/app/core/models/otc-daily-bal';
import { utils, writeFile, WorkSheet } from 'xlsx';
import { OTCBalSettlementEMV } from 'src/app/core/models/otc-bal-settlement-emv';
import { OTCCollectionReceipting } from 'src/app/core/models/otc-collection-receipting.interface';
import { perm } from 'src/permissions/perm';

import { jsPDF } from "jspdf";
import "jspdf-autotable";

@Component({
  selector: 'app-daily-balancing-detail',
  templateUrl: './daily-balancing-detail.component.html',
  styleUrls: ['./daily-balancing-detail.component.scss']
})
export class DailyBalancingDetailComponent implements OnInit{

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
    private datePipe: DatePipe,
    private zone: NgZone,
  ){
    let previousDBUrl = this.router.url;
    this.router.events.subscribe(event => {
      if (event instanceof NavigationStart) {
          const newUrl = event.url;

          if(history.state.flag == false){
            // Skip auto update if navigation was triggered by submit
            if (this.isSubmitNavigation) {
              this.isSubmitNavigation = false; // Reset it
              previousDBUrl = newUrl;
              return;
            }

            // Call autoUpdStatus only if navigating away from detail page (non-submit)
            if (previousDBUrl.includes('/daily-balancing-detail') && !newUrl.includes('/daily-balancing-detail')) {
              this.autoUpdStatus();
            }

            previousDBUrl = newUrl;
          }
        }
    });
  }

  private isSubmitNavigation = false;

  //Permissions
  permOTCDBal = perm.OTC_DAILY_BALANCING_View_Detail;
  permOTCDBalAllow = "";
  permOTCDBalDetailAllow = 0;

  balDate: Date = new Date();
  branch_code: String = '';
  flag: boolean = false;

  modelBalInfo: OTCBalInfo[] = [];
  modelBalRC: OTCBalRC[] = [];
  modelBalEMV: OTCEMVInfo[] = [];
  modelBalCash: OTCBalCash[] = [];
  modelBalPhy: OTCPHYInfo[] = [];
  modelList: OTCDailyBal[] = [];
  modelBalSettlementEMV: OTCBalSettlementEMV[] = [];
  modelOTCCR: OTCCollectionReceipting[] = [];

  filterByEMV: OTCPHYInfo[] = [];
  filterByCheque: OTCPHYInfo[] = [];
  filterByMO: OTCPHYInfo[] = [];
  filterByBD: OTCPHYInfo[] = [];

  moneyMap = new Map<string, any>();   // Optimized data structure
  emvMap = new Map<string, any>();  // Optimized data structure

  page = environment.DefaultPage;
  itemsPerPage = environment.ItemPerPage;
  isDisplay: boolean = false;
  isLoading: boolean = true;
  showResultAlert = false;

  isEmptyResult = false;
  isEmptyResultRC = false;
  isEmptyResultEMV = false;
  isEmptyResultEMVS = false;
  isEmptyResultChe = false;
  isEmptyResultBD = false;
  isEmptyResultMO = false;
  isEmptyResultCash = false;

  totalRecordsEMV: number = 0;
  grandTotal = 0;
  grandCashTotal: number = 0;
  totalEMVAmount: number = 0;
  totalEMVSAmount: number = 0;
  totalChequeAmount: number = 0;
  totalBDAmount: number = 0;
  totalMOAmount: number = 0;
  totalEMVSettlementAmount: number = 0;
  changePaymentModeBox: boolean = false;
  hasRCQuantity: boolean = false;

  file_nm: string | null = null;
  file_content: string | null = null;

  //default pagination
  selectedValue = environment.dropdownOptions[0];
  dropdownOptions = environment.dropdownOptions;

  fileRows:{
      file: File | null; 
      t_id: string; 
      settlement_dt: Date | null; 
      batch_no: number | 0; 
      batch_cnt: number | 0; 
      batch_amt:number | 0;
      isLocked: boolean | false;
    }[] = [];

  totalFileRows: {
    t_id: string; 
    totalAmt: number 
  }[] = [];

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

  async ngOnInit(): Promise<void>{
    this.isLoading = true;
    this.loadPermission();
    
    //load data must be place at last
    if(history.state.bal_date != null && history.state.branch_code != null){
      this.balDate = history.state.bal_date;
      this.branch_code = history.state.branch_code;

      if(history.state.flag != null){
        this.flag = history.state.flag;
      }
  
      await Promise.all([
        this.loadBalInfo(),
        this.loadBalRC(),
        this.flag ? this.loadBalSettlementEMV() : Promise.resolve(),
        this.loadBalCash(),
        this.loadBalEMV(),
      ]);

      await this.loadBalPhy();
    }
  }

  loadPermission(){
    this.authService.checkUserRole(this.authService.username, this.permOTCDBal)
    .subscribe(
      (response: any) => {
        this.permOTCDBalAllow = response.data;
        this.permOTCDBalDetailAllow = this.permOTCDBalAllow.includes(perm.OTC_DAILY_BALANCING_View_Detail) ? 1 : 0;
        if(this.permOTCDBalDetailAllow === 0){
          this.router.navigate(['/access-denied']);
          return;
        }
      }
    )
  }

  DefaultBox() {
    this.changePaymentModeBox = false;
  }

  showResultAlertBox() {
    this.showResultAlert = true;
    setTimeout(() => (this.showResultAlert = false), 2000);
  }

  // Function to update the second table data
  updateSecondTable() {
    const groupedData: { [key: string]: number } = {};

    this.fileRows.forEach(row => {
      if (groupedData[row.t_id]) {
        groupedData[row.t_id] += row.batch_amt;
      } else {
        groupedData[row.t_id] = row.batch_amt;
      }
    });

    // Convert grouped data to an array for the second table
    this.totalFileRows = Object.keys(groupedData).map(t_id => ({
      t_id,
      totalAmt: groupedData[t_id]
    }));
  }

  calculateTotal() {
    this.grandCashTotal = this.moneys.reduce((sum, money) => sum + money.price * money.quantity, 0);
    this.calculateGrandTotal();
  }

  // Function to calculate the grand total of totalAmt in the second table
  calculateGrandTotal(){
    if(this.flag === false){
      this.grandTotal = this.totalEMVSettlementAmount + this.grandCashTotal + this.totalChequeAmount + this.totalBDAmount + this.totalMOAmount;
    }
    else{
      this.grandTotal = this.totalEMVSAmount + this.grandCashTotal + this.totalChequeAmount + this.totalBDAmount + this.totalMOAmount;
    }
}

  // Function to calculate the grand total of totalAmt in the second table
  calculateFileGrandTotal(): number {
    let total = 0;
    if(this.emvMap.size > 0){
      this.emvMap.forEach(value => {
        total += value.total_cash || 0;
      });

      this.totalEMVSettlementAmount = total;
      this.calculateGrandTotal();
    }

    return total;
    //return this.totalFileRows.reduce((sum, row) => sum + row.totalAmt, 0);
  }

  addRow() {
    // Add a new row with default values
    this.fileRows.push({
      t_id: '',
      settlement_dt: this.balDate,
      batch_no: 0,
      batch_cnt: 0,
      batch_amt: 0,
      file: null,
      isLocked: false,
    });
  }

  onFileSelected(event: Event, index: number) {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.fileRows[index].file = input.files[0];
    }
  }

  preventInvalidInput(event: KeyboardEvent): void {
    const invalidChars = ['-', '.', 'e', 'E'];

    if (invalidChars.includes(event.key)) {
        event.preventDefault();
    }
  }

  // Added in 11 Jun 2025
  isFileRowInvalid(row: any): boolean {
    return (
      !row.file ||                       // File must be selected
      !row.t_id ||                       // Terminal ID must be selected
      !row.settlement_dt ||             // Settlement date must be selected
      !row.batch_no || row.batch_no <= 0 || // Batch number must be > 0
      !row.batch_cnt || row.batch_cnt <= 0 || // Batch count must be > 0
      !row.batch_amt || row.batch_amt <= 0    // Batch amount must be > 0
    );
  }

  isFileRowValid(): boolean {
    const total_emv = this.modelBalInfo?.[0]?.total_emv;

    // If total_emv is 0, null, or undefined → return true (skip validation)
    if (total_emv == null || total_emv === 0) {
      return true;
    }

    // Otherwise, perform full checks
    const hasRows = this.fileRows.length > 0;
    const allValid = this.fileRows.every(row => !this.isFileRowInvalid(row));

    return hasRows && allValid;
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
        await this.loadBalInfo();
        await this.loadBalEMV();
        await this.loadBalPhy();
      }
    });

    this.changePaymentModeBox = true;
  }

  LoadEMVData(singleItem: number) {
    this.selectedValue = singleItem;
    this.itemsPerPage = this.selectedValue;
    this.loadBalEMV();
  }

  async loadBalInfo(): Promise<void> {
    return new Promise((resolve, reject) => {
      // Set your authorization header
      const headers = new HttpHeaders({
        Authorization: environment.authKey,
        'Content-Type': 'application/json',
      });

      this.isDisplay = true;
      this.isLoading = true;
      const url = environment.apiUrl + '/api/otcbal/v1/getotcdetails';

      const Body: any = {
        branch_code: this.branch_code,
        bal_date: this.balDate,
      };

      this.http.post(url, Body, { headers }).subscribe(
        (response: any) => {
          this.modelBalInfo = response.data;
          if (response.data.length == 0) {
            this.isDisplay = true;
            this.showResultAlertBox();
            this.isLoading = false;
          } else {
            this.isLoading = false;
            this.isDisplay = false;
            if(this.modelBalInfo[0].total_bd == undefined){
              this.modelBalInfo[0].total_bd = 0;
            }
            if(this.modelBalInfo[0].total_cash == undefined){
              this.modelBalInfo[0].total_cash = 0;
            }
            if(this.modelBalInfo[0].total_che == undefined){
              this.modelBalInfo[0].total_che = 0;
            }
            if(this.modelBalInfo[0].total_mo == undefined){
              this.modelBalInfo[0].total_mo = 0;
            }
            if(this.modelBalInfo[0].total_emv == undefined){
              this.modelBalInfo[0].total_emv = 0;
            }
            if(this.modelBalInfo[0].total_phy == undefined){
              this.modelBalInfo[0].total_phy = 0;
            }
          }

          // Resolve the Promise when the operation is successful
          resolve(); 
        },
        (error) => {
          console.error(error);
          this.isLoading = false;
          // Handle errors here
          // Reject the Promise in case of an error
          reject(error); 
        }
      );
    });
  }

  async loadBalRC(): Promise<void>{
    return new Promise((resolve, reject) => {
      // Set your authorization header
      const headers = new HttpHeaders({
        Authorization: environment.authKey,
        'Content-Type': 'application/json',
      });

      this.isDisplay = true;
      this.isLoading = true;
      const url = environment.apiUrl + '/api/otcbal/v1/getotcrc';

      const Body: any = {
        branch_code: this.branch_code,
        bal_date: this.balDate,
      };

      this.http.post(url, Body, { headers }).subscribe(
        (response: any) => {
          this.modelBalRC = response.data;
          if (response.data.length == 0) {
            this.isDisplay = true;
            this.isEmptyResultRC = true;
            this.showResultAlertBox();
            this.isLoading = false;
          } else {
            this.isEmptyResultRC = false;
            this.isLoading = false;
          }

          // Resolve the Promise when the operation is successful
          resolve(); 
        },
        (error) => {
          console.error(error);
          this.isLoading = false;
          // Handle errors here
          // Reject the Promise in case of an error
          reject(error); 
        }
      );
    });
  }

  async loadBalEMV(): Promise<void>{
    return new Promise((resolve, reject) => {
      // Set your authorization header
      const headers = new HttpHeaders({
        Authorization: environment.authKey,
        'Content-Type': 'application/json',
      });

      this.isDisplay = true;
      this.isLoading = true;
      const url = environment.apiUrl + '/api/otcbal/v1/getotcemvcol';

      const Body: any = {
        branch_code: this.branch_code,
        bal_date: this.balDate,
        i_page: 1,
        i_size: 10,
      };

      this.http.post(url, Body, { headers }).subscribe(
        (response: any) => {
          this.modelBalEMV = response.data;
          if (response.data.length == 0) {
            this.isDisplay = true;
            this.isEmptyResultEMV = true;
            this.showResultAlertBox();
            this.isLoading = false;
            this.totalRecordsEMV = 0;
            this.totalEMVAmount = 0;
          } else {
            this.isEmptyResultEMV = false;
            this.isLoading = false;

            this.totalRecordsEMV = this.modelBalEMV[0].total;
            this.totalEMVAmount = this.modelBalEMV.reduce((sum,emv) => sum + emv.amount,0);
          }

          // Resolve the Promise when the operation is successful
          resolve(); 
        },
        (error) => {
          console.error(error);
          this.isLoading = false;
          // Handle errors here
          // Reject the Promise in case of an error
          reject(error); 
        }
      );
    });
  }  

  async loadBalCash(): Promise<void>{
    return new Promise((resolve, reject) => {
      // Set your authorization header
      const headers = new HttpHeaders({
        Authorization: environment.authKey,
        'Content-Type': 'application/json',
      });

      this.isDisplay = true;
      this.isLoading = true;
      const url = environment.apiUrl + '/api/otcbal/v1/getotccashcol';

      const Body: any = {
        branch_code: this.branch_code,
        bal_date: this.balDate,
      };

      this.http.post(url, Body, { headers }).subscribe(
        (response: any) => {
          this.modelBalCash = response.data;
          if (response.data.length == 0) {
            this.isDisplay = true;
            this.isEmptyResultCash = true;
            this.showResultAlertBox();
            this.isLoading = false;
          } else {
            this.isEmptyResultCash = false;
            this.isLoading = false;

            // Clear and populate the moneyMap for fast lookup
            this.moneyMap.clear();
            this.hasRCQuantity = this.modelBalCash.some(item => item.quantity === 99999);

            if (!this.hasRCQuantity) {
              this.modelBalCash.forEach((item) => {
                this.moneyMap.set(item.param_cd, item);
                const money = this.moneys.find(m => m.param_cd === item.param_cd);
                if (money) {
                  money.quantity = item.quantity || 0;
                }
              });

              this.grandCashTotal = this.moneys.reduce((sum, money) => sum + money.price * money.quantity, 0);
            }
          }

          // Resolve the Promise when the operation is successful
          resolve(); 
        },
        (error) => {
          console.error(error);
          this.isLoading = false;
          // Handle errors here
          // Reject the Promise in case of an error
          reject(error); 
        }
      );
    });
  }

  async loadBalPhy(): Promise<void>{
    return new Promise((resolve, reject) => {
      // Set your authorization header
      const headers = new HttpHeaders({
        Authorization: environment.authKey,
        'Content-Type': 'application/json',
      });

      this.isDisplay = true;
      this.isLoading = true;
      const url = environment.apiUrl + '/api/otcbal/v1/getotcphyinfo';

      const Body: any = {
        branch_code: this.branch_code,
        bal_date: this.balDate,
      };

      this.http.post(url, Body, { headers }).subscribe(
        (response: any) => {
          this.zone.run(() => {
            this.modelBalPhy = response.data;
            if (response.data.length == 0) {
              this.isDisplay = true;
              this.showResultAlertBox();
              this.isLoading = false;
              this.isEmptyResultChe = true;
              this.isEmptyResultBD = true;
              this.isEmptyResultMO = true;
              this.isEmptyResultEMV = true;
              this.calculateGrandTotal();
            } else {
              this.isLoading = false;

              this.filterByCheque = this.modelBalPhy.filter(item=> item.detail_type.trim() === 'cheque');
              this.filterByBD = this.modelBalPhy.filter(item=> item.detail_type.trim() === 'bank draft');
              this.filterByMO = this.modelBalPhy.filter(item=> item.detail_type.trim() === 'money order');
              this.filterByEMV = this.modelBalPhy.filter(item=> item.detail_type.trim() === 'emv');

              if(this.filterByCheque.length == 0){
                this.isEmptyResultChe = true;
              }
              
              if(this.filterByBD.length == 0){
                this.isEmptyResultBD = true;
              }

              if(this.filterByMO.length == 0){
                this.isEmptyResultMO = true;
              }

              if(this.filterByEMV.length == 0){
                this.isEmptyResult = true;
                this.isEmptyResultEMV = true;
              }
              else{
                if(this.flag === true){
                  // Step 1: Define the type for groupedAmounts
                  const groupedAmounts: Record<string, number> = this.modelBalSettlementEMV.reduce((acc: Record<string, number>, item) => {
                    const terminalId = item.terminalId;
                    const amount = parseFloat(item.batchAmt) || 0; // Ensure amount is a number

                    if (!acc[terminalId]) {
                      acc[terminalId] = 0;
                    }
                    acc[terminalId] += amount;
                    return acc;
                  }, {}); // Initialize accumulator as an empty object

                  // Step 2: Map grouped amounts to filterByEMV
                  this.filterByEMV = this.filterByEMV.map((item) => {
                    const terminalId = item.col_slip_no;
                    const totalFromEMV = groupedAmounts[terminalId] || 0;

                    return {
                      ...item,
                      totalFromEMV, // Add total amount from modelBalSettlementEMV
                    };
                  });
                }
              }

              this.totalChequeAmount = this.filterByCheque.reduce((sum,che) => sum + che.che_amt,0);     
              this.totalBDAmount = this.filterByBD.reduce((sum,bd) => sum + bd.bd_amt,0);  
              this.totalMOAmount = this.filterByMO.reduce((sum,mo) => sum + mo.mo_amt,0);

              // Use setTimeout to delay calling calculateGrandTotal, if necessary
              setTimeout(() => {
                this.calculateGrandTotal();
              }, 0);
            }

            // Resolve the Promise when the operation is successful
            resolve();
          })
        },
        (error) => {
          console.error(error);
          this.isLoading = false;
          // Handle errors here
          // Reject the Promise in case of an error
          reject(error); 
        }
      );
    });
  }

  async loadBalSettlementEMV(): Promise<void>{
    return new Promise((resolve, reject) => {
      // Set your authorization header
      const headers = new HttpHeaders({
        Authorization: environment.authKey,
        'Content-Type': 'application/json',
      });

      this.isDisplay = true;
      this.isLoading = true;
      const url = environment.apiUrl + '/api/otcbal/v1/getotcbaldoclist';

      const Body: any = {
        branch_code: this.branch_code,
        bal_date: this.balDate,
      };

      this.http.post(url, Body, { headers }).subscribe(
        (response: any) => {
          this.modelBalSettlementEMV = response.data;
          if (response.data.length == 0) {
            this.isDisplay = true;
            this.isEmptyResultEMVS = true;
            this.showResultAlertBox();
            this.isLoading = false;
          } else {
            this.isEmptyResultEMVS = false;
            this.isLoading = false;
            this.totalEMVSAmount = this.modelBalSettlementEMV[0].total;
          }

          // Resolve the Promise when the operation is successful
          resolve(); 
        },
        (error) => {
          console.error(error);
          this.isLoading = false;
          // Handle errors here
          // Reject the Promise in case of an error
          reject(error); 
        }
      );    
    });
  }

  isDataMatching(): boolean {
    if(!this.modelBalInfo?.length) return false;
    return (
      Number((this.modelBalInfo[0]?.total_emv ?? 0).toFixed(2)) === Number(this.totalEMVSettlementAmount?.toFixed(2)) &&
      Number((this.modelBalInfo[0]?.total_cash?? 0).toFixed(2)) === Number(this.grandCashTotal?.toFixed(2)) &&
      Number((this.modelBalInfo[0]?.total_che?? 0).toFixed(2)) === Number(this.totalChequeAmount?.toFixed(2)) &&
      Number((this.modelBalInfo[0]?.total_bd?? 0).toFixed(2)) === Number(this.totalBDAmount?.toFixed(2)) &&
      Number((this.modelBalInfo[0]?.total_mo?? 0).toFixed(2)) === Number(this.totalMOAmount?.toFixed(2)) &&
      Number((this.modelBalInfo[0]?.total?? 0).toFixed(2)) === Number(this.grandTotal?.toFixed(2)) &&
      this.flag == false
    );
}

  deleteRow(index: number, row: any) {
    const tIdKey = row.t_id.toString().toUpperCase();

    // Remove the row from the fileRows array
    this.fileRows.splice(index, 1);

    if (tIdKey) {
      // Recalculate totals for the affected t_id
      const total = this.fileRows
        .filter(r => r.t_id?.toString().toUpperCase() === tIdKey)
        .reduce((sum, r) => sum + (r.batch_amt || 0), 0);

      if (total > 0) {
        this.emvMap.set(tIdKey, { total_cash: total });
      } else {
        this.emvMap.set(tIdKey, { total_cash: 0 }); // or delete, based on your preference
      }

      // Force change detection
      this.emvMap = new Map(this.emvMap);
    }
  }

  // When the user enters data, update the map for fast access
  onEMVSettlementChange(row: any) {
    const tId = row.t_id?.toString().toUpperCase(); // Normalize t_id to uppercase

    if (!tId) return; // Skip if t_id is not defined
  
    // Calculate the total cash for the current t_id
    const total = this.fileRows
      .filter(r => r.t_id?.toString().toUpperCase() === tId) // Match rows with the same t_id
      .reduce((sum, r) => sum + (r.batch_amt || 0), 0); // Sum up batch_amt
  
    // Update the emvMap with the new total
    this.emvMap.set(tId, { total_cash: total });
  }

  onDropdownChange(row: any): void {
    if (!row.isLocked) {
      row.isLocked = true; // Lock the dropdown after the first selection
    }
  }

  mappingDomination(param_cd: string): string {
    return this.moneyMap.get(param_cd)?.quantity || 'N/A';
  }

  viewRC(item: any): void{
    this.router.navigate(['/otc-rcpt-dets'], 
      { state: 
        {
          mtt_id: item.mtt_id,
          otc_id: item.otc_id,
          otc_counter_id: item.otc_counter_id,
          counter_id: item.counter_id,
          otc_pymt_mode: item.otc_pymt_mode,
      }
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
            queryParams: { orn_no: item.orn_no, curr_page: "daily-balancing-detail" },
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

  uploadEMVDOC(){
    this.fileRows.forEach((row, index) => {
      const formData = new FormData();
    
      // Append the file if available
      if (row.file) {
        const reader = new FileReader();
        reader.onload = () => {
          const base64String = (reader.result as string).split(',')[1];
          formData.append('file_content', base64String);
          formData.append('fileNm', row.file ? row.file.name : 'null');
          formData.append('fileType', row.file ? row.file.type : 'txt');
          formData.append('fileSize', row.file ? row.file.size.toString() : '0');

          // Append other non-file fields
          formData.append('terminal_id', row.t_id);
          formData.append('settlement_dt', row.settlement_dt ? row.settlement_dt.toString() : Date.toString());
          formData.append('batch_no', row.batch_no.toString());
          formData.append('batch_cnt', row.batch_cnt.toString());
          formData.append('batch_amt', row.batch_amt.toString());

          // Pass the entire formData object to the insEMVDoc method
          this.insEMVDoc(formData);
        };
        reader.readAsDataURL(row.file); // Asynchronous file reading
      }     
    })
  }

  insEMVDoc(formData: FormData){
    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    this.isDisplay = true;
    this.isLoading = true;
    const url = environment.apiUrl + '/api/otcbal/v1/insotcbaldoc';
    const Body: any = {
      branch_code: this.branch_code,
      bal_date: this.balDate,
      fileNm: formData.get('fileNm') as string,
      fileType: formData.get('fileType') as string,
      fileSize: formData.get('fileSize') as string,
      fileContent: formData.get('file_content') as string,
      fileCategory: 'S',
      dtSettlement: formData.get('settlement_dt') as string,
      terminalId: (formData.get('terminal_id') as string)?.toUpperCase(),
      batchNo: formData.get('batch_no') as string,
      batchCount: formData.get('batch_cnt') as string,
      batchAmt: formData.get('batch_amt') as string,
    };

    this.http.post(url, Body, { headers }).subscribe(
      (response: any) => {
        this.modelBalCash = response.data;
        if (response.data.length == 0) {
          this.isDisplay = true;
          this.showResultAlertBox();
          this.isLoading = false;
        } else {
          this.isLoading = false;

          // Clear and populate the moneyMap for fast lookup
          this.moneyMap.clear();
          this.modelBalCash.forEach((item) => {
            this.moneyMap.set(item.param_cd, item);
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

  insCash(){
    // Add branch_code and bal_date to each item in this.moneys
    this.moneys = this.moneys.map(item => ({
      ...item,  // Spread the existing properties of the item
      branch_code: this.branch_code,  // Add the branch_code
      bal_date: this.balDate  // Add the bal_date
    }));

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    this.isDisplay = true;
    this.isLoading = true;
    const url = environment.apiUrl + '/api/otcbal/v1/insotcdbalcashbytotal';

    this.http.post(url, this.moneys, { headers }).subscribe(
      (response: any) => {
        this.modelBalCash = response.data;
        if (response.data.length == 0) {
          this.isDisplay = true;
          this.showResultAlertBox();
          this.isLoading = false;
        } else {
          this.isLoading = false;

          // Clear and populate the moneyMap for fast lookup
          this.moneyMap.clear();
          this.modelBalCash.forEach((item) => {
            this.moneyMap.set(item.param_cd, item);
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

  updStatus(): void{
    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });
  
    this.isSubmitNavigation = true;
    this.isDisplay = true;
    this.isLoading = true;
    const url = environment.apiUrl + '/api/otcdailybal/v1/updotcdailybalstatus';
  
    const Body: any = {
      branch_code: this.branch_code,
      bal_date: this.balDate,
      bal_status: 'C',
      bal_type: 'D',
    };
  
    this.http.post(url, Body, { headers }).subscribe(
      (response: any) => {
        this.modelList = response.data;
        if (response.data.length == 0) {
          this.isDisplay = true;
          this.showResultAlertBox();
          this.isLoading = false;
        } else {
          this.isLoading = false;
          this.router.navigate(['/daily-balancing-listing'], 
            { state: 
              {
              success: true,
              balancingDate: this.balDate,
              branch_code: this.branch_code
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

  downloadFile(item: any): void {
    const url = environment.apiUrl + '/api/otcbal/v1/getotcbaldoc';
 
    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });
 
    const Body: any = {
      docID: item.docID,
    };
 
    this.http.post(url, Body, { headers }).subscribe(
      (response: any) => {
        this.file_content = response.data;

        if(this.file_content != null)
        {
          this.downloadFileContent(item.fileNm, this.file_content);
        }

        if (response.data.length == 0) {
          this.totalEMVSAmount = 0;
          //this.showResultAlertBox();
          this.isLoading = false;
        } else {
          this.totalEMVSAmount = response.data[0].total;
          this.DefaultBox();
          this.isLoading = false;
          this.isDisplay = true;
        }
      },
      (error) => {
        console.error(error);
        this.isLoading = false;
      }
    );
  }

  downloadFileContent(item: any, item2: any): void{
    this.isLoading = true;
    const binaryString = window.atob(item2);
    const len = binaryString.length;
    const uint8Array = new Uint8Array(len);
 
    for (let i = 0; i < len; i++) {
      uint8Array[i] = binaryString.charCodeAt(i);
    }
 
    const blob = new Blob([uint8Array], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' });
 
    const url = URL.createObjectURL(blob);
 
    const anchor = document.createElement('a');
    anchor.href = url;
    anchor.download = item;
 
    document.body.appendChild(anchor);
    anchor.click();
 
    document.body.removeChild(anchor);
    URL.revokeObjectURL(url);
  }

  submit(): void{
    const total_emv = this.modelBalInfo?.[0]?.total_emv;

    if (total_emv != null && total_emv > 0) {
      this.uploadEMVDOC();
    }
    
    if(this.modelBalInfo[0].total_cash != 0){
      this.insCash();
    }

    this.updStatus();
  }

  cancel(): void{
    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    this.isDisplay = true;
    this.isLoading = true;
    const url = environment.apiUrl + '/api/otcdailybal/v1/updotcdailybalstatus';

    const Body: any = {
      branch_code: this.branch_code,
      bal_date: this.balDate,
      bal_status: 'P',
      bal_type: 'D',
    };

    this.http.post(url, Body, { headers }).subscribe(
      (response: any) => {
        this.modelList = response.data;
        if (response.data.length == 0) {
          this.isDisplay = true;
          this.showResultAlertBox();
          this.isLoading = false;
          this.location.back();
        } else {
          this.isLoading = false;
          this.location.back();
        }
      },
      (error) => {
        console.error(error);
        this.isLoading = false;
        // Handle errors here
      }
    );
  }

  autoUpdStatus(): void{
    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    this.isDisplay = true;
    this.isLoading = true;
    const url = environment.apiUrl + '/api/otcdailybal/v1/updotcdailybalstatus';

    const Body: any = {
      branch_code: this.branch_code,
      bal_date: this.balDate,
      bal_status: 'P',
      bal_type: 'D',
    };

    this.http.post(url, Body, { headers }).subscribe(
      () => {}, // Ignore the response
      () => {}  // Ignore the error
    );
  }

  back(): void{
    this.location.back();
  }

  exportToPDF(): void {
    const doc = new jsPDF({ orientation: 'landscape', unit: 'mm', format: 'a4' });
    let isFirstPage = true; // Track if it's the first page to avoid unnecessary page breaks

  // Get Branch Code and Counter ID from the model
  const branchCode = this.modelBalInfo[0].branch_cd;
  const balDate = this.balDate;

    const dataToExport = [
        { 
        title: 'Counter(s) Summary', 
        data: this.modelBalInfo.length > 0 ? this.modelBalInfo.map(item => 
          ({
            'Branch Code': item.branch_cd,
            'Balancing Date Period': balDate,
            'No. Of Counters': item.no_of_counters,
            'No. of Transactions': item.no_of_txn,
            'No. of Receipts Cancelled': item.no_of_rcpt_can,
            'Total EMV (RM)': item.total_emv.toFixed(2),
            'Total Physical (RM)': item.total_phy.toFixed(2),
            'Total Cash (RM)': item.total_cash.toFixed(2),
            'Total Cheque (RM)': item.total_che.toFixed(2),
            'Total Bank Draft (RM)': item.total_bd.toFixed(2),
            'Total Money Order (RM)': item.total_mo.toFixed(2),
            'Total Amount (RM)': item.total.toFixed(2)
          })) : [{ 
            'Branch Code': '',
            'Balancing Date Period': '',
            'No. Of Counters': '',
            'No. of Transactions': '',
            'No. of Receipts Cancelled': '',
            'Total EMV (RM)': '',
            'Total Physical (RM)': '',
            'Total Cash (RM)': '',
            'Total Cheque (RM)': '',
            'Total Bank Draft (RM)': '',
            'Total Money Order (RM)': '',
            'Total Amount (RM)': ''
        }]},
        {
          title: 'Receipt Cancellation', 
          data: this.modelBalRC.length > 0 ? this.modelBalRC.map(item => 
            ({
              'Collection Slip No': item.coll_slip_no,
              'Order Reference No.': item.orn_no,
              'Receipt No.': item.rcpt_no,
              'Payment Mode': item.otc_pymt_mode,
              'Requested By': item.requested_by,
              'Approved By': item.approved_by,
              'Receipt Cancellation Reason': item.remark,
              'Total Amount (RM)': item.totalAmount.toFixed(2)
            })) : [{ 
              'Collection Slip No': '',
              'Order Reference No.': '',
              'Receipt No.': '',
              'Payment Mode': '',
              'Requested By': '',
              'Approved By': '',
              'Receipt Cancellation Reason': '',
              'Total Amount (RM)': ''
          }]
        },
        {
          title: 'EMV Transactions', 
          data: this.modelBalEMV.length > 0 ? this.modelBalEMV.map(item => 
            ({
              'Collection Slip No': item.col_slip_no,
              'Order Reference No.': item.orn_no,
              'Transaction Trace': item.trans_trace,
              'Batch Number': item.batch_no,
              'Host Number': item.host_no,
              'Terminal ID': item.t_id,
              'Total Amount (RM)': item.amount.toFixed(2)
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
          title: 'EMV Settlement Info', 
          data: this.modelBalSettlementEMV.length > 0 ? this.modelBalSettlementEMV.map(item => 
            ({
              'File Name': item.fileNm,
              'Terminal ID': item.terminalId,
              'Settlement Date Time': item.dtSettlement,
              'Batch Number': item.batchNo,
              'Batch Count': item.batchCount,
              'Batch Amount (RM)': (parseFloat(item.batchAmt) || 0).toFixed(2)
            })) : [{ 
              'File Name': '',
              'Terminal ID': '',
              'Settlement Date Time': '',
              'Batch Number': '',
              'Batch Count': '',
              'Batch Amount (RM)': ''
          }]
        },
        {
          title: 'EMV Txn VS EMV Settlement', 
          data: this.filterByEMV.length > 0 ? this.filterByEMV.map(item => 
            ({
              'Terminal ID': item.col_slip_no,
              'EMV Txn Total (RM)': item.total_cash_amt.toFixed(2),
              'EMV Settlement Total (RM)': item.totalFromEMV.toFixed(2)
            })) : [{ 
              'Terminal ID': '',
              'EMV Txn Total (RM)': '',
              'EMV Settlement Total (RM)': ''
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
              'Amount (RM)': item.che_amt.toFixed(2)
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
              'Amount (RM)': item.bd_amt.toFixed(2)
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
              'Amount (RM)': item.mo_amt.toFixed(2)
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
          data: this.modelBalInfo.length > 0 ? [ 
            {
                '': 'RMS Collection', // Second row: RMS Collection
                'EMV Amount (RM)': this.modelBalInfo[0].total_emv.toFixed(2),
                'Cash Amount (RM)': this.modelBalInfo[0].total_cash.toFixed(2),
                'Cheque Amount (RM)': this.modelBalInfo[0].total_che.toFixed(2),
                'Bank Draft Amount (RM)': this.modelBalInfo[0].total_bd.toFixed(2),
                'Money Order Amount (RM)': this.modelBalInfo[0].total_mo.toFixed(2),
                'Total Collected Amount (RM)': this.modelBalInfo[0].total.toFixed(2)
            },
            {
                '': 'Counter Collection', // Third row: Counter Collection
                'EMV Amount (RM)': this.totalEMVAmount.toFixed(2),
                'Cash Amount (RM)': this.grandCashTotal.toFixed(2),
                'Cheque Amount (RM)': this.totalChequeAmount.toFixed(2),
                'Bank Draft Amount (RM)': this.totalBDAmount.toFixed(2),
                'Money Order Amount (RM)': this.totalMOAmount.toFixed(2),
                'Total Collected Amount (RM)': this.grandTotal.toFixed(2)
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
    let formattedDate = this.datePipe.transform(this.balDate, 'yyyyMMMdd');
    const filename = `DB_${formattedDate}_${branchCode}_${dates}.pdf`;

    // Save the PDF
    doc.save(filename);
}

}
