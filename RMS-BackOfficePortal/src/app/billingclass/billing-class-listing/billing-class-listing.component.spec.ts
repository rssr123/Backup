import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BillingClassListingComponent } from './billing-class-listing.component';

describe('BillingClassListingComponent', () => {
  let component: BillingClassListingComponent;
  let fixture: ComponentFixture<BillingClassListingComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [BillingClassListingComponent]
    });
    fixture = TestBed.createComponent(BillingClassListingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
