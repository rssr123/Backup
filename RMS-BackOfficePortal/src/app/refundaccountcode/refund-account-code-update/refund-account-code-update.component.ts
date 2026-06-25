import { Component, Inject, OnInit, ViewChild } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { NgForm, NgModel } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { environment } from 'src/environments/environment';
import { Systemstatus } from '../../shared/enums/systemstatus';

@Component({
  selector: 'app-refund-account-code-update',
  templateUrl: './refund-account-code-update.component.html',
  styleUrls: ['./refund-account-code-update.component.scss']
})
export class RefundAccountCodeUpdateComponent implements OnInit {

  onKeyDown(event: KeyboardEvent): void {
    const allowedKeys = ['Backspace', 'Delete', 'ArrowLeft', 'ArrowRight', '.'];
    if (allowedKeys.includes(event.code)) {
      return;
    }
    if (!isNaN(Number(event.key)) || event.key === '.') {
      return;
    }
    event.preventDefault();
  }

  @ViewChild('refundAccountIDRef') refundAccountIDControl!: NgModel;
  @ViewChild('refundAccountCodeRef') refundAccountCodeControl!: NgModel;
  @ViewChild('refundAccountDescriptionRef') refundAccountDescriptionControl!: NgModel;

  errorMessages: string[] = [];
  error: boolean = false;

  refundAccountID: number | null = null;
  refundAccountCode: String | null = null;
  refundAccountDescription: String | null = null;
  tempRefundAccountID: number | null = null;
  tempRefundAccountCode: String | null = null;
  tempRefundAccountDescription: String | null = null;
  
  totalRecords: number = 0;

  isLoading: boolean = false;

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: any,
    public dialogRef: MatDialogRef<RefundAccountCodeUpdateComponent>,
    private http: HttpClient
  ) { }

  ngOnInit(): void {
    this.defaultSetting();
    this.refundAccountID = this.data.id;
    this.refundAccountCode = this.data.cd;
    this.refundAccountDescription = this.data.de;
    this.tempRefundAccountID = this.data.id;
    this.tempRefundAccountCode = this.data.cd;
    this.tempRefundAccountDescription = this.data.de;
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
      // this.dialogRef.close();
      this.UpdateRefundAccountCode();

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
  UpdateRefundAccountCode(): void {
    const url = environment.apiUrl + '/api/rac/v1/updrefundaccountcode';

    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    const body: any = {
      i_rtt_acc_id: this.refundAccountID,
      i_acc_cd: this.refundAccountCode,
      i_acc_desc: this.refundAccountDescription
    };

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
  checkRequiredField(): Boolean {
    if (this.refundAccountIDControl.invalid) {
      this.error = true;
      this.errorMessages.push('Refund Account ID is required');
    }

    if (this.refundAccountCodeControl.invalid) {
      this.error = true;
      this.errorMessages.push('Refund Account Code is required');
    }

    if (this.refundAccountDescriptionControl.invalid) {
      this.error = true;
      this.errorMessages.push('Refund Account Description is required');
    }

    if (this.error) {
      return true;
    } 
    
    else {
      return false;
    }
  }

  async validation(): Promise<boolean> {
    if (!this.refundAccountCode) {
      // Form is not valid, you can handle this case or simply return
      return true;
    }

    if (this.tempRefundAccountCode == this.refundAccountCode) {
      return false;
    }

    const url = environment.apiUrl + '/api/rac/v1/getrefundaccountcode';

    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    const body: any = {
      i_page: '1',
      i_size: '1',
      i_acc_cd: this.refundAccountCode,
      i_status: Systemstatus.Active,
    };

    try {
      const response: any = await this.http
        .post(url, body, { headers })
        .toPromise();

      if (response.header.statusCode === '01') {
        return false;
      } else {
        this.error = true;
        this.errorMessages.push('Refund Account Code is duplicate.');
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

}
