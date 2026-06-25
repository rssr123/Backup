import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { environment } from 'src/environments/environment';

@Component({
  selector: 'app-branch-code-delete',
  templateUrl: './branch-code-delete.component.html',
  styleUrls: ['./branch-code-delete.component.scss'],
})
export class BranchCodeDeleteComponent implements OnInit {
  branchCodeId: string | null = null; // Branch code to delete
  isLoading: boolean = false;
  errorMessages: string[] = [];
  error: boolean = false;

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: any,
    public dialogRef: MatDialogRef<BranchCodeDeleteComponent>,
    private http: HttpClient
  ) {}

  ngOnInit(): void {
    this.branchCodeId = this.data.code; // Set the branch code from the dialog data
  }

  async Delete(): Promise<void> {
    await this.DeleteBranchCode();
    this.dialogRef.close('deleted'); // Close dialog with 'deleted' status
  }

  closed(): void {
    this.dialogRef.close(); // Close dialog without action
  }

  async DeleteBranchCode(): Promise<void> {
    const url = `${environment.apiUrl}/api/bc/v1/delbranchcode`;

    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    const body: any = {
      i_code: this.branchCodeId,
      i_modified_by: 'current_user', // Replace with actual user from context/auth
      i_status: 'D', // Status to indicate deactivation or deletion
    };

    try {
      this.isLoading = true; // Show loading spinner
      const response = await this.http.post(url, body, { headers }).toPromise();
      console.log('Branch code deleted successfully:', response);
    } catch (error) {
      console.error('Error deleting branch code:', error);
      this.error = true;
      this.errorMessages.push('Failed to delete the branch code. Please try again.');
    } finally {
      this.isLoading = false; // Hide loading spinner
    }
  }
}
