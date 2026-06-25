import { HttpClient, HttpHeaders } from '@angular/common/http';
import { ChangeDetectorRef, Component } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { Router, ActivatedRoute } from '@angular/router';
import { NgbPaginationConfig } from '@ng-bootstrap/ng-bootstrap';
import { TranslateService } from '@ngx-translate/core';
import { AuthService } from 'src/app/core/services/auth.service';
import { GlobalService } from 'src/app/shared/global.service';
import { environment } from 'src/environments/environment';
import { ParamService } from 'src/app/core/services/param.service';
import { OTCCollectionReceiptingBankDraft, OTCCollectionReceiptingCheque, OTCCollectionReceiptingMoneyOrder, OTCCollectionReceiptingPymtItem, OTCHist, OTCPaymentModel, OTCPaymentDetails } from 'src/app/core/models/otc-collection-receipting.interface';
import { OTCBank } from 'src/app/core/models/otc-collection-returned-cheque.interface';
import { CounterCheckInStatus } from 'src/app/core/services/otc-counter-status.service';
import { OtcEmvQueryComponent } from '../otc-emv-query/otc-emv-query.component';

@Component({
  selector: 'app-otc-payment-screen',
  templateUrl: './otc-payment-screen.component.html',
  styleUrls: ['./otc-payment-screen.component.scss']
})
export class OtcPaymentScreenComponent {
  page = environment.DefaultPage;
  itemsPerPage = environment.ItemPerPage;

  coll_slip_no: String | null = null;
  orn_no: String | null = null;
  modelData: any;
  paymentItems: OTCCollectionReceiptingPymtItem[] = [];
  bankmodel: OTCBank[] = [];
  selectedChequeDt: Date[] | null = null;
  selectedBankDraftDt: Date[] | null = null;
  selectedMoneyOrderDt: Date[] | null = null;
  selectedPaymentMode: string = ''; // Tracks the selected payment mode
  chequeModel: OTCCollectionReceiptingCheque[] = [];
  isAddCheque: boolean = true;
  bankDraftModel: OTCCollectionReceiptingBankDraft[] = [];
  isAddBankDraft: boolean = true;
  moneyOrderModel: OTCCollectionReceiptingMoneyOrder[] = [];
  isAddMoneyOrder: boolean = true;
  otcHistModel: OTCHist[] = [];
  totalGrossAmount: number = 0; // Variable to hold the total sum of gross amounts
  totalChequeAmount: number = 0;
  totalBDAmount: number = 0;
  totalMOAmount: number = 0;
  OTCCheckedIn: number = 0;
  username = this.authService.username;

  paymentModel: OTCPaymentModel = {
    payer_email: '',
    pymt_mode: '',
    cash_amt: 0,
    // Initialize other fields here
  };

  isLoading: boolean = false;
  isLoadingPhysical: boolean = false;
  totalRecords: number = 0;

  showResultAlert = false;

  showResultAlertBox() {
    this.showResultAlert = true;
    setTimeout(() => this.showResultAlert = false, 2000);
  }

  showPaymentAlert = false;

  showPaymentAlertBox() {
    this.showPaymentAlert = true;
    setTimeout(() => this.showPaymentAlert = false, 2000);
  }

  showQueryAlert = false;
  queryMessage: string = '';

  showQueryAlertBox(message: string) {
    this.queryMessage = message; // Store the message
    this.showQueryAlert = true;
    setTimeout(() => (this.showQueryAlert = false), 10000);
  }

  constructor(
    private http: HttpClient,
    config: NgbPaginationConfig,
    private router: Router,
    public dialog: MatDialog,
    private ParamService: ParamService,
    private cd: ChangeDetectorRef,
    private route: ActivatedRoute,
    private translate: TranslateService,
    private globalService: GlobalService,
    private authService: AuthService,
    public counterCheckInStatus: CounterCheckInStatus,
  ) {
    config.maxSize = environment.PaginationMaxSize;
    config.boundaryLinks = true;
    this.translate.setDefaultLang(this.globalService.getGlobalValue()); // default language
    this.translate.use(this.globalService.getGlobalValue());
    this.route.queryParams.subscribe(params => {
      this.orn_no = params['orn_no'];
    });

    const navigation = this.router.getCurrentNavigation();
    this.modelData = navigation?.extras.state?.['item']; // Retrieve the passed data

  }

  ngOnInit() {
    //this.selected = new Date();
    this.route.paramMap.subscribe(params => {
      this.coll_slip_no = params.get('coll_slip_no');
    });
    // console.log('Model Data:', this.modelData); // Use the data as needed
    this.fetchPaymentItems();
    this.fetchBanks();
    this.fetchOTCHist();
    this.loadCounterInfo();
  }

