import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { environment } from 'src/environments/environment';
import { NgbPaginationConfig } from '@ng-bootstrap/ng-bootstrap';
import { Router } from '@angular/router';
import { bankReconDetail } from '../core/models/bank-recon-details.interface';
import { MatDialog } from '@angular/material/dialog';
import { ParamService } from '../core/services/param.service';

import { perm } from 'src/permissions/perm';
import { AuthService } from 'src/app/core/services/auth.service';

import { TranslateService } from '@ngx-translate/core';
import { GlobalService } from 'src/app/shared/global.service';
import { AgBankStmtListingComponent } from '../ag-bank-stmt-listing/ag-bank-stmt-listing.component';
import { AgBankTxnListingComponent } from '../ag-bank-txn-listing/ag-bank-txn-listing.component';
import { AgPgFileTxnListingComponent } from '../ag-pg-file-txn-listing/ag-pg-file-txn-listing.component';
import { AgDeleteFileDiaglogComponent } from '../ag-delete-file-diaglog/ag-delete-file-diaglog.component';

@Component({
  selector: 'app-non-rms-receipting-details',
  templateUrl: './non-rms-receipting-details.component.html',
  styleUrls: ['./non-rms-receipting-details.component.scss']
})
export class NonRmsReceiptingDetailsComponent {
  page = environment.DefaultPage;
  itemsPerPage = environment.ItemPerPage;

  modelData: any;
  fileContent: string = '';
  transactions: any[] = [];
  fileUploads: {
    id: number;
    fileContent: string;
    transactions: any[];
    uploaded: boolean;
    fileName: string;
    fileSize: number;
    text_content: string;
    ag_doc_id?: number;
    bank_stmt_count?: number; // From API
    bank_stmt_trans?: number; // From API
    pg_settlement_trans?: number; // From API
    total_pg_amt?: number; // From API // Store ag_doc_id after upload
  }[] = [];
  // Fixed column positions
  columnConfig = [
    { key: 'acct_no', start: 0, end: 51 },
    { key: 'acct_type', start: 51, end: 102 },
    { key: 'acct_nm', start: 102, end: 152 },
    { key: 'dt_fr', start: 204, end: 214 },
    { key: 'dt_to', start: 255, end: 266 },
    { key: 'total_debit', start: 306, end: 357 },
    { key: 'total_credit', start: 357, end: 408 },
    { key: 'begin_bal', start: 408, end: 459 },
    { key: 'end_bal', start: 459, end: 510 },
    { key: 'dt_txn', start: 510, end: 561 },
    { key: 'time_txn', start: 561, end: 566 },
    { key: 'dt_posting', start: 612, end: 623 },
    { key: 'time_posting', start: 663, end: 668 },
    { key: 'txn_desc', start: 714, end: 765 },
    { key: 'txn_ref', start: 765, end: 816 },
    { key: 'debit', start: 816, end: 867 },
    { key: 'credit', start: 867, end: 918 },
    { key: 'source_cd', start: 918, end: 969 },
    { key: 'teller_id', start: 969, end: 1020 },
    { key: 'brn_chn', start: 1020, end: 1071 },
    { key: 'txn_cd', start: 1071, end: 1122 },
    { key: 'end_bal2', start: 1122, end: 1224 },
    { key: 'virtual_acct', start: 1224, end: 1275 },
    { key: 'txn_desc2', start: 1275, end: 1326 },
    { key: 'txn_desc3', start: 1326, end: 1377 },
    { key: 'txn_desc4', start: 1377, end: 1428 },
    { key: 'dt_expiry', start: 1428, end: 1478 }
  ];

  isDisplay: boolean = false;
  isLoading: boolean = false;
  isDisplayTaskLists: boolean = false;
  isLoadingTaskLists: boolean = false;
  br: bankReconDetail[] = [];
  task_no: any;
  totalRecords: number = 0;

  file_nm: string | null = null;
  file_content: string | null = null;
  total_no_pg_txn: number | null = null;
  total_gross_amt: number | null = null;
  total_mdr: number | null = null;
  total_net_amt: number | null = null;
  total_no_bk_txn: number | null = null;
  total_bank_txn: number | null = null;
  total_pg_file_txn: number | null = null;
  total_pg_disbursed_amt: number | null = null;
  task_status: string | null = null;
  recon_status: string | null = null;
  stmt_no: string | null = null;
  dt_settlement: Date | null = null;
  remarks: string | null = null;

