import { formatDate, Location  } from '@angular/common';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { NavigationEnd, Router } from '@angular/router';
import { NgbPaginationConfig } from '@ng-bootstrap/ng-bootstrap';
import { PGReconList } from 'src/app/core/models/pg-recon';
import { ParamData } from 'src/app/core/models/param.interface';
import { Systemstatus } from 'src/app/shared/enums/systemstatus';
import { environment } from 'src/environments/environment';
import { ParamService } from '../../../core/services/param.service';
import { GlobalService } from 'src/app/shared/global.service';
import { TranslateService } from '@ngx-translate/core';
import { perm } from 'src/permissions/perm';
import { AuthService } from 'src/app/core/services/auth.service';
import { OTCReceiptCancellationBalStatusDetails, OTCReceiptCancellationHistoryDetails, OTCReceiptCancellationHistoryDetailsAudit, OTCReceiptCancellationOrderInfoDetails, OTCReceiptCancellationPaymentInfoDetails, OTCReceiptCancellationPaymentItemsDetails, OTCReceiptCancellationRecepitInfoDetails, OTCReceiptCancellationSupervisor, OTCReceiptCancellatioRCStatusDetails } from 'src/app/core/models/otc-receipt-cancellation.interface';
import { MTTItemDetails } from 'src/app/core/models/mtt-details.interface';
import { User } from 'src/app/core/models/entity';
import { CancelTaskComponent } from '../cancel-task/cancel-task.component';
import { filter, lastValueFrom } from 'rxjs';
import { CounterCheckInStatus } from 'src/app/core/services/otc-counter-status.service';


@Component({
  selector: 'app-otc-receipt-details',
  templateUrl: './otc-receipt-details.component.html',
  styleUrls: ['./otc-receipt-details.component.scss']
})
export class OtcReceiptDetailsComponent implements OnInit {

  userHigherOfficialRole = "OTCSUPERVISOR";
  users: User[] = [];

  error: boolean = false;
  errorMessages: string[] = [];
  receiptDate = null; //order info
  isDisplayReport: boolean = false;
  isLoadingReport: boolean = false;
  sourceSystem: string | null = null;
  orderStatus: string | null = null;
  collectionSlipNo: string | null = null;
  orderRefNo: string | null = null;
  customerName: string | null = null;
  customerPhone: string | null = null;
  customerEmail: string | null = null;
  addressLine1: string | null = null;
  addressLine2: string | null = null;
  addressLine3: string | null = null;
  postCode: string | null = null;
  city: string | null = null;
  state: string | null = null;
  itemDescription: string | null = null; //payment item
  quantity: number | null = null;
  amount: number | null = null;
  tax: number | null = null;
  incentiveCode: string | null = null;
  discount: number | null = null;
  grossAmount: number | null = null;
  payerEmail: string | null = null;//payment info
  OTCPaymentMode: string | null = null;
  cashAmount: number = 0;
  chequeNo: string | null = null;
  chequeBankAccountNo: string | null = null;
  OTCChequeId: number | null = null;
  chequeAmount: number | null = null;
  chequeDate: Date | null = null;
  chequeBankName: string | null = null;
  chequePayerName: string | null = null;
  alertMessage: string | undefined = undefined;
  OTCSupervisorssm4uuserrefno: string | null = null;
  OTCSupervisorEmail: string | null = null;

  approvedBy: string | null = null;
  approverId: string | null = null;
  counterId: string | null = null;
  otcCounterId: number | null = null;
  rcStatus: string | null = null;
  histOTCStatus: any[] = [];
  states: any[] = [];

  //receipt info
  rcptNo: string | null = null
  fileNm: string | null = null
  rcptDt: Date | null = null
  rcptStatus: string | null = null
  rcptReprint: number | null = null
  ssdocrefId: string | null = null
  verId: string | null = null
  ornNo: string | null = null
  fileContent: string | null = null;
  fileType: string | null = null;
  idamanFileName: string | null = null;
  file_content: string | null = null;

