import { Component } from '@angular/core';
import { Param, User } from '../../core/models/entity';
import { environment } from 'src/environments/environment';
import moment from 'moment';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { formatDate } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { RIPL } from '../../core/models/ri-pl.interface';
import { AuthService } from '../../core/services/auth.service';


@Component({
  selector: 'app-ri-pl-detail',
  templateUrl: './ri-pl-detail.component.html',
  styleUrls: ['./ri-pl-detail.component.scss']
})
export class RIPLDetailComponent {
  username = this.authService.username;
  
  isDisplay: boolean = false;
  isLoading: boolean = false;
  isDisplayTaskLists: boolean = false;
  isLoadingTaskLists: boolean = false;
  errorMessages: string[] = [];
  error: boolean = false;
  ripl: RIPL[] = []
  updateWFstatus: string = ""
  dropDownTotalRecord = 1000;
  navigateToAssignedTask = false

  statusOptions: Param[] = [];
  users: User[] = [];
  page = environment.DefaultPage; riplId: any;
  itemsPerPage = environment.ItemPerPage;
  totalRecords: number = 0;
  selectedEffectiveDate!: { start?: moment.Moment; end?: moment.Moment };
  selectedRequestedDate!: { start?: moment.Moment; end?: moment.Moment };
  showResultAlert = false;

  showResultAlertBox() {
    this.showResultAlert = true;
    setTimeout(() => this.showResultAlert = false, 2000);
  }

  idId:BigInteger | null = null;
  transactionType: String | null = null;
  entityType: String | null = null;
  entityNo: String | null = null;
  calendarYear: Date | null = null;
  dueDate: Date | null = null;
  impairDate: Date | null = null;
  writeOffDate: Date | null = null;
  companyType: String | null = null;
  accrualAmount: number | null = null;
  receiptNo: String | null = null;
  createdDate: Date | null = null;
  riplStatus: String | null = null;

  //default pagination
  selectedValue = environment.dropdownOptions[0];
  dropdownOptions = environment.dropdownOptions;

  LoadData(singleItem: number) {
    this.selectedValue = singleItem;
    this.itemsPerPage = this.selectedValue;
    this.loadData();
  }

  constructor(private route: ActivatedRoute, private http: HttpClient, private router: Router, public authService: AuthService) { }


  ngOnInit() {

    this.selectedEffectiveDate = {
      start: moment().subtract(1, 'month'),
      end: moment(),
    };

    this.selectedRequestedDate = {
      start: moment().subtract(1, 'month'),
      end: moment(),
    };

    this.riplId = history.state.ripl_id


    this.loadData()
    this.populateForm()

  }



  loadData() {
    this.isDisplay = true;
    this.isLoading = true;
    const url = environment.apiUrl + '/api/ripl/v1/sp_getRIPL';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    const Body: any = {
      i_page: this.page.toString(),
      i_size: this.itemsPerPage.toString(),
    };

    console.log(this.transactionType);
    if (this.transactionType && this.transactionType.trim()) {
      Body.i_txn_type = this.transactionType;
    }

    if (this.entityType && this.entityType.trim()) {
      Body.i_entity_type = this.entityType;
    }

    if (this.entityNo && this.entityNo.trim()) {
      Body.i_entity_no = this.entityNo;
    }

    if (this.calendarYear) {
      Body.i_calendar_yr = formatDate(this.calendarYear, 'YYYY', 'en');
    }
    
    if (this.dueDate) {
      Body.i_dt_due = formatDate(this.dueDate, 'YYYY-MM-dd', 'en');
      
    }

    if (this.impairDate) {
      Body.i_dt_impair = formatDate(this.impairDate, 'YYYY-MM-dd', 'en');
      
    }

    if (this.writeOffDate) {
      Body.i_dt_writeoff = formatDate(this.writeOffDate, 'YYYY-MM-dd', 'en');
      
    }

    if (this.companyType && this.companyType.trim()) {
      Body.i_ripl_ctype = this.companyType;
    }

    if (this.accrualAmount && this.accrualAmount.toString().trim()) {
      Body.i_accr_amt = this.companyType;
    }

    if (this.receiptNo && this.receiptNo.trim()) {
      Body.i_rcpt_no = this.receiptNo;
    }

    if (this.createdDate) {
      Body.i_dt_created = formatDate(this.createdDate, 'YYYY-MM-dd', 'en');
      
    }

    if (this.riplStatus && this.riplStatus.trim()) {
      Body.i_status = this.riplStatus;
    }


    console.log(Body);

    this.http.post(url, Body, { headers }).subscribe(
      (response: any) => {
        this.ripl = response.data;
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

    this.riplId = this.riplId;
    this.isDisplay = true;
    this.isLoading = true;

    const url = environment.apiUrl + '/api/ripl/v1/sp_getRIPL';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    const Body: any = {
      i_page: this.page.toString(),
      i_size: 1,
      i_ripl_id: this.riplId,
    };

    console.log("Start");

    this.http.post(url, Body, { headers }).subscribe(
      (response: any) => {
        if (response.data.length === 0) {
          console.log("Start1");
          this.totalRecords = 0;
        } else {
          this.totalRecords = response.data[0].total;
          this.ripl = response.data;
          this.transactionType = this.ripl[0].i_txn_type;
          this.entityType = this.ripl[0].i_entity_type;
          this.entityNo = this.ripl[0].i_entity_no;
          this.calendarYear = this.ripl[0].i_calendar_yr;
          this.dueDate = this.ripl[0].i_dt_due;
          this.impairDate = this.ripl[0].i_dt_impair;
          this.writeOffDate = this.ripl[0].i_dt_writeoff;
          this.companyType = this.ripl[0].i_ripl_ctype;
          this.accrualAmount = this.ripl[0].i_accr_amt;
          this.receiptNo = this.ripl[0].i_rcpt_no;
          this.createdDate = this.ripl[0].i_dt_created;
          this.riplStatus = this.ripl[0].i_status;
          this.isLoading = false;
          console.log("Start2");
          console.log(response.data);
        }
      },
      (error) => {
        console.error(error);
        this.isLoading = false;
        // Handle errors here
      }
    );

  }

  back(): void{
    window.history.back();
  }
}
