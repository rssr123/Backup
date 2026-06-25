import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Component, OnInit, ViewChild } from '@angular/core';
import { NgForm, NgModel } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { ParamData } from 'src/app/core/models/param.interface';
import { PostCodeData } from 'src/app/core/models/postcode.interface';
import { environment } from 'src/environments/environment';
import { ParamService } from '../../../core/services/param.service';
import { MFT } from 'src/app/core/models/entity';
import { Systemstatus } from 'src/app/shared/enums/systemstatus';

@Component({
  selector: 'app-service-provider-maintenance-add',
  templateUrl: './service-provider-maintenance-add.component.html',
  styleUrls: ['./service-provider-maintenance-add.component.scss']
})
export class ServiceProviderMaintenanceAddComponent implements OnInit {
  @ViewChild('cityRef') cityRef!: NgModel;
  @ViewChild('stateRef') stateRef!: NgModel;


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

  @ViewChild('profileNameRef') profileNameControl!: NgModel;
  @ViewChild('customerNameRef') customerNameControl!: NgModel;
  @ViewChild('customerAddress1Ref') customerAddress1Control!: NgModel;
  @ViewChild('customerAddress2Ref') customerAddress2Control!: NgModel;
  @ViewChild('customerAddress3Ref') customerAddress3Control!: NgModel;
  @ViewChild('postcodeRef') postcodeControl!: NgModel;
  @ViewChild('cityRef') cityControl!: NgModel;
  @ViewChild('stateRef') stateControl!: NgModel;
  @ViewChild('emailRef') emailControl!: NgModel;
  @ViewChild('phoneRef') phoneControl!: NgModel;
  @ViewChild('feeDetailIdRef') feeDetailIdControl!: NgModel;
  @ViewChild('entityTypeRef') entityTypeControl!: NgModel;
  @ViewChild('entityNoRef') entityNoControl!: NgModel;
  @ViewChild('entityNameRef') entityNameControl!: NgModel;

  errorMessages: string[] = [];
  error: boolean = false;
  profileNm: String | null = null;
  custNm: String | null = null;
  custAddr1: String | null = null;
  custAddr2: String | null = null;
  custAddr3: String | null = null;
  custPostcode: String | null = null;
  custCity: String | null = null;
  custState: String | null = null;
  custEmail: String | null = null;
  custPhone: String | null = null;
  feeDetailId: String | null = null;
  fee_detail_id: String | null = null;
  entityType: String | null = null;
  entityNo: String | null = null;
  entityNm: String | null = null;
  postcode: string | null = null;
  city: string | null = null;
  state: string | null = null;
  shortformstate: string | null = null;
  states: any[] = [];
  uniqueCities: string[] = [];
  uniqueStates: string[] = [];
  postCodes: PostCodeData[] = [];
  totalPostCodeRecords: number = 0;


  totalRecords: number = 0;
  isLoading: boolean = false;

