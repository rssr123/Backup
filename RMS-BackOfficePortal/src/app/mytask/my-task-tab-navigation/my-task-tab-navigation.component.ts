// Wilson's code
import { Component, EventEmitter, Input, Output, OnInit, OnDestroy, ChangeDetectorRef } from '@angular/core';
import { NotificationService } from 'src/app/core/services/notification.service';
import { TranslateService } from '@ngx-translate/core';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-my-task-tab-navigation',
  templateUrl: './my-task-tab-navigation.component.html',
  styleUrls: ['./my-task-tab-navigation.component.scss']
})
export class MyTaskTabNavigationComponent implements OnInit, OnDestroy {
  @Input() currentTab: string = 'mft'; 
  @Input() currentTaskMode: string = '';
  @Output() tabChange = new EventEmitter<string>(); // Event emitter for tab changes
  @Output() tableLoadComplete = new EventEmitter<string>(); // Event emitter when table loading is complete

  private subscriptions: Subscription[] = [];

  constructor(
    public notificationService: NotificationService, 
    private translate: TranslateService,
    private cdr: ChangeDetectorRef
  ) {}

  // Initialize with default values, will be updated after table loads
  mftAssignedTaskNotification = this.notificationService.$notificationMyTask;
  mftCreatedTaskNotification = this.notificationService.$notificationCreatedTask;
  refundAssignedTaskNotification = this.notificationService.$notificationRefundAssignedTasks;
  refundCreatedTaskNotification = this.notificationService.$notificationRefundCreatedTasks;
  billingAssignedTaskNotification = this.notificationService.$notificationBillingAssignedTasks;
  billingCreatedTaskNotification = this.notificationService.$notificationBillingCreatedTasks;
  otcAssignedTaskNotification = this.notificationService.$notificationOctRCAssignedTasks;
  otcCreatedTaskNotification = this.notificationService.$notificationOctRCCreatedTasks;
  cccAssignedTaskNotification = this.notificationService.$notificationCCCAssignedTasks;
  cccCreatedTaskNotification = this.notificationService.$notificationCCCCreatedTasks;

  tabs = [
    { id: 'mft', label: 'labels.Master Fee Table (MFT)', assignedTaskCount: this.mftAssignedTaskNotification, createdTaskCount: this.mftCreatedTaskNotification },
    { id: 'refund', label: 'labels.refund', assignedTaskCount: this.refundAssignedTaskNotification, createdTaskCount: this.refundCreatedTaskNotification },
    { id: 'billing', label: 'menu.billing', assignedTaskCount: this.billingAssignedTaskNotification, createdTaskCount: this.billingCreatedTaskNotification },
    { id: 'otc', label: 'labels.overthecounter', assignedTaskCount: this.otcAssignedTaskNotification, createdTaskCount: this.otcCreatedTaskNotification },
    { id: 'ccc', label: 'menu.creditcontrol', assignedTaskCount: this.cccAssignedTaskNotification, createdTaskCount: this.cccCreatedTaskNotification }
  ];

  ngOnInit(): void {
    // Don't load notification counts on init - wait for table to load
    // Subscribe to observable changes to trigger change detection
    this.subscriptions.push(
      this.notificationService.$notificationMyTask.subscribe(() => this.cdr.detectChanges()),
      this.notificationService.$notificationCreatedTask.subscribe(() => this.cdr.detectChanges()),
      this.notificationService.$notificationRefundAssignedTasks.subscribe(() => this.cdr.detectChanges()),
      this.notificationService.$notificationRefundCreatedTasks.subscribe(() => this.cdr.detectChanges()),
      this.notificationService.$notificationBillingAssignedTasks.subscribe(() => this.cdr.detectChanges()),
      this.notificationService.$notificationBillingCreatedTasks.subscribe(() => this.cdr.detectChanges()),
      this.notificationService.$notificationOctRCAssignedTasks.subscribe(() => this.cdr.detectChanges()),
      this.notificationService.$notificationOctRCCreatedTasks.subscribe(() => this.cdr.detectChanges()),
      this.notificationService.$notificationCCCAssignedTasks.subscribe(() => this.cdr.detectChanges()),
      this.notificationService.$notificationCCCCreatedTasks.subscribe(() => this.cdr.detectChanges())
    );
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(sub => sub.unsubscribe());
  }

  // Method to be called when table loading is complete
  onTableLoadComplete(tabId: string): void {
    // First load notification counts for the specific tab
    this.loadNotificationCountsForTab(tabId);
    
    // Then refresh all notification counts to ensure latest data
    this.loadAllNotificationCounts();
    
    this.tableLoadComplete.emit(tabId);
  }

  // Load notification counts for specific tab
  private loadNotificationCountsForTab(tabId: string): void {
    switch(tabId) {
      case 'mft':
        this.notificationService.getMyTaskNotificationCount();
        this.notificationService.getCreatedTaskNotificationCount();
        break;
      case 'refund':
        this.notificationService.getRefundAssignedTaskNotificationCount();
        this.notificationService.getRefundCreatedTaskNotificationCount();
        break;
      case 'billing':
        this.notificationService.getBillingAssignedTaskNotificationCount();
        this.notificationService.getBillingCreatedTaskNotificationCount();
        break;
      case 'otc':
        this.notificationService.getOtcRCAssignedTaskNotificationCount();
        this.notificationService.getOtcRCCreatedTaskNotificationCount();
        break;
      case 'ccc':
        this.notificationService.getCCCAssignedTaskNotificationCount();
        this.notificationService.getCCCCreatedTaskNotificationCount();
        break;
    }
  }

  // Load all notification counts (can be called after all tables are loaded)
  loadAllNotificationCounts(): void {
    this.notificationService.getTaskNotificationCounts(this.notificationService.username);
  }

  selectTab(tabId: string): void {
    this.currentTab = tabId;
    // this.currentTaskMode = taskMode;
    this.tabChange.emit(tabId); // Notify parent component of tab change
    
    // Refresh notification counts when switching tabs to ensure latest data
    this.notificationService.refreshNotificationCounts();
  }
}
