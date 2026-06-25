import { HttpClient, HttpHeaders } from '@angular/common/http';
import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
// import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { NgbPaginationConfig } from '@ng-bootstrap/ng-bootstrap';
import { environment } from 'src/environments/environment';
// import { PGUpload } from '../../core/models/pg-upload.interface';
import { ParamData } from 'src/app/core/models/param.interface';
// import { Systemstatus } from '../../shared/enums/systemstatus';
import { DatePipe, formatDate } from '@angular/common';
import { MatDialog } from '@angular/material/dialog';
import { Systemstatus } from '../../shared/enums/systemstatus';
import { BankRecon } from 'src/app/core/models/bank-recon.interface';
import { ParamService } from '../../core/services/param.service';
import { LangChangeEvent, TranslateService } from '@ngx-translate/core';
import { GlobalService } from 'src/app/shared/global.service';
import { isInteger } from '@ng-bootstrap/ng-bootstrap/util/util';
import { perm } from 'src/permissions/perm';
import { AuthService } from 'src/app/core/services/auth.service';


@Component({
  selector: 'bank-recon-listing',
  templateUrl: './bank-recon-listing.component.html',
  styleUrls: ['./bank-recon-listing.component.scss']
})

export class BankReconComponent implements OnInit {


  page = environment.DefaultPage;
  itemsPerPage = environment.ItemPerPage;
  model: BankRecon[] = [];
  totalRecords: number = 0;
  isReadOnly = false;
  isEmptyResult = false;
  totalCount = 0;

  taskId: String | null = null;
  settlementDate: Date | null = null;
  settleDate: Date | null = null;
  merchantId: String | null = null;
  taskStatus: String | null = null;
  dateUploaded: Date | null = null;
  reconStatus: String | null = null;

  errorMessage: string | null = null;

  isDisplay: boolean = false;

  isLoading: boolean = false;

  //toogle start
  rightSectionCollapsed: boolean = true;

  //default pagination
  selectedValue = environment.dropdownOptions[0];
  dropdownOptions = environment.dropdownOptions;

  dt_settle: string[] = [];
  task_status: ParamData[] = [];
  recon_status: ParamData[] = [];
  selectedStatus: string = Systemstatus.Active;

  //files
  selectedFiles: File[] = [];
  selectedFilesSize: number = 0;
  isDisplayFileRequired: boolean = false;
  inputDetails: boolean = true;
  fileContainerWidth: string = '100%';
  totalFilesSize: number = 0;

  checkResult: number = 0;
  selected: any;
  getparam: any;
  item: any;

  fileInput: File | null = null;
  errorMessages: string[] = [];
  error: boolean = false;
  formatError: boolean = false;
  duplicateError: boolean = false;
  serverError: boolean = false;

  errorBox: boolean = false;

  LoadData(singleItem: number) {
    this.selectedValue = singleItem;
    this.itemsPerPage = this.selectedValue;
    this.loadData();
  }

  toggleRightSection() {
    this.rightSectionCollapsed = !this.rightSectionCollapsed;
  }
  //toogle end

  showResultAlert = false;

  showResultAlertBox() {
    this.showResultAlert = true;
    setTimeout(() => (this.showResultAlert = false), 2000);
  }

  // Configuring Permissions for User and roles variables
  permBRF = perm.Bank_and_Payment_Gateway_Files_View_Bank_Statement_Upload_Screen + "," + perm.Bank_and_Payment_Gateway_Files_Upload_Bank_Statement  
    + "," + perm.Bank_and_Payment_Gateway_Files_View_Closed_Cancelled_PG_Bank_Reconciliation_Task
    + "," + perm.BP_036_Reconciliation_Of_Collection_View_Reconciliation_Details_Page_Bank
    ; // all the perm_cd for this module seperated with comma
  permBRFAllow = ""; // variable to store allowed permission for the user
  permListAllow: number = 0; // if 0 then not allow to view listing page, else allow
  permUploadAllow: number = 0;
  permCancelViewAllow: number = 0;
  permReconViewAllow: number = 0;
  // end configuration

