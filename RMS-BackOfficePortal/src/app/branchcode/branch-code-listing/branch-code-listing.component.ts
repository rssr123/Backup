import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Router } from '@angular/router';
import { NgbPaginationConfig } from '@ng-bootstrap/ng-bootstrap';
import { environment } from 'src/environments/environment';
import { TranslateService } from '@ngx-translate/core';
import { GlobalService } from 'src/app/shared/global.service';
import { MatDialog } from '@angular/material/dialog';
import { BranchCodeAddComponent } from 'src/app/branchcode/branch-code-add/branch-code-add.component';
import { BranchCodeUpdateComponent } from 'src/app/branchcode/branch-code-update/branch-code-update.component';
import { Systemstatus } from '../../shared/enums/systemstatus';
import { fadeInOut } from '../../shared/animation';
import { ParamData } from '../../core/models/param.interface';
import { ParamService } from '../../core/services/param.service';
import { formatDate } from '@angular/common';
import { AuthService } from 'src/app/core/services/auth.service';
import { perm } from 'src/permissions/perm';

@Component({
  selector: 'app-branch-code-listing',
  templateUrl: './branch-code-listing.component.html',
  styleUrls: ['./branch-code-listing.component.scss'],
  animations: [fadeInOut],
})
export class BranchCodeListingComponent implements OnInit {
  page = environment.DefaultPage;
  itemsPerPage = environment.ItemPerPage;
  model: any[] = [];
  totalRecords: number = 0;

  // Configuring Permissions
  permBCD = perm.Branch_Code_Maintenance_View_Page + "," + perm.Branch_Code_Maintenance_Add_Item + "," + perm.Branch_Code_Maintenance_Edit;
  permBCDAllow = '';
  permListAllow: number = 0;
  permAddAllow: number = 0;
  permEditAllow: number = 0;

  branchCode: string | null = null;
  branchName: string | null = null;
  branchType: string | null = null;
  modifiedBy: string | null = null;

  isDisplay: boolean = false;
  isLoading: boolean = false;

  //date range picker
  selected: Date[] | null = null;
  bsValue = new Date();
  tempDate !: Date;
  minDate = new Date();

  editBox: boolean = false;
  addBox: boolean = false;
  deleteBox: boolean = false;

  //toogle start
  rightSectionCollapsed: boolean = true;

  //default pagination
  selectedValue = environment.dropdownOptions[0];
  dropdownOptions = environment.dropdownOptions;

  states: ParamData[] = [];
  selectedState: string = Systemstatus.Active;

  checkResult: number = 0;

  LoadData(singleItem: number) {
    this.selectedValue = singleItem;
    this.itemsPerPage = this.selectedValue;
    this.loadData();
  }

  toggleRightSection() {
    this.rightSectionCollapsed = !this.rightSectionCollapsed;
  }
  //toogle end

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

  showGenericAlert = false;

  showGenericAlertBox() {
    this.showGenericAlert = true;
    setTimeout(() => (this.showGenericAlert = false), 2000);
  }
  //for alert box end

  constructor(
    private http: HttpClient,
    config: NgbPaginationConfig,
    private router: Router,
    public dialog: MatDialog,
    private ParamService: ParamService,
    private translate: TranslateService,
    private globalService: GlobalService,
    private cd: ChangeDetectorRef,
    private authService: AuthService
  ) {
    config.maxSize = environment.PaginationMaxSize;
    config.boundaryLinks = true;
    this.translate.setDefaultLang(this.globalService.getGlobalValue()); // default language
    this.translate.use(this.globalService.getGlobalValue());
  }

  ngOnInit(): void {
    this.minDate.setMonth(this.minDate.getMonth() - 1);
    this.selected = null;
    this.loadStates();
    this.loadData();
  }

