import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { UserRole, Roles } from '../core/models/entity';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Router } from '@angular/router';
import { NgbPaginationConfig } from '@ng-bootstrap/ng-bootstrap';
import { environment } from 'src/environments/environment';
import { MatDialog } from '@angular/material/dialog';
import { ParamService } from '../core/services/param.service';
import { ParamData } from 'src/app/core/models/param.interface';
import { Systemstatus } from '../shared/enums/systemstatus';
import { TranslateService } from '@ngx-translate/core';
import { GlobalService } from '../shared/global.service';

@Component({
  selector: 'app-userrole',
  templateUrl: './userrole_v2.component.html',
  styleUrls: ['./userrole_v2.component.scss']
})

export class UserroleComponent implements OnInit {
  page = environment.DefaultPage;
  itemsPerPage = environment.ItemPerPage;
  dropDownSize = environment.DropDownSize;
  model: UserRole[] = [];
  roleOption: Roles[] = []
  totalRecords: number = 0;
  userRolesArray: { name: string, roles: string[], userRef: string }[] = [];
  User: String | null = null;
  Role: String | null = null;

  isDisplay: boolean = false;
  isLoading: boolean = false;

  states: ParamData[] = [];

  //toogle start
  rightSectionCollapsed: boolean = true;

  //default pagination
  selectedValue = environment.dropdownOptions[0];
  dropdownOptions = environment.dropdownOptions;

  showResultAlert = false;
  showResultAlertBox() {
    this.showResultAlert = true;
    setTimeout(() => this.showResultAlert = false, 2000);
  }

  showGenericAlert = false;
  showGenericAlertBox() {
    this.showGenericAlert = true;
    setTimeout(() => (this.showGenericAlert = false), 2000);
  }

  toggleRightSection() {
    this.rightSectionCollapsed = !this.rightSectionCollapsed;
  }

  constructor(
    private http: HttpClient,
    config: NgbPaginationConfig,
    private router: Router,
    public dialog: MatDialog,
    private ParamService: ParamService,
    private cd: ChangeDetectorRef,
    private translate: TranslateService,
    private globalService: GlobalService
  ) {
    config.maxSize = environment.PaginationMaxSize;
    config.boundaryLinks = true;
    this.translate.setDefaultLang(this.globalService.getGlobalValue()); // default language
    this.translate.use(this.globalService.getGlobalValue());
  }

  ngOnInit(): void {
    this.populateRoles();
    this.loadStates();
    // load data must be place at last
    this.loadData();

  }

  LoadData(singleItem: number) {
    this.selectedValue = singleItem;
    this.itemsPerPage = this.selectedValue;
    this.loadData();
  }

  //loadData Start
  loadData() {
    this.isDisplay = true;
    this.isLoading = true;
    const url = environment.apiUrl + '/api/UR/v1/getuserrole';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    const Body: any = {
      i_page: this.page.toString(),
      i_size: this.itemsPerPage.toString(),
    };

    if (this.User && this.User.trim()) {
      Body.i_user = this.User;
    }

    if (this.Role && this.Role.trim()) {
      Body.i_user_role = this.Role;
    }

    console.log(Body);

    this.http.post(url, Body, { headers }).subscribe(
      (response: any) => {
        this.userRolesArray = [];
        // this.userRolesMap.clear();
        this.model = response.data;
        if (response.data.length == 0) {
          this.totalRecords = 0;
          // this.isDisplay = false;
          this.showResultAlertBox();
          this.isLoading = false;
        } else {
          this.isLoading = true;
          this.totalRecords = response.data[0].totalRoles;

          response.data.forEach((item: any) => {
            this.isLoading = true;
            const existingUserRoles = this.userRolesArray.find(user => user.userRef === item.userRef);

            if (existingUserRoles) {
              existingUserRoles.roles.push(item.role_nm_en);

            } else {
              this.userRolesArray.push({
                userRef: item.userRef,
                name: item.name,
                roles: [item.role_nm_en]
              });

            }
          });

          for (let i = 0; i < this.userRolesArray.length; i++) {
            this.userRolesArray[i].roles[0] = this.userRolesArray[i].roles[0].split(', ').sort().join(', ');
          }

          console.log(this.userRolesArray);

          //this.AlertBoxInitialize();
          this.isLoading = false;
        }
        console.log(response.data);
        console.log(this.totalRecords);
      },
      (error) => {
        console.error(error);
        this.isLoading = false;
      }
    );
  }

  //loadData End
  apply(): void {
    this.isLoading = true;
    this.loadData();
    this.rightSectionCollapsed = true;
  }

  reset(): void {
    this.User = null;
    this.Role = null;
  }

  refreshMainPage(): void {
    this.page = 1;
    this.loadData();
  }

  loadStates() {
    this.ParamService.getStates('1', '100', '', 'Status').subscribe(
      (response: any) => {
        if (response && response.data && Array.isArray(response.data)) {
          this.states = response.data as ParamData[];
          //this.states.push({ param_cd: '', nm_en: 'All', nm_bm: 'All', total: 5 }); //add 'All' options
          this.states.push(response.data);
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
      i_status: Systemstatus.Active
    };

    // Send an HTTP POST request to the API
    this.http.post(url, requestBody, { headers }).subscribe(
      (response: any) => {
        if (response.data.length === 0) {
          console.error('Invalid response format:', response);
        }
        else {
          this.roleOption = response.data
          // Handle a successful response (e.g., show a success message)
        }
      },
      (error) => {
        console.error('There was an error retrieving the Roles:', error);
      }
    );

  }
}