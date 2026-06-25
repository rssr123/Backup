import { HttpClient, HttpHeaders } from '@angular/common/http';
import { ChangeDetectorRef, Component } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { Router, ActivatedRoute } from '@angular/router';
import { NgbPaginationConfig } from '@ng-bootstrap/ng-bootstrap';
import { TranslateService } from '@ngx-translate/core';
import { AuthService } from 'src/app/core/services/auth.service';
import { GlobalService } from 'src/app/shared/global.service';
import { environment } from 'src/environments/environment';
import { ParamService } from 'src/app/core/services/param.service';
import { ParamData } from 'src/app/core/models/param.interface';
import { DatePipe } from '@angular/common';
import { perm } from 'src/permissions/perm';

@Component({
  selector: 'app-billing-adjustment',
  templateUrl: './billing-adjustment.component.html',
  styleUrls: ['./billing-adjustment.component.scss']
})
export class BillingAdjustmentComponent {
  page = environment.DefaultPage;
  itemsPerPage = environment.ItemPerPage;

  currentUser: String | null = null;
  currentName:String | null = null;
  isUser: Boolean = false;
  roles: String | null = 'ANONYMOUS';
  billNo: String | null = null;
  billInfo: any = null;
  billing_mthd: String | null = '-';
  b_mthd: String | null = null;
  dateRangeString: String | null = '-';
  billing_day: String | null = '-';
  cust_id: String | null = '-';
  cust_nm: String | null = '-';
  cust_email: String | null = '-';
  cust_phone: String | null = '-';
  cust_addr1: String | null = '-';
  cust_addr2: String | null = '-';
  cust_addr3: String | null = '-';
  cust_postcode: String | null = '-';
  cust_city: String | null = '-';
  cust_state: String | null = '-';
  ent_nm: String | null = '-';
  ent_ty: String | null = '-';
  ent_no: String | null = '-';
  ss_cd: String | null = '-';
  billing_no: String | null = '-';
  loa_id: String | null = '-';
  agm_id: String | null = '-';
  billing_desc: String | null = '-';
  billing_cnt: String | null = '-';
  billing_freq: String | null = '-';
  req_name: String | null = '-';
  req_email: String | null = '-';
  billing_items: any[] = [];
  old_billing_items: any[] = [];
  billing_list: any[] = [];
  documents_list: any[] = [];
  history: any[] = [];
  billingStatusOptions: ParamData[] = [];

  total_amount: number = 0;
  old_total_amount: number = 0;

  file_content = "";

  totalHistoryRecords: number = 0;
  totalRecordsListOfIssuance: number = 0;
  totalRecordsListOfDoc: number = 0;
  totalRecordsListOfItem: number = 0;

  // default pagination
  selectedValue = environment.dropdownOptions[0];
  dropdownOptions = environment.dropdownOptions;
  dropDownSize = environment.DropDownSize;
  isLoadingHistory: boolean = true;
  isLoading: boolean = true;
  isLoadingBill: boolean = true;
  isLoadingPerms: boolean = true;
  allowAdj: boolean = false;
  isEditable: boolean = false;
  isUploadable: boolean = false; 
  isBillUnauth: boolean = false;

  isValidToAdj: boolean = false;
  isFinPopulating: boolean = false;

  permCheck = perm.Billing_Adjustment_Request;
  permCheckReturnString = ""; // variable to store allowed permission for the user
  permAllow: number = 0;
  showInsertAlert: boolean = false;

  constructor(
	private http: HttpClient,
	config: NgbPaginationConfig,
	private router: Router,
	public dialog: MatDialog,
	private ParamService: ParamService,
	private cd: ChangeDetectorRef,
	private route: ActivatedRoute,
	private translate: TranslateService,
	private globalService: GlobalService,
	private authService: AuthService,
	public datepipe: DatePipe
  ) {
	config.maxSize = environment.PaginationMaxSize;
	config.boundaryLinks = true;
	this.translate.setDefaultLang(this.globalService.getGlobalValue()); // default language
	this.translate.use(this.globalService.getGlobalValue());
	const navigation = this.router.getCurrentNavigation();
  }