  loadData(): void {
    this.authService.checkUserRole(this.authService.username, this.permBCD).subscribe(
      (response: any) => {
        this.permBCDAllow = response.data;
        this.permListAllow = this.permBCDAllow.includes(perm.Branch_Code_Maintenance_View_Page) ? 1 : 0;
        this.permAddAllow = this.permBCDAllow.includes(perm.Branch_Code_Maintenance_Add_Item) ? 1 : 0;
        this.permEditAllow = this.permBCDAllow.includes(perm.Branch_Code_Maintenance_Edit) ? 1 : 0;

        if (this.permListAllow === 0) {
          console.log("Access denied");
          this.router.navigate(['/access-denied']);
          return;
        }

        this.isLoading = true;

        const url = environment.apiUrl + '/api/bc/v1/getbranchcodes';
        const headers = new HttpHeaders({
          Authorization: environment.authKey,
          'Content-Type': 'application/json',
        });

        const Body: any = {
          i_page: this.page.toString(),
          i_size: this.itemsPerPage.toString(),
          i_code: this.branchCode?.trim() || null,
          i_bcm_desc: this.branchName?.trim() || null,
          i_bcm_ty: this.branchType?.trim() || null,
          i_modified_by: this.modifiedBy?.trim() || null,
          i_dt_modified_fr: this.selected ? formatDate(this.selected[0], 'yyyy-MM-dd', 'en') : null,
          i_dt_modified_to: this.selected ? formatDate(this.selected[1], 'yyyy-MM-dd', 'en') : null,
          // i_status: this.selectedState || Systemstatus.Active,
        };

        let temp = "";

        if (this.selectedState.length > 0 && (this.selectedState == Systemstatus.Active || this.selectedState == Systemstatus.Inactive)) {
          temp = this.selectedState;
        }

        if (temp == Systemstatus.Active || temp == Systemstatus.Inactive) {
          Body.i_status = temp;
        }

        this.http.post(url, Body, { headers }).subscribe(
          (response: any) => {
            this.model = response.data || [];
            this.totalRecords = response.data?.length > 0 ? response.data[0].total : 0;
            // this.AlertBoxInitialize();
            this.isDisplay = true; // Always display table headers
            this.isLoading = false;
          },
          (error) => {
            console.error(error);
            this.isDisplay = true; // Display table headers even on error
            this.isLoading = false;
          }
        );
      },
      (error) => {
        console.error(error);
      }
    );
  }


  //   loadData(): void {
  //     this.authService.checkUserRole(this.authService.username, this.permBCD)
  //     .subscribe(
  //       (response: any) => {
  //         this.permBCDAllow = response.data;
  //         this.permListAllow = this.permBCDAllow.includes(perm.Branch_Code_Maintenance_View_Page) ? 1 : 0;
  //         this.permAddAllow = this.permBCDAllow.includes(perm.Branch_Code_Maintenance_Add_Item) ? 1 : 0;
  //         this.permEditAllow = this.permBCDAllow.includes(perm.Branch_Code_Maintenance_Edit) ? 1 : 0;
  //         console.log(this.permListAllow, this.permAddAllow, this.permEditAllow);
  //         if (this.permListAllow === 0) {
  //           console.log("access-denied");
  //           this.router.navigate(['/access-denied']);
  //           return; // Exit the function to prevent further execution
  //         }
  //         console.log(this.permListAllow, this.permAddAllow, this.permEditAllow);



  //         this.isLoading = true;

  //         const url = environment.apiUrl + '/api/bc/v1/getbranchcodes';
  //         const headers = new HttpHeaders({
  //           Authorization: environment.authKey,
  //           'Content-Type': 'application/json',
  //     });


  //     const Body: any = {
  //       i_page: this.page.toString(),
  //       i_size: this.itemsPerPage.toString(),
  //     };

  //     // if (this.branchCode && this.branchCode.trim()) {
  //     //   Body.i_code = this.branchCode;
  //     // }

  //     // if (this.branchType && this.branchType.trim()) {
  //     //   Body.i_bcm_ty = this.branchType;
  //     // }

  //     if (this.branchName && this.branchName.trim()) {
  //       Body.i_bcm_desc = this.branchName;
  //     }


  //     if (this.modifiedBy && this.modifiedBy.trim()) {
  //       Body.i_modified_by = this.modifiedBy;
  //     }

  //     if (this.selected) {
  //       Body.i_dt_modified_fr = formatDate(this.selected[0], 'yyyy-MM-dd', 'en'); //.format('YYYY-MM-DD');
  //       this.selected[1].setDate(this.selected[1].getDate() + 1);
  //       Body.i_dt_modified_to = formatDate(this.selected[1], 'yyyy-MM-dd', 'en');
  //     }

  //     let temp = '';

  //     if (
  //       this.selectedState.length > 0 &&
  //       (this.selectedState == Systemstatus.Active ||
  //         this.selectedState == Systemstatus.Inactive)
  //     ) {
  //       temp = this.selectedState;
  //     }

  //     if (temp == Systemstatus.Active || temp == Systemstatus.Inactive) {
  //       Body.i_status = temp;
  //     }

  //     this.http.post(url, Body, { headers }).subscribe(
  //       (response: any) => {
  //         this.model = response.data;
  //         console.log(response.data);

  //         if (response.data.length == 0) {
  //           this.totalRecords = 0;
  //           this.isDisplay = true;
  //           this.showResultAlertBox();
  //           this.isLoading = false;
  //         } else {
  //           this.totalRecords = response.data[0].total;
  //           this.AlertBoxInitialize();
  //           this.DefaultBox();
  //           this.isLoading = false;
  //         }

  //       },
  //       (error) => {
  //         console.error(error);
  //         this.isLoading = false;

  //         this.showGenericAlertBox();
  //       }
  //     );
  //   },
  //   (error) => {
  //     console.error(error);
  //   }
  // );


  // }

  //   const body: any = {
  //     i_code: this.branchCode?.trim() || '',
  //     i_bcm_desc: this.branchName?.trim() || '',
  //     i_bcm_ty: this.branchType?.trim() || '',
  //     i_modified_by: this.modifiedBy?.trim() || '',
  //     i_status: this.selectedState?.trim() || '',
  //   };

  //   this.http.post(url, body, { headers }).subscribe(
  //     (response: any) => {
  //       // Store the entire data in `allData`
  //       const allData = response.data.map((item: any) => ({
  //         code: item.code,
  //         bcmTy: item.bcmTy,
  //         bcmDesc: item.bcmDesc,
  //         modifiedBy: item.modifiedBy,
  //         dtModified: item.dtModified,
  //         status: item.status,
  //       }));

  //       // Update total records
  //       this.totalRecords = allData.length;

  //       // Apply in-memory pagination
  //       this.model = allData.slice(
  //         (this.page - 1) * this.itemsPerPage,
  //         this.page * this.itemsPerPage
  //       );

  //       this.isDisplay = true;
  //       this.isLoading = false;
  //     },
  //     (error) => {
  //       console.error('API Error:', error);
  //       this.isLoading = false;
  //     }
  //   );
  // }

  editSelected(item: any): void {
    console.log("Edit button clicked for item:", item);
    const dialogRef = this.dialog.open(BranchCodeUpdateComponent, {
      width: '50%',
      data: {
        bcm_id: item.bcm_id,
        branchCode: item.code,
        branchName: item.bcmDesc,
        branchType: item.bcmTy,
      },
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result === 'updated') {
        //this.loadData();
        this.editBox = true;
      }
      this.refreshMainPage();
    });
  }

  addSelected(): void {
    const dialogRef = this.dialog.open(BranchCodeAddComponent, {
      width: '50%',
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result === 'inserted') {
        //this.loadData();
        this.addBox = true;
      }
      this.refreshMainPage();
    });
  }

  apply(): void {
    this.loadData();
    this.rightSectionCollapsed = true;
  }

  reset(): void {
    this.branchCode = null;
    this.branchType = null;
    this.branchName = null;
    this.modifiedBy = null;
    this.minDate.setDate(this.minDate.getMonth() - 1);
    this.selected = null;

    this.selectedState = '';
  }

  refreshMainPage(): void {
    this.page = 1;
    this.loadData();
  }

  loadStates() {
    this.ParamService.getStates('1', '100', '', 'Status').subscribe(
      (response: any) => {
        if (response && response.data && Array.isArray(response.data)) {
          this.states.push({ param_cd: '', nm_en: 'All', nm_bm: 'All', total: 5 }); //add 'All' options
          this.states = [...this.states, ...response.data];
        } else {
          console.error('Invalid response format:', response);
        }
      },
      (error) => {
        console.error('There was an error retrieving the status:', error);
      }
    );
  }

  async checkRecordInUse(branch_cd_id: any): Promise<any> {
    const url = environment.apiUrl + '/api/tc/v1/checkbranchcodeexist';

    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    const body = {
      i_branch_cd_id: branch_cd_id,
    };

    const response: any = await this.http.post(url, body, { headers }).toPromise();
    try {
      this.checkResult = response.data;
      console.log("check: " + response.data);
      return response.data;
    }
    catch (error) {
      console.error(error);
      return error;
    }

  }

  showCantActivateAlert = false;
  uploadErrorMessage: string = '';

  showUploadErrorAlertBox(message: string) {
    this.uploadErrorMessage = message; // Store the message
    this.showCantActivateAlert = true;
    setTimeout(() => (this.showCantActivateAlert = false), 10000);
  }

  // toggle action
  async toggleActivation(item: any) {
    console.log("Toggle button clicked for item:", item);
    this.isDisplay = true;
    this.isLoading = true;
    this.checkResult = 0;
    // Toggle the local status for a responsive UI
    if (item.status === Systemstatus.Active) {
      item.status = Systemstatus.Delete;
    } else {
      item.status = Systemstatus.Active;
    }

    // if (item.status == 'D') {
    //   //CHECK RECORD IN USE
    //   this.checkResult -= await this.checkRecordInUse(item.branch_cd_id);
    // }

    if (this.checkResult == 0) {

      const url = environment.apiUrl + '/api/bc/v1/delbranchcode';

      const headers = new HttpHeaders({
        Authorization: environment.authKey,
        'Content-Type': 'application/json',
      });

      const body = {
        i_bcm_id: item.bcm_id,
        i_code: item.code,
        i_modified_by: null,
        i_status: item.status,
      };

      this.http.post(url, body, { headers }).subscribe(
        (response: any) => {
          // Handle the response
          this.loadData();
          this.cd.detectChanges();
        },
        (error) => {
          // In case of error, revert the status change in the UI
          item.status = item.status === Systemstatus.Active ? Systemstatus.Inactive : Systemstatus.Active;
          this.uploadErrorMessage = this.translate.instant("Unable to activate or deactivate the selected item. Another item with the same Branch Code is already active.");
          this.showUploadErrorAlertBox(this.uploadErrorMessage);
          console.error('Error toggling status:', error);
          this.loadData();
          //this.refreshMainPage();
          this.cd.detectChanges();
        }
      );
    }
    else {
      this.showDeactiveAlertBox();
      item.status = item.status === Systemstatus.Active ? Systemstatus.Inactive : Systemstatus.Active;
      this.loadData();
      //this.refreshMainPage();
      this.cd.detectChanges();
    }
  }

  // async toggleActivation(item: any) {
  //   this.isLoading = true; // Show loading spinner

  //   // Determine the new status
  //   const newStatus = item.status === 'Active' ? 'D' : 'A';

  //   // Prepare the payload for the API
  //   const body = {
  //     i_code: item.code,
  //     i_bcm_ty: item.bcmTy, // Send the branch type (if required)
  //     i_bcm_desc: item.bcmDesc, // Send the branch description (if required)
  //     i_modified_by: this.authService.getUsername(), // Get the current user
  //     i_status: newStatus, // Update the status
  //   };

  //   try {
  //     const url = `${environment.apiUrl}/api/bc/v1/updateBranchCode`;
  //     const headers = new HttpHeaders({
  //       Authorization: environment.authKey,
  //       'Content-Type': 'application/json',
  //     });

  //     const response: any = await this.http.post(url, body, { headers }).toPromise();

  //     if (response && response.data) {
  //       // Update the local item's status based on the backend response
  //       this.loadData();
  //       item.status = newStatus;
  //       this.showUpdateAlertBox(); // Show success alert
  //     }
  //   } catch (error) {
  //       console.error('Error updating branch code:', error);

  //     // Revert the status change locally if there's an error
  //     item.status = item.status === 'A' ? 'Active' : 'Inactive';
  //     this.showGenericAlertBox(); // Show error alert
  //   } finally {
  //     this.isLoading = false; // Reset loading state
  //   }
  // }

}
