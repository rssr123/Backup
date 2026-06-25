import { ChangeDetectorRef, Component, OnInit, ViewChild } from '@angular/core';
import { fadeInOut } from '../../shared/animation';
import { environment } from 'src/environments/environment';
import { FmsLedger } from '../../core/models/fms-ledger.interface';
import { ParamData } from 'src/app/core/models/param.interface';
import { Systemstatus } from '../../shared/enums/systemstatus';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { NgbPaginationConfig } from '@ng-bootstrap/ng-bootstrap';
import { ActivatedRoute, Router } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import { ParamService } from '../../core/services/param.service';
import moment from 'moment';
import { FmsledgerAddComponent } from '../fmsledger-add/fmsledger-add.component';
import { FMSLedgerDocResponse, FmsLedgerDoc } from 'src/app/core/models/fms-ledger-doc.interface';
import { Observable, async } from 'rxjs';
import { GlobalService } from 'src/app/shared/global.service';
import { TranslateService } from '@ngx-translate/core';
import { perm } from 'src/permissions/perm';
import { AuthService } from 'src/app/core/services/auth.service';
import * as XLSX from 'xlsx';

@Component({
  selector: 'app-fmsledger-listing',
  templateUrl: './fmsledger-listing.component.html',
  styleUrls: ['./fmsledger-listing.component.scss'],
  animations: [fadeInOut],
})

export class FmsledgerListingComponent implements OnInit {
  page = environment.DefaultPage;
  itemsPerPage = environment.ItemPerPage;
  model: FmsLedger[] = [];
  modelDoc: FmsLedgerDoc[] = [];
  filecontent: FMSLedgerDocResponse[] = [];
  totalRecords: number = 0;
  validationErrorMessage: String = '';

  fms_ledger_id: String | null = null;
  fms_detail_nm_en: String | null = null;
  fms_detail_id: String | null = null;
  fms_ledger_cd: String | null = null;
  fms_cd: String | null = null;
  fmsId: String | null = null;
  fmsCd: String | null = null;
  file_index: String | null = null;
  file_content = "";
  file_size: number = 0;

  isDisplay: boolean = false;

  isLoading: boolean = false;
  //date range picker
  selected!: Date[]; //{ start?: moment.Moment; end?: moment.Moment };
  bsValue = new Date();
  tempDate!: Date;
  minDate = new Date();
  //date range picker

  addBox: boolean = false;
  viewBox: boolean = false;


  //toogle start
  isToggled: boolean = false;
  rightSectionCollapsed: boolean = true;
  overlayRightSection: boolean = false;
  showUploadField: boolean = false;
  selectedFileName: string | null = null;

  //default pagination
  selectedValue = environment.dropdownOptions[0];
  dropdownOptions = environment.dropdownOptions;

  states: ParamData[] = [];
  selectedState: string = Systemstatus.Active;

  checkResult: number = 0;

  permFMSLedger = perm.FMS_Ledger_Code_View_FMS_Ledger_Code_Details + "," + perm.FMS_Ledger_Code_FMS_Code_Upload_Excel;
  permFMSLedgerAllow = ""; // variable to store allowed permission for the user
  permListAllow: number = 0; // if 0 then not allow to view listing page, else allow
  permFileUploadAllow: number = 0; // if 0 then not allow to activate tax code, else allow

  summary: { found: number; notFound: number } = { found: 0, notFound: 0 };


  LoadData(singleItem: number) {
    this.selectedValue = singleItem;
    this.itemsPerPage = this.selectedValue;

    this.loadData();
  }

  toggleRightSection() {
    this.rightSectionCollapsed = !this.rightSectionCollapsed;
    this.overlayRightSection = !this.overlayRightSection;
  }

  toggleFoundVisibility() {
    this.viewBox = !this.viewBox;
    this.showUploadField = !this.showUploadField;
    if (!this.showUploadField) {
      this.selectedFileName = null;
    }
  }

  goBackToDownload() {
    this.showUploadField = false; // Reset to show download section
    this.selectedFileName = ''; // Clear selected file name (optional)
  }
  //toogle end

  DefaultBox() {
    this.addBox = false;
  }

  AlertBoxInitialize() {
    if (this.addBox) {
      // this.showInsertAlertBox();
    }
  }

