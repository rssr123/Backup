import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { GlobalService } from '../shared/global.service';
import { TranslateService } from '@ngx-translate/core';
import { AuthService } from 'src/app/services/auth.service';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { environment } from 'src/environments/environment';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss']
})
export class HeaderComponent implements OnInit {
  title = 'RMS';
  isNavVisible: boolean = false;
  username$: Observable<string>;
  name$: Observable<string>;
  authenticated$: Observable<string>;

  dynamicMessage: string = "E-Services Public Portal";
  datetimeNow: Date = new Date();
  formattedDatetime: string = this.formatDate(this.datetimeNow);

  selectedLanguage: string;
  languages = ['en', 'bm'];

  constructor(private cdr: ChangeDetectorRef,private globalService: GlobalService,private translate: TranslateService,private authService: AuthService ) {
    this.selectedLanguage = this.globalService.getGlobalValue();
    this.translate.setDefaultLang(this.globalService.getGlobalValue());
    this.translate.use(this.globalService.getGlobalValue());
    this.username$ = this.authService.getUsername();
    this.name$ = this.authService.getName();
    this.authenticated$ = this.authService.getAuthenticated();
    
  }

  ngOnInit() {

    // Update the datetime every second
    setInterval(() => {
      this.datetimeNow = new Date();
      this.formattedDatetime = this.formatDate(this.datetimeNow);

      // Update the template
      this.cdr.detectChanges();
    }, 1000);
  }

  useLanguage(language: string) {
    this.globalService.setGlobalValue(language);  
    this.selectedLanguage = language;
    this.translate.use(language); // to change the language at runtime
  }

  private formatDate(date: Date): string {
    const dateString = date.toLocaleDateString('en-GB', {
      // Notice 'en-GB' for day-month-year format
      day: '2-digit',
      month: 'short',
      year: 'numeric',
    });
    const timeString = date.toLocaleTimeString('en-US', {
      hour: '2-digit',
      minute: '2-digit',
      second: '2-digit',
      hour12: false, // Use 24-hour format
    });

    return `${dateString} | ${timeString}`;
  }
}
