import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BillAdjustmentReviewComponent } from './billing-adjustment-review.component';

describe('BillAdjustmentReviewComponent', () => {
  let component: BillAdjustmentReviewComponent;
  let fixture: ComponentFixture<BillAdjustmentReviewComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [BillAdjustmentReviewComponent]
    });
    fixture = TestBed.createComponent(BillAdjustmentReviewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
