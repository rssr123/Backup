import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Component, OnInit, ViewChild } from '@angular/core';
import { NgForm, NgModel } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { environment } from 'src/environments/environment';
import { Systemstatus } from '../../shared/enums/systemstatus';
import { SourceSystemCode } from 'src/app/core/models/entity';

@Component({
  selector: 'app-fee-group-add',
  templateUrl: './fee-group-add.component.html',
  styleUrls: ['./fee-group-add.component.scss'],
})
export class FeeGroupAddComponent implements OnInit {
  @ViewChild('feeGroupNameENRef') feeGroupNameENControl!: NgModel;
  @ViewChild('feeGroupNameBMRef') feeGroupNameBMControl!: NgModel;
  @ViewChild('ssCdRef') ssCdControl!: NgModel;
  @ViewChild('ssfeeGroupIdRef') ssfeeGroupIdControl!: NgModel;

  errorMessages: string[] = [];
  error: boolean = false;
  feeGroupNameEN: String | null = null;
  feeGroupNameBM: String | null = null;
  ssCd: String | null = null;
  ssFeeGroupId: number =0;
  totalRecords: number = 0;
  isLoading: boolean = false;
  page = environment.DefaultPage;
  itemsPerPage = environment.ItemPerPage;
  sourceSystemCodeOptions: SourceSystemCode[] = [];

  constructor(
    public dialogRef: MatDialogRef<FeeGroupAddComponent>,
    private http: HttpClient
  ) {}

  ngOnInit(): void {
    this.defaultSetting();
    this.loadSourceSystem();
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
      this.InsertFeeGroup();

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
  InsertFeeGroup(): void {
    const url = environment.apiUrl + '/api/fg/v1/addfeegroup';

    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    const body: any = {
      i_page: '1',
      i_size: '1',
      i_fee_grp_nm_en: this.feeGroupNameEN,
      i_fee_grp_nm_bm: this.feeGroupNameBM,
      i_ss_cd: this.ssCd,
      i_ss_fee_grp_id: this.ssFeeGroupId
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
  async checkRequiredField(): Promise<boolean> {
    this.error = false;
    this.errorMessages = [];
  
    if (this.feeGroupNameENControl.invalid) {
      this.error = true;
      this.errorMessages.push('Fee Group Name (EN) is required');
    }
  
    if (this.feeGroupNameBMControl.invalid) {
      this.error = true;
      this.errorMessages.push('Fee Group Name (BM) is required');
    }

    if (this.ssCdControl.invalid) {
      this.error = true;
      this.errorMessages.push('Source System is required');
    }
    if (this.ssfeeGroupIdControl.invalid) {
      this.error = true;
      this.errorMessages.push('Source System Fee Group ID is required');
    }
  
    return this.error;
  }
  
  async checkForDuplicate(fieldName: string, fieldValue: string, errorMessage: string): Promise<boolean> {
    const url = environment.apiUrl + '/api/fg/v1/getfeegroup';
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });
    const body: any = {
      i_page: '1',
      i_size: '1',
      i_status: Systemstatus.Active
    };
    body[fieldName] = fieldValue;
  
    try {
      const response: any = await this.http.post(url, body, { headers }).toPromise();
      if (response.header.statusCode === '01') {
        return false; // No duplicate found
      } else {
        this.error = true;
        this.errorMessages.push(errorMessage);
        return true; // Duplicate found
      }
    } catch (error) {
      this.error = true;
      this.errorMessages.push('Internal Server Error.');
      console.error(error);
      return true; // Error occurred
    }
  }
  
  async validation(): Promise<boolean> {
    if (await this.checkRequiredField()) {
      return true;
    }
  
    // Check for duplicate Fee Group Name (EN)
    if (await this.checkForDuplicate('i_fee_grp_nm_en', this.feeGroupNameENControl.value ?? '', 'Fee Group Name (EN) is duplicate')) {
      return true;
    }
  
    // Check for duplicate Fee Group Name (BM)
    if (await this.checkForDuplicate('i_fee_grp_nm_bm', this.feeGroupNameBMControl.value ?? '', 'Fee Group Name (BM) is duplicate')) {
      return true;
    }

    // Check for duplicate Source System Fee Group ID
    if (await this.checkForDuplicate('i_ss_fee_grp_id', this.ssfeeGroupIdControl.value ?? '', 'Source System Fee Group ID is duplicate')) {
      return true;
    }
  
    return false; // All checks passed, no duplicates
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
  //form handle before submit end

  loadSourceSystem() {
    const url = environment.apiUrl + '/api/rms/v1/getsourcesystem';
    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json'
    });
    // Create the request body with your form data
    const requestBody = {
      i_page: 1,
      i_size: this.itemsPerPage,
      i_ss_id: null,
      i_ss_cd: null,
      i_ss_nm: null,
      i_modified_by: null,
      i_dt_modified_fr: null,
      i_dt_modified_to: null,
      i_status: Systemstatus.Active
    };
    // Send an HTTP POST request to the API
    this.http.post(url, requestBody, { headers }).subscribe(
      (response: any) => {
        if (response.data.length === 0) {
          console.error('Invalid response format:', response);
        }
        else {
          this.sourceSystemCodeOptions = response.data;
          // this.sourceSystemCodeOptions=this.sourceSystemCodeOptions.concat(response.data)
          // Handle a successful response (e.g., show a success message)
        }
      },
      (error) => {
        console.error('There was an error retrieving the source system code:', error);
        // Handle API errors (e.g., show an error message)
      }
    );
  }
}
