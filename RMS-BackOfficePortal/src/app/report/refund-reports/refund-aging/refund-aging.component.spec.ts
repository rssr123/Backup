import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RefundAgingComponent } from './refund-aging.component';

describe('RefundAgingComponent', () => {
  let component: RefundAgingComponent;
  let fixture: ComponentFixture<RefundAgingComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [RefundAgingComponent]
    });
    fixture = TestBed.createComponent(RefundAgingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
