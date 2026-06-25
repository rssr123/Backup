import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { environment } from 'src/environments/environment';
import { ApiResponse } from '../models/api-response.model';


@Component({
  selector: 'app-payment-response-redirect',
  templateUrl: './payment-response-redirect.component.html',
  styleUrls: ['./payment-response-redirect.component.scss']
})

export class PaymentResponseRedirectComponent {

  result!:string;
  pymt_status!: string;
  orn_no!: string;
  rcpt_no!: string;
  rcpt_dt!: string;
  ss_return_url!:string;
  show:boolean=true;
  txnStatus!:number;

  constructor(private route: ActivatedRoute, private http: HttpClient) {

  }

  ngOnInit(): void {

    this.route.queryParams.subscribe(params => {
      
      // below is the code to update the payment status
      const transactionType = params['TransactionType'];
      const pymtMethod = params['PymtMethod'];
      const serviceID = params['ServiceID'];
      const paymentID = params['PaymentID'];
      const orderNumber = params['OrderNumber'];
      const amount = params['Amount'];
      const currencyCode = params['CurrencyCode'];
      const txnID = params['TxnID'];
      this.txnStatus = params['TxnStatus'];
      const param6 = params['Param6'];
      const param7 = params['Param7'];
      const txnMessage = params['TxnMessage'];
      const hashValue = params['HashValue'];
      const hashValue2 = params['HashValue2'];
      const issuingBank = params['IssuingBank']??'';
      const authCode = params['AuthCode']??'';
      const bankRefNo = params['BankRefNo']??'';
      const respTime = params['RespTime']??'';

      this.updatePaymentStatus({
        TransactionType: transactionType,
        PymtMethod: pymtMethod,
        ServiceID: serviceID,
        PaymentID: paymentID,
        OrderNumber: orderNumber,
        Amount: amount,
        CurrencyCode: currencyCode,
        TxnID: txnID,
        TxnStatus: this.txnStatus,
        Param6: param6,
        Param7: param7,
        TxnMessage: txnMessage,
        HashValue: hashValue,
        HashValue2: hashValue2,
        IssuingBank: issuingBank,
        AuthCode: authCode,
        BankRefNo: bankRefNo,
        RespTime: respTime
      });

      

    });
  }

  updatePaymentStatus(params: any):void{
    const url = environment.apiUrl + '/api/onlinepayment/v1/return';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/x-www-form-urlencoded' //'application/json'
    });

    const body = new URLSearchParams();

    for (const key in params) {
      if (params.hasOwnProperty(key)) {
        body.set(key, params[key]);
      }
    }

    console.log(body.toString());

    // Make the HTTP GET request
    this.http.post<ApiResponse<string>>(url, body.toString(), { headers }).subscribe(
      (response) => {
        console.log(response);
        // for auto redirect to ss

        if(this.txnStatus==1){
          this.ss_return_url = params['Param6'];
          this.orn_no = params['OrderNumber'];
          this.pymt_status='F';
          this.rcpt_no='';
          this.rcpt_dt='';
          console.log("orn_no: ",this.orn_no);

          this.proceed();
        }
        
        // const data=response.data.trim();
        // const parts = data.split('_');
        // if (parts.length > 1) {
        //   //console.log(parts[1]);
        // this.warning_status=(parts[1]==='TRUE'||parts[1]==='true');

        // this.status=parts[0];

        // if(parts[0]=="P")
        //   this.paid=true;
        // }
        // You can process the response data here
      },
      (error) => {
        console.log("error");
        console.log(error);
        // Handle errors here
      }
    );
  }

  proceed():void{
    document.location.href = this.ss_return_url+'?orn_no='+this.orn_no+'&pymt_status='+this.pymt_status+'&rcpt_no='+this.rcpt_no+'&rcpt_dt='+this.rcpt_dt;
  }

}
