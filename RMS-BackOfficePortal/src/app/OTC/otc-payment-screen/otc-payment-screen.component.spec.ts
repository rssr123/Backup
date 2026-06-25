import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OtcPaymentScreenComponent } from './otc-payment-screen.component';

describe('OtcPaymentScreenComponent', () => {
  let component: OtcPaymentScreenComponent;
  let fixture: ComponentFixture<OtcPaymentScreenComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [OtcPaymentScreenComponent]
    });
    fixture = TestBed.createComponent(OtcPaymentScreenComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
