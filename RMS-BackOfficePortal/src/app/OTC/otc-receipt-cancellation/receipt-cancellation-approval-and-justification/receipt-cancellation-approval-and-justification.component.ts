import { Component, OnInit } from '@angular/core';
import { FeeGroup } from '../../../core/models/fee-group';
import { MFTWFDoc, SourceSystemCode, User } from '../../../core/models/entity';
import { ActivatedRoute } from '@angular/router';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { environment } from 'src/environments/environment';
import { MatDialog } from '@angular/material/dialog';
import { EMPTY, Observable, map } from 'rxjs';
import { forkJoin, of } from 'rxjs';
import { DataService } from '../../../core/services/data.service';
import { Systemstatus } from '../../../shared/enums/systemstatus';
import { NgForm } from '@angular/forms';
import { Router } from '@angular/router';
import { ParamService } from '../../../core/services/param.service';
import { fadeInOut } from '../../../shared/animation';
import { TranslateService } from '@ngx-translate/core';
import { GlobalService } from 'src/app/shared/global.service';
import { AuthService } from 'src/app/core/services/auth.service';
import { perm } from 'src/permissions/perm';
import { NgbPaginationConfig } from '@ng-bootstrap/ng-bootstrap';
import { OTCReceiptCancellationHistoryDetails, OTCReceiptCancellationHistoryDetailsAudit, OTCReceiptCancellationOrderInfoDetails, OTCReceiptCancellationPaymentInfoDetails, OTCReceiptCancellationPaymentItemsDetails, OTCReceiptCancellationRecepitInfoDetails, OTCReceiptCancellationTaskAndReqInfoApproval } from 'src/app/core/models/otc-receipt-cancellation.interface';
import { CounterCheckInStatus } from 'src/app/core/services/otc-counter-status.service';


@Component({
  selector: 'app-receipt-cancellation-approval-and-justification',
  templateUrl: './receipt-cancellation-approval-and-justification.component.html',
  styleUrls: ['./receipt-cancellation-approval-and-justification.component.scss']
})
export class ReceiptCancellationApprovalAndJustificationComponent implements OnInit {

  decision: string = "";
  decisionOption: any[] = [];
  error: boolean = false;
  errorMessages: string[] = [];
  histOTCStatus: any[] = [];
  states: any[] = [];

  taskID: string | null = null;
  taskDescription: number | null = null;
  otcID: number | null = null;
  status: string | null = null;
  refNo: string | null = null;
  assignedDate: Date | null = null;
  nm_en: string | null = null;

  counterID: string | null = null;
  displayCounterIDFromTable: string | null = null;
  requestedBy: string | null = null;
  requestedByNm: string | null = null;
  requesterID: string | null = null;
  dateRequested: Date | null = null;

  justification: string = "";
  justificationOption: any[] = [];
  others: string | null = null;
  payerEmail: string | null = null;//payment info
  OTCPaymentMode: string | null = null;
  cashAmount: number | null = null;
  chequeNo: string | null = null;
  chequeBankAccountNo: string | null = null;
  OTCChequeId: number | null = null;
  chequeAmount: number | null = null;
  chequeDate: Date | null = null;
  chequeBankName: string | null = null;
  chequePayerName: string | null = null;

  sourceSystem: string | null = null;
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



  pagePaymentItem = environment.DefaultPage;
  itemsPerPagePaymentItem = environment.ItemPerPage;
  pagePaymentInfo = environment.DefaultPage;
  itemsPerPagePaymentInfo = environment.ItemPerPage;
  pageReceiptInfo = environment.DefaultPage;
  itemsPerPageReceiptInfo = environment.ItemPerPage;
  pageHistory = environment.DefaultPage;
  itemsPerPageHistory = environment.ItemPerPage;

  mttId: number | null = null;
  otcrcID: number | null = null;
  rcType: number | null = null;
  otcCounterId: number | null = null;
  isLoadingTaskAndRequesterInfo: boolean = false;
  isLoadingOrderInfo: boolean = false;
  isLoadingPaymentItems: boolean = false;
  isLoadingPaymentInfo: boolean = false;
  isLoadingReceiptInfo: boolean = false;
  isLoadingHistory: boolean = false;
  isCashAmountCheckingFinishLoading: boolean = false; //to check if cash is 0 or not to determine the approve type
  isSubmitLoading: boolean = false;
  displayOthers: boolean = false;
  toUpdateMTT: boolean = false;

