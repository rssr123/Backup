import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { environment } from 'src/environments/environment';
import { MFT, MFTWF, Param, User } from '../../core/models/entity';
import { Router } from '@angular/router';
import { NgForm } from '@angular/forms';
import { DataService } from '../../core/services/data.service';
import moment from 'moment';
import { Systemstatus } from '../../shared/enums/systemstatus';
import { trigger, state, style, transition, animate } from '@angular/animations';
import { fadeInOut } from '../../shared/animation';
import { TranslateService } from '@ngx-translate/core';
import { GlobalService } from 'src/app/shared/global.service';
import { AuthService } from 'src/app/core/services/auth.service';
import { perm } from 'src/permissions/perm';

@Component({
  selector: 'app-mft-item-details',
  templateUrl: './mft-item-details.component.html',
  styleUrls: ['./mft-item-details.component.scss'],
  animations: [fadeInOut]
})
export class MftItemDetailsComponent implements OnInit {

  username = this.authService.username;

  isLoading: boolean = false;
  isDisplayTaskLists: boolean = false;
  isLoadingTaskLists: boolean = false;
  errorMessages: string[] = [];
  error: boolean = false;
  mfts: MFT[] = [];
  updateWFstatus: string = "";
  navigateToAssignedTask = false;
  mftwfs: MFTWF[] = [];
  statusOptions: Param[] = [];
  users: User[] = [];
  checkboxOptions: string[] | undefined = undefined;
  ssCdWithSpace: string | null = null;
  page = environment.DefaultPage;
  itemsPerPage = environment.ItemPerPage;
  totalMFTRecords: number = 0;
  totalRecords: number = 0;
  forCheckingMFTWFS: MFTWF[] = [];
  currentMFTWFStatus: string | null = null;

  pageTaskLists = environment.DefaultPage;
  itemsPerPageTaskLists = environment.ItemPerPage;
  totalRecordsTaskLists: number = 0;


  selectedEffectiveDate!: { start?: moment.Moment; end?: moment.Moment };
  selectedRequestedDate!: { start?: moment.Moment; end?: moment.Moment };

  wfId: number | null = null;
  feeDetailPk: number | null = null;
  feeDetailId: string | null = null;
  feeGroupId: string | null = null;
  feeGrpNmEn: string | null = null;
  feeGrpNmBm: string | null = null;
  feeDetailNmE: string | null = null;
  feeDetailNmB: string | null = null;
  unitFee: number | null = null;
  promoStartDate: Date | null = null;
  promoEndDate: Date | null = null;
  promoFee: number | null = null;
  taxCd: string | null = null;
  allowOTC: number | null = null;
  llParentId: string | null = null;
  llStartDay: number | null = null;
  llStartMth: number | null = null;
  llEndDay: number | null = null;
  llEndMth: number | null = null;
  ledgerCd: string | null = null;
  ssCd: string | null = null;
  dtCreated: Date | null = null;
  dtModified: Date | null = null;
  createdBy: string | null = null;
  createdByNm: string | null = null;
  modifiedBy: string | null = null;
  modifiedByNm: string | null = null;
  status: string | null = null;
  effectiveDate: Date | null = null;
  textRemarks: string | null = null;
  ssm4uuserrefno: string | null = null;
  taskId: string | null = null;
  selectedTaskId: string | null = null;
  selectedStatus: string | null = null;
  nm: string | null = null;
  isPublic: number| null = null;

  //default pagination
  selectedValue = environment.dropdownOptions[0];
  dropdownOptions = environment.dropdownOptions;

  LoadData(singleItem: number) {
    this.selectedValue = singleItem;
    this.itemsPerPageTaskLists = this.selectedValue;
    this.loadData();
  }

  permMFTDetails = perm.Master_Fee_Table_View_MFT_Details + "," + perm.Master_Fee_Table_Edit_MFT; // all the perm_cd for this module seperated with comma
  permMFTDetailsAllow = ""; // variable to store allowed permission for the user
  permListAllow: number = 0; // if 0 then not allow to view listing page, else allow
  permEditMFTAllow: number = 0; // if 0 then not allow to view listing page, else allow

  constructor(
    private route: ActivatedRoute,
    private http: HttpClient,
    private router: Router,
    private translate: TranslateService,
    private globalService: GlobalService,
    private authService: AuthService) {
    this.translate.setDefaultLang(this.globalService.getGlobalValue()); // default language
    this.translate.use(this.globalService.getGlobalValue());
  }


