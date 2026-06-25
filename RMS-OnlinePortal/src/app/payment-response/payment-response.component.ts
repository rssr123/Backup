import { Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-payment-response',
  templateUrl: './payment-response.component.html',
  styleUrls: ['./payment-response.component.scss']
})
export class PaymentResponseComponent {

  result!:string;
  pymt_status!: string;
  orn_no!: string;
  rcpt_no!: string;
  rcpt_dt!: string;
  ss_return_url!:string;
  show:boolean=true;

  isPaymentSuccess: boolean = false;
  isPaymentFailed: boolean = false;
  isPaymentPending: boolean = false;

  constructor(private route: ActivatedRoute) {

  }

  ngOnInit(): void {

    this.route.queryParams.subscribe(params => {
      this.orn_no = params['orn_no'];
      //this.pymt_status = params['pymt_status'];
      this.rcpt_dt = params['rcpt_dt'];
      this.rcpt_no = params['rcpt_no'];
      this.ss_return_url=params['ss_return_url'];

      const parts = params['pymt_status'].split('_');
      this.pymt_status= parts[0];
      console.log("pymt_status[0]: ",parts[0]);
      console.log("pymt_status[1]: ",parts[1]);

      // if(this.rcpt_no==null || this.rcpt_no==''){
      //   this.show=false;
      // }else{
      //   this.show=true;
      // }

      // Determine which screen to display based on payment status
      if (this.pymt_status === 'P') {
        this.isPaymentSuccess = true;
        this.isPaymentFailed = false;
        this.isPaymentPending = false;
      } else if (this.pymt_status === 'F') {
        this.isPaymentSuccess = false;
        this.isPaymentFailed = true;
        this.isPaymentPending = false;
      } else if (this.pymt_status === 'PP') {
        this.isPaymentSuccess = false;
        this.isPaymentFailed = false;
        this.isPaymentPending = true;
      }

      //console.log("receive orn no from java backend: ",this.result);
      //this.getPaymentDetail(this.orn_no);
    });
  }

  proceed():void{
    if(this.orn_no.startsWith('CTL')){
      document.location.href = this.ss_return_url
    }
    else{
      //console.log('https://'+this.ss_return_url+'?orn_no='+this.orn_no+'&pymt_status='+this.pymt_status+'&rcpt_no='+this.rcpt_no+'&rcpt_dt='+this.rcpt_dt);
      document.location.href = this.ss_return_url+'?orn_no='+this.orn_no+'&pymt_status='+this.pymt_status+'&rcpt_no='+this.rcpt_no+'&rcpt_dt='+this.rcpt_dt;
    }
  }

}