  async ngOnInit() {
		const queryParams = this.route.snapshot.queryParamMap;
		const tempBillNo = queryParams.get('billing_no');
		if (tempBillNo !== null && tempBillNo !== 'null')
		  this.billNo = tempBillNo;

		this.authService.getUsername().subscribe(response =>{
		  this.currentUser = response;
      if(this.currentUser == null || this.currentUser == 'undefined' || this.currentUser == '' || this.currentUser == 'Anonymous')
      	this.currentUser = 'Anonymous';
      else
        this.roles = this.authService.roles;
      this.authService.getName().subscribe(response =>{
        this.currentName = response;
        if(this.currentName == null || this.currentName == 'undefined' || this.currentName == '' || this.currentName == 'Anonymous')
            this.currentName = 'Anonymous';
      });
		});
		if(this.billNo != null){
		  	this.loadPermission(this.billNo as string);
		  	this.checkValidBill();
				this.fetchBillInfo();
		}
  }

  async checkValidBill()  {
	const headers = new HttpHeaders({
	  Authorization: environment.authKey,
	  'Content-Type': 'application/json',
	});
	const url = environment.apiUrl + '/api/billing/v1/validbilcanadj';

	const Body: any = {
	  i_billing_no: this.billNo
	};
	
	this.http.post(url, Body, { headers }).subscribe(
	  (response: any) => {
			if(response.data == 'true'){
				this.isValidToAdj = true;
	      if(this.isFinPopulating){
        	this.isUser = this.currentUser == this.billInfo.created_by || this.currentName == this.billInfo.created_by;
	    		if(this.isUser || this.permAllow > 0){
		  			this.allowAdj = true;
						this.isEditable = true;
	    		}
	    		else{
	    			this.allowAdj = false;
						this.isEditable = false;
	    		}	
	      }
			}
	  },
	  (error) => {
		console.error(error);
	  }
	);
  }

  async fetchBillInfo()  {
	const headers = new HttpHeaders({
	  Authorization: environment.authKey,
	  'Content-Type': 'application/json',
	});
	const url = environment.apiUrl + '/api/billing/v1/getbill';

	const Body: any = {
	  i_billing_no: this.billNo,
	  i_page: this.page,
	  i_size: this.itemsPerPage,
	  i_bil_item_status: 'A',
	  i_bil_details_flag: true
	};
	console.log(Body);
	
	this.http.post(url, Body, { headers }).subscribe(
	  (response: any) => {
		this.billInfo = response.data;

    this.isLoadingBill = false;
		console.log(response.data);
		if(response.data.status == 'UNAUTHORIZED'){
      this.isBillUnauth = true;
      this.isLoading = false;
      this.isLoadingHistory = false;
      var billing_no = this.billNo;
			if(environment.production)
				this.router.navigate(['/billing-details'], { queryParams: { billing_no }});
    }
    else if(!this.isLoadingPerms)
			this.populate();
	  },
	  (error) => {
		this.isLoading = false;
		console.error(error);
	  }
	);
  }

