import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BillingCancellationSearchComponent } from './billing-cancellation-search.component';

describe('BillingCancellationSearchComponent', () => {
  let component: BillingCancellationSearchComponent;
  let fixture: ComponentFixture<BillingCancellationSearchComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [BillingCancellationSearchComponent]
    });
    fixture = TestBed.createComponent(BillingCancellationSearchComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
