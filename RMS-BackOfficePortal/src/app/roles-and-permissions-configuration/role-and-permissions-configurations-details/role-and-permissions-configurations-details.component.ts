import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Router } from '@angular/router';
import { NgbPaginationConfig } from '@ng-bootstrap/ng-bootstrap';
import { environment } from 'src/environments/environment';
import { MatDialog } from '@angular/material/dialog';
import { RoleAndPermissionsConfigurationsAddRolesComponent } from '../role-and-permissions-configurations-add-roles/role-and-permissions-configurations-add-roles.component';
import { Systemstatus } from '../../shared/enums/systemstatus';
import { ParamService } from '../../core/services/param.service';
import { PermissionByID, Permissions, Roles, rolePerm } from 'src/app/core/models/entity';
import { RoleAndPermissionsConfigurationsUpdateRoleStatusComponent } from '../role-and-permissions-configurations-update-role-status/role-and-permissions-configurations-update-role-status.component';
import { RoleAndPermissionsConfigurationsDiscardChangesComponent } from '../role-and-permissions-configurations-discard-changes/role-and-permissions-configurations-discard-changes.component';
import { forkJoin, switchMap } from 'rxjs';
import { TranslateService } from '@ngx-translate/core';
import { GlobalService } from 'src/app/shared/global.service';
import { perm } from 'src/permissions/perm';
import { AuthService } from 'src/app/core/services/auth.service';


@Component({
  selector: 'app-role-and-permissions-configurations-details',
  templateUrl: './role-and-permissions-configurations-details.component.html',
  styleUrls: ['./role-and-permissions-configurations-details.component.scss']
})

export class RoleAndPermissionsConfigurationsDetailsComponent {
  page = environment.DefaultPage;
  itemsPerPage = environment.ItemPerPage;
  dropDownSize = environment.DropDownSize;
  isDisplay: boolean = false;
  isLoading: boolean = false;
  isDataLoading: boolean = false;
  updatedStatus: boolean | null = null;
  totalRecords: number = 0;
  editBox: boolean = false;
  addBox: boolean = false;
  deleteBox: boolean = false;
  rightSectionCollapsed: boolean = false;
  added: boolean = false;
  changeStatus: boolean = false;

  Role: String | null = null;
  modelPermission: Permissions[] = [];

  permRPC = perm.Roles_and_Permissions_Configuration_View_Roles_and_Permissions_Configuration_Page + "," + perm.Roles_and_Permissions_Configuration_Add_User_Role + "," + perm.Roles_and_Permissions_Configuration_Deactivate_User_Role+ "," + perm.Roles_and_Permissions_Configuration_Edit_User_Role_Name_Permissions;
  permRPCAllow = "";
  permListAllow: number = 0;
  permAddRoleAllow: number = 0;
  permDeactivateAllow: number = 0;
  permEditPermAllow: number = 0;

  moduleArray = [];
  permissionArray = [];
  roleOption: Roles[] = []
  permissionWithID: PermissionByID[] = [];
  rolePermission: rolePerm[] = [];

  DefaultBox() {
    this.editBox = false;
    this.addBox = false;
    this.deleteBox = false;
  }

  AlertBoxInitialize() {
    if (this.editBox) {
      this.showUpdateAlertBox();
    } else if (this.addBox) {
      this.showInsertAlertBox();
    } else if (this.deleteBox) {
      this.showDeleteAlertBox();
    }
  }

  constructor(
    private http: HttpClient,
    config: NgbPaginationConfig,
    private router: Router,
    public dialog: MatDialog,
    private ParamService: ParamService,
    private cd: ChangeDetectorRef,
    private translate: TranslateService,
    private globalService: GlobalService,
    private authService: AuthService) 
  {
    config.maxSize = environment.PaginationMaxSize;
    config.boundaryLinks = true;
    this.translate.setDefaultLang(this.globalService.getGlobalValue()); // default language
    this.translate.use(this.globalService.getGlobalValue());
  }

  //for alert box start
  showInsertAlert = false;

