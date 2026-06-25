import { Component, OnInit, ViewChild } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { NgForm, NgModel } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { environment } from 'src/environments/environment';

@Component({
  selector: 'app-refund-account-code-add',
  templateUrl: './refund-account-code-add.component.html',
  styleUrls: ['./refund-account-code-add.component.scss']
})
export class RefundAccountCodeAddComponent implements OnInit {
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

  @ViewChild('refundAccountCodeRef') refundAccountCodeControl!: NgModel;
  @ViewChild('refundAccountDescriptionRef') refundAccountDescriptionControl!: NgModel;

  errorMessages: string[] = [];
  error: boolean = false;

  refundAccountID: number | null = null;
  refundAccountCode: String | null = null;
  refundAccountDescription: String | null = null;

  totalRecords: number = 0;
  isLoading: boolean = false;

  constructor(
    public dialogRef: MatDialogRef<RefundAccountCodeAddComponent>,
    private http: HttpClient
  ) { }

  ngOnInit(): void {
    this.defaultSetting();
  }

  onClose(): void {
    this.dialogRef.close();
  }

  closed(): void {
    this.dialogRef.close();
  }

  async submit() {
    this.isLoading = true;
    this.defaultSetting();

    const isValid = await this.validation();

    //false means no error, validation passed, can insert
    if (!isValid) {
      // this.dialogRef.close();
      this.InsertRefundAccountCode();

      this.dialogRef.close('inserted');
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
  InsertRefundAccountCode(): void {
    const url = environment.apiUrl + '/api/rac/v1/addrefundaccountcode';

    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    const body: any = {
      // i_page: '1',
      // i_size: '1',
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
    } else {
      return false;
    }
  }

  async validation(): Promise<boolean> {
    // if(this.ValidationRequiredField()){
    //   return true;
    // }

    if (!this.refundAccountCode) {
      // Form is not valid, you can handle this case or simply return
      return true;
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
      this.submit();
    } else {
      Object.values(form.controls).forEach((control) => {
        control.markAsTouched();
      });
    }
  }
}
