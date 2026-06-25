import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BillApprovalComponent } from './billing-approval.component';

describe('BillApprovalComponent', () => {
  let component: BillApprovalComponent;
  let fixture: ComponentFixture<BillApprovalComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [BillApprovalComponent]
    });
    fixture = TestBed.createComponent(BillApprovalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
