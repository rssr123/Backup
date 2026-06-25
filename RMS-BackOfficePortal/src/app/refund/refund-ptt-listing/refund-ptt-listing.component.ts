import { formatDate } from '@angular/common';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { ChangeDetectorRef, Component, OnInit, ElementRef, HostListener, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { NgbPaginationConfig } from '@ng-bootstrap/ng-bootstrap';
import { TranslateService } from '@ngx-translate/core';
import { Param, SourceSystemCode } from 'src/app/core/models/entity';
import { MTT } from 'src/app/core/models/mtt-interface';
//import { StateData } from 'src/app/core/models/state.interface';
import { ParamData } from 'src/app/core/models/param.interface';
import { Systemstatus } from 'src/app/shared/enums/systemstatus';
import { GlobalService } from 'src/app/shared/global.service';
import { environment } from 'src/environments/environment';
import { perm } from 'src/permissions/perm';
import { AuthService } from 'src/app/core/services/auth.service';
import { RefundPTT } from 'src/app/core/models/refundptt-interface';


@Component({
  selector: 'app-refund-ptt-listing',
  templateUrl: './refund-ptt-listing.component.html',
  styleUrls: ['./refund-ptt-listing.component.scss']
})
export class RefundPTTListingComponent implements OnInit {
  @ViewChild('filterPanel', { static: true }) filterPanel!: ElementRef;
  @ViewChild('filterToggle', { static: true }) filterToggle!: ElementRef;

  getStatusInfo(): string {
    return 'An FMS Code can only be activated if the configured Ledger Count is greater than or equal to the number of MFT items. (Ledger Count ≥ MFT Items)';
  }

  page = environment.DefaultPage;
  itemsPerPage = environment.ItemPerPage;
  model: RefundPTT[] = [];
  totalRecords: number = 0;


  orn_no: String | null = null;
  orn_dt: String | null = null;
  total_amt: String | null = null;
  rcpt_no: String | null = null;
  rcpt_dt: String | null = null;
  mtt_id: number | null = null;
  rms_type: string | null = null;
  total: String | null = null;
  status: string | null = null;
  nm: string | null = null;
  ent_no: string | null = null;
  ent_nm: string | null = null;
  txn_id: string | null = null;
  statusOptions: Param[] = [];
  sourceSystemCodeOptions: SourceSystemCode[] = [];

  // Configuring Permissions for User and roles
  permRefundPTT = perm.Refund_Paid_Transaction_View_Listing_Page;
  permRefundPTTAllow = "";
  permRefundPTTListAllow: number = 1;

  invalidInput: boolean = false;


  isDisplay: boolean = false;

  isLoading: boolean = false;
  //date range picker
  selected!: Date[];
  bsValue = new Date();
  tempDate!: Date;
  minDate = new Date();
  //date range picker

  //date range picker 2
  selected2!: Date[];
  bsValue2 = new Date();
  tempDate2!: Date;
  minDate2 = new Date();
  //date rangee picker 2 

  editBox: boolean = false;
  addBox: boolean = false;
  deleteBox: boolean = false;
  viewBox: boolean = false;

  selectedValue = environment.dropdownOptions[0];
  dropdownOptions = environment.dropdownOptions;

  states: ParamData[] = [];
  selectedState: string | null = null;

  checkResult: number = 0;
  dropDownSize = environment.DropDownSize;

  LoadData(singleItem: number) {
    this.selectedValue = singleItem;
    this.itemsPerPage = this.selectedValue;
    this.loadData();
  }

  //   toogle start
  rightSectionCollapsed: boolean = true;

  //   toggleRightSection() {
  //     this.rightSectionCollapsed = !this.rightSectionCollapsed;
  //   }




  constructor(
    private http: HttpClient,
    config: NgbPaginationConfig,
    private router: Router,
    public dialog: MatDialog,
    private cd: ChangeDetectorRef,
    private translate: TranslateService,
    private globalService: GlobalService,
    private authService: AuthService,
    private elementRef: ElementRef,

  ) {
    config.maxSize = environment.PaginationMaxSize;
    config.boundaryLinks = true;
    this.translate.setDefaultLang(this.globalService.getGlobalValue()); // default language
    this.translate.use(this.globalService.getGlobalValue());
  }

  private getDefaultDateRange(): Date[] {
    const today = new Date();
    const yesterday = new Date(today);
    yesterday.setDate(today.getDate() - 1);

    // normalize to local midnight
    const y = new Date(yesterday.getFullYear(), yesterday.getMonth(), yesterday.getDate());
    const t = new Date(today.getFullYear(), today.getMonth(), today.getDate());

    return [y, t];
  }

  ngOnInit(): void {
    this.bsValue = new Date();
    this.minDate.setMonth(this.bsValue.getMonth() - 1);
    // this.selected = [this.minDate, this.bsValue];
    this.selected2 = this.getDefaultDateRange();

    this.bsValue2 = new Date();
    this.minDate2.setMonth(this.bsValue.getMonth() - 1);
    // this.selected2 = [this.minDate, this.bsValue];
    //this.selected[1].setDate(this.selected[1].getDate() + 1);
    // {
    //   start: moment().subtract(1, 'month'),
    //   end: moment(),
    // };

    //this.loadStates();
    //load data must be place at last
    this.populateStatus();
    this.populateSourceSystemCode();
    this.loadData();
  }


  toggleRightSection() {
    this.rightSectionCollapsed = !this.rightSectionCollapsed;
  }

  validateInput(event: any): void {
    const input = event.target.value;

    if (!/^[0-9]*(\.[0-9]+)?$/.test(input)) {
      this.invalidInput = true;
    } else {
      this.invalidInput = false;
    }
  }

  //loadData Start
  loadData() {

    this.authService.checkUserRole(this.authService.username, this.permRefundPTT)
      .subscribe(
        (response: any) => {
          this.permRefundPTTAllow = response.data;
          this.permRefundPTTListAllow = this.permRefundPTTAllow.includes(perm.Refund_Paid_Transaction_View_Listing_Page) ? 1 : 0;
          console.log(this.permRefundPTTListAllow);
          if (this.permRefundPTTListAllow === 0) {
            console.log("access-denied");
            this.router.navigate(['/access-denied']);
            return; // Exit the function to prevent further execution
          }
          // console.log(this.permListAllow, this.permAddAllow, this.permEditAllow);



          // Set your authorization header
          const headers = new HttpHeaders({
            Authorization: environment.authKey,
            'Content-Type': 'application/json',
          });

          this.isDisplay = true;
          this.isLoading = true;
          const url = environment.apiUrl + '/api/refundl/v1/getrefundpttlisting';


          const Body: any = {
            i_page: this.page.toString(),
            i_size: this.itemsPerPage.toString(),
          };

          // if (this.ss_cd && this.ss_cd.trim()) {
          //   Body.i_ss_cd = this.ss_cd;
          // }

          if (this.orn_no && this.orn_no.trim()) {
            Body.i_orn_no = this.orn_no;
          }


          if (this.rcpt_no && this.rcpt_no.trim()) {
            Body.i_rcpt_no = this.rcpt_no;
          }

          if (this.txn_id && this.txn_id.trim()) {
            Body.i_txn_id = this.txn_id;
          }

          if (this.ent_nm && this.ent_nm.trim()) {
            Body.i_ent_nm = this.ent_nm;
          }


          if (this.selected2 && this.selected2.length > 0) {
            //Body.i_dt_modified_fr =
            Body.i_orn_dt_fr = formatDate(this.selected2[0], 'YYYY-MM-dd', 'en'); //.format('YYYY-MM-DD');
            this.selected2[1].setDate(this.selected2[1].getDate());
            Body.i_orn_dt_to = formatDate(this.selected2[1], 'YYYY-MM-dd', 'en');
          }



          if (this.selectedState != null) {
            Body.i_order_status = this.selectedState;
          }


          console.log(Body);

          this.http.post(url, Body, { headers }).subscribe(
            (response: any) => {

              console.log("original data");
              console.log(response.data);


              this.model = response.data;

              if (response.data.length == 0) {
                this.totalRecords = 0;
                this.isDisplay = false;
                this.isLoading = false;
              } else {
                this.totalRecords = response.data[0].total;

                this.isLoading = false;
              }
              // console.log(response.data);
              //  console.log(this.totalRecords);
            },
            (error) => {
              console.error(error);
              this.isLoading = false;
            }
          );
        },
        (error) => {
          console.error(error);
        }
      );
  }
  //loadData End

  populateStatus() {
    const url = environment.apiUrl + '/api/rms/v1/getparam';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json'
    });


    // Create the request body with your form data
    const requestBody = {
      i_page: this.page,
      i_size: this.dropDownSize, //dont use item per page here because it is for table
      i_param_cd: null,
      i_param_grp_nm: 'OrderStatus'
    };

    // Send an HTTP POST request to the API
    this.http.post(url, requestBody, { headers }).subscribe(
      (response: any) => {
        if (response.data.length === 0) {
          console.error('Invalid response format:', response);
        }
        else {
          this.statusOptions = response.data;
        }
      },
      (error) => {
        console.error('There was an error retrieving the status:', error);

      }
    );
  }

  populateSourceSystemCode() {
    const url = environment.apiUrl + '/api/rms/v1/getsourcesystem';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json'
    });


    // Create the request body with your form data
    const requestBody = {
      i_page: this.page,
      i_size: this.dropDownSize,
      i_ss_id: null,
      i_ss_cd: null,
      i_ss_nm: null,
      i_modified_by: null,
      i_dt_modified_fr: null,
      i_dt_modified_to: null,
      i_status: Systemstatus.Active
    };


    // Send an HTTP POST request to the API
    this.http.post(url, requestBody, { headers }).subscribe(
      (response: any) => {
        if (response.data.length === 0) {
          console.error('Invalid response format:', response);
        }
        else {
          this.sourceSystemCodeOptions = response.data
          // this.sourceSystemCodeOptions=this.sourceSystemCodeOptions.concat(response.data)
          // Handle a successful response (e.g., show a success message)
        }
      },
      (error) => {
        console.error('There was an error retrieving the source code system:', error);
        // Handle API errors (e.g., show an error message)
      }
    );

  }

  viewSelected(item: any): void {
    const mtt_id = item.mtt_id;
    const orn_no = item.orn_no;
    const txn_id = item.txn_id;
    const rms_type = item.rms_type;
    const rtt_status = item.rtt_status;
    this.router.navigate(['/refund-initial-fa'], { state: { mtt_id, orn_no, txn_id, rms_type, rtt_status } });
  }

  apply(): void {
    this.loadData();
    this.rightSectionCollapsed = true;
  }

  reset(): void {
    this.orn_no = null;
    this.total_amt = null;
    this.rcpt_no = null;
    this.txn_id = null;
    this.ent_nm = null;
    this.selectedState = null;
    this.selected2 = this.getDefaultDateRange();
    // this.selected = {
    //   start: moment().subtract(1, 'month'),
    //   end: moment(),
    // };
    this.bsValue = new Date();
    this.minDate.setMonth(this.bsValue.getMonth() - 1);
    this.selected = [];
    // this.selected = [this.minDate, this.bsValue];
    // this.selected[1].setDate(this.selected[1].getDate() + 1);

    this.bsValue2 = new Date();
    this.minDate2.setMonth(this.bsValue.getMonth() - 1);
    this.selected2 = [];
    // this.selected2 = [this.minDate, this.bsValue];
    // this.selected2[1].setDate(this.selected[1].getDate() + 1);
  }

  refreshMainPage(): void {
    this.page = 1;
    this.loadData();
  }

  /** instead of just toggleRightSection(), catch the event and stop it */
  onFilterToggleClick(event: MouseEvent) {
    event.stopPropagation();       // ← prevent the document listener from seeing this click
    this.toggleRightSection();     // ← your existing open/close logic
  }

  // Uncomment the following code if you want to collapse the filter panel when clicking outside of it
  // @HostListener('document:click', ['$event.target'])
  // onDocumentClick(target: HTMLElement) {
  //   const clickedInsidePanel = this.filterPanel.nativeElement.contains(target);
  //   const clickedToggle = this.filterToggle.nativeElement.contains(target);

  //   // if you click neither the toggle nor the panel, collapse
  //   if (!clickedInsidePanel && !clickedToggle) {
  //     this.rightSectionCollapsed = true;
  //   }
  // }
  @HostListener('document:click', ['$event'])
  onDocumentClick(event: MouseEvent) {
    // if panel is already closed, nothing to do
    if (this.rightSectionCollapsed) { return; }

    const target = event.target as HTMLElement;

    // 1) if click was inside the panel, do nothing:
    if (this.filterPanel.nativeElement.contains(target)) {
      return;
    }

    // 2) if click was on the toggle button itself, do nothing:
    if (this.filterToggle.nativeElement.contains(target)) {
      return;
    }

    // otherwise collapse
    this.rightSectionCollapsed = true;
  }


}


