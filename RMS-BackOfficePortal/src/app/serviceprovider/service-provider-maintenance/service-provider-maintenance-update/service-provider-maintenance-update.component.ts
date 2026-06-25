import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Component, Inject, OnInit, ViewChild } from '@angular/core';
import { NgForm, NgModel } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { environment } from 'src/environments/environment';
import { Systemstatus } from '../../../shared/enums/systemstatus';
import { PostCodeData } from 'src/app/core/models/postcode.interface';
import { ParamService } from '../../../core/services/param.service';
import { ParamData } from 'src/app/core/models/param.interface';
import { MFT } from 'src/app/core/models/entity';

@Component({
  selector: 'app-service-provider-maintenance-update',
  templateUrl: './service-provider-maintenance-update.component.html',
  styleUrls: ['./service-provider-maintenance-update.component.scss']
})
export class ServiceProviderMaintenanceUpdateComponent implements OnInit {

  @ViewChild('cityRef') cityRef!: NgModel;
  @ViewChild('stateRef') stateRef!: NgModel;
  @ViewChild('profileNameRef') profileNameControl!: NgModel;
  @ViewChild('customerNameRef') customerNameControl!: NgModel;
  @ViewChild('customerAddr1Ref') customerAddr1Control!: NgModel;
  @ViewChild('customerAddr2Ref') customerAddr2Control!: NgModel;
  @ViewChild('customerAddr3Ref') customerAddr3Control!: NgModel;
  @ViewChild('postcodeRef') postcodeControl!: NgModel;
  @ViewChild('cityRef') cityControl!: NgModel;
  @ViewChild('stateRef') stateControl!: NgModel;
  @ViewChild('emailRef') emailControl!: NgModel;
  @ViewChild('phoneRef') phoneControl!: NgModel;
  @ViewChild('feeDetailIdRef') feeDetailIdControl!: NgModel;
  @ViewChild('entityTypeRef') entityTypeControl!: NgModel;
  @ViewChild('entityNoRef') entityNoControl!: NgModel;
  @ViewChild('entityNameRef') entityNameControl!: NgModel;

  onKeyDown(event: KeyboardEvent): void {
    const allowedKeys = ['Backspace', 'Delete', 'ArrowLeft', 'ArrowRight', '.'];
    if (allowedKeys.includes(event.code)) {
      return;
    }
    if (!isNaN(Number(event.key)) || event.key === '.') {
      return;
    }
    event.preventDefault();
  }




  errorMessages: string[] = [];
  error: boolean = false;


  agPfId: String | null = null;
  profileNm: String | null = null;
  custNm: String | null = null;
  custAddr1: String | null = null;
  custAddr2: String | null = null;
  custAddr3: String | null = null;
  postcode: String | null = null;
  city: String | null = null;
  state: String | null = null;
  email: String | null = null;
  phone: String | null = null;
  feeDetailId: String | null = null;
  entityType: String | null = null;
  entityNo: String | null = null;
  entityNm: String | null = null;

  tempAgPfId: String | null = null;
  tempProfileNm: string | "" = "";
  tempCustNm: String | null = null;
  tempCustAddr1: String | null = null;
  tempCustAddr2: String | null = null;
  tempCustAddr3: String | null = null;
  tempCustPostcode: String | null = null;
  tempCustCity: String | null = null;
  tempCustState: String | null = null;
  tempCustEmail: String | null = null;
  tempCustPhone: String | null = null;
  tempFeeDetailId: String | null = null;
  tempEntityType: String | null = null;
  tempEntityNo: String | null = null;
  tempEntityNm: String | null = null;

  // postcode: string | null = null;
  // city: string | null = null;
  // state: string | null = null;
  shortformstate: string | null = null;
  states: any[] = [];
  uniqueCities: string[] = [];
  uniqueStates: string[] = [];
  postCodes: PostCodeData[] = [];
  totalPostCodeRecords: number = 0;



  totalRecords: number = 0;

  isLoading: boolean = false;

  stateMapping: { [key: string]: string } = {
    'KELANTAN': 'KTN',
    'MELAKA': 'MLK',
    'NEGERI SEMBILAN': 'NSN',
    'PAHANG': 'PHG',
    'PULAU PINANG': 'PNG',
    'PERAK': 'PRK',
    'PERLIS': 'PLS',
    'SELANGOR': 'SGR',
    'TERENGGANU': 'TRG',
    'SABAH': 'SBH',
    'SARAWAK': 'SWK',
    'WILAYAH PERSEKUTUAN KUALA LUMPUR': 'KUL',
    'WILAYAH PERSEKUTUAN LABUAN': 'LBN',
    'WILAYAH PERSEKUTUAN PUTRAJAYA': 'PJY',
    'JOHOR': 'JHR',
    'KEDAH': 'KDH'
  };


  constructor(
    @Inject(MAT_DIALOG_DATA) public data: any,
    public dialogRef: MatDialogRef<ServiceProviderMaintenanceUpdateComponent>,
    private http: HttpClient,
    private ParamService: ParamService
  ) { }

