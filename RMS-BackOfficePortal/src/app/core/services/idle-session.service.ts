import { Injectable, NgZone } from '@angular/core';
import { fromEvent, interval, merge, Subscription } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { environment } from 'src/environments/environment';
import { AuthService } from 'src/app/core/services/auth.service';
import { debounceTime } from 'rxjs/operators';
import { TriggerNotificationUpdateService } from 'src/app/core/services/TriggerNotificationUpdateService.service';


// @Injectable({ providedIn: 'root' })
// export class IdleSessionService {
//   //right now, we have spring tomcat timeout, default is 30minutes. but i want to use UI to control the logout
//   //maximum 20minutes as for now. the 20minutes
//   private sessionCheckInterval = 1200000; //<--this is miniseconds
//   private timerSub?: Subscription;
//   private alertShown = false;

//   constructor(private http: HttpClient, private router: Router, private authService: AuthService ) {}

//   startMonitoring() {
//     if (this.timerSub) return;
  
//     this.timerSub = interval(this.sessionCheckInterval).subscribe(() => {
//       // ✅ Only proceed if user is logged in
//       if (!this.alertShown && this.authService.getAuthenticationstatus()) {
//         this.http.get(`${environment.apiUrl}/api/session/v1/status`, { responseType: 'text' }).subscribe({
//           next: () => {
//             this.alertShown = true;
  
//            // 🔄 Convert interval to minutes (rounded to 1 decimal place)
//            const idleMinutes = Math.floor(this.sessionCheckInterval / 60000);


//            alert(`You have been idle for ${idleMinutes} minute(s). The system will now log you out.`);
 
  
//             // ✅ Logout and redirect to home
//             this.router.navigate(['/logout']);
//           },
//           error: (err) => {
//             if (err.status === 401) {
//               // ❌ Session already expired, redirect immediately
//               this.alertShown = true;
//               this.router.navigate(['/home']).then(() => window.location.reload());
//             }
//           }
//         });
//       }
//     });
//   }
  
  

//   stopMonitoring() {
//     this.timerSub?.unsubscribe();
//     this.timerSub = undefined;
//     this.alertShown = false;
//   }
// }


const IDLE_TIMEOUT_MINUTES = 20;
const REFRESH_NOTIFICATION_MINUTES = 1;
const IDLE_STORAGE_KEY = 'user-tab-start';
const LOGOUT_SYNC_KEY = 'idle-logout-trigger';

@Injectable({ providedIn: 'root' })
export class IdleSessionService {
  private sessionCheckInterval = 10000; // check every 10 seconds
  private timerSub?: Subscription;
  private alertShown = false;

  constructor(
    private http: HttpClient,
    private router: Router,
    private authService: AuthService,
    private zone: NgZone,
    private tnu: TriggerNotificationUpdateService
  ) {
    this.resetStartTime(); // Reset time when this service is created
    this.listenToStorageChanges(); // Set up multi-tab sync listener
      this.monitorRouteChanges(); // 
  }

  startMonitoring() {
    if (this.timerSub) return;

    this.zone.runOutsideAngular(() => {
      this.timerSub = interval(this.sessionCheckInterval).subscribe(() => {
        const started = localStorage.getItem(IDLE_STORAGE_KEY);
        const startedTime = started ? parseInt(started, 10) : Date.now();
        const now = Date.now();
        const diffInMinutes = (now - startedTime) / 60000;

        //if(diffInMinutes >= REFRESH_NOTIFICATION_MINUTES)
        //  this.tnu.emitEvent('trigger');

        if (
          diffInMinutes >= IDLE_TIMEOUT_MINUTES &&
          !this.alertShown &&
          this.authService.getAuthenticationstatus()
        ) {
          this.alertShown = true;

          // Store a timestamp in localStorage to notify all tabs
          localStorage.setItem(LOGOUT_SYNC_KEY, Date.now().toString());

          // Verify session is still valid before displaying warning
          this.http.get(`${environment.apiUrl}/api/session/v1/status`, { responseType: 'text' }).subscribe({
            next: () => {
              this.zone.run(() => {
                window.dispatchEvent(new Event('show-idle-warning')); // Show modal in current tab
                this.tnu.emitEvent('trigger');
              });
            },
            error: () => {
              this.zone.run(() => {
                this.router.navigate(['/home']).then(() => window.location.reload());
              });
            }
          });
        }
      });
    });
  }

  stopMonitoring() {
    this.timerSub?.unsubscribe();
    this.timerSub = undefined;
    this.alertShown = false;
  }

  private monitorRouteChanges() {
  this.router.events.subscribe(() => {
    this.resetStartTime(); // ⏱ Reset idle timer on every navigation
  });
}


  private resetStartTime() {
    localStorage.setItem(IDLE_STORAGE_KEY, Date.now().toString());
  }

  private listenToStorageChanges() {
    window.addEventListener('storage', (event: StorageEvent) => {
      if (event.key === LOGOUT_SYNC_KEY) {
        this.zone.run(() => {
          if (this.authService.getAuthenticationstatus()) {
            // Fire modal on other tabs
            window.dispatchEvent(new Event('show-idle-warning'));
          }
        });
      }
    });
  }
}