  totalBankStmtTransactions: number = 0;
  totalPGSettlementTransactions: number = 0;
  totalPGDisbursedAmount: number = 0;

  // Configuring Permissions for User and roles variables
  permBR = perm.BP_036_Reconciliation_Of_Collection_View_Bank_Statement_Transaction_List + "," +
    perm.BP_036_Reconciliation_Of_Collection_View_PG_Transaction_List_PG_Bank + "," +
    perm.BP_036_Reconciliation_Of_Collection_Download_Bank_Statement_File + "," +
    perm.BP_036_Reconciliation_Of_Collection_Download_PG_Settlement_File_PG_Bank + "," +
    perm.BP_036_Reconciliation_Of_Collection_Open_Close_Add_Remarks_PG_Bank

    ; // all the perm_cd for this module seperated with comma
  permBRAllow = ""; // variable to store allowed permission for the user
  permBankTransListAllow: number = 0; // if 0 then not allow to view listing page, else allow
  permViewPGTransAllow: number = 0;
  permDLBankAllow: number = 0;
  permDLPGBankAllow: number = 0;
  permOpenCloseCancelAllow: number = 0;

  // permUploadAllow: number = 0;
  // permDownloadAllow: number = 0;
  // permCancelViewAllow: number = 0;
  // permReconViewAllow: number = 0;
  // end configuration

  //Pop up Box
  pgTxnBox: boolean = false;
  bankTxnBox: boolean = false;

  result: number | null = 0;
  colorResult: boolean = false;

  choices: string[] = ['Open', 'Close'];
  isReadOnly: boolean = false;

  constructor(
    private http: HttpClient,
    config: NgbPaginationConfig,
    private router: Router,
    public dialog: MatDialog,
    private ParamService: ParamService,
    private cd: ChangeDetectorRef,
    private translateService: TranslateService,
    private globalService: GlobalService,
    private authService: AuthService
  ) {
    config.maxSize = environment.PaginationMaxSize;
    config.boundaryLinks = true;
    this.translateService.setDefaultLang(this.globalService.getGlobalValue());
    this.translateService.use(this.globalService.getGlobalValue());

    const navigation = this.router.getCurrentNavigation();
    this.modelData = navigation?.extras.state?.['item']; // Retrieve the passed data
  }

  ngOnInit(): void {
    this.task_no = history.state.task_id;
    this.getExistingUploadedDocuments(); // Load existing uploaded documents
    this.calculateTotalFilesUploaded(); // Update file count

    this.isReadOnly = this.modelData?.task_status === 'COMPLETED';
    this.remarks = this.modelData?.remarks;

    if (this.modelData.task_status === "OPEN") {
      this.task_status = "Open"; // Match the radio button value
    } else if (this.modelData.task_status === "COMPLETED") {
      this.task_status = "Close"; // Match the radio button value
    }

  }

  showDeleteAlert = false;
  deleteMessage: string = '';

  showDeleteAlertBox(message: string) {
    this.deleteMessage = message; // Store the message
    this.showDeleteAlert = true;
    setTimeout(() => (this.showDeleteAlert = false), 10000);
  }

  showDeleteErrorAlert = false;
  showDeleteErrorAlertBox() {
    this.showDeleteErrorAlert = true;
    setTimeout(() => (this.showDeleteAlert = false), 10000);
  }

  showUploadErrorAlert = false;
  uploadErrorMessage: string = '';

  showUploadErrorAlertBox(message: string) {
    this.uploadErrorMessage = message; // Store the message
    this.showUploadErrorAlert = true;
    setTimeout(() => (this.showUploadErrorAlert = false), 10000);
  }

  showUploadSuccessAlert = false;
  uploadSuccessMessage: string = '';
  showUploadSuccessAlertBox(message: string) {
    this.uploadSuccessMessage = message;
    this.showUploadSuccessAlert = true;
    setTimeout(() => (this.showUploadSuccessAlert = false), 10000);
  }

  addFileRow(): void {
    const newFileUpload = { id: this.fileUploads.length + 1, fileContent: '', transactions: [], uploaded: false, fileName: '', fileSize: 0, text_content: '' };
    this.fileUploads.push(newFileUpload);
  }

  uploadedFiles: Set<string> = new Set(); // Store unique file names

