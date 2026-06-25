import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Component } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute, Router } from '@angular/router';
import { Param, SourceSystemCode } from 'src/app/core/models/entity';
import { MTTDetails, MTTItem, MTTPG, MTTRCPT, MTTRCPTRP } from 'src/app/core/models/mtt-details.interface';
import { ParamData } from 'src/app/core/models/param.interface';
import { DataService } from 'src/app/core/services/data.service';
import { environment } from 'src/environments/environment';
import { MttDetailsItemListingComponent } from '../mtt-details-item-listing/mtt-details-item-listing.component';
import { MttDetailsPgListingComponent } from '../mtt-details-pg-listing/mtt-details-pg-listing.component';
import { saveAs } from "file-saver";
import { Observable, tap, finalize } from 'rxjs';
import { OTCHist, OTCRcpt, OTCEMV, OTCPaymentDetails, OTCPaymentHeader } from 'src/app/core/models/otc-collection-receipting.interface';

@Component({
  selector: 'app-mtt-justi-listing',
  templateUrl: './mtt-justi-listing.component.html',
  styleUrls: ['./mtt-justi-listing.component.scss']
})
export class MttJustiListingComponent {
  page = environment.DefaultPage;
  itemsPerPage = environment.ItemPerPage;
  model: MTTDetails[] = [];
  MTTItemModel: MTTItem[] = [];
  MTTPGModel: MTTPG[] = [];
  MTTRCPTModel: MTTRCPT[] = [];
  MTTRCPTRPModel: MTTRCPTRP[] = [];
  totalRecords: number = 0;
  mtt_id: number | null = null;

  ss_cd: String | null = null;
  orn_no: String | null = null;
  orn_dt: Date | string | null = null;
  total_amt: number = 0;
  rcpt_no: string | null = null;
  ssdocref_id: string | null = null;
  ver_id: string | null = null;
  cust_ip: String | null = null;
  cust_nm: String | null = null;
  cust_addr_1: String | null = null;
  cust_addr_2: String | null = null;
  cust_addr_3: String | null = null;
  cust_postcode: String | null = null;
  cust_city: String | null = null;
  cust_state: String | null = null;
  cust_email: String | null = null;
  cust_phone: String | null = null;
  order_status: string | null = null;

  total: String | null = null;
  nm: string | null = null;
  selectedSourceSystemCodes: any[] = [];
  statusOptions: Param[] = [];
  sourceSystemCodeOptions: SourceSystemCode[] = [];

  // MTT ITEM:
  item_desc: String | null = null;
  item_ref_no: String | null = null;
  qty: number | null = -1;
  unit_fee: number | null = -1;
  gross_amt: number | null = -1;
  disc_amt: number | null = -1;
  tax_amt: number | null = -1;
  mtt_item_id: number | null = -1;
  net_amt: number | null = -1;

  // MTT PG:
  pymt_submit_dt: Date | null = null;
  pg_pymt_id: String | null = null;
  pg_pymt_amt: number | null = -999;
  pg_txn_status: String | null = null;
  mtt_pg_id: number | null = -1;

  // MTT RCPT:
  rcpt_no_mtt: String | null = null;
  rcpt_dt: Date | null = null;
  rcpt_reprint: number | null = -999;
  dt_modified: Date | null = null;

  invalidInput: boolean = false;

  isDisplay: boolean = false;

  isLoading: boolean = false;
  //date range picker
  selected!: Date[];
  bsValue = new Date();
  tempDate!: Date;
  minDate = new Date();
  //date range picker

  //date range picker 2
  selected2!: Date[];
  bsValue2 = new Date();
  tempDate2!: Date;
  minDate2 = new Date();
  //date rangee picker 2 

  editBox: boolean = false;
  addBox: boolean = false;
  deleteBox: boolean = false;
  viewBox: boolean = false;

  selectedValue = environment.dropdownOptions[0];
  dropdownOptions = environment.dropdownOptions;

  states: ParamData[] = [];
  selectedState: string | null = null;

  checkResult: number = 0;
  dropDownSize = environment.DropDownSize;
  mtt: any;
  file_content: any;

