import { HTTP_INTERCEPTORS, HttpClient, HttpClientModule } from '@angular/common/http';
import { APP_INITIALIZER, NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { RouterModule } from '@angular/router';
import { TranslateLoader, TranslateModule } from '@ngx-translate/core';
import { TranslateHttpLoader } from '@ngx-translate/http-loader';
import { NgxBootstrapIconsModule, allIcons } from 'ngx-bootstrap-icons';
import { FormsModule } from '@angular/forms';
import { MatIconModule } from '@angular/material/icon';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { NgSelectModule } from '@ng-select/ng-select';
import { ButtonModule } from 'primeng/button';
import { ConfirmDialogModule } from 'primeng/confirmdialog';
import { ConfirmPopupModule } from 'primeng/confirmpopup';
import { RippleModule } from 'primeng/ripple';
import { TableModule } from 'primeng/table';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatDialogModule } from '@angular/material/dialog';
import { DynamicDialogModule } from 'primeng/dynamicdialog';
import { PaginatorModule } from 'primeng/paginator';
import { MatSelectModule } from '@angular/material/select';
import { NgMultiSelectDropDownModule } from 'ng-multiselect-dropdown';
import { BsDatepickerModule } from 'ngx-bootstrap/datepicker';
import { CommonModule, DatePipe } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { MAT_DATE_LOCALE, MatNativeDateModule } from '@angular/material/core';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { NgxDaterangepickerMd } from 'ngx-daterangepicker-material';
import { NgChartsModule } from 'ng2-charts';

import { AppComponent } from './app.component';
import { HomeComponent } from './home/home.component';
import { FooterComponent } from './masterpage/footer/footer.component';
import { HeaderComponent } from './masterpage/header/header.component';
import { MultiLanguageComponent } from './multi-language/multi-language.component';

import { FeeGroupAddComponent } from './feegroup/fee-group-add/fee-group-add.component';
import { FeeGroupListingComponent } from './feegroup/fee-group-listing/fee-group-listing.component';
import { FeeGroupUpdateComponent } from './feegroup/fee-group-update/fee-group-update.component';
import { MasterFeeTableComponent } from './mastercomponent/master-fee-table/master-fee-table.component';
import { MasterTaskListComponent } from './mastercomponent/master-task-list/master-task-list.component';
import { MftItemDetailsComponent } from './mft/mft-item-details/mft-item-details.component';

