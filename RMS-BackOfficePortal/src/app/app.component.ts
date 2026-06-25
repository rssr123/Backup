import { Component, OnInit, HostListener, ChangeDetectorRef } from '@angular/core';
import { AuthService } from './core/services/auth.service';
import { ActivatedRoute, ActivationEnd, ActivationStart, NavigationEnd, Router } from '@angular/router';
import { NotificationService } from './core/services/notification.service';
import { filter, map } from 'rxjs/operators';
// import { NgDynamicBreadcrumbService } from 'ng-dynamic-breadcrumb';
import { TranslateService } from '@ngx-translate/core';
import { Breadcrumb} from './core/models/entity';
import { LanguageService } from './language.service';
import { IdleSessionService } from './core/services/idle-session.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app_v2.component.scss', '../styles.scss']
})
export class AppComponent implements OnInit {
  isAppInitialized = false;
  username: string | null = null;
  isHomePage = false;
  isOTCCollectionandReceipting = false;
  isOTCReturnedCheque = false;
  isOTCEmvReconciliation = false;
  isDashboard = false;

  isOtcCheckIn = false;
  isOtcCheckOut = false;
  isBillingCancellationSearch = false;
  isBillingAdjustmentSearch = false;
  isOTCReceiptScreen = false;
  isAccessDenied = false;
  isReceiptReprint = false;
  isOTCReceiptCancellation = false;
  isPgreconlisting = false;
  isBankReconListing = false;
  isReprintReceiptJustification = false;

  isOTCDBal = false;
  isOTCDSBal = false;
  isOTCMBal = false;

  breadcrumbs: Breadcrumb[] = [];
  isLoggedIn: boolean = false; // Track login status

  showTimeoutWarning = false;

  constructor(
    private authService: AuthService,
    private route: ActivatedRoute,
    private router: Router,
    private notificationService: NotificationService,
    // private breadcrumbService: NgDynamicBreadcrumbService,
    private translate: TranslateService,
    private cd: ChangeDetectorRef,
    private languageService: LanguageService, // Inject the LanguageService
    private idleSessionService: IdleSessionService
  ) {

  }

