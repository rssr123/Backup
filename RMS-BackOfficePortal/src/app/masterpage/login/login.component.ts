import { Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { AuthService } from '../../core/services/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})

export class LoginComponent {
	constructor(private authService : AuthService, private router : Router, private http: HttpClient) {
	}
  	ngOnInit() {
			// Check if already logged in first
	  	/*
			const storedUsername = this.authService.username;
			if(storedUsername && storedUsername !== 'anonymousUser' && storedUsername !== '') {
				// Already logged in, redirect to home
				this.router.navigate(['/home']);
			} else {
				// Trigger SAML login
				this.authService.login();
			}*/
  	}
}