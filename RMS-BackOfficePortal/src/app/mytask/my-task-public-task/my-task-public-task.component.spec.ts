import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MyTaskPublicTaskComponent } from './my-task-public-task.component';

describe('MyTaskPublicTaskComponent', () => {
  let component: MyTaskPublicTaskComponent;
  let fixture: ComponentFixture<MyTaskPublicTaskComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [MyTaskPublicTaskComponent]
    });
    fixture = TestBed.createComponent(MyTaskPublicTaskComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
