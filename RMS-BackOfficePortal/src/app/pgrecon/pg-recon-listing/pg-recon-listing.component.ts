import { formatDate } from '@angular/common';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Component } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { NgbPaginationConfig } from '@ng-bootstrap/ng-bootstrap';
import { PGReconList } from 'src/app/core/models/pg-recon';
import { ParamData } from 'src/app/core/models/param.interface';
import { Systemstatus } from 'src/app/shared/enums/systemstatus';
import { environment } from 'src/environments/environment';
import { ParamService } from '../../core/services/param.service';
import { GlobalService } from 'src/app/shared/global.service';
import { TranslateService } from '@ngx-translate/core';
import { perm } from 'src/permissions/perm';
import { AuthService } from 'src/app/core/services/auth.service';

@Component({
  selector: 'app-pg-recon-listing',
  templateUrl: './pg-recon-listing.component.html',
  styleUrls: ['./pg-recon-listing.component.scss']
})
export class PgReconListingComponent {
  page = environment.DefaultPage;
  itemsPerPage = environment.ItemPerPage;
  model: PGReconList[] = [];
  totalRecords: number = 0;
  isReadOnly = false;
  isEmptyResult = false;
  totalCount = 0;
  fileContainerWidth: string = '100%';
  errorMessage: string | null = null;
  selectedFilesSize: number = 0;
  selectedFile: File | null=null;
  addBox: boolean = false;
  viewBox: boolean = false;
  showUploadAlert: boolean = false;

  taskId: String | null = null;
  settlementDate: Date | null = null;
  merchantId: String | null = null;
  taskStatus: String | null = null;
  reconStatus: String | null = null;
  fileName: string | null = null;
  file_content = "";

  isDisplay: boolean = false;
  isLoading: boolean = false;

  //toogle start
  rightSectionCollapsed: boolean = true;

  //default pagination
  selectedValue = environment.dropdownOptions[0];
  dropdownOptions = environment.dropdownOptions;

  dt_settle: ParamData[] = [];
  task_status: ParamData[] = [];
  recon_status: ParamData[] = [];
  selectedStatus: string = Systemstatus.Active;

  checkResult: number = 0;
  getparam: any;
  item: any;

  bsValue = new Date();
  bsValue2 = new Date();
  minDate_dt_settlement = new Date();
  minDate_dt_uploaded = new Date();
  dateSettlement!: Date[];
  dateUploaded!: Date[];

  fileInput: File | null=null;
  dt_settlement:Date | null=null;//= new Date();
  formatError: boolean = false;
  duplicateError: boolean = false;
  serverError: boolean = false;

  disableButton: boolean = true;
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

  DefaultBox() {
    this.addBox = false;
  }

  AlertBoxInitialize() {
    if (this.addBox) {
      // this.showInsertAlertBox();
    }
  }

    // Configuring Permissions for User and roles variables
    permPRF = perm.Bank_and_Payment_Gateway_Files_View_PG_Settlement_Upload_Screen + "," + perm.Bank_and_Payment_Gateway_Files_Upload_PG_Settlement_File + "," + perm.Bank_and_Payment_Gateway_Files_Download_PG_Settlement_File
               + "," + perm.Bank_and_Payment_Gateway_Files_View_Closed_Cancelled_PG_RMS_Reconciliation_Task
               + "," + perm.BP_036_Reconciliation_Of_Collection_View_Reconciliation_Details_Page_PG; // all the perm_cd for this module seperated with comma
    permPRFAllow = ""; // variable to store allowed permission for the user
    permListAllow: number = 0; // if 0 then not allow to view listing page, else allow
    permUploadAllow: number = 0; 
    permDownloadAllow: number = 0; 
    permCancelViewAllow: number = 0; 
    permReconViewAllow: number = 0;
    // end configuration

