import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MultiLanguageComponent } from '../multi-language/multi-language.component';
import { Routes, RouterModule } from '@angular/router';
import { HomeComponent } from '../home/home.component';
import { LoginComponent } from '../login/login.component';
import { LogoutComponent } from '../logout/logout.component';
import { PaymentPageComponent } from '../payment-page/payment-page.component';
import { LoadingPageComponent } from '../loading-page/loading-page.component';
import { AuthGuard } from '../guard/auth.guard';
import { GhlComponent } from '../ghl/ghl.component';
import { PaymentResponseComponent } from '../payment-response/payment-response.component';
import { PaymentResponseRedirectComponent } from '../payment-response-redirect/payment-response-redirect.component';
import {CatalogueProductServiceComponent} from '../catalogue-product-service/catalogue-product-service.component';
import { CatalogueJsonComponent } from '../catalogue-json/catalogue-json.component';
import { RefundTHTComponent } from '../refund/refund-tht/refund-tht.component';
import { RefundChargebackComponent } from '../refund/refund-chargeback/refund-chargeback.component';
import { RefundRefundformComponent } from '../refund/refund-refundform/refund-refundform.component';
import { RefundDirectApplicationComponent } from '../refund/refund-direct-application/refund-direct-application.component';
import { RefundSubmitBankinfoComponent } from '../refund/refund-task-list/refund-submit-bankinfo/refund-submit-bankinfo.component';
import { RefundChargebackInfoComponent } from '../refund/refund-chargeback-info/refund-chargeback-info.component'
import { LoadingHomePageComponent } from '../loading-home-page/loading-home-page.component';

import { BillRegistrationComponent} from 'src/app/billing/billing-registration/billing-registration.component';
import { BillListingComponent} from 'src/app/billing/billing-listing/billing-listing.component';
import { BillReviewComponent} from 'src/app/billing/billing-review/billing-review.component';
import { BillingDetailsComponent} from 'src/app/billing/billing-details/billing-details.component';
import { BillingCancellationSearchComponent } from 'src/app/billing/billing-cancellation-search/billing-cancellation-search.component';
import { BillingCancellationListingComponent } from 'src/app/billing/billing-cancellation-listing/billing-cancellation-listing.component';
import { BillingCancellationComponent } from 'src/app/billing/billing-cancellation/billing-cancellation.component';
import { BillCancellationReviewComponent } from 'src/app/billing/billing-cancellation-review/billing-cancellation-review.component';
import { BillingAdjustmentSearchComponent } from 'src/app/billing/billing-adjustment-search/billing-adjustment-search.component';
import { BillingAdjustmentComponent } from 'src/app/billing/billing-adjustment/billing-adjustment.component';
import { BillAdjustmentReviewComponent } from 'src/app/billing/billing-adjustment-review/billing-adjustment-review.component';



