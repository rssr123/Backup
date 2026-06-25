import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { environment } from 'src/environments/environment';
import { TaxCodeTest } from 'src/app/core/models/tax-code.interface';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { AuthService } from 'src/app/core/services/auth.service';
import { NgbPaginationConfig } from '@ng-bootstrap/ng-bootstrap';
import { Router } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import { TranslateService } from '@ngx-translate/core';
import { GlobalService } from 'src/app/shared/global.service';
import { ParamService } from 'src/app/core/services/param.service';
import { StateData } from '../core/models/state.interface';
import { Systemstatus } from '../shared/enums/systemstatus';
import { formatDate } from '@angular/common';

/*
This is a sample module which demonstrates how to implement dynamic rows and Auto suggestions with text input in Angular.
Please refer to the following functions and html code for UI design. 
*/

@Component({
  selector: 'app-dynamicrowssample',
  templateUrl: './dynamicrowssample.component.html',
  styleUrls: ['./dynamicrowssample.component.scss']
})
export class DynamicrowssampleComponent implements OnInit {
  page = environment.DefaultPage;
  itemsPerPage = environment.ItemPerPage;
  model: TaxCodeTest[] = [];
  totalRecords: number = 0;

  taxCode: String | null = null;
  taxCodeNameEN: String | null = null;
  taxCodeNameBM: String | null = null;
  taxPercentage: String | null = null;
  modifiedBy: String | null = null;

  isDisplay: boolean = false;
  isLoading: boolean = false;
  isAdd: boolean = true;

  selected: Date[] | null = null;
  minDate = new Date();

  states: StateData[] = [];
  selectedState: string = Systemstatus.Active;

