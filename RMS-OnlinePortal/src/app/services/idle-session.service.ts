import { Injectable, NgZone } from '@angular/core';
import { fromEvent, interval, merge, Subscription } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { environment } from 'src/environments/environment';
import { AuthService } from './auth.service';

const IDLE_TIMEOUT_MINUTES = 15;
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
        private zone: NgZone
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

                // Log idle countdown for debugging/visibility
                const timeoutInSeconds = IDLE_TIMEOUT_MINUTES * 60;
                const elapsedSeconds = (now - startedTime) / 1000;
                const remainingSeconds = Math.max(0, timeoutInSeconds - elapsedSeconds);
                // console.log(`User idle for ${Math.round(elapsedSeconds)}s. Timeout in ${Math.round(remainingSeconds)}s.`);

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

                    this.zone.run(() => {
                        window.dispatchEvent(new Event('show-idle-warning')); // Show modal in current tab
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