  async ngOnInit() {

    this.selectedEffectiveDate = {
      start: moment().subtract(1, 'month'),
      end: moment(),
    };

    this.selectedRequestedDate = {
      start: moment().subtract(1, 'month'),
      end: moment(),
    };

    this.feeDetailPk = history.state.fee_detail_pk;
    //this.feeDetailId = history.state.fee_detail_id;

    if (this.feeDetailPk !== undefined) {
      this.isLoading = true;
      this.checkPermission();
      this.loadData();
      //  this.populateStatus();
      // this.populateRequester();
      await this.populateForm();

    }

  }

  loadData() {
    this.isDisplayTaskLists = true;
    this.isLoadingTaskLists = true;

    const urlMftWF = environment.apiUrl + '/api/mftwf/v1/getmasterfeetableworkflow';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json'
    });


    // Create the request body with your form data
    const Body: any = {

      i_page: this.pageTaskLists.toString(),
      i_size: this.itemsPerPageTaskLists.toString(),
      i_fee_detail_pk: this.feeDetailPk
    };

    Body.i_wf_is_in_prg = "lf";


    this.http.post(urlMftWF, Body, { headers }).subscribe(
      (response: any) => {
        // console.log(response);
        // You can process the response data here

        if (response.data.length === 0) {
          this.totalRecordsTaskLists = 0;
          this.isDisplayTaskLists = false
          this.isLoadingTaskLists = false;
        //  console.error('Invalid master fee table work flow response format:', response);// no needed because data.length can be 0 after scheduler execute and delete data from mftwf table
        }
        else {
          this.mftwfs = response.data;
          this.isLoadingTaskLists = false;
          this.totalRecordsTaskLists = response.data[0].total;
        }
        //console.log(response.data);
        // console.log(this.totalRecords);

      },
      (error) => {
        console.error('There was an error retrieving the master fee table work flow:', error);
        this.isLoadingTaskLists = false;
        // Handle errors here
      }
    );
  }


  apply() {
    this.loadData()
  }


  reset() {

    this.selectedTaskId = null;
    this.selectedStatus = null;
    this.nm = null;
    this.selectedEffectiveDate = {
      start: moment().subtract(1, 'month'),
      end: moment(),
    };
    this.selectedRequestedDate = {
      start: moment().subtract(1, 'month'),
      end: moment(),
    };


  }


  async viewSelected(item: any) {

    /*  const wf_id = item.wf_id;
      const status = item.status;
      const action = item.action;
      const fee_detail_id = item.fee_detail_id;
      const task_id = item.task_id;
      const assign_to = item.assign_to;
  
      
      if (item.action === "Request Add" && item.status === "Q-R" && this.username === item.assign_to) {
        this.router.navigate(['/mft-req-form-add'], { state: { wf_id, status, action, fee_detail_id, task_id, assign_to } });
      }
      else if (item.action === "Request Add-FIN" && item.status === "Q-FA" && this.username === item.assign_to) {
        this.router.navigate(['/mft-fa-fa-query-add'], { state: { wf_id, status, action, fee_detail_id, task_id, assign_to } });
      }
   
      else {
        this.router.navigate(['/created-task-details'], { state: { wf_id, status, action, fee_detail_id, task_id, assign_to } });
      }*/


    const wf_id = item.wf_id;
    const task_id = item.task_id;
    const status__From_Assigned = item.status;
    const fee_detail_pk = item.fee_detail_pk
    let edit_Mode: boolean = false;
    let show_requester_table: boolean = false;

    console.log("AssingTo is : " + item.assign_to);

    const validResponse = await this.checkCurrentMFTWFStatus(wf_id);
    if (!validResponse) {
      return;
    }

    console.log("currentMFTWFStatus" + this.currentMFTWFStatus);
    if(status__From_Assigned !== this.currentMFTWFStatus){
      const showTaskNotUpdateAlert = true;
      this.router.navigate(['/master-fee-table'], { state: { showTaskNotUpdateAlert } });
      return;
    }

    //start from requester
    if (item.action === "Request Add" && item.status === "P-RHOD" && item.assign_to === this.username) { //pending Requester HOD approval
      this.router.navigate(['/mft-reqhod-appr-add'], { state: { wf_id, task_id, status__From_Assigned } });
    }
    else if (item.action === "Request Add" && item.status === "P-FA" && item.assign_to === this.username) {  //pending Finance Admin approval
      this.router.navigate(['/mft-fa-appr-add'], { state: { wf_id, task_id, status__From_Assigned } });
    }
    else if (item.action === "Request Add" && item.status === "P-FHOD" && item.assign_to === this.username) { //pending Finance HOD approval
      edit_Mode = false;
      this.router.navigate(['/mft-fhod-appr-add'], { state: { wf_id, task_id, edit_Mode, status__From_Assigned } });
    }
    else if (item.action === "Request Add" && item.status === "Q-R" && item.assign_to === this.username) {  //query to Requester 
      this.router.navigate(['/mft-req-form-add'], { state: { wf_id, task_id, status__From_Assigned } });
    }
    else if (item.action === "Request Add" && item.status === "Q-RHOD" && item.assign_to === this.username) {  // query to Requester HOD 
      this.router.navigate(['/mft-reqhod-appr-add'], { state: { wf_id, task_id, status__From_Assigned } });
    }
    else if (item.action === "Request Add" && item.status === "Q-FA" && item.assign_to === this.username) { //query to Finance Admin
      this.router.navigate(['/mft-fa-appr-add'], { state: { wf_id, task_id, status__From_Assigned } });
    }
    else if (item.action === "Request Edit" && item.status === "P-RHOD" && item.assign_to === this.username) { //pending Requester HOD approval
      this.router.navigate(['/mft-reqhod-appr-edit'], { state: { wf_id, task_id, status__From_Assigned } });
    }
    else if (item.action === "Request Edit" && item.status === "P-FA" && item.assign_to === this.username) {  //pending Finance Admin approval
      this.router.navigate(['/mft-fa-appr-edit'], { state: { wf_id, task_id, status__From_Assigned, fee_detail_pk } });
    }
    else if (item.action === "Request Edit" && item.status === "P-FHOD" && item.assign_to === this.username) { //pending Finance HOD approval
      edit_Mode = true;
      this.router.navigate(['/mft-fhod-appr-add'], { state: { wf_id, task_id, edit_Mode, fee_detail_pk, status__From_Assigned } });
    }
    else if (item.action === "Request Edit" && item.status === "Q-R" && item.assign_to === this.username) {  //query to Requester 
      this.router.navigate(['/mft-req-form-edit'], { state: { wf_id, task_id, status__From_Assigned } });
    }
    else if (item.action === "Request Edit" && item.status === "Q-RHOD" && item.assign_to === this.username) {  // query to Requester HOD 
      this.router.navigate(['/mft-reqhod-appr-edit'], { state: { wf_id, task_id, status__From_Assigned } });
    }
    else if (item.action === "Request Edit" && item.status === "Q-FA" && item.assign_to === this.username) { //query to Finance Admin
      this.router.navigate(['/mft-fa-appr-edit'], { state: { wf_id, task_id, status__From_Assigned } });
    }
    //start from finance admin
    else if (item.action === "Request Add-FIN" && item.status === "P-FHOD" && item.assign_to === this.username) {  //pending Finance HOD approval
      edit_Mode = false;
      this.router.navigate(['/mft-fa-fhod-appr-add'], { state: { wf_id, task_id, edit_Mode, status__From_Assigned } });
    }
    else if (item.action === "Request Add-FIN" && item.status === "Q-FA" && item.assign_to === this.username) {  //query to Finance Admin
      this.router.navigate(['/mft-fa-fa-rqt-add'], { state: { wf_id, task_id, status__From_Assigned } });
    }
    else if (item.action === "Request Edit-FIN" && item.status === "P-FHOD" && item.assign_to === this.username) {  //pending Finance HOD approval
      edit_Mode = true;
      this.router.navigate(['/mft-fa-fhod-appr-add'], { state: { wf_id, task_id, edit_Mode, fee_detail_pk, status__From_Assigned } });
    }
    else if (item.action === "Request Edit-FIN" && item.status === "Q-FA" && item.assign_to === this.username) {  //query to Finance Admin
      this.router.navigate(['/mft-fa-fa-rqt-edit'], { state: { wf_id, task_id, fee_detail_pk, status__From_Assigned } });
    }
    else {
      //since end of flow like C,EFT,R-PHOD is assign_to null there will come to this page
      const from_route = 2;
     // const fee_detail_id = this.feeDetailId;

      if (item.action === "Request Add" || item.action === "Request Add-FIN") {
        edit_Mode = false;
      }
      else {
        edit_Mode = true;
      }

      if (item.action === "Request Add-FIN" || item.action === "Request Edit-FIN") {
        show_requester_table = false;
      }
      else {
        show_requester_table = true;
      }

      this.router.navigate(['/mft-item-task-list'], { state: { wf_id, status__From_Assigned, task_id, fee_detail_pk, edit_Mode, show_requester_table, from_route} });
      //this.router.navigate(['/created-task-details'], { state: { wf_id, status__From_Assigned, task_id, fee_detail_pk, edit_Mode, show_requester_table } });
    }

  }

  async populateForm(): Promise<void> {

    const url = environment.apiUrl + '/api/mft/v1/getmasterfeetable';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    const Body: any = {
      i_page: this.page.toString(),
      i_size: 1,
      i_fee_detail_pk: this.feeDetailPk,
    };

    try {
      const response: any = await this.http.post(url, Body, { headers }).toPromise();
      // console.log("Ast is "+ response.header.statusCode)
      if (response.header.statusCode === '00') {
        this.totalMFTRecords = response.data[0].total;
        this.mfts = response.data;
        this.feeDetailId = this.mfts[0].fee_detail_id;
        this.feeGrpNmEn = this.mfts[0].fee_grp_nm_en;
        this.feeDetailNmE = this.mfts[0].fee_detail_nm_e;
        this.feeDetailNmB = this.mfts[0].fee_detail_nm_b;
        this.unitFee = this.mfts[0].unit_fee;
        this.promoStartDate = this.mfts[0].promo_startdt;
        this.promoEndDate = this.mfts[0].promo_enddt;
        this.promoFee = this.mfts[0].promo_fee;
        this.taxCd = this.mfts[0].tax_cd;
        this.allowOTC = this.mfts[0].allow_otc;
        this.llParentId = this.mfts[0].ll_parent_id;
        this.llStartDay = this.mfts[0].ll_start_day;
        this.llStartMth = this.mfts[0].ll_start_mth;
        this.llEndDay = this.mfts[0].ll_end_day;
        this.llEndMth = this.mfts[0].ll_end_mth;
        this.ledgerCd = this.mfts[0].ledger_cd;
        this.ssCd = this.mfts[0].ss_cd;
        this.dtCreated = this.mfts[0].dt_created;
        this.createdBy = this.mfts[0].created_by;
        this.createdByNm = this.mfts[0].created_by_nm;
        this.dtModified = this.mfts[0].dt_modified;
        this.modifiedBy = this.mfts[0].modified_by;
        this.modifiedByNm = this.mfts[0].modified_by_nm;
        this.status = this.mfts[0].status;
        this.isPublic = this.mfts[0].isPub;

        if (this.ssCd !== null) {
         // this.checkboxOptions = this.ssCd.split(',');
         this.ssCdWithSpace = this.ssCd.split(',').join(', '); //for display
        }

        // this.checkboxOptions = this.ssCd?.split(',');
        //this.convertSsCd();
        this.isLoading = false;

      } else {
        this.totalMFTRecords = 0;
        this.isLoading = false;
        console.error('Invalid master fee table work flow history response format:', response);
      }
    } catch (error) {
      this.isLoading = false;
      console.error('There was an error retrieving the master fee table work flow history:', error);
    }

    /*this.http.post(url, Body, { headers }).subscribe(
      (response: any) => {
        if (response.data.length === 0) {
          this.totalMFTRecords = 0;
          this.isLoading = false;
        } else {
          this.totalMFTRecords = response.data[0].total;
          this.mfts = response.data;
          this.feeGrpNmEn = this.mfts[0].fee_grp_nm_en;
          this.feeDetailNmE = this.mfts[0].fee_detail_nm_e;
          this.feeDetailNmB = this.mfts[0].fee_detail_nm_b;
          this.unitFee = this.mfts[0].unit_fee;
          this.promoStartDate = this.mfts[0].promo_startdt;
          this.promoEndDate = this.mfts[0].promo_enddt;
          this.promoFee = this.mfts[0].promo_fee;
          this.taxCd = this.mfts[0].tax_cd;
          this.allowOTC = this.mfts[0].allow_otc;
          this.llParentId = this.mfts[0].ll_parent_id;
          this.llStartDay = this.mfts[0].ll_start_day;
          this.llStartMth = this.mfts[0].ll_start_mth;
          this.llEndDay = this.mfts[0].ll_end_day;
          this.llEndMth = this.mfts[0].ll_end_mth;
          this.ledgerCd = this.mfts[0].ledger_cd;
          this.ssCd = this.mfts[0].ss_cd;
          this.dtCreated = this.mfts[0].dt_created;
          this.createdBy = this.mfts[0].created_by;
          this.createdByNm = this.mfts[0].created_by_nm;
          this.dtModified = this.mfts[0].dt_modified;
          this.modifiedBy = this.mfts[0].modified_by;
          this.modifiedByNm = this.mfts[0].modified_by_nm;
          this.status = this.mfts[0].status;

          if(this.ssCd !== null){
            this.checkboxOptions = this.ssCd.split(',');
          }

         // this.checkboxOptions = this.ssCd?.split(',');
          //this.convertSsCd();
          this.isLoading = false;

        }
      },
      (error) => {
        console.error(error);
        this.isLoading = false;
        // Handle errors here
      }
    );*/
  }


  populateStatus() {
    const url = environment.apiUrl + '/api/rms/v1/getparam';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json'
    });


    // Create the request body with your form data
    const requestBody = {
      i_page: environment.DefaultPage,
      i_size: environment.DropDownSize, //dont use item per page here because it is for table
      i_param_cd: '',
      i_param_grp_nm: 'Status-MFT'
    };

    // Send an HTTP POST request to the API
    this.http.post(url, requestBody, { headers }).subscribe(
      (response: any) => {
        console.error('Invalid status response format:', response);
        // this.statusOptions=this.statusOptions.concat(response.data)
        this.statusOptions = response.data;
        // Handle a successful response (e.g., show a success message)
      },
      (error) => {
        console.error('There was an error retrieving the status:', error);
        // Handle API errors (e.g., show an error message)
      }
    );
  }

  populateRequester() {

    const url = environment.apiUrl + '/api/mft/v1/getuserbyrole';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json'
    });

    // Create the request body with your form data
    const requestBody: any = {
      i_page: '1',
      i_size: environment.DropDownSize,
      i_role_nm_en: 'REQUESTER',
      i_role_nm_bm: null,
      i_status: Systemstatus.Active
    };

    this.http.post(url, requestBody, { headers }).subscribe(
      (response: any) => {
        if (response.data.length === 0) {
          console.error('Invalid approver response format:', response);
        }
        else {
          this.users = response.data;
        }

        //  console.log('Rolees is : '+this.roles )
      },
      (error) => {
        console.error('There was an error retrieving the approver:', error);
        // Handle errors here
      }
    );
  }

  convertSsCd() {
    this.checkboxOptions = this.ssCd?.split(',');
    console.log("Converted checkbox is : " + this.checkboxOptions);
  }

  editSelected() {
    const fee_detail_pk = this.feeDetailPk;

    this.router.navigate(['/mft-fa-fa-rqt-edit'], { state: { fee_detail_pk } });
  }

  checkPermission() {
    this.authService.checkUserRole(this.authService.username, this.permMFTDetails)
      .subscribe(
        (response: any) => {
          this.permMFTDetailsAllow = response.data;
          this.permListAllow = this.permMFTDetailsAllow.includes(perm.Master_Fee_Table_View_MFT_Details) ? 1 : 0;
          this.permEditMFTAllow = this.permMFTDetailsAllow.includes(perm.Master_Fee_Table_Edit_MFT) ? 1 : 0;
          if (this.permListAllow === 0) {
            this.router.navigate(['/access-denied']);
            return; // Exit the function to prevent further execution
          }
        }
      );
  }

  async checkCurrentMFTWFStatus(wfid:number): Promise<boolean> {

    const urlMftWF = environment.apiUrl + '/api/mftwf/v1/getmasterfeetableworkflow';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json'
    });


    // Create the request body with your form data
    const Body: any = {

      i_page: this.page.toString(),
      i_size: "1",
      i_wf_id: wfid,
      i_wf_is_in_prg: "f"
    };

    try {
      const response: any = await this.http.post(urlMftWF, Body, { headers }).toPromise();
      // console.log("Ast is "+ response.header.statusCode)
      if (response.header.statusCode === '00') {
        // this.totalRecords = response.data[0].total;
        this.forCheckingMFTWFS = response.data;
        this.currentMFTWFStatus = this.forCheckingMFTWFS[0].status;
        return true;
      } else {
        // this.totalRecords = 0;
        console.error('Invalid master fee table work flow history response format:', response);
        return false;
      }
    } catch (error) {
      console.error('There was an error retrieving the master fee table work flow history:', error);
      return false;
    }
  }

}
