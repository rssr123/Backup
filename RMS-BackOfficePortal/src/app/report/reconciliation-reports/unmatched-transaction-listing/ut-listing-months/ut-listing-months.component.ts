import { Component, OnInit } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { environment } from 'src/environments/environment';
import { NgbPaginationConfig } from '@ng-bootstrap/ng-bootstrap';
import { UnmatchTransMonth, ExportUnmatchTransMonth } from '../../../../core/models/unmatchtrans';
import { Router } from '@angular/router';
import { trigger, state, style, transition, animate } from '@angular/animations';
import moment from 'moment';
import { DataService } from '../../../../core/services/data.service';
import { fadeInOut } from '../../../../shared/animation';
import { DatePipe, formatDate } from '@angular/common';
import { ParamService } from '../../../../core/services/param.service';
import * as XLSX from 'xlsx';
import { ParamData } from 'src/app/core/models/param.interface';
import { TranslateService } from '@ngx-translate/core';
import { GlobalService } from 'src/app/shared/global.service';
import { AuthService } from 'src/app/core/services/auth.service';
import { perm } from 'src/permissions/perm';
import jsPDF from 'jspdf';
import 'jspdf-autotable';

@Component({
  selector: 'app-ut-listing-months',
  templateUrl: './ut-listing-months.component.html',
  styleUrls: ['./ut-listing-months.component.scss'],
  animations: [fadeInOut]
})
export class UtListingMonthsComponent implements OnInit {

  maxDate = new Date();

  formatNumber(value: string | number): string {
    // Ensure value is treated as a number
    const numericValue = typeof value === 'string' ? parseFloat(value) : value;

    // Check if numericValue is NaN or not
    if (isNaN(numericValue)) {
      return '-';
    }

    // Format to two decimal places with thousand separators
    return numericValue.toFixed(2).replace(/\B(?=(\d{3})+(?!\d))/g, ',');
  }


  page = environment.DefaultPage;
  itemsPerPage = environment.ItemPerPage;
  dropDownSize = environment.DropDownSize;
  // mfts: MFT[] = [];
  // exportMFT: ExportMFT[] = [];



  utlm: UnmatchTransMonth[] = [];
  exportUtlm: ExportUnmatchTransMonth[] = [];

  periodKey: Date | null = null;


  totalRecords: number = 0;

  totalMFTRecords: number = 0;
  isDisplay: boolean = false;
  isLoading: boolean = false;
  errorMessagesAccessDenied: string[] = [];
  errorAccessDenied: boolean = false;
  invalidInputFrom: boolean = false;
  invalidInputTo: boolean = false;

  // //date range picker
  // selected!: Date[];//{ start?: moment.Moment; end?: moment.Moment };
  // bsValue = new Date();
  // tempDate !: Date;
  // minDate = new Date();
  // //date range picker 

  feeDetailId: String | null = null;
  unitFeeRangeFr: String | null = null;
  unitFeeRangeTo: String | null = null;
  sourceSystemCode: String | null = null;
  taxCode: String | null = null;
  modifiedBy: String | null = null;
  alertMessage: string | undefined = undefined;
  submittedForApprovalBox: boolean = false;
  checkboxOptions: string[] | undefined = undefined;
  alertMessageTaskPending: string | undefined = undefined;
  alertMessageAccessDenied: string | undefined = undefined;

  //for alert box start
  showSubmittedForApprovalAlert = false;
  showSubmittedForApprovalAlertBox() {
    this.showSubmittedForApprovalAlert = true;
    setTimeout(() => this.showSubmittedForApprovalAlert = false, 2000);
  }

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
  //for alert box end

  //toogle start
  rightSectionCollapsed: boolean = true;

  //default pagination
  selectedValue = environment.dropdownOptions[0];
  dropdownOptions = environment.dropdownOptions;

