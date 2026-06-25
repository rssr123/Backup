import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, Router, ActivatedRoute } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {
  
  constructor(private authService: AuthService, private router: Router,private route: ActivatedRoute) {}

  async canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Promise<boolean> {
    console.log("AuthGuard#canActivate started");
    if (route.data && route.data['noAuthCheck']) {
      this.authService.setInitialized(true);
      return true;
    }

    // try {

    //   const isAuthenticated = await this.authService.verifyAuthenticationStatus();
    //   if (isAuthenticated) {
    //     this.authService.setInitialized(true);     
    //     return true;
    //   } else {
    //     // window.location.href = environment.apiAuthUrl+'/saml2/authenticate/ssm';
    //     window.location.href = environment.apiAuthUrl+'/saml2/sso/ssm';
    //     this.authService.setInitialized(false);
    //     return false;
    //   }
    // } catch (error) {
    //   // window.location.href = environment.apiAuthUrl+'/saml2/authenticate/ssm';
    //   window.location.href = environment.apiAuthUrl+'/saml2/sso/ssm';
    //   console.log("Error occurred:", error);
    //   this.authService.setInitialized(false);
    //   return false;
    // }

    try {

      let url=state.url
      let urlParams = new URLSearchParams(url.split('?')[1]);

      this.route.queryParams.subscribe(params => {
        localStorage.setItem('pID', params['pr']);
      });
      
      //localStorage.setItem('redirectUrl', state.url);
      this.authService.triggerRedirectUrl(state.url);
      
      const isAuthenticated =
        await this.authService.verifyAuthenticationStatus();
      if (isAuthenticated) {
        this.authService.setInitialized(true);
        return true;
      } else {       
        window.location.href = `${environment.apiUrl}/saml2/authenticate/ssm?redirectUrl=${environment.angularPortal}${state.url}&gson=` + this.authService.getNonce() + "&relayState=force";
        // `${environment.apiAuthUrl}/saml2/authenticate/ssm?redirectUrl=${environment.angularPortal}${state.url}`;
        // `${environment.apiAuthUrl}/saml2/sso/azure?redirectUrl=${environment.angularPortal}${state.url}`;

        this.authService.setInitialized(false);
        return false;
      }
    } catch (error) {
      window.location.href =`${environment.apiUrl}/saml2/authenticate/ssm?redirectUrl=${environment.angularPortal}${state.url}&gson=`+ this.authService.getNonce() + "&relayState=force";
      // window.location.href =`${environment.apiAuthUrl}/saml2/authenticate/ssm?redirectUrl=${environment.angularPortal}${state.url}`;
      // window.location.href =`${environment.apiAuthUrl}/saml2/sso/azure?redirectUrl=${environment.angularPortal}${state.url}`;
      console.log('Error occurred:', error);
      this.authService.setInitialized(false);
      return false;
    }
  }
}
