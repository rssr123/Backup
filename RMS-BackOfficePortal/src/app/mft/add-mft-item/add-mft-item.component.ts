import { Component, OnInit, ViewChild, ElementRef, ChangeDetectorRef, ViewEncapsulation, AfterViewInit } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { environment } from 'src/environments/environment';
import { FeeGroup } from '../../core/models/fee-group';
import { DatePipe, DecimalPipe } from '@angular/common';
import { Param, SourceSystemCode, TaxCode, User } from '../../core/models/entity';
import { Observable, ReplaySubject, forkJoin, of, throwError } from 'rxjs';
import { catchError, concatMap, delay, map } from 'rxjs/operators';
import { FormBuilder, FormControl, FormGroup, NgForm, NgModel, Validators } from '@angular/forms';
import { Systemstatus } from '../../shared/enums/systemstatus';
import { Router } from '@angular/router';
import { DataService } from '../../core/services/data.service';
import { trigger, state, style, transition, animate } from '@angular/animations';

@Component({
  selector: 'app-add-mft-item',
  templateUrl: './add-mft-item.component.html',
  styleUrls: ['./add-mft-item.component.scss'],
  animations: [
    trigger('fadeInOut', [
      transition(':enter', [   // :enter is alias to 'void => *'
        style({opacity:0}),
        animate(600, style({opacity:1})) 
      ]),
      transition(':leave', [   // :leave is alias to '* => void'
        animate(600, style({opacity:0})) 
      ])
    ])
  ]
})


export class AddMftItemComponent implements OnInit, AfterViewInit{

  username = this.authService.username
    
  /*@ViewChild('feeGroupIdRef') feeGroupIdControl!: NgModel;
  @ViewChild('feeGroupIdRef') feeGroupIdControl!: NgModel;
  @ViewChild('feeGroupIdRef') feeGroupIdControl!: NgModel;
  @ViewChild('feeGroupIdRef') feeGroupIdControl!: NgModel;
  @ViewChild('feeGroupIdRef') feeGroupIdControl!: NgModel;
  @ViewChild('feeGroupIdRef') feeGroupIdControl!: NgModel;
  @ViewChild('feeGroupIdRef') feeGroupIdControl!: NgModel;
  @ViewChild('feeGroupIdRef') feeGroupIdControl!: NgModel;*/


//hardcore 'Later need change current date' in submit ()
//hardcode userrole
userRole="REQUESTER"

userHigherOfficialRole=""
feeGroups!:FeeGroup[]
//feeGroupId:string="" //selected value from fee group id drop down



initialNumber=[
  { value:0, label:'False'},
  { value:1, label:'True'},
]
selectedFiles: File[] =[]

errorMessages: string[] = [];
error: boolean = false;
isLoading: boolean = false;
validForm!:FormGroup 
page=1;
itemsPerPage = 10;
totalRecords:number=0;
status:Param[]=[]
dropDownTotalRecord=1000;
sourceSystemCodes:SourceSystemCode[]=[]
selectedSourceSystemCodes: any[] = [];

dropdownSettings = {
  singleSelection: false,
  placeholder:'a',
  idField: 'ss_cd',
  textField: 'ss_cd',
  selectAllText: 'Select All',
  unSelectAllText: 'Unselect All',
  itemsShowLimit: 3,
  allowSearchFilter: true

};

users:User[]=[]
workFlowStatus=""
taxCode:TaxCode[]=[]
datetimeNow: Date = new Date();
formattedDatetime: string = this.formatDate(this.datetimeNow);

//ngmodel 
feeDetailId:string | null = null
feeGroupId : string | null = null
feeDetailNmEn:string | null = null
feeDetailNmBm:string | null = null
feeAmt: string | null = null
promoStartDate:string | null = null
promoEndDate:string | null = null
promoFee : string | null = null
taxCd : string | null = null
allowOTC : string | null = null
llParentId:string | null = null
llStartDay : string | null = null
llStartMth : string | null = null
llEndDay: string | null = null
llEndMth : string | null = null
ledgerCd:string | null = null
sourceSystemCode:string[]=[]
effectiveDate:string | null = null
textRemarks:string | null = null
ssm4uuserrefno:string | null = null

constructor(private http:HttpClient, private datePipe: DatePipe, private cdr: ChangeDetectorRef, private fb: FormBuilder,private router: Router,
  private dataService: DataService, private el: ElementRef){}

