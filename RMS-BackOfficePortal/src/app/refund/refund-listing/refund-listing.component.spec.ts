import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RefundListingComponent } from './refund-listing.component';

describe('RefundListingComponent', () => {
  let component: RefundListingComponent;
  let fixture: ComponentFixture<RefundListingComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [RefundListingComponent]
    });
    fixture = TestBed.createComponent(RefundListingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
