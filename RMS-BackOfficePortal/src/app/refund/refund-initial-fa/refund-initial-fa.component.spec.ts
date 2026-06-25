import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RefundInitialFAComponent } from './refund-initial-fa.component';

describe('RefundInitialFAComponent', () => {
  let component: RefundInitialFAComponent;
  let fixture: ComponentFixture<RefundInitialFAComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [RefundInitialFAComponent]
    });
    fixture = TestBed.createComponent(RefundInitialFAComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