  //for alert box start
  showInsertAlert = false;

  showInsertAlertBox() {
    this.showInsertAlert = true;
    setTimeout(() => (this.showInsertAlert = false), 10000);
  }

  showUpdateAlert = false;

  showUpdateAlertBox() {
    this.showUpdateAlert = true;
    setTimeout(() => (this.showUpdateAlert = false), 10000);
  }

  showWrongNameAlert = false;

  showWrongNameAlertBox() {
    this.showWrongNameAlert = true;
    setTimeout(() => (this.showWrongNameAlert = false), 10000);
  }

  showFileSizeAlert = false;

  showFileSizeAlertBox() {
    this.showFileSizeAlert = true;
    setTimeout(() => (this.showFileSizeAlert = false), 10000);
  }


  showResultAlert = false;

  showResultAlertBox() {
    this.showResultAlert = true;
    setTimeout(() => (this.showResultAlert = false), 10000);
  }

  showDeactiveAlert = false;

  showDeactiveAlertBox() {
    this.showDeactiveAlert = true;
    setTimeout(() => (this.showDeactiveAlert = false), 10000);
  }

  showGenericAlert = false;

  showGenericAlertBox() {
    this.showGenericAlert = true;
    setTimeout(() => (this.showGenericAlert = false), 10000);
  }
  //for alert box end

  showUploadAlert = false;

  showUploadAlertBox() {
    this.showUploadAlert = true;
    setTimeout(() => (this.showUploadAlert = false), 10000);
  }

  i_file_content: any;
  event: any;

  handleFileInput(event: any) {
    const fileInput = event.target;
    if (fileInput.files && fileInput.files.length > 0) {
      this.selectedFileName = fileInput.files[0].name;
      this.event = event;
      this.file_size = fileInput.files[0].size;
    } else {
      this.selectedFileName = null;
    }
    // this.onFileSelected(event);

  }

  async onFileSelected(event: any) {

    const file: File = event.target.files[0];

    if (file) {
      if (this.isFileSizeValid(file)) {
        this.fileNmValid(file).subscribe(async (isValid: boolean) => {
          if (isValid) {
            this.i_file_content = await new Promise((resolve, reject) => {
              const reader = new FileReader();

              reader.onload = (e: any) => {
                resolve(e.target.result);
              };

              reader.onerror = reject;

              reader.readAsDataURL(file);
            });

            this.uploadFile(file);
          } else {
            console.error('Please provide a unique file name');
            this.showWrongNameAlertBox();
            this.isLoading = false;
          }
        });
      } else {
        console.error('File size exceeds the limit (5 MB).');
        this.showFileSizeAlertBox();
      this.isLoading = false;
      }
    } else {
      this.showUploadAlertBox();
      this.isLoading = false;
    }


    //const selectedFile = event.target.files[0];
  }


  fileNmValid(file: File): Observable<boolean> {
    const url = environment.apiUrl + '/api/fmsl/v1/checkdocexist';

    const headers = new HttpHeaders({
      Authorization: 'Basic cm95OnBhc3M=',
      'Content-Type': 'application/json'
    });

    const body: any = {
      i_file_nm: file.name
    };

    console.log(body);

    return new Observable<boolean>((observer) => {
      this.http.post(url, body, { headers }).subscribe(
        (response: any) => {
          if (response.data > 0) {
            console.log('File name failed');
            observer.next(false);

          } else {
            console.log('File name passed');
            // this.refreshMainPage();
            observer.next(true);
          }
          observer.complete();
        },
        (error) => {
          console.error(error);
          observer.error(error);
          observer.complete();
        }
      );
    });
  }


  isFileSizeValid(file: File): boolean {
    // Check if the file size is within the limit (5 MB)
    const maxSizeInBytes = 5 * 1024 * 1024; // 5 MB
    return file.size <= maxSizeInBytes;
  }

  // uploadFile(file: File) {

  //   // const formData: FormData = new FormData();
  //   // formData.append('file', file, file.name);
  //   this.isLoading = true;

  //   const url = environment.apiUrl + '/api/fmsl/v1/addfmsdoc';

  //   // Set your authorization header
  //   const headers = new HttpHeaders({
  //     Authorization: 'Basic cm95OnBhc3M=',
  //     'Content-Type': 'application/json'
  //   });