  ngOnInit(): void {
    this.idleSessionService.startMonitoring(); // ⏱️ Start monitoring on app load

    window.addEventListener('show-idle-warning', () => {
      this.showTimeoutWarning = true;
    });

    // Subscribe to authentication initialization
    this.authService.isInitialized.subscribe(initialized => {
      this.isAppInitialized = initialized;
      if (initialized) {
        // ✅ Check if user ended up on dashboard and redirect to home
        // this.authService.ensureCorrectRouting(); // Commented out - method doesn't exist
      }
    });

    this.router.events.pipe(
      filter(event => event instanceof NavigationEnd)
    ).subscribe(() => {
      // Check if the current route is the homepage
      this.isHomePage = this.router.url === '/home';
      this.isDashboard = this.router.url === '/dashboard';

      this.isOTCCollectionandReceipting = this.router.url === '/otc-collection-receipting';
      this.isOTCReturnedCheque = this.router.url === '/otc-returned-cheque';
      this.isOTCEmvReconciliation = this.router.url === '/otc-emv-reconciliation';
      this.isOtcCheckIn = this.router.url === '/otc-checkin';
      this.isOtcCheckOut = this.router.url === '/otc-checkout';
      this.isBillingCancellationSearch = this.router.url === '/billing-cancellation-search';
      this.isBillingAdjustmentSearch = this.router.url === '/billing-adjustment-search';
      this.isOTCReceiptScreen = this.router.url.startsWith('/otc-receipt-screen');
      this.isAccessDenied = this.router.url === '/access-denied';
      this.isReceiptReprint = this.router.url === '/reprintreceipt';
      this.isOTCReceiptCancellation = this.router.url.startsWith('/rcpt-no-vld');
      this.isPgreconlisting = this.router.url === '/pgrecon-listing';
      this.isOTCDBal = this.router.url === '/daily-balancing-listing';
      this.isOTCDSBal = this.router.url === '/daily-balancing-listing?success=true';
      this.isOTCMBal = this.router.url === '/master-balancing-listing';
      this.isBankReconListing = this.router.url === '/bank-recon-listing';
      this.isReprintReceiptJustification = this.router.url === '/reprintreceiptjustification';

    });

    // Subscribe to language changes
    this.languageService.language$.subscribe(language => {
      //console.log(language);
      this.translate.use(language); // Change the language
      this.updateBreadcrumbs(); // Update breadcrumbs
    });

        // Subscribe to app initialization
        this.authService.isInitialized.subscribe(initialized => {
          this.isAppInitialized = initialized;
        });
    
        // Subscribe to authentication status
        this.authService.isAuthenticated.subscribe(authStatus => {
          this.isLoggedIn = authStatus;
        });
    
        // Initialize app if not already initialized
        this.authService.initializeApp();

    // Load notifications after a short delay to reduce initial load
      setTimeout(() => {this.loadNotifications();}, 500); // Short 0.5 second delay

    this.notificationService.$notificationMyTask.subscribe(notificationMyTaskCount => {
      //console.log("Notification my task count updated:", notificationMyTaskCount);
    });

    this.notificationService.$notificationCreatedTask.subscribe(notificationCreatedTaskCount => {
      //console.log("Notification created task count updated:", notificationCreatedTaskCount);
    });

    // Wilson's
    // OTC-RC
    //this.notificationService.getOtcRCAssignedTaskNotificationCount();
    //this.notificationService.getOtcRCCreatedTaskNotificationCount();

    this.notificationService.$notificationOctRCAssignedTasks.subscribe(notificationOctRCAssignedTaskCount => {
      //console.log("Notification Oct RC assigned task count updated:", notificationOctRCAssignedTaskCount);
    });

    this.notificationService.$notificationOctRCCreatedTasks.subscribe(notificationOctRCCreatedTaskCount => {
      //console.log("Notification Oct RC created task count updated:", notificationOctRCCreatedTaskCount);
    });

    // Refund
    //this.notificationService.getRefundAssignedTaskNotificationCount();
    //this.notificationService.getRefundCreatedTaskNotificationCount();

    this.notificationService.$notificationRefundAssignedTasks.subscribe(notificationRefundAssignedTaskCount => {
      //console.log("Notification Refund assigned task count updated:", notificationRefundAssignedTaskCount);
    });

    this.notificationService.$notificationRefundCreatedTasks.subscribe(notificationRefundCreatedTaskCount => {
      //console.log("Notification Refund created task count updated:", notificationRefundCreatedTaskCount);
    });

    // Billing
    //this.notificationService.getBillingAssignedTaskNotificationCount();
    //this.notificationService.getBillingCreatedTaskNotificationCount();

    this.notificationService.$notificationBillingAssignedTasks.subscribe(notificationBillingAssignedTaskCount => {
      //console.log("Notification Billing assigned task count updated:", notificationBillingAssignedTaskCount);
    });

    this.notificationService.$notificationBillingCreatedTasks.subscribe(notificationBillingCreatedTaskCount => {
      //console.log("Notification Billing created task count updated:", notificationBillingCreatedTaskCount);
    });

    // Credit Control
    //this.notificationService.getCCCAssignedTaskNotificationCount();
    //this.notificationService.getCCCCreatedTaskNotificationCount();

    this.notificationService.$notificationCCCAssignedTasks.subscribe(notificationCCCAssignedTaskCount => {
      //console.log("Notification Credit Control assigned task count updated:", notificationCCCAssignedTaskCount);
    });

    this.notificationService.$notificationBillingCreatedTasks.subscribe(notificationCCCCreatedTaskCount => {
      //console.log("Notification Credit Control created task count updated:", notificationCCCCreatedTaskCount);
    });

  }

  loadNotifications(){
    if(this.authService.username != null && this.authService.username != '')
      this.notificationService.getTaskNotificationCounts(this.authService.username);
    else
      setTimeout(()=>{this.loadNotifications();}, 500);
  }

  @HostListener('window:storage', ['$event'])
  onStorageEvent(event: StorageEvent) {
    if (event.key === 'loggedOut' && event.newValue === 'true') {
      this.router.navigate(['/home']).then(() => {
        window.location.reload();
      });
    }
  }

  private updateBreadcrumbs() {    
    this.router.events
      .pipe(
        filter(event => event instanceof ActivationEnd),
        map(() => this.route),
        map(route => {
          while (route.firstChild) {
            route = route.firstChild;
          }
          return route;
        }),
        filter(route => route.outlet === 'primary'),
        map(route => route.snapshot.data['breadcrumb'])
      )
      .subscribe((breadcrumbData: Array<any>) => {
        if (breadcrumbData) {
          this.breadcrumbs = breadcrumbData;
          // breadcrumbData.forEach((crumb: any) => {
            // const translationKey = crumb.translatedKey;
            // console.log(translationKey)
            // this.translate.get(translationKey).subscribe((translatedLabel: string) => {
              // crumb.label = translatedLabel; // Update the label with the translated value
              // this.breadcrumbService.updateBreadcrumbLabels({ [crumb.label]: translatedLabel });
              // this.cd.detectChanges(); // Trigger change detection to update the view
            // });
          // });
        }
      });
  }

