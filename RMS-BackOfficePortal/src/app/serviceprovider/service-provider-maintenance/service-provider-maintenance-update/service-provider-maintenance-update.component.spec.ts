import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ServiceProviderMaintenanceUpdateComponent } from './service-provider-maintenance-update.component';

describe('ServiceProviderMaintenanceUpdateComponent', () => {
  let component: ServiceProviderMaintenanceUpdateComponent;
  let fixture: ComponentFixture<ServiceProviderMaintenanceUpdateComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ServiceProviderMaintenanceUpdateComponent]
    });
    fixture = TestBed.createComponent(ServiceProviderMaintenanceUpdateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