  mttId: number | null = null;
  otcId: number | null = null;
  OTCReceiptCancellationOrderInfoDetails: OTCReceiptCancellationOrderInfoDetails[] = [];
  OTCReceiptCancellationPaymentItemsDetails: OTCReceiptCancellationPaymentItemsDetails[] = [];
  OTCReceiptCancellationPaymentInfoDetails: OTCReceiptCancellationPaymentInfoDetails[] = [];
  OTCReceiptCancellationReceiptInfoDetails: OTCReceiptCancellationRecepitInfoDetails[] = [];
  OTCReceiptCancellationHistoryDetails: OTCReceiptCancellationHistoryDetails[] = [];
  OTCReceiptCancellationBalStatusDetails: OTCReceiptCancellationBalStatusDetails[] = [];
  OTCReceiptCancellatioRCStatusDetails: OTCReceiptCancellatioRCStatusDetails[] = [];
  OTCReceiptCancellationHistoryDetailsAudit: OTCReceiptCancellationHistoryDetailsAudit[] = [];
  OTCReceiptCancellationSupervisor: OTCReceiptCancellationSupervisor[] = [];

  pagePaymentItem = environment.DefaultPage;
  itemsPerPagePaymentItem = environment.ItemPerPage;
  pagePaymentInfo = environment.DefaultPage;
  itemsPerPagePaymentInfo = environment.ItemPerPage;
  pageReceiptInfo = environment.DefaultPage;
  itemsPerPageReceiptInfo = environment.ItemPerPage;
  pageHistory = environment.DefaultPage;
  itemsPerPageHistory = environment.ItemPerPage;
  isLoadingOrderInfo: boolean = false;
  isLoadingPaymentItems: boolean = false;
  isLoadingPaymentInfo: boolean = false;
  isLoadingReceiptInfo: boolean = false;
  isLoadingHistory: boolean = false;
  //isOTCPaymentModeFinishLoading: boolean = false;
  isCancelReceiptLoading: boolean = false;
  isOrderInfoFinishLoading: boolean = false; //needed to get the order status value

  totalReceiptInfoRecords: number = 0;
  totalHistoryRecords: number = 0;
  totalPaymentItemsRecords: number = 0;
  isDisplayReceiptInfo: boolean = false;
  isDisplayHist: boolean = false;
  OTCCheckedIn: number = 0;
  rcTypeFromOTCPaymentMode: number = 0; //1=EV, 2=PHYSICAL, 3=CA
  otcCounterIdWherePaymentMake: number = 0; //otc counter id from mtt table
  allowCancel: boolean = true;

  // Configuring Permissions for User and roles variables
  permOTC = perm.OTC_Receipt_Cancellation_View_Details
  permOTCAllow = ""; // variable to store allowed permission for the user
  permListAllow: number = 0; // if 0 then not allow to view listing page, else allow



  constructor(
    private http: HttpClient,
    config: NgbPaginationConfig,
    private router: Router,
    public dialog: MatDialog,
    private ParamService: ParamService,
    private translateService: TranslateService,
    private globalService: GlobalService,
    private authService: AuthService,
    public counterCheckInStatus: CounterCheckInStatus,
    private location: Location
  ) {
    config.maxSize = environment.PaginationMaxSize;
    config.boundaryLinks = true;
    this.translateService.setDefaultLang(this.globalService.getGlobalValue());
    this.translateService.use(this.globalService.getGlobalValue());
  }


  ngOnInit(): void {

    this.mttId = history.state.mtt_id;
    this.otcId = history.state.otc_id;
    this.otcCounterIdWherePaymentMake = history.state.otc_counter_id;
    //this.counterId = history.state.counter_id;
    this.OTCPaymentMode = history.state.otc_pymt_mode;

    if (this.OTCPaymentMode === 'EV') {
      this.rcTypeFromOTCPaymentMode = 1;
    }
    else if (this.OTCPaymentMode === 'CA') {
      this.rcTypeFromOTCPaymentMode = 3;
    }
    else {//physical
      this.rcTypeFromOTCPaymentMode = 2;
    }

    console.log("MTTID is " + this.mttId);

    //this.loadCounterInfo();
    this.loadOrderInfo();
    this.loadPaymentItems();
    this.loadPaymentInfo();
    this.loadReceiptInfo();
    //this.loadHistory();
    this.loadHistoryAudit();
    this.getRCStatus(true);
    this.loadOTCStatus();
    this.loadStates();
  }



