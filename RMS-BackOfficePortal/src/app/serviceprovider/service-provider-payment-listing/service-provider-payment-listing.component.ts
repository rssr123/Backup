import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { TaxCode } from '../../core/models/tax-code.interface';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Router } from '@angular/router';
import { NgbPaginationConfig } from '@ng-bootstrap/ng-bootstrap';
import { environment } from 'src/environments/environment';
import { TranslateService } from '@ngx-translate/core';
import { GlobalService } from 'src/app/shared/global.service';
import { MatDialog } from '@angular/material/dialog';

import { Systemstatus } from '../../shared/enums/systemstatus';
import { fadeInOut } from '../../shared/animation';
import { ParamData } from 'src/app/core/models/param.interface';
import { ParamService } from '../../core/services/param.service';
import { formatDate } from '@angular/common';
import { AuthService } from 'src/app/core/services/auth.service';
import { perm } from 'src/permissions/perm';
import { Observable, finalize, switchMap } from 'rxjs';
import { ServiceProvider } from 'src/app/core/models/service-provider.interface';

@Component({
  selector: 'app-service-provider-payment-listing',
  templateUrl: './service-provider-payment-listing.component.html',
  styleUrls: ['./service-provider-payment-listing.component.scss']
})
export class ServiceProviderPaymentListingComponent implements OnInit {

  page = environment.DefaultPage;
  itemsPerPage = environment.ItemPerPage;
  model: ServiceProvider[] = [];
  pymtStatus: ParamData[] = [];
  isReadOnly = false;
  totalRecords: number = 0;

  // Configuring Permissions for User and roles variables
  permSP = perm.Service_Provider_View_Listing_Page; // all the perm_cd for this module seperated with comma
  permSPAllow = ""; // variable to store allowed permission for the user
  permListAllow: number = 0; // if 0 then not allow to view listing page, else allow
  // end configuration

  profileName: String | null = null;
  agBilNo: String | null = null;
  customerEmail: String | null = null;
  totalAmountPayable: String | null = null;
  paymentStatus: String | null = null;
  agBil: String | null = null;

  isDisplay: boolean = false;

  isLoading: boolean = false;
  //date range picker
  selectedCollectionDate: Date[] | null = null;
  selectedPaymentDate: Date[] | null = null;
  selectedEmailSentDate: Date[] | null = null;
  bsValue = new Date();
  tempDate !: Date;
  minDate = new Date();
  // selected!: { start?: moment.Moment; end?: moment.Moment };
  //date range picker

  editBox: boolean = false;
  addBox: boolean = false;
  deleteBox: boolean = false;
  emailBox: boolean = false;

  //toogle start
  rightSectionCollapsed: boolean = true;

  //default pagination
  selectedValue = environment.dropdownOptions[0];
  dropdownOptions = environment.dropdownOptions;

  states: ParamData[] = [];
  selectedState: string = Systemstatus.Active;

  checkResult: number = 0;

  LoadData(singleItem: number) {
    this.selectedValue = singleItem;
    this.itemsPerPage = this.selectedValue;
    this.loadData();
    //this.resendNotification(singleItem);
  }

  toggleRightSection() {
    this.rightSectionCollapsed = !this.rightSectionCollapsed;
  }
  //toogle end

  DefaultBox() {
    this.editBox = false;
    this.addBox = false;
    this.deleteBox = false;
  }

  AlertBoxInitialize() {
    
      this.showEmailAlertBox();
    
  }

  //for alert box start
  showInsertAlert = false;

  showInsertAlertBox() {
    this.showInsertAlert = true;
    setTimeout(() => (this.showInsertAlert = false), 2000);
  }

  showUpdateAlert = false;

  showUpdateAlertBox() {
    this.showUpdateAlert = true;
    setTimeout(() => (this.showUpdateAlert = false), 2000);
  }

  showDeleteAlert = false;

  showDeleteAlertBox() {
    this.showDeleteAlert = true;
    setTimeout(() => (this.showDeleteAlert = false), 2000);
  }

  showResultAlert = false;

  showResultAlertBox() {
    this.showResultAlert = true;
    setTimeout(() => (this.showResultAlert = false), 2000);
  }

