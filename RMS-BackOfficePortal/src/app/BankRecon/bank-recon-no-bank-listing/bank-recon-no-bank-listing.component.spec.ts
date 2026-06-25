import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BankReconNoBankListingComponent } from './bank-recon-no-bank-listing.component';

describe('BankReconNoBankListingComponent', () => {
  let component: BankReconNoBankListingComponent;
  let fixture: ComponentFixture<BankReconNoBankListingComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [BankReconNoBankListingComponent]
    });
    fixture = TestBed.createComponent(BankReconNoBankListingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