  showInsertAlertBox() {
    this.showInsertAlert = true;
    setTimeout(() => (this.showInsertAlert = false), 2000);
  }

  showUpdateAlert = false;

  showUpdateAlertBox() {
    this.showUpdateAlert = true;
    setTimeout(() => (this.showUpdateAlert = false), 2000);
  }

  showDeleteAlert = false;

  showDeleteAlertBox() {
    this.showDeleteAlert = true;
    setTimeout(() => (this.showDeleteAlert = false), 2000);
  }

  showResultAlert = false;

  showResultAlertBox() {
    this.showResultAlert = true;
    setTimeout(() => (this.showResultAlert = false), 2000);
  }

  showDeactiveAlert = false;

  showDeactiveAlertBox() {
    this.showDeactiveAlert = true;
    setTimeout(() => (this.showDeactiveAlert = false), 2000);
  }

  //for alert box end
  ngOnInit(): void {
    this.Role = null;
    this.populateRoles();
    this.getPermissionId();
    //this.loadStates();
    // load data must be place at last
    this.loadData();
  }

  loadData() {
    console.log(this.Role);
    this.isDisplay = true;
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    this.authService.checkUserRole(this.authService.username, this.permRPC)
    .subscribe(
      (response: any) => {
        this.permRPCAllow = response.data;
        this.permListAllow = this.permRPCAllow.includes(perm.Roles_and_Permissions_Configuration_View_Roles_and_Permissions_Configuration_Page) ? 1 : 0;
        this.permAddRoleAllow = this.permRPCAllow.includes(perm.Roles_and_Permissions_Configuration_Add_User_Role) ? 1 : 0;
        this.permEditPermAllow = this.permRPCAllow.includes(perm.Roles_and_Permissions_Configuration_Edit_User_Role_Name_Permissions) ? 1 : 0;
        this.permDeactivateAllow = this.permRPCAllow.includes(perm.Roles_and_Permissions_Configuration_Deactivate_User_Role) ? 1 : 0;
        console.log(this.permListAllow, this.permAddRoleAllow, this.permEditPermAllow, this.permDeactivateAllow);

        if (this.permListAllow == 0) {
          this.router.navigate(['/access-denied']);
        }

        this.isLoading = true;
        const url = environment.apiUrl + '/api/RPC/v1/getpermissions';
    
        // Set your authorization header
        // const headers = new HttpHeaders({
        //   Authorization: environment.authKey,
        //   'Content-Type': 'application/json',
        // });
    
        const Body: any = {
        };
    
        this.http.post(url, Body, { headers }).subscribe(
          (response: any) => {
            this.modelPermission = response.data;
       
            if (response.data.length == 0) {
              this.totalRecords = 0;
              this.isDisplay = false;
              this.showResultAlertBox();
              this.isLoading = false;
            }
            
            else {
              this.totalRecords = response.data[0].total;
              console.log(this.totalRecords);
              // this.permissionArray = response.data.reduce((acc: any[], item: any) => {
              //   const functions = item.function_nm.split(',').map((f: string) => f.trim()).filter((f: string) => f !== '');
              //   acc.push(item.module_nm, functions);
              //   return acc;
              // }, []);
              this.moduleArray = response.data.map((item: any) => item.module_nm);
              this.permissionArray = response.data.map((item: any) => item.function_nm.split(',').map((f: string) => f.trim()).filter((f: string) => f !== ''));
              //this.AlertBoxInitialize();
              this.collapsedIndexes = this.moduleArray.map((_, index) => index);// Initialize all indexes as collapsed
              console.log(this.moduleArray);
              console.log(this.permissionArray);
              this.isLoading = false;
            }
          },
          (error) => {
            console.error(error);
            this.isLoading = false;
          }
        );
      },
      (error) => {
        console.error(error);
      }
    );
  }

  refreshMainPage(): void {
    this.page = 1;
    this.loadData();
  }