  //   const Body: any = {
  //     i_fms_id: this.fmsId,
  //     i_file_nm: file.name,
  //     i_file_content: this.i_file_content,
  //     i_file_type: file.type,
  //     i_file_size: file.size.toString(),
  //     i_created_by: null,
  //     i_modified_by: null,
  //     i_status: Systemstatus.Inactive,
  //     i_fee_detail_id: null,
  //     i_fee_detail_nm_en: null,
  //     i_fms_ledger_cd: null

  //   };

  //   console.log(Body);

  //   this.http.post(url, Body, { headers }).subscribe(
  //     (response: any) => {
  //       this.file_index = response.data[1]
  //       if (response.data < 0) {
  //         console.log("upload failed")
  //         this.showUploadAlertBox();
  //         // this.isLoading = false;
  //         this.isDisplay = true;
  //         return false;
  //       }
  //       else {
  //         console.log("upload success")
  //         this.refreshMainPage();
  //         this.showInsertAlertBox();
  //         this.loadDoc();
  //         // this.isLoading=false;
  //         // this.isDisplay=true;
  //         this.loadData();
  //         return true;
  //       }
  //     },
  //     (error) => {
  //       console.error(error);
  //       this.showUploadAlertBox();
  //       // this.isLoading = false;
  //       this.isDisplay = true;


  //       // Handle errors here
  //     }
  //   );
  //   //return this.http.post('http://localhost:8080/upload', formData);
  // }

  uploadFile(file: File) {
    // Read the file content
    const reader = new FileReader();
    reader.readAsBinaryString(file);

    reader.onload = (e) => {
      const binaryData = e.target?.result;
      const workbook = XLSX.read(binaryData, { type: 'binary' });
      const sheetName = workbook.SheetNames[0];
      const worksheet = workbook.Sheets[sheetName];
      const jsonData = XLSX.utils.sheet_to_json(worksheet, { header: 1 });

      // Extract column headers and data rows
      const headersExcel: any = jsonData[0]; // First row is headers
      const dataRows = jsonData.slice(1); // Remaining rows are data

      // Find "Fee Detail ID" column index
      const feeDetailIdIndex = headersExcel.indexOf("Fee Detail ID");
      if (feeDetailIdIndex === -1) {
        const message = "Invalid file format. 'Fee Detail ID' column is missing."
        this.showDuplicateBoxAlert(message); // Display in the new alert box
        this.isLoading = false;
        return;
      }

      // Collect all Fee Detail IDs and check for duplicates
      const feeDetailIds = dataRows.map((row: any) => row[feeDetailIdIndex]);
      const duplicateIds = this.findDuplicates(feeDetailIds);

      if (duplicateIds.length > 0) {
        const message = `Duplicate Fee Detail IDs found: ${duplicateIds.join(", ")}`;
        this.showDuplicateBoxAlert(message); // Display in the new alert box
        this.isLoading = false;
        this.cd.detectChanges();
        return;
      }

      this.isLoading = true;
      // Proceed with file upload if no duplicates
      const url = environment.apiUrl + '/api/fmsl/v1/addfmsdoc';
      const headersObj = new HttpHeaders({
        Authorization: 'Basic cm95OnBhc3M=',
        'Content-Type': 'application/json'
      });

      const body: any = {
        i_fms_id: this.fmsId,
        i_file_nm: file.name,
        i_file_content: this.i_file_content,
        i_file_type: file.type,
        i_file_size: file.size.toString(),
        i_created_by: null,
        i_modified_by: null,
        i_status: Systemstatus.Inactive,
        i_fee_detail_id: null,
        i_fee_detail_nm_en: null,
        i_fms_ledger_cd: null
      };

      this.isLoading = true;

      console.log(this.isLoading);

      this.http.post(url, body, { headers: headersObj }).subscribe(
        (response: any) => {
          if (response.data < 0) {
            console.log("Upload failed");
            this.showUploadAlertBox();
            // this.showValidationError(response.data.message);
          } else {
            console.log("Upload success");
            this.refreshMainPage();
            this.showInsertAlertBox();
            this.loadDoc();
            this.loadData();
            this.getSummaryCount();
          }
          this.isLoading = false;
        },
        (error) => {
          console.error(error);
          // this.showUploadAlertBox();
          this.isLoading = false;
          console.log(error.error.header.message)
          this.showValidationError(error.error.header.message);
        }
      );
    };

    reader.onerror = () => {
      this.showErrorMessage("Error reading the file. Please try again.");
      this.isLoading = false;
    };
  }

