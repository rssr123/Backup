import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RoleAndPermissionsConfigurationsDiscardChangesComponent } from './role-and-permissions-configurations-discard-changes.component';

describe('RoleAndPermissionsConfigurationsDiscardChangesComponent', () => {
  let component: RoleAndPermissionsConfigurationsDiscardChangesComponent;
  let fixture: ComponentFixture<RoleAndPermissionsConfigurationsDiscardChangesComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [RoleAndPermissionsConfigurationsDiscardChangesComponent]
    });
    fixture = TestBed.createComponent(RoleAndPermissionsConfigurationsDiscardChangesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
