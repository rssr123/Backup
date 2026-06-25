import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PaymentCollectionSSComponent } from './payment-collection-s-s.component';

describe('PaymentCollectionSSComponent', () => {
  let component: PaymentCollectionSSComponent;
  let fixture: ComponentFixture<PaymentCollectionSSComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [PaymentCollectionSSComponent]
    });
    fixture = TestBed.createComponent(PaymentCollectionSSComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
