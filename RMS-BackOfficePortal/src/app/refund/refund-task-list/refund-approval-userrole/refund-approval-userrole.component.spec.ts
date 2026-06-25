import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RefundApprovalUserroleComponent } from './refund-approval-userrole.component';

describe('RefundApprovalUserroleComponent', () => {
  let component: RefundApprovalUserroleComponent;
  let fixture: ComponentFixture<RefundApprovalUserroleComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [RefundApprovalUserroleComponent]
    });
    fixture = TestBed.createComponent(RefundApprovalUserroleComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
