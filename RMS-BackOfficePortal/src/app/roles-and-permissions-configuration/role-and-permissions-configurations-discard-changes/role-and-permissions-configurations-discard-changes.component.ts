import { Component } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';
import {TranslateService} from '@ngx-translate/core';
import { GlobalService } from 'src/app/shared/global.service';

@Component({
  selector: 'app-role-and-permissions-configurations-discard-changes',
  templateUrl: './role-and-permissions-configurations-discard-changes.component.html',
  styleUrls: ['./role-and-permissions-configurations-discard-changes.component.scss']
})

export class RoleAndPermissionsConfigurationsDiscardChangesComponent {
  constructor(
    public dialogRef: MatDialogRef<RoleAndPermissionsConfigurationsDiscardChangesComponent>,
    private translate: TranslateService,
    private globalService: GlobalService) 
    {
      this.translate.setDefaultLang(this.globalService.getGlobalValue()); // default language
      this.translate.use(this.globalService.getGlobalValue());
    }

  onNoClick(): void {
    this.dialogRef.close(false);
    
  }
}