  ngAfterViewInit() {
    const placeholder = this.el.nativeElement.querySelector('.dropdown-header .filter-text');
    if (placeholder) {
      placeholder.setAttribute('placeholder', ''); // Set the placeholder to an empty string
    }
  }





ngOnInit():void{

  if(this.userRole=='REQUESTER'){
    this.userHigherOfficialRole="REQUESTERHOD"
  }
  else{
    this.userHigherOfficialRole=""
  }

  this.populateFeeGroupId()
this.populateAppover()
//this.populateStatus()
this.populateSourceSystemCode()
this.populateTaxCode()
}

private formatDate(date: Date): string {
  const dateString = date.toLocaleDateString('en-GB', {
    // Notice 'en-GB' for day-month-year format
    day: '2-digit',
    month: 'short',
    year: 'numeric',
  });
  const timeString = date.toLocaleTimeString('en-US', {
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit',
    hour12: false, // Use 24-hour format
  });

  return `${dateString} ${timeString}`;
}

async submit(){
  this.isLoading = true;
  this.defaultSetting();
  const isValid = await this.validation();

  //false means no error, validation passed, can insert
  if (!isValid) {
    this.insertMasterFeeTableWorkFlow().subscribe((result) => {
      if(result==true){
        this.dataService.setShowInsertAlert(true)
        this.router.navigate(['/mft']);
    };
  });
  }
  this.isLoading = false;
 }


//default setting start
defaultSetting(): void {
    this.error = false;
    // this.errorMessage="";
    this.errorMessages = [];
}

insertMasterFeeTableWorkFlow(){
  const insertURL = environment.apiUrl + '/api/mftwf/v1/insertmasterfeetableworkflow';

  // Set your authorization header
  const headers = new HttpHeaders({
    Authorization: environment.authKey,
    'Content-Type': 'application/json'
  });
  
  let formattedStartDate = this.datePipe.transform(this.promoStartDate, 'dd/MM/yyyy')||"";
  let formattedEndDate = this.datePipe.transform(this.promoEndDate, 'dd/MM/yyyy')||"";
  let formattedEffectiveDate = this.datePipe.transform(this.effectiveDate, 'dd/MM/yyyy')||"";

  //convert selected source system code from array to string
  //let resultString = '';
  let resultString = this.selectedSourceSystemCodes.join(',');
  // this.selectedSourceSystemCodes.forEach(obj => {
  //   resultString += obj.ss_cd + ',';
  // }); 
  // resultString = resultString.slice(0, -1);

//console.log(resultString);


  const requestBody:any={

    i_fee_detail_id: this.feeDetailId,
    i_fee_grp_id: this.feeGroupId,
    i_fee_detail_nm_en: this.feeDetailNmEn,
    i_fee_detail_nm_bm: this.feeDetailNmBm,
    i_fee_amt: this.feeAmt,
  };

if(formattedStartDate && formattedStartDate.trim()){
  requestBody.i_promo_startdt=this.promoStartDate;
}

if(formattedEndDate && formattedEndDate.trim()){
  requestBody.i_promo_enddt = this.promoEndDate;
}


if(this.promoFee && this.promoFee.trim()){
  requestBody.i_promo_fee = this.promoFee;
}
 
requestBody.i_tax_cd = this.taxCd;
requestBody.i_allow_otc=this.allowOTC;

if(this.llParentId && this.llParentId.trim()){
  requestBody.i_ll_parent_id = this.llParentId;
}


if(this.llStartDay && this.llStartDay.trim()){
  requestBody.i_ll_start_day = this.llStartDay;
}

if(this.llStartMth && this.llStartMth.trim()){
  requestBody.i_ll_start_mth = this.llStartMth;
}

if(this.llEndDay && this.llEndDay.trim()){
  requestBody.i_ll_end_day = this.llEndDay;
}

if(this.llEndMth && this.llEndMth.trim()){
  requestBody.i_ll_end_mth = this.llEndMth;
}

  requestBody.i_ledger_cd=this.ledgerCd;
  requestBody.i_ss_cd = resultString;
 // requestBody.i_created_by = 'admin';  //inserted at backend
 // requestBody.i_modified_by='admin';
  requestBody.i_status='P-RHOD';
  
if(formattedEffectiveDate && formattedEffectiveDate.trim()){
  requestBody.i_effective_date = this.effectiveDate;
}
    
if(this.textRemarks){
  requestBody.i_remark=this.textRemarks;
}
    
  requestBody.i_assign_to=this.ssm4uuserrefno;
  requestBody.i_action="Request Add";


  return this.http.post(insertURL, requestBody, { headers }).pipe(
    map((response: any) => {
      if (response.data.length === 0) {
        return false;
      } else {
        console.log('Success response:', response);
        return true
      }
    })
  );
}

populateFeeGroupId(){
  const url = environment.apiUrl + '/api/fg/v1/getfeegroup';

  // Set your authorization header
  const headers = new HttpHeaders({
    Authorization: environment.authKey,
    'Content-Type': 'application/json'
  });

  const Body: any = {
    i_page: this.page.toString(),
    i_size: this.dropDownTotalRecord.toString(),
    i_fee_grp_id:null,
    i_fee_grp_nm_en:null,
    i_fee_grp_nm_bm:null,
    i_modified_by:null,
    i_dt_modified_fr:null,
    i_dt_modified_to:null,
    i_status:Systemstatus.Active

  };

  this.http.post(url, Body, { headers }).subscribe(
    (response:any) => {
      if(response.data.length==0){
      
      }
      else{
        this.feeGroups = response.data;
        
      }
      return response.data;
    },
    (error) => {
      console.error(error);
      return error;
      // Handle errors here
    }
  );

  // return this.http.post<any[]>(url, Body, { headers }).pipe(
  //   map((response: any) => {
  //     if (response.data.length > 0) {
  //       this.feeGroups = response.data;
  //     }
  //     return response.data;
  //   }),
  //   catchError((error) => {
  //     console.error(error);
  //     return throwError(error);
  //   })
  // );
}

i_file_content:any;

async onFileSelected(event:any) {
  
  const file: File = event.target.files[0];
  
  if (file) {
    this.i_file_content = await new Promise((resolve, reject) => {
      const reader = new FileReader();
      
      reader.onload = (e: any) => {
        resolve(e.target.result);
      };
      
      reader.onerror = reject;
      
      reader.readAsDataURL(file);
    });
  }
  
  this.uploadFile(file);

  //const selectedFile = event.target.files[0];
}


uploadFile(file: File) {
  // const formData: FormData = new FormData();
  // formData.append('file', file, file.name);

  const url = environment.apiUrl + '/api/mftwfdoc/v1/addmasterfeetableworkflowdocument';

  // Set your authorization header
  const headers = new HttpHeaders({
    Authorization: 'Basic cm95OnBhc3M=',
    'Content-Type': 'application/json'
  });
    
      const Body: any = {
        i_wf_id: 39,
        i_file_nm: file.name,
        i_file_content: this.i_file_content,
        i_file_type:file.type,
        i_file_size:file.size.toString(),
        i_created_by:null,
        i_modified_by:null,
        i_status:Systemstatus.Active
    
      };

      // const formData = new FormData();

      // formData.append('i_wf_id', '39');
      // formData.append('i_file_nm', file.name);
      // formData.append('i_file_content', file, file.name);
      // formData.append('i_file_type', file.type);
      // formData.append('i_file_size', file.size.toString());
      // formData.append('i_created_by', '');
      // formData.append('i_modified_by', '');
      // formData.append('i_status', Systemstatus.Active);

      console.log(Body);
    
      this.http.post(url, Body, { headers }).subscribe(
        (response:any) => {
          if(response.data<0){
           console.log("upload failed")
           return false;
          }
          else{
            console.log("upload success")
            return true;
          }
        },
        (error) => {
          console.error(error);
          // Handle errors here
        }
      );
 //return this.http.post(environment.apiUrl + '/upload', formData);
}

// base64:any
// onFileSelected(event: any) {

  
//   const selectedFile = event.target.files[0];

//  /* if (selectedFile) {
//     const reader = new FileReader();
//     reader.onload = () => {
//       const arrayBuffer = reader.result as ArrayBuffer;
//   const uint8Array = new Uint8Array(arrayBuffer);
//   const numberArray = Array.from(uint8Array);
//   const base64String = btoa(String.fromCharCode.apply(null, numberArray));
//     }*/

//     let targetEvent = event.target

//     let file :File = targetEvent.files[0];
//     let fileReader:FileReader = new FileReader();

//     fileReader.onload = (e) => {
//       this.base64 = fileReader.result
//     }
//     fileReader.readAsDataURL(file);



//       const url = environment.apiUrl + '/api/mftwfdoc/v1/addmasterfeetableworkflowdocument';

//       // Set your authorization header
//       const headers = new HttpHeaders({
//         Authorization: environment.authKey,
//         'Content-Type': 'application/json'
//       });
    
//       const Body: any = {
//         i_wf_id: 39,
//         i_file_nm: "sf",
//         i_file_content:this.base64,
//         i_file_type:"png",
//         i_file_size:5,
//         i_created_by:null,
//         i_modified_by:null,
//         i_status:Systemstatus.Active
    
//       };
    
    
//       this.http.post(url, Body, { headers }).subscribe(
//         (response:any) => {
//           if(response.data<0){
//            console.log("upload failed")
//           }
//           else{
//             console.log("upload success")
//           }
//         },
//         (error) => {
//           console.error(error);
//           // Handle errors here
//         }
//       );

//       console.log(this.base64);

//     };
//   //  reader.readAsArrayBuffer(selectedFile);
//  // }






