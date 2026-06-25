import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BankReconPgFileTxnListingComponent } from './bank-recon-pg-file-txn-listing.component';

describe('BankReconPgFileTxnListingComponent', () => {
  let component: BankReconPgFileTxnListingComponent;
  let fixture: ComponentFixture<BankReconPgFileTxnListingComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [BankReconPgFileTxnListingComponent]
    });
    fixture = TestBed.createComponent(BankReconPgFileTxnListingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
