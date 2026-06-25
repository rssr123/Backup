import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RefundRefundformComponent } from './refund-refundform.component';

describe('RefundRefundformComponent', () => {
  let component: RefundRefundformComponent;
  let fixture: ComponentFixture<RefundRefundformComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [RefundRefundformComponent]
    });
    fixture = TestBed.createComponent(RefundRefundformComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