  showValidationAlert = false;
  // Function to Show Validation Errors in Frontend
  showValidationError(errorMessage: string) {
    this.showValidationAlert = true;
    this.validationErrorMessage = errorMessage;

    setTimeout(() => (this.showValidationAlert = false), 10000);
  }

  // Fetch summary count after file upload
  getSummaryCount() {
    const url = environment.apiUrl + '/api/fmsl/v1/getsummarycount';
    const headersObj = new HttpHeaders({
      Authorization: 'Basic cm95OnBhc3M=',
      'Content-Type': 'application/json'
    });
    const requestBody = { i_fms_id: this.fmsId }; // Adjust based on required request body

    this.http.post(url, requestBody, { headers: headersObj }).subscribe(
      (response: any) => {
        if (response && response.data) {
          this.summary = { found: response.data[0], notFound: response.data[1] };
        }
      },
      (error) => {
        console.error("Error fetching summary count:", error);
      }
    );
  }

  // Helper function to find duplicate values in an array
  findDuplicates(arr: any[]): any[] {
    const seen = new Set();
    const duplicates = new Set();
    arr.forEach(item => {
      if (seen.has(item)) {
        duplicates.add(item);
      } else {
        seen.add(item);
      }
    });
    return Array.from(duplicates);
  }
  showDuplicateBox = false;
  showDuplicateBox2 = false;
  duplicateMessage = ""; // Store the dynamic message
  showDuplicateBoxAlert(message: string) {
    this.duplicateMessage = message; // Store the message
    this.showDuplicateBox2 = true;
    setTimeout(() => (this.showDuplicateBox2 = false), 10000);
  }

