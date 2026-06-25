import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BillingAdjustmentComponent } from './billing-adjustment.component';

describe('BillingAdjustmentComponent', () => {
  let component: BillingAdjustmentComponent;
  let fixture: ComponentFixture<BillingAdjustmentComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [BillingAdjustmentComponent]
    });
    fixture = TestBed.createComponent(BillingAdjustmentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
