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
import { CourtOrder } from 'src/app/core/models/court-order.interface';

@Component({
  selector: 'app-court-order-listing',
  templateUrl: './court-order-listing.component.html',
  styleUrls: ['./court-order-listing.component.scss']
})
export class CourtOrderListingComponent implements OnInit {

  page = environment.DefaultPage;
  itemsPerPage = environment.ItemPerPage;
  model: CourtOrder[] = [];
  pymtStatus: ParamData[] = [];
  isReadOnly = false;
  totalRecords: number = 0;

  // Configuring Permissions for User and roles variables
  permCO = perm.Court_Order_View_Listing_Page; // all the perm_cd for this module seperated with comma
  permCOAllow = ""; // variable to store allowed permission for the user
  permListAllow: number = 0; // if 0 then not allow to view listing page, else allow
  // end configuration

  taskID: String | null = null;
  taskStatus: String | null = null;
  paymentStatus: String | null = null;
  transactionType: String | null = null;
  caseNo: String | null = null;
  assignBy: String | null = null;
  amount: String | null = null;

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
    if (this.editBox) {
      this.showUpdateAlertBox();
    } else if (this.addBox) {
      this.showInsertAlertBox();
    } else if (this.deleteBox) {
      this.showDeleteAlertBox();
    }
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
    // this.loadStates();
    this.loadData();
    //this.loadEntType();
  }

  //loadData Start
  loadData() {
    this.authService.checkUserRole(this.authService.username, this.permCO)
      .subscribe(
        (response: any) => {
          this.permCOAllow = response.data;
          this.permListAllow = this.permCOAllow.includes(perm.Court_Order_View_Listing_Page) ? 1 : 0;
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
          const url = environment.apiUrl + '/api/co/v1/courtorder';

          const Body: any = {
            i_page: this.page.toString(),
            i_size: this.itemsPerPage.toString(),
          };

          if (this.taskID && this.taskID.trim()) {
            Body.i_task_no = this.taskID;
          }


          if (this.paymentStatus && this.paymentStatus.trim()) {
            Body.i_pymt_status = this.paymentStatus;
          }

        

          if (this.transactionType && this.transactionType.trim()) {
            Body.i_txn_ty = this.transactionType;
          }

          if (this.caseNo && this.caseNo.trim()) {
            Body.i_attr_case_no = this.caseNo;
          }


          







          console.log(Body);

          this.http.post(url, Body, { headers }).subscribe(
            (response: any) => {

              // console.log("original data");
              // console.log(response.data);


              this.model = response.data;

              if (response.data.length == 0) {
                this.totalRecords = 0;
                this.isDisplay = true;
                //this.showResultAlertBox();
                this.isLoading = false;
              } else {
                this.totalRecords = response.data[0].total;
                this.AlertBoxInitialize();
                this.DefaultBox();
                this.isLoading = false;
              }
              // console.log(response.data);
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




  apply(): void {
    this.loadData();
    this.rightSectionCollapsed = true;
  }

  reset(): void {
    this.taskID = null;
    this.paymentStatus = null;
    this.transactionType = null;
    this.caseNo = null;


  }

  refreshMainPage(): void {
    this.page = 1;
    this.loadData();
  }

  viewSelected(item: any) {
    const cc_case_id = item.cc_case_id;
    const cc_case_a_id = item.cc_case_a_id;
    const cc_cs_item_id = item.cc_cs_item_id;
    const task_no = item.task_no;
    
    this.router.navigate(['/credit-control-case'], { 
      queryParams: { task_no },
      state: { cc_case_id , cc_case_a_id , cc_cs_item_id } 
    });    console.log("cc_case_id=" + cc_case_id);
    console.log("cc_case_a_id=" + cc_case_a_id);
    console.log("cc_cs_item_id=" + cc_cs_item_id)
    console.log("task_no=" + task_no);
  }



  // loadStates() {
  //   this.ParamService.getStates('1', '100', '', 'Status').subscribe(
  //     (response: any) => {
  //       if (response && response.data && Array.isArray(response.data)) {
  //         //this.states = response.data as ParamData[];
  //         this.states.push({ param_cd: '', nm_en: 'All', nm_bm: 'All', total: 5 }); //add 'All' options
  //         //this.states.push(response.data);
  //         this.states = [...this.states, ...response.data];
  //         //this.states.sort((a, b) => a.nm_en.localeCompare(b.nm_en)); // sorting
  //       } else {
  //         console.error('Invalid response format:', response);
  //       }
  //     },
  //     (error) => {
  //       console.error('There was an error retrieving the status:', error);
  //     }
  //   );
  // }

  // async checkRecordInUse(tax_cd_id: any): Promise<any> {
  //   const url = environment.apiUrl + '/api/tc/v1/checktaxcodeexist';

  //   const headers = new HttpHeaders({
  //     Authorization: environment.authKey,
  //     'Content-Type': 'application/json',
  //   });

  //   const body = {
  //     i_tax_cd_id: tax_cd_id,
  //   };

  //   const response: any = await this.http.post(url, body, { headers }).toPromise();
  //   try {
  //     this.checkResult = response.data;
  //     console.log("check: " + response.data);
  //     return response.data;
  //   }
  //   catch (error) {
  //     console.error(error);
  //     return error;
  //   }



  //}




  // loadEntType() {
  //   this.ParamService.getStates('1', '100', '', 'OrderStatus').subscribe(
  //     (response: any) => {
  //       if (response && response.data && Array.isArray(response.data)) {
  //         //this.states = response.data as ParamData[];
  //         this.pymtStatus.push({
  //           param_cd: '',
  //           nm_en: 'All',
  //           nm_bm: 'All',
  //           total: 5
  //         }); //add 'All' options
  //         //this.states.push(response.data);
  //         this.pymtStatus = [...this.pymtStatus, ...response.data];
  //         //this.states.sort((a, b) => a.nm_en.localeCompare(b.nm_en)); // sorting
  //         this.paymentStatus = this.pymtStatus[0].param_cd;
  //       } else {
  //         console.error('Invalid response format:', response);
  //       }
  //     },
  //     (error: any) => {
  //       console.error('There was an error retrieving the status:', error);
  //     }
  //   );
  // }

}
