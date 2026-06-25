import { AfterViewInit, Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { GHLPayment } from '../models/ghl-request.model';
import { HttpClient } from '@angular/common/http';
import { DecimalPipe } from '@angular/common';
import { environment } from 'src/environments/environment';

@Component({
  selector: 'app-ghl',
  templateUrl: './ghl.component.html',
  styleUrls: ['./ghl.component.scss'],
  providers: [DecimalPipe]
})
export class GhlComponent implements OnInit,AfterViewInit {
  @ViewChild('submitBtn') submitBtn!: ElementRef;

  ghlrequest!: GHLPayment; // Define the type of your data

  eghl:string='';
  returnURL:string='';

  constructor(private route: ActivatedRoute,private http: HttpClient,private decimalPipe: DecimalPipe) {}

  ngOnInit() {

    this.eghl=environment.eghl;
    this.returnURL=environment.url+'/payment-response-redirect';

    this.route.paramMap.subscribe((params) => {
      const ghlrequestParam = params.get('ghlrequest');
      if (ghlrequestParam) {
        this.ghlrequest = JSON.parse(ghlrequestParam) as GHLPayment;
        this.ghlrequest.page_timeout=this.ghlrequest.page_timeout*60;
        this.ghlrequest.amt=parseFloat(this.ghlrequest.amt).toFixed(2);
        //this.ghlrequest.amt!=this.decimalPipe.transform(this.ghlrequest.amt);
      }
      //console.log(this.ghlrequest);
    });

  }

  get combinedCustInfo(): string {
    return `${this.ghlrequest.cust_nm || ''} ${this.ghlrequest.cust_email || ''}`;
  }
  
  set combinedCustInfo(value: string) {
    const [cust_nm, ...cust_emailParts] = value.split(' ');
    this.ghlrequest.cust_nm = cust_nm || '';
    this.ghlrequest.cust_email = cust_emailParts.join(' ') || '';
  }

  ngAfterViewInit() {
    //comment for testing
    setTimeout(() => {
      this.submitBtn.nativeElement.click();
      }, 5);  //10=0.01s //5=0.005s

    //this.submitBtn.nativeElement.click();

  }

  onButtonClick() {
    //console.log('Button clicked!');
  }

  onSubmit() {

    //console.log("here");

    //Send the form data to your API using Angular's HttpClient
    // this.http.post('https://pay.e-ghl.com/IPGSG/Payment.aspx', this.ghlrequest)
    //   .subscribe(response => {
    //     // Handle the API response here
    //     console.log('API Response:', response);
    //   }, error => {
    //     // Handle errors here
    //     console.error('API Error:', error);
    //   });
  }
}
