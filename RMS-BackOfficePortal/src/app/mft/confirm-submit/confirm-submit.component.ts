import { Component } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';

@Component({
  selector: 'app-confirm-submit',
  templateUrl: './confirm-submit.component.html',
  styleUrls: ['./confirm-submit.component.scss']
})
export class ConfirmSubmitComponent {

  constructor(
    public dialogRef: MatDialogRef<ConfirmSubmitComponent>,
  ) {}


  closed(): void {
    this.dialogRef.close('no');
  }

  confirmsubmit(): void {
    this.dialogRef.close('yes');
  }
}
