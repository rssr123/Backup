import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RoleAndPermissionsConfigurationsAddRolesComponent } from './role-and-permissions-configurations-add-roles.component';

describe('RoleAndPermissionsConfigurationsAddRolesComponent', () => {
  let component: RoleAndPermissionsConfigurationsAddRolesComponent;
  let fixture: ComponentFixture<RoleAndPermissionsConfigurationsAddRolesComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [RoleAndPermissionsConfigurationsAddRolesComponent]
    });
    fixture = TestBed.createComponent(RoleAndPermissionsConfigurationsAddRolesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
