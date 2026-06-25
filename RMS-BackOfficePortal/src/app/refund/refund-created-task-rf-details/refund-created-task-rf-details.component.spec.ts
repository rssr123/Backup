import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RefundCreatedTaskRfDetailsComponent } from './refund-created-task-rf-details.component';

describe('RefundCreatedTaskRfDetailsComponent', () => {
  let component: RefundCreatedTaskRfDetailsComponent;
  let fixture: ComponentFixture<RefundCreatedTaskRfDetailsComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [RefundCreatedTaskRfDetailsComponent]
    });
    fixture = TestBed.createComponent(RefundCreatedTaskRfDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
