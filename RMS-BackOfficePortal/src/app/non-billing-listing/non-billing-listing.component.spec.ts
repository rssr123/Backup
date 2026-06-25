import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NonBillingListingComponent } from './non-billing-listing.component';

describe('NonBillingListingComponent', () => {
  let component: NonBillingListingComponent;
  let fixture: ComponentFixture<NonBillingListingComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [NonBillingListingComponent]
    });
    fixture = TestBed.createComponent(NonBillingListingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