  otcHistModel: OTCHist[] = [];
  otcRcptModel: OTCRcpt[] = [];
  otcEMVModel: OTCEMV[] = [];
  otcPaymentDetails: OTCPaymentDetails[] = [];
  otcPaymentHeader: OTCPaymentHeader[] = [];
  otcPayerEmail: String | null = null;
  otcPymtMode: String | null = null;
  cashPayments: OTCPaymentDetails[] = [];
  chequePayments: OTCPaymentDetails[] = [];
  moneyOrderPayments: OTCPaymentDetails[] = [];
  bankDraftPayments: OTCPaymentDetails[] = [];
  totalGrossAmount: number = 0; // Variable to hold the total sum of gross amounts
  totalChequeAmount: number = 0;
  totalBDAmount: number = 0;
  totalMOAmount: number = 0;
  otcPaymentDetailsCashAmt: number | null = 0;
  
  idamanFileName: string | null = null;


  LoadData(singleItem: number) {
    this.selectedValue = singleItem;
    this.itemsPerPage = this.selectedValue;
    this.loadData();
  }

  constructor(private route: ActivatedRoute, private http: HttpClient, private router: Router, private dataService: DataService, public dialog: MatDialog,) { }
  
  

  ngOnInit() {
    this.mtt_id = history.state.mtt_id;
    this.ss_cd = history.state.ss_cd;
    this.orn_no = history.state.orn_no;
    this.total_amt = history.state.total_amt;
    this.rcpt_no = history.state.rcpt_no;
    this.order_status = history.state.order_status;

    console.log(this.mtt_id, this.ss_cd, this.orn_no, this.total_amt, this.rcpt_no, this.order_status);

    if (this.ss_cd !== undefined) {
      this.isLoading = true;
      this.loadData();
      this.fetchOTCEMV().subscribe();
      this.fetchOTCHist().subscribe();
      this.fetchOTCPaymentDetails().subscribe();
      this.fetchOTCHeader().subscribe();
      this.fetchOTCRcpt().subscribe();
      this.fetchMTTRcptRp().subscribe();
      // this.populateForm();
    }
    console.log(this.MTTRCPTModel.length);
    
  }