  loadOrderInfo() {
    this.authService.checkUserRole(this.authService.username, this.permOTC)
      .subscribe(
        (response: any) => {
          this.permOTCAllow = response.data;
          this.permListAllow = this.permOTCAllow.includes(perm.OTC_Receipt_Cancellation_View_Details) ? 1 : 0;

          console.log(this.permListAllow,);
          if (this.permListAllow === 0) {
            console.log("access-denied");
            this.router.navigate(['/access-denied']);
            return; // Exit the function to prevent further execution
          }

          this.isLoadingOrderInfo = true;

          const urlMftWFHis = environment.apiUrl + '/api/otcrcptccl/v1/getotcreceiptcancellationorderinfodetails';

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
                this.isLoadingOrderInfo = false;
                // this.totalRecordsHist = 0;
                console.error('Invalid otc receipt cancellation order info details response format:', response);
              }
              else {
                this.OTCReceiptCancellationOrderInfoDetails = response.data;
                this.sourceSystem = this.OTCReceiptCancellationOrderInfoDetails[0].ss_cd;
                this.orderRefNo = this.OTCReceiptCancellationOrderInfoDetails[0].orn_no;
                this.customerName = this.OTCReceiptCancellationOrderInfoDetails[0].cust_nm;
                this.addressLine1 = this.OTCReceiptCancellationOrderInfoDetails[0].cust_addr_1;
                this.addressLine2 = this.OTCReceiptCancellationOrderInfoDetails[0].cust_addr_2;
                this.addressLine3 = this.OTCReceiptCancellationOrderInfoDetails[0].cust_addr_3;
                this.postCode = this.OTCReceiptCancellationOrderInfoDetails[0].cust_postcode;
                this.city = this.OTCReceiptCancellationOrderInfoDetails[0].cust_city;
                this.state = this.OTCReceiptCancellationOrderInfoDetails[0].cust_state;
                this.customerEmail = this.OTCReceiptCancellationOrderInfoDetails[0].cust_email;
                this.customerPhone = this.OTCReceiptCancellationOrderInfoDetails[0].cust_phone;
                this.orderStatus = this.OTCReceiptCancellationOrderInfoDetails[0].order_status;
                this.collectionSlipNo = this.OTCReceiptCancellationOrderInfoDetails[0].coll_slip_no;

                // this.isDisplayHist = true;
                this.isLoadingOrderInfo = false;
                this.isOrderInfoFinishLoading = true;
                //this.totalRecordsHist = response.data[0].total;
              }
              //  console.log("MFTWF is "+this.mftwf[0].fee_detail_id);
              //  console.log(this.totalRecords);

            },
            (error) => {
              console.error('There was an error retrieving the otc receipt cancellation order info details:', error);
              this.isLoadingOrderInfo = false;
            }
          );
        });
  }

  loadPaymentItems() {

    this.isLoadingPaymentItems = true;

    const urlMftWFHis = environment.apiUrl + '/api/otcrcptccl/v1/getpymtitems';

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
          this.totalPaymentItemsRecords = 0;
          this.isLoadingPaymentItems = false;
          // this.totalRecordsHist = 0;
          console.error('Invalid otc receipt cancellation payment info details response format:', response);
        }
        else {
          this.totalPaymentItemsRecords = response.data[0].total;
          this.OTCReceiptCancellationPaymentItemsDetails = response.data;
          // this.itemDescription = this.OTCReceiptCancellationPaymentItemsDetails[0].item_desc;
          // this.quantity = this.OTCReceiptCancellationPaymentItemsDetails[0].qty;
          // this.amount = this.OTCReceiptCancellationPaymentItemsDetails[0].unit_fee;
          // this.tax = this.OTCReceiptCancellationPaymentItemsDetails[0].tax_amt;
          // this.incentiveCode = this.OTCReceiptCancellationPaymentItemsDetails[0].grant_cd;
          // this.discount = this.OTCReceiptCancellationPaymentItemsDetails[0].disc_amt;
          // this.grossAmount = this.OTCReceiptCancellationPaymentItemsDetails[0].gross_amt;

          // this.isDisplayHist = true;
          this.isLoadingPaymentItems = false;
          //this.totalRecordsHist = response.data[0].total;
        }
        //  console.log("MFTWF is "+this.mftwf[0].fee_detail_id);
        //  console.log(this.totalRecords);

      },
      (error) => {
        console.error('There was an error retrieving the otc receipt cancellation payment item details:', error);
        this.isLoadingPaymentItems = false;
      }
    );
  }

  loadPaymentInfo() {

    this.isLoadingPaymentInfo = true;

    const urlMftWFHis = environment.apiUrl + '/api/otcrcptccl/v1/getotcreceiptcancellationpaymentinfodetails';

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
          this.OTCReceiptCancellationPaymentInfoDetails = response.data;
          this.payerEmail = this.OTCReceiptCancellationPaymentInfoDetails[0].payer_email;
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

  calculateTotalCashAmount(): number {
    // Calculate the sum of cash_amt, defaulting to 0 if null or undefined
    return this.OTCReceiptCancellationPaymentInfoDetails.reduce((sum, item) => {
      return sum + (item.cash_amt || 0); // Default to 0 if cash_amt is null or undefined
    }, 0);
  }

  loadReceiptInfo() {

    this.isLoadingReceiptInfo = true;

    const urlMftWFHis = environment.apiUrl + '/api/otcrcptccl/v1/getotcreceiptcancellationreceiptinfodetails';

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
          this.isDisplayReceiptInfo = false;
          this.isLoadingReceiptInfo = false;
          this.totalReceiptInfoRecords = 0;
          console.error('Invalid otc receipt cancellation receipt info details response format:', response);
        }
        else {
          this.totalReceiptInfoRecords = response.data[0].total;
          this.OTCReceiptCancellationReceiptInfoDetails = response.data;
          this.rcptNo = this.OTCReceiptCancellationReceiptInfoDetails[0].rcpt_no;
          this.fileNm = this.OTCReceiptCancellationReceiptInfoDetails[0].file_nm;
          console.log("fileNm is " + this.fileNm);
          console.log("File_nm is " + this.OTCReceiptCancellationReceiptInfoDetails[0].file_nm);
          this.rcptDt = this.OTCReceiptCancellationReceiptInfoDetails[0].rcpt_dt;
          this.rcptStatus = this.OTCReceiptCancellationReceiptInfoDetails[0].rcpt_status;
          this.rcptReprint = this.OTCReceiptCancellationReceiptInfoDetails[0].rcpt_reprint;
          this.ssdocrefId = this.OTCReceiptCancellationReceiptInfoDetails[0].ssdocref_id;
          this.verId = this.OTCReceiptCancellationReceiptInfoDetails[0].ver_id;
          this.ornNo = this.OTCReceiptCancellationReceiptInfoDetails[0].orn_no;
          this.fileContent = this.OTCReceiptCancellationReceiptInfoDetails[0].file_content;
          this.idamanFileName = this.OTCReceiptCancellationReceiptInfoDetails[0].idaman_file_name;
          this.fileType = this.OTCReceiptCancellationReceiptInfoDetails[0].file_type;


          this.isDisplayReceiptInfo = true;
          this.isLoadingReceiptInfo = false;
          //this.totalRecordsHist = response.data[0].total;
        }
        //  console.log("MFTWF is "+this.mftwf[0].fee_detail_id);
        //  console.log(this.totalRecords);

      },
      (error) => {
        console.error('There was an error retrieving the otc receipt cancellation receipt info details:', error);
        this.isLoadingReceiptInfo = false;
      }
    );
  }

  //downalod start
  downloadFile(idaman_file_name: string): void {
    const url = environment.apiUrl + '/api/otcrcptccl/v1/getotcreceiptcancellationreceiptinfodetails';

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
          //  this.totalRecords = 0;
          //this.showResultAlertBox();
          //  this.isLoading = false;
        } else {
          // this.totalRecords = response.data[0].total;
          // this.AlertBoxInitialize();
          // this.DefaultBox();
          //  this.isLoading = false;
          // this.isDisplay = true;
        }
        // console.log(response.data);
        //  console.log(this.totalRecords);
      },
      (error) => {
        console.error(error);
        // this.isLoading = false;

        //this.showGenericAlertBox();
      }
    );
  }

  downloadFileContent(fileName: string, fileContent: string): void {
    // this.isLoading = true;
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

  //download end

  loadHistory() {

    this.isLoadingHistory = true;

    const urlMftWFHis = environment.apiUrl + '/api/otcrcptccl/v1/getotcreceiptcancellationhistorydetails';

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
          this.isLoadingHistory = false;
          this.totalHistoryRecords = 0;
          console.error('Invalid otc receipt cancellation history table details response format:', response);
        }
        else {
          this.totalHistoryRecords = response.data[0].total;
          this.OTCReceiptCancellationHistoryDetails = response.data;
          this.isDisplayHist = true;
          this.isLoadingHistory = false;
        }
      },
      (error) => {
        console.error('There was an error retrieving the history table:', error);
        this.isLoadingHistory = false;
      }
    );
  }


  async cancelReceipt() {

    const dialogRef = this.dialog.open(CancelTaskComponent, {
      width: '20%',
    });

    // Wait for dialogRef.afterClosed() to finish
    const result = await lastValueFrom(dialogRef.afterClosed());

    if (result === 'no' || result === undefined) {
      return; // Stop execution if "No" is clicked
    }

    this.errorMessages = []; // Clear previous error messages
    this.error = false;      // Also reset the error flag if needed

    await this.loadCounterInfo();
    if (this.OTCCheckedIn === 0) {
      console.log('OTC counter not checked in trigger here');
      this.error = true;
      this.errorMessages.push('Counter check is required for receipt cancellation');
      return;
    }

    if (this.verId === null) {
      this.error = true;
      this.errorMessages.push('Receipt failed to upload. Please try again later.');
      return;
    }

    // await this.getOTCSupervisor(); //must put below await this.loadCounterInfo() to get the otc counter id
    // if(this.OTCSupervisorssm4uuserrefno === null || this.OTCSupervisorEmail === null) {
    //   console.log('OTC Supervisor not found trigger here');
    //   this.error = true;
    //   this.errorMessages.push('OTC Supervisor not found');
    //   return;
    // }


    if (this.orderStatus !== 'P') {
      this.error = true;
      this.errorMessages.push('Order status is not paid status');
    }
    else {

      this.isCancelReceiptLoading = true;

      let invalidToCancel = await this.getRCStatus(false);
      if (invalidToCancel === false) {
        console.log('Can cancel, pass rc status check');
        let invalidToContinueCancel = await this.getBalanceStatus();

        if (invalidToContinueCancel === false) {
          console.log('Can cancel, pass balance status check');
          const invalidInsert = await this.submitReceiptCancellation();

          if (invalidInsert === false) {
            this.isCancelReceiptLoading = false;
            this.alertMessage = 'Submitted.';
            const alert_msg = this.alertMessage;
            this.router.navigate(['/rcpt-no-vld'], { state: { alert_msg } });
          }
        }
        else {
          this.isCancelReceiptLoading = false;
          console.log('RMS OTC BAL no need continue');
          return;
        }
      }
      else {
        this.isCancelReceiptLoading = false;
        console.log('Receipt cancellation invalid or in progress');
      }
    }
  }

  async getRCStatus(from_view: boolean): Promise<boolean> { //check if receipt cancellation is in progress or already successfully cancelled

    //let cancellationInProgress: number;

    const url = environment.apiUrl + '/api/otcrcptccl/v1/getotcrcpltoCancel';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json'
    });


    // Create the request body with your form data
    const requestBody: any = {
      i_mtt_id: this.mttId,
    };

    try {
      const response: any = await this.http.post(url, requestBody, { headers }).toPromise();
      if (response.header.statusCode === '00') {
        this.OTCReceiptCancellatioRCStatusDetails = response.data;
        if (this.OTCReceiptCancellatioRCStatusDetails[0].count === 0) { //means no cancellation in progress
          this.allowCancel = true;
          return false;
        }
        else {
          if (this.OTCReceiptCancellatioRCStatusDetails[0].rc_status === 'R' || this.OTCReceiptCancellatioRCStatusDetails[0].rc_status === 'VF'
            || this.OTCReceiptCancellatioRCStatusDetails[0].rc_status === 'N'
          ) { //means got data in otc_rc but got rejected or failed etc and user can cancel again therefore return false
            this.allowCancel = true;
            return false;
          }
          else {
            if (from_view === false) {//from view === false means from cancel button is press, not load from view
              this.error = true;
            }

            if (this.OTCReceiptCancellatioRCStatusDetails[0].rc_status === 'VS') {
              if (from_view === false) {
                this.errorMessages.push('This receipt has already been cancelled.')
              }
              console.log('This receipt has already been cancelled.');
            }
            else {
              if (from_view === false) {
                this.errorMessages.push('Receipt cancellation is in progress.');
              }
              console.log('Receipt cancellation is in progress.');
            }
            this.allowCancel = false;
            return true;
          }
        }
      } else {
        this.error = true;
        this.errorMessages.push('Internal Server Error.');
        console.error('Unable to get data from getotcrcpltoCancel');
        this.allowCancel = false;
        return true;
      }
    } catch (error) {
      this.error = true;
      this.errorMessages.push('Internal Server Error.');
      console.error(error);
      this.allowCancel = false;
      return true; // Error occurred
    }


  }

  async getBalanceStatus(): Promise<boolean> {

    let balType = '';

    if (this.OTCPaymentMode === 'EV') { //emv mode
      balType = 'D';
    }
    else {
      balType = 'BM'; //physical mode
    }

    const url = environment.apiUrl + '/api/otcrcptccl/v1/getotcreceiptcancellationbalancestatusdetails';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json'
    });


    // Create the request body with your form data
    const requestBody: any = {
      i_otc_counter_id: this.otcCounterIdWherePaymentMake,
      i_rc_type: this.rcTypeFromOTCPaymentMode,
    };

    try {
      const response: any = await this.http.post(url, requestBody, { headers }).toPromise();
      if (response.header.statusCode === '00') {
        this.OTCReceiptCancellationBalStatusDetails = response.data;

        if (this.OTCReceiptCancellationBalStatusDetails[0].allow_cancel <= 0) { //means cannot cancel
          this.error = true;
          this.errorMessages.push(this.OTCReceiptCancellationBalStatusDetails[0].error_msg);
          return true;

          // if (balType === 'D' && (this.OTCReceiptCancellationBalStatusDetails[0].bal_status === 'P' || this.OTCReceiptCancellationBalStatusDetails[0].bal_status === 'C')) {
          //   return false;
          // }

          // if (balType === 'BM' && (this.OTCReceiptCancellationBalStatusDetails[0].bal_status === 'P')) {
          //   return false;
          // }

          // if (this.OTCReceiptCancellationBalStatusDetails[0].bal_status === 'IP' && balType === 'D') {
          //   this.error = true;
          //   this.errorMessages.push('Daily Balancing for this transaction is in progress. The receipt can no longer be cancelled.');
          //   return true;
          // }
          // else if (this.OTCReceiptCancellationBalStatusDetails[0].bal_status === 'C' && balType === 'D') {
          //   this.error = true;
          //   this.errorMessages.push('Daily Balancing for this transaction has already been completed. The receipt can no longer be cancelled.');
          //   return true;
          // }
          // else if (this.OTCReceiptCancellationBalStatusDetails[0].bal_status === 'IP' && balType === 'BM') {
          //   this.error = true;
          //   this.errorMessages.push('Master Balancing for this transaction is in progress. The receipt can no longer be cancelled.');
          //   return true;
          // }
          // else if (this.OTCReceiptCancellationBalStatusDetails[0].bal_status === 'C' && balType === 'BM') {
          //   this.error = true;
          //   this.errorMessages.push('Master Balancing for this transaction has already been completed. The receipt can no longer be cancelled.');
          //   return true;
          // }

        }
        else { //can cancel
          return false;
        }
      } else {
        console.error('Unable to get data from getotcreceiptcancellationbalancestatusdetails');
        return true;
      }
    } catch (error) {
      this.error = true;
      this.errorMessages.push('Internal Server Error.');
      console.error(error);
      return true; // Error occurred
    }
  }

  async submitReceiptCancellation(): Promise<boolean> {

    const updmftwfStatusUrl = environment.apiUrl + '/api/otcrcptccl/v1/insertotcreceiptcancellation';

    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json'
    });


    console.log('OTC Payment mode is ' + this.OTCPaymentMode);

    let rcType: number = 0;

    if (this.OTCPaymentMode === 'EV') {
      rcType = 1;
    }
    else if (this.OTCPaymentMode === 'CA') {
      rcType = 3;
    }
    else {//physical
      rcType = 2;
    }

    const Body: any = {

      i_otc_id: this.otcId,
      i_justication: null,
      i_rc_type: rcType,
      i_rc_status: 'PS', //pending otc supervisor
      i_counter_id: this.counterId,
      //i_requsted_by, backend set value
      //i_requster_id, backend set value
      i_others: null,
      i_approved_by: null,
      i_approver_id: null,
      //  i_dt_approved: null, //query settle this
      i_remark: null,
      //i_created_by backend set value
      //i_modified_by backend set value
      i_status: 'A',
      i_assigned_to: null,
      i_action: 'Request Receipt Cancellation',
      i_otc_counter_id: this.otcCounterId,
    };


    //console.log('Action taken is '+this.decision)

    try {
      const response: any = await this.http.post(updmftwfStatusUrl, Body, { headers }).toPromise();
      if (response.header.statusCode === '00') {
        return false; // Update success
      } else {
        this.error = true;
        this.errorMessages.push('Cancel not successful');
        return true; // update failed
      }
    } catch (error) {
      this.error = true;
      this.errorMessages.push('Internal Server Error.');
      console.error(error);
      return true; // Error occurred
    }


  }


  populateAppover() {

    const url = environment.apiUrl + '/api/mft/v1/getuserbyrole';

    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json'
    });


    const requestBody = {
      i_page: 1,
      i_size: 1,
      i_role_nm_en: this.userHigherOfficialRole,
      i_role_nm_bm: this.userHigherOfficialRole,
      i_status: Systemstatus.Active
    };

    this.http.post(url, requestBody, { headers }).subscribe(
      (response: any) => {
        if (response.data.length == 0) {
          console.error('Invalid approver response format:', response);
        }
        else {
          this.users = response.data;
          this.approvedBy = this.users[0].ssm4uuserrefno;
          this.approverId = this.users[0].email;
        }
      },
      (error) => {
        console.error('There was an error retrieving the approver:', error);
        // Handle errors here
      }
    );
  }

  getTotalGrossAmountPaymentItems(): number {
    if (!this.OTCReceiptCancellationPaymentItemsDetails) {
      return 0;
    }
    return this.OTCReceiptCancellationPaymentItemsDetails.reduce((sum, item) => sum + (item.net_amt || 0), 0);
  }

  getTotalCheckAmountPaymentInfo(): number {
    if (!this.OTCReceiptCancellationPaymentInfoDetails) {
      return 0;
    }
    return this.OTCReceiptCancellationPaymentInfoDetails.reduce((sum, item) => sum + (item.che_amt || 0), 0);
  }

  getTotalBankDraftAmountPaymentInfo(): number {
    if (!this.OTCReceiptCancellationPaymentInfoDetails) {
      return 0;
    }
    return this.OTCReceiptCancellationPaymentInfoDetails.reduce((sum, item) => sum + (item.bd_amt || 0), 0);
  }

  getTotalMoneyOrderAmountPaymentInfo(): number {
    if (!this.OTCReceiptCancellationPaymentInfoDetails) {
      return 0;
    }
    return this.OTCReceiptCancellationPaymentInfoDetails.reduce((sum, item) => sum + (item.mo_amt || 0), 0);
  }

  getTotalEMVAmountPaymentInfo(): number {
    if (!this.OTCReceiptCancellationPaymentInfoDetails) {
      return 0;
    }
    return this.OTCReceiptCancellationPaymentInfoDetails.reduce((sum, item) => sum + (item.amt || 0), 0);
  }

  back() {
   // this.router.navigate(['/rcpt-no-vld']);
   this.location.back();

  }

  loadOTCStatus() {
    this.ParamService.getStates(environment.DefaultPage.toString(), environment.ItemPerPage.toString(), '', 'OTC-RC').subscribe((response: any) => {
      if (response.data.length >= 0) {
        // this.states = response.data as ParamData[]; later
        this.histOTCStatus = response.data as any[];
        this.histOTCStatus.sort((a, b) => a.nm_en.localeCompare(b.nm_en)); // sorting
      }
      else
        console.error('Invalid response format:', response);
    },
      (error) => {
        console.error('There was an error retrieving the status:', error);
      }
    );
  }

  getOTCStatusName(status: string | null): string {
    if (!status) {
      return '';
    }
    const statusName = this.histOTCStatus.find((option) => option.param_cd === status);
    return statusName ? statusName.nm_en : status;
  }


  getValidChequePaymentInfoDetails(): any[] {
    return this.OTCReceiptCancellationPaymentInfoDetails?.filter(item => item.che_amt !== null) || [];
  }

  getValidBankDraftPaymentInfoDetails(): any[] {
    return this.OTCReceiptCancellationPaymentInfoDetails?.filter(item => item.bd_amt !== null) || [];
  }

  getValidMOPaymentInfoDetails(): any[] {
    return this.OTCReceiptCancellationPaymentInfoDetails?.filter(item => item.mo_amt !== null) || [];
  }

  getValidEVPaymentInfoDetails(): any[] {
    return this.OTCReceiptCancellationPaymentInfoDetails?.filter(item => item.amt !== null) || [];
  }

  loadStates() {
    this.ParamService.getStates(environment.DefaultPage.toString(), environment.ItemPerPage.toString(), '', 'State').subscribe((response: any) => {
      if (response.data.length >= 0) {
        // this.states = response.data as ParamData[]; later
        this.states = response.data as any[];
        this.states.sort((a, b) => a.nm_en.localeCompare(b.nm_en)); // sorting
      }
      else
        console.error('Invalid response format:', response);
    },
      (error) => {
        console.error('There was an error retrieving the status:', error);
      }
    );
  }

  getStateName(stateDefault: string | null): string {
    if (!stateDefault) {
      return '';
    }
    const stateName = this.states.find((option) => option.param_cd === stateDefault);
    return stateName ? stateName.nm_en : stateDefault;
  }

  loadHistoryAudit() {

    this.isLoadingHistory = true;

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
          this.isLoadingHistory = false;
          this.totalHistoryRecords = 0;
          console.error('Invalid otc receipt cancellation history response format:', response);
        }
        else {
          this.totalHistoryRecords = response.data[0].total;
          this.OTCReceiptCancellationHistoryDetailsAudit = response.data;
          this.isDisplayHist = true;
          this.isLoadingHistory = false;
        }
      },
      (error) => {
        console.error('There was an error retrieving the otc receipt cancellation history info details audit:', error);
        this.isLoadingHistory = false;
      }
    );
  }

  getRcStatusLabel(rc_status: string, rc_type: any): string {
    const status = rc_status?.trim();       // Trim in case of whitespace
    const type = Number(rc_type);           // Convert to number

    if (['PEMV', 'PCR', 'PS', 'PU'].includes(status)) {
      return 'Request Receipt Cancellation';
    }

    if (['R', 'A'].includes(status)) {
      return 'Receipt Cancellation';
    }

    if ((status === 'VS' || status === 'VF') && (type === 1 || type === 2)) {
      return 'Receipt Cancellation';
    }

    if (status === 'VS' && type === 3) {
      return 'Cash Returned';
    }

    if (status === 'VF' && type === 3) {
      return 'Cash Return';
    }

    return ''; // fallback
  }

  async loadCounterInfo() {
    const permUrl = environment.apiUrl + '/api/otc/v1/checkinstatus';
    // Make the HTTP GET request
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json'
    });
    var requestBody: { [k: string]: any } = {
      i_session_id: localStorage.getItem('otcSession')
    };
    try {
      const response: any = await this.http.post(permUrl, requestBody, { headers : headers }).toPromise();
      this.counterCheckInStatus.data = response.data;

      if (this.counterCheckInStatus.data.counter_id?.length > 0) {
        this.otcCounterId = this.counterCheckInStatus.data.otc_counter_id;
        this.counterId = this.counterCheckInStatus.data.counter_id;
        this.OTCCheckedIn = 1;
        console.log('Counter ID: ' + this.counterCheckInStatus.data.counter_id);
        console.log('OTC Counter ID: ' + this.counterCheckInStatus.data.otc_counter_id);
      }
    } catch (error) {
      console.log(error);
      this.counterCheckInStatus.data = ''; // still notify observer
      this.OTCCheckedIn = 0;
    }
  }

  // async getOTCSupervisor() {

  //   if(this.otcCounterId === null){
  //     console.error('OTC Counter ID is null in getOTCSupervisor');
  //     return;
  //   }

  //   const url = environment.apiUrl + '/api/otcrcptccl/v1/getotcreceiptcancellationsupervisor';

  //   // Set your authorization header
  //   const headers = new HttpHeaders({
  //     Authorization: environment.authKey,
  //     'Content-Type': 'application/json'
  //   });


  //   // Create the request body with your form data
  //   const requestBody: any = {
  //     i_otc_counter_id: this.otcCounterId
  //   };

  //   try {
  //     const response: any = await this.http.post(url, requestBody, { headers }).toPromise();
  //     if (response.data.length === 0) {
  //       console.error('Unable to find OTC supervisor:', response);
  //     }
  //     else {
  //       this.OTCReceiptCancellationSupervisor = response.data;
  //       this.OTCSupervisorssm4uuserrefno = this.OTCReceiptCancellationSupervisor[0].ssm4uuserrefno;
  //       this.OTCSupervisorEmail = this.OTCReceiptCancellationSupervisor[0].email;
  //       console.log('OTC Supervisor ID: ' + this.OTCSupervisorssm4uuserrefno);
  //       console.log('OTC Supervisor Email: ' + this.OTCSupervisorEmail);
  //     }
  //   } catch (error) {
  //     console.error('There was an error retrieving the otc supervisor:', error);
  //   }
  //  }

}
