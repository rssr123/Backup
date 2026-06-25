import { Component } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';

@Component({
  selector: 'app-mft-cancel-task',
  templateUrl: './mft-cancel-task.component.html',
  styleUrls: ['./mft-cancel-task.component.scss']
})
export class MftCancelTaskComponent {

  constructor(
    public dialogRef: MatDialogRef<MftCancelTaskComponent>,
  ) {}


  closed(): void {
    this.dialogRef.close('no');
  }

  confirmcancel(): void {
    this.dialogRef.close('yes');
  }
}
