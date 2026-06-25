import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Component, OnInit, ViewChild } from '@angular/core';
import { NgForm, NgModel } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { environment } from 'src/environments/environment';

@Component({
  selector: 'app-billing-class-add',
  templateUrl: './billing-class-add.component.html',
  styleUrls: ['./billing-class-add.component.scss'],
})
export class BillingClassAddComponent implements OnInit {
  @ViewChild('classIdRef') classIdControl!: NgModel;
  @ViewChild('classDescRef') classDescControl!: NgModel;

  errorMessages: string[] = [];
  error: boolean = false;

  classId: string | null = null;
  classDesc: string | null = null;

  isLoading: boolean = false;

  constructor(public dialogRef: MatDialogRef<BillingClassAddComponent>, private http: HttpClient) {}

  ngOnInit(): void {
    this.resetErrors();
  }

  resetErrors(): void {
    this.error = false;
    this.errorMessages = [];
  }

  onClose(): void {
    this.dialogRef.close();
  }

  async submit(): Promise<void> {
    this.isLoading = true;
    this.resetErrors();

    const url = `${environment.apiUrl}/api/blc/v1/addBillingClass`;
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    const body = {
      i_class_id: this.classId,
      i_class_desc: this.classDesc,
      i_status: 'A', // Default status to "Active"
      i_created_by: 'current_user', // Replace with authenticated user
      i_modified_by: 'current_user', // Replace with authenticated user
    };

    try {
      await this.http.post(url, body, { headers }).toPromise();
      this.dialogRef.close('inserted');
    } catch (error: any) {
      console.error('Error:', error);
      if (error.status === 400 ) {
        this.errorMessages.push('Billing Class ID already exists. Please use a different ID.');
      } else {
        this.errorMessages.push('Failed to add billing class. Please try again.');
      }
      this.error = true;
    } finally {
      this.isLoading = false;
    }
  }

  handleFormSubmit(form: NgForm): void {
    if (form.valid) {
      this.submit();
    } else {
      Object.values(form.controls).forEach((control) => {
        control.markAsTouched();
      });
    }
  }

  validateFields(): boolean {
    if (!this.classIdControl.valid) {
      this.errorMessages.push('Billing Class ID is required.');
    }
    if (!this.classDescControl.valid) {
      this.errorMessages.push('Billing Class Description is required.');
    }

    return this.errorMessages.length === 0; // Return `true` if no errors
  }
}
