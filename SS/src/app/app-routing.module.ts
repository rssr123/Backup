import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { SssampleScreenComponent } from './sssample-screen/sssample-screen.component';
import { SsstatusComponent } from './ssstatus/ssstatus.component';
import { SsbilpayComponent } from './ssbilpay/ssbilpay.component';
import { SsbilpaystatusComponent } from './ssbilpaystatus/ssbilpaystatus.component';
import { WhitelistScreenComponent } from './whitelist-screen/whitelist-screen.component';


const routes: Routes = [
  { path: '', redirectTo: '/sssample', pathMatch: 'full' },
  { path: 'sssample', component: SssampleScreenComponent },
  { path: 'ssstatus', component: SsstatusComponent },
  { path: 'ssbilpay', component: SsbilpayComponent },
  { path: 'ssbilpaystatus', component: SsbilpaystatusComponent },
  { path: 'whitelist-screen', component: WhitelistScreenComponent },
  // Add additional routes here
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }