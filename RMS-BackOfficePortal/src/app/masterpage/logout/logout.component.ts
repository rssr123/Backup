import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { AuthService } from '../../core/services/auth.service';
import { Router } from '@angular/router';

@Component({
   selector: 'app-logout',
   templateUrl: './logout.component.html',
   styleUrls: ['./logout.component.scss']
})
export class LogoutComponent implements OnInit {
   constructor(private authService : AuthService, private router: Router, private http: HttpClient) { 
   }
	
   ngOnInit() {
      this.authService.logout();
      //this.router.navigate(['/']);
   }

}