import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { jsPDF } from 'jspdf';
import html2canvas from 'html2canvas';
import { environment } from 'src/environments/environment';
import { Router } from '@angular/router';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { TranslateService } from '@ngx-translate/core';
import { GlobalService } from 'src/app/shared/global.service';
import { AuthService } from 'src/app/core/services/auth.service';
import { OTCBIS } from 'src/app/core/models/otc-bal-bis';


@Component({
  selector: 'app-master-bankinslip',
  templateUrl: './master-bankinslip.component.html',
  styleUrls: ['./master-bankinslip.component.scss']
})
export class MasterBankinslipComponent implements OnInit{
  title = 'pdf-generator';
  listings: any = {};
  isLoading: boolean = false;

  modelBISInfo: OTCBIS[] = [];
  modelBISCash: OTCBIS[] = [];
  modelBISPhy: OTCBIS[] = [];
  filterByCheque: OTCBIS[] = [];
  filterByMO: OTCBIS[] = [];
  filterByBD: OTCBIS[] = [];
  totalChequeAmount: number = 0;
  totalBDAmount: number = 0;
  totalMOAmount: number = 0;
  vflag = false;

  slip_no: string = '';

  page = environment.DefaultPage;
  itemsPerPage = environment.ItemPerPage;

  branch_code: string = '';
  balDate: Date = new Date();

  constructor(
    private http: HttpClient,
    private router: Router,
    private translateService: TranslateService,
    private globalService: GlobalService,
    private authService: AuthService,
    private cdRef: ChangeDetectorRef,
  ) {
    this.translateService.setDefaultLang(this.globalService.getGlobalValue());
    this.translateService.use(this.globalService.getGlobalValue());
  }

    ngAfterViewInit() {
    this.handleZoomLevels();
    this.setupResizeListener();
  }


  private handleZoomLevels() {
    const contentElement = document.getElementById('contentToConvert');
    if (!contentElement) return;

    // Detect zoom level
    const detectZoom = () => {
      const viewport = window.visualViewport;
      const windowWidth = window.innerWidth;
      const screenWidth = screen.width;
      
      // Calculate zoom level
      let zoomLevel = 1;
      if (viewport) {
        zoomLevel = viewport.scale;
      } else {
        // Fallback method
        zoomLevel = windowWidth / document.documentElement.clientWidth;
      }

      // Apply appropriate scaling
      this.applyZoomAdjustment(contentElement, zoomLevel);
    };

    // Initial detection
    detectZoom();

    // Listen for zoom changes
    window.addEventListener('resize', detectZoom);
    if (window.visualViewport) {
      window.visualViewport.addEventListener('resize', detectZoom);
    }
  }

  private applyZoomAdjustment(element: HTMLElement, zoomLevel: number) {
    // Remove any existing zoom classes
    element.classList.remove('zoom-75', 'zoom-50', 'zoom-125', 'zoom-150');
    
    // Apply appropriate zoom class based on zoom level
    if (zoomLevel >= 1.5) {
      element.classList.add('zoom-150');
    } else if (zoomLevel >= 1.25) {
      element.classList.add('zoom-125');
    } else if (zoomLevel <= 0.75) {
      element.classList.add('zoom-75');
    } else if (zoomLevel <= 0.5) {
      element.classList.add('zoom-50');
    }

    // Alternative: Direct style manipulation for precise control
    const containerWidth = 945; // Your fixed width
    const availableWidth = window.innerWidth - 40; // Some padding
    
    if (availableWidth < containerWidth) {
      const scale = availableWidth / containerWidth;
      element.style.transform = `scale(${scale})`;
      element.style.transformOrigin = 'top left';
    } else {
      element.style.transform = 'none';
    }
  }

  private setupResizeListener() {
    let resizeTimeout: any;
    
    window.addEventListener('resize', () => {
      clearTimeout(resizeTimeout);
      resizeTimeout = setTimeout(() => {
        this.handleZoomLevels();
      }, 250);
    });
  }

  
  // Optional: Add zoom controls
  setZoomLevel(level: number) {
    const contentElement = document.getElementById('contentToConvert');
    if (!contentElement) return;

    contentElement.style.transform = `scale(${level})`;
    contentElement.style.transformOrigin = 'top left';
  }

  async ngOnInit(): Promise<void>{
    this.isLoading = true;
    const storedListings = JSON.parse(localStorage.getItem('mBankparam') || '{}');
    this.branch_code = storedListings.branch_code;
    this.balDate = storedListings.bal_date;
    if(storedListings.vflag != undefined || storedListings.vflag != null){
      this.vflag = storedListings.vflag;
    }

  
    await this.loadBISInfo(),
    await this.loadBISCash(),
    await this.loadBISPhy(),
    await new Promise(resolve => setTimeout(resolve, 2000)); 
    await this.generatePDF();

    setTimeout(() => {
      window.close(); // Only works if the window was opened by JS
    }, 7000); // Close after 5 seconds
  }

