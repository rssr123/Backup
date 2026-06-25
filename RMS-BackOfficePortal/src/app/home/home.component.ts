import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { TranslateService } from '@ngx-translate/core';
import { environment } from '../../environments/environment';
import { GlobalService } from '../shared/global.service';
import { AuthService } from '../core/services/auth.service';
import { Component, OnInit, AfterViewInit, ViewChild, ElementRef, Renderer2, ChangeDetectorRef, HostListener, Injectable } from '@angular/core';
import { animate, state, style, transition, trigger } from '@angular/animations';
import { ChartConfiguration } from 'chart.js';
import { LanguageService } from '../language.service';
import { BaseChartDirective } from 'ng2-charts';
import { take } from 'rxjs';
import { Router } from '@angular/router';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss'],
  animations: [
    trigger('slideIn', [
      state('void', style({ transform: 'translateX(100%)' })), // Initial position: off-screen to the right
      transition(':enter', [
        animate('1s ease-out', style({ transform: 'translateX(0)' })) // Animation to bring it to position
      ])
    ])
  ]
})
export class HomeComponent implements OnInit {
  @ViewChild('logoImage') logoImage!: ElementRef;
  @ViewChild('labels') labels!: ElementRef;
  @ViewChild(BaseChartDirective) chart!: BaseChartDirective;

  isLoggedIn: boolean = false; // Track login status
  isAuthenticating: boolean = false; // Track if we're checking authentication
  current_lang: string = '';

  constructor(private translate: TranslateService, private http: HttpClient,
    private globalService: GlobalService, private authService: AuthService,
    private elementRef: ElementRef, private renderer: Renderer2,
    private cdref: ChangeDetectorRef, private cd: ChangeDetectorRef, 
    private languageService: LanguageService, private router: Router) {
    this.translate.setDefaultLang(this.globalService.getGlobalValue()); // default language
    this.translate.use(this.globalService.getGlobalValue());
  }

  useLanguage(language: string) {
    this.globalService.setGlobalValue(language);
    this.translate.use(language); // to change the language at runtime
  }

  async ngOnInit() {
    // Check if we're returning from login
    const currentUrl = window.location.href;
    const hasSessionHint = this.authService.username && this.authService.isLoggedOut !== true;
    
    if (currentUrl.includes('?') || hasSessionHint) {
      this.isAuthenticating = true;
    }
    
    this.authService.handleSuccessfulLogin();

    // Subscribe to authentication status
    this.authService.isAuthenticated.pipe(take(2)).subscribe(authStatus => {
      console.log('Auth status:', authStatus);
      this.isAuthenticating = false;

      if (authStatus && !this.isLoggedIn) {
        this.isLoggedIn = true;
        this.getDashboardInit(); // Fetch initial dashboard data
      }
    });

    // Listen for language changes
    this.translate.onLangChange.subscribe((event) => {
      this.current_lang = event.lang;
      console.log("Language updated to:", this.current_lang);
      this.cd.detectChanges();
    });
  }