  // Auto suggestion sample data, when develop can use sp_get API to populate the array to have the same behaviour as the sample
  suggestions: string[] = ['Apple', 'Avocado', 'Banana', 'Cherry', 'Date', 'Elderberry', 'Fig', 'Grape', 'Honeydew', 'Kiwi', 'Lemon', 'Mango', 'Nectarine', 'Orange', 'Peach', 'Quince', 'Raspberry', 'Strawberry', 'Tangerine', 'Ugli', 'Vanilla', 'Watermelon', 'Ximenia', 'Yellow Plum', 'Zucchini', 'Tax', 'Tax Code', 'Tax Code Name', 'Tax Percentage', 'Modified By', 'Status'];
  filteredSuggestions: string[] = [];
  currentInput: string = '';
  activeRow: TaxCodeTest | null = null;

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
    this.translate.setDefaultLang(this.globalService.getGlobalValue());
    this.translate.use(this.globalService.getGlobalValue());
  }

  ngOnInit(): void {
    this.minDate.setMonth(this.minDate.getMonth() - 1);
    this.selected = null;
    this.loadStates();
    this.loadData();
  }


  filterSuggestions(input: string, row: TaxCodeTest): void {
    row.autoSuggest = input;
    // Set the active row
    this.activeRow = row;
    if (input) {
      row.filteredSuggestions = this.suggestions.filter((suggestion) =>
        suggestion.toLowerCase().startsWith(input.toLowerCase())
      );
    } else {
      row.filteredSuggestions = [];
    }
    // Clear filtered suggestions for other rows
    this.model.forEach((r) => {
      if (r !== row) {
        r.filteredSuggestions = [];
      }
    });
  }

  selectSuggestion(suggestion: string, row: TaxCodeTest): void {
    row.autoSuggest = suggestion;
    row.filteredSuggestions = [];
    this.activeRow = row;
  }

  loadData() {
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    this.isDisplay = true;
    this.isLoading = true;
    const url = environment.apiUrl + '/api/tc/v1/gettaxcode';

    const Body: any = {
      i_page: this.page.toString(),
      i_size: this.itemsPerPage.toString(),
    };

    if (this.taxCode && this.taxCode.trim()) Body.i_tax_cd = this.taxCode;
    if (this.taxCodeNameEN && this.taxCodeNameEN.trim()) Body.i_tax_cd_nm_en = this.taxCodeNameEN;
    if (this.taxCodeNameBM && this.taxCodeNameBM.trim()) Body.i_tax_cd_nm_bm = this.taxCodeNameBM;
    if (this.taxPercentage && this.taxPercentage.trim()) Body.i_tax_pct = this.taxPercentage;
    if (this.modifiedBy && this.modifiedBy.trim()) Body.i_modified_by = this.modifiedBy;

    if (this.selected) {
      Body.i_dt_modified_fr = formatDate(this.selected[0], 'YYYY-MM-dd', 'en');
      this.selected[1].setDate(this.selected[1].getDate() + 1);
      Body.i_dt_modified_to = formatDate(this.selected[1], 'YYYY-MM-dd', 'en');
    }

    let temp = "";
    if (this.selectedState.length > 0 && (this.selectedState == Systemstatus.Active || this.selectedState == Systemstatus.Inactive)) {
      temp = this.selectedState;
    }

    if (temp == Systemstatus.Active || temp == Systemstatus.Inactive) {
      Body.i_status = temp;
    }

    this.http.post(url, Body, { headers }).subscribe(
      (response: any) => {
        this.model = response.data;
        this.totalRecords = response.data.length > 0 ? response.data[0].total : 0;
        this.isLoading = false;
      },
      (error) => {
        console.error(error);
        this.isLoading = false;
      }
    );
  }

  loadStates() {
    this.ParamService.getStates('1', '100', '', 'Status').subscribe(
      (response: any) => {
        if (response && response.data && Array.isArray(response.data)) {
          this.states = [{ param_cd: '', nm_en: 'All', nm_bm: 'All', total: 5 }, ...response.data];
        } else {
          console.error('Invalid response format:', response);
        }
      },
      (error) => {
        console.error('Error retrieving the status:', error);
      }
    );
  }

  addRow() {
    this.model.push({
      tax_cd: '',
      tax_pct: 0,
      tax_cd_nm_en: '',
      tax_cd_nm_bm: '',
      modifiedBy: '',
      dtModified: new Date(),
      isNew: true,
      taxCd: undefined,
      dtCreated: new Date(),
      createdBy: '',
      status_en: '',
      status_bm: '',
      isEditable: true,
      autoSuggest: '',
      filteredSuggestions: []
    });
    this.isAdd = false;
    this.activeRow = null;
  }

  saveRow(index: number) {
    const row = this.model[index];
    const body: any = {
      i_page: '1',
      i_size: '1',
      i_tax_cd: row.tax_cd,
      i_tax_cd_nm_en: row.tax_cd_nm_en,
      i_tax_cd_nm_bm: row.tax_cd_nm_bm,
      i_tax_pct: row.tax_pct,
      i_status: row.status_en
    };
    const url = environment.apiUrl + '/api/tc/v1/addtaxcode';
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    this.http.post(url, body, { headers }).subscribe(
      (response: any) => {
        row.isNew = false;
        row.isEditable = false;
        this.loadData();  // Reload data to update table
      },
      (error) => {
        console.error('Error adding row:', error);
      }
    );
  }

  editRow(index: number) {
    this.model[index].isEditable = true; // Enable editing for the specified row
  }

  updateRow(index: number) {
    const row = this.model[index];
    const body: any = {
      i_tax_cd: row.tax_cd,
      i_tax_cd_nm_en: row.tax_cd_nm_en,
      i_tax_cd_nm_bm: row.tax_cd_nm_bm,
      i_tax_pct: row.tax_pct,
      i_modified_by: row.modifiedBy,
      i_status: 'A'
    };

    const url = environment.apiUrl + '/api/tc/v1/updtaxcode';
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    this.http.post(url, body, { headers }).subscribe(
      (response: any) => {
        row.isNew = false;
        row.isEditable = false;
        this.loadData();  // Reload data to reflect changes
      },
      (error) => {
        console.error('Error updating row:', error);
      }
    );
  }

  deleteRow(index: number) {
    const row = this.model[index];
    const body: any = {
      i_tax_cd: row.tax_cd,
      i_modified_by: row.modifiedBy,
      i_status: 'D'
    };
    const url = environment.apiUrl + '/api/tc/v1/deltaxcode';
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    this.http.post(url, body, { headers }).subscribe(
      (response: any) => {
        this.model.splice(index, 1);  // Remove from UI
        this.totalRecords--;
        this.loadData();  // Reload data to update table
      },
      (error) => {
        console.error('Error deleting row:', error);
      }
    );
  }

  removeNewRow(index: number) {
    this.model.splice(index, 1);
    this.isAdd = true;
  }
}