  constructor(
    private http: HttpClient,
    config: NgbPaginationConfig,
    private router: Router,
    public dialog: MatDialog,
    private ParamService: ParamService,
    private cdr: ChangeDetectorRef,
    private translateService: TranslateService,
    private globalService: GlobalService,
    private authService: AuthService
  ) {
    config.maxSize = environment.PaginationMaxSize;
    config.boundaryLinks = true;
    this.translateService.setDefaultLang(this.globalService.getGlobalValue());
    this.translateService.use(this.globalService.getGlobalValue());
    // Subscribe to the language change event
    this.translateService.onLangChange.subscribe((event: LangChangeEvent) => {
      // Clear the error message when the language changes
      this.errorMessage = '';
      // Manually trigger change detection to update the view with the cleared error message
      this.cdr.detectChanges();
    });
  }

  ngOnInit(): void {
    this.loadTask();
    this.loadPgStatus();
    this.getSettlementDates();
    this.loadData();
  }

  getSettlementDates() {
    this.isDisplay = true;
    this.isLoading = true;
    const url = environment.apiUrl + '/api/bankRecon/v1/sp_getPGSettlementDate';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    const Body: any = {
      i_page: this.page.toString(),
      i_size: this.itemsPerPage.toString(),
    };

    if (this.settleDate) {
      Body.i_dt_settlement = formatDate(new Date(this.settleDate.toString()), 'dd/MM/YYYY', 'en');
    }

    this.http.post(url, Body, { headers }).subscribe(
      (response: any) => {
        this.dt_settle = response.data;
      },
      (error: any) => {
        console.error(error);
        this.isLoading = false;
        // Handle errors here
      }
    );

  }

  i_file_content: any;

  async onFileSelectedv2_oldfunction(event: any) {
    let files = event.target.files;
    const file: File = event.target.files[0];
    let currentFilesTotalSize = 0;

    if (file && file.type !== 'text/plain') {
      this.translateService.get('invalidformatmessage').subscribe((translation: string) => {
        this.errorMessage = translation;
      });
      this.fileInput = null; // Clear the file input
      return;
    }

    for (let i = 0; i < files.length; i++) {
      currentFilesTotalSize += files[i].size;
      const fileName = files[i].name;
      // Check if the file with the same name already exists in selectedFiles
      const isDuplicate = this.selectedFiles.some((file) => file.name === files[i].name);
      if (isDuplicate) {
        this.translateService.get('duplicatefilemessage', { fileName }).subscribe((translation: string) => {
          this.errorMessage = translation;
        });
        //this.errorMessage = `File "${files[i].name}" already selected. Please choose a different file.`;
      }
    }

    // Clear the error message if a valid .txt file is selected
    this.errorMessage = '';

    if (file) {
      // Check if the file with the same name already exists in selectedFiles
      const isDuplicate = this.selectedFiles.some((selectedFile) => selectedFile.name === file.name);
      if (isDuplicate) {
        this.translateService.get('duplicatefilemessage', file.name).subscribe((translation: string) => {
          this.errorMessage = translation;
        });
      } else {

        // Calculate the total size of selected files
        currentFilesTotalSize = this.selectedFiles.reduce((totalSize, selectedFile) => totalSize + selectedFile.size, 0);

        // Check if adding the new file exceeds the 5MB limit
        if (currentFilesTotalSize + file.size <= 5 * 1024 * 1024) {

          // Continue processing the file if not a duplicate and within the size limit
          this.i_file_content = await new Promise((resolve, reject) => {
            const reader = new FileReader();

            reader.onload = (e: any) => {
              resolve(e.target.result);
            };

            reader.onerror = reject;

            reader.readAsDataURL(file);
          });

          // Update the total file size
          this.totalFilesSize += file.size;
          if (this.totalFilesSize > 5 * 1000 * 1000) {
            this.translateService.get('filesizelimitmessage').subscribe((translation: string) => {
              this.errorMessage = translation;
            });
          }
          // Display the total file size in the UI
          this.updateFileSizeDisplay();

          // Add the file to selectedFiles array
          this.selectedFiles.push(file);
        } else {
          this.translateService.get('filesizelimitmessage').subscribe((translation: string) => {
            this.errorMessage = translation;
          });
        }
      }
    }

    // Update the file input
    this.fileInput = event.target.files[0];
  }

