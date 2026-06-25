import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { NavigationEnd, Router, RouterModule, Routes } from '@angular/router';
import { BankReconComponent } from 'src/app/BankRecon/bank-recon-listing/bank-recon-listing.component';
import { OtcCollectionReceiptingComponent } from 'src/app/OTC/otc-collection-receipting/otc-collection-receipting.component';
import { OtcReturnedChequeComponent } from 'src/app/OTC/otc-returned-cheque/otc-returned-cheque.component';
import { AccessDeniedComponent } from 'src/app/access-denied/access-denied.component';
import { DashboardComponent } from 'src/app/dashboard/dashboard.component';
// import { DynamicrowssampleComponent } from 'src/app/dynamicrowssample/dynamicrowssample.component';
import { FMSAccountListingComponent } from 'src/app/fmsaccount/fms-account-listing/fms-account-listing.component';
import { FmsledgerListingComponent } from 'src/app/fmsledger/fmsledger-listing/fmsledger-listing.component';
import { FPASchedulerListingComponent } from 'src/app/fpascheduler/fpa-scheduler-listing/fpa-scheduler-listing.component';
import { MttDetailsListingComponent } from 'src/app/mtt/mtt-details-listing/mtt-details-listing.component';
import { MttJustiListingComponent } from 'src/app/mtt/mtt-justi-listing/mtt-justi-listing.component';
import { MttListingComponent } from 'src/app/mtt/mtt-listing/mtt-listing.component';
import { DailyCollectionListingComponent } from 'src/app/report/collection-reports/daily-collection-listing/daily-collection-listing.component';
import { DeferredIncomeAgingComponent } from 'src/app/report/accrual-reports/deferred-income-aging/deferred-income-aging.component';
import { MatchedTransactionListingComponent } from 'src/app/report/reconciliation-reports/matched-transaction-listing/matched-transaction-listing.component';
import { PgSettlementDisbursementListingComponent } from 'src/app/report/reconciliation-reports/pg-settlement-disbursement-listing/pg-settlement-disbursement-listing.component';
import { RICPAgingComponent } from 'src/app/report/accrual-reports/ricp-aging/ricp-aging.component';
import { RIPLAgingReportComponent } from 'src/app/report/accrual-reports/ripl-aging-report/ripl-aging-report.component';
import { UnmatchedAgingComponent } from 'src/app/report/reconciliation-reports/unmatched-aging/unmatched-aging.component';
import { UtListingDaysComponent } from 'src/app/report/reconciliation-reports/unmatched-transaction-listing/ut-listing-days/ut-listing-days.component';
import { UtListingMonthsComponent } from 'src/app/report/reconciliation-reports/unmatched-transaction-listing/ut-listing-months/ut-listing-months.component';
import { UtListingTransactionsComponent } from 'src/app/report/reconciliation-reports/unmatched-transaction-listing/ut-listing-transactions/ut-listing-transactions.component';
import { RoleAndPermissionsConfigurationsDetailsComponent } from 'src/app/roles-and-permissions-configuration/role-and-permissions-configurations-details/role-and-permissions-configurations-details.component';
import { UserroleComponent } from 'src/app/userrole/userrole.component';
import { BankReconDetailsComponent } from '../../BankRecon/bank-recon-details/bank-recon-details.component';
import { DeferredIncomeDetailsComponent } from '../../DI/deferred-income-details/deferred-income-details.component';
import { DeferredIncomeListingComponent } from '../../DI/deferred-income-listing/deferred-income-listing.component';
import { RicpDetailsComponent } from '../../RICP/ricp-details/ricp-details.component';
import { RicpListingComponent } from '../../RICP/ricp-listing/ricp-listing.component';
import { RIPLDetailComponent } from '../../RIPL/ri-pl-detail/ri-pl-detail.component';
import { RIPLComponent } from '../../RIPL/ri-pl-listing/ri-pl-listing.component';
import { FeeGroupListingComponent } from '../../feegroup/fee-group-listing/fee-group-listing.component';
import { FmsAddComponent } from '../../fms/fms-add/fms-add.component';
import { FmsListingComponent } from '../../fms/fms-listing/fms-listing.component';
import { HomeComponent } from '../../home/home.component';
import { MasterFeeTableComponent } from '../../mastercomponent/master-fee-table/master-fee-table.component';
import { MasterTaskListComponent } from '../../mastercomponent/master-task-list/master-task-list.component';
import { LoginComponent } from '../../masterpage/login/login.component';
import { LogoutComponent } from '../../masterpage/logout/logout.component';
import { CreatedTaskDetailsComponent } from '../../mft/created-task-details/created-task-details.component';
import { MftFaApprAddComponent } from '../../mft/mft-fa-appr-add/mft-fa-appr-add.component';
import { MftFaApprEditComponent } from '../../mft/mft-fa-appr-edit/mft-fa-appr-edit.component';
import { MftFaFaEditComponent } from '../../mft/mft-fa-fa-edit/mft-fa-fa-edit.component';
import { MftFaFaRqtAddComponent } from '../../mft/mft-fa-fa-rqt-add/mft-fa-fa-rqt-add.component';
import { MftFaFhodApprAddComponent } from '../../mft/mft-fa-fhod-appr-add/mft-fa-fhod-appr-add.component';
import { MftFhodApprAddComponent } from '../../mft/mft-fhod-appr-add/mft-fhod-appr-add.component';
import { MftItemDetailsComponent } from '../../mft/mft-item-details/mft-item-details.component';
import { MftReqFormAddComponent } from '../../mft/mft-req-form-add/mft-req-form-add.component';
import { MftReqFormEditComponent } from '../../mft/mft-req-form-edit/mft-req-form-edit.component';
import { MftReqhodApprAddComponent } from '../../mft/mft-reqhod-appr-add/mft-reqhod-appr-add.component';
import { MftReqhodApprEditComponent } from '../../mft/mft-reqhod-appr-edit/mft-reqhod-appr-edit.component';
import { TaskDetailsComponent } from '../../mft/task-details/task-details.component';
import { MultiLanguageComponent } from '../../multi-language/multi-language.component';
import { MyTaskAssignedTasksComponent } from '../../mytask/my-task-assigned-tasks/my-task-assigned-tasks.component';
import { MyTaskCreatedTaskComponent } from '../../mytask/my-task-created-task/my-task-created-task.component';
import { PgReconDetailComponent } from '../../pgrecon/pg-recon-detail/pg-recon-detail.component';
import { PgReconListingComponent } from '../../pgrecon/pg-recon-listing/pg-recon-listing.component';
import { PaymentCollectionComponent } from '../../report/collection-reports/payment-collection/payment-collection.component';
import { TaxCodeListingComponent } from '../../taxcode/tax-code-listing/tax-code-listing.component';
import { ReprintreceiptComponent } from 'src/app/OTC/reprintreceipt/reprintreceipt.component';
import { ReprintreceiptdetailsComponent } from 'src/app/OTC/reprintreceiptdetails/reprintreceiptdetails.component';
import { ReprintreceiptjustificationComponent } from 'src/app/OTC/reprintreceiptjustification/reprintreceiptjustification.component';
import { BibssListingComponent } from 'src/app/billingissuance/billing-issuance-by-ss/bibss-listing/bibss-listing.component';
import { BibssDetailsComponent } from 'src/app/billingissuance/billing-issuance-by-ss/bibss-details/bibss-details.component';
import { BibssListComponent } from 'src/app/billingissuance/billing-issuance-by-ss/bibss-list/bibss-list.component';

import { ReceiptNoValidationComponent } from 'src/app/OTC/otc-receipt-cancellation/receipt-no-validation/receipt-no-validation.component';
import { OtcReceiptDetailsComponent } from 'src/app/OTC/otc-receipt-cancellation/otc-receipt-details/otc-receipt-details.component';
import { AuthGuard } from '../guard/auth.guard';
import { OtcPaymentScreenComponent } from 'src/app/OTC/otc-payment-screen/otc-payment-screen.component';
import { OtcReceiptScreenComponent } from 'src/app/OTC/otc-receipt-screen/otc-receipt-screen.component';
import { NonBillingRegistrationComponent } from 'src/app/non-billing-registration/non-billing-registration.component';
import { OTCCheckInComponent } from 'src/app/OTC/otc-checkin/otc-checkin.component';
import { OTCCheckOutComponent } from 'src/app/OTC/otc-checkout/otc-checkout.component';
import { BillingCancellationSearchComponent } from 'src/app/billing/billing-cancellation-search/billing-cancellation-search.component';
import { RefundPTTListingComponent } from 'src/app/refund/refund-ptt-listing/refund-ptt-listing.component';
import { RefundInitialFAComponent } from 'src/app/refund/refund-initial-fa/refund-initial-fa.component';
import { RefundRequestSelectSMEComponent } from 'src/app/refund/refund-initial-fa/refund-request-select-sme/refund-request-select-sme.component';
import { RefundApprovalFaComponent } from 'src/app/refund/refund-task-list/refund-approval-fa/refund-approval-fa.component';
import { RefundApprovalUserroleComponent } from 'src/app/refund/refund-task-list/refund-approval-userrole/refund-approval-userrole.component';
import { BillingCancellationComponent } from 'src/app/billing/billing-cancellation/billing-cancellation.component';
import { BillingCancellationListingComponent } from 'src/app/billing/billing-cancellation-listing/billing-cancellation-listing.component';
import { BillingAdjustmentSearchComponent } from 'src/app/billing/billing-adjustment-search/billing-adjustment-search.component';
import { BillingAdjustmentComponent } from 'src/app/billing/billing-adjustment/billing-adjustment.component';

//import { BillingCancellationRecurringComponent } from 'src/app/billing-cancellation-recurring/billing-cancellation-recurring.component';
import { BillRegistrationComponent } from 'src/app/billing/billing-registration/billing-registration.component';
import { BillListingComponent } from 'src/app/billing/billing-listing/billing-listing.component';
import { BillApprovalComponent } from 'src/app/billing/billing-approval/billing-approval.component';
import { BillAdjustmentApprovalComponent } from 'src/app/billing/billing-adjustment-approval/billing-adjustment-approval.component';
import { BillCancellationApprovalComponent } from 'src/app/billing/billing-cancellation-approval/billing-cancellation-approval.component';
//import { BillingCancellationRecurringComponent } from 'src/app/billing-cancellation-recurring/billing-cancellation-recurring.component';
import { OtcEmvReconciliationComponent } from 'src/app/OTC/otc-emv-reconciliation/otc-emv-reconciliation.component';
import { OtcEmvReconciliationDetailsComponent } from 'src/app/OTC/otc-emv-reconciliation-details/otc-emv-reconciliation-details.component';
import { RefundAccountCodeListingComponent } from '../../refundaccountcode/refund-account-code-listing/refund-account-code-listing.component';
import { BranchCodeCounterListingComponent } from '../../branchcodecounter/branch-code-counter-listing/branch-code-counter-listing.component';
import { CounterCollectionComponent } from '../../report/otc-report/counter-collection/counter-collection.component';
import { DailyBalancingComponent } from 'src/app/report/otc-report/daily-balancing/daily-balancing.component';
import { MasterBalancingComponent } from 'src/app/report/otc-report/master-balancing/master-balancing.component';
import { OtcCollectionComponent } from 'src/app/report/otc-report/otc-collection/otc-collection.component';
import { OtcCollectionPlusComponent } from 'src/app/report/otc-report/otc-collection-plus/otc-collection-plus.component';
import { OtcReceiptCancellationComponent } from 'src/app/report/otc-report/otc-receipt-cancellation/otc-receipt-cancellation.component';
import { BankInSlipComponent } from 'src/app/report/otc-report/bank-in-slip/bank-in-slip.component';
import { OtcReturnedChequeReportComponent } from 'src/app/report/otc-report/otc-returned-cheque-report/otc-returned-cheque-report.component';
//import { BillingAdjustmentRecurringComponent } from 'src/app/billing-adjustment-recurring/billing-adjustment-recurring.component';

