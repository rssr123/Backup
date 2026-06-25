import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-ssbilpaystatus',
  templateUrl: './ssbilpaystatus.component.html',
  styleUrls: ['./ssbilpaystatus.component.scss']
})
export class SsbilpaystatusComponent {
  jsonData: any = {};

  constructor(private route: ActivatedRoute) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      this.jsonData = {
        orn_no: params['orn_no'] || "",
        pymt_status: params['pymt_status'] || "",
        rcpt_no: params['rcpt_no'] === "null" || params['rcpt_no'] == null ? "" : params['rcpt_no'],
        rcpt_dt: params['rcpt_dt'] === "null" || params['rcpt_dt'] == null ? "" : params['rcpt_dt'],
        err_msg: params['err_msg'] || "" // Default empty error message
      };
    });
  }






}
