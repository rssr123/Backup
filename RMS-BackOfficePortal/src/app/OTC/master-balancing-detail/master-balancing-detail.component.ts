import { ChangeDetectorRef, Component, NgZone, OnInit } from '@angular/core';
import { environment } from 'src/environments/environment';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { NgbPaginationConfig } from '@ng-bootstrap/ng-bootstrap';
import { NavigationStart, ResolveEnd, Router } from '@angular/router';
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
import { OTCBalSettlementEMV } from 'src/app/core/models/otc-bal-settlement-emv';
import { utils, writeFile, WorkSheet } from 'xlsx';
import { OTCCollectionReceipting } from 'src/app/core/models/otc-collection-receipting.interface';

import { jsPDF } from "jspdf";
import "jspdf-autotable";
import { BoundCurves } from 'html2canvas/dist/types/render/bound-curves';

@Component({
  selector: 'app-master-balancing-detail',
  templateUrl: './master-balancing-detail.component.html',
  styleUrls: ['./master-balancing-detail.component.scss']
})
export class MasterBalancingDetailComponent implements OnInit  {

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
    private zone: NgZone
  ){
    let previousMBUrl = this.router.url;
    this.router.events.subscribe(event => {
      if (event instanceof NavigationStart) {
          const newUrl = event.url;

          if(history.state.flag == false){
            // Skip auto update if navigation was triggered by submit
            if (this.isSubmitNavigation) {
              this.isSubmitNavigation = false; // Reset it
              previousMBUrl = newUrl;
              return;
            }

            // Call autoUpdStatus only if navigating away from detail page (non-submit)
            if (previousMBUrl.includes('/master-balancing-detail') && !newUrl.includes('/master-balancing-detail')) {
              this.autoUpdStatus();
            }

            previousMBUrl = newUrl;
          }
        }
    });
  }

  private isSubmitNavigation = false;

  balDate: Date = new Date();
  branch_code: String = '';
  flag: boolean = false;

  modelBalInfo: OTCBalInfo[] = [];
  modelBalRC: OTCBalRC[] = [];
  modelBalEMV: OTCEMVInfo[] = [];
  modelBalSettlementEMV: OTCBalSettlementEMV[] = [];
  modelBalCash: OTCBalCash[] = [];
  modelBalPhy: OTCPHYInfo[] = [];
  modelList: OTCDailyBal[] = [];
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
  isLoading: boolean = false;
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

  async ngOnInit(): Promise<void> {
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
        this.loadBalCash(),
        this.loadBalEMV(),
        this.loadBalSettlementEMV(),
      ]);

      await this.loadBalPhy();
    }
  }

  DefaultBox() {
    this.changePaymentModeBox = false;
  }

  showResultAlertBox() {
    this.showResultAlert = true;
    setTimeout(() => (this.showResultAlert = false), 2000);
  }

  calculateTotal() {
    this.grandCashTotal = this.moneys.reduce((sum, money) => sum + money.price * money.quantity, 0);
    this.calculateGrandTotal();
  }

  // Function to calculate the grand total of totalAmt in the second table
  calculateGrandTotal(){
    this.grandTotal = this.totalEMVSAmount + this.grandCashTotal + this.totalChequeAmount + this.totalBDAmount + this.totalMOAmount;
  }

  preventInvalidInput(event: KeyboardEvent): void {
    const invalidChars = ['-', '.', 'e', 'E'];

    if (invalidChars.includes(event.key)) {
        event.preventDefault();
    }
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

        // Force view to update
        //window.location.reload();
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
      const url = environment.apiUrl + '/api/otcmbal/v1/getotcmdetails';
  
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
          } else {
            if (this.modelBalInfo[0].total_bd == undefined) {
              this.modelBalInfo[0].total_bd = 0;
            }
            if (this.modelBalInfo[0].total_cash == undefined) {
              this.modelBalInfo[0].total_cash = 0;
            }
            if (this.modelBalInfo[0].total_che == undefined) {
              this.modelBalInfo[0].total_che = 0;
            }
            if (this.modelBalInfo[0].total_mo == undefined) {
              this.modelBalInfo[0].total_mo = 0;
            }
            if (this.modelBalInfo[0].total_emv == undefined) {
              this.modelBalInfo[0].total_emv = 0;
            }
            if (this.modelBalInfo[0].total_phy == undefined) {
              this.modelBalInfo[0].total_phy = 0;
            }
          }
  
          resolve(); // Resolve the Promise when the operation is successful
        },
        (error) => {
          console.error(error);
          this.isLoading = false;
          reject(error); // Reject the Promise in case of an error
        }
      );
    });
  }

  async loadBalRC(): Promise<void> {
    return new Promise((resolve, reject) => {
      // Set your authorization header
      const headers = new HttpHeaders({
        Authorization: environment.authKey,
        'Content-Type': 'application/json',
      });
  
      this.isDisplay = true;
      this.isLoading = true;
      const url = environment.apiUrl + '/api/otcmbal/v1/getotcmrc';
  
      const Body: any = {
        branch_code: this.branch_code,
        bal_date: this.balDate,
      };
  
      this.http.post(url, Body, { headers }).subscribe(
        (response: any) => {
          this.modelBalRC = response.data;
  
          if (response.data.length === 0) {
            this.isDisplay = true;
            this.isEmptyResultRC = true;
            this.showResultAlertBox();
          } else {
            this.isEmptyResultRC = false;
          }
  
          this.isLoading = false;
          resolve(); // Resolve the Promise when the operation is successful
        },
        (error) => {
          console.error(error);
          this.isLoading = false;
          reject(error); // Reject the Promise in case of an error
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
    const url = environment.apiUrl + '/api/otcmbal/v1/getotcmemvcol';

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
          this.isEmptyResult = true;
          this.showResultAlertBox();
          this.isLoading = false;
        } else {
          this.isEmptyResult = false;
          this.isLoading = false;

          this.totalRecordsEMV = this.modelBalEMV[0].total;
          this.totalEMVAmount = this.modelBalEMV.reduce((sum,emv) => sum + emv.amount,0);
        }

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
  
  async loadBalSettlementEMV(): Promise<void>{
    return new Promise((resolve, reject) => {
    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    this.isDisplay = true;
    this.isLoading = true;
    const url = environment.apiUrl + '/api/otcmbal/v1/getotcmbaldoclist';

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

  async loadBalCash(): Promise<void>{
    return new Promise((resolve, reject) => {
    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    this.isDisplay = true;
    this.isLoading = true;
    const url = environment.apiUrl + '/api/otcmbal/v1/getotcmcashcol';

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

          this.modelBalCash.forEach((item) => {
            this.moneyMap.set(item.param_cd, item);
            // Initialize the quantity from the moneyMap when the row is created
            const money = this.moneys.find(m => m.param_cd === item.param_cd);
            if (money) {
              money.quantity = item.quantity || 0; // If quantity exists in the item, use it; otherwise set it to 0.
            }
          });
          this.grandCashTotal = this.moneys.reduce((sum, money) => sum + money.price * money.quantity, 0);
        } 

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

  async loadBalPhy(): Promise<void>{
    return new Promise((resolve, reject) => {
    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    this.isDisplay = true;
    this.isLoading = true;
    const url = environment.apiUrl + '/api/otcmbal/v1/getotcmphyinfo';

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
          this.isEmptyResultChe = true;
          this.isEmptyResultBD = true;
          this.isEmptyResultMO = true;
          this.isEmptyResultEMV = true;
          this.showResultAlertBox();
          this.isLoading = false;
          this.calculateGrandTotal();
        } else {
          this.isLoading = false;

          this.filterByCheque = this.modelBalPhy.filter(item=> item.detail_type.trim() === 'cheque');
          this.filterByBD = this.modelBalPhy.filter(item=> item.detail_type.trim() === 'bank draft');
          this.filterByMO = this.modelBalPhy.filter(item=> item.detail_type.trim() === 'money order');
          this.filterByEMV = this.modelBalPhy.filter(item=> item.detail_type.trim() === 'emv');

          this.totalChequeAmount = this.filterByCheque.reduce((sum,che) => sum + che.che_amt,0);     
          this.totalBDAmount = this.filterByBD.reduce((sum,bd) => sum + bd.bd_amt,0);  
          this.totalMOAmount = this.filterByMO.reduce((sum,mo) => sum + mo.mo_amt,0);

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
            this.isEmptyResultEMV = true;
          }
          else if(this.filterByEMV.length > 0){
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

          // Use setTimeout to delay calling calculateGrandTotal, if necessary
          setTimeout(() => {
            this.calculateGrandTotal();
          }, 0);
        }

        resolve();
        })
      },
      (error) => {
        console.error(error);
        this.isLoading = false;
        // Handle errors here
        reject(error);
      }
    );
    });
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
            queryParams: { orn_no: item.orn_no, curr_page: "master-balancing-detail" },
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
        // console.log(response.data);
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

  mappingEMV(terminalID: string): string{
    return this.emvMap.get(terminalID)?.batch_amt ?? 'N/A';;
  }

  isDataMatching(): boolean {
    if(!this.modelBalInfo?.length){
      return false;
    }
    else{
      const totalEMV = Number(this.modelBalInfo[0].total_emv) || 0;
      const totalEMVSAmount = Number(this.totalEMVSAmount) || 0;
      
      const totalCash = Number(this.modelBalInfo[0].total_cash) || 0;
      const grandCashTotal = Number(this.grandCashTotal) || 0;
      
      const totalCheque = Number(this.modelBalInfo[0].total_che) || 0;
      const totalChequeAmount = Number(this.totalChequeAmount) || 0;
      
      const totalBD = Number(this.modelBalInfo[0].total_bd) || 0;
      const totalBDAmount = Number(this.totalBDAmount) || 0;
      
      const totalMO = Number(this.modelBalInfo[0].total_mo) || 0;
      const totalMOAmount = Number(this.totalMOAmount) || 0;
      
      const total = Number(this.modelBalInfo[0].total) || 0;
      const grandTotal = Number(this.grandTotal) || 0;
      
      const isMatch =
        totalEMV.toFixed(2) === totalEMVSAmount.toFixed(2) &&
        totalCash.toFixed(2) === grandCashTotal.toFixed(2) &&
        totalCheque.toFixed(2) === totalChequeAmount.toFixed(2) &&
        totalBD.toFixed(2) === totalBDAmount.toFixed(2) &&
        totalMO.toFixed(2) === totalMOAmount.toFixed(2) &&
        total.toFixed(2) === grandTotal.toFixed(2);
      
      return isMatch; 
    }
  }

  generatePDFfromAnotherPage(): void{
    const selectedBranchCode = this.branch_code;
    const balancingDate = this.balDate;

    const masterListing = { branch_code: selectedBranchCode, bal_date: balancingDate , vflag: true};

    // Store only these two parameters in localStorage
    localStorage.removeItem('mBankparam')
    localStorage.setItem('mBankparam', JSON.stringify(masterListing));
    //this.router.navigate(['/master-bankinslip']);
    const envURL = environment.angularPortal;
    let fullURL = '';
    if(envURL.includes('rmsbo')){
      fullURL = '/rmsbo/master-bankinslip';
    }
    else{
      fullURL = '/master-bankinslip';
    }

    const pdfWindow =  window.open(fullURL, '_blank', 'width=100,height=100');

    if (pdfWindow) {
      pdfWindow.moveTo(0, 0);
      pdfWindow.resizeTo(screen.width, screen.height);
    }
  }

  submitBankInSlip(): Promise<void>{
    return new Promise((resolve, reject) => {
      // Set your authorization header
      const headers = new HttpHeaders({
        Authorization: environment.authKey,
        'Content-Type': 'application/json',
      });
    
      this.isDisplay = true;
      this.isLoading = true;
      const url = environment.apiUrl + '/api/otcbis/v1/insbankinslip';
    
      const Body: any = {
        branch_code: this.branch_code,
        bal_date: this.balDate,
      };
    
      this.http.post(url, Body, { headers }).subscribe(
        (response: any) => {
          this.modelList = response.data;
          if (response.data.length == 0) {
            this.isDisplay = true;
            this.showResultAlertBox();
            this.isLoading = false;
          }

          resolve();
        },
        (error) => {
          console.error(error);
          this.isLoading = false;
          // Handle errors here
          reject(error);
        }
      );
    });
  }

  insCash(): Promise<void>{
    return new Promise((resolve, reject) => {
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
      const url = environment.apiUrl + '/api/otcbal/v1/insotcbalcashbytotal';

      this.http.post(url, this.moneys, { headers }).subscribe(
        (response: any) => {
          this.modelBalCash = response.data;
          if (response.data.length == 0) {
            this.isDisplay = true;
            this.showResultAlertBox();
            this.isLoading = false;
          } 
          // else {
          //   this.isLoading = false;

          //   // Clear and populate the moneyMap for fast lookup
          //   this.moneyMap.clear();
          //   this.modelBalCash.forEach((item) => {
          //     this.moneyMap.set(item.param_cd, item);
          //   });
          // }
          resolve(); 
        },
        (error) => {
          console.error(error);
          this.isLoading = false;
          // Handle errors here
          reject(error); 
        }
      );
    });    
  }

  submitCash(): Promise<void>{
    return new Promise((resolve, reject) => {
      // Add branch_code and bal_date to each item in this.moneys
      // this.moneys = this.moneys.map(item => ({
      //   ...item,  // Spread the existing properties of the item
      //   branch_code: this.branch_code,  // Add the branch_code
      //   bal_date: this.balDate  // Add the bal_date
      // }));

      // Set your authorization header
      const headers = new HttpHeaders({
        Authorization: environment.authKey,
        'Content-Type': 'application/json',
      });

      const Body: any = {
        branch_code: this.branch_code,
        bal_date: this.balDate,
      };

      this.isDisplay = true;
      this.isLoading = true;
      const url = environment.apiUrl + '/api/otcbal/v1/updotcbalcashbytotal';

      this.http.post(url, Body, { headers }).subscribe(
        (response: any) => {
          this.modelBalCash = response.data;
          if (response.data.length == 0) {
            this.isDisplay = true;
            this.showResultAlertBox();
            this.isLoading = false;
          } //else {
            // Clear and populate the moneyMap for fast lookup
            //this.moneyMap.clear();
            //this.modelBalCash.forEach((item) => {
            //  this.moneyMap.set(item.param_cd, item);
            //});
          //}
          
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

  submitPhy(): Promise<void>{
    return new Promise((resolve, reject) => {
      const headers = new HttpHeaders({
        Authorization: environment.authKey,
        'Content-Type': 'application/json',
      });

      const balDate = this.balDate; // Use your actual value or variable
      const branch_code = this.branch_code;   // Use your actual value or variable

      this.modelBalPhy.forEach(items => {
        items.bal_date = balDate;
        items.branch_code = branch_code;
      });

      this.isDisplay = true;
      this.isLoading = true;
      const url = environment.apiUrl + '/api/otcbalancingreq/v1/insotcbalphy';

      this.http.post(url, this.modelBalPhy, { headers }).subscribe(
        (response: any) => {
          if (response.data.length == 0) {
            this.isDisplay = true;
            this.showResultAlertBox();
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

  submitEMV(): Promise<void>{
    return new Promise((resolve, reject) => {
      const headers = new HttpHeaders({
        Authorization: environment.authKey,
        'Content-Type': 'application/json',
      });

      const Body: any = {
        branch_code: this.branch_code,
        bal_date: this.balDate
      };

      this.isDisplay = true;
      this.isLoading = true;
      const url = environment.apiUrl + '/api/otcbalancingreq/v1/insotcbalemvs';

      this.http.post(url, Body, { headers }).subscribe(
        (response: any) => {
          if (response.data.length == 0) {
            this.isDisplay = true;
            this.showResultAlertBox();
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

  updStatus(): Promise<void>{
    return new Promise((resolve, reject) => {
      // Set your authorization header
      const headers = new HttpHeaders({
        Authorization: environment.authKey,
        'Content-Type': 'application/json',
      });
    
      this.isSubmitNavigation = true;
      this.isDisplay = true;
      this.isLoading = true;
      const url = environment.apiUrl + '/api/otcmasterbal/v1/updotcmasterbalstatus';
    
      const Body: any = {
        branch_code: this.branch_code,
        bal_date: this.balDate,
        bal_status: 'C',
        bal_type: 'BM',
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
            const selectedBranchCode = this.branch_code;  // Assuming this.selectedBranchCode contains the branch code
            const balancingDate = this.balDate;  // Assuming this.balancingDate contains the balancing date
            const masterListing = { branch_code: selectedBranchCode, bal_date: balancingDate, vflag: false };

            //Submit to FMS
            this.submitFMS();

            // Store only these two parameters in localStorage
            localStorage.removeItem('mBankparam')
            localStorage.setItem('mBankparam', JSON.stringify(masterListing));
            this.router.navigate(['/master-bankinslip']);
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

  autoUpdStatus(): void{
    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    this.isDisplay = true;
    this.isLoading = true;
    const url = environment.apiUrl + '/api/otcmasterbal/v1/updotcmasterbalstatus';

    const Body: any = {
      branch_code: this.branch_code,
      bal_date: this.balDate,
      bal_status: 'P',
      bal_type: 'BM',
    };

    this.http.post(url, Body, { headers }).subscribe(
      () => {}, // Ignore the response
      () => {}  // Ignore the error
    );
  }

  cancel(): void{
    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    this.isDisplay = true;
    this.isLoading = true;
    const url = environment.apiUrl + '/api/otcmasterbal/v1/updotcmasterbalstatus';

    const Body: any = {
      branch_code: this.branch_code,
      bal_date: this.balDate,
      bal_status: 'P',
      bal_type: 'BM',
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

  submitFMS(): void{
    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    this.isLoading = true;
    const url = environment.apiUrl + '/api/otcbis/v1/updotcfms';

    const Body: any = {
      otc_type: 'm',
      dt_balancing: this.balDate
    };

    // Fire and forget - no handling of response or error
    this.http.post(url, Body, { headers }).subscribe(
      () => {}, // Ignore the response
      () => {}  // Ignore the error
    );
  }

  async submit(): Promise<void>{
    this.isLoading = true;
    if(this.modelBalInfo[0].total_cash != 0){
      await this.insCash();
      await this.submitCash();
    }
    if(this.modelBalInfo[0].total_emv != 0){
      await this.submitEMV();
    }
    if(this.modelBalInfo[0].total_che != 0 || this.modelBalInfo[0].total_bd != 0 || this.modelBalInfo[0].total_mo != 0){
      await this.submitPhy();
    }
    await this.submitBankInSlip();
    //this.submitFMS();
    await this.updStatus();
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
              'Total EMV (RM)': (item.total_emv ?? 0).toFixed(2),
              'Total Physical (RM)': (item.total_phy ?? 0).toFixed(2),
              'Total Cash (RM)': (item.total_cash ?? 0).toFixed(2),
              'Total Cheque (RM)': (item.total_che ?? 0).toFixed(2),
              'Total Bank Draft (RM)': (item.total_bd ?? 0).toFixed(2),
              'Total Money Order (RM)': (item.total_mo ?? 0).toFixed(2),
              'Total Amount (RM)': (item.total ?? 0).toFixed(2)
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
                'Total Amount (RM)': (item.totalAmount ?? 0).toFixed(2)
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
                'EMV Txn Total (RM)': (item.total_cash_amt ?? 0).toFixed(2),
                'EMV Settlement Total (RM)': (item.totalFromEMV ?? 0).toFixed(2)
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
            data: this.modelBalInfo.length > 0 ? [ 
              {
                  '': 'RMS Collection', // Second row: RMS Collection
                  'EMV Amount (RM)': (this.modelBalInfo[0].total_emv ?? 0).toFixed(2),
                  'Cash Amount (RM)': (this.modelBalInfo[0].total_cash ?? 0).toFixed(2),
                  'Cheque Amount (RM)': (this.modelBalInfo[0].total_che ?? 0).toFixed(2),
                  'Bank Draft Amount (RM)': (this.modelBalInfo[0].total_bd ?? 0).toFixed(2),
                  'Money Order Amount (RM)': (this.modelBalInfo[0].total_mo ?? 0).toFixed(2),
                  'Total Collected Amount (RM)': (this.modelBalInfo[0].total ?? 0).toFixed(2)
              },
              {
                  '': 'Counter Collection', // Third row: Counter Collection
                  'EMV Amount (RM)': (this.totalEMVAmount ?? 0).toFixed(2),
                  'Cash Amount (RM)': (this.grandCashTotal ?? 0).toFixed(2),
                  'Cheque Amount (RM)': (this.totalChequeAmount ?? 0).toFixed(2),
                  'Bank Draft Amount (RM)': (this.totalBDAmount ?? 0).toFixed(2),
                  'Money Order Amount (RM)': (this.totalMOAmount ?? 0).toFixed(2),
                  'Total Collected Amount (RM)': (this.grandTotal ?? 0).toFixed(2)
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
    const filename = `MB_${formattedDate}_${branchCode}_${dates}.pdf`;

    // Save the PDF
    doc.save(filename);
}

}