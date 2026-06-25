import { Component } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { ActivatedRoute, Router } from '@angular/router';
import { environment } from 'src/environments/environment';

@Component({
  selector: 'app-ssstatus',
  templateUrl: './ssstatus.component.html',
  styleUrls: ['./ssstatus.component.scss']
})
export class SsstatusComponent {

  billing_no: string | null = null;
  modal: any[] = [];
  apiURL = environment.apiUrl;

  constructor(
    private http: HttpClient,
    private router: Router,
    private route: ActivatedRoute,
  ) { }

  ngOnInit() {

    const queryParams = this.route.snapshot.queryParamMap;

    const tempbilling_no = queryParams.get('billing_no');
    // if (tempsscdFromCallback !== null && tempsscdFromCallback !== 'null') {
    this.billing_no = tempbilling_no;

    this.loadBilling()
  }





  loadBilling() {

    // const url = 'https://localhost:8080/api/bibss/v1/callbacksubmitbilling';
    const url = this.apiURL + "/api/bibss/v1/callbacksubmitbilling";

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: 'Basic cm95OnBhc3M=',
      'Content-Type': 'application/json'
    });


    // Create the request body with your form data
    const requestBody = {
      i_billing_no: this.billing_no
    };

    // Send an HTTP POST request to the API
    this.http.post(url, requestBody, { headers }).subscribe(
      (response: any) => {
        if (response.data.length === 0) {
          console.error('Invalid response format:', response);
        }
        else {
          this.modal = response.data;
        }
      },
      (error) => {
        console.error('There was an error retrieving the call back url:', error);

      }
    );
  }











}
