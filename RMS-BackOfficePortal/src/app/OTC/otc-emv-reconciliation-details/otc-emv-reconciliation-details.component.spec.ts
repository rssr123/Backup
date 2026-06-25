import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OtcEmvReconciliationDetailsComponent } from './otc-emv-reconciliation-details.component';

describe('OtcEmvReconciliationDetailsComponent', () => {
  let component: OtcEmvReconciliationDetailsComponent;
  let fixture: ComponentFixture<OtcEmvReconciliationDetailsComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [OtcEmvReconciliationDetailsComponent]
    });
    fixture = TestBed.createComponent(OtcEmvReconciliationDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
