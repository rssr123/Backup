import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BillReviewComponent } from './billing-review.component';

describe('BillReviewComponent', () => {
  let component: BillReviewComponent;
  let fixture: ComponentFixture<BillReviewComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [BillReviewComponent]
    });
    fixture = TestBed.createComponent(BillReviewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
