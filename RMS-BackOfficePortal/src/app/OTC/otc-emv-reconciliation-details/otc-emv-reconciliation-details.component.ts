import { HttpClient, HttpHeaders } from '@angular/common/http';
import { ChangeDetectorRef, Component } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { Router, ActivatedRoute } from '@angular/router';
import { DatePipe, formatDate } from '@angular/common';
import { NgbPaginationConfig } from '@ng-bootstrap/ng-bootstrap';
import { TranslateService } from '@ngx-translate/core';
import { AuthService } from 'src/app/core/services/auth.service';
import { GlobalService } from 'src/app/shared/global.service';
import { environment } from 'src/environments/environment';
import { ParamService } from 'src/app/core/services/param.service';
import { perm } from 'src/permissions/perm';
import { jsPDF } from "jspdf";
import "jspdf-autotable";

import {
  OTCEMVReconciliation,
  OTCEMVReconciliationCheck,
  OTCEMVReconciliationStatus,
} from 'src/app/core/models/otc-emv-reconciliation.interface';
import {
  OTCEMVReconciliationSummary,
  OTCEMVReconciliationRC,
  OTCEMVReconciliationSettlement,
  OTCEMVReconciliationSettlement2,
  OTCEMVReconciliationSettlement3,
} from 'src/app/core/models/otc-emv-reconciliation.interface';

@Component({
  selector: 'app-otc-emv-reconciliation-details',
  templateUrl: './otc-emv-reconciliation-details.component.html',
  styleUrls: ['./otc-emv-reconciliation-details.component.scss'],
})
export class OtcEmvReconciliationDetailsComponent {
  isLoading: boolean = false;
  totalRecords: number = 0;

  page = environment.DefaultPage;
  itemsPerPage = environment.ItemPerPage;

  permList =
    perm.OTC_EMV_Reconciliation_View_Details_Page +
    ',' +
    perm.OTC_EMV_Reconciliation_Perform_Reconciliation;
  permListResponse = '';
  permViewAllow: number = 0; // if 0 then not allow to view listing page, else allow
  permActionAllow: number = 0;

  stateModel: any;

  balancingDate: string | null = null;
  balancingDateForce: string | null = null;

  rcEmvStatus: OTCEMVReconciliationStatus[] = [];
  summaryModel: OTCEMVReconciliationSummary[] = [];
  rcModel: OTCEMVReconciliationRC[] = [];
  settlementModel: OTCEMVReconciliationSettlement[] = [];
  settlementModel2: OTCEMVReconciliationSettlement2[] = [];
  settlementModel3: OTCEMVReconciliationSettlement3[] = [];

  rcEmvStatusId: number | null = null;
  rcEmvStatusStatus: string | null = null;
  summaryBranchCount: number | null = null;
  summaryDatePeriod: Date | null = null;
  summaryEmvSettlementCount: number | null = null;
  summaryEmvTransactionCount: number | null = null;
  summaryEmvAmount: number | null = null;
  summaryReceiptsCancelledCount: number | null = null;

