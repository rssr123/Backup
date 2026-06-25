import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Router } from '@angular/router';
import { NgbPaginationConfig } from '@ng-bootstrap/ng-bootstrap';
import { environment } from 'src/environments/environment';
import { MatDialog } from '@angular/material/dialog';
import { ParamService } from '../core/services/param.service';
import { ParamData } from 'src/app/core/models/param.interface';
import { TranslateService } from '@ngx-translate/core';
import { GlobalService } from '../shared/global.service';
import { CCTaskList } from '../core/models/credit-control-sme-task-list.interface';
import { perm } from 'src/permissions/perm';
import { AuthService } from '../core/services/auth.service';
import { TriggerNotificationUpdateService } from 'src/app/core/services/TriggerNotificationUpdateService.service';

@Component({
  selector: 'app-credit-control-sme-task-list',
  templateUrl: './credit-control-sme-task-list.component.html',
  styleUrls: ['./credit-control-sme-task-list.component.scss']
})
export class CreditControlSmeTaskListComponent {
  page = environment.DefaultPage;
  itemsPerPage = environment.ItemPerPage;
  dropDownSize = environment.DropDownSize;
  model: CCTaskList[] = [];
  totalRecords: number = 0;

  task_id: String | null = null;
  task_status: String | null = null;
  payment_status: String | null = null;
  txn_type: String | null = null;
  case_no: String | null = null;
  cust_nm: String | null = null;

  selectedItems: boolean[] = [];  // Array to track checkboxes
  selectAll: boolean = false;

  isDisplay: boolean = false;
  isLoading: boolean = false;

  task_statuses: ParamData[] = [];
  pymt_statuses: ParamData[] = [];
  txn_types: ParamData[] = [];

  // toggle start
  rightSectionCollapsed: boolean = true;

  // default pagination
  selectedValue = environment.dropdownOptions[0];
  dropdownOptions = environment.dropdownOptions;

