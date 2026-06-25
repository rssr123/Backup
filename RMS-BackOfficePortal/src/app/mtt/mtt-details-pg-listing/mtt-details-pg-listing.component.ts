import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { TranslateService } from '@ngx-translate/core';
import { MTTPGDetails } from 'src/app/core/models/mtt-details.interface';
import { GlobalService } from 'src/app/shared/global.service';
import { environment } from 'src/environments/environment';

@Component({
  selector: 'app-mtt-details-pg-listing',
  templateUrl: './mtt-details-pg-listing.component.html',
  styleUrls: ['./mtt-details-pg-listing.component.scss']
})
export class MttDetailsPgListingComponent {
  MTTItemDetails: MTTPGDetails[] = [];
  isDisplay: boolean = false;
  isLoading: boolean = false;
  mtt_item_id: String | null = null;
  
  
  
  constructor(
    @Inject(MAT_DIALOG_DATA) public data: any,
    public dialogRef: MatDialogRef<MttDetailsPgListingComponent>,
    private http: HttpClient,
    private translate: TranslateService,
    private globalService: GlobalService
  ) {
    this.translate.setDefaultLang(this.globalService.getGlobalValue()); // default language
    this.translate.use(this.globalService.getGlobalValue());
  }
  closed(): void {
    this.dialogRef.close();
  }
  
  ngOnInit(): void {
    this.mtt_item_id = this.data.mtt_item_id;
    this. loadData();
  
  }
  
  loadData(): void {
    this.isLoading = true;
    const url = environment.apiUrl + '/api/mttl/v1/getmttpgdetails';
  
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });
  
    const body: any = {
      i_mtt_pg_id: this.mtt_item_id,
    };
  
    try {
      this.http
        .post(url, body, { headers })
        .subscribe(
          (response: any) => {
          this.MTTItemDetails = response.data;
          console.log('Success response:', response);
          this.isLoading = false;
        },
        (error) => {
          console.error('Error:', error);
        }
      );
    } catch (error) {
      console.error(error);
    }
  }
  
  
  
  }
  