  populate(){
    var billing_no = this.billNo;
    this.isUser = this.currentUser == this.billInfo.created_by || this.currentName == this.billInfo.created_by;

    if (this.permAllow === 0 && !this.isUser) {
      if(environment.production)
        this.router.navigate(['/billing-details'], { queryParams: { billing_no }});
      alert('bad permission: ' + this.permCheckReturnString);  
    }

		this.cust_id = this.billInfo.cust_id;
		this.cust_nm = this.billInfo.cust_nm;
		this.cust_email = this.billInfo.cust_email;
		this.cust_phone = this.billInfo.cust_phone;
		this.cust_addr1 = this.billInfo.cust_addr1;
		this.cust_addr2 = this.billInfo.cust_addr2;
		this.cust_addr3 = this.billInfo.cust_addr3;
		this.cust_postcode = this.billInfo.cust_postcode;
		this.cust_city = this.billInfo.cust_city;
		this.cust_state = this.billInfo.cust_state;
		this.ent_nm = this.billInfo.ent_nm;
		this.ent_ty = this.billInfo.ent_ty;
		this.ent_no = this.billInfo.ent_no;
		this.ss_cd = this.billInfo.ss_cd;
		this.billing_no = this.billInfo.billing_no;
		this.loa_id = this.billInfo.loa_id;
		this.agm_id = this.billInfo.agm_id;
		this.billing_desc = this.billInfo.billing_desc;
		this.billing_cnt = this.billInfo.billing_cnt;
		this.billing_freq = this.billInfo.billing_freq;
		this.req_name = this.billInfo.req_name;
		this.req_email = this.billInfo.req_email;
		this.billing_list = this.billInfo.billing_list;
		this.documents_list = this.billInfo.documents_list;
		this.history = this.billInfo.history;
		this.b_mthd = this.billInfo.billing_mthd;
		this.totalHistoryRecords = this.billInfo.history_size;
		this.totalRecordsListOfIssuance = this.billInfo.issuance_size;
		this.totalRecordsListOfDoc = this.billInfo.documents_size;
		this.totalRecordsListOfItem = this.billInfo.items_size;
		this.billing_items = JSON.parse(JSON.stringify(this.billInfo.billing_items));
		this.old_billing_items = this.billInfo.billing_items;

		if(this.billing_items != null && this.billing_items.length > 0)
		  for(const item of this.billing_items){
				this.total_amount += item.final_amt;
				item.unit_fee = item.unit_fee.toFixed(2);
		  }

		  this.old_total_amount = JSON.parse(JSON.stringify(this.total_amount));

		if(this.billing_list != null && this.billing_list.length > 0){
		  var billing_day_number = new Date(this.billing_list[this.billing_list.length-1].bil_child_date).getDate();
		  if(billing_day_number == 28 && this.billing_list.length > 1)
			billing_day_number = new Date(this.billing_list[this.billing_list.length-2].bil_child_date).getDate();
		  this.billing_day = billing_day_number.toString() + this.nthNumber(billing_day_number) + ' day';
		}

		if(this.agm_id != null)
		  this.dateRangeString = this.datepipe.transform(new Date(this.billInfo.dt_agm_start), 'dd MMM yyyy') + ' - ' + this.datepipe.transform(new Date(this.billInfo.dt_agm_end), 'dd MMM yyyy');
		else if(this.agm_id == null && this.loa_id != null)
		  this.dateRangeString = this.datepipe.transform(new Date(this.billInfo.dt_loa_start), 'dd MMM yyyy') + ' - ' + this.datepipe.transform(new Date(this.billInfo.dt_loa_end), 'dd MMM yyyy');

		this.loadSourceSytemData();

		this.loadBSParam();
		this.loadParam(this.b_mthd as string,'Billing-Method');
		this.loadParam(this.ent_ty as string,'EntityType');
		this.loadParam(this.cust_state as string,'State');
		this.loadParam(this.billing_freq as string,'Billing-FreqType');

		//Only owner of bill can request for adjustment
		if(this.isValidToAdj){        
			if(this.isUser || this.permAllow > 0){
			  this.allowAdj = true;
			  this.isEditable = true;
			}
			else{
			  this.allowAdj = false;
			  this.isEditable = false;				
			}
		}
    this.isFinPopulating = true;
		this.isLoading = false;
		this.isLoadingHistory = false;
  }

  nthNumber(number: number): string{
	if (number > 3 && number < 21) return "th";
		switch (number % 10) {
		  case 1: return "st";
		  case 2: return "nd";
		  case 3: return "rd";
		  default: return "th";
		}
  }

  formatDate(sdate: Date): string{
		const cdate = new Date(sdate);
		return cdate.getDate() + this.nthNumber(cdate.getDate()) + ' ' + cdate.toLocaleString('default', { month: 'long' }) + ' ' + cdate.getFullYear();
  }

  loadParam(paramCd: string, paramGrpNm: string){
		  this.ParamService.getStates(environment.DefaultPage.toString(), environment.ItemPerPage.toString(), paramCd, paramGrpNm).subscribe((response: any) => {
			if (response.data.length > 0) {
			  if(paramGrpNm == 'EntityType')
				this.ent_ty = response.data[0].nm_en;
			  else if (paramGrpNm == 'State'){    
				var state = response.data[0].nm_en;
				state = state.charAt(0) + state.substring(1).toLowerCase();
				this.cust_state = state;
			  }
			  else if (paramGrpNm == 'Billing-Method')
					this.billing_mthd = response.data[0].nm_en;
				else if (paramGrpNm == 'Billing-FreqType')
					this.billing_freq = response.data[0].nm_en;
			} 
			else
			  console.error('Invalid response format:', response);
		  },
		  (error) => {
			console.error('There was an error retrieving the status:', error);
		  }
		);
  } 

