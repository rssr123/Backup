import { formatDate } from '@angular/common';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { Router, ActivatedRoute } from '@angular/router';
import { NgbPaginationConfig } from '@ng-bootstrap/ng-bootstrap';
import { Systemstatus } from 'src/app/shared/enums/systemstatus';
import { environment } from 'src/environments/environment';
import { ParamService } from '../../core/services/param.service';
import { GlobalService } from 'src/app/shared/global.service';
import { TranslateService } from '@ngx-translate/core';
import { perm } from 'src/permissions/perm';
import { AuthService } from 'src/app/core/services/auth.service';
import { ParamData } from 'src/app/core/models/param.interface';

@Component({
  selector: 'app-credit-control-case-listing',
  templateUrl: './credit-control-case-listing.component.html',
  styleUrls: ['./credit-control-case-listing.component.scss']
})
export class CreditControlCaseListingComponent implements OnInit {
  taskNo: string | null = null;
  taskDesc: string | null = null;
  taskStatus: string | null = null;
  taskStatusString: string | null = null;
  assignedDate: Date | null = null;

  custID: string | null = null;
  custName: string | null = null;
  custEmail: string | null = null;
  custPhoneNo: string | null = null;
  add1: string | null = null;
  add2: string | null = null;
  add3: string | null = null;
  postcode: string | null = null;
  city: string | null = null;
  state: string | null = null;
  fmsCaseNo: string | null = null;
  caseCreatedDate: Date | null = null;
  fmsARIRefNo: string | null = null;
  paymentStatus: string | null = null;
  txnType: string | null = null;
  txnRefNo: string | null = null;
  cheqNo: string | null = null;
  rcptNo: string | null = null;
  amountPayable: number = 0;

  taskInfo: any = null;
  reminders: any[] = [];
  payment_items: any[] = [];
  history: any[] = [];
  documents_list: any[] = [];

  totalRecordsListOfReminder: number = 0;
  totalRecordsListOfItem: number = 0;
  totalHistoryRecords: number = 0;
  totalRecordsListOfDoc: number = 0;
  total_amount: number = 0; 

  file_content = "";

  taskStatusOptions: ParamData[] = [];

  alertMessage: string | undefined = undefined;

  page = environment.DefaultPage;
  itemsPerPage = environment.ItemPerPage;
  dropDownSize = environment.DropDownSize;

  pageHistory = environment.DefaultPage;
  itemsPerPageHistory = environment.ItemPerPage;

  // default pagination
  selectedValue = environment.dropdownOptions[0];
  dropdownOptions = environment.dropdownOptions;
  isLoadingHistory: boolean = true;
  isLoading: boolean = true;
  isLoadingData: boolean = true;
  isLoadingPerms: boolean = true;
  isTaskUnauth: boolean = false;
  roles: any = this.authService.roles;

  permCheck = perm.Billing_Listing_Details;
  permCheckReturnString = ""; // variable to store allowed permission for the user
  permAllow: number = 0;

  maxFileSize = 10 * 1024 * 1024; // 10MB
  selectedFiles: File[] = [];
  selectedFilesSize: number = 0;

  selectedAction: any | null = null;
  remarks: string | null = null;
  selectedGroup: string | null = null;
  debitCreditAmount: string | null = null;

  constructor(
    private http: HttpClient,
    config: NgbPaginationConfig,
    private router: Router,
    private route: ActivatedRoute,
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


  async ngOnInit() {
    const queryParams = this.route.snapshot.queryParamMap;
    const tempNo = queryParams.get('task_no');
    if (tempNo !== null && tempNo !== 'null')
      this.taskNo = tempNo;

    if(this.taskNo != null){
      //this.loadPermission();
      this.isLoadingPerms = false;
      this.fetchTaskInfo();
    }
  }

  async fetchTaskInfo()  {
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });
    const url = environment.apiUrl + '/api/cc/v1/getcreditcontrolcase';

    const Body: any = {
      i_task_no: this.taskNo,
      i_page: environment.DefaultPage,
      i_size: environment.ItemPerPage
    };

