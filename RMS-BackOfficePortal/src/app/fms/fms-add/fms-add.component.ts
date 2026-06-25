import { Component, OnInit, ViewChild } from '@angular/core';
import { MFT } from '../../core/models/MFT.interface';
import { NgForm, NgModel } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { environment } from 'src/environments/environment';
import { GlobalService } from 'src/app/shared/global.service';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-fms-add',
  templateUrl: './fms-add.component.html',
  styleUrls: ['./fms-add.component.scss'],
})
export class FmsAddComponent implements OnInit {

  @ViewChild('fmsCodeRef') fmsCodeControl!: NgModel;

  errorMessages: string[] = [];
  error: boolean = false;
  fmsCode: String | null = null;
  totalRecords: number = 0;
  isLoading: boolean = false;

  constructor(
    public dialogRef: MatDialogRef<FmsAddComponent>,
    private http: HttpClient,
    private translate: TranslateService,
    private globalService: GlobalService
  ) {
    this.translate.setDefaultLang(this.globalService.getGlobalValue()); // default language
    this.translate.use(this.globalService.getGlobalValue());
  }

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
      this.InsertFmsCode();

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

  InsertFmsCode(): void {
    const url = environment.apiUrl + '/api/fms/v1/addfms';

    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    const body: any = {
      i_fms_cd: this.fmsCode
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
  
    if (this.fmsCodeControl.invalid) {
      this.error = true;
      this.errorMessages.push('FMS Code is required');
    }
  
    return this.error;
  }
  
  async checkForDuplicate(fieldName: string, fieldValue: string, errorMessage: string): Promise<boolean> {
    const url = environment.apiUrl + '/api/fms/v1/getfms';
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });
    const body: any = {
      i_page: '1',
      i_size: '1',
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
  
    // Check for duplicate Fms code
    if (await this.checkForDuplicate("i_fms_cd", this.fmsCodeControl.value ?? '', 'FMS Code is duplicate')) {
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

  
}
