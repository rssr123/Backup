import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Component, Inject, OnInit, ViewChild } from '@angular/core';
import { NgForm, NgModel } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { environment } from 'src/environments/environment';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-service-provider-maintenance-delete',
  templateUrl: './service-provider-maintenance-delete.component.html',
  styleUrls: ['./service-provider-maintenance-delete.component.scss']
})
export class ServiceProviderMaintenanceDeleteComponent implements OnInit {
  agPfId: String | null = null;
  constructor(
    @Inject(MAT_DIALOG_DATA) public data: any,
    public dialogRef: MatDialogRef<ServiceProviderMaintenanceDeleteComponent>,
    private http: HttpClient
  ) { }
  ngOnInit(): void {
    this.agPfId = this.data.agPfId;
  }

  errorMessages: string[] = [];
  error: boolean = false;

  
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
    const url = environment.apiUrl + '/api/sp/v1/delserviceprovidermaintenance';

    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    const body: any = {
      i_ag_pf_id: this.agPfId,
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