  fetchData(): void {
    const url = environment.apiUrl + '/api/mft/v1/getFeeDetailListing';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/x-www-form-urlencoded'
    });

    const body = new URLSearchParams();
    body.set('feeGrpId', '');
    body.set('ssCd', '');


    // Make the HTTP GET request
    this.http.post(url, body.toString(), { headers }).subscribe(
      (response) => {
        console.log(response);
        // You can process the response data here
      },
      (error) => {
        console.error(error);
        // Handle errors here
      }
    );
  }

  // adjustLabelFontSizes(): void {
  //   const logoImageWidth = this.logoImage.nativeElement.offsetWidth;
  //   const labels = this.labels.nativeElement.querySelectorAll('label');

  //   labels.forEach((label: HTMLElement) => {
  //     const fontSize = logoImageWidth * 0.015; // Adjust the multiplier as needed
  //     label.style.fontSize = fontSize + 'px';
  //   });
  // }

  title = 'ng2-charts-demo';

  public barChartLegend = true;
  public barChartPlugins = [];

  public barChartData: ChartConfiguration<'bar'>['data'] = { labels: [], datasets: [{ data: [], label: 'Receipts' }] };
  public barChartDataRevenue: ChartConfiguration<'bar'>['data'] = { labels: [], datasets: [{ data: [], label: 'Revenue' }] };

  // Common bar chart options
  public barChartOptions: ChartConfiguration<'bar'>['options'] = this.createBarChartOptions('Number of Receipts');
  public barChartOptionsRevenue: ChartConfiguration<'bar'>['options'] = this.createBarChartOptions('Total Revenue (million)');


  // BM
  public barChartDataBM: ChartConfiguration<'bar'>['data'] = { labels: [], datasets: [{ data: [], label: 'Resit' }] };
  public barChartDataRevenueBM: ChartConfiguration<'bar'>['data'] = { labels: [], datasets: [{ data: [], label: 'Hasil' }] };

  // Common bar chart options
  public barChartOptionsBM: ChartConfiguration<'bar'>['options'] = this.createBarChartOptionsBM('Jumlah Resit');
  public barChartOptionsRevenueBM: ChartConfiguration<'bar'>['options'] = this.createBarChartOptionsBM('Jumlah Hasil (Juta)');


  // Helper to create chart options with a dynamic y-axis label
  createBarChartOptions(yLabel: string): ChartConfiguration<'bar'>['options'] {
    return {
      responsive: true,
      scales: {
        x: {
          type: 'category',
          title: {
            display: true,
            text: 'Time Period',
            // text: xLabel,
            font: { weight: 'bold', size: 14 } // Bold axis label
          },
          ticks: {
            font: { weight: 'bold', size: 12 } // Bold values on x-axis
          },
        },
        y: {
          type: 'linear',
          title: {
            display: true,
            text: yLabel,
            font: { weight: 'bold', size: 14 } // Bold axis label
          },
          ticks: {
            font: { weight: 'bold', size: 12 } // Bold values on y-axis
          },
          beginAtZero: true
        }
      },
      plugins: {
        legend: {
          display: true,
          position: 'top',
          labels: {
            font: { weight: 'bold', size: 14 } // Bold legend text
          }
        }
      }
    };
  }

  createBarChartOptionsBM(yLabel: string): ChartConfiguration<'bar'>['options'] {
    return {
      responsive: true,
      scales: {
        x: {
          type: 'category',
          title: {
            display: true,
            text: 'Tempoh Masa',
            // text: xLabel,
            font: { weight: 'bold', size: 14 } // Bold axis label
          },
          ticks: {
            font: { weight: 'bold', size: 12 } // Bold values on x-axis
          },
        },
        y: {
          type: 'linear',
          title: {
            display: true,
            text: yLabel,
            font: { weight: 'bold', size: 14 } // Bold axis label
          },
          ticks: {
            font: { weight: 'bold', size: 12 } // Bold values on y-axis
          },
          beginAtZero: true
        }
      },
      plugins: {
        legend: {
          display: true,
          position: 'top',
          labels: {
            font: { weight: 'bold', size: 14 } // Bold legend text
          }
        }
      }
    };
  }

  // Selection properties
  selectedReportType: 'year' | 'month' | 'day' = 'year';
  selectedReportTypeRevenue: 'year' | 'month' | 'day' = 'year';
  selectedYear: number | null = null;
  selectedYearRevenue: number | null = null;
  selectedMonth: number | null = null;
  selectedMonthRevenue: number | null = null;

  // Available years and months
  availableYears = [2025, 2026, 2027, 2028, 2029, 2030, 2031, 2032, 2033, 2034, 2035, 2036, 2037, 2038, 2039, 2040]; // Example years, populate as needed
  availableMonths = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12];

  allMonths = Array.from({ length: 12 }, (_, i) => i + 1);
  allMonthsLabel = [
    { id: 1, name: 'Jan' },
    { id: 2, name: 'Feb' },
    { id: 3, name: 'Mar' },
    { id: 4, name: 'Apr' },
    { id: 5, name: 'May' },
    { id: 6, name: 'Jun' },
    { id: 7, name: 'Jul' },
    { id: 8, name: 'Aug' },
    { id: 9, name: 'Sep' },
    { id: 10, name: 'Oct' },
    { id: 11, name: 'Nov' },
    { id: 12, name: 'Dec' }
  ];
  allDays = Array.from({ length: 31 }, (_, i) => i + 1);

  public pieChartData: ChartConfiguration<'pie'>['data'] = {
    labels: [],
    datasets: [{ data: [] }]
  };

  public pieChartLabels = ["Online", 'OTC'];

  public pieChartDatasets: ChartConfiguration<'pie'>['data'] = {
    labels: [],
    datasets: [{ data: [] }]
  };

  // public pieChartDatasets = [{
  //   data: [3024533452.55, 1000331122.55]
  // }];
  refundPieChartData: any;
  public pieChartLabels2 = ["Refunded", 'Pending', 'Rejected'];
  public pieChartDatasets2 = [{
    data: [58172, 12412, 532]
  }];

  public pieChartOptions: ChartConfiguration<'pie'>['options'] = {
    responsive: true,
    plugins: {
      datalabels: {
        color: '#fff', // Color of the labels inside the pie chart
        font: {
          weight: 'bold'
        },
        formatter: (value: number) => `${value}`, // Display raw values inside each slice
      },
      legend: {
        display: true,
        position: 'top',
        labels: {
          font: {
            weight: 'bold', // Make the legend text bold
            size: 14, // Optional: adjust font size if needed
            color: 'black', // Color of the labels inside the pie chart
          }
        }
      }
      // title: {
      //   display: true,
      //   text: 'Chart.js Pie Chart'
      // }
    }
  } as any;

  getDashboardInit() {
    const url = `${environment.apiUrl}/api/dashboard/v1/getdashboardinit`;
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json'
    });
  
    this.http.post(url, {}, { headers }).subscribe((response: any) => {
      if (response.data && response.data.length > 0) {
        const data = response.data;
  
        // Revenue by Year
        const revenueYearData = data.filter((item: any) => item.revenue_year != null);
        this.updateChartRevenueData(revenueYearData, 'revenue_year', 'revenue');
  
        // Revenue by Source System (SS)
        const revenueBySS = data.filter((item: any) => item.ss_cd != null && item.revenue_by_ss != null);
        const labelsSS = revenueBySS.map((item: any) => item.ss_cd);
        const valuesSS = revenueBySS.map((item: any) => item.revenue_by_ss);
        this.pieChartData = {
          labels: labelsSS,
          datasets: [{ data: valuesSS, backgroundColor: this.generateColors(labelsSS.length) }]
        };
  
        // Revenue by Payment Method
        const revenueByPM = data.filter((item: any) => item.payment_method != null && item.revenue_by_pm != null);
        const labelsPM = revenueByPM.map((item: any) => item.payment_method);
        const valuesPM = revenueByPM.map((item: any) => item.revenue_by_pm);
        this.pieChartDatasets = {
          labels: labelsPM,
          datasets: [{ data: valuesPM, backgroundColor: this.generateColors(labelsPM.length) }]
        };
  
        // Receipt Counts by Year
        const receiptYearData = data.filter((item: any) => item.rcpt_year != null);
        this.updateChartData(receiptYearData, 'rcpt_year');
  
        // Refund Status Count
        const refundStatusData = data.filter((item: any) => item.refund_status != null);
        const refundLabels = refundStatusData.map((item: any) => item.refund_status.trim());
        const refundCounts = refundStatusData.map((item: any) => item.refund_count);
        this.refundPieChartData = {
          labels: refundLabels,
          datasets: [{
            data: refundCounts,
            backgroundColor: this.generateColors(refundLabels.length)
          }]
        };
      } else {
        console.warn('Empty dashboard data');
      }
    });
  }

  // =======================================================================================
  // API Call to pull data from the database
  // Method to load data based on the selected report type
  loadReportData() {
    if (this.selectedReportType === 'year') {
      this.getRcptYear();
    } else if (this.selectedReportType === 'month' && this.selectedYear) {
      this.getRcptMonth(this.selectedYear);
    } else if (this.selectedReportType === 'day' && this.selectedYear && this.selectedMonth) {
      this.getRcptDay(this.selectedYear, this.selectedMonth);
    }
  }

  loadReportDataRevenue() {
    if (this.selectedReportTypeRevenue === 'year') {
      this.getRevenueYear();
    } else if (this.selectedReportTypeRevenue === 'month' && this.selectedYearRevenue) {
      this.getRevenueMonth(this.selectedYearRevenue);
    } else if (this.selectedReportTypeRevenue === 'day' && this.selectedYearRevenue && this.selectedMonthRevenue) {
      this.getRevenueDay(this.selectedYearRevenue, this.selectedMonthRevenue);
    }
  }

  // Revenue by year/ month day
  getRevenueYear() {
    const url = `${environment.apiUrl}/api/dashboard/v1/getrevenueyear`;
    const headers = new HttpHeaders({ Authorization: environment.authKey, 'Content-Type': 'application/json' });

    this.http.post(url, {}, { headers }).subscribe((response: any) => {
      if (response.data && response.data.length > 0) {
        console.log(response.data);
        this.updateChartRevenueData(response.data, 'receiptYear', 'revenue');
      }
      else {
        this.clearChartDataRevenue();
      }
    });
  }

  getRevenueMonth(year: number) {
    const url = `${environment.apiUrl}/api/dashboard/v1/getrevenuemonth`;
    const headers = new HttpHeaders({ Authorization: environment.authKey, 'Content-Type': 'application/json' });
    this.http.post(url, { i_year: year }, { headers }).subscribe((response: any) => {
      if (response.data && response.data.length > 0) {
        console.log(response.data);
        this.updateChartRevenueData(response.data, 'receiptMonth', 'revenue');
      }
      else {
        this.clearChartDataRevenue();
      }
    });
  }

  getRevenueDay(year: number, month: number) {
    const url = `${environment.apiUrl}/api/dashboard/v1/getrevenueday`;
    const headers = new HttpHeaders({ Authorization: environment.authKey, 'Content-Type': 'application/json' });
    this.http.post(url, { i_year: year, i_month: month }, { headers }).subscribe((response: any) => {
      if (response.data && response.data.length > 0) {
        console.log(response.data);
        this.updateChartRevenueData(response.data, 'receiptDate', 'revenue');
      }
      else {
        this.clearChartDataRevenue();
      }
    });
  }

  // =====================================================
  // Receipt by Year/ Month/ Day
  getRcptYear() {
    const url = `${environment.apiUrl}/api/dashboard/v1/getrcptyear`;
    const headers = new HttpHeaders({ Authorization: environment.authKey, 'Content-Type': 'application/json' });

    this.http.post(url, {}, { headers }).subscribe((response: any) => {
      if (response.data && response.data.length > 0) {
        // this.updateChartData(response.data, 'receiptYear', 'count_rcpt');
        this.updateChartData(response.data, 'receiptYear');
      }
    });
  }

  getRcptMonth(year: number) {
    const url = `${environment.apiUrl}/api/dashboard/v1/getrcptmonth`;
    const headers = new HttpHeaders({ Authorization: environment.authKey, 'Content-Type': 'application/json' });
    this.http.post(url, { i_year: year }, { headers }).subscribe((response: any) => {
      if (response.data && response.data.length > 0) {
        console.log(response.data);
        // this.updateChartData(response.data, 'receiptMonth', 'count_rcpt');
        this.updateChartData(response.data, 'receiptMonth');
      }
      else {
        this.clearChartData();
      }
    });
  }

  getRcptDay(year: number, month: number) {
    const url = `${environment.apiUrl}/api/dashboard/v1/getrcptday`;
    const headers = new HttpHeaders({ Authorization: environment.authKey, 'Content-Type': 'application/json' });
    this.http.post(url, { i_year: year, i_month: month }, { headers }).subscribe((response: any) => {
      if (response.data && response.data.length > 0) {
        console.log(response.data);
        // this.updateChartData(response.data, 'receiptDate', 'count_rcpt');
        this.updateChartData(response.data, 'receiptDate');
      }
      else {
        this.clearChartData();
      }
    });
  }

  // =================================================================================

  // Method to update chart data based on response, filling in missing months/days

  updateChartData(data: any[], labelKey: string) {
    let labels: any[] = [];
    let mttData: number[] = [];
    let otcData: number[] = [];
  
    if (this.selectedReportType === 'year') {
      labels = data.map(item => item[labelKey]);
      mttData = data.map(item => item['count_rcpt_mtt'] || 0);
      otcData = data.map(item => item['count_rcpt_otc'] || 0);
  
    } else if (this.selectedReportType === 'month') {
      const mttMap = new Map(data.map(item => [item[labelKey], item['count_rcpt_mtt']]));
      const otcMap = new Map(data.map(item => [item[labelKey], item['count_rcpt_otc']]));
  
      labels = this.allMonthsLabel.map(month => month.name);
      const labelData = this.allMonths;
  
      mttData = labelData.map(month => mttMap.get(month) || 0);
      otcData = labelData.map(month => otcMap.get(month) || 0);
  
    } else if (this.selectedReportType === 'day') {
      const mttMap = new Map(data.map(item => [item[labelKey], item['count_rcpt_mtt']]));
      const otcMap = new Map(data.map(item => [item[labelKey], item['count_rcpt_otc']]));
  
      labels = this.allDays;
      mttData = labels.map(day => mttMap.get(day) || 0);
      otcData = labels.map(day => otcMap.get(day) || 0);
    }
  
    this.barChartData = {
      labels: labels,
      datasets: [
        { data: mttData, label: 'MTT Receipts', backgroundColor: '#FFCE56' },
        { data: otcData, label: 'OTC Receipts', backgroundColor: '#FFEE8C' }
      ]
    };
  
    this.barChartDataBM = {
      labels: labels,
      datasets: [
        { data: mttData, label: 'Resit MTT', backgroundColor: '#FFCE56' },
        { data: otcData, label: 'Resit OTC', backgroundColor: '#FFEE8C' }
      ]
    };
  }

  updateChartRevenueData(data: any[], labelKey: string, dataKey: string) {
    let labels: any[] = [];
    let datasetData: number[] = [];

    if (this.selectedReportTypeRevenue === 'year') {
      // Year view - directly use data as is
      labels = data.map(item => item[labelKey]);
      datasetData = data.map(item => item[dataKey]);

    } else if (this.selectedReportTypeRevenue === 'month') {
      // Month view - display all months, filling missing ones with zero
      const dataMap = new Map(data.map(item => [item[labelKey], item[dataKey]]));
      labels = this.allMonthsLabel.map(month => month.name);  // Full month names as labels
      datasetData = this.allMonths.map(month => dataMap.get(month) || 0);  // Use month ID for data lookup

    } else if (this.selectedReportTypeRevenue === 'day') {
      // Day view - display all days, filling missing ones with zero
      const dataMap = new Map(data.map(item => [item[labelKey], item[dataKey]]));
      labels = this.allDays.map(day => `${day}`);
      datasetData = this.allDays.map(day => dataMap.get(day) || 0);  // Ensure all days are present
    }

    this.barChartDataRevenue = {
      labels: labels,
      datasets: [{ data: datasetData, label: 'Total Revenue', backgroundColor: "#FFCE56" }]
    };

    this.barChartDataRevenueBM = {
      labels: labels,
      datasets: [{ data: datasetData, label: 'Jumlah Hasil (Juta)', backgroundColor: "#FFCE56" }]
    };

    this.cd.detectChanges();
  }

  // Method to set empty chart data
  clearChartData() {
    this.barChartData = { labels: [], datasets: [{ data: [], label: 'Receipts', backgroundColor: "#FFCE56" }] };
    this.barChartDataBM = { labels: [], datasets: [{ data: [], label: 'Resit', backgroundColor: "#FFCE56" }] };
    this.cd.detectChanges();
  }

  // Method to set empty chart data
  clearChartDataRevenue() {
    this.barChartDataRevenue = { labels: [], datasets: [{ data: [], label: 'Revenue' }] };
    this.barChartDataRevenueBM = { labels: [], datasets: [{ data: [], label: 'Hasil' }] };
    this.cd.detectChanges();
  }

  // Event handlers for dropdown changes
  onReportTypeChange() {
    this.loadReportData();  // Load data based on the selected type
  }

  onReportTypeRevenueChange() {
    this.loadReportDataRevenue();  // Load data based on the selected type
  }


  onYearChange() {
    if (this.selectedReportType === 'month' || this.selectedReportType === 'day') {
      this.loadReportData();
    }
  }

  onYearRevenueChange() {
    if (this.selectedReportTypeRevenue === 'month' || this.selectedReportTypeRevenue === 'day') {
      this.loadReportDataRevenue();
    }
  }

  onMonthChange() {
    if (this.selectedReportType === 'day') {
      this.loadReportData();
    }
  }

  onMonthRevenueChange() {
    if (this.selectedReportTypeRevenue === 'day') {
      this.loadReportDataRevenue();
    }
  }

  getRevenueBySS() {
    const url = `${environment.apiUrl}/api/dashboard/v1/getrevenuebyss`;
    const headers = new HttpHeaders({ Authorization: environment.authKey, 'Content-Type': 'application/json' });

    this.http.post(url, {}, { headers }).subscribe(
      (response: any) => {
        if (response.data && response.data.length > 0) {
          console.log(response.data)
          const labels = response.data.map((item: any) => item.ss_cd); // Extract ss_cd for labels
          const data = response.data.map((item: any) => item.revenue); // Extract revenue for values

          this.pieChartData = {
            labels: labels,
            datasets: [{ data: data, backgroundColor: this.generateColors(labels.length) }]
          };
        } else {
          console.warn('No data found for pie chart');
        }
      },
      error => {
        console.error('Error fetching revenue data:', error);
      }
    );
  }

  getRefundCountChart() {
    const url = `${environment.apiUrl}/api/dashboard/v1/getrefundcount`;
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json'
    });

    this.http.post(url, {}, { headers }).subscribe(
      (response: any) => {
        if (response.data && response.data.length > 0) {
          console.log(response.data);
          const labels = response.data.map((item: any) => item.refund_status);
          const data = response.data.map((item: any) => item.count_refund);

          this.refundPieChartData = {
            labels: labels,
            datasets: [{
              data: data,
              backgroundColor: this.generateColors(labels.length)
            }]
          };
        } else {
          console.warn('No refund data found for pie chart');
        }

    
        
      },
      error => {
        console.error('Error fetching refund count data:', error);
      }
    );
  }

  getRevenueByPaymentMethod() {
    const url = `${environment.apiUrl}/api/dashboard/v1/getrevenuebyspaymentmethod`;
    const headers = new HttpHeaders({ Authorization: environment.authKey, 'Content-Type': 'application/json' });

    this.http.post(url, {}, { headers }).subscribe(
      (response: any) => {
        if (response.data && response.data.length > 0) {
          console.log(response.data)
          const labels = response.data.map((item: any) => item.ss_cd); // Extract ss_cd for labels
          const data = response.data.map((item: any) => item.revenue); // Extract revenue for values

          this.pieChartDatasets = {
            labels: labels,
            datasets: [{ data: data, backgroundColor: this.generateColors(labels.length) }]
          };
        } else {
          console.warn('No data found for pie chart');
        }
      },
      error => {
        console.error('Error fetching revenue data:', error);
      }
    );
  }

  generateColors(length: number) {
    // Define a fixed array of colors
    const colors = [
      '#FF6384', // Red
      '#36A2EB', // Blue
      '#FFCE56', // Yellow
      '#4BC0C0', // Green
      '#9966FF', // Purple
      '#FF9F40', // Orange
      '#000000', // Black
      '#008080', // Teal
      '#A52A2A'  // Brown
    ];

    // Return colors up to the specified length, repeating if needed
    return Array.from({ length }, (_, i) => colors[i % colors.length]);
  }
}