  isLastBreadcrumb(breadcrumb: any): boolean {
    return this.breadcrumbs.indexOf(breadcrumb) === this.breadcrumbs.length - 1;
  }
  
}


// import { Component, OnInit, ChangeDetectorRef, HostListener } from '@angular/core';
// import { AuthService } from './core/services/auth.service';
// import { ActivatedRoute, NavigationEnd, Router } from '@angular/router';
// import { NotificationService } from './core/services/notification.service';
// import { filter } from 'rxjs/operators';
// import { BreadcrumbService } from './breadcrumb.service';
// import { Breadcrumb, BreadcrumbConfig } from './core/models/entity';
// import { GlobalService } from './shared/global.service';
// import { DynamicBreadcrumbService } from 'ng-dynamic-breadcrumb';
// import { TranslateService } from '@ngx-translate/core';

// @Component({
//   selector: 'app-root',
//   templateUrl: './app.component.html',
//   styleUrls: ['./app.component.scss', '../styles.scss']
// })
// export class AppComponent implements OnInit {
//   isAppInitialized = false;
//   username: string | null = null;
//   isHomePage: boolean = false;
//   breadcrumbs: Breadcrumb[] = [];
//   isEnglish: boolean =  false;
//   isEn: String =  "";

//   breadcrumbConfig: BreadcrumbConfig = {
//     bgColor: '',
//     fontSize: 18,
//     fontColor: '#0275d8',
//     lastLinkColor: '#000',
//     symbol: ' / ',
//     margin:'2%'
//   };
  
//   constructor(private authService: AuthService,
//     private route: ActivatedRoute,
//     private router: Router,
//     private notificationService: NotificationService,
//     private breadcrumbService: DynamicBreadcrumbService,
//     private globalService: GlobalService,
//     private translate: TranslateService,
//   ) { }

  

//   ngOnInit() {
//     // const breadcrumbs = document.getElementsByClassName('_ngcontent-ng-c216760715');

//     // // Loop through the HTMLCollection and apply styles
//     // for (let i = 0; i < breadcrumbs.length; i++) {
//     //   // Type assertion to HTMLElement
//     //   (breadcrumbs[i] as HTMLElement).style.marginLeft = '2%';
//     // }

//     // this.route.data.subscribe(data => {
//     //   const breadcrumbData = data['breadcrumb'] as Breadcrumb[];
//     //   if (breadcrumbData) {
//     //     this.breadcrumbService.getBreadcrumbs(breadcrumbData)
//     //       .then(breadcrumbs => {
//     //         this.breadcrumbs = breadcrumbs;
//     //         console.log('Breadcrumbs:', this.breadcrumbs); // Log to check contents
//     //       })
//     //       .catch(error => {
//     //         console.error('Error fetching breadcrumbs', error);
//     //       });
//     //   }
//     // });

//     // console.log("loading");
//     this.isEn = this.globalService.getGlobalValue();
//     if (this.isEn == 'EN'){
//       this.isEnglish = true;
//     }
//     else{
//       this.isEnglish = false;
//     }
//     this.authService.isInitialized.subscribe(initialized => {
//       // console.log("current staus:" , initialized);
//       this.isAppInitialized = initialized;
//     });

//     this.router.events.pipe(
//       filter(event => event instanceof NavigationEnd)
//     ).subscribe(() => {
//       // Check if the current route is the homepage
//       this.isHomePage = this.router.url === '/home';
//       this.setBreadcrumbs();
//     });


//     // first-time call API immediately, no delay
//     // this.notificationService.getMyTaskNotificationCount();
//     // this.notificationService.getCreatedTaskNotificationCount();

//     // this.notificationService.$notificationMyTask.subscribe(notificationMyTaskCount => {
//     //   console.log("Notification my task count updated:", notificationMyTaskCount);
//     // });

//     // this.notificationService.$notificationCreatedTask.subscribe(notificationCreatedTaskCount => {
//     //   console.log("Notification created task count updated:", notificationCreatedTaskCount);
//     // });
//   }


//   @HostListener('window:storage', ['$event'])
//   onStorageEvent(event: StorageEvent) {
//     if (event.key === 'loggedOut' && event.newValue === 'true') {
//       this.router.navigate(['/home'])
//         .then(() => {
//           window.location.reload();
//         });

//     }
//   }

//   private setBreadcrumbs() {
//     const breadcrumbs = this.route.root.snapshot.children[0].data['breadcrumb'];
//     if (breadcrumbs) {
//       this.translate.get(breadcrumbs).subscribe((translatedBreadcrumb: string) => {
//         this.breadcrumbService.updateBreadcrumbLabels({ [this.router.url]: translatedBreadcrumb });
//       });
//     }
//   }






// }
