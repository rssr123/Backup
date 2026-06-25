import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Component, OnInit, ViewChild } from '@angular/core';
import { NgForm, NgModel } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { environment } from 'src/environments/environment';

@Component({
  selector: 'app-tax-code-add',
  templateUrl: './tax-code-add.component.html',
  styleUrls: ['./tax-code-add.component.scss'],
})
export class TaxCodeAddComponent implements OnInit {

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

  @ViewChild('taxCodeNameENRef') taxCodeControl!: NgModel;
  @ViewChild('taxCodeNameENRef') taxCodeNameENControl!: NgModel;
  @ViewChild('taxCodeNameBMRef') taxCodeNameBMControl!: NgModel;
  @ViewChild('taxPercentageRef') taxPercentageControl!: NgModel;
 // @ViewChild('taxPercentageRef') taxPercentageControl!: NgModel;

  errorMessages: string[] = [];
  error: boolean = false;
  taxCode: String | null = null;
  taxCodeNameEN: String | null = null;
  taxCodeNameBM: String | null = null;
  taxPercentage: number | null = null;
  totalRecords: number = 0;
  isLoading: boolean = false;

  constructor(
    public dialogRef: MatDialogRef<TaxCodeAddComponent>,
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
      this.InsertTaxCode();

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
  InsertTaxCode(): void {
    const url = environment.apiUrl + '/api/tc/v1/addtaxcode';

    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    const body: any = {
      i_page: '1',
      i_size: '1',
      i_tax_cd: this.taxCode,
      i_tax_cd_nm_en: this.taxCodeNameEN,
      i_tax_cd_nm_bm: this.taxCodeNameBM,
      i_tax_pct: this.taxPercentage
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

   
    if (this.taxPercentageControl.invalid ) {
      this.error = true;
      this.errorMessages.push('Tax Percentage is required');

    }

    


    if (this.error) {
      return true;
    } else {
      return false;
    }
  }

  async validation(): Promise<boolean> {
    this.defaultSetting();
  
    // Required field check
    const isRequiredFieldInvalid = this.checkRequiredField();
    if (isRequiredFieldInvalid) {
      return true;
    }
  
    const url = environment.apiUrl + '/api/tc/v1/gettaxcode';
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });
  
    const checkFields = [
      { field: 'i_tax_cd', value: this.taxCode?.trim().toLowerCase(), key: 'tax_cd', message: 'Tax Code is duplicate.' },
      { field: 'i_tax_cd_nm_en', value: this.taxCodeNameEN?.trim().toLowerCase(), key: 'tax_cd_nm_en', message: 'Tax Code Name (EN) is duplicate.' },
      { field: 'i_tax_cd_nm_bm', value: this.taxCodeNameBM?.trim().toLowerCase(), key: 'tax_cd_nm_bm', message: 'Tax Code Name (BM) is duplicate.' }
    ];
  
    for (let i = 0; i < checkFields.length; i++) {
      const body: any = {
        i_page: '1',
        i_size: '1000', // Large size to get all matching records
      };
      body[checkFields[i].field] = checkFields[i].value;
  
      try {
        const response: any = await this.http.post(url, body, { headers }).toPromise();
  
        // Check for exact match (case-insensitive)
        const exactMatch = response.data.some((item: any) => 
          item[checkFields[i].key]?.trim().toLowerCase() === checkFields[i].value
        );
  
        if (exactMatch) {
          this.error = true;
          this.errorMessages.push(checkFields[i].message);
        }
      } catch (error) {
        this.error = true;
        this.errorMessages.push('Internal Server Error.');
        console.error(error);
        return true;
      }
    }
  
    return this.error; // Return true if there are errors, otherwise false
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
}
