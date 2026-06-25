import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DeferredIncomeDetailsComponent } from './deferred-income-details.component';

describe('DeferredIncomeDetailsComponent', () => {
  let component: DeferredIncomeDetailsComponent;
  let fixture: ComponentFixture<DeferredIncomeDetailsComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [DeferredIncomeDetailsComponent]
    });
    fixture = TestBed.createComponent(DeferredIncomeDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
