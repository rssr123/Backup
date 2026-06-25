import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BankReconPgListingComponent } from './bank-recon-pg-listing.component';

describe('BankReconPgListingComponent', () => {
  let component: BankReconPgListingComponent;
  let fixture: ComponentFixture<BankReconPgListingComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [BankReconPgListingComponent]
    });
    fixture = TestBed.createComponent(BankReconPgListingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