  async onFileUpload(event: any, fileUpload: any): Promise<void> {

    const fileInput = event.target;
    const file = fileInput.files[0];

    if (!file) {
      this.showUploadErrorAlertBox('No file selected!')
      return;
    }

    // Check for duplicate file name
    if (this.uploadedFiles.has(file.name)) {
      this.showUploadErrorAlertBox('This file has already been uploaded! Please choose a different file.')
      // **Clear the file input field**
      fileInput.value = '';  // Reset file input

      return;
    }

    // Add the file name to the set to prevent future duplicates
    this.uploadedFiles.add(file.name);

    // Read file as Base64 (same behavior as uploadFile function)
    const base64Content = await new Promise<string>((resolve, reject) => {
      const reader = new FileReader();
      reader.onload = (e: any) => resolve(e.target.result);
      reader.onerror = reject;
      reader.readAsDataURL(file);
    });

    fileUpload.fileName = file.name;
    fileUpload.fileSize = Math.round(file.size / 1024); // Convert to KB
    fileUpload.fileContent = base64Content; // Store Base64-encoded content
    fileUpload.uploaded = false; // Mark as not yet uploaded

    // // Read file as Base64 for API upload
    // const readerForBase64 = new FileReader();
    // readerForBase64.onload = (e) => {
    //   fileUpload.fileName = file.name;
    //   fileUpload.fileSize = Math.round(file.size / 1024); // Convert to KB
    //   fileUpload.fileContent = btoa(e.target?.result as string); // Convert to Base64
    //   // fileUpload.uploaded = true;
    //   fileUpload.uploaded = false;
    // };
    // readerForBase64.readAsDataURL(file); // Read as Base64

    // Read file as text for data extraction
    const readerForText = new FileReader();
    readerForText.onload = (e) => {
      fileUpload.text_content = e.target?.result as string || ''; // Store plain text
      this.processFileContent(fileUpload); // Extract transaction data
      // this.getTotalCounts();
      // this.calculateTotalFilesUploaded();
    };
    readerForText.readAsText(file); // Read as text
  }

  processFileContent(fileUpload: any): void {
    const lines = fileUpload.text_content.split('\n').slice(1);
    fileUpload.transactions = [];

    lines.forEach((line: string) => {
      if (line.trim().length > 0) {
        const transaction: any = {};
        this.columnConfig.forEach(col => {
          transaction[col.key] = line.substring(col.start, col.end).trim();
        });

        // Convert debit and credit to numbers
        // transaction.debit = parseFloat(transaction.debit) || 0;
        // transaction.credit = parseFloat(transaction.credit) || 0;

        // 251014- Handle "," inside credit and debit field
        transaction.debit = (transaction.debit === "-" ? 0 : parseFloat((transaction.debit || "0").toString().replace(/,/g, "")) || 0);
        transaction.credit = (transaction.credit === "-" ? 0 : parseFloat((transaction.credit || "0").toString().replace(/,/g, "")) || 0);
        transaction.fileName = fileUpload.fileName;
        fileUpload.transactions.push(transaction);
        this.transactions.push(transaction);
      }
    });
  }

  removeFileRow(id: number): void {
    // Find the file being removed
    const fileToRemove = this.fileUploads.find(file => file.id === id);

    if (fileToRemove) {
      this.uploadedFiles.delete(fileToRemove.fileName); // Remove the file name from the Set
    }

    // Remove the file from the fileUploads array
    this.fileUploads = this.fileUploads.filter(file => file.id !== id);

    // this.getTotalCounts();
    // this.calculateTotalFilesUploaded(); // Update file count
  }

  getTotal(transactions: any[], key: string): number {
    return transactions.reduce((sum, txn) => sum + (parseFloat(txn[key]) || 0), 0);
  }

  getPGSettlementCount(fileUpload: any): number {
    return fileUpload.transactions ? fileUpload.transactions.filter((txn: any) => txn.txn_ref.includes(this.modelData.stmt_no)).length : 0;
  }

  getTotalPGDisbursedAmounts(fileUpload: any): number {
    return fileUpload.transactions
      ? fileUpload.transactions
        .filter((txn: any) => txn.txn_ref.includes(this.modelData.stmt_no)) // Filter transactions matching stmt_no
        .reduce((sum: number, txn: any) => sum + txn.credit, 0) // Sum the filtered transactions' credits
      : 0;
  }

