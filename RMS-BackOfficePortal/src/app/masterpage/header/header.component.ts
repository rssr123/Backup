import { ChangeDetectorRef, Component, OnInit, ElementRef, HostListener } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { GlobalService } from '../../shared/global.service';
import { perm } from 'src/permissions/perm';
import { AuthService } from 'src/app/core/services/auth.service';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { environment } from 'src/environments/environment';
import { Observable, of, combineLatest, map } from 'rxjs';
import { NotificationService } from 'src/app/core/services/notification.service';
import { CounterCheckInStatus } from 'src/app/core/services/otc-counter-status.service';
import { Router, NavigationStart } from '@angular/router';
import { TriggerNotificationUpdateService } from 'src/app/core/services/TriggerNotificationUpdateService.service';

@Component({
  selector: 'app-header',
  templateUrl: './header_v2.component.html',
  styleUrls: ['./header_v2.component.scss'],
})
export class HeaderComponent implements OnInit {
  title = 'RMS';
  counterTitle: String = '';
  isNavVisible: boolean = false;
  totalNotificationCount$: Observable<number> = of(0);
  totalNotificationCountForAssignedTasks$: Observable<number> = of(0);
  totalNotificationCountForCreatedTasks$: Observable<number> = of(0);

  dynamicMessage: string = 'RMS Back Office';
  datetimeNow: Date = new Date();
  formattedDatetime: string = this.formatDate(this.datetimeNow);

  selectedLanguage: string;
  languages = ['en', 'bm'];

  notifications: any;
  maxTryCount: number = 30;
  tryCount: number = 0;
  stopFlag: boolean = false;

  permHeader = perm.Tax_Code_Maintenance_View_Page
    + "," + perm.Fee_Group_Maintenance_View_Page
    + "," + perm.Roles_and_Permissions_Configuration_View_Roles_and_Permissions_Configuration_Page
    + "," + perm.FMS_Ledger_Code_View_FMS_Code_Listing_Page
    + "," + perm.Master_Fee_Table_View_MFT
    + "," + perm.Master_Fee_Table_View_Master_Task_List
    + "," + perm.Master_Fee_Table_Add_MFT_Requester_Form_R
    + "," + perm.Master_Fee_Table_Edit_MFT_Requester_Form_R
    + "," + perm.Accrual_Listing_Deferred_Income_RI_Periodic_Lodgement_RI_Compound_View_Listing_Page
    + "," + perm.Accrual_Listing_Deferred_Income_RI_Periodic_Lodgement_RI_Compound_View_Details_Page
    // + "," + perm.Deferred_Income_Listing_Page + "," + perm.Deferred_Income_View_Details_Page
    // + "," + perm.RICP_Listing_Page + "," + perm.RICP_View_Details_Page
    // + "," + perm.RIPL_Listing_Page + "," + perm.RIPL_View_Details_Page;
    + "," + perm.Bank_and_Payment_Gateway_Files_View_PG_Settlement_Upload_Screen
    + "," + perm.Bank_and_Payment_Gateway_Files_View_Bank_Statement_Upload_Screen
    + "," + perm.MTT_Listing_View_Listing_Page
    + "," + perm.MTT_Listing_View_Details_Page
    + "," + perm.User_Role_View_Listing_Page
    + "," + perm.Reporting_and_Analysis_View_Payment_Collection_Report_Payment_Mode
    + "," + perm.Reporting_and_Analysis_View_Payment_Collection_Report_Source_System
    + "," + perm.Reporting_and_Analysis_View_Payment_Collection_Report_Fee_Detail_ID
    + "," + perm.Reporting_and_Analysis_Daily_Collection_Listing
    + "," + perm.Reporting_and_Analysis_Matched_Transaction_Listing
    + "," + perm.Reporting_and_Analysis_PG_Settlement_Disbursement_Listing
    + "," + perm.Reporting_and_Analysis_Deferred_Income_Aging
    + "," + perm.Reporting_and_Analysis_Unmatched_Transaction_Listing
    + "," + perm.Reporting_and_Analysis_RIPL_Aging
    + "," + perm.Reporting_and_Analysis_Unmatched_Aging
    + "," + perm.Reporting_and_Analysis_RICP_Aging
    + "," + perm.FMS_Account_Code_View_Page
    + "," + perm.View_Financial_Post_Accounting_Schedulers_Page
    + "," + perm.Reporting_and_Analysis_RICP_Aging
    + "," + perm.Assigned_Task_Listing
    + "," + perm.Created_Task_Listing
    + "," + perm.Branch_Code_Maintenance_View_Page
    + "," + perm.Billing_Class_Maintenance_View_Page
    + "," + perm.Billing_Type_Maintenance_View_Page
    + "," + perm.Reporting_and_Analysis_Refund_Status_Detailed
    + "," + perm.Reporting_and_Analysis_Refund_Summary_Status
    + "," + perm.Reporting_and_Analysis_Refund_Aging
    + "," + perm.Reporting_and_Analysis_Billing
    + "," + perm.Reporting_and_Analysis_Catalogue_Product_Service
    + "," + perm.Reporting_and_Analysis_Summary_Billing_by_Class_ID
    + "," + perm.Reporting_and_Analysis_Detailed_Billing_by_Class_ID
    + "," + perm.Reporting_and_Analysis_Detailed_Billing_by_Billing_Type
    + "," + perm.Assigned_Task_Listing
    + "," + perm.Created_Task_Listing
    + "," + perm.OTC_Collection_Receipting_View_Listing_Page
    + "," + perm.OTC_Returned_Cheque_View_Listing_Page
    + "," + perm.Assigned_Task_Listing
    + "," + perm.OTC_Check_In
    + "," + perm.OTC_Check_Out
    + "," + perm.Billing_Listing_Details
    + "," + perm.Billing_Registration
    + "," + perm.Billing_Cancellation_Listing
    + "," + perm.Billing_Adjustment_Listing
    + "," + perm.Refund_Account_Code_Maintenance_View_Page
    + "," + perm.OTC_Receipt_Cancellation_View_Listing_Page
    + "," + perm.Reporting_and_Analysis_Counter_Collection
    + "," + perm.Reporting_and_Analysis_Daily_Balancing
    + "," + perm.Reporting_and_Analysis_Master_Balancing
    + "," + perm.Reporting_and_Analysis_OTC_Collection
    + "," + perm.Reporting_and_Analysis_OTC_Collection_By_Fee_Detail_ID
    + "," + perm.Reporting_and_Analysis_OTC_Receipt_Cancellation
    + "," + perm.Reporting_and_Analysis_OTC_Returned_Cheque
    + "," + perm.Reporting_and_Analysis_Bank_In_Slip
    + "," + perm.OTC_EMV_Reconciliation_View_Listing_Page
    + "," + perm.OTC_Reprint_Receipt_View_Listing_Page
    + "," + perm.Service_Provider_View_Listing_Page
    + "," + perm.Service_Provider_Maintenance_View_Page
    + "," + perm.OTC_EMV_Reconciliation_View_Listing_Page
    + "," + perm.Assigned_Task_Listing
    + "," + perm.Created_Task_Listing
    + "," + perm.Finance_Admin_Task_Listing
    + "," + perm.BYM_Task_Listing
    + "," + perm.PG_Task_Listing
    + "," + perm.OTC_Staff_Task_Listing
    + "," + perm.OTC_Supervisor_Task_Listing
    + "," + perm.OTC_Branch_Manager_Task_Listing
    + "," + perm.SME_Task_Listing
    + "," + perm.Public_Task_Listing
    + "," + perm.LGL_Task_Listing
    + "," + perm.OTC_COUNTER_BALANCING_Detail_Page
    + "," + perm.OTC_DAILY_BALANCING_View_Listing
    + "," + perm.OTC_DAILY_BALANCING_View_Detail
    + "," + perm.OTC_MASTER_BALANCING_View_Listing
    + "," + perm.OTC_MASTER_BALANCING_View_Detail
    + "," + perm.OTC_MASTER_BALANCING_Download_BankInSlip
    + "," + perm.OTC_BALANCING_Update_PaymentMode
    + "," + perm.Refund_Paid_Transaction_View_Listing_Page
    + "," + perm.Non_RMS_Receipting_View_Listing_Page
    + "," + perm.Credit_Control_SME_Task_View_Listing
    + "," + perm.Court_Order_View_Listing_Page
    + "," + perm.Refund_Chargeback_View_Listing_Page
    + "," + perm.Refund_Request
    + "," + perm.Non_Billing_Listing_View
    + "," + perm.Refund_Listing;
  //Reporting_and_Analysis_RIPL_Aging