  fetchPaymentItems(): void {
    // this.isLoading = true;
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    const url = environment.apiUrl + '/api/OTCCR/v1/getpymtitems'; // API endpoint
    const requestBody = {
      i_coll_slip_no: this.coll_slip_no,
      i_orn_no: this.orn_no,
    };

    this.http.post(url, requestBody, { headers }).subscribe(
      (response: any) => {
        this.paymentItems = response?.data || []; // Store the received data
        this.calculateTotalGrossAmount();
        this.isLoading = false;
      },
      (error) => {
        console.error('Error fetching payment items:', error);
        this.isLoading = false;
      }
    );
  }

  // onCashAmountChange(event: Event): void {
  //   const input = (event.target as HTMLInputElement).value;

  //   // Parse and validate the input value
  //   const parsedValue = parseFloat(input);
  //   this.paymentModel.cash_amt = isNaN(parsedValue) ? 0 : parsedValue;

  //   // Determine payment mode
  //   this.determinePaymentMode();

  //   // Debug log for tracking
  //   console.log('Updated Cash Amount:', this.paymentModel.cash_amt);
  // }

  // onCashAmountChange(event: Event): void {
  //   const input = (event.target as HTMLInputElement).value;

  //   // Parse and validate the input value
  //   let parsedValue = parseFloat(input);

  //   // Ensure the value is greater than 0
  //   if (isNaN(parsedValue) || parsedValue <= 0) {
  //     parsedValue = 0.00; // Set to a minimum positive value (you can adjust this)
  //   }

  //   this.paymentModel.cash_amt = parsedValue;

  //   // Determine payment mode
  //   this.determinePaymentMode();
  // }
  cashAmountError: boolean = false;

  onCashAmountChange(event: Event): void {
    const input = (event.target as HTMLInputElement).value;

    // Parse the input value
    let parsedValue = parseFloat(input);

    // Clear previous error
    this.cashAmountError = false;

    // Ensure the value is greater than 0
    if (isNaN(parsedValue) || parsedValue <= 0) {
      parsedValue = 0.00; // Set to a minimum positive value (you can adjust this)
      return;
    }

    // Check if second decimal digit is 0 or 5
    const decimals = (parsedValue.toFixed(2)).split('.')[1];
    if (decimals[1] !== '0' && decimals[1] !== '5') {
      this.cashAmountError = true;
      return;
    }

    this.paymentModel.cash_amt = parsedValue;

    // Determine payment mode
    this.determinePaymentMode();
  }


  calculateTotalGrossAmount(): void {
    this.totalGrossAmount = this.paymentItems.reduce((sum, item) => sum + item.net_amt, 0);
    // this.totalGrossAmount = 4;
    // if(environment['211'] || environment.production){
    //   this.totalGrossAmount = 4;
    // }
    // else{
    //   this.totalGrossAmount = this.paymentItems.reduce((sum, item) => sum + item.gross_amt, 0);
    // }
  }

  fetchBanks(): void {
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    const url = environment.apiUrl + '/api/rms/v1/getbanks';

    const Body: any = {
    };

    this.http.post(url, Body, { headers }).subscribe(
      (response: any) => {
        this.bankmodel = response.data;
        this.totalRecords = response.data.length > 0 ? response.data[0].total : 0;
        this.isLoading = false;
      },
      (error) => {
        console.error(error);
        this.isLoading = false;
      }
    );

  }

  calculateTotalChequeAmount(): void {
    this.totalChequeAmount = this.chequeModel.reduce((sum, item) => sum + Number(item.che_amt), 0);
  }

  calculateTotalBDAmount(): void {
    this.totalBDAmount = this.bankDraftModel.reduce((sum, item) => sum + Number(item.bd_amt), 0);
  }

  calculateTotalMOAmount(): void {
    this.totalMOAmount = this.moneyOrderModel.reduce((sum, item) => sum + Number(item.mo_amt), 0);
  }

  addCheque() {
    this.chequeModel.push({
      otc_body_id: 0,
      che_bank_nm: '',
      che_no: '',
      che_payer_nm: '',
      che_date: new Date(),
      che_ba_acct_no: '',
      che_amt: 0,
      che_id: '',
      che_status: '',
      isEditable: true,
      isNew: true
    });
    this.isAddCheque = false;
  }

