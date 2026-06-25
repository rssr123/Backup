import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RefundListingInfoRfComponent } from './refund-listing-info-rf.component';

describe('RefundListingInfoRfComponent', () => {
  let component: RefundListingInfoRfComponent;
  let fixture: ComponentFixture<RefundListingInfoRfComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [RefundListingInfoRfComponent]
    });
    fixture = TestBed.createComponent(RefundListingInfoRfComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
