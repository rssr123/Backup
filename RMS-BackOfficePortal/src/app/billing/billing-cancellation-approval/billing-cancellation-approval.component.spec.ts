import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BillCancellationApprovalComponent } from './billing-cancellation-approval.component';

describe('BillCancellationApprovalComponent', () => {
  let component: BillCancellationApprovalComponent;
  let fixture: ComponentFixture<BillCancellationApprovalComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [BillCancellationApprovalComponent]
    });
    fixture = TestBed.createComponent(BillCancellationApprovalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
