import { Component, OnInit, ViewChild } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { NgForm, NgModel } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { environment } from 'src/environments/environment';

import { Systemstatus } from '../../shared/enums/systemstatus';
import { BranchCodeCounterList } from 'src/app/core/models/branch-code-counter-list.interface';

@Component({
  selector: 'app-branch-code-counter-add',
  templateUrl: './branch-code-counter-add.component.html',
  styleUrls: ['./branch-code-counter-add.component.scss']
})
export class BranchCodeCounterAddComponent implements OnInit {
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

  @ViewChild('counterIDRef') counterIDControl!: NgModel;
  @ViewChild('terminalIDRef') terminalIDControl!: NgModel;
  @ViewChild('counterIPRef') counterIPControl!: NgModel;
  @ViewChild('branchIDRef') branchIDControl!: NgModel;

  errorMessages: string[] = [];
  error: boolean = false;

  branchCodeCounterID: Number | null = null;
  counterID: String | null = null;
  terminalID: String | null = null;
  counterIP: String | null = null;
  branchID: Number | null = null;

  model2: BranchCodeCounterList[] = [];
  branchIDCheck = false;

  totalRecords: number = 0;
  isLoading: boolean = false;

  constructor(
    public dialogRef: MatDialogRef<BranchCodeCounterAddComponent>,
    private http: HttpClient
  ) { }

  ngOnInit(): void {
    this.defaultSetting();
    this.loadCodes();
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

    if (this.branchID === null) {
      this.branchIDCheck = true;
      this.isLoading = false;
      return;
    }

    //false means no error, validation passed, can insert
    if (!isValid) {
      // this.dialogRef.close();
      this.InsertBranchCodeCounter();

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

  onBranchCodeChange(event: any): void {
    this.branchID = event.target.value;
  }

  //insert Start
  InsertBranchCodeCounter(): void {
    const url = environment.apiUrl + '/api/bcc/v1/addbranchcodecounter';

    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    const body: any = {
      // i_page: '1',
      // i_size: '1',
      i_counter_id: this.counterID,
      i_terminal_id: this.terminalID,
      i_counter_ip: this.counterIP,
      i_bcm_id: this.branchID
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
  checkRequiredField(): Boolean {
    if (this.counterIDControl.invalid) {
      this.error = true;
      this.errorMessages.push('Counter ID is required');
    }

    if (this.terminalIDControl.invalid) {
      this.error = true;
      this.errorMessages.push('Terminal ID is required');
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
    } else {
      return false;
    }
  }

  async validation(): Promise<boolean> {
    // if(this.ValidationRequiredField()){
    //   return true;
    // }

    if (!this.counterID) {
      // Form is not valid, you can handle this case or simply return
      return true;
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
    };

    try {
      const response: any = await this.http
        .post(url, body, { headers })
        .toPromise();

      if (response.header.statusCode === '01') {
        return false;
      } else {
        this.error = true;
        this.errorMessages.push('Branch Code Counter ID is duplicate.');
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
