import { HttpClient } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';


@Component({
  selector: 'app-crsstatus',
  templateUrl: './crsstatus.component.html',
  styleUrls: ['./crsstatus.component.scss']
})
export class CrsstatusComponent implements OnInit{
  orn_no = String
  pymt_status= String
  rcpt_no=String
  rcpt_dt=String
  constructor(private route: ActivatedRoute,private router: Router
    ,private http: HttpClient){}

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      this.orn_no = params['orn_no'];
      this.pymt_status = params['pymt_status'];
      this.rcpt_no = params['rcpt_no'];
      this.rcpt_dt = params['rcpt_dt'];
      
  });

  // http://localhost:4300/crsstatus?orn_no=CRS202310201234643&pymt_status=A&rcpt_no=rpt11233&rcpt_dt=23-12-2023
}

}