  // Function to show error messages
  showErrorMessage(message: string) {
    alert(message); // Replace with a better UI alert if needed
  }

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
    private authService: AuthService
  ) {
    config.maxSize = environment.PaginationMaxSize;
    config.boundaryLinks = true;
    this.translate.setDefaultLang(this.globalService.getGlobalValue()); // default language
    this.translate.use(this.globalService.getGlobalValue());
    this.route.queryParams.subscribe(params => {
      this.fmsCd = params['fmsCd'];
    });

  }

  ngOnInit() {
    //this.selected = new Date();
    this.route.paramMap.subscribe(params => {
      this.fmsId = params.get('id');
    });

    this.bsValue = new Date();
    this.minDate.setMonth(this.bsValue.getMonth() - 1);
    this.selected = [this.minDate, this.bsValue];
    //this.selected[1].setDate(this.selected[1].getDate() + 1);
    // {
    //   start: moment().subtract(1, 'month'),
    //   end: moment(),
    // };

    this.loadStates();
    this.getSummaryCount();


    //load data must be place at last
    this.loadData();

    this.loadDoc();

  }

  async loadDoc() {
    this.isLoading = true;
    const url = environment.apiUrl + '/api/fmsl/v1/getfmsdoc';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    const Body: any = {
      // i_page: this.page.toString(),
      // i_size: 1000000,
    };

    if (this.fmsId && this.fmsId.trim()) {
      Body.i_fms_id = this.fmsId;
    }


    console.log(Body);

    this.http.post(url, Body, { headers }).subscribe(
      async (response: any) => {

        console.log("original data");
        console.log(response.data);

        this.modelDoc = response.data;
        length = response.data.length;

        if (length != 0) {
          this.AlertBoxInitialize();
          this.DefaultBox();
        } else {
          this.isLoading = false;


        }
        // console.log(response.data);
        //  console.log(this.totalRecords);
      },
      (error) => {
        console.error(error);
        this.isLoading = false;

        this.showGenericAlertBox();
      }
    );

  }

  downloadFileContent(fileName: string, fileContent: string): void {
    this.isLoading = true;
    const binaryString = window.atob(fileContent);
    const len = binaryString.length;
    const uint8Array = new Uint8Array(len);

    for (let i = 0; i < len; i++) {
      uint8Array[i] = binaryString.charCodeAt(i);
    }

    const blob = new Blob([uint8Array], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' });

    const url = URL.createObjectURL(blob);

    const anchor = document.createElement('a');
    anchor.href = url;
    anchor.download = fileName;

    document.body.appendChild(anchor);
    anchor.click();

    document.body.removeChild(anchor);
    URL.revokeObjectURL(url);
  }

  downloadFile(fileName: string): void {
    const url = environment.apiUrl + '/api/fmsl/v1/getfmsfilecontent';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    const Body: any = {
      i_file_nm: fileName
    };

    console.log(Body);

    this.http.post(url, Body, { headers }).subscribe(
      (response: any) => {
        // console.log(response.data);
        this.file_content = response.data;
        this.downloadFileContent(fileName, this.file_content);

        if (response.data.length == 0) {
          this.totalRecords = 0;
          // this.showResultAlertBox();
          this.isLoading = false;
        } else {
          this.totalRecords = response.data[0].total;
          this.AlertBoxInitialize();
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

        this.showGenericAlertBox();
      }
    );
  }

  //loadData Start
  loadData() {
    this.authService.checkUserRole(this.authService.username, this.permFMSLedger)
      .subscribe(
        (response: any) => {
          this.permFMSLedgerAllow = response.data;
          this.permListAllow = this.permFMSLedgerAllow.includes(perm.FMS_Ledger_Code_View_FMS_Ledger_Code_Details) ? 1 : 0;
          this.permFileUploadAllow = this.permFMSLedgerAllow.includes(perm.FMS_Ledger_Code_FMS_Code_Upload_Excel) ? 1 : 0;
          console.log(this.permListAllow, this.permFileUploadAllow);
          if (this.permListAllow === 0) {
            console.log("access-denied");
            this.router.navigate(['/access-denied']);
            return; // Exit the function to prevent further execution
          }
        });
    this.isLoading = true;

    const url = environment.apiUrl + '/api/fmsl/v1/getfmsledger';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    const Body: any = {
      i_page: this.page.toString(),
      i_size: this.itemsPerPage.toString(),
    };

    if (this.fmsId && this.fmsId.trim()) {
      Body.i_fms_id = this.fmsId;
    }

    // if (this.file_index) {
    //   Body.i_file_index = this.file_index;
    // }

    if (this.fms_detail_id && this.fms_detail_id.trim()) {
      Body.i_fms_detail_id = this.fms_detail_id;
    }

    if (this.fms_detail_nm_en && this.fms_detail_nm_en.trim()) {
      Body.i_fms_detail_nm_en = this.fms_detail_nm_en;
    }

    if (this.fms_ledger_cd && this.fms_ledger_cd.trim()) {
      Body.i_fms_ledger_cd = this.fms_ledger_cd;
    }

    // if (this.modified_by && this.modified_by.trim()) {
    //   Body.i_modified_by = this.modified_by;
    // }

    // if (this.selected) {
    //   //Body.i_dt_modified_fr =
    //   Body.i_dt_modified_fr = formatDate(this.selected[0], 'YYYY-MM-dd', 'en'); //.format('YYYY-MM-DD');
    //   this.selected[1].setDate(this.selected[1].getDate() + 1);
    //   Body.i_dt_modified_to = formatDate(this.selected[1], 'YYYY-MM-dd', 'en');
    // }

    // // if (this.selected && this.selected.start && this.selected.end) {
    // //   Body.i_dt_modified_fr = this.selected.start.format('YYYY-MM-DD');
    // //   Body.i_dt_modified_to = this.selected.end
    // //     .add(1, 'day')
    // //     .format('YYYY-MM-DD');
    // // }

    let temp = '';

    if (
      this.selectedState.length > 0 &&
      (this.selectedState == "NF" ||
        this.selectedState == "F" || this.selectedState == "DU")
    ) {
      temp = this.selectedState;
    }

    if (this.selectedState == "NF" ||
      this.selectedState == "F" || this.selectedState == "DU") {
      Body.i_found = temp;
    }

    console.log(Body);

    this.http.post(url, Body, { headers }).subscribe(
      (response: any) => {
        this.model = response.data;
        if (this.model.length > 0) {
          this.fms_cd = this.model[0].fms_cd;
        }


        if (response.data.length == 0) {
          this.totalRecords = 0;
          // this.showResultAlertBox();
          this.isLoading = false;
        } else {
          this.totalRecords = response.data[0].total;
          this.AlertBoxInitialize();
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

        this.showGenericAlertBox();
      }
    );

  }
  //loadData End

  addSelected(): void {
    this.DefaultBox();
    const dialogRef = this.dialog.open(FmsledgerAddComponent, {
      width: '50%',
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result === 'inserted') {
        this.addBox = true;
        if (this.event && this.event !== undefined && this.event !== null) {
          this.isLoading = true;
          this.isDisplay = true;
          this.onFileSelected(this.event);
        }
        else {
          this.showUploadAlertBox();
          this.addBox = false;
          this.isLoading = false;
          this.showUploadField = true;
        }
      }
      if (result === 'cancel') {
        this.addBox = false;
        this.isLoading = false;
        this.showUploadField = true;
      }

    });
  }

  apply(): void {
    this.loadData();
    this.rightSectionCollapsed = true;

  }

  reset(): void {
    this.fms_ledger_id = null;
    this.fms_detail_id = null;
    this.fms_detail_nm_en = null;
    this.fms_ledger_cd = null;
    this.selectedState = 'All';
    // this.selected = {
    //   start: moment().subtract(1, 'month'),
    //   end: moment(),
    // };
    this.bsValue = new Date();
    this.minDate.setMonth(this.bsValue.getMonth() - 1);
    this.selected = [this.minDate, this.bsValue];
    this.selected[1].setDate(this.selected[1].getDate() + 1);
  }

  refreshMainPage(): void {
    this.isLoading = true;
    this.page = 1;
    this.loadData();
  }

  loadStates() {
    this.ParamService.getStates('1', '100', '', 'Status-FMS').subscribe(
      (response: any) => {
        if (response && response.data && Array.isArray(response.data)) {
          //this.states = response.data as ParamData[];
          this.states.push({
            param_cd: '',
            nm_en: 'All',
            nm_bm: 'All',
            total: 5,
          }); //add 'All' options
          //this.states.push(response.data);
          this.states = [...this.states, ...response.data];
          //this.states.sort((a, b) => a.nm_en.localeCompare(b.nm_en)); // sorting
        } else {
          console.error('Invalid response format:', response);
        }
      },
      (error) => {
        console.error('There was an error retrieving the status:', error);
      }
    );
  }


  downloadExcelTemplateWithSample() {
    //   try {
    //     // Hardcoded Excel template content with one row of headers
    //     const excelTemplateContent = 'Fee Detail ID\tFee Detail Name (EN)\tLedger Code\n';

    //     // Create a Blob with the hardcoded content
    //     const blob = new Blob([excelTemplateContent], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' });

    //     // Create a link element to trigger the download
    //     const link = document.createElement('a');
    //     link.href = window.URL.createObjectURL(blob);

    //     // Set the file name for the download
    //     const timestamp = new Date().toISOString().replace(/[-T:.Z]/g, ''); // Generate timestamp without special characters
    //     link.download = `FmsLedgerTemplate_${timestamp}.xlsx`;

    //     // Append the link to the document
    //     document.body.appendChild(link);

    //     // Trigger the click event to start the download
    //     link.click();

    //     // Remove the link from the document
    //     document.body.removeChild(link);
    //   } catch (error) {
    //     console.error('Error downloading Excel template:', error);
    //   }
    // }                                                                                                                      downloadExcelTemplate() {
    try {
      // Hardcoded Excel template content with one row of headers
      const excelTemplateContent =
        [
          ['Fee Detail ID', 'Fee Detail Name (EN)', 'Ledger Code'],
          ['FD17-1', 'Fee to Apply Extension of Time S.609 CA2016 (per 30 day)', 'NSF123ABCD12'], // Sample row 1
          ['FD18-1', 'Fee to Appeal to Minister pursuant to S.38E CCMA2001', 'NSF123ABCD12'],
          ['FD18-2', 'Fee to Appeal to Minister pursuant to Reg. 7 of the COMPANIES (PC for COSEC) REGULATIONS 2019', 'NSF123ABCD12'],
          ['FD18-3', 'Fee to Appeal to Minister pursuant to S.27(3) CA2016', 'NSF123ABCD12'],
          ['FD19-1', 'Fee to Apply Exemption pursuant to S.615 CA2016', 'NSF123ABCD12'] // Sample row 2
        ];
      // Create a new workbook
      const workbook = XLSX.utils.book_new();

      // Add the content to a new worksheet
      const worksheet = XLSX.utils.aoa_to_sheet(excelTemplateContent);

      // Add the worksheet to the workbook
      XLSX.utils.book_append_sheet(workbook, worksheet, 'Sheet1');

      // Generate the Excel file
      const excelFile = XLSX.write(workbook, { type: 'array', bookType: 'xlsx' });

      // Convert the array buffer to a Blob
      const blob = new Blob([excelFile], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' });

      // Create a link element to trigger the download
      const link = document.createElement('a');
      link.href = window.URL.createObjectURL(blob);

      // Set the file name for the download
      const timestamp = new Date().toISOString().replace(/[-T:.Z]/g, ''); // Generate timestamp without special characters
      link.download = `FmsLedgerTemplate_${timestamp}.xlsx`; // Use .xlsx extension for Excel file

      // Append the link to the document
      document.body.appendChild(link);

      // Trigger the click event to start the download
      link.click();

      // Remove the link from the document
      document.body.removeChild(link);
    } catch (error) {
      console.error('Error downloading Excel template:', error);
    }
  }

  downloadExcelTemplate() {
    try {
      // Hardcoded Excel template content with one row of headers
      const excelTemplateContent =
        [
          ['Fee Detail ID', 'Fee Detail Name (EN)', 'Ledger Code']
        ];

      // Create a new workbook
      const workbook = XLSX.utils.book_new();

      // Add the content to a new worksheet
      const worksheet = XLSX.utils.aoa_to_sheet(excelTemplateContent);

      // Add the worksheet to the workbook
      XLSX.utils.book_append_sheet(workbook, worksheet, 'Sheet1');

      // Generate the Excel file
      const excelFile = XLSX.write(workbook, { type: 'array', bookType: 'xlsx' });

      // Convert the array buffer to a Blob
      const blob = new Blob([excelFile], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' });

      // Create a link element to trigger the download
      const link = document.createElement('a');
      link.href = window.URL.createObjectURL(blob);

      // Set the file name for the download
      const timestamp = new Date().toISOString().replace(/[-T:.Z]/g, ''); // Generate timestamp without special characters
      link.download = `FmsLedgerTemplate_${timestamp}.xlsx`; // Use .xlsx extension for Excel file

      // Append the link to the document
      document.body.appendChild(link);

      // Trigger the click event to start the download
      link.click();

      // Remove the link from the document
      document.body.removeChild(link);
    } catch (error) {
      console.error('Error downloading Excel template:', error);
    }
  }


  // async checkRecordInUse(fms_ledger_id: any): Promise<any> {
  //   const url = environment.apiUrl + '/api/fg/v1/checkfeegroupexist';

  //   const headers = new HttpHeaders({
  //     Authorization: environment.authKey,
  //     'Content-Type': 'application/json',
  //   });

  //   const body = {
  //     i_fee_grp_id: fee_grp_id,
  //   };
  //   const response: any = await this.http
  //     .post(url, body, { headers })
  //     .toPromise();
  //   try {
  //     this.checkResult = response.data;
  //     console.log('check: ' + response.data);
  //     return response.data;
  //   } catch (error) {
  //     console.error(error);
  //     return error;
  //   }

  // await this.http.post(url, body, { headers }).subscribe(
  //   (response: any) => {
  //     // Handle the response
  //     this.checkResult=response.data;
  //     //let e = response.data;
  //     console.log("check: "+response.data);
  //     return response.data;
  //     //this.loadData();
  //     //this.cd.detectChanges();
  //   },
  //   (error) => {
  //     console.log(error);
  //     return error;
  //     // In case of error, revert the status change in the UI
  //     // item.status = item.status === Systemstatus.Active ? Systemstatus.Inactive : Systemstatus.Active;
  //     // console.error('Error toggling status:', error);
  //     //this.loadData();
  //     //this.cd.detectChanges();
  //   }
  // );
  //}

  toggleButton() {
    this.isToggled = !this.isToggled;
  }

}
