import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RefundApprovalFaComponent } from './refund-approval-fa.component';

describe('RefundApprovalFaComponent', () => {
  let component: RefundApprovalFaComponent;
  let fixture: ComponentFixture<RefundApprovalFaComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [RefundApprovalFaComponent]
    });
    fixture = TestBed.createComponent(RefundApprovalFaComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
