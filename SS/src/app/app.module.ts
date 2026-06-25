import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';


import { AppComponent } from './app.component';
import { HTTP_INTERCEPTORS,HttpClientModule } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { SssampleScreenComponent } from './sssample-screen/sssample-screen.component';
import { AppRoutingModule } from './app-routing.module';
import { SsstatusComponent } from './ssstatus/ssstatus.component';
import { SsbilpayComponent } from './ssbilpay/ssbilpay.component';
import { SsbilpaystatusComponent } from './ssbilpaystatus/ssbilpaystatus.component';
import { WhitelistScreenComponent } from './whitelist-screen/whitelist-screen.component';
import { NonceInterceptor } from './nonce.interceptor';


@NgModule({
  declarations: [
    AppComponent,
    SssampleScreenComponent,
    SsstatusComponent,
    SsbilpayComponent,
    SsbilpaystatusComponent,
    WhitelistScreenComponent

  ],
  imports: [
    BrowserModule,
FormsModule,
    HttpClientModule,
    AppRoutingModule
  ],
  providers: [
    { provide: HTTP_INTERCEPTORS, useClass: NonceInterceptor, multi: true }],
  bootstrap: [AppComponent]
})
export class AppModule { }