  async onFileSelected(event: any) {
    const files: FileList = event.target.files;
    if (!files || files.length === 0) return;

    let currentFilesTotalSize = this.selectedFiles.reduce(
      (total, selectedFile) => total + selectedFile.size,
      0
    );

    this.errorMessage = '';

    for (let i = 0; i < files.length; i++) {
      const file = files[i];

      // 1 Validate file type
      if (file.type !== 'text/plain') {
        this.translateService.get('invalidformatmessage').subscribe((t: string) => {
          this.errorMessage = t;
        });
        continue;
      }

      // 2 Prevent duplicates
      const isDuplicate = this.selectedFiles.some(f => f.name === file.name);
      if (isDuplicate) {
        this.translateService.get('duplicatefilemessage', { fileName: file.name })
          .subscribe((t: string) => {
            this.errorMessage = t;
          });
        continue;
      }

      // 3 Enforce total 5 MB size limit
      if (currentFilesTotalSize + file.size > 5 * 1024 * 1024) {
        this.translateService.get('filesizelimitmessage').subscribe((t: string) => {
          this.errorMessage = t;
        });
        continue;
      }

      //Passed all checks — add to selectedFiles
      this.selectedFiles.push(file);
      currentFilesTotalSize += file.size;
    }

    // Update total
    this.totalFilesSize = currentFilesTotalSize;
    this.updateFileSizeDisplay();

    // Allow re-selecting same files again
    event.target.value = '';
  }

  updateFileSizeDisplay() {
    // Update the file size display in the UI
    this.selectedFilesSize = this.totalFilesSize;
  }

  get isUploadDisabled(): boolean {
    return !this.settlementDate || this.totalFilesSize === 0;
  }

  async upload() {
    await this.readFileAsync();
    // this.getSettlementDates();
    this.clear();
    this.loadData();
    this.getSettlementDates();
  }

  async readFileAsync(): Promise<boolean> {
    if (!this.selectedFiles || this.selectedFiles.length === 0) {
      console.error("No files selected.");
      return false;
    }
    else{
      console.log("Total Files:", this.selectedFiles.length);
    }

    let allSuccess = true;

    for (const file of this.selectedFiles) {
      try {
        const result = await this.uploadFile(file);
        if (!result) {
          allSuccess = false;
        }
      } catch (error) {
        console.error(`Error uploading ${file.name}:`, error);
        allSuccess = false;
      }
    }

    return allSuccess;
  }

  async uploadFile(file: File): Promise<boolean> {

  this.formatError = false;
  this.duplicateError = false;
  this.serverError = false;

  this.isDisplay = true;
  this.isLoading = true;

  const url = environment.apiUrl + '/api/bankRecon/v1/sp_uploadBankDoc';

  // Set your authorization header
  const headers = new HttpHeaders({
    Authorization: environment.authKey,
    'Content-Type': 'application/json'
  });

  // Use the file from the argument (NOT this.fileInput)
  const Body: any = {
    i_dt_settlement: this.settlementDate,
    i_file_content: await this.readFileContent(file), // Read content per file
    i_file_nm: file.name,
    i_file_type: file.type,
    i_file_size: file.size.toString()
  };

  try {
    const response: any = await this.http.post(url, Body, { headers }).toPromise();

    if (response.header.statusCode === '00') {
      return true; 
    } else {
      this.serverError = true;
      return false;
    }
  } catch (error: any) {
    console.error(`Upload failed for ${file.name}:`, error);

    if (error?.error?.header?.statusCode === '02') {
      this.formatError = true;
    } else if (error?.error?.header?.statusCode === '04') {
      this.duplicateError = true;
    }

    return false;
  } finally {
    this.isDisplay = false;
    this.isLoading = false;
  }
}

private readFileContent(file: File): Promise<string> {
  return new Promise((resolve, reject) => {
    const reader = new FileReader();
    reader.onload = (e: any) => resolve(e.target.result);
    reader.onerror = reject;
    reader.readAsDataURL(file);
  });
}

