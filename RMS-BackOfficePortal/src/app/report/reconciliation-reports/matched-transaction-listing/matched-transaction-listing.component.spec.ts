import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MatchedTransactionListingComponent } from './matched-transaction-listing.component';

describe('MatchedTransactionListingComponent', () => {
  let component: MatchedTransactionListingComponent;
  let fixture: ComponentFixture<MatchedTransactionListingComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [MatchedTransactionListingComponent]
    });
    fixture = TestBed.createComponent(MatchedTransactionListingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
