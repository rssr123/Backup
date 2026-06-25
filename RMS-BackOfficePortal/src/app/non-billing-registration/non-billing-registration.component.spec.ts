import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NonBillingRegistrationComponent } from './non-billing-registration.component';

describe('NonBillingRegistrationComponent', () => {
  let component: NonBillingRegistrationComponent;
  let fixture: ComponentFixture<NonBillingRegistrationComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [NonBillingRegistrationComponent]
    });
    fixture = TestBed.createComponent(NonBillingRegistrationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
