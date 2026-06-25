import { Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

@Component({
  selector: 'app-ag-delete-file-diaglog',
  templateUrl: './ag-delete-file-diaglog.component.html',
  styleUrls: ['./ag-delete-file-diaglog.component.scss']
})
export class AgDeleteFileDiaglogComponent {
  constructor(
    public dialogRef: MatDialogRef<AgDeleteFileDiaglogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { message: string }
  ) {}

  onConfirm(): void {
    this.dialogRef.close(true); // User confirms deletion
  }

  onCancel(): void {
    this.dialogRef.close(false); // User cancels deletion
  }
}
