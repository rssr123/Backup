import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MyTaskAssignedTasksComponent } from './my-task-assigned-tasks.component';

describe('MyTaskAssignedTasksComponent', () => {
  let component: MyTaskAssignedTasksComponent;
  let fixture: ComponentFixture<MyTaskAssignedTasksComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [MyTaskAssignedTasksComponent]
    });
    fixture = TestBed.createComponent(MyTaskAssignedTasksComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
