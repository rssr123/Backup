import { Component, Inject, OnInit , ChangeDetectorRef} from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { fadeInOut } from '../../shared/animation';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Router } from '@angular/router';
import { MAT_DIALOG_DATA, MatDialog, MatDialogRef } from '@angular/material/dialog';
import { ParamService } from '../../core/services/param.service';
import { GlobalService } from 'src/app/shared/global.service';
import { AuthService } from 'src/app/core/services/auth.service';
import { environment } from 'src/environments/environment';
import { perm } from 'src/permissions/perm';

@Component({
  selector: 'app-counter-balancing-edit',
  templateUrl: './counter-balancing-edit.component.html',
  styleUrls: ['./counter-balancing-edit.component.scss'],
  animations: [fadeInOut],
})
export class CounterBalancingEditComponent implements OnInit{
  constructor(
    private http: HttpClient,
    private router: Router,
    public dialog: MatDialog,
    public dialogRef: MatDialogRef<CounterBalancingEditComponent>,
    private ParamService: ParamService,
    private translateService: TranslateService,
    private globalService: GlobalService,
    private authService: AuthService,
    @Inject(MAT_DIALOG_DATA) public data: any,
  ) {
    this.translateService.setDefaultLang(this.globalService.getGlobalValue());
    this.translateService.use(this.globalService.getGlobalValue());
  }

  modelParam: any[] = [];
  modelBankParam: any[] = [];
  defaultDropdownValue: any;
  defaultBankDropdownValue: any;
  chequeTable: boolean = false;
  bdTable: boolean = false;
  moTable: boolean = false;

  isDisplay: boolean = false;
  isLoading: boolean = false;
  showResultAlert = false;
  isEmptyResult = false;

  amount: number = 0.00;

  permOTCPymtMode = perm.OTC_BALANCING_Update_PaymentMode;
  permOTCPymtModeAllow = "";

  ngOnInit(): void{

    if (this.data.item.che_date) {
      // Convert string to Date
      this.data.item.che_date = new Date(this.data.item.che_date);
    }
    else if(this.data.item.bd_date){
      // Convert string to Date
      this.data.item.bd_date = new Date(this.data.item.bd_date);
    }

    if(this.data.item.detail_type.trim() === 'cheque'){
      this.amount = this.data.item.che_amt
    }
    else if(this.data.item.detail_type.trim() === 'bank draft'){
      this.amount = this.data.item.bd_amt
    }
    else if(this.data.item.detail_type.trim() === 'money order'){
      this.amount = this.data.item.mo_amt
    }

    this.checkDisplayTable();
    this.loadPaymentModeParam();
    this.loadBankParam();
  }

  showResultAlertBox() {
    this.showResultAlert = true;
    setTimeout(() => (this.showResultAlert = false), 2000);
  }

  isPhoneInvalid: boolean = false;

  checkPhoneLength(): void {
    const phoneLength = this.data.item.mo_contact_no?.length || 0;
    this.isPhoneInvalid = phoneLength < 10 || phoneLength > 15;
  }

  allowOnlyNumbers(event: KeyboardEvent): void {
    const pattern = /^[0-9]$/;
    const inputChar = String.fromCharCode(event.charCode);

    if (!pattern.test(inputChar)) {
      event.preventDefault(); // blocks the input
    }
  }

  cancel(): void{
    this.dialogRef.close(null); 
  }

  loadPaymentModeParam(){
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    this.isDisplay = true;
    this.isLoading = true;
    const url = environment.apiUrl + '/api/rms/v1/getparam';

    const Body: any = {
      i_page : 1,
      i_size : 20,
      i_param_cd : null,
      i_param_grp_nm : 'OTC-PaymentMode'
    };

    this.http.post(url, Body, { headers }).subscribe(
      (response: any) => {
        this.modelParam = response.data;
        if (response.data.length == 0) {
          this.isDisplay = true;
          this.isEmptyResult = true;
          this.showResultAlertBox();
          this.isLoading = false;
        } else {
          this.isEmptyResult = false;
          this.isLoading = false;

          this.modelParam = this.modelParam.filter((item) => ['cheque','bank draft','money order'].includes(item.nm_en.toLowerCase().trim()));
          this.defaultDropdownValue = this.modelParam.find(option => option.nm_en.toLowerCase().trim() === this.data.item.detail_type.toLowerCase().trim());
        }
      },
      (error) => {
        console.error(error);
        this.isLoading = false;
        // Handle errors here
      }
    );
  }