  showDeactiveAlert = false;

  showDeactiveAlertBox() {
    this.showDeactiveAlert = true;
    setTimeout(() => (this.showDeactiveAlert = false), 2000);
  }

  showEmailAlert = false;

  showEmailAlertBox() {
    this.showEmailAlert = true;
    setTimeout(() => (this.showEmailAlert = false), 2000);
  }

  showGenericAlert = false;

  showGenericAlertBox() {
    this.showGenericAlert = true;
    setTimeout(() => (this.showGenericAlert = false), 2000);
  }
  //for alert box end


  

  constructor(
    private http: HttpClient,
    config: NgbPaginationConfig,
    private router: Router,
    public dialog: MatDialog,
    private ParamService: ParamService,
    private translate: TranslateService,
    private globalService: GlobalService,
    private cd: ChangeDetectorRef,
    private authService: AuthService
  ) {
    config.maxSize = environment.PaginationMaxSize;
    config.boundaryLinks = true;
    this.translate.setDefaultLang(this.globalService.getGlobalValue()); // default language
    this.translate.use(this.globalService.getGlobalValue());
  }

  ngOnInit(): void {
    this.minDate.setMonth(this.minDate.getMonth() - 1);
    this.selectedCollectionDate = null;
    this.selectedPaymentDate = null;
    this.selectedEmailSentDate = null;
    this.loadStates();
    this.loadData();
    this.loadEntType();
  }

  //loadData Start
  loadData() {
    this.authService.checkUserRole(this.authService.username, this.permSP)
      .subscribe(
        (response: any) => {
          this.permSPAllow = response.data;
          this.permListAllow = this.permSPAllow.includes(perm.Service_Provider_View_Listing_Page) ? 1 : 0;
          if (this.permListAllow === 0) {
            console.log("access-denied");
            this.router.navigate(['/access-denied']);
            return; // Exit the function to prevent further execution
          }
          console.log(this.permListAllow);

          // Set your authorization header
          const headers = new HttpHeaders({
            Authorization: environment.authKey,
            'Content-Type': 'application/json',
          });


          this.isDisplay = true;
          this.isLoading = true;
          const url = environment.apiUrl + '/api/sp/v1/serviceprovider';

          const Body: any = {
            i_page: this.page.toString(),
            i_size: this.itemsPerPage.toString(),
          };

          if (this.agBil && this.agBil.trim()) {
            Body.i_ag_bil = this.agBil;
            console.log("ag_bil: " + this.agBil);
          }

          if (this.profileName && this.profileName.trim()) {
            Body.i_profile_nm = this.profileName;
          }

          if (this.agBilNo && this.agBilNo.trim()) {
            Body.i_ag_bil_no = this.agBilNo;
          }


          if (this.customerEmail && this.customerEmail.trim()) {
            Body.i_cust_email = this.customerEmail;
          }

          if (this.totalAmountPayable && this.totalAmountPayable.trim()) {
            Body.i_total_amt_payable = this.totalAmountPayable;
          }

          if (this.selectedCollectionDate) {
            Body.i_date_collection_fr = formatDate(this.selectedCollectionDate[0], 'YYYY-MM-dd', 'en');
            this.selectedCollectionDate[1].setDate(this.selectedCollectionDate[1].getDate() + 1);
            Body.i_date_collection_to = formatDate(this.selectedCollectionDate[1], 'YYYY-MM-dd', 'en');
          }

          if (this.paymentStatus && this.paymentStatus.trim()) {
            Body.i_pymt_status = this.paymentStatus;
          }



          if (this.selectedPaymentDate) {
            Body.i_dt_pymt_fr = formatDate(this.selectedPaymentDate[0], 'YYYY-MM-dd', 'en');
            this.selectedPaymentDate[1].setDate(this.selectedPaymentDate[1].getDate() + 1);
            Body.i_dt_pymt_to = formatDate(this.selectedPaymentDate[1], 'YYYY-MM-dd', 'en');
          }

          if (this.selectedEmailSentDate) {
            Body.i_date_email_sent_fr = formatDate(this.selectedEmailSentDate[0], 'YYYY-MM-dd', 'en');
            this.selectedEmailSentDate[1].setDate(this.selectedEmailSentDate[1].getDate() + 1);
            Body.i_date_email_sent_to = formatDate(this.selectedEmailSentDate[1], 'YYYY-MM-dd', 'en');
          }





          console.log(Body);

          this.http.post(url, Body, { headers }).subscribe(
            (response: any) => {

              // console.log("original data");
              // console.log(response.data);


              this.model = response.data;
              console.log(response.data.length);

              if (response.data.length == 0) {
                this.totalRecords = 0;
                this.isDisplay = true;
                //this.showResultAlertBox();
                this.isLoading = false;
              } else {
                this.totalRecords = response.data[0].total;
                //this.AlertBoxInitialize();
                this.DefaultBox();
                this.isLoading = false;
              }
              console.log(response.data);
              //  console.log(this.totalRecords);
            },
            (error) => {
              console.error(error);
              this.isLoading = false;

              this.showGenericAlertBox();
            }
          );
        },
        (error) => {
          console.error(error);
        }
      );


  }
  //loadData End