  total: number = 0;
  total2: number = 0;
  total3: number = 0;

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
    public datepipe: DatePipe
  ) {
    config.maxSize = environment.PaginationMaxSize;
    config.boundaryLinks = true;
    this.translate.setDefaultLang(this.globalService.getGlobalValue());
    this.translate.use(this.globalService.getGlobalValue());

    // Params
    this.route.queryParams.subscribe((params) => {
      this.balancingDateForce = params['balancingdate'];
    });
    //console.log('Load Param: ' + this.balancingDateForce);

    // State
    const navigation = this.router.getCurrentNavigation();
    this.stateModel = navigation?.extras.state?.['item'];
    this.balancingDate = navigation?.extras.state?.['balancingdate'];
    //console.log('Load State: ' + this.balancingDate);
  }

  ngOnInit() {
    this.authorization();
    this.loadForce();
    this.getRcEmvStatus();
    this.loadOTCEMVReconciliationSummary();
    this.loadOTCEMVReconciliationRC();
    this.loadOTCEMVReconciliationSettlement();
  }

  authorization() {
    this.authService
      .checkUserRole(this.authService.username, this.permList)
      .subscribe(
        (response: any) => {
          this.permListResponse = response.data;
          this.permViewAllow = this.permListResponse.includes(
            perm.OTC_EMV_Reconciliation_View_Details_Page
          )
            ? 1
            : 0;
          this.permActionAllow = this.permListResponse.includes(
            perm.OTC_EMV_Reconciliation_Perform_Reconciliation
          )
            ? 1
            : 0;

          //console.log('AuthList: ' + this.permViewAllow, this.permActionAllow);

          if (this.permViewAllow === 0) {
            console.log('access-denied');
            this.router.navigate(['/access-denied']);
            return;
          }

          //console.log('AuthResp: ' + this.permViewAllow, this.permActionAllow);
        },
        (error: any) => {
          console.log(error);
        }
      );
  }

  loadForce() {
    if (this.balancingDateForce) {
      this.balancingDate = this.balancingDateForce;
    }
  }

  getRcEmvStatus() {
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    this.isLoading = true;
    const url = environment.apiUrl + '/api/OTCEMVR/v1/getrcemv';

    const Body: any = {
      i_date_from: this.balancingDate + ' 00:00:00',
      i_date_to: this.balancingDate + ' 23:59:59',
    };

    //console.log(Body);

    this.http.post(url, Body, { headers }).subscribe(
      (response: any) => {
        this.rcEmvStatus = response.data;
        this.extractStatus();
        this.loadOTCEMVReconciliationSettlement2();
      },
      (error) => {
        console.error(error);
        this.isLoading = false;
      }
    );
  }

  extractStatus(): void {
    this.rcEmvStatusId = this.rcEmvStatus[0].rc_emv_id;
    this.rcEmvStatusStatus = this.rcEmvStatus[0].rc_emv_status;
  }

  updateRcEmvStatus(status: string) {
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    const url = environment.apiUrl + '/api/OTCEMVR/v1/updrcemv';

    const Body: any = {
      i_dt_balancing: this.balancingDate + ' 12:00:00',
      i_rc_emv_status: status,
    };

    //console.log(Body);

    this.http.post(url, Body, { headers }).subscribe(
      (response: any) => {
        // console.log("updateRcEmvStatus: ", response);
        this.refresh();
      },
      (error) => {
        console.error(error);
        this.isLoading = false;
      }
    );
  }

  loadOTCEMVReconciliationSummary(): void {
    this.isLoading = true;
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    const url =
      environment.apiUrl + '/api/OTCEMVR/v1/getotcemvreconciliationsummary';
    const requestBody = {
      i_date_from: this.balancingDate + ' 00:00:00',
      i_date_to: this.balancingDate + ' 23:59:59',
    };

    this.http.post(url, requestBody, { headers }).subscribe(
      (response: any) => {
        this.summaryModel = response?.data || [];
        this.isLoading = false;
        // console.log("summaryModel: ", this.summaryModel);
        this.extractSummaryData();
      },
      (error) => {
        console.error('Error: ', error);
        this.isLoading = false;
      }
    );
  }

  extractSummaryData(): void {
    this.summaryBranchCount = this.summaryModel[0].branch_count;
    this.summaryDatePeriod = this.summaryModel[0].date_period;
    this.summaryEmvSettlementCount = this.summaryModel[0].emv_settlement_count;
    this.summaryEmvTransactionCount =
      this.summaryModel[0].emv_transaction_count;
    this.summaryEmvAmount = this.summaryModel[0].emv_amt;
    this.summaryReceiptsCancelledCount =
      this.summaryModel[0].receipts_cancelled_count;
  }

  getFormattedDate() {
    return this.datepipe.transform(this.balancingDate, 'dd MMM yyyy');
  }

  loadOTCEMVReconciliationRC(): void {
    this.isLoading = true;
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    const url =
      environment.apiUrl + '/api/OTCEMVR/v1/getotcemvreconciliationrc';
    const requestBody = {
      i_date_from: this.balancingDate + ' 00:00:00',
      i_date_to: this.balancingDate + ' 23:59:59',
    };

    this.http.post(url, requestBody, { headers }).subscribe(
      (response: any) => {
        this.rcModel = response?.data || [];
        this.isLoading = false;
        //console.log('rcModel: ', this.rcModel);
      },
      (error) => {
        console.error('Error: ', error);
        this.isLoading = false;
      }
    );
  }

  loadOTCEMVReconciliationSettlement(): void {
    this.isLoading = true;
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    const url =
      environment.apiUrl + '/api/OTCEMVR/v1/getotcemvreconciliationsettlement';
    const requestBody = {
      i_date_from: this.balancingDate + ' 00:00:00',
      i_date_to: this.balancingDate + ' 23:59:59',
    };

    this.http.post(url, requestBody, { headers }).subscribe(
      (response: any) => {
        this.settlementModel = response?.data || [];
        this.isLoading = false;
        // console.log("settlementModel: ", this.settlementModel);
        this.calculateTotal();
      },
      (error) => {
        console.error('Error: ', error);
        this.isLoading = false;
      }
    );
  }

  loadOTCEMVReconciliationSettlement2(): void {
    this.isLoading = true;
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    const url =
      environment.apiUrl + '/api/OTCEMVR/v1/getotcemvreconciliationsettlement2';
    const requestBody = {
      i_rc_emv_id: this.rcEmvStatusId,
    };

    this.http.post(url, requestBody, { headers }).subscribe(
      (response: any) => {
        this.settlementModel3 = response?.data || [];
        this.isLoading = false;
        // console.log("settlementModel3: ", this.settlementModel3);
        this.calculateTotal3();
      },
      (error) => {
        console.error('Error: ', error);
        this.isLoading = false;
      }
    );
  }

  downloadFileContent(
    fileName: string,
    fileContent: string,
    mimeType: string
  ): void {
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

  downloadFile(otc_bal_doc_id: number, file_nm: string): void {
    const url = environment.apiUrl + '/api/OTCEMVR/v1/getotcbaldoccontent';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    const Body: any = {
      i_otc_bal_doc_id: otc_bal_doc_id,
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

  downloadFile2(rc_emv_doc_id: number, file_nm: string): void {
    const url = environment.apiUrl + '/api/OTCEMVR/v1/getrcemvdoccontent';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    const Body: any = {
      i_rc_emv_doc_id: rc_emv_doc_id,
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

  addItem() {
    this.settlementModel2.push({
      file: new File([''], 'placeholder.txt', { type: 'text/plain' }),
      file_nm: '',
      debit_card_lines: 0,
      debit_card_amount: 0,
      credit_card_lines: 0,
      credit_card_amount: 0,
      total_amt: 0,
    });
  }

  deleteItem(index: number) {
    this.settlementModel2.splice(index, 1);
    this.calculateTotal2();
  }

  selectFile(index: number) {
    const inputElement = document.createElement('input');
    inputElement.type = 'file';
    inputElement.click();

    inputElement.onchange = () => {
      const file = inputElement.files?.[0];
      if (file) {
        this.settlementModel2[index].file = file;
        this.settlementModel2[index].file_nm = file.name;

        const reader = new FileReader();
        reader.onload = (e: any) => {
          const fileContent = e.target.result;
          const { debitAmounts, creditAmounts } =
            this.extractDebitCredit(fileContent);

          this.settlementModel2[index].debit_card_lines = debitAmounts.length;
          this.settlementModel2[index].credit_card_lines = creditAmounts.length;
          this.settlementModel2[index].debit_card_amount = debitAmounts.reduce(
            (total, amount) => total + amount,
            0
          );
          this.settlementModel2[index].credit_card_amount =
            creditAmounts.reduce((total, amount) => total + amount, 0);
          this.onAmountChange(index);
        };
        reader.readAsText(file);
      }
    };
  }

  extractDebitCredit(fileContent: string): {
    debitAmounts: number[];
    creditAmounts: number[];
  } {
    const debitAmounts: number[] = [];
    const creditAmounts: number[] = [];
    const lines = fileContent.split('\n');
    const debitRegex = /\bDR\/CARD.*?(\d{1,3}(?:,\d{3})*\.\d{2})/;
    const creditRegex = /\bCR\/CARD.*?(\d{1,3}(?:,\d{3})*\.\d{2})/;

    lines.forEach((line) => {
      const debitMatch = line.match(debitRegex);
      if (debitMatch) {
        debitAmounts.push(parseFloat(debitMatch[1].replace(/,/g, '')));
      }

      const creditMatch = line.match(creditRegex);
      if (creditMatch) {
        creditAmounts.push(parseFloat(creditMatch[1].replace(/,/g, '')));
      }
    });

    // console.log("Debit Amounts:", debitAmounts);
    // console.log("Credit Amounts:", creditAmounts);

    return { debitAmounts, creditAmounts };
  }

  onAmountChange(index: number): void {
    const debitAmount = this.settlementModel2[index].debit_card_amount || 0;
    const creditAmount = this.settlementModel2[index].credit_card_amount || 0;
    const totalAmount = debitAmount + creditAmount;
    this.settlementModel2[index].total_amt = totalAmount;

    this.calculateTotal2();
  }

  calculateTotal(): void {
    this.total = this.settlementModel.reduce(
      (sum, item) => sum + item.batch_amt,
      0
    );
  }

  calculateTotal2(): void {
    this.total2 = this.settlementModel2.reduce(
      (sum, item) => sum + item.total_amt,
      0
    );
  }

  calculateTotal3(): void {
    this.total3 = this.settlementModel3.reduce(
      (sum, item) => sum + item.total,
      0
    );
  }

  checkSubmit() {
    if (this.total !== this.total2) {
      return true;
    }
    return false;
  }

  back() {
    this.router.navigate(['/otc-emv-reconciliation']);
  }

  cancel() {
    this.router.navigate(['/otc-emv-reconciliation']);
    this.updateRcEmvStatus('P');
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
      otc_type: 'e',
      dt_balancing: this.balancingDate
    };

    // Fire and forget - no handling of response or error
    this.http.post(url, Body, { headers }).subscribe(
      () => {}, // Ignore the response
      () => {}  // Ignore the error
    );
  }

  async submit() {
    this.isLoading = true;
    const fileUploadSuccess = await this.readFileAsync();
    if (!fileUploadSuccess) {
      alert('Some files failed to upload. Please check and try again.');
      this.isLoading = false;
      return;
    }
    
    this.submitFMS();
    this.updateRcEmvStatus('C');
  }

  refresh() {
    window.location.reload();
  }

  navigateToIndividual(item: any): void {
    const mtt_id = item.mtt_id;
    const otc_id = item.otc_id;
    const otc_counter_id = item.otc_counter_id;
    const counter_id = item.counter_id;
    const otc_pymt_mode = item.otc_pymt_mode;
    this.router.navigate(['/otc-rcpt-dets'], {
      state: { mtt_id, otc_id, otc_counter_id, counter_id, otc_pymt_mode },
    });
  }

  async readFileAsync(): Promise<boolean> {
    let result = true;

    // Upload Documents
    for (const item of this.settlementModel2) {
      const success = await this.uploadFile(item);
      if (!success) result = false;
    }

    return result;
  }

  async uploadFile(item: OTCEMVReconciliationSettlement2): Promise<boolean> {
    const base64Content = await new Promise<string>((resolve, reject) => {
      const reader = new FileReader();
      reader.onload = (e: any) => resolve(e.target.result);
      reader.onerror = reject;
      reader.readAsDataURL(item.file);
    });

    const requestBody = {
      i_dt_balancing: this.balancingDate + ' 12:00:00',
      i_file_nm: item.file.name,
      i_file_content: base64Content,
      i_file_type: item.file.type,
      i_file_size: item.file.size,
      i_dr_count: item.debit_card_lines,
      i_dr_amt: item.debit_card_amount,
      i_cr_count: item.debit_card_lines,
      i_cr_amt: item.credit_card_amount,
      i_total: item.total_amt,
      i_rc_emv_id: this.rcEmvStatusId,
    };

    //console.log(requestBody);

    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    try {
      await this.http
        .post(environment.apiUrl + '/api/OTCEMVR/v1/insrcemvdoc', requestBody, {
          headers,
        })
        .toPromise();
      //console.log(`File ${item.file.name} uploaded successfully.`);
      return true;
    } catch (error) {
      //console.error(`Error uploading file ${item.file.name}:`, error);
      return false;
    }
  }

  downloadReport(): void {
      const doc = new jsPDF();
      let isFirstPage = true; // Track if it's the first page to avoid unnecessary page breaks
  
      const dataToExport = [
          { 
            title: 'Daily Balancing Summary',
            data:
              this.summaryModel.length > 0
                ? this.summaryModel.map((item) => ({
                    'No. of Branches': item.branch_count,
                    'Balancing Date Period': this.datepipe.transform(
                      item.date_period,
                      'dd MMM yyyy'
                    ),
                    'No. of Settlements': item.emv_settlement_count,
                    'No. of EMV Transactions': item.emv_transaction_count,
                    'Total EMV (RM)': item.emv_amt.toFixed(2),
                    'No. of Receipts Cancelled': item.receipts_cancelled_count,
                  }))
                : [
                    {
                      'No. of Branches': '',
                      'Balancing Date Period': '',
                      'No. of Settlements': '',
                      'No. of EMV Transactions': '',
                      'Total EMV (RM)': '',
                      'No. of Receipts Cancelled': '',
                    },
                  ],
          },
          {
            title: 'Receipt Cancellation Table',
            data:
              this.rcModel.length > 0
                ? this.rcModel.map((item) => ({
                    'Branch Code': item.branch_cd,
                    'Collection Slip No.': item.coll_slip_no,
                    'Order Reference No.': item.orn_no,
                    'Receipt No.': item.rcpt_no,
                    'Total Amount (RM)': item.amount.toFixed(2),
                    'Payment Mode': item.payment_mode,
                    'Requested By': item.requested_by,
                    'Approved By': item.approved_by,
                    'Receipt Cancellation Reason': item.reason,
                  }))
                : [
                    {
                      'Branch Code': '',
                      'Collection Slip No.': '',
                      'Order Reference No.': '',
                      'Receipt No.': '',
                      'Total Amount (RM)': '',
                      'Payment Mode': '',
                      'Requested By': '',
                      'Approved By': '',
                      'Receipt Cancellation Reason': '',
                    },
                  ],
          },
          {
            title: 'EMV Settlement Information',
            data:
              this.settlementModel.length > 0
                ? this.settlementModel.map((item) => ({
                    'Branch Code': item.branch_cd,
                    'File Upload': item.file_nm,
                    'Terminal ID': item.terminal_id,
                    'Settlement Datetime': item.date,
                    'Batch Number': item.batch_no,
                    'Batch Count': item.batch_count,
                    'Batch Amount (RM)': item.batch_amt.toFixed(2),
                  }))
                : [
                    {
                      'Branch Code': '',
                      'File Upload': '',
                      'Terminal ID': '',
                      'Settlement Datetime': '',
                      'Batch Number': '',
                      'Batch Count': '',
                      'Batch Amount (RM)': '',
                    },
                  ],
          },
          {
            title: 'Master Settlement Information',
            data:
              this.settlementModel3.length > 0
                ? this.settlementModel3.map((item) => ({
                    'File Upload': item.file_nm,
                    'Debit Card Lines Extracted': item.dr_count,
                    'Debit Card Amount (RM)': item.dr_amt.toFixed(2),
                    'Credit Card Lines Extracted': item.cr_count,
                    'Credit Card Amount (RM)': item.cr_amt.toFixed(2),
                    'Total Amount (RM)': item.total.toFixed(2),
                  }))
                : [
                    {
                      'File Upload': '',
                      'Debit Card Lines Extracted': '',
                      'Debit Card Amount (RM)': '',
                      'Credit Card Lines Extracted': '',
                      'Credit Card Amount (RM)': '',
                      'Total Amount (RM)': '',
                    },
                  ],
          },
          {
            title: 'EMV vs Master Settlement Total',
            data: [
              {
                'EMV Settlement Total (RM)': this.total.toFixed(2),
                'Master Settlement Total (RM)': this.total3.toFixed(2),
              },
            ],
          },  
      ];
  
      dataToExport.forEach((section, index) => {
          if (!isFirstPage) {
              doc.addPage(); // Create a new page for each section
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
      const filename = `OTC_EMV_Reconciliation_Report_${this.balancingDate}.pdf`;
  
      // Save the PDF
      doc.save(filename);
  }
}
