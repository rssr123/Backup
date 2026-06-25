import { Component, Input } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { GlobalService } from 'src/app/shared/global.service';

@Component({
  selector: 'loading-overlay',
  templateUrl: './loading-overlay.component.html',
  styleUrls: ['./loading-overlay.component.scss'],
})
export class LoadingOverlayComponent {
  constructor(
    private translate: TranslateService,
    private globalService: GlobalService
  ) {
    this.translate.setDefaultLang(this.globalService.getGlobalValue());
    this.translate.use(this.globalService.getGlobalValue());
  }

  @Input() prefixText: string = '';
  @Input() loadingText: string = '';
  @Input() suffixText: string = '';
}

// HTML Usage:
//
// <loading-overlay
//     *ngIf="isLoading"
//     [prefixText]="''"
//     [loadingText]="''"
//     [suffixText]="''"
// >
// </loading-overlay>
//
// Translations:
// Translation text can be written as: [loadingText]="'label.key'"