  getTotalCounts() {
    this.totalBankStmtTransactions = this.fileUploads.reduce(
      (sum: number, fileUpload: any) =>
        sum + (fileUpload.uploaded ? fileUpload.bank_stmt_trans || 0 : fileUpload.transactions.length),
      0
    );

    this.totalPGSettlementTransactions = this.fileUploads.reduce(
      (sum: number, fileUpload: any) =>
        sum + (fileUpload.uploaded ? fileUpload.pg_settlement_trans || 0 : this.getPGSettlementCount(fileUpload)),
      0
    );

    this.totalPGDisbursedAmount = this.fileUploads.reduce(
      (sum: number, fileUpload: any) =>
        sum + (fileUpload.uploaded ? fileUpload.total_pg_amt || 0 : this.getTotalPGDisbursedAmounts(fileUpload)),
      0
    );

    if (parseFloat(this.totalPGDisbursedAmount.toFixed(2)) === parseFloat(this.modelData.total_net_amt.toFixed(2))) {
      this.colorResult = true;
    }
  }


  // Function to calculate the total number of uploaded files
  // calculateTotalFilesUploaded() {
  //   this.total_no_bk_txn = this.fileUploads.filter(file => file.uploaded).length;
  // }

  calculateTotalFilesUploaded() {
    this.total_no_bk_txn = this.fileUploads.length;
  }

  // submitFiles() {
  //   if (this.fileUploads.length === 0) {
  //     this.showUploadErrorAlertBox('No files to upload!');
  //     return;
  //   }

  //   // Filter only uploaded files
  //   const uploadedFiles = this.fileUploads.filter(file => !file.uploaded);
  //   console.log(uploadedFiles);

  //   if (uploadedFiles.length === 0) {
  //     this.updateAgSale();
  //     return;
  //   }

  //   const requestBody = uploadedFiles.map(file => ({
  //     i_ag_sale_id: this.modelData.ag_sale_id, // Replace with actual sale ID
  //     i_ag_type: "B", // Replace with actual type
  //     i_file_name: file.fileName || '',
  //     i_file_content: file.fileContent, // Base64 file content
  //     i_file_type: 'TXT', // Set based on actual file type
  //     i_file_size_kb: file.fileSize || 0,
  //     i_settle_status: this.task_status === 'Open' ? 'PBSR' : 'SBSR',
  //     i_task_status: this.task_status === 'Open' ? 'O' : 'C',
  //     i_remark: this.remarks
  //   }));

  //   // Define API endpoint
  //   this.isLoading = true;
  //   const apiUrl = environment.apiUrl + '/api/RMSNR/v1/insagsaledoc';

  //   const headers = new HttpHeaders({
  //     Authorization: environment.authKey,
  //     'Content-Type': 'application/json',
  //   });

  //   // Make API call
  //   this.http.post(apiUrl, requestBody, { headers }).subscribe(
  //     (response: any) => {
  //       console.log('Upload successful:', response);
  //       this.showUploadSuccessAlertBox('Files uploaded successfully!');

  //       uploadedFiles.forEach((file, index) => {
  //         file.ag_doc_id = response.data[index]; // Assign received ag_doc_id
  //       });

  //       // Assign ag_doc_id to each transaction based on file mapping
  //       this.transactions.forEach(txn => {
  //         const matchingFile = this.fileUploads.find(file => file.fileName === txn.fileName);
  //         if (matchingFile && matchingFile.ag_doc_id) {
  //           txn.ag_doc_id = matchingFile.ag_doc_id;
  //         }
  //       });

  //       this.getTotalCounts(); // Refresh total calculations
  //       this.calculateTotalFilesUploaded(); // Update file count
  //       this.submitTransactions(); // Insert transactions
  //     },
  //     error => {
  //       console.error('Error uploading files:', error);
  //       this.showUploadErrorAlertBox('Error uploading files. Please try again.');
  //       this.isLoading = false;
  //     }
  //   );
  // }

