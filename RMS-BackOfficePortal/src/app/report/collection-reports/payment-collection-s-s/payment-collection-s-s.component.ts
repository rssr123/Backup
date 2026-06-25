import { Component, OnInit, Output, EventEmitter } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { environment } from 'src/environments/environment';
import { NgbPaginationConfig } from '@ng-bootstrap/ng-bootstrap';
import { MFT, MFTWF, MasterTaskList, Param, SourceSystemCode, User } from '../../../core/models/entity';
import { Router } from '@angular/router';
import { trigger, state, style, transition, animate } from '@angular/animations';
import { Systemstatus } from '../../../shared/enums/systemstatus';
import moment from 'moment';
import { DataService } from '../../../core/services/data.service';
import { fadeInOut } from '../../../shared/animation';
import { DatePipe, formatDate } from '@angular/common';
import { saveAs } from "file-saver";
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { catchError } from 'rxjs/operators';
import { perm } from 'src/permissions/perm';
import { AuthService } from 'src/app/core/services/auth.service';

@Component({
	selector: 'app-payment-collection-s-s',
  templateUrl: './payment-collection-s-s.component.html',
  styleUrls: ['./payment-collection-s-s.component.scss']
})
export class PaymentCollectionSSComponent implements OnInit {

	dlUrl = environment.apiUrl + '/api/report/v1/pymt_col_report';

	months: string[] = ['January', 'February', 'March', 'April', 'May', 'June', 'July', 'August', 'September', 'October', 'November', 'December'];
	pymtMthds: any = [{ value: 'CC', label: 'Credit Card' },
	{ value: 'DD', label: 'Direct Debit' },
	{ value: 'WA', label: 'e-Wallet' }];
	groupType: string = 'pymt_col_s_s';
	start_month: number | null = 1;
	end_month: number | null = 1;
	search_year: number | null = 0;
	startDateString: string | null = null;
	endDateString: string | null = null;
	pymt_Mthd: string[] = ['CC'];
	date_range: Date[] = [new Date((new Date()).getFullYear() + '-01-01'), new Date()];

	showFilterAlert = false;
	showNoDataAlert = false;
	isLoading = false;

	showFilterAlertBox() {
		this.showFilterAlert = true;
		setTimeout(() => this.showFilterAlert = false, 5000);
	}
	showNoDataAlertBox() {
		this.showNoDataAlert = true;
		setTimeout(() => this.showNoDataAlert = false, 5000);
	}

	// Configuring Permissions for User and roles variables
	permReport = perm.Reporting_and_Analysis_View_Payment_Collection_Report_Payment_Mode + "," + perm.Reporting_and_Analysis_Download_PDF_Payment_Mode + "," +
		perm.Reporting_and_Analysis_Download_Excel_Payment_Mode + "," +
		perm.Reporting_and_Analysis_Download_CSV_Payment_Mode + "," +
		perm.Reporting_and_Analysis_View_Payment_Collection_Report_Source_System + "," +
		perm.Reporting_and_Analysis_Download_PDF_Source_System + "," +
		perm.Reporting_and_Analysis_Download_Excel_Source_System + "," +
		perm.Reporting_and_Analysis_Download_CSV_Source_System + "," +
		perm.Reporting_and_Analysis_View_Payment_Collection_Report_Fee_Detail_ID + "," +
		perm.Reporting_and_Analysis_Download_PDF_Fee_Detail_ID + "," +
		perm.Reporting_and_Analysis_Download_Excel_Fee_Detail_ID + "," +
		perm.Reporting_and_Analysis_Download_CSV_Fee_Detail_ID;
	// all the perm_cd for this module seperated with comma

	permReportAllow = ""; // variable to store allowed permission for the user
	permViewPaymentModeAllow: number = 0;
	permDownloadPDFPaymentModeAllow: number = 0;
	permDownloadExcelPaymentModeAllow: number = 0;
	permDownloadCSVPaymentModeAllow: number = 0;
	permViewSourceSystemAllow: number = 0;
	permDownloadPDFSourceSystemAllow: number = 0;
	permDownloadExcelSourceSystemAllow: number = 0;
	permDownloadCSVSourceSystemAllow: number = 0;
	permViewFeeDetailIDAllow: number = 0;
	permDownloadPDFFeeDetailIDAllow: number = 0;
	permDownloadExcelFeeDetailIDAllow: number = 0;
	permDownloadCSVFeeDetailIDAllow: number = 0;
	// end configuration

	constructor(private http: HttpClient, config: NgbPaginationConfig, private router: Router, private dataService: DataService, private authService: AuthService, public datepipe: DatePipe) {
		config.maxSize = 3;
		config.boundaryLinks = true;
	}

	ngOnInit() {
		this.resetWithDefaults();
		this.loadPermission();
	}