  ngOnInit(): void {
    this.defaultSetting();
    this.agPfId = this.data.agPfId;
    this.profileNm = this.data.profileNm;
    this.custNm = this.data.custNm;
    this.custAddr1 = this.data.custAddr1;
    this.custAddr2 = this.data.custAddr2;
    this.custAddr3 = this.data.custAddr3;
    this.postcode = this.data.postcode;
    this.city = this.data.city;
    this.state = this.data.state;
    this.email = this.data.email;
    this.phone = this.data.phone;
    this.feeDetailId = this.data.feeDetailId;
    this.entityType = this.data.entityType;
    this.entityNo = this.data.entityNo;
    this.entityNm = this.data.entityNm;

    this.tempAgPfId = this.data.agPfId;
    this.tempProfileNm = this.data.profileNm;
    this.tempCustNm = this.data.custNm;
    this.tempCustAddr1 = this.data.custAddr1;
    this.tempCustAddr2 = this.data.custAddr2;
    this.tempCustAddr3 = this.data.custAddr3;
    this.tempCustPostcode = this.data.custPostcode;
    this.tempCustCity = this.data.custCity;
    this.tempCustState = this.data.custState;
    this.tempCustEmail = this.data.custEmail;
    this.tempCustPhone = this.data.custPhone;
    this.tempFeeDetailId = this.data.feeDetailId;
    this.tempEntityType = this.data.entityType;
    this.tempEntityNo = this.data.entityNo;
    this.tempEntityNm = this.data.entityNm;
    this.populateFeeDetailID();
    this.loadPostcode();
    this.loadEntType();


  }

  onClose(): void {
    this.dialogRef.close();
  }

  closed(): void {
    this.dialogRef.close();
  }





  //default setting start
  defaultSetting(): void {
    this.error = false;
    // this.errorMessage="";
    this.errorMessages = [];
  }

  //default setting end