  uploadFile(fileUpload: any) {
    if (!fileUpload || !fileUpload.fileContent) {
      this.showUploadErrorAlertBox('No file selected for upload!');
      return;
    }
  
    const requestBody = {
      i_stmt_no: this.modelData.stmt_no, // Replace with actual sale ID
      i_ag_type: "B", // Replace with actual type
      i_file_name: fileUpload.fileName || '',
      i_file_content: fileUpload.fileContent, // Base64 file content
      i_file_type: 'TXT', // Set based on actual file type
      i_file_size_kb: fileUpload.fileSize || 0,
      i_settle_status: this.task_status === 'Open' ? 'PBSR' : 'SBSR',
      i_task_status: this.task_status === 'Open' ? 'O' : 'C',
      i_remark: this.remarks? this.remarks : ''
    };
  
    this.isLoading = true;
    const apiUrl = environment.apiUrl + '/api/RMSNR/v1/insagsaledoc';
  
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });
  
    // API Call for Single File Upload
    this.http.post(apiUrl, requestBody, { headers }).subscribe(
      (response: any) => {
        console.log('File uploaded successfully:', response);
        this.showUploadSuccessAlertBox(`"${fileUpload.fileName}" uploaded successfully!`);
  
        fileUpload.uploaded = true;
        fileUpload.ag_doc_id = response.data; // Assign received document ID
  
        // Ensure transactions are mapped to the uploaded document
        this.transactions.forEach(txn => {
          if (txn.fileName === fileUpload.fileName) {
            txn.ag_doc_id = fileUpload.ag_doc_id;
          }
        });
  
        this.getTotalCounts(); // Refresh totals
        this.calculateTotalFilesUploaded(); // Update file count
  
        // Call submitTransactions() immediately after a successful file upload
        this.submitTransactions(fileUpload.ag_doc_id);
      },
      (error) => {
        console.error('Error uploading file:', error);
        this.showUploadErrorAlertBox('Error uploading file. Please try again.');
        this.isLoading = false;
      }
    );
  }
  

  submitTransactions(ag_doc_id: number) {
    if (this.transactions.length === 0) {
      this.showUploadErrorAlertBox('No transactions to upload!');
      return;
    }

    const transactionsWithDocId = this.transactions.filter(txn => txn.ag_doc_id !== undefined);

    const requestBody = this.transactions.map(txn => ({
      // i_ag_sale_id: this.modelData.ag_sale_id,
      i_stmt_no: this.modelData.stmt_no,
      i_ag_doc_id: ag_doc_id,  // Ensure correct mapping
      i_acct_no: txn.acct_no,
      i_acct_type: txn.acct_type,
      i_acct_nm: txn.acct_nm,
      i_dt_fr: txn.dt_fr,
      i_dt_to: txn.dt_to,
      i_total_debit: txn.total_debit,
      i_total_credit: txn.total_credit,
      i_begin_bal: txn.begin_bal,
      i_end_bal: txn.end_bal,
      i_dt_txn: txn.dt_txn,
      i_dt_posting: txn.dt_posting,
      i_time_posting: txn.time_posting,
      i_time_txn: txn.i_time_txn,
      i_txn_desc: txn.txn_desc,
      i_txn_ref: txn.txn_ref,
      i_debit: txn.debit.toString(),
      i_credit: txn.credit.toString(),
      i_source_cd: txn.source_cd,
      i_teller_id: txn.teller_id,
      i_brn_chn: txn.brn_chn,
      i_txn_cd: txn.txn_cd,
      i_end_bal2: txn.end_bal2,
      i_virtual_acct: txn.virtual_acct,
      i_txn_desc2: txn.txn_desc2,
      i_txn_desc3: txn.txn_desc3,
      i_txn_desc4: txn.txn_desc4,
      i_dt_expiry: txn.dt_expiry
    }));

    // const requestBody = transactionsWithDocId.map(txn => ({
    //   i_ag_sale_id: this.modelData.ag_sale_id,
    //   i_ag_doc_id: txn.ag_doc_id,  // Ensure correct mapping
    //   i_acct_no: txn.acct_no,
    //   i_acct_type: txn.acct_type,
    //   i_acct_nm: txn.acct_nm,
    //   i_dt_fr: txn.dt_fr,
    //   i_dt_to: txn.dt_to,
    //   i_total_debit: txn.total_debit,
    //   i_total_credit: txn.total_credit,
    //   i_begin_bal: txn.begin_bal,
    //   i_end_bal: txn.end_bal,
    //   i_dt_txn: txn.dt_txn,
    //   i_dt_posting: txn.dt_posting,
    //   i_time_posting: txn.time_posting,
    //   i_time_txn: txn.i_time_txn,
    //   i_txn_desc: txn.txn_desc,
    //   i_txn_ref: txn.txn_ref,
    //   i_debit: txn.debit.toString(),
    //   i_credit: txn.credit.toString(),
    //   i_source_cd: txn.source_cd,
    //   i_teller_id: txn.teller_id,
    //   i_brn_chn: txn.brn_chn,
    //   i_txn_cd: txn.txn_cd,
    //   i_end_bal2: txn.end_bal2,
    //   i_virtual_acct: txn.virtual_acct,
    //   i_txn_desc2: txn.txn_desc2,
    //   i_txn_desc3: txn.txn_desc3,
    //   i_txn_desc4: txn.txn_desc4,
    //   i_dt_expiry: txn.dt_expiry
    // }));
    console.log('Request body:', requestBody);

    this.isLoading = true;
    const apiUrl = environment.apiUrl + '/api/RMSNR/v1/insagbanktxn';

    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    // Make API call to insert transactions
    this.http.post(apiUrl, requestBody, { headers }).subscribe(
      response => {
        console.log('Transactions uploaded successfully:', response);
        this.showUploadSuccessAlertBox('Transactions inserted successfully!');
        // alert('Transactions inserted successfully!');
        this.fileUploads = []; // Clear after successful upload
        this.transactions = []; // Clear after successful upload
        this.isLoading = false;

        this.getExistingUploadedDocuments();
        // this.router.navigate(['/non-rms-receipting-listing']);
      },
      error => {
        console.error('Error uploading transactions:', error);
        this.showUploadErrorAlertBox('Error uploading transactions. Please try again.');
        this.isLoading = false;
      }
    );
  }

  oldDocuments = true;
  getExistingUploadedDocuments(): void {
    this.isLoading = true;
    const apiUrl = `${environment.apiUrl}/api/RMSNR/v1/getagdocstatistics`;

    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    const requestBody = {
      i_ag_sale_id: this.modelData.ag_sale_id,
      i_stmt_no: this.modelData.stmt_no,
    };

    this.http.post(apiUrl, requestBody, { headers }).subscribe(
      (response: any) => {
        if (response.data.length === 0) {
          console.log('No previously uploaded documents found.');
          this.oldDocuments = false;
          this.isLoading = false;
          return;
        }

        // Map API response to UI data structure
        this.fileUploads = response.data.map((doc: any, index: number) => ({
          id: index + 1,
          fileName: doc.file_nm,
          fileSize: 0, // Assuming file size isn't returned by API
          fileContent: '',
          uploaded: true,
          text_content: '',
          transactions: [],
          ag_doc_id: doc.ag_doc_id, // Store document ID for delete API
          bank_stmt_count: doc.bank_stmt_count,
          bank_stmt_trans: doc.bank_stmt_trans,
          pg_settlement_trans: doc.pg_settlement_trans,
          total_pg_amt: doc.total_pg_amt,
        }));

        // Store file names to prevent duplicate uploads
        this.uploadedFiles = new Set(this.fileUploads.map((file) => file.fileName));
        this.calculateTotalFilesUploaded(); // Update file count
        this.getTotalCounts(); // Calculate total counts
        this.isLoading = false;

        console.log('Loaded existing documents:', this.fileUploads);
      },
      (error) => {
        console.error('Error fetching existing uploaded documents:', error);
        this.showUploadErrorAlertBox('Failed to load uploaded documents.');
        this.isLoading = false;
      }
    );
  }

  deleteFile(fileUpload: any): void {
    console.log('Deleting file:', fileUpload);

    if (!fileUpload.ag_doc_id) {
      this.showDeleteAlertBox('File ID missing, unable to delete.');
      return;
    }

    // Open the confirmation dialog
    const dialogRef = this.dialog.open(AgDeleteFileDiaglogComponent, {
      width: '400px',
      data: { message: `Are you sure you want to delete "${fileUpload.fileName}"?` }
    });



    dialogRef.afterClosed().subscribe((result: boolean) => {
      if (result) {
        this.isLoading = true;
        const apiUrl = `${environment.apiUrl}/api/RMSNR/v1/delagdoc`;

        const headers = new HttpHeaders({
          Authorization: environment.authKey,
          'Content-Type': 'application/json',
        });

        const requestBody = {
          i_ag_doc_id: fileUpload.ag_doc_id, // Required document ID
        };

        this.http.post(apiUrl, requestBody, { headers }).subscribe(
          (response: any) => {

            console.log("File deleted successfully:", response);
            this.showDeleteAlertBox(`"${fileUpload.fileName}" deleted successfully!`);

            // Remove file from UI after successful deletion
            this.fileUploads = this.fileUploads.filter(file => file.ag_doc_id !== fileUpload.ag_doc_id);
            this.uploadedFiles.delete(fileUpload.fileName); // Ensure it can be re-uploaded

            this.getTotalCounts(); // Recalculate totals
            this.calculateTotalFilesUploaded(); // Update UI
            this.getExistingUploadedDocuments(); // Refresh list of uploaded documents
            this.isLoading = false;
          },
          (error) => {
            console.error("Error deleting file:", error);
            this.isLoading = false;
            this.showDeleteErrorAlertBox();
          }
        );
      }
    });
  }

  updateAgSale(): void {

    this.isLoading = true;
    const apiUrl = `${environment.apiUrl}/api/RMSNR/v1/updagsale`;

    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    const requestBody = {
      // i_ag_sale_id: this.modelData.ag_sale_id,
      i_stmt_no: this.modelData.stmt_no,
      i_settle_status: this.task_status === 'Open' ? 'PBSR' : 'SBSR',
      i_task_status: this.task_status === 'Open' ? 'O' : 'C',
      i_remark: this.remarks,
      i_discrepancy_amt: this.modelData.total_net_amt - this.totalPGDisbursedAmount
    };

    this.http.post(apiUrl, requestBody, { headers }).subscribe(
      (response: any) => {
        this.isLoading = false;
        this.router.navigate(['/non-rms-receipting-listing']);
      },
      (error) => {
        this.isLoading = false;
        console.error("Error deleting file:", error);
        this.showUploadErrorAlertBox('Failed to update AG Sale. Please try again.');
      }
    );
  }

  DefaultBox() {
    this.pgTxnBox = false;
    this.bankTxnBox = false;
  }

  AlertBoxInitialize() {
    if (this.pgTxnBox) {
      this.bankTxn();
    } else if (this.bankTxnBox) {
      this.bankTxn();
    }
  }

  bankTxn(): void {
    this.DefaultBox();
    const dialogRef = this.dialog.open(AgBankTxnListingComponent, {
      height: '80%',
      data: { modelData: this.modelData } // Pass modelData to the dialog
    });

    this.bankTxnBox = true;
  }

  noBkTxn(): void {
    this.DefaultBox();

    const dialogRef = this.dialog.open(AgBankStmtListingComponent, {
      height: '80%',
      data: { modelData: this.modelData } // Pass modelData to the dialog
    });

    this.bankTxnBox = true;
  }


  pgFileTxn(): void {
    this.DefaultBox();
    const dialogRef = this.dialog.open(AgPgFileTxnListingComponent, {
      height: '80%',
      data: { modelData: this.modelData } // Pass modelData to the dialog
    });

    this.bankTxnBox = true;
  }

  cancel() {
    this.router.navigate(['/non-rms-receipting-listing']);
  }

  downloadFile(file_name: string, ag_doc_id: number): void {
    const url = environment.apiUrl + '/api/RMSNR/v1/getagdoccontent';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    const Body: any = {
      i_ag_doc_id: ag_doc_id
    };

    this.http.post(url, Body, { headers }).subscribe(
      (response: any) => {
        // console.log(response.data);
        this.file_content = response.data;
        console.log('Content: ' + this.file_content);
        console.log('File Name: ' + file_name);
        if (file_name != "" && this.file_content != null) {
          this.downloadFileContent(file_name, this.file_content);
        }

        if (response.data.length == 0) {
          this.totalRecords = 0;
          //this.showResultAlertBox();
          this.isLoading = false;
        } else {
          this.totalRecords = response.data[0].total;
          this.AlertBoxInitialize();
          this.DefaultBox();
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

    const blob = new Blob([uint8Array], { type: 'text/plain' });

    const url = URL.createObjectURL(blob);

    const anchor = document.createElement('a');
    anchor.href = url;
    anchor.download = fileName;

    document.body.appendChild(anchor);
    anchor.click();

    document.body.removeChild(anchor);
    URL.revokeObjectURL(url);
  }


}