import { BankReconBkListingComponent } from './BankRecon/bank-recon-bk-listing/bank-recon-bk-listing.component';
import { BankReconDetailsComponent } from './BankRecon/bank-recon-details/bank-recon-details.component';
import { BankReconComponent } from './BankRecon/bank-recon-listing/bank-recon-listing.component';
import { BankReconNoBankListingComponent } from './BankRecon/bank-recon-no-bank-listing/bank-recon-no-bank-listing.component';
import { BankReconPgFileTxnListingComponent } from './BankRecon/bank-recon-pg-file-txn-listing/bank-recon-pg-file-txn-listing.component';
import { BankReconPgListingComponent } from './BankRecon/bank-recon-pg-listing/bank-recon-pg-listing.component';
import { DeferredIncomeDetailsComponent } from './DI/deferred-income-details/deferred-income-details.component';
import { DeferredIncomeListingComponent } from './DI/deferred-income-listing/deferred-income-listing.component';
import { RicpDetailsComponent } from './RICP/ricp-details/ricp-details.component';
import { RicpListingComponent } from './RICP/ricp-listing/ricp-listing.component';
import { RIPLDetailComponent } from './RIPL/ri-pl-detail/ri-pl-detail.component';
import { RIPLComponent } from './RIPL/ri-pl-listing/ri-pl-listing.component';
import { AccessDeniedComponent } from './access-denied/access-denied.component';
import { AuthGuard } from './core/guard/auth.guard';
import { AppRoutingModule } from './core/route/app-routing.module';
import { AuthService } from './core/services/auth.service';
import { FeeGroupDeleteComponent } from './feegroup/fee-group-delete/fee-group-delete.component';
import { FmsAddComponent } from './fms/fms-add/fms-add.component';
import { FmsListingComponent } from './fms/fms-listing/fms-listing.component';
import { FmsUpdateComponent } from './fms/fms-update/fms-update.component';
import { FMSAccountListingComponent } from './fmsaccount/fms-account-listing/fms-account-listing.component';
import { FMSAccountUpdateComponent } from './fmsaccount/fms-account-update/fms-account-update.component';
import { FmsledgerAddComponent } from './fmsledger/fmsledger-add/fmsledger-add.component';
import { FmsledgerListingComponent } from './fmsledger/fmsledger-listing/fmsledger-listing.component';
import { FPASchedulerListingComponent } from './fpascheduler/fpa-scheduler-listing/fpa-scheduler-listing.component';
import { LoginComponent } from './masterpage/login/login.component';
import { LogoutComponent } from './masterpage/logout/logout.component';
import { CreatedTaskDetailsComponent } from './mft/created-task-details/created-task-details.component';
import { MftFaApprAddComponent } from './mft/mft-fa-appr-add/mft-fa-appr-add.component';
import { MftFaApprEditComponent } from './mft/mft-fa-appr-edit/mft-fa-appr-edit.component';
import { MftFaFaEditComponent } from './mft/mft-fa-fa-edit/mft-fa-fa-edit.component';
import { MftFaFaRqtAddComponent } from './mft/mft-fa-fa-rqt-add/mft-fa-fa-rqt-add.component';
import { MftFaFhodApprAddComponent } from './mft/mft-fa-fhod-appr-add/mft-fa-fhod-appr-add.component';
import { MftFhodApprAddComponent } from './mft/mft-fhod-appr-add/mft-fhod-appr-add.component';
import { MftReqFormAddComponent } from './mft/mft-req-form-add/mft-req-form-add.component';
import { MftReqFormEditComponent } from './mft/mft-req-form-edit/mft-req-form-edit.component';
import { MftReqhodApprAddComponent } from './mft/mft-reqhod-appr-add/mft-reqhod-appr-add.component';
import { MftReqhodApprEditComponent } from './mft/mft-reqhod-appr-edit/mft-reqhod-appr-edit.component';
import { TaskDetailsComponent } from './mft/task-details/task-details.component';
import { MttDetailsItemListingComponent } from './mtt/mtt-details-item-listing/mtt-details-item-listing.component';
import { MttDetailsListingComponent } from './mtt/mtt-details-listing/mtt-details-listing.component';
import { MttDetailsPgListingComponent } from './mtt/mtt-details-pg-listing/mtt-details-pg-listing.component';
import { MttListingComponent } from './mtt/mtt-listing/mtt-listing.component';
import { MyTaskAssignedTasksComponent } from './mytask/my-task-assigned-tasks/my-task-assigned-tasks.component';
import { MyTaskCreatedTaskComponent } from './mytask/my-task-created-task/my-task-created-task.component';
import { PgDetailListingComponent } from './pgrecon/pg-detail-listing/pg-detail-listing.component';
import { PgReconDetailComponent } from './pgrecon/pg-recon-detail/pg-recon-detail.component';
import { PgReconListingComponent } from './pgrecon/pg-recon-listing/pg-recon-listing.component';
import { RmsDetailListingComponent } from './pgrecon/rms-detail-listing/rms-detail-listing.component';
import { DailyCollectionListingComponent } from './report/collection-reports/daily-collection-listing/daily-collection-listing.component';
import { DeferredIncomeAgingComponent } from './report/accrual-reports/deferred-income-aging/deferred-income-aging.component';
import { MatchedTransactionListingComponent } from './report/reconciliation-reports/matched-transaction-listing/matched-transaction-listing.component';
import { PgSettlementDisbursementListingComponent } from './report/reconciliation-reports/pg-settlement-disbursement-listing/pg-settlement-disbursement-listing.component';
import { RICPAgingComponent } from './report/accrual-reports/ricp-aging/ricp-aging.component';
import { UnmatchedAgingComponent } from './report/reconciliation-reports/unmatched-aging/unmatched-aging.component';
import { PaymentCollectionComponent } from './report/collection-reports/payment-collection/payment-collection.component';
import { RoleAndPermissionsConfigurationsAddRolesComponent } from './roles-and-permissions-configuration/role-and-permissions-configurations-add-roles/role-and-permissions-configurations-add-roles.component';
import { RoleAndPermissionsConfigurationsDetailsComponent } from './roles-and-permissions-configuration/role-and-permissions-configurations-details/role-and-permissions-configurations-details.component';
import { RoleAndPermissionsConfigurationsDiscardChangesComponent } from './roles-and-permissions-configuration/role-and-permissions-configurations-discard-changes/role-and-permissions-configurations-discard-changes.component';
import { RoleAndPermissionsConfigurationsUpdateRoleStatusComponent } from './roles-and-permissions-configuration/role-and-permissions-configurations-update-role-status/role-and-permissions-configurations-update-role-status.component';
import { CustomInterceptor } from './shared/auth.interceptor';
import { TaxCodeAddComponent } from './taxcode/tax-code-add/tax-code-add.component';
import { TaxCodeDeleteComponent } from './taxcode/tax-code-delete/tax-code-delete.component';
import { TaxCodeListingComponent } from './taxcode/tax-code-listing/tax-code-listing.component';
import { TaxCodeUpdateComponent } from './taxcode/tax-code-update/tax-code-update.component';
import { UserroleComponent } from './userrole/userrole.component';

