import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Component, Inject, OnInit, ViewChild } from '@angular/core';
import { NgForm, NgModel } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { ParamData } from 'src/app/core/models/param.interface';
import { environment } from 'src/environments/environment';
import { ParamService } from '../../core/services/param.service';
import { NBLCM } from 'src/app/core/models/otc-collection-returned-cheque.interface';
import { Systemstatus } from 'src/app/shared/enums/systemstatus';
import { MFT, SourceSystemCode } from 'src/app/core/models/entity';

@Component({
  selector: 'app-billing-type-update',
  templateUrl: './billing-type-update.component.html',
  styleUrls: ['./billing-type-update.component.scss'],
})
export class BillingTypeUpdateComponent implements OnInit {
  @ViewChild('billingTypeIdRef') billingTypeIdControl!: NgModel;
  @ViewChild('billingTypeDescRef') billingTypeDescControl!: NgModel;

  errorMessages: string[] = [];
  error: boolean = false;
  isLoading: boolean = false;

  // Data-binding variables
  billingTypeId: string | null = null;
  billingTypeDesc: string | null = null;
  billingTypeCId: string | null = null;
  tempBillingTypeId: string | null = null;
  tempBillingTypeDesc: string | null = null;
  classId: string | null = null;
  nblcm: NBLCM[] = [];
  billing_types: ParamData[] = [];
  selectedBillingType: string = ''; // Default to an empty string

  sourceSystemCodeOptions: SourceSystemCode[] = [];
  itemsPerPage = environment.ItemPerPage;
  ssCd: string | null = null;
  page = environment.DefaultPage;
  dropDownSize = environment.DropDownSize;

