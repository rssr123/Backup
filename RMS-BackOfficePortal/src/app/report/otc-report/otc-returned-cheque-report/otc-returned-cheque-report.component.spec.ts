import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OtcReturnedChequeReportComponent } from './otc-returned-cheque-report.component';

describe('OtcReturnedChequeReportComponent', () => {
  let component: OtcReturnedChequeReportComponent;
  let fixture: ComponentFixture<OtcReturnedChequeReportComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [OtcReturnedChequeReportComponent]
    });
    fixture = TestBed.createComponent(OtcReturnedChequeReportComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
