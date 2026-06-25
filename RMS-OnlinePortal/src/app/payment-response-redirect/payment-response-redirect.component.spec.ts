import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PaymentResponseRedirectComponent } from './payment-response-redirect.component';

describe('PaymentResponseRedirectComponent', () => {
  let component: PaymentResponseRedirectComponent;
  let fixture: ComponentFixture<PaymentResponseRedirectComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [PaymentResponseRedirectComponent]
    });
    fixture = TestBed.createComponent(PaymentResponseRedirectComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
  
});