 /*const selectedFile = event.target.files[0];
 if (selectedFile) {
    this.convertFileToBlob(selectedFile).then((blob: Blob) => {
      // Do something with the blob, for example, send it to a server or use it in your application
      console.log(blob);
      const url = environment.apiUrl + '/api/mftwfdoc/v1/addmasterfeetableworkflowdocument';

      // Set your authorization header
      const headers = new HttpHeaders({
        Authorization: environment.authKey,
        'Content-Type': 'application/json'
      });
    
      const Body: any = {
        i_wf_id: 39,
        i_file_nm: "sf",
        i_file_content:blob,
        i_file_type:"png",
        i_file_size:5,
        i_created_by:null,
        i_modified_by:null,
        i_status:Systemstatus.Active
    
      };
    
    
      this.http.post(url, Body, { headers }).subscribe(
        (response:any) => {
          if(response.data<0){
           console.log("upload failed")
          }
          else{
            console.log("upload success")
          }
        },
        (error) => {
          console.error(error);
          // Handle errors here
        }
      );

      
      
    });
  }
  }

  convertFileToBlob(file: File): Promise<Blob> {
    return new Promise((resolve, reject) => {
      const reader = new FileReader();

      reader.onloadend = () => {
        // `result` contains the contents of the file as a data URL
        const dataUrl = reader.result as string;

        // Convert data URL to Blob
        const byteString = atob(dataUrl.split(',')[1]);
        const arrayBuffer = new ArrayBuffer(byteString.length);
        const uint8Array = new Uint8Array(arrayBuffer);

        for (let i = 0; i < byteString.length; i++) {
          uint8Array[i] = byteString.charCodeAt(i);
        }

        const blob = new Blob([uint8Array], { type: file.type });
        resolve(blob);
      };

      reader.onerror = (error) => {
        reject(error);
      };

      // Read the file as a data URL
      reader.readAsDataURL(file);
    });
  }*/
   


populateAppover(){

  const url = environment.apiUrl + '/api/mft/v1/getuserbyrole';

  const headers = new HttpHeaders({
    Authorization: environment.authKey,
    'Content-Type': 'application/json'
  });


 const requestBody = {
  i_page: this.page,
  i_size: this.dropDownTotalRecord,
  i_role_nm_en: this.userHigherOfficialRole,
  i_role_nm_bm: this.userHigherOfficialRole,
  i_status: Systemstatus.Active
};

  this.http.post(url, requestBody, { headers }).subscribe(
    (response:any) => {
      if(response.data.length==0){
      
      }
      else{
      this.users = response.data;
        }
    },
    (error) => {
      console.error(error);
      // Handle errors here
    }
  );
}

clearFiles(){
  this.selectedFiles=[]
 // console.log('sizes'+this.selectedFiles)
  
}

populateStatus(){
  const url = environment.apiUrl + '/api/rms/v1/getparam';

// Set your authorization header
const headers = new HttpHeaders({
  Authorization: environment.authKey,
  'Content-Type': 'application/json'
});


 // Create the request body with your form data
 const requestBody = {
  page: this.page,
  size: this.dropDownTotalRecord,
  param_cd: Systemstatus.Active,
  param_grp_nm: 'Status-MFT'
};

// Send an HTTP POST request to the API
this.http.post(url, requestBody, { headers }).subscribe(
  (response:any) => {
    if(response.data.length==0){
      
    }
    else{
    this.status=response.data
    }
    // Handle a successful response (e.g., show a success message)
  },
  (error) => {
    console.error('API error:', error);
    // Handle API errors (e.g., show an error message)
  }
);

}

populateSourceSystemCode(){

  const url = environment.apiUrl + '/api/rms/v1/getsourcesystem';

// Set your authorization header
const headers = new HttpHeaders({
  Authorization: environment.authKey,
  'Content-Type': 'application/json'
});


 // Create the request body with your form data
 const requestBody = {
  i_page: this.page,
  i_size: this.dropDownTotalRecord,
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
  (response:any) => {
    console.log('API response:', response);
    if(response.data.length==0){
      
    }
    else{
    this.sourceSystemCodes=response.data
    // Handle a successful response (e.g., show a success message)
    }
  },
  (error) => {
    console.error('API error:', error);
    // Handle API errors (e.g., show an error message)
  }
);

}

populateTaxCode(){

  const url = environment.apiUrl + '/api/tc/v1/gettaxcode';

  // Set your authorization header
  const headers = new HttpHeaders({
    Authorization: environment.authKey,
    'Content-Type': 'application/json'
  });
  
  
   // Create the request body with your form data
   const requestBody = {

    i_page: this.page,
    i_size: this.dropDownTotalRecord,
    i_tax_cd_id: null,
    i_tax_cd: null,
    i_tax_cd_nm_en: null,
    i_tax_cd_nm_bm: null,
    i_modified_by: null,
    i_dt_modified_fr: null,
    i_dt_modified_to: null,
    i_status: Systemstatus.Active
  };
  
  // Send an HTTP POST request to the API
  this.http.post(url, requestBody, { headers }).subscribe(
    (response:any) => {
      //console.log('API response:', response);
      if(response.data.length==0){
      
      }
      else{
      this.taxCode=response.data
      }
      // Handle a successful response (e.g., show a success message)
    },
    (error) => {
      console.error('API error:', error);
      // Handle API errors (e.g., show an error message)
    }
  );
  
}

async validation(): Promise<boolean> {

   let invalidMTF = true
   let invalidMTFWF = true

  if (!this.feeDetailId) {
    // Form is not valid, you can handle this case or simply return
    return true;
  }


 invalidMTF = await this.checkDuplicateMFT()
 invalidMTFWF = await this.checkDuplicateMFTWF()

  if(invalidMTF === false && invalidMTFWF ===false){
    return false
  }
  else{
    return true
  }
  
}

async checkDuplicateMFT(){

  const url = environment.apiUrl + '/api/mft/v1/getmasterfeetable';

  const headers = new HttpHeaders({
    Authorization: environment.authKey,
    'Content-Type': 'application/json',
  });

  const body: any = {
    i_page: '1',
    i_size: '1',
    i_fee_detail_id:this.feeDetailId,

  };

  try {
    const response: any = await this.http
      .post(url, body, { headers })
      .toPromise();
      console.log('bng'+response.header.statusCode)
    if (response.header.statusCode === '01') {
      return false;
    } else {
      this.error = true;
      this.errorMessages.push('Fee Detail ID is duplicate in Master Fee Table.');
      return true;
    }
  } catch (error) {
    this.error = true;
    this.errorMessages.push('Internal Server Error.');
    console.error(error);
    return true;
  }

}

async checkDuplicateMFTWF(){

  const url = environment.apiUrl + '/api/mftwf/v1/getmasterfeetableworkflow';

  const headers = new HttpHeaders({
    Authorization: environment.authKey,
    'Content-Type': 'application/json',
  });

  const body: any = {
    i_page: '1',
    i_size: '1',
    i_fee_detail_id:this.feeDetailId,

  };

  try {
    const response: any = await this.http
      .post(url, body, { headers })
      .toPromise();
      console.log('bng'+response.header.statusCode)
    if (response.header.statusCode === '01') {
      return false;
    } else {
      this.error = true;
      this.errorMessages.push('Fee Detail ID is duplicate Master Fee Table work flow.');
      return true;
    }
  } catch (error) {
    this.error = true;
    this.errorMessages.push('Internal Server Error.');
    console.error(error);
    return true;
  }

}

//validation end

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

cancel(){
  this.dataService.setShowInsertAlert(false)
  this.router.navigate(['/mft']);
}

/*OnlyNumberAllowed(event:any):boolean{

  const charCode = (event.which)?event.which : event.keyCode;

    if(charCode>31 && (charCode <48 || charCode>57)){
      return false;
    }
    return true;
}*/


//decimalInput: string = ''; // Changed to string type to directly bind to input value
 //fee amt checking start
  formatInput(feeAmtRef: any): void {
    let value = parseFloat(feeAmtRef.value);
    if (!isNaN(value) && value >= 0) {
      this.feeAmt = value.toFixed(2); // Convert number to string with 2 decimal places
    } else if (value < 0) {
      feeAmtRef.control.setErrors({ negative: true }); // Set a custom 'negative' error
    }
  }
 