	changeFilters(filterType: string | null) {
		var startMonthContainer = document.getElementById('start_month_container');
		var endMonthContainer = document.getElementById('end_month_container');
		var searchYearContainer = document.getElementById('search_year_container');
		var feeDetailIdDateRangeContainer = document.getElementById('fee_detail_date_range_container');
		var pymtMtdContainer = document.getElementById('pymt_mtd_container');

		if (filterType == 'pymt_col_fee_dt_id') {
			startMonthContainer!.hidden = true;
			endMonthContainer!.hidden = true;
			searchYearContainer!.hidden = true;
			feeDetailIdDateRangeContainer!.hidden = false;
			pymtMtdContainer!.hidden = false;

			var ISOStringDate = new Date(new Date().getTime() - (new Date().getTimezoneOffset() * 60 * 1000));
			this.endDateString = ISOStringDate.toISOString().split('T')[0];
			var dateParts = this.endDateString.split('-');
			if (this.start_month === null)
				this.start_month = 1;
			if (this.end_month === null)
				this.end_month = Number(dateParts[1]);
			if (this.search_year === null || this.search_year < 999)
				this.search_year = Number(dateParts[0]);
		}
		else {
			startMonthContainer!.hidden = false;
			endMonthContainer!.hidden = false;
			searchYearContainer!.hidden = false;
			feeDetailIdDateRangeContainer!.hidden = true;
			pymtMtdContainer!.hidden = true;
			if (this.date_range === null || this.date_range.length === 0)
				this.date_range = [new Date((new Date()).getFullYear() + '-01-01'), new Date()];
			if (this.pymt_Mthd === null || this.pymt_Mthd.length === 0)
				this.pymt_Mthd = ['CC'];
		}
	}

	resetWithDefaults() {
		var ISOStringDate = new Date(new Date().getTime() - (new Date().getTimezoneOffset() * 60 * 1000));
		this.endDateString = ISOStringDate.toISOString().split('T')[0];
		var dateParts = this.endDateString.split('-');
		this.end_month = Number(dateParts[1]);
		this.search_year = Number(dateParts[0]);
		this.startDateString = dateParts[0] + "-01-01";
		this.start_month = 1;
		this.groupType = 'pymt_col_s_s';
		this.date_range = [new Date((new Date()).getFullYear() + '-01-01'), new Date()];
		this.pymt_Mthd = ['CC'];
	}

	reset() {
		this.endDateString = null;
		this.end_month = null;
		this.search_year = null;
		this.startDateString = null;
		this.start_month = null;
		this.date_range = [];
		this.pymt_Mthd = [];
	}

	dlbuttonPerms() {
		if (this.isLoading || this.selectedFileType.length < 1 || (this.groupType === null) || (this.start_month === null) || (this.end_month === null) || (this.pymt_Mthd === null) || (this.search_year === null) 
			|| (this.search_year < 999) || (this.pymt_Mthd.length === 0) || (this.date_range.length === 0)
			|| (this.groupType === 'pymt_col_pymt_md' && this.permDownloadExcelPaymentModeAllow === 0)
			|| (this.groupType === 'pymt_col_s_s' && this.permDownloadExcelSourceSystemAllow === 0)
			|| (this.groupType === 'pymt_col_fee_dt_id' && this.permDownloadExcelFeeDetailIDAllow === 0))
			return true;
		return false;
	}

	dlReport(fileType: string) {

		if (this.groupType === 'pymt_col_fee_dt_id') {
			this.startDateString = this.datepipe.transform(this.date_range[0], 'yyyy-MM-dd');
			this.endDateString = this.datepipe.transform(this.date_range[1], 'yyyy-MM-dd');
		}
		else {
			this.startDateString = (this.search_year || 0).toString() + '-'
				+ ((this.start_month || 0).toString().length < 2 ? '0' + (this.start_month || 0).toString() : (this.start_month || 0).toString())
				+ '-01';
			this.endDateString = (this.search_year || 0).toString() + '-'
				+ ((this.end_month || 0).toString().length < 2 ? '0' + (this.end_month || 0).toString() : (this.end_month || 0).toString())
				+ '-01';    //SQL will get last day of month, no need to calculate it.
		}

		//console.log(fileType + '-' + this.startDateString + '-' + this.endDateString + '-' + this.groupType);

		// Set your authorization header
		const headers = new HttpHeaders({
			Authorization: environment.authKey,
			'Content-Type': 'application/json; application/octet-stream;'
		});

		// Create the request body with your form data
		var requestBody: { [k: string]: any } = {
			i_start_date: this.startDateString,
			i_end_date: this.endDateString,
			i_report_type: this.groupType,
			i_report_format: fileType
		};

		if (this.groupType === 'pymt_col_fee_dt_id')
			requestBody['i_pymt_mthd'] = this.pymt_Mthd.toString();

		var fileTypeMime = fileType == 'pdf' ? 'application/pdf' : fileType == 'xlsx' ? 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' : fileType == 'csv' ? 'text/csv' :
			'application/octet-stream';
		this.isLoading = true;
		this.http.post(this.dlUrl, requestBody, { observe: 'response', responseType: 'arraybuffer', headers: headers })
		.subscribe(response => {
			this.isLoading = false;
			if(response.headers.get('Content-Type')!.includes('application/json')){	//Failed because did not return blob data
				//var decoded = JSON.parse(String.fromCharCode.apply(null, Array.from<number>(new Uint8Array(response.body as ArrayBuffer))));
				//console.log(decoded);
				this.showNoDataAlertBox();
			}
			else{
				var blob = new Blob([response.body as unknown as Blob], { type: fileTypeMime });
				saveAs(blob, response.headers.get('content-disposition')!.split('filename=')[1]);
			}
		});
	}

