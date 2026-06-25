import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreatedTaskDetailsComponent } from './created-task-details.component';

describe('CreatedTaskDetailsComponent', () => {
  let component: CreatedTaskDetailsComponent;
  let fixture: ComponentFixture<CreatedTaskDetailsComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [CreatedTaskDetailsComponent]
    });
    fixture = TestBed.createComponent(CreatedTaskDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
