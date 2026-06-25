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
import { OTCCollectionReceiptingBankDraft, OTCCollectionReceiptingCheque, OTCCollectionReceiptingMoneyOrder, OTCCollectionReceiptingPymtItem, OTCHist, OTCPaymentModel, OTCPaymentDetails, OTCPaymentHeader, OTCRcpt, OTCEMV } from 'src/app/core/models/otc-collection-receipting.interface';
import { OTCBank } from 'src/app/core/models/otc-collection-returned-cheque.interface';
import { perm } from 'src/permissions/perm';
import { forkJoin, Observable } from 'rxjs';
import { tap, finalize } from 'rxjs/operators';

@Component({
  selector: 'app-otc-receipt-screen',
  templateUrl: './otc-receipt-screen.component.html',
  styleUrls: ['./otc-receipt-screen.component.scss']
})
export class OtcReceiptScreenComponent {
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
  otcRcptModel: OTCRcpt[] = [];
  otcEMVModel: OTCEMV[] = [];
  otcPaymentDetails: OTCPaymentDetails[] = [];
  otcPaymentHeader: OTCPaymentHeader[] = [];
  otcPayerEmail: String | null = null;
  otcPymtMode: String | null = null;
  breadcrumbLabel: string = ''; // Define a property to hold the breadcrumb label

  cashPayments: OTCPaymentDetails[] = [];
  chequePayments: OTCPaymentDetails[] = [];
  moneyOrderPayments: OTCPaymentDetails[] = [];
  bankDraftPayments: OTCPaymentDetails[] = [];
  totalGrossAmount: number = 0; // Variable to hold the total sum of gross amounts
 
  totalChequeAmount: number = 0;
  totalBDAmount: number = 0;
  totalMOAmount: number = 0;

  permReceipt = perm.OTC_Returned_Cheque_View_Dishonor; // all the perm_cd for this module seperated with comma
  permReceiptAllow = ""; // variable to store allowed permission for the user
  permDishonorAllow: number = 1; // if 0 then not allow to view listing page, else allow

  file_content: string | null = null;

  otcPaymentDetailsCashAmt: number| null = 0;
  paymentModel: OTCPaymentModel = {
    payer_email: '',
    pymt_mode: '',
    cash_amt: 0,
    // Initialize other fields here
  };
 
 
  isLoading: boolean = false;
  totalRecords: number = 0;
 
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
    private authService: AuthService
  ) {
    config.maxSize = environment.PaginationMaxSize;
    config.boundaryLinks = true;
    this.translate.setDefaultLang(this.globalService.getGlobalValue()); // default language
    this.translate.use(this.globalService.getGlobalValue());
    this.route.queryParams.subscribe(params => {
      this.orn_no = params['orn_no'];
      this.breadcrumbLabel = params['curr_page'];
    });

    console.log(this.breadcrumbLabel);
    const navigation = this.router.getCurrentNavigation();
    this.modelData = navigation?.extras.state?.['item']; // Retrieve the passed data
    console.log(this.modelData);
 
  }
 
  ngOnInit() {
    //this.selected = new Date();
    this.route.paramMap.subscribe(params => {
      this.coll_slip_no = params.get('coll_slip_no');
    });
    
    this.executeAllApiCalls();

  }
  
executeAllApiCalls() {
  const paymentItems$ = this.fetchPaymentItems();
  const banks$ = this.fetchBanks();
  const hist$ = this.fetchOTCHist();
  const rcpt$ = this.fetchOTCRcpt();
  const details$ = this.fetchOTCPaymentDetails();
  const header$ = this.fetchOTCHeader();
  const emv$ = this.fetchOTCEMV();

  forkJoin([paymentItems$, banks$, hist$, rcpt$, details$, header$, emv$]).subscribe(
    ([paymentItems, banks, hist, rcpt, details, header, emv]) => {
      console.log('All API responses received:', {
        paymentItems,
        banks,
        hist,
        rcpt,
        details,
        header,
        emv,
      });
      this.calculateTotalGrossAmount();
    },
    (error) => {
      console.error('Error during API calls:', error);
    }
  );
}
  
  
fetchPaymentItems(): Observable<any> {
  this.isLoading = true;

  const headers = new HttpHeaders({
    Authorization: environment.authKey,
    'Content-Type': 'application/json',
  });
  
  const url = environment.apiUrl + '/api/OTCCR/v1/getpymtitems'; // API endpoint
  const requestBody = {
    i_coll_slip_no: this.coll_slip_no,
    i_orn_no: this.orn_no,
  };

  return this.http.post(url, requestBody, { headers }).pipe(
    tap((response: any) => {
      this.paymentItems = response?.data || []; // Store the received data
      console.log(this.paymentItems);
      this.calculateTotalGrossAmount();
    }),
    finalize(() => {
      this.isLoading = false;
    })
  );
}

  calculateTotalGrossAmount(): void {
    this.totalGrossAmount = this.paymentItems.reduce((sum, item) => sum + item.net_amt, 0);
  }


