import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreditControlSmeTaskListComponent } from './credit-control-sme-task-list.component';

describe('CreditControlSmeTaskListComponent', () => {
  let component: CreditControlSmeTaskListComponent;
  let fixture: ComponentFixture<CreditControlSmeTaskListComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [CreditControlSmeTaskListComponent]
    });
    fixture = TestBed.createComponent(CreditControlSmeTaskListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