  states: ParamData[] = [];

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
    this.submittedForApprovalBox = false;
  }

  AlertBoxInitialize() {
    if (this.submittedForApprovalBox) {
      this.showSubmittedForApprovalAlertBox();
    }
  }




  // Configuring Permissions for User and roles variables
  permReport = perm.Reporting_and_Analysis_Unmatched_Transaction_Listing

  permReportAllow = ""; // variable to store allowed permission for the user
  permListAllow: number = 0; // if 0 then not allow to view listing page, else allow

  constructor(
    private datePipe: DatePipe,
    private http: HttpClient,
    config: NgbPaginationConfig,
    private router: Router,
    private ParamService: ParamService,
    private translate: TranslateService,
    private globalService: GlobalService,
    private authService: AuthService) {
    config.maxSize = 3;
    config.boundaryLinks = true;
    this.translate.setDefaultLang(this.globalService.getGlobalValue()); // default language
    this.translate.use(this.globalService.getGlobalValue());
    this.maxDate.setFullYear(this.maxDate.getFullYear(), this.maxDate.getMonth(), this.maxDate.getDate());

  }



  ngOnInit(): void {

    const currentDate = new Date();
    this.periodKey = new Date(currentDate.getFullYear(), currentDate.getMonth(), currentDate.getDate());

    //put default box above alert message
    this.DefaultBox()
    this.alertMessage = history.state.alert_msg;
    this.alertMessageTaskPending = history.state.alert_msg_task_pending;
    this.alertMessageAccessDenied = history.state.alert_msg_access_denied;
    if (this.alertMessage !== undefined) {
      if (this.alertMessage === "submittedForApproval") {
        this.submittedForApprovalBox = true;
      }
    }

    if (this.alertMessageAccessDenied !== undefined) {
      this.errorAccessDenied = true;
      this.errorMessagesAccessDenied.push(this.alertMessageAccessDenied);
    }
    // Reset alert_msg in history state so if refresh page, alert message will not persist
    //history.replaceState({ ...history.state, alert_msg: undefined }, '');
    history.replaceState({ ...history.state, alert_msg: undefined, alert_msg_access_denied: undefined }, '');


    //load data must be place at last
    this.loadData();
    this.loadPermission();
  }


  loadData() {

    this.isLoading = true;

    const url = environment.apiUrl + '/api/utl/v1/getunmatchtransmonth';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json'
    });


    const Body: any = {
      i_period_key: this.periodKey
    };

    if (this.periodKey) {
      Body.i_period_key = formatDate(this.periodKey, 'YYYY-MM-dd', 'en');
    }



    let temp = "";

    this.http.post(url, Body, { headers }).subscribe(
      (response: any) => {

        if (response.data.length === 0) {
          this.totalRecords = 0;
          this.showResultAlertBox();
          this.isDisplay = false;
          this.isLoading = false;
        } else {
          this.utlm = response.data;
          this.totalRecords = response.data[0].total;
          this.totalMFTRecords = response.data[0].total;
          this.AlertBoxInitialize();
          this.DefaultBox();
          this.isDisplay = true;
          this.isLoading = false;
        }
        // console.log("MFT legnth is" +this.mfts.length)
        //  console.log(response.data);
        //   console.log(this.totalRecords);
      },
      (error) => {
        console.error(error);
        this.isDisplay = false;
        this.isLoading = false;
        this.showGenericAlertBox();

      }
    );

  }

  loadPermission() {
    this.authService.checkUserRole(this.authService.username, this.permReport)
      .subscribe(
        (response: any) => {
          this.permReportAllow = response.data;
          this.permListAllow = this.permReportAllow.includes(perm.Reporting_and_Analysis_Unmatched_Transaction_Listing) ? 1 : 0;
          if (this.permListAllow === 0) {
            console.log("access-denied");
            this.router.navigate(['/access-denied']);
            return; // Exit the function to prevent further execution
          }
        }
      );
  }

  addSelected() {
    // this.router.navigate(['/mft-fa-fa-rqt-add']);
  }



  apply(): void {
    this.loadData();
    this.rightSectionCollapsed = true;
  }

  convertToFixedTwoDecimal(value: string): string {
    if (value === "N/A") {
      return value;
    }
    return parseFloat(value).toLocaleString(undefined, { maximumFractionDigits: 2 });
  }


  viewSelected(item: any) {

    const dummydate = item.dummydate
    //const ripl_id = item.ripl_id
    this.router.navigate(['/ut-listing-days'], { state: { dummydate } });

  }



  fileType: any = [
    { value: 'pdf', label: 'PDF' },
    { value: 'csv', label: 'CSV' },
    { value: 'xlsx', label: 'XLSX' }
  ];
  selectedFileType: string = 'pdf'; // Default value






  async exportSelected() {
    console.log('Selected File Type:', this.selectedFileType); // Debug log

    const url = environment.apiUrl + '/api/utl/v1/getunmatchtransmonth';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    const Body: any = {
      i_period_key: '2024-01-01'
    };

    if (this.periodKey) {
      Body.i_period_key = formatDate(this.periodKey, 'YYYY-MM-dd', 'en');
    }

    // Extract the year from i_period_key
    const year = Body.i_period_key.split('-')[0];

    this.http.post(url, Body, { headers }).subscribe(
      (response: any) => {
        if (response.data.length === 0) {
          console.log("Export not success");
        } else {
          this.exportUtlm = response.data;

          // Change key name of exportUtlm
          this.exportUtlm = response.data.map((item: any) => ({
            'Period': item.period,
            'In': isNaN(parseFloat(item.in)) ? '' : parseFloat(item.in).toFixed(2),
            'Out': isNaN(parseFloat(item.out)) ? '' : parseFloat(item.out).toFixed(2),
            'Variance': isNaN(parseFloat(item.variance)) ? '' : parseFloat(item.variance).toFixed(2),
            'Period Balance': item.periodbalance
          }));

          const fileName = `Unmatched Transaction Listing - Months View - ${year}`;

          if (this.selectedFileType === 'xlsx') {
            const ws: XLSX.WorkSheet = XLSX.utils.json_to_sheet(this.exportUtlm);

            // Add year at the top of the header
            XLSX.utils.sheet_add_aoa(ws, [['Period', 'In', 'Out', 'Variance', 'Period Balance']], { origin: 'A1' });

            const wb: XLSX.WorkBook = XLSX.utils.book_new();
            XLSX.utils.book_append_sheet(wb, ws, 'Sheet1');
            XLSX.writeFile(wb, `${fileName}.xlsx`);
            console.log("Export successfully as XLSX");
          } else if (this.selectedFileType === 'csv') {
            const ws: XLSX.WorkSheet = XLSX.utils.json_to_sheet(this.exportUtlm);
            const csv: string = XLSX.utils.sheet_to_csv(ws);
            const blob = new Blob([csv], { type: 'text/csv;charset=utf-8;' });
            const link = document.createElement('a');
            if (link.download !== undefined) {
              const url = URL.createObjectURL(blob);
              link.setAttribute('href', url);
              link.setAttribute('download', `${fileName}.csv`);
              link.style.visibility = 'hidden';
              document.body.appendChild(link);
              link.click();
              document.body.removeChild(link);
            }
            console.log("Export successfully as CSV");
          } else if (this.selectedFileType === 'pdf') {
            const doc = new jsPDF();

            // Add year at the top of the header
            doc.setFontSize(12);
            doc.text(`Unmatched Transactions Listing Month Year - ${year}`, 14, 10);
            (doc as any).autoTable({
              startY: 20,
              head: [['Period', 'In', 'Out', 'Variance', 'Period Balance']],
              body: this.exportUtlm.map((item: any) => [item.Period, item.In, item.Out, item.Variance, item['Period Balance']]),
              headStyles: {
                fillColor: '#BDBDBD',
                textColor: '#000000' // Set text color to black
              },
              willDrawCell: (data: {
                row: { index: number; }; cell: {
                  styles: {
                    border: { top: { style: string; width: number; }; left: { style: string; width: number; }; bottom: { style: string; width: number; }; right: { style: string; width: number; }; }; lineWidth: number; textColor: string; fontStyle: string;
                  };
                };
              }) => {
                console.log('Applying styles to the last row', data.row.index, this.exportUtlm.length);
                // Check if the current row is the last row
                if (data.row.index === this.exportUtlm.length - 1) {
                  // Make the border thicker and text bold for the last row
                  data.cell.styles.lineWidth = 0.5; // Thicker border for the last row
                  data.cell.styles.textColor = '#000000'; // Ensure text color is black for visibility
                  data.cell.styles.fontStyle = 'bold'; // Bold text for the last row
                }
                // Standard border settings for all cells
                data.cell.styles.border = {
                  top: { style: 'solid', width: 0.5 },
                  left: { style: 'solid', width: 0.5 },
                  bottom: { style: 'solid', width: 0.5 },
                  right: { style: 'solid', width: 0.5 }
                };
              },
              didDrawCell: (data: { cell?: any; row: { index: number; }; doc?: any; }) => {
                const { doc, cell, row } = data;
                // Ensure the entire table has borders
                if (cell && doc) {
                  doc.rect(cell.x, cell.y, cell.width, cell.height);
                  // Bold the text and border for the last row
                  if (row.index === this.exportUtlm.length - 1) {
                    doc.setFont('helvetica', 'bold');
                    doc.setDrawColor(0); // Set draw color to black for the border
                    doc.setLineWidth(0.5); // Thicker border for the last row
                    doc.rect(cell.x, cell.y, cell.width, 0); // Redraw the border for the last row to make it thicker
                  }
                }
              }

            });
            doc.save(`${fileName}.pdf`);
            console.log("Export successfully as PDF");
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




}
