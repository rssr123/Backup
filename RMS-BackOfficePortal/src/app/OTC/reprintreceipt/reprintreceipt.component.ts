import { ChangeDetectorRef, Component, OnInit } from '@angular/core';

import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Router } from '@angular/router';
import { NgbPaginationConfig } from '@ng-bootstrap/ng-bootstrap';
import { environment } from 'src/environments/environment';
import { TranslateService } from '@ngx-translate/core';
import { GlobalService } from 'src/app/shared/global.service';
import { MatDialog } from '@angular/material/dialog';
import { Systemstatus } from '../../shared/enums/systemstatus';
import { fadeInOut } from '../../shared/animation';
import { ParamData } from '../../core/models/param.interface';
import { ParamService } from '../../core/services/param.service';
import { formatDate } from '@angular/common';
import { AuthService } from 'src/app/core/services/auth.service';
import { perm } from 'src/permissions/perm';
import { Observable, finalize, switchMap } from 'rxjs';
import { ReprintReceipt } from 'src/app/core/models/reprint-receipt.interface';

@Component({
  selector: 'app-reprintreceipt',
  templateUrl: './reprintreceipt.component.html',
  styleUrls: ['./reprintreceipt.component.scss'],
  animations: [fadeInOut]
})
export class ReprintreceiptComponent implements OnInit {
  page = environment.DefaultPage;
  itemsPerPage = environment.ItemPerPage;
  model: ReprintReceipt[] = [];
  totalRecords: number = 0;
  isEmptyResult = false;

  


  // Configuring Permissions for User and roles variables
  permRR = perm.OTC_Reprint_Receipt_View_Listing_Page ; // all the perm_cd for this module seperated with comma
  permRRAllow = ""; // variable to store allowed permission for the user
  permListAllow: number = 0; // if 0 then not allow to view listing page, else allow
  
  // end configuration
 
  rcptNo: String | null = null;

  otc_id: string | null = null;
  otc_rc_rp_id: string | null = null
  otcRcptId: string | null = null
  otcPymtMode: string | null = null;

  reprintReceipt: String | null = null;

  isDisplay: boolean = false;

  isLoading: boolean = false;
  //date range picker
  selected: Date[] | null = null;
  bsValue = new Date();
  tempDate !: Date;
  minDate = new Date();
  // selected!: { start?: moment.Moment; end?: moment.Moment };
  //date range picker
  mttId: any;

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
    this.selected = null;
    this.loadStates();
    this.loadData();
  }

  //loadData Start
  loadData() {

    this.authService.checkUserRole(this.authService.username, this.permRR)
      .subscribe(
        (response: any) => {
          this.permRRAllow = response.data;
          this.permListAllow = this.permRRAllow.includes(perm.OTC_Reprint_Receipt_View_Listing_Page) ? 1 : 0;
          console.log(this.permListAllow);
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
    const url = environment.apiUrl + '/api/rr/v1/reprintreceipt';

    const Body: any = {
      i_page: this.page.toString(),
      i_size: this.itemsPerPage.toString(),
    };

    if (this.rcptNo && this.rcptNo.trim()) {
      Body.i_rcpt_no = this.rcptNo;
    }







    let temp = "";

    if (this.selectedState.length > 0 && (this.selectedState == Systemstatus.Active || this.selectedState == Systemstatus.Inactive)) {
      temp = this.selectedState;
    }




    console.log(Body);

    this.http.post(url, Body, { headers }).subscribe(
      (response: any) => {

        // console.log("original data");
        // console.log(response.data);


        this.model = response.data;
        console.log(this.model);
        //this.rcptNo = response.data[0].rcpt_no;

        if (response.data.length == 0) {
          this.totalRecords = 0;
          this.isDisplay = true;
          this.showResultAlertBox();
          this.isEmptyResult = true;
          this.isLoading = false;
        } else {
          this.totalRecords = response.data[0].total;
          this.AlertBoxInitialize();
          this.DefaultBox();
          this.isLoading = false;
          this.isEmptyResult = false;
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

  viewSelected(item: any) {
    this.mttId = item.mtt_id;
    this.otc_id = item.otc_id;
    this.otc_rc_rp_id = item.otc_rc_rp_id;
    this.otcRcptId = item.otc_rcpt_id;
    this.otcPymtMode = item.otc_pymt_mode;
    const rcpt_no = item.rcpt_no;
    this.router.navigate(['/reprintreceiptdetails'], { state: { mtt_id: this.mttId, otc_id: this.otc_id, otc_rc_rp_id: this.otc_rc_rp_id, otc_rcpt_id: this.otcRcptId, otc_pymt_mode: this.otcPymtMode, rcpt_no } });
    console.log("mtt_id=" + this.mttId, "otc_id=" + this.otc_id, "otc_rc_rp_id=" + this.otc_rc_rp_id, "otc_rcpt_id=" + this.otcRcptId, "otc_pymt_mode=" + this.otcPymtMode);
    this.UpdateReceiptJust();
  }

  search(): void {
    this.loadData();
  }

  reset(): void {
    this.rcptNo = null;


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

  // ReceiptWatermark(): void {
  //   const url = environment.apiUrl + '/api/rr/v1/receiptreprintwatermark';

  //   const headers = new HttpHeaders({
  //     Authorization: environment.authKey,
  //     'Content-Type': 'application/json',
  //   });

  //   const body: any = {
  //     mttId: this.mttId,

  //   };
  //   console.log("mttid"+this.mttId);

  //   try {
  //     this.http
  //       .post(url, body, { headers })
  //       .toPromise()
  //       .then((response) => {
  //         console.log('Success response:', response);
  //       })
  //       .catch((error) => {
  //         console.error('Error:', error);
  //       });
  //   } catch (error) {
  //     console.error(error);
  //   }
  // }

  

  UpdateReceiptJust(): void {
    const url = environment.apiUrl + '/api/rr/v1/updrcptjust_rr';

    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    const body: any = {
      i_otc_id: this.otc_id,
      i_otc_rc_rp_id: this.otc_rc_rp_id,
      i_otc_rcpt_id: this.otcRcptId,
      i_modified_by: 'system'

    };
    console.log(body);
    
    console.log(body);

    try {
      this.http
        .post(url, body, { headers })
        .toPromise()
        .then((response) => {
          console.log('Success response:', response);
        })
        .catch((error) => {
          console.error('Error:', error);
        });
      console.log('Success response:', body);
    } catch (error) {
      console.error(error);
    }
  }

  


}