  permHeaderAllow = "";
  permTCDListAllow: number = 0;
  permFGListAllow: number = 0;
  permRPCListAllow: number = 0;
  permOTCAllow: number = 0;
  permALListAllow: number = 0;
  permADListAllow: number = 0;
  username$: Observable<string>;
  name$: Observable<string>;
  authenticated$: Observable<boolean>;
  permFMSListAllow: number = 0;
  permFMSAccountCodeListAllow: number = 0;
  permFPAListAllow: number = 0;

  permMFTListAllow: number = 0;
  permMTLListAllow: number = 0;
  permRequestAddMFT: number = 0;
  permRequestEditMFT: number = 0;

  permRICPListAllow: number = 0;
  permRIPLListAllow: number = 0;
  permDIListAllow: number = 0;
  permPaymentReconFilesListAllow: number = 0;
  permBankReconFilesListAllow: number = 0;
  permAssignedTaskListAllow: number = 0;
  permCreatedTaskListAllow: number = 0;

  permMTTListAllow: number = 0;
  permMTTDetailsAllow: number = 0;
  permURListAllow: number = 0;
  permRACListAllow: number = 0;
  permBCCListAllow: number = 0;

  permReportPMListAllow: number = 0;
  permReportSSListAllow: number = 0;
  permReportFDIDListAllow: number = 0;

  permReportDailyColListAllow: number = 0;
  permReportMatchTransListAllow: number = 0;
  permReportPGSetDisbListAllow: number = 0;
  permReportUnmatchTransListAllow: number = 0;
  permReportDIAgingAllow: number = 0;
  permReportRIPLAgingAllow: number = 0;
  permReportUmAgingAllow: number = 0;
  permReportRICPAgingAllow: number = 0;

  permReportRefundStatusDetailedAllow: number = 0;
  permReportRefundSummaryStatusAllow: number = 0;
  permReportRefundAgingAllow: number = 0;
  permReportBillingAllow: number = 0;
  permReportCatProdServAllow: number = 0;
  permReportSummaryBilAllow: number = 0;
  permReportDetBilClassIdAllow: number = 0;
  permReportDetBilBilTypeAllow: number = 0;

  //loginClicked = false;

  permBCDListAllow: number = 0;

  permBCListAllow: number = 0;

  permBTListAllow: number = 0;
  permOTCCollectionReceiptingAllow: number = 0;
  permOTCReturnedChequeAllow: number = 0;

  permOTCCheckInAllow: number = 0;
  permOTCCheckOutAllow: number = 0;

  permBillViewListingAndDetailsAllow: number = 0;
  permBillRegistrationAllow: number = 0;
  permBillCancellationListing: number = 0;
  permBillAdjustmentListing: number = 0;
  permNonBilListing: number = 0;
  permBillingMenu: number = 0;

  permOTCReceiptCancellationAllow: number = 0;

  permOTCReprintReceiptAllow: number = 0;
  //loginClicked = false;

  permReportCounterCollectionAllow: number = 0;
  permReportDailyBalancingAllow: number = 0;
  permReportMasterBalancingAllow: number = 0;
  permReportOTCCollectionAllow: number = 0;
  permReportOTCCollectionByFeeDetailIDAllow: number = 0;
  permReportOTCReceiptCancellationAllow: number = 0;
  permReportOTCReturnedChequeAllow: number = 0;
  permReportBankInSlipAllow: number = 0;

  permOTCEMVReconciliationAllow: number = 0;

  OTCCheckedIn: number = 0;

  permFATaskListAllow: number = 0;
  permBYMTaskListAllow: number = 0;
  permPGTaskListAllow: number = 0;
  permOTCStaffTaskListAllow: number = 0;
  permOTCSupervisorTaskListAllow: number = 0;
  permOTCBranchManagerTaskListAllow: number = 0;
  permSMETaskListAllow: number = 0;
  permPublicTaskListAllow: number = 0;
  permLGLTaskListAllow: number = 0;

  //OTC Balancing permissions
  permOTCCBalAllow: number = 0;
  permOTCDBalListingAllow: number = 0;
  permOTCDBalDetailAllow: number = 0;
  permOTCMBalListingAllow: number = 0;
  permOTCMBalDetailAllow: number = 0;
  permOTCMBalDownloadAllow: number = 0;
  permOTCBalChangePymtModeAllow: number = 0;
  permRefundPaidTransactionTableAllow: number = 0;
  permRefundAllow: number = 0;
  permRefundChargebackTableAllow: number = 0;
  permRefundListingAllow: number = 0;
  permSPListAllow: number = 0;
  permSPMListAllow: number = 0;

  permNonRmsListingAllow: number = 0;
  permCCListingAllow: number = 0;
  permCOListingAllow: number = 0;

  isMenuOpen: boolean = false;