	loadPermission() {
		this.authService.checkUserRole(this.authService.username, this.permReport)
			.subscribe(
				(response: any) => {
					this.permReportAllow = response.data;
					this.permViewPaymentModeAllow = this.permReportAllow.includes(perm.Reporting_and_Analysis_View_Payment_Collection_Report_Payment_Mode) ? 1 : 0;
					this.permDownloadPDFPaymentModeAllow = this.permReportAllow.includes(perm.Reporting_and_Analysis_Download_PDF_Payment_Mode) ? 1 : 0;
					this.permDownloadExcelPaymentModeAllow = this.permReportAllow.includes(perm.Reporting_and_Analysis_Download_Excel_Payment_Mode) ? 1 : 0;
					this.permDownloadCSVPaymentModeAllow = this.permReportAllow.includes(perm.Reporting_and_Analysis_Download_CSV_Payment_Mode) ? 1 : 0;
					// if(this.permViewPaymentModeAllow == 1){
					// 	this.groupType = 'pymt_col_pymt_md';
					// }
					//console.log('This grouptype is : '+ this.groupType)
					//console.log( "Payment mode :"+ this.permViewPaymentModeAllow, this.permDownloadPDFPaymentModeAllow, this.permDownloadExcelPaymentModeAllow, this.permDownloadCSVPaymentModeAllow);

					this.permViewSourceSystemAllow = this.permReportAllow.includes(perm.Reporting_and_Analysis_View_Payment_Collection_Report_Source_System) ? 1 : 0;
					this.permDownloadPDFSourceSystemAllow = this.permReportAllow.includes(perm.Reporting_and_Analysis_Download_PDF_Source_System) ? 1 : 0;
					this.permDownloadExcelSourceSystemAllow = this.permReportAllow.includes(perm.Reporting_and_Analysis_Download_Excel_Source_System) ? 1 : 0;
					this.permDownloadCSVSourceSystemAllow = this.permReportAllow.includes(perm.Reporting_and_Analysis_Download_CSV_Source_System) ? 1 : 0;
					//console.log( "SS :"+ this.permViewSourceSystemAllow, this.permDownloadPDFSourceSystemAllow, this.permDownloadExcelSourceSystemAllow, this.permDownloadCSVSourceSystemAllow);


					this.permViewFeeDetailIDAllow = this.permReportAllow.includes(perm.Reporting_and_Analysis_View_Payment_Collection_Report_Fee_Detail_ID) ? 1 : 0;
					this.permDownloadPDFFeeDetailIDAllow = this.permReportAllow.includes(perm.Reporting_and_Analysis_Download_PDF_Fee_Detail_ID) ? 1 : 0;
					this.permDownloadExcelFeeDetailIDAllow = this.permReportAllow.includes(perm.Reporting_and_Analysis_Download_Excel_Fee_Detail_ID) ? 1 : 0;
					this.permDownloadCSVFeeDetailIDAllow = this.permReportAllow.includes(perm.Reporting_and_Analysis_Download_CSV_Fee_Detail_ID) ? 1 : 0;
					//console.log( "Fee detail id : "+ this.permViewFeeDetailIDAllow, this.permDownloadPDFFeeDetailIDAllow, this.permDownloadExcelFeeDetailIDAllow, this.permDownloadCSVFeeDetailIDAllow);

					//console.log(response);
				}
			);
	}


	selectedFileType: string = 'pdf';

	fileTypes = [
		{ value: 'xlsx', label: 'Excel' },
		{ value: 'csv', label: 'CSV' },
		{ value: 'pdf', label: 'PDF' }
	];

	submitDownload(): void {
		if (this.selectedFileType) {
			this.dlReport(this.selectedFileType);
		}
	}



}
