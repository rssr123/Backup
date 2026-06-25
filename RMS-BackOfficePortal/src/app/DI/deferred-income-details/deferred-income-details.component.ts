
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { environment } from 'src/environments/environment';

import { DeferredIncome } from '../../core/models/entity';
import { ParamService } from '../../core/services/param.service';
import { ParamData } from 'src/app/core/models/param.interface';
import { Param, User } from '../../core/models/entity';
import { Router } from '@angular/router';
import { NgForm } from '@angular/forms';
import moment from 'moment';
import { Systemstatus } from '../../shared/enums/systemstatus';
import { trigger, state, style, transition, animate } from '@angular/animations';
import { fadeInOut } from '../../shared/animation';
import { from } from 'rxjs';

@Component({
  selector: 'app-deferred-income-details',
  templateUrl: './deferred-income-details.component.html',
  styleUrls: ['./deferred-income-details.component.scss']
})
export class DeferredIncomeDetailsComponent implements OnInit {

  isDisplay: boolean = false;
  isLoading: boolean = false;
  isDisplayTaskLists: boolean = false;
  isLoadingTaskLists: boolean = false;
  errorMessages: string[] = [];
  error: boolean = false;
  di: DeferredIncome[] = []
  updateWFstatus: string = ""
  dropDownTotalRecord = 1000;
  navigateToAssignedTask = false

  statusOptions: Param[] = [];
  users: User[] = [];
  page = environment.DefaultPage; 
  diId: any;
  itemsPerPage = environment.ItemPerPage;
  totalRecords: number = 0;
  selectedEffectiveDate!: { start?: moment.Moment; end?: moment.Moment };
  selectedRequestedDate!: { start?: moment.Moment; end?: moment.Moment };
  showResultAlert = false;

  showResultAlertBox() {
    this.showResultAlert = true;
    setTimeout(() => this.showResultAlert = false, 2000);
  }

  idId: BigInteger | null = null
  feeDetailId: string | null = null
  transactionType: String | null = null;
  entityType: string | null = null
  entityNo: string | null = null
  effectiveDate: Date | null = null
  expiryDate: Date | null = null
  terminationDate: Date | null = null
  itemReferanceNo: String | null = null;
  approvalStatus: string | null = null
  approvalDate: Date | null = null
  numberOfYears: number | null = null
  unitFee: number | null = null
  totalFee: number | null = null
  balanceDIAmount: number | null = null
  nextCalculationDate: Date | null = null
  createdDate: Date | null = null
  diStatus: string | null = null
  statusNmEn: string | null = null


  //default pagination
  selectedValue = environment.dropdownOptions[0];
  dropdownOptions = environment.dropdownOptions;

  LoadData(singleItem: number) {
    this.selectedValue = singleItem;
    this.itemsPerPage = this.selectedValue;
    this.loadData();
  }

  constructor(private route: ActivatedRoute, private http: HttpClient, private router: Router) { }


  ngOnInit() {

    this.selectedEffectiveDate = {
      start: moment().subtract(1, 'month'),
      end: moment(),
    };

    this.selectedRequestedDate = {
      start: moment().subtract(1, 'month'),
      end: moment(),
    };

    this.diId = history.state.di_id


    this.loadData()
    this.populateForm()

  }



  loadData() {
    this.isDisplay = true;
    this.isLoading = true;
    const url = environment.apiUrl + '/api/di/v1/getdeferredincome';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    const Body: any = {
      i_page: this.page.toString(),
      i_size: this.itemsPerPage.toString(),
    };

    if (this.feeDetailId && this.feeDetailId.trim()) {
      Body.i_fee_detail_id = this.feeDetailId;
    }

    if (this.transactionType && this.transactionType.trim()) {
      Body.i_txn_type = this.transactionType;
    }

    if (this.entityType && this.entityType.trim()) {
      Body.i_entity_type= this.entityType;
    }

    if (this.entityNo && this.entityNo.trim()) {
      Body.i_entity_no = this.entityNo;
    }

    if (this.effectiveDate && this.effectiveDate) {
      Body.i_effective_date = this.effectiveDate;
    }

    if (this.expiryDate && this.expiryDate) {
      Body.i_expiry_date = this.expiryDate;
    }

    if (this.approvalStatus && this.approvalStatus.trim()) {
      Body.i_approval_status = this.approvalStatus;
    }

    if (this.approvalDate && this.approvalDate) {
      Body.i_approval_date = this.approvalDate;
    }

    if (this.itemReferanceNo && this.itemReferanceNo.trim()) {
      Body.i_item_ref_no = this.itemReferanceNo;
    }

    if (this.diStatus && this.diStatus.trim()) {
      Body.i_status = this.diStatus;
    }

    this.http.post(url, Body, { headers }).subscribe(
      (response: any) => {
        this.di = response.data;
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
        // Handle errors here  
      }
    );
  }


  apply() {
    this.loadData()
    
  }


  reset() {


    this.selectedEffectiveDate = {
      start: moment().subtract(1, 'month'),
      end: moment(),
    };
    this.selectedRequestedDate = {
      start: moment().subtract(1, 'month'),
      end: moment(),
    };


  }

  populateForm() {

    this.diId = this.diId;
    this.isDisplay = true;
    this.isLoading = true;

    const url = environment.apiUrl + '/api/di/v1/getdeferredincome';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    const Body: any = {
      i_page: this.page.toString(),
      i_size: 1,
      i_di_id: this.diId,
    };

    this.http.post(url, Body, { headers }).subscribe(
      (response: any) => {
        if (response.data.length === 0) {
          this.totalRecords = 0;
        } else {
          this.totalRecords = response.data[0].total;
          this.di = response.data;
          this.feeDetailId = this.di[0].fee_detail_id;
          this.transactionType = this.di[0].txn_type;
          this.entityType = this.di[0].entity_type;
          this.entityNo = this.di[0].entity_no;
          this.effectiveDate = this.di[0].dt_effective;
          this.expiryDate = this.di[0].dt_expiry;
          this.terminationDate = this.di[0].dt_termination;
          this.itemReferanceNo = this.di[0].item_ref_no;
          this.approvalStatus = this.di[0].approval_status;
          this.approvalDate = this.di[0].dt_approval;
          this.numberOfYears = this.di[0].no_of_yr;
          this.unitFee = this.di[0].unit_fee;
          this.totalFee = this.di[0].total_fee;
          this.balanceDIAmount = this.di[0].bal_di_amt;
          this.nextCalculationDate = this.di[0].next_calc_dt;
          this.createdDate = this.di[0].dt_created;
          this.diStatus = this.di[0].status;
          this.statusNmEn = this.di[0].status_nm_en;
          this.isLoading = false;
        }
      },
      (error) => {
        console.error(error);
        this.isLoading = false;
        // Handle errors here
      }
    );

  }

}