  constructor(
    private http: HttpClient,
    private cdr: ChangeDetectorRef,
    private translate: TranslateService,
    private globalService: GlobalService,
    public authService: AuthService,
    public notificationService: NotificationService,
    public counterCheckInStatus: CounterCheckInStatus,
    private router: Router,
    private tnu: TriggerNotificationUpdateService,
    private eRef: ElementRef
  ) {
    this.selectedLanguage = this.globalService.getGlobalValue();
    this.translate.setDefaultLang(this.globalService.getGlobalValue());
    this.translate.use(this.globalService.getGlobalValue());
    this.username$ = this.authService.getUsername();
    this.name$ = this.authService.getName();
    this.authenticated$ = of(this.authService.getAuthenticationstatus());

    this.tnu.messageReceived$.subscribe(data => {
      //this.reloadNotification();
      //this.loadNotification();
      this.stopFlag = true;
      clearInterval(this.notifications);
    });
    this.router.events.subscribe(event => {
      if (event instanceof NavigationStart) {
        this.isMenuOpen = false;
      }
    });

    if(this.notifications == null)
      this.notifications =  setInterval(() => {
        this.reloadNotification();
        this.loadNotification();
      }, 10000);
  }

// Add these methods to your HeaderComponent class:

// ✅ Method to check if user has any operational area permissions
hasOperationalAreaPermissions(): boolean {
  return (
    this.permOTCCheckInAllow === 1 || 
    this.permOTCCheckOutAllow === 1 || 
    this.permOTCCollectionReceiptingAllow === 1 || 
    this.permOTCReturnedChequeAllow === 1 || 
    this.permOTCReprintReceiptAllow === 1 || 
    this.permOTCReceiptCancellationAllow === 1 || 
    this.permOTCDBalListingAllow === 1 || 
    this.permOTCMBalListingAllow === 1 ||  
    this.permALListAllow === 1 || 
    this.permCCListingAllow === 1 || 
    this.permCOListingAllow === 1 || 
    this.permBillingMenu === 1 || 
    this.permBankReconFilesListAllow === 1 || 
    this.permPaymentReconFilesListAllow === 1 || 
    this.permReportPMListAllow === 1 || 
    this.permReportSSListAllow === 1 || 
    this.permReportFDIDListAllow === 1 ||
    this.permReportDailyColListAllow === 1 || 
    this.permReportMatchTransListAllow === 1 || 
    this.permReportPGSetDisbListAllow === 1 || 
    this.permReportUnmatchTransListAllow === 1 || 
    this.permReportDIAgingAllow === 1 || 
    this.permReportRIPLAgingAllow === 1 || 
    this.permReportUmAgingAllow === 1 || 
    this.permReportRICPAgingAllow === 1 ||
    this.permReportCounterCollectionAllow === 1 || 
    this.permReportDailyBalancingAllow === 1 || 
    this.permReportMasterBalancingAllow === 1 || 
    this.permReportOTCCollectionAllow === 1 || 
    this.permReportOTCCollectionByFeeDetailIDAllow === 1 || 
    this.permReportOTCReceiptCancellationAllow === 1 || 
    this.permReportOTCReturnedChequeAllow === 1 || 
    this.permReportBankInSlipAllow === 1 || 
    this.permSPListAllow === 1 || 
    this.permSPMListAllow === 1 ||
    this.permRefundListingAllow === 1 ||
    this.permRefundChargebackTableAllow === 1 ||      
    this.permRefundPaidTransactionTableAllow === 1
  );
}

// ✅ Method to toggle mega menu
toggleMegaMenu(event: Event): void {
  event.preventDefault();
  event.stopPropagation();
  
  // Close all Bootstrap dropdowns before toggling mega menu
  this.closeAllBootstrapDropdowns();
  
  this.isMenuOpen = !this.isMenuOpen;
  
  // Add/remove body class to prevent scrolling when menu is open
  if (this.isMenuOpen) {
    document.body.classList.add('mega-menu-open');
    // Calculate and set the exact position based on actual header heights
    this.setMegaMenuPosition();
  } else {
    document.body.classList.remove('mega-menu-open');
  }
}

// ✅ Method to close all Bootstrap dropdowns
private closeAllBootstrapDropdowns(): void {
  // Close all open Bootstrap dropdowns
  const openDropdowns = document.querySelectorAll('.dropdown-menu.show');
  openDropdowns.forEach(dropdown => {
    dropdown.classList.remove('show');
    const toggle = dropdown.previousElementSibling as HTMLElement;
    if (toggle) {
      toggle.setAttribute('aria-expanded', 'false');
    }
  });
  
  // Remove show class from dropdown parents
  const openDropdownParents = document.querySelectorAll('.dropdown.show');
  openDropdownParents.forEach(parent => {
    parent.classList.remove('show');
  });
}

// ✅ Method to set mega menu position - REMOVED to prevent blinking
private setMegaMenuPosition(): void {
  // Commenting out dynamic positioning to prevent blinking
  // CSS will handle the positioning instead
  
  /*
  setTimeout(() => {
    const header1 = document.querySelector('.header-container');
    const header2 = document.querySelector('#header2');
    const megaMenu = document.querySelector('.mega-menu-panel') as HTMLElement;
    
    if (header1 && header2 && megaMenu) {
      const header1Height = header1.getBoundingClientRect().height;
      const header2Height = header2.getBoundingClientRect().height;
      const totalHeaderHeight = header1Height + header2Height;
      
      // Check if mobile view
      const isMobile = window.innerWidth < 768;
      
      // Position below header2 for both desktop and mobile
      megaMenu.style.top = `${totalHeaderHeight}px`;
      megaMenu.style.marginTop = '0';
      
      if (isMobile) {
        // Mobile: Zero gap styling
        megaMenu.style.borderTop = 'none';
        megaMenu.style.paddingTop = '0';
      } else {
        // Desktop: Keep original styling (let CSS handle padding and borders)
        megaMenu.style.borderTop = '';
        megaMenu.style.paddingTop = '';
      }
    }
  }, 0);
  */
}

// ✅ Method to close mega menu
closeMegaMenu(): void {
  this.isMenuOpen = false;
  document.body.classList.remove('mega-menu-open');
  
  // Also collapse mobile menu if it's open
  this.collapseMobileMenu();
}

// ✅ Update the existing handleOutsideClick method
@HostListener('document:click', ['$event'])
handleOutsideClick(event: MouseEvent): void {
  const clickedElement = event.target as HTMLElement;
  
  // Check if it's the mega menu toggle or part of the mega menu
  const isToggle = clickedElement.closest('.mega-menu-toggle');
  const isMegaMenu = clickedElement.closest('.mega-menu-panel');
  
  if (!isToggle && !isMegaMenu && this.isMenuOpen) {
    this.closeMegaMenu();
  }
}

// ✅ Listen for window resize to adjust mega menu position
@HostListener('window:resize', ['$event'])
onWindowResize(event: any): void {
  if (this.isMenuOpen) {
    this.setMegaMenuPosition();
  }
}

// ✅ Update ngOnDestroy to cleanup
ngOnDestroy(): void {
  // Clean up body class when component is destroyed
  document.body.classList.remove('mega-menu-open');
  if(this.notifications) clearInterval(this.notifications);
}

  Login() {
    //this.loginClicked = true;
    this.authService.login();
  }

  // ✅ Method to collapse mobile navbar when menu item is clicked
  collapseMobileMenu() {
    const navbarCollapse = document.getElementById('navMenu');
    if (navbarCollapse && navbarCollapse.classList.contains('show')) {
      // Remove the 'show' class to collapse the menu
      navbarCollapse.classList.remove('show');
      
      // Also ensure the burger button is not in 'collapsed' state
      const navbarToggler = document.querySelector('.navbar-toggler');
      if (navbarToggler) {
        navbarToggler.classList.add('collapsed');
        navbarToggler.setAttribute('aria-expanded', 'false');
      }
    }
  }