  loadBankParam(){
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    this.isDisplay = true;
    this.isLoading = true;
    const url = environment.apiUrl + '/api/rms/v1/getparam';

    const Body: any = {
      i_page : 1,
      i_size : 40,
      i_param_cd : null,
      i_param_grp_nm : 'Common-BankName'
    };

    this.http.post(url, Body, { headers }).subscribe(
      (response: any) => {
        this.modelBankParam = response.data;
        if (response.data.length == 0) {
          this.isDisplay = true;
          this.isEmptyResult = true;
          this.showResultAlertBox();
          this.isLoading = false;
        } else {
          this.isEmptyResult = false;
          this.isLoading = false;

          if(this.data.item.detail_type.trim().toLowerCase() === 'cheque'){
            this.defaultBankDropdownValue = this.modelBankParam.find(option => option.nm_en.toLowerCase().trim() === this.data.item.che_bank_nm.toLowerCase().trim());
          }
          else if(this.data.item.detail_type.trim().toLowerCase() === 'bank draft'){
            this.defaultBankDropdownValue = this.modelBankParam.find(option => option.nm_en.toLowerCase().trim() === this.data.item.bd_bank_nm.toLowerCase().trim());
          }
        }
      },
      (error) => {
        console.error(error);
        this.isLoading = false;
        // Handle errors here
      }
    );
  }

  checkDisplayTable(){
    if(this.data.item.detail_type.trim().toLowerCase() === 'cheque'){
      this.chequeTable = true;
      this.bdTable = false;
      this.moTable = false;
      this.data.item.n_detail_type = 'cheque';
    }
    else if(this.data.item.detail_type.trim().toLowerCase() === 'bank draft'){
      this.chequeTable = false;
      this.bdTable = true;
      this.moTable = false;
      this.data.item.n_detail_type = 'bank draft';
    }
    else if(this.data.item.detail_type.trim().toLowerCase() === 'money order'){
      this.chequeTable = false;
      this.bdTable = false;
      this.moTable = true;
      this.data.item.n_detail_type = 'money order';
    }
    else{
      this.chequeTable = false;
      this.bdTable = true;
      this.moTable = false;
    }
  }

  changePMT(){
    if(this.defaultDropdownValue?.nm_en.trim().toLowerCase() === 'cheque'){
      this.chequeTable = true;
      this.bdTable = false;
      this.moTable = false;
      this.data.item.n_detail_type = 'cheque';
    }
    else if(this.defaultDropdownValue?.nm_en.trim().toLowerCase() === 'bank draft'){
      this.chequeTable = false;
      this.bdTable = true;
      this.moTable = false;
      this.data.item.n_detail_type = 'bank draft';
    }
    else if(this.defaultDropdownValue?.nm_en.trim().toLowerCase() === 'money order'){
      this.chequeTable = false;
      this.bdTable = false;
      this.moTable = true;
      this.data.item.n_detail_type = 'money order';
    }
  }

  submit():void{

    if(this.data.item.detail_type.trim().toLowerCase() == 'money order'){
      //Bank Draft
      if(this.data.item.n_detail_type.trim().toLowerCase() != 'money order' && this.data.item.n_detail_type.trim().toLowerCase() == 'bank draft'){
        this.data.item.bd_bank_nm = this.defaultBankDropdownValue?.nm_en;
        this.data.item.bd_amt = this.amount;
      }
      // Cheque
      else if(this.data.item.n_detail_type.trim().toLowerCase() != 'money order' && this.data.item.n_detail_type.trim().toLowerCase() == 'cheque'){
        this.data.item.che_bank_nm = this.defaultBankDropdownValue?.nm_en;
        this.data.item.che_amt = this.amount;
      }
    }
    else if(this.data.item.n_detail_type.trim().toLowerCase() == 'bank draft'){
      this.data.item.bd_bank_nm = this.defaultBankDropdownValue?.nm_en;
      this.data.item.bd_amt = this.amount;
    }
    else if(this.data.item.n_detail_type.trim().toLowerCase() == 'cheque'){
      this.data.item.che_bank_nm = this.defaultBankDropdownValue?.nm_en;
      this.data.item.che_amt = this.amount;
    }
    else if(this.data.item.n_detail_type.trim().toLowerCase() == 'money order'){
      this.data.item.mo_amt = this.amount;
    }

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    this.isDisplay = true;
    this.isLoading = true;
    const url = environment.apiUrl + '/api/otcbalancingreq/v1/updotcbalpymtmode';

    this.http.post(url, this.data.item, { headers }).subscribe(
      (response: any) => {

        if (response.data.length == 0) {
          this.isDisplay = true;
          this.isEmptyResult = true;
          this.showResultAlertBox();
          this.isLoading = false;
          
        } else {
          this.isEmptyResult = false;
          this.isLoading = false;

          this.dialogRef.close(this.data.item);
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