  //send email noti
  resendNotification(i_ag_bil: number): void {
    const url = environment.apiUrl + '/api/sp/v1/send-notification';
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json'
    });
    const body = { i_ag_bil };
    console.log('Sending notification:', body);

    this.http.post(url, body, { headers }).subscribe(
      response => {
        console.log('Notification sent successfully:', response);
        // Handle success response
      },
      error => {
        console.error('Error sending notification:', error);
        // Handle error response
      }
    );

    this.loadData();
    setTimeout(() => {
      this.AlertBoxInitialize();
    }, 5000);

  }




  apply(): void {
    this.loadData();
    this.rightSectionCollapsed = true;
  }

  reset(): void {
    this.profileName = null;
    this.agBilNo = null; 
    this.customerEmail = null;
    this.totalAmountPayable = null;
    this.paymentStatus = null;

    this.minDate.setDate(this.minDate.getMonth() - 1);
    this.selectedCollectionDate = null;
    this.selectedPaymentDate = null;
    this.selectedEmailSentDate = null;
  }

  refreshMainPage(): void {
    this.page = 1;
    this.loadData();
  }

  loadStates() {
    this.ParamService.getStates('1', '100', '', 'Status').subscribe(
      (response: any) => {
        if (response && response.data && Array.isArray(response.data)) {
          //this.states = response.data as ParamData[];
          this.states.push({ param_cd: '', nm_en: 'All', nm_bm: 'All', total: 5 }); //add 'All' options
          //this.states.push(response.data);
          this.states = [...this.states, ...response.data];
          //this.states.sort((a, b) => a.nm_en.localeCompare(b.nm_en)); // sorting
        } else {
          console.error('Invalid response format:', response);
        }
      },
      (error) => {
        console.error('There was an error retrieving the status:', error);
      }
    );
  }

  async checkRecordInUse(tax_cd_id: any): Promise<any> {
    const url = environment.apiUrl + '/api/tc/v1/checktaxcodeexist';

    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    const body = {
      i_tax_cd_id: tax_cd_id,
    };

    const response: any = await this.http.post(url, body, { headers }).toPromise();
    try {
      this.checkResult = response.data;
      console.log("check: " + response.data);
      return response.data;
    }
    catch (error) {
      console.error(error);
      return error;
    }



  }




  loadEntType() {
    this.ParamService.getStates('1', '100', '', 'cc-pymt-status').subscribe(
      (response: any) => {
        if (response && response.data && Array.isArray(response.data)) {
          //this.states = response.data as ParamData[];
          this.pymtStatus.push({
            param_cd: '',
            nm_en: 'All',
            nm_bm: 'All',
            total: 5
          }); //add 'All' options
          //this.states.push(response.data);
          this.pymtStatus = [...this.pymtStatus, ...response.data];
          //this.states.sort((a, b) => a.nm_en.localeCompare(b.nm_en)); // sorting
          this.paymentStatus = this.pymtStatus[0].param_cd;
        } else {
          console.error('Invalid response format:', response);
        }
      },
      (error: any) => {
        console.error('There was an error retrieving the status:', error);
      }
    );
  }

}