fetchBanks(): Observable<any> {
  this.isLoading = true;

  const headers = new HttpHeaders({
    Authorization: environment.authKey,
    'Content-Type': 'application/json',
  });

  const url = environment.apiUrl + '/api/rms/v1/getbanks';

  const Body: any = {};

  return this.http.post(url, Body, { headers }).pipe(
    tap((response: any) => {
      this.bankmodel = response.data;
      this.totalRecords = response.data.length > 0 ? response.data[0].total : 0;
    }),
    finalize(() => {
      this.isLoading = false;
    })
  );
}


fetchOTCEMV(): Observable<any> {
  this.isLoading = true;

  const headers = new HttpHeaders({
    Authorization: environment.authKey,
    'Content-Type': 'application/json',
  });

  const url = environment.apiUrl + '/api/OTCCR/v1/getotcemv';

  const Body: any = {
    i_mtt_id: this.modelData.mtt_id,
  };

  return this.http.post(url, Body, { headers }).pipe(
    tap((response: any) => {
      // Ensure response.data is treated as an array
      this.otcEMVModel = response.data
        ? Array.isArray(response.data)
          ? response.data
          : [response.data]
        : [];
      console.log(this.otcEMVModel);
      console.log(this.otcEMVModel.length);
    }),
    finalize(() => {
      this.isLoading = false;
    })
  );
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
      alert("Please ensure all fields are filled and the amount is greater than 0.");
      return; // Exit without saving
    }
 
    this.chequeModel[index].isEditable = false;
    this.chequeModel[index].isNew = false;
    this.isAddCheque = true;
  }
 
  editChequeRow(index: number) {
    this.chequeModel[index].isEditable = true;
  }
 
  removeChequeNewRow(index: number) {
    this.chequeModel.splice(index, 1);
    this.isAddCheque = true;
  }
 
  deleteChequeRow(index: number) {
    this.chequeModel.splice(index, 1); // Remove the row from the array
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
      alert("Please ensure all fields are filled and the amount is greater than 0.");
      return; // Exit without saving
    }
    this.bankDraftModel[index].isEditable = false;
    this.bankDraftModel[index].isNew = false;
    this.isAddBankDraft = true;
  }
 
  editBDRow(index: number) {
    this.bankDraftModel[index].isEditable = true;
  }
 
  removeBDNewRow(index: number) {
    this.bankDraftModel.splice(index, 1);
    this.isAddBankDraft = true;
  }
 
  deleteBDRow(index: number) {
    this.bankDraftModel.splice(index, 1); // Remove the row from the array
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
      !row.mo_contact_no||
      !row.mo_amt ||
      row.mo_amt <= 0
    ) {
      alert("Please ensure all fields are filled and the amount is greater than 0.");
      return; // Exit without saving
    }
    this.moneyOrderModel[index].isEditable = false;
    this.moneyOrderModel[index].isNew = false;
    this.isAddMoneyOrder = true;
  }
 
  editMORow(index: number) {
    this.moneyOrderModel[index].isEditable = true;
  }
 
  removeMONewRow(index: number) {
    this.moneyOrderModel.splice(index, 1);
    this.isAddMoneyOrder = true;
  }
 
  deleteMORow(index: number) {
    this.moneyOrderModel.splice(index, 1); // Remove the row from the array
    if (this.moneyOrderModel.length === 0) {
      this.isAddMoneyOrder = true; // If all rows are deleted, show Add button
    }
  }
 
  submitOTCPayment(): void {
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });
 
    const paymentBody: any = {
      "i_mtt_id": this.modelData.mtt_id,
      "i_emv_sale": null,
      "i_otc_counter_id": null,
      "i_payer_email": this.paymentModel.payer_email,
      "i_otc_pymt_mode": this.paymentModel.pymt_mode,
    };
 
    const bodyForSecondApi: any = this.createBodyForSecondApi();  // Method to create the body for the second API call
 
    // First API call - insotcpayment
    this.http.post(environment.apiUrl + '/api/OTCCR/v1/insotcpayment', paymentBody, { headers })
      .toPromise()
      .then((response) => {
        console.log('First API Success:', response);
 
        // Second API call - insotcbodypayment, after the first API succeeds
        return this.http.post(environment.apiUrl + '/api/OTCCR/v1/insotcbodypayment', bodyForSecondApi, { headers }).toPromise();
      })
      .then((secondResponse: any) => {
        console.log('Second API Success:', secondResponse);
        // Handle success logic for the second API
      })
      .catch((error) => {
        console.error('Error:', error);
        // Handle any error in either API
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
        "i_che_bank_nm": "",  // Empty for cheque, bank draft, money order
        "i_che_no": "",
        "i_che_date": "",
        "i_che_ba_acct_no": "",
        "i_che_amt": "",
        "i_che_payer_nm": "",
        "i_che_status": "",
        "i_bd_bank_nm": "",
        "i_bd_no": "",
        "i_bd_date": "",
        "i_bd_amt": "",
        "i_mo_rm_no": "",
        "i_mo_payer_nm": "",
        "i_mo_id_no": "",
        "i_mo_contact_no": "",
        "i_mo_amt": "",
        "i_mo_date": "",
        "i_che_id": ""
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
          "i_che_status": cheque.che_status,
          // Set other fields to empty for cheque rows
          "i_bd_bank_nm": "",
          "i_bd_no": "",
          "i_bd_date": "",
          "i_bd_amt": "",
          "i_mo_rm_no": "",
          "i_mo_payer_nm": "",
          "i_mo_id_no": "",
          "i_mo_contact_no": "",
          "i_mo_amt": "",
          "i_mo_date": "",
          "i_che_id": cheque.che_id
        });
      });
    }
 
    // Add rows for each bank draft if any exist
    if (this.bankDraftModel.length > 0) {
      this.bankDraftModel.forEach((draft) => {
        secondApiBody.push({
          "i_mtt_id": this.modelData.mtt_id,
          "i_cash_amt": "", // Empty cash amount for bank draft rows
          "i_che_bank_nm": "",  // Empty for bank draft rows
          "i_che_no": "",
          "i_che_date": "",
          "i_che_ba_acct_no": "",
          "i_che_amt": "",
          "i_che_payer_nm": "",
          "i_che_status": "",
          "i_bd_bank_nm": draft.bd_bank_nm,
          "i_bd_no": draft.bd_no,
          "i_bd_date": draft.bd_date,
          "i_bd_amt": draft.bd_amt,
          // Set other fields to empty for bank draft rows
          "i_mo_rm_no": "",
          "i_mo_payer_nm": "",
          "i_mo_id_no": "",
          "i_mo_contact_no": "",
          "i_mo_amt": "",
          "i_mo_date": "",
          "i_che_id": ""
        });
      });
    }
 
    // Add rows for each money order if any exist
    if (this.moneyOrderModel.length > 0) {
      this.moneyOrderModel.forEach((order) => {
        secondApiBody.push({
          "i_mtt_id": this.modelData.mtt_id,
          "i_cash_amt": "", // Empty cash amount for money order rows
          "i_che_bank_nm": "",  // Empty for money order rows
          "i_che_no": "",
          "i_che_date": "",
          "i_che_ba_acct_no": "",
          "i_che_amt": "",
          "i_che_payer_nm": "",
          "i_che_status": "",
          "i_bd_bank_nm": "",  // Empty for money order rows
          "i_bd_no": "",
          "i_bd_date": "",
          "i_bd_amt": "",
          "i_mo_rm_no": order.mo_rm_no,
          "i_mo_payer_nm": order.mo_payer_nm,
          "i_mo_id_no": order.mo_id_no,
          "i_mo_contact_no": order.mo_contact_no,
          "i_mo_amt": order.mo_amt,
          "i_mo_date": order.mo_date,
          "i_che_id": ""
        });
      });
    }
 
    console.log('Second API Body:', secondApiBody);
   
    return secondApiBody;
  }

