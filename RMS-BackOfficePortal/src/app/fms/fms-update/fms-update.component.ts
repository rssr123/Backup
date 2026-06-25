import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Component, Inject, OnInit, ViewChild } from '@angular/core';
import { NgForm, NgModel } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { environment } from 'src/environments/environment';

@Component({
  selector: 'app-fms-update',
  templateUrl: './fms-update.component.html',
  styleUrls: ['./fms-update.component.scss']
})
export class FmsUpdateComponent implements OnInit {
    @ViewChild('fmsCodeRef') fmsCodeControl!: NgModel;
  
    errorMessages: string[] = [];
    error: boolean = false;
  
    fmsId: String | null = null;
    fmsCode: String | null = null;
    tempFmsCode: string | "" = ""; 
    // status_en: String | null = null;
    is_active: String | null = null;
    totalRecords: number = 0;
  
    isLoading: boolean = false;
  
    constructor(
      @Inject(MAT_DIALOG_DATA) public data: any,
      public dialogRef: MatDialogRef<FmsUpdateComponent>,
      private http: HttpClient
    ) {}
  
    ngOnInit(): void {
      this.defaultSetting();
      this.fmsId = this.data.id;
      this.fmsCode = this.data.code;
      this.tempFmsCode = this.data.code;
      // this.status_en= this.data.status;
      this.is_active = this.data.active;
      // this.status_en = this.data.status;
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
        this.UpdateFms();
  
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
    UpdateFms(): void {
      const url = environment.apiUrl + '/api/fms/v1/updatefms';
  
      const headers = new HttpHeaders({
        Authorization: environment.authKey,
        'Content-Type': 'application/json',
      });
  
      const body: any = {
        i_fms_id: this.fmsId,
        i_fms_cd: this.fmsCode,
        // i_status: this.status_en,
        i_is_active: this.is_active
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
    async checkForNoChange(currentValue: string | null, originalValue: string | null): Promise<boolean> {
      // Convert any potential null values to an empty string
      currentValue = currentValue ?? '';
      originalValue = originalValue ?? '';
      return currentValue !== originalValue;
    }
  
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
    
    // Ensure the values passed to checkForNoChange are treated as strings
    const fmsCode = this.fmsCodeControl.value ?? ''; // Convert null to empty string
    const tempFmsCode = this.tempFmsCode ?? ''; // Convert null to empty string
   
    // Check for changes and duplicates for Fee Group Name (EN)
    if (await this.checkForNoChange(fmsCode, tempFmsCode)) {
      if (await this.checkForDuplicate('i_fms_cd', fmsCode, 'FMS Code is duplicate')) {
        return true;
      }
    }
  
    
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