  saveChequeRow(index: number) {
    const row = this.chequeModel[index];
    if (
      !row.che_bank_nm ||
      !row.che_payer_nm ||
      !row.che_no ||
      !row.che_date ||
      !row.che_ba_acct_no ||
      !row.che_amt ||
      row.che_amt <= 0
    ) {
      this.showResultAlertBox();
      // alert("Please ensure all fields are filled and the amount is greater than 0.");
      return; // Exit without saving
    }

    this.chequeModel[index].isEditable = false;
    this.chequeModel[index].isNew = false;
    this.isAddCheque = true;
    this.calculateTotalChequeAmount();
  }

  editChequeRow(index: number) {
    this.chequeModel[index].isEditable = true;
  }

  removeChequeNewRow(index: number) {
    this.chequeModel.splice(index, 1);
    this.calculateTotalChequeAmount();
    this.determinePaymentMode();
    this.isAddCheque = true;
  }

  deleteChequeRow(index: number) {
    this.chequeModel.splice(index, 1); // Remove the row from the array
    this.calculateTotalChequeAmount();
    this.determinePaymentMode();
    if (this.chequeModel.length === 0) {
      this.isAddCheque = true; // If all rows are deleted, show Add button
    }
  }

  addBankDraft() {
    this.bankDraftModel.push({
      bd_bank_nm: '',
      bd_no: '',
      bd_date: new Date(),
      bd_amt: 0,
      isEditable: true,
      isNew: true
    });
    this.isAddBankDraft = false;
  }

  saveBDRow(index: number) {
    const row = this.bankDraftModel[index];
    if (
      !row.bd_bank_nm ||
      !row.bd_no ||
      !row.bd_date ||
      !row.bd_amt ||
      row.bd_amt <= 0
    ) {
      this.showResultAlertBox();
      // alert("Please ensure all fields are filled and the amount is greater than 0.");
      return; // Exit without saving
    }
    this.bankDraftModel[index].isEditable = false;
    this.bankDraftModel[index].isNew = false;
    this.isAddBankDraft = true;
    this.calculateTotalBDAmount();
  }

  editBDRow(index: number) {
    this.bankDraftModel[index].isEditable = true;
  }

  removeBDNewRow(index: number) {
    this.bankDraftModel.splice(index, 1);
    this.calculateTotalBDAmount();
    this.determinePaymentMode();
    this.isAddBankDraft = true;
  }

  deleteBDRow(index: number) {
    this.bankDraftModel.splice(index, 1); // Remove the row from the array
    this.calculateTotalBDAmount();
    this.determinePaymentMode();
    if (this.bankDraftModel.length === 0) {
      this.isAddBankDraft = true; // If all rows are deleted, show Add button
    }
  }

  addMoneyOrder() {
    this.moneyOrderModel.push({
      mo_rm_no: '',
      mo_payer_nm: '',
      mo_id_no: '',
      mo_contact_no: '',
      mo_amt: 0,
      mo_date: new Date(),
      isEditable: true,
      isNew: true
    });
    this.isAddMoneyOrder = false;
  }

  saveMORow(index: number) {
    const row = this.moneyOrderModel[index];
    if (
      !row.mo_rm_no ||
      !row.mo_date ||
      !row.mo_payer_nm ||
      !row.mo_id_no ||
      !row.mo_contact_no ||
      !row.mo_amt ||
      row.mo_amt <= 0
    ) {
      this.showResultAlertBox();
      // alert("Please ensure all fields are filled and the amount is greater than 0.");
      return; // Exit without saving
    }
    this.moneyOrderModel[index].isEditable = false;
    this.moneyOrderModel[index].isNew = false;
    this.isAddMoneyOrder = true;
    this.calculateTotalMOAmount();
  }

  editMORow(index: number) {
    this.moneyOrderModel[index].isEditable = true;
  }

  removeMONewRow(index: number) {
    this.moneyOrderModel.splice(index, 1);
    this.calculateTotalMOAmount();
    this.determinePaymentMode();
    this.isAddMoneyOrder = true;
  }

  deleteMORow(index: number) {
    this.moneyOrderModel.splice(index, 1); // Remove the row from the array
    this.calculateTotalMOAmount();
    this.determinePaymentMode();
    if (this.moneyOrderModel.length === 0) {
      this.isAddMoneyOrder = true; // If all rows are deleted, show Add button
    }
  }

