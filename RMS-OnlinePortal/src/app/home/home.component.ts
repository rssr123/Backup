import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { environment } from '../../environments/environment';
import { GlobalService } from '../shared/global.service';

import { ApiServiceService } from '../services/api-service.service';
import { ApiResponse } from '../models/api-response.model';
import { AuthService } from '../services/auth.service';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit {

  data: Array<{
    col1: number;
    col2: number;
  }> = [];

  constructor(private translate: TranslateService, private http: HttpClient,
    private globalService: GlobalService, private apiService: ApiServiceService,
    private authService: AuthService) {

  
    this.translate.setDefaultLang(this.globalService.getGlobalValue()); // default language
    this.translate.use(this.globalService.getGlobalValue());
  }

  useLanguage(language: string) {
    this.globalService.setGlobalValue(language);  
    this.translate.use(language); // to change the language at runtime
  }

  ngOnInit(): void {
    // Call a method to fetch data from the API when the component initializes
    // this.apiService.fetchData().subscribe(
    //   (response: ApiResponse<typeof this.data>) => {  // This is the success callback
    //     this.data = response.data;
    //     console.log('Data fetched successfully:', this.data);
    //     // You can also handle any UI updates for successful data retrieval here.
    //   },
    //   (error: any) => {  // This is the error callback
    //     console.error('Error fetching data:', error);
    //     // Handle UI updates for an error scenario, perhaps show an error message to the user.
    //   },
    //   () => {  // This is the completion callback (optional and is called after all data has been received or an error occurs)
    //     console.log('Data fetch operation completed.');
    //     // Any cleanup or finalization logic can go here.
    //   }
    // );
    //this.authService.checkLogin(); //commneted by roy
  }
  
  public localStorageItem(id: string): string {
	  return localStorage.getItem(id) as string;
	}
  // fetchData(): void {
  //   // const url = environment.apiUrl + '/api/mft/v1/getFeeDetailListing';
  //   const url = environment.apiUrl + '/api/mft/v1/getTableData';
  //   // Set your authorization header
  //   const headers = new HttpHeaders({
  //     Authorization: environment.authKey,
  //     'Content-Type': 'application/x-www-form-urlencoded'
  //   });

  //   const body = new URLSearchParams();
  //   body.set('feeGrpId', '');
  //   body.set('ssCd', '');


  //   // Make the HTTP GET request
  //   this.http.get(url,  { headers }).subscribe(
  //     (response) => {
  //       console.log(response);
  //       // You can process the response data here
  //     },
  //     (error) => {
  //       console.error(error);
  //       // Handle errors here
  //     }
  //   );
  // }
}
