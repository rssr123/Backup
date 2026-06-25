import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { environment } from 'src/environments/environment';
import { ApiServiceService } from '../services/api-service.service';
import { GlobalService } from '../shared/global.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-loading-page',
  templateUrl: './loading-page.component.html',
  styleUrls: ['./loading-page.component.scss']
})
export class LoadingPageComponent implements OnInit  {

  constructor(private translate: TranslateService, private http: HttpClient,
    private globalService: GlobalService, private apiService: ApiServiceService,private router: Router) {

  
    this.translate.setDefaultLang(this.globalService.getGlobalValue()); // default language
    this.translate.use(this.globalService.getGlobalValue());
  }

  ngOnInit(): void {
      setTimeout(() => {
        this.redirectPage();
    }, 5000);  //5s
      
  }

  redirectPage(): void {
    const url = environment.apiUrl + '/api/onlinepayment/v1/rms_paymentPage';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json'
    });

    const body = new URLSearchParams();
    body.set('feeGrpId', '');
    body.set('ssCd', '');


    // Make the HTTP GET request
    this.http.post(url, body.toString(), { headers }).subscribe(
      (response) => {
        console.log(response);
        // You can process the response data here
      },
      (error) => {
        console.error(error);
        // Handle errors here
      }

    );

    this.router.navigate(['/payment-page']);
  }

}