  constructor(
    private http: HttpClient,
    config: NgbPaginationConfig,
    private router: Router,
    public dialog: MatDialog,
    private ParamService: ParamService,
    private translateService: TranslateService,
    private globalService: GlobalService,
    private authService: AuthService
  ) {
    config.maxSize = environment.PaginationMaxSize;
    config.boundaryLinks = true;
    this.translateService.setDefaultLang(this.globalService.getGlobalValue());
    this.translateService.use(this.globalService.getGlobalValue());
  }

  ngOnInit(): void {

    this.bsValue = new Date();
    this.minDate_dt_settlement = new Date();
    this.minDate_dt_settlement.setMonth(this.bsValue.getMonth() - 1);
    // this.dateSettlement = [this.minDate_dt_settlement, this.bsValue];

    this.bsValue2 = new Date();
    this.minDate_dt_uploaded = new Date();
    this.minDate_dt_uploaded.setMonth(this.bsValue2.getMonth() - 1);
    // this.dateUploaded = [this.minDate_dt_uploaded, this.bsValue2];

    this.loadTask();
    this.loadPgStatus();

    this.loadData(); // must last
    
  }

  i_file_content: any;

  async onFileSelected(event: any) {

    this.disableButton=false;
    let currentFilesTotalSize = 0;


    const file: File = event.target.files[0];

    if (file) {
      currentFilesTotalSize = file.size;
      const fileName = file.name;
      console.log('This file size is ' + currentFilesTotalSize)
      // Check if adding the new file exceeds the 5MB limit
      if (file.size <= 5 * 1024 * 1024) {

      this.i_file_content = await new Promise((resolve, reject) => {
        const reader = new FileReader();

        reader.onload = (e: any) => {
          resolve(e.target.result);
        };

        reader.onerror = reject;

        reader.readAsDataURL(file);
      });
      this.selectedFilesSize = currentFilesTotalSize;
      this.selectedFile = file;
      this.fileInput=event.target.files[0];
    }

    if (file.size > 5 * 1000 * 1000) {
      this.translateService.get('filesizelimitmessage').subscribe((translation: string) => {
      this.errorMessage = translation;});
    }
    // Display the total file size in the UI
   
    
   
  }
}

  async upload(){
    this.showUploadAlert=false;
    // this.errorMessage=false;
    this.formatError=false;
    this.duplicateError=false;
    await this.readFileAsync();
    this.loadData();
  }

  async readFileAsync(): Promise<boolean> {
    let result: boolean = false;
    // console.log(this.fileInput.name);
    if (this.fileInput!=null) {
      //check file extension
      const allowedExtensions = ['xls', 'xlsx'];
      const fileExtension = this.fileInput.name.split('.')?.pop()?.toLowerCase();
      //if allowed extension then upload file
      if (fileExtension && allowedExtensions.includes(fileExtension)) {
        result = await this.uploadFile(this.fileInput);
      } else {
        this.formatError = true;
      }
    }
    return result;
  }

    async uploadFile(file: File): Promise<boolean> {

    this.formatError = false;
    this.duplicateError = false;
    this.serverError = false;

    this.isDisplay = true;
    this.isLoading = true;
    const url = environment.apiUrl + '/api/pgrecon/v1/sp_uploadPGDoc'; 

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    

    const Body: any = {

      i_file_nm: file.name,
      i_file_content: this.i_file_content,
      i_file_type: file.type,
      i_file_size: file.size.toString()
    };

    if (this.dt_settlement != null) {
      Body.i_dt_settlement= formatDate(this.dt_settlement, 'dd/MM/YYYY', 'en');//this.dt_settlement;
     }
   

    try {
      const response: any = await this.http.post(url, Body, { headers }).toPromise();
      if (response.header.statusCode === '00') {
        this.clear(); //clear file input
        // this.isDisplay = false;
        // this.isLoading = false;
        this.showUploadAlert=true;
        return false; // Insert success
      } 
      else {
        this.serverError = true;
        this.showUploadAlert=false;
        return true; // Insert failed
      }
    } catch (error:any) {
      //this.serverError = true;
      console.error(error);
      if(error.error.header.statusCode === '02'){
        this.formatError = true;
      }else if (error.error.header.statusCode === '04'){
        this.duplicateError = true;
        //return true; // Insert failed
      }
      this.isDisplay = false;
      this.isLoading = false;
      return true; // Error occurred
    }
    
  }

