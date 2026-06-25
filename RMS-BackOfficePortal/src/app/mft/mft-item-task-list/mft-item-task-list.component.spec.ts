import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MftItemTaskListComponent } from './mft-item-task-list.component';

describe('MftItemTaskListComponent', () => {
  let component: MftItemTaskListComponent;
  let fixture: ComponentFixture<MftItemTaskListComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [MftItemTaskListComponent]
    });
    fixture = TestBed.createComponent(MftItemTaskListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