  loadBSParam(){
		  this.ParamService.getStates(environment.DefaultPage.toString(), environment.ItemPerPage.toString(), '', 'Billing-Status').subscribe((response: any) => {
			if (response.data.length >= 0) {
			  this.billingStatusOptions = response.data as ParamData[];
			  this.billingStatusOptions.sort((a, b) => a.nm_en.localeCompare(b.nm_en)); // sorting
			} 
			else
			  console.error('Invalid response format:', response);
		  },
		  (error) => {
			console.error('There was an error retrieving the status:', error);
		  }
		);
  }   

  loadSourceSytemData(){
	const headers = new HttpHeaders({
	  Authorization: environment.authKey,
	  'Content-Type': 'application/json',
	});

	const url = environment.apiUrl + '/api/rms/v1/getsourcesystem';

	const Body: any = {
	  i_page: 1,
	  i_size: 1,
	  i_ss_id: null, 
	  i_ss_cd: this.ss_cd, 
	  i_ss_nm: null, 
	  i_modified_by: null,
	  i_dt_modified_fr: null, 
	  i_dt_modified_to: null, 
	  i_status: 'A'
	};
	
	this.http.post(url, Body, { headers }).subscribe(
	  (response: any) => {
		this.ss_cd = response.data[0].ss_nm;
	  },
	  (error) => {
		console.error(error);
	  }
	);
  }

  getParamDescWF(paramCd : String){
	for(const i of this.billingStatusOptions)
	  if(i.param_cd == paramCd)
		return i.nm_en;
	return paramCd;
  }

  calculateTotalGrossAmount(): void {
	this.total_amount = this.billing_items.reduce((sum, item) => sum + item.final_amt, 0);
  }

  fetchBillHist(page: number, size: number): void {
	const headers = new HttpHeaders({
	  Authorization: environment.authKey,
	  'Content-Type': 'application/json',
	});

	const url = environment.apiUrl + '/api/billing/v1/getbilhist'; // API endpoint
	const requestBody = {
	  i_billing_no: this.billNo,
	  i_page: page,
	  i_size: size
	};

	this.http.post(url, requestBody, { headers }).subscribe(
	  (response: any) => {
		this.history.concat(response.data[0]);
		console.log(response.data);
	  },
	  (error) => {
		console.error('Error fetching History:', error);
	  }
	);
  }

  updateBillAdjust(): void {
		this.isUploadable = !this.checkIfSameData() && this.allowAdj;
		  if(!this.isUploadable)
			return;
		this.isLoading = true;
		const url = environment.apiUrl + '/api/billing/v1/reqbilladj';

		const headers = new HttpHeaders({
		  Authorization: environment.authKey,
		  'Content-Type': 'application/json',
		});

		var b_items = [];
		for(const i of this.billing_items)
		  b_items.push({              
			  desc:this.translate.store.currentLang === 'en' ? 
				i.mft_desc_en : i.mft_desc_bm,
			  price:parseFloat(i.unit_fee).toFixed(2),
			  qty:parseFloat(i.qty),
			  tax:parseFloat(i.tax_pct),
			  taxc:parseFloat(i.tax_amt).toFixed(2),
			  total:parseFloat(i.final_amt).toFixed(2),
			  mftPk:parseFloat(i.mft_pk)
			});

		// Prepare the body with multiple items
		const body: any = {
		  i_billing_no: this.billNo,
		  i_remark: 'Request Billing Adjustment',
		  i_billing_items: b_items
		};
		try {
		  this.http.post(url, body, { headers }).toPromise() .then((response) => {
			  console.log('Success response:', response);			  
        this.showInsertAlert = true;
        this.isLoading = false;
        //this.allowAdj = false;
        this.isUploadable = false;
				this.isEditable = false;

        setTimeout(() => {
          this.showInsertAlert = false;
          this.router.navigate(['/billing-adjustment-search']);
          location.href = 'billing-adjustment-search';
          return;
        }, 2000);
			})
			.catch((error) => {
			  console.error('Error:', error);
			});
		} catch (error) {
		  console.error(error);
		}
  }

