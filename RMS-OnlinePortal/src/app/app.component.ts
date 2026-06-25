import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { AuthService } from './services/auth.service';
import { IdleSessionService } from './services/idle-session.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss', '../styles.scss']
})
export class AppComponent implements OnInit {
  isAppInitialized = false;
  showTimeoutWarning = false;
  constructor(private authService: AuthService, private idleSessionService: IdleSessionService
  ) { }

  ngOnInit(): void {
    this.idleSessionService.startMonitoring(); // ⏱️ Start monitoring on app load

    window.addEventListener('show-idle-warning', () => {
      this.showTimeoutWarning = true;
    });
    this.authService.isInitialized.subscribe(initialized => {
      console.log("current staus:", initialized);
      this.isAppInitialized = initialized;
    });
  }

}

// import { Component, OnInit, OnDestroy } from '@angular/core';
// import { HttpClient, HttpHeaders } from '@angular/common/http';
// import { Subscription, interval, of } from 'rxjs';
// import { switchMap, catchError } from 'rxjs/operators';
// import { AuthService } from './services/auth.service';
// import { environment } from '../environments/environment';

// @Component({
//   selector: 'app-root',
//   templateUrl: './app.component.html',
//   styleUrls: ['./app.component.scss','../styles.scss']
// })
// export class AppComponent implements OnInit, OnDestroy {
//   isAppInitialized = false;
//   private hbSub?: Subscription;

//   constructor(private authService: AuthService, private http: HttpClient) {}

//   ngOnInit() {
//     console.log('loading');

//     // keep your existing init wiring
//     this.authService.isInitialized.subscribe(init => {
//       console.log('current status:', init);
//       this.isAppInitialized = init;
//     });

//     // ---- heartbeat setup ----
//     const heartbeatUrl = `${environment.apiUrl}/api/heartbeat`;

//     // Build headers:
//     // - Authorization must be a valid Basic header (you already store it in environment.authKey)
//     // - x-gson-statistics must be the same nonce used during login (persisted in localStorage)
//     const headers = () => {
//       const basic = environment.authKey; // e.g. "Basic cm95OnBhc3M="
//       const nonce = localStorage.getItem('nonce') || '';
//       return new HttpHeaders({
//         Authorization: basic,
//         'x-gson-statistics': nonce
//       });
//     };

//     // Fire once immediately
//     this.http.get(heartbeatUrl, { headers: headers(), withCredentials: true })
//       .pipe(catchError(err => {
//         console.error('❌ Initial heartbeat failed:', err);
//         return of(null);
//       }))
//       .subscribe(() => console.log('✅ Initial heartbeat sent'));

//     // Repeat every 10 minutes (must be < server session timeout)
//     this.hbSub = interval(10 * 60 * 1000)
//       .pipe(
//         switchMap(() =>
//           this.http.get(heartbeatUrl, { headers: headers(), withCredentials: true })
//             .pipe(catchError(err => {
//               console.error('❌ Heartbeat failed:', err);
//               return of(null);
//             }))
//         )
//       )
//       .subscribe(() => console.log('✅ Heartbeat sent'));
//   }

//   ngOnDestroy() {
//     this.hbSub?.unsubscribe();
//   }
// }
