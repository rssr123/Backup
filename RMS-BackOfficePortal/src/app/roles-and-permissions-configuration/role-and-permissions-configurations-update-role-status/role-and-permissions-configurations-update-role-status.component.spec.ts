import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RoleAndPermissionsConfigurationsUpdateRoleStatusComponent } from './role-and-permissions-configurations-update-role-status.component';

describe('RoleAndPermissionsConfigurationsUpdateRoleStatusComponent', () => {
  let component: RoleAndPermissionsConfigurationsUpdateRoleStatusComponent;
  let fixture: ComponentFixture<RoleAndPermissionsConfigurationsUpdateRoleStatusComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [RoleAndPermissionsConfigurationsUpdateRoleStatusComponent]
    });
    fixture = TestBed.createComponent(RoleAndPermissionsConfigurationsUpdateRoleStatusComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
