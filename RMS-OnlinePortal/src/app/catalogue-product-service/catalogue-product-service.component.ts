import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { environment } from 'src/environments/environment';
import { Router } from '@angular/router';
import { DecimalPipe } from '@angular/common';
import { NgbPaginationConfig } from '@ng-bootstrap/ng-bootstrap';
import { AuthService } from '../services/auth.service';

@Component({
  selector: 'app-catalogue-product-service',
  templateUrl: './catalogue-product-service.component.html',
  styleUrls: ['./catalogue-product-service.component.scss'],
  providers: [DecimalPipe]
})
export class CatalogueProductServiceComponent implements OnInit {
  page = environment.DefaultPage;
  itemsPerPage = environment.ItemPerPage;
  model: any[] = [];
  totalRecords: number = 0;

  feeDetailNmE: string | null = null;
  itemName: string | null = null;
  billingNo: string | null = null;
  nonBilNo: string | null = null;

  nonBillingItems: any[] = [];
  billingItems: any[] = [];

  selectedCategory: string = 'Catalogue Items'; // Default category
  catalogueCategories = ['Catalogue Items', 'Billing', 'Non-Billing'];

  isDisplay: boolean = false;
  isLoading: boolean = false;

  selectedValue = environment.dropdownOptions[0];
  dropdownOptions = environment.dropdownOptions;

  constructor(private http: HttpClient, private router: Router,config: NgbPaginationConfig, private authService: AuthService) {}

  ngOnInit(): void {
    this.loadData();
  }

  LoadData(singleItem: number) {
    this.selectedValue = singleItem;
    this.itemsPerPage = this.selectedValue;
    this.loadData();
  }

  loadData(): void {
    this.isLoading = true;
  
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });
  
    let url = '';
    let body = {};
  
    if (this.selectedCategory === 'Catalogue Items') {
      url = environment.apiUrl + '/api/catalogue/v1/getcatalogueitem';
      body = {
        i_page: this.page.toString(),
        i_size: this.itemsPerPage.toString(),
        feeDetailNmE: this.itemName, // Pass the item name filter if any
      };
    } else if (this.selectedCategory === 'Billing') {
      url = environment.apiUrl + '/api/blitem/v1/getbilitem';
      body = {
        i_page: this.page.toString(),
        i_size: this.itemsPerPage.toString(),
        i_billing_no: this.billingNo,
        i_cust_email: this.authService.emailSubject.getValue()
      };
    } else if (this.selectedCategory === 'Non-Billing') {
      url = environment.apiUrl + '/api/nonblitem/v1/getnonbilitem';
      body = {
        i_page: this.page.toString(),
        i_size: this.itemsPerPage.toString(),
        i_non_bil_no: this.nonBilNo,
        i_cust_email: this.authService.emailSubject.getValue()
      };
    }
  
    this.http.post(url, body, { headers }).subscribe(
      (response: any) => {
        // Update the arrays based on the selected category
        if (this.selectedCategory === 'Catalogue Items') {
          this.model = response.data || [];
        } else if (this.selectedCategory === 'Billing') {
          this.billingItems = response.data || [];
        } else if (this.selectedCategory === 'Non-Billing') {
          this.nonBillingItems = response.data || [];
        }
        this.totalRecords = response.data?.length > 0 ? response.data[0].total : 0;
        this.isDisplay = true;
        this.isLoading = false;
      },
      (error) => {
        console.error(error);
        this.isDisplay = true;
        this.isLoading = false;
      }
    );
  }

  searchItems(): void {
    this.page = 1; // Reset to the first page for a new search
  
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });
  
    let url = '';
    let body: any = {};
    this.isLoading = true;
  
    if (this.selectedCategory === 'Catalogue Items') {
      url = environment.apiUrl + '/api/catalogue/v1/getcatalogueitem';
      body = {
        i_page: this.page.toString(),
        i_size: this.itemsPerPage.toString(),
        i_fee_detail_nm_e: this.itemName || null, // Include itemName for filtering
      };
    } else if (this.selectedCategory === 'Billing') {
      url = environment.apiUrl + '/api/blitem/v1/getbilitem';
      body = {
        i_page: this.page.toString(),
        i_size: this.itemsPerPage.toString(),
        i_billing_no: this.billingNo || null,
        i_cust_email: this.authService.emailSubject.getValue()
      };
      console.log(this.authService.emailSubject.getValue())
    } else if (this.selectedCategory === 'Non-Billing') {
      url = environment.apiUrl + '/api/nonblitem/v1/getnonbilitem';
      body = {
        i_page: this.page.toString(),
        i_size: this.itemsPerPage.toString(),
        i_non_bil_no: this.nonBilNo || null,
        i_cust_email: this.authService.emailSubject.getValue()
      };
    } else {
      console.warn('Invalid category selected for search.');
      this.isLoading = false;
      return;
    }
  
    this.http.post(url, body, { headers }).subscribe(
      (response: any) => {
        if (this.selectedCategory === 'Catalogue Items') {
          this.model = response.data || [];
        } else if (this.selectedCategory === 'Billing') {
          this.billingItems = response.data || [];
        } else if (this.selectedCategory === 'Non-Billing') {
          this.nonBillingItems = response.data || [];
        }
  
        this.totalRecords = response.data?.length > 0 ? response.data[0].total : 0;
        this.isDisplay = true;
        this.isLoading = false;
      },
      (error) => {
        console.error(error);
        this.isDisplay = true;
        this.isLoading = false;
      }
    );
  }
  
  resetFilters() {
    this.itemName = null;
    this.billingNo = null;
    this.nonBilNo = null;
	}

  onCategoryChange() {
    this.loadData(); // Reload data when category changes
  }
  
  changeCategory(category: string): void {
    this.selectedCategory = category;
    this.loadData();  // Reload data based on the selected category
  }

  payForItem(item: any) {
    this.router.navigate(['/catalogue-json'], { state: { selectedItem: item } });
  }

  payBil(billingNo: string): void {
    this.router.navigate(['/payment-page'], { queryParams: { pr: billingNo, from: 'cat' } });
  }

  payNonBilling(nonBilNo: string): void {
    this.router.navigate(['/payment-page'], { queryParams: { pr: nonBilNo, from: 'cat' } });
  }

  fetchCatalogueItems() {
    const catalogueItemRequest = {
      i_page: 1,
      i_size: 10,
      i_fee_detail_nm_e: '',
      i_quantity: 1
    };
  
    this.http.post<any>(environment.apiUrl + '/api/catalogue/v1/getcatalogueitem', catalogueItemRequest)
      .subscribe(response => {
        if (response && response.data) {
          localStorage.setItem('catalogueData', JSON.stringify(response.data)); // Save data to local storage
        }
      });
  }
  
}