  determinePaymentMode() {
    let hasCash = this.paymentModel.cash_amt && this.paymentModel.cash_amt > 0;
    let hasCheque = this.chequeModel && this.chequeModel.length > 0 && this.chequeModel.some(c => c.che_amt && c.che_amt > 0);
    let hasBankDraft = this.bankDraftModel && this.bankDraftModel.length > 0 && this.bankDraftModel.some(bd => bd.bd_amt && bd.bd_amt > 0);
    let hasMoneyOrder = this.moneyOrderModel && this.moneyOrderModel.length > 0 && this.moneyOrderModel.some(mo => mo.mo_amt && mo.mo_amt > 0);

    // Determine payment mode
    if (hasCash && !hasCheque && !hasBankDraft && !hasMoneyOrder) {
      this.paymentModel.pymt_mode = 'CA'; // Cash only
    } else if (!hasCash && hasCheque && !hasBankDraft && !hasMoneyOrder) {
      this.paymentModel.pymt_mode = 'CE'; // Cheque only
    } else if (!hasCash && !hasCheque && hasBankDraft && !hasMoneyOrder) {
      this.paymentModel.pymt_mode = 'BD'; // Bank Draft only
    } else if (!hasCash && !hasCheque && !hasBankDraft && hasMoneyOrder) {
      this.paymentModel.pymt_mode = 'MO'; // Money Order only
    } else if (hasCash || hasCheque || hasBankDraft || hasMoneyOrder) {
      this.paymentModel.pymt_mode = 'MX'; // Mixed payment
    } else {
      this.paymentModel.pymt_mode = ''; // No payment selected
    }
  }

  isPhysicalCollectionValid(): boolean {
    const hasCashAmount = this.paymentModel.cash_amt && this.paymentModel.cash_amt > 0;

    const hasChequeInfo = this.chequeModel.some(
      (item) =>
        !item.isNew &&
        item.che_bank_nm &&
        item.che_payer_nm &&
        item.che_no &&
        item.che_date &&
        item.che_ba_acct_no &&
        item.che_amt > 0
    );
    const hasBankDraftInfo = this.bankDraftModel.some(
      (item) =>
        !item.isNew &&
        item.bd_bank_nm &&
        item.bd_no &&
        item.bd_date &&
        item.bd_amt > 0
    );
    const hasMoneyOrderInfo = this.moneyOrderModel.some(
      (item) =>
        !item.isNew &&
        item.mo_rm_no &&
        item.mo_date &&
        item.mo_payer_nm &&
        item.mo_id_no &&
        item.mo_contact_no &&
        item.mo_amt > 0
    );

    const hasUnsavedCheque = this.chequeModel.some(item => item.isNew);
    const hasUnsavedBankDraft = this.bankDraftModel.some(item => item.isNew);
    const hasUnsavedMoneyOrder = this.moneyOrderModel.some(item => item.isNew);

    const totalAmount = Number(this.paymentModel.cash_amt + this.totalChequeAmount + this.totalBDAmount + this.totalMOAmount);

    // At least one of these conditions must be true for validity
    return (hasCashAmount || hasChequeInfo || hasBankDraftInfo || hasMoneyOrderInfo) && totalAmount === this.totalGrossAmount && !hasUnsavedCheque && !hasUnsavedBankDraft && !hasUnsavedMoneyOrder;
  }