  ngOnInit() {
    // Update the datetime every second
    setInterval(() => {
      this.datetimeNow = new Date();
      this.formattedDatetime = this.formatDate(this.datetimeNow);
      // Update the template
      this.cdr.detectChanges();
    }, 1000);
    
    if(this.notifications == null)
      this.notifications =  setInterval(() => {
        this.reloadNotification();
        this.loadNotification();
      }, 10000);

    this.loadData();
    this.router.events.subscribe(event => {
      if (event instanceof NavigationStart) {
        this.isMenuOpen = false;
      }
    });

    // Add event listeners to close mega menu when Bootstrap dropdowns are opened
    this.setupBootstrapDropdownListeners();
  }

  // ✅ Method to setup Bootstrap dropdown event listeners
  private setupBootstrapDropdownListeners(): void {
    // Listen for Bootstrap dropdown events
    document.addEventListener('show.bs.dropdown', (event) => {
      // Close mega menu when any Bootstrap dropdown is opened
      if (this.isMenuOpen) {
        this.closeMegaMenu();
      }
    });
  }

  menuGroups = [
    // otc
    {
      show: () => this.permOTCCheckInAllow === 1 || this.permOTCCheckOutAllow === 1 || this.permOTCCollectionReceiptingAllow === 1 || this.permOTCReturnedChequeAllow === 1 || this.permOTCReprintReceiptAllow === 1 || this.permOTCReceiptCancellationAllow === 1 || this.permOTCDBalListingAllow === 1 || this.permOTCMBalListingAllow === 1,
      title: 'menu.otc',
      items: [
        { show: () => this.permOTCCheckInAllow === 1 && this.OTCCheckedIn === 0, label: 'menu.otccheckin', link: '/otc-checkin' },
        { show: () => this.permOTCCheckOutAllow === 1 && this.OTCCheckedIn === 1, label: 'menu.otccheckout', link: '/otc-checkout' },
        { show: () => this.permOTCCollectionReceiptingAllow === 1 && this.OTCCheckedIn === 1, label: 'labels.otccollectionandreceipting', link: '/otc-collection-receipting' },
        { show: () => this.permOTCReturnedChequeAllow === 1 , label: 'labels.otcreturncheque', link: '/otc-returned-cheque' },
        { show: () => this.permOTCReprintReceiptAllow === 1, label: 'labels.reprintreceipt', link: '/reprintreceipt' },
        { show: () => this.permOTCReceiptCancellationAllow === 1 && this.OTCCheckedIn === 1, label: 'labels.otcreceiptcancellation', link: '/rcpt-no-vld' },
        { show: () => this.permOTCDBalListingAllow === 1, label: 'labels.dailybalancing', link: '/daily-balancing-listing' },
        { show: () => this.permOTCMBalListingAllow === 1, label: 'labels.masterbalancing', link: '/master-balancing-listing' },
      ]
    },
    // refund
    {
      show: () => this.permRefundPaidTransactionTableAllow === 1  || this.permRefundChargebackTableAllow === 1 || this.permRefundListingAllow === 1,
      title: 'menu.refund',
      items: [
        { show: () => this.permRefundPaidTransactionTableAllow === 1, label: 'menu.refundrequestbyfinance', link: '/paid-transaction-table' },
        { show: () => this.permRefundChargebackTableAllow === 1, label: 'menu.paymentgatewaychargeback', link: '/refund-chargeback' },
        { show: () => this.permRefundListingAllow === 1, label: 'menu.refundtransactiontable', link: '/refund-listing' },
      ]
    },
    // accrual
    {
      show: () => this.permALListAllow === 1,
      title: 'menu.accrual',
      items: [
        { show: () => true, label: 'menu.deferredincomelisting', link: '/deferred-income-listing' },
        { show: () => true, label: 'menu.receivableincomeperiodiclodgement', link: '/ri-pl-listing' },
        { show: () => true, label: 'menu.receivableincomecompound', link: '/ricp-listing' },
        { show: () => true, label: 'menu.receivableincomelitigation', link: '/rilt-listing' },
      ]
    },
    // credit control
    {
      show: () => this.permCCListingAllow === 1 || this.permCOListingAllow === 1,
      title: 'menu.creditcontrol',
      items: [
        { show: () => this.permCCListingAllow === 1, label: 'labels.mastercreditcontrollisting', link: '/credit-control-sme-task-list' },
        { show: () => this.permCOListingAllow === 1, label: 'menu.courtorderlisting', link: '/court-order-listing' },
      ]
    },
    //billing
    {
      show: () => this.permBillingMenu === 1,
      title: 'menu.billing',
      items: [
        { show: () => this.permBillViewListingAndDetailsAllow === 1, label: 'menu.billist', link: '/billing-listing' },
        { show: () => this.permBillRegistrationAllow === 1, label: 'menu.bilreg', link: '/billing-registration' },
        { show: () => this.permBillCancellationListing === 1, label: 'menu.billingcancellation', link: '/billing-cancellation-search' },
        { show: () => this.permBillCancellationListing === 1, label: 'menu.billingcancellationlisting', link: '/billing-cancellation-listing' },
        { show: () => this.permBillAdjustmentListing === 1, label: 'menu.billingadjustmentsearch', link: '/billing-adjustment-search' },
        { show: () => this.permNonBilListing === 1, label: 'menu.nonbillinglisting', link: '/non-billing-listing' },
      ]
    },
    //reconciliation
    {
      show: () => this.permBankReconFilesListAllow === 1 || this.permPaymentReconFilesListAllow === 1,
      title: 'menu.reconciliation',
      items: [
        { show: () => this.permPaymentReconFilesListAllow === 1, label: 'menu.paymentgatewayreconciliation', link: '/pgrecon-listing' },
        { show: () => this.permBankReconFilesListAllow === 1, label: 'menu.bankreconciliation', link: '/bank-recon-listing' },
        { show: () => this.permNonRmsListingAllow === 1, label: 'menu.nonrmsreconciliation', link: '/non-rms-receipting-listing' },
        { show: () => this.permOTCEMVReconciliationAllow === 1, label: 'labels.otcemvreconciliation', link: '/otc-emv-reconciliation' },
      ]
    },
    //reports
    {
      show: () => this.permReportPMListAllow === 1 || this.permReportSSListAllow === 1 || this.permReportFDIDListAllow === 1
                || this.permReportDailyColListAllow === 1 || this.permReportMatchTransListAllow ===1 || this.permReportPGSetDisbListAllow === 1 || this.permReportUnmatchTransListAllow === 1 || this.permReportDIAgingAllow === 1 || this.permReportRIPLAgingAllow === 1 || this.permReportUmAgingAllow ===1 || this.permReportRICPAgingAllow === 1
                || this.permReportCounterCollectionAllow === 1 || this.permReportDailyBalancingAllow === 1 || this.permReportMasterBalancingAllow === 1 || this.permReportOTCCollectionAllow === 1 || this.permReportOTCCollectionByFeeDetailIDAllow === 1 || this.permReportOTCReceiptCancellationAllow === 1 || this.permReportOTCReturnedChequeAllow === 1 || this.permReportBankInSlipAllow === 1,
      title: 'menu.report',
      items: [
        { show: () => this.permReportDailyColListAllow === 1 || this.permReportFDIDListAllow === 1 || this.permReportSSListAllow === 1 || this.permReportPMListAllow === 1 , label: 'menu.collectionreport', link: '/collection-reports-listing' },
        { show: () => this.permReportMatchTransListAllow === 1 || this.permReportPGSetDisbListAllow === 1 ||  this.permReportUmAgingAllow === 1 || this.permReportUnmatchTransListAllow === 1, label: 'menu.reconciliationreport', link: '/reconciliation-reports-listing' },
        { show: () => this.permReportDIAgingAllow === 1 || this.permReportRICPAgingAllow === 1 || this.permReportRIPLAgingAllow === 1, label: 'menu.accrualreport', link: '/accrual-reports-listing' },
        { show: () => this.permReportBankInSlipAllow === 1 || this.permReportOTCCollectionByFeeDetailIDAllow === 1 || this.permReportDailyBalancingAllow === 1 || this.permReportMasterBalancingAllow === 1 || this.permReportOTCCollectionAllow === 1 || this.permReportOTCCollectionByFeeDetailIDAllow === 1 || this.permReportOTCReceiptCancellationAllow === 1 || this.permReportOTCReturnedChequeAllow === 1, label: 'labels.overthecounter', link: '/otc-reports-listing' },
        { show: () => this.permReportRefundAgingAllow === 1 || this.permReportRefundSummaryStatusAllow === 1 || this.permReportRefundStatusDetailedAllow === 1, label: 'menu.refundreport', link: '/refund-reports-listing' },
        { show: () => this.permReportCatProdServAllow === 1 || this.permReportDetBilBilTypeAllow === 1 || this.permReportDetBilClassIdAllow === 1 || this.permReportSummaryBilAllow === 1, label: 'menu.cataloguereport', link: '/catalogue-reports-listing' },
      ]
    },
    //sp
    {
      show: () => this.permSPListAllow === 1 || this.permSPMListAllow === 1,
      title: 'menu.sp',
      items: [
        { show: () => this.permSPListAllow === 1, label: 'menu.sppaymentlisting', link: '/service-provider-payment-listing' },
        { show: () => this.permSPMListAllow === 1, label: 'menu.spprofile', link: '/service-provider-maintenance-listing' },
      ]
    },

  ];

