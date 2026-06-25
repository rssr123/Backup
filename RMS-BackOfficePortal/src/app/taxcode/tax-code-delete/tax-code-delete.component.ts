import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Component, Inject, OnInit, ViewChild } from '@angular/core';
import { NgForm, NgModel } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { environment } from 'src/environments/environment';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-tax-code-delete',
  templateUrl: './tax-code-delete.component.html',
  styleUrls: ['./tax-code-delete.component.scss'],
})
export class TaxCodeDeleteComponent implements OnInit {
  taxCode: String | null = null;
  constructor(
    @Inject(MAT_DIALOG_DATA) public data: any,
    public dialogRef: MatDialogRef<TaxCodeDeleteComponent>,
    private http: HttpClient
  ) {}
  ngOnInit(): void {
    this.taxCode = this.data.cd;
  }

  errorMessages: string[] = [];
  error: boolean = false;
  
  taxCodeNameEN: String | null = null;
  taxCodeNameBM: String | null = null;
  totalRecords: number = 0;
  isLoading: boolean = false;
  //delete start
  async Delete() {
    this.DeleteTaxCode();

    this.dialogRef.close('deleted');
  }
  //delete end

  closed(): void {
    this.dialogRef.close();
  }

  //delete Start
  DeleteTaxCode(): void {
    const url = environment.apiUrl + '/api/tc/v1/deltaxcode';

    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    const body: any = {
      i_tax_cd: this.taxCode,
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
  //delete End
}
