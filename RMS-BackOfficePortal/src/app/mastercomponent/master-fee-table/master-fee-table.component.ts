import { Component, OnInit } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { environment } from 'src/environments/environment';
import { NgbPaginationConfig } from '@ng-bootstrap/ng-bootstrap';
import { ExportMFT, MFT, SourceSystemCode, TaxCode } from '../../core/models/entity';
import { Router } from '@angular/router';
import { trigger, state, style, transition, animate } from '@angular/animations';
import moment from 'moment';
import { Systemstatus } from '../../shared/enums/systemstatus';
import { DataService } from '../../core/services/data.service';
import { fadeInOut } from '../../shared/animation';
import { formatDate } from '@angular/common';
import { ParamService } from '../../core/services/param.service';
import * as XLSX from 'xlsx';

import { TranslateService } from '@ngx-translate/core';
import { GlobalService } from 'src/app/shared/global.service';
import { AuthService } from 'src/app/core/services/auth.service';
import { perm } from 'src/permissions/perm';
import { NotificationService } from 'src/app/core/services/notification.service';
import { StateData } from 'src/app/core/models/state.interface';

@Component({
  selector: 'app-master-fee-table',
  templateUrl: './master-fee-table.component.html',
  styleUrls: ['./master-fee-table.component.scss'],
  animations: [fadeInOut]
})
export class MasterFeeTableComponent implements OnInit {

  page = environment.DefaultPage;
  itemsPerPage = environment.ItemPerPage;
  dropDownSize = environment.DropDownSize;
  mfts: MFT[] = [];
  exportMFT: ExportMFT[] = [];
  totalRecords: number = 0;
  sourceSystemCodeOptions: SourceSystemCode[] = [];
  taxCodeOptions: TaxCode[] = [];
  totalMFTRecords: number = 0;
  isDisplay: boolean = false;
  isLoading: boolean = false;
  errorMessagesAccessDenied: string[] = [];
  errorAccessDenied: boolean = false;
  invalidInputFrom: boolean = false;
  invalidInputTo: boolean = false;

  //date range picker
  selected!: Date[];//{ start?: moment.Moment; end?: moment.Moment };
  bsValue = new Date();
  tempDate !: Date;
  minDate = new Date();
  //date range picker 

  feeDetailId: String | null = null;
  unitFeeRangeFr: String | null = null;
  unitFeeRangeTo: String | null = null;
  sourceSystemCode: String | null = null;
  taxCode: String | null = null;
  modifiedBy: String | null = null;
  alertMessage: string | undefined = undefined;
  submittedForApprovalBox: boolean = false;
  checkboxOptions: string[] | undefined = undefined;
  alertMessageTaskPending: string | undefined = undefined;
  alertMessageAccessDenied: string | undefined = undefined;
  showTaskNotUpdateAlert: boolean | undefined = undefined;

  //for alert box start
  showSubmittedForApprovalAlert = false;
  showSubmittedForApprovalAlertBox() {
    this.showSubmittedForApprovalAlert = true;
    setTimeout(() => this.showSubmittedForApprovalAlert = false, 10000);
  }

  showResultAlert = false;
  showResultAlertBox() {
    this.showResultAlert = true;
    setTimeout(() => this.showResultAlert = false, 10000);
  }

  showGenericAlert = false;
  showGenericAlertBox() {
    this.showGenericAlert = true;
    setTimeout(() => (this.showGenericAlert = false), 10000);
  }

  showTaskNotExist = false;
  showTaskNotExistAlertBox() {
    this.showTaskNotExist = true;
    setTimeout(() => (this.showTaskNotExist = false), 10000);
  }
  //for alert box end

  //toogle start
  rightSectionCollapsed: boolean = true;

  //default pagination
  selectedValue = environment.dropdownOptions[0];
  dropdownOptions = environment.dropdownOptions;

