import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Component, OnInit, ViewChild } from '@angular/core';
import { NgForm, NgModel } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { ParamData } from 'src/app/core/models/param.interface';
import { environment } from 'src/environments/environment';
import { ParamService } from '../../core/services/param.service';
import { MFT, SourceSystemCode } from 'src/app/core/models/entity';
import { Systemstatus } from 'src/app/shared/enums/systemstatus';
import { NBLCM } from 'src/app/core/models/otc-collection-returned-cheque.interface';

@Component({
  selector: 'app-billing-type-add',
  templateUrl: './billing-type-add.component.html',
  styleUrls: ['./billing-type-add.component.scss'],
})
export class BillingTypeAddComponent implements OnInit {
  @ViewChild('btIdRef') btIdControl!: NgModel;
  @ViewChild('btDescRef') btDescControl!: NgModel;

  errorMessages: string[] = [];
  error: boolean = false;
  isReadOnly = false;

  btCd: string | null = null; // Billing Type ID
  btDesc: string | null = null; // Billing Type Description

  btTy: string | null = null;
  classId: string | null = null;
  ssCd: string | null = null;
  mftId: MFT[] | null = null; // MFT ID
  dpsMftId: MFT[] | null = null; // DPS MFT ID
  // mftId: string | null = null;
  // dpsMftId: string | null = null;

  billing_types: ParamData[] = [];
  selectedBillingType: string = ''; // Default to an empty string

  isLoading: boolean = false;
  sourceSystemCodeOptions: SourceSystemCode[] = [];
  itemsPerPage = environment.ItemPerPage;
  mfts: MFT[] = [];
  page = environment.DefaultPage;
  dropDownSize = environment.DropDownSize;

  nblcm: NBLCM[] = [];

  constructor(public dialogRef: MatDialogRef<BillingTypeAddComponent>, private http: HttpClient, private ParamService: ParamService) { }

  ngOnInit(): void {
    this.resetErrors();
    this.loadBillingType();
    this.loadSourceSystem();
    this.populateFeeDetailID();
    this.fetchNBLCM();
  }

  resetErrors(): void {
    this.error = false;
    this.errorMessages = [];
  }

  onClose(): void {
    this.dialogRef.close();
  }

  async submit(): Promise<void> {
    this.isLoading = true;
    this.resetErrors();

    // ✅ Validation: ensure MFT and DPS MFT selections are equal
    if (Array.isArray(this.dpsMftId) && this.dpsMftId.length > 0 && this.dpsMftId.length !== this.mftId?.length) {
      this.error = true;
      this.errorMessages.push('The number of selected Deposit MFTs must match the number of selected MFTs.');
      this.isLoading = false;
      return;
    }

    const url = `${environment.apiUrl}/api/bltc/v1/addBillingType`;
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    const body = {
      i_bt_cd: this.btCd,
      i_bt_desc: this.btDesc,
      i_bt_ty: this.selectedBillingType,
      i_class_id: this.classId,
      i_ss_cd: this.ssCd,
    };

    try {
      // 1. Call the main insert API to create billing type
      const bltcResponse: any = await this.http.post(url, body, { headers }).toPromise();

      if (bltcResponse?.data) {
        const bltcId = bltcResponse.data;

        // 2. Prepare items for insBLTCItem
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

        // 3. Call API to insert BLTC items
        const itemUrl = `${environment.apiUrl}/api/bltc/v1/insBLTCItem`;
        await this.http.post(itemUrl, itemBody, { headers }).toPromise();

        this.dialogRef.close('inserted');
      } else {
        this.errorMessages.push('Failed to retrieve inserted Billing Type ID.');
        this.error = true;
      }
    } catch (error: any) {
      console.error('Error:', error);
      if (error.status === 400) {
        this.errorMessages.push('Billing Type ID already exists. Please use a different Billing Type Code.');
      } else {
        this.errorMessages.push('Failed to add billing type. Please try again with another Billing Type Code.');
      }
      this.error = true;
    } finally {
      this.isLoading = false;
    }
  }

  handleFormSubmit(form: NgForm): void {
    if (form.valid) {
      this.submit();
    } else {
      Object.values(form.controls).forEach((control) => {
        control.markAsTouched();
      });
    }
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

  validateFields(): boolean {
    if (!this.btIdControl.valid) {
      this.errorMessages.push('Billing Type ID is required.');
    }
    if (!this.btDescControl.valid) {
      this.errorMessages.push('Billing Type Description is required.');
    }

    return this.errorMessages.length === 0; // Return `true` if no errors
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

}