fetchOTCHist(): Observable<any> {
  this.isLoading = true;

  const headers = new HttpHeaders({
    Authorization: environment.authKey,
    'Content-Type': 'application/json',
  });

  const url = environment.apiUrl + '/api/OTCCR/v1/getotccrhist'; // API endpoint
  const requestBody = {
    i_mtt_id: this.modelData.mtt_id,
  };

  return this.http.post(url, requestBody, { headers }).pipe(
    tap((response: any) => {
      this.otcHistModel = response?.data || []; // Store the received data
      console.log(this.otcHistModel);
    }),
    finalize(() => {
      this.isLoading = false;
    })
  );
}

  calculateTotalChequeAmount(): void {
    this.totalChequeAmount = this.chequePayments.reduce((sum, item) => sum + item.che_amt, 0);
  }
 
  calculateTotalBDAmount(): void {
    this.totalBDAmount = this.bankDraftPayments.reduce((sum, item) => sum + item.bd_amt, 0);
  }
 
  calculateTotalMOAmount(): void {
    this.totalMOAmount = this.moneyOrderPayments.reduce((sum, item) => sum + item.mo_amt, 0);
  }

fetchOTCPaymentDetails(): Observable<any> {
  this.isLoading = true;

  const headers = new HttpHeaders({
    Authorization: environment.authKey,
    'Content-Type': 'application/json',
  });

  const url = environment.apiUrl + '/api/OTCCR/v1/getOTCPaymentDetails'; // API endpoint
  const requestBody = {
    i_mtt_id: this.modelData.mtt_id,
  };

  return this.http.post(url, requestBody, { headers }).pipe(
    tap((response: any) => {
      this.otcPaymentDetails = response?.data || []; // Store the received data
      this.cashPayments = this.otcPaymentDetails.filter(item => item.cash_amt !== null);
      this.otcPaymentDetailsCashAmt = this.cashPayments.length > 0 ? this.cashPayments[0].cash_amt : 0;

      this.chequePayments = this.otcPaymentDetails.filter(item => item.che_amt !== null);
      this.moneyOrderPayments = this.otcPaymentDetails.filter(item => item.mo_amt !== null);
      this.bankDraftPayments = this.otcPaymentDetails.filter(item => item.bd_amt !== null);

      this.calculateTotalChequeAmount();
      this.calculateTotalBDAmount();
      this.calculateTotalMOAmount();
    }),
    finalize(() => {
      this.isLoading = false;
    })
  );
}