  get visibleMenuGroups() {
    return this.menuGroups.filter(group => group.show());
  }

  get menuRows() {
    const cols = 4;
    const groups = this.visibleMenuGroups;
    const rows = [];
    for (let i = 0; i < groups.length; i += cols) {
      rows.push(groups.slice(i, i + cols));
    }
    return rows;
  }

  loadNotification() {
    //for notification
    this.totalNotificationCount$ = combineLatest([
      this.notificationService.$notificationMyTask,
      this.notificationService.$notificationCreatedTask,
      this.notificationService.$notificationOctRCAssignedTasks,
      this.notificationService.$notificationOctRCCreatedTasks,
      this.notificationService.$notificationRefundAssignedTasks,
      this.notificationService.$notificationRefundCreatedTasks,
      this.notificationService.$notificationBillingAssignedTasks,
      this.notificationService.$notificationBillingCreatedTasks,
      this.notificationService.$notificationCCCAssignedTasks,
      this.notificationService.$notificationCCCCreatedTasks
    ]).pipe(
      map(([myTaskCount, createdTaskCount, octRCAssignedTaskCount, octRCCreatedTaskCount, refundAssignedTaskCount, refundCreatedTaskCount, billingAssignedTaskCount, billingCreatedTaskCount, cccAssignedTaskCount, cccCreatedTaskCount]) => myTaskCount + createdTaskCount + octRCAssignedTaskCount + octRCCreatedTaskCount + refundAssignedTaskCount + refundCreatedTaskCount + billingAssignedTaskCount + billingCreatedTaskCount + cccAssignedTaskCount + cccCreatedTaskCount)
    );

    // For assigned tasks's total count
    this.totalNotificationCountForAssignedTasks$ = combineLatest([
      this.notificationService.$notificationMyTask,
      this.notificationService.$notificationOctRCAssignedTasks,
      this.notificationService.$notificationRefundAssignedTasks,
      this.notificationService.$notificationBillingAssignedTasks,
      this.notificationService.$notificationCCCAssignedTasks,
    ]).pipe(
      map(([myTaskCount, octRCAssignedTaskCount, refundAssignedTaskCount, billingAssignedTaskCount, cccAssignedTaskCount]) => myTaskCount + octRCAssignedTaskCount + refundAssignedTaskCount + billingAssignedTaskCount + cccAssignedTaskCount)
    );

    // For created tasks's total count
    this.totalNotificationCountForCreatedTasks$ = combineLatest([
      this.notificationService.$notificationCreatedTask,
      this.notificationService.$notificationOctRCCreatedTasks,
      this.notificationService.$notificationRefundCreatedTasks,
      this.notificationService.$notificationBillingCreatedTasks,
      this.notificationService.$notificationCCCCreatedTasks
    ]).pipe(
      map(([createdTaskCount, octRCCreatedTaskCount, refundCreatedTaskCount, billingCreatedTaskCount, cccCreatedTaskCount]) => createdTaskCount + octRCCreatedTaskCount + refundCreatedTaskCount + billingCreatedTaskCount + cccCreatedTaskCount)
    );
  }

  reloadNotification() {
    if(this.stopFlag){
      clearInterval(this.notifications);
      this.notifications = null;
      return;
    }
    console.log('🔄 Refreshing notifications...');
    if(this.authService.username == null || this.authService.username == '' || 
        this.authService.username.toLowerCase() == 'anonymoususer' || 
        this.authService.username.toLowerCase() == 'anonymous'){
      console.log('❌ Detected no valid username! Stop notification refresh request.')
      this.setDefaultPermissions();
      if(this.tryCount > this.maxTryCount){
        clearInterval(this.notifications);
        this.notifications = null;
      }
      this.tryCount = this.tryCount + 1;
      return;
    }
    this.tryCount = 0;

    this.notificationService.getTaskNotificationCounts(this.authService.username);
    //this.notificationService.getMyTaskNotificationCount();
    //this.notificationService.getOtcRCAssignedTaskNotificationCount();
    //this.notificationService.getRefundAssignedTaskNotificationCount();
    //this.notificationService.getBillingAssignedTaskNotificationCount();
    //this.notificationService.getCCCAssignedTaskNotificationCount();
  }