  clear() {
    this.dt_settlement = null;
    this.settlementDate = null;
    this.selectedFile = null;
    this.selectedFilesSize = 0;
    this.errorMessage = null;
    this.fileInput = null; // Assign an empty File object
  }

  clearFiles() {
    this.selectedFile = null;
    this.selectedFilesSize = 0;
    this.errorMessage = null;
    this.fileInput = null; // Assign an empty File object
    this.showUploadAlert=false;
    // this.isDisplayFileRequired = true;
    // console.log('sizes'+this.selectedFiles)
  }

  viewSelected(item: any) {
    this.isLoading = true;
    const task_id = item.i_task_id;
    if(item.i_recon_status === 'Pending SS Collection Reconciliation'){
      // Set your authorization header
      const url = environment.apiUrl + '/api/pgrecon/v1/sp_checkpgtask';
      const headers = new HttpHeaders({
        Authorization: environment.authKey,
        'Content-Type': 'application/json',
      });
      const Body: any = {
        i_task_id: task_id
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
              this.router.navigate(['/pgrecon-detail'], { state: { task_id } });
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
      const task_id = item.i_task_id;
      this.router.navigate(['/pgrecon-detail'], { state: { task_id } });
    }
  }

  //loadData Start
  loadData() {
    this.authService.checkUserRole(this.authService.username, this.permPRF)
    .subscribe(
      (response: any) => {
        this.permPRFAllow = response.data;
        this.permListAllow = this.permPRFAllow.includes(perm.Bank_and_Payment_Gateway_Files_View_PG_Settlement_Upload_Screen) ? 1 : 0;
        this.permUploadAllow = this.permPRFAllow.includes(perm.Bank_and_Payment_Gateway_Files_Upload_PG_Settlement_File) ? 1 : 0;
        this.permDownloadAllow = this.permPRFAllow.includes(perm.Bank_and_Payment_Gateway_Files_Download_PG_Settlement_File) ? 1 : 0;
        this.permCancelViewAllow = this.permPRFAllow.includes(perm.Bank_and_Payment_Gateway_Files_View_Closed_Cancelled_PG_RMS_Reconciliation_Task) ? 1 : 0;
        this.permReconViewAllow = this.permPRFAllow.includes(perm.BP_036_Reconciliation_Of_Collection_View_Reconciliation_Details_Page_PG) ? 1 : 0;
        console.log(this.permListAllow, this.permUploadAllow, this.permDownloadAllow, this.permCancelViewAllow, this.permReconViewAllow);
        if (this.permListAllow === 0) {
          console.log("access-denied");
          this.router.navigate(['/access-denied']);
          return; // Exit the function to prevent further execution
        }
        console.log(this.permListAllow, "button upload "+this.permUploadAllow, this.permDownloadAllow, this.permCancelViewAllow);

    this.isDisplay = true;
    this.isLoading = true;
    const url = environment.apiUrl + '/api/pgrecon/v1/sp_getPGReconList'; 

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
      Body.i_task_id = this.taskId;
    }

    if (this.fileName && this.fileName.trim()) {
      Body.i_file_nm = this.fileName;
    }

    if (this.dateSettlement&& this.dateSettlement.length > 0) {
      //Body.i_dt_modified_fr =
      Body.i_dt_settlement_fr = formatDate(this.dateSettlement[0], 'YYYY-MM-dd', 'en');
      const temp =this.dateSettlement[1];
      temp.setDate(this.dateSettlement[1].getDate() + 1);
      //this.dateUploaded[1].setDate(this.dateSettlement[1].getDate() + 1);
      //Body.i_dt_settlement_to = formatDate(this.dateSettlement[1].setDate(this.dateSettlement[1].getDate() + 1), 'YYYY-MM-dd', 'en');
      Body.i_dt_settlement_to = formatDate(temp, 'YYYY-MM-dd', 'en');

    }

    if (this.dateUploaded&& this.dateUploaded.length > 0) {
      //Body.i_dt_modified_fr =
      Body.i_dt_uploaded_fr = formatDate(this.dateUploaded[0], 'YYYY-MM-dd', 'en'); 
      const temp = this.dateUploaded[1];
      temp.setDate(this.dateUploaded[1].getDate() + 1);
      //this.dateUploaded[1].setDate(this.dateUploaded[1].getDate() + 1);
      //Body.i_dt_uploaded_to = formatDate(this.dateUploaded[1].setDate(this.dateUploaded[1].getDate() + 1), 'YYYY-MM-dd', 'en');
      Body.i_dt_uploaded_to = formatDate(temp, 'YYYY-MM-dd', 'en');
    }

    if (this.merchantId && this.merchantId.trim()) {
      Body.i_merchant_id = this.merchantId;
    }

    if (this.taskStatus && this.taskStatus.trim()) {
      Body.i_task_status = this.taskStatus;
    }

    if (this.reconStatus && this.reconStatus.trim()) {
      Body.i_recon_status = this.reconStatus;
    }

  
    this.http.post(url, Body, { headers }).subscribe(
      (response: any) => {
        console.log(response.data);
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
        // console.log(response.data);
        //  console.log(this.totalRecords);
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
    this.fileName = null;

    this.bsValue = new Date();
    this.minDate_dt_settlement = new Date();
    this.minDate_dt_settlement.setMonth(this.bsValue.getMonth() - 1);
    this.dateSettlement=[];
    // this.dateSettlement = [this.minDate_dt_settlement, this.bsValue];

    this.bsValue2 = new Date();
    this.minDate_dt_uploaded = new Date();
    this.minDate_dt_uploaded.setMonth(this.bsValue2.getMonth() - 1);
    this.dateUploaded=[];
    // this.dateUploaded = [this.minDate_dt_uploaded, this.bsValue2];

    this.merchantId = null;
    this.taskStatus = null;
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
    this.ParamService.getStates('1', '100', '', 'Status-PGRC').subscribe(
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
  
  //prevent manual key for date
  onKeyDown(event: KeyboardEvent): void {
    if (event.key === 'Backspace') {
      return;
    }
    // Prevent manual key entry
    event.preventDefault();
  }

  cancel(item:any){
    const url = environment.apiUrl + '/api/pgrecon/v1/sp_updPGReconDetail';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    const Body: any = {
      i_task_id: item.i_task_id,//this.model.task_id,
      //i_remarks: this.remarks,
      i_task_status: 'Cancel'//this.taskStatus
    };

    console.log(Body);

    this.http.post(url, Body, { headers }).subscribe(
      (response: any) => {
        this.model = response.data;
        console.log(response.data);
      },
      (error) => {
        console.error(error);
        // Handle errors here  
      }
    );

   this.loadData();

  }

  downloadFile(task_id : string, file_nm: string): void {
    const url = environment.apiUrl + '/api/pgrecon/v1/sp_getrcpgdoc';
    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });
    const Body: any = {
      i_task_id: task_id,
    };
    console.log(Body);
    this.http.post(url, Body, { headers }).subscribe(
      (response: any) => {
        // console.log(response.data);
        this.file_content = response.data;
        if(task_id != "" && this.file_content != null)
        {
          this.downloadFileContent(task_id, this.file_content, file_nm);
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

  downloadFileContent(task_id: string, fileContent: string, file_nm: string): void {
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
    anchor.download = file_nm;
    document.body.appendChild(anchor);
    anchor.click();
    document.body.removeChild(anchor);
    URL.revokeObjectURL(url);
  }

  get isUploadDisabled(): boolean {
    return !this.selectedFile || !this.dt_settlement;
  }

}
