import { Component } from '@angular/core';
import { environment } from 'src/environments/environment';

@Component({
  selector: 'app-ssbilpay',
  templateUrl: './ssbilpay.component.html',
  styleUrls: ['./ssbilpay.component.scss']
})
export class SsbilpayComponent {

  apiURL = environment.apiUrl;
  mockss_url = environment.mockss_url;

  billing_no: string | null = null;
  // orn_no: string | null = null;
  ss_return_url: string | null = null;


  get requestPayload() {
    return {
      billing_no: this.billing_no ?? '',
      ss_return_url: `${this.mockss_url}/ssbilpaystatus`,
    };
  }

}