  loadData() {
    console.log('🔄 Loading user permissions...');
    const permUrl = environment.apiUrl + '/api/RPC/v1/checkuserrole';
    if(this.authService.username == null || this.authService.username == '' || this.authService.username.toLowerCase() == 'anonymoususer' || this.authService.username.toLowerCase() == 'anonymous'){
      setTimeout(() => {this.loadData();}, 1000);
      return;
    }
    this.authService.checkUserRole(this.authService.username, this.permHeader)
      .subscribe({
        next: (response: any) => {
          console.log('✅ User permissions loaded successfully');
          //console.log(response.data);
          this.permHeaderAllow = response.data;
          this.permTCDListAllow = this.permHeaderAllow.includes(perm.Tax_Code_Maintenance_View_Page) ? 1 : 0;
          this.permFGListAllow = this.permHeaderAllow.includes(perm.Fee_Group_Maintenance_View_Page) ? 1 : 0;
          this.permRPCListAllow = this.permHeaderAllow.includes(perm.Roles_and_Permissions_Configuration_View_Roles_and_Permissions_Configuration_Page) ? 1 : 0;
          this.permALListAllow = this.permHeaderAllow.includes(perm.Accrual_Listing_Deferred_Income_RI_Periodic_Lodgement_RI_Compound_View_Listing_Page) ? 1 : 0;
          this.permADListAllow = this.permHeaderAllow.includes(perm.Accrual_Listing_Deferred_Income_RI_Periodic_Lodgement_RI_Compound_View_Details_Page) ? 1 : 0;
          this.permFMSListAllow = this.permHeaderAllow.includes(perm.FMS_Ledger_Code_View_FMS_Code_Listing_Page) ? 1 : 0;

          this.permMFTListAllow = this.permHeaderAllow.includes(perm.Master_Fee_Table_View_MFT) ? 1 : 0;
          this.permMTLListAllow = this.permHeaderAllow.includes(perm.Master_Fee_Table_View_Master_Task_List) ? 1 : 0;
          this.permRequestAddMFT = this.permHeaderAllow.includes(perm.Master_Fee_Table_Add_MFT_Requester_Form_R) ? 1 : 0;
          this.permRequestEditMFT = this.permHeaderAllow.includes(perm.Master_Fee_Table_Edit_MFT_Requester_Form_R) ? 1 : 0;
          this.permMTTListAllow = this.permHeaderAllow.includes(perm.MTT_Listing_View_Listing_Page) ? 1 : 0;
          this.permMTTDetailsAllow = this.permHeaderAllow.includes(perm.MTT_Listing_View_Details_Page) ? 1 : 0;
          this.permURListAllow = this.permHeaderAllow.includes(perm.User_Role_View_Listing_Page) ? 1 : 0;
          this.permRACListAllow = this.permHeaderAllow.includes(perm.Refund_Account_Code_Maintenance_View_Page) ? 1 : 0;
          this.permBCCListAllow = this.permHeaderAllow.includes(perm.Branch_Code_Maintenance_View_Page) ? 1 : 0;

          // this.permRICPListAllow = this.permHeaderAllow.includes(perm.RICP_Listing_Page) ? 1 : 0;
          // this.permRIPLListAllow = this.permHeaderAllow.includes(perm.RIPL_Listing_Page) ? 1 : 0;
          // this.permDIListAllow = this.permHeaderAllow.includes(perm.Deferred_Income_Listing_Page) ? 1 : 0;

          //console.log(this.permTCDListAllow, this.permFGListAllow, this.permRPCListAllow, this.permALListAllow);

          this.permBCDListAllow = this.permHeaderAllow.includes(perm.Branch_Code_Maintenance_View_Page) ? 1 : 0;

          this.permBCListAllow = this.permHeaderAllow.includes(perm.Billing_Class_Maintenance_View_Page) ? 1 : 0;

          this.permBTListAllow = this.permHeaderAllow.includes(perm.Billing_Type_Maintenance_View_Page) ? 1 : 0;


          //console.log(this.permTCDListAllow, this.permFGListAllow, this.permRPCListAllow, this.permALListAllow);

          this.permPaymentReconFilesListAllow = this.permHeaderAllow.includes(perm.Bank_and_Payment_Gateway_Files_View_PG_Settlement_Upload_Screen) ? 1 : 0;
          this.permBankReconFilesListAllow = this.permHeaderAllow.includes(perm.Bank_and_Payment_Gateway_Files_View_Bank_Statement_Upload_Screen) ? 1 : 0;
          this.permAssignedTaskListAllow = this.permHeaderAllow.includes(perm.Assigned_Task_Listing) ? 1 : 0;
          this.permCreatedTaskListAllow = this.permHeaderAllow.includes(perm.Created_Task_Listing) ? 1 : 0;

          this.permFATaskListAllow = this.permHeaderAllow.includes(perm.Finance_Admin_Task_Listing) ? 1 : 0;
          this.permBYMTaskListAllow = this.permHeaderAllow.includes(perm.BYM_Task_Listing) ? 1 : 0;
          this.permPGTaskListAllow = this.permHeaderAllow.includes(perm.PG_Task_Listing) ? 1 : 0;
          this.permOTCStaffTaskListAllow = this.permHeaderAllow.includes(perm.OTC_Staff_Task_Listing) ? 1 : 0;
          this.permOTCSupervisorTaskListAllow = this.permHeaderAllow.includes(perm.OTC_Supervisor_Task_Listing) ? 1 : 0;
          this.permOTCBranchManagerTaskListAllow = this.permHeaderAllow.includes(perm.OTC_Branch_Manager_Task_Listing) ? 1 : 0;
          this.permSMETaskListAllow = this.permHeaderAllow.includes(perm.SME_Task_Listing) ? 1 : 0;
          this.permPublicTaskListAllow = this.permHeaderAllow.includes(perm.Public_Task_Listing) ? 1 : 0;
          this.permLGLTaskListAllow = this.permHeaderAllow.includes(perm.LGL_Task_Listing) ? 1 : 0;

          this.permReportPMListAllow = this.permHeaderAllow.includes(perm.Reporting_and_Analysis_View_Payment_Collection_Report_Payment_Mode) ? 1 : 0;
          this.permReportSSListAllow = this.permHeaderAllow.includes(perm.Reporting_and_Analysis_View_Payment_Collection_Report_Source_System) ? 1 : 0;
          this.permReportFDIDListAllow = this.permHeaderAllow.includes(perm.Reporting_and_Analysis_View_Payment_Collection_Report_Fee_Detail_ID) ? 1 : 0;
          //console.log(this.permTCDListAllow, this.permFGListAllow, this.permRPCListAllow);

          this.permReportDailyColListAllow = this.permHeaderAllow.includes(perm.Reporting_and_Analysis_Daily_Collection_Listing) ? 1 : 0;
          this.permReportMatchTransListAllow = this.permHeaderAllow.includes(perm.Reporting_and_Analysis_Matched_Transaction_Listing) ? 1 : 0;
          this.permReportPGSetDisbListAllow = this.permHeaderAllow.includes(perm.Reporting_and_Analysis_PG_Settlement_Disbursement_Listing) ? 1 : 0;
          this.permReportUnmatchTransListAllow = this.permHeaderAllow.includes(perm.Reporting_and_Analysis_Unmatched_Transaction_Listing) ? 1 : 0;
          this.permReportDIAgingAllow = this.permHeaderAllow.includes(perm.Reporting_and_Analysis_Deferred_Income_Aging) ? 1 : 0;
          this.permReportRIPLAgingAllow = this.permHeaderAllow.includes(perm.Reporting_and_Analysis_RIPL_Aging) ? 1 : 0;
          this.permReportUmAgingAllow = this.permHeaderAllow.includes(perm.
            Reporting_and_Analysis_Unmatched_Aging) ? 1 : 0;
          this.permReportRICPAgingAllow = this.permHeaderAllow.includes(perm.
            Reporting_and_Analysis_RICP_Aging) ? 1 : 0;
          this.permFMSAccountCodeListAllow = this.permHeaderAllow.includes(perm.FMS_Account_Code_View_Page) ? 1 : 0;
          this.permFPAListAllow = this.permHeaderAllow.includes(perm.View_Financial_Post_Accounting_Schedulers_Page) ? 1 : 0;
          this.permOTCCollectionReceiptingAllow = this.permHeaderAllow.includes(perm.OTC_Collection_Receipting_View_Listing_Page) ? 1 : 0;
          this.permOTCReturnedChequeAllow = this.permHeaderAllow.includes(perm.OTC_Returned_Cheque_View_Listing_Page) ? 1 : 0;
          this.permOTCReceiptCancellationAllow = this.permHeaderAllow.includes(perm.OTC_Receipt_Cancellation_View_Listing_Page) ? 1 : 0;
          // console.log('Daily col : ' + this.permReportDailyColListAllow, 'match : ' + this.permReportMatchTransListAllow, 'PG : ' + this.permReportPGSetDisbListAllow
          //   , 'DI : ' + this.permReportDIAgingAllow);

          this.permOTCCheckInAllow = this.permHeaderAllow.includes(perm.OTC_Check_In) ? 1 : 0;
          this.permOTCCheckOutAllow = this.permHeaderAllow.includes(perm.OTC_Check_Out) ? 1 : 0;

          this.permBillViewListingAndDetailsAllow = this.permHeaderAllow.includes(perm.Billing_Listing_Details) ? 1 : 0;
          this.permBillRegistrationAllow = this.permHeaderAllow.includes(perm.Billing_Registration) ? 1 : 0;
          this.permBillCancellationListing = this.permHeaderAllow.includes(perm.Billing_Cancellation_Listing) ? 1 : 0;
          this.permBillAdjustmentListing = this.permHeaderAllow.includes(perm.Billing_Adjustment_Listing) ? 1 : 0;
          this.permNonBilListing = this.permHeaderAllow.includes(perm.Non_Billing_Listing_View) ? 1 : 0;

          if (this.permBillViewListingAndDetailsAllow == 1 || this.permBillRegistrationAllow == 1 || this.permBillCancellationListing == 1 || this.permBillAdjustmentListing == 1 || this.permNonBilListing == 1)
            this.permBillingMenu = 1;

          this.permReportRefundStatusDetailedAllow = this.permHeaderAllow.includes(perm.Reporting_and_Analysis_Refund_Status_Detailed) ? 1 : 0;
          this.permReportRefundSummaryStatusAllow = this.permHeaderAllow.includes(perm.Reporting_and_Analysis_Refund_Summary_Status) ? 1 : 0;
          this.permReportRefundAgingAllow = this.permHeaderAllow.includes(perm.Reporting_and_Analysis_Refund_Aging) ? 1 : 0;
          this.permReportBillingAllow = this.permHeaderAllow.includes(perm.Reporting_and_Analysis_Billing) ? 1 : 0;
          this.permReportCatProdServAllow = this.permHeaderAllow.includes(perm.Reporting_and_Analysis_Catalogue_Product_Service) ? 1 : 0;
          this.permReportSummaryBilAllow = this.permHeaderAllow.includes(perm.Reporting_and_Analysis_Summary_Billing_by_Class_ID) ? 1 : 0;
          this.permReportDetBilClassIdAllow = this.permHeaderAllow.includes(perm.Reporting_and_Analysis_Detailed_Billing_by_Class_ID) ? 1 : 0;
          this.permReportDetBilBilTypeAllow = this.permHeaderAllow.includes(perm.Reporting_and_Analysis_Detailed_Billing_by_Billing_Type) ? 1 : 0;
          //console.log("Cancel is " + this.permOTCReceiptCancellationAllow);

          this.permOTCReprintReceiptAllow = this.permHeaderAllow.includes(perm.OTC_Reprint_Receipt_View_Listing_Page) ? 1 : 0;
          // console.log('Daily col : ' + this.permReportDailyColListAllow, 'match : ' + this.permReportMatchTransListAllow, 'PG : ' + this.permReportPGSetDisbListAllow
          //   , 'DI : ' + this.permReportDIAgingAllow);
          this.permReportCounterCollectionAllow = this.permHeaderAllow.includes(perm.Reporting_and_Analysis_Counter_Collection) ? 1 : 0;
          this.permReportDailyBalancingAllow = this.permHeaderAllow.includes(perm.Reporting_and_Analysis_Daily_Balancing) ? 1 : 0;
          this.permReportMasterBalancingAllow = this.permHeaderAllow.includes(perm.Reporting_and_Analysis_Master_Balancing) ? 1 : 0;
          this.permReportOTCCollectionAllow = this.permHeaderAllow.includes(perm.Reporting_and_Analysis_OTC_Collection) ? 1 : 0;
          this.permReportOTCCollectionByFeeDetailIDAllow = this.permHeaderAllow.includes(perm.Reporting_and_Analysis_OTC_Collection_By_Fee_Detail_ID) ? 1 : 0;
          this.permReportOTCReceiptCancellationAllow = this.permHeaderAllow.includes(perm.Reporting_and_Analysis_OTC_Receipt_Cancellation) ? 1 : 0;
          this.permReportOTCReturnedChequeAllow = this.permHeaderAllow.includes(perm.Reporting_and_Analysis_OTC_Returned_Cheque) ? 1 : 0;
          this.permReportBankInSlipAllow = this.permHeaderAllow.includes(perm.Reporting_and_Analysis_Bank_In_Slip) ? 1 : 0;
          this.permOTCEMVReconciliationAllow = this.permHeaderAllow.includes(perm.OTC_EMV_Reconciliation_View_Listing_Page) ? 1 : 0;
          this.permSPMListAllow = this.permHeaderAllow.includes(perm.Service_Provider_Maintenance_View_Page) ? 1 : 0;
          this.permSPListAllow = this.permHeaderAllow.includes(perm.Service_Provider_View_Listing_Page) ? 1 : 0;
          //OTC Balancing
          this.permOTCCBalAllow = this.permHeaderAllow.includes(perm.OTC_COUNTER_BALANCING_Detail_Page) ? 1 : 0;
          this.permOTCDBalListingAllow = this.permHeaderAllow.includes(perm.OTC_DAILY_BALANCING_View_Listing) ? 1 : 0;
          this.permOTCDBalDetailAllow = this.permHeaderAllow.includes(perm.OTC_DAILY_BALANCING_View_Detail) ? 1 : 0;
          this.permOTCMBalListingAllow = this.permHeaderAllow.includes(perm.OTC_MASTER_BALANCING_View_Listing) ? 1 : 0;
          this.permOTCMBalDetailAllow = this.permHeaderAllow.includes(perm.OTC_MASTER_BALANCING_View_Detail) ? 1 : 0;
          this.permOTCMBalDownloadAllow = this.permHeaderAllow.includes(perm.OTC_MASTER_BALANCING_Download_BankInSlip) ? 1 : 0;
          this.permOTCBalChangePymtModeAllow = this.permHeaderAllow.includes(perm.OTC_BALANCING_Update_PaymentMode) ? 1 : 0;

          //Refund
          this.permRefundAllow = this.permHeaderAllow.includes(perm.Refund_Request) ? 1 : 0;
          this.permRefundPaidTransactionTableAllow = this.permHeaderAllow.includes(perm.Refund_Paid_Transaction_View_Listing_Page) ? 1 : 0;
          this.permRefundChargebackTableAllow = this.permHeaderAllow.includes(perm.Refund_Chargeback_View_Listing_Page) ? 1 : 0;
          this.permRefundListingAllow = this.permHeaderAllow.includes(perm.Refund_Listing) ? 1 : 0;

          // Non RMS Receipting & Credit Control
          this.permCCListingAllow = this.permHeaderAllow.includes(perm.Credit_Control_SME_Task_View_Listing) ? 1 : 0;
          this.permNonRmsListingAllow = this.permHeaderAllow.includes(perm.Non_RMS_Receipting_View_Listing_Page) ? 1 : 0;
          this.permCOListingAllow = this.permHeaderAllow.includes(perm.Court_Order_View_Listing_Page) ? 1 : 0;

        },
        error: (error: any) => {
          console.error('❌ Error loading user permissions:', error);
          // Set default permissions on error to prevent UI freeze
          this.setDefaultPermissions();
          setTimeout(() => {this.loadData();}, 1000);
          // this.router.navigate(['/access-denied']);
        }
      });
    this.loadCounterInfo();
  }

