import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DailyBalancingDetailComponent } from './daily-balancing-detail.component';

describe('DailyBalancingDetailComponent', () => {
  let component: DailyBalancingDetailComponent;
  let fixture: ComponentFixture<DailyBalancingDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [DailyBalancingDetailComponent]
    });
    fixture = TestBed.createComponent(DailyBalancingDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
