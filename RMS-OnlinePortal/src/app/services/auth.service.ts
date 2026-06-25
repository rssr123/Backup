import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, catchError, tap, throwError } from 'rxjs';
import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { environment } from 'src/environments/environment';
import { Router } from '@angular/router';

@Injectable({
   providedIn: 'root'
})
export class AuthService {

    private isAuthenticatedSubject = new BehaviorSubject<boolean>(false);
    public isAuthenticated = this.isAuthenticatedSubject.asObservable();
    private initializedSubject = new BehaviorSubject<boolean>(false);
    public isInitialized = this.initializedSubject.asObservable();

    username: string = "anonymousUser";
    roles: string = "ANONYMOUS";
    name: string = "Unknown User";
    email: string = '';
    redirectUrl: string = '';

    constructor(private http: HttpClient, private router: Router) { }

    triggerRedirectUrl(url: string){
        this.redirectUrl = url;
    }

    // Method to get and clear redirect URL
    getAndClearRedirectUrl(): string | null {
        //const url = localStorage.getItem('redirectUrl');
        //console.log("this is the url before clean >>>", url);
        //localStorage.removeItem('redirectUrl'); // Clear after retrieval
        return this.redirectUrl;
    }

    login(): void {
        window.location.href = `${environment.apiUrl}/saml2/authenticate/ssm?redirectUrl=${environment.angularPortal}/home&gson=` + this.getNonce(); // + "&relayState=force";
        // window.location.href = environment.apiAuthUrl+"/saml2/authenticate/ssm";

    }

    logout(): void{
        // Open popup immediately (user-initiated action) to avoid browser blocking
        let logoutPopup: Window | null = null;
        if(environment.forceLogoutIDP) {
            const popupParam = 'menubar=no,directories=no,toolbar=no,scrollbars=yes,resizeable=0,location=no,top=96,left=949,width=420,height=450';
            logoutPopup = window.open(environment.idpLogoutEndpoint, "_ssologout", popupParam);
            console.log('Logout popup opened:', logoutPopup);
        }
        this.http.post(`${environment.apiUrl}/logout`, '')
            .subscribe({ 
                next: resp =>{
                    console.log(resp); 
                    this.logoutCleanup(logoutPopup);
                },
                error: error => {
                    console.log('Error loading logout app link!');
                    console.log(error);
                    this.logoutCleanup(logoutPopup);
                }
            });
    }

    logoutCleanup(logoutPopup: Window | null){
        this.getAndClearRedirectUrl();

        // ✅ Clear observables to show login button
        this.usernameSubject.next('');
        this.nameSubject.next('');
        this.emailSubject.next('');
        this.isAuthenticatedSubject.next(false);

        if(environment.forceLogoutIDP && logoutPopup) {
            this.startSecondLogout(logoutPopup);
        } else if(environment.forceLogoutIDP) {
            // Fallback to iframe method if popup failed
            this.startSecondLogout();
        } else {
            window.location.href =`${environment.ssm4uEndpoint}`;
            /*this.router.navigate(['/home'])
            .then(() => {
                window.location.reload();
            });*/
        }
    }

    startSecondLogout(logoutPopup?: Window | null){
        console.log('start init iframe logouthandler');
        
        if (logoutPopup) {
            // If we have a popup window, use it directly
            console.log('Using popup window for logout');
            setTimeout(() => {
                if (logoutPopup && !logoutPopup.closed) {
                    logoutPopup.close();
                }
                this.redirectToHome();
            }, 3000);
            return;
        }
        // Fallback to iframe method
        var element = document.createElement('iframe'); 
        element.setAttribute('src', environment.idpLogoutEndpoint);
        element.setAttribute('id', 'idpiframehandle');
        element.style.display = "none";
        document.body.appendChild(element);
        setTimeout(() => this.checkIframeLoaded(), 100);
        console.log('first logout timeout initialized');
    }