  // ✅ Set default permissions to prevent UI freeze on API failure
  setDefaultPermissions() {
    console.log('⚠️ Setting default permissions due to API failure');
    this.permTCDListAllow = 0;
    this.permFGListAllow = 0;
    this.permRPCListAllow = 0;
    this.permALListAllow = 0;
    this.permADListAllow = 0;
    this.permFMSListAllow = 0;
    this.permMFTListAllow = 0;
    this.permMTLListAllow = 0;
    this.permRequestAddMFT = 0;
    this.permRequestEditMFT = 0;
    this.permMTTListAllow = 0;
    this.permMTTDetailsAllow = 0;
    this.permURListAllow = 0;
    this.permRACListAllow = 0;
    this.permBCCListAllow = 0;
    this.permBCDListAllow = 0;
    this.permBCListAllow = 0;
    this.permBTListAllow = 0;
    this.permOTCCollectionReceiptingAllow = 0;
    this.permOTCReturnedChequeAllow = 0;
    this.permOTCCheckInAllow = 0;
    this.permOTCCheckOutAllow = 0;
    this.permBillViewListingAndDetailsAllow = 0;
    this.permBillRegistrationAllow = 0;
    this.permBillCancellationListing = 0;
    this.permBillAdjustmentListing = 0;
    this.permNonBilListing = 0;
    this.permBillingMenu = 0;
    this.permOTCReceiptCancellationAllow = 0;
    this.permOTCReprintReceiptAllow = 0;
    this.permRefundAllow = 0;
    this.permRefundPaidTransactionTableAllow = 0;
    this.permRefundChargebackTableAllow = 0;
    this.permCCListingAllow = 0;
    this.permNonRmsListingAllow = 0;
    this.permCOListingAllow = 0;
    this.permAssignedTaskListAllow = 0;
    this.permCreatedTaskListAllow = 0;
    this.permOTCDBalListingAllow = 0;
    this.permOTCMBalListingAllow = 0;
    this.permBankReconFilesListAllow = 0;
    this.permPaymentReconFilesListAllow = 0;
    this.permReportPMListAllow = 0;
    this.permReportSSListAllow = 0;
    this.permReportFDIDListAllow = 0;
    this.permReportDailyColListAllow = 0;
    this.permReportMatchTransListAllow = 0;
    this.permReportPGSetDisbListAllow = 0;
    this.permReportUnmatchTransListAllow = 0;
    this.permReportDIAgingAllow = 0;
    this.permReportRIPLAgingAllow = 0;
    this.permReportUmAgingAllow = 0 ;
    this.permReportRICPAgingAllow = 0;
    this.permReportCounterCollectionAllow = 0;
    this.permReportDailyBalancingAllow = 0;
    this.permReportMasterBalancingAllow = 0;
    this.permReportOTCCollectionAllow = 0;
    this.permReportOTCCollectionByFeeDetailIDAllow = 0;
    this.permReportOTCReceiptCancellationAllow = 0;
    this.permReportOTCReturnedChequeAllow = 0;
    this.permReportBankInSlipAllow = 0;
    this.permSPListAllow = 0;
    this.permSPMListAllow = 0;
  }