  validateInput(feeAmtRef: any): void {
    let value = feeAmtRef.value;
    // Check for more than one dot or more than two decimal places
    if ((value.match(/\./g) || []).length > 1 || (value.includes('.') && value.split('.')[1].length > 2)) {
      feeAmtRef.control.setErrors({ pattern: true }); // Set a custom 'pattern' error
    }
  }
//fee amt checking end

 //promo fee checking start
 formatInputPromoFee(promoFeeRef: any): void {

  let value = parseFloat(promoFeeRef.value);
  if (!isNaN(value) && value >= 0) {
    this.promoFee = value.toFixed(2); // Convert number to string with 2 decimal places
  } else if (value < 0) {
    promoFeeRef.control.setErrors({ negative: true }); // Set a custom 'negative' error
  }
}

validateInputPromoFee(promoFeeRef: any): void {

  let value = promoFeeRef.value;
  // Check for more than one dot or more than two decimal places
  if ((value.match(/\./g) || []).length > 1 || (value.includes('.') && value.split('.')[1].length > 2)) {
    promoFeeRef.control.setErrors({ pattern: true }); // Set a custom 'pattern' error
  }
}
//promo fee  checking end

  onClosed(formControl:any){
    formControl.control.markAsTouched();
  }



 // llStartDay checking start
  formatInputllStartDay(llStartDayRef: any): void {
    let value = llStartDayRef.value;
  
    if (value === '') {
      // Allow empty value
      this.llStartDay = value;
    } else {
      let intValue = parseInt(value, 10);
      if (!isNaN(intValue) && intValue >= 0) {
        this.llStartDay = value; // Positive integer
      } else {
        llStartDayRef.control.setErrors({ pattern: true }); // Set a custom 'pattern' error
      }
    }
  }
  
