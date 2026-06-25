import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RefundReportsListingComponent } from './refund-reports-listing.component';

describe('RefundReportsListingComponent', () => {
  let component: RefundReportsListingComponent;
  let fixture: ComponentFixture<RefundReportsListingComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [RefundReportsListingComponent]
    });
    fixture = TestBed.createComponent(RefundReportsListingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
