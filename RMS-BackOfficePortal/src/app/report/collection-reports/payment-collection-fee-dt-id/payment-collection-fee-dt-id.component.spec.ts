import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PaymentCollectionFeeDtIdComponent } from './payment-collection-fee-dt-id.component';

describe('PaymentCollectionFeeDtIdComponent', () => {
  let component: PaymentCollectionFeeDtIdComponent;
  let fixture: ComponentFixture<PaymentCollectionFeeDtIdComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [PaymentCollectionFeeDtIdComponent]
    });
    fixture = TestBed.createComponent(PaymentCollectionFeeDtIdComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
