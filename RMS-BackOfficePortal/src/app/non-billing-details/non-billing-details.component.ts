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
import { NonBillingItem, OTCBank, NonBillDocs, NonBilHist } from 'src/app/core/models/otc-collection-returned-cheque.interface';
//import { BillingCancellationAdjustmentSSDoc, BillingCancellationAdjustmentSSItem, BillingCancellationAdjustmentSSSearch, BillingHist } from '../core/models/billing-cancellation-adjustment-ss.interface';


@Component({
  selector: 'app-non-billing-details',
  templateUrl: './non-billing-details.component.html',
  styleUrls: ['./non-billing-details.component.scss']
})
export class NonBillingDetailsComponent {
  page = environment.DefaultPage;
  itemsPerPage = environment.ItemPerPage;

  coll_slip_no: String | null = null;
  orn_no: String | null = null;
  modelData: any;
  paymentItems: NonBillingItem[] = [];
  supportingDocs: NonBillDocs[] = [];
  bankmodel: OTCBank[] = [];
  isAddMoneyOrder: boolean = true;
  billHist: NonBilHist[] = [];
  otcRcptModel: OTCRcpt[] = [];
  file_content = "";
  totalGrossAmount: number = 0; // Variable to hold the total sum of gross amounts
  otcPaymentDetailsCashAmt: number | null = 0;
  isEditable: boolean = false;

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
    this.fetchBillItems();
    // this.fetchBanks();
    this.fetchBillHist();
    this.fetchBillDocs();
    // this.fetchOTCPaymentDetails();
    // this.fetchOTCHeader();
    // this.fetchOTCEMV();
    this.calculateRowTotals();

  }

  adjustBill(): void {
    this.isEditable = true;
  }

  fetchBillItems(): void {
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    const url = environment.apiUrl + '/api/OTCRC/v1/getnonbillingitems'; // API endpoint
    const requestBody = {
      i_non_bil_id: this.modelData.non_bil_id
    };

    this.http.post(url, requestBody, { headers }).subscribe(
      (response: any) => {
        this.paymentItems = response?.data || []; // Store the received data
        console.log(this.paymentItems);
        this.isLoading = false;
        this.calculateRowTotals();
      },
      (error) => {
        console.error('Error fetching payment items:', error);
        this.isLoading = false;
      }
    );
  }

  // calculateTotalGrossAmount(): void {
  //   this.totalGrossAmount = this.paymentItems.reduce((sum, item) => sum + item.final_amt, 0);
  // }

  calculateRowTotals(): void {
    this.totalGrossAmount = 0; // Reset totalGrossAmount before recalculation

    this.paymentItems.forEach((item) => {
      // Calculate gross amount for each row
      const grossAmount = item.unit_fee * item.qty;
      
      // Update tax amount and final amount for the row
      item.tax_amt = (grossAmount * item.tax_pct) / 100;
      item.item_total_amt = grossAmount + item.tax_amt;

      // Add the row's final amount to the overall total
      this.totalGrossAmount += item.item_total_amt;
    });
  }

  fetchBillHist(): void {
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    const url = environment.apiUrl + '/api/OTCRC/v1/getnonbilhist'; // API endpoint
    const requestBody = {
      i_non_bil_id: this.modelData.non_bil_id
    };

    this.http.post(url, requestBody, { headers }).subscribe(
      (response: any) => {
        this.billHist = response?.data || []; // Store the received data
        console.log(this.billHist);
        this.isLoading = false;
      },
      (error) => {
        console.error('Error fetching OTC History:', error);
        this.isLoading = false;
      }
    );
  }

  fetchBillDocs(): void {
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    const url = environment.apiUrl + '/api/OTCRC/v1/getnonbilldocs'; // API endpoint
    const requestBody = {
      i_non_bil_id: this.modelData.non_bil_id,
    };

    this.http.post(url, requestBody, { headers }).subscribe(
      (response: any) => {
        this.supportingDocs = response?.data || []; // Store the received data
        console.log(this.supportingDocs);
        this.isLoading = false;
      },
      (error) => {
        console.error('Error fetching Docs:', error);
        this.isLoading = false;
      }
    );
  }

  downloadFileContent(fileName: string, fileContent: string, mimeType: string): void {
    const binaryString = window.atob(fileContent); // Decode base64 content
    const len = binaryString.length;
    const uint8Array = new Uint8Array(len);
  
    for (let i = 0; i < len; i++) {
      uint8Array[i] = binaryString.charCodeAt(i); // Convert to byte array
    }
  
    // Use the provided MIME type dynamically
    const blob = new Blob([uint8Array], { type: mimeType });
    const url = URL.createObjectURL(blob);
  
    const anchor = document.createElement('a');
    anchor.href = url;
    anchor.download = fileName; // Use the provided file name
  
    document.body.appendChild(anchor);
    anchor.click(); // Trigger download
  
    document.body.removeChild(anchor);
    URL.revokeObjectURL(url); // Clean up the object URL
    this.isLoading = false;
  }
  
  downloadFile(bil_doc_id: number, file_nm: string): void {
    const url = environment.apiUrl + '/api/OTCRC/v1/getnonbilldocscontent';
  
    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });
  
    const Body: any = {
      i_non_bil_doc_id: bil_doc_id,
    };
  
    this.http.post(url, Body, { headers }).subscribe(
      (response: any) => {
        const fileContent = response.data;
        const mimeType = response.mimeType || 'application/octet-stream'; // Fallback MIME type
        this.downloadFileContent(file_nm, fileContent, mimeType);
  
        if (!fileContent || fileContent.length === 0) {
          this.totalRecords = 0;
          this.isLoading = false;
        } else {
          this.totalRecords = response.data[0]?.total || 0;
          this.isLoading = false;
        }
      },
      (error) => {
        console.error(error);
        this.isLoading = false;
      }
    );
  }
  

  navigateToAdjustmentListingScreen(): void {
    this.router.navigate(['/billing-adjustment-search']);
  }
}
