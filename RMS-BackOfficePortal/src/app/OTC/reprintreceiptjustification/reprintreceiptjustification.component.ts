import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, NavigationEnd } from '@angular/router';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { environment } from 'src/environments/environment';

import { RROrderInfo, RRPaymentItems, RRPaymentInfo, RRReceiptInfo, RRHistoryTable, RRJustification, RRPaymentInfoV2, RRHistoryTable_v2 } from '../../core/models/reprint-receipt.interface';
import { ParamService } from '../../core/services/param.service';
import { ParamData } from '../../core/models/param.interface';
import { Param, User } from '../../core/models/entity';
import { Router } from '@angular/router';
import { NgForm } from '@angular/forms';
import moment from 'moment';
import { Systemstatus } from '../../shared/enums/systemstatus';
import { trigger, state, style, transition, animate } from '@angular/animations';
import { fadeInOut } from '../../shared/animation';
import { from } from 'rxjs';
import { OTCReceiptCancellationHistoryDetails } from 'src/app/core/models/otc-receipt-cancellation.interface';

@Component({
  selector: 'app-reprintreceiptjustification',
  templateUrl: './reprintreceiptjustification.component.html',
  styleUrls: ['./reprintreceiptjustification.component.scss']
})
export class ReprintreceiptjustificationComponent implements OnInit {
  isDisplay: boolean = false;
  isLoading: boolean = false;
  isEmptyResult = false;
  isDisplayTaskLists: boolean = false;
  isLoadingTaskLists: boolean = false;
  isLoadingPaymentInfo: boolean = false;
  errorMessages: string[] = [];
  error: boolean = false;
  selectedJustification: string = '';
  remark: string = ''; // Define the remark property



  rroi: RROrderInfo[] = []
  rrpi: RRPaymentItems[] = []
  rrpii: RRPaymentInfo[] = []
  rrpiiV2: RRPaymentInfoV2[] = []
  rrri: RRReceiptInfo[] = []
  rrht: RRHistoryTable[] = []
  rrhtV2: RRHistoryTable_v2[] = []
  rrj: RRJustification[] = []


  updateWFstatus: string = ""
  dropDownTotalRecord = 1000;
  navigateToAssignedTask = false

  statusOptions: Param[] = [];
  users: User[] = [];
  page = environment.DefaultPage;
  mttId: any;
  itemsPerPage = environment.ItemPerPage;
  pagePaymentInfo = environment.DefaultPage;
  itemsPerPagePaymentInfo = environment.ItemPerPage;
  totalRecords: number = 0;
  totalRcptInfoRecords: number = 0;
  selectedEffectiveDate!: { start?: moment.Moment; end?: moment.Moment };
  selectedRequestedDate!: { start?: moment.Moment; end?: moment.Moment };
  showResultAlert = false;

  showResultAlertBox() {
    this.showResultAlert = true;
    setTimeout(() => this.showResultAlert = false, 2000);
  }

  isFormValid(): boolean {
    if (this.selectedJustification === 'Others') {
      // When 'Others' is selected, ensure the remark field is filled
      return this.remark.trim().length > 0;
    } else {
      // For other justifications, just check if one is selected
      return !!this.selectedJustification;
    }
  }


  //order info
  ssCd: string | null = null
  collSlipNo: string | null = null
  ornNo: string | null = null
  custNm: string | null = null
  custPhone: string | null = null
  custEmail: string | null = null
  custAddr1: string | null = null
  custAddr2: string | null = null
  custAddr3: string | null = null
  custPostcode: string | null = null
  custCity: string | null = null
  custState: string | null = null
  orderStatus: string | null = null


  //payment items
  itemDesc: string | null = null
  qty: number | null = null
  netAmt: number | null = null
  taxAmt: number | null = null
  grantCd: string | null = null
  discAmt: number | null = null
  grossAmt: number | null = null
  grossAmtTotal: number | null = null


  //payment info
  otcId: string | null = null
  userEmail: string | null = null
  otcPymtMode: string | null = null
  cashAmt: number | null = null
  cashAmtTotal: number | null = null
  otcCheId: string | null = null
  cheStatus: string | null = null
  cheBankNm: string | null = null
  cheNo: string | null = null
  cheDate: string | null = null
  cheBaAcctNo: string | null = null
  cheAmt: number | null = null
  cheAmtTotal: number | null = null


