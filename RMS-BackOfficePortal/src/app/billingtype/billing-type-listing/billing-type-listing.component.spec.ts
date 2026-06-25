import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BillingTypeListingComponent } from './billing-type-listing.component';

describe('BillingTypeListingComponent', () => {
  let component: BillingTypeListingComponent;
  let fixture: ComponentFixture<BillingTypeListingComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [BillingTypeListingComponent]
    });
    fixture = TestBed.createComponent(BillingTypeListingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
