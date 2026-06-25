import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RefundAccountCodeDeleteComponent } from './refund-account-code-delete.component';

describe('RefundAccountCodeDeleteComponent', () => {
  let component: RefundAccountCodeDeleteComponent;
  let fixture: ComponentFixture<RefundAccountCodeDeleteComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [RefundAccountCodeDeleteComponent]
    });
    fixture = TestBed.createComponent(RefundAccountCodeDeleteComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
