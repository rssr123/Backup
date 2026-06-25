import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RefundPTTListingComponent } from './refund-ptt-listing.component';

describe('RefundPTTListingComponent', () => {
  let component: RefundPTTListingComponent;
  let fixture: ComponentFixture<RefundPTTListingComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [RefundPTTListingComponent]
    });
    fixture = TestBed.createComponent(RefundPTTListingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
