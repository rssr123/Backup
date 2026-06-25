import { ChangeDetectorRef, Component } from '@angular/core';
import { environment } from 'src/environments/environment';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { OTCBank, OTCCheque } from 'src/app/core/models/otc-collection-returned-cheque.interface';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { NgbPaginationConfig } from '@ng-bootstrap/ng-bootstrap';
import { TranslateService } from '@ngx-translate/core';
import { AuthService } from 'src/app/core/services/auth.service';
import { GlobalService } from 'src/app/shared/global.service';
import { ParamService } from 'src/app/core/services/param.service';
import { perm } from 'src/permissions/perm';

@Component({
  selector: 'app-otc-returned-cheque',
  templateUrl: './otc-returned-cheque.component.html',
  styleUrls: ['./otc-returned-cheque.component.scss']
})
export class OtcReturnedChequeComponent {

  isLoading: boolean = false;
  totalRecords: number = 0;

  page = environment.DefaultPage;
  itemsPerPage = environment.ItemPerPage;
  model: OTCCheque[] = [];
  bankmodel: OTCBank[] = [];

  isDisplay: boolean = true;

  //default pagination
  selectedValue = environment.dropdownOptions[0];
  dropdownOptions = environment.dropdownOptions;
  dropDownSize = environment.DropDownSize;

  chequeNo: String | null = null;
  chequeBankNm: String | null = null;
  chequeRcptNo: String | null = null;

  permListAllow: number = 0; // if 0 then not allow to view listing page, else allow
  permViewAllow: number = 0;
  permReturnCheque = perm.OTC_Returned_Cheque_View_Listing_Page + "," + perm.OTC_Returned_Cheque_View_Details
  permReturnChequeAllow = "";

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
    this.loadData();
    this.fetchBanks();
  }

  fetchBanks(): void {
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    const url = environment.apiUrl + '/api/rms/v1/getbanks';

    const Body: any = {
    };

    this.http.post(url, Body, { headers }).subscribe(
      (response: any) => {
        this.bankmodel = response.data;
        this.isLoading = false;
      },
      (error) => {
        console.error(error);
        this.isLoading = false;
      }
    );

  }

  loadData() {
    this.authService.checkUserRole(this.authService.username, this.permReturnCheque)
      .subscribe(
        (response: any) => {
          this.permReturnChequeAllow = response.data;
          this.permListAllow = this.permReturnChequeAllow.includes(perm.OTC_Returned_Cheque_View_Listing_Page) ? 1 : 0;
          this.permViewAllow = this.permReturnChequeAllow.includes(perm.OTC_Returned_Cheque_View_Details) ? 1 : 0;
  
          console.log(this.permListAllow, this.permViewAllow);
  
          if (this.permListAllow === 0) {
            console.log("access-denied");
            this.router.navigate(['/access-denied']);
            return; // Exit the function to prevent further execution
          }
  
          console.log(this.permListAllow, this.permViewAllow);

    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    this.isDisplay = true;
    this.isLoading = true;
    const url = environment.apiUrl + '/api/OTCRC/v1/getchequeinfo';

    const Body: any = {
      i_page: this.page.toString(),
      i_size: this.itemsPerPage.toString(),
    };

    if (this.chequeNo && this.chequeNo.trim()) Body.i_che_no = this.chequeNo;
    if (this.chequeBankNm) Body.i_che_bank_nm = this.chequeBankNm;
    if (this.chequeRcptNo && this.chequeRcptNo.trim()) Body.i_rcpt_no = this.chequeRcptNo;


    this.http.post(url, Body, { headers }).subscribe(
      (response: any) => {
        this.model = response.data;
        console.log(response.data);
        this.totalRecords = response.data.length > 0 ? response.data[0].total : 0;
        this.isLoading = false;
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

  LoadData(singleItem: number) {
    this.selectedValue = singleItem;
    this.itemsPerPage = this.selectedValue;
    this.loadData();
  }

  clearFields(): void {
    this.chequeNo = null;
    this.chequeBankNm = null;
    this.chequeRcptNo = null;
  }

  navigateToReceiptScreen(item: any): void {
    const coll_slip_no = item.coll_slip_no;
    const orn_no = item.orn_no;
    const curr_page = "otc-returned-cheque";
    this.router.navigate(['/otc-receipt-screen', coll_slip_no], { queryParams: { orn_no, curr_page }, state: { item } });
  }
}
