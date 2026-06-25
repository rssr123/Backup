import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Component, Inject, OnInit, ViewChild } from '@angular/core';
import { NgForm, NgModel } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { environment } from 'src/environments/environment';
import { Systemstatus } from '../../shared/enums/systemstatus';

@Component({
  selector: 'app-fms-account-update',
  templateUrl: './fms-account-update.component.html',
  styleUrls: ['./fms-account-update.component.scss'],
})
export class FMSAccountUpdateComponent implements OnInit {
  @ViewChild('fmsAccountNameRef') fmsAccountNameControl!: NgModel;
  @ViewChild('fmsAccountTypeRef') fmsAccountTypeControl!: NgModel;
  @ViewChild('fmsAccountCodeRef') fmsAccountCodeControl!: NgModel;

  errorMessages: string[] = [];
  error: boolean = false;

  fmsAcctId: number | null = null;
  acctName: String | null = null;
  acctType: String | null = null;
  acctCode: String | null = null;
  tempAcctType: string | "" = "";
  tempAcctCode: string | "" = "";
  status: String | null = null;
  modifiedBy: String | null = null;
  totalRecords: number = 0;

  isLoading: boolean = false;

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: any,
    public dialogRef: MatDialogRef<FMSAccountUpdateComponent>,
    private http: HttpClient
  ) { }

  ngOnInit(): void {
    this.defaultSetting();
    this.fmsAcctId = this.data.id;
    this.acctName = this.data.name;
    this.acctType = this.data.type;
    this.acctCode = this.data.code;
    this.tempAcctCode = this.data.code;
    this.tempAcctType = this.data.type;
    this.status = this.data.status;
    this.modifiedBy = this.data.modified_by;
  }

  onClose(): void {
    this.dialogRef.close();
  }

  closed(): void {
    this.dialogRef.close();
  }

  async update() {
    this.isLoading = true;
    this.defaultSetting();

    const isValid = await this.validation();

    //false means no error, validation passed, can insert
    if (!isValid) {
      await this.UpdateFMSAccount();
      this.dialogRef.close('updated');
    }
    this.isLoading = false;
  }

  //default setting start
  defaultSetting(): void {
    this.error = false;
    // this.errorMessage="";
    this.errorMessages = [];
  }

  //default setting end

  //insert Start
  UpdateFMSAccount(): void {
    const url = environment.apiUrl + '/api/fmsaccount/v1/updatefmsaccount';

    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    const body: any = {
      i_fms_acct_id: this.fmsAcctId,
      i_acct_nm: this.acctName,
      i_acct_type: this.acctType,
      i_acct_cd: this.acctCode,
      i_modified_by: this.modifiedBy,
      i_status: 'A'
    };

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
    } catch (error) {
      console.error(error);
    }
  }
  //insert End

  //validation start
  async checkForNoChange(currentValue: string | null, originalValue: string | null): Promise<boolean> {
    // Convert any potential null values to an empty string
    currentValue = currentValue ?? '';
    originalValue = originalValue ?? '';
    return currentValue !== originalValue;
  }

  // checkRequiredField(): Boolean {
  //   this.error = false;
  //   this.errorMessages = [];

  //   if (this.fmsAccountTypeControl.invalid) {
  //     this.error = true;
  //     this.errorMessages.push('FMS Account Type is required');
  //   }

  //   if (this.fmsAccountCodeControl.invalid) {
  //     this.error = true;
  //     this.errorMessages.push('FMS Account Code is required');
  //   }

  //   return this.error;
  // }

  checkRequiredField(): Boolean {
    this.error = false;
    this.errorMessages = [];

    // Check if FMS Account Type is empty
    if (!this.acctType || this.acctType.trim() === '') {
      this.error = true;
      this.errorMessages.push('FMS Account Type is required');
    }

    // Check if FMS Account Code is empty
    if (!this.acctCode || this.acctCode.trim() === '') {
      this.error = true;
      this.errorMessages.push('FMS Account Code is required');
    }

    return this.error;
  }


  // async checkForDuplicate(fieldName: string, fieldValue: string, errorMessage: string): Promise<boolean> {
  //   const url = environment.apiUrl + '/api/fmsaccount/v1/getfmsaccount';
  //   const headers = new HttpHeaders({
  //     Authorization: environment.authKey,
  //     'Content-Type': 'application/json',
  //   });
  //   const body: any = {
  //     i_page: '1',
  //     i_size: '9999', // Adjust the size to retrieve all records
  //     i_status: Systemstatus.Active,
  //   };

  //   try {
  //     const response: any = await this.http.post(url, body, { headers }).toPromise();
  //     const fmsAccounts: any[] = response.data;
  //     const existingCodes: string[] = fmsAccounts.map((account: any) => account.acct_cd);

  //     if (existingCodes.includes(fieldValue)) {
  //       this.error = true;
  //       this.errorMessages.push(errorMessage);
  //       return true; // Duplicate found
  //     } else {
  //       return false; // No duplicate found
  //     }
  //   } catch (error) {
  //     this.error = true;
  //     this.errorMessages.push('Internal Server Error.');
  //     console.error(error);
  //     return true; // Error occurred
  //   }

  // }

  async validation(): Promise<boolean> {
    if (this.checkRequiredField()) {
      return true;
    }

    // Ensure the values passed to checkForNoChange are treated as strings
    const acctCode = this.fmsAccountCodeControl.value ?? ''; // Convert null to empty string
    const tempAcctCode = this.tempAcctCode ?? ''; // Convert null to empty string
    const acctType = this.fmsAccountTypeControl.value ?? ''; // Convert null to empty string
    const tempAcctType = this.tempAcctType ?? ''; // Convert null to empty string

    // Check for changes and duplicates for FMS Account Type
    // if (await this.checkForNoChange(acctCode, tempAcctCode)) {
    //   if (await this.checkForDuplicate('i_acct_cd', acctCode, 'FMS Account Code is duplicate')) {
    //     return true;
    //   }
    // }

    return false; // All checks passed, no duplicates
  }
  //validation end

  //form handle before submit start
  handleFormSubmit(form: NgForm) {
    if (form.valid) {
      this.update();
    } else {
      Object.values(form.controls).forEach((control) => {
        control.markAsTouched();
      });
    }
  }
  //form handle before submit end
}
