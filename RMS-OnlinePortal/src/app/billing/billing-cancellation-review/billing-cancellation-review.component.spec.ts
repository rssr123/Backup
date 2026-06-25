import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BillCancellationReviewComponent } from './billing-cancellation-review.component';

describe('BillCancellationReviewComponent', () => {
  let component: BillCancellationReviewComponent;
  let fixture: ComponentFixture<BillCancellationReviewComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [BillCancellationReviewComponent]
    });
    fixture = TestBed.createComponent(BillCancellationReviewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
