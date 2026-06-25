import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';


import { AppComponent } from './app.component';
import { CRSSampleScreenComponent } from './crssample-screen/crssample-screen.component';
import { HTTP_INTERCEPTORS, HttpClientModule } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { CrsstatusComponent } from './crsstatus/crsstatus.component';
import { AppRoutingModule } from './app-routing.module';
import { OtcPageComponent } from './otc-page/otc-page.component';
import { NonceInterceptor } from './nonce.interceptor';


@NgModule({
  declarations: [
    AppComponent,
    CRSSampleScreenComponent,
    CrsstatusComponent,
    OtcPageComponent
  ],
  imports: [
    BrowserModule,
FormsModule,
    HttpClientModule,
    AppRoutingModule
  ],
  providers: [
    { provide: HTTP_INTERCEPTORS, useClass: NonceInterceptor, multi: true }
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
