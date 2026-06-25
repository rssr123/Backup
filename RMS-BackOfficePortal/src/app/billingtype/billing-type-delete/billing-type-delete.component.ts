import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { environment } from 'src/environments/environment';

@Component({
  selector: 'app-billing-type-delete',
  templateUrl: './billing-type-delete.component.html',
  styleUrls: ['./billing-type-delete.component.scss'],
})
export class BillingTypeDeleteComponent implements OnInit {
  billingTypeId: string | null = null; // Billing Type ID to delete
  isLoading: boolean = false;
  errorMessages: string[] = [];
  error: boolean = false;

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: any,
    public dialogRef: MatDialogRef<BillingTypeDeleteComponent>,
    private http: HttpClient
  ) {}

  ngOnInit(): void {
    this.billingTypeId = this.data.btCd; // Set the Billing Type ID from the dialog data
  }

  async delete(): Promise<void> {
    await this.deleteBillingType();
    this.dialogRef.close('deleted'); // Close dialog with 'deleted' status
  }

  close(): void {
    this.dialogRef.close(); // Close dialog without action
  }

  async deleteBillingType(): Promise<void> {
    const url = `${environment.apiUrl}/api/blt/v1/delBillingType`; // Updated API endpoint

    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    const body: any = {
      i_bt_cd: this.billingTypeId, // Billing Type Code
      i_modified_by: 'current_user', // Replace with actual user from context/auth
      i_status: 'D', // Status to indicate deactivation or deletion
    };

    try {
      this.isLoading = true; // Show loading spinner
      const response = await this.http.post(url, body, { headers }).toPromise();
      console.log('Billing type deleted successfully:', response);
    } catch (error) {
      console.error('Error deleting billing type:', error);
      this.error = true;
      this.errorMessages.push('Failed to delete the billing type. Please try again.');
    } finally {
      this.isLoading = false; // Hide loading spinner
    }
  }
}
