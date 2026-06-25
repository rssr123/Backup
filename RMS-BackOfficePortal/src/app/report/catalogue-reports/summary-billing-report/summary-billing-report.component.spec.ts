import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SummaryBillingReportComponent } from './summary-billing-report.component';

describe('SummaryBillingReportComponent', () => {
  let component: SummaryBillingReportComponent;
  let fixture: ComponentFixture<SummaryBillingReportComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [SummaryBillingReportComponent]
    });
    fixture = TestBed.createComponent(SummaryBillingReportComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
