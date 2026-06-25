import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { environment } from 'src/environments/environment';
import { NgbPaginationConfig } from '@ng-bootstrap/ng-bootstrap';
import { Router } from '@angular/router';
import { DataService } from 'src/app/core/services/data.service';
import { AuthService } from 'src/app/core/services/auth.service';
import { DatePipe } from '@angular/common';
import { saveAs } from "file-saver";
import { empty } from 'rxjs';
import { NgForm } from '@angular/forms';
import { RICPAging } from 'src/app/core/models/ricpaging.interface';
import { fadeInOut } from 'src/app/shared/animation';
import { perm } from 'src/permissions/perm';

@Component({
  selector: 'app-ricp-aging',
  templateUrl: './ricp-aging.component.html',
  styleUrls: ['./ricp-aging.component.scss'],
  animations: [fadeInOut]
})
export class RICPAgingComponent implements OnInit {
  months: String[] = ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"];

  requestDate: string | null = null;
  showExpiry: number | null = 0;
  showCanVoid: number | null = 0;
  entityType: string | null = null;
  entityName: string | null = null;
  issDt!: Date[];
  issDtFr: string | null = null;
  issDtTo: string | null = null;
  pymtDt!: Date[];
  pymtDtFr: string | null = null;
  pymtDtTo: string | null = null;
  expDt!: Date[];
  expDtFr: string | null = null;
  expDtTo: string | null = null;
  woDt!: Date[];
  woDtFr: string | null = null;
  woDtTo: string | null = null;
  canDt!: Date[];
  canDtFr: string | null = null;
  canDtTo: string | null = null;
  voidDt!: Date[];
  voidDtFr: string | null = null;
  voidDtTo: string | null = null;

  emailNotification: number | null = 0;
  isDisplayReport: boolean = false;
  isLoadingReport: boolean = false;
  pageReport = environment.DefaultPage;
  itemsPerPageReport = environment.ItemPerPage;
  dropDownSize = environment.DropDownSize;
  totalRecordsReport: number = 0;
  ricpagings: RICPAging[] = [];

  selectedFileType: string = 'pdf'; // Default value

  //default pagination
  selectedValue = environment.dropdownOptions[0];
  dropdownOptions = environment.dropdownOptions;

  LoadData(singleItem: number) {
    this.selectedValue = singleItem;
    this.itemsPerPageReport = this.selectedValue;
    this.loadData();
  }

  fileType: any =
    [{ value: 'csv', label: 'CSV' },
    { value: 'pdf', label: 'PDF' },
    { value: 'xlsx', label: 'XLSX' }]

  emailOption: any =
    [
      { value: 0, label: 'No' },
      { value: 1, label: 'Yes' }
    ]

  entityTypeOptions: any = []

  showResultAlert = false;
  showResultAlertBox() {
    this.showResultAlert = true;
    setTimeout(() => this.showResultAlert = false, 2000);
  }

  showCancelAlert = false;
  showCancelAlertBox() {
    this.showCancelAlert = true;
    setTimeout(() => this.showCancelAlert = false, 2000);
  }
  
  showPendingAlert = false;
  showPendingAlertBox() {
    this.showPendingAlert = true;
    setTimeout(() => this.showPendingAlert = false, 2000);
  }
  
  // Configuring Permissions for User and roles variables
  permReport = perm.Reporting_and_Analysis_RICP_Aging

  permReportAllow = ""; // variable to store allowed permission for the user
  permListAllow: number = 0; // if 0 then not allow to view listing page, else allow

  alphaNumberOnly (e : any) {
    var regex = new RegExp("^[a-zA-Z0-9]+$");
    var str = String.fromCharCode(!e.charCode ? e.which : e.charCode);
    if (regex.test(str)) {
        return true;
    }

    e.preventDefault();
    return false;
  }

