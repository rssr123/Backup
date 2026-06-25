import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DetailedBillingBtReportComponent } from './detailed-billing-bt-report.component';

describe('DetailedBillingBtReportComponent', () => {
  let component: DetailedBillingBtReportComponent;
  let fixture: ComponentFixture<DetailedBillingBtReportComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [DetailedBillingBtReportComponent]
    });
    fixture = TestBed.createComponent(DetailedBillingBtReportComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
