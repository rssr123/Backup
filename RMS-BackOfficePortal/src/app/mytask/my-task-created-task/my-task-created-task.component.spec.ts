import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MyTaskCreatedTaskComponent } from './my-task-created-task.component';

describe('MyTaskCreatedTaskComponent', () => {
  let component: MyTaskCreatedTaskComponent;
  let fixture: ComponentFixture<MyTaskCreatedTaskComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [MyTaskCreatedTaskComponent]
    });
    fixture = TestBed.createComponent(MyTaskCreatedTaskComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
