import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BillAdjustmentApprovalComponent } from './billing-adjustment-approval.component';

describe('BillAdjustmentApprovalComponent', () => {
  let component: BillAdjustmentApprovalComponent;
  let fixture: ComponentFixture<BillAdjustmentApprovalComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [BillAdjustmentApprovalComponent]
    });
    fixture = TestBed.createComponent(BillAdjustmentApprovalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
