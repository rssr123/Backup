import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Component, Inject, OnInit, ViewChild } from '@angular/core';
import { NgForm, NgModel } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { environment } from 'src/environments/environment';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-fee-group-delete',
  templateUrl: './fee-group-delete.component.html',
  styleUrls: ['./fee-group-delete.component.scss'],
})
export class FeeGroupDeleteComponent implements OnInit {
  feeGroupId: String | null = null;
  constructor(
    @Inject(MAT_DIALOG_DATA) public data: any,
    public dialogRef: MatDialogRef<FeeGroupDeleteComponent>,
    private http: HttpClient
  ) {}
  ngOnInit(): void {
    this.feeGroupId = this.data.id;
  }

  errorMessages: string[] = [];
  error: boolean = false;

  feeGroupNameEN: String | null = null;
  feeGroupNameBM: String | null = null;
  totalRecords: number = 0;
  isLoading: boolean = false;
  //delete start
  async Delete() {
    this.DeleteFeeGroup();

    this.dialogRef.close('deleted');
  }
  //delete end

  closed(): void {
    this.dialogRef.close();
  }

  //delete Start
  DeleteFeeGroup(): void {
    const url = environment.apiUrl + '/api/fg/v1/deletefeegroup';

    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    const body: any = {
      i_fee_grp_id: this.feeGroupId,
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
