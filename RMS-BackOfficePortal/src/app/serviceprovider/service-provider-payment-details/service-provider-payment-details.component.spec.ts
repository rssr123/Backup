import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ServiceProviderPaymentDetailsComponent } from './service-provider-payment-details.component';

describe('ServiceProviderPaymentDetailsComponent', () => {
  let component: ServiceProviderPaymentDetailsComponent;
  let fixture: ComponentFixture<ServiceProviderPaymentDetailsComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ServiceProviderPaymentDetailsComponent]
    });
    fixture = TestBed.createComponent(ServiceProviderPaymentDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