  loadCounterInfo() {
    const permUrl = environment.apiUrl + '/api/otc/v1/checkinstatus';
    // Make the HTTP GET request
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json'
    });
    var requestBody: { [k: string]: any } = {
      i_session_id: localStorage.getItem('otcSession')
    };
    this.http.post(permUrl, requestBody, { headers }).subscribe(
      (response: any) => {
        this.counterCheckInStatus.data = response.data;
        if (this.counterCheckInStatus.data.counter_id.length > 0) {
          this.counterTitle = 'Counter ID: ' + this.counterCheckInStatus.data.counter_id + ' | ';
          //localStorage.setItem('otcSession', this.counterCheckInStatus.data.session_id);
          this.OTCCheckedIn = 1;
        }
        else {
          this.counterCheckInStatus.data = ''; //still update something to push the observer
          this.OTCCheckedIn = 0;
        }
      },
      (error) => {
        console.log(error);
        localStorage.removeItem('otcSession');
        this.counterCheckInStatus.data = ''; //still update something to push the observer
        this.OTCCheckedIn = 0;
      });
  }

  toggleNav() {
    console.log('Toggling Navigation...');
    this.isNavVisible = !this.isNavVisible;
    this.cdr.detectChanges(); // Manually trigger change detection
  }

  private formatDate(date: Date): string {
    const dateString = date.toLocaleDateString('en-GB', {
      // Notice 'en-GB' for day-month-year format
      day: '2-digit',
      month: 'short',
      year: 'numeric',
    });
    const timeString = date.toLocaleTimeString('en-US', {
      hour: '2-digit',
      minute: '2-digit',
      second: '2-digit',
      hour12: false, // Use 24-hour format
    });

    return `${dateString} | ${timeString}`;
  }

  // this.dynamicMessage = "Customer Portal";

  // this.datetimeNow = new Date();

  // // Update the datetime every second
  // setInterval(() => {
  //   this.datetimeNow = new Date();
  //   this.formattedDatetime = this.datetimeNow.toLocaleDateString('en-US', {
  //     month: 'long',
  //     day: 'numeric',
  //     year: 'numeric',
  //     hour: 'numeric',
  //     minute: 'numeric',
  //     second: 'numeric',
  //     hour12: true,
  //   });

  //   // Update the template
  //   this.cdr.detectChanges();
  // }, 1000);
  //   }

  useLanguage(language: string) {
    this.globalService.setGlobalValue(language);
    this.selectedLanguage = language;
    this.translate.use(language); // to change the language at runtime
  }

}