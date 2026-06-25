import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BillingCancellationListingComponent } from './billing-cancellation-listing.component';

describe('BillingCancellationListingComponent', () => {
  let component: BillingCancellationListingComponent;
  let fixture: ComponentFixture<BillingCancellationListingComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [BillingCancellationListingComponent]
    });
    fixture = TestBed.createComponent(BillingCancellationListingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