  preventTyping(event: KeyboardEvent): void {
	// Prevent typing any key other than navigation keys
	const allowedKeys = ['Backspace', 'ArrowLeft', 'ArrowRight', 'Delete', 'Tab']; // Allow navigation and delete keys
	if (!allowedKeys.includes(event.key)) {
		event.preventDefault();
	}
  }

  hasBilDoc(bilChildDate: string | Date): boolean {
    const currentDate = new Date();
    const childDate = new Date(bilChildDate);
    return childDate > currentDate ? true : false;
  }

  downloadFileContent(fileName: string, fileContent: string, mimeType: string): void {
	const binaryString = window.atob(fileContent); // Decode base64 content
	const len = binaryString.length;
	const uint8Array = new Uint8Array(len);
  
	for (let i = 0; i < len; i++) {
	  uint8Array[i] = binaryString.charCodeAt(i); // Convert to byte array
	}
  
	// Use the provided MIME type dynamically
	const blob = new Blob([uint8Array], { type: mimeType });
	const url = URL.createObjectURL(blob);
  
	const anchor = document.createElement('a');
	anchor.href = url;
	anchor.download = fileName; // Use the provided file name
  
	document.body.appendChild(anchor);
	anchor.click(); // Trigger download
  
	document.body.removeChild(anchor);
	URL.revokeObjectURL(url); // Clean up the object URL
	this.isLoading = false;
  }
  
	downloadImg(item: any): void {
  	if(item.isDownloadingFile)
  		return;
  	item.isDownloadingFile = true;
		const url = environment.apiUrl + '/api/billing/v1/getbilchildimgblob';
  
	// Set your authorization header
	const headers = new HttpHeaders({
	  Authorization: environment.authKey,
	  'Content-Type': 'application/json',
	});
  
	const Body: any = {
	  i_child_id: item.bil_child_id,
	};
  
	this.http.post(url, Body, { headers }).subscribe(
	  (response: any) => {
			const fileContent = response.data;
			const mimeType = response.mimeType || 'application/octet-stream'; // Fallback MIME type
			this.downloadFileContent(item.bil_no + "_img.pdf", fileContent, mimeType);
	  
			if (!fileContent || fileContent.length === 0) {
	        item.isDownloadingFile = false;
			} else {
	        item.isDownloadingFile = false;
			}
		  },
		  (error) => {
			console.error(error);
	        item.isDownloadingFile = false;
		  }
		);
  }

  downloadFile(item: any): void {
  	if(item.isDownloadingFile)
  		return;
  	item.isDownloadingFile = true;
		const url = environment.apiUrl + '/api/billing/v1/getbildocblob';
  
	// Set your authorization header
	const headers = new HttpHeaders({
	  Authorization: environment.authKey,
	  'Content-Type': 'application/json',
	});
  
	const Body: any = {
	  i_doc_id: item.bil_doc_id,
	};
  
	this.http.post(url, Body, { headers }).subscribe(
	  (response: any) => {
		const fileContent = response.data;
		const mimeType = response.mimeType || 'application/octet-stream'; // Fallback MIME type
		this.downloadFileContent(item.file_nm, fileContent, mimeType);
  
		if (!fileContent || fileContent.length === 0) {
        item.isDownloadingFile = false;
		} else {
        item.isDownloadingFile = false;
		}
	  },
	  (error) => {
		console.error(error);
        item.isDownloadingFile = false;
	  }
	);
  }

  navigateToAdjustmentListingScreen(): void {
	this.router.navigate(['/billing-adjustment-search']);
  }

  recalcBICost(item: any){
		item.unit_fee = ~~parseFloat(item.unit_fee);
		item.qty = ~~parseFloat(item.qty);
		item.tax_amt = parseFloat((((item.unit_fee*item.qty)*((item.tax_pct+100)/100))-(item.unit_fee*item.qty)).toFixed(2));
		this.total_amount -= item.final_amt; //remove old total
		item.final_amt = parseFloat(((item.unit_fee*item.qty) + item.tax_amt).toFixed(2));
		this.total_amount += item.final_amt;
		item.unit_fee = item.unit_fee.toFixed(2);

		this.isUploadable = !this.checkIfSameData() && this.allowAdj;
  }

