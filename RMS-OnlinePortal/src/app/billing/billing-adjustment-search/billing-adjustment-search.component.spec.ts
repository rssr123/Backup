import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BillingAdjustmentSearchComponent } from './billing-adjustment-search.component';

describe('BillingAdjustmentSearchComponent', () => {
  let component: BillingAdjustmentSearchComponent;
  let fixture: ComponentFixture<BillingAdjustmentSearchComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [BillingAdjustmentSearchComponent]
    });
    fixture = TestBed.createComponent(BillingAdjustmentSearchComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
