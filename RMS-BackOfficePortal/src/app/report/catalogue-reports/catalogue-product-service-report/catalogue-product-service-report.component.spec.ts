import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CatalogueProductServiceReportComponent } from './catalogue-product-service-report.component';

describe('CatalogueProductServiceReportComponent', () => {
  let component: CatalogueProductServiceReportComponent;
  let fixture: ComponentFixture<CatalogueProductServiceReportComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [CatalogueProductServiceReportComponent]
    });
    fixture = TestBed.createComponent(CatalogueProductServiceReportComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
