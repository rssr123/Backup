import { Component } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { GlobalService } from '../shared/global.service';
import { LanguageService } from '../language.service';

@Component({
  selector: 'app-multi-language',
  templateUrl: './multi-language.component.html',
  styleUrls: ['./multi-language.component.scss']
})
export class MultiLanguageComponent {

  constructor(
    private translate: TranslateService,
    private globalService: GlobalService,
    private languageService: LanguageService // Inject LanguageService
  ) {
    this.translate.setDefaultLang(this.globalService.getGlobalValue()); // default language
    this.translate.use(this.globalService.getGlobalValue());
  }

  useLanguage(language: string) {
    this.globalService.setGlobalValue(language);
    this.translate.use(language); // Change the language at runtime
    this.languageService.changeLanguage(language); // Notify other components
  }
}