  constructor(private http: HttpClient, config: NgbPaginationConfig, private router: Router, private dataService: DataService, private authService: AuthService, public datepipe: DatePipe) {
    config.maxSize = 3;
    config.boundaryLinks = true;
  }

  ngOnInit() {
    this.waitForElm('.btnAdd').then((elm) => {
      this.loadData();
    });
    this.resetWithDefaults();
    this.populateEntityTypes();
    this.loadPermission();
  }

  formatDate(date: Date): string {
    const day = ('0' + date.getDate()).slice(-2);
    const month = date.toLocaleString('default', { month: 'short' });
    const year = date.getFullYear();
    return `${day} ${month} ${year}`;
  }

  resetWithDefaults() {
    this.requestDate = this.formatDate(new Date());
    this.showExpiry = 0;
    this.showCanVoid = 0;
    this.entityType = null;
    this.entityName = null;
    this.issDt = [];
    this.issDt.push(new Date(new Date().getFullYear() + "-01-01"));
    this.issDt.push(new Date(new Date().getFullYear() + "-12-31"));
    this.issDtFr = null;
    this.issDtTo= null;
    this.pymtDt = [];
    this.pymtDtFr = null;
    this.pymtDtTo = null;
    this.expDt = [];
    this.expDtFr = null;
    this.expDtTo = null;
    this.woDt = [];
    this.woDtFr = null;
    this.woDtTo = null;
    this.canDt = [];
    this.canDtFr = null;
    this.canDtTo = null;
    this.voidDt = [];
    this.voidDtFr = null;
    this.voidDtTo = null;
    this.emailNotification = 0;
    this.selectedFileType = 'pdf';
  }

  onKeyDown(event: KeyboardEvent): void {
    if (event.key === 'Backspace') {
      return;
    }
    // Prevent manual key entry
    event.preventDefault();
  }

  //form handle before submit start
  async handleFormSubmit(form: NgForm) {

    if(this.selectedFileType == null || this.selectedFileType.length < 1)
      return;

    if(this.ricpagings !== null && this.ricpagings.length > 0){
          if(this.ricpagings[0].status !== 'P')
            await this.insertRICPAging()
    }
    else
      await this.insertRICPAging()
  }

  convertDateString(dateS: Date): string{
    var date = new Date(dateS.toString());
    return ("0" + date.getDate()).slice(-2) + ' ' + this.months[date.getMonth()] + ' ' + date.getFullYear() + ' ' +
     ("0" + date.getHours()).slice(-2)  + ':' + ("0" + date.getMinutes()).slice(-2) + ':' + ("0" + date.getSeconds()).slice(-2);
  }

  loadPermission() {
    this.authService.checkUserRole(this.authService.username, this.permReport)
      .subscribe(
        (response: any) => {
          this.permReportAllow = response.data;
          this.permListAllow = this.permReportAllow.includes(perm.Reporting_and_Analysis_RICP_Aging) ? 1 : 0;
          if (this.permListAllow === 0) {
            console.log("access-denied");
            this.router.navigate(['/access-denied']);
            return; // Exit the function to prevent further execution
          }
        }
      );
  }