import { OtcCollectionReceiptingComponent } from './OTC/otc-collection-receipting/otc-collection-receipting.component';
import { OtcPaymentScreenComponent } from './OTC/otc-payment-screen/otc-payment-screen.component';
import { DashboardComponent } from './dashboard/dashboard.component';
// import { DynamicrowssampleComponent } from './dynamicrowssample/dynamicrowssample.component';
import { RIPLAgingReportComponent } from './report/accrual-reports/ripl-aging-report/ripl-aging-report.component';
import { UtListingDaysComponent } from './report/reconciliation-reports/unmatched-transaction-listing/ut-listing-days/ut-listing-days.component';
import { UtListingMonthsComponent } from './report/reconciliation-reports/unmatched-transaction-listing/ut-listing-months/ut-listing-months.component';
import { UtListingTransactionsComponent } from './report/reconciliation-reports/unmatched-transaction-listing/ut-listing-transactions/ut-listing-transactions.component';
import { RefundAccountCodeListingComponent } from './refundaccountcode/refund-account-code-listing/refund-account-code-listing.component';
import { RefundAccountCodeAddComponent } from './refundaccountcode/refund-account-code-add/refund-account-code-add.component';
import { RefundAccountCodeDeleteComponent } from './refundaccountcode/refund-account-code-delete/refund-account-code-delete.component';
import { RefundAccountCodeUpdateComponent } from './refundaccountcode/refund-account-code-update/refund-account-code-update.component';

import { BranchCodeCounterListingComponent } from './branchcodecounter/branch-code-counter-listing/branch-code-counter-listing.component';
import { BranchCodeCounterAddComponent } from './branchcodecounter/branch-code-counter-add/branch-code-counter-add.component';
import { BranchCodeCounterUpdateComponent } from './branchcodecounter/branch-code-counter-update/branch-code-counter-update.component';
import { BranchCodeCounterDeleteComponent } from './branchcodecounter/branch-code-counter-delete/branch-code-counter-delete.component';

import { CounterCollectionComponent } from './report/otc-report/counter-collection/counter-collection.component';
import { DailyBalancingComponent } from './report/otc-report/daily-balancing/daily-balancing.component';
import { MasterBalancingComponent } from './report/otc-report/master-balancing/master-balancing.component';
import { OtcCollectionComponent } from './report/otc-report/otc-collection/otc-collection.component';
import { OtcCollectionPlusComponent } from './report/otc-report/otc-collection-plus/otc-collection-plus.component';
import { OtcReceiptCancellationComponent } from './report/otc-report/otc-receipt-cancellation/otc-receipt-cancellation.component';
import { OtcReturnedChequeReportComponent } from './report/otc-report/otc-returned-cheque-report/otc-returned-cheque-report.component';
import { OtcReturnedChequeComponent } from './OTC/otc-returned-cheque/otc-returned-cheque.component';
import { BankInSlipComponent } from './report/otc-report/bank-in-slip/bank-in-slip.component';
import { OtcReceiptScreenComponent } from './OTC/otc-receipt-screen/otc-receipt-screen.component';
import { NonBillingRegistrationComponent } from './non-billing-registration/non-billing-registration.component';
import { ReprintreceiptComponent } from './OTC/reprintreceipt/reprintreceipt.component';
import { ReprintreceiptdetailsComponent } from './OTC/reprintreceiptdetails/reprintreceiptdetails.component';
import { ReprintreceiptjustificationComponent } from './OTC/reprintreceiptjustification/reprintreceiptjustification.component';

import { CounterBalancingEditComponent } from './OTC/counter-balancing-edit/counter-balancing-edit.component';
import { CounterBalancingListingComponent } from './OTC/counter-balancing-listing/counter-balancing-listing.component';


import { RefundPTTListingComponent } from './refund/refund-ptt-listing/refund-ptt-listing.component';
import { RefundInitialFAComponent } from './refund/refund-initial-fa/refund-initial-fa.component';
import { RefundRequestSelectSMEComponent } from './refund/refund-initial-fa/refund-request-select-sme/refund-request-select-sme.component';
import { RefundApprovalFaComponent } from './refund/refund-task-list/refund-approval-fa/refund-approval-fa.component';

