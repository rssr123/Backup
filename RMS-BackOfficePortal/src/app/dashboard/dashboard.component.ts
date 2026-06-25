import { animate, state, style, transition, trigger } from '@angular/animations';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { ChangeDetectorRef, Component } from '@angular/core';
import { ChartConfiguration } from 'chart.js';
import { environment } from 'src/environments/environment';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss'],
  animations: [
    trigger('slideIn', [
      state('void', style({ transform: 'translateX(100%)' })), // Initial position: off-screen to the right
      transition(':enter', [
        animate('1s ease-out', style({ transform: 'translateX(0)' })) // Animation to bring it to position
      ])
    ])
  ]
})

export class DashboardComponent {
  title = 'ng2-charts-demo';

  // Loading states for smooth transitions
  isDataLoading = true;
  chartsLoaded = {
    receipts: false,
    revenue: false,
    revenueBySS: false,
    revenueByPayment: false,
    refundStatus: false
  };

  public barChartLegend = true;
  public barChartPlugins = [];
  public barChartData: ChartConfiguration<'bar'>['data'] = { labels: [], datasets: [{ data: [], label: 'Receipts' }] };
  public barChartDataRevenue: ChartConfiguration<'bar'>['data'] = { labels: [], datasets: [{ data: [], label: 'Revenue' }] };


  // Common bar chart options
  public barChartOptions: ChartConfiguration<'bar'>['options'] = this.createBarChartOptions('Number of Receipts');
  public barChartOptionsRevenue: ChartConfiguration<'bar'>['options'] = this.createBarChartOptions('Total Revenue (million)');

    // Helper to create chart options with a dynamic y-axis label
  createBarChartOptions(yLabel: string): ChartConfiguration<'bar'>['options'] {
    return {
      responsive: true,
      scales: {
        x: { type: 'category', title: { display: true, text: 'Time Period', font: { weight: 'bold', size: 14 } } },
        y: { type: 'linear', title: { display: true, text: yLabel, font: { weight: 'bold', size: 14 } }, beginAtZero: true }
      },
      plugins: {
        legend: { display: true, position: 'top', labels: { font: { weight: 'bold', size: 14 } } }
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
  availableYears = [2024, 2025, 2026, 2027, 2028, 2029, 2030, 2031, 2032, 2033, 2034, 2035]; // Example years, populate as needed
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
  public pieChartDatasets = [{
    data: [3024533452.55, 1000331122.55]
  }];

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


  constructor(private http: HttpClient, private cd: ChangeDetectorRef) {
    // Start loading data immediately but show loading states
    this.initializeDashboardData();
  }

  private initializeDashboardData() {
    // Load all chart data in parallel for better performance
    Promise.all([
      this.loadReceiptData(),
      this.loadRevenueData(),
      this.loadRevenueBySSData()
    ]).then(() => {
      this.isDataLoading = false;
      this.cd.detectChanges();
    }).catch(error => {
      console.error('Error loading dashboard data:', error);
      this.isDataLoading = false;
      this.cd.detectChanges();
    });
  }

  private loadReceiptData(): Promise<void> {
    return new Promise((resolve, reject) => {
      this.getRcptYear();
      // Simulate loading time and resolve
      setTimeout(() => {
        this.chartsLoaded.receipts = true;
        resolve();
      }, 800);
    });
  }

  private loadRevenueData(): Promise<void> {
    return new Promise((resolve, reject) => {
      this.getRevenueYear();
      setTimeout(() => {
        this.chartsLoaded.revenue = true;
        resolve();
      }, 1200);
    });
  }

  private loadRevenueBySSData(): Promise<void> {
    return new Promise((resolve, reject) => {
      this.getRevenueBySS();
      setTimeout(() => {
        this.chartsLoaded.revenueBySS = true;
        this.chartsLoaded.revenueByPayment = true;
        this.chartsLoaded.refundStatus = true;
        resolve();
      }, 1000);
    });
  }

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
  // API calls
  getRcptYear() {
    const url = `${environment.apiUrl}/api/dashboard/v1/getrcptyear`;
    const headers = new HttpHeaders({ Authorization: environment.authKey, 'Content-Type': 'application/json' });

    this.http.post(url, {}, { headers }).subscribe((response: any) => {
      if (response.data && response.data.length > 0) {
        this.updateChartData(response.data, 'receiptYear', 'count_rcpt');
      }
    });
  }

  getRcptMonth(year: number) {
    const url = `${environment.apiUrl}/api/dashboard/v1/getrcptmonth`;
    const headers = new HttpHeaders({ Authorization: environment.authKey, 'Content-Type': 'application/json' });
    this.http.post(url, { i_year: year }, { headers }).subscribe((response: any) => {
      if (response.data && response.data.length > 0) {
        console.log(response.data);
        this.updateChartData(response.data, 'receiptMonth', 'count_rcpt');
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
        this.updateChartData(response.data, 'receiptDate', 'count_rcpt');
      }
      else {
        this.clearChartData();
      }
    });
  }

  // API calls
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

  // Method to update chart data based on response, filling in missing months/days
  updateChartData(data: any[], labelKey: string, dataKey: string) {
    let labels: any[] = [];
    let datasetData: number[] = [];

    if (this.selectedReportType === 'year') {
      // Year view - directly use data as is
      labels = data.map(item => item[labelKey]);
      datasetData = data.map(item => item[dataKey]);

    } else if (this.selectedReportType === 'month') {
      // Month view - fill in missing months with zero counts
      const dataMap = new Map(data.map(item => [item[labelKey], item[dataKey]]));
      labels = this.allMonthsLabel.map(month => month.name);
      const labelData = this.allMonths;
      datasetData = labelData.map(month => dataMap.get(month) || 0);
      console.log(dataMap, labels, datasetData);

    } else if (this.selectedReportType === 'day') {
      // Day view - fill in missing days with zero counts
      const dataMap = new Map(data.map(item => [item[labelKey], item[dataKey]]));
      labels = this.allDays;
      datasetData = labels.map(day => dataMap.get(day) || 0);
    }

    this.barChartData = {
      labels: labels,
      datasets: [{ data: datasetData, label: 'Receipts', backgroundColor: "#FFCE56" }]
    };

    this.cd.detectChanges();
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

    this.cd.detectChanges();
}

  // Method to set empty chart data
  clearChartData() {
    this.barChartData = { labels: [], datasets: [{ data: [], label: 'Receipts', backgroundColor: "#FFCE56"}] };
    this.cd.detectChanges();
  }

  // Method to set empty chart data
  clearChartDataRevenue() {
    this.barChartDataRevenue = { labels: [], datasets: [{ data: [], label: 'Revenue' }] };
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


