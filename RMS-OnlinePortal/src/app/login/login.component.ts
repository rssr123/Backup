import { Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { AuthService } from '../services/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})

export class LoginComponent {
	constructor(private authService : AuthService, private router : Router, private http: HttpClient) {
	}
  	ngOnInit() {

		setTimeout(() => {
			this.router.navigate(['/home']);
		}, 1000);
	  	// if(localStorage.getItem('username') == null || localStorage.getItem('username') == 'anonymousUser')
		// 	this.authService.login();
		// this.router.navigate(['/']);
  	}
  
}