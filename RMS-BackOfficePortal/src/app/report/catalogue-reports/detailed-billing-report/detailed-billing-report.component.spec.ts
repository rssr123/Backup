import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DetailedBillingReportComponent } from './detailed-billing-report.component';

describe('DetailedBillingReportComponent', () => {
  let component: DetailedBillingReportComponent;
  let fixture: ComponentFixture<DetailedBillingReportComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [DetailedBillingReportComponent]
    });
    fixture = TestBed.createComponent(DetailedBillingReportComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