//import { BillingCancellationRecurringComponent } from './billing-cancellation-recurring/billing-cancellation-recurring.component';
//import { BillingAdjustmentRecurringComponent } from './billing-adjustment-recurring/billing-adjustment-recurring.component';
import { RefundApprovalUserroleComponent } from './refund/refund-task-list/refund-approval-userrole/refund-approval-userrole.component';
import { OTCCheckInComponent } from './OTC/otc-checkin/otc-checkin.component';
import { OTCCheckOutComponent } from './OTC/otc-checkout/otc-checkout.component';
import { BillRegistrationComponent } from './billing/billing-registration/billing-registration.component';
import { BillListingComponent } from './billing/billing-listing/billing-listing.component';
import { BillApprovalComponent } from './billing/billing-approval/billing-approval.component';
import { BillingCancellationSearchComponent } from './billing/billing-cancellation-search/billing-cancellation-search.component';
import { BillingCancellationComponent } from './billing/billing-cancellation/billing-cancellation.component';
import { BillingCancellationListingComponent } from './billing/billing-cancellation-listing/billing-cancellation-listing.component';
import { BillingAdjustmentSearchComponent } from './billing/billing-adjustment-search/billing-adjustment-search.component';
import { BillingAdjustmentComponent } from './billing/billing-adjustment/billing-adjustment.component';
import { OtcEmvReconciliationComponent } from './OTC/otc-emv-reconciliation/otc-emv-reconciliation.component';
import { OtcEmvReconciliationDetailsComponent } from './OTC/otc-emv-reconciliation-details/otc-emv-reconciliation-details.component';
import { BillingDetailsComponent } from './billing/billing-details/billing-details.component';
import { BillAdjustmentApprovalComponent } from 'src/app/billing/billing-adjustment-approval/billing-adjustment-approval.component';
import { BillCancellationApprovalComponent } from 'src/app/billing/billing-cancellation-approval/billing-cancellation-approval.component';
import { BibssListingComponent } from './billingissuance/billing-issuance-by-ss/bibss-listing/bibss-listing.component';
import { BibssDetailsComponent } from './billingissuance/billing-issuance-by-ss/bibss-details/bibss-details.component';
import { BibssListComponent } from './billingissuance/billing-issuance-by-ss/bibss-list/bibss-list.component';
//import { BillingCancellationRecurringComponent } from './billing-cancellation-recurring/billing-cancellation-recurring.component';
//import { BillingAdjustmentRecurringComponent } from './billing-adjustment-recurring/billing-adjustment-recurring.component';
import { NonBillingListingComponent } from './non-billing-listing/non-billing-listing.component';
import { NonBillingDetailsComponent } from './non-billing-details/non-billing-details.component';
import { RiltListingComponent } from './RILT/rilt-listing/rilt-listing.component';


import { ReceiptNoValidationComponent } from './OTC/otc-receipt-cancellation/receipt-no-validation/receipt-no-validation.component';
import { OtcReceiptDetailsComponent } from './OTC/otc-receipt-cancellation/otc-receipt-details/otc-receipt-details.component';
import { ReceiptCancellationApprovalAndJustificationComponent } from './OTC/otc-receipt-cancellation/receipt-cancellation-approval-and-justification/receipt-cancellation-approval-and-justification.component';
import { UpdateTaskStatusComponent } from './OTC/otc-receipt-cancellation/update-task-status/update-task-status.component';
import { BibssCustomerIdValidationComponent } from './billingissuance/billing-issuance-by-ss/bibss-customer-id-validation/bibss-customer-id-validation.component';
import { MyTaskTabNavigationComponent } from './mytask/my-task-tab-navigation/my-task-tab-navigation.component';
import { MyTaskPublicTaskComponent } from './mytask/my-task-public-task/my-task-public-task.component'; // Wilson's

// import {NgDynamicBreadcrumbModule} from 'ng-dynamic-breadcrumb';

import { BranchCodeAddComponent } from './branchcode/branch-code-add/branch-code-add.component';
import { BranchCodeListingComponent } from './branchcode/branch-code-listing/branch-code-listing.component';
import { BranchCodeUpdateComponent } from './branchcode/branch-code-update/branch-code-update.component';
import { BranchCodeDeleteComponent } from './branchcode/branch-code-delete/branch-code-delete.component';

