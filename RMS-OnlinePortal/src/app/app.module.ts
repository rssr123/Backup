import { HttpClient, HttpInterceptor, HTTP_INTERCEPTORS, provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { BrowserModule } from '@angular/platform-browser';
import { MatDialogModule } from '@angular/material/dialog';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { BsDatepickerModule } from 'ngx-bootstrap/datepicker';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { APP_INITIALIZER, NgModule } from '@angular/core';
import { TranslateModule, TranslateLoader } from '@ngx-translate/core';
import { TranslateHttpLoader } from '@ngx-translate/http-loader';
import { MultiLanguageComponent } from './multi-language/multi-language.component';
import { NgxBootstrapIconsModule, allIcons } from 'ngx-bootstrap-icons';
import { RouterModule, Routes } from '@angular/router';

import { NgSelectModule } from '@ng-select/ng-select';
import { MatIconModule } from '@angular/material/icon';
import { ButtonModule } from 'primeng/button';
import { ConfirmDialogModule } from 'primeng/confirmdialog';
import { ConfirmPopupModule } from 'primeng/confirmpopup';
import { RippleModule } from 'primeng/ripple';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { DynamicDialogModule } from 'primeng/dynamicdialog';

import { MatSelectModule } from '@angular/material/select';
import { NgMultiSelectDropDownModule } from 'ng-multiselect-dropdown';
import { CommonModule, DatePipe } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { MAT_DATE_LOCALE, MatNativeDateModule } from '@angular/material/core';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { NgxDaterangepickerMd } from 'ngx-daterangepicker-material';
import { NgChartsModule } from 'ng2-charts';
import { TableModule } from 'primeng/table';

import { AppComponent } from './app.component';
import { HomeComponent } from './home/home.component';
import { LoginComponent } from './login/login.component';
import { FooterComponent } from './footer/footer.component';
import { HeaderComponent } from './header/header.component';
import { CustomInterceptor } from './shared/auth.interceptor';
import { LogoutComponent } from './logout/logout.component';
import { LoadingPageComponent } from './loading-page/loading-page.component';
import { PaymentPageComponent } from './payment-page/payment-page.component';
import { FormsModule } from '@angular/forms';
import { GhlComponent } from './ghl/ghl.component';
import { PaymentResponseComponent } from './payment-response/payment-response.component';
import { AppRoutingModule } from './route/app-routing.module';
import { AuthGuard } from './guard/auth.guard';
import { AuthService } from './services/auth.service';
import { PaymentResponseRedirectComponent } from './payment-response-redirect/payment-response-redirect.component';
import { CatalogueProductServiceComponent } from './catalogue-product-service/catalogue-product-service.component';
import { PaginatorModule } from 'primeng/paginator';
import { NgbPaginationModule } from '@ng-bootstrap/ng-bootstrap';
import { CatalogueJsonComponent } from './catalogue-json/catalogue-json.component';
import { RefundTHTComponent } from './refund/refund-tht/refund-tht.component';
import { RefundChargebackComponent } from './refund/refund-chargeback/refund-chargeback.component';
import { RefundRefundformComponent } from './refund/refund-refundform/refund-refundform.component';
import { RefundDirectApplicationComponent } from './refund/refund-direct-application/refund-direct-application.component';
import { RefundSubmitBankinfoComponent } from './refund/refund-task-list/refund-submit-bankinfo/refund-submit-bankinfo.component';
import { RefundChargebackInfoComponent } from './refund/refund-chargeback-info/refund-chargeback-info.component';
import { LoadingHomePageComponent } from './loading-home-page/loading-home-page.component';

import { BillRegistrationComponent } from 'src/app/billing/billing-registration/billing-registration.component';
import { BillListingComponent } from 'src/app/billing/billing-listing/billing-listing.component';
import { BillReviewComponent } from 'src/app/billing/billing-review/billing-review.component';
import { BillingDetailsComponent } from 'src/app/billing/billing-details/billing-details.component';
import { BillingCancellationSearchComponent } from 'src/app/billing/billing-cancellation-search/billing-cancellation-search.component';
import { BillingCancellationListingComponent } from 'src/app/billing/billing-cancellation-listing/billing-cancellation-listing.component';
import { BillingCancellationComponent } from 'src/app/billing/billing-cancellation/billing-cancellation.component';
import { BillCancellationReviewComponent } from 'src/app/billing/billing-cancellation-review/billing-cancellation-review.component';
import { BillingAdjustmentSearchComponent } from 'src/app/billing/billing-adjustment-search/billing-adjustment-search.component';
import { BillingAdjustmentComponent } from 'src/app/billing/billing-adjustment/billing-adjustment.component';
import { BillAdjustmentReviewComponent } from 'src/app/billing/billing-adjustment-review/billing-adjustment-review.component';
import { LoadingOverlayComponent } from './components/loading-overlay/loading-overlay.component';
import { FilterPipe } from './filter.pipe';
// import { RetryInterceptor } from 'src/retry.interceptor';
import { NonceInterceptor } from './services/nonce.interceptor';
import { TimeoutWarningComponent } from './shared/timeout-warning/timeout-warning.component';


export function HttpLoaderFactory(http: HttpClient) {
    return new TranslateHttpLoader(http, './assets/i18n/', '.json');
}



@NgModule({
    declarations: [
        AppComponent,
        HomeComponent,
        LoginComponent,
        MultiLanguageComponent,
        FooterComponent,
        HeaderComponent,
        LogoutComponent,
        LoadingPageComponent,
        PaymentPageComponent,
        GhlComponent,
        PaymentResponseComponent,
        PaymentResponseRedirectComponent,
        CatalogueProductServiceComponent,
        CatalogueJsonComponent,
        RefundTHTComponent,
        RefundChargebackComponent,
        RefundRefundformComponent,
        RefundChargebackInfoComponent,
        RefundDirectApplicationComponent,
        RefundSubmitBankinfoComponent,
        LoadingHomePageComponent,
        BillRegistrationComponent,
        BillListingComponent,
        BillReviewComponent,
        BillingDetailsComponent,
        BillingCancellationSearchComponent,
        BillingCancellationListingComponent,
        BillingCancellationComponent,
        BillCancellationReviewComponent,
        BillingAdjustmentSearchComponent,
        BillingAdjustmentComponent,
        BillAdjustmentReviewComponent,
        LoadingOverlayComponent,
        FilterPipe,
        TimeoutWarningComponent,

    ],
    exports: [RouterModule],
    bootstrap: [AppComponent], imports: [MatDialogModule,
        ReactiveFormsModule,
        BrowserModule,
        NgxBootstrapIconsModule.pick(allIcons),
        TranslateModule.forRoot({
            loader: {
                provide: TranslateLoader,
                useFactory: HttpLoaderFactory,
                deps: [HttpClient]
            }
        }),
        TableModule,
        FormsModule,
        AppRoutingModule,
        NgSelectModule,
        PaginatorModule,
        NgbPaginationModule,
        NgbModule,
        BsDatepickerModule.forRoot(),
        MatDialogModule,
        MatIconModule,
        BsDatepickerModule.forRoot(),
        NgxDaterangepickerMd.forRoot({ separator: ' to ' }),
        BrowserAnimationsModule,
        NgbModule,
        ButtonModule,
        ConfirmDialogModule,
        ConfirmPopupModule,
        RippleModule,
        TableModule,
        MatCheckboxModule,
        DynamicDialogModule,
        PaginatorModule,
        MatSelectModule,
        NgMultiSelectDropDownModule,
        CommonModule,
        ReactiveFormsModule,
        MatNativeDateModule,
        MatDatepickerModule,
        MatFormFieldModule,
        MatInputModule,
        NgChartsModule], providers: [{
            provide: APP_INITIALIZER,
            useFactory: (authService: AuthService) => () => authService.initializeApp(),
            deps: [AuthService],
            multi: true
        }, 
        // { provide: HTTP_INTERCEPTORS, useClass: RetryInterceptor, multi: true },
        { provide: HTTP_INTERCEPTORS, useClass: CustomInterceptor, multi: true, },
        { provide: MAT_DATE_LOCALE, useValue: 'en-GB' },
        { provide: HTTP_INTERCEPTORS, useClass: NonceInterceptor, multi: true },

            DatePipe, AuthGuard, AuthService, provideHttpClient(withInterceptorsFromDi())]
    //,bootstrap: [AppComponent]
})
export class AppModule { }