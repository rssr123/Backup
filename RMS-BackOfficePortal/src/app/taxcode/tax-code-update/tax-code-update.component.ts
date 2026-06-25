import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Component, Inject, OnInit, ViewChild } from '@angular/core';
import { NgForm, NgModel } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { environment } from 'src/environments/environment';
import { Systemstatus } from '../../shared/enums/systemstatus';


@Component({
  selector: 'app-tax-code-update',
  templateUrl: './tax-code-update.component.html',
  styleUrls: ['./tax-code-update.component.scss'],
})
export class TaxCodeUpdateComponent implements OnInit {

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

  @ViewChild('taxCodeRef') taxCodeControl!: NgModel;
  @ViewChild('taxCodeNameENRef') taxCodeNameENControl!: NgModel;
  @ViewChild('taxCodeNameBMRef') taxCodeNameBMControl!: NgModel;
  @ViewChild('taxPercentageRef') taxPercentageControl!: NgModel;


  errorMessages: string[] = [];
  error: boolean = false;
  taxCodeId: number | null = null;
  taxCodeStatus: string | null = null;
  taxCode: String | null = null;
  taxCodeNameEN: String | null = null;
  taxCodeNameBM: String | null = null;
  taxPercentage: number | null = null;
  tempTaxCode: String | null = null;
  tempTaxCodeNameEN: String | null = null;
  tempTaxCodeNameBM: String | null = null;
  tempTaxPercentage: number | null = null;
  totalRecords: number = 0;

  isLoading: boolean = false;

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: any,
    public dialogRef: MatDialogRef<TaxCodeUpdateComponent>,
    private http: HttpClient
  ) { }

  ngOnInit(): void {
    this.defaultSetting();
    this.taxCodeId = this.data.taxCodeId;
    this.taxCode = this.data.id;
    this.taxCodeNameEN = this.data.en;
    this.taxCodeNameBM = this.data.bm;
    this.taxPercentage = this.data.pct;
    this.taxCodeStatus = this.data.status;
    this.tempTaxCode = this.data.id;
    this.tempTaxCodeNameEN = this.data.en;
    this.tempTaxCodeNameBM = this.data.bm;
    this.tempTaxPercentage = this.data.pct;
    this.checkStatus();
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
      this.UpdateTaxCode();

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
  UpdateTaxCode(): void {
    const url = environment.apiUrl + '/api/tc/v1/updtaxcode';

    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    const body: any = {
      i_tax_cd_id: this.taxCodeId,
      i_tax_cd: this.taxCode,
      i_tax_cd_nm_en: this.taxCodeNameEN,
      i_tax_cd_nm_bm: this.taxCodeNameBM,
      i_tax_pct: this.taxPercentage,
      i_status: this.taxCodeStatus
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
    if (this.taxCodeControl.invalid) {
      this.error = true;
      this.errorMessages.push('Tax Code is required');
    }

    if (this.taxCodeNameENControl.invalid) {
      this.error = true;
      this.errorMessages.push('Tax Code Name (EN) is required');
    }

    if (this.taxCodeNameBMControl.invalid) {
      this.error = true;
      this.errorMessages.push('Tax Code Name (BM) is required');
    }

    if (this.taxPercentageControl.invalid) {
      this.error = true;
      this.errorMessages.push('Tax Percentage is required');
    }

    if (this.error) {
      return true;
    } 
    
    else {
      return false;
    }
  }

 



  async validation(): Promise<boolean> {
    this.defaultSetting();
  
    // Check for required fields
    if (this.checkRequiredField()) {
      return true;
    }
  
    // Check for duplicate Tax Code
    if (await this.isDuplicateTaxCode()) {
      this.error = true;
      this.errorMessages.push('Tax Code is duplicate.');
      return true;
    }
  
    // Check for duplicate Tax Code Name (EN)
    if (await this.isDuplicateTaxCodeNameEN()) {
      this.error = true;
      this.errorMessages.push('Tax Code Name (EN) is duplicate.');
      return true;
    }
  
    // Check for duplicate Tax Code Name (BM)
    if (await this.isDuplicateTaxCodeNameBM()) {
      this.error = true;
      this.errorMessages.push('Tax Code Name (BM) is duplicate.');
      return true;
    }
  
    return false;
  }

  async isDuplicateTaxCode(): Promise<boolean> {
    if (!this.taxCode || this.tempTaxCode === this.taxCode) {
      return false;
    }
  
    const url = environment.apiUrl + '/api/tc/v1/gettaxcode';
  
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });
  
    const body: any = {
      i_page: '1',
      i_size: '1000',
      i_tax_cd: this.taxCode,
      i_status: this.taxCodeStatus
    };
  
    try {
      const response: any = await this.http.post(url, body, { headers }).toPromise();
      // Filter for exact match
      const isDuplicate = response.data.some((item: any) => item.tax_cd === this.taxCode);
      return isDuplicate;
    } catch (error) {
      console.error(error);
      this.errorMessages.push('Internal Server Error.');
      return true;
    }
  }
  

  async isDuplicateTaxCodeNameEN(): Promise<boolean> {
    if (!this.taxCodeNameEN || this.tempTaxCodeNameEN === this.taxCodeNameEN) {
      return false;
    }
  
    const url = environment.apiUrl + '/api/tc/v1/gettaxcode';
  
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });
  
    const body: any = {
      i_page: '1',
      i_size: '1000',
      i_tax_cd_nm_en: this.taxCodeNameEN,
      i_status: this.taxCodeStatus
    };
  
    try {
      const response: any = await this.http.post(url, body, { headers }).toPromise();
      // Filter for exact match
      const isDuplicate = response.data.some((item: any) => item.tax_cd_nm_en === this.taxCodeNameEN);
      return isDuplicate;
    } catch (error) {
      console.error(error);
      this.errorMessages.push('Internal Server Error.');
      return true;
    }
  }
  

  async isDuplicateTaxCodeNameBM(): Promise<boolean> {
    if (!this.taxCodeNameBM || this.tempTaxCodeNameBM === this.taxCodeNameBM) {
      return false;
    }
  
    const url = environment.apiUrl + '/api/tc/v1/gettaxcode';
  
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });
  
    const body: any = {
      i_page: '1',
      i_size: '1000',
      i_tax_cd_nm_bm: this.taxCodeNameBM,
      i_status: this.taxCodeStatus
    };
  
    try {
      const response: any = await this.http.post(url, body, { headers }).toPromise();
      // Filter for exact match
      const isDuplicate = response.data.some((item: any) => item.tax_cd_nm_bm === this.taxCodeNameBM);
      return isDuplicate;
    } catch (error) {
      console.error(error);
      this.errorMessages.push('Internal Server Error.');
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
  //form handle before submit end


  checkStatus(): void {
    const url = environment.apiUrl + '/api/tc/v1/gettaxcode';
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });
  
    const body: any = {
      i_page: '1',
      i_size: '1',
      i_tax_cd_id: this.taxCodeId
    };

    console.log(body)
  
    this.http.post(url, body, { headers })
      .toPromise()
      .then((response: any) => {
        if (response.data && response.data.length > 0) {
          const status = Array.isArray(response.data) ? response.data[0]?.status : response.data.status;
          console.log('Extracted Status:', status);
          this.taxCodeStatus = status;
          console.log('Status:', this.taxCodeStatus);
  
          // Disable input if status is inactive
          if (status === Systemstatus.Active) {
            this.taxCodeControl.control.disable();
          }
        }
      })
      .catch((error) => {
        console.error('Error checking status:', error);
        this.errorMessages.push('Internal Server Error.');
      });
  }
  
  
}
