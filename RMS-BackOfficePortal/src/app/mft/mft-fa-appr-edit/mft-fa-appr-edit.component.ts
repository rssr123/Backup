import { Component, OnInit, ViewChild, ElementRef, ChangeDetectorRef, ViewEncapsulation, AfterViewInit } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { environment } from 'src/environments/environment';
import { FeeGroup } from '../../core/models/fee-group';
import { DatePipe, DecimalPipe } from '@angular/common';
import { MFT, MFTWF, MFTWFDoc, MFTWFHist, Param, SourceSystemCode, TaxCode, User } from '../../core/models/entity';
import { FormBuilder, FormControl, FormGroup, NgForm, NgModel, Validators } from '@angular/forms';
import { Systemstatus } from '../../shared/enums/systemstatus';
import { ActivatedRoute, Router } from '@angular/router';
import { fadeInOut } from '../../shared/animation';
import { TranslateService } from '@ngx-translate/core';
import { GlobalService } from 'src/app/shared/global.service';
import { AuthService } from 'src/app/core/services/auth.service';
import { perm } from 'src/permissions/perm';
import { debounceTime, distinctUntilChanged, startWith, Subject, switchMap, tap } from 'rxjs';

@Component({
  selector: 'app-mft-fa-appr-edit',
  templateUrl: './mft-fa-appr-edit.component.html',
  styleUrls: ['./mft-fa-appr-edit.component.scss'],
  animations: [fadeInOut]
})
export class MftFaApprEditComponent implements OnInit {

  username = this.authService.username;
  userHigherOfficialRole = "FINANCEHOD";

  feeGroups: FeeGroup[] = [];
  initialNumber = [
    { value: 0, label: 'No' },
    { value: 1, label: 'Yes' },
  ]
  errorMessages: string[] = [];
  error: boolean = false;
  isLoading: boolean = false;
  page = environment.DefaultPage;
  dropDownSize = environment.DropDownSize;
  totalRecords: number = 0;
  totalMFTRecords: number = 0;
  status: Param[] = [];
  sourceSystemCodes: SourceSystemCode[] = [];
  selectedSourceSystemCodes: any[] = [];
  decisionOption: any[] = [];
  queryFromStatus: string | null = null;
  queryBackTo: string | null = null;
  statusFromAssigned: string | null = null;
  notReject: boolean = true;
  actionType: string | null = null;
  //inputDetails: boolean = false;
  decisionDetails: boolean = false;
  approverDetails: boolean = false;
  disableApprover: boolean = true;
  decision: string | null = null;
  wfId: number | null = null;
  taskId: string | null = null;
  assignTo: string | null = null;
  alertMessage: string | undefined = undefined;
  users: User[] = [];
  taxCode: TaxCode[] = [];
  file_content = "";
  currentMFTWFDetail: MFTWF[] = [];
  currentMFTWFStatus: string | null = null;
  currentMFTWFAssignTo: string | null = null;
  populateFromRFields: boolean = false;
  errorMessagesTaskNotUpdate: string[] = [];
  errorTaskNotUpdate: boolean = false;

  //to insert into master fee table workflow
  feeDetailPk: string | number | null = null;
  feeDetailId: string | null = null;
  feeGroupId: string | number | null = null;
  feeDetailNmE: string | null = null;
  feeDetailNmB: string | null = null;
  feeAmt: string | null = null;
  promoStartDate: string | null = null;
  promoEndDate: string | null = null;
  promoFee: string | null = null;
  taxCdId: string | number | null = null;
  allowOTC: string | number | null = null;
  llParentId: string | null = null;
  llStartDay: string | null = null;
  llStartMth: string | null = null;
  llEndDay: string | null = null;
  llEndMth: string | null = null;
  ledgerCd: string | null = null;
  dtCreated: Date | null = null;
  createdBy: string | null = null;
  createdByNm: string | null = null;
  effectiveDate: Date | null = null;
  textRemarks: string | null = null;
  ssm4uuserrefno: string | null = null;
  mftStatus: string | null = null;
  isPublic: number| null = null;
  placeHolderisPublic: number| null = null;

  mftwfHis: MFTWFHist[] = [];
  mftwfs: MFTWF[] = [];
  mfts: MFT[] = [];
  emailMftwfs: MFTWF[] = [];
  mftsForLlPID: MFT[] = [];
  mftwfSupDocs: MFTWFDoc[] = [];
  checkboxRssCd: string[] | undefined = undefined;
  rSsCdWithSpace: string | null = null;
  checkboxSsCd: string[] | undefined = undefined;
  ssCdWithSpace: string | null = null;
  mftStatusOptions: any[] = [
    { value: 'A', label: 'Active' },
    { value: 'D', label: 'Inactive' },
  ];

  //email
  emailWfId: string | number | null = null;
  emailAssignTo: string | null = null;
  emailStatus: string | null = null;
  emailFeeDetailPk: string | number | null = null;
  emailfeeDetailId: string | null = null;
  emailrfeeDetNm: string | null = null;
  emailTaskId: string | null = null;
  emailAction: string | null = null;

  isDisplaySupDoc: boolean = false;
  isLoadingSupDoc: boolean = false;
  isDisplayHist: boolean = false;
  isLoadingHist: boolean = false;

  //promotion start date and end date validation
  currentDate: Date = new Date();
  promoStartDateAsDate: Date | null = null;
  promoEndDateAsDate: Date | null = null;
  effectiveDateAsDate: Date | null = null;
  passDateValidation: boolean = false;
  promotionStartDateNotSelected: boolean = true;
  promotionEndDateNotSelected: boolean = true;
  promotionStartDateInvalid: boolean = true;
  promotionEndDateInvalid: boolean = true;
  promotionEndDateLessThanStartDate: boolean = true;
  minPromoStartDate: Date = new Date();
  minPromoEndDate: Date = new Date();

  //to display all the read only field
  requester: string | null = null;
  requesterName: string | null = null;
  requesterHOD: string | null = null;
  rFeeDetNm: string | null = null;
  rFeeAmt: number | null = null;
  rSsCd: string | null = null;
  rPromoStartDt: Date | null = null;
  rPromoEndDt: Date | null = null;
  rPromoFee: number | null = null;
  rLlRequired: number = 0;
  rAddNotes: string | null = null;
  displayFeeDetailId: string | null = null;
  displayFeeGroupNmEn: string | null = null;
  displayFeeDetailNmE: string | null = null;
  displayFeeDetailNmB: string | null = null;
  displayFeeAmt: number | null = null;
  displayPromoStartDate: Date | null = null;
  displayPromoEndDate: Date | null = null;
  displayPromoFee: number | null = null;
  displayTaxCd: string | null = null;
  displayAllowOTC: number | null = null;
  displayLlParentId: string | null = null;
  displayLlStartDay: number | null = null;
  displayLlStartMth: number | null = null;
  displayLlEndDay: number | null = null;
  displayLlEndMth: number | null = null;
  displayLedgerCd: string | null = null;
  modifiedBy: string | null = null;
  modifiedByNm: string | null = null;
  displayCreatedBy: string | null = null
  displayCreatedByNm: string | null = null
  displayModifiedBy: string | null = null
  displayModifiedByNm: string | null = null
  displayDtCreated: Date | null = null;
  displayDtModified: Date | null = null;
  displaySsCd: string | null = null;
  displayMFTStatus: string | null = null;
  displayStatusEn: string | null = null

  //display for first column mft value
  currentFeeDetailId: string | null = null;
  currentFeeGroupNmEn: string | null = null;
  currentFeeDetailNmEn: string | null = null;
  currentFeeDetailNmBm: string | null = null;
  currentUnitFee: number | null = null;
  currentPromoStartDate: Date | null = null;
  currentPromoEndDate: Date | null = null;
  currentPromoFee: number | null = null;
  currentTaxCd: string | null = null;
  currentAllowOTC: number | null = null;
  currentLlParentId: string | null = null;
  currentLlStartDay: number | null = null;
  currentLlStartMth: number | null = null;
  currentLlEndDay: number | null = null;
  currentLlEndMth: number | null = null;
  currentLedgerCd: string | null = null;
  currentEffectiveDate: Date | null = null;
  currentCreatedBy: string | null = null;
  currentCreatedByNm: string | null = null;
  currentModifiedBy: string | null = null;
  currentModifiedByNm: string | null = null;
  currentDtCreated: Date | null = null;
  currentDtModified: Date | null = null;
  currentCheckboxSsCd: string[] | undefined = undefined;
  currentSsCdWithSpace: string | null = null;
  currentSsCd: string | null = null;
  currentStatus: string | null = null;
  currentIsPublic: number | null = null;

  //Supporting Document Table setting
  pageSupDoc = environment.DefaultPage;
  itemsPerPageSupDoc = environment.ItemPerPage;
  totalRecordsSupDoc: number = 0;

  //History Table setting
  pageHist = environment.DefaultPage;
  itemsPerPageHist = environment.ItemPerPage;
  totalRecordsHist: number = 0;

  //default pagination for history
  selectedValueHist = environment.dropdownOptions[0];
  dropdownOptionsHist = environment.dropdownOptions;

