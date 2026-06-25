import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UtListingTransactionsComponent } from './ut-listing-transactions.component';

describe('UtListingTransactionsComponent', () => {
  let component: UtListingTransactionsComponent;
  let fixture: ComponentFixture<UtListingTransactionsComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [UtListingTransactionsComponent]
    });
    fixture = TestBed.createComponent(UtListingTransactionsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
