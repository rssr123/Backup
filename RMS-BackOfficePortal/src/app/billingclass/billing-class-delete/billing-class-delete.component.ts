import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { environment } from 'src/environments/environment';

@Component({
  selector: 'app-billing-class-delete',
  templateUrl: './billing-class-delete.component.html',
  styleUrls: ['./billing-class-delete.component.scss'],
})
export class BillingClassDeleteComponent implements OnInit {
  billingClassId: string | null = null; // Billing class ID to delete
  isLoading: boolean = false;
  errorMessages: string[] = [];
  error: boolean = false;

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: any,
    public dialogRef: MatDialogRef<BillingClassDeleteComponent>,
    private http: HttpClient
  ) {}

  ngOnInit(): void {
    this.billingClassId = this.data.classId; // Set the billing class ID from the dialog data
  }

  async delete(): Promise<void> {
    await this.deleteBillingClass();
    this.dialogRef.close('deleted'); // Close dialog with 'deleted' status
  }

  close(): void {
    this.dialogRef.close(); // Close dialog without action
  }

  async deleteBillingClass(): Promise<void> {
    const url = `${environment.apiUrl}/api/blc/v1/delbillingclass`; // Updated API endpoint

    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    const body: any = {
      i_class_id: this.billingClassId,
      i_modified_by: 'current_user', // Replace with actual user from context/auth
      i_status: 'D', // Status to indicate deactivation or deletion
    };

    try {
      this.isLoading = true; // Show loading spinner
      const response = await this.http.post(url, body, { headers }).toPromise();
      console.log('Billing class deleted successfully:', response);
    } catch (error) {
      console.error('Error deleting billing class:', error);
      this.error = true;
      this.errorMessages.push('Failed to delete the billing class. Please try again.');
    } finally {
      this.isLoading = false; // Hide loading spinner
    }
  }
}