  //for parentid 
  llParentSearch$ = new Subject<string>();
  loadingLlParent = false;
  llParentPage = 1;
  llParentPageSize = 20;
  currentLlParentTerm = '';
  dropDownSize100 = 100;


  LoadDataHist(singleItem: number) {
    this.selectedValueHist = singleItem;
    this.itemsPerPageHist = this.selectedValueHist;
    this.loadDataHist();
  }

  //default pagination for supporting document
  selectedValueSupDoc = environment.dropdownOptions[0];
  dropdownOptionsSupDoc = environment.dropdownOptions;

  LoadDataSupDoc(singleItem: number) {
    this.selectedValueSupDoc = singleItem;
    this.itemsPerPageSupDoc = this.selectedValueSupDoc;
    this.loadDataSupDoc();
  }

  // Configuring Permissions for User and roles variables
  permRequestEditMFTwithRequesterFormFA = perm.Master_Fee_Table_Request_Edit_MFT_with_Requester_Form_FA; // all the perm_cd for this module seperated with comma
  permRequestEditMFTwithRequesterFormFAAllow = ""; // variable to store allowed permission for the user
  permListAllow: number = 0; // if 0 then not allow to view listing page, else allow
  // end configuration

  constructor(
    private http: HttpClient,
    private datePipe: DatePipe,
    private router: Router,
    private cdref: ChangeDetectorRef,
    private route: ActivatedRoute,
    private translate: TranslateService,
    private globalService: GlobalService,
    private authService: AuthService) {
    this.translate.setDefaultLang(this.globalService.getGlobalValue()); // default language
    this.translate.use(this.globalService.getGlobalValue());
  }

  async ngOnInit() {

    const queryParams = this.route.snapshot.queryParamMap;

    const tempWf_id = queryParams.get('wf_id');
    if (tempWf_id !== null && tempWf_id !== 'null') {
      this.wfId = +tempWf_id; //+ mean convert string to number
    }
    else {
      this.wfId = history.state.wf_id;
    }

    const tempTask_id = queryParams.get('task_id');
    if (tempTask_id !== null && tempTask_id !== 'null') {
      this.taskId = tempTask_id;
    }
    else {
      this.taskId = history.state.task_id;
    }

    const tempStatus__From_Assigned = queryParams.get('status__From_Assigned');
    if (tempStatus__From_Assigned !== null && tempStatus__From_Assigned !== 'null') {
      this.statusFromAssigned = tempStatus__From_Assigned;
      //  console.log("status__From_Assigned from queryparam" + this.statusFromAssigned)
    }
    else {
      this.statusFromAssigned = history.state.status__From_Assigned;
      //console.log("status__From_Assigned from state" + this.statusFromAssigned)
    }

    const tempFee_detail_pk = queryParams.get('fee_detail_pk');
    if (tempFee_detail_pk !== null && tempFee_detail_pk !== 'null') {
      this.feeDetailPk = +tempFee_detail_pk;
    }
    else {
      this.feeDetailPk = history.state.fee_detail_pk;
      //console.log("Fee detail pk is "+this.feeDetailPk)
    }

    // this.wfId = history.state.wf_id;
    ///this.taskId = history.state.task_id;
    //this.statusFromAssigned = history.state.status__From_Assigned;
    //this.feeDetailPk = history.state.fee_detail_pk;

    if (this.wfId !== undefined) {


      this.decisionOption = [
        { label: 'Query to Requester', value: 'Q-R' },//Query Pending Requester
        { label: 'Query to Requester HOD', value: 'Q-RHOD' },//Query Pending Requester HOD
        { label: 'Approve', value: 'P-FHOD' }, //Pending Finance HOD Approval
        { label: 'Reject', value: 'RJ-FA' } //Rejected by Finance Admin
      ]

      this.minPromoStartDate.setDate(this.minPromoStartDate.getDate() + 1);
      this.minPromoEndDate.setDate(this.minPromoEndDate.getDate() + 2);

      this.isLoading = true;
      this.checkPermission();
      await this.checkStatusAndAssignTo(false);
      this.loadDataSupDoc();
      this.loadDataHist();
      await this.populateFeeGroupId(); //check permission here
      await this.populateSourceSystemCode(); //need to use wait for feefroupid, sscode, lodgementid and taxcode first because when load the form, if the taxcode, sscd etc no longer active after compare 
      await this.populateTaxCode();   //with the existing taxcode etc in the form and  will auto remove from user current form(eg when query back and suddenly the previus selectedtaxcode is inactive)
      //await this.populateLateLodgementID(); //load fee detail id from master fee table
        this.llParentSearch$  //for parentid
        .pipe(
          startWith(''), 
          debounceTime(300),
          distinctUntilChanged(),
          tap(term => {
            this.currentLlParentTerm = term ?? '';
            this.llParentPage = 1;
            this.mftsForLlPID = [];
            this.loadingLlParent = true;
          }),
          switchMap(term => this.loadLlParent(term))
            )
            .subscribe({
              next: data => {
                this.mftsForLlPID = data;
                this.loadingLlParent = false;
              },
              error: () => {
                this.loadingLlParent = false;
              }
            });
            
      await this.populateCurrentValueForm();


      if (this.statusFromAssigned === "Q-FA") { //query from finance hod
        // this.inputDetails = true;
        this.populateFromRFields = false; //false because it populate from workflow not r fields anymore
        this.decisionDetails = false;
        this.actionType = "Add-Reply";
        const tempQuery = await this.getworkflowhistory_status("Q-FA");
        this.queryBackTo = tempQuery.assign_to;
        this.queryFromStatus = tempQuery.status;
      }
      else { //this.statusFromAssigned === "P-FA"

        const tempQuery = await this.getworkflowhistory_status("P-FA");
        this.queryBackTo = tempQuery.assign_to;
        this.queryFromStatus = tempQuery.status;

        if (this.queryFromStatus === "P-RHOD") { //means task is approved from requester HOD
          this.populateFromRFields = true;
          // this.inputDetails = true;
          this.decisionDetails = true;
          this.actionType = "Add";
          /// this.populateFeeGroupId();
          // this.populateSourceSystemCode();
          //  this.populateTaxCode();
          //  this.populateLateLodgementID(); //load fee detail id from master fee table
          this.populateAppover();
        }
        else { //statusFromRequesterHOD === "Q-RHOD" //means task is query back to requester HOD and requester HOD has reply the query
          // this.inputDetails = false;
          this.populateFromRFields = false; //false because it populate from workflow not r fields anymore
          this.decisionDetails = true;
          this.actionType = "Add";
          this.populateAppover();
        }
        //  console.log("Query to is "+this.queryBackTo);
        //  console.log("Query from status is "+this.queryFromStatus);
      }

      await this.populateForm();//need in insert because r fields and for load value createdby, must put after input details and decision details is assigned
      const tempRequester = await this.getworkflowhistory_ast('Q-R')
      if (tempRequester.assign_to !== null && tempRequester.assign_to_nm !== null) { //means requester has been reassigned by super admin
        this.requester = tempRequester.assign_to;
        this.requesterName = tempRequester.assign_to_nm;
      }
      else { //means requester has not been reassigned by super admin
        this.requester = this.createdBy;
        this.requesterName = this.createdByNm;
      }

      const tempRequesterHOD = await this.getworkflowhistory_ast('P-RHOD');
      this.requesterHOD = tempRequesterHOD.assign_to;

      this.isLoading = false;
      //  console.log("Requestr to is "+this.requester)
      //  console.log("Requester HOD from status is "+this.requesterHOD)
    }
  }

  //update master fee table workflow start
  async updateMFTWF() {
    this.isLoading = true;
    this.defaultSetting();
    this.getAssignTo();

    // let isValid: boolean = false;

    //false means no error, validation passed, can update
    // if (!isValid) { //if decision=reject, no need to validate
    const invalidUpdate = await this.updateMasterFeeTableWorkFlow()
    if (invalidUpdate === false) {
      this.sendEmail();
      this.isLoading = false;
      const alert_msg = this.alertMessage;
      this.router.navigate(['/my-task-assigned-tasks'], { state: { alert_msg } });
    }
    // }
    this.isLoading = false;
  }

