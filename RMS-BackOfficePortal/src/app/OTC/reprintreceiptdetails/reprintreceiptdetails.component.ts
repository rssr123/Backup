import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { environment } from 'src/environments/environment';

import { RROrderInfo, RRPaymentItems, RRPaymentInfo, RRReceiptInfo, RRHistoryTable, RRPaymentInfoV2, RRJustification, RRHistoryTable_v2 } from '../../core/models/reprint-receipt.interface';
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
  selector: 'app-reprintreceiptdetails',
  templateUrl: './reprintreceiptdetails.component.html',
  styleUrls: ['./reprintreceiptdetails.component.scss']
})
export class ReprintreceiptdetailsComponent implements OnInit {

  isDisplay: boolean = false;
  isLoading: boolean = false;
  isEmptyResult = false;
  isDisplayTaskLists: boolean = false;
  isLoadingTaskLists: boolean = false;
  isLoadingPaymentInfo: boolean = false;
  errorMessages: string[] = [];
  error: boolean = false;
  totalReceiptInfoRecords: number = 0;
  isDisplayReceiptInfo: boolean = false;
  totalHistoryRecords: number = 0;
  isDisplayHist: boolean = false;

  rroi: RROrderInfo[] = []
  rrpi: RRPaymentItems[] = []
  rrpii: RRPaymentInfo[] = []
  rrri: RRReceiptInfo[] = []
  rrht: RRHistoryTable[] = []
  rrhtV2: RRHistoryTable_v2[] = []
  rrpiiV2: RRPaymentInfoV2[] = []
  rrj: RRJustification[] = []
  histOTCStatus: any[] = [];

  OTCReceiptCancellationHistoryDetails: OTCReceiptCancellationHistoryDetails[] = [];


  pagePaymentInfo = environment.DefaultPage;
  itemsPerPagePaymentInfo = environment.ItemPerPage;



  updateWFstatus: string = ""
  dropDownTotalRecord = 1000;
  navigateToAssignedTask = false

  statusOptions: Param[] = [];
  users: User[] = [];
  page = environment.DefaultPage;
  mttId: any;
  itemsPerPage = environment.ItemPerPage;
  totalRecords: number = 0;
  selectedEffectiveDate!: { start?: moment.Moment; end?: moment.Moment };
  selectedRequestedDate!: { start?: moment.Moment; end?: moment.Moment };
  showResultAlert = false;

  showResultAlertBox() {
    this.showResultAlert = true;
    setTimeout(() => this.showResultAlert = false, 2000);
  }


  //order info
  orderStatus: string | null = null
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
  otcRcRpId: string | null = null
  otcRcptId: string | null = null
  otc_rc_rp_id: string | null = null


  //default pagination
  selectedValue = environment.dropdownOptions[0];
  dropdownOptions = environment.dropdownOptions;

  LoadData(singleItem: number) {
    this.selectedValue = singleItem;
    this.itemsPerPage = this.selectedValue;
    //this.loadData();
  }

  constructor(private route: ActivatedRoute, private http: HttpClient, private router: Router) { }


  ngOnInit() {

    this.mttId = history.state.mtt_id
    this.otcId = history.state.otc_id
    this.otcRcRpId = history.state.otc_rc_rp_id
    this.otcRcptId = history.state.otc_rcpt_id
    this.otcPymtMode = history.state.otc_pymt_mode;
    this.rcptNo = history.state.rcpt_no;

    console.log("otcId=" + this.otcId, "mttId=" + this.mttId, "otcRcRpId=" + this.otcRcRpId, "otcRcptId=" + this.otcRcptId, "otcPymtMode=" + this.otcPymtMode);
    console.log("rcptNo=" + this.rcptNo);


    //this.loadData()
    this.populateForm()
    this.populateFormPaymentItems()
    this.populateFormPaymentInfo()
    //this.populateFormPaymentInfoV2()
    this.populateFormReceiptInfo()
    this.populatercrpid()
    //this.populateFormHistoryTable()
    this.loadHistory()
    if (!sessionStorage.getItem('reloaded')) {
      sessionStorage.setItem('reloaded', 'true');
      window.location.reload();
    } else {
      sessionStorage.removeItem('reloaded');
    }

  }



  // loadData() {
  //   this.isDisplay = true;
  //   this.isLoading = true;
  //   const url = environment.apiUrl + '/api/rr/v1/orderinfo_rr';

  //   // Set your authorization header
  //   const headers = new HttpHeaders({
  //     Authorization: environment.authKey,
  //     'Content-Type': 'application/json',
  //   });

  //   const Body: any = {
  //     i_page: this.page.toString(),
  //     i_size: this.itemsPerPage.toString(),
  //   };



  //   console.log(Body);

