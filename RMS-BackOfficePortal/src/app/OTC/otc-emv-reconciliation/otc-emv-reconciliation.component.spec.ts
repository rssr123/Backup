import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OtcEmvReconciliationComponent } from './otc-emv-reconciliation.component';

describe('OtcEmvReconciliationComponent', () => {
  let component: OtcEmvReconciliationComponent;
  let fixture: ComponentFixture<OtcEmvReconciliationComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [OtcEmvReconciliationComponent]
    });
    fixture = TestBed.createComponent(OtcEmvReconciliationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
