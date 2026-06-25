import { formatDate } from '@angular/common';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Component, OnInit, ElementRef, HostListener, ViewChild } from '@angular/core';
import { Router } from '@angular/router';
import { NgbPaginationConfig } from '@ng-bootstrap/ng-bootstrap';
import { environment } from 'src/environments/environment';
import { AuthService } from 'src/app/core/services/auth.service';
import { perm } from 'src/permissions/perm';

@Component({
  selector: 'app-refund-chargeback',
  templateUrl: './refund-chargeback.component.html',
  styleUrls: ['./refund-chargeback.component.scss']
})
export class RefundChargebackComponent implements OnInit {
  @ViewChild('filterPanel', { static: true }) filterPanel!: ElementRef;
  @ViewChild('filterToggle', { static: true }) filterToggle!: ElementRef;

  // pagination
  page = environment.DefaultPage;
  itemsPerPage = environment.ItemPerPage;
  selectedValue = environment.dropdownOptions[0];
  dropdownOptions = environment.dropdownOptions;
  dropDownSize = environment.DropDownSize;

  // table
  model: any[] = [];
  totalRecords = 0;

  // filters (list method — 4 values)
  orn_no: string | null = null;   // Order Reference No
  rcpt_no: string | null = null;   // Receipt No
  txn_id: string | null = null;   // Transaction ID
  selected2!: Date[];              // Transaction Date range [from, to]

  // Configuring Permissions for User and roles
  permRefundChargebackTable = perm.Refund_Chargeback_View_Listing_Page;
  permRefundChargebackTableAllow = "";
  permRefundChargebackTableAllowListAllow: number = 1;

  // ui state
  isDisplay = false;
  isLoading = false;
  rightSectionCollapsed = true;

  // datepicker support
  bsValue2 = new Date();
  minDate2 = new Date();

  constructor(
    private http: HttpClient,
    private config: NgbPaginationConfig,
    private router: Router,
    private elementRef: ElementRef,
    private authService: AuthService,
  ) {
    // keep your pagination behavior consistent
    this.config.maxSize = environment.PaginationMaxSize;
    this.config.boundaryLinks = true;
  }

  private getDefaultDateRange(): Date[] {
    const today = new Date();
    const yesterday = new Date(today);
    yesterday.setDate(today.getDate() - 1);

    // normalize to local midnight
    const y = new Date(yesterday.getFullYear(), yesterday.getMonth(), yesterday.getDate());
    const t = new Date(today.getFullYear(), today.getMonth(), today.getDate());

    return [y, t];
  }

  ngOnInit(): void {
    // init date range limiter similar to your PTT page
    this.bsValue2 = new Date();
    this.minDate2.setMonth(this.bsValue2.getMonth() - 1);
    // default range = yesterday → today (normalized to midnight)
    this.selected2 = this.getDefaultDateRange();
    // initial load
    this.loadData();
  }

  // pagination dropdown change
  LoadData(singleItem: number) {
    this.selectedValue = singleItem;
    this.itemsPerPage = this.selectedValue;
    this.loadData();
  }



  // core loader — mirrors "refund ptt listing" style
  loadData(): void {

    this.authService.checkUserRole(this.authService.username, this.permRefundChargebackTable)
      .subscribe(
        (response: any) => {
          this.permRefundChargebackTableAllow = response.data;
          this.permRefundChargebackTableAllowListAllow = this.permRefundChargebackTableAllow.includes(perm.Refund_Chargeback_View_Listing_Page) ? 1 : 0;
          console.log(this.permRefundChargebackTableAllowListAllow);
          if (this.permRefundChargebackTableAllowListAllow === 0) {
            console.log("access-denied");
            this.router.navigate(['/access-denied']);
            return; // Exit the function to prevent further execution
          }

          const headers = new HttpHeaders({
            Authorization: environment.authKey,
            'Content-Type': 'application/json',
          });

          this.isDisplay = true;
          this.isLoading = true;

          const url = environment.apiUrl + '/api/refundl/v1/getrefundtht';

          const Body: any = {
            i_page: this.page.toString(),
            i_size: this.itemsPerPage.toString(),
            i_order_status: 'P',
            i_rms_type: 'Online',
            i_platform_call: 'bo'
          };

          // 1) Order Reference No.
          if (this.orn_no && this.orn_no.trim()) {
            Body.i_orn_no = this.orn_no.trim();
          }

          // 2) Receipt No.
          if (this.rcpt_no && this.rcpt_no.trim()) {
            Body.i_rcpt_no = this.rcpt_no.trim();
          }

          // 3) Transaction ID
          if (this.txn_id && this.txn_id.trim()) {
            Body.i_txn_id = this.txn_id.trim();
          }

          // 4) Transaction Date range
          if (this.selected2 && this.selected2.length === 2) {
            // NOTE: Angular date format tokens are lowercase: 'yyyy-MM-dd'
            Body.i_orn_dt_fr = formatDate(this.selected2[0], 'yyyy-MM-dd', 'en');
            Body.i_orn_dt_to = formatDate(this.selected2[1], 'yyyy-MM-dd', 'en');
          }
          console.log('Request Body:', Body);
          // request
          this.http.post(url, Body, { headers }).subscribe(
            (resp: any) => {
              this.model = resp.data || [];
              if (this.model.length === 0) {
                this.totalRecords = 0;
                this.isDisplay = false;
              } else {
                this.totalRecords = this.model[0].total ?? this.model.length;
              }
              this.isLoading = false;
            },
            (error) => {
              console.error(error);
              this.isLoading = false;
            }
          );
        },
        (error) => {
          console.error(error);
        }
      );
  }

  // search actions (list method)
  apply(): void {
    this.page = 1;            // reset to first page on new search
    this.loadData();
    this.rightSectionCollapsed = true;
  }

  reset(): void {
    this.orn_no = null;
    this.rcpt_no = null;
    this.txn_id = null;

    // ✅ Put back default date range (yesterday → today)
    this.selected2 = this.getDefaultDateRange();
  }

  refreshMainPage(): void {
    this.page = 1;
    this.loadData();
  }

  // nav to details
  navigateToRefundInfo(item: any): void {
    this.nevigatetoChargeBack(item);
  }

  nevigatetoChargeBack(item: any): void {
    const rms_type = item.rms_type;
    const mtt_id = item.mtt_id;
    const txn_id = item.txn_id;
    const orn_no = item.orn_no;
    const order_status = item.order_status;
    const rtt_status = item.rtt_status;

    this.router.navigate(
      ['/refund-chargeback-info'],
      { state: { mtt_id, orn_no, txn_id, rms_type, order_status, rtt_status } }
    );
  }

  // toggle
  toggleRightSection() {
    this.rightSectionCollapsed = !this.rightSectionCollapsed;
  }

  /** prevent outside click handler from firing when the toggle is clicked */
  onFilterToggleClick(event: MouseEvent) {
    event.stopPropagation();
    this.toggleRightSection();
  }

  // collapse filter when clicking outside
  @HostListener('document:click', ['$event'])
  onDocumentClick(event: MouseEvent) {
    if (this.rightSectionCollapsed) { return; }

    const target = event.target as HTMLElement;

    if (this.filterPanel.nativeElement.contains(target)) { return; }
    if (this.filterToggle.nativeElement.contains(target)) { return; }

    this.rightSectionCollapsed = true;
  }
}
