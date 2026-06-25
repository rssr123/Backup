import { Component, OnInit } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { environment } from 'src/environments/environment';
import { NgbPaginationConfig } from '@ng-bootstrap/ng-bootstrap';
import { ParamData } from 'src/app/core/models/param.interface';
import { Router } from '@angular/router';
import { ParamService } from '../../core/services/param.service';
import { MatDialog } from '@angular/material/dialog';
import { Systemstatus } from '../../shared/enums/systemstatus';
import { bankReconDetails } from 'src/app/core/models/entity';
import { TranslateService } from '@ngx-translate/core';
import { GlobalService } from 'src/app/shared/global.service';
import { BankReconBkListingComponent } from '../bank-recon-bk-listing/bank-recon-bk-listing.component';
import { perm } from 'src/permissions/perm';
import { AuthService } from 'src/app/core/services/auth.service';


@Component({
  selector: 'app-bank-recon-no-bank-listing',
  templateUrl: './bank-recon-no-bank-listing.component.html',
  styleUrls: ['./bank-recon-no-bank-listing.component.scss']
})
export class BankReconNoBankListingComponent implements OnInit {

  page = environment.DefaultPage;
  itemsPerPage = environment.ItemPerPage;
  errorMessage: string | null = null;
  isDisplay: boolean = false;
  isLoading: boolean = false;
  isReadOnly = false;
  isEmptyResult = false;
  totalRecords: number = 0;

  //default pagination
  selectedValue = environment.dropdownOptions[0];
  dropdownOptions = environment.dropdownOptions;

  file_content: string | null = null;

  pgTxnBox: boolean = false;
  bankTxnBox: boolean = false;


  filenm: string | null = null;
  filesizekb: string | null = null;
  uploadedby: string | null = null;
  dtuploaded: string | null = null;

  //Model
  model: bankReconDetails[] = [];
  task_no: string | null = null;
  file_nm: string | null = null;
  file_size: string | null = null;
  uploaded_by: string | null = null;
  dt_uploaded: string | null = null;

  // Configuring Permissions for User and roles variables
  permBRN = perm.BP_036_Reconciliation_Of_Collection_Download_Bank_Statement_File;
  // all the perm_cd for this module seperated with comma

  permBRNAllow = ""; // variable to store allowed permission for the user
  permDLBankAllow: number = 0; // if 0 then not allow to view listing page, else allow


  constructor(
    private http: HttpClient,
    config: NgbPaginationConfig,
    private router: Router,
    public dialog: MatDialog,
    private ParamService: ParamService,
    private translateService: TranslateService,
    private globalService: GlobalService,
    private authService: AuthService
  ) {
    config.maxSize = environment.PaginationMaxSize;
    config.boundaryLinks = true;
    this.translateService.setDefaultLang(this.globalService.getGlobalValue());
    this.translateService.use(this.globalService.getGlobalValue());
  }

  ngOnInit(): void {
    this.loadData();
  }

  reset() {
    this.filenm = null;
    this.filesizekb = null;
    this.uploadedby = null;
    this.dtuploaded = null;
  }

  apply() {
    this.loadData();
    
  }

  LoadData(singleItem: number) {
    this.selectedValue = singleItem;
    this.itemsPerPage = this.selectedValue;
    this.loadData();
  }

  DefaultBox() {
    this.pgTxnBox = false;
    this.bankTxnBox = false;
  }

  bankTxn(): void {
    this.DefaultBox();
    const dialogRef = this.dialog.open(BankReconBkListingComponent, {
      height: '80%',
    });

    this.bankTxnBox = true;
  }

  AlertBoxInitialize() {
    if (this.pgTxnBox) {
      this.bankTxn();
    } else if (this.bankTxnBox) {
      this.bankTxn();
    }
  }

