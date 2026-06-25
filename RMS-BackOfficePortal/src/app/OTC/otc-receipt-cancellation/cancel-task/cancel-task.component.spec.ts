import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CancelTaskComponent } from './cancel-task.component';

describe('CancelTaskComponent', () => {
  let component: CancelTaskComponent;
  let fixture: ComponentFixture<CancelTaskComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [CancelTaskComponent]
    });
    fixture = TestBed.createComponent(CancelTaskComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
