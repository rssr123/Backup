import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Component, Inject, OnInit, ViewChild } from '@angular/core';
import { NgForm, NgModel } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { environment } from 'src/environments/environment';

@Component({
  selector: 'app-branch-code-update',
  templateUrl: './branch-code-update.component.html',
  styleUrls: ['./branch-code-update.component.scss'],
})
export class BranchCodeUpdateComponent implements OnInit {
  @ViewChild('branchCodeRef') branchCodeControl!: NgModel;
  @ViewChild('branchNameRef') branchNameControl!: NgModel;
  @ViewChild('branchTypeRef') branchTypeControl!: NgModel;

  errorMessages: string[] = [];
  error: boolean = false;
  isLoading: boolean = false;

  // Data-binding variables
  branchCode: string | null = null;
  branchName: string | null = null;
  branchType: string | null = null;
  bcm_id: number | null = null; // Assuming this is the ID of the branch code
  tempBranchCode: string | null = null;
  tempBranchName: string | null = null;
  tempBranchType: string | null = null;

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: any,
    public dialogRef: MatDialogRef<BranchCodeUpdateComponent>,
    private http: HttpClient
  ) {}

  ngOnInit(): void {
    this.initializeForm();
  }

  // Initialize form fields
  initializeForm(): void {
    this.branchCode = this.data.branchCode || ''; // Existing branch code
    this.branchName = this.data.branchName || ''; // Existing branch name
    this.branchType = this.data.branchType || ''; // Existing branch type
    this.bcm_id = this.data.bcm_id || null; // Existing branch code ID

    console.log(this.data);

    this.tempBranchCode = this.branchCode;
    this.tempBranchName = this.branchName;
    this.tempBranchType = this.branchType;
  }

  closeDialog(): void {
    this.dialogRef.close();
  }

  async updateBranch() {
    if (!this.branchCode || !this.branchName || !this.branchType) {
      this.errorMessages = ['All fields are required.'];
      this.error = true;
      return;
    }

    // Prepare request payload
    const body = {
      i_bcm_id: this.bcm_id,
      i_code: this.branchCode,
      i_bcm_desc: this.branchName,
      i_bcm_ty: this.branchType
    };

    this.isLoading = true;

    const url = `${environment.apiUrl}/api/bc/v1/updateBranchCode`;
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
      this.errorMessages.push('Failed to update the branch. The branch code has already exist.');
      this.error = true;
    } finally {
      this.isLoading = false;
    }
  }

  async handleFormSubmit(form: NgForm): Promise<void> {
    if (form.valid) {
      await this.updateBranch();
    } else {
      Object.values(form.controls).forEach((control) => {
        control.markAsTouched();
      });
    }
  }

  async validation(): Promise<boolean> {
    this.errorMessages = [];
    if (!this.branchCode) {
      this.errorMessages.push('Branch Code is required.');
      return true;
    }
    if (!this.branchName) {
      this.errorMessages.push('Branch Name is required.');
      return true;
    }
    if (!this.branchType) {
      this.errorMessages.push('Branch Type is required.');
      return true;
    }
    return false; // Validation passed
  }
}
