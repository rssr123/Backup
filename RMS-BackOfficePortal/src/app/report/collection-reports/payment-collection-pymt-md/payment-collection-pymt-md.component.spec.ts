import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PaymentCollectionPymtMdComponent } from './payment-collection-pymt-md.component';

describe('PaymentCollectionPymtMdComponent', () => {
  let component: PaymentCollectionPymtMdComponent;
  let fixture: ComponentFixture<PaymentCollectionPymtMdComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [PaymentCollectionPymtMdComponent]
    });
    fixture = TestBed.createComponent(PaymentCollectionPymtMdComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
