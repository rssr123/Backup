import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Component, Inject, OnInit, ViewChild, EventEmitter, Output } from '@angular/core';
import { NgForm, NgModel } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { environment } from 'src/environments/environment';
import { Systemstatus } from '../../shared/enums/systemstatus';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-role-and-permissions-configurations-update-role-status',
  templateUrl: './role-and-permissions-configurations-update-role-status.component.html',
  styleUrls: ['./role-and-permissions-configurations-update-role-status.component.scss']
})
export class RoleAndPermissionsConfigurationsUpdateRoleStatusComponent {
  @ViewChild('roleNameRef') roleNameControl!: NgModel;

  errorMessages: string[] = [];
  error: boolean = false;

  roleName: String | null = null;
  status: any | null = null;

  isLoading: boolean = false;

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: any,
    public dialogRef: MatDialogRef<RoleAndPermissionsConfigurationsUpdateRoleStatusComponent>,
    private http: HttpClient,
  ) { }

  ngOnInit(): void {
    this.defaultSetting();
    this.roleName = this.data.i_role_nm_en;
    this.status = this.data.i_status;
    console.log(this.status);
  }

  onClose(): void {
    this.dialogRef.close();
  }

  closed(): void {
    this.dialogRef.close();
  }


  async update() {
    this.isLoading = true;
    this.defaultSetting();
    // this.dialogRef.close();
    const isValid = await this.validation();
  
    if (!isValid) {
      this.UpdateRoleStatus().subscribe(
        (response) => {
          console.log('Success response:', response);
          this.dialogRef.close('updated');
        },
        (error) => {
          console.error('Error:', error);
          // Set isLoading to false in case of an error
          this.isLoading = false;
        }
      );
    } else {
      // Set isLoading to false if validation fails
      this.isLoading = false;
    }
  }
  
  

  //default setting start
  defaultSetting(): void {
    this.error = false;
    this.errorMessages = [];
  }

  //default setting end

  //update Start
  UpdateRoleStatus(): Observable<any> {
    this.isLoading = true;
    const url = environment.apiUrl + '/api/RPC/v1/updrolestatus';

    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    const body: any = {
      i_r_role_nm_en: this.roleName,
    };
    this.isLoading = true;
    return this.http.post(url, body, { headers });

    // try {
    //   this.http
    //     .post(url, body, { headers })
    //     .toPromise()
    //     .then((response) => {
    //       console.log('Success response:', response);
    //     })
    //     .catch((error) => {
    //       console.error('Error:', error);
    //     });
    // } catch (error) {
    //   console.error(error);
    // }
  }
  //insert End
  //validation end

  //form handle before submit start
  handleFormSubmit(form: NgForm) {
    if (form.valid) {
      this.update();
    } else {
      Object.values(form.controls).forEach((control) => {
        control.markAsTouched();
      });
    }
  }

  async validation(): Promise<boolean> {
    if(!this.status){
      return false;
    }

    if (!this.roleName) {
      return true;
    }

    const url = environment.apiUrl + '/api/UR/v1/getuserrole';

    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    const body: any = {
      i_page: '1',
      i_size: '1',
      i_user_role: this.roleName,
    };

    try {
      const response: any = await this.http
        .post(url, body, { headers })
        .toPromise();

      if (response.header.statusCode === '00') {
        this.error = true;
        this.errorMessages.push('There are users currently assigned to this user role.');
        return true;
      } else {
        return false;
      }
    } catch (error) {
      this.error = true;
      this.errorMessages.push('Internal Server Error.');
      console.error(error);
      return true;
    }
  }
  //validation end

}