import { BillingClassAddComponent } from './billingclass/billing-class-add/billing-class-add.component';
import { BillingClassUpdateComponent } from './billingclass/billing-class-update/billing-class-update.component';
import { BillingClassListingComponent } from './billingclass/billing-class-listing/billing-class-listing.component';
import { BillingClassDeleteComponent } from './billingclass/billing-class-delete/billing-class-delete.component';
import { BillingTypeAddComponent } from './billingtype/billing-type-add/billing-type-add.component';
import { BillingTypeDeleteComponent } from './billingtype/billing-type-delete/billing-type-delete.component';
import { BillingTypeListingComponent } from './billingtype/billing-type-listing/billing-type-listing.component';
import { BillingTypeUpdateComponent } from './billingtype/billing-type-update/billing-type-update.component';
import { RefundStatusDetailedComponent } from './report/refund-reports/refund-status/refund-status.component';
import { RefundSsmReportComponent } from './report/refund-reports/refund-ssm-report/refund-ssm-report.component';
import { RefundAgingComponent } from './report/refund-reports/refund-aging/refund-aging.component';
import { BillingReportComponent } from './report/catalogue-reports/billing-report/billing-report.component';
import { CatalogueProductServiceReportComponent } from './report/catalogue-reports/catalogue-product-service-report/catalogue-product-service-report.component';
import { SummaryBillingReportComponent } from './report/catalogue-reports/summary-billing-report/summary-billing-report.component';
import { DetailedBillingReportComponent } from './report/catalogue-reports/detailed-billing-report/detailed-billing-report.component';
import { DetailedBillingBtReportComponent } from './report/catalogue-reports/detailed-billing-bt-report/detailed-billing-bt-report.component';
import { ServiceProviderPaymentListingComponent } from './serviceprovider/service-provider-payment-listing/service-provider-payment-listing.component';
import { CourtOrderListingComponent } from './courtorder/court-order-listing/court-order-listing.component';
import { CourtOrderDetailsComponent } from './courtorder/court-order-details/court-order-details.component';
import { CreditControlCaseListingComponent } from './courtorder/credit-control-case-listing/credit-control-case-listing.component';
import { CreditControlCaseComponent } from './creditcontrol/credit-control-case/credit-control-case.component';
import { ServiceProviderMaintenanceAddComponent } from './serviceprovider/service-provider-maintenance/service-provider-maintenance-add/service-provider-maintenance-add.component';
import { ServiceProviderMaintenanceListingComponent } from './serviceprovider/service-provider-maintenance/service-provider-maintenance-listing/service-provider-maintenance-listing.component';
import { ServiceProviderMaintenanceDeleteComponent } from './serviceprovider/service-provider-maintenance/service-provider-maintenance-delete/service-provider-maintenance-delete.component';
import { ServiceProviderMaintenanceUpdateComponent } from './serviceprovider/service-provider-maintenance/service-provider-maintenance-update/service-provider-maintenance-update.component';
import { AccrualReportsListingComponent } from './report/accrual-reports/accrual-reports-listing/accrual-reports-listing.component';
import { CatalogueReportsListingComponent } from './report/catalogue-reports/catalogue-reports-listing/catalogue-reports-listing.component';
import { CollectionReportsListingComponent } from './report/collection-reports/collection-reports-listing/collection-reports-listing.component';
import { OtcReportsListingComponent } from './report/otc-report/otc-reports-listing/otc-reports-listing.component';
import { ReconciliationReportsListingComponent } from './report/reconciliation-reports/reconciliation-reports-listing/reconciliation-reports-listing.component';
import { RefundReportsListingComponent } from './report/refund-reports/refund-reports-listing/refund-reports-listing.component';
import { PaymentCollectionFeeDtIdComponent } from './report/collection-reports/payment-collection-fee-dt-id/payment-collection-fee-dt-id.component';
import { PaymentCollectionPymtMdComponent } from './report/collection-reports/payment-collection-pymt-md/payment-collection-pymt-md.component';
import { PaymentCollectionSSComponent } from './report/collection-reports/payment-collection-s-s/payment-collection-s-s.component';
import { NonRmsReceiptingListingComponent } from './non-rms-receipting-listing/non-rms-receipting-listing.component';
import { NonRmsReceiptingDetailsComponent } from './non-rms-receipting-details/non-rms-receipting-details.component';
import { CreditControlSmeTaskListComponent } from './credit-control-sme-task-list/credit-control-sme-task-list.component';
import { AgBankStmtListingComponent } from './ag-bank-stmt-listing/ag-bank-stmt-listing.component';
import { AgBankTxnListingComponent } from './ag-bank-txn-listing/ag-bank-txn-listing.component';
import { AgPgFileTxnListingComponent } from './ag-pg-file-txn-listing/ag-pg-file-txn-listing.component';
import { AgDeleteFileDiaglogComponent } from './ag-delete-file-diaglog/ag-delete-file-diaglog.component';
import { CreditControlCaseViewerComponent } from 'src/app/creditcontrol/credit-control-case-view/credit-control-case-view.component';
import { DailyBalancingDetailComponent } from './OTC/daily-balancing-detail/daily-balancing-detail.component';
import { DailyBalancingListingComponent } from './OTC/daily-balancing-listing/daily-balancing-listing.component';
import { DailyBalancingWarningComponent } from './OTC/daily-balancing-warning/daily-balancing-warning.component';
import { MasterBalancingListingComponent } from './OTC/master-balancing-listing/master-balancing-listing.component';
import { MasterBalancingDetailComponent } from './OTC/master-balancing-detail/master-balancing-detail.component';
import { RefundListingComponent } from './refund/refund-listing/refund-listing.component';
import { RefundListingInfoComponent } from './refund/refund-listing-info/refund-listing-info.component';
import { RefundListingInfoRfComponent } from './refund/refund-listing-info-rf/refund-listing-info-rf.component';
import { RefundApproveBlankFormComponent } from './refund/refund-approve-blank-form/refund-approve-blank-form.component';
import { MasterBankinslipComponent } from './OTC/master-bankinslip/master-bankinslip.component';
import { CancelTaskComponent } from './OTC/otc-receipt-cancellation/cancel-task/cancel-task.component';
import { OtcEmvQueryComponent } from './OTC/otc-emv-query/otc-emv-query.component';
import { LoadingOverlayComponent } from './components/loading-overlay/loading-overlay.component';
import { RefundChargebackComponent } from './refund/refund-chargeback/refund-chargeback.component';
import { RefundChargebackInfoComponent } from './refund/refund-chargeback-info/refund-chargeback-info.component';
import { MftItemTaskListComponent } from './mft/mft-item-task-list/mft-item-task-list.component';
import { MftCancelTaskComponent } from './mft/mft-cancel-task/mft-cancel-task.component';
import { ConfirmSubmitComponent } from './mft/confirm-submit/confirm-submit.component';
import { FilterPipe } from './filter.pipe';
import { TimeoutWarningComponent } from './shared/timeout-warning/timeout-warning.component';
import { TriggerNotificationUpdateService } from 'src/app/core/services/TriggerNotificationUpdateService.service';
import { RefundCreatedTaskDetailsComponent } from './refund/refund-created-task-details/refund-created-task-details.component';
import { RefundCreatedTaskRfDetailsComponent } from './refund/refund-created-task-rf-details/refund-created-task-rf-details.component';
// import { RetryInterceptor } from 'src/retry.interceptor';
import { MttJustiListingComponent } from './mtt/mtt-justi-listing/mtt-justi-listing.component';
import { NonceInterceptor } from './core/services/nonce.interceptor';