  loadData() {
    this.isLoading = true;
    const url = environment.apiUrl + '/api/mttl/v1/getmttdetails';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json'
    });


    // Create the request body with your form data
    const Body: any = {

      i_ss_cd: this.ss_cd,
      i_orn_no: this.orn_no,
      i_order_status: this.order_status,
      i_total_amt: this.total_amt,
      i_rcpt_no: this.rcpt_no
    };

    this.http.post(url, Body, { headers }).subscribe(
      (response: any) => {
        // console.log(response);
        // You can process the response data here

        if (response.data.length === 0) {
          this.totalRecords = 0;
          console.error('Invalid master fee table work flow response format:', response);
        }
        else {
          this.model = response.data;
          this.orn_dt = this.model[0].orn_dt;
          this.cust_ip = this.model[0].cust_ip;
          this.cust_nm = this.model[0].cust_nm;
          this.cust_addr_1 = this.model[0].cust_addr_1;
          this.cust_addr_2 = this.model[0].cust_addr_2;
          this.cust_addr_3 = this.model[0].cust_addr_3;
          this.cust_postcode = this.model[0].cust_postcode;
          this.cust_city = this.model[0].cust_city;
          this.cust_state = this.model[0].cust_state;
          this.cust_email = this.model[0].cust_email;
          this.cust_phone = this.model[0].cust_phone;


          this.isLoading = false;
          this.isDisplay = true;
        }
        //console.log(response.data);
        // console.log(this.totalRecords);

      },
      (error) => {
        console.error('There was an error retrieving the master fee table work flow:', error);
        this.isLoading = false;
        // Handle errors here
      }
    );

    const MTTItemurl = environment.apiUrl + '/api/mttl/v1/getmttitem';

    // Create the request body with your form data
    const MTTItemBody: any = {
      i_mtt_id: this.mtt_id,
    };

    this.http.post(MTTItemurl, MTTItemBody, { headers }).subscribe(
      (response: any) => {
        // console.log(response);
        // You can process the response data here
        if (response.data.length === 0) {
          this.totalRecords = 0;
          console.error('No Records Returned', response);
        }
        else {
          this.MTTItemModel = response.data;
          this.item_desc = this.MTTItemModel[0].item_desc;
          this.item_ref_no = this.MTTItemModel[0].item_ref_no;
          this.qty = this.MTTItemModel[0].qty;
          this.unit_fee = this.MTTItemModel[0].unit_fee;
          this.gross_amt = this.MTTItemModel[0].gross_amt;
          this.disc_amt = this.MTTItemModel[0].disc_amt;
          this.tax_amt = this.MTTItemModel[0].tax_amt;
          this.net_amt = this.MTTItemModel[0].net_amt;

          this.isLoading = false;
          this.isDisplay = true;
        }

      },
      (error) => {
        this.isLoading = false;
      }
    );

    const MTTPGurl = environment.apiUrl + '/api/mttl/v1/getmttpg';

    // Create the request body with your form data
    const MTTPGBody: any = {
      i_mtt_id: this.mtt_id,
    };

    this.http.post(MTTPGurl, MTTPGBody, { headers }).subscribe(
      (response: any) => {
        // console.log(response);
        // You can process the response data here
        if (response.data.length === 0) {
          this.totalRecords = 0;
          console.error('No Records Returned', response);
        }
        else {
          this.MTTPGModel = response.data;
          this.pymt_submit_dt = this.MTTPGModel[0].pymt_submit_dt;
          this.pg_pymt_id = this.MTTPGModel[0].pg_pymt_id;
          this.pg_pymt_amt = this.MTTPGModel[0].pg_pymt_amt;
          this.pg_txn_status = this.MTTPGModel[0].pg_txn_status;

          this.isLoading = false;
          this.isDisplay = true;
        }
      },
      (error) => {
        console.error('There was an error retrieving the master fee table work flow:', error);
        this.isLoading = false;
        // Handle errors here
      }
    );

    const MTTRCPTurl = environment.apiUrl + '/api/mttl/v1/getmttrcpt';

    // Create the request body with your form data
    const MTTRCPTBody: any = {
      i_mtt_id: this.mtt_id,
    };

    this.http.post(MTTRCPTurl, MTTRCPTBody, { headers }).subscribe(
      (response: any) => {
        // console.log(response);
        // You can process the response data here
        if (response.data.length === 0) {
          this.totalRecords = 0;
          console.error('No Records Returned', response);
        }
        else {
          this.MTTRCPTModel = response.data;
          this.rcpt_no_mtt = this.MTTRCPTModel[0].rcpt_no;
          this.rcpt_dt = this.MTTRCPTModel[0].rcpt_dt;
          this.rcpt_reprint = this.MTTRCPTModel[0].rcpt_reprint;
          this.dt_modified = this.MTTRCPTModel[0].dt_modified;

          this.isLoading = false;
          this.isDisplay = true;
        }

      },
      (error) => {
        this.isLoading = false;
        // Handle errors here
      }
    );

  }

  viewMTTItemBox: boolean = false;

  DefaultBox() {
    this.viewMTTItemBox = false;

  }

  viewMTTItemDetails(item: any): void {
    this.DefaultBox();
    const dialogRef = this.dialog.open(MttDetailsItemListingComponent, {
      width: '50%',
      data: {
        mtt_item_id: item.mtt_item_id,
      },
    });

    // dialogRef.afterClosed().subscribe((result) => {
    //   if (result === 'updated') {
    //     this.editBox = true;
    //   }
    //   this.refreshMainPage();
    // });
    // }
  }

  viewMTTPgDetails(item: any): void {
    this.DefaultBox();
    const dialogRef = this.dialog.open(MttDetailsPgListingComponent, {
      width: '50%',
      data: {
        mtt_item_id: item.mtt_pg_id,
      },
    });
  }

  dlRcpt(orn_no: any) {
    if (orn_no == null)
      return;

    const generateURL = environment.apiUrl + '/api/receipt/v1/dl_rcpt';
    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json'
    });

    // Create the request body with your form data
    var requestBody: { [k: string]: any } = {
      i_orn_no: this.rcpt_no,
      i_mtt_id: this.mtt_id
    };

    // this.http.post(generateURL, requestBody, { observe: 'response', responseType: 'blob', headers: headers })
    //   .subscribe(response => {
    //     var blob = new Blob([response.body as Blob], { type: 'pdf' });
    //     saveAs(blob, response.headers.get('content-disposition')!.split('filename=')[1]);
    //   });

    this.http.post(generateURL, requestBody, { headers }).subscribe(
      (response: any) => {
        // console.log(response.data);
        this.file_content = response.data;
        if (this.file_content != null) {
          this.downloadFileContent(this.file_content, orn_no);
        }
        if (response.data.length == 0) {
          this.totalRecords = 0;
          //this.showResultAlertBox();
          this.isLoading = false;
        } else {
          this.totalRecords = response.data[0].total;
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

    const filename = 'SSM-Receipt-' + orn_no + '.pdf';
  }

  downloadFileContent(fileContent: string, orn_no: string): void {
    this.isLoading = true;
    const binaryString = window.atob(fileContent);
    const len = binaryString.length;
    const uint8Array = new Uint8Array(len);
    for (let i = 0; i < len; i++) {
      uint8Array[i] = binaryString.charCodeAt(i);
    }
    const blob = new Blob([uint8Array], { type: 'application/pdf' });
    const url = URL.createObjectURL(blob);
    const anchor = document.createElement('a');
    anchor.href = url;
    const filename = 'SSM-Receipt-' + orn_no + '.pdf';
    anchor.download = filename;
    document.body.appendChild(anchor);
    anchor.click();
    document.body.removeChild(anchor);
    URL.revokeObjectURL(url);
  }

  



  getTotalNetAmount(): number {
    return this.MTTItemModel
      ?.map(item => item.net_amt || 0)
      .reduce((sum, val) => sum + val, 0);
  }

  

  mttId: any;
  fileContent: string | null = null;
  fileType: string | null = null;

  downloadFile(fileName: string, verId: string, sourceSysDocRefID: string) {
    const generateURL = environment.apiUrl + '/api/OTCCR/v1/downloadOTCRcpt';
    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json'
    });

    const requestBody = {
      refNo1: fileName,
      verID: verId,
      sourceSysDocRefID: sourceSysDocRefID,
    };

    this.http.post(generateURL, requestBody, { headers }).subscribe(
      (response: any) => {
        this.file_content = response.data;
        if (this.file_content != null) {
          this.downloadFileContentOTC(this.file_content, fileName);
        }
        if (response.data.length == 0) {
          this.totalRecords = 0;
          this.isLoading = false;
        } else {
          this.totalRecords = response.data[0].total;
          this.isLoading = false;
        }
      },
      (error) => {
        console.error(error);
        this.isLoading = false;
      }
    );
  }

  downloadFileContentOTC(fileContent: string, orn_no: string): void {
    this.isLoading = true;
    const binaryString = window.atob(fileContent);
    const len = binaryString.length;
    const uint8Array = new Uint8Array(len);
    for (let i = 0; i < len; i++) {
      uint8Array[i] = binaryString.charCodeAt(i);
    }
    const blob = new Blob([uint8Array], { type: 'application/pdf' });
    const url = URL.createObjectURL(blob);
    const anchor = document.createElement('a');
    anchor.href = url;
    const filename = 'SSM-Receipt-' + orn_no + '.pdf';
    anchor.download = filename;
    document.body.appendChild(anchor);
    anchor.click();
    document.body.removeChild(anchor);
    URL.revokeObjectURL(url);
  }





  fetchOTCHist(): Observable<any> {
    this.isLoading = true;

    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    const url = environment.apiUrl + '/api/OTCCR/v1/getotccrhist'; // API endpoint
    const requestBody = {
      i_mtt_id: this.mtt_id,
    };

    return this.http.post(url, requestBody, { headers }).pipe(
      tap((response: any) => {
        this.otcHistModel = response?.data || []; // Store the received data
        console.log(this.otcHistModel);
      }),
      finalize(() => {
        this.isLoading = false;
      })
    );
  }

  calculateTotalChequeAmount(): void {
    this.totalChequeAmount = this.chequePayments.reduce((sum, item) => sum + item.che_amt, 0);
  }

  calculateTotalBDAmount(): void {
    this.totalBDAmount = this.bankDraftPayments.reduce((sum, item) => sum + item.bd_amt, 0);
  }

  calculateTotalMOAmount(): void {
    this.totalMOAmount = this.moneyOrderPayments.reduce((sum, item) => sum + item.mo_amt, 0);
  }

  fetchOTCPaymentDetails(): Observable<any> {
    this.isLoading = true;

    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    const url = environment.apiUrl + '/api/OTCCR/v1/getOTCPaymentDetails'; // API endpoint
    const requestBody = {
      i_mtt_id: this.mtt_id,
    };

    return this.http.post(url, requestBody, { headers }).pipe(
      tap((response: any) => {
        this.otcPaymentDetails = response?.data || []; // Store the received data
        this.cashPayments = this.otcPaymentDetails.filter(item => item.cash_amt !== null);
        this.otcPaymentDetailsCashAmt = this.cashPayments.length > 0 ? this.cashPayments[0].cash_amt : 0;

        this.chequePayments = this.otcPaymentDetails.filter(item => item.che_amt !== null);
        this.moneyOrderPayments = this.otcPaymentDetails.filter(item => item.mo_amt !== null);
        this.bankDraftPayments = this.otcPaymentDetails.filter(item => item.bd_amt !== null);

        this.calculateTotalChequeAmount();
        this.calculateTotalBDAmount();
        this.calculateTotalMOAmount();
      }),
      finalize(() => {
        this.isLoading = false;
      })
    );
  }

  fetchOTCHeader(): Observable<any> {
    this.isLoading = true;

    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    const url = environment.apiUrl + '/api/OTCCR/v1/getOTCPaymentHeader'; // API endpoint
    const requestBody = {
      i_mtt_id: this.mtt_id,
    };

    return this.http.post(url, requestBody, { headers }).pipe(
      tap((response: any) => {
        this.otcPaymentHeader = response?.data || [];
        this.otcPayerEmail = this.otcPaymentHeader.length > 0 ? this.otcPaymentHeader[0].payer_email : '';
        this.otcPymtMode = this.otcPaymentHeader.length > 0 ? this.otcPaymentHeader[0].otc_pymt_mode : '';
      }),
      finalize(() => {
        this.isLoading = false;
      })
    );
  }

  fetchOTCRcpt(): Observable<any> {
    this.isLoading = true;

    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    const url = environment.apiUrl + '/api/OTCCR/v1/getotccrrcpt'; // API endpoint
    const requestBody = {
      i_mtt_id: this.mtt_id,
    };

    return this.http.post(url, requestBody, { headers }).pipe(
      tap((response: any) => {
        this.otcRcptModel = response?.data || [];
        console.log(this.otcRcptModel);
      }),
      finalize(() => {
        this.isLoading = false;
      })
    );
  }

  fetchMTTRcptRp(): Observable<any> {
    this.isLoading = true;

    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    const url = environment.apiUrl + '/api/rr/v1/mttrcptrp'; // API endpoint
    const requestBody = {
      i_mtt_id: this.mtt_id,
    };

    return this.http.post(url, requestBody, { headers }).pipe(
      tap((response: any) => {
        this.MTTRCPTRPModel = response?.data || [];
        console.log(this.MTTRCPTRPModel);
        this.rcpt_no = this.MTTRCPTRPModel[0]?.rcpt_no?.toString() ?? null;
        this.ssdocref_id = this.MTTRCPTRPModel[0]?.ssdocref_id?.toString() ?? null;
        this.ver_id = this.MTTRCPTRPModel[0]?.ver_id?.toString() ?? null;

        // this.downloadFile(rcpt_no, ver_id, ssdocref_id);

      }),
      finalize(() => {
        this.isLoading = false;
      })
    );
  }

  

  fetchOTCEMV(): Observable<any> {
    this.isLoading = true;

    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    const url = environment.apiUrl + '/api/OTCCR/v1/getotcemv';

    const Body: any = {
      i_mtt_id: this.mtt_id,
    };

    return this.http.post(url, Body, { headers }).pipe(
      tap((response: any) => {
        // Ensure response.data is treated as an array
        this.otcEMVModel = response.data
          ? Array.isArray(response.data)
            ? response.data
            : [response.data]
          : [];
        console.log(this.otcEMVModel);
        console.log(this.otcEMVModel.length);
      }),
      finalize(() => {
        this.isLoading = false;
      })
    );
  }

  selectedJustification: string = '';

  isFormValid(): boolean {
    if (this.selectedJustification === 'Others') {
      // When 'Others' is selected, ensure the remark field is filled
      return this.remark.trim().length > 0;
    } else {
      // For other justifications, just check if one is selected
      return !!this.selectedJustification;
    }
  }

  resetForm(): void {
    this.selectedJustification = ''; // Clear the selected justification
    this.remark = ''; // Clear the remark
    window.history.back()
  }


  remark: string = '';

  async UpdateReceiptCount(): Promise<void> {
      const url = environment.apiUrl + '/api/rr/v1/updrcptcount_mtt';
  
      const headers = new HttpHeaders({
        Authorization: environment.authKey,
        'Content-Type': 'application/json',
      });
  
      const body: any = {
        i_mtt_id: this.mtt_id
      };
  
      try {
        this.http
          .post(url, body, { headers })
          .toPromise()
          .then((response) => {
            console.log('Success response:', response);
          })
          .catch((error) => {
            console.error('Error:', error);
          });
      } catch (error) {
        console.error(error);
      }
    }




  async handleSubmit(): Promise<void> {
  try {
    this.downloadFile(this.rcpt_no!, this.ver_id!, this.ssdocref_id!);
    await this.UpdateReceiptCount();
  } catch (error) {
    console.error('Error in handleSubmit:', error);
  }
}






}