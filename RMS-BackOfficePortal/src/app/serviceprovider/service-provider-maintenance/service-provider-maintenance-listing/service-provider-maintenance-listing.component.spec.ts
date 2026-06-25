import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ServiceProviderMaintenanceListingComponent } from './service-provider-maintenance-listing.component';

describe('ServiceProviderMaintenanceListingComponent', () => {
  let component: ServiceProviderMaintenanceListingComponent;
  let fixture: ComponentFixture<ServiceProviderMaintenanceListingComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ServiceProviderMaintenanceListingComponent]
    });
    fixture = TestBed.createComponent(ServiceProviderMaintenanceListingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