    private redirectToHome(): void {
        window.location.href = environment.angularPortal + '/home';
        window.location.reload();
    }
    /*
    startSecondLogout(){
        console.log('start init iframe logouthandler');
        var element = document.createElement('iframe'); 
        element.setAttribute('src', environment.idpLogoutEndpoint);
        element.setAttribute('id', 'idpiframehandle');
        element.style.display = "none";
        document.body.appendChild(element);
        setTimeout(this.checkIframeLoaded, 100);
        console.log('first logout timeout initialized');
    }
    */
    checkIframeLoaded = () => {
        var iframe: any = document.getElementById('idpiframehandle');
        var iframeDoc = iframe!.contentDocument;
        var popupParam = 'menubar=no,directories=no,toolbar=no,scrollbars=yes,resizeable=0,location=no,top=96,left=949,width=420,height=450';
        if(iframeDoc == null){
            try{
                console.log('test - before iframe contentWindow assignment');
                iframeDoc = iframe!.contentWindow.document;
                console.log('test - after iframe contentWindow assignment');

                if(iframeDoc.readyState == 'complete'){
                    this.redirectToHome();
                }
                else{
                    var child = window.open(environment.idpLogoutEndpoint, "_ssologout", popupParam);
                    setTimeout(function(){child!.close();}, 3000);
                    setTimeout(() => {
                        this.redirectToHome();
                    }, 5000);
                }

            }catch(error){
                console.log('Error loading iFrame!');
                console.log(error);
                var child = window.open(environment.idpLogoutEndpoint, "_ssologout", popupParam);
                setTimeout(function(){child!.close();}, 3000);
                setTimeout(() => {
                    this.redirectToHome();
                }, 5000);
            }
        }
        else if (iframeDoc.readyState == 'complete'){
            this.redirectToHome();
        }
        setTimeout(this.checkIframeLoaded, 100);
    }
    /*
    checkIframeLoaded() {
        var iframe: any = document.getElementById('idpiframehandle');
        var iframeDoc = iframe!.contentDocument;
        var popupParam = 'menubar=no,directories=no,toolbar=no,scrollbars=yes,resizeable=0,location=no,top=96,left=949,width=420,height=450';
        if(iframeDoc == null){
            try{
                console.log('test - before iframe contentWindow assignment');
                iframeDoc = iframe!.contentWindow.document;
                console.log('test - after iframe contentWindow assignment');

                if(iframeDoc.readyState == 'complete'){
                    window.location.href = environment.angularPortal + '/home';
                    window.location.reload();
                }
                else{
                    var child = window.open(environment.idpLogoutEndpoint, "_ssologout", popupParam);
                    setTimeout(function(){child!.close();}, 3000);
                    setTimeout(function(){
                        window.location.href = environment.angularPortal + '/home';
                        window.location.reload();
                    }, 5000);
                }

            }catch(error){
                console.log('Error loading iFrame!');
                console.log(error);
                var child = window.open(environment.idpLogoutEndpoint, "_ssologout", popupParam);
                setTimeout(function(){child!.close();}, 3000);
                setTimeout(function(){
                    window.location.href = environment.angularPortal + '/home';
                    window.location.reload();
                }, 5000);
            }
        }
        else if (iframeDoc.readyState == 'complete'){
            window.location.href = environment.angularPortal + '/home';
            window.location.reload();
        }
        setTimeout(this.checkIframeLoaded, 100);
    }
    */

    async checkLogin(): Promise<boolean> {
        const httpOptions = {
            headers: new HttpHeaders({
                Authorization: environment.authKey,
                'Content-Type': 'application/json',
        })};
        var resp = await this.http.get<any>(environment.apiUrl+'/loginInfo', httpOptions).toPromise();
        // var resp = await this.http.get<any>(environment.apiAuthUrl+'/loginInfo').toPromise();
   
        if (resp.username != null) {
            this.username = resp.username;
            this.roles = resp.roles;
            this.name = resp.name;
            this.email = resp.email;
        }
        //localStorage.setItem('username', this.username);
        //localStorage.setItem('roles', this.roles);
        //localStorage.setItem('name', this.name);

        const isAuthenticated = this.username !== "anonymousUser";
    
        this.isAuthenticatedSubject.next(isAuthenticated);  // Update the BehaviorSubject
       

        return (this.username == "anonymousUser" ? false : true);
    }

