import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RoleAndPermissionsConfigurationsDetailsComponent } from './role-and-permissions-configurations-details.component';

describe('RoleAndPermissionsConfigurationsDetailsComponent', () => {
  let component: RoleAndPermissionsConfigurationsDetailsComponent;
  let fixture: ComponentFixture<RoleAndPermissionsConfigurationsDetailsComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [RoleAndPermissionsConfigurationsDetailsComponent]
    });
    fixture = TestBed.createComponent(RoleAndPermissionsConfigurationsDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