  downloadFileContent(fileName: string, fileContent: string): void {
    this.isLoading = true;
    const binaryString = window.atob(fileContent);
    const len = binaryString.length;
    const uint8Array = new Uint8Array(len);

    for (let i = 0; i < len; i++) {
      uint8Array[i] = binaryString.charCodeAt(i);
    }

    const blob = new Blob([uint8Array], { type: 'text/plain' });

    const url = URL.createObjectURL(blob);

    const anchor = document.createElement('a');
    anchor.href = url;
    anchor.download = fileName;

    document.body.appendChild(anchor);
    anchor.click();

    document.body.removeChild(anchor);
    URL.revokeObjectURL(url);
  }

  downloadFile(bank_file_nm: string): void {
    const url = environment.apiUrl + '/api/brdc/v1/getrcbkdoc';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    const Body: any = {
      task_no: history.state.task_id,
      file_nm: bank_file_nm
    };

    this.http.post(url, Body, { headers }).subscribe(
      (response: any) => {
        // console.log(response.data);
        this.file_content = response.data;
        console.log('Content: ' + this.file_content);
        console.log('File Name: ' + bank_file_nm);
        if (bank_file_nm != "" && this.file_content != null) {
          this.downloadFileContent(bank_file_nm, this.file_content);
        }

        if (response.data.length == 0) {
          this.totalRecords = 0;
          //this.showResultAlertBox();
          this.isLoading = false;
        } else {
          this.totalRecords = response.data[0].total;
          this.AlertBoxInitialize();
          this.DefaultBox();
          this.isLoading = false;
          this.isDisplay = true;
        }
        // console.log(response.data);
        //  console.log(this.totalRecords);
      },
      (error) => {
        console.error(error);
        this.isLoading = false;

        //this.showGenericAlertBox();
      }
    );
  }

  //loadData Start
  loadData() {
    this.authService.checkUserRole(this.authService.username, this.permBRN)
      .subscribe(
        (response: any) => {
          this.permBRNAllow = response.data;
          this.permDLBankAllow = this.permBRNAllow.includes(perm.BP_036_Reconciliation_Of_Collection_Download_Bank_Statement_File) ? 1 : 0;

          //console.log(this.permBRNAllow, this.permDLBankAllow);


          this.task_no = history.state.task_id;

          this.isDisplay = true;
          this.isLoading = true;
          const url = environment.apiUrl + '/api/brdc/v1/getnobankstmt';

          // Set your authorization header
          const headers = new HttpHeaders({
            Authorization: environment.authKey,
            'Content-Type': 'application/json',
          });

          const Body: any = {
            i_page: this.page.toString(),
            i_size: this.itemsPerPage.toString(),
          };

          if (this.task_no && this.task_no.trim()) {
            Body.task_no = this.task_no;
          }

          if (this.filenm && this.filenm.trim()) {
            Body.file_nm = this.filenm;
          }

          if (this.filesizekb && this.filesizekb.trim()) {
            Body.file_size = this.filesizekb;
          }

          if (this.uploadedby && this.uploadedby.trim()) {
            Body.uploaded_by = this.uploadedby;
          }

          if (this.dtuploaded && this.dtuploaded.trim()) {
            Body.dt_uploaded = this.dtuploaded;
          }

          this.http.post(url, Body, { headers }).subscribe(
            (response: any) => {
              console.log(response.data);
              this.model = response.data;
              if (response.data.length == 0) {
                this.totalRecords = 0;
                this.isEmptyResult = true;
                //this.isDisplay = false;
                //this.showResultAlert; // Remove parentheses here
                this.isLoading = false;
              } else {
                this.totalRecords = response.data[0].total;
                this.isEmptyResult = false;
                this.isLoading = false;
                // this.AlertBoxInitialize();

                this.model = response.data.map((item: any) => ({


                  bank_file_nm: item.file_nm,
                  bank_file_size_kb: item.file_size,
                  bank_uploaded_by: item.uploaded_by,
                  bank_dt_uploaded: item.dt_uploaded

                }));

                this.isLoading = false;
              }
            },
            (error: any) => {
              console.error(error);
              this.isLoading = false;
              // Handle errors here
            }
          );
        });
  }
  //loadData End

}
