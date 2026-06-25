import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AccrualReportsListingComponent } from './accrual-reports-listing.component';

describe('AccrualReportsListingComponent', () => {
  let component: AccrualReportsListingComponent;
  let fixture: ComponentFixture<AccrualReportsListingComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [AccrualReportsListingComponent]
    });
    fixture = TestBed.createComponent(AccrualReportsListingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