  loadCounterInfo() {
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
          // this.counterTitle = 'Counter ID: ' + this.counterCheckInStatus.data.counter_id + ' | ';
          this.OTCCheckedIn = 1;
        }
      },
      (error) => {
        console.log(error);
        this.counterCheckInStatus.data = ''; //still update something to push the observer
        this.OTCCheckedIn = 0;
      }
    );

  }

  submitOTCPayment(): void {
    this.isLoadingPhysical = true;
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    const paymentBody: any = {
      i_mtt_id: this.modelData.mtt_id,
      i_emv_sale: null,
      // i_otc_counter_id: this.counterCheckInStatus?.data || null,
      i_otc_counter_id: this.counterCheckInStatus.data.counter_id,
      i_payer_email: this.paymentModel.payer_email,
      i_otc_pymt_mode: this.paymentModel.pymt_mode,
      i_created_by: this.username
    };

    const bodyForSecondApi: any = this.createBodyForSecondApi(); // Method to create the body for the second API call

    // First API call - insotcpayment
    this.http
      .post(environment.apiUrl + '/api/OTCCR/v1/insotcpayment', paymentBody, { headers })
      .toPromise()
      .then((response) => {
        console.log('First API Success:', response);

        // Second API call - insotcbodypayment, after the first API succeeds
        return this.http
          .post(environment.apiUrl + '/api/OTCCR/v1/insotcbodypayment', bodyForSecondApi, { headers })
          .toPromise();
      })
      .then((secondResponse: any) => {
        console.log('Second API Success:', secondResponse);
        // Redirect to /otc-collection-receipting after both API calls succeed
        this.fetchCollectionInfo();
        this.router.navigate(['/otc-collection-receipting']);
      })
      .catch((error) => {
        console.error('Error:', error);
        this.showPaymentAlertBox(); // Show alert box for payment error
        // Handle any error in either API
      })
      .finally(() => {
        this.isLoadingPhysical = false;
      });
  }

  createBodyForSecondApi(): any {
    // Prepare the body for the second API using chequeModel, bankDraftModel, moneyOrderModel, etc.
    const secondApiBody = [];

    // Start by adding the common properties for the first row

    // Add cash amount if it's greater than 0
    if (this.paymentModel.cash_amt > 0) {
      secondApiBody.push({
        "i_mtt_id": this.modelData.mtt_id,
        "i_cash_amt": this.paymentModel.cash_amt,
        "i_che_bank_nm": null,  // Empty for cheque, bank draft, money order
        "i_che_no": null,
        "i_che_date": null,
        "i_che_ba_acct_no": null,
        "i_che_amt": null,
        "i_che_payer_nm": null,
        "i_che_status": null,
        "i_bd_bank_nm": null,
        "i_bd_no": null,
        "i_bd_date": null,
        "i_bd_amt": null,
        "i_mo_rm_no": null,
        "i_mo_payer_nm": null,
        "i_mo_id_no": null,
        "i_mo_contact_no": null,
        "i_mo_amt": null,
        "i_mo_date": null,
        "i_che_id": null
      });
    }

    // Add rows for each cheque if any exist
    if (this.chequeModel.length > 0) {
      this.chequeModel.forEach((cheque) => {
        secondApiBody.push({
          "i_mtt_id": this.modelData.mtt_id,
          "i_cash_amt": "", // Empty cash amount for cheque rows
          "i_che_bank_nm": cheque.che_bank_nm,
          "i_che_no": cheque.che_no,
          "i_che_date": cheque.che_date,
          "i_che_ba_acct_no": cheque.che_ba_acct_no,
          "i_che_amt": cheque.che_amt,
          "i_che_payer_nm": cheque.che_payer_nm,
          "i_che_status": "Valid",
          // Set other fields to empty for cheque rows
          "i_bd_bank_nm": null,
          "i_bd_no": null,
          "i_bd_date": null,
          "i_bd_amt": null,
          "i_mo_rm_no": null,
          "i_mo_payer_nm": null,
          "i_mo_id_no": null,
          "i_mo_contact_no": null,
          "i_mo_amt": null,
          "i_mo_date": null,
          "i_che_id": cheque.che_id
        });
      });
    }

    // Add rows for each bank draft if any exist
    if (this.bankDraftModel.length > 0) {
      this.bankDraftModel.forEach((draft) => {
        secondApiBody.push({
          "i_mtt_id": this.modelData.mtt_id,
          "i_cash_amt": null, // Empty cash amount for bank draft rows
          "i_che_bank_nm": null,  // Empty for bank draft rows
          "i_che_no": null,
          "i_che_date": null,
          "i_che_ba_acct_no": null,
          "i_che_amt": null,
          "i_che_payer_nm": null,
          "i_che_status": null,
          "i_bd_bank_nm": draft.bd_bank_nm,
          "i_bd_no": draft.bd_no,
          "i_bd_date": draft.bd_date,
          "i_bd_amt": draft.bd_amt,
          // Set other fields to empty for bank draft rows
          "i_mo_rm_no": null,
          "i_mo_payer_nm": null,
          "i_mo_id_no": null,
          "i_mo_contact_no": null,
          "i_mo_amt": null,
          "i_mo_date": null,
          "i_che_id": null
        });
      });
    }

    // Add rows for each money order if any exist
    if (this.moneyOrderModel.length > 0) {
      this.moneyOrderModel.forEach((order) => {
        secondApiBody.push({
          "i_mtt_id": this.modelData.mtt_id,
          "i_cash_amt": null, // Empty cash amount for money order rows
          "i_che_bank_nm": null,  // Empty for money order rows
          "i_che_no": null,
          "i_che_date": null,
          "i_che_ba_acct_no": null,
          "i_che_amt": null,
          "i_che_payer_nm": null,
          "i_che_status": null,
          "i_bd_bank_nm": null,  // Empty for money order rows
          "i_bd_no": null,
          "i_bd_date": null,
          "i_bd_amt": null,
          "i_mo_rm_no": order.mo_rm_no,
          "i_mo_payer_nm": order.mo_payer_nm,
          "i_mo_id_no": order.mo_id_no,
          "i_mo_contact_no": order.mo_contact_no,
          "i_mo_amt": order.mo_amt,
          "i_mo_date": order.mo_date,
          "i_che_id": null
        });
      });
    }

    console.log('Second API Body:', secondApiBody);

    return secondApiBody;
  }

  fetchOTCHist(): void {
    // this.isLoading = true;
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    const url = environment.apiUrl + '/api/OTCCR/v1/getotccrhist'; // API endpoint
    const requestBody = {
      i_mtt_id: this.modelData.mtt_id,
    };

    this.http.post(url, requestBody, { headers }).subscribe(
      (response: any) => {
        this.otcHistModel = response?.data || []; // Store the received data
        this.isLoading = false;
      },
      (error) => {
        console.error('Error fetching OTC History:', error);
        this.isLoading = false;
      }
    );
  }

  // submitOTCEMVPayment(): void {
  //   this.isLoading = true;
  //   const headers = new HttpHeaders({
  //     Authorization: environment.authKey,
  //     'Content-Type': 'application/json',
  //   });

  //   const emvPayment: any = {
  //     amount: this.totalGrossAmount,
  //     // additionalData: "TI" + this.modelData.mtt_id,
  //     additionalData: ("TI" + this.modelData.mtt_id).padEnd(24, "0"),
  //     command: "C200",

  //     i_mtt_id: this.modelData.mtt_id,
  //     // i_otc_counter_id: this.counterCheckInStatus?.data || null,
  //     i_otc_counter_id: this.counterCheckInStatus.data.counter_id,
  //     i_payer_email: this.paymentModel.payer_email,
  //     i_otc_pymt_mode: 'EV'
  //   };

  //   // Single API call - emvPayment
  //   this.http
  //     .post(environment.apiUrl + '/api/OTCCR/v1/emvPayment', emvPayment, { headers })
  //     .toPromise()
  //     .then((response: any) => {
  //       console.log('API Success:', response);
  //       // If needed, process the response here

  //       if (response.data == 0) {
  //         const dialogRef = this.dialog.open(OtcEmvQueryComponent, {
  //           width: '400px',
  //           data: { message: "Requery Transaction?" }
  //         });

  //         dialogRef.afterClosed().subscribe((result: boolean) => {
  //           if (result) {
  //             this.isLoading = true;
  //             const apiUrl = `${environment.apiUrl}/api/OTCCR/v1/emvPayment`;

  //             const headers = new HttpHeaders({
  //               Authorization: environment.authKey,
  //               'Content-Type': 'application/json',
  //             });

  //             const requestBody = {
  //               amount: this.totalGrossAmount,
  //               additionalData: ("TI" + this.modelData.mtt_id).padEnd(24, "0"),
  //               command: "C208",
  //               i_mtt_id: this.modelData.mtt_id,
  //               // i_otc_counter_id: this.counterCheckInStatus?.data || null,
  //               i_otc_counter_id: this.counterCheckInStatus.data.counter_id,
  //               i_payer_email: this.paymentModel.payer_email,
  //               i_otc_pymt_mode: 'EV'
  //             };

  //             this.http.post(apiUrl, requestBody, { headers }).subscribe(
  //               (response: any) => {
  //                 // Query failed
  //                 if(response.data == 0){
  //                   this.showQueryAlertBox("Transaction not found. Please make payment again!");
  //                 }
  //                 // Query success
  //                 if(response.data == 1){
  //                   this.fetchCollectionInfo(); // route to OTC Receipt screen
  //                   this.router.navigate(['/otc-collection-receipting']);
  //                 }
  //                 this.isLoading = false;
  //               },
  //               (error) => {
  //                 console.error("Error requery this EMV Transaction", error);
  //                 this.isLoading = false;
  //               }
  //             );
  //           }
  //         });
  //       }

  //       if (response.data == 1) {
  //         this.fetchCollectionInfo();
  //         this.router.navigate(['/otc-collection-receipting']);
  //       }

  //     })
  //     .catch((error) => {
  //       console.error('Error:', error);
  //       // Handle any error from the API
  //     })
  //     .finally(() => {
  //       this.isLoading = false;
  //     });
  // }

  // submitOTCEMVPayment(): void {
  //   this.isLoading = true;

  //   const headers = new HttpHeaders({
  //     Authorization: environment.authKey,
  //     'Content-Type': 'application/json',
  //   });

  //   const emvPayment: any = {
  //     amount: this.totalGrossAmount,
  //     additionalData: ("TI" + this.modelData.mtt_id).padEnd(24, "0"),
  //     command: "C200",
  //     i_mtt_id: this.modelData.mtt_id,
  //     i_otc_counter_id: this.counterCheckInStatus.data.counter_id,
  //     i_payer_email: this.paymentModel.payer_email,
  //     i_otc_pymt_mode: 'EV'
  //   };

  //   // Call the CLIENT's local EMV API on localhost:8081
  //   const clientLocalEmvApi = `http://localhost:8081/emv/api/emv/v1/emvPayment`;
  //   // const clientLocalEmvApi = `http://localhost:8081/api/emv/v1/emvPayment`;

  //   this.http.post(clientLocalEmvApi, emvPayment, { headers }).toPromise()
  //     .then((emvResponse: any) => {
  //       console.log('Client Local EMV API Response:', emvResponse);

  //       // Ensure response has data
  //       if (!emvResponse || !emvResponse.data) {
  //         throw new Error("Invalid EMV response");
  //       }

  //       // Now send the EMV response to the server
  //       const serverApiUrl = environment.apiUrl + '/api/OTCCR/v1/emvPayment';

  //       const finalRequest = {
  //         ...emvPayment,
  //         emvResponse: emvResponse.data // Include the EMV response
  //       };

  //       return this.http.post(serverApiUrl, finalRequest, { headers }).toPromise();
  //     })
  //     .then((serverResponse: any) => {
  //       console.log('Server API Response:', serverResponse);

  //       if (serverResponse.data === 0) {
  //         const dialogRef = this.dialog.open(OtcEmvQueryComponent, {
  //           width: '400px',
  //           data: { message: "Requery Transaction? Please ensure the EMV USB Port is properly plugged with your machine." }
  //         });

  //         dialogRef.afterClosed().subscribe((result: boolean) => {
  //           // if (result) {
  //           //   this.isLoading = true;

  //           //   this.requeryTransaction();
  //           // }

  //           if (result) {
  //             this.isLoading = true;
  //             const apiUrl = `http://localhost:8081/emv/api/emv/v1/emvPayment`;

  //             const headers = new HttpHeaders({
  //               Authorization: environment.authKey,
  //               'Content-Type': 'application/json',
  //             });

  //             const requestBody = {
  //               amount: this.totalGrossAmount,
  //               additionalData: ("TI" + this.modelData.mtt_id).padEnd(24, "0"),
  //               command: "C208",
  //               i_mtt_id: this.modelData.mtt_id,
  //               // i_otc_counter_id: this.counterCheckInStatus?.data || null,
  //               i_otc_counter_id: this.counterCheckInStatus.data.counter_id,
  //               i_payer_email: this.paymentModel.payer_email,
  //               i_otc_pymt_mode: 'EV'
  //             };

  //             this.http.post(clientLocalEmvApi, requestBody, { headers }).toPromise()
  //             .then((emvResponse: any) => {
  //               console.log('Client Local EMV API Response:', emvResponse);

  //               // Ensure response has data
  //               if (!emvResponse || !emvResponse.data) {
  //                 throw new Error("Invalid EMV response");
  //               }

  //               // Now send the EMV response to the server
  //               const serverApiUrl = environment.apiUrl + '/api/OTCCR/v1/emvPayment';

  //               const finalRequest = {
  //                 ...requestBody,
  //                 emvResponse: emvResponse.data // Include the EMV response
  //               };

  //               return this.http.post(serverApiUrl, finalRequest, { headers }).toPromise();
  //             })
  //             .then((serverResponse: any) => {
  //               // Query failed
  //               if (serverResponse.data === 0) {
  //                 this.showQueryAlertBox("Transaction not found. Please make payment again!");
  //               }
  //               // Query success
  //               if (serverResponse.data === 1) {
  //                 this.fetchCollectionInfo(); // route to OTC Receipt screen
  //                 this.router.navigate(['/otc-collection-receipting']);
  //               }
  //               this.isLoading = false;
  //             }
  //             )
  //             .catch(error => {
  //               console.error('Error:', error);
  //             })
  //           }
  //         });
  //       }

  //       if (serverResponse.data === 1) {
  //         this.fetchCollectionInfo();
  //         this.router.navigate(['/otc-collection-receipting']);
  //       }
  //     })
  //     .catch(error => {
  //       console.error('Error:', error);
  //     })
  //     .finally(() => {
  //       this.isLoading = false;
  //     });
  // }

  submitOTCEMVPayment(): void {
    this.isLoading = true;

    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    const emvPayment: any = {
      amount: this.totalGrossAmount,
      additionalData: ("TI" + this.modelData.mtt_id).padEnd(24, "0"),
      command: "C200",
      i_mtt_id: this.modelData.mtt_id,
      i_otc_counter_id: this.counterCheckInStatus.data.counter_id,
      i_payer_email: this.paymentModel.payer_email,
      i_otc_pymt_mode: 'EV'
    };

    // Call the CLIENT's local EMV API on localhost:8081
    // Call the CLIENT's local EMV API on localhost:8314
    const clientLocalEmvApi = `http://localhost:8314/emv/api/emv/v1/emvPayment`;

    this.http.post(clientLocalEmvApi, emvPayment, { headers }).toPromise()
      .then((emvResponse: any) => {
        console.log('Client Local EMV API Response:', emvResponse);

        // Ensure response has data
        if (!emvResponse || !emvResponse.data) {
          throw new Error("Invalid EMV response");
        }

        // Send EMV response to server
        const serverApiUrl = environment.apiUrl + '/api/OTCCR/v1/emvPayment';

        const finalRequest = {
          ...emvPayment,
          emvResponse: emvResponse.data
        };

        return this.http.post(serverApiUrl, finalRequest, { headers }).toPromise();
      })
      .then((serverResponse: any) => {
        console.log('Server API Response:', serverResponse);

        // Query failed — show requery button
        if (serverResponse.data === 0) {
          // this.showRequeryButton = true; // Enable the requery button
        }

        // Query success — navigate to receipt screen
        if (serverResponse.data === 1) {
          this.fetchCollectionInfo();
          this.router.navigate(['/otc-collection-receipting']);
        }
      })
      .catch(error => {
        console.error('Error:', error);
      })
      .finally(() => {
        this.isLoading = false;
      });
  }

  // ✅ Separate requery logic — triggered by the button click
  requeryTransaction(): void {
    this.isLoading = true;

    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    const clientLocalEmvApi = `http://localhost:8314/emv/api/emv/v1/emvPayment`;

    const requestBody = {
      amount: this.totalGrossAmount,
      additionalData: ("TI" + this.modelData.mtt_id).padEnd(24, "0"),
      command: "C208",
      i_mtt_id: this.modelData.mtt_id,
      i_otc_counter_id: this.counterCheckInStatus.data.counter_id,
      i_payer_email: this.paymentModel.payer_email,
      i_otc_pymt_mode: 'EV'
    };

    this.http.post(clientLocalEmvApi, requestBody, { headers }).toPromise()
      .then((emvResponse: any) => {
        console.log('Client Local EMV API Response:', emvResponse);

        if (!emvResponse || !emvResponse.data) {
          throw new Error("Invalid EMV response");
        }

        const serverApiUrl = environment.apiUrl + '/api/OTCCR/v1/emvPayment';

        const finalRequest = {
          ...requestBody,
          emvResponse: emvResponse.data
        };

        return this.http.post(serverApiUrl, finalRequest, { headers }).toPromise();
      })
      .then((serverResponse: any) => {
        console.log('Requery Server Response:', serverResponse);

        if (serverResponse.data === 0) {
          this.showQueryAlertBox("Transaction not found. Please make payment again!");
        }

        if (serverResponse.data === 1) {
          this.fetchCollectionInfo();
          this.router.navigate(['/otc-collection-receipting']);
        }
      })
      .catch(error => {
        console.error('Error:', error);
      })
      .finally(() => {
        this.isLoading = false;
        // this.showRequeryButton = false; // Hide the requery button after action
      });
  }

  cancel(): void {
    this.router.navigate(['/otc-collection-receipting']);
  }

  fetchCollectionInfo(): void {
    const url = environment.apiUrl + '/api/OTCCR/v1/getcollectioninfo';

    const body = {
      i_page: '1',
      i_size: '10',
      i_coll_slip_no: this.modelData.coll_slip_no,
      i_orn_no: this.modelData.orn_no
    };

    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    this.http.post(url, body, { headers }).subscribe(
      (response: any) => {
        if (response.data && response.data.length > 0) {
          const item = response.data[0];

          this.router.navigate(['/otc-receipt-screen', this.modelData.coll_slip_no], {
            queryParams: { orn_no: this.modelData.orn_no, curr_page: "otc-collection-receipting" },
            state: { item }
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

  get totalPhysicalAmount(): number {
    const cash = Number(this.paymentModel.cash_amt) || 0;
    const cheque = Number(this.totalChequeAmount) || 0;
    const bankDraft = Number(this.totalBDAmount) || 0;
    const moneyOrder = Number(this.totalMOAmount) || 0;

    return cash + cheque + bankDraft + moneyOrder;
  }

}