import { BillingDetailsComponent } from 'src/app/billing/billing-details/billing-details.component';
//import { BillingAdjustmentRecurringComponent } from 'src/app/billing-adjustment-recurring/billing-adjustment-recurring.component';
import { NonBillingListingComponent } from 'src/app/non-billing-listing/non-billing-listing.component';
import { NonBillingDetailsComponent } from 'src/app/non-billing-details/non-billing-details.component';
import { RiltListingComponent } from 'src/app/RILT/rilt-listing/rilt-listing.component';
import { BranchCodeListingComponent } from 'src/app/branchcode/branch-code-listing/branch-code-listing.component';
import { BillingClassListingComponent } from 'src/app/billingclass/billing-class-listing/billing-class-listing.component';
import { BillingTypeListingComponent } from 'src/app/billingtype/billing-type-listing/billing-type-listing.component';
import { BillingReportComponent } from 'src/app/report/catalogue-reports/billing-report/billing-report.component';
import { CatalogueProductServiceReportComponent } from 'src/app/report/catalogue-reports/catalogue-product-service-report/catalogue-product-service-report.component';
import { DetailedBillingBtReportComponent } from 'src/app/report/catalogue-reports/detailed-billing-bt-report/detailed-billing-bt-report.component';
import { DetailedBillingReportComponent } from 'src/app/report/catalogue-reports/detailed-billing-report/detailed-billing-report.component';
import { RefundAgingComponent } from 'src/app/report/refund-reports/refund-aging/refund-aging.component';
import { RefundSsmReportComponent } from 'src/app/report/refund-reports/refund-ssm-report/refund-ssm-report.component';
import { RefundStatusDetailedComponent } from 'src/app/report/refund-reports/refund-status/refund-status.component';
import { SummaryBillingReportComponent } from 'src/app/report/catalogue-reports/summary-billing-report/summary-billing-report.component';
import { ServiceProviderPaymentListingComponent } from 'src/app/serviceprovider/service-provider-payment-listing/service-provider-payment-listing.component';
import { CourtOrderListingComponent } from 'src/app/courtorder/court-order-listing/court-order-listing.component';
import { CourtOrderDetailsComponent } from 'src/app/courtorder/court-order-details/court-order-details.component';
import { ServiceProviderMaintenanceListingComponent } from 'src/app/serviceprovider/service-provider-maintenance/service-provider-maintenance-listing/service-provider-maintenance-listing.component';

import { RefundApproveBlankFormComponent } from 'src/app/refund/refund-approve-blank-form/refund-approve-blank-form.component';
import { RefundListingComponent } from 'src/app/refund/refund-listing/refund-listing.component';
import { RefundListingInfoComponent } from 'src/app/refund/refund-listing-info/refund-listing-info.component';
import { RefundListingInfoRfComponent } from 'src/app/refund/refund-listing-info-rf/refund-listing-info-rf.component';
import { CreditControlCaseViewerComponent } from 'src/app/creditcontrol/credit-control-case-view/credit-control-case-view.component';
import { CreditControlCaseComponent } from 'src/app/creditcontrol/credit-control-case/credit-control-case.component';
import { ReceiptCancellationApprovalAndJustificationComponent } from 'src/app/OTC/otc-receipt-cancellation/receipt-cancellation-approval-and-justification/receipt-cancellation-approval-and-justification.component';
import { UpdateTaskStatusComponent } from 'src/app/OTC/otc-receipt-cancellation/update-task-status/update-task-status.component';
import { BibssCustomerIdValidationComponent } from 'src/app/billingissuance/billing-issuance-by-ss/bibss-customer-id-validation/bibss-customer-id-validation.component';
import { MyTaskPublicTaskComponent } from 'src/app/mytask/my-task-public-task/my-task-public-task.component';
import { NonRmsReceiptingListingComponent } from 'src/app/non-rms-receipting-listing/non-rms-receipting-listing.component';
import { NonRmsReceiptingDetailsComponent } from 'src/app/non-rms-receipting-details/non-rms-receipting-details.component';
import { CreditControlSmeTaskListComponent } from 'src/app/credit-control-sme-task-list/credit-control-sme-task-list.component';

import { AccrualReportsListingComponent } from 'src/app/report/accrual-reports/accrual-reports-listing/accrual-reports-listing.component';
import { ReconciliationReportsListingComponent } from 'src/app/report/reconciliation-reports/reconciliation-reports-listing/reconciliation-reports-listing.component';
import { CollectionReportsListingComponent } from 'src/app/report/collection-reports/collection-reports-listing/collection-reports-listing.component';
import { OtcReportsListingComponent } from 'src/app/report/otc-report/otc-reports-listing/otc-reports-listing.component';
import { RefundReportsListingComponent } from 'src/app/report/refund-reports/refund-reports-listing/refund-reports-listing.component';
import { CatalogueReportsListingComponent } from 'src/app/report/catalogue-reports/catalogue-reports-listing/catalogue-reports-listing.component';
import { PaymentCollectionFeeDtIdComponent } from 'src/app/report/collection-reports/payment-collection-fee-dt-id/payment-collection-fee-dt-id.component';
import { PaymentCollectionSSComponent } from 'src/app/report/collection-reports/payment-collection-s-s/payment-collection-s-s.component';
import { PaymentCollectionPymtMdComponent } from 'src/app/report/collection-reports/payment-collection-pymt-md/payment-collection-pymt-md.component';

import { CounterBalancingListingComponent } from 'src/app/OTC/counter-balancing-listing/counter-balancing-listing.component';
import { CounterBalancingEditComponent } from 'src/app/OTC/counter-balancing-edit/counter-balancing-edit.component';
import { DailyBalancingListingComponent } from 'src/app/OTC/daily-balancing-listing/daily-balancing-listing.component';
import { MasterBalancingListingComponent } from 'src/app/OTC/master-balancing-listing/master-balancing-listing.component';
import { DailyBalancingDetailComponent } from 'src/app/OTC/daily-balancing-detail/daily-balancing-detail.component';
import { MasterBalancingDetailComponent } from 'src/app/OTC/master-balancing-detail/master-balancing-detail.component';
import { MasterBankinslipComponent } from 'src/app/OTC/master-bankinslip/master-bankinslip.component';
import { AuthService } from '../services/auth.service';
import { filter } from 'rxjs';

//import { MftItemTaskListComponent } from 'src/app/mft/mft-item-task-list/mft-item-task-list.component';
import { RefundChargebackComponent } from 'src/app/refund/refund-chargeback/refund-chargeback.component';
import { RefundChargebackInfoComponent } from 'src/app/refund/refund-chargeback-info/refund-chargeback-info.component';
import { MftItemTaskListComponent } from 'src/app/mft/mft-item-task-list/mft-item-task-list.component';
import { RefundCreatedTaskDetailsComponent } from 'src/app/refund/refund-created-task-details/refund-created-task-details.component';
import { RefundCreatedTaskRfDetailsComponent } from 'src/app/refund/refund-created-task-rf-details/refund-created-task-rf-details.component';


