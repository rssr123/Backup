import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RefundChargebackComponent } from './refund-chargeback.component';

describe('RefundChargebackComponent', () => {
  let component: RefundChargebackComponent;
  let fixture: ComponentFixture<RefundChargebackComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [RefundChargebackComponent]
    });
    fixture = TestBed.createComponent(RefundChargebackComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
