import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Component } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { config } from 'rxjs';
import { PGReconDetail, PGReconList } from 'src/app/core/models/pg-recon';
import { environment } from 'src/environments/environment';
import { PgDetailListingComponent } from '../pg-detail-listing/pg-detail-listing.component';
import { Router } from '@angular/router';
import { RmsDetailListingComponent } from '../rms-detail-listing/rms-detail-listing.component';
import { DecimalPipe } from '@angular/common';
import { perm } from 'src/permissions/perm';
import { AuthService } from 'src/app/core/services/auth.service';


@Component({
  selector: 'app-pg-recon-detail',
  templateUrl: './pg-recon-detail.component.html',
  styleUrls: ['./pg-recon-detail.component.scss'],
  providers: [DecimalPipe],
})
export class PgReconDetailComponent {

  task_id: any;
  model!: PGReconDetail;
  remarks: any;
  taskStatus: any;
  choices: string[] = ['Open', 'Close', 'Cancel'];
  rightSectionCollapsed: boolean = true;

  PGDetailsBox: boolean = false;
  RMSDetailsBox: boolean = false;

  readOnly: boolean = false;
  remarksError: boolean = false;

  // pg pop up
  txnDate: any=null;
  txnType: any =null;
  found : any=null;
  txnID : any=null;
  txnCode: any=null;
  subCri: any=null;

  //rms pop up
  pymtDate:any=null;
  custNm:any=null;
  foundInPg:any=null;
  txnID2:any=null;
  ornNo:any=null;
  subCri2:any=null;
  orderStatus:any=null;

  // Configuring Permissions for User and roles
  permPGRD = perm.BP_036_Reconciliation_Of_Collection_View_RMS_Transaction_list + "," + 
             perm.BP_036_Reconciliation_Of_Collection_View_PG_Transaction_List_PG_RMS + "," +
             perm.BP_036_Reconciliation_Of_Collection_Open_Close_Add_Remarks_PG_RMS;
  permPGRDAllow = "";
  permViewRMSListAllow: number = 0;
  permViewPGListAllow: number = 0;
  permOpenCloseCancel: number = 0;
  //permPGRDDetailsAllow: number = 0;
  // end configuration

  constructor(
    private http: HttpClient,
    public dialog: MatDialog,
    private router: Router,
    private authService: AuthService
  ) {
  }

  ngOnInit() {

    this.task_id = history.state.task_id;

    this.loadData();
    //this.populateForm();

  }

  loadData() {

    this.authService.checkUserRole(this.authService.username, this.permPGRD)
    .subscribe(
      (response: any) => {
        this.permPGRDAllow = response.data;
        this.permViewRMSListAllow = this.permPGRDAllow.includes(perm.BP_036_Reconciliation_Of_Collection_View_RMS_Transaction_list) ? 1 : 0;
        this.permViewPGListAllow = this.permPGRDAllow.includes(perm.BP_036_Reconciliation_Of_Collection_View_PG_Transaction_List_PG_RMS) ? 1 : 0;
        this.permOpenCloseCancel = this.permPGRDAllow.includes(perm.BP_036_Reconciliation_Of_Collection_Open_Close_Add_Remarks_PG_RMS) ? 1 : 0;
        //this.permPGRDDetailsAllow = this.permPGRDAllow.includes(perm.Accrual_Listing_Deferred_Income_RI_Periodic_Lodgement_RI_Compound_View_Details_Page) ? 1 : 0;
        console.log(this.permViewRMSListAllow, this.permViewRMSListAllow);
        

    const url = environment.apiUrl + '/api/pgrecon/v1/sp_getPGReconDetail';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    const Body: any = {
      i_task_id: this.task_id
    };

    console.log(Body);

    this.http.post(url, Body, { headers }).subscribe(
      (response: any) => {
        this.model = response.data;
        this.remarks = response.data.remarks;
        this.taskStatus = response.data.task_status;

        if(this.taskStatus == 'Close' || this.taskStatus == 'Cancel')
        {
          this.readOnly = true;
        }
        console.log(response.data);
        //  console.log(this.totalRecords);
      },
      (error) => {
        console.error(error);
        // Handle errors here  
      }
    );

  },
  (error) => {
    console.error(error);
  }
);
  }

  async validation(): Promise<boolean> {

    console.log(this.readOnly);
    if(this.readOnly==false && this.remarks.length==0){
      this.remarksError=true;
      return false;
    }
      
    // this.error = false;
    // this.errorMessages = [];
  
    // if (this.remarks.invalid) {
    //   this.error = true;
    //   this.errorMessages.push('Fee Group Name (EN) is required');
    // }
  
    // if (this.feeGroupNameBMControl.invalid) {
    //   this.error = true;
    //   this.errorMessages.push('Fee Group Name (BM) is required');
    // }

    // if (this.ssCdControl.invalid) {
    //   this.error = true;
    //   this.errorMessages.push('Source System is required');
    // }

    // if (this.ssfeeGroupIdControl.invalid) {
    //   this.error = true;
    //   this.errorMessages.push('Source System Fee Group ID is required');
    // }

    this.remarksError=false;
  
    return true;
  }

