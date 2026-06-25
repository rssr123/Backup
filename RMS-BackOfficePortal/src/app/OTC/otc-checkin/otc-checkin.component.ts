import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { TranslateService } from '@ngx-translate/core';
import { environment } from '../../../environments/environment';
import { GlobalService } from '../../shared/global.service';
import { AuthService } from '../../core/services/auth.service';
import { Component, OnInit, AfterViewInit, ViewChild, ElementRef, Renderer2, ChangeDetectorRef, HostListener, OnDestroy } from '@angular/core';
import { Observable, Subscription } from 'rxjs';
import { Router, ActivatedRoute, NavigationEnd } from '@angular/router';
import { animate, state, style, transition, trigger } from '@angular/animations';
import { CounterCheckInStatus } from 'src/app/core/services/otc-counter-status.service';
import { perm } from 'src/permissions/perm';
import { filter } from 'rxjs/operators';

@Component({
  selector: 'app-otc',
  templateUrl: './otc-checkin.component.html',
  styleUrls: ['./otc-checkin.component.scss']
})
export class OTCCheckInComponent implements OnInit {
  @ViewChild('logoImage') logoImage!: ElementRef;
  @ViewChild('labels') labels!: ElementRef;

  isCheckedIn: boolean = true;
  branch_code: string = '';
  counter_id: string = '';
  branchCounterMap: any;
  counterList: string[] = [];
  branchList: string[] = [];
  counter_id_list_flag: boolean = false;
  branch_code_list_flag: boolean = false;
  timestamp_now: Date = new Date();
  errorFlag: number = 0;
  error_msg1: string = '';
  error_counter: string = '';
  error_user: string = '';
  error_msg2: string = '';
  
  permCheck = perm.OTC_Check_In;
  permAllow = ""; // variable to store allowed permission for the user
  permCheckInAllow: number = 0;

  constructor(private translate: TranslateService, private http: HttpClient, public authService: AuthService, 
    private router: Router, private elementRef: ElementRef, private renderer: Renderer2,
    private cdref: ChangeDetectorRef, private cd: ChangeDetectorRef,
    public counterCheckInStatus: CounterCheckInStatus, public route: ActivatedRoute) {
    counterCheckInStatus.counterIdChanged.subscribe(counterId=>this.updatedCounterId(counterId));
  }

  updatedCounterId(counterId: any){
    if(counterId.counter_id == null || counterId.counter_id == '' || counterId.counter_id.length < 1)
      this.isCheckedIn = false;
    else
      this.isCheckedIn = true;
  }
  
  ngOnInit() {
    this.loadPermission();
    this.fetchBranchListingData();
    if(localStorage.getItem('otcSession') != null || localStorage.getItem('otcSession') != '')
      this.checkSession();
    else
      this.isCheckedIn = false;
  }
  
  checkSession() {
    const permUrl = environment.apiUrl + '/api/otc/v1/checkinstatus';
    // Make the HTTP GET request
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json'
    });
    var requestBody: { [k: string]: any } = {
      i_session_id: localStorage.getItem('otcSession')
    };
    this.http.post(permUrl, requestBody, { headers }).subscribe(
      (response: any) => {
        if (response.data.counter_id == null || response.data.counter_id.length < 1) 
          this.isCheckedIn = false;
      },
      (error) => {
        this.isCheckedIn = false;
      });
  }

  fetchBranchListingData() {
    const url = environment.apiUrl + '/api/otc/v1/checkinlist';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/x-www-form-urlencoded'
    });

    // Make the HTTP GET request
    this.http.get(url, {headers : headers}).subscribe(
      (response:any) => {
        this.branchCounterMap = response.data;
        for(var prop in this.branchCounterMap){
          this.branchList.push(prop);
        }
        this.branch_code = this.branchList[0];
        this.counterList = this.branchCounterMap[this.branch_code].counters;
        this.counter_id = this.counterList[0];
      },
      (error: any) => {
        console.error(error);
      }
    );
  }

  updateCounterList(branch_code: string){
    this.counterList = this.branchCounterMap[branch_code].counters;
    this.counter_id = this.counterList[0];
  }

  buttonPerms(){
    if(!this.isCheckedIn && this.counter_id.length > 0 && this.permCheckInAllow == 1)
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
    document.getElementById("overlay")!.style.display = 'block';
    (<HTMLButtonElement>document.getElementById("checkIn"))!.disabled = true;
    this.counter_id_list_flag = true;
    this.branch_code_list_flag = true;
  }

  closeResultAlertBox() {
    this.showResultAlert = false;
    var tmp = <HTMLElement>document.getElementsByTagName('app-root')[0];
    tmp.style.pointerEvents = 'all';
    document.getElementById("mainContainerOtc")!.style.pointerEvents = 'all';
    document.getElementById("overlay")!.style.display = 'none';
    (<HTMLButtonElement>document.getElementById("checkIn"))!.disabled = false;
    this.counter_id_list_flag = false;
    this.branch_code_list_flag = false;
  }

  checkIn(){
    (<HTMLButtonElement>document.getElementById("checkIn"))!.disabled = true;
    this.counter_id_list_flag = true;
    this.branch_code_list_flag = true;
    const url = environment.apiUrl + '/api/otc/v1/checkin';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json'
    });

    // Create the request body with your form data
    var requestBody: {[k: string]: any} = {
      i_counter_id: this.counter_id,
      i_branch_cd: this.branchCounterMap[this.branch_code].branch_cd
    };

    this.http.post(url, requestBody, { headers }).subscribe((response: any) => {
      if (response.data && response.data.length > 0) {
        localStorage.setItem('otcSession', response.data);
        location.reload();
      }
    },
      (error: any) => {
        console.error(error);
        if(error.error.data != null){
          var errorInfo = error.error.data;
          if(Object.keys(errorInfo)[0] == 'counter_id' || Object.keys(errorInfo)[1] == 'counter_id'){
            this.errorFlag = 1;
            this.error_user = this.authService.username;
            //this.error_msg1 = 'Check-In for user ' + this.error_user + ' is unavailable. The user is already checked in.';
            this.timestamp_now = errorInfo['check_in'].replace('T', ' ');
            this.error_counter = errorInfo['counter_id'];
            //this.error_msg2 = 'Please ask supervisor to perform Counter Balancing to check-out at ' + this.error_counter;
          }
          else if (Object.keys(errorInfo)[0] == 'user_id' || Object.keys(errorInfo)[1] == 'user_id'){
            this.errorFlag = 2;
            //this.error_msg1 = 'Check-In for ' + this.counter_id + ' is unavailable. The selected counter is currently Open.';
            this.timestamp_now = errorInfo['check_in'].replace('T', ' ');
            this.error_user = errorInfo['user_id'];
            this.error_counter = this.counter_id;            
            //this.error_msg2 = 'Please perform Counter Balancing to check-out at ' + this.error_counter;
          }
          else if (Object.keys(errorInfo)[0] == 'check_in_blocked' || Object.keys(errorInfo)[1] == 'check_in_blocked')
            this.errorFlag = 3;
        }
        this.showResultAlertBox();
      });
  }

  loadPermission() {
    this.authService.checkUserRole(this.authService.username, this.permCheck)
      .subscribe(
        (response: any) => {
          this.permAllow = response.data;          
          this.permCheckInAllow = this.permAllow.includes(perm.OTC_Check_In) ? 1 : 0;
          if (this.permCheckInAllow === 0) {
            if(environment.production)
              this.router.navigate(['/access-denied']);
            console.log("access-denied");
            console.log(this.permAllow);
            alert('bad permission');
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
}