  loadBISInfo(): Promise<void> {
    return new Promise((resolve, reject) => {
      // Set your authorization header
      const headers = new HttpHeaders({
        Authorization: environment.authKey,
        'Content-Type': 'application/json',
      });
  
      this.isLoading = true;
      const url = environment.apiUrl + '/api/otcbis/v1/getotcbisinfo';
  
      const Body: any = {
        branch_code: this.branch_code,
        bal_date: this.balDate,
      };
  
      this.http.post(url, Body, { headers }).subscribe(
        (response: any) => {
          this.modelBISInfo = response.data;
  
          if (response.data.length == 0) {
            this.isLoading = false;
          } else {
            if (this.modelBISInfo[0].gtotal_cash === undefined) {
              this.modelBISInfo[0].gtotal_cash = 0;
            }
            if (this.modelBISInfo[0].gtotal_bd === undefined) {
              this.modelBISInfo[0].gtotal_bd = 0;
            }
            if (this.modelBISInfo[0].gtotal_che === undefined) {
              this.modelBISInfo[0].gtotal_che = 0;
            }
            if (this.modelBISInfo[0].gtotal_mo === undefined) {
              this.modelBISInfo[0].gtotal_mo = 0;
            }
            if(this.modelBISInfo[0].bankInSlipNo === undefined){
              this.modelBISInfo[0].bankInSlipNo = 'BIS00000000000000'
            }

            this.slip_no = this.modelBISInfo[0].bankInSlipNo;
          }
          
          resolve(); // Resolve the Promise when the operation is successful
        },
        (error) => {
          console.error(error);
          this.isLoading = false;
          reject(error); // Reject the Promise in case of an error
        }
      );
    });
  }

  loadBISCash(): Promise<void>{
    return new Promise((resolve, reject) => {
    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    this.isLoading = true;
    const url = environment.apiUrl + '/api/otcbis/v1/getotcbiscash';

    const Body: any = {
      branch_code: this.branch_code,
      bal_date: this.balDate,
    };

    this.http.post(url, Body, { headers }).subscribe(
      (response: any) => {
        this.modelBISCash = response.data;
        if (response.data.length == 0) {
          this.isLoading = false;
        }

        resolve();
      },
      (error) => {
        console.error(error);
        this.isLoading = false;
        // Handle errors here
        reject(error); // Reject the Promise in case of an error
      }
    );
    });
  }

  loadBISPhy(): Promise<void>{
    return new Promise((resolve, reject) => {
    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    this.isLoading = true;
    const url = environment.apiUrl + '/api/otcbis/v1/getotcbisphy';

    const Body: any = {
      branch_code: this.branch_code,
      bal_date: this.balDate,
    };

    this.http.post(url, Body, { headers }).subscribe(
      (response: any) => {
        this.modelBISPhy = response.data;
        if (response.data.length == 0) {
          this.isLoading = false;
        } else {
          this.filterByCheque = this.modelBISPhy.filter(item=> item.detail_type.trim() === 'cheque');
          this.filterByBD = this.modelBISPhy.filter(item=> item.detail_type.trim() === 'bank draft');
          this.filterByMO = this.modelBISPhy.filter(item=> item.detail_type.trim() === 'money order');

          this.totalChequeAmount = this.filterByCheque.reduce((sum,che) => sum + che.che_amt,0);     
          this.totalBDAmount = this.filterByBD.reduce((sum,bd) => sum + bd.bd_amt,0);  
          this.totalMOAmount = this.filterByMO.reduce((sum,mo) => sum + mo.mo_amt,0);

          this.isLoading = false;
        }

        resolve();
      },
      (error) => {
        console.error(error);
        this.isLoading = false;
        // Handle errors here
        reject(error);
      }
    );
    });
  }

