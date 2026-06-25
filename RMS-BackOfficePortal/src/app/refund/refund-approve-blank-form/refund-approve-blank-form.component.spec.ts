import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RefundApproveBlankFormComponent } from './refund-approve-blank-form.component';

describe('RefundApproveBlankFormComponent', () => {
  let component: RefundApproveBlankFormComponent;
  let fixture: ComponentFixture<RefundApproveBlankFormComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [RefundApproveBlankFormComponent]
    });
    fixture = TestBed.createComponent(RefundApproveBlankFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
