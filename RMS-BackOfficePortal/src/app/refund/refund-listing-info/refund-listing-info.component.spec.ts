import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RefundListingInfoComponent } from './refund-listing-info.component';

describe('RefundListingInfoComponent', () => {
  let component: RefundListingInfoComponent;
  let fixture: ComponentFixture<RefundListingInfoComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [RefundListingInfoComponent]
    });
    fixture = TestBed.createComponent(RefundListingInfoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
