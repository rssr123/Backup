import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BankReconBkListingComponent } from './bank-recon-bk-listing.component';

describe('BankReconBkListingComponent', () => {
  let component: BankReconBkListingComponent;
  let fixture: ComponentFixture<BankReconBkListingComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [BankReconBkListingComponent]
    });
    fixture = TestBed.createComponent(BankReconBkListingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
