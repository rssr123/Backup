import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AgBankTxnListingComponent } from './ag-bank-txn-listing.component';

describe('AgBankTxnListingComponent', () => {
  let component: AgBankTxnListingComponent;
  let fixture: ComponentFixture<AgBankTxnListingComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [AgBankTxnListingComponent]
    });
    fixture = TestBed.createComponent(AgBankTxnListingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
