import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Component , Inject} from '@angular/core';
import { environment } from 'src/environments/environment';
import { MAT_DIALOG_DATA, MatDialog, MatDialogRef } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { AuthService } from 'src/app/core/services/auth.service';
import { GlobalService } from 'src/app/shared/global.service';
import { OTCDailyBal } from 'src/app/core/models/otc-daily-bal';

@Component({
  selector: 'app-daily-balancing-warning',
  templateUrl: './daily-balancing-warning.component.html',
  styleUrls: ['./daily-balancing-warning.component.scss']
})
export class DailyBalancingWarningComponent {
  constructor(
    private http: HttpClient,
    private router: Router,
    public dialogRef: MatDialogRef<DailyBalancingWarningComponent>,
    private translateService: TranslateService,
    private globalService: GlobalService,
    private authService: AuthService,
    @Inject(MAT_DIALOG_DATA) public data: any,
  ) {
    this.translateService.setDefaultLang(this.globalService.getGlobalValue());
    this.translateService.use(this.globalService.getGlobalValue());
  }

  modelValidation: OTCDailyBal[] = [];
  o_result: number = 999;

  isDisplay: boolean = false;
  isLoading: boolean = false;

  confirm():void{
        // Set your authorization header
        const headers = new HttpHeaders({
          Authorization: environment.authKey,
          'Content-Type': 'application/json',
        });
    
        this.isDisplay = true;
        this.isLoading = true;
        const url = environment.apiUrl + '/api/otcdailybal/v1/checkotcdailybalval';
    
        const Body: any = {
          branch_code: this.data.bc,
          bal_date: this.data.bd,
        };
    
        this.http.post(url, Body, { headers }).subscribe(
          (response: any) => {
            if (response.data.length == 0) {
              this.isDisplay = true;
              this.isLoading = false;
            } else {
              this.isLoading = false;
              this.o_result = response.data;
              if(this.o_result === 0)
                {
                  this.dialogRef.close({notification: false});
                }
                else{
                  this.dialogRef.close({notification: true});
                }
            }
          },
          (error) => {
            console.error(error);
            this.isLoading = false;
            // Handle errors here
          }
        ); 
  }

  cancel():void{
    this.dialogRef.close({notification: false});
  }
}
