import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { TranslateService } from '@ngx-translate/core';
import { environment } from '../../../environments/environment';
import { GlobalService } from '../../shared/global.service';
import { AuthService } from '../../core/services/auth.service';
import { Component, OnInit, AfterViewInit, ViewChild, ElementRef, Renderer2, ChangeDetectorRef, HostListener } from '@angular/core';
import { Observable } from 'rxjs';
import { Router } from '@angular/router';
import { animate, state, style, transition, trigger } from '@angular/animations';
import { CounterCheckInStatus } from 'src/app/core/services/otc-counter-status.service';
import { perm } from 'src/permissions/perm';

@Component({
  selector: 'app-otc',
  templateUrl: './otc-checkout.component.html',
  styleUrls: ['./otc-checkout.component.scss']
})
export class OTCCheckOutComponent implements OnInit {
  @ViewChild('logoImage') logoImage!: ElementRef;
  @ViewChild('labels') labels!: ElementRef;

  isCheckedIn: boolean = false;
  counter_id: string = "";
  otc_counter_id: number = 0;

  permCheck = perm.OTC_Check_Out;
  permAllow = ""; // variable to store allowed permission for the user
  permCheckOutAllow: number = 0;

  constructor(private translate: TranslateService, private http: HttpClient, public authService: AuthService, private router: Router, private elementRef: ElementRef, private renderer: Renderer2,
    private cdref: ChangeDetectorRef, private cd: ChangeDetectorRef,
    public counterCheckInStatus: CounterCheckInStatus) {
    counterCheckInStatus.counterIdChanged.subscribe(counterId=>this.updatedCounterId(counterId));
  }

  updatedCounterId(counterId: any){
    if(counterId?.counter_id && (counterId.counter_id + '').length > 0){
      this.isCheckedIn = true;
      this.counter_id = counterId;
      this.otc_counter_id = counterId.otc_counter_id;
      this.showResultAlertBox();
    }
  }
  
  async ngOnInit(): Promise<any> {
    this.loadPermission();
    this.waitForElm('#close').then((elm) => {
      var checkOutList = <HTMLElement>(Array.from(document.querySelectorAll('a')).find(el => el.innerText == 'Counter Check Out'));
      if(checkOutList != null)
        checkOutList.onclick = (e) =>{this.showResultAlertBox();};
    });
  }

  buttonPerms(){
    if(this.isCheckedIn && this.counter_id.length > 0 && this.permCheckOutAllow == 1)
      return false;
    else
      return true;
  }

  showResultAlert = false;

  showResultAlertBox() {
    this.showResultAlert = true;
    var tmp = <HTMLElement>document.getElementsByTagName('app-root')[0];
    tmp.style.pointerEvents = 'none';
    document.getElementById("mainContainerOtc")!.style.pointerEvents = 'none';
    document.getElementById("overlayout")!.style.display = 'block';
  }

  closeResultAlertBox() {
    this.showResultAlert = false;
    var tmp = <HTMLElement>document.getElementsByTagName('app-root')[0];
    tmp.style.pointerEvents = 'all';
    document.getElementById("mainContainerOtc")!.style.pointerEvents = 'all';
    document.getElementById("overlayout")!.style.display = 'none';
    //if(environment.production)
    //  location.href = 'home';
    this.router.navigate(['/home']);
  }

  checkOut(){
    (<HTMLButtonElement>document.getElementById("check-out"))!.disabled = true;
    (<HTMLButtonElement>document.getElementById("close"))!.disabled = true;
    const url = environment.apiUrl + '/api/otc/v1/checkout';
    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json'
    });

    // Create the request body with your form data
    var requestBody: {[k: string]: any} = {
      i_counter_id: this.counter_id,
      i_session_id: localStorage.getItem('otcSession')
    };
    this.http.post(url, requestBody, { headers }).subscribe((response: any) => {
      if(response.data && response.data.length > 0)
        if(response.data == 'counterbalancing'){
          var counter_id = this.counter_id;
          var otc_counter_id = this.otc_counter_id;

          var debugFlag = localStorage.getItem('debug') == 'true' ? true : false;
          if(debugFlag){
            console.log('Debug');
            console.log(response.data);
            console.log(counter_id);
            console.log(otc_counter_id);
            console.log('Navigating to counter-balancing-listing with:', {
              counter_id: this.counter_id,
              otc_counter_id: this.otc_counter_id
            });
            alert("Debug: Goto counterbalancing");
          }
          //this.router.navigate(['/counter-balancing-listing'], { state: { counter_id, otc_counter_id} });
          // location.href = 'counter-balancing-listing';
         // this.router.navigate(['/counter-balancing-listing']);
         this.router.navigate(['/counter-balancing-listing'], {
          state: {
            counter_id: this.counter_id,
            otc_counter_id: this.otc_counter_id,
            flag: false
            }
          });

        }
        else if(response.data == 'checkout'){
          localStorage.removeItem('otcSession');
          // location.href = 'home'; //go to other page
          this.router.navigate(['/home']);
        }else{
        console.log('Checkout failed:' + response.data);
        this.closeResultAlertBox();
        }
    },
      (error: any) => {
        console.error(error);
        this.closeResultAlertBox();
      });
  }

  loadPermission() {
    this.authService.checkUserRole(this.authService.username, this.permCheck)
      .subscribe(
        (response: any) => {
          this.permAllow = response.data;
          this.permCheckOutAllow = this.permAllow.includes(perm.OTC_Check_Out) ? 1 : 0;
          if (this.permCheckOutAllow === 0) {
            if(environment.production)
              this.router.navigate(['/access-denied']);
            console.log("access-denied");
            console.log(this.permAllow);
            alert('bad permission');
            return; // Exit the function to prevent further execution
          }

          if(this.counterCheckInStatus.counterId != null)
            if(this.counterCheckInStatus.counterId.counter_id.length > 0){
              this.isCheckedIn = true;
              this.counter_id = this.counterCheckInStatus.counterId.counter_id;
              this.otc_counter_id = this.counterCheckInStatus.counterId.otc_counter_id;
              this.showResultAlertBox();
            }
        },
        (error: any) => {
            //if(!environment.production)
              //alert('permission load failed');
            console.log("access-denied");
            console.log(error);
            this.router.navigate(['/access-denied']);
        }
      );
  }

  waitForElm(selector: any) {
    return new Promise(resolve => {
        if (document.querySelector(selector)) {
            return resolve(document.querySelector(selector));
        }

        const observer = new MutationObserver(mutations => {
            if (document.querySelector(selector)) {
                observer.disconnect();
                resolve(document.querySelector(selector));
            }
        });

        // If you get "parameter 1 is not of type 'Node'" error, see https://stackoverflow.com/a/77855838/492336
        observer.observe(document.body, {
            childList: true,
            subtree: true
        });
    });
  }
}