    this.http.post(url, Body, { headers }).subscribe(
      (response: any) => {
        this.taskInfo = response.data;

        console.log(response.data);
        if(response.data.status == 'UNAUTHORIZED'){
          this.isTaskUnauth = true;
          this.isLoading = false;
          this.isLoadingHistory = false;
        }
        else if(!this.isLoadingPerms){
          this.isLoadingData = false;
          this.populate();
        }
      },
      (error) => {
        console.error(error);
        this.isLoadingData = false;
        if(!this.isLoadingPerms)
          this.isLoading = false;
      }
    );
  }

  populate(){
    this.taskDesc = this.taskInfo.invoice_desc;
    this.taskStatus = this.taskInfo.task_status;
    this.assignedDate = this.taskInfo.dt_assigned;

    this.custName = this.taskInfo.cust_nm;
    this.custEmail = this.taskInfo.cust_email;
    this.custPhoneNo = this.taskInfo.cust_phone;
    this.add1 = this.taskInfo.cust_addr1;
    this.add2 = this.taskInfo.cust_addr2;
    this.add3 = this.taskInfo.cust_addr3;
    this.postcode = this.taskInfo.cust_postcode;
    this.city = this.taskInfo.cust_city;
    this.state = this.taskInfo.cust_state;

    this.fmsCaseNo = this.taskInfo.attr_case_no;
    this.caseCreatedDate = this.taskInfo.dt_created;
    this.fmsARIRefNo = this.taskInfo.fms_ari_ref_no;
    this.paymentStatus = this.taskInfo.pymt_status;
    this.txnType = this.taskInfo.txn_ty;
    this.txnRefNo = this.taskInfo.ref_no_txn;
    //this.cheqNo = this.taskInfo.;
    this.rcptNo = this.taskInfo.rcpt_no;
    this.amountPayable = this.taskInfo.pymt_amt;

    this.reminders = this.taskInfo.reminders;
    this.payment_items = this.taskInfo.payment_items;
    this.history = this.taskInfo.history;
    this.documents_list = this.taskInfo.documents_list;

    this.totalRecordsListOfReminder = this.taskInfo.reminders_size;
    this.totalRecordsListOfItem = this.taskInfo.payment_items_size;
    this.totalHistoryRecords = this.taskInfo.history_size;
    this.totalRecordsListOfDoc = this.taskInfo.documents_size;

    if(this.payment_items != null && this.payment_items.length > 0)
      for(const item of this.payment_items){
        item.final_amt = (~~parseFloat(item.qty)*~~parseFloat(item.unit_price))-~~parseFloat(item.disc_amt);
        this.total_amount += item.final_amt;
      }
    
    this.loadTSParam();

    this.loadParam(this.state as string,'State');
    this.loadParam(this.paymentStatus as string, 'cc-pymt-status');
    this.loadParam(this.txnType as string, 'cc-txn-type');

    this.isLoading = false;
    this.isLoadingHistory = false;
  }

  loadParam(paramCd: string, paramGrpNm: string){
      this.ParamService.getStates(environment.DefaultPage.toString(), environment.ItemPerPage.toString(), paramCd, paramGrpNm).subscribe((response: any) => {
        if (response.data.length >= 0 && response.data[0] != null && response.data[0].nm_en != null) {
          if (paramGrpNm == 'State'){    
            var stateTmp = response.data[0].nm_en;
            stateTmp= stateTmp.charAt(0) + stateTmp.substring(1).toLowerCase();
            this.state = stateTmp;
          }
          else if (paramGrpNm == 'cc-pymt-status')
            this.paymentStatus = response.data[0].nm_en;
          else if (paramGrpNm == 'cc-txn-type')
            this.txnType = response.data[0].nm_en;
        } 
        else
          console.error('Invalid response format:', response);
      },
      (error) => {
        console.error('There was an error retrieving the status:', error);
      }
    );
  } 

  loadTSParam(){
      this.ParamService.getStates(environment.DefaultPage.toString(), environment.ItemPerPage.toString(), '', 'cc-case').subscribe((response: any) => {
        if (response.data.length >= 0) {
          this.taskStatusOptions = response.data as ParamData[];
          this.taskStatusOptions.sort((a, b) => a.nm_en.localeCompare(b.nm_en)); // sorting
        } 
        else
          console.error('Invalid response format:', response);
      },
      (error) => {
        console.error('There was an error retrieving the status:', error);
      }
    );
  }   

  getTaskStatusName(status: string | null): string {
    if (!status) {
      return ''; // Return a default value if stateCode is null
    }
    const taskStatus = this.taskStatusOptions.find((option) => option.param_cd === status);
    return taskStatus ? taskStatus.nm_en : status; // Return the name if found, otherwise return the code
  }

  formatOrdinalDate(dateInput: string | Date): string {
    if (!dateInput) return '';

    const date = new Date(dateInput);
    const day = date.getDate();
    const month = date.toLocaleString('default', { month: 'long' }); // Full month name
    const year = date.getFullYear();

    const suffix = this.getOrdinalSuffix(day);

    return `${day}${suffix} ${month} ${year}`;
  }

  private getOrdinalSuffix(day: number): string {
    if (day > 3 && day < 21) return 'th'; // 4th to 20th always have 'th'
    switch (day % 10) {
      case 1: return 'st';
      case 2: return 'nd';
      case 3: return 'rd';
      default: return 'th';
    }
  }

  //download file start
  downloadFile(item: any): void {
    if(item.isDownloadingFile)
      return;
    item.isDownloadingFile = true;
    const url = environment.apiUrl + '/api/cc/v1/getcccdocblob';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    const Body: any = {
      i_doc_id: item.cc_doc_id
    };

    this.http.post(url, Body, { headers }).subscribe(
      (response: any) => {
        // console.log(response.data);
        this.file_content = response.data;
        this.downloadFileContent(item.file_nm, this.file_content);

        if (response.data.length == 0) {
          console.error('Invalid billing document response format:', response);
        } else {
          console.log('Successful download file ' + item.file_nm);
        }
        item.isDownloadingFile = false;
      },
      (error) => {
        console.error('There was an error downloading the billing document:', error);
        item.isDownloadingFile = false;
      }
    );
  }

  downloadFileContent(fileName: string, fileContent: string): void {
    // event.preventDefault(); // Prevent the default behavior of the anchor element

    // Check if file_content exists
    if (fileContent) {
      const contentType = 'application/octet-stream';
      const blob = this.base64ToBlob(fileContent, contentType);
      const blobUrl = URL.createObjectURL(blob);

      // Create an anchor element and trigger the download
      const link = document.createElement('a');
      link.href = blobUrl;
      link.download = fileName;
      link.click();

      // Cleanup
      URL.revokeObjectURL(blobUrl);
    }
  }

  base64ToBlob(base64: string, contentType: string): Blob {
    const byteCharacters = atob(base64);
    const byteNumbers = new Array(byteCharacters.length);

    for (let i = 0; i < byteCharacters.length; i++) {
      byteNumbers[i] = byteCharacters.charCodeAt(i);
    }

    const byteArray = new Uint8Array(byteNumbers);
    return new Blob([byteArray], { type: contentType });
  }
  //download file end

  loadPermission() {
    this.authService.checkUserRole(this.authService.username, this.permCheck)
        .subscribe((response: any) => {
          this.permCheckReturnString = response.data;
          this.permAllow = this.permCheckReturnString.includes(perm.Billing_Listing_Details) ? 1 : 0;
          if (this.permAllow === 0) {
            if(environment.production)
              this.router.navigate(['/access-denied']);
            console.log(response.data);
            alert('bad permission: ' + this.permCheckReturnString);  
          }
          this.isLoadingPerms = false;

          if(this.isTaskUnauth){
            this.isLoading = false;
            this.isLoadingHistory = false;
          }
          else if(!this.isLoadingData)
            this.populate();
        },
        (error: any) => {
          if(environment.production)
            this.router.navigate(['/access-denied']);
          console.log(error);
          alert('permission load failed');
          this.isLoadingPerms = false;
          if(!this.isLoadingData && this.taskInfo != null)
            this.populate();
          else if(!this.isLoadingData)
            this.isLoadingData = false;
        }
      );
  }

  LoadHistory(singleItem: number) {
    this.selectedValue = singleItem;
    this.itemsPerPage = this.selectedValue;
    this.loadHistory();
  }

  loadHistory() {
    this.isLoadingHistory = true;
    const url = environment.apiUrl + '/api/cc/v1/getccchist';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json'
    });


    // Create the request body with your form data
    const requestBody: any = {      
      i_page: this.page,
      i_size: this.itemsPerPage,
      i_task_no: this.taskNo
    };

    this.http.post(url, requestBody, { headers })
    .subscribe((response: any) => {
        if (response.data.length === 0) {
          this.isLoadingHistory = false;
          console.error('Invalid history table details response format:', response);
        }
        else {
          this.history = response.data;
          this.isLoadingHistory = false;
        }
      },
      (error) => {
        console.error('There was an error retrieving the history table:', error);
        this.isLoadingHistory = false;
      }
    );
  }

  clearSupportingFiles(): void {
    this.selectedFiles = [];
    this.selectedFilesSize = 0;
  }

  onSupportingFilesSelected(event: any): void {
    const files: FileList = event.target.files;
    let currentFilesTotalSize = this.selectedFilesSize;
  
    for (let i = 0; i < files.length; i++) {
      const file = files[i];
  
      if (currentFilesTotalSize + file.size > 10 * 1024 * 1024) {
        alert('Total file size exceeds 10MB. Please select smaller files.');
        continue;
      }
  
      if (!this.selectedFiles.some((f) => f.name === file.name)) {
        this.selectedFiles.push(file);
        currentFilesTotalSize += file.size;
      } else {
        alert(`File "${file.name}" already selected.`);
      }
    }
  
    this.selectedFilesSize = currentFilesTotalSize;
  }

  assignToHistoryCheck(item: any): string{
    if(this.taskStatus == 'PD' || this.taskStatus == 'RQ' || this.taskStatus == 'CMI' || this.taskStatus == 'DMI')
      return item.assign_to ? item.assign_to : item.pickup ? item.pickup : 'N/A';
    return item.pickup ? item.pickup : item.assign_to ? item.assign_to : 'N/A';
  }

  showTaskAction(){
    if(((this.taskStatus == 'PD' || this.taskStatus == 'CMI' || this.taskStatus == 'DMI' || this.taskStatus == 'RQ') && this.roles.includes('FINANCEADMIN')) 
      || (this.taskStatus == 'PFSM' && this.roles.includes('FINANCESENIORMANAGER')) 
      || (this.taskStatus == 'PFH' && this.roles.includes('FINANCEHOD')) 
      || (this.taskStatus == 'PSSS' && this.roles.includes('SSSME')) 
      || ((this.taskStatus == 'PL' || this.taskStatus == 'PCO') && this.roles.includes('LEGAL')))
        return true;

    return false;
  }

  cancelForm(){
    location.href = 'my-task-assigned-tasks';
  }

  submitCheck(){
    if(this.remarks == null || this.remarks.length < 1)
      return true;
    if(this.taskStatus != 'PD' && this.taskStatus != 'PFSM' && this.taskStatus != 'PFH' && this.taskStatus != 'PSSS' && this.taskStatus != 'PL' && this.taskStatus != 'PCO' && this.taskStatus != 'CMI' && this.taskStatus != 'DMI' && this.taskStatus != 'RQ')
      return true;
    else if(this.taskStatus == 'PD' || this.taskStatus == 'CMI' || this.taskStatus == 'DMI' || this.taskStatus == 'RQ'){
      if(this.selectedAction == null)
        return true;
      if(this.selectedAction == 'assign' && this.selectedGroup == null)
        return true;
      if((this.selectedAction == 'debit' || this.selectedAction == 'credit') && (this.debitCreditAmount == null || ~~parseFloat(this.debitCreditAmount ) == 0))
        return true;
      return false;
    }
    else if(this.taskStatus == 'PFSM' || this.taskStatus == 'PFH' || this.taskStatus == 'PSSS')
      return false;
    else if((this.taskStatus == 'PL' || this.taskStatus == 'PCO') && this.selectedAction != null)
      return false;
    return true;
  }

  async submitForm(){
    const url = environment.apiUrl + '/api/cc/v1/updccc';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json'
    });

    // Create the request body with your form data
    const requestBody: any = {      
      i_task_no: this.taskNo,
      i_remark: this.remarks
    };
    if(this.taskStatus == 'PD' || this.taskStatus == 'PL' || this.taskStatus == 'PCO' || this.taskStatus == 'CMI' || this.taskStatus == 'DMI' || this.taskStatus == 'RQ')
      requestBody['i_action'] = this.selectedAction;

    if(this.selectedFilesSize != 0){
      var fileArray: any[] = [];
      for(const file of this.selectedFiles){
        const base64Content = await new Promise<string>((resolve, reject) => {
          const reader = new FileReader();
          reader.onload = (e: any) => resolve(e.target.result);
          reader.onerror = reject;
          reader.readAsDataURL(file);
        });
    
        const fileBody = {
          i_file_nm: file.name,
          i_file_content: base64Content,
          i_file_type: file.type,
          i_file_size: file.size,
          i_file_category: 'S' // Supporting Documents
        };
        fileArray.push(fileBody);
      }
      requestBody['i_supporting_documents'] = fileArray;
    }

    if((this.taskStatus == 'PD' || this.taskStatus == 'CMI' || this.taskStatus == 'DMI' || this.taskStatus == 'RQ') && this.selectedAction == 'assign')
      requestBody['i_group'] = this.selectedGroup;
    else if((this.taskStatus == 'PD' || this.taskStatus == 'CMI' || this.taskStatus == 'DMI' || this.taskStatus == 'RQ') && (this.selectedAction == 'debit' || this.selectedAction == 'credit'))
      requestBody['i_amount'] = this.debitCreditAmount;
    console.log(requestBody);
    this.isLoading = true;
    this.http.post(url, requestBody, { headers })
    .subscribe((response: any) => {
        this.isLoading = false;
        if (response.data && response.data == 'true') {
          location.href = 'my-task-assigned-tasks';
        }
      },
      (error) => {
        console.error('There was an error updating the case:', error);
        this.isLoading = false;
      }
    );

  }
}
