import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Component, Inject, ViewChild } from '@angular/core';
import { NgForm, NgModel } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { TranslateService } from '@ngx-translate/core';
import { FmsAddComponent } from 'src/app/fms/fms-add/fms-add.component';
import { GlobalService } from 'src/app/shared/global.service';
import { environment } from 'src/environments/environment';

@Component({
  selector: 'app-fmsledger-add',
  templateUrl: './fmsledger-add.component.html',
  styleUrls: ['./fmsledger-add.component.scss']
})
export class FmsledgerAddComponent {

  constructor(public dialogRef: MatDialogRef<FmsledgerAddComponent>,  private http: HttpClient,  private translate: TranslateService,
    private globalService: GlobalService) {
      this.translate.setDefaultLang(this.globalService.getGlobalValue()); // default language
      this.translate.use(this.globalService.getGlobalValue());
    }

  onSave(): void {
    // Implement the logic to save the ledger or perform other actions
    // For example, you can emit an event, make an API call, etc.

    // Close the dialog and pass the result back to the component that opened it
    this.dialogRef.close('inserted');
  }

  onCancel(): void {
    // Implement the logic when the user cancels the operation
    // For example, reset form data, clear selections, etc.

    // Close the dialog without saving
    this.dialogRef.close('cancel');
  }

}