  validateInputllStartDay(llStartDayRef: any): void {
    let value = llStartDayRef.value;
  
    if (value !== '' && !/^\d+$/.test(value)) {
      llStartDayRef.control.setErrors({ pattern: true }); // Set a custom 'pattern' error for non-integer values
    }
  }
  // llStartDay checking end;


  // llStartMth checking start;
   formatInputllStartMth(llStartMthRef: any): void {
    let value = llStartMthRef.value;
  
    if (value === '') {
      // Allow empty value
      this.llStartMth = value;
    } else {
      let intValue = parseInt(value, 10);
      if (!isNaN(intValue) && intValue >= 0) {
        this.llStartMth = value; // Positive integer
      } else {
        llStartMthRef.control.setErrors({ pattern: true }); // Set a custom 'pattern' error
      }
    }
  }
  
  validateInputllStartMth(llStartDayRef: any): void {
    let value = llStartDayRef.value;
  
    if (value !== '' && !/^\d+$/.test(value)) {
      llStartDayRef.control.setErrors({ pattern: true }); // Set a custom 'pattern' error for non-integer values
    }
  }
  // llStartMthRef checking end;


  // llEndDay checking start;
  formatInputllEndDay(llEndDayRef: any): void {
    let value = llEndDayRef.value;
  
    if (value === '') {
      // Allow empty value
      this.llEndDay = value;
    } else {
      let intValue = parseInt(value, 10);
      if (!isNaN(intValue) && intValue >= 0) {
        this.llEndDay = value; // Positive integer
      } else {
        llEndDayRef.control.setErrors({ pattern: true }); // Set a custom 'pattern' error
      }
    }
  }
  