const routes: Routes = [
  { path: 'multi-language', component: MultiLanguageComponent, canActivate: [AuthGuard] },
  { path: 'home', component: HomeComponent, data: { noAuthCheck: true } }, // always no auth check
  { path: 'dashboard', component: DashboardComponent, data: { noAuthCheck: true } }, // always no auth check
  { path: 'logout', component: LogoutComponent, data: { noAuthCheck: true } }, // logout route
  // { path: 'dynamicrowssample', component: DynamicrowssampleComponent, data: { noAuthCheck: true } }, // always no auth check
  {
    path: 'tax-code-listing', component: TaxCodeListingComponent, canActivate: [AuthGuard], data: {
      title: 'Tax Code', breadcrumb: [{ label: 'menu.home', url: '/home' },
      {
        label: 'labels.taxcodelisting',
        url: '/tax-code-listing'
      }
      ]
    }
  },
  {
    path: 'reprintreceipt', component: ReprintreceiptComponent, canActivate: [AuthGuard], data: {
      title: 'Reprint Receipt', breadcrumb: [{ label: 'menu.home', url: '/home' },
      {
        label: 'labels.reprintreceipt',
        url: '/reprintreceipt'
      }
      ],
    }
  },
  {
    path: 'reprintreceiptdetails', component: ReprintreceiptdetailsComponent, data: {
      title: 'Reprint Receipt', breadcrumb: [{ label: 'menu.home', url: '/home' },
      {
        label: 'labels.reprintreceipt',
        url: '/reprintreceipt'
      },
      {
        label: 'labels.reprintreceiptdetails',
        url: '/reprintreceiptdetails'
      },

      ],
      canActivate: [AuthGuard]

    }
  },
  {
    path: 'reprintreceiptjustification', component: ReprintreceiptjustificationComponent, data: {
      title: 'Reprint Receipt', breadcrumb: [{ label: 'menu.home', url: '/home' },
      {
        label: 'labels.reprintreceipt',
        url: '/reprintreceipt'
      },
      {
        label: 'labels.reprintreceiptdetails',
        url: '/reprintreceiptdetails'
      },
      {
        label: 'labels.reprintreceiptjustification',
        url: '/reprintreceiptjustification'
      }
      ],
      canActivate: [AuthGuard]

    }
  },
  {
    path: 'refund-account-code-listing', component: RefundAccountCodeListingComponent, canActivate: [AuthGuard], data: {
      title: 'Refund Account Code', breadcrumb: [{ label: 'menu.home', url: '/home' },
      {
        label: 'labels.refundaccountcodelisting',
        url: '/refund-account-code-listing'
      }
      ]
    },
  },

  {
    path: 'branch-code-counter-listing', component: BranchCodeCounterListingComponent, canActivate: [AuthGuard], data: {
      title: 'Branch Code Counter Mapping', breadcrumb: [{ label: 'menu.home', url: '/home' },
      {
        label: 'labels.branchcodecountermapping',
        url: '/branch-code-counter-listing'
      }
      ],
    }
  },

  {
    path: 'master-fee-table', component: MasterFeeTableComponent, canActivate: [AuthGuard], data: {
      title: 'Master Fee Table', breadcrumb: [{ label: 'menu.home', url: '/home' },
      {
        label: 'labels.masterfeetable',
        url: '/master-fee-table'
      }
      ]
    }
  },

  {
    path: 'master-task-list', component: MasterTaskListComponent, canActivate: [AuthGuard], data: {
      title: 'Master Task List', breadcrumb: [{ label: 'menu.home', url: '/home' },
      {
        label: 'labels.mastertasklist',
        url: '/master-task-list'
      },
      ]
    }
  },

  {
    path: 'mft-item-detail', component: MftItemDetailsComponent, canActivate: [AuthGuard], data: {
      title: 'MFT Item Details', breadcrumb: [{ label: 'menu.home', url: '/home' },
      {
        label: 'labels.masterfeetable',
        url: '/master-fee-table'
      },
      {
        label: 'labels.mftitemdetails',
        url: '/mft-item-detail'
      }
      ]
    }
  },
  // { path: 'master-task-list', component: MasterTaskListComponent, canActivate: [AuthGuard] },
  {
    path: 'fee-group-listing', component: FeeGroupListingComponent, canActivate: [AuthGuard], data: {
      title: 'Fee Group', breadcrumb: [{ label: 'menu.home', url: '/home' },
      {
        label: 'labels.feegrouplisting',
        url: '/fee-group-listing'
      }
      ]
    }
  },
  { path: 'logout', component: LogoutComponent, data: { noAuthCheck: true } },
  {
    path: 'fms-listing', component: FmsListingComponent, canActivate: [AuthGuard], data: {
      title: 'FMS Code Listing', breadcrumb: [{ label: 'menu.home', url: '/home' },
      {
        label: 'labels.fmscodelisting',
        url: '/fms-listing'
      }
      ]
    }
  },
  { path: 'fms-add', component: FmsAddComponent, canActivate: [AuthGuard] },
  {
    path: 'mft-req-form-add', component: MftReqFormAddComponent, canActivate: [AuthGuard], data: {
      title: 'Request Add MFT - Requester Form', breadcrumb: [{ label: 'menu.home', url: '/home' },
      {
        label: 'labels.requestnewmft-requesterform',
        url: '/mft-req-form-add'
      }
      ]
    }
  },
  {
    path: 'my-task-assigned-tasks',
    children: [
    	{path: '', component: MyTaskAssignedTasksComponent, canActivate: [AuthGuard],
	    	data: {title: 'Assigned Tasks', breadcrumb: [{ label: 'menu.home', url: '/home' },
	      		{label: 'labels.assignedtasks', url: '/my-task-assigned-tasks'}]}
    	},
    	{path: 'billing-approval', component: BillApprovalComponent, canActivate: [AuthGuard],
	    	data: { title: 'Billing Approval', breadcrumb: [{ label: 'menu.home', url: '/home' },
	    			{label: 'labels.assignedtasks', url: '/my-task-assigned-tasks/billing'},
	      		{label: 'menu.bilaproval', url: '/my-task-assigned-tasks/billing'}]}
	  	},
	  	{path: 'billing-adjustment-approval', component: BillAdjustmentApprovalComponent, canActivate: [AuthGuard], 
	      	data: { title: 'Billing Adjustment Approval', breadcrumb: [{ label: 'menu.home', url: '/home' },
						{label: 'labels.assignedtasks', url: '/my-task-assigned-tasks/billing'},
	          {label: 'menu.biladjaproval', url: '/my-task-assigned-tasks/billing'}]}
	 		},
	  	{path: 'billing-cancellation-approval', component: BillCancellationApprovalComponent, canActivate: [AuthGuard], 
	      	data: {title: 'Billing Cancellation Approval', breadcrumb: [{ label: 'menu.home', url: '/home' },
	    			{label: 'labels.assignedtasks', url: '/my-task-assigned-tasks/billing'},
	          { label: 'menu.bilcanaproval', url: '/my-task-assigned-tasks/billing' }]}
  		},
  	  {path: 'credit-control-case', component: CreditControlCaseComponent, canActivate: [AuthGuard], 
  	  		data: {title: 'Credit Control Case', breadcrumb: [{ label: 'menu.home', url: '/home' },
	    			{label: 'labels.assignedtasks', url: '/my-task-assigned-tasks/ccc'},
      			{label: 'labels.cccase', url: '/my-task-assigned-tasks/ccc'}]}
      },
      {path: 'billing-details', component: BillingDetailsComponent, canActivate: [AuthGuard],
      		data: {title: 'Billing Details', breadcrumb: [{ label: 'menu.home', url: '/home' },
	    			{label: 'labels.assignedtasks', url: '/my-task-assigned-tasks/billing'},
      			{ label: 'menu.bildet', url: '/my-task-assigned-tasks/billing' }]}
  	  }
      //add more paths here
    ]
  },
	{path: 'my-task-assigned-tasks/:pool', component: MyTaskAssignedTasksComponent, canActivate: [AuthGuard],
		data: {title: 'Assigned Tasks', breadcrumb: [{ label: 'menu.home', url: '/home' },
	  		{label: 'labels.assignedtasks', url: '/my-task-assigned-tasks'}]}
	},
  {
    path: 'my-task-created-task',
    children: [
    	{path: '', component: MyTaskCreatedTaskComponent, canActivate: [AuthGuard],
	    	data: {title: 'Created Tasks', breadcrumb: [{ label: 'menu.home', url: '/home' },
	      		{label: 'labels.createdtasks', url: '/my-task-created-task'}]}
    	},
    	{path: 'billing-approval', component: BillApprovalComponent, canActivate: [AuthGuard],
	    	data: { title: 'Billing Approval', breadcrumb: [{ label: 'menu.home', url: '/home' },
	    			{label: 'labels.createdtasks', url: '/my-task-created-task/billing'},
	      			{label: 'menu.bilaproval', url: '/my-task-created-task/billing'}]}
	  	},
	  	{path: 'billing-adjustment-approval', component: BillAdjustmentApprovalComponent, canActivate: [AuthGuard], 
	      	data: { title: 'Billing Adjustment Approval', breadcrumb: [{ label: 'menu.home', url: '/home' },
					{label: 'labels.createdtasks', url: '/my-task-created-task/billing'},
          			{label: 'menu.biladjaproval', url: '/my-task-created-task/billing'}]}
	 		},
	  	{path: 'billing-cancellation-approval', component: BillCancellationApprovalComponent, canActivate: [AuthGuard], 
	      	data: {title: 'Billing Cancellation Approval', breadcrumb: [{ label: 'menu.home', url: '/home' },
    				{label: 'labels.createdtasks', url: '/my-task-created-task/billing'},
          			{ label: 'menu.bilcanaproval', url: '/my-task-created-task/billing' }]}
  		},
  	  {path: 'credit-control-case-view', component: CreditControlCaseViewerComponent, canActivate: [AuthGuard], 
  	  		data: {title: 'Credit Control Case Details', breadcrumb: [{ label: 'menu.home', url: '/home' },
	    			{label: 'labels.createdtasks', url: '/my-task-created-task/ccc'},
	      			{label: 'labels.cccdetails', url: '/my-task-created-task/ccc'}]}
      },
      {path: 'mft-item-task-list', component: MftItemTaskListComponent, canActivate: [AuthGuard], 
      		data: {title: 'MFT Item Details', breadcrumb: [{ label: 'menu.home', url: '/home' },
	    			{label: 'labels.createdtasks', url: '/my-task-created-task/mft'},
		      		{label: 'labels.mftitemdetails',url: '/mft-item-detail'}]}
	  	},
  		{path: 'created-task-details', component: CreatedTaskDetailsComponent, canActivate: [AuthGuard],
  				data: {title: 'Task Details', breadcrumb: [{ label: 'menu.home', url: '/home' },
    				{label: 'labels.createdtasks', url: '/my-task-created-task'}]}
  		},
		  {path: 'otc-rcpt-dets', component: OtcReceiptDetailsComponent, canActivate: [AuthGuard], 
		  		data: {title: 'OTC Receipt Cancellation Details', breadcrumb: [{ label: 'menu.home', url: '/home' },
		  			{label: 'labels.createdtasks', url: '/my-task-created-task'},
		      		{label: 'labels.otcreceiptscreen',url: '/otc-rcpt-dets'}]}
		  },
      {path: 'billing-details', component: BillingDetailsComponent, canActivate: [AuthGuard],
      		data: {title: 'Billing Details', breadcrumb: [{ label: 'menu.home', url: '/home' },
    				{label: 'labels.createdtasks', url: '/my-task-created-task/billing'},
      			{ label: 'menu.bildet', url: '/my-task-created-task/billing' }]}
  	  }
    ]
  },
	{path: 'my-task-created-task/:pool', component: MyTaskCreatedTaskComponent, canActivate: [AuthGuard],
  	data: {title: 'Created Tasks', breadcrumb: [{ label: 'menu.home', url: '/home' },
    		{label: 'labels.createdtasks', url: '/my-task-created-task'}]}
	},
  {
    path: 'task-details', component: TaskDetailsComponent, canActivate: [AuthGuard], data: {
      title: 'Master Task List Details', breadcrumb: [{ label: 'menu.home', url: '/home' },
      {
        label: 'labels.mastertasklist',
        url: '/master-task-list'
      },
      {
        label: 'labels.mftitemdetails',
        url: '/task-details'
      }
      ]
    }
  },
  { path: 'mft-fa-appr-add', component: MftFaApprAddComponent, canActivate: [AuthGuard] },
  { path: 'mft-reqhod-appr-add', component: MftReqhodApprAddComponent, canActivate: [AuthGuard] },
  { path: 'mft-fa-fhod-appr-add', component: MftFaFhodApprAddComponent, canActivate: [AuthGuard] },
  { path: 'mft-fhod-appr-add', component: MftFhodApprAddComponent, canActivate: [AuthGuard] },
  {
    path: 'mft-fa-fa-rqt-add', component: MftFaFaRqtAddComponent, canActivate: [AuthGuard], data: {
      title: 'MFT Add Request', breadcrumb: [{ label: 'menu.home', url: '/home' },
      {
        label: 'labels.masterfeetable',
        url: '/master-fee-table'
      },
      {
        label: 'labels.mftaddrequest',
        url: '/mft-fa-fa-rqt-add'
      }
      ]
    }
  },
  { path: 'mft-fa-fa-rqt-edit', component: MftFaFaEditComponent, canActivate: [AuthGuard] },
  {
    path: 'mft-req-form-edit', component: MftReqFormEditComponent, canActivate: [AuthGuard], data: {
      title: 'Request Edit MFT - Requester Form', breadcrumb: [{ label: 'menu.home', url: '/home' },
      {
        label: 'labels.requesteditmft-requesterform',
        url: '/mft-req-form-edit'
      }
      ]
    }
  },
  { path: 'mft-reqhod-appr-edit', component: MftReqhodApprEditComponent, canActivate: [AuthGuard] },
  { path: 'mft-fa-appr-edit', component: MftFaApprEditComponent, canActivate: [AuthGuard] },
  { path: 'created-task-details', component: CreatedTaskDetailsComponent, canActivate: [AuthGuard] },
  {
    path: 'fms-ledger-listing/:id', component: FmsledgerListingComponent, canActivate: [AuthGuard], data: {
      title: 'FMS Code Details', breadcrumb: [{ label: 'menu.home', url: '/home' },
      {
        label: 'labels.fmscodelisting',
        url: '/fms-listing'
      },
      {
        label: 'labels.fmscodedetails',
        url: '/fms-ledger-listing'
      }
      ]
    }
  },
  {
    path: 'userrole', component: UserroleComponent, canActivate: [AuthGuard], data: {
      title: 'User & User Role(s)', breadcrumb: [{ label: 'menu.home', url: '/home' },
      {
        label: 'labels.useranduserroles',
        url: '/userrole'
      }
      ]
    }
  },
  {
    path: 'deferred-income-listing', component: DeferredIncomeListingComponent, canActivate: [AuthGuard], data: {
      title: 'Deferred Income Transaction Listing', breadcrumb: [{ label: 'menu.home', url: '/home' },
      {
        label: 'labels.deferredincometransactionlisting',
        url: '/deferred-income-listing'
      }
      ]
    }
  },
  {
    path: 'deferred-income-details', component: DeferredIncomeDetailsComponent, canActivate: [AuthGuard], data: {
      title: 'Deferred Income Transaction Listing', breadcrumb: [{ label: 'menu.home', url: '/home' },
      {
        label: 'labels.deferredincometransactionlisting',
        url: '/deferred-income-listing'
      },
      {
        label: 'labels.deferredincometransactiondetails',
        url: '/deferred-income-details'
      }
      ]
    }
  },
  {
    path: 'rilt-listing', component: RiltListingComponent, canActivate: [AuthGuard], data: {
      title: 'Receivable Income Compound Listing', breadcrumb: [{ label: 'menu.home', url: '/home' },
      {
        label: 'labels.receivableincomelitigationlisting',
        url: '/rilt-listing'
      }
      ]
    }
  },
  {
    path: 'ricp-details', component: RicpDetailsComponent, canActivate: [AuthGuard], data: {
      title: 'Receivable Income Compound Details', breadcrumb: [{ label: 'menu.home', url: '/home' },
      {
        label: 'labels.receivableincomecompoundlisting',
        url: '/ricp-listing'
      },
      {
        label: 'labels.receivableincomecompounddetails',
        url: '/ricp-details'
      }
      ]
    }
  },
  {
    path: 'ricp-listing', component: RicpListingComponent, canActivate: [AuthGuard], data: {
      title: 'Receivable Income Compound Listing', breadcrumb: [{ label: 'menu.home', url: '/home' },
      {
        label: 'labels.receivableincomecompoundlisting',
        url: '/ricp-listing'
      }
      ]
    }
  },
  {
    path: 'ri-pl-listing', component: RIPLComponent, canActivate: [AuthGuard], data: {
      title: 'Receivable Income Periodic Lodgment Listing', breadcrumb: [{ label: 'menu.home', url: '/home' },
      {
        label: 'labels.receivableincomeperiodiclodgmentlisting',
        url: '/ri-pl-listing'
      }
      ]
    }
  },
  {
    path: 'ri-pl-detail', component: RIPLDetailComponent, canActivate: [AuthGuard], data: {
      title: 'Receivable Income Periodic Lodgment Details', breadcrumb: [{ label: 'menu.home', url: '/home' },
      {
        label: 'labels.receivableincomeperiodiclodgmentlisting',
        url: '/ri-pl-listing'
      },
      {
        label: 'labels.Receivable Income Periodic Lodgment Details',
        url: '/ri-pl-details'
      }
      ]
    }
  },
  {
    path: 'bank-recon-detail', component: BankReconDetailsComponent, canActivate: [AuthGuard], data: {
      title: 'Payment Gateway Settlement VS Bank Statement', breadcrumb: [{ label: 'menu.home', url: '/home' },
      {
        label: 'menu.bankstatementfileupload',
        url: '/bank-recon-listing'
      },
      {
        label: 'labels.paymentgatewayvsbankstatementreconciliation',
        url: '/bank-recon-detail'
      }
      ]
    }
  },
  {

    path: 'role-and-permissions-configurations-details', component: RoleAndPermissionsConfigurationsDetailsComponent, canActivate: [AuthGuard], data: {
      title: 'Role and Permissions Configuration', breadcrumb: [{ label: 'menu.home', url: '/home' },
      {
        label: 'labels.roleandpermissionsconfigurations',
        url: '/role-and-permissions-configurations-details'
      }
      ]
    }
  },
  //{ path: 'payment-collection', component: PaymentCollectionComponent, canActivate: [AuthGuard]},
  {
    path: 'payment-collection', component: PaymentCollectionComponent, canActivate: [AuthGuard], data: {
      title: 'Payment Collection', breadcrumb: [{ label: 'menu.home', url: '/home' },
      {
        label: 'menu.paymentcollection',
        url: '/payment-collection'
      }
      ]
    }
  },
  {
    path: 'pgrecon-listing', component: PgReconListingComponent, canActivate: [AuthGuard], data: {
      title: 'Payment Gateway Settlement File Upload', breadcrumb: [{ label: 'menu.home', url: '/home' },
      {
        label: 'menu.paymentgatewayfileupload',
        url: '/pgrecon-listing'
      }
      ]
    }
  },
  {
    path: 'pgrecon-detail', component: PgReconDetailComponent, canActivate: [AuthGuard], data: {
      title: 'Payment Gateway Settlement VS Bank Statement', breadcrumb: [{ label: 'menu.home', url: '/home' },
      {
        label: 'menu.paymentgatewayfileupload',
        url: '/pgrecon-listing'
      },
      {
        label: 'labels.paymentgatewaydetails',
        url: '/pgrecon-detail'
      }
      ]
    }
  },
  {
    path: 'mtt-listing', component: MttListingComponent, data: {
      title: 'MTT Listing', breadcrumb: [{ label: 'menu.home', url: '/home' },
      {
        label: 'labels.mttlisting',
        url: '/mtt-listing'
      }
      ], canActivate: [AuthGuard]
    }
  },
  { path: 'access-denied', component: AccessDeniedComponent },
  {
    path: 'mtt-details', component: MttDetailsListingComponent, data: {
      title: 'MTT Listing Details', breadcrumb: [{ label: 'menu.home', url: '/home' },
      {
        label: 'labels.mttlisting',
        url: '/mtt-listing'
      },
      {
        label: 'labels.MTT Listing Details',
        url: '/mtt-details'
      }
      ], canActivate: [AuthGuard]
    }
  },
  {
    path: 'master-transaction-table', component: MttListingComponent, canActivate: [AuthGuard], data: {
      title: 'Master Transaction Table', breadcrumb: [{ label: 'menu.home', url: '/home' },
      {
        label: 'labels.mastertransactiontable',
        url: '/master-transaction-table'
      }
      ]
    }
  },
  { path: 'access-denied', component: AccessDeniedComponent },
  {
    path: 'mtt-details', component: MttDetailsListingComponent, canActivate: [AuthGuard], data: {
      title: 'MTT Details', breadcrumb: [{ label: 'menu.home', url: '/home' },
      {
        label: 'labels.mastertransactiontable',
        url: '/master-transaction-table'
      },
      {
        label: 'labels.mttdetails',
        url: '/mtt-details'
      }
      ]
    }
  },
  {
    path: 'mtt-justi', component: MttJustiListingComponent, canActivate: [AuthGuard], data: {
      title: 'MTT Justification', breadcrumb: [{ label: 'menu.home', url: '/home' },
      {
        label: 'labels.mastertransactiontable',
        url: '/master-transaction-table'
      },
      { 
        label: 'labels.mttdetails',
        url: '/mtt-details'
      },
      {
        label: 'labels.mttjustification',
        url: '/mtt-justi'
      }
      ]
    }
  },
  {
    path: 'bank-recon-listing', component: BankReconComponent, canActivate: [AuthGuard], data: {
      title: 'Bank Statement File Record(s)', breadcrumb: [{ label: 'menu.home', url: '/home' },
      {
        label: 'menu.bankstatementfileupload',
        url: '/bank-recon-listing'
      }
      ]
    }
  },
  {
    path: 'daily-collection-listing', component: DailyCollectionListingComponent, canActivate: [AuthGuard], data: {
      title: 'Daily Collection Listing', breadcrumb: [{ label: 'menu.home', url: '/home' },
      {
        label: 'menu.collectionreport',
        url: '/collection-reports-listing'
      },
      {
        label: 'menu.dailycollectionlisting',
        url: '/daily-collection-listing'
      }
      ]
    }
  },
  {
    path: 'ripl-aging-report', component: RIPLAgingReportComponent, canActivate: [AuthGuard], data: {
      title: 'Receivable Income Periodic Lodgement Aging Report Generation', breadcrumb: [{ label: 'menu.home', url: '/home' },
      {
        label: 'menu.accrualreport',
        url: '/accrual-reports-listing'
      },
      {
        label: 'menu.riplagingreport',
        url: '/ripl-aging-report'
      }
      ]
    }
  },
  {
    path: 'ut-listing-transactions', component: UtListingTransactionsComponent, canActivate: [AuthGuard], data: {
      title: 'Unmatched Transaction Listing - Transactions View ', breadcrumb: [{ label: 'menu.home', url: '/home' },
      {
        label: 'labels.Unmatched Transaction Listing - Months View',
        url: '/ut-listing-months'
      },
      {
        label: 'labels.Unmatched Transaction Listing - Days View',
        url: '/ut-listing-days'
      },
      {
        label: 'labels.Unmatched Transaction Listing - Transactions View',
        url: '/ut-listing-transactions'
      }
      ]
    }
  },
  {
    path: 'ut-listing-months', component: UtListingMonthsComponent, canActivate: [AuthGuard], data: {
      title: 'Unmatched Transaction Listing - Months View', breadcrumb: [{ label: 'menu.home', url: '/home' },
      {
        label: 'menu.reconciliationreport',
        url: '/reconciliation-reports-listing'
      },
      {
        label: 'labels.Unmatched Transaction Listing - Months View',
        url: '/ut-listing-months'
      }
      ]
    }
  },
  {
    path: 'ut-listing-days', component: UtListingDaysComponent, canActivate: [AuthGuard], data: {
      title: 'Unmatched Transaction Listing - Months View', breadcrumb: [{ label: 'menu.home', url: '/home' },
      {
        label: 'menu.reconciliationreport',
        url: '/reconciliation-reports-listing'
      },
      {
        label: 'labels.Unmatched Transaction Listing - Months View',
        url: '/ut-listing-months'
      },
      {
        label: 'labels.Unmatched Transaction Listing - Days View',
        url: '/ut-listing-days'
      }
      ]
    }
  },
  {
    path: 'matched-transaction-listing', component: MatchedTransactionListingComponent, canActivate: [AuthGuard], data: {
      title: 'Matched Transaction Listing', breadcrumb: [{ label: 'menu.home', url: '/home' },
      {
        label: 'menu.reconciliationreport',
        url: '/reconciliation-reports-listing'
      },

      {
        label: 'labels.matchedtransactionlisting',
        url: '/matched-transaction-listing'
      }
      ]
    }
  },
  {
    path: 'pg-settlement-disbursement-listing', component: PgSettlementDisbursementListingComponent, canActivate: [AuthGuard], data: {
      title: 'PG Settlement/Disbursement Listing', breadcrumb: [{ label: 'menu.home', url: '/home' },
      {
        label: 'menu.reconciliationreport',
        url: '/reconciliation-reports-listing'
      },
      {
        label: 'labels.pgsettlementdisbursementlisting', // ✅ Fix typo (no "/")
        url: '/pg-settlement-disbursement-listing'
      }
      ]
    }
  },
  {
    path: 'deferred-income-aging', component: DeferredIncomeAgingComponent, canActivate: [AuthGuard], data: {
      title: 'Deferred Income Aging Report Generation', breadcrumb: [{ label: 'menu.home', url: '/home' },
      {
        label: 'menu.accrualreport',
        url: '/accrual-reports-listing'
      },

      {
        label: 'labels.deferredincomeagingreportgeneration',
        url: '/deferred-income-aging'
      }
      ]
    }
  },
  {
    path: 'unmatched-aging', component: UnmatchedAgingComponent, canActivate: [AuthGuard], data: {
      title: 'Unmatched Aging Report Generation', breadcrumb: [{ label: 'menu.home', url: '/home' },
      {
        label: 'menu.reconciliationreport',
        url: '/reconciliation-reports-listing'
      },
      {
        label: 'labels.unmatchedagingreportgeneration',
        url: '/unmatched-aging'
      }
      ]
    }
  },
  {
    path: 'ricp-aging', component: RICPAgingComponent, canActivate: [AuthGuard], data: {
      title: 'Receivable Income Compound Aging Report Generation', breadcrumb: [{ label: 'menu.home', url: '/home' },
      {
        label: 'menu.accrualreport',
        url: '/accrual-reports-listing'
      },
      {
        label: 'labels.ricpagingreportgeneration',
        url: '/ricp-aging'
      }
      ]
    }
  },
  {
    path: 'fms-account-code', component: FMSAccountListingComponent, canActivate: [AuthGuard], data: {
      title: 'FMS Account Code', breadcrumb: [{ label: 'menu.home', url: '/home' },
      {
        label: 'labels.fmsaccountcode',
        url: '/fms-account-code'
      }
      ]
    }
  },
  {
    path: 'fpa-scheduler', component: FPASchedulerListingComponent, canActivate: [AuthGuard], data: {
      title: 'Financial Post Accounting Schedulers', breadcrumb: [{ label: 'menu.home', url: '/home' },
      {
        label: 'labels.fpascheduler',
        url: '/fpa-scheduler'
      }
      ]
    }
  },

  {
    path: 'counter-collection-report', component: CounterCollectionComponent, canActivate: [AuthGuard], data: {
      title: 'Counter Collection Report', breadcrumb: [{ label: 'menu.home', url: '/home' },
      {
        label: 'menu.otcreport',
        url: '/otc-reports-listing'
      },
      {
        label: 'labels.countercollectionreport',
        url: '/counter-collection'
      }
      ]
    }
  },
  
  // {
  //   path: 'daily-balancing-report', component: DailyBalancingComponent, canActivate: [AuthGuard], data: {
  //     title: 'Daily Balancing Report', breadcrumb: [{ label: 'menu.home', url: '/home' },
  //     {
  //       label: 'labels.dailybalancingreport',
  //       url: '/daily-balancing-report'
  //     }
  //     ]
  //   }
  // },
  {
    path: 'daily-balancing-report', component: DailyBalancingComponent, data: {
      title: 'Daily Balancing Report', breadcrumb: [{ label: 'menu.home', url: '/home' },
      {
        label: 'menu.otcreport',
        url: '/otc-reports-listing'
      },
      {
        label: 'labels.dailybalancingreport',
        url: '/daily-balancing-report'
      }
      ]
    }
  },
  // {
  //   path: 'master-balancing-report', component: MasterBalancingComponent, data: {
  //     title: 'Master Balancing Report', breadcrumb: [{ label: 'menu.home', url: '/home' },
  //     {
  //       label: 'labels.masterbalancingreport',
  //       url: '/master-balancing-report'
  //     }
  //     ], canActivate: [AuthGuard]
  //   }
  // },
  {
    path: 'master-balancing-report', component: MasterBalancingComponent, canActivate: [AuthGuard], data: {
      title: 'Master Balancing Report', breadcrumb: [{ label: 'menu.home', url: '/home' },
      {
        label: 'menu.otcreport',
        url: '/otc-reports-listing'
      },
      {
        label: 'labels.masterbalancingreport',
        url: '/master-balancing-report'
      }
      ]
    }
  },
  {
    path: 'otc-collection-report', component: OtcCollectionComponent, canActivate: [AuthGuard], data: {
      title: 'OTC Collection Report', breadcrumb: [{ label: 'menu.home', url: '/home' },
      {
        label: 'menu.otcreport',
        url: '/otc-reports-listing'
      },
      {
        label: 'labels.otccollectionreport',
        url: '/otc-collection-report'
      }
      ]
    }
  },
  {
    path: 'otc-collection-plus-report', component: OtcCollectionPlusComponent, canActivate: [AuthGuard], data: {
      title: 'OTC Collection Report by Fee Detail and Branch', breadcrumb: [{ label: 'menu.home', url: '/home' },
      {
        label: 'menu.otcreport',
        url: '/otc-reports-listing'
      },
      {
        label: 'labels.otccollectionplusreport',
        url: '/otc-collection-plus-report'
      }
      ],
    }
  },

  {
    path: 'otc-receipt-cancellation-report', component: OtcReceiptCancellationComponent, canActivate: [AuthGuard], data: {
      title: 'OTC Receipt Cancellation Report', breadcrumb: [{ label: 'menu.home', url: '/home' },
      {
        label: 'menu.otcreport',
        url: '/otc-reports-listing'
      },
      {
        label: 'labels.otcreceiptcancellationreport',
        url: '/otc-receipt-cancellation-report'
      }
      ]
    }
  },
  {
    path: 'otc-returned-cheque-report', component: OtcReturnedChequeReportComponent, canActivate: [AuthGuard], data: {
      title: 'OTC Returned Cheque Report', breadcrumb: [{ label: 'menu.home', url: '/home' },
      {
        label: 'menu.otcreport',
        url: '/otc-reports-listing'
      },
      {
        label: 'labels.otcreturnedchequereport',
        url: '/otc-returned-cheque-report'
      }
      ]
    }
  },
  {
    path: 'bank-in-slip-report', component: BankInSlipComponent, canActivate: [AuthGuard], data: {
      title: 'Bank In Slip Report', breadcrumb: [{ label: 'menu.home', url: '/home' },
      {
        label: 'menu.otcreport',
        url: '/otc-reports-listing'
      },
      {
        label: 'labels.bankinslipreport',
        url: '/bank-in-slip-report'
      }
      ]
    }
  },
  {
    path: 'otc-checkin', component: OTCCheckInComponent, canActivate: [AuthGuard], data: {
      title: 'Counter Check In', breadcrumb: [{ label: 'menu.home', url: '/home' },
      {
        label: 'menu.otccheckin',
        url: '/otc-checkin'
      }
      ]
    }
  },
  {
    path: 'otc-checkout', component: OTCCheckOutComponent, canActivate: [AuthGuard], data: {
      title: 'Counter Check Out', breadcrumb: [{ label: 'menu.home', url: '/home' },
      {
        label: 'menu.otccheckout',
        url: '/otc-checkout'
      }
      ]
    }
  },
  {
    path: 'billing-registration', component: BillRegistrationComponent, canActivate: [AuthGuard], 
    data: { title: 'Billing Registration', 
    	breadcrumb: [{ label: 'menu.home', url: '/home' },
      		{ label: 'menu.bilreg', url: '/billing-registration' }]
    }
  },
  {
    path: 'billing-listing',
    children: [{path: '', component: BillListingComponent, canActivate: [AuthGuard], 
    	data: { title: 'Billing Listing', breadcrumb: [{ label: 'menu.home', url: '/home' },
		    { label: 'menu.billist', url: '/billing-listing' }]}
    },
    {path: 'billing-details', component: BillingDetailsComponent, canActivate: [AuthGuard],
    	data: {title: 'Billing Details', breadcrumb: [{ label: 'menu.home', url: '/home' },
  			{ label: 'menu.billist', url: '/billing-listing' },
  			{ label: 'menu.bildet', url: '/billing-listing' }]}
    },
    {path: 'billing-approval', component: BillApprovalComponent, canActivate: [AuthGuard],
    	data: {title: 'Billing Approval', breadcrumb: [{ label: 'menu.home', url: '/home' },
  			{ label: 'menu.billist', url: '/billing-listing' },
  			{ label: 'menu.bilaproval', url: '/billing-listing' }]}
    }]
  },
  {
    path: 'billing-approval', component: BillApprovalComponent, data: {
      title: 'Billing Approval', breadcrumb: [{ label: 'menu.home', url: '/home' },
      {
        label: 'menu.bilaproval',
        url: '/billing-listing'
      }
      ], canActivate: [AuthGuard]
    }
  },
  {
    path: 'billing-adjustment-approval', component: BillAdjustmentApprovalComponent, data: {
      title: 'Billing Adjustment Approval', breadcrumb: [{ label: 'menu.home', url: '/home' },
      	{ label: 'menu.biladjaproval', url: '/billing-adjustment-search' }
      ], canActivate: [AuthGuard]
    }
  },
  {
    path: 'billing-cancellation-approval', component: BillCancellationApprovalComponent, data: {
      title: 'Billing Cancellation Approval', breadcrumb: [{ label: 'menu.home', url: '/home' },
      {
        label: 'menu.bilcanaproval',
        url: '/billing-cancellation-search'
      }
      ], canActivate: [AuthGuard]
    }
  },
  {
    path: 'otc-returned-cheque', component: OtcReturnedChequeComponent, canActivate: [AuthGuard], data: {
      title: 'Returned Cheque', breadcrumb: [{ label: 'menu.home', url: '/home' },
      {
        label: 'labels.otcreturncheque',
        url: '/otc-returned-cheque'
      }
      ]
    }
  },
  {
    path: 'otc-collection-receipting', component: OtcCollectionReceiptingComponent, canActivate: [AuthGuard], data: {
      title: 'OTC Collection and Receipting', breadcrumb: [{ label: 'menu.home', url: '/home' },
      {
        label: 'labels.otccollectionandreceipting',
        url: '/otc-collection-receipting'
      }
      ]
    }
  },
  {
    path: 'otc-payment-screen/:coll_slip_no', component: OtcPaymentScreenComponent, canActivate: [AuthGuard], data: {
      title: 'OTC Payment Screen', breadcrumb: [{ label: 'menu.home', url: '/home' },
      {
        label: 'labels.otccollectionandreceipting',
        url: '/otc-collection-receipting'
      },
      {
        label: 'labels.otcpaymentscreen',
        url: '/otc-payment-screen'
      }
      ]
    }
  },
  {
    path: 'otc-receipt-screen/:coll_slip_no', component: OtcReceiptScreenComponent, canActivate: [AuthGuard], data: {
      title: 'OTC Receipt Screen', breadcrumb: [{ label: 'menu.home', url: '/home' },
      {
        label: 'labels.otccollectionandreceipting',
        url: '/otc-collection-receipting'
      },
      {
        label: 'labels.otcreceiptscreen',
        url: '/otc-receipt-screen'
      }
      ]
    }
  },
  {
    path: 'non-billing-registration', component: NonBillingRegistrationComponent, canActivate: [AuthGuard], data: {
      title: 'Non Billing Registration', breadcrumb: [{ label: 'menu.home', url: '/home' },
      {
        label: 'labels.otcreturncheque',
        url: '/otc-returned-cheque'
      },
      {
        label: 'labels.otcreceiptscreen',
        url: '/otc-receipt-screen'
      },
      {
        label: 'labels.nonbillingregistration',
        url: '/non-billing-registration'
      }
      ]
    }
  },
  {
    path: 'billing-cancellation-search',
    children: [{path: '', component: BillingCancellationSearchComponent, canActivate: [AuthGuard], 
    	data: {title: 'Billing Cancellation Search', breadcrumb: [{ label: 'menu.home', url: '/home' },
		    { label: 'menu.billingcancellation', url: '/billing-cancellation-search' }]}
    },
    {path: 'billing-details', component: BillingDetailsComponent, canActivate: [AuthGuard],
    	data: {title: 'Billing Details', breadcrumb: [{ label: 'menu.home', url: '/home' },
  			{ label: 'menu.billingcancellation', url: '/billing-cancellation-search' },
  			{ label: 'menu.bildet', url: '/billing-details' }]}
    },
    {path: 'billing-cancellation', component: BillingCancellationComponent, canActivate: [AuthGuard],
    	data: {title: 'Billing Details', breadcrumb: [{ label: 'menu.home', url: '/home' },
  			{ label: 'menu.billingcancellation', url: '/billing-cancellation-search' },
  			{ label: 'labels.billingcancellation', url: '/billing-cancellation-search' }]}
    },
    {path: 'billing-cancellation-approval', component: BillCancellationApprovalComponent, canActivate: [AuthGuard], 
	      	data: {title: 'Billing Cancellation Approval', breadcrumb: [{ label: 'menu.home', url: '/home' },
  			{ label: 'menu.billingcancellation', url: '/billing-cancellation-search' },
	         { label: 'menu.bilcanaproval', url: '/billing-cancellation-search' }]}
  	}]
  },
  {
    path: 'billing-cancellation', component: BillingCancellationComponent, canActivate: [AuthGuard], data: {
      title: 'Billing Cancellation', breadcrumb: [{ label: 'menu.home', url: '/home' },
      {
        label: 'labels.billingcancellationlisting',
        url: '/billing-cancellation-listing'
      },
      {
        label: 'labels.billingcancellation',
        url: '/billing-cancellation-listing'
      }
      ]
    }
  },
  /*{
    path: 'billing-cancellation-recurring/:bill_no', component: BillingCancellationRecurringComponent, canActivate: [AuthGuard], data: {
      title: 'Billing Cancellation', breadcrumb: [{ label: 'menu.home', url: '/home' },
      {
        label: 'labels.billingcancellation',
        url: '/billing-cancellation-search'
      },
      {
        label: 'labels.billingdetails',
        url: '/billing-cancellation-recurring'
    },
      ]
    }
  }*/
  {
    path: 'billing-cancellation-listing',
    children: [{path: '', component: BillingCancellationListingComponent, canActivate: [AuthGuard], 
    	data: {title: 'Billing Cancellation', breadcrumb: [{ label: 'menu.home', url: '/home' },
      		{ label: 'labels.billingcancellationlisting', url: '/billing-cancellation-listing' }]}
    },
    {path: 'billing-details', component: BillingDetailsComponent, canActivate: [AuthGuard],
    	data: {title: 'Billing Details', breadcrumb: [{ label: 'menu.home', url: '/home' },
  			{ label: 'labels.billingcancellationlisting', url: '/billing-cancellation-listing' },
  			{ label: 'menu.bildet', url: '/billing-cancellation-listing' }]}
    },
    {path: 'billing-cancellation', component: BillingCancellationComponent, canActivate: [AuthGuard],
    	data: {title: 'Billing Details', breadcrumb: [{ label: 'menu.home', url: '/home' },
  			{ label: 'labels.billingcancellationlisting', url: '/billing-cancellation-listing' },
  			{ label: 'labels.billingcancellation', url: '/billing-cancellation-listing' }]}
    },
    {path: 'billing-cancellation-approval', component: BillCancellationApprovalComponent, canActivate: [AuthGuard], 
	      	data: {title: 'Billing Cancellation Approval', breadcrumb: [{ label: 'menu.home', url: '/home' },
  			{ label: 'labels.billingcancellationlisting', url: '/billing-cancellation-listing' },
	         { label: 'menu.bilcanaproval', url: '/billing-cancellation-listing' }]}
  	}]
  },
  {
    path: 'billing-adjustment-search',
    children: [{path: '', component: BillingAdjustmentSearchComponent, canActivate: [AuthGuard], 
    	data: {title: 'Billing Adjustment Search', breadcrumb: [{ label: 'menu.home', url: '/home' },
      		{ label: 'labels.billingadjustmentsearch', url: '/billing-adjustment-search'}]}
	},
    {path: 'billing-details', component: BillingDetailsComponent, canActivate: [AuthGuard], 
    	data: {title: 'Billing Details', breadcrumb: [{ label: 'menu.home', url: '/home' },
  			{ label: 'labels.billingadjustmentsearch', url: '/billing-adjustment-search' },
  			{ label: 'menu.bildet', url: '/billing-adjustment-search' }]}
    },
  	{path: 'billing-adjustment', component: BillingAdjustmentComponent, canActivate: [AuthGuard], 
      	data: { title: 'Billing Adjustment', breadcrumb: [{ label: 'menu.home', url: '/home' },
  			{ label: 'labels.billingadjustmentsearch', url: '/billing-adjustment-search' },
      		{label: 'menu.billingadjustment', url: '/billing-adjustment-search'}]}
 	},
  	{path: 'billing-adjustment-approval', component: BillAdjustmentApprovalComponent, canActivate: [AuthGuard], 
      	data: { title: 'Billing Adjustment Approval', breadcrumb: [{ label: 'menu.home', url: '/home' },
  			{ label: 'labels.billingadjustmentsearch', url: '/billing-adjustment-search' },
      		{label: 'menu.biladjaproval', url: '/billing-adjustment-search'}]}
 	}]
  },
  {
    path: 'billing-adjustment', component: BillingAdjustmentComponent, canActivate: [AuthGuard], data: {
      title: 'Billing Adjustment', breadcrumb: [{ label: 'menu.home', url: '/home' },
      {
        label: 'labels.billingadjustmentsearch',
        url: '/billing-adjustment-search'
      },
      {
        label: 'labels.billingadjustment',
        url: '/billing-adjustment-search'
      },
      ], canActivate: [AuthGuard]
    }
  },
  {
    path: 'bibss-customer-id-validation', component: BibssCustomerIdValidationComponent, data: {
      title: 'Billing Registration', breadcrumb: [{ label: 'menu.home', url: '/home' },
      {
        label: 'Billing Registration',
        url: '/bibss-customer-id-validation'
      }
      ]
    }, canActivate: [AuthGuard]
  },
  {
    path: 'bibss-listing', component: BibssListingComponent, data: {
      title: 'Billing Issuance', breadcrumb: [{ label: 'menu.home', url: '/home' },
      {
        label: 'Billing Listing',
        url: '/bibss-listing'
      }
      ], canActivate: [AuthGuard]
    }
  },
  /*{
    path: 'billing-adjustment-recurring', component: BillingAdjustmentRecurringComponent, canActivate: [AuthGuard], data: {
      title: 'Billing Adjustment', breadcrumb: [{ label: 'menu.home', url: '/home' },
      {
        label: 'labels.billingadjustmentsearch',
        url: '/billing-adjustment-search'
      }

      ], canActivate: [AuthGuard]
    }
  },

  {
    path: 'billing-adjustment-recurring', component: BillingAdjustmentRecurringComponent, data: {
      title: 'Billing Adjustment', breadcrumb: [{ label: 'menu.home', url: '/home' },
      {
        label: 'labels.billingadjustmentsearch',
        url: '/billing-adjustment-search'
      },
      {
        label: 'labels.billingdetails',
        url: '/billing-adjustment-recurring'
    },
      ]
    }
  },*/
  {
    path: 'otc-emv-reconciliation', component: OtcEmvReconciliationComponent, canActivate: [AuthGuard], data: {
      title: 'EMV Reconciliation', breadcrumb: [{ label: 'menu.home', url: '/home' },
      {
        label: 'labels.otcemvreconciliation',
        url: '/otc-emv-reconciliation'
      },
      ]
    }
  },
  {
    path: 'bibss-list', component: BibssListComponent, data: {
      title: 'Billing Listing', breadcrumb: [{ label: 'menu.home', url: '/home' },
      {
        label: 'Billing Listing',
        url: '/bibss-list'
      },
      ], canActivate: [AuthGuard]
    }
  },
  {

    path: 'otc-emv-reconciliation-details', component: OtcEmvReconciliationDetailsComponent, data: {
      title: 'OTC Receipt Screen', breadcrumb: [{ label: 'menu.home', url: '/home' },
      {
        label: 'labels.otcemvreconciliation',
        url: '/otc-emv-reconciliation'
      },
      {
        label: 'labels.otcemvreconciliationdetails',
        url: '/otc-emv-reconciliation-details'
      }
      ]
    }
  },
  {
    path: 'non-billing-listing', component: NonBillingListingComponent, canActivate: [AuthGuard], data: {
      title: 'Non Billing Listing', breadcrumb: [{ label: 'menu.home', url: '/home' },
      {
        label: 'labels.nonbillinglisting',
        url: '/non-billing-listing'
      },
      ]
    }
  },
  {
    path: 'non-billing-details/:bill_no', component: NonBillingDetailsComponent, canActivate: [AuthGuard], data: {
      title: 'Non Billing Details', breadcrumb: [{ label: 'menu.home', url: '/home' },
      {
        label: 'labels.nonbillinglisting',
        url: '/non-billing-listing'
      },
      {
        label: 'labels.nonbillingdetails',
        url: '/non-billing-details'
      },
      ]
    }
  },

  {
    path: 'branch-code-listing', component: BranchCodeListingComponent, canActivate: [AuthGuard], data: {
      title: 'Branch Code', breadcrumb: [{ label: 'menu.home', url: '/home' },
      {
        label: 'labels.branchcodelisting',
        url: '/branch-code-listing'
      }
      ],


    },

  },

  {
    path: 'billing-class-listing',
    component: BillingClassListingComponent,
    canActivate: [AuthGuard],
    data: {
      title: 'Billing Class',
      breadcrumb: [
        { label: 'menu.home', url: '/home' },
        {
          label: 'labels.billingclasslisting',
          url: '/billing-class-listing'
        }
      ],

    }
  },

  {
    path: 'billing-type-listing',
    component: BillingTypeListingComponent,
    canActivate: [AuthGuard],
    data: {
      title: 'Billing Type',
      breadcrumb: [
        { label: 'menu.home', url: '/home' },
        {
          label: 'labels.billingtypelisting',
          url: '/billing-type-listing'
        }
      ],

    }
  },


  {
    path: 'refund-summary-status', component: RefundSsmReportComponent, canActivate: [AuthGuard], data: {
      title: 'Refund Summary Status Report Generation', breadcrumb: [{ label: 'menu.home', url: '/home' },
      {
        label: 'menu.refundreport',
        url: '/refund-reports-listing'
      },
      {
        label: 'labels.refundsummarystatusreportgeneration',
        url: '/refund-summary-status'
      }
      ],
    }
  },

  {
    path: 'refund-aging', component: RefundAgingComponent, canActivate: [AuthGuard], data: {
      title: 'Refund Aging Report Generation', breadcrumb: [{ label: 'menu.home', url: '/home' },
      {
        label: 'menu.refundreport',
        url: '/refund-reports-listing'
      },
      {
        label: 'labels.refundagingreportgeneration',
        url: '/refund-aging'
      }
      ],
    }
  },

  {
    path: 'billing-report', component: BillingReportComponent, canActivate: [AuthGuard], data: {
      title: 'Billing Report Generation', breadcrumb: [{ label: 'menu.home', url: '/home' },
      {
        label: 'menu.cataloguereport',
        url: '/catalogue-reports-listing'
      },
      {
        label: 'labels.billingreportgeneration',
        url: '/billing'
      }
      ],
    }
  },

  {
    path: 'catalogue-product-service-report', component: CatalogueProductServiceReportComponent, canActivate: [AuthGuard], data: {
      title: 'Catalogue Product Service Report Generation', breadcrumb: [{ label: 'menu.home', url: '/home' },
      {
        label: 'menu.cataloguereport',
        url: '/catalogue-reports-listing'
      },
      {
        label: 'labels.catalogueproductservicereportgeneration',
        url: '/catalogue-product-service'
      }
      ],
    }
  },

  {
    path: 'summary-billing-report', component: SummaryBillingReportComponent, canActivate: [AuthGuard], data: {
      title: 'Summary Billing by Class ID Report Generation', breadcrumb: [{ label: 'menu.home', url: '/home' },
      {
        label: 'menu.cataloguereport',
        url: '/catalogue-reports-listing'
      },
      {
        label: 'labels.summarybillingreportgeneration',
        url: '/summary-billing'
      }
      ],
    }
  },

  {
    path: 'detailed-billing-report', component: DetailedBillingReportComponent, canActivate: [AuthGuard], data: {
      title: 'Detailed Billing by Class ID Report Generation', breadcrumb: [{ label: 'menu.home', url: '/home' },
      {
        label: 'menu.cataloguereport',
        url: '/catalogue-reports-listing'
      },
      {
        label: 'labels.detailedbillingreportgeneration',
        url: '/detailed-billing'
      }
      ],
    }
  },

  {
    path: 'detailed-billing-bt-report', component: DetailedBillingBtReportComponent, canActivate: [AuthGuard], data: {
      title: 'Detailed Billing by Billing Type Report Generation', breadcrumb: [{ label: 'menu.home', url: '/home' },
      {
        label: 'menu.cataloguereport',
        url: '/catalogue-reports-listing'
      },
      {
        label: 'labels.detailedbillingbtreportgeneration',
        url: '/detailed-billing-bt'
      }
      ],
    }
  },

  {
    path: 'refund-status-detailed', component: RefundStatusDetailedComponent, canActivate: [AuthGuard], data: {
      title: 'Refund Status Detailed Report Generation', breadcrumb: [{ label: 'menu.home', url: '/home' },
      {
        label: 'menu.refundreport',
        url: '/refund-reports-listing'
      },
      {
        label: 'labels.refundstatusdetailedreportgeneration',
        url: '/refund-status-detailed'
      }
      ],
    }
  },
  {
    path: 'billing-details', component: BillingDetailsComponent, data: {
      title: 'Billing Details', breadcrumb: [{ label: 'menu.home', url: '/home' },
      { label: 'menu.bildet', url: '/billing-details' }],
      canActivate: [AuthGuard]
    }
  },
  {
    path: 'paid-transaction-table', component: RefundPTTListingComponent, data: {
      title: 'Paid Transaction Table', breadcrumb: [{ label: 'menu.home', url: '/home' },
      {
        label: 'labels.paidtransactiontable',
        url: '/paid-transaction-table'
      }
      ], canActivate: [AuthGuard]
    }
  },
  {
    path: 'refund-initial-fa', component: RefundInitialFAComponent, data: {
      title: 'Refund Initial FA', breadcrumb: [{ label: 'menu.home', url: '/home' },
      {
        label: 'labels.paidtransactiontable',
        url: '/paid-transaction-table'
      },
      {
        label: 'labels.refundinitialfa',
        url: '/refund-initial-fa'
      }
      ], canActivate: [AuthGuard]
    }
  },
  {
    path: 'refund-request-select-sme', component: RefundRequestSelectSMEComponent, data: {
      title: 'Refund Request Select SME', breadcrumb: [{ label: 'menu.home', url: '/home' },
      {
        label: 'labels.paidtransactiontable',
        url: '/paid-transaction-table'
      },
      {
        label: 'labels.refundinitialfa',
        url: '/refund-initial-fa'
      },
      {
        label: 'labels.refundrequestselectsme',
        url: '/refund-request-select-sme'
      }
      ], canActivate: [AuthGuard]
    }
  },
  {
    path: 'refund-approval-fa', component: RefundApprovalFaComponent, data: {
      title: 'Refund Approval FA', breadcrumb: [{ label: 'menu.home', url: '/home' },
      {
        label: 'labels.refundapproval',
        url: '/refund-approval-fa'
      },
      ], canActivate: [AuthGuard]
    }
  },
  {
    path: 'refund-approval-userrole', component: RefundApprovalUserroleComponent, data: {
      title: 'Refund Approval User Role', breadcrumb: [{ label: 'menu.home', url: '/home' },
      {
        label: 'labels.refundapproval',
        url: '/refund-approval-userrole'
      },
      ], canActivate: [AuthGuard]
    }
  },
  {
    path: 'credit-control-case-view', component: CreditControlCaseViewerComponent, data: {
      title: 'Credit Control Case Details', breadcrumb: [{ label: 'menu.home', url: '/home' },
      {
        label: 'labels.cccdetails',
        url: '/credit-control-case-view'
      },
      ], canActivate: [AuthGuard]
    }
  },
  {
    path: 'credit-control-case', component: CreditControlCaseComponent, data: {
      title: 'Credit Control Case', breadcrumb: [{ label: 'menu.home', url: '/home' },
      {
        label: 'labels.cccase',
        url: '/credit-control-case'
      },
      ], canActivate: [AuthGuard]
    }
  },
  {
    path: 'refund-approval-blank-form', component: RefundApproveBlankFormComponent, data: {
      title: 'Refund Approval Blank Form', breadcrumb: [{ label: 'menu.home', url: '/home' },
      {
        label: 'labels.assignedtasks',
        url: '/my-task-assigned-tasks'
      },
      {
        label: 'labels.refundapprovalblankform',
        url: '/refund-approval-blank-form'
      },
      ], canActivate: [AuthGuard]
    }
  },
  {
    path: 'refund-listing', component: RefundListingComponent, data: {
      title: 'Refund Listing', breadcrumb: [{ label: 'menu.home', url: '/home' },
      {
        label: 'labels.refundlisting',
        url: '/refund-listing'
      },
      ], canActivate: [AuthGuard]
    }
  },

  {
    path: 'refund-listing-info', component: RefundListingInfoComponent, data: {
      title: 'Refund Listing Info', breadcrumb: [{ label: 'menu.home', url: '/home' },
      {
        label: 'labels.refundlisting',
        url: '/refund-listing'
      },
      {
        label: 'labels.refundlistinginfo',
        url: '/refund-listing-info'
      },
      ], canActivate: [AuthGuard]
    }
  },

  {
    path: 'refund-listing-info-rf', component: RefundListingInfoRfComponent, data: {
      title: 'Refund Listing Info Refund Form', breadcrumb: [{ label: 'menu.home', url: '/home' },
      {
        label: 'labels.refundlisting',
        url: '/refund-listing'
      },
      {
        label: 'labels.refundlistinginfoRF',
        url: '/refund-listing-info-rf'
      },
      ], canActivate: [AuthGuard]
    }
  },
  {
    path: 'refund-created-task-details', component: RefundCreatedTaskDetailsComponent, data: {
      title: 'Refund Created Task Details', breadcrumb: [{ label: 'menu.home', url: '/home' },
      {
        label: 'labels.createdtasks',
        url: '/my-task-created-task/refund'
      },
      {
        label: 'labels.refundcreatedtaskdetails',
        url: '/refund-created-task-details'
      },
      ], canActivate: [AuthGuard]
    }
  },

  {
    path: 'refund-created-task-rf-details', component: RefundCreatedTaskRfDetailsComponent, data: {
      title: 'Refund Listing Info Refund Form', breadcrumb: [{ label: 'menu.home', url: '/home' },
        {
          label: 'labels.createdtasks',
          url: '/my-task-created-task/refund'
        },
        {
          label: 'labels.refundcreatedtaskrfdetails',
          url: '/refund-created-task-rf-details'
        },
      ], canActivate: [AuthGuard]
    }
  },
  {
    path: 'rcpt-no-vld', component: ReceiptNoValidationComponent, data: {
      // title: 'Financial Post Accounting Schedulers', breadcrumb: [{ label: 'menu.home', url: '/home' },
      // {
      //   label: 'labels.otcreceiptcancellation',
      //   url: '/rcpt-no-vld'
      // }
      //], canActivate: [AuthGuard]
    }
  },
  {
    path: 'otc-rcpt-dets', component: OtcReceiptDetailsComponent, data: {
      title: 'OTC Receipt Cancellation Details', breadcrumb: [{ label: 'menu.home', url: '/home' },
      {
        label: 'labels.otcreceiptcancellation',
        url: '/rcpt-no-vld'
      },
      {
        label: 'labels.otcreceiptscreen',
        url: '/otc-rcpt-dets'
      }
      ], canActivate: [AuthGuard]
    }
  },
  {
    path: 'rcpt-ccl-app-and-just', component: ReceiptCancellationApprovalAndJustificationComponent, data: {
      title: 'Receipt Cancellation Approval and Justification', breadcrumb: [{ label: 'menu.home', url: '/home' },
      {
        label: 'labels.assignedtasks',
        url: '/my-task-assigned-tasks'
      },
      {
        label: 'labels.receiptcancellation',
        url: '/rcpt-ccl-app-and-just'
      }
      ], canActivate: [AuthGuard]
    }
  },
  {
    path: 'update-task-status', component: UpdateTaskStatusComponent, data: {
      title: 'Update Receipt Cancellation Task Status', breadcrumb: [{ label: 'menu.home', url: '/home' },
      {
        label: 'labels.assignedtasks',
        url: '/my-task-assigned-tasks'
      },
      {
        label: 'labels.receiptcancellation',
        url: '/update-task-status'
      }
      ], canActivate: [AuthGuard]
    }
  },
  {
    path: 'bibss-customer-id-validation', component: BibssCustomerIdValidationComponent, data: {
      title: 'Billing Registration by Source System', breadcrumb: [{ label: 'menu.home', url: '/home' },
      {
        label: 'labels.billingregistration',
        url: '/bibss-customer-id-validation'
      }
      ], canActivate: [AuthGuard]
    }
  },
  {
    path: 'my-task-public-task/fa', component: MyTaskPublicTaskComponent, data: {
      title: 'Public Group Tasks', breadcrumb: [{ label: 'menu.home', url: '/home' },
      {
        label: 'labels.financeadmintasklist',
        url: '/my-task-public-task/fa'
      }
      ], canActivate: [AuthGuard]
    }
  },
  {
    path: 'my-task-public-task/bym', component: MyTaskPublicTaskComponent, data: {
      title: 'Public Group Tasks', breadcrumb: [{ label: 'menu.home', url: '/home' },
      {
        label: 'labels.bymtasklist',
        url: '/my-task-public-task/bym'
      }
      ], canActivate: [AuthGuard]
    }
  },
  {
    path: 'my-task-public-task/pg', component: MyTaskPublicTaskComponent, data: {
      title: 'Public Group Tasks', breadcrumb: [{ label: 'menu.home', url: '/home' },
      {
        label: 'labels.pgtasklist',
        url: '/my-task-public-task/pg'
      }
      ], canActivate: [AuthGuard]
    }
  },
  {
    path: 'my-task-public-task/os', component: MyTaskPublicTaskComponent, data: {
      title: 'Public Group Tasks', breadcrumb: [{ label: 'menu.home', url: '/home' },
      {
        label: 'labels.otcstafftasklist',
        url: '/my-task-public-task/fa'
      }
      ], canActivate: [AuthGuard]
    }
  },
  {
    path: 'my-task-public-task/osp', component: MyTaskPublicTaskComponent, data: {
      title: 'Public Group Tasks', breadcrumb: [{ label: 'menu.home', url: '/home' },
      {
        label: 'labels.otcsupervisortasklist',
        url: '/my-task-public-task/osp'
      }
      ], canActivate: [AuthGuard]
    }
  },
  {
    path: 'my-task-public-task/obm', component: MyTaskPublicTaskComponent, data: {
      title: 'Public Group Tasks', breadcrumb: [{ label: 'menu.home', url: '/home' },
      {
        label: 'labels.otcbranchmanagertasklist',
        url: '/my-task-public-task/obm'
      }
      ], canActivate: [AuthGuard]
    }
  },
  {
    path: 'my-task-public-task/sme', component: MyTaskPublicTaskComponent, data: {
      title: 'Public Group Tasks', breadcrumb: [{ label: 'menu.home', url: '/home' },
      {
        label: 'labels.smetasklist',
        url: '/my-task-public-task/sme'
      }
      ], canActivate: [AuthGuard]
    }
  },
  {
    path: 'my-task-public-task/lgl', component: MyTaskPublicTaskComponent, data: {
      title: 'Public Group Tasks', breadcrumb: [{ label: 'menu.home', url: '/home' },
      {
        label: 'labels.lgltasklist',
        url: '/my-task-public-task/lgl'
      }
      ], canActivate: [AuthGuard]
    }
  },
  {
    path: 'my-task-public-task/:pool', component: MyTaskPublicTaskComponent, data: {
      title: 'Public Group Tasks', breadcrumb: [{ label: 'menu.home', url: '/home' },
      {
        label: 'labels.publictasks',
        url: '/my-task-public-task'
      }
      ], canActivate: [AuthGuard]
    }
  },
  {
    path: 'non-rms-receipting-listing', component: NonRmsReceiptingListingComponent, canActivate: [AuthGuard], data: {
      title: 'Non RMS Receipting Listing', breadcrumb: [{ label: 'menu.home', url: '/home' },
      {
        label: 'labels.bankstatementreconciliationfornonrmsreceipting',
        url: '/non-rms-receipting-listing'
      },
      ]
    }
  },
  {
    path: 'non-rms-receipting-details', component: NonRmsReceiptingDetailsComponent, canActivate: [AuthGuard], data: {
      title: 'Non RMS Receipting Details', breadcrumb: [{ label: 'menu.home', url: '/home' },
      {
        label: 'menu.bankstatementreconciliationfornonrmsreceipting',
        url: '/non-rms-receipting-listing'
      },
      {
        label: 'labels.Account Receivables Invoice VS Bank Statement',
        url: '/non-rms-receipting-details'
      }
      ]
    }
  },
  {
    path: 'credit-control-sme-task-list', 
    children: [{path: '', component: CreditControlSmeTaskListComponent, canActivate: [AuthGuard], 
    	data: {title: 'Master Credit Control Listing', breadcrumb: [{ label: 'menu.home', url: '/home' },
      		{ label: 'labels.mastercreditcontrollisting', url: '/credit-control-sme-task-list'}]}
    },
    {
    	path: 'credit-control-case-view', component: CreditControlCaseViewerComponent, canActivate: [AuthGuard],
	    data: {title: 'Credit Control Case Details', breadcrumb: [{ label: 'menu.home', url: '/home' },
	    	{ label: 'labels.mastercreditcontrollisting', url: '/credit-control-sme-task-list'},
	      	{label: 'labels.cccdetails', url: '/credit-control-sme-task-list' }]}
 	},
 	{
 		path: 'credit-control-case', component: CreditControlCaseComponent, canActivate: [AuthGuard],
	    data: {title: 'Credit Control Case', breadcrumb: [{ label: 'menu.home', url: '/home' },
	    	{ label: 'labels.mastercreditcontrollisting', url: '/credit-control-sme-task-list'},
	      	{label: 'labels.cccase',url: '/credit-control-sme-task-list'}]}
  	}]
  },
  {
    path: 'counter-balancing-listing', component: CounterBalancingListingComponent, canActivate: [AuthGuard], data: {
      title: 'Counter Balancing', breadcrumb: [{ label: 'menu.home', url: '/home' },
      {
        label: 'labels.counterbalancinglisting',
        url: '/counter-balancing-listing'
      }]
    }
  },
  {
    path: 'counter-balancing-edit', component: CounterBalancingEditComponent, data: {
      title: 'Counter Balancing Edit', breadcrumb: [{ label: 'menu.home', url: '/home' },
      {
        label: 'labels.counter-balancing-edit',
        url: '/counter-balancing-edit'
      }
      ], canActivate: [AuthGuard]
    }
  },
  {
    path: 'daily-balancing-listing', component: DailyBalancingListingComponent, data: {
      title: 'Daily Balancing Listing', breadcrumb: [{ label: 'menu.home', url: '/home' },
      {
        label: 'labels.dailybalancinglisting',
        url: '/daily-balancing-listing'
      }
      ], canActivate: [AuthGuard]
    }
  },
  {
    path: 'daily-balancing-detail', component: DailyBalancingDetailComponent, data: {
      title: 'Daily Balancing Detail', breadcrumb: [{ label: 'menu.home', url: '/home' },
      {
        label: 'labels.dailybalancinglisting',
        url: '/daily-balancing-listing'
      },
      {
        label: 'labels.dailybalancingdetail',
        url: '/daily-balancing-detail'
      }
      ], canActivate: [AuthGuard]
    }
  },
  {
    path: 'master-balancing-listing', component: MasterBalancingListingComponent, data: {
      title: 'Master Balancing Listing', breadcrumb: [{ label: 'menu.home', url: '/home' },
      {
        label: 'labels.masterbalancinglisting',
        url: '/master-balancing-listing'
      }
      ], canActivate: [AuthGuard]
    }
  },
  {
    path: 'master-balancing-detail', component: MasterBalancingDetailComponent, data: {
      title: 'Master Balancing Detail', breadcrumb: [{ label: 'menu.home', url: '/home' },
      {
        label: 'labels.masterbalancinglisting',
        url: '/master-balancing-listing'
      },
      {
        label: 'labels.masterbalancingdetail',
        url: '/master-balancing-detail'
      }
      ], canActivate: [AuthGuard]
    }
  },
  {
    path: 'master-bankinslip', component: MasterBankinslipComponent, data: {
      title: 'Master Bank In Slip', breadcrumb: [{ label: 'menu.home', url: '/home' },
      {
        label: 'labels.master-bankinslip',
        url: '/master-bankinslip'
      }
      ], canActivate: [AuthGuard]
    }
  },
  //
  {
    path: 'service-provider-payment-listing', component: ServiceProviderPaymentListingComponent, canActivate: [AuthGuard], data: {
      title: 'Service Provider Payment Listing', breadcrumb: [{ label: 'menu.home', url: '/home' },
      {
        label: 'menu.serviceproviderpaymentlisting',
        url: '/service-provider-payment-listing'
      }
      ]
    }
  },
  {
    path: 'court-order-listing', component: CourtOrderListingComponent, canActivate: [AuthGuard], data: {
      title: 'Court Order Listing', breadcrumb: [{ label: 'menu.home', url: '/home' },
      {
        label: 'menu.courtorderlisting',
        url: '/court-order-listing'
      }
      ]
    }
  },
  {
    path: 'court-order-details', component: CourtOrderDetailsComponent, data: {
      title: 'Court Order Details', breadcrumb: [{ label: 'menu.home', url: '/home' },
      {
        label: 'labels.courtorderlisting',
        url: '/court-order-listing'
      },
      {
        label: 'labels.courtorderdetails',
        url: '/court-order-details'
      },

      ],
      canActivate: [AuthGuard]

    }
  },

  {
    path: 'service-provider-maintenance-listing', component: ServiceProviderMaintenanceListingComponent, canActivate: [AuthGuard], data: {
      title: 'Service Provider Maintenance', breadcrumb: [{ label: 'menu.home', url: '/home' },
      {
        label: 'Service Provider Maintenance',
        url: '/service-provider-maintenance-listing'
      }
      ]
    }
  },
  {
    path: 'accrual-reports-listing', component: AccrualReportsListingComponent, canActivate: [AuthGuard], data: {
      title: 'Accrual Reports Listing', breadcrumb: [{ label: 'menu.home', url: '/home' },
      {
        label: 'menu.accrualreport',
        url: '/accrual-reports-listing'
      }
      ]
    }
  },
  {
    path: 'reconciliation-reports-listing', component: ReconciliationReportsListingComponent, canActivate: [AuthGuard], data: {
      title: 'Reconciliation Reports Listing', breadcrumb: [{ label: 'menu.home', url: '/home' },
      {
        label: 'menu.reconciliationreport',
        url: '/reconciliation-reports-listing'
      }
      ]
    }
  },
  {
    path: 'collection-reports-listing', component: CollectionReportsListingComponent, canActivate: [AuthGuard], data: {
      title: 'Collection Reports Listing', breadcrumb: [{ label: 'menu.home', url: '/home' },
      {
        label: 'menu.collectionreport',
        url: '/collection-reports-listing'
      }
      ]
    }
  },
  {
    path: 'otc-reports-listing', component: OtcReportsListingComponent, canActivate: [AuthGuard], data: {
      title: 'OTC Reports Listing', breadcrumb: [{ label: 'menu.home', url: '/home' },
      {
        label: 'menu.otcreport',
        url: '/otc-reports-listing'
      }
      ]
    }
  },
  {
    path: 'refund-reports-listing', component: RefundReportsListingComponent, canActivate: [AuthGuard], data: {
      title: 'Refund Reports Listing', breadcrumb: [{ label: 'menu.home', url: '/home' },
      {
        label: 'menu.refundreport',
        url: '/refund-reports-listing'
      }
      ]
    }
  },
  {
    path: 'catalogue-reports-listing', component: CatalogueReportsListingComponent, canActivate: [AuthGuard], data: {
      title: 'Catalogue Reports Listing', breadcrumb: [{ label: 'menu.home', url: '/home' },
      {
        label: 'menu.cataloguereport',
        url: '/catalogue-reports-listing'
      }
      ]
    }
  },
  {
    path: 'payment-collection-fee-dt-id', component: PaymentCollectionFeeDtIdComponent, canActivate: [AuthGuard], data: {
      title: 'Payment Collection Fee Detail ID', breadcrumb: [{ label: 'menu.home', url: '/home' },
      {
        label: 'menu.collectionreport',
        url: '/collection-reports-listing'
      },
      {
        label: 'menu.payment-collection-fee-dt-id',
        url: '/payment-collection-fee-dt-id'
      }
      ]
    }
  },
  {
    path: 'payment-collection-pymt-md', component: PaymentCollectionPymtMdComponent, canActivate: [AuthGuard], data: {
      title: 'Payment Collection Payment Mode', breadcrumb: [{ label: 'menu.home', url: '/home' },
      {
        label: 'menu.collectionreport',
        url: '/collection-reports-listing'
      },
      {
        label: 'menu.payment-collection-pymt-md',
        url: '/payment-collection-pymt-md'
      }
      ]
    }
  },

  {
    path: 'payment-collection-s-s', component: PaymentCollectionSSComponent, canActivate: [AuthGuard], data: {
      title: 'Payment Collection Source System', breadcrumb: [{ label: 'menu.home', url: '/home' },
      {
        label: 'menu.collectionreport',
        url: '/collection-reports-listing'
      },

      {
        label: 'menu.payment-collection-s-s',
        url: '/payment-collection-s-s'
      }
      ]
    }
  },
  {
    path: 'refund-chargeback', component: RefundChargebackComponent, canActivate: [AuthGuard], data: {
      title: 'Refund ChargeBack', breadcrumb: [{ label: 'menu.home', url: '/home' },
      {
        label: 'labels.chargebackpaidtransactiontable',
        url: '/refund-chargeback'
      }
      ]
    }
  },
  {
    path: 'refund-chargeback-info', component: RefundChargebackInfoComponent, canActivate: [AuthGuard], data: {
      title: 'Refund ChargeBack Info', breadcrumb: [{ label: 'menu.home', url: '/home' },
      {
        label: 'labels.chargebackpaidtransactiontable',
        url: '/refund-chargeback'
      },

      {
        label: 'labels.refundchargebackinfo',
        url: '/refund-chargeback-info'
      }
      ]
    }
  },
  {
    path: 'mft-item-task-list', component: MftItemTaskListComponent, canActivate: [AuthGuard], data: {
      title: 'MFT Item Details', breadcrumb: [{ label: 'menu.home', url: '/home' },
      {
        label: 'labels.masterfeetable',
        url: '/master-fee-table'
      },
      {
        label: 'labels.mftitemdetails',
        url: '/mft-item-detail'
      }
      ]
    }
  },
  { path: '', redirectTo: 'home', pathMatch: 'full' }, // Default route,
  { path: '**', redirectTo: 'home' } // Wildcard route //20250312,Roy-RouteIssue
];



@NgModule({
  declarations: [],
  imports: [
    CommonModule,
    RouterModule.forRoot(routes, { initialNavigation: 'enabledNonBlocking' }) //20250312,Roy-RouteIssue
  ],
  exports: [RouterModule]
})

export class AppRoutingModule {
  constructor(private router: Router, private authService: AuthService) {
    // ✅ Listen for navigation changes and trigger authentication check
    this.router.events
      .pipe(filter(event => event instanceof NavigationEnd))
      .subscribe(() => {
        console.log("Route changed, refreshing authentication status...");
        this.authService.checkAuthenticationStatus(true).subscribe(
          response => console.log("Authentication status refreshed:", response),
          error => console.error("Failed to refresh authentication status:", error)
        );

        this.authService.checkLogin(true).then(
          (isAuthenticated) => console.log("✅ Authentication refreshed:", isAuthenticated),
          (error) => console.error("❌ Authentication refresh failed:", error)
        );

      });
  }
}