  mftId: MFT[] | null = null; // MFT ID
  dpsMftId: MFT[] | null = null; // DPS MFT ID
  mfts: MFT[] = [];

  

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: any,
    public dialogRef: MatDialogRef<BillingTypeUpdateComponent>,
    private http: HttpClient, private ParamService: ParamService
  ) { }

  ngOnInit(): void {
    this.initializeForm();
    this.loadBillingType();
    this.fetchNBLCM();
    this.loadSourceSystem();
    this.populateFeeDetailID();
  }

  resetErrors(): void {
    this.error = false;
    this.errorMessages = [];
  }

  // Initialize form fields
  initializeForm(): void {
    this.billingTypeId = this.data.item.btCd || ''; // Existing billing type ID
    this.billingTypeCId = this.data.item.bltc_id || ''; //
    this.billingTypeDesc = this.data.item.btDesc || ''; // Existing billing type description
    this.selectedBillingType = this.data.item.btTy || ''; // Existing billing type
    this.classId = this.data.item.classId || ''; // Existing class ID
    this.ssCd = this.data.item.ssCd || ''; // Existing source system code

    const selectedMftIds = this.data?.item?.items?.map((item: any) => item.mftId) || [];
    this.mftId = this.mfts.filter(mft => selectedMftIds.includes(mft.fee_detail_id));

    //this.dpsMftId = this.data.item.items?.map((item: any) => item.dpsMftId) || []; 
    
    const selectedDpsMftIds = this.data?.item?.items?.map((item: any) => item.dpsMftId) || [];
    this.dpsMftId = this.mfts.filter(mft => selectedDpsMftIds.includes(mft.fee_detail_id));

    this.tempBillingTypeId = this.billingTypeId;
    this.tempBillingTypeDesc = this.billingTypeDesc;

    console.log('MFT ID:', this.mftId);
  }
  


  closeDialog(): void {
    this.dialogRef.close();
  }

  // async updateBillingType() {
  //   if (!this.billingTypeId || !this.billingTypeDesc) {
  //     this.errorMessages = ['All fields are required.'];
  //     this.error = true;
  //     return;
  //   }

  //   // Prepare request payload
  //   const body = {
  //     i_bt_cd: this.billingTypeId,
  //     i_bt_ty: this.selectedBillingType,
  //     i_bt_desc: this.billingTypeDesc,
  //     i_class_id: this.classId,
  //     i_ss_cd: this.ssCd,
  //     i_status: 'A'
  //   };

  //   this.isLoading = true;

  //   const url = `${environment.apiUrl}/api/bltc/v1/updBillingType`;
  //   const headers = new HttpHeaders({
  //     Authorization: environment.authKey,
  //     'Content-Type': 'application/json',
  //   });

  //   try {
  //     const response = await this.http.post(url, body, { headers }).toPromise();
  //     console.log('Update successful:', response);
  //     this.dialogRef.close('updated');
  //   } catch (error) {
  //     console.error('Update failed:', error);
  //     this.errorMessages.push('Failed to update the billing type.');
  //     this.error = true;
  //   } finally {
  //     this.isLoading = false;
  //   }
  // }

  async updateBillingType(): Promise<void> {
    this.isLoading = true;
    this.resetErrors();
  
    // ✅ Validation: Ensure all required fields are filled
    if (!this.billingTypeId || !this.billingTypeDesc) {
      this.error = true;
      this.errorMessages.push('All fields are required.');
      this.isLoading = false;
      return;
    }
  
    // ✅ Validation: ensure MFT and DPS MFT selections are equal
    if (Array.isArray(this.dpsMftId) && this.dpsMftId.length > 0 && this.dpsMftId.length !== this.mftId?.length) {
      this.error = true;
      this.errorMessages.push('The number of selected Deposit MFTs must match the number of selected MFTs.');
      this.isLoading = false;
      return;
    }
  
    const url = `${environment.apiUrl}/api/bltc/v1/updBillingType`;
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });
  
    const body = {
      i_bt_cd: this.billingTypeId,
      i_bt_ty: this.selectedBillingType,
      i_bt_desc: this.billingTypeDesc,
      i_class_id: this.classId,
      i_ss_cd: this.ssCd,
      i_status: 'A'
    };

    console.log('Request Body:', body);
  
    try {
      // 1. Call the main update API
        await this.http.post(url, body, { headers }).toPromise();
        const currentBillingTypeCId = this.billingTypeCId;

  
      if (this.billingTypeCId !== null && this.billingTypeCId !== '' ) {
        const bltcId = this.billingTypeCId;
        console.log('BLTC ID:', bltcId);

        //console.log('BLTC ID returned from update:', bltcId);


  
        // 2. Prepare items for updBLTCItem
        const itemBody = this.mftId?.map((mft: any, index: number) => {
          const dps = this.dpsMftId ? this.dpsMftId[index] : null;
  
          return {
            i_bltc_id: bltcId,
            i_mft_pk: mft.fee_detail_pk,
            i_mft_id: mft.fee_detail_id,
            i_dps_mft_pk: dps?.fee_detail_pk,
            i_dps_mft_id: dps?.fee_detail_id,
          };
        });
  
        // 3. Call API to update BLTC items
        const itemUrl = `${environment.apiUrl}/api/bltc/v1/updBLTCItem`;
        await this.http.post(itemUrl, itemBody, { headers }).toPromise();
  
        this.dialogRef.close('updated');
      } else {
        this.errorMessages.push('Failed to retrieve updated Billing Type ID.');
        this.error = true;
      }
    } catch (error: any) {
      console.error('Error:', error);
      if (error.status === 400) {
        this.errorMessages.push('Billing Type update conflict. Please check your inputs.');
      } else {
        this.errorMessages.push('Failed to update billing type. Please try again.');
      }
      this.error = true;
    } finally {
      this.isLoading = false;
    }
  }
  

  async handleFormSubmit(form: NgForm): Promise<void> {
    if (form.valid) {
      await this.updateBillingType();
    } else {
      Object.values(form.controls).forEach((control) => {
        control.markAsTouched();
      });
    }
  }

  async validation(): Promise<boolean> {
    this.errorMessages = [];
    if (!this.billingTypeId) {
      this.errorMessages.push('Billing Type ID is required.');
      return true;
    }
    if (!this.billingTypeDesc) {
      this.errorMessages.push('Billing Type Description is required.');
      return true;
    }
    return false; // Validation passed
  }

  loadBillingType() {
    this.ParamService.getStates('1', '100', '', 'bltc-type').subscribe(
      (response: any) => {
        if (response && response.data && Array.isArray(response.data)) {
          this.billing_types = response.data as ParamData[];
        } else {
          console.error('Invalid response format:', response);
        }
      },
      (error) => {
        console.error('There was an error retrieving the status:', error);
      }
    );
  }

  async populateFeeDetailID(): Promise<void> {

    const url = environment.apiUrl + '/api/mft/v1/getmasterfeetable';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    const Body: any = {
      i_page: this.page,
      i_size: this.dropDownSize,
      i_status: Systemstatus.Active
    };

    try {
      const response: any = await this.http.post(url, Body, { headers }).toPromise();
      if (response.header.statusCode === '00') {
        this.mfts = response.data;
        console.log('MFTs:', this.mfts);
        this.initializeForm();
        // return false; // Insert success
      } else {
        // this.error = true;
        // this.errorMessages.push('Insert not successful');
        console.error('Invalid master fee table response format:', response);
        // return true; // Insert failed
      }
    } catch (error) {
      //this.error = true;
      // this.errorMessages.push('Internal Server Error.');
      console.error('There was an error retrieving the master fee table:', error);
      // return true; // Error occurred
    }
  }

  fetchNBLCM(): void {
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    const url = `${environment.apiUrl}/api/bltc/v1/getnblcm`;

    const Body: any = {
    };

    this.http.post(url, Body, { headers }).subscribe(
      (response: any) => {
        this.nblcm = response.data;
        this.isLoading = false;
      },
      (error) => {
        console.error(error);
        this.isLoading = false;
      }
    );
  }

  loadSourceSystem() {
    const url = environment.apiUrl + '/api/rms/v1/getsourcesystem';
    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json'
    });
    // Create the request body with your form data
    const requestBody = {
      i_page: 1,
      i_size: this.itemsPerPage,
      i_ss_id: null,
      i_ss_cd: null,
      i_ss_nm: null,
      i_modified_by: null,
      i_dt_modified_fr: null,
      i_dt_modified_to: null,
      i_status: Systemstatus.Active
    };
    // Send an HTTP POST request to the API
    this.http.post(url, requestBody, { headers }).subscribe(
      (response: any) => {
        if (response.data.length === 0) {
          console.error('Invalid response format:', response);
        }
        else {
          this.sourceSystemCodeOptions = response.data;
          // this.sourceSystemCodeOptions=this.sourceSystemCodeOptions.concat(response.data)
          // Handle a successful response (e.g., show a success message)
        }
      },
      (error) => {
        console.error('There was an error retrieving the source system code:', error);
        // Handle API errors (e.g., show an error message)
      }
    );
  }
}
