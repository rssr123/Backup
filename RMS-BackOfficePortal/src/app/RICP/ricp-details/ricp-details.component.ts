import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { environment } from 'src/environments/environment';

import { RICP } from '../../core/models/entity';
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
  selector: 'app-ricp-details',
  templateUrl: './ricp-details.component.html',
  styleUrls: ['./ricp-details.component.scss']
})
export class RicpDetailsComponent implements OnInit {
  isLoading: boolean = false;

  isDisplay: boolean = false;
  
  isDisplayTaskLists: boolean = false;
  isLoadingTaskLists: boolean = false;
  errorMessages: string[] = [];
  error: boolean = false;
  ricp: RICP[] = []
  updateWFstatus: string = ""
  dropDownTotalRecord = 1000;
  navigateToAssignedTask = false
  ricpId: any;
  statusOptions: Param[] = [];
  users: User[] = [];
  page = environment.DefaultPage; 
  

  itemsPerPage = environment.ItemPerPage;
  totalRecords: number = 0;
  selectedEffectiveDate!: { start?: moment.Moment; end?: moment.Moment };
  selectedRequestedDate!: { start?: moment.Moment; end?: moment.Moment };
  showResultAlert = false;

  showResultAlertBox() {
    this.showResultAlert = true;
    setTimeout(() => this.showResultAlert = false, 2000);
  }

  txnType: string | null = null
  entityType: string | null = null
  entityNo: string | null = null
  calenderYear: string | null = null
  cpNo: string | null = null
  cpActId: string | null = null
  cpSectId: string | null = null
  cpSubSectId: string | null = null
  issuanceDate: Date | null = null
  expiryDate: Date | null = null
  voidDate: Date | null = null
  cancelDate: Date | null = null
  writeoffDate: Date | null = null
  cpAmount: number | null = null
  accrualAmount: number | null = null
  cpTier: number | null = null
  cpTierAmount: number | null = null
  createdDate: Date | null = null
  ricpStatus: string | null = null
  


  // //default pagination
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

    this.ricpId = history.state.ricp_id


    this.loadData()
    this.populateForm()

  }



  loadData() {
    this.isDisplay = true;
    this.isLoading = true;
    
    const url = environment.apiUrl + '/api/ricp/v1/getricp';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    const Body: any = {
      i_page: this.page.toString(),
      i_size: this.itemsPerPage.toString(),
    };


    if (this.entityType && this.entityType.trim()) {
      Body.i_entity_type = this.entityType;
    }
    

    if (this.entityNo && this.entityNo.trim()) {
      Body.i_entity_no = this.entityNo;
    }

    if (this.cpNo && this.cpNo.trim()) {
      Body.i_cp_no = this.cpNo;
    }

    if (this.issuanceDate && this.issuanceDate) {
      const selectedDate = new Date(this.issuanceDate);
      selectedDate.setDate(selectedDate.getDate() + 1);
      const formattedEffectiveDate = selectedDate.toISOString().split('T')[0];
      Body.i_dt_issuance = formattedEffectiveDate;
    }

    if (this.expiryDate && this.expiryDate) {
      const selectedDate = new Date(this.expiryDate);
      selectedDate.setDate(selectedDate.getDate() + 1);
      const formattedEffectiveDate = selectedDate.toISOString().split('T')[0];
      Body.i_dt_expiry = formattedEffectiveDate;
    }

    //number
    if (this.cpAmount && this.cpAmount) {
      Body.i_cp_amount = this.cpAmount;
    }

    if (this.accrualAmount && this.accrualAmount) {
      Body.i_accr_amt = this.accrualAmount;
    }

    if (this.cpTier && this.cpTier) {
      Body.i_cp_tier = this.cpTier;
    }

    if (this.cpTierAmount && this.cpTierAmount) {
      Body.i_tier_amt = this.cpTierAmount;
    }
    //number end


    if (this.ricpStatus && this.ricpStatus.trim()) {
      Body.i_status = this.ricpStatus;
    }



    console.log(Body);

    this.http.post(url, Body, { headers }).subscribe(
      (response: any) => {
        this.ricp = response.data.ricpList;
        if (response.data.total == 0) {
          this.totalRecords = 0;
          this.isDisplay = false;

          this.isLoading = false;
        } else {
          this.totalRecords = response.data.total;

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

    this.ricpId = this.ricpId;
    this.isDisplay = true;
    this.isLoading = true;

    const url = environment.apiUrl + '/api/ricp/v1/getricp';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    const Body: any = {
      i_page: this.page.toString(),
      i_size: 1,
      i_ricp_id: this.ricpId,
    };

    this.http.post(url, Body, { headers }).subscribe(
      (response: any) => {
        if (response.data.total === 0) {
          this.totalRecords = 0;
        } else {
          this.totalRecords = response.data.total;
          this.ricp = response.data.ricpList;
          this.txnType = this.ricp[0].txn_type;
          this.entityType = this.ricp[0].entity_type;
          this.entityNo = this.ricp[0].entity_no;
          this.calenderYear = this.ricp[0].calendar_yr;
          this.cpNo = this.ricp[0].cp_no;
          this.cpActId = this.ricp[0].cp_act_id;
          this.cpSectId = this.ricp[0].cp_sect_id;
          this.cpSubSectId = this.ricp[0].cp_sub_sect_id;
          this.issuanceDate = this.ricp[0].dt_issuance;
          this.expiryDate = this.ricp[0].dt_expiry;
          this.voidDate = this.ricp[0].dt_void;
          this.cancelDate = this.ricp[0].dt_cancel;
          this.writeoffDate = this.ricp[0].dt_writeoff;
          this.cpAmount = this.ricp[0].cp_amt;
          this.accrualAmount = this.ricp[0].accr_amt;
          this.cpTier = this.ricp[0].cp_tier;
          this.cpTierAmount = this.ricp[0].cp_tier_amt;
          this.createdDate = this.ricp[0].dt_created;
          this.ricpStatus = this.ricp[0].status;
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
