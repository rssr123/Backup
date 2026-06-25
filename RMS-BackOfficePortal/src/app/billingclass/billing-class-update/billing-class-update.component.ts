import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Component, Inject, OnInit, ViewChild } from '@angular/core';
import { NgForm, NgModel } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { environment } from 'src/environments/environment';

@Component({
  selector: 'app-billing-class-update',
  templateUrl: './billing-class-update.component.html',
  styleUrls: ['./billing-class-update.component.scss'],
})
export class BillingClassUpdateComponent implements OnInit {
  @ViewChild('billingClassIdRef') billingClassIdControl!: NgModel;
  @ViewChild('billingClassDescRef') billingClassDescControl!: NgModel;

  errorMessages: string[] = [];
  error: boolean = false;
  isLoading: boolean = false;

  // Data-binding variables
  billingClassId: string | null = null;
  billingClassDesc: string | null = null;
  blcm_id: number = 0;
  tempBillingClassId: string | null = null;
  tempBillingClassDesc: string | null = null;

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: any,
    public dialogRef: MatDialogRef<BillingClassUpdateComponent>,
    private http: HttpClient
  ) { }

  ngOnInit(): void {
    this.initializeForm();
  }

  // Initialize form fields
  initializeForm(): void {
    this.billingClassId = this.data.classId || ''; // Existing billing class ID
    this.billingClassDesc = this.data.classDesc || ''; // Existing billing class description
    this.blcm_id = this.data.blcm_id; // Existing billing class maintenance ID
    console.log('Data received:', this.data);
    this.tempBillingClassId = this.billingClassId;
    this.tempBillingClassDesc = this.billingClassDesc;
  }

  closeDialog(): void {
    this.dialogRef.close();
  }

  async updateBillingClass() {
    if (!this.billingClassId || !this.billingClassDesc) {
      this.errorMessages = ['All fields are required.'];
      this.error = true;
      return;
    }

    // Prepare request payload
    const body = {
      i_blcm_id: this.blcm_id,
      i_class_id: this.billingClassId,
      i_class_desc: this.billingClassDesc,
      i_status: 'A'
    };

    this.isLoading = true;

    const url = `${environment.apiUrl}/api/blc/v1/updBillingClass`;
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    try {
      const response = await this.http.post(url, body, { headers }).toPromise();
      console.log('Update successful:', response);
      this.dialogRef.close('updated');
    } catch (error) {
      console.error('Update failed:', error);
      this.errorMessages.push('Failed to update the billing class. The Class ID already exists.');
      this.error = true;
    } finally {
      this.isLoading = false;
    }
  }

  async handleFormSubmit(form: NgForm): Promise<void> {
    if (form.valid) {
      await this.updateBillingClass();
    } else {
      Object.values(form.controls).forEach((control) => {
        control.markAsTouched();
      });
    }
  }

  async validation(): Promise<boolean> {
    this.errorMessages = [];
    if (!this.billingClassId) {
      this.errorMessages.push('Billing Class ID is required.');
      return true;
    }
    if (!this.billingClassDesc) {
      this.errorMessages.push('Billing Class Description is required.');
      return true;
    }
    return false; // Validation passed
  }
}