  states: StateData[] = [];
  selectedState: string = Systemstatus.Active;

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
    this.submittedForApprovalBox = false;
  }

  AlertBoxInitialize() {
    if (this.submittedForApprovalBox) {
      this.showSubmittedForApprovalAlertBox();
    }
  }

  permMFT = perm.Master_Fee_Table_View_MFT + "," + perm.Master_Fee_Table_Add_MFT + "," + perm.Master_Fee_Table_Export_MFT_Excel + "," + perm.Master_Fee_Table_View_MFT_Info; // all the perm_cd for this module seperated with comma
  permMFTAllow = ""; // variable to store allowed permission for the user
  permListAllow: number = 0; // if 0 then not allow to view listing page, else allow
  permMFTInfoListAllow: number = 0; // if 0 then not allow to view listing page, else allow
  permAddMFTAllow: number = 0; 
  permExportMFTExcelAllow: number = 0;

  constructor(
    private http: HttpClient,
    config: NgbPaginationConfig,
    private router: Router,
    private ParamService: ParamService,
    private translate: TranslateService,
    private globalService: GlobalService,
    private authService: AuthService,
   private notificationService: NotificationService ) {
    config.maxSize = 3;
    config.boundaryLinks = true;
    this.translate.setDefaultLang(this.globalService.getGlobalValue()); // default language
    this.translate.use(this.globalService.getGlobalValue());
  }


  ngOnInit(): void {

    this.bsValue = new Date();
    this.minDate.setMonth(this.bsValue.getMonth() - 1);
    //this.selected = [this.minDate, this.bsValue];

    //put default box above alert message
    this.DefaultBox()
    this.alertMessage = history.state.alert_msg;
    this.alertMessageTaskPending = history.state.alert_msg_task_pending;
    this.alertMessageAccessDenied = history.state.alert_msg_access_denied;
    this.showTaskNotUpdateAlert = history.state.showTaskNotUpdateAlert;

    if (this.alertMessage !== undefined) {
      if (this.alertMessage === "submittedForApproval") {
        this.submittedForApprovalBox = true;
      }
    }

    if(this.alertMessageAccessDenied !== undefined){
      this.errorAccessDenied = true;
      this.errorMessagesAccessDenied.push(this.alertMessageAccessDenied);
    }

    if(this.showTaskNotUpdateAlert === true){
      this.showTaskNotExistAlertBox();
    }
    
    // Reset alert_msg in history state so if refresh page, alert message will not persist
    //history.replaceState({ ...history.state, alert_msg: undefined }, '');
    history.replaceState({ ...history.state, alert_msg: undefined, alert_msg_access_denied: undefined, showTaskNotUpdateAlert:undefined   }, '');
    this.populateSourceSystemCode();
    this.populateTaxCode();
    this.loadStates();
    //load data must be place at last
    this.loadData();
  }


  loadData() {

    // this.isDisplay = true;
    this.isLoading = true;

    this.authService.checkUserRole(this.authService.username, this.permMFT)
      .subscribe(
        (response: any) => {
          this.permMFTAllow = response.data;
          this.permListAllow = this.permMFTAllow.includes(perm.Master_Fee_Table_View_MFT) ? 1 : 0;
          this.permMFTInfoListAllow = this.permMFTAllow.includes(perm.Master_Fee_Table_View_MFT_Info) ? 1 : 0;
          this.permAddMFTAllow = this.permMFTAllow.includes(perm.Master_Fee_Table_Add_MFT) ? 1 : 0;
          this.permExportMFTExcelAllow = this.permMFTAllow.includes(perm.Master_Fee_Table_Export_MFT_Excel) ? 1 : 0;
          if (this.permListAllow === 0 && this.permMFTInfoListAllow === 0) {
            this.router.navigate(['/access-denied']);
            return; // Exit the function to prevent further execution
          }

          const url = environment.apiUrl + '/api/mft/v1/getmasterfeetable';

          // Set your authorization header
          const headers = new HttpHeaders({
            Authorization: environment.authKey,
            'Content-Type': 'application/json',
          });

          const Body: any = {
            i_page: this.page,
            i_size: this.itemsPerPage,
            i_fee_detail_pk: null
          };

          if (this.feeDetailId && this.feeDetailId.trim()) {
            Body.i_fee_detail_id = this.feeDetailId;
          }

          if (!this.invalidInputFrom) {
            Body.i_unit_fee_fr = this.unitFeeRangeFr;
          }

          if (!this.invalidInputTo) {
            Body.i_unit_fee_to = this.unitFeeRangeTo;
          }

          if (this.sourceSystemCode && this.sourceSystemCode.trim()) {
            Body.i_ss_cd = this.sourceSystemCode;
          }

          if (this.taxCode && this.taxCode.trim()) {
            Body.i_tax_cd = this.taxCode;
          }


          if (this.selected && this.selected.length > 0) {
            //Body.i_dt_modified_fr = 
            Body.i_dt_modified_fr = formatDate(this.selected[0], 'YYYY-MM-dd', 'en');//.format('YYYY-MM-DD');
            this.selected[1].setDate(this.selected[1].getDate() + 1,);
            Body.i_dt_modified_to = formatDate(this.selected[1], 'YYYY-MM-dd', 'en');
          }


          /* if (this.selected && this.selected.start && this.selected.end) {
             Body.i_dt_modified_fr = this.selected.start.format('YYYY-MM-DD');
             Body.i_dt_modified_to = this.selected.end
               .add(1, 'day')
               .format('YYYY-MM-DD');
           }*/
          if (this.modifiedBy && this.modifiedBy.trim()) {
            Body.i_modified_by = this.modifiedBy;
          }

          let temp = "";

          if (this.selectedState.length > 0 &&
            (this.selectedState == Systemstatus.Active || this.selectedState == Systemstatus.Inactive)) {
            temp = this.selectedState;
          }

          if (temp == Systemstatus.Active || temp == Systemstatus.Inactive) {
            Body.i_status = temp;
          }


          console.log(Body);

          this.http.post(url, Body, { headers }).subscribe(
            (response: any) => {

              if (response.data.length === 0) {
                this.totalRecords = 0;
                //this.showResultAlertBox();
                this.isDisplay = false;
                this.isLoading = false;
              } else {
                this.mfts = response.data;
                this.totalRecords = response.data[0].total;
                this.totalMFTRecords = response.data[0].total;
                this.AlertBoxInitialize();
                this.DefaultBox();
                this.isDisplay = true;
                this.isLoading = false;
              }
              // console.log("MFT legnth is" +this.mfts.length)
              //  console.log(response.data);
              //   console.log(this.totalRecords);
            },
            (error) => {
              console.error(error);
              this.isDisplay = false;
              this.isLoading = false;
              this.showGenericAlertBox();

            }
          );
        }
      );
  }

  addSelected() {
    this.router.navigate(['/mft-fa-fa-rqt-add']);
  }

  viewSeleted(item: any) {

    const fee_detail_pk = item.fee_detail_pk;
    const fee_detail_id = item.fee_detail_id;

    this.router.navigate(['/mft-item-detail'], { state: { fee_detail_pk, fee_detail_id } });
  }

  apply(): void {
    this.loadData();
    this.rightSectionCollapsed = true;
  }

  reset() {
    this.feeDetailId = null;
    this.unitFeeRangeFr = null;
    this.unitFeeRangeTo = null;
    this.modifiedBy = null;
    this.bsValue = new Date();
    this.minDate.setMonth(this.bsValue.getMonth() - 1);
    this.selected = [];
    //this.selected = [this.minDate, this.bsValue];
   //this.selected[1].setDate(this.selected[1].getDate() + 1);
    this.sourceSystemCode = null;
    //this.taxCode= null; 
    //this.taxCode= this.taxCodeOptions[0].tax_cd; 
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
          this.sourceSystemCodeOptions = response.data;
          // this.sourceSystemCodeOptions=this.sourceSystemCodeOptions.concat(response.data)
          // Handle a successful response (e.g., show a success message)
        }
      },
      (error) => {
        console.error('There was an error retrieving the source system code:', error);
        // Handle API errors (e.g., show an error message)
      }
    );

  }

  populateTaxCode() {

    const url = environment.apiUrl + '/api/tc/v1/gettaxcode';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json'
    });


    // Create the request body with your form data
    const requestBody = {

      i_page: this.page,
      i_size: this.dropDownSize,
      i_tax_cd_id: null,
      i_tax_cd: null,
      i_tax_cd_nm_en: null,
      i_tax_cd_nm_bm: null,
      i_modified_by: null,
      i_dt_modified_fr: null,
      i_dt_modified_to: null,
      i_status: Systemstatus.Active
    };

    // Send an HTTP POST request to the API
    this.http.post(url, requestBody, { headers }).subscribe(
      (response: any) => {
        //console.log('API response:', response);
        if (response.data.length === 0) {
          console.error('Invalid response format:', response);
        }
        else {
          this.taxCodeOptions = response.data;
          // this.taxCodeOptions=this.taxCodeOptions.concat(response.data)
        }
      },
      (error) => {
        console.error('There was an error retrieving the tax code:', error);
        // Handle API errors (e.g., show an error message)
      }
    );
  }


  //Unit Fee Range checking start
  validateInputFrom(event: any): void {
    const input = event.target.value;

    if (!/^[0-9]*(\.[0-9]+)?$/.test(input)) {
      this.invalidInputFrom = true;
    } else {
      this.invalidInputFrom = false;
    }
  }

  validateInputTo(event: any): void {
    const input = event.target.value;

    if (!/^[0-9]*(\.[0-9]+)?$/.test(input)) {
      this.invalidInputTo = true;
    } else {
      this.invalidInputTo = false;
    }
  }
  //Unit Fee Range checking end


  async exportSelected() {


    //call api to get total MFT records
    //const totalMFTRecords = await this.getMFTTotalRecords();

    // console.log("Total MFT records is :" + totalMFTRecords);

    const url = environment.apiUrl + '/api/mft/v1/getmasterfeetable';


    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    const Body: any = {
      i_page: environment.DefaultPage,
      i_size: this.totalMFTRecords,
    };

    if (this.feeDetailId && this.feeDetailId.trim()) {
      Body.i_fee_detail_id = this.feeDetailId;
    }

    if (!this.invalidInputFrom) {
      Body.i_unit_fee_fr = this.unitFeeRangeFr;
    }

    if (!this.invalidInputTo) {
      Body.i_unit_fee_to = this.unitFeeRangeTo;
    }

    if (this.sourceSystemCode && this.sourceSystemCode.trim()) {
      Body.i_ss_cd = this.sourceSystemCode;
    }

    if (this.taxCode && this.taxCode.trim()) {
      Body.i_tax_cd = this.taxCode;
    }


    if (this.selected && this.selected.length > 0) {
      //Body.i_dt_modified_fr = 
      Body.i_dt_modified_fr = formatDate(this.selected[0], 'YYYY-MM-dd', 'en');//.format('YYYY-MM-DD');
      this.selected[1].setDate(this.selected[1].getDate() + 1,);
      Body.i_dt_modified_to = formatDate(this.selected[1], 'YYYY-MM-dd', 'en');
    }


    /* if (this.selected && this.selected.start && this.selected.end) {
       Body.i_dt_modified_fr = this.selected.start.format('YYYY-MM-DD');
       Body.i_dt_modified_to = this.selected.end
         .add(1, 'day')
         .format('YYYY-MM-DD');
     }*/
    if (this.modifiedBy && this.modifiedBy.trim()) {
      Body.i_modified_by = this.modifiedBy;
    }

    let temp = "";

    if (this.selectedState.length > 0 && (this.selectedState == Systemstatus.Active || this.selectedState == Systemstatus.Inactive)) {
      temp = this.selectedState;
    }

    if (temp == Systemstatus.Active || temp == Systemstatus.Inactive) {
      Body.i_status = temp;
    }

    this.http.post(url, Body, { headers }).subscribe(
      (response: any) => {
        if (response.data.length === 0) {
          console.log("Export not success");
        } else {
          this.exportMFT = response.data;

          for (let i = 0; i < this.exportMFT.length; i++) {

            if (this.exportMFT[i].allow_otc === 1) {
              this.exportMFT[i].allow_otc = "Yes";
            }
            else {
              this.exportMFT[i].allow_otc = "No"
            }

            if (this.exportMFT[i].status === "A") {
              this.exportMFT[i].status = "Active"
            }
            else {
              this.exportMFT[i].status = "Inactive"
            }

            if(this.exportMFT[i].isPub === 1){
              this.exportMFT[i].isPub = "Yes";
            }
            else{
              this.exportMFT[i].isPub = "No";
            }
          }

          //change key name of exportMFT
          this.exportMFT = response.data.map((item: any) => ({
            'Fee Detail ID': item.fee_detail_id,
            'Fee Detail Name(EN)': item.fee_detail_nm_e,
            'Source System Code': item.ss_cd,
            'Fee Group': item.fee_grp_nm_en,
            'Unit Fee': item.unit_fee,
            'Promotion Start Date': item.promo_startdt,
            'Promotion End Date': item.promo_enddt,
            'Promotion Fee': item.promo_fee,
            'Tax Code': item.tax_cd,
            'Allow OTC': item.allow_otc,
            'Late Lodgement Parent ID': item.ll_parent_id,
            'Late Lodgement Start Day': item.ll_start_day,
            'Late Lodgement Start Month': item.ll_start_mth,
            'Late Lodgement End Day': item.ll_end_day,
            'Late Lodgement End Month': item.ll_end_mth,
            'Ledger Code': item.ledger_cd,
            'Date Created': item.dt_created,
            'Created By': item.created_by_nm,
            'Date Modified': item.dt_modified,
            'Modified By': item.modified_by_nm,
            'Status': item.status,
            'Allow Catalogue': item.isPub,
          }));

          const ws: XLSX.WorkSheet = XLSX.utils.json_to_sheet(this.exportMFT);
          const wb: XLSX.WorkBook = XLSX.utils.book_new();
          XLSX.utils.book_append_sheet(wb, ws, 'Sheet1');
          XLSX.writeFile(wb, 'master_fee_table.xlsx');
          console.log("Export successfully");
        }
      },
      (error) => {
        console.error(error);
        this.isLoading = false;
        // Handle errors here
      }
    );

  }

  loadStates() {
    this.ParamService.getStates('1', '100', '', 'Status').subscribe(
      (response: any) => {
        if (response && response.data && Array.isArray(response.data)) {
          //this.states = response.data as StateData[];
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

  /* async getMFTTotalRecords(): Promise<number> {
 
     const url = environment.apiUrl + '/api/mft/v1/getmasterfeetable';
 
 
     const headers = new HttpHeaders({
       Authorization: environment.authKey,
       'Content-Type': 'application/json',
     });
 
     const Body: any = {
       i_page: this.page,
       i_size: this.itemsPerPage,
     };
 
     try {
       const response: any = await this.http.post(url, Body, { headers }).toPromise();
       if (response.header.statusCode === '01') {
         return 0; // No data found
       } else {
         return response.data[0].total; 
       }
     } catch (error) {
       console.error('There was an error retrieving the total MFT records:', error);
       return 0; // Error occurred
     }
 
 
   }*/

}




