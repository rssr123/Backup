import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MftCancelTaskComponent } from './mft-cancel-task.component';

describe('MftCancelTaskComponent', () => {
  let component: MftCancelTaskComponent;
  let fixture: ComponentFixture<MftCancelTaskComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [MftCancelTaskComponent]
    });
    fixture = TestBed.createComponent(MftCancelTaskComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