export function HttpLoaderFactory(http: HttpClient) {
  return new TranslateHttpLoader(http, './assets/i18n/', `.json?v=${new Date().getTime()}`);
}


@NgModule({
  declarations: [
    AppComponent,
    HomeComponent,
    MultiLanguageComponent,
    FooterComponent,
    HeaderComponent,
    MasterFeeTableComponent,
    MasterTaskListComponent,
    MftItemDetailsComponent,
    FeeGroupListingComponent,
    FeeGroupAddComponent,
    FeeGroupUpdateComponent,
    FeeGroupDeleteComponent,
    LogoutComponent,
    LoginComponent,
    FmsListingComponent,
    FmsUpdateComponent,
    FmsAddComponent,
    MftReqFormAddComponent,
    TaxCodeAddComponent,
    TaxCodeDeleteComponent,
    TaxCodeListingComponent,
    TaxCodeUpdateComponent,
    MftFaApprAddComponent,
    MftReqhodApprAddComponent,
    MyTaskAssignedTasksComponent,
    MyTaskCreatedTaskComponent,
    TaskDetailsComponent,
    MftFaFhodApprAddComponent,
    MftFhodApprAddComponent,
    CreatedTaskDetailsComponent,
    MftFaFaRqtAddComponent,
    MftFaApprEditComponent,
    MftFaFaEditComponent,
    MftReqFormEditComponent,
    MftReqhodApprEditComponent,
    FmsledgerListingComponent,
    UserroleComponent,
    DeferredIncomeDetailsComponent,
    DeferredIncomeListingComponent,
    RicpDetailsComponent,
    RicpListingComponent,
    RIPLComponent,
    RIPLDetailComponent,
    BankReconDetailsComponent,
    BankReconPgListingComponent,
    BankReconBkListingComponent,
    FmsledgerAddComponent,
    UserroleComponent,
    RoleAndPermissionsConfigurationsAddRolesComponent,
    RoleAndPermissionsConfigurationsDetailsComponent,
    RoleAndPermissionsConfigurationsUpdateRoleStatusComponent,
    RoleAndPermissionsConfigurationsDiscardChangesComponent,
    PaymentCollectionComponent,
    PgReconListingComponent,
    PgReconDetailComponent,
    MttListingComponent,
    AccessDeniedComponent,
    MttDetailsListingComponent,
    MttDetailsItemListingComponent,
    MttDetailsPgListingComponent,
    PgDetailListingComponent,
    RmsDetailListingComponent,
    BankReconComponent,
    BankReconNoBankListingComponent,
    BankReconPgFileTxnListingComponent,
    DailyCollectionListingComponent,
  
    RIPLAgingReportComponent,
    UtListingMonthsComponent,
    UtListingDaysComponent,
    UtListingTransactionsComponent,

    MatchedTransactionListingComponent,
    PgSettlementDisbursementListingComponent,
    DeferredIncomeAgingComponent,
    UnmatchedAgingComponent,
    RICPAgingComponent,
    FMSAccountListingComponent,
    FMSAccountUpdateComponent,
    FPASchedulerListingComponent,
    DashboardComponent,
    BranchCodeListingComponent,
    BranchCodeAddComponent,
    BranchCodeUpdateComponent,
    BranchCodeDeleteComponent,
    BillingClassAddComponent,
    BillingClassUpdateComponent,
    BillingClassListingComponent,
    BillingClassDeleteComponent,
    BillingTypeAddComponent,
    BillingTypeDeleteComponent,
    BillingTypeListingComponent,
    BillingTypeUpdateComponent,
    RefundStatusDetailedComponent,
    RefundSsmReportComponent,
    RefundAgingComponent,
    BillingReportComponent,
    CatalogueProductServiceReportComponent,
    SummaryBillingReportComponent,
    DetailedBillingReportComponent,
    DetailedBillingBtReportComponent,
    RefundAccountCodeListingComponent,
    RefundAccountCodeAddComponent,
    RefundAccountCodeDeleteComponent,
    RefundAccountCodeUpdateComponent,
    BranchCodeCounterListingComponent,
    BranchCodeCounterAddComponent,
    BranchCodeCounterUpdateComponent,
    BranchCodeCounterDeleteComponent,
    CounterCollectionComponent,
    DailyBalancingComponent,
    MasterBalancingComponent,
    OtcCollectionComponent,
    OtcCollectionPlusComponent,
    OtcReceiptCancellationComponent,
    OtcReturnedChequeComponent,
    BankInSlipComponent,
    // DynamicrowssampleComponent,
    OtcCollectionReceiptingComponent,
    OtcPaymentScreenComponent,
    OtcReturnedChequeComponent,
    OtcReceiptScreenComponent,
    NonBillingRegistrationComponent,
    OTCCheckInComponent,
    OTCCheckOutComponent,
    ReprintreceiptComponent,
    ReprintreceiptdetailsComponent,
    ReprintreceiptjustificationComponent,
    CounterBalancingEditComponent,
    CounterBalancingListingComponent,

    
    BillRegistrationComponent,
    BillListingComponent,
    BillApprovalComponent,
    ReceiptNoValidationComponent,
    OtcReceiptDetailsComponent,
    ReceiptCancellationApprovalAndJustificationComponent,
    UpdateTaskStatusComponent,
    BibssCustomerIdValidationComponent,
    BibssListingComponent,
    BibssDetailsComponent,
    BibssListComponent,
    BillingCancellationSearchComponent,
    RefundPTTListingComponent,
    RefundInitialFAComponent,
    CreditControlCaseViewerComponent,
    CreditControlCaseComponent,
    RefundRequestSelectSMEComponent,
    RefundApprovalFaComponent,
    BillingCancellationComponent,
    BillingCancellationListingComponent,
    BillingAdjustmentSearchComponent,
    BillingAdjustmentComponent,
    OtcReturnedChequeReportComponent,
    OtcEmvReconciliationComponent,
    OtcEmvReconciliationDetailsComponent,
    //BillingAdjustmentRecurringComponent,
    RefundApprovalUserroleComponent,
    BillingDetailsComponent,
    BillAdjustmentApprovalComponent,
    BillCancellationApprovalComponent,
    //BillingAdjustmentRecurringComponent,
    NonBillingListingComponent,
    NonBillingDetailsComponent,
    RiltListingComponent,
    RefundPTTListingComponent,
    CreditControlCaseViewerComponent,
    CreditControlCaseComponent,
    RefundApproveBlankFormComponent,
    ReceiptNoValidationComponent,
    OtcReceiptDetailsComponent,
    ReceiptCancellationApprovalAndJustificationComponent,
    UpdateTaskStatusComponent,
    BibssCustomerIdValidationComponent,
    MyTaskTabNavigationComponent,
    MyTaskPublicTaskComponent,
    NonRmsReceiptingListingComponent,
    NonRmsReceiptingDetailsComponent,
    CreditControlSmeTaskListComponent,
    AgBankStmtListingComponent,
    AgBankTxnListingComponent,
    AgPgFileTxnListingComponent,
    AgDeleteFileDiaglogComponent,

    ServiceProviderPaymentListingComponent,
    CourtOrderListingComponent,
    CourtOrderDetailsComponent,
    CreditControlCaseListingComponent,
    ServiceProviderMaintenanceAddComponent,
    ServiceProviderMaintenanceListingComponent,
    ServiceProviderMaintenanceDeleteComponent,
    ServiceProviderMaintenanceUpdateComponent,
    AccrualReportsListingComponent,
    CatalogueReportsListingComponent,
    CollectionReportsListingComponent,
    OtcReportsListingComponent,
    ReconciliationReportsListingComponent,
    RefundReportsListingComponent,
    PaymentCollectionFeeDtIdComponent,
    PaymentCollectionPymtMdComponent,
    PaymentCollectionSSComponent,
    DailyBalancingListingComponent,
    DailyBalancingWarningComponent,
    MasterBalancingListingComponent,
    DailyBalancingDetailComponent,
    MasterBalancingDetailComponent,
    RefundListingComponent,
    RefundListingInfoComponent,
    RefundListingInfoRfComponent,
    RefundApproveBlankFormComponent,
    MyTaskTabNavigationComponent,
    MyTaskPublicTaskComponent,
    MasterBankinslipComponent,
    CancelTaskComponent,
    OtcEmvQueryComponent,
    LoadingOverlayComponent,
    RefundChargebackComponent,
    RefundChargebackInfoComponent,
    MftItemTaskListComponent,
    MftCancelTaskComponent,
    ConfirmSubmitComponent,
    FilterPipe,
    TimeoutWarningComponent,
    RefundCreatedTaskDetailsComponent,
    RefundCreatedTaskRfDetailsComponent,
    MttJustiListingComponent
    
  ],
  imports: [
    BrowserModule,
    // RouterModule.forRoot(routes),
    HttpClientModule,
    NgxBootstrapIconsModule.pick(allIcons),
    TranslateModule.forRoot({
      loader: {
        provide: TranslateLoader,
        useFactory: HttpLoaderFactory,
        deps: [HttpClient],
      },
    }),

    TableModule,
    ButtonModule,
    ConfirmPopupModule,
    ConfirmDialogModule,
    BrowserAnimationsModule,
    FormsModule,
    MatIconModule,
    RippleModule,
    DynamicDialogModule,
    MatDialogModule,
    PaginatorModule,
    ReactiveFormsModule,
    MatDatepickerModule,
    MatInputModule,
    MatFormFieldModule,
    MatNativeDateModule,
    NgbModule,
    CommonModule,
    NgxDaterangepickerMd.forRoot({separator: ' to '}),
    AppRoutingModule,
    MatSelectModule,
    BsDatepickerModule.forRoot(),
    NgMultiSelectDropDownModule.forRoot(),
    MatCheckboxModule,
    NgSelectModule,
    NgChartsModule
  ],

  exports: [RouterModule],
  providers: [
    {
      provide: APP_INITIALIZER,
      useFactory: (authService: AuthService) => () =>
        authService.initializeApp(),
      deps: [AuthService],
      multi: true,
    },
    { provide: HTTP_INTERCEPTORS, useClass: NonceInterceptor, multi: true },
    // { provide: HTTP_INTERCEPTORS, useClass: RetryInterceptor, multi: true},
    { provide: HTTP_INTERCEPTORS, useClass: CustomInterceptor, multi: true },
    { provide: MAT_DATE_LOCALE, useValue: 'en-GB' },
    DatePipe,
    AuthGuard,
    AuthService,
    TriggerNotificationUpdateService
  ],
  bootstrap: [AppComponent],
})
export class AppModule {}