  totalReceiptInfoRecords: number = 0;
  totalHistoryRecords: number = 0;
  totalPaymentItemsRecords: number = 0;
  isDisplayReceiptInfo: boolean = false;
  isDisplayHist: boolean = false;
  OTCCheckedIn: number = 0;

  OTCReceiptCancellationOrderInfoDetails: OTCReceiptCancellationOrderInfoDetails[] = [];
  OTCReceiptCancellationPaymentItemsDetails: OTCReceiptCancellationPaymentItemsDetails[] = [];
  OTCReceiptCancellationPaymentInfoDetails: OTCReceiptCancellationPaymentInfoDetails[] = [];
  OTCReceiptCancellationReceiptInfoDetails: OTCReceiptCancellationRecepitInfoDetails[] = [];
  OTCReceiptCancellationHistoryDetails: OTCReceiptCancellationHistoryDetails[] = [];
  OTCReceiptCancellationTaskAndReqInfoApproval: OTCReceiptCancellationTaskAndReqInfoApproval[] = [];
  OTCReceiptCancellationHistoryDetailsAudit: OTCReceiptCancellationHistoryDetailsAudit[] = [];

  // Configuring Permissions for User and roles variables
  permOTC = perm.OTC_Receipt_Cancellation_Cancellation_Approval
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
  ) {
    config.maxSize = environment.PaginationMaxSize;
    config.boundaryLinks = true;
    this.translateService.setDefaultLang(this.globalService.getGlobalValue());
    this.translateService.use(this.globalService.getGlobalValue());
  }

  ngOnInit() {

    this.mttId = history.state.mtt_id;
    this.otcrcID = history.state.otc_rc_id;
    this.rcType = history.state.rc_type;

    console.log('This otcrcID is ' + this.otcrcID);
    // this.otcCounterId = history.state.otc_counter_id; //can get from query no need pass
    //this.cashAmount = history.state.cash_amt;
    this.justificationOption = [
      { label: 'Customer Request', value: 'Customer Request' },//Query Pending Requester
      { label: 'Incorrect Payment Amount', value: 'Incorrect Payment Amount' }, //Query Pending Requester HOD
      { label: 'Incorrect Payment Item', value: 'Incorrect Payment Item' }, //Query Pending Finance Admin
      { label: 'Others', value: 'Others' }]
    console.log('This rc type is ' + this.rcType);
    this.updateDecisionOptions(); //is ok to load here because when select the choice, the value will be overwrite
    this.loadCounterInfo();
    this.loadTaskAndRequesterInfo();
    this.loadOrderInfo();
    this.loadPaymentItems();
    this.loadPaymentInfo();
    this.loadReceiptInfo();
    //this.loadHistory();
    this.loadHistoryAudit();
    this.loadOTCStatus();
    this.loadStates();
  }




  updateDecisionOptions() {
    if (this.rcType === 1) {
      this.decisionOption = [
        { label: 'Approve', value: 'PEMV' },  //emv
        { label: 'Reject', value: 'R' }
      ];
    } else {
      if (this.cashAmount != null) { // Checks for both null and undefined
        if (this.cashAmount > 0) {
          this.decisionOption = [
            { label: 'Approve', value: 'PCR' },  //Pending Cash Return
            { label: 'Reject', value: 'R' }
          ];
        }
      }
      else { //mean cash amount is 0 therefore straight success
        this.decisionOption = [
          { label: 'Approve', value: 'A' },
          { label: 'Reject', value: 'R' }
        ];
      }

    }
  }

  onDecisionChange() {
    this.updateDecisionOptions()
  }

  onJustificationChange() {
    if (this.justification === "Others") {
      this.displayOthers = true;
    }
    else {
      this.displayOthers = false;
    }
  }

  cancel() {
    this.router.navigate(['/my-task-assigned-tasks']);
  }

  loadTaskAndRequesterInfo() {
    this.authService.checkUserRole(this.authService.username, this.permOTC)
      .subscribe(
        (response: any) => {
          this.permOTCAllow = response.data;
          this.permListAllow = this.permOTCAllow.includes(perm.OTC_Receipt_Cancellation_Cancellation_Approval) ? 1 : 0;

          console.log(this.permListAllow,);
          if (this.permListAllow === 0) {
            console.log("access-denied");
            this.router.navigate(['/access-denied']);
            return; // Exit the function to prevent further execution
          }

          this.isLoadingTaskAndRequesterInfo = true;

          const urlMftWFHis = environment.apiUrl + '/api/otcrcptccl/v1/getotcreceiptcancellationtaskandreqinfoapproval';

          // Set your authorization header
          const headers = new HttpHeaders({
            Authorization: environment.authKey,
            'Content-Type': 'application/json'
          });


          // Create the request body with your form data
          const requestBody: any = {
            i_otc_rc_id: this.otcrcID
          };

          this.http.post(urlMftWFHis, requestBody, { headers }).subscribe(
            (response: any) => {

              if (response.data.length === 0) {
                this.isLoadingTaskAndRequesterInfo = false;
                console.error('Invalid otc receipt cancellation order info details response format:', response);
              }
              else {
                this.OTCReceiptCancellationTaskAndReqInfoApproval = response.data;
                this.otcID = this.OTCReceiptCancellationTaskAndReqInfoApproval[0].otc_id;
                this.taskID = this.OTCReceiptCancellationTaskAndReqInfoApproval[0].task_id;
                this.taskDescription = this.OTCReceiptCancellationTaskAndReqInfoApproval[0].rc_type;
                this.status = this.OTCReceiptCancellationTaskAndReqInfoApproval[0].rc_status;
                this.nm_en = this.OTCReceiptCancellationTaskAndReqInfoApproval[0].nm_en;
                this.assignedDate = this.OTCReceiptCancellationTaskAndReqInfoApproval[0].date_assigned;
                // this.counterID = this.OTCReceiptCancellationTaskAndReqInfoApproval[0].counter_id;
                this.displayCounterIDFromTable = this.OTCReceiptCancellationTaskAndReqInfoApproval[0].counter_id;
                this.requestedBy = this.OTCReceiptCancellationTaskAndReqInfoApproval[0].requested_by;
                this.requestedByNm = this.OTCReceiptCancellationTaskAndReqInfoApproval[0].requested_by_nm;
                this.requesterID = this.OTCReceiptCancellationTaskAndReqInfoApproval[0].requester_id;
                this.dateRequested = this.OTCReceiptCancellationTaskAndReqInfoApproval[0].date_requested;
                //.otcCounterId = this.OTCReceiptCancellationTaskAndReqInfoApproval[0].otc_counter_id;
                this.isLoadingTaskAndRequesterInfo = false;
              }

            },
            (error) => {
              console.error('There was an error retrieving the otc receipt cancellation order info details:', error);
              this.isLoadingTaskAndRequesterInfo = false;
            }
          );
        });
  }


  loadOrderInfo() {

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
          this.collectionSlipNo = this.OTCReceiptCancellationOrderInfoDetails[0].coll_slip_no;

          // this.isDisplayHist = true;
          this.isLoadingOrderInfo = false;
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

          console.error('Invalid otc receipt cancellation order info details response format:', response);
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
        console.error('There was an error retrieving the otc receipt cancellation order info details:', error);
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
          // this.isCashAmountCheckingFinishLoading = true; cannot user choose if not data
          console.error('Invalid otc receipt cancellation order info details response format:', response);
        }
        else {
          this.OTCReceiptCancellationPaymentInfoDetails = response.data;
          this.payerEmail = this.OTCReceiptCancellationPaymentInfoDetails[0].payer_email;
          this.OTCPaymentMode = this.OTCReceiptCancellationPaymentInfoDetails[0].otc_pymt_mode;
          //below are comment out because it can have multiple while top one only need display
          console.log('Payment mode is ' + this.OTCPaymentMode);
          this.cashAmount = this.calculateTotalCashAmount();
          // this.updateDecisionOptions();
          this.isCashAmountCheckingFinishLoading = true;
          // this.chequeNo = this.OTCReceiptCancellationPaymentInfoDetails[0].che_no;
          // this.chequeBankAccountNo = this.OTCReceiptCancellationPaymentInfoDetails[0].che_ba_acct_no;
          // this.OTCChequeId = this.OTCReceiptCancellationPaymentInfoDetails[0].otc_che_id;
          // this.chequeAmount = this.OTCReceiptCancellationPaymentInfoDetails[0].che_amt;
          // this.chequeDate = this.OTCReceiptCancellationPaymentInfoDetails[0].che_date;
          // this.chequeBankName = this.OTCReceiptCancellationPaymentInfoDetails[0].che_bank_nm;
          // this.chequePayerName = this.OTCReceiptCancellationPaymentInfoDetails[0].che_payer_nm;

          // this.isDisplayHist = true;
          this.isLoadingPaymentInfo = false;

          //this.totalRecordsHist = response.data[0].total;
        }
        //  console.log("MFTWF is "+this.mftwf[0].fee_detail_id);
        //  console.log(this.totalRecords);

      },
      (error) => {
        console.error('There was an error retrieving the otc receipt cancellation order info details:', error);
        this.isLoadingPaymentInfo = false;
        //  this.isCashAmountCheckingFinishLoading = true; cannot user choose if not data

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
          console.error('Invalid otc receipt cancellation order info details response format:', response);
        }
        else {
          this.totalReceiptInfoRecords = response.data[0].total;
          this.OTCReceiptCancellationReceiptInfoDetails = response.data;
          this.rcptNo = this.OTCReceiptCancellationReceiptInfoDetails[0].rcpt_no;
          this.fileNm = this.OTCReceiptCancellationReceiptInfoDetails[0].file_nm;
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
        console.error('There was an error retrieving the otc receipt cancellation order info details:', error);
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
          console.error('Invalid otc receipt cancellation order info details response format:', response);
        }
        else {
          this.totalHistoryRecords = response.data[0].total;
          this.OTCReceiptCancellationHistoryDetails = response.data;
          this.isDisplayHist = true;
          this.isLoadingHistory = false;
        }
      },
      (error) => {
        console.error('There was an error retrieving the otc receipt cancellation history info details:', error);
        this.isLoadingHistory = false;
      }
    );
  }


  //form handle before submit start
  async handleFormSubmit(form: NgForm) {
    if (form.valid) {
      // const validProceedToSubmit = await this.checkStatusAndAssignTo(); //ensure the mftwf status is updated before submit
      // if (validProceedToSubmit === true) {
      this.submit();
      // }
      // else {
      //console.log("Invalid proceed to submit")
      // }

    } else {
      Object.values(form.controls).forEach((control) => {
        control.markAsTouched();
      });
    }
  }
  //form handle before submit end

  async submit() {
     
    //await this.loadCounterInfo(); //no need because supervisor can approve without counter check in, if he didn't checkin then is null else got value
    // if(this.OTCCheckedIn === 0) {
    //   this.error = true;
    //   this.errorMessages.push('Counter check in required for receipt cancellation');
    //   return;
    // }


    let invalidSecondUpdate = false;

    this.isSubmitLoading = true;
    let invalidUpdate = await this.updateReceiptCancellation(this.decision);

    if (invalidUpdate) {//if true then error
      this.isSubmitLoading = false;
      this.error = true;
      this.errorMessages.push('Submit not successful');
      console.log('First update receipt cancellation failed');
      return;
    }

    if (this.decision === 'A' || this.decision === 'PCR') {//if approve then got second update change status from approved to successful and then update mtt status
      const alert_msg = "approved"
      console.log('This A or PCR decision is ' + this.decision);

      if (this.decision === 'A') {
        invalidSecondUpdate = await this.updateReceiptCancellation('VS'); //hardcode VS here because if approve need to insert successful status
      }

      if (!invalidSecondUpdate) {

        //this.toUpdateMTT = true; //true because need to update mtt status
        let invalidIpdateMTTStatus = await this.updateMTTStatusAndReceipt();
        this.isSubmitLoading = false;

        if (!invalidIpdateMTTStatus) { //success update therefore route
          this.router.navigate(['/my-task-assigned-tasks'], { state: { alert_msg } });
        }
        else { //in case fail to update receipt cancellation
          this.error = true;
          this.errorMessages.push('Submit not successful');
          console.log('Update MTT successful and receipt cancellation failed');
          this.isSubmitLoading = false;
          return;
        }
      }
      else {
        this.error = true;
        this.errorMessages.push('Submit not successful');
        console.log('Second update receipt cancellation failed');
        this.isSubmitLoading = false;
        return;
      }
    }
    else {//means other decision except approve and pending cash return
      console.log('This decision is ' + this.decision);
      let alert_msg = "";
      if (this.decision === 'R') {
        alert_msg = "rejected"
      }
      else {
        alert_msg = "approved" //for pemv
      }
      this.isSubmitLoading = false;
      this.router.navigate(['/my-task-assigned-tasks'], { state: { alert_msg } });
    }
  }




  async updateReceiptCancellation(rcStatus: string): Promise<boolean> {
    console.log('This decision is ' + this.decision);
    //return false
    let assignTo = null;
    let action = null;

    if (this.decision === 'PEMV' || this.decision === 'PCR') { //A means end of flow because straight update mtt
      assignTo = this.requestedBy
    }
    else {
      assignTo = null;
    }

    if (this.decision === 'PEMV' || this.decision === 'PCR') {
      action = 'Request Receipt Cancellation'
    }
    else if (this.decision === 'R' || this.decision === 'A') {
      action = 'Receipt Cancellation'
    }



    const url = environment.apiUrl + '/api/otcrcptccl/v1/updotcreceiptcancellation';

    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json'
    });


    const Body: any = {
      i_otc_rc_id: this.otcrcID,
      i_otc_id: this.otcID,
      i_justication: this.justification,
      i_rc_type: this.rcType,
      i_rc_status: rcStatus,
      i_task_id: this.taskID,
      i_counter_id: this.counterID,
      i_requested_by: this.requestedBy,
      i_requester_id: this.requesterID,
      i_others: this.others,
      // i_approved_by:  backend setter
      //i_approver_id: backend setter
      i_remark: null,
      //i_modified_by backend set value
      i_status: 'A',
      i_assigned_to: assignTo,
      i_action: action,
      i_otc_counter_id: this.otcCounterId,
      i_from_otcsupervisor: 1
    };


    //console.log('Action taken is '+this.decision)

    try {
      const response: any = await this.http.post(url, Body, { headers }).toPromise();
      if (response.header.statusCode === '00') {
        return false; // Update success
      } else {
        this.error = true;
        this.errorMessages.push('Submit not successful');
        return true; // update failed
      }
    } catch (error) {
      this.error = true;
      this.errorMessages.push('Internal Server Error.');
      console.error(error);
      return true; // Error occurred
    }
  }


  async updateMTTStatusAndReceipt(): Promise<boolean> {

    const url = environment.apiUrl + '/api/otcrcptccl/v1/updatemttorderstatusandreceipt';

    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json'
    });


    const Body: any = {
      i_mtt_id: this.mttId,
      i_order_status: 'C',
      // i_update_MTT_status: this.toUpdateMTT,
      i_otc_rc_id: this.otcrcID
      // i_modified_by: 'system'  //backend set value
    };


    try {
      const response: any = await this.http.post(url, Body, { headers }).toPromise();
      if (response.header.statusCode === '00') {
        return false; // Update success
      } else {
        // this.error = true;
        //this.errorMessages.push('Update MTT not successful');
        console.log('Update MTT not successful');
        return true; // update failed
      }
    } catch (error) {
      this.error = true;
      this.errorMessages.push('Internal Server Error.');
      console.error(error);
      return true; // Error occurred
    }


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
          console.error('Invalid otc receipt cancellation order info details response format:', response);
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
    this.http.post(permUrl, requestBody, { headers }).subscribe(
      (response: any) => {
        this.counterCheckInStatus.data = response.data;
        if (this.counterCheckInStatus.data.counter_id.length > 0) {
          //this.counterTitle = 'Counter ID: ' + this.counterCheckInStatus.data.counter_id + ' | ';
          this.otcCounterId = this.counterCheckInStatus.data.otc_counter_id;
          this.counterID = this.counterCheckInStatus.data.counter_id;
          this.OTCCheckedIn = 1;
          console.log('Counter ID: ' + this.counterCheckInStatus.data.counter_id);
          console.log('OTC Counter ID: ' + this.counterCheckInStatus.data.otc_counter_id);
        }
      },
      (error) => {
        console.log(error);
        this.counterCheckInStatus.data = ''; //still update something to push the observer
        this.OTCCheckedIn = 0;
      }
    );
  }
  

}