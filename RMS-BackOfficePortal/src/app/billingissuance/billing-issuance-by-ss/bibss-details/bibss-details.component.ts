import { formatDate } from '@angular/common';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { NgbPaginationConfig } from '@ng-bootstrap/ng-bootstrap';
import { PGReconList } from 'src/app/core/models/pg-recon';
import { ParamData } from 'src/app/core/models/param.interface';
import { Systemstatus } from 'src/app/shared/enums/systemstatus';
import { environment } from 'src/environments/environment';
import { ParamService } from '../../../core/services/param.service';
import { GlobalService } from 'src/app/shared/global.service';
import { TranslateService } from '@ngx-translate/core';
import { perm } from 'src/permissions/perm';
import { AuthService } from 'src/app/core/services/auth.service';
import { OTCReceiptCancellationBalStatusDetails, OTCReceiptCancellationHistoryDetails, OTCReceiptCancellationOrderInfoDetails, OTCReceiptCancellationPaymentInfoDetails, OTCReceiptCancellationPaymentItemsDetails, OTCReceiptCancellationRecepitInfoDetails } from 'src/app/core/models/otc-receipt-cancellation.interface';
import { MTTItemDetails } from 'src/app/core/models/mtt-details.interface';
import { User } from 'src/app/core/models/entity';
import { BillingIssuanceBySBillingDoc, BillingIssuanceBySSBillingDetails, BillingIssuanceBySSHistory, BillingIssuanceBySSListofBilItems, BillingIssuanceBySSListOfIssuance } from 'src/app/core/models/biiling-issuance-by-ss.interface';


@Component({
  selector: 'app-bibss-details',
  templateUrl: './bibss-details.component.html',
  styleUrls: ['./bibss-details.component.scss']
})
export class BibssDetailsComponent implements OnInit {
  //
  billingMethod: string | null = null;
  bilId: number | null = null;


  custID: string | null = null;
  custName: string | null = null;
  custEmail: string | null = null;
  custPhoneNo: string | null = null;
  add1: string | null = null;
  add2: string | null = null;
  add3: string | null = null;
  postcode: string | null = null;
  city: string | null = null;
  state: string | null = null;
  entityName: string | null = null;
  entityType: string | null = null;
  entityNo: string | null = null;
  ss: string | null = null;
  bilNo: string | null = null;
  // bilMethod: string | null = null;
  bilDesc: string | null = null;
  reqName: string | null = null;
  reqEmail: string | null = null;
  loaRefNo: string | null = null;
  agmtRefNo: string | null = null;
  loaStartDate: Date | null = null;
  loaEndDate: Date | null = null;
  agmtStartDate: Date | null = null;
  agmtEndDate: Date | null = null;
  bilCount: number | null = null;
  bilFrequency: string | null = null;
  dayOfBilIssued: string | null = null;
  status: string | null = null;


  file_content = "";
  billingDetails: BillingIssuanceBySSBillingDetails[] = [];
  bilingListOfItems: BillingIssuanceBySSListofBilItems[] = [];
  billingListOfIssuance: BillingIssuanceBySSListOfIssuance[] = [];
  billingListOfDoc: BillingIssuanceBySBillingDoc[] = [];
  billingHistory: BillingIssuanceBySSHistory[] = [];
  statesOptions: any[] = [];
  entityTypesOptions: any[] = [];
  sourceSystemCodesOptions: any[] = [];
  billingStatusOptions: any[] = [];
  billingFrequencyTypeOptions: any[] = [];
  alertMessage: string | undefined = undefined;


  listOfItemPage = environment.DefaultPage;
  itemsPerPageListOfItem = environment.ItemPerPage;
  listOfBillingIssuancePage = environment.DefaultPage;
  itemsPerPageListOfBillingIssuance = environment.ItemPerPage;
  listOfBillingDocPage = environment.DefaultPage;
  itemsPerPageListOfBillingDoc = environment.ItemPerPage;
  page = environment.DefaultPage;
  dropDownSize = environment.DropDownSize;


  // pageReceiptInfo = environment.DefaultPage;
  // itemsPerPageReceiptInfo = environment.ItemPerPage;
  pageHistory = environment.DefaultPage;
  itemsPerPageHistory = environment.ItemPerPage;



  isLoadingBillingDetails: boolean = false;
  isLoadingBillingListOfItems: boolean = false;
  isLoadingBillingListOfIssuance: boolean = false;
  isLoadingBillingListOfDoc: boolean = false;
  isLoadingHistory: boolean = false;