  addSelected(){
    this.DefaultBox();
    const dialogRef = this.dialog.open(RoleAndPermissionsConfigurationsAddRolesComponent, {
      width: '50%',
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result === 'inserted') {
        this.addBox = true;
        this.added = true;
        this.populateRoles();
        this.getRolePermission();
      }
      this.loadData();
    });
  }

  editRoleStatus(item: any): void {
    this.DefaultBox();
    const dialogRef = this.dialog.open(RoleAndPermissionsConfigurationsUpdateRoleStatusComponent, {
      width: '50%',
      data: {
        i_role_nm_en: this.Role,
        i_status: this.isUserRoleActive()
      },
    });
    dialogRef.afterClosed().subscribe((result) => {
      if (result === 'updated') {
        this.editBox = true;
        this.changeStatus = true;
        this.populateRoles();
        this.cd.detectChanges();
      }
     // window.location.reload();
      this.loadData();
    });
  }

  cancelClicked(): void {
    const dialogRef = this.dialog.open(RoleAndPermissionsConfigurationsDiscardChangesComponent);

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        // User clicked 'Yes' in the confirmation dialog, perform cancellation logic here
        this.getRolePermission();  // Or any other logic you want to execute
      }
    });
  }

  populateRoles() {
    const url = environment.apiUrl + '/api/rms/v1/getroles';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json'
    });

    // Create the request body with your form data
    const requestBody = {
      i_page: this.page,
      i_size: this.dropDownSize,
      i_r_id: null,
      i_r_role_nm_en: null,
      i_r_role_nm_bm: null,
      i_modified_by: null,
      i_dt_modified_fr: null,
      i_dt_modified_to: null,
      i_status: null
    };

    // Send an HTTP POST request to the API
    this.http.post(url, requestBody, { headers }).subscribe(
      (response: any) => {
        if (response.data.length === 0) {
          console.error('Invalid response format:', response);
        }
        else {
          this.roleOption = response.data
          console.log(this.roleOption);
          // Handle a successful response (e.g., show a success message)
          if (this.added) {
            // Set the newly added role as the selected role
            this.Role = null;
            this.added = false;
            this.uncheckAllCheckboxes();
          }

          if (this.changeStatus) {
            // Set the updated role as the selected role
            this.Role = null;
            this.changeStatus = false; // Reset the change status flag
            this.uncheckAllCheckboxes();
          }

        }
      },
      (error) => {
        console.error('There was an error retrieving the Roles:', error);
      }
    );
  }

  isUserRoleActive(): boolean {
    return this.roleOption.find((role: Roles) => role.roleNmEn === this.Role)?.status === Systemstatus.Active;
  }


  savePermissions(): void {
    this.isLoading = true;
    if (this.Role) {
      const selectedRole = this.roleOption.find((role) => role.roleNmEn === this.Role);
      console.log(selectedRole);

      if (selectedRole) {
        const role_id = selectedRole.roleId;
        // console.log(role_id);
        const selectedPermissions = [];

        for (let i = 0; i < this.moduleArray.length; i++) {
          const module = this.moduleArray[i];
          const modulePermissions = this.permissionArray[i];

          for (const permission of modulePermissions as string[]) {
            const checkboxId = `${module}-${permission}`;
            console.log(checkboxId)
            const checkboxElement = document.getElementById(checkboxId) as HTMLInputElement;

            const selectedPermissionId = this.permissionWithID.find((permissionItem: any) =>
              permissionItem.module_nm === module && permissionItem.function_nm === permission)?.perm_id;
            console.log(selectedPermissionId);

            selectedPermissions.push({
              module: module,
              permission: permission,
              isAllow: checkboxElement && checkboxElement.checked ? 1 : 0,
              permission_id: selectedPermissionId,
            });
          }
        }

        const deleteUrl = environment.apiUrl + '/api/RPC/v1/delroleperm';
        const addUrl = environment.apiUrl + '/api/RPC/v1/addroleperm';
        const headers = new HttpHeaders({
          Authorization: environment.authKey,
          'Content-Type': 'application/json',
        });

        const deleteBody = {
          i_role_id: role_id
        };

        // Create the body array for adding permissions
        const bodyArray = selectedPermissions.map(permission => ({
          i_role_id: role_id,
          i_perm_id: permission.permission_id,
          i_is_allow: permission.isAllow,
        }));

        this.http.post(deleteUrl, deleteBody, { headers }).pipe(
          switchMap(() => {
            // After delete is successful, send the batch of permissions
            return this.http.post(addUrl, bodyArray, { headers });
          })
        ).subscribe(
          (response: any) => {
            console.log(response);
            this.showInsertAlertBox();
            this.isLoading = false;
            this.loadData();
          },
          (error) => {
            console.error(error);
            this.isLoading = false;
          }
        );
      // First, send the delete request, then send the add requests
      // this.http.post(deleteUrl, deleteBody, { headers }).pipe(
      //   switchMap(() => {
      //     // After delete is successful, perform the add requests
      //     const observables = bodyArray.map(body => this.http.post(addUrl, addBody, { headers }));
      //     return forkJoin(observables);  // Perform all add requests concurrently
      //   })
      // ).subscribe(
      //   (responses: any[]) => {
      //     console.log(responses);
      //     this.showInsertAlertBox();
      //     this.isLoading = false;
      //     this.loadData();
      //   },
      //   (error) => {
      //     console.error(error);
      //     this.isLoading = false;
      //   }
      // );

        // const url = environment.apiUrl + '/api/RPC/v1/addroleperm';
        // console.log(selectedPermissions)

        // const headers = new HttpHeaders({
        //   Authorization: environment.authKey,
        //   'Content-Type': 'application/json',
        // });

        // const deleteurl = environment.apiUrl + '/api/RPC/v1/delroleperm';

        // // Create the request body with your form data
        // const deleteBody = {
        //   i_role_id: role_id
        // };

        // // Send an HTTP POST request to the API
        // this.http.post(deleteurl, deleteBody, { headers }).subscribe(
        //   (response: any) => {
        //     console.log(response);
        //   },
        //   (error) => {
        //     console.error(error);
        //   }
        // );

        // const bodyArray = selectedPermissions.map(permission => ({
        //   i_role_id: role_id,
        //   i_perm_id: permission.permission_id,
        //   i_is_allow: permission.isAllow,
        // }));

        // const observables = bodyArray.map(body => this.http.post(url, body, { headers }));

        // forkJoin(observables).subscribe(
        //   (responses: any[]) => {
        //     console.log(responses);
        //     this.showInsertAlertBox();
        //     this.isLoading = false;
        //     this.loadData();

        //   },
        //   (error) => {
        //     console.error(error);
        //     this.isLoading = false;
        //   }
        // );
      }
    }
  }

  getPermissionId() {
    const url = environment.apiUrl + '/api/RPC/v1/getpermissionsid';

    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    this.http.post(url, {}, { headers }).subscribe(
      (response: any) => {
        if (response.data.length === 0) {
          console.error('Invalid response format:', response);
        }
        else {
          this.permissionWithID = response.data;
          // Handle a successful response (e.g., show a success message)
        }
      },
      (error) => {
        console.error('There was an error retrieving the permission\'s id:', error);
        return null;
      }
    );
  }

  getRolePermission() {
    if (this.Role) {
      const selectedRole = this.roleOption.find((role) => role.roleNmEn === this.Role);

      if (selectedRole) {
        const role_id = selectedRole.roleId;

        const url = environment.apiUrl + '/api/RPC/v1/getroleperm';

        const headers = new HttpHeaders({
          Authorization: environment.authKey,
          'Content-Type': 'application/json',
        });

        const requestBody = {
          i_role_id: role_id
        };
        this.isDataLoading = true;
        this.isDisplay = true;
        this.http.post(url, requestBody, { headers }).subscribe(
          (response: any) => {
            if (response.data.length === 0) {
              this.rolePermission = [];
              // this.isDisplay = false;
              // console.error('Invalid response format:', response);
            } else {
              this.rolePermission = response.data;

              // Handle a successful response (e.g., show a success message)
            }

            this.updateCheckboxStates();
            this.isDataLoading = false;
          },
          (error) => {
            console.error('There was an error retrieving the role permissions:', error);
            this.isDataLoading = false;
            return null;

          }
        );
      }
    }
  }

  updateCheckboxStates() {
    for (let i = 0; i < this.moduleArray.length; i++) {
      const module = this.moduleArray[i];
      const modulePermissions = this.permissionArray[i];
      const moduleCheckId = `${module}-check`;
      const moduleCheckBoxElement = document.getElementById(moduleCheckId) as HTMLInputElement;
      moduleCheckBoxElement.checked = false;
      let allChildChecked = true;

      for (const permission of modulePermissions as string[]) {
        const selectedPermissionId = this.permissionWithID.find((permissionItem: any) =>
          permissionItem.module_nm === module && permissionItem.function_nm === permission)?.perm_id;

        const checkboxId = `${module}-${permission}`;
        const checkboxElement = document.getElementById(checkboxId) as HTMLInputElement;

        const matchingRolePermission = this.rolePermission.find(
          (rp) => rp.perm_id === selectedPermissionId && rp.is_allow === 1
        );

        if (matchingRolePermission) {
          // Checkbox should be checked if is_allow is 1
          checkboxElement.checked = true;
        } else {
          // Checkbox should be unchecked if is_allow is not 1
          checkboxElement.checked = false;
        }

        if (!checkboxElement.checked) {
          // If any child checkbox is not checked, set allChildChecked to false
          allChildChecked = false;
        }
      }
      // Check the parent checkbox if all child checkboxes are checked
      moduleCheckBoxElement.checked = allChildChecked;
      
    }
  }

  checkParent(moduleIndex: number): void {
    const module = this.moduleArray[moduleIndex];
    const modulePermissions = this.permissionArray[moduleIndex];
    const moduleCheckId = `${module}-check`;
    const moduleCheckBoxElement = document.getElementById(moduleCheckId) as HTMLInputElement;

    for (const permission of modulePermissions as string[]) {
      const checkboxId = `${module}-${permission}`;
      const checkboxElement = document.getElementById(checkboxId) as HTMLInputElement;

      if (moduleCheckBoxElement.checked) {
        checkboxElement.checked = true;
      } else {
        // If the parent checkbox is unchecked, uncheck its own child checkboxes
        checkboxElement.checked = false;
      }
    }
  }

  checkChild() {
    for (let i = 0; i < this.moduleArray.length; i++) {
      const module = this.moduleArray[i];
      const modulePermissions = this.permissionArray[i];
      const moduleCheckId = `${module}-check`;
      const moduleCheckBoxElement = document.getElementById(moduleCheckId) as HTMLInputElement;
      let allChildChecked = true;

      for (const permission of modulePermissions as string[]) {
        const checkboxId = `${module}-${permission}`;
        const checkboxElement = document.getElementById(checkboxId) as HTMLInputElement;

        if (!checkboxElement.checked) {
          // If any child checkbox is not checked, set allChildChecked to false
          allChildChecked = false;
        }
      }
      // Check the parent checkbox if all child checkboxes are checked
      moduleCheckBoxElement.checked = allChildChecked;

    }
  }

  uncheckAllCheckboxes() {
    for (let i = 0; i < this.moduleArray.length; i++) {
      const module = this.moduleArray[i];
      const modulePermissions = this.permissionArray[i];
  
      for (const permission of modulePermissions as string[]) {
        const checkboxId = `${module}-${permission}`;
        const checkboxElement = document.getElementById(checkboxId) as HTMLInputElement;
        
        // Uncheck the checkbox
        checkboxElement.checked = false;
      }
  
      // Uncheck the parent checkbox
      const moduleCheckId = `${module}-check`;
      const moduleCheckBoxElement = document.getElementById(moduleCheckId) as HTMLInputElement;
      moduleCheckBoxElement.checked = false;
    }
  }

  collapsedIndexes: number[] = [];

  toggleCollapse(index: number): void {
    const idx = this.collapsedIndexes.indexOf(index);
    if (idx > -1) {
      this.collapsedIndexes.splice(idx, 1); // Remove if exists (expand)
    } else {
      this.collapsedIndexes.push(index); // Add if doesn't exist (collapse)
    }
  }
}