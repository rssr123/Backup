import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DailyBalancingComponent } from './daily-balancing.component';

describe('DailyBalancingComponent', () => {
  let component: DailyBalancingComponent;
  let fixture: ComponentFixture<DailyBalancingComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [DailyBalancingComponent]
    });
    fixture = TestBed.createComponent(DailyBalancingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