fetchOTCHeader(): Observable<any> {
  this.isLoading = true;

  const headers = new HttpHeaders({
    Authorization: environment.authKey,
    'Content-Type': 'application/json',
  });

  const url = environment.apiUrl + '/api/OTCCR/v1/getOTCPaymentHeader'; // API endpoint
  const requestBody = {
    i_mtt_id: this.modelData.mtt_id,
  };

  return this.http.post(url, requestBody, { headers }).pipe(
    tap((response: any) => {
      this.otcPaymentHeader = response?.data || [];
      this.otcPayerEmail = this.otcPaymentHeader.length > 0 ? this.otcPaymentHeader[0].payer_email : '';
      this.otcPymtMode = this.otcPaymentHeader.length > 0 ? this.otcPaymentHeader[0].otc_pymt_mode : '';
    }),
    finalize(() => {
      this.isLoading = false;
    })
  );
}

fetchOTCRcpt(): Observable<any> {
  this.isLoading = true;

  const headers = new HttpHeaders({
    Authorization: environment.authKey,
    'Content-Type': 'application/json',
  });

  const url = environment.apiUrl + '/api/OTCCR/v1/getotccrrcpt'; // API endpoint
  const requestBody = {
    i_mtt_id: this.modelData.mtt_id,
  };

  return this.http.post(url, requestBody, { headers }).pipe(
    tap((response: any) => {
      this.otcRcptModel = response?.data || [];
      console.log(this.otcRcptModel);
    }),
    finalize(() => {
      this.isLoading = false;
    })
  );
}

