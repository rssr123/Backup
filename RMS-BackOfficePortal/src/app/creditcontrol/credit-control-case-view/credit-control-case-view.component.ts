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
  selector: 'app-credit-control-case-view',
  templateUrl: './credit-control-case-view.component.html',
  styleUrls: ['./credit-control-case-view.component.scss']
})
export class CreditControlCaseViewerComponent implements OnInit {
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

  permCheck = perm.Credit_Control_SME_Task_View_Details;
  permCheckReturnString = ""; // variable to store allowed permission for the user
  permAllow: number = 0;

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
      this.loadPermission();
      //this.isLoadingPerms = false;
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
    this.cheqNo = this.taskInfo.pymt_attr_doc_no;
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
    
    for(let i = 0; i < this.reminders.length; i++){
      if(this.reminders[i].reminder_cnt == 0){
        this.reminders.splice(i, 1);
        this.totalRecordsListOfReminder--;
      }
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
        if (response.data.length >= 0) {
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
          this.permAllow = this.permCheckReturnString.includes(perm.Credit_Control_SME_Task_View_Details) ? 1 : 0;
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
}
