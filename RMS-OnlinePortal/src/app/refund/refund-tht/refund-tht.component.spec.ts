import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RefundTHTComponent } from './refund-tht.component';

describe('RefundTHTComponent', () => {
  let component: RefundTHTComponent;
  let fixture: ComponentFixture<RefundTHTComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [RefundTHTComponent]
    });
    fixture = TestBed.createComponent(RefundTHTComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