  validateInputllEndDay(llEndDayRef: any): void {
    let value = llEndDayRef.value;
  
    if (value !== '' && !/^\d+$/.test(value)) {
      llEndDayRef.control.setErrors({ pattern: true }); // Set a custom 'pattern' error for non-integer values
    }
  }
  // llEndDay checking end;


  // llEndMth checking start;
  formatInputllEndMth(llEndMthRef: any): void {
    let value = llEndMthRef.value;
  
    if (value === '') {
      // Allow empty value
      this.llEndMth = value;
    } else {
      let intValue = parseInt(value, 10);
      if (!isNaN(intValue) && intValue >= 0) {
        this.llEndMth = value; // Positive integer
      } else {
        llEndMthRef.control.setErrors({ pattern: true }); // Set a custom 'pattern' error
      }
    }
  }
  
  validateInputllEndMth(llEndMthRef: any): void {
    let value = llEndMthRef.value;
  
    if (value !== '' && !/^\d+$/.test(value)) {
      llEndMthRef.control.setErrors({ pattern: true }); // Set a custom 'pattern' error for non-integer values
    }
  }
  // llEndMth checking end;

  onKeyDown(event: KeyboardEvent): void {
    if (event.key === 'Backspace') {
            return;
    }
    // Prevent manual key entry
    event.preventDefault();
    }
}
