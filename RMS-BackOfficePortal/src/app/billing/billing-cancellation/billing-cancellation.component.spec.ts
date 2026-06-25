import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BillingCancellationComponent } from './billing-cancellation.component';

describe('BillingCancellationComponent', () => {
  let component: BillingCancellationComponent;
  let fixture: ComponentFixture<BillingCancellationComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [BillingCancellationComponent]
    });
    fixture = TestBed.createComponent(BillingCancellationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
