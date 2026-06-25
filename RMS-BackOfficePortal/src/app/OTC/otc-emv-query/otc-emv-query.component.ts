import { HttpHeaders } from '@angular/common/http';
import { Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { environment } from 'src/environments/environment';

@Component({
  selector: 'app-otc-emv-query',
  templateUrl: './otc-emv-query.component.html',
  styleUrls: ['./otc-emv-query.component.scss']
})
export class OtcEmvQueryComponent {
  
  constructor(
    public dialogRef: MatDialogRef<OtcEmvQueryComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { message: string }
  ) {}

  onConfirm(): void {
    this.dialogRef.close(true); // User confirms deletion
  }

  onCancel(): void {
    this.dialogRef.close(false); // User cancels deletion
  }

}
