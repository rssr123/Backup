import { Component } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';

@Component({
  selector: 'app-cancel-task',
  templateUrl: './cancel-task.component.html',
  styleUrls: ['./cancel-task.component.scss']
})
export class CancelTaskComponent {

  constructor(
    public dialogRef: MatDialogRef<CancelTaskComponent>,
  ) {}


  closed(): void {
    this.dialogRef.close('no');
  }

  confirmcancel(): void {
    this.dialogRef.close('yes');
  }
}
