import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ServiceProviderMaintenanceAddComponent } from './service-provider-maintenance-add.component';

describe('ServiceProviderMaintenanceAddComponent', () => {
  let component: ServiceProviderMaintenanceAddComponent;
  let fixture: ComponentFixture<ServiceProviderMaintenanceAddComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ServiceProviderMaintenanceAddComponent]
    });
    fixture = TestBed.createComponent(ServiceProviderMaintenanceAddComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
