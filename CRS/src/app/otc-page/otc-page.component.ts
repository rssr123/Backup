import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-otc-page',
  templateUrl: './otc-page.component.html',
  styleUrls: ['./otc-page.component.scss']
})
export class OtcPageComponent implements OnInit {
  message!:String|null;

  constructor(private route: ActivatedRoute, private router: Router) { } // Inject ActivatedRoute

  ngOnInit(): void {
    // Retrieve the parameter from the route
    this.message = this.route.snapshot.paramMap.get('message');
    console.log('Received Message:', this.message);
  }

  goBack() {
    this.router.navigate(['/crssample']);
  }
}