  //receipt info
  rcptNo: string | null = null
  fileNm: string | null = null
  rcptDt: string | null = null
  rcptStatus: string | null = null
  rcptReprint: number | null = null
  ssdocrefId: string | null = null
  verIdOtc: string | null = null
  verIdMtt: string | null = null
  verId: string | null = null

  rmsType: string | null = null

  //payment info V2

  payerEmail: string | null = null
  otcBodyId: number | null = null
  chePayerNm: string | null = null
  moAmt: number | null = null
  moRmNo: string | null = null
  moDate: string | null = null
  moPayerNm: string | null = null
  moIdNo: string | null = null
  moContactNo: string | null = null
  bdAmt: number | null = null
  bdNo: string | null = null
  bdDate: string | null = null
  bdBankNm: string | null = null
  cheId: string | null = null
  transTrace: string | null = null
  batchNo: string | null = null
  hostNo: string | null = null
  tId: string | null = null
  amt: number | null = null
  total: number | null = null
  cashAmount: number | null = null;

  //  ornNo: string | null = null
  fileContent: string | null = null;
  fileType: string | null = null;
  idamanFileName: string | null = null;

  //history table
  action: string | null = null
  dtAction: string | null = null
  otcStatus: string | null = null
  counterId: string | null = null
  actBy: string | null = null
  status: string | null = null

  //download file
  file_content: string | null = null;

  //justification
  otc_rc_rp_id: string | null = null
  otc_rcpt_id: string | null = null
  justification: string | null = null
  dt_created: string | null = null
  dt_modified: string | null = null
  created_by: string | null = null
  modified_by: string | null = null
  // status: string | null = null
  rcpt_no: string | null = null
  otcRcRpId: string | null = null
  otcRcptId: string | null = null


  totalHistoryRecords: number = 0;
  isDisplayHist: boolean = false;
  OTCReceiptCancellationHistoryDetails: OTCReceiptCancellationHistoryDetails[] = [];
  histOTCStatus: any[] = [];



  //default pagination
  selectedValue = environment.dropdownOptions[0];
  dropdownOptions = environment.dropdownOptions;

  LoadData(singleItem: number) {
    this.selectedValue = singleItem;
    this.itemsPerPage = this.selectedValue;

  }

  constructor(private route: ActivatedRoute, private http: HttpClient, private router: Router) { }
  previousUrl: string | null = null;

  ngOnInit() {
    // this.router.events.subscribe(event => {
    // if (event instanceof NavigationEnd) {
    //   this.previousUrl = event.url || '1'; // Set to '1' if null
    //   console.log('Previous URL:', this.previousUrl);
    //   }
    // });

    // // If previousUrl is still null after subscription, set it to '1'
    // if (!this.previousUrl) {
    //   this.previousUrl = '1';
    // }
    // console.log('Previous URL after check:', this.previousUrl);


    this.mttId = history.state.mtt_id
    this.otcId = history.state.otc_id
    this.otcRcRpId = history.state.otc_rc_rp_id
    this.otcRcptId = history.state.otc_rcpt_id
    this.otcPymtMode = history.state.otc_pymt_mode || history.state.otc_payment_mode;
    this.rcptNo = history.state.rcpt_no;
    this.rmsType = history.state.rms_type;
    this.otcRcptId = history.state.otc_rcpt_id; // Ensure otcRcptId is set correctly
    
    console.log(this.mttId, this.otcId, this.otcRcRpId, this.otcRcptId, this.otcPymtMode, this.rcptNo, this.rmsType);
    console.log("Receipt No", this.rcptNo);



    this.populateForm()
    this.populateFormPaymentItems()
    this.populateFormPaymentInfo()
    //this.populateFormPaymentInfoV2()
    this.populateFormReceiptInfo()
    this.populatercrpid()
    //this.populateFormHistoryTable()
    this.populateFormJustification()
    this.loadHistory()

    if (!sessionStorage.getItem('reloaded')) {
      sessionStorage.setItem('reloaded', 'true');
      window.location.reload();
    } else {
      sessionStorage.removeItem('reloaded');
    }


  }

  // isPreviousPageReprintReceiptDetails(): boolean {
  //   return this.previousUrl === '/reprintreceiptdetails';
  // }

  // isPreviousPageMttDetails(): boolean {
  //   return this.previousUrl === '/mtt-details';
  // }








