import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DeferredIncomeAgingComponent } from './deferred-income-aging.component';

describe('DeferredIncomeAgingComponent', () => {
  let component: DeferredIncomeAgingComponent;
  let fixture: ComponentFixture<DeferredIncomeAgingComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [DeferredIncomeAgingComponent]
    });
    fixture = TestBed.createComponent(DeferredIncomeAgingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
