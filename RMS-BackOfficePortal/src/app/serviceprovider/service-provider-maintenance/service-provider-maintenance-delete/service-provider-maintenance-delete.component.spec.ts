import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ServiceProviderMaintenanceDeleteComponent } from './service-provider-maintenance-delete.component';

describe('ServiceProviderMaintenanceDeleteComponent', () => {
  let component: ServiceProviderMaintenanceDeleteComponent;
  let fixture: ComponentFixture<ServiceProviderMaintenanceDeleteComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ServiceProviderMaintenanceDeleteComponent]
    });
    fixture = TestBed.createComponent(ServiceProviderMaintenanceDeleteComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