  // totalReceiptInfoRecords: number = 0;
  totalHistoryRecords: number = 0;
  totalRecordsListOfIssuance: number = 0;
  totalRecordsListOfDoc: number = 0;
  totalRecordsListOfItem: number = 0;
  // isDisplayListOfIssuance: boolean = false;
  // isDisplayReceiptInfo: boolean = false;
  isDisplayHist: boolean = false;

   // Configuring Permissions for User and roles variables
   permBilDet = perm.Billing_Issuance_By_Source_System_Billing_Details;
   permBilDetAllow = ""; // variable to store allowed permission for the user
   permListAllow: number = 0; // if 0 then not allow to view listing page, else allow

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

    this.bilId = history.state.bil_id;
    this.billingMethod = history.state.billing_method;
    // this.mttId = history.state.mtt_id;
    // this.otcId = history.state.otc_id;
    // this.otcCounterId = history.state.otc_counter_id;
    // this.counterId = history.state.counter_id;
    // this.OTCPaymentMode = history.state.otc_pymt_mode;

    this.loadStates('State');// to replace state with state name
    this.loadStates('EntityType');
    this.loadStates('Billing-Status');
    this.loadStates('Billing-FreqType');
    this.loadBillingDetails();
    this.loadBillingListOfItems();
    this.loadBillingListOfIssuance();
    this.loadBillingListOfDoc();
    this.populateSourceSystemCode();
    this.loadHistory();
  }

  loadBillingDetails() {
    this.authService.checkUserRole(this.authService.username, this.permBilDet)
      .subscribe(
        (response: any) => {
          this.permBilDetAllow = response.data;
          this.permListAllow = this.permBilDetAllow.includes(perm.Billing_Issuance_By_Source_System_Billing_Details) ? 1 : 0;

          console.log(this.permListAllow,);
          if (this.permListAllow === 0) {
            console.log("access-denied");
            this.router.navigate(['/access-denied']);
            return; // Exit the function to prevent further execution
          }




          this.isLoadingBillingDetails = true;

          const urlMftWFHis = environment.apiUrl + '/api/bibss/v1/getbillingissuancebyssbillingdetails';

          // Set your authorization header
          const headers = new HttpHeaders({
            Authorization: environment.authKey,
            'Content-Type': 'application/json'
          });


          // Create the request body with your form data
          const requestBody: any = {
            i_bil_id: this.bilId
          };

          this.http.post(urlMftWFHis, requestBody, { headers }).subscribe(
            (response: any) => {

              if (response.data.length === 0) {
                // this.isDisplayHist = false;
                this.isLoadingBillingDetails = false;
                // this.totalRecordsHist = 0;
                console.error('Invalid billing details response format:', response);
              }
              else {
                this.billingDetails = response.data;
                this.custID = this.billingDetails[0].cust_id;
                this.custName = this.billingDetails[0].cust_nm;
                this.custEmail = this.billingDetails[0].cust_email;
                this.custPhoneNo = this.billingDetails[0].cust_phone;
                this.add1 = this.billingDetails[0].cust_addr1;
                this.add2 = this.billingDetails[0].cust_addr2;
                this.add3 = this.billingDetails[0].cust_addr3;
                this.postcode = this.billingDetails[0].cust_postcode;
                this.city = this.billingDetails[0].cust_city;
                this.state = this.billingDetails[0].cust_state;
                this.entityName = this.billingDetails[0].ent_nm;
                this.entityType = this.billingDetails[0].ent_ty;
                this.entityNo = this.billingDetails[0].ent_no;
                this.ss = this.billingDetails[0].ss_cd;
                this.bilNo = this.billingDetails[0].billing_no;
                this.bilDesc = this.billingDetails[0].billing_desc;
                this.reqName = this.billingDetails[0].req_name;
                this.reqEmail = this.billingDetails[0].req_email;
                this.loaRefNo = this.billingDetails[0].loa_id;
                this.agmtRefNo = this.billingDetails[0].agm_id;
                this.loaStartDate = this.billingDetails[0].dt_loa_start;
                this.loaEndDate = this.billingDetails[0].dt_loa_end;
                this.agmtStartDate = this.billingDetails[0].dt_agm_start;
                this.agmtEndDate = this.billingDetails[0].dt_agm_end;
                this.bilCount = this.billingDetails[0].billing_cnt;
                this.bilFrequency = this.billingDetails[0].billing_freq;

                // this.isDisplayHist = true;
                this.isLoadingBillingDetails = false;
                // this.isOrderInfoFinishLoading = true;
                //this.totalRecordsHist = response.data[0].total;
              }


            },
            (error) => {
              console.error('There was an error retrieving billing details:', error);
              this.isLoadingBillingDetails = false;
            }
          );
        });
  }


  loadBillingListOfItems() {

    this.isLoadingBillingListOfItems = true;

    const urlMftWFHis = environment.apiUrl + '/api/bibss/v1/getbillingissuancebysslistofbillingitems';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json'
    });


    // Create the request body with your form data
    const requestBody: any = {
      i_bil_id: this.bilId
    };

    this.http.post(urlMftWFHis, requestBody, { headers }).subscribe(
      (response: any) => {

        if (response.data.length === 0) {
          // this.isDisplayHist = false;
          this.isLoadingBillingListOfItems = false;
          this.totalRecordsListOfItem = 0;
          console.error('Invalid billing items response format:', response);
        }
        else {
          this.bilingListOfItems = response.data;
          this.isLoadingBillingListOfItems = false;
          // this.isOrderInfoFinishLoading = true;
          this.totalRecordsListOfItem = response.data[0].total;
        }


      },
      (error) => {
        console.error('There was an error retrieving billing items:', error);
        this.isLoadingBillingListOfItems = false;
      }
    );
  }


  loadBillingListOfIssuance() {

    this.isLoadingBillingListOfIssuance = true;

    const urlMftWFHis = environment.apiUrl + '/api/bibss/v1/getbillingissuancebysslistofbillingissuance';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json'
    });


    // Create the request body with your form data
    const requestBody: any = {
      i_bil_id: this.bilId
    };

    this.http.post(urlMftWFHis, requestBody, { headers }).subscribe(
      (response: any) => {

        if (response.data.length === 0) {
          // this.isDisplayHist = false;
          this.isLoadingBillingListOfIssuance = false;
          this.totalRecordsListOfIssuance = 0;
          console.error('Invalid billing issuance response format:', response);
        }
        else {
          this.billingListOfIssuance = response.data;
          this.isLoadingBillingListOfIssuance = false;
          // this.isOrderInfoFinishLoading = true;
          this.totalRecordsListOfIssuance = response.data[0].total;


          var billing_day_number = new Date(this.billingListOfIssuance[this.billingListOfIssuance.length - 1].bil_child_date).getDate();
          if (billing_day_number == 28 && this.billingListOfIssuance.length > 1)
            billing_day_number = new Date(this.billingListOfIssuance[this.billingListOfIssuance.length - 2].bil_child_date).getDate();
          this.dayOfBilIssued = billing_day_number.toString() + this.getOrdinalSuffix(billing_day_number) + ' day';



          // Determine the status with priority: C > U > P
          for (const item of this.billingListOfIssuance) {
            if (item.bil_status === 'C') {
              this.status = 'C';
              break; // Stop checking further once C is found
            }
            if (item.bil_status === 'U') {
              this.status = 'U'; // Continue checking in case C appears later
            }
          }

          // If no C or U was found, check if all are P
          if (!this.status) {
            const allP = this.billingListOfIssuance.every(item => item.bil_status === 'P');
            this.status = allP ? 'P' : ''; // Default status if none of the conditions are met
          }
          if (!this.status) {
            const allWFA = this.billingListOfIssuance.every(item => item.bil_status === 'WF-A');
            this.status = allWFA ? 'WF-A' : ''; // Default status if none of the conditions are met
          }
        }
      },
      (error) => {
        console.error('There was an error retrieving billing issuance:', error);
        this.isLoadingBillingListOfIssuance = false;
      }
    );
  }

  loadBillingListOfDoc() {

    this.isLoadingBillingListOfDoc = true;

    const urlMftWFHis = environment.apiUrl + '/api/bibssdoc/v1/getbillingissuancebyssdocument';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json'
    });


    // Create the request body with your form data
    const requestBody: any = {
      i_bil_id: this.bilId
    };

    this.http.post(urlMftWFHis, requestBody, { headers }).subscribe(
      (response: any) => {

        if (response.data.length === 0) {
          // this.isDisplayHist = false;
          this.isLoadingBillingListOfDoc = false;
          this.totalRecordsListOfDoc = 0;
          console.error('Invalid billing docs response format:', response);
        }
        else {
          this.billingListOfDoc = response.data;


          this.isLoadingBillingListOfDoc = false;
          // this.isOrderInfoFinishLoading = true;
          this.totalRecordsListOfDoc = response.data[0].total;
        }
      },
      (error) => {
        console.error('There was an error retrieving billing docs:', error);
        this.isLoadingBillingListOfDoc = false;
      }
    );

  }


  //download file start
  downloadFile(file_nm: string, bil_id: number): void {

    const url = environment.apiUrl + '/api/bibssdoc/v1/getbillingissuancebyssdocfilecontent';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    const Body: any = {
      i_bil_id: bil_id
    };

    console.log(Body);

    this.http.post(url, Body, { headers }).subscribe(
      (response: any) => {
        // console.log(response.data);
        this.file_content = response.data;
        this.downloadFileContent(file_nm, this.file_content);

        if (response.data.length == 0) {
          this.isLoadingBillingListOfDoc = false;
          console.error('Invalid billing document response format:', response);
        } else {
          this.isLoadingBillingListOfDoc = false;
          console.log('Successful download file ' + file_nm);
        }
      },
      (error) => {
        this.isLoadingBillingListOfDoc = false;
        console.error('There was an error downloading the billing document:', error);;
      }
    );
  }

  downloadFileContent(fileName: string, fileContent: string): void {
    // event.preventDefault(); // Prevent the default behavior of the anchor element
    this.isLoadingBillingListOfDoc = true;

    // Check if file_content exists
    if (fileContent) {
      const contentType = 'application/octet-stream';
      const blob = this.base64ToBlob(fileContent, contentType);
      const blobUrl = URL.createObjectURL(blob);

      // Create an anchor element and trigger the download
      const link = document.createElement('a');
      link.href = blobUrl;
      link.download = fileName;
      link.click();

      // Cleanup
      URL.revokeObjectURL(blobUrl);
    }
  }

  base64ToBlob(base64: string, contentType: string): Blob {
    const byteCharacters = atob(base64);
    const byteNumbers = new Array(byteCharacters.length);

    for (let i = 0; i < byteCharacters.length; i++) {
      byteNumbers[i] = byteCharacters.charCodeAt(i);
    }

    const byteArray = new Uint8Array(byteNumbers);
    return new Blob([byteArray], { type: contentType });
  }
  //download file end

  loadHistory() {

    this.isLoadingHistory = true;

    const urlMftWFHis = environment.apiUrl + '/api/bibss/v1/getbillingissuancebysshistory';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json'
    });


    // Create the request body with your form data
    const requestBody: any = {
      i_bil_id: this.bilId
    };

    this.http.post(urlMftWFHis, requestBody, { headers }).subscribe(
      (response: any) => {

        if (response.data.length === 0) {
          this.isDisplayHist = false;
          this.isLoadingHistory = false;
          this.totalHistoryRecords = 0;
          console.error('Invalid otc receipt cancellation history table details response format:', response);
        }
        else {
          this.totalHistoryRecords = response.data[0].total;
          this.billingHistory = response.data;
          // this.isDisplayHist = true;
          this.isLoadingHistory = false;
        }
      },
      (error) => {
        console.error('There was an error retrieving the history table:', error);
        this.isLoadingHistory = false;
      }
    );
  }

  getTotalUnitFeesListOfItems(): number {
    if (!this.bilingListOfItems) {
      return 0;
    }
    return this.bilingListOfItems.reduce((sum, item) => sum + (item.unit_fee || 0), 0);
  }

  getTotalQuantityListOfItems(): number {
    if (!this.bilingListOfItems) {
      return 0;
    }
    return this.bilingListOfItems.reduce((sum, item) => sum + (item.qty || 0), 0);
  }

  getTotalTaxAmountListOfItems(): number {
    if (!this.bilingListOfItems) {
      return 0;
    }
    return this.bilingListOfItems.reduce((sum, item) => sum + (item.tax_amt || 0), 0);
  }

  getTotalGrossAmountListOfItems(): number {
    if (!this.bilingListOfItems) {
      return 0;
    }
    return this.bilingListOfItems.reduce((sum, item) => sum + (item.final_amt || 0), 0);
  }

  back() {
    this.router.navigate(['/bibss-list']);
  }

  formatOrdinalDate(dateInput: string | Date): string {
    if (!dateInput) return '';

    const date = new Date(dateInput);
    const day = date.getDate();
    const month = date.toLocaleString('default', { month: 'long' }); // Full month name
    const year = date.getFullYear();

    const suffix = this.getOrdinalSuffix(day);

    return `${day}${suffix} ${month} ${year}`;
  }

  private getOrdinalSuffix(day: number): string {
    if (day > 3 && day < 21) return 'th'; // 4th to 20th always have 'th'
    switch (day % 10) {
      case 1: return 'st';
      case 2: return 'nd';
      case 3: return 'rd';
      default: return 'th';
    }
  }

  isValidDate(bilChildDate: string | Date): string {
    const currentDate = new Date();
    const childDate = new Date(bilChildDate);
    return childDate > currentDate ? 'Valid' : 'Invalid';
  }

  loadStates(paramGrpNm: string) {
    this.ParamService.getStates(environment.DefaultPage.toString(), environment.ItemPerPage.toString(), '', paramGrpNm).subscribe((response: any) => {
      if (response.data.length >= 0) {
        // this.states = response.data as ParamData[]; later
        if (paramGrpNm === 'State') {
          this.statesOptions = response.data as any[];
          this.statesOptions.sort((a, b) => a.nm_en.localeCompare(b.nm_en)); // sorting
        }
        else if (paramGrpNm === 'EntityType') {
          this.entityTypesOptions = response.data as any[];
          this.entityTypesOptions.sort((a, b) => a.nm_en.localeCompare(b.nm_en));
        }
        else if (paramGrpNm === 'Billing-Status') {
          this.billingStatusOptions = response.data as any[];
          this.billingStatusOptions.sort((a, b) => a.nm_en.localeCompare(b.nm_en));
        }
        else if (paramGrpNm === 'Billing-FreqType') {
          this.billingFrequencyTypeOptions = response.data as any[];
          this.billingFrequencyTypeOptions.sort((a, b) => a.nm_en.localeCompare(b.nm_en));
        }
      }
      else
        console.error('Invalid response format:', response);
    },
      (error) => {
        console.error('There was an error retrieving the status:', error);
      }
    );
  }


  getStateName(stateCode: string | null): string {
    if (!stateCode) {
      return '';
    }
    const state = this.statesOptions.find((option) => option.param_cd === stateCode);
    return state ? state.nm_en : stateCode;
  }

  getEntityTypeName(entityCode: string | null): string {
    if (!entityCode) {
      return '';
    }
    const entity = this.entityTypesOptions.find((option) => option.param_cd === entityCode);
    return entity ? entity.nm_en : entityCode;
  }

  getSourceSystemName(ssCode: string | null): string {
    if (!ssCode) {
      return '';
    }
    const sourceSystem = this.sourceSystemCodesOptions.find((option) => option.ss_cd === ssCode);
    return sourceSystem ? sourceSystem.ss_nm : ssCode;
  }

  getBillingStatusName(bil_status: string | null): string {
    if (!bil_status) {
      return '';
    }
    const billingStatus = this.billingStatusOptions.find((option) => option.param_cd === bil_status);
    return billingStatus ? billingStatus.nm_en : bil_status;
  }

  getBillingFrequencyTypeName(bil_freq_type: string | null): string {
    if (!bil_freq_type) {
      return '';
    }
    const billingFrequencyType = this.billingFrequencyTypeOptions.find((option) => option.param_cd === bil_freq_type);
    return billingFrequencyType ? billingFrequencyType.nm_en : bil_freq_type
  }

  populateSourceSystemCode() {

    const url = environment.apiUrl + '/api/rms/v1/getsourcesystem';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json'
    });


    // Create the request body with your form data
    const Body = {
      i_page: this.page,
      i_size: this.dropDownSize,
      i_ss_id: null,
      i_ss_cd: null,
      i_ss_nm: null,
      i_modified_by: null,
      i_dt_modified_fr: null,
      i_dt_modified_to: null,
      i_status: Systemstatus.Active
    };

    this.http.post(url, Body, { headers }).subscribe(
      (response: any) => {

        if (response.data.length === 0) {
          console.error('Invalid source system response format:', response);
        }
        else {
          this.sourceSystemCodesOptions = response.data;
        }
      },
      (error) => {
        console.error('There was an error retrieving the source system:', error);
      }
    );

  }










}
