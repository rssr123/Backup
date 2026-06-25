import { formatDate } from '@angular/common';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Component } from '@angular/core';
import { environment } from 'src/environments/environment';
import { Router } from '@angular/router';


@Component({
  selector: 'app-sssample-screen',
  templateUrl: './sssample-screen.component.html',
  styleUrls: ['./sssample-screen.component.scss']
})
export class SssampleScreenComponent {

  apiURL = environment.apiUrl;
  mockss_url = environment.mockss_url;

  // ss_cd: string | null = null;
  ss_cd: string | null = null;
  callbackurl: string | null = null;

  constructor(private http: HttpClient, private router: Router) {
    console.log('Constructor before called');
    // this.basePaymentData = this.initializePaymentDetailsByTable();
    console.log('Constructor called');
    // const jsonData = JSON.stringify(this.basePaymentData);
    // this.jsonData = JSON.stringify(this.basePaymentData);
    // console.log(jsonData);
  }


  ngOnInit() {
    this.fetchSS();
  }


  get requestPayload() {
    return {
      ss_cd: this.ss_cd ?? '',
      callbackurl: `${this.mockss_url}/ssstatus`,
    };
  }

  get formEncoded(): string {
    const p = new URLSearchParams();
    p.set('ss_cd', this.ss_cd ?? '');
    p.set('callbackurl', `${this.mockss_url}/ssstatus`);
    return p.toString();
  }


  ssOptions: any[] = [];
  selectedss: string | null = null;

  fetchSS() {
    const url = environment.apiUrl + '/api/rms/v1/getsourcesystem';
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    const requestBody = {
      i_page: 1,
      i_size: 1000,
    };

    this.http.post(url, requestBody, { headers }).subscribe(
      (response: any) => {
        this.ssOptions = response.data;

        //console.log('Source System Options:', this.ssOptions);

        if (this.ssOptions.length > 0) {
          this.ss_cd = this.ssOptions[0].ss_cd;
        }
      },
      (error) => {
        console.error('Error fetching fee details:', error);
      }
    );
  }



}
