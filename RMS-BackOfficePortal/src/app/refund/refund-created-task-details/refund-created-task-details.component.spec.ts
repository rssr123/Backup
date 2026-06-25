import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RefundCreatedTaskDetailsComponent } from './refund-created-task-details.component';

describe('RefundCreatedTaskDetailsComponent', () => {
  let component: RefundCreatedTaskDetailsComponent;
  let fixture: ComponentFixture<RefundCreatedTaskDetailsComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [RefundCreatedTaskDetailsComponent]
    });
    fixture = TestBed.createComponent(RefundCreatedTaskDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
