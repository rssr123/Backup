import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Component, OnInit, ViewChild } from '@angular/core';
import { NgForm, NgModel } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { environment } from 'src/environments/environment';

@Component({
  selector: 'app-branch-code-add',
  templateUrl: './branch-code-add.component.html',
  styleUrls: ['./branch-code-add.component.scss'],
})
export class BranchCodeAddComponent implements OnInit {
  @ViewChild('branchCodeRef') branchCodeControl!: NgModel;
  @ViewChild('branchNameRef') branchNameControl!: NgModel;
  @ViewChild('branchTypeRef') branchTypeControl!: NgModel;

  errorMessages: string[] = [];
  error: boolean = false;

  branchCode: string | null = null;
  branchName: string | null = null;
  branchType: string | null = null;

  isLoading: boolean = false;

  constructor(public dialogRef: MatDialogRef<BranchCodeAddComponent>, private http: HttpClient) {}

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

    const url = `${environment.apiUrl}/api/bc/v1/addbranchcodes`;
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    const body = {
      i_code: this.branchCode,
      i_bcm_ty: this.branchType,
      i_bcm_desc: this.branchName,
      i_status: 'A', // Default status to "Active"
      i_created_by: 'current_user', // Replace with authenticated user
      i_modified_by: 'current_user', // Replace with authenticated user
    };

    try {
      await this.http.post(url, body, { headers }).toPromise();
      this.dialogRef.close('inserted');
    } catch (error: any) {
      console.error('Error:', error);
      if (error.status === 400) {
        this.errorMessages.push('Branch Code already exists. Please use a different code.');
      } else {
        this.errorMessages.push('Failed to add branch. Please try again.');
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
    if (!this.branchCodeControl.valid) {
      this.errorMessages.push('Branch Code is required.');
    }
    if (!this.branchTypeControl.valid) {
      this.errorMessages.push('Branch Type is required.');
    }
    if (!this.branchNameControl.valid) {
      this.errorMessages.push('Branch Name is required.');
    }

    return this.errorMessages.length === 0; // Return `true` if no errors
  }
}