  //   this.http.post(url, Body, { headers }).subscribe(
  //     (response: any) => {
  //       this.rroi = response.data;
  //       if (response.data.length == 0) {
  //         this.totalRecords = 0;
  //         this.isDisplay = false;

  //         this.isLoading = false;
  //       } else {
  //         this.totalRecords = response.data[0].total;

  //         this.isLoading = false;
  //       }
  //       // console.log(response.data);
  //       //  console.log(this.totalRecords);
  //     },
  //     (error) => {
  //       console.error(error);
  //       this.isLoading = false;
  //       // Handle errors here  
  //     }
  //   );
  // }


  // apply() {
  //   this.loadData()
  // }


  reset() {


    this.selectedEffectiveDate = {
      start: moment().subtract(1, 'month'),
      end: moment(),
    };
    this.selectedRequestedDate = {
      start: moment().subtract(1, 'month'),
      end: moment(),
    };


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
        console.log('Response:', response);

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

  // populateFormPaymentInfoV2() {

  //   this.mttId = this.mttId;
  //   this.isDisplay = true;
  //   this.isLoading = true;
  //   this.isLoadingPaymentInfo = true;

  //   const url = environment.apiUrl + '/api/rr/v1/paymentinfo_rr_v2';

  //   // Set your authorization header
  //   const headers = new HttpHeaders({
  //     Authorization: environment.authKey,
  //     'Content-Type': 'application/json',
  //   });

  //   const Body: any = {

  //     i_mtt_id: this.mttId
  //   };

  //   this.http.post(url, Body, { headers }).subscribe(
  //     (response: any) => {
  //       if (response.data.length === 0) {
  //         this.isLoadingPaymentInfo = false;
  //       } else {
  //         this.isLoadingPaymentInfo = false;
  //         this.totalRecords = response.data[0].total;
  //         this.rrpiiV2 = response.data;
  //         this.mttId = this.rrpiiV2[0].mtt_id;
  //         this.isEmptyResult = false;

  //         this.ornNo = this.rrpiiV2[0].orn_no;
  //         this.collSlipNo = this.rrpiiV2[0].coll_slip_no;
  //         this.payerEmail = this.rrpiiV2[0].payer_email;
  //         this.otcPymtMode = this.rrpiiV2[0].otc_pymt_mode;
  //         this.otcBodyId = this.rrpiiV2[0].otc_body_id;
  //         this.cashAmt = this.rrpiiV2[0].cash_amt;
  //         this.cheAmt = this.rrpiiV2[0].che_amt;
  //         this.cheDate = this.rrpiiV2[0].che_date;
  //         this.cheBankNm = this.rrpiiV2[0].che_bank_nm;
  //         this.chePayerNm = this.rrpiiV2[0].che_payer_nm;
  //         this.cheNo = this.rrpiiV2[0].che_no;
  //         this.cheStatus = this.rrpiiV2[0].che_status;
  //         this.moAmt = this.rrpiiV2[0].mo_amt;
  //         this.moRmNo = this.rrpiiV2[0].mo_rm_no;
  //         this.moDate = this.rrpiiV2[0].mo_date;
  //         this.moPayerNm = this.rrpiiV2[0].mo_payer_nm;
  //         this.moIdNo = this.rrpiiV2[0].mo_id_no;
  //         this.moContactNo = this.rrpiiV2[0].mo_contact_no;
  //         this.bdAmt = this.rrpiiV2[0].bd_amt;
  //         this.bdNo = this.rrpiiV2[0].bd_no;
  //         this.bdDate = this.rrpiiV2[0].bd_date;
  //         this.bdBankNm = this.rrpiiV2[0].bd_bank_nm;
  //         this.cheBaAcctNo = this.rrpiiV2[0].che_ba_acct_no;
  //         this.cheId = this.rrpiiV2[0].che_id;
  //         this.transTrace = this.rrpiiV2[0].trans_trace;
  //         this.batchNo = this.rrpiiV2[0].batch_no;
  //         this.hostNo = this.rrpiiV2[0].host_no;
  //         this.tId = this.rrpiiV2[0].t_id;
  //         this.amt = this.rrpiiV2[0].amt;
  //         this.total = this.rrpiiV2[0].total;


  //         this.isLoading = false;
  //       }
  //     },
  //     (error) => {
  //       console.error(error);
  //       this.isLoading = false;
  //       this.isLoadingPaymentInfo = false;
  //       // Handle errors here
  //     }
  //   );

  // }




  viewSelected() {
    this.ReceiptWatermark();
    this.router.navigate(['/reprintreceiptjustification'], { state: { mtt_id: this.mttId, otc_id: this.otcId, otc_rc_rp_id: this.otcRcRpId, otc_rcpt_id: this.otcRcptId, otc_pymt_mode: this.otcPymtMode, rcpt_no: this.rcptNo } });
    console.log("mttid=" + this.mttId, "otcid=" + this.otcId);
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
    console.log('mttId', this.mttId)


    this.http.post(url, Body, { headers }).subscribe(
      (response: any) => {
        if (response.data.length === 0) {
          this.totalRecords = 0;
          this.isEmptyResult = false;
        } else {
          this.totalRecords = response.data[0].total;
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
      },
      (error) => {
        console.error(error);
        this.isLoading = false;
        // Handle errors here
      }
    );

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

  // UpdateReceiptJust(): void {
  //   const url = environment.apiUrl + '/api/rr/v1/updrcptjust_rr';

  //   const headers = new HttpHeaders({
  //     Authorization: environment.authKey,
  //     'Content-Type': 'application/json',
  //   });

  //   const body: any = {
  //     i_otc_id: this.otcId,
  //     i_otc_rc_rp_id: this.otcRcRpId,
  //     i_otc_rcpt_id: this.otcRcptId,
  //     i_modified_by: 'system'

  //   };
    
  //   console.log(body);

  //   try {
  //     this.http
  //       .post(url, body, { headers })
  //       .toPromise()
  //       .then((response) => {
  //         console.log('Success response:', response);
  //       })
  //       .catch((error) => {
  //         console.error('Error:', error);
  //       });
  //     console.log('Success response:', body);
  //   } catch (error) {
  //     console.error(error);
  //   }
  // }

  // populateFormHistoryTable() {

  //   this.mttId = this.mttId;
  //   this.isDisplay = true;
  //   this.isLoading = true;



  //   const url = environment.apiUrl + '/api/rr/v1/historytable_rr';

  //   // Set your authorization header
  //   const headers = new HttpHeaders({
  //     Authorization: environment.authKey,
  //     'Content-Type': 'application/json',
  //   });

  //   const Body: any = {

  //     i_mtt_id: this.mttId
  //   };

  //   this.http.post(url, Body, { headers }).subscribe(
  //     (response: any) => {
  //       if (response.data.length === 0) {
  //         this.totalRecords = 0;
  //         this.isEmptyResult = false;
  //         this.totalHistoryRecords = 0;
  //       } else {
  //         this.totalRecords = response.data[0].total;
  //         this.totalHistoryRecords = response.data[0].total;
  //         this.rrht = response.data;
  //         this.mttId = this.rrht[0].mtt_id;
  //         this.isEmptyResult = false;

  //         // mtt_id:string;
  //         // rcpt_no:string;
  //         // file_nm:string;
  //         // rcpt_dt:string;
  //         // rcpt_status:string;
  //         // rcpt_reprint:number;

  //         this.action = this.rrht[0].action;
  //         this.dtAction = this.rrht[0].dt_action;
  //         this.otcStatus = this.rrht[0].otc_status;
  //         this.counterId = this.rrht[0].counter_id;
  //         this.actBy = this.rrht[0].act_by;

  //         this.status = this.rrht[0].status;
  //         this.isDisplayHist = true;





  //         this.isLoading = false;
  //       }
  //     },
  //     (error) => {
  //       console.error(error);
  //       this.isLoading = false;
  //       // Handle errors here
  //     }
  //   );

  // }

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
          console.error('Invalid otc receipt history table details response format:', response);
        }
        else {
          this.totalHistoryRecords = response.data[0].total;
          this.rrhtV2 = response.data;
          this.isDisplayHist = true;
          this.isLoading = false;
        }
      },
      (error) => {
        console.error('There was an error retrieving the history table:', error);
        this.isLoading = false;
      }
    );
  }

  downloadFile(idaman_file_name: string): void {
    const url = environment.apiUrl + '/api/rr/v1/receiptinfo_rr';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    const Body: any = {
      i_mtt_id: this.mttId
    };

    this.http.post(url, Body, { headers }).subscribe(
      (response: any) => {
        // console.log(response.data);
        this.file_content = response.data;
        console.log('Content: ' + this.fileContent);
        console.log('File Name: ' + this.idamanFileName);
        if (this.idamanFileName != "" && this.fileContent != null) {
          this.downloadFileContent(idaman_file_name, this.fileContent);
        }

        if (response.data.length == 0) {
          this.totalRecords = 0;
          //this.showResultAlertBox();
          this.isLoading = false;
        } else {
          this.totalRecords = response.data[0].total;
          // this.AlertBoxInitialize();
          // this.DefaultBox();
          this.isLoading = false;
          this.isDisplay = true;
        }
        // console.log(response.data);
        //  console.log(this.totalRecords);
      },
      (error) => {
        console.error(error);
        this.isLoading = false;

        //this.showGenericAlertBox();
      }
    );
  }

  downloadFileContent(fileName: string, fileContent: string): void {
    this.isLoading = true;
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

  getTotalCheckAmountPaymentInfo(): number {
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




}