  async insertRICPAging(): Promise<void> {

    const insertURL = environment.apiUrl + '/api/report/v1/ricp_aging/req_report';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json'
    });

    this.requestDate = this.datepipe.transform(this.requestDate, 'yyyy-MM-dd') || null;

    if (this.issDt && this.issDt.length > 0) {
      this.issDtFr = this.datepipe.transform(this.issDt[0], 'yyyy-MM-dd');
      this.issDtTo = this.datepipe.transform(this.issDt[1], 'yyyy-MM-dd');
    }

    const body: any = {
      i_req_date: this.requestDate,
      i_dt_iss_fr: this.issDtFr,
      i_dt_iss_to: this.issDtTo,
      i_exp_status: this.showExpiry,
      i_can_v_status: this.showCanVoid,
      i_file_type: this.selectedFileType,
      i_ent_ty: '',
      i_ent_nm: '',
      i_dt_rcpt_fr: '',
      i_dt_rcpt_to: '',
      i_dt_exp_fr: '',
      i_dt_exp_to: '',
      i_dt_wo_fr: '',
      i_dt_wo_to: '',
      i_dt_can_fr: '',
      i_dt_can_to: '',
      i_dt_void_fr: '',
      i_dt_void_to: '',
      i_email: this.emailNotification
    };

    if (this.entityType !== null && this.entityType.trim() !== '') {
      body.i_ent_ty = this.entityType;
    }

    if (this.entityName !== null && this.entityName.trim() !== '') {
      body.i_ent_nm = this.entityName;
    }

    if (this.pymtDt && this.pymtDt.length > 0) {
      this.pymtDtFr = this.datepipe.transform(this.pymtDt[0], 'yyyy-MM-dd');
      this.pymtDtTo = this.datepipe.transform(this.pymtDt[1], 'yyyy-MM-dd');
      body.i_dt_rcpt_fr = this.pymtDtFr;
      body.i_dt_rcpt_to = this.pymtDtTo;
    }

    if (this.expDt && this.expDt.length > 0) {
      this.expDtFr = this.datepipe.transform(this.expDt[0], 'yyyy-MM-dd');
      this.expDtTo = this.datepipe.transform(this.expDt[1], 'yyyy-MM-dd');
      body.i_dt_exp_fr = this.expDtFr;
      body.i_dt_exp_to = this.expDtTo;
    }

    if (this.woDt && this.woDt.length > 0) {
      this.woDtFr = this.datepipe.transform(this.woDt[0], 'yyyy-MM-dd');
      this.woDtTo = this.datepipe.transform(this.woDt[1], 'yyyy-MM-dd');
      body.i_dt_wo_fr = this.woDtFr;
      body.i_dt_wo_to = this.woDtTo;
    }

    if (this.canDt && this.canDt.length > 0) {
      this.canDtFr = this.datepipe.transform(this.canDt[0], 'yyyy-MM-dd');
      this.canDtTo = this.datepipe.transform(this.canDt[1], 'yyyy-MM-dd');
      body.i_dt_can_fr = this.canDtFr;
      body.i_dt_can_to = this.canDtTo;
    }

    if (this.voidDt && this.voidDt.length > 0) {
      this.voidDtFr = this.datepipe.transform(this.voidDt[0], 'yyyy-MM-dd');
      this.voidDtTo = this.datepipe.transform(this.voidDt[1], 'yyyy-MM-dd');
      body.i_dt_void_fr = this.voidDtFr;
      body.i_dt_void_to = this.voidDtTo;
    }

    try {
      (<HTMLButtonElement>document.getElementById('submit'))!.disabled = true;
      const response: any = await this.http.post(insertURL, body, { headers }).toPromise();
      if (response.header.statusCode === '00') {
        //  this.wfIdFromInsert = response.data;
        this.showResultAlertBox();
        this.loadData();
      } else {
        console.log("error")
      }
    } catch (error) {
      console.error(error);
    }
  }

  loadData() {

    this.isDisplayReport = true;
    this.isLoadingReport = true;

    const urlMftWF = environment.apiUrl + '/api/report/v1/ricp_aging/listing';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json'
    });

    // Create the request body with your form data
    const Body: any = {
      i_page: this.pageReport,
      i_size: this.itemsPerPageReport
    };

    this.http.post(urlMftWF, Body, { headers }).subscribe(
      (response: any) => {
        if (response.data.length === 0) {
          this.totalRecordsReport = 0;
          this.isDisplayReport = false
          this.isLoadingReport = false;
        }
        else {
          this.ricpagings = response.data.data;
          this.isLoadingReport = false;
          this.totalRecordsReport = response.data.total;
          //console.log(this.ricpagings);
        }

        if(this.ricpagings !== null && this.ricpagings.length > 0){
          if(this.ricpagings[0].status === 'P')
            (<HTMLButtonElement>document.getElementById('submit'))!.disabled = true;
          else
            (<HTMLButtonElement>document.getElementById('submit'))!.disabled = false;
        }
        else
            (<HTMLButtonElement>document.getElementById('submit'))!.disabled = false;
      },
      (error) => {
        console.error('There was an error retrieving the RICP aging flow:', error);
        this.isLoadingReport = false;
        // Handle errors here
      }
    );
  }

  populateEntityTypes(){
    const url = environment.apiUrl + '/api/rms/v1/getparam';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json'
    });

     // Create the request body with your form data
     const requestBody = {
      i_page: this.pageReport,
      i_size: this.dropDownSize,
      i_param_cd: null,
      i_param_grp_nm: 'EntityType'
    };

    // Send an HTTP POST request to the API
    this.http.post(url, requestBody, { headers }).subscribe(
      (response:any) => {
        if(response.data.length > 0){
          this.entityTypeOptions.push({value: null, label: ''});
          for(const item of response.data){
            this.entityTypeOptions.push({value: item.param_cd, label: item.nm_en});
          }
        }
      },
      (error) => {
        console.error('API error: Cannot populate EntityType', error);
        // Handle API errors (e.g., show an error message)
      }
    );
  }


  formatDateForReportName(dateString: string): string {
    const formattedDate = this.datepipe.transform(dateString, 'yyyyMMdd');
    return formattedDate ? formattedDate : '';
  }

  getReportName(item: any): string {
    //const receiptDate = this.formatDateForReportName(item.p_dt_req);
    //return `RICP_Aging_${receiptDate}.${item.p_file_type}`;
    //return `RICP_Aging_${receiptDate}`;
    //return item.p_file_nm.split('.')[0];
    return item.p_file_nm;
  }


  downloadFile(file_nm: string, file_ext: string) {

    const generateURL = environment.apiUrl + '/api/report/v1/aging/download_report';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json'
    });

    // Create the request body with your form data
    var requestBody: { [k: string]: any } = {
      i_file_name: file_nm,
      i_report_format: file_ext
    };

    var fileTypeMime = file_ext == 'pdf' ? 'application/pdf' : file_ext == 'xlsx' ? 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' : file_ext == 'csv' ? 'text/csv' :
      'application/octet-stream';

    this.http.post(generateURL, requestBody, { observe: 'response', responseType: 'blob', headers: headers })
      .subscribe(response => {
        var blob = new Blob([response.body as Blob], { type: fileTypeMime });
        saveAs(blob, response.headers.get('content-disposition')!.split('filename=')[1]);
      });
  }


  async cancelFile(rpt_ricp_age_id: number, event: any): Promise<void>{
    event.target.disabled = true;
    const updURL = environment.apiUrl + '/api/report/v1/ricp_aging/cancel';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json'
    });

    // Create the request body with your form data
    var requestBody: { [k: string]: any } = {
      i_req_id: rpt_ricp_age_id
    };

    try {
      const response: any = await this.http.post(updURL, requestBody, { headers }).toPromise();
      if (response.header.statusCode === '00') {
        this.showCancelAlertBox();
        this.loadData();
      } else {
        console.log("error")
      }
    } catch (error) {
      console.error(error);
    }
  }

  waitForElm(selector: any) {
    return new Promise(resolve => {
        if (document.querySelector(selector)) {
            return resolve(document.querySelector(selector));
        }

        const observer = new MutationObserver(mutations => {
            if (document.querySelector(selector)) {
                observer.disconnect();
                resolve(document.querySelector(selector));
            }
        });

        // If you get "parameter 1 is not of type 'Node'" error, see https://stackoverflow.com/a/77855838/492336
        observer.observe(document.body, {
            childList: true,
            subtree: true
        });
    });
  }
}
