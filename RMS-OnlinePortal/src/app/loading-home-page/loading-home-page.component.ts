import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { environment } from 'src/environments/environment';
import { ApiServiceService } from '../services/api-service.service';
import { GlobalService } from '../shared/global.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-loading-page',
  templateUrl: './loading-home-page.component.html',
  styleUrls: ['./loading-home-page.component.scss']
})
export class LoadingHomePageComponent implements OnInit  {

  constructor(private translate: TranslateService, private http: HttpClient,
    private globalService: GlobalService, private apiService: ApiServiceService,private router: Router) {
    this.translate.setDefaultLang(this.globalService.getGlobalValue()); // default language
    this.translate.use(this.globalService.getGlobalValue());
  }
  
  ngOnInit(): void {
    var y = document.getElementById('header2') as HTMLElement;
    y.style.display = "none";

    setTimeout(() => {
      this.redirectPage();
    }, 1000);  //1s
  }
  async redirectPage(): Promise<void> {
    var resp = await this.http.get<any>(`${environment.apiUrl}/api/auth/isinternaluser`).toPromise();
    if(resp.flag == 'true')
      window.location.href = environment.bo_url + '/home';
    else
      this.router.navigate(['/home']);
  }
}