const routes: Routes = [
  //{ path: 'multi-language', component: MultiLanguageComponent, canActivate: [AuthGuard] }, //sample route with authentication
  { path: 'multi-language', component: MultiLanguageComponent, canActivate: [AuthGuard] },
  { path: 'home', component: HomeComponent, data: { noAuthCheck: true } },
  { path: 'ghl', component: GhlComponent, canActivate: [AuthGuard] },
  { path: 'login', component: LoginComponent, canActivate: [AuthGuard] },
  { path: 'logout', component: LogoutComponent, data: { noAuthCheck: true } },
  { path: 'payment-page', component: PaymentPageComponent, canActivate: [AuthGuard] }, // No authentication required
  //{ path: 'payment-page', component: PaymentPageComponent, data: { noAuthCheck: true } }, // No authentication required
  { path: 'loading-page', component: LoadingPageComponent, canActivate: [AuthGuard] },
  { path: 'payment-response', component: PaymentResponseComponent, canActivate: [AuthGuard] },
  { path: 'payment-response-redirect', component: PaymentResponseRedirectComponent, canActivate: [AuthGuard] },
  {
    path: 'refund-tht', component: RefundTHTComponent,canActivate: [AuthGuard], data: {
      title: 'Refund Transaction History Table', breadcrumb: [{ label: 'menu.home', url: '/home' },
      {
        label: 'labels.transactionhistorytable',
        url: '/refund-tht'
      },

      ], canActivate: [AuthGuard]
    }
  },
  // {
  //   path: 'refund-chargeback', component: RefundChargebackComponent, data: {
  //     title: 'Refund Chargeback', breadcrumb: [{ label: 'menu.home', url: '/home' },
  //     {
  //       label: 'labels.transactionhistorytable',
  //       url: '/refund-tht'
  //     },

  //     ], canActivate: [AuthGuard]
  //   }
  // },

  {
    path: 'refund-direct-application', component: RefundDirectApplicationComponent,canActivate: [AuthGuard], data: {
      title: 'Direct Refund Application (Appeal)', breadcrumb: [{ label: 'menu.home', url: '/home' },
      {
        label: 'labels.transactionhistorytable',
        url: '/refund-tht'
      },
      {
        label: 'labels.directrefundapplication',
        url: '/refund-direct-application'
      }

      ], canActivate: [AuthGuard]
    }
  },  
  {
    path: 'refund-submit-bankinfo', component: RefundSubmitBankinfoComponent,canActivate: [AuthGuard], data: {
      title: 'Refund Submit Bank Info', breadcrumb: [{ label: 'menu.home', url: '/home' },
      {
        label: 'labels.transactionhistorytable',
        url: '/refund-tht'
      },
      {
        label: 'labels.refund-submit-bankinfo',
        url: '/refund-submit-bankinfo'
      }

      ], canActivate: [AuthGuard]
    }
  },
  {
    path: 'refund-refundform', component: RefundRefundformComponent,canActivate: [AuthGuard], data: {
      title: 'Refund Form', breadcrumb: [{ label: 'menu.home', url: '/home' },

      {
        label: 'labels.refundform',
        url: '/refund-refundform'
      }

      ], canActivate: [AuthGuard]
    }
  },
  // {
  //   path: 'refund-chargeback-info', component: RefundChargebackInfoComponent, data: {
  //     title: 'Refund Form', breadcrumb: [{ label: 'menu.home', url: '/home' },

  //     {
  //       label: 'labels.refundform',
  //       url: '/refund-refundform'
  //     },
  //     {
  //       label: 'labels.refundchargeback',
  //       url: '/refund-chargeback'
  //     },

  //     ], canActivate: [AuthGuard]
  //   }
  // },
  { path: 'loading-page', component: LoadingPageComponent,canActivate: [AuthGuard], data: { noAuthCheck: true } },  
  { path: 'payment-response', component: PaymentResponseComponent, canActivate: [AuthGuard] }, 
  {path:'payment-response-redirect',component:PaymentResponseRedirectComponent,canActivate: [AuthGuard]},
  // { path: '', redirectTo: '/payment-page', pathMatch: 'full' }, // Default route
  {
    path: 'catalogue-product-service', component: CatalogueProductServiceComponent, canActivate: [AuthGuard], data: {
      title: 'New Product and Services Registration in Catalogue', breadcrumb: [{ label: 'menu.home', url: '/home' },
      {
        label: 'menu.catalogueproductservice',
        url: '/catalogue-product-service'
      }
      ], 
    }
  },
  {
    path: 'catalogue-json', component: CatalogueJsonComponent, canActivate: [AuthGuard], data: {
      title: 'Catalogue JSON', breadcrumb: [{ label: 'menu.home', url: '/home' },
      {
        label: 'menu.cataloguejsongenerator',
        url: '/catalogue-json'
      }
      ], 
    }
  },
  // { path: '', redirectTo: '/payment-page', pathMatch: 'full' },  // Default route
  { path:'payment-response-redirect',component:PaymentResponseRedirectComponent,canActivate: [AuthGuard]},
  { path:'loading-home-page',component:LoadingHomePageComponent, canActivate: [AuthGuard] },
  //{ path:'billing-registration',component:BillRegistrationComponent,data:{noAuthCheck:true}},
  { path:'billing-listing',component:BillListingComponent, canActivate: [AuthGuard] },
  { path:'billing-review',component:BillReviewComponent, canActivate: [AuthGuard] },
  { path:'billing-details',component:BillingDetailsComponent, canActivate: [AuthGuard] },
  { path:'billing-cancellation-search',component:BillingCancellationSearchComponent, canActivate: [AuthGuard] },
  { path:'billing-cancellation-listing',component:BillingCancellationListingComponent, canActivate: [AuthGuard] },
  { path:'billing-cancellation',component:BillingCancellationComponent, canActivate: [AuthGuard] },
  { path:'billing-cancellation-review',component:BillCancellationReviewComponent, canActivate: [AuthGuard] },
  { path:'billing-adjustment-search',component:BillingAdjustmentSearchComponent, canActivate: [AuthGuard] },
  { path:'billing-adjustment',component:BillingAdjustmentComponent, canActivate: [AuthGuard] },
  { path:'billing-adjustment-review',component:BillAdjustmentReviewComponent, canActivate: [AuthGuard] },
  { path: '', redirectTo: '/home', pathMatch: 'full' } // Default route
];

@NgModule({
  declarations: [],
  imports: [
    CommonModule,
    RouterModule.forRoot(routes, { initialNavigation: 'enabledBlocking' })
  ],
  exports: [RouterModule]
})
export class AppRoutingModule { }
