import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ReceiptCancellationApprovalAndJustificationComponent } from './receipt-cancellation-approval-and-justification.component';

describe('ReceiptCancellationApprovalAndJustificationComponent', () => {
  let component: ReceiptCancellationApprovalAndJustificationComponent;
  let fixture: ComponentFixture<ReceiptCancellationApprovalAndJustificationComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ReceiptCancellationApprovalAndJustificationComponent]
    });
    fixture = TestBed.createComponent(ReceiptCancellationApprovalAndJustificationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