  entityTypeList = ['Company', 'Individual', 'Organization'];

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
    public dialogRef: MatDialogRef<ServiceProviderMaintenanceAddComponent>,
    private http: HttpClient,
    private ParamService: ParamService,
  ) { }

  ngOnInit(): void {
    this.loadEntType();
    this.defaultSetting();
    this.loadPostcode();
    this.populateFeeDetailID();
  }

  onClose(): void {
    this.dialogRef.close();
  }

  closed(): void {
    this.dialogRef.close();
  }

  async submit() {
    this.isLoading = true;
    this.defaultSetting();

    const isValid = await this.validation();

    //false means no error, validation passed, can insert
    if (!isValid) {
      // this.dialogRef.close();
      this.InsertProfile();

      this.dialogRef.close('inserted');
    }
    this.isLoading = false;
  }

  //default setting start
  defaultSetting(): void {
    this.error = false;
    // this.errorMessage="";
    this.errorMessages = [];
  }

  //default setting end

  

  //validation start
  checkRequiredField(): Boolean {
    if (this.profileNameControl.invalid) {
      this.error = true;
      this.errorMessages.push('Profile Name required');
    }

    if (this.customerNameControl.invalid) {
      this.error = true;
      this.errorMessages.push('Customer Name required');
    }

    if (this.customerAddress1Control.invalid) {
      this.error = true;
      this.errorMessages.push('Customer Address required');
    }

    if (this.postcodeControl.invalid) {
      this.error = true;
      this.errorMessages.push('Postcode required');
    }


    if (this.cityControl.invalid) {
      this.error = true;
      this.errorMessages.push('City is required');
    }

    if (this.stateControl.invalid) {
      this.error = true;
      this.errorMessages.push('State is required');
    }

    if (this.emailControl.invalid) {
      this.error = true;
      this.errorMessages.push('Email is required');
    }

    if (this.phoneControl.invalid) {
      this.error = true;
      this.errorMessages.push('Phone is required');
    }

    if (this.feeDetailIdControl.invalid) {
      this.error = true;
      this.errorMessages.push('Fee Detail ID is required');
    }

    if (this.entityTypeControl.invalid) {
      this.error = true;
      this.errorMessages.push('Entity Type is required');
    }

    if (this.entityNoControl.invalid) {
      this.error = true;
      this.errorMessages.push('Entity No is required');
    }

    if (this.entityNameControl.invalid) {
      this.error = true;
      this.errorMessages.push('Entity Name is required');
    }




    if (this.error) {
      return true;
    } else {
      return false;
    }
  }

  // async validation(): Promise<boolean> {
  //   // if(this.ValidationRequiredField()){
  //   //   return true;
  //   // }

  //   if (!this.taxCode) {
  //     // Form is not valid, you can handle this case or simply return
  //     return true;
  //   }

  //   const url = environment.apiUrl + '/api/tc/v1/gettaxcode';

  //   const headers = new HttpHeaders({
  //     Authorization: environment.authKey,
  //     'Content-Type': 'application/json',
  //   });

  //   const body: any = {
  //     i_page: '1',
  //     i_size: '1',
  //     i_tax_cd: this.taxCode,
  //   };

  //   try {
  //     const response: any = await this.http
  //       .post(url, body, { headers })
  //       .toPromise();

  //     if (response.header.statusCode === '01') {
  //       return false;
  //     } else {
  //       this.error = true;
  //       this.errorMessages.push('Tax Code is duplicate.');
  //       return true;
  //     }
  //   } catch (error) {
  //     this.error = true;
  //     this.errorMessages.push('Internal Server Error.');
  //     console.error(error);
  //     return true;
  //   }
  // }
  //validation end

  isReadOnly = false;
  enType: ParamData[] = [];

  loadEntType() {
    this.ParamService.getStates('1', '100', '', 'EntityType').subscribe(
      (response: any) => {
        if (response && response.data && Array.isArray(response.data)) {
          //this.states = response.data as ParamData[];
          this.enType.push({
            param_cd: '',
            nm_en: '',
            nm_bm: '',
            total: 5
          }); //add 'All' options
          //this.states.push(response.data);
          this.enType = [...this.enType, ...response.data];
          //this.states.sort((a, b) => a.nm_en.localeCompare(b.nm_en)); // sorting
          this.entityType = this.enType[0].param_cd;
        } else {
          console.error('Invalid response format:', response);
        }
      },
      (error: any) => {
        console.error('There was an error retrieving the status:', error);
      }
    );
  }


  //form handle before submit start
  handleFormSubmit(form: NgForm) {
    if (form.valid) {
      this.submit();
    } else {
      Object.values(form.controls).forEach((control) => {
        control.markAsTouched();
      });
    }
  }
  //form handle before submit end

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

  //validation start
  // checkRequiredField(): Boolean {
  //   if (this.taxCodeControl.invalid) {
  //     this.error = true;
  //     this.errorMessages.push('Tax Code is required');
  //   }

  //   if (this.taxCodeNameENControl.invalid) {
  //     this.error = true;
  //     this.errorMessages.push('Tax Code Name (EN) is required');
  //   }

  //   if (this.taxCodeNameBMControl.invalid) {
  //     this.error = true;
  //     this.errorMessages.push('Tax Code Name (BM) is required');
  //   }

   
  //   if (this.taxPercentageControl.invalid ) {
  //     this.error = true;
  //     this.errorMessages.push('Tax Percentage is required');

  //   }

    


  //   if (this.error) {
  //     return true;
  //   } else {
  //     return false;
  //   }
  // }
  //profileNm: String | null = null;

  async validation(): Promise<boolean> {
    this.defaultSetting();
  
    // Required field check
    // const isRequiredFieldInvalid = this.checkRequiredField();
    // if (isRequiredFieldInvalid) {
    //   return true;
    // }
  
    const url = environment.apiUrl + '/api/sp/v1/getserviceprovidermaintenance';
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });
  
    const checkFields = [
      { field: 'i_profile_nm', value: this.profileNm?.trim().toLowerCase(), key: 'profile_nm', message: 'Profile Name is duplicate.' }
      // { field: 'i_tax_cd_nm_en', value: this.taxCodeNameEN?.trim().toLowerCase(), key: 'tax_cd_nm_en', message: 'Tax Code Name (EN) is duplicate.' },
      // { field: 'i_tax_cd_nm_bm', value: this.taxCodeNameBM?.trim().toLowerCase(), key: 'tax_cd_nm_bm', message: 'Tax Code Name (BM) is duplicate.' }
    ];
  
    for (let i = 0; i < checkFields.length; i++) {
      const body: any = {
        i_page: '1',
        i_size: '1000', // Large size to get all matching records
      };
      body[checkFields[i].field] = checkFields[i].value;
  
      try {
        const response: any = await this.http.post(url, body, { headers }).toPromise();
  
        // Check for exact match (case-insensitive)
        const exactMatch = response.data.some((item: any) => 
          item[checkFields[i].key]?.trim().toLowerCase() === checkFields[i].value
        );
  
        if (exactMatch) {
          this.error = true;
          this.errorMessages.push(checkFields[i].message);
        }
      } catch (error) {
        this.error = true;
        this.errorMessages.push('Internal Server Error.');
        console.error(error);
        return true;
      }
    }
  
    return this.error; // Return true if there are errors, otherwise false
  }

  //postcode start
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

  /// end postcode

  //////
  mfts: MFT[] = [];
  page = environment.DefaultPage;
  itemsPerPage = environment.ItemPerPage;
  dropDownSize = environment.DropDownSize;
  mftsForFeeDetailIdAndName: MFT[] = [];
  tempFeeDetailId: string | null = null;
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
      i_ss_cd: 'iSPARS',
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
  //////

  //insert Start
  InsertProfile(): void {
    const url = environment.apiUrl + '/api/sp/v1/insserviceprovidermaintenance';
    const selectedItem = this.mfts.find(item => item.fee_detail_pk === this.tempFeeDetailPK);
    

    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    const body: any = {
      i_profile_nm: this.profileNm || '',
      i_cust_nm: this.custNm || '',
      i_cust_addr_1: this.custAddr1 || '',
      i_cust_addr_2: this.custAddr2 || '',
      i_cust_addr_3: this.custAddr3 || '',
      i_cust_postcode: this.postcode || '',
      i_cust_city: this.city || '',
      i_cust_state: this.stateMapping[this.state?.toUpperCase() || ''] || null, // Map state to short form
      i_cust_email: this.custEmail || '',
      i_cust_phone: this.custPhone || '',
      i_fee_detail_id: selectedItem?.fee_detail_id,
      i_entity_type: this.entityType || '',
      i_entity_no: this.entityNo || '',
      i_entity_nm: this.entityNm || ''
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

  

}
