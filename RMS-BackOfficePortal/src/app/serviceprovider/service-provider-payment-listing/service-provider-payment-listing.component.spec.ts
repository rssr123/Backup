import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ServiceProviderPaymentListingComponent } from './service-provider-payment-listing.component';

describe('ServiceProviderPaymentListingComponent', () => {
  let component: ServiceProviderPaymentListingComponent;
  let fixture: ComponentFixture<ServiceProviderPaymentListingComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ServiceProviderPaymentListingComponent]
    });
    fixture = TestBed.createComponent(ServiceProviderPaymentListingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
