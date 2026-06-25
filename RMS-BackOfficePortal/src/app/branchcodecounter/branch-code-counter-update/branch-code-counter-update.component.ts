import { Component, Inject, OnInit, ViewChild } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { NgForm, NgModel } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { environment } from 'src/environments/environment';
import { Systemstatus } from '../../shared/enums/systemstatus';

import { BranchCodeCounterList } from 'src/app/core/models/branch-code-counter-list.interface';

@Component({
  selector: 'app-branch-code-counter-update',
  templateUrl: './branch-code-counter-update.component.html',
  styleUrls: ['./branch-code-counter-update.component.scss']
})
export class BranchCodeCounterUpdateComponent implements OnInit {
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

  @ViewChild('branchCodeCounterIDRef') branchCodeCounterIDControl!: NgModel;
  @ViewChild('counterIDRef') counterIDControl!: NgModel;
  @ViewChild('terminalIDRef') terminalIDControl!: NgModel;
  @ViewChild('counterIPRef') counterIPControl!: NgModel;
  @ViewChild('branchIDRef') branchIDControl!: NgModel;
  @ViewChild('branchCodeRef') branchCodeControl!: NgModel;

  errorMessages: string[] = [];
  error: boolean = false;

  branchCodeCounterID: number | null = null;
  counterID: String | null = null;
  terminalID: String | null = null;
  counterIP: String | null = null;
  branchID: Number | null = null;

  tempBranchCodeCounterID: number | null = null;
  tempCounterID: String | null = null;
  tempTerminalID: String | null = null;
  tempCounterIP: String | null = null;
  tempBranchID: Number | null = null;

  model2: BranchCodeCounterList[] = [];
  
  totalRecords: number = 0;

  isLoading: boolean = false;

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: any,
    public dialogRef: MatDialogRef<BranchCodeCounterUpdateComponent>,
    private http: HttpClient
  ) { }

  ngOnInit(): void {
    this.defaultSetting();
    this.branchCodeCounterID = this.data.id;
    this.counterID = this.data.counter_id;
    this.terminalID = this.data.terminal_id;
    this.counterIP = this.data.counter_ip;
    this.tempCounterID = this.data.counter_id;
    this.tempTerminalID = this.data.terminal_id;
    this.tempCounterIP = this.data.counter_ip;
    this.loadCodes();
    this.branchID = this.data.bcm_id;
    this.tempBranchID = this.data.bcm_id;
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

  loadCodes() {
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    const url2 = environment.apiUrl + '/api/helper/v1/getbranchcodecounterlist';

    const body2 = {
      i_status: Systemstatus.Active
    }

    this.http.post(url2, body2, { headers }).subscribe(
      (response: any) => {
        this.model2 = response.data;
      },
      (error) => {
        console.error(error);
      }
    )
  }

  //insert Start
  UpdateRefundAccountCode(): void {
    const url = environment.apiUrl + '/api/bcc/v1/updbranchcodecounter';

    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    const body: any = {
      i_bcc_id: this.branchCodeCounterID,
      i_counter_id: this.counterID,
      i_terminal_id: this.terminalID,
      i_counter_ip: this.counterIP,
      i_bcm_id: this.branchID
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
    if (this.branchCodeCounterIDControl.invalid) {
      this.error = true;
      this.errorMessages.push('Branch Code Counter ID is required');
    }

    if (this.counterIDControl.invalid) {
      this.error = true;
      this.errorMessages.push('Counter ID is required');
    }

    if (this.terminalIDControl.invalid) {
      this.error = true;
      this.errorMessages.push('Terminal ID Description is required');
    }

    if (this.counterIPControl.invalid) {
      this.error = true;
      this.errorMessages.push('Counter IP is required');
    }

    if (this.branchIDControl.invalid) {
      this.error = true;
      this.errorMessages.push('Branch Code is required');
    }

    if (this.error) {
      return true;
    } 
    
    else {
      return false;
    }
  }

  async validation(): Promise<boolean> {
    if (!this.counterID) {
      // Form is not valid, you can handle this case or simply return
      return true;
    }

    if (this.tempCounterID == this.counterID) {
      return false;
    }

    const url = environment.apiUrl + '/api/bcc/v1/getbranchcodecounter';

    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    const body: any = {
      i_page: '1',
      i_size: '1',
      i_counter_id: this.counterID,
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
        this.errorMessages.push('Counter ID is duplicate.');
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

  onBranchCodeChange(event: any): void {
    this.branchID = event.target.value;
  }

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