  async apply(){

    const isValid = await this.validation();

    if (isValid) {

      const url = environment.apiUrl + '/api/pgrecon/v1/sp_updPGReconDetail';

      // Set your authorization header
      const headers = new HttpHeaders({
        Authorization: environment.authKey,
        'Content-Type': 'application/json',
      });

      const Body: any = {
        i_task_id: this.model.task_id,
        i_remarks: this.remarks,
        i_task_status: this.taskStatus
      };

      console.log(Body);

      this.http.post(url, Body, { headers }).subscribe(
        (response: any) => {
          this.model = response.data;
          this.remarks = response.data.remarks;
          this.taskStatus = response.data.task_status;
          console.log(response.data);
          //  console.log(this.totalRecords);
        },
        (error) => {
          console.error(error);
          // Handle errors here  
        }
      );

      this.router.navigate(['/pgrecon-listing']);
      
    }

    

  }

  cancel(){
    this.router.navigate(['/pgrecon-listing']);
  }

  DefaultBox() {
    this.PGDetailsBox = false;
    this.RMSDetailsBox = false;
  }

  PGDetailsSelected(item: any): void {
    
    this.DefaultBox();
    this.resetPopUp();

    this.txnDate = this.model.dt_settlement_char;

    if(item=="pgTxnSettlementNo" || item=="pgTxnMatchedNo"){
      this.txnCode = 'Settlement';
    }

    if(item=="pgTxnAdjNo"){
      this.txnCode = 'Txn Adjustment';
    }

    if(item=="pgTxnFoundNo"){
      this.txnCode = 'Settlement';
      this.found=1;
    }

    if(item=="pgTxnSAMNo"){
      this.txnCode = 'Settlement';
      this.found=1;
      this.subCri='SAM';
    }

    if(item=="pgTxnSNMNo"){
      this.txnCode = 'Settlement';
      this.found=1;
      this.subCri='SNM';
    }

    if(item=="pgTxnTXFNo"){
      this.txnCode = 'Settlement';
      this.found=1;
      this.subCri='TXF';
    }

    if(item=="pgTxnNotFoundNo"){
      this.txnCode = 'Settlement';
      this.found=0;
    }

    const dialogRef = this.dialog.open(PgDetailListingComponent, {
      height: '70%',
      data: {
        txnDate: this.txnDate,
        txnType: this.txnType,
        found : this.found,
        txnID : this.txnID,
        txnCode: this.txnCode,
        subCri: this.subCri,
        taskNo: this.model.task_id
      },
    });

    

    // this.PGDetailsBox = true;

    dialogRef.afterClosed().subscribe(() => {
      this.resetPopUp();
      // if (result === 'updated') {
      //   this.PGDetailsBox = true;
      // }
      //this.refreshMainPage();
      
    });
  }

  resetPopUp(){
    // pg pop up
    this.txnDate=null;
    this.txnType=null;
    this.found=null;
    this.txnID=null;
    this.txnCode=null;
    this.subCri=null;

    this.pymtDate=null;
    this.custNm=null;
    this.foundInPg=null;
    this.txnID2=null;
    this.ornNo=null;
    this.subCri2=null;
    this.orderStatus=null;
  }


  RMSDetailsSelected(item: any): void {

    this.DefaultBox();
    this.resetPopUp();

    this.pymtDate = this.model.dt_settlement_char;

    if(item=="rmsTxnNo"){
      
    }

    if(item=="rmsPaidNo"||item=="rmsRcptNo"){
      this.orderStatus='P'
    }

    if(item=="rmsFaiedNo"){
      this.orderStatus='F'
    }

    if(item=="rmsSAMNo"){
      this.subCri2='SAM'
      this.foundInPg=1;
      this.orderStatus='P'
    }

    if(item=="rmsSNMNo"){
      this.subCri2='SNM'
      this.foundInPg=1;
      this.orderStatus='P'
    }

    if(item=="rmsTXFNo"){
      this.subCri2='TXF'
      this.foundInPg=1;
      this.orderStatus='F'
    }

    if(item=="rmsFIPNo"){
      this.subCri2='FIP';
      this.orderStatus='P';
    }

    if(item=="rmsCIPNo"){
      this.subCri2='CIP'
    }

    if(item=="rmsNCPNo"){
      this.subCri2='NCP'
    }

    if(item=="rmsNFPNo"){
      this.subCri2='NFP';
      this.orderStatus='P';
    }

    const dialogRef = this.dialog.open(RmsDetailListingComponent, {
      height: '70%',
      data: {
        pymtDate:this.pymtDate,
        custNm:this.custNm,
        foundInPg:this.foundInPg,
        txnID2:this.txnID2,
        ornNo:this.ornNo,
        subCri2:this.subCri2,
        orderStatus:this.orderStatus
      },
    });

    //this.RMSDetailsBox = true;

    dialogRef.afterClosed().subscribe(() => {
      this.resetPopUp();
      // if (result === 'updated') {
        
      // }
      //this.refreshMainPage();
    });
  }
}