  //insert Start
  UpdateProfile(): void {
    const url = environment.apiUrl + '/api/sp/v1/updserviceprovidermaintenance';
    const selectedItem = this.mfts.find(item => item.fee_detail_pk === Number(this.feeDetailId));
    console.log('Selected state:', this.state);
    console.log('Mapped state:', this.stateMapping[this.state?.toUpperCase() || '']);


    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    const body: any = {
      i_ag_pf_id: this.agPfId,
      i_profile_nm: this.profileNm,
      i_cust_nm: this.custNm,
      i_cust_addr_1: this.custAddr1,
      i_cust_addr_2: this.custAddr2,
      i_cust_addr_3: this.custAddr3,
      i_cust_postcode: this.postcode,
      i_cust_city: this.city,
      i_cust_state: this.state ? this.stateMapping[this.state.trim().toUpperCase()] || null : null,
      i_cust_email: this.email,
      i_cust_phone: this.phone,
      i_fee_detail_id: selectedItem?.fee_detail_id || this.tempFeeDetailId, // Use selected or existing fee detail ID
      i_entity_type: this.entityType,
      i_entity_no: this.entityNo,
      i_entity_nm: this.entityNm
    };
    console.log('body:', body);


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
  //insert End

  checkRequiredField(): Boolean {



    if (this.profileNameControl.invalid) {
      this.error = true;
      this.errorMessages.push('Profile Name is required');
    }



    // if (this.customerNameControl.invalid) {
    //   this.error = true;
    //   this.errorMessages.push('Control Name is required');
    // }

    // if (this.taxCodeNameBMControl.invalid) {
    //   this.error = true;
    //   this.errorMessages.push('Tax Code Name (BM) is required');
    // }

    // if (this.taxPercentageControl.invalid) {
    //   this.error = true;
    //   this.errorMessages.push('Tax Percentage is required');
    // }

    if (this.error) {
      return true;
    }

    else {
      return false;
    }
  }

  async validation(): Promise<boolean> {
    this.defaultSetting();

    // Check for required fields
    if (this.checkRequiredField()) {
      return true;
    }

    // Check for duplicate Tax Code
    if (await this.isDuplicateProfileName()) {
      this.error = true;
      this.errorMessages.push('Profile Name is duplicate.');
      return true;
    }
    return false;


  }

  async isDuplicateProfileName(): Promise<boolean> {
    if (!this.profileNm || this.tempProfileNm === this.profileNm) {
      return false;
    }

    const url = environment.apiUrl + '/api/sp/v1/getserviceprovidermaintenance';

    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    const body: any = {
      i_page: '1',
      i_size: '1000',
      i_profile_nm: this.profileNm
      // i_status: this.taxCodeStatus
    };

    try {
      const response: any = await this.http.post(url, body, { headers }).toPromise();
      // Filter for exact match
      const isDuplicate = response.data.some((item: any) => item.profile_nm === this.profileNm);
      return isDuplicate;
    } catch (error) {
      console.error(error);
      this.errorMessages.push('Internal Server Error.');
      return true;
    }
  }

  handleFormSubmit(form: NgForm) {
    if (form.valid) {
      this.update();
    } else {
      Object.values(form.controls).forEach((control) => {
        control.markAsTouched();
      });
    }
  }


  async update() {
    this.isLoading = true;
    this.defaultSetting();

    const isValid = await this.validation();

    //false means no error, validation passed, can insert
    if (!isValid) {
      // this.dialogRef.close();
      this.UpdateProfile();

      this.dialogRef.close('updated');
    }
    this.isLoading = false;
  }
  //form handle before submit end






  //postcode start
  loadPostcode() {

    const url = environment.apiUrl + '/api/rms/v1/getpostcode';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    // const Body: any = {
    // };

    this.http.post(url, {}, { headers }).subscribe(
      (response: any) => {

        if (response.data.length === 0) {
          this.totalPostCodeRecords = 0;
        } else {
          this.postCodes = response.data;
          this.totalPostCodeRecords = response.data[0].total;
          this.extractUniqueCitiesAndStates();
        }
      },
      (error) => {
        console.error('There was an error retrieving the postcode:', error);
      }
    );

  }

  onPostcodeChange(selectedPostcode: string | null) {
    if (!selectedPostcode) {
      this.city = null;
      this.state = null;

      if (this.cityRef) this.cityRef.control.markAsTouched();
      if (this.stateRef) this.stateRef.control.markAsTouched();

      return;
    }

    const match = this.postCodes.find(p => String(p.postcode) === selectedPostcode);
    this.city = match ? match.city : null;
    this.state = match ? match.state : null;
  }


  extractUniqueCitiesAndStates() {
    this.uniqueCities = [...new Set(this.postCodes.map(p => p.city))].sort((a, b) =>
      a.localeCompare(b)
    );

    this.uniqueStates = [...new Set(this.postCodes.map(p => p.state))].sort((a, b) =>
      a.localeCompare(b)
    );
  }

  upperCity = (term: string): string | null => {
    if (!term) return null;
    const trimmed = term.trim().toUpperCase();
    return trimmed.length > 50 ? trimmed.substring(0, 50) : trimmed; //allow maximum 50 characters
  };


  checkTag = (term: string): string | null => {
    if (/^\d{1,5}$/.test(term)) {
      return term; // ensure 1–5 digit number
    }

    return null;
  };

  //postcode end

  //get param_cd of state
  updateShortformState(): void {
    const match = this.states.find(s => s.nm_en === this.state);
    this.shortformstate = match ? match.param_cd : null;
  }



  mfts: MFT[] = [];
  page = environment.DefaultPage;
  itemsPerPage = environment.ItemPerPage;
  dropDownSize = environment.DropDownSize;
  mftsForFeeDetailIdAndName: MFT[] = [];
  tempFeeDetailNmEn: string | null = null;

  async getFeeDetailIDAndNmByPK(): Promise<void> {

    const url = environment.apiUrl + '/api/mft/v1/getmasterfeetable';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    const Body: any = {
      i_page: this.page,
      i_size: this.dropDownSize,
      i_fee_detail_pk: this.tempFeeDetailPK,

      i_status: Systemstatus.Active
    };

    try {
      const response: any = await this.http.post(url, Body, { headers }).toPromise();
      if (response.header.statusCode === '00') {
        this.mftsForFeeDetailIdAndName = response.data;
        this.tempFeeDetailId = this.mftsForFeeDetailIdAndName[0].fee_detail_id;
        this.tempFeeDetailNmEn = this.mftsForFeeDetailIdAndName[0].fee_detail_nm_e;
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


  tempFeeDetailPK: number | null = null;

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
      i_ss_cd: 'SP',
      i_status: Systemstatus.Active
    };

    try {
      const response: any = await this.http.post(url, Body, { headers }).toPromise();
      if (response.header.statusCode === '00') {
        this.mfts = response.data;
        // Filter out duplicate fee_detail_id and fee_detail_nm_e values
        // this.mfts = Array.from(
        //   new Map(this.mfts.map(item => [`${item.fee_detail_id}-${item.fee_detail_nm_e}`, item])).values()
        // );


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

  isReadOnly = false;
  enType: ParamData[] = [];

  loadEntType() {
    this.ParamService.getStates('1', '100', '', 'EntityType').subscribe(
      (response: any) => {
        if (response && response.data && Array.isArray(response.data)) {
          this.enType = response.data;

          // Only set entityType if it is not already set or not found in options
          const found = this.enType.find(e => e.param_cd === this.entityType);
          if (!found) {
            // If editing, don't override with first option
            if (this.entityType == null && this.enType.length > 0) {
              this.entityType = this.enType[0].param_cd;
            }
          }
          // If found, do nothing; entityType is already correct
        } else {
          console.error('Invalid response format:', response);
        }
      },
      (error: any) => {
        console.error('There was an error retrieving the status:', error);
      }
    );
  }
}
