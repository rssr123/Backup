import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Component, OnInit, ViewChild } from '@angular/core';
import { NgForm, NgModel } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { TranslateService } from '@ngx-translate/core';
import { GlobalService } from 'src/app/shared/global.service';

@Component({
  selector: 'app-role-and-permissions-configurations-add-roles',
  templateUrl: './role-and-permissions-configurations-add-roles.component.html',
  styleUrls: ['./role-and-permissions-configurations-add-roles.component.scss']
})
export class RoleAndPermissionsConfigurationsAddRolesComponent {
  @ViewChild('RoleNameRef') roleNameControl!: NgModel;

  errorMessages: string[] = [];
  error: boolean = false;
  role_nm_en: String | null = null;
  totalRecords: number = 0;
  isLoading: boolean = false;

  constructor(
    public dialogRef: MatDialogRef<RoleAndPermissionsConfigurationsAddRolesComponent>,
    private http: HttpClient,
    private translate: TranslateService,
    private globalService: GlobalService
  ) { 
    this.translate.setDefaultLang(this.globalService.getGlobalValue()); // default language
    this.translate.use(this.globalService.getGlobalValue());
  }

  ngOnInit(): void {
    this.defaultSetting();
  }

  onClose(): void {
    this.dialogRef.close();
  }

  closed(): void {
    this.dialogRef.close();
  }

  async submit() {
    this.isLoading = true;
    this.defaultSetting();

    const isValid = await this.validation();

    // false means no error, validation passed, can insert
    if (!isValid) {
      this.InsertRoles().subscribe(
        (response) => {
          this.isLoading = true;
          console.log('Success response:', response);
          this.dialogRef.close('inserted');
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
    //this.isLoading = false;

  }

  //default setting start
  defaultSetting(): void {
    this.error = false;
    // this.errorMessage="";
    this.errorMessages = [];
  }

  //default setting end
  // InsertRoles function
  InsertRoles(): Observable<any> {
    this.isLoading = true;
    const url = environment.apiUrl + '/api/RPC/v1/addroles';
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });
    const body: any = {
      i_r_role_nm_en: this.role_nm_en,
    };

    // Return the observable
    return this.http.post(url, body, { headers });
  }


  //insert Start
  // InsertRoles(): void {
  //   const url = environment.apiUrl + '/api/RPC/v1/addroles';

  //   const headers = new HttpHeaders({
  //     Authorization: environment.authKey,
  //     'Content-Type': 'application/json',
  //   });

  //   const body: any = {
  //     i_r_role_nm_en: this.role_nm_en,
  //   };

  //   try {
  //     this.http
  //       .post(url, body, { headers })
  //       .toPromise()
  //       .then((response) => {
  //         console.log('Success response:', response);
  //       })
  //       .catch((error) => {
  //         console.error('Error:', error);
  //       });
  //   } catch (error) {
  //     console.error(error);
  //   }
  // }
  //insert End


  //validation start
  checkRequiredField(): Boolean {
    if (this.roleNameControl.invalid) {
      this.error = true;
      this.errorMessages.push('Role Name is required');
    }

    if (this.error) {
      return true;
    }
    else {
      return false;
    }
  }

  async validation(): Promise<boolean> {

    if (!this.role_nm_en) {
      // Form is not valid, you can handle this case or simply return
      return true;
    }

    const url = environment.apiUrl + '/api/rms/v1/getroles';

    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    const body: any = {
      i_page: '1',
      i_size: '1',
      i_r_role_nm_en: this.role_nm_en,
    };

    try {
      const response: any = await this.http
        .post(url, body, { headers })
        .toPromise();

      if (response.header.statusCode === '01') {
        return false;
      } else {
        this.error = true;
        this.errorMessages.push('The user role name you have entered already exists.');
        return true;
      }
    } catch (error) {
      this.error = true;
      this.errorMessages.push('Internal Server Error.');
      console.error(error);
      return true;
    }
  }
  //validation end

  //form handle before submit start
  handleFormSubmit(form: NgForm) {
    if (form.valid) {
      this.submit();
    } else {
      Object.values(form.controls).forEach((control) => {
        control.markAsTouched();
      });
    }
  }
  //form handle before submit end

}
