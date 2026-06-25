import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RefundAccountCodeAddComponent } from './refund-account-code-add.component';

describe('RefundAccountCodeAddComponent', () => {
  let component: RefundAccountCodeAddComponent;
  let fixture: ComponentFixture<RefundAccountCodeAddComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [RefundAccountCodeAddComponent]
    });
    fixture = TestBed.createComponent(RefundAccountCodeAddComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
