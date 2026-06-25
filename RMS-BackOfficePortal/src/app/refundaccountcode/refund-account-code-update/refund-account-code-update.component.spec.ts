import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RefundAccountCodeUpdateComponent } from './refund-account-code-update.component';

describe('RefundAccountCodeUpdateComponent', () => {
  let component: RefundAccountCodeUpdateComponent;
  let fixture: ComponentFixture<RefundAccountCodeUpdateComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [RefundAccountCodeUpdateComponent]
    });
    fixture = TestBed.createComponent(RefundAccountCodeUpdateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