    checkAuthenticationStatus(): Observable<any> {
        const httpOptions = {
            headers: new HttpHeaders({
                Authorization: environment.authKey,
                'Content-Type': 'application/json',
        })};
        return this.http.get(environment.apiUrl+'/api/auth/details', httpOptions).pipe(
        // return this.http.get(environment.apiAuthUrl+'/api/auth/details').pipe(
            tap((response: any) => {
                console.log('Received response:', response);
                this.usernameSubject.next(response.username);
                this.authenticatedSubject.next(response.authenticated); 
                this.nameSubject.next(response.name);
                this.emailSubject.next(response.email);
            })
        );
    }

    usernameSubject = new BehaviorSubject<string>(''); //holds the username
    nameSubject = new BehaviorSubject<string>(''); //holds the name
    authenticatedSubject = new BehaviorSubject<string>(''); //holds the authentication status
    emailSubject = new BehaviorSubject<string>(''); //holds the email

    getUsername(): Observable<string> {
        return this.usernameSubject.asObservable();
    }

    getName(): Observable<string> {
        return this.nameSubject.asObservable();
    }
    
    getEmail(): Observable<string> {
        return this.emailSubject.asObservable();
    }

    getAuthenticated(): Observable<string> {
        return this.authenticatedSubject.asObservable();
    }

	getAuthenticationstatus(): boolean {
		return this.isAuthenticatedSubject.getValue();
    }

    setAuthenticated(status: boolean): void {
        this.isAuthenticatedSubject.next(status);
    }

    verifyAuthenticationStatus(): Promise<boolean> {
        return new Promise((resolve, reject) => {
            this.checkAuthenticationStatus().subscribe(
                response => {
                    if (response && response.username) {
                        console.log(response);
                        this.setAuthenticated(true);
                        resolve(true);
                    } else {
                        console.log(response);
                        this.setAuthenticated(false);
                        resolve(false);
                    }
                },
                error => {
                    console.error(error);
                    this.setAuthenticated(false);
                    reject(error);
                }
            );
        });
    }

    setInitialized(state: boolean): void {
        this.initializedSubject.next(state);
      }

    async initializeApp(): Promise<void> {
        try {
          await this.verifyAuthenticationStatus(); // Or any other method to check authentication      
          this.initializedSubject.next(true);
        } catch (error) {
          console.error('Failed to initialize app:', error);
          this.initializedSubject.next(true);
        }
      }

    private key = 'gson-statistics';

    getNonce(): string {
        let nonce = localStorage.getItem(this.key);
        if (!nonce || typeof nonce !== 'string' || nonce.length < 30) {
          nonce = this.generateNonce();
          localStorage.setItem(this.key, nonce);
        }
        else {
            const cleaned = nonce
                .replace(/\+/g, '-')
                .replace(/\//g, '_')
                .replace(/=+$/, '');
            if (cleaned !== nonce) {
                localStorage.setItem(this.key, cleaned);
                nonce = cleaned;
            }
        }
        return nonce;
    }

    private generateNonce(): string {
        const array = new Uint8Array(32);
        crypto.getRandomValues(array);

        // Base64URL encode
        let nonce = btoa(String.fromCharCode(...array))
            .replace(/\+/g, '-')   // replace + with -
            .replace(/\//g, '_')   // replace / with _
            .replace(/=+$/, '');   // strip padding =

        // Ensure first character is alphanumeric
        if (!/^[A-Za-z0-9]/.test(nonce)) {
            const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
            const randomChar = chars[Math.floor(Math.random() * chars.length)];
            nonce = randomChar + nonce.slice(1);
        }

        return nonce;
    }
}
