import { Injectable } from '@angular/core';
import {
  CanActivate,
  ActivatedRouteSnapshot,
  RouterStateSnapshot,
  Router,
} from '@angular/router';
import { AuthService } from '../services/auth.service';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root',
})
export class AuthGuard implements CanActivate {
  constructor(private authService: AuthService, private router: Router) {}

  async canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): Promise<boolean> {
    console.log('AuthGuard#canActivate started');
    if (route.data && route.data['noAuthCheck']) {
      this.authService.setInitialized(true);
      return true;
    }

    try {
      //localStorage.setItem('redirectUrl', state.url);
      //localStorage.setItem('redirectUrl', "/home");
      this.authService.triggerRedirectUrl('/home');
      const isAuthenticated =
        await this.authService.verifyAuthenticationStatus();
      if (isAuthenticated) {
        this.authService.setInitialized(true);
        return true;
      } else {       
        window.location.href =
        //`${environment.apiUrl}/login/saml2/sso/ssm?redirectUrl=${environment.angularPortal}${state.url}`;
        `${environment.apiAuthUrl}/saml2/authenticate/ssm?redirectUrl=${environment.angularPortal}${state.url}&gson=` + this.authService.getNonce();

        this.authService.setInitialized(false);
        return false;
      }
    } catch (error) {
      //window.location.href =`${environment.apiUrl}/login/saml2/sso/ssm?redirectUrl=${environment.angularPortal}${state.url}`;
      window.location.href =`${environment.apiAuthUrl}/saml2/authenticate/ssm?redirectUrl=${environment.angularPortal}${state.url}&gson=` + this.authService.getNonce();

      console.log('Error occurred:', error);
      this.authService.setInitialized(false);
      return false;
    }
  }
}
