import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RefundChargebackInfoComponent } from './refund-chargeback-info.component';

describe('RefundChargebackInfoComponent', () => {
  let component: RefundChargebackInfoComponent;
  let fixture: ComponentFixture<RefundChargebackInfoComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [RefundChargebackInfoComponent]
    });
    fixture = TestBed.createComponent(RefundChargebackInfoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