  resetForm(): void {
    this.selectedJustification = ''; // Clear the selected justification
    this.remark = ''; // Clear the remark
    window.history.back()
  }


  populateForm() {

    this.mttId = this.mttId;
    this.isDisplay = true;
    this.isLoading = true;

    const url = environment.apiUrl + '/api/rr/v1/orderinfo_rr';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    const Body: any = {
      // i_page: this.page.toString(),
      // i_size: 1,
      i_mtt_id: this.mttId
    };

    this.http.post(url, Body, { headers }).subscribe(
      (response: any) => {
        if (response.data.length === 0) {
          this.totalRecords = 0;
        } else {
          this.totalRecords = response.data[0].total;
          this.rroi = response.data;
          this.mttId = this.rroi[0].mtt_id;
          this.ssCd = this.rroi[0].ss_cd;
          this.collSlipNo = this.rroi[0].coll_slip_no;
          this.ornNo = this.rroi[0].orn_no;
          this.custNm = this.rroi[0].cust_nm;
          this.custPhone = this.rroi[0].cust_phone;
          this.custEmail = this.rroi[0].cust_email;
          this.custAddr1 = this.rroi[0].cust_addr_1;
          this.custAddr2 = this.rroi[0].cust_addr_2;
          this.custAddr3 = this.rroi[0].cust_addr_3;
          this.custPostcode = this.rroi[0].cust_postcode;
          this.custCity = this.rroi[0].cust_city;
          this.custState = this.rroi[0].cust_state;
          this.orderStatus = this.rroi[0].order_status;


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

  populateFormPaymentItems() {

    this.mttId = this.mttId;
    this.isDisplay = true;
    this.isLoading = true;

    const url = environment.apiUrl + '/api/rr/v1/paymentitems_rr';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    const Body: any = {
      // i_page: this.page.toString(),
      // i_size: 1,
      i_mtt_id: this.mttId
    };

    this.http.post(url, Body, { headers }).subscribe(
      (response: any) => {
        if (response.data.length === 0) {
          this.totalRecords = 0;
          this.isEmptyResult = false;
        } else {
          this.totalRecords = response.data[0].total;
          this.rrpi = response.data;
          this.mttId = this.rrpi[0].mtt_id;
          this.isEmptyResult = false;


          this.itemDesc = this.rrpi[0].item_desc;
          this.qty = this.rrpi[0].qty;
          this.netAmt = this.rrpi[0].net_amt;
          this.taxAmt = this.rrpi[0].tax_amt;
          this.grantCd = this.rrpi[0].grant_cd;
          this.discAmt = this.rrpi[0].disc_amt;
          this.grossAmt = this.rrpi[0].gross_amt;
          this.grossAmtTotal = this.rrpi[0].gross_amt_total;





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

  populateFormPaymentInfo() {
    this.isLoadingPaymentInfo = true;

    const urlMftWFHis = environment.apiUrl + '/api/rr/v1/paymentinfo_rr_v2';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json'
    });


    // Create the request body with your form data
    const requestBody: any = {
      i_mtt_id: this.mttId
    };

    this.http.post(urlMftWFHis, requestBody, { headers }).subscribe(
      (response: any) => {

        if (response.data.length === 0) {
          // this.isDisplayHist = false;
          this.isLoadingPaymentInfo = false;
          // this.totalRecordsHist = 0;
          //  this.isOTCPaymentModeFinishLoading = true;
          console.error('Invalid otc receipt cancellation payment info details response format:', response);
        }
        else {
          this.rrpiiV2 = response.data;
          this.payerEmail = this.rrpiiV2[0].payer_email;
          //below are comment out because it can have multiple while top one only need display

          this.cashAmount = this.calculateTotalCashAmount();


          // this.chequeNo = this.OTCReceiptCancellationPaymentInfoDetails[0].che_no;
          // this.chequeBankAccountNo = this.OTCReceiptCancellationPaymentInfoDetails[0].che_ba_acct_no;
          // this.OTCChequeId = this.OTCReceiptCancellationPaymentInfoDetails[0].otc_che_id;
          // this.chequeAmount = this.OTCReceiptCancellationPaymentInfoDetails[0].che_amt;
          // this.chequeDate = this.OTCReceiptCancellationPaymentInfoDetails[0].che_date;
          // this.chequeBankName = this.OTCReceiptCancellationPaymentInfoDetails[0].che_bank_nm;
          // this.chequePayerName = this.OTCReceiptCancellationPaymentInfoDetails[0].che_payer_nm;

          // this.isDisplayHist = true;
          this.isLoadingPaymentInfo = false;
          // this.isOTCPaymentModeFinishLoading = true;
          //this.totalRecordsHist = response.data[0].total;
        }
        //  console.log("MFTWF is "+this.mftwf[0].fee_detail_id);
        //  console.log(this.totalRecords);

      },
      (error) => {
        console.error('There was an error retrieving the otc receipt cancellation payment info details:', error);
        this.isLoadingPaymentInfo = false;
        // this.isOTCPaymentModeFinishLoading = false;
      }
    );

  }






  populateFormReceiptInfo() {

    this.mttId = this.mttId;
    this.otcId = this.otcId;
    this.isDisplay = true;
    this.isLoading = true;

    const url = environment.apiUrl + '/api/rr/v1/receiptinfo_rr';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    const Body: any = {

      i_mtt_id: this.mttId

    };
    console.log(Body);


    this.http.post(url, Body, { headers }).subscribe(
      (response: any) => {
        if (response.data.length === 0) {
          this.totalRcptInfoRecords = 0;
          this.isEmptyResult = false;
        } else {
          this.totalRcptInfoRecords = response.data[0].total;
          this.rrri = response.data;
          this.mttId = this.rrri[0].mtt_id;
          this.isEmptyResult = false;

          // mtt_id:string;
          // rcpt_no:string;
          // file_nm:string;
          // rcpt_dt:string;
          // rcpt_status:string;
          // rcpt_reprint:number;

          this.rcptNo = this.rrri[0].rcpt_no;
          this.fileNm = this.rrri[0].file_nm;
          this.rcptDt = this.rrri[0].rcpt_dt;
          this.rcptStatus = this.rrri[0].rcpt_status;
          this.rcptReprint = this.rrri[0].rcpt_reprint;
          this.ssdocrefId = this.rrri[0].ssdocref_id;
          this.verId = this.rrri[0].ver_id;
          this.ornNo = this.rrri[0].orn_no;
          this.fileContent = this.rrri[0].file_content;
          this.idamanFileName = this.rrri[0].idaman_file_name;
          this.fileType = this.rrri[0].file_type;


          console.log(this.rrri[0].ver_id_mtt);





          this.isLoading = false;
        }
        console.log(response.data);
        console.log("totalreceiptinfo" + this.totalRcptInfoRecords);
      },
      (error) => {
        console.error(error);
        this.isLoading = false;
        // Handle errors here
      }
    );

  }

  useFileContentInAnotherFunction() {
    if (this.fileContent) {
      console.log('Using file content:', this.fileContent);
      // Add your logic here (e.g., display, convert, download)
    } else {
      console.warn('fileContent is not set yet.');
    }
  }




  populateFormJustification() {

    this.otcId = this.otcId;
    this.isDisplay = true;
    this.isLoading = true;

    const url = environment.apiUrl + '/api/rr/v1/justification_rr';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    const Body: any = {

      i_otc_id: this.otcId
    };

    this.http.post(url, Body, { headers }).subscribe(
      (response: any) => {
        if (response.data.length === 0) {
          this.totalRecords = 0;
          this.isEmptyResult = false;
        } else {
          this.totalRecords = response.data[0].total;
          this.rrj = response.data;
          this.otcId = this.rrj[0].otc_id;
          this.isEmptyResult = false;

          // mtt_id:string;
          // rcpt_no:string;
          // file_nm:string;
          // rcpt_dt:string;
          // rcpt_status:string;
          // rcpt_reprint:number;

          this.otc_rc_rp_id = this.rrj[0].otc_rc_rp_id;
          this.otc_rcpt_id = this.rrj[0].otc_rcpt_id;
          this.justification = this.rrj[0].justification;
          this.dt_created = this.rrj[0].dt_created;
          this.dt_modified = this.rrj[0].dt_modified;
          this.created_by = this.rrj[0].created_by;
          this.modified_by = this.rrj[0].modified_by;
          this.rcpt_no = this.rrj[0].rcpt_no;
          this.fileType = this.rrj[0].file_type;
          this.fileContent = this.rrj[0].file_content;
          this.idamanFileName = this.rrj[0].idaman_file_name;
          console.log(this.idamanFileName);




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

  loadHistory() {

    this.isLoading = true;

    const urlMftWFHis = environment.apiUrl + '/api/otcrcptccl/v1/getotcreceiptcancellationhistorydetailsaudit';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json'
    });


    // Create the request body with your form data
    const requestBody: any = {
      i_mtt_id: this.mttId
    };

    this.http.post(urlMftWFHis, requestBody, { headers }).subscribe(
      (response: any) => {

        if (response.data.length === 0) {
          this.isDisplayHist = false;
          this.isLoading = false;
          this.totalHistoryRecords = 0;
          console.error('Invalid otc receipt cancellation order info details response format:', response);
        }
        else {
          this.totalHistoryRecords = response.data[0].total;
          this.rrhtV2 = response.data;
          this.isDisplayHist = true;
          this.isLoading = false;
        }
      },
      (error) => {
        console.error('There was an error retrieving the otc receipt cancellation order info details:', error);
        this.isLoading = false;
      }
    );
  }


  async downloadFile(idaman_file_name: string): Promise<void> {
    const url = environment.apiUrl + '/api/rr/v1/receiptinfo_rr';
    console.log('File Name (param): ' + idaman_file_name);

    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    const Body: any = {
      i_mtt_id: this.mttId
    };

    this.http.post(url, Body, { headers }).subscribe(
      (response: any) => {
        console.log('Full Response:', response);

        const data = response.data?.[0];

        if (!data) {
          this.totalRecords = 0;
          this.isLoading = false;
          return;
        }

        this.fileContent = data.file_content;
        this.idamanFileName = data.idaman_file_name;

        console.log('Extracted file content:', this.fileContent);
        console.log('Extracted file name:', this.idamanFileName);

        if (this.idamanFileName && this.fileContent) {
          this.downloadFileContent(this.idamanFileName, this.fileContent);
        }

        this.totalRecords = data.total || 0;
        this.isLoading = false;
        this.isDisplay = true;
      },
      (error) => {
        console.error('HTTP Error:', error);
        this.isLoading = false;
      }
    );
  }






  downloadFileContent(fileName: string, fileContent: string): void {
    //this.isLoading = true;
    const binaryString = window.atob(fileContent);
    const len = binaryString.length;
    const uint8Array = new Uint8Array(len);

    for (let i = 0; i < len; i++) {
      uint8Array[i] = binaryString.charCodeAt(i);
    }

    const blob = new Blob([uint8Array], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' });

    const url = URL.createObjectURL(blob);

    const anchor = document.createElement('a');
    anchor.href = url;
    anchor.download = fileName;

    document.body.appendChild(anchor);
    anchor.click();

    document.body.removeChild(anchor);
    URL.revokeObjectURL(url);

    // Add a small delay to ensure download is triggered before reload
    setTimeout(() => {
      location.reload();
    }, 1500); // Delay can be adjusted as needed
  }

  async UpdateReceiptCount(): Promise<void> {
    const url = environment.apiUrl + '/api/rr/v1/updrcptcount_rr';

    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    const body: any = {
      i_otc_rcpt_id: this.otcRcptId
    };

    try {
      this.http
        .post(url, body, { headers })
        .toPromise()
        .then((response) => {
          console.log('Success response:', response);
        })
        .catch((error) => {
          console.error('Error:', error);
        });
    } catch (error) {
      console.error(error);
    }
  }

  async UpdateReceiptJust(): Promise<void> {
    const url = environment.apiUrl + '/api/rr/v1/updrcptjust_rr';

    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    const body: any = {
      i_otc_id: this.otcId,
      i_otc_rc_rp_id: this.otc_rc_rp_id,
      i_otc_rcpt_id: this.otcRcptId,
      i_justication: this.selectedJustification

    };
    if (this.selectedJustification === 'Others' && this.remark) {
      body.i_justication = this.remark; // Set i_justification to the value of remark
    } else {
      body.i_justication = this.selectedJustification; // Otherwise, use the selected justification
    }
    console.log(body);

    try {
      this.http
        .post(url, body, { headers })
        .toPromise()
        .then((response) => {
          console.log('Success response:', response);
        })
        .catch((error) => {
          console.error('Error:', error);
        });
      console.log('Success response:', body);
    } catch (error) {
      console.error(error);
    }
  }






  getTotalBankDraftAmountPaymentInfo(): number {
    if (!this.rrpiiV2) {
      return 0;
    }
    return this.rrpiiV2.reduce((sum, item) => sum + (item.bd_amt || 0), 0);
  }

  getTotalChequeAmountPaymentInfo(): number {
    if (!this.rrpiiV2) {
      return 0;
    }
    return this.rrpiiV2.reduce((sum, item) => sum + (item.che_amt || 0), 0);
  }


  getTotalMoneyOrderAmountPaymentInfo(): number {
    if (!this.rrpiiV2) {
      return 0;
    }
    return this.rrpiiV2.reduce((sum, item) => sum + (item.mo_amt || 0), 0);
  }

  getTotalEMVAmountPaymentInfo(): number {
    if (!this.rrpiiV2) {
      return 0;
    }
    return this.rrpiiV2.reduce((sum, item) => sum + (item.amt || 0), 0);
  }

  getOTCStatusName(status: string | null): string {
    if (!status) {
      return '';
    }
    const statusName = this.histOTCStatus.find((option) => option.param_cd === status);
    return statusName ? statusName.nm_en : status;
  }

  calculateTotalCashAmount(): number {
    // Calculate the sum of cash_amt, defaulting to 0 if null or undefined
    return this.rrpiiV2.reduce((sum, item) => {
      return sum + (item.cash_amt || 0); // Default to 0 if cash_amt is null or undefined
    }, 0);
  }

  getValidChequePaymentInfoDetails(): any[] {
    return this.rrpiiV2?.filter(item => item.che_amt !== null) || [];
  }

  getValidBankDraftPaymentInfoDetails(): any[] {
    return this.rrpiiV2?.filter(item => item.bd_amt !== null) || [];
  }

  getValidMOPaymentInfoDetails(): any[] {
    return this.rrpiiV2?.filter(item => item.mo_amt !== null) || [];
  }

  getValidEVPaymentInfoDetails(): any[] {
    return this.rrpiiV2?.filter(item => item.amt !== null) || [];
  }

  getTotalCheckAmountPaymentInfo(): number {
    if (!this.rrpiiV2) {
      return 0;
    }
    return this.rrpiiV2.reduce((sum, item) => sum + (item.che_amt || 0), 0);
  }




  populatercrpid() {

    this.otcId = this.otcId;
    this.rcptNo = this.rcptNo;
    console.log('rcptNo', this.rcptNo);
    this.isDisplay = true;
    this.isLoading = true;

    const url = environment.apiUrl + '/api/rr/v1/reprintreceipt';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    const Body: any = {

      i_page: '1',
      i_size: '1',
      i_rcpt_no: this.rcptNo
    };

    this.http.post(url, Body, { headers }).subscribe(
      (response: any) => {
        if (response.data.length === 0) {
          this.totalRecords = 0;
          this.isEmptyResult = false;
        } else {
          this.totalRecords = response.data[0].total;
          this.rrj = response.data;
          this.otcId = this.rrj[0].otc_id;
          this.isEmptyResult = false;

          // mtt_id:string;
          // rcpt_no:string;
          // file_nm:string;
          // rcpt_dt:string;
          // rcpt_status:string;
          // rcpt_reprint:number;

          this.otc_rc_rp_id = this.rrj[0].otc_rc_rp_id;
          console.log('tesssss' + this.rrj[0].otc_rc_rp_id);





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

  async ReceiptWatermark(): Promise<void> {
    const url = environment.apiUrl + '/api/rr/v1/updatemttorderstatusandreceipt';
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });
    const body: any = {
      i_mtt_id: this.mttId,
      i_order_status: 'V',
      i_otc_rc_rp_id: this.otc_rc_rp_id

    };
    console.log("mttid in ReceiptWatermark:", this.mttId);
    console.log(body);
    try {
      const response = await this.http.post(url, body, { headers }).toPromise();
      console.log('Success response in ReceiptWatermark:', response);
    } catch (error) {
      console.error('Error in ReceiptWatermark:', error);
      throw error; // Rethrow the error to be caught in handleSubmit
    }
  }


  async handleSubmit(): Promise<void> {
    try {
      console.log('Starting handleSubmit...');
      console.log('mttId:', this.mttId);
      //await this.ReceiptWatermark();




      await this.UpdateReceiptCount();
      await this.UpdateReceiptJust();
      await this.downloadFile(this.idamanFileName || '');

      //location.reload();


    } catch (error) {
      console.error('Error in handleSubmit:', error);
    }
  }





}
