import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DailyBalancingWarningComponent } from './daily-balancing-warning.component';

describe('DailyBalancingWarningComponent', () => {
  let component: DailyBalancingWarningComponent;
  let fixture: ComponentFixture<DailyBalancingWarningComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [DailyBalancingWarningComponent]
    });
    fixture = TestBed.createComponent(DailyBalancingWarningComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
