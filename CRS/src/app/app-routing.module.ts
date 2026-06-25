import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { CRSSampleScreenComponent } from './crssample-screen/crssample-screen.component';
import { CrsstatusComponent } from './crsstatus/crsstatus.component';
import { OtcPageComponent } from './otc-page/otc-page.component';

const routes: Routes = [
  { path: '', redirectTo: '/crssample', pathMatch: 'full' },
  { path: 'crssample', component: CRSSampleScreenComponent },
  { path: 'crsstatus', component: CrsstatusComponent },
  { path: 'otc/:message', component: OtcPageComponent}
  // Add additional routes here
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }