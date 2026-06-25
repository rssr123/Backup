import { ChangeDetectorRef, Component, OnInit, ViewChild } from '@angular/core';
import { ServiceProviderMaintenance } from '../../../core/models/service-provider.interface';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Router } from '@angular/router';
import { NgbPaginationConfig } from '@ng-bootstrap/ng-bootstrap';
import { environment } from 'src/environments/environment';
import { TranslateService } from '@ngx-translate/core';
import { GlobalService } from 'src/app/shared/global.service';
import { MatDialog } from '@angular/material/dialog';
import { ServiceProviderMaintenanceAddComponent } from '../service-provider-maintenance-add/service-provider-maintenance-add.component';
import { ServiceProviderMaintenanceDeleteComponent } from '../service-provider-maintenance-delete/service-provider-maintenance-delete.component';
import { ServiceProviderMaintenanceUpdateComponent } from '../service-provider-maintenance-update/service-provider-maintenance-update.component';
import { Systemstatus } from '../../../shared/enums/systemstatus';
import { fadeInOut } from '../../../shared/animation';
import { ParamData } from 'src/app/core/models/param.interface';
import { ParamService } from '../../../core/services/param.service';
import { formatDate } from '@angular/common';
import { AuthService } from 'src/app/core/services/auth.service';
import { perm } from 'src/permissions/perm';
import { Observable, finalize, switchMap } from 'rxjs';
import { NgModel } from '@angular/forms';
import { PostCodeData } from 'src/app/core/models/postcode.interface';
// import { profile } from 'console';

@Component({
  selector: 'app-service-provider-maintenance-listing',
  templateUrl: './service-provider-maintenance-listing.component.html',
  styleUrls: ['./service-provider-maintenance-listing.component.scss']
})
export class ServiceProviderMaintenanceListingComponent implements OnInit {
  @ViewChild('cityRef') cityRef!: NgModel;
  @ViewChild('stateRef') stateRef!: NgModel;

  page = environment.DefaultPage;
  itemsPerPage = environment.ItemPerPage;
  model: ServiceProviderMaintenance[] = [];
  totalRecords: number = 0;

  // Configuring Permissions for User and roles variables
  permSPM = perm.Service_Provider_Maintenance_View_Page + "," + perm.Service_Provider_Maintenance_Add_Item + "," + perm.Service_Provider_Maintenance_Edit; // all the perm_cd for this module seperated with comma
  permSPMAllow = ""; // variable to store allowed permission for the user
  permListAllow: number = 0; // if 0 then not allow to view listing page, else allow
  permAddAllow: number = 0; // if 0 then not allow to add tax code, else allow
  permEditAllow: number = 0; // if 0 then not allow to edit tax code, else allow
  // end configuration



  agPfId: number | null = null;
  profileNm: string | null = null;
  custNm: string | null = null;
  custAddr1: string | null = null;
  custAddr2: string | null = null;
  custAddr3: string | null = null;
  custPostcode: string | null = null;
  custCity: string | null = null;
  custState: string | null = null;
  custEmail: string | null = null;
  custPhone: string | null = null;
  feeDetailId: string | null = null;
  entityType: string | null = null;
  entityNo: string | null = null;
  entityNm: string | null = null;
  status: string | null = null;
  total: number | null = null;
  

  postCodes: PostCodeData[] = [];
  uniqueCities: string[] = [];
  uniqueStates: string[] = [];
  shortformstate: string | null = null;
  totalPostCodeRecords: number = 0;



  isDisplay: boolean = true;///change here

  isLoading: boolean = false;
  //date range picker
  selected: Date[] | null = null;
  bsValue = new Date();
  tempDate !: Date;
  minDate = new Date();
  // selected!: { start?: moment.Moment; end?: moment.Moment };
  //date range picker

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
    this.loadEntType();
    this.minDate.setMonth(this.minDate.getMonth() - 1);
    this.selected = null;
    this.loadStates();
    this.loadPostcode();
    this.loadData();
  }

  //loadData Start
  loadData() {
    this.authService.checkUserRole(this.authService.username, this.permSPM)
      .subscribe(
        (response: any) => {
          this.permSPMAllow = response.data;
          this.permListAllow = this.permSPMAllow.includes(perm.Service_Provider_Maintenance_View_Page) ? 1 : 0;
          this.permAddAllow = this.permSPMAllow.includes(perm.Service_Provider_Maintenance_Add_Item) ? 1 : 0;
          this.permEditAllow = this.permSPMAllow.includes(perm.Service_Provider_Maintenance_Edit) ? 1 : 0;
          console.log(this.permListAllow, this.permAddAllow, this.permEditAllow);
          if (this.permListAllow === 0) {
            console.log("access-denied");
            this.router.navigate(['/access-denied']);
            return; // Exit the function to prevent further execution
          }
          console.log(this.permListAllow, this.permAddAllow, this.permEditAllow);

          // Set your authorization header
          const headers = new HttpHeaders({
            Authorization: environment.authKey,
            'Content-Type': 'application/json',
          });


          this.isDisplay = true;
          this.isLoading = true;
          const url = environment.apiUrl + '/api/sp/v1/getserviceprovidermaintenance';

          const Body: any = {
            i_page: this.page.toString(),
            i_size: this.itemsPerPage.toString(),
          };

          if (this.profileNm && this.profileNm.trim()) {
            Body.i_profile_nm = this.profileNm;
          }

          if (this.custNm && this.custNm.trim()) {
            Body.i_cust_nm = this.custNm;
          }

          // if (this.custAddr1 && this.custAddr1.trim()) {
          //   Body.i_cust_addr1 = this.custAddr1;
          // }

          // if (this.custAddr2 && this.custAddr2.trim()) {
          //   Body.i_cust_addr2 = this.custAddr2;
          // }

          // if (this.custAddr3 && this.custAddr3.trim()) {
          //   Body.i_cust_addr3 = this.custAddr3;
          // }

          if (this.custPostcode && this.custPostcode.trim()) {
            Body.i_cust_postcode = this.custPostcode;
          }

          if (this.custCity && this.custCity.trim()) {
            Body.i_cust_city = this.custCity;
          }

          if (this.custState && this.custState.trim()) {
            Body.i_cust_state = this.custState;
          }

          if (this.custEmail && this.custEmail.trim()) {
            Body.i_cust_email = this.custEmail;
          }

          if (this.custPhone && this.custPhone.trim()) {
            Body.i_cust_phone = this.custPhone;
          }

          if (this.feeDetailId && this.feeDetailId.trim()) {
            Body.i_fee_detail_id = this.feeDetailId;
          }

          if (this.entityType && this.entityType.trim()) {
            Body.i_entity_type = this.entityType;
          }

          if (this.entityNo && this.entityNo.trim()) {
            Body.i_entity_no = this.entityNo;
          }

          if (this.entityNm && this.entityNm.trim()) {
            Body.i_entity_nm = this.entityNm;
          }

          if (this.status && this.status.trim()) {
            Body.i_status = this.status;
          }
          console.log(Body);


          // if (this.total) {
          //   Body.i_total = this.total;
          // }


          // if (this.selected) {
          //   Body.i_dt_modified_fr = formatDate(this.selected[0], 'YYYY-MM-dd', 'en');
          //   this.selected[1].setDate(this.selected[1].getDate() + 1);
          //   Body.i_dt_modified_to = formatDate(this.selected[1], 'YYYY-MM-dd', 'en');
          // }


          let temp = "";

          if (this.selectedState.length > 0 && (this.selectedState == Systemstatus.Active || this.selectedState == Systemstatus.Inactive)) {
            temp = this.selectedState;
          }

          if (temp == Systemstatus.Active || temp == Systemstatus.Inactive) {
            Body.i_status = temp;
          }


          console.log(Body);

          this.http.post(url, Body, { headers }).subscribe(
            (response: any) => {

              // console.log("original data");
              // console.log(response.data);


              this.model = response.data;

              if (response.data.length == 0) {
                this.totalRecords = 0;
                this.isDisplay = true;
                this.showResultAlertBox();
                this.isLoading = false;
              } else {
                this.totalRecords = response.data[0].total;
                this.AlertBoxInitialize();
                this.DefaultBox();
                this.isLoading = false;
              }
              console.log(response.data);
              //  console.log(this.totalRecords);
            },
            (error) => {
              console.error(error);
              this.isLoading = false;

              this.showGenericAlertBox();
            }
          );
        },
        (error) => {
          console.error(error);
        }
      );


  }
  //loadData End

  //editSelected Start
  editSelected(item: any): void {
    this.DefaultBox();
    const dialogRef = this.dialog.open(ServiceProviderMaintenanceUpdateComponent, {
      width: '50%',
      data: {
        agPfId:item.ag_pf_id,
        profileNm: item.profile_nm,
        custNm: item.cust_nm,
        custAddr1: item.cust_addr_1,
        custAddr2: item.cust_addr_2,
        custAddr3: item.cust_addr_3,
        postcode: item.cust_postcode,
        city: item.cust_city,
        state: item.cust_state,
        email: item.cust_email,
        phone: item.cust_phone,
        feeDetailId: item.fee_detail_id,
        entityType: item.entity_type,
        entityNo: item.entity_no,
        entityNm: item.entity_nm
        
      }
      
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result === 'updated') {
        this.editBox = true;
      }
      this.refreshMainPage();
    });
  }
  //editSelected End

  //deleteSelected Start
  deleteSelected(item: any): void {
    this.DefaultBox();
    const dialogRef = this.dialog.open(ServiceProviderMaintenanceDeleteComponent, {
      width: '50%',
      data: { agPfId: item.ag_pf_id },
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result === 'deleted') {
        this.deleteBox = true;
      }
      this.refreshMainPage();
    });
  }
  //deleteSelected End

  addSelected(): void {
    this.DefaultBox();
    const dialogRef = this.dialog.open(ServiceProviderMaintenanceAddComponent, {
      width: '50%',
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result === 'inserted') {
        this.addBox = true;
      }
      this.refreshMainPage();
    });
  }

  isReadOnly = false;
  enType: ParamData[] = [];

  loadEntType() {
    this.ParamService.getStates('1', '100', '', 'EntityType').subscribe(
      (response: any) => {
        if (response && response.data && Array.isArray(response.data)) {
          //this.states = response.data as ParamData[];
          this.enType.push({
            param_cd: '',
            nm_en: 'ALL',
            nm_bm: 'ALL',
            total: 5
          }); //add 'All' options
          //this.states.push(response.data);
          this.enType = [...this.enType, ...response.data];
          //this.states.sort((a, b) => a.nm_en.localeCompare(b.nm_en)); // sorting
          this.entityType = this.enType[0].param_cd;
        } else {
          console.error('Invalid response format:', response);
        }
      },
      (error: any) => {
        console.error('There was an error retrieving the status:', error);
      }
    );
  }

  apply(): void {
    this.loadData();
    this.rightSectionCollapsed = true;
  }

  reset(): void {
    this.profileNm = null;
    this.custNm = null;
    // this.custAddr1 = null;
    // this.custAddr2 = null;
    // this.custAddr3 = null;
    this.custPostcode = null;
    this.custCity = null;
    this.custState = null;
    this.custEmail = null;
    this.custPhone = null;
    this.feeDetailId = null;
    this.entityType = null;
    this.entityNo = null;
    this.entityNm = null;
    this.status = null;

    // this.minDate.setDate(this.minDate.getMonth() - 1);
    // this.selected = null;
  }

  refreshMainPage(): void {
    this.page = 1;
    this.loadData();
  }

  loadStates() {
    this.ParamService.getStates('1', '100', '', 'Status').subscribe(
      (response: any) => {
        if (response && response.data && Array.isArray(response.data)) {
          //this.states = response.data as ParamData[];
          this.states.push({ param_cd: '', nm_en: 'All', nm_bm: 'All', total: 5 }); //add 'All' options
          //this.states.push(response.data);
          this.states = [...this.states, ...response.data];
          //this.states.sort((a, b) => a.nm_en.localeCompare(b.nm_en)); // sorting
        } else {
          console.error('Invalid response format:', response);
        }
      },
      (error) => {
        console.error('There was an error retrieving the status:', error);
      }
    );
  }

  async checkRecordInUse(tax_cd_id: any): Promise<any> {
    const url = environment.apiUrl + '/api/tc/v1/checktaxcodeexist';

    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    const body = {
      i_tax_cd_id: tax_cd_id,
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

  //toggle action
  async toggleActivation(item: any) {
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
    //   this.checkResult -= await this.checkRecordInUse(item.tax_cd_id);
    // }

    if (this.checkResult == 0) {

      const url = environment.apiUrl + '/api/sp/v1/delserviceprovidermaintenance';

      const headers = new HttpHeaders({
        Authorization: environment.authKey,
        'Content-Type': 'application/json',
      });

      const body = {
        i_ag_pf_id: item.ag_pf_id,
        i_modified_by: null,
        i_status: item.status
      };

      console.log("this is the body", body);

      this.http.post(url, body, { headers }).subscribe(
        (response: any) => {
          // Handle the response

          this.loadData();
          this.cd.detectChanges();
        },
        (error) => {
          // In case of error, revert the status change in the UI
          item.status = item.status === Systemstatus.Active ? Systemstatus.Inactive : Systemstatus.Active;
          console.error('Error toggling status:', error);
          this.loadData();
          this.cd.detectChanges();
        }
      );
    }
    else {
      this.showDeactiveAlertBox();
      item.status = item.status === Systemstatus.Active ? Systemstatus.Inactive : Systemstatus.Active;
      this.loadData();
      this.cd.detectChanges();
    }
  }
  //toggle action

  loadPostcode() {

    const url = environment.apiUrl + '/api/rms/v1/getpostcode';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    // const Body: any = {
    // };

    this.http.post(url, {}, { headers }).subscribe(
      (response: any) => {

        if (response.data.length === 0) {
          this.totalPostCodeRecords = 0;
        } else {
          this.postCodes = response.data;
          this.totalPostCodeRecords = response.data[0].total;
          this.extractUniqueCitiesAndStates();
        }
      },
      (error) => {
        console.error('There was an error retrieving the postcode:', error);
      }
    );

  }


  //postcode start
  city: string | null = null;
  state: string | null = null;

  onPostcodeChange(selectedPostcode: string | null) {
    if (!selectedPostcode) {
      this.city = null;
      this.state = null;

      if (this.cityRef) this.cityRef.control.markAsTouched();
      if (this.stateRef) this.stateRef.control.markAsTouched();

      return;
    }

    const match = this.postCodes.find(p => String(p.postcode) === selectedPostcode);
    this.city = match ? match.city : null;
    this.state = match ? match.state : null;
  }


  extractUniqueCitiesAndStates() {
    this.uniqueCities = [...new Set(this.postCodes.map(p => p.city))].sort((a, b) =>
      a.localeCompare(b)
    );

    this.uniqueStates = [...new Set(this.postCodes.map(p => p.state))].sort((a, b) =>
      a.localeCompare(b)
    );
  }

  upperCity = (term: string): string | null => {
    if (!term) return null;
    const trimmed = term.trim().toUpperCase();
    return trimmed.length > 50 ? trimmed.substring(0, 50) : trimmed; //allow maximum 50 characters
  };


  checkTag = (term: string): string | null => {
    if (/^\d{1,5}$/.test(term)) {
      return term; // ensure 1–5 digit number
    }

    return null;
  };

  //postcode end

  //get param_cd of state
  updateShortformState(): void {
    const match = this.states.find(s => s.nm_en === this.state);
    this.shortformstate = match ? match.param_cd : null;
  }

  /// end postcode

}
