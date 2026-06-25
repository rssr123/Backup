import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NonRmsReceiptingListingComponent } from './non-rms-receipting-listing.component';

describe('NonRmsReceiptingListingComponent', () => {
  let component: NonRmsReceiptingListingComponent;
  let fixture: ComponentFixture<NonRmsReceiptingListingComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [NonRmsReceiptingListingComponent]
    });
    fixture = TestBed.createComponent(NonRmsReceiptingListingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
