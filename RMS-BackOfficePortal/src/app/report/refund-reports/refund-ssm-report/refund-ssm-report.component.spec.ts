import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RefundSsmReportComponent } from './refund-ssm-report.component';

describe('RefundSsmReportComponent', () => {
  let component: RefundSsmReportComponent;
  let fixture: ComponentFixture<RefundSsmReportComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [RefundSsmReportComponent]
    });
    fixture = TestBed.createComponent(RefundSsmReportComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
