import { ChangeDetectorRef,Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { environment } from 'src/environments/environment';
import { NgbPaginationConfig } from '@ng-bootstrap/ng-bootstrap';
import { Router } from '@angular/router';
import { bankReconDetail } from '../../core/models/bank-recon-details.interface';
import { MatDialog } from '@angular/material/dialog';
import { fadeInOut } from '../../shared/animation';
import { ParamService } from '../../core/services/param.service';
import { BankReconPgListingComponent } from '../bank-recon-pg-listing/bank-recon-pg-listing.component';
import { BankReconBkListingComponent } from '../bank-recon-bk-listing/bank-recon-bk-listing.component';
import { BankReconNoBankListingComponent } from '../bank-recon-no-bank-listing/bank-recon-no-bank-listing.component';
import { BankReconPgFileTxnListingComponent } from '../bank-recon-pg-file-txn-listing/bank-recon-pg-file-txn-listing.component';
import { perm } from 'src/permissions/perm';
import { AuthService } from 'src/app/core/services/auth.service';

import { TranslateService } from '@ngx-translate/core';
import { GlobalService } from 'src/app/shared/global.service';

@Component({
  selector: 'app-bank-recon-details',
  templateUrl: './bank-recon-details.component.html',
  styleUrls: ['./bank-recon-details.component.scss']
})

export class BankReconDetailsComponent implements OnInit{
  page = environment.DefaultPage;
  itemsPerPage = environment.ItemPerPage;
 
  isDisplay: boolean = false;
  isLoading: boolean = false;
  isDisplayTaskLists: boolean = false;
  isLoadingTaskLists: boolean = false;
  br:bankReconDetail[] = [];
  task_no:any;
  totalRecords: number = 0;
 
  file_nm: string | null = null;
  file_content: string | null = null;
  total_no_pg_txn: number | null = null;
  total_gross_amt: number | null = null;
  total_mdr: number | null = null;
  total_net_amt: number | null = null;
  total_no_bk_txn: number | null = null;
  total_bank_txn: number | null = null;
  total_pg_file_txn: number | null = null;
  total_pg_disbursed_amt: number | null = null;
  task_status: string | null = null;
  recon_status: string | null = null;
  stmt_no: string | null = null;
  dt_settlement: Date | null = null;
  remarks: string | null = null;

  // Configuring Permissions for User and roles variables
  permBR = perm.BP_036_Reconciliation_Of_Collection_View_Bank_Statement_Transaction_List + "," +
           perm.BP_036_Reconciliation_Of_Collection_View_PG_Transaction_List_PG_Bank + "," +
           perm.BP_036_Reconciliation_Of_Collection_Download_Bank_Statement_File + "," +
           perm.BP_036_Reconciliation_Of_Collection_Download_PG_Settlement_File_PG_Bank + "," +
           perm.BP_036_Reconciliation_Of_Collection_Open_Close_Add_Remarks_PG_Bank
           
    ; // all the perm_cd for this module seperated with comma
  permBRAllow = ""; // variable to store allowed permission for the user
  permBankTransListAllow: number = 0; // if 0 then not allow to view listing page, else allow
  permViewPGTransAllow: number = 0;
  permDLBankAllow: number = 0;
  permDLPGBankAllow: number = 0;
  permOpenCloseCancelAllow: number = 0;

  isStatusCancelClosed: boolean = false;
  remarksError: boolean = false;

  // permUploadAllow: number = 0;
  // permDownloadAllow: number = 0;
  // permCancelViewAllow: number = 0;
  // permReconViewAllow: number = 0;
  // end configuration
 
  //Pop up Box
  pgTxnBox: boolean = false;
  bankTxnBox: boolean = false;

  result: number | null = 0;
  colorResult: boolean = false;

 choices: string[] = ['Open', 'Close', 'Cancel'];
 
  constructor(
    private http: HttpClient,
    config: NgbPaginationConfig,
    private router: Router,
    public dialog: MatDialog,
    private ParamService: ParamService,
    private cd: ChangeDetectorRef,
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
    this.task_no = history.state.task_id;
      this.loadData();
      this.populateForm();
  }
 
  loadData() {
    this.authService.checkUserRole(this.authService.username, this.permBR)
      .subscribe(
        (response: any) => {
          this.permBRAllow = response.data;
          this.permBankTransListAllow = this.permBRAllow.includes(perm.BP_036_Reconciliation_Of_Collection_View_Bank_Statement_Transaction_List) ? 1 : 0;
          this.permViewPGTransAllow = this.permBRAllow.includes(perm.BP_036_Reconciliation_Of_Collection_View_PG_Transaction_List_PG_Bank) ? 1 : 0;
          this.permDLBankAllow = this.permBRAllow.includes(perm.BP_036_Reconciliation_Of_Collection_Download_Bank_Statement_File) ? 1 : 0;
          this.permDLPGBankAllow = this.permBRAllow.includes(perm.BP_036_Reconciliation_Of_Collection_Download_PG_Settlement_File_PG_Bank) ? 1 : 0;
          this.permOpenCloseCancelAllow = this.permBRAllow.includes(perm.BP_036_Reconciliation_Of_Collection_Open_Close_Add_Remarks_PG_Bank) ? 1 : 0;      
          
    this.isDisplay = true;
    this.isLoading = true;
    const url = environment.apiUrl + '/api/brdc/v1/getrcbankdetails';
 
    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });
 
    const Body: any = {};
 
    if(this.task_no && this.task_no.trim())
    {
      Body.task_no = this.task_no;
    }
 
    this.http.post(url, Body, { headers }).subscribe(
      (response: any) => {
        this.br = response.data;
        if (response.data.length == 0) {
          this.totalRecords = 0;
          this.isDisplay = false;
 
          this.isLoading = false;
        } else {
          this.totalRecords = response.data[0].total;
 
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
 
  refreshMainPage(): void {
    this.page = 1;
    this.loadData();
  }
 
  DefaultBox() {
    this.pgTxnBox = false;
    this.bankTxnBox = false;
  }
 
  AlertBoxInitialize() {
    if (this.pgTxnBox) {
      this.bankTxn();
    }else if(this.bankTxnBox) {
      this.bankTxn();
    }
  }
 
  pgTxn(): void {
    this.DefaultBox();
    const dialogRef = this.dialog.open(BankReconPgListingComponent, { 
      height: '80%',
      data: { dt_settlement: this.dt_settlement }
    });
    
    this.pgTxnBox = true;
  }
 
  bankTxn(): void {
    this.DefaultBox();
    const dialogRef = this.dialog.open(BankReconBkListingComponent, {
      height: '80%',
    });
 
    this.bankTxnBox = true;
  }

  noBkTxn(): void {
    this.DefaultBox();
    const dialogRef = this.dialog.open(BankReconNoBankListingComponent, {
      height: '80%',
    });
 
    this.bankTxnBox = true;
  }

  pgFileTxn(): void {
    this.DefaultBox();
    const dialogRef = this.dialog.open(BankReconPgFileTxnListingComponent, {
      height: '80%',
    });
 
    this.bankTxnBox = true;
  }



  async apply(){

    const isValid = await this.validation();

    if (isValid) {

      const url = environment.apiUrl + '/api/brdc/v1/updrcbankdetailstatus';

        // Set your authorization header
        const headers = new HttpHeaders({
          Authorization: environment.authKey,
          'Content-Type': 'application/json',
        });
        
        const Body: any = {
          task_no: this.task_no,
          task_status: this.task_status,
          remarks: this.remarks,
        };

        this.http.post(url, Body, { headers }).subscribe(
          (response: any) => {
            this.router.navigate(['/bank-recon-listing']);
          },
          (error) => {
            this.isLoading = false;
            // Handle errors here
          }
        );

    }

  }
 
  cancel(){
    this.router.navigate(['/bank-recon-listing']);
  }

  populateForm() {
 
    this.task_no = this.task_no;
    this.isDisplay = true;
    this.isLoading = true;
 
    const url = environment.apiUrl + '/api/brdc/v1/getrcbankdetails';
 
    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });
 
    const Body: any = {
      task_no: this.task_no,
    };
 
    this.http.post(url, Body, { headers }).subscribe(
      (response: any) => {
        if (response.data.length === 0)
        {
          this.totalRecords = 0;
          this.isLoading = false;
        }
        else
        {
          this.totalRecords = response.data[0].total;
          this.br = response.data;
          this.file_nm = this.br[0].file_nm;
          this.total_no_pg_txn = this.br[0].total_no_pg_txn;
          this.total_gross_amt = this.br[0].total_gross_amt;
          this.total_mdr = this.br[0].total_mdr;
          this.total_net_amt = this.br[0].total_net_amt;
          this.total_no_bk_txn = this.br[0].total_no_bk_txn;
          this.total_bank_txn = this.br[0].total_bank_txn;
          this.total_pg_file_txn = this.br[0].total_pg_file_txn;
          this.total_pg_disbursed_amt = this.br[0].total_pg_disbursed_amt;
          this.task_no = this.br[0].task_no;
          this.task_status = this.br[0].task_status;
          this.recon_status = this.br[0].recon_status;
          this.stmt_no = this.br[0].stmt_no;
          this.dt_settlement = this.br[0].dt_settlement;
          this.remarks = this.br[0].remarks;

          if(this.total_pg_disbursed_amt == this.total_net_amt)
          {
              this.colorResult = true;
          }
          else
          {
              this.colorResult = false;
          }

          if(this.br[0].task_status.toLowerCase().trim() != 'open'){
            this.isStatusCancelClosed = true;
          }
          else{
            this.isStatusCancelClosed = false;
          }

          this.isLoading = false;
        }
      },
      (error) => {
        console.error(error);
        this.isLoading = false;
        // Handle errors here
      }
    );
 
  }

  downloadFile(file_name : string): void {
    const url = environment.apiUrl + '/api/brdc/v1/getrcpgdoc';
 
    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });
 
    const Body: any = {
      i_task_no: history.state.task_id,
    };
 
    this.http.post(url, Body, { headers }).subscribe(
      (response: any) => {
        this.file_content = response.data;
        if(file_name != "" && this.file_content != null)
        {
          this.downloadFileContent(file_name, this.file_content);
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
      },
      (error) => {
        console.error(error);
        this.isLoading = false;
 
        //this.showGenericAlertBox();
      }
    );
  }

  downloadFileContent(fileName: string, fileContent: string): void {
    this.isLoading = true;
    const binaryString = window.atob(fileContent);
    const len = binaryString.length;
    const uint8Array = new Uint8Array(len);
 
    for (let i = 0; i < len; i++) {
      uint8Array[i] = binaryString.charCodeAt(i);
    }
 
    const blob = new Blob([uint8Array], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' });
 
    const url = URL.createObjectURL(blob);
 
    const anchor = document.createElement('a');
    anchor.href = url;
    anchor.download = fileName;
 
    document.body.appendChild(anchor);
    anchor.click();
 
    document.body.removeChild(anchor);
    URL.revokeObjectURL(url);
  }

  async validation(): Promise<boolean> {

    console.log(this.remarks);

    if(this.isStatusCancelClosed==false && this.remarks==null){
      this.remarksError=true;
      return false;
    }

    this.remarksError=false;
  
    return true;
  }

}
