import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, ReplaySubject, catchError, tap, throwError, timer, retry, delayWhen } from 'rxjs';
import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { PerformanceService } from './performance.service';
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

    private authCheckPromise: Promise<boolean> | null = null; // ✅ Cache the authentication check
    username: string = '';
    roles: string = '';
    name: string = '';
    email: string = '';
    isLoggedOut: boolean = true;
    redirectUrl: string = '';

    // ✅ Add cache expiration for user data
    private userCacheExpiry: number = 0;
    private authCacheExpiry: number = 0;
    private readonly CACHE_DURATION = 5 * 60 * 1000; // 5 minutes cache

    private authDetailsSubject = new ReplaySubject<any>(1); // ✅ Caches latest authentication response
    authDetails$ = this.authDetailsSubject.asObservable();
    private hasFetchedAuth = false; // ✅ Prevents duplicate API calls within the session

    private userDetailsSubject = new ReplaySubject<any>(1); // ✅ Cache latest user details
    userDetails$ = this.userDetailsSubject.asObservable();  // ✅ Expose as observable for components
    private hasFetchedUser = false; // ✅ Prevents duplicate API calls within the session


    constructor(private http: HttpClient, private performanceService: PerformanceService, private router: Router) { 
        this.getNonce()
    }

    // ✅ Retry utility for authentication API calls
    private retryAuth<T>(source: Observable<T>): Observable<T> {
        return source.pipe(
            retry({
                count: 2, // Retry up to 2 times
                delay: (error: HttpErrorResponse, retryCount: number) => {
                    // Only retry on network errors, not auth failures
                    if (error.status === 401 || error.status === 403) {
                        return throwError(() => error); // Don't retry auth failures
                    }
                    
                    // Exponential backoff: 1s, 2s
                    const delayMs = Math.pow(2, retryCount) * 1000;
                    console.log(`Retrying auth API call in ${delayMs}ms (attempt ${retryCount + 1})`);
                    return timer(delayMs);
                }
            })
        );
    }

    // ✅ Method to manually clear authentication cache
    clearAuthCache(): void {
        this.hasFetchedUser = false;
        this.hasFetchedAuth = false;
        this.userCacheExpiry = 0;
        this.authCacheExpiry = 0;
        this.authCheckPromise = null;
        console.log("Authentication cache cleared");
    }

    triggerRedirectUrl(url: string){
        this.redirectUrl = url;
    }

    // Method to get and clear redirect URL
    getAndClearRedirectUrl(): string | null {
        //const url = '/home'; //localStorage.getItem('redirectUrl');
        console.log('this is the url before clean >>>', this.redirectUrl);
        //localStorage.removeItem('redirectUrl'); // Clear after retrieval
        return this.redirectUrl;
    }

    // Modify login method to handle redirect after successful login
    handleSuccessfulLogin(): void {
        const redirect = this.getAndClearRedirectUrl();
        console.log("this is the url >>>", redirect);
        if (redirect) {

            window.location.href = `${environment.angularPortal}${redirect}`;

        }
        // else {
        //     window.location.href = 'https://localhost:4200';
        // }
    }


    login(): void {
       
        window.location.href = `${environment.apiUrl}/saml2/authenticate/ssm?redirectUrl=${environment.angularPortal}/home&gson=` + this.getNonce(); // + "&relayState=force";
        //window.location.href = `${environment.apiUrl}/login/saml2/sso/ssm?gson=` + this.getNonce() + "&relayState=force";
        //window.location.href = `${environment.apiAuthUrl}/saml2/authenticate/ssm`;
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
                window.location.href = environment.idpLogoutEndpoint;
                /*var child = window.open(environment.idpLogoutEndpoint, "_ssologout", popupParam);
                setTimeout(function(){child!.close();}, 3000);
                setTimeout(() => {
                    this.redirectToHome();
                }, 5000);*/
            }
        }
        else if (iframeDoc.readyState == 'complete'){
            this.redirectToHome();
        }

        setTimeout(this.checkIframeLoaded, 100);
    }

   async checkLogin(forceRefresh: boolean = false): Promise<boolean> {
    const now = Date.now();
    
    // ✅ Check cache expiry instead of just hasFetchedUser flag
    if (this.hasFetchedUser && !forceRefresh && now < this.userCacheExpiry) {
        console.log("Using cached user authentication status");
        return this.isAuthenticatedSubject.getValue(); // ✅ Return cached authentication status
    }
    try {
        const httpOptions = {
            headers: new HttpHeaders({
                Authorization: environment.authKey,
                'Content-Type': 'application/json',
        })};
        
        // ✅ Add retry logic for the API call
        const resp = await this.retryAuth(
            this.http.get<any>(`${environment.apiUrl}/loginInfo`, httpOptions)
        ).toPromise();
        //const resp = await this.http.get<any>(`${environment.apiAuthUrl}/loginInfo`).toPromise();

        if (resp && resp.username) {
            //console.log(resp)
            this.username = resp.username;
            this.roles = resp.roles;
            this.name = resp.name;
            this.email = resp.email;
            this.isLoggedOut = false;
    
            //console.log(resp)

            // ✅ Update cache
            this.userDetailsSubject.next(resp);
            this.hasFetchedUser = true; // ✅ Prevents duplicate API calls
            this.userCacheExpiry = Date.now() + this.CACHE_DURATION; // ✅ Set cache expiry
        }

        // ✅ Update authentication status
        const isAuthenticated = !!(this.username && 
                               this.username.toLowerCase().trim() !== "anonymoususer" && 
                               this.username.toLowerCase().trim() !== "anonymous" &&
                               this.username.trim() !== "");
        
        if (isAuthenticated) {
            // ✅ Update observables for authenticated users only
            this.usernameSubject.next(this.username);
            this.nameSubject.next(this.name);
            this.emailSubject.next(this.email);
            this.rolesSubject.next(this.roles);
            this.isLoggedOut = false;
        } else {
            // ✅ Clear localStorage and observables for unauthenticated users
            
            this.username = '';
            this.name = '';
            this.email = '';
            this.roles = '';

            this.usernameSubject.next('');
            this.nameSubject.next('');
            this.emailSubject.next('');
            this.rolesSubject.next('');
            this.isLoggedOut = true;
        }
        
        this.isAuthenticatedSubject.next(isAuthenticated);

        return isAuthenticated;
    } catch (error) {
        console.error("Error checking login info", error);
        
        // ✅ Only clear state if it's a 401/403 (authentication error)
        const httpError = error as HttpErrorResponse;
        if (httpError.status === 401 || httpError.status === 403) {
            console.log("Authentication failed - clearing user state");
            this.hasFetchedUser = false;
            this.userCacheExpiry = 0; // ✅ Reset cache expiry
            this.username = '';
            this.name = '';
            this.email = '';
            this.roles = '';
            this.isLoggedOut = true;
            
            this.usernameSubject.next('');
            this.nameSubject.next('');
            this.emailSubject.next('');
            this.rolesSubject.next('');
            this.isAuthenticatedSubject.next(false);
            
            return false;
        } else {
            // ✅ For network errors or temporary issues, keep existing state
            console.warn("Temporary error checking login - keeping current state", error);
            return this.isAuthenticatedSubject.getValue(); // Return cached state
        }
    }
}

    
checkAuthenticationStatus(forceRefresh: boolean = false): Observable<any> {
    const now = Date.now();
    
    // ✅ Check cache expiry instead of just hasFetchedAuth flag
    if ((forceRefresh || !this.hasFetchedAuth || now >= this.authCacheExpiry)) { // ✅ Force API if requested or cache expired
        this.hasFetchedAuth = true;
        this.authCacheExpiry = now + this.CACHE_DURATION; // ✅ Set cache expiry
        this.hasFetchedAuth = true;
        const httpOptions = {
            headers: new HttpHeaders({
                Authorization: environment.authKey,
                'Content-Type': 'application/json',
        })};
        
        return this.retryAuth(
            this.http.get<any>(`${environment.apiUrl}/api/auth/details`, httpOptions)
        ).pipe(
        // return this.http.get<any>(`${environment.apiAuthUrl}/api/auth/details`).pipe(
            tap(response => {
                this.authDetailsSubject.next(response); // ✅ Update cache
                this.usernameSubject.next(response.username);
                this.nameSubject.next(response.name);
                this.emailSubject.next(response.email);
                this.isLoggedOut = false;
                //console.log(response)
            }),
            catchError(error => {
                console.error('Error fetching authentication status', error);
                const httpError = error as HttpErrorResponse;
                
                // ✅ Only reset cache on actual auth failures, not network issues
                if (httpError.status === 401 || httpError.status === 403) {
                    this.hasFetchedAuth = false;
                    this.authCacheExpiry = 0; // ✅ Reset cache expiry
                    console.log("Authentication failed - resetting auth cache");
                } else {
                    console.warn("Temporary error fetching auth status - keeping cache");
                }
                return throwError(() => error);
            })
        );
    }
    return this.authDetails$; // ✅ Return cached response
}


    usernameSubject = new BehaviorSubject<string>(''); //holds the username
    nameSubject = new BehaviorSubject<string>(''); //holds the username
    emailSubject = new BehaviorSubject<string>(''); //holds the email
    rolesSubject = new BehaviorSubject<string>(''); //holds the roles

    getUsername(): Observable<string> {
        return this.usernameSubject.asObservable();
    }

    getName(): Observable<string> {
        return this.nameSubject.asObservable();
    }

    getEmail(): Observable<string> {
        return this.emailSubject.asObservable();
    }

    getRoles(): Observable<string> {
        return this.rolesSubject.asObservable();
    }

    getAuthenticationstatus(): boolean {
        return this.isAuthenticatedSubject.getValue();
    }

    setAuthenticated(status: boolean): void {
        this.isAuthenticatedSubject.next(status);
    }

  verifyAuthenticationStatus(): Promise<boolean> {
    if (this.authCheckPromise) {
        return this.authCheckPromise; // ✅ Return cached promise instead of calling API again
    }

    this.authCheckPromise = new Promise((resolve, reject) => {
        this.checkAuthenticationStatus().subscribe(
            async response => {
                if (response && response.username) {
                    if (!this.hasFetchedUser) {  
                        await this.checkLogin(); 
                    }
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

    return this.authCheckPromise;
}

    setInitialized(state: boolean): void {
        this.initializedSubject.next(state);
    }

    async initializeApp(): Promise<void> {
        try {
            await this.performanceService.measurePerformanceAsync(
                () => this.verifyAuthenticationStatus(),
                'verifyAuthenticationStatus'
            );

            this.initializedSubject.next(true);
        } catch (error) {
            console.error('Failed to initialize app:', error);
            this.initializedSubject.next(true);
        }
    }

    checkUserRole(i_username: string, i_perm_cd: any): Observable<any> {
        const permUrl = environment.apiUrl + '/api/RPC/v1/checkuserrole';
        // const permUrl = environment.apiAuthUrl + '/api/RPC/v1/checkuserrole';
        const headers = new HttpHeaders({
            Authorization: environment.authKey,
            'Content-Type': 'application/json',
        });

        const bodyPermTCDList: any = {
            i_username: i_username,
            i_perm_cd: i_perm_cd,
        };

        return this.http.post(permUrl, bodyPermTCDList, { headers });
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