  generatePDF(): Promise<void> {
  return new Promise((resolve, reject) => {
    this.isLoading = true;

    const element = document.getElementById('contentToConvert');
    const wrapper = document.getElementById('pdfWrapper'); // make sure your wrapper div has this ID

    if (!element || !wrapper) {
      this.isLoading = false;
      reject('Required elements not found');
      return;
    }

    // Save original styles
    const originalWrapperZoom = (wrapper.style as any).zoom;
    const originalDisplay = element.style.display;
    const originalTransform = element.style.transform;
    const originalZoom = (element.style as any).zoom;

    // Force neutral zoom and visibility
    (wrapper.style as any).zoom = '1';
    element.style.display = 'block';
    element.style.transform = 'none';
    (element.style as any).zoom = '1';

    setTimeout(() => {
      html2canvas(element, {
        scale: 2,
        width: 1536,
        height: element.scrollHeight,
        useCORS: true,
        logging: false
      }).then((canvas) => {
        const pdf = new jsPDF('p', 'mm', 'a4');
        const pageWidth = 210;
        const pageHeight = 297;
        const headerHeight = 30;
        const contentHeight = pageHeight - headerHeight;

        const imgWidth = pageWidth;
        const sliceHeightInPx = (contentHeight * canvas.width) / imgWidth;
        const totalPages = Math.ceil(canvas.height / sliceHeightInPx);

        for (let page = 0; page < totalPages; page++) {
          const currentSliceHeight = Math.min(
            sliceHeightInPx,
            canvas.height - page * sliceHeightInPx
          );

          const pdfCanvas = document.createElement('canvas');
          pdfCanvas.width = canvas.width;
          pdfCanvas.height = currentSliceHeight;

          const ctx = pdfCanvas.getContext('2d');
          if (ctx) {
            ctx.drawImage(
              canvas,
              0,
              page * sliceHeightInPx,
              canvas.width,
              currentSliceHeight,
              0,
              0,
              canvas.width,
              currentSliceHeight
            );

            const imgData = pdfCanvas.toDataURL('image/jpeg', 1.0);

            this.addHeader(pdf, pageWidth, headerHeight);

            pdf.addImage(
              imgData,
              'JPEG',
              0,
              headerHeight,
              imgWidth,
              (currentSliceHeight * imgWidth) / canvas.width,
              undefined,
              'FAST'
            );

            this.addFooter(pdf, page + 1, totalPages);

            if (page < totalPages - 1) {
              pdf.addPage('a4', 'p');
            }
          }
        }

        // Restore original styles
        (wrapper.style as any).zoom = originalWrapperZoom;
        element.style.display = originalDisplay;
        element.style.transform = originalTransform;
        (element.style as any).zoom = originalZoom;

        this.isLoading = false;
        pdf.save(this.slip_no + '.pdf');
        resolve();
      }).catch((error) => {
        (wrapper.style as any).zoom = originalWrapperZoom;
        element.style.display = originalDisplay;
        element.style.transform = originalTransform;
        (element.style as any).zoom = originalZoom;

        this.isLoading = false;
        reject(error);
      });
    }, 100);
  });
}

  
  /**
   * Adds a repeated header to each page
   */
  private addHeader(pdf: jsPDF, pageWidth: number, headerHeight: number): void {
    const logoWidth = 40; // Width of the logo
    const logoHeight = 25; // Height of the logo
    const backgroundColor = '#FFFF00'; // Background color for the left side
  
    // Draw the background color on the left side (for the left box area)
    pdf.setFillColor(backgroundColor);
    pdf.rect(0, 0, 15, headerHeight, 'F'); // Background color box

    // Set the logo's y position to 2 (or even 0 if you want no margin at all)
    const logoYPosition = 2;  // Minimize margin-top for the logo
  
    // Use base64 encoded image string or fetch it synchronously
    const logoBase64 = 'data:image/png;base64,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';  // Replace with your base64 logo string
  
    // Add the logo at the correct position beside the background
    pdf.addImage(logoBase64, 'PNG', logoWidth -10 , logoYPosition, logoWidth, logoHeight); // Place the logo beside the background box

    // Add text to the right of the logo
    pdf.setFont('helvetica', 'bold');
    pdf.setFontSize(20);
    pdf.setTextColor('#000');
    // Adjust text position to the right of the logo (give space)
    const textX = logoWidth + 40; 
    const textY = headerHeight / 2; // Vertically center the text
    pdf.text('BANK IN SLIP (' + this.slip_no + ')', textX, textY + 5);

    // Draw a horizontal line below the header
    pdf.setDrawColor(169, 169, 169);
    pdf.line(0, headerHeight, pageWidth, headerHeight - 1); // Draw a horizontal line under the header

    // Add footer to the current page
  
  }

  // Reusable footer function
  addFooter(doc: jsPDF, currentPage: number, totalPages: number) {
    const footerText = 'Page ' + currentPage + ' of ' + totalPages;
    doc.setFontSize(10);
    doc.text(footerText, 105, 290, { align: 'center' }); // Footer position (bottom-center)
  }



  redirectToMasterBal(){
    this.router.navigate(['/master-balancing-listing'], 
      { state: 
        { 
        flag: true
      }
    });
  }

}