  permCC = perm.Credit_Control_SME_Task_View_Listing + "," + perm.Credit_Control_SME_Task_Pickup + "," + perm.Credit_Control_SME_Task_View_Details; // all the perm_cd for this module seperated with comma
  permCCAllow = ""; // variable to store allowed permission for the user
  permViewAllow: number = 0; // if 0 then not allow to view listing page, else allow
  permViewListingAllow: number = 0; // if 0 then not allow to view listing page, else allow
  permPickupAllow: number = 0;

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
    private globalService: GlobalService,
    private authService: AuthService,
    private tnu: TriggerNotificationUpdateService
  ) {
    config.maxSize = environment.PaginationMaxSize;
    config.boundaryLinks = true;
    this.translate.setDefaultLang(this.globalService.getGlobalValue()); // default language
    this.translate.use(this.globalService.getGlobalValue());
  }

  ngOnInit(): void {
    this.loadPymtStatus();
    this.loadTaskStatues();
    this.loadTxnType();
    this.loadData();

  }

  toggleSelectAll() {
    this.selectedItems = this.model.map(() => this.selectAll);
  }

  updateHeaderCheckbox() {
    const checkedCount = this.selectedItems.filter(item => item).length;
    if (checkedCount === this.model.length) {
      this.selectAll = true;
    } else if (checkedCount > 0) {
      this.selectAll = false;
    } else {
      this.selectAll = false;
    }
  }

  LoadData(singleItem: number) {
    this.selectedValue = singleItem;
    this.itemsPerPage = this.selectedValue;
    this.loadData();
  }

  //loadData Start
  loadData() {
    this.authService.checkUserRole(this.authService.username, this.permCC)
      .subscribe(
        (response: any) => {
          this.permCCAllow = response.data;
          this.permViewListingAllow = this.permCCAllow.includes(perm.Credit_Control_SME_Task_View_Listing) ? 1 : 0;
          this.permPickupAllow = this.permCCAllow.includes(perm.Credit_Control_SME_Task_Pickup) ? 1 : 0;
          this.permViewAllow = this.permCCAllow.includes(perm.Credit_Control_SME_Task_View_Details) ? 1 : 0;


          if (this.permViewListingAllow === 0) {
            console.log("access-denied");
            this.router.navigate(['/access-denied']);
            return; // Exit the function to prevent further execution
          }
          this.isDisplay = true;
          this.isLoading = true;
          const url = environment.apiUrl + '/api/CCSME/v1/getccsmetasklist';

          // Set your authorization header
          const headers = new HttpHeaders({
            Authorization: environment.authKey,
            'Content-Type': 'application/json',
          });

          const Body: any = {
            i_page: this.page.toString(),
            i_size: this.itemsPerPage.toString(),
          };

          if (this.task_id && this.task_id.trim()) {
            Body.i_task_id = this.task_id;
          }

          if (this.task_status && this.task_status.trim()) {
            Body.i_task_status = this.task_status;
          }

          if (this.payment_status && this.payment_status.trim()) {
            Body.i_payment_status = this.payment_status;
          }

          if (this.txn_type && this.txn_type.trim()) {
            Body.i_txn_type = this.txn_type;
          }

          if (this.case_no && this.case_no.trim()) {
            Body.i_case_no = this.case_no;
          }

          if (this.cust_nm && this.cust_nm.trim()) {
            Body.i_cust_nm = this.cust_nm;
          }

          //console.log(Body);

          this.http.post(url, Body, { headers }).subscribe(
            (response: any) => {
              this.model = response.data;
              if (response.data.length == 0) {
                this.totalRecords = 0;
                // this.showResultAlertBox();
                this.isLoading = false;
              }
              else {
                this.isLoading = true;
                this.totalRecords = response.data[0].total;
                this.isLoading = false;
              }
              //console.log(response.data);
              //console.log(this.totalRecords);
            },
            (error) => {
              console.error(error);
              this.isLoading = false;
            }
          );
        },
        (error) => {
          console.error('Error fetching user role permissions', error);
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
    this.task_id = null;
    this.task_status = null;
    this.payment_status = null;
    this.txn_type = null;
    this.case_no = null;
    this.cust_nm = null;
  }

  refreshMainPage(): void {
    this.page = 1;
    this.loadData();
  }

  loadTaskStatues() {
    this.ParamService.getStates('1', '100', '', 'cc-case').subscribe(
      (response: any) => {
        if (response && response.data && Array.isArray(response.data)) {
          this.task_statuses = response.data as ParamData[];
          console.log(response);
        } else {
          console.error('Invalid response format:', response);
        }
      },
      (error) => {
        console.error('There was an error retrieving the status:', error);
      }
    );
  }

  loadPymtStatus() {
    this.ParamService.getStates('1', '100', '', 'cc-pymt-status').subscribe(
      (response: any) => {
        if (response && response.data && Array.isArray(response.data)) {
          this.pymt_statuses = response.data as ParamData[];
        } else {
          console.error('Invalid response format:', response);
        }
      },
      (error) => {
        console.error('There was an error retrieving the status:', error);
      }
    );
  }

  loadTxnType() {
    this.ParamService.getStates('1', '100', '', 'cc-txn-type').subscribe(
      (response: any) => {
        if (response && response.data && Array.isArray(response.data)) {
          this.txn_types = response.data as ParamData[];
        } else {
          console.error('Invalid response format:', response);
        }
      },
      (error) => {
        console.error('There was an error retrieving the status:', error);
      }
    );
  }

  showInsertAlert: boolean = false;
  submitSelectedTasks() {
    const selectedTasks = this.model
      .filter((_, index) => this.selectedItems[index])
      .map(task => ({
        i_cc_case_id: task.cc_case_id  // Assuming each task has an 'id' field
      }));

    if (selectedTasks.length === 0) {
      alert("Please select at least one task.");
      return;
    }

    const apiUrl = environment.apiUrl + '/api/CCSME/v1/assignccsmetask';
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });
    console.log(selectedTasks);
    this.isLoading = true
    this.http.post(apiUrl, selectedTasks, { headers }).subscribe(
      response => {
        //console.log("Tasks assigned successfully:", response);
        //window.location.reload();
        this.showInsertAlert = true;
        setTimeout(() => {
          this.showInsertAlert = false;        
          this.tnu.emitEvent('trigger');
          this.loadData();
          return;
        }, 4000);
      },
      error => {
        console.error("Error assigning tasks:", error);
      }
    );
  }

  // navigateToDetail(item: any): void {
  //   this.router.navigate(['/credit-control-case-view'],  { queryParams: {  }, state: { item } });
  // }

  navigateToDetail(item: any): void {
    if(this.disableCheckBox(item))
      this.router.navigate(['/credit-control-sme-task-list/credit-control-case'], { queryParams: { task_no: item.task_id }});
    else
      this.router.navigate(['/credit-control-sme-task-list/credit-control-case-view'], {
        queryParams: { task_no: item.task_id },  // Pass task_no from item
        state: { item }  // Pass additional state if needed
      });
  }

  disableCheckBox(item: any){
    if(item.pick_up == this.authService.username || item.pick_up == this.authService.name)
      return true;
    
    const match = this.task_statuses.find(p => String(p.nm_en) === item.task_status || String(p.nm_bm) === item.task_status)!;
    if(match.param_cd =='WO' || match.param_cd =='C')
      return false;

    const roles = this.authService.roles!.split(',');
    if(match.param_cd == 'PD' || match.param_cd == 'IP' || match.param_cd == 'RQ' || match.param_cd == 'O'
      || match.param_cd == 'I' || match.param_cd == 'CMI' || match.param_cd == 'DMI')
      return roles.includes('FINANCEADMIN');
    else if(match.param_cd == 'PFSM')
      return roles.includes('FINANCESENIORMANAGER');
    else if(match.param_cd == 'PFH')
      return roles.includes('FINANCEHOD');
    else if(match.param_cd == 'PSSS')
      return roles.includes('SSSME');
    else if(match.param_cd == 'PL' || match.param_cd == 'PCO')
      return roles.includes('LEGAL');

    return false;
  }
}