  async uploadFilev2_oldfile(file: File): Promise<boolean> {

    this.formatError = false;
    this.duplicateError = false;
    this.serverError = false;

    this.isDisplay = true;
    this.isLoading = true;

    const url = environment.apiUrl + '/api/bankRecon/v1/sp_uploadBankDoc';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json'
    });

    const Body: any = {
      i_dt_settlement: this.settlementDate,
      i_file_content: this.i_file_content,
      i_file_nm: this.fileInput ? this.fileInput.name : '',
      i_file_type: this.fileInput ? this.fileInput.type : '',
      i_file_size: this.fileInput ? this.fileInput.size.toString() : ''
    };

    try {
      const response: any = await this.http.post(url, Body, { headers }).toPromise();
      if (response.header.statusCode === '00') {
        return false; // Insert success
      }
      else {
        this.serverError = true;
        return true; // Insert failed
      }
    } catch (error: any) {
      //this.serverError = true;
      console.error(error);
      if (error.error.header.statusCode === '02') {
        this.formatError = true;
      } else if (error.error.header.statusCode === '04') {
        this.duplicateError = true;
        return true; // Insert failed
      }
      return true; // Error occurred
    }
    this.isDisplay = false;
    this.isLoading = false;
  }

  clearFiles() {
    this.selectedFiles = [];
    this.selectedFilesSize = 0;
    this.errorMessage = null;
    this.fileInput = null;
    this.totalFilesSize = 0;
    // this.isDisplayFileRequired = true;
  }

  clear() {
    this.settlementDate = null;
    this.selectedFiles = [];
    this.selectedFilesSize = 0;
    this.errorMessage = null;
    this.fileInput = null;
    this.totalFilesSize = 0;
  }

  viewSelected(item: any) {
    this.isLoading = true;
    const task_id = item.i_task_no;
    if(item.i_recon_status === 'Pending Bank Statement Reconciliation'){
      // Set your authorization header
      const url = environment.apiUrl + '/api/bankRecon/v1/sp_checkbktask';
      const headers = new HttpHeaders({
        Authorization: environment.authKey,
        'Content-Type': 'application/json',
      });
      const Body: any = {
        i_task_no: task_id
      };
      this.http.post(url, Body, { headers }).subscribe(
        (response: any) => {
          if (response.data.length == 0) {
            this.isLoading = false;
            this.showResultAlert; 
          } else {
            this.isLoading = false;
            if(response.data == 1){
              this.errorBox = false;
              this.router.navigate(['/bank-recon-detail'], { state: { task_id } });
            }
            else{
              this.errorBox = true;
            }
          }
        },
        (error: any) => {
          console.error(error);
          this.isLoading = false;
          // Handle errors here
        }
      );
    }
    else{
      this.errorBox = false;
      this.isLoading = false;
      this.router.navigate(['/bank-recon-detail'], { state: { task_id } });
    }
  }

  //loadData Start
  loadData() {
    this.authService.checkUserRole(this.authService.username, this.permBRF)
      .subscribe(
        (response: any) => {
          this.permBRFAllow = response.data;
          this.permListAllow = this.permBRFAllow.includes(perm.Bank_and_Payment_Gateway_Files_View_Bank_Statement_Upload_Screen) ? 1 : 0;
          this.permUploadAllow = this.permBRFAllow.includes(perm.Bank_and_Payment_Gateway_Files_Upload_Bank_Statement) ? 1 : 0;
          this.permCancelViewAllow = this.permBRFAllow.includes(perm.Bank_and_Payment_Gateway_Files_View_Closed_Cancelled_PG_Bank_Reconciliation_Task) ? 1 : 0;
          this.permReconViewAllow = this.permBRFAllow.includes(perm.BP_036_Reconciliation_Of_Collection_View_Reconciliation_Details_Page_Bank) ? 1 : 0;
          console.log(this.permListAllow, this.permUploadAllow, this.permCancelViewAllow, this.permReconViewAllow);
          if (this.permListAllow === 0) {
            console.log("access-denied");
            this.router.navigate(['/access-denied']);
            return; // Exit the function to prevent further execution
          }
          console.log("View listing :" + this.permListAllow, "button upload :" + this.permUploadAllow, "View closed,canceled :" + this.permCancelViewAllow);

          this.isDisplay = true;
          this.isLoading = false;
          const url = environment.apiUrl + '/api/bankRecon/v1/sp_getBankReconTask';

          // Set your authorization header
          const headers = new HttpHeaders({
            Authorization: environment.authKey,
            'Content-Type': 'application/json',
          });

          const Body: any = {
            i_page: this.page.toString(),
            i_size: this.itemsPerPage.toString(),
          };

          if (this.taskId && this.taskId.trim()) {
            Body.i_task_no = this.taskId;
          }

          if (this.settleDate) {
            Body.i_dt_settlement = formatDate(new Date(this.settleDate.toString()), 'YYYY-MM-dd', 'en');
          }

          if (this.merchantId && this.merchantId.trim()) {
            Body.i_merchant_id = this.merchantId;
          }

          if (this.taskStatus && this.taskStatus.trim()) {
            Body.i_task_status = this.taskStatus;
          }

          if (this.dateUploaded) {
            Body.i_dt_uploaded = formatDate(new Date(this.dateUploaded.toString()), 'YYYY-MM-dd', 'en');
          }

          if (this.reconStatus && this.reconStatus.trim()) {
            Body.i_recon_status = this.reconStatus;
          }

          this.http.post(url, Body, { headers }).subscribe(
            (response: any) => {
              this.model = response.data;
              if (response.data.length == 0) {
                this.totalRecords = 0;
                this.isEmptyResult = true;
                //this.isDisplay = false;
                this.showResultAlert; // Remove parentheses here
                this.isLoading = false;
              } else {
                this.totalRecords = response.data[0].total;
                this.isEmptyResult = false;
                // this.AlertBoxInitialize();
                this.isLoading = false;
              }
            },
            (error: any) => {
              console.error(error);
              this.isLoading = false;
              // Handle errors here
            }
          );
        });
  }
  //loadData End

  apply(): void {
    this.loadData();
    this.rightSectionCollapsed = true;
  }

  reset(): void {
    this.taskId = null;
    this.settleDate = null;
    this.merchantId = null;
    this.taskStatus = null;
    this.dateUploaded = null;
    this.reconStatus = null;
  }

  loadTask() {
    this.ParamService.getStates('1', '100', '', 'Status-Task').subscribe(
      (response: any) => {
        if (response && response.data && Array.isArray(response.data)) {
          this.task_status.push({
            param_cd: '',
            nm_en: 'All',
            nm_bm: 'All',
            total: 5
          }); //add 'All' options
          this.task_status = [...this.task_status, ...response.data];
          // Set the default selected state to 'All'
          this.taskStatus = this.task_status[0].param_cd; // Convert string to Date object
          //this.states.sort((a, b) => a.nm_en.localeCompare(b.nm_en)); // sorting
        } else {
          console.error('Invalid response format:', response);
        }
      },
      (error: any) => {
        console.error('There was an error retrieving the status:', error);
      }
    );
  }

  loadPgStatus() {
    this.ParamService.getStates('1', '100', '', 'Status-BRC').subscribe(
      (response: any) => {
        if (response && response.data && Array.isArray(response.data)) {
          this.recon_status.push({
            param_cd: '',
            nm_en: 'All',
            nm_bm: 'All',
            total: 5
          }); //add 'All' options
          this.recon_status = [...this.recon_status, ...response.data];
          // Set the default selected state to 'All'
          this.reconStatus = this.recon_status[0].param_cd; // Convert string to Date object
          //this.states.sort((a, b) => a.nm_en.localeCompare(b.nm_en)); // sorting
        } else {
          console.error('Invalid response format:', response);
        }
      },
      (error: any) => {
        console.error('There was an error retrieving the status:', error);
      }
    );
  }

  cancel(item: any) {
    const url = environment.apiUrl + '/api/brdc/v1/updrcbankdetailstatus';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    const Body: any = {
      task_no: item.i_task_no,
      task_status: 'Cancel',
      remarks: 'Cancel'
    };

    this.http.post(url, Body, { headers }).subscribe(
      (response: any) => {
        this.model = response.data;
      },
      (error) => {
      }
    );

    this.loadData();
    this.getSettlementDates();

  }

}