  checkIfSameData(){
	for(const item of this.billing_items){
		const old = this.old_billing_items.find((option) => option.mft_pk === item.mft_pk);
		if(old.qty != item.qty || old.unit_fee != item.unit_fee)
		  return false;
	}
	return true;
  }

  getOldData(mft_pk: number){
  	return this.old_billing_items.find((option) => option.mft_pk === mft_pk);
  }

  loadPermission(billing_no: string) {
	this.authService.checkUserRole(this.currentUser as string, this.permCheck)
	  	.subscribe((response: any) => {
			this.permCheckReturnString = response.data;
			this.permAllow = this.permCheckReturnString.includes(perm.Billing_Adjustment_Request) ? 1 : 0;

		  this.isLoadingPerms = false;
			if(this.isBillUnauth){
        this.isLoading = false;
        this.isLoadingHistory = false;
				if(environment.production)
					this.router.navigate(['/billing-details'], { queryParams: { billing_no }});
      }
      else if(!this.isLoadingBill)
				this.populate();
		},
		(error: any) => {
			if(environment.production)
				this.router.navigate(['/billing-details'], { queryParams: { billing_no }});
			console.log(error);
			alert('permission load failed');
  		this.isLoadingPerms = false;
			if(!this.isLoadingBill)
				this.populate();
		}
	  );
  }

  getBillingStatusName(bil_status: string | null): string {
    if (!bil_status) {
      return ''; // Return a default value if stateCode is null
    }
    const billingStatus = this.billingStatusOptions.find((option) => option.param_cd === bil_status);
    return billingStatus ? billingStatus.nm_en : bil_status; // Return the name if found, otherwise return the code
  }

  isValidDate(bilChildDate: string | Date): string {
    const currentDate = new Date();
    const childDate = new Date(bilChildDate);
    return childDate > currentDate ? 'Valid' : 'Invalid';
  }

  LoadHistory(singleItem: number) {
    this.selectedValue = singleItem;
    this.itemsPerPage = this.selectedValue;
    this.loadHistory();
  }

 	loadHistory() {
    this.isLoadingHistory = true;
    const url = environment.apiUrl + '/api/billing/v1/getbilhist';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json'
    });


    // Create the request body with your form data
    const requestBody: any = {      
    	i_page: this.page,
      i_size: this.itemsPerPage,
      i_billing_no: this.billNo
    };

    this.http.post(url, requestBody, { headers })
    .subscribe((response: any) => {
        if (response.data.length === 0) {
          this.isLoadingHistory = false;
          console.error('Invalid history table details response format:', response);
        }
        else {
          this.history = response.data;
          this.isLoadingHistory = false;
        }
      },
      (error) => {
        console.error('There was an error retrieving the history table:', error);
        this.isLoadingHistory = false;
      }
    );
  }
  
  back(){
    window.history.back();
  }

  async enablePayment(item: any){
  	const url = environment.apiUrl + '/api/billing/v1/refreshbillingpayment';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json'
    });

    // Create the request body with your form data
    var requestBody: {[k: string]: any} = {
      i_billing_no: item.bil_no
    };

    item.isEnablingPayment = true;
    this.http.post(url, requestBody, { headers }).subscribe((response: any) => {
      if (response.data && response.data.length > 0) {
        if(parseFloat(response.data) < 1){
          var debugFlag = localStorage.getItem('debug') == 'true' ? true : false;
          if(debugFlag){
            console.log('Failed, bad status code: ' + response.data);
          }
        }
        else{
          item.is_expired = 0;
          this.selectedValue = environment.dropdownOptions[0];
          this.itemsPerPage = environment.ItemPerPage;
          this.page = environment.DefaultPage;
          this.loadHistory();
        }
        
        item.isEnablingPayment = false;
      }
    },
      (error: any) => {
        item.isEnablingPayment = false;
        console.log('Fail to re-enable expired bill! Error in Posting.');
        console.error(error);
        this.selectedValue = environment.dropdownOptions[0];
        this.itemsPerPage = environment.ItemPerPage;
        this.page = environment.DefaultPage;
        this.loadHistory();
    });
  }
}