  async updateMasterFeeTableWorkFlow(): Promise<boolean> {

    const updmftwfUrl = environment.apiUrl + '/api/mftwf/v1/updatemasterfeetableworkflow';

    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json'
    });

    //change date format from dd MMM yyyy to yyyy-MM-dd
    let formattedPromoStartDate = this.datePipe.transform(this.promoStartDate, 'yyyy-MM-dd') || "";
    let formattedPromoEndDate = this.datePipe.transform(this.promoEndDate, 'yyyy-MM-dd') || "";
    let formattedRPromoStartDt = this.datePipe.transform(this.rPromoStartDt, 'yyyy-MM-dd') || "";
    let formattedRPromoEndDt = this.datePipe.transform(this.rPromoEndDt, 'yyyy-MM-dd') || "";
    let formattedEffectiveDate = this.datePipe.transform(this.effectiveDate, 'yyyy-MM-dd') || "";

    let tempPromoStartDate: string | null = null
    let tempPromoEndDate: string | null = null
    /* if (this.inputDetails === true && this.decisionDetails === false) { //query from finance hod
   
       tempPromoStartDate = formattedPromoStartDate;
       tempPromoEndDate = formattedPromoEndDate;
     }
     else {
       tempPromoStartDate = this.promoStartDate;
       tempPromoEndDate = this.promoEndDate;
   
     }*/


    let resultString = this.selectedSourceSystemCodes.join(',');

    //console.log(resultString);
    let Body: any;
    if (this.decision !== 'RJ-FA') {

      Body = {
        i_wf_id: this.wfId,
        i_fee_detail_pk: this.feeDetailPk,
        i_fee_detail_id: this.currentFeeDetailId, //because dont have input for new fee detail id
      };

      if (this.feeGroupId) {
        Body.i_fee_grp_id = this.feeGroupId;
      }

      if (this.feeDetailNmE && this.feeDetailNmE.trim()) {
        Body.i_fee_detail_nm_e = this.feeDetailNmE;
      }

      if (this.feeDetailNmB && this.feeDetailNmB.trim()) {
        Body.i_fee_detail_nm_b = this.feeDetailNmB;
      }

      if (this.feeAmt && this.feeAmt.trim()) {
        Body.i_fee_amt = this.feeAmt;
      }

      if (this.promoStartDate) {
        Body.i_promo_startdt = formattedPromoStartDate;
      }

      if (this.promoEndDate) {
        Body.i_promo_enddt = formattedPromoEndDate;
      }


      if (this.promoFee && this.promoFee.trim()) {
        Body.i_promo_fee = this.promoFee;
      }

      if (this.taxCdId) {
        Body.i_tax_cd_id = this.taxCdId;
      }

      //  if (this.allowOTC) {
      Body.i_allow_otc = this.allowOTC;
      //  }

      // Body.i_tax_cd_id = this.taxCdId;
      // Body.i_allow_otc = this.allowOTC;

      if (this.llParentId && this.llParentId.trim()) {
        Body.i_ll_parent_id = this.llParentId;
      }

      if (this.llStartDay && this.llStartDay.trim()) {
        Body.i_ll_start_day = this.llStartDay;
      }

      if (this.llStartMth && this.llStartMth.trim()) {
        Body.i_ll_start_mth = this.llStartMth;
      }

      if (this.llEndDay && this.llEndDay.trim()) {
        Body.i_ll_end_day = this.llEndDay;
      }

      if (this.llEndMth && this.llEndMth.trim()) {
        Body.i_ll_end_mth = this.llEndMth;
      }

      // Body.i_ledger_cd = this.ledgerCd;
      // Body.i_ss_cd = resultString;
      if (this.ledgerCd && this.ledgerCd.trim()) {
        Body.i_ledger_cd = this.ledgerCd;
      }

      if (resultString && resultString.trim()) {
        Body.i_ss_cd = resultString;
      }

      if (formattedEffectiveDate) {
        Body.i_effective_date = formattedEffectiveDate;
      }

      Body.i_modified_by = this.username;
      Body.i_status = this.decision;
      Body.i_assign_to = this.assignTo;
      Body.i_remark = this.textRemarks;
      Body.i_action = "Request Edit";
      Body.i_r_fee_det_nm = this.rFeeDetNm;
      Body.i_r_fee_amt = this.rFeeAmt;
      Body.i_r_ss_cd = this.rSsCd;
      Body.i_r_promo_startdt = formattedRPromoStartDt;
      Body.i_r_promo_enddt = formattedRPromoEndDt;
      Body.i_r_promo_fee = this.rPromoFee;
      Body.i_r_ll_required = this.rLlRequired;
      Body.i_ispub = this.isPublic;
      Body.i_r_add_notes = this.rAddNotes;

      if (this.mftStatus && this.mftStatus.trim()) {
        Body.i_mft_status = this.mftStatus;
      }
    }
    else {

      Body = {
        i_wf_id: this.wfId,
        i_fee_detail_pk: this.feeDetailPk,
        i_fee_detail_id: null,
        i_fee_grp_id: null,
        i_fee_detail_nm_e: null,
        i_fee_detail_nm_b: null,
        i_fee_amt: null,
        i_promo_startdt: null,
        i_promo_enddt: null,
        i_promo_fee: null,
        i_tax_cd_id: null,
        i_allow_otc: null,
        i_ll_parent_id: null,
        i_ll_start_day: null,
        i_ll_start_mth: null,
        i_ll_end_day: null,
        i_ll_end_mth: null,
        i_ledger_cd: null,
        i_ss_cd: null,
        i_effective_date: formattedEffectiveDate,
        i_modified_by: this.username,
        i_status: this.decision,
        i_assign_to: this.assignTo,
        i_remark: this.textRemarks,
        i_action: "Request Edit",
        i_r_fee_det_nm: this.rFeeDetNm,
        i_r_fee_amt: this.rFeeAmt,
        i_r_ss_cd: this.rSsCd,
        i_r_promo_startdt: formattedRPromoStartDt,
        i_r_promo_enddt: formattedRPromoEndDt,
        i_r_ll_required: this.rLlRequired,
        i_ispub: this.isPublic,
        i_r_add_notes: this.rAddNotes,
        i_mft_status: null
      };
    }

    try {
      const response: any = await this.http.post(updmftwfUrl, Body, { headers }).toPromise();
      if (response.header.statusCode === '00') {
        return false; // Update success
      } else {
        this.error = true;
        this.errorMessages.push('Submit not successful');
        return true; // update failed
      }
    } catch (error) {
      this.error = true;
      this.errorMessages.push('Internal Server Error.');
      console.error(error);
      return true; // Error occurred
    }
  }

  //edit no need check wf since allow multiple user to submit at the same time
  // async validation(): Promise<boolean> {

  //   let invalidMTF = true;
  //   let invalidMTFWF = true;

  //   if (!this.feeDetailId) {
  //     // Form is not valid, you can handle this case or simply return
  //     return true;
  //   }


  //   invalidMTF = await this.checkDuplicateMFT();
  //   invalidMTFWF = await this.checkDuplicateMFTWF();

  //   if (invalidMTF === false && invalidMTFWF === false) {
  //     return false;
  //   }
  //   else {
  //     return true;
  //   }

  // }

  async checkDuplicateMFT() {

    const url = environment.apiUrl + '/api/mft/v1/checkmasterfeetableexist';

    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    const body: any = {
      i_page: '1',
      i_size: '1',
      i_fee_detail_id: this.feeDetailId,
      i_status: Systemstatus.Active
    };

    try {
      const response: any = await this.http
        .post(url, body, { headers })
        .toPromise();
      console.log('bng' + response.header.statusCode)
      if (response.header.statusCode === '01') {
        return false;
      } else {
        this.error = true;
        this.errorMessages.push('Fee Detail ID is duplicate in Master Fee Table.');
        return true;
      }
    } catch (error) {
      this.error = true;
      this.errorMessages.push('Internal Server Error.');
      console.error(error);
      return true;
    }

  }

  async checkDuplicateMFTWF() {

    const url = environment.apiUrl + '/api/mftwf/v1/checkmasterfeetableworkflowexist';

    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    const body: any = {
      i_page: '1',
      i_size: '1',
      i_fee_detail_id: this.feeDetailId
    };

    try {
      const response: any = await this.http
        .post(url, body, { headers })
        .toPromise();
      console.log('bng' + response.header.statusCode)
      if (response.header.statusCode === '01') {
        return false;
      } else {
        this.error = true;
        this.errorMessages.push('Fee Detail ID is duplicate Master Fee Table work flow.');
        return true;
      }
    } catch (error) {
      this.error = true;
      this.errorMessages.push('Internal Server Error.');
      console.error(error);
      return true;
    }
  }
  //validation end
  //update master fee table workflow end

  //default setting start
  defaultSetting(): void {
    this.error = false;
    // this.errorMessage="";
    this.errorMessages = [];
  }

  async populateFeeGroupId(): Promise<void> {

    const url = environment.apiUrl + '/api/fg/v1/getfeegroup';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json'
    });

    const Body: any = {
      i_page: this.page,
      i_size: this.dropDownSize,
      i_fee_grp_id: null,
      i_fee_grp_nm_en: null,
      i_fee_grp_nm_bm: null,
      i_modified_by: null,
      i_dt_modified_fr: null,
      i_dt_modified_to: null,
      i_status: Systemstatus.Active

    };

    try {
      const response: any = await this.http.post(url, Body, { headers }).toPromise();
      if (response.header.statusCode === '00') {
        this.feeGroups = response.data;
        // return false; // Insert success
      } else {
        //this.error = true;
        //this.errorMessages.push('Insert not successful');
        console.error('Invalid fee group id response format:', response);
        // return true; // Insert failed
      }
    } catch (error) {
      //this.error = true;
      //this.errorMessages.push('Internal Server Error.');
      console.error('There was an error retrieving the fee group id:', error);
      // return true; // Error occurred
    }
  }

  populateAppover() {

    const url = environment.apiUrl + '/api/mft/v1/getuserbyrole';

    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json'
    });


    const requestBody = {
      i_page: this.page,
      i_size: this.dropDownSize,
      i_role_nm_en: this.userHigherOfficialRole,
      i_role_nm_bm: this.userHigherOfficialRole,
      i_status: Systemstatus.Active
    };

    this.http.post(url, requestBody, { headers }).subscribe(
      (response: any) => {
        if (response.data.length == 0) {
          console.error('Invalid approver response format:', response);
        }
        else {
          this.users = response.data.filter((user: User) => user.ssm4uuserrefno !== this.username);
        }
      },
      (error) => {
        console.error('There was an error retrieving the approver:', error);
      }
    );
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
      page: this.page,
      size: this.dropDownSize,
      param_cd: Systemstatus.Active,
      param_grp_nm: 'Status-MFT'
    };

    // Send an HTTP POST request to the API
    this.http.post(url, requestBody, { headers }).subscribe(
      (response: any) => {
        if (response.data.length == 0) {
          console.error('Invalid status response format:', response);
        }
        else {
          this.status = response.data;
        }
      },
      (error) => {
        console.error('There was an error retrieving the status:', error);
      }
    );

  }

  async populateSourceSystemCode(): Promise<void> {

    const url = environment.apiUrl + '/api/rms/v1/getsourcesystem';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json'
    });


    // Create the request body with your form data
    const Body = {
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

    try {
      const response: any = await this.http.post(url, Body, { headers }).toPromise();
      if (response.header.statusCode === '00') {
        this.sourceSystemCodes = response.data
        // return false; // Insert success
      } else {
        // this.error = true;
        // this.errorMessages.push('Insert not successful');
        console.error('Invalid source system code response format:', response);
        // return true; // Insert failed
      }
    } catch (error) {
      //this.error = true;
      // this.errorMessages.push('Internal Server Error.');
      console.error('There was an error retrieving the source system code:', error);
      // return true; // Error occurred
    }

  }

  async populateTaxCode(): Promise<void> {

    const url = environment.apiUrl + '/api/tc/v1/gettaxcode';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json'
    });


    // Create the request body with your form data
    const Body = {

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

    try {
      const response: any = await this.http.post(url, Body, { headers }).toPromise();
      if (response.header.statusCode === '00') {
        this.taxCode = response.data
        // return false; // Insert success
      } else {
        // this.error = true;
        // this.errorMessages.push('Insert not successful');
        console.error('Invalid tax code response format:', response);
        // return true; // Insert failed
      }
    } catch (error) {
      // this.error = true;
      // this.errorMessages.push('Internal Server Error.');
      console.error('There was an error retrieving the tax code:', error);
      // return true; // Error occurred
    }

  }



  //form handle before submit start
  async handleFormSubmit(form: NgForm) {

    let formValidation: boolean | null = false;

    // if (this.inputDetails === true) {

    if (!this.promotionStartDateNotSelected && !this.promotionEndDateNotSelected && !this.promotionStartDateInvalid && !this.promotionEndDateInvalid && !this.promotionEndDateLessThanStartDate) {
      this.passDateValidation = true;
    }
    else {
      this.passDateValidation = false;
    }

    formValidation = form.valid && this.passDateValidation;



    // console.log("form valid is " + form.valid);
    //  console.log("selected files is " + this.selectedFiles.length);
    // console.log("pass date validation is " + this.passDateValidation);
    // console.log("effective date valid is " + this.effectiveDateInvalid);

    // }
    // else {
    //   formValidation = form.valid;
    //  console.log("form valid is " + form.valid);
    //  console.log("effective date valid is " + this.effectiveDateInvalid);
    //  }

    if (formValidation) {

      // if (this.statusFromAssigned === "Q-FA") {
      //   this.updateMFTWF();
      // }
      // else { //this.statusFromAssigned === "P-FA"
      //   if (this.queryFromStatus === "P-RHOD") {  //means requester hod has approved the task
      //     this.updateMFTWF();
      //   }
      //   else { //this.queryFromStatus === "Q-RHOD"  //means query and reply from requester hod
      //     this.updateMFTWF_Status();
      //   }
      // }
      const validProceedToSubmit = await this.checkStatusAndAssignTo(true); //ensure the mftwf status is updated before submit
      if (validProceedToSubmit === true) {
        this.updateMFTWF();
      }
      else {
        console.log("Invalid proceed to submit")
      }
    } else {
      Object.entries(form.controls).forEach(([fieldName, control]) => {
        control.markAsTouched();
        if (control.invalid) {
          console.error(`Field "${fieldName}" is invalid`, control.errors);
        }
      });
    }
  }
  //form handle before submit end

  cancel() {
    this.router.navigate(['/my-task-assigned-tasks']);
  }

  //update master fee table workflow status start
  async updateMFTWF_Status() {
    this.isLoading = true;
    this.defaultSetting();

    this.getAssignTo()

    const invalidUpdate = await this.updateMasterFeeTableWorkFlow_Status()
    if (invalidUpdate === false) {
      this.sendEmail();
      this.isLoading = false;
      const alert_msg = this.alertMessage;
      this.router.navigate(['/my-task-assigned-tasks'], { state: { alert_msg } });
    }

    this.isLoading = false;
  }

  async updateMasterFeeTableWorkFlow_Status(): Promise<boolean> {

    const updmftwfStatusUrl = environment.apiUrl + '/api/mftwf/v1/updatemasterfeetableworkflow_status';

    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json'
    });

    const Body: any = {
      i_wf_id: this.wfId,
      i_assign_to: this.assignTo,
      i_status: this.decision,
    };

    if (this.textRemarks && this.textRemarks.trim()) {
      Body.i_remark = this.textRemarks;
    }

    Body.i_modified_by = this.username;

    //console.log('Action taken is '+this.decision)

    try {
      const response: any = await this.http.post(updmftwfStatusUrl, Body, { headers }).toPromise();
      if (response.header.statusCode === '00') {
        return false; // Update success
      } else {
        this.error = true;
        this.errorMessages.push('Submit not successful');
        return true; // update failed
      }
    } catch (error) {
      this.error = true;
      this.errorMessages.push('Internal Server Error.');
      console.error(error);
      return true; // Error occurred
    }
  }
  //update master fee table workflow status end

  //input checking start
  //fee amt checking start
  formatInput(feeAmtRef: any): void {
    let value = parseFloat(feeAmtRef.value);
    if (!isNaN(value) && value >= 0) {
      this.feeAmt = value.toFixed(2); // Convert number to string with 2 decimal places
    } else if (value < 0) {
      feeAmtRef.control.setErrors({ negative: true }); // Set a custom 'negative' error
    }
  }

  validateInput(ref: any): void {
    let value = ref.value;
    // Check for more than one dot or more than two decimal places
    if ((value.match(/\./g) || []).length > 1 || (value.includes('.') && value.split('.')[1].length > 2)) {
      ref.control.setErrors({ pattern: true }); // Set a custom 'pattern' error
    }
  }
  //fee amt checking end

  //promo fee checking start
  formatInputPromoFee(promoFeeRef: any): void {

    let value = parseFloat(promoFeeRef.value);
    if (!isNaN(value) && value >= 0) {
      this.promoFee = value.toFixed(2); // Convert number to string with 2 decimal places
    } else if (value < 0) {
      promoFeeRef.control.setErrors({ negative: true }); // Set a custom 'negative' error
    }
  }
  //promo fee  checking end

  
  // llStartDay checking start
 formatInputllStartDay(llStartDayRef: any): void {
    
    let value = (llStartDayRef.value ?? '').toString().trim();

    if (value === '') {
      //  Allow empty or null — clear errors and model
      this.llStartDay = '';
      llStartDayRef.control.setErrors(null);
      return;
    }

    //  Check if it’s a valid positive integer
    const intValue = parseInt(value, 10);
    if (!isNaN(intValue) && /^\d+$/.test(value) && intValue >= 0) {
      this.llStartDay = value;
      llStartDayRef.control.setErrors(null);
    } else {
      //  Invalid: non-numeric, negative, or malformed
      llStartDayRef.control.setErrors({ pattern: true });
    }
  }

  validateLateLodgementInput(ref: any): void {
    let value = ref.value;

    if (value !== '' && !/^\d+$/.test(value)) {
      ref.control.setErrors({ pattern: true }); // Set a custom 'pattern' error for non-integer values
    }
  }
  // llStartDay checking end;


  // llStartMth checking start;
  formatInputllStartMth(llStartMthRef: any): void {
    
    let value = (llStartMthRef.value ?? '').toString().trim();

    if (value === '') {
      //  Allow empty or null — clear errors and model
      this.llStartMth = '';
      llStartMthRef.control.setErrors(null);
      return;
    }

    //  Check if it’s a valid positive integer
    const intValue = parseInt(value, 10);
    if (!isNaN(intValue) && /^\d+$/.test(value) && intValue >= 0) {
      this.llStartMth = value;
      llStartMthRef.control.setErrors(null);
    } else {
      //  Invalid: non-numeric, negative, or malformed
      llStartMthRef.control.setErrors({ pattern: true });
    }
  }
  // llStartMthRef checking end;


  // llEndDay checking start;
  formatInputllEndDay(llEndDayRef: any): void {
    
    let value = (llEndDayRef.value ?? '').toString().trim();

    if (value === '') {
      //  Allow empty or null — clear errors and model
      this.llEndDay = '';
      llEndDayRef.control.setErrors(null);
      return;
    }

    //  Check if it’s a valid positive integer
    const intValue = parseInt(value, 10);
    if (!isNaN(intValue) && /^\d+$/.test(value) && intValue >= 0) {
      this.llEndDay = value;
      llEndDayRef.control.setErrors(null);
    } else {
      //  Invalid: non-numeric, negative, or malformed
      llEndDayRef.control.setErrors({ pattern: true });
    }
  }
  // llEndDay checking end;


  // llEndMth checking start;
  formatInputllEndMth(llEndMthRef: any): void {
    
    let value = (llEndMthRef.value ?? '').toString().trim();

    if (value === '') {
      //  Allow empty or null — clear errors and model
      this.llEndMth = '';
      llEndMthRef.control.setErrors(null);
      return;
    }

    //  Check if it’s a valid positive integer
    const intValue = parseInt(value, 10);
    if (!isNaN(intValue) && /^\d+$/.test(value) && intValue >= 0) {
      this.llEndMth = value;
      llEndMthRef.control.setErrors(null);
    } else {
      //  Invalid: non-numeric, negative, or malformed
      llEndMthRef.control.setErrors({ pattern: true });
    }
  }
  // llEndMth checking end;
  //input checking end

  clearEndDate(event: KeyboardEvent) {
    if (event.key === 'Backspace' && this.promoEndDate) {
      this.promoEndDate = null; // Clears the date
      event.preventDefault(); // Prevent default backspace behavior
    } else {
      event.preventDefault(); // Disable all other keys
    }
  }
  
  clearStartDate(event: KeyboardEvent) {
    if (event.key === 'Backspace' && this.promoStartDate) {
      this.promoStartDate = null; // Clears the date
      event.preventDefault(); // Prevent default backspace behavior
    } else {
      event.preventDefault(); // Disable all other keys
    }
  }

  onDecisionChange() {

    if (this.decision === 'P-FHOD') {
      this.alertMessage = 'approved';
      this.disableApprover = false;
      this.notReject = true;
    }
    else if (this.decision === 'Q-R' || this.decision === 'Q-RHOD') {
      this.alertMessage = 'submitted';
      this.disableApprover = true;
      this.notReject = true;
    }
    else if (this.decision === 'RJ-FA') {
      this.alertMessage = 'rejected';
      this.disableApprover = true;
      this.notReject = false;
    }
    else {
      this.alertMessage = undefined;
      this.disableApprover = true;
      this.notReject = true;
    }
  }


  loadDataSupDoc() {
    // this.isDisplaySupDoc = true;
    this.isLoadingSupDoc = true;

    const urlMftWFHis = environment.apiUrl + '/api/mftwfdoc/v1/getmasterfeetableworkflowdocument';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json'
    });


    // Create the request body with your form data
    const requestBody: any = {

      i_page: this.pageSupDoc,
      i_size: this.itemsPerPageSupDoc,
      i_wf_id: this.wfId,
      i_status: Systemstatus.Active,

    };

    this.http.post(urlMftWFHis, requestBody, { headers }).subscribe(
      (response: any) => {

        if (response.data.length === 0) {
          this.isDisplaySupDoc = false;
          this.isLoadingSupDoc = false;
          this.totalRecordsSupDoc = 0;
          console.error('Invalid master fee table work flow document response format:', response);
        }
        else {
          this.mftwfSupDocs = response.data;
          this.isDisplaySupDoc = true;
          this.isLoadingSupDoc = false;
          this.totalRecordsSupDoc = response.data[0].total;
        }
        //  console.log("MFTWF is "+this.mftwf[0].fee_detail_id);
        //  console.log(this.totalRecords);

      },
      (error) => {
        console.error('There was an error retrieving the master fee table work flow document:', error);
        this.isLoadingSupDoc = false;
        // Handle errors here
      }
    );
  }

  loadDataHist() {
    // this.isDisplayHist = true;
    this.isLoadingHist = true;

    const urlMftWFHis = environment.apiUrl + '/api/mftwfh/v1/getmasterfeetableworkflowhistory';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json'
    });


    // Create the request body with your form data
    const requestBody: any = {

      i_page: this.pageHist,
      i_size: this.itemsPerPageHist,
      i_wf_id: this.wfId,
      i_status: null,

    };

    this.http.post(urlMftWFHis, requestBody, { headers }).subscribe(
      (response: any) => {

        if (response.data.length === 0) {
          this.isDisplayHist = false;
          this.isLoadingHist = false;
          this.totalRecordsHist = 0;
          console.error('Invalid master fee table work flow history response format:', response);
        }
        else {
          this.mftwfHis = response.data;
          this.isDisplayHist = true;
          this.isLoadingHist = false;
          this.totalRecordsHist = response.data[0].total;
        }
        //  console.log("MFTWF is "+this.mftwf[0].fee_detail_id);
        //  console.log(this.totalRecords);

      },
      (error) => {
        console.error('There was an error retrieving the master fee table work flow history:', error);
        this.isLoadingHist = false;
      }
    );

  }

  async populateForm(): Promise<void> {

    const urlMftWF = environment.apiUrl + '/api/mftwf/v1/getmasterfeetableworkflow';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json'
    });

    // Create the request body with your form data
    const Body: any = {

      i_page: "1",
      i_size: "1",
      i_wf_id: this.wfId,
      i_wf_is_in_prg: "f"
    };

    try {
      const response: any = await this.http.post(urlMftWF, Body, { headers }).toPromise();
      // console.log("Ast is "+ response.header.statusCode)
      if (response.header.statusCode === '00') {
        this.totalRecords = response.data[0].total
        this.mftwfs = response.data;
        this.effectiveDate = this.mftwfs[0].effective_date;
        this.rFeeDetNm = this.mftwfs[0].r_fee_det_nm;
        this.rFeeAmt = this.mftwfs[0].r_fee_amt;
        this.rSsCd = this.mftwfs[0].r_ss_cd;
        this.rPromoStartDt = this.mftwfs[0].r_promo_startdt;
        this.rPromoEndDt = this.mftwfs[0].r_promo_enddt;
        this.rPromoFee = this.mftwfs[0].r_promo_fee;
        this.rLlRequired = this.mftwfs[0].r_ll_required;
        this.placeHolderisPublic = this.mftwfs[0].is_pub; //to ensure the value not change when isPublic value change
        this.isPublic = this.mftwfs[0].is_pub;
        this.rAddNotes = this.mftwfs[0].r_add_notes;
        this.dtCreated = this.mftwfs[0].dt_created;

        if (this.rSsCd !== null) {
          // this.checkboxRssCd = this.rSsCd.split(',');
          this.rSsCdWithSpace = this.rSsCd.split(',').join(', '); //for display
        }

        // if (this.inputDetails === false && this.decisionDetails === true) {  //means task is query back to requester HOD and requester HOD has reply the query
        //   this.displayFeeDetailId = this.mftwfs[0].fee_detail_id;
        //   this.displayFeeGroupNmEn = this.mftwfs[0].fee_grp_nm_en;
        //   this.displayFeeDetailNmE = this.mftwfs[0].fee_detail_nm_e;
        //   this.displayFeeDetailNmB = this.mftwfs[0].fee_detail_nm_b;
        //   this.displayFeeAmt = this.mftwfs[0].fee_amt;
        //   this.displayPromoStartDate = this.mftwfs[0].promo_startdt;
        //   this.displayPromoEndDate = this.mftwfs[0].promo_enddt;
        //   this.displayPromoFee = this.mftwfs[0].promo_fee;
        //   this.displayTaxCd = this.mftwfs[0].tax_cd;
        //   this.displayAllowOTC = this.mftwfs[0].allow_otc;
        //   this.displayLlParentId = this.mftwfs[0].ll_parent_id;
        //   this.displayLlStartDay = this.mftwfs[0].ll_start_day;
        //   this.displayLlStartMth = this.mftwfs[0].ll_start_mth;
        //   this.displayLlEndDay = this.mftwfs[0].ll_end_day;
        //   this.displayLlEndMth = this.mftwfs[0].ll_end_mth;
        //   this.displayLedgerCd = this.mftwfs[0].ledger_cd;
        //   this.displaySsCd = this.mftwfs[0].ss_cd;
        //   this.displayDtCreated = this.mftwfs[0].dt_created;
        //   this.displayCreatedBy = this.mftwfs[0].created_by;
        //   this.displayCreatedByNm = this.mftwfs[0].created_by_nm;
        //   this.displayDtModified = this.mftwfs[0].dt_modified;
        //   this.displayModifiedBy = this.mftwfs[0].modified_by;
        //   this.displayModifiedByNm = this.mftwfs[0].modified_by_nm;
        //   this.displayMFTStatus = this.mftwfs[0].mft_status;

        //   if (this.displaySsCd !== null) {
        //    // this.checkboxSsCd = this.displaySsCd.split(',');
        //     this.ssCdWithSpace = this.displaySsCd.split(',').join(', '); //for display
        //   }
        // }

        // if (this.inputDetails === true && this.decisionDetails === false) {  //means query from finance hod
        if (this.populateFromRFields === false) {  //means query from finance hod
          // this.feeDetailPk = this.mftwfs[0].fee_detail_pk === null ? null : this.mftwfs[0].fee_detail_pk.toString();;
          this.feeDetailId = this.mftwfs[0].fee_detail_id === null ? null : this.mftwfs[0].fee_detail_id.toString();

          if (this.mftwfs[0].fee_grp_id === null || this.mftwfs[0].fee_grp_id === undefined) { //because ngmodel is feeGroupId
            this.feeGroupId = null;
          }
          else {
            if (this.feeGroups.some(feeGroup => feeGroup.fee_grp_id === this.mftwfs[0].fee_grp_id)) {
              this.feeGroupId = this.mftwfs[0].fee_grp_id;
            }
            else {
              this.feeGroupId = null;
            }
          }

          this.feeDetailNmE = this.mftwfs[0].fee_detail_nm_e === null ? null : this.mftwfs[0].fee_detail_nm_e.toString();
          this.feeDetailNmB = this.mftwfs[0].fee_detail_nm_b === null ? null : this.mftwfs[0].fee_detail_nm_b.toString();
          this.feeAmt = this.mftwfs[0].fee_amt === null ? null : this.mftwfs[0].fee_amt.toFixed(2).toString();
          this.promoStartDate = this.mftwfs[0].promo_startdt === null ? null : this.mftwfs[0].promo_startdt.toString();
          this.promoEndDate = this.mftwfs[0].promo_enddt === null ? null : this.mftwfs[0].promo_enddt.toString();
          this.promoFee = this.mftwfs[0].promo_fee === null ? null : this.mftwfs[0].promo_fee.toFixed(2).toString();

          if (this.mftwfs[0].tax_cd_id === null || this.mftwfs[0].tax_cd_id === undefined) {
            this.taxCdId = null;
          }
          else {
            if (this.taxCode.some(taxCode => taxCode.tax_cd_id === this.mftwfs[0].tax_cd_id)) {
              this.taxCdId = this.mftwfs[0].tax_cd_id;
            }
            else {
              this.taxCdId = null;
            }
          }

          this.allowOTC = this.mftwfs[0].allow_otc;

          // if (this.mftwfs[0].ll_parent_id === null || this.mftwfs[0].ll_parent_id === undefined) {
          //   this.llParentId = null;
          // }
          // else {
          //   if (this.mftsForLlPID.some(mftsForLlPID => mftsForLlPID.fee_detail_id.includes(this.mftwfs[0].ll_parent_id))) {
          //     this.llParentId = this.mftwfs[0].ll_parent_id.toString(); // sp, taxcd is use for tax_cd_id
          //   }
          //   else {
          //     this.llParentId = null;
          //   }
          // }
          const llParentId = this.mftwfs[0].ll_parent_id;

        if (llParentId) {
          const selectedLlParent = await this.loadLlParentById(llParentId);

          if (selectedLlParent) {
            // Ensure dropdown contains it
            if (!this.mftsForLlPID.some(m => m.fee_detail_id === llParentId)) {
              this.mftsForLlPID = [selectedLlParent, ...this.mftsForLlPID];
            }

            // Select it
            this.llParentId = llParentId;
          } else {
            this.llParentId = null;
          }
        } else {
          this.llParentId = null;
        }

          this.llStartDay = this.mftwfs[0].ll_start_day === null ? null : this.mftwfs[0].ll_start_day.toString();
          this.llStartMth = this.mftwfs[0].ll_start_mth === null ? null : this.mftwfs[0].ll_start_mth.toString();
          this.llEndDay = this.mftwfs[0].ll_end_day === null ? null : this.mftwfs[0].ll_end_day.toString();
          this.llEndMth = this.mftwfs[0].ll_end_mth === null ? null : this.mftwfs[0].ll_end_mth.toString();
          this.ledgerCd = this.mftwfs[0].ledger_cd === null ? null : this.mftwfs[0].ledger_cd.toString();

          if (this.mftwfs[0].ss_cd === null || this.mftwfs[0].ss_cd === undefined) {
            this.selectedSourceSystemCodes = [];
          }
          else {

            let tempSelectedSourceSystemCodes = this.mftwfs[0].ss_cd.toString().split(',');
            this.selectedSourceSystemCodes = []; //when perform array binding to ngmodel, need to declare again

            for (let i = 0; i < tempSelectedSourceSystemCodes.length; i++) {
              if (this.sourceSystemCodes.some(sourceSystemCode => sourceSystemCode.ss_cd.includes(tempSelectedSourceSystemCodes[i]))) {
                this.selectedSourceSystemCodes.push(tempSelectedSourceSystemCodes[i]);
              }
            }
          }

          this.modifiedBy = this.mftwfs[0].modified_by === null ? null : this.mftwfs[0].modified_by.toString(); //not in use
          this.modifiedByNm = this.mftwfs[0].modified_by_nm === null ? null : this.mftwfs[0].modified_by_nm.toString();
          this.mftStatus = this.mftwfs[0].mft_status === null ? null : this.mftwfs[0].mft_status.toString();
        }

        else { //(this.populateFromRFields === true) means task is approved from requester HOD therefore need insert and populate from r fields
          // if (this.inputDetails === true && this.decisionDetails === true) {  //means task is approved from requester HOD therefore need insert
          //this.feeDetailNmE = this.mftwfs[0].r_fee_det_nm === null ? null : this.mftwfs[0].r_fee_det_nm.toString();

          let tempFeeDetName: string;

          if (this.mftwfs[0].r_fee_det_nm !== null) {

            const feeDetName = this.mftwfs[0].r_fee_det_nm.toString();
            const feeDetNameAfterSplit = feeDetName.split(/:(.+)/);

            // Trim any leading or trailing whitespaces
            tempFeeDetName = feeDetNameAfterSplit.length > 1 ? feeDetNameAfterSplit[1].trim() : feeDetName;
            this.feeDetailNmE = tempFeeDetName;
    
          }
          else {
            this.feeDetailNmE = null;
          }

          this.feeAmt = this.mftwfs[0].r_fee_amt === null ? null : this.mftwfs[0].r_fee_amt.toFixed(2).toString();

          if (this.mftwfs[0].r_ss_cd === null || this.mftwfs[0].r_ss_cd === undefined) {
            this.selectedSourceSystemCodes = [];
          }
          else {

            let tempSelectedSourceSystemCodes = this.mftwfs[0].r_ss_cd.toString().split(',');
            this.selectedSourceSystemCodes = []; //when perform array binding to ngmodel, need to declare again

            for (let i = 0; i < tempSelectedSourceSystemCodes.length; i++) {
              if (this.sourceSystemCodes.some(sourceSystemCode => sourceSystemCode.ss_cd.includes(tempSelectedSourceSystemCodes[i]))) {
                this.selectedSourceSystemCodes.push(tempSelectedSourceSystemCodes[i]);
              }
            }
          }

          this.promoStartDate = this.mftwfs[0].r_promo_startdt === null ? null : this.mftwfs[0].r_promo_startdt.toString();
          this.promoEndDate = this.mftwfs[0].r_promo_enddt === null ? null : this.mftwfs[0].r_promo_enddt.toString();
          this.promoFee = this.mftwfs[0].r_promo_fee === null ? null : this.mftwfs[0].r_promo_fee.toFixed(2).toString();
        }

        //all status need to use below info
        this.createdBy = this.mftwfs[0].created_by === null ? null : this.mftwfs[0].created_by.toString(); //not in use
        this.createdByNm = this.mftwfs[0].created_by_nm === null ? null : this.mftwfs[0].created_by_nm.toString();
      } else {
        this.totalRecords = 0;
        console.error('Invalid master fee table work flow response format:', response);
      }
    } catch (error) {
      console.error('There was an error retrieving the master fee table work flow:', error);
    }
  }

  async getworkflowhistory_ast(status: string): Promise<{ assign_to: string | null, assign_to_nm: string | null }> {

    const url = environment.apiUrl + '/api/mftwfh/v1/getworkflowhistory_ast';

    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json'
    });

    const body: any = {
      i_task_id: this.taskId,
      i_status: status,
    };

    try {
      const response: any = await this.http.post(url, body, { headers }).toPromise();

      if (response.header.statusCode === '00') {
        return {
          assign_to: response.data[0].assign_to,
          assign_to_nm: response.data[0].assign_to_nm
        }; // Data found
      } else {
        // this.error = true;
        // this.errorMessages.push('Data not found');
        console.error('Invalid master fee table history assign to response format:', response);
        return { assign_to: null, assign_to_nm: null }; // Data not found
      }
    } catch (error) {
      //  this.error = true;
      //  this.errorMessages.push('Internal Server Error.');
      console.error('There was an error retrieving the master fee table history assign to:', error);
      return { assign_to: null, assign_to_nm: null }; // Error occurred
    }
  }

  //download file start
  downloadFile(file_nm: string, wfdoc_id: number): void {

    const url = environment.apiUrl + '/api/mftwfdoc/v1/getmftwfdocfilecontent';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    const Body: any = {
      i_wfdoc_id: wfdoc_id
    };

    console.log(Body);

    this.http.post(url, Body, { headers }).subscribe(
      (response: any) => {
        // console.log(response.data);
        this.file_content = response.data;
        this.downloadFileContent(file_nm, this.file_content);

        if (response.data.length == 0) {
          this.isLoadingSupDoc = false;
          console.error('Invalid master fee table work flow document response format:', response);
        } else {
          this.isLoadingSupDoc = false;
          console.log('Successful download file ' + file_nm);
        }
      },
      (error) => {
        this.isLoadingSupDoc = false;
        console.error('There was an error downloading the master fee table work flow document:', error);;
      }
    );
  }

  downloadFileContent(fileName: string, fileContent: string): void {
    // event.preventDefault(); // Prevent the default behavior of the anchor element
    this.isLoadingSupDoc = true;

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


  getAssignTo() {

    if (this.statusFromAssigned === "Q-FA") {
      this.assignTo = this.queryBackTo;
      this.decision = this.queryFromStatus;
      this.emailAssignTo = this.queryBackTo;
      this.alertMessage = "submittedForApproval";
    }
    else {
      if (this.decision === 'Q-R') //Query Pending Requester
      {
        this.assignTo = this.requester;
        this.emailAssignTo = this.requester;
      }
      else if (this.decision === 'Q-RHOD') //Query Pending Requester HOD
      {
        this.assignTo = this.requesterHOD;
        this.emailAssignTo = this.requesterHOD;
      }
      else if (this.decision === 'P-FHOD') { //Pending Finance HOD Approval
        this.assignTo = this.ssm4uuserrefno;
        this.emailAssignTo = this.ssm4uuserrefno;
      }
      else if (this.decision === 'RJ-FA') {  //Rejected by Finance Admin
        this.assignTo = null;
        this.emailAssignTo = this.createdBy;
      }
      else {
        this.assignTo = null;
        this.emailAssignTo = this.requester;
      }
    }
  }

  async getworkflowhistory_status(status: string): Promise<{ assign_to: string | null, assign_to_nm: string | null, status: string | null }> {

    const url = environment.apiUrl + '/api/mftwfh/v1/getworkflowhistory_status';

    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json'
    });

    const body: any = {
      i_task_id: this.taskId,
      i_status: status,
    };

    try {
      const response: any = await this.http.post(url, body, { headers }).toPromise();
      if (response.header.statusCode === '00') {
        return {
          assign_to: response.data[0].assign_to,
          assign_to_nm: response.data[0].assign_to_nm,
          status: response.data[0].status
        }; // Data found
      } else {
        //  this.error = true;
        //this.errorMessages.push('Data not found');
        console.error('Invalid master fee table work flow history assign to by status response format:', response);
        return { assign_to: null, assign_to_nm: null, status: null }; // Data not found
      }
    } catch (error) {
      // this.error = true;
      //  this.errorMessages.push('Internal Server Error.');
      console.error('There was an error retrieving the master fee table work flow history assign to by status:', error);
      return { assign_to: null, assign_to_nm: null, status: null }; // Error occurred
    }
  }

  async populateLateLodgementID(): Promise<void> {

    const url = environment.apiUrl + '/api/mft/v1/getmasterfeetable';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    const Body: any = {
      i_page: this.page.toString(),
      i_size: this.dropDownSize.toString(),
      i_status: Systemstatus.Active
    };

    try {
      const response: any = await this.http.post(url, Body, { headers }).toPromise();
      if (response.header.statusCode === '00') {
        this.mftsForLlPID = response.data;
        // return false; // Insert success
      } else {
        // this.error = true;
        // this.errorMessages.push('Insert not successful');
        console.error('Invalid master fee table response format:', response);
        // return true; // Insert failed
      }
    } catch (error) {
      //this.error = true;
      // this.errorMessages.push('Internal Server Error.');
      console.error('There was an error retrieving the master fee table:', error);
      // return true; // Error occurred
    }
  }


  //promomotion start, end and effective date start
  promotionDateConvertion() {
    if (this.promoStartDate === undefined) { //this is because after select date and when backspace to remove, this become undefined
      this.promoStartDate = null;
      this.cdref.detectChanges();
    }
    if (this.promoEndDate === undefined) { //this is because after select date and when backspace to remove, this become undefined
      this.promoEndDate = null;
      this.cdref.detectChanges();
    }

    if (this.promoStartDate !== null) {
      this.promoStartDateAsDate = new Date(this.promoStartDate);
      this.promoStartDateAsDate.setHours(0, 0, 0, 0);
    }
    else {
      this.promoStartDateAsDate = null;
    }

    if (this.promoEndDate !== null) {
      this.promoEndDateAsDate = new Date(this.promoEndDate);
      this.promoEndDateAsDate.setHours(0, 0, 0, 0);
    }
    else {
      this.promoEndDateAsDate = null;
    }
  }

  isPromoStartDateInvalid(): boolean {

    this.promotionDateConvertion();

    // Check if promoStartDateAsDate is not null
    if (this.promoStartDateAsDate !== null) {

      // Check if promoStartDateAsDate is less than or equal to today
      if (this.promoStartDateAsDate <= this.currentDate) {
        this.promotionStartDateInvalid = true;
        return true;
      }
      else {
        this.promotionStartDateInvalid = false;
      }
    }
    this.promotionStartDateInvalid = false;
    return false;
  }

  isPromoEndDateInvalid(): boolean {

    this.promotionDateConvertion();

    // Check if promoEndDateAsDate is not null
    if (this.promoEndDateAsDate !== null) {

      // Check if promoEndDateAsDate is less than or equal to today
      if (this.promoEndDateAsDate <= this.currentDate) {
        this.promotionEndDateInvalid = true;
        return true;
      }
      else {
        this.promotionEndDateInvalid = false;
      }
    }
    this.promotionEndDateInvalid = false;
    return false;
  }

  isPromoStartDateNotSelected(): boolean {
    this.promotionDateConvertion();

    if ((this.promoEndDateAsDate !== null) && this.promoStartDateAsDate === null) {
      this.promotionStartDateNotSelected = true;
      return true;
    }
    else {
      this.promotionStartDateNotSelected = false;
      return false;
    }
  }

  isPromoEndDateNotSelected(): boolean {
    this.promotionDateConvertion();

    if ((this.promoStartDateAsDate !== null) && this.promoEndDateAsDate === null) {
      this.promotionEndDateNotSelected = true;
      return true;
    }
    else {
      this.promotionEndDateNotSelected = false;
      return false;
    }
  }


  isPromoEndDateLessThanPromoEndDate(): boolean {

    if (this.promoEndDateAsDate !== null && this.promoStartDateAsDate !== null) {
      if (this.promoEndDateAsDate <= this.promoStartDateAsDate) {
        this.promotionEndDateLessThanStartDate = true;
        return true;
      }
      else {
        this.promotionEndDateLessThanStartDate = false;
        return false;
      }
    }
    this.promotionEndDateLessThanStartDate = false;
    return false;

  }
  //promomotion start, end and effective date end

  async populateCurrentValueForm(): Promise<void> {

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
      i_status: Systemstatus.Active
    };

    try {
      const response: any = await this.http.post(url, Body, { headers }).toPromise();
      if (response.header.statusCode === '00') {
        this.totalMFTRecords = response.data[0].total;
        this.mfts = response.data;
        this.currentFeeDetailId = this.mfts[0].fee_detail_id;
        this.currentFeeGroupNmEn = this.mfts[0].fee_grp_nm_en;
        this.currentFeeDetailNmEn = this.mfts[0].fee_detail_nm_e;
        this.currentFeeDetailNmBm = this.mfts[0].fee_detail_nm_b;
        this.currentUnitFee = this.mfts[0].unit_fee;
        this.currentPromoStartDate = this.mfts[0].promo_startdt;
        this.currentPromoEndDate = this.mfts[0].promo_enddt;
        this.currentPromoFee = this.mfts[0].promo_fee;
        this.currentTaxCd = this.mfts[0].tax_cd;
        this.currentAllowOTC = this.mfts[0].allow_otc;
        this.currentLlParentId = this.mfts[0].ll_parent_id;
        this.currentLlStartDay = this.mfts[0].ll_start_day;
        this.currentLlStartMth = this.mfts[0].ll_start_mth;
        this.currentLlEndDay = this.mfts[0].ll_end_day;
        this.currentLlEndMth = this.mfts[0].ll_end_mth;
        this.currentLedgerCd = this.mfts[0].ledger_cd;
        this.currentSsCd = this.mfts[0].ss_cd;
        this.currentDtCreated = this.mfts[0].dt_created;
        this.currentCreatedBy = this.mfts[0].created_by;
        this.currentCreatedByNm = this.mfts[0].created_by_nm;
        this.currentDtModified = this.mfts[0].dt_modified;
        this.currentModifiedBy = this.mfts[0].modified_by;
        this.currentModifiedByNm = this.mfts[0].modified_by_nm;
        this.currentStatus = this.mfts[0].status;
        this.currentIsPublic = this.mfts[0].isPub;

        if (this.currentSsCd !== null) {
          //this.currentCheckboxSsCd = this.currentSsCd.split(',');
          this.currentSsCdWithSpace = this.currentSsCd.split(',').join(', '); //for display
        }
      } else {
        this.totalMFTRecords = 0;
        console.error('Invalid master fee table response format:', response);
      }
    } catch (error) {
      console.error('There was an error retrieving the master fee table:', error);
    }
  }

  //email start

  async sendEmail() {

    const url = environment.apiUrl + '/api/mftemail/v1/backend';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    this.emailWfId = this.wfId;

    await this.getworkflowDetailAfterInsert(); //use wfid to find fee detail id and task id
    console.log("emailWfId is " + this.emailWfId);
    console.log("emailfeeDetailId is " + this.emailfeeDetailId);
    console.log("emailTaskId is " + this.emailTaskId);
    console.log("emailrfeeDetNm is " + this.emailrfeeDetNm);
    const Body: any = {

      i_wf_id: this.emailWfId,
      i_task_id: this.emailTaskId,
      i_fee_detail_pk: this.emailFeeDetailPk,
      i_fee_detail_id: this.emailfeeDetailId,
      i_r_fee_det_nm: this.emailrfeeDetNm,
      i_status: this.decision,
      i_action: this.emailAction,
      i_send_to: this.emailAssignTo,
      i_cc: "",
      i_bcc: ""
    };

    this.http.post(url, Body, { headers }).subscribe(
      (response: any) => {
        console.log('Email awaiting scheduler to send email.')
      },
      (error) => {
        console.error('There was an error sending the email:', error);
      }
    );
  }


  async getworkflowDetailAfterInsert(): Promise<void> {

    const urlMftWF = environment.apiUrl + '/api/mftwf/v1/getmasterfeetableworkflow';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json'
    });


    // Create the request body with your form data
    const Body: any = {

      i_page: "1",
      i_size: "1",
      i_wf_id: this.emailWfId,
      i_wf_is_in_prg: "f"
    };

    try {
      const response: any = await this.http.post(urlMftWF, Body, { headers }).toPromise();

      if (response.header.statusCode === '00') {
        this.emailMftwfs = response.data;
        this.emailFeeDetailPk = this.emailMftwfs[0].fee_detail_pk;
        this.emailfeeDetailId = this.emailMftwfs[0].fee_detail_id;
        this.emailTaskId = this.emailMftwfs[0].task_id;
        this.emailrfeeDetNm = this.emailMftwfs[0].r_fee_det_nm;
        this.emailAction = this.emailMftwfs[0].action;

      } else {
        //this.error = true;
        //this.errorMessages.push('Data not found');
        console.error('Invalid master work work flow response format:', response);
        //  return ''; //  Data not found
      }
    } catch (error) {
      // this.error = true;
      // this.errorMessages.push('Internal Server Error.');
      console.error('There was an error retrieving the master fee table work flow:', error);
      //return ''; // Error occurred
    }
  }

  //email end

  checkPermission() {
    this.authService.checkUserRole(this.authService.username, this.permRequestEditMFTwithRequesterFormFA)
      .subscribe(
        (response: any) => {
          this.permRequestEditMFTwithRequesterFormFAAllow = response.data;
          console.log("this.permRequestEditMFTwithRequesterFormFAAllow " + this.permRequestEditMFTwithRequesterFormFAAllow);
          this.permListAllow = this.permRequestEditMFTwithRequesterFormFAAllow.includes(perm.Master_Fee_Table_Request_Edit_MFT_with_Requester_Form_FA) ? 1 : 0;
          if (this.permListAllow === 0) {
            this.router.navigate(['/access-denied']);
            return; // Exit t he function to prevent further execution

          }
        }
      );
  }

  async checkStatusAndAssignTo(clicked: boolean): Promise<boolean> {

    const urlMftWF = environment.apiUrl + '/api/mftwf/v1/getmasterfeetableworkflow';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json'
    });

    const Body: any = {

      i_page: "1",
      i_size: "1",
      i_wf_id: this.wfId,
      i_wf_is_in_prg: "f"
    };

    try {
      const response: any = await this.http.post(urlMftWF, Body, { headers }).toPromise();

      if (response.header.statusCode === '00') {
        this.currentMFTWFDetail = response.data;
        this.currentMFTWFStatus = this.currentMFTWFDetail[0].status;
        this.currentMFTWFAssignTo = this.currentMFTWFDetail[0].assign_to;

        console.log("currentMFTWFAssignTo is " + this.currentMFTWFAssignTo);
        console.log("username is " + this.username);
        console.log("currentMFTWFStatus is " + this.currentMFTWFStatus);
        console.log("statusFromAssigned is " + this.statusFromAssigned);

        // if (this.currentMFTWFAssignTo !== this.username || this.currentMFTWFStatus !== this.statusFromAssigned) {
        //   const alert_msg_access_denied = "This task is assigned or reassigned to another user."
        //   this.router.navigate(['/master-fee-table'], { state: { alert_msg_access_denied } });
        //   return false; // Exit t he function to prevent further execution
        // }

        if (this.currentMFTWFAssignTo !== this.username && this.currentMFTWFAssignTo !== null) {
          // const alert_msg_access_denied = "This task is assigned or reassigned to another user."
          // this.router.navigate(['/master-fee-table'], { state: { alert_msg_access_denied } });
          const showTaskNotUpdateAlert = true;
            this.router.navigate(['/my-task-assigned-tasks'], { state: { showTaskNotUpdateAlert} });
          return false;
        }

        if (clicked === false) {
          if (this.currentMFTWFStatus === "C" || this.currentMFTWFStatus === "APV" || this.currentMFTWFStatus === "EFT"
            || this.currentMFTWFStatus === "RJ-RHOD" || this.currentMFTWFStatus === "RJ-FA" || this.currentMFTWFStatus === "RJ-FHOD") { //below parameter are different for each page
            // const alert_msg_task_canceled_closed = "This task has been canceled or closed and it is no longer active."
            // const wf_id = this.wfId;
            // const status__From_Assigned = this.currentMFTWFStatus;
            // const task_id = this.taskId;
            // const assign_to = this.currentMFTWFAssignTo;
            // const fee_detail_pk = this.feeDetailPk;
            // const edit_Mode = true;
            // const show_requester_table = true;
            // const from_view = false;
            // const navigate_not_refresh = true
            // this.router.navigate(['/task-details'], { state: { wf_id, status__From_Assigned, task_id, assign_to, fee_detail_pk, edit_Mode, show_requester_table, from_view, navigate_not_refresh } });
            const showTaskNotUpdateAlert = true;
            this.router.navigate(['/my-task-assigned-tasks'], { state: { showTaskNotUpdateAlert} });
            return false;
          }
        }
        else {
          if (this.currentMFTWFStatus !== this.statusFromAssigned) {
            // const alert_msg_access_denied = "This task is assigned or reassigned to another user."
            // this.router.navigate(['/master-fee-table'], { state: { alert_msg_access_denied } });
            // this.errorTaskNotUpdate = true;
            // this.errorMessagesTaskNotUpdate.push("The task status has been updated. Please refresh the page to reflect the latest changes.");
            const showTaskNotUpdateAlert = true;
            this.router.navigate(['/my-task-assigned-tasks'], { state: { showTaskNotUpdateAlert} });
            return false;
          }
        }

        return true;

      } else {
        console.error('Invalid master work work flow response format:', response);
        return false;
      }
    } catch (error) {
      console.error('There was an error retrieving the master fee table work flow:', error);
      return false;
    }
  }

    // Update minPromoEndDate dynamically
    onStartDateChange(selectedStartDate: Date) {
      if (selectedStartDate) {
        const newMinEndDate = new Date(selectedStartDate);
        newMinEndDate.setDate(newMinEndDate.getDate() + 1); // Ensure it's at least 1 day later
        this.minPromoEndDate = newMinEndDate;
      }
    }


    //load parentld
  async loadLlParent(searchTerm: string = ''): Promise<any[]> {
  const url = environment.apiUrl + '/api/mft/v1/getmasterfeetable_typesearch';

  const headers = new HttpHeaders({
    Authorization: environment.authKey,
    'Content-Type': 'application/json',
  });

  const Body = {
    i_page: this.llParentPage,
    i_size: this.dropDownSize100,
    i_status: Systemstatus.Active,
    i_ss_cd: null,
    i_searchTerm: searchTerm
  };

  const response: any = await this.http.post(url, Body, { headers }).toPromise();

  if (response.header.statusCode === '00') {
    return response.data ?? [];
  }

  return [];
}

loadMoreLlParent(): void {
  if (this.loadingLlParent) return;

  this.llParentPage++;
  this.loadingLlParent = true;

  this.loadLlParent(this.currentLlParentTerm)
    .then(data => {
      this.mftsForLlPID = [...this.mftsForLlPID, ...data];
      this.loadingLlParent = false;
    })
    .catch(() => {
      this.loadingLlParent = false;
    });
}

//below is use to populate parentlld after user query back to requester using feeDetailId
async loadLlParentById(feeDetailId: string): Promise<any | null> {
  const url = environment.apiUrl + '/api/mft/v1/getmasterfeetable';

  const headers = new HttpHeaders({
    Authorization: environment.authKey,
    'Content-Type': 'application/json',
  });

  const Body = {
    i_page: 1,
    i_size: 1,
    i_fee_detail_id: feeDetailId,
    i_status: Systemstatus.Active
  };

  try {
    const response: any = await this.http.post(url, Body, { headers }).toPromise();
    if (response.header.statusCode === '00' && response.data?.length) {
      return response.data[0];
    }
  } catch (e) {
    console.error('loadLlParentById failed', e);
  }

  return null;
}


}