downloadFile(fileName: string, verId: string, sourceSysDocRefID: string) {

    const generateURL = environment.apiUrl + '/api/OTCCR/v1/downloadOTCRcpt';
    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json'
    });

    const requestBody = {
      refNo1: fileName,
      verID: verId,
      sourceSysDocRefID: sourceSysDocRefID,
    };

    // this.http.post(generateURL, requestBody, { observe: 'response', responseType: 'blob', headers: headers })
    //   .subscribe(response => {
    //     var blob = new Blob([response.body as Blob], { type: 'pdf' });
    //     saveAs(blob, response.headers.get('content-disposition')!.split('filename=')[1]);
    //   });

    this.http.post(generateURL, requestBody, { headers }).subscribe(
      (response: any) => {
        // console.log(response.data);
        this.file_content = response.data;
        if (this.file_content != null) {
          this.downloadFileContent(this.file_content, fileName);
        }
        if (response.data.length == 0) {
          this.totalRecords = 0;
          //this.showResultAlertBox();
          this.isLoading = false;
        } else {
          this.totalRecords = response.data[0].total;
          // this.DefaultBox();
          this.isLoading = false;
          // this.isDisplay = true;
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

  downloadFileContent(fileContent: string, orn_no: string): void {
    this.isLoading = true;
    const binaryString = window.atob(fileContent);
    const len = binaryString.length;
    const uint8Array = new Uint8Array(len);
    for (let i = 0; i < len; i++) {
      uint8Array[i] = binaryString.charCodeAt(i);
    }
    const blob = new Blob([uint8Array], { type: 'application/pdf' });
    const url = URL.createObjectURL(blob);
    const anchor = document.createElement('a');
    anchor.href = url;
    const filename = 'SSM-Receipt-' + orn_no + '.pdf';
    anchor.download = filename;
    document.body.appendChild(anchor);
    anchor.click();
    document.body.removeChild(anchor);
    URL.revokeObjectURL(url);
  }

  // downloadFile(fileName: string, verId: string, sourceSysDocRefID: string) {
  //   const requestBody = {
  //     refNo1: fileName,
  //     verID: verId,
  //     sourceSysDocRefID: sourceSysDocRefID,
  //   };
 
  //   const headers = new HttpHeaders({
  //     Authorization: environment.authKey,
  //     'Content-Type': 'application/json',
  //   });
 
  //   const url = `${environment.apiUrl}/api/OTCCR/v1/downloadOTCRcpt`;
 
  //   this.http
  //     .post(url, requestBody, {
  //       observe: 'response',
  //       responseType: 'blob', // Expect binary content
  //       headers: headers,
  //     })
  //     .subscribe(
  //       (response) => {
  //         const contentDisposition = response.headers.get('content-disposition');
  //         let fileNameFromHeader = 'SSM-Receipt-' + fileName + '.pdf'; // Fallback name
  //         if (contentDisposition) {
  //           const match = contentDisposition.match(/filename="?(.+)"?/);
  //           if (match && match[1]) {
  //             fileNameFromHeader = match[1];
  //           }
  //         }
          
  //         console.log(fileNameFromHeader);
 
  //         const blob = new Blob([response.body as Blob], { type: 'application/pdf' });
 
  //         // Create a link to download the file
  //         const link = document.createElement('a');
  //         const objectUrl = URL.createObjectURL(blob);
  //         link.href = objectUrl;
  //         link.download = fileNameFromHeader.trim().replace(/\s+/g, '_'); // Ensure no spaces
  //         // link.download = fileNameFromHeader || fileName;
  //         link.click();
  //         URL.revokeObjectURL(objectUrl);
  //       },
  //       (error) => {
  //         console.error('Error downloading file:', error);
  //       }
  //     );
  // }
 
  // navigateToNonBillingRegistration(): void {
  //   // const coll_slip_no = item.coll_slip_no;
  //   // const orn_no = item.orn_no;
  //   this.router.navigate(['/non-billing-registration']);
  //   // this.router.navigate(['/otc-payment-screen', coll_slip_no], { queryParams: { orn_no }, state: { item } });
  // }
 
  navigateToNonBillingRegistration(chequeNo: string, chequeAmount: number, otcBodyID: number, chequeID: string): void {
    this.router.navigate(['/non-billing-registration'], {
      state: {chequeNo: chequeNo, chequeAmount: chequeAmount, modelData: this.modelData, otcBodyID: otcBodyID, chequeID: chequeID},
    });
  }
 
 
}