import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BankReconComponent } from './bank-recon-listing.component';

describe('BankUploadComponent', () => {
  let component: BankReconComponent;
  let fixture: ComponentFixture<BankReconComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [BankReconComponent]
    });
    fixture = TestBed.createComponent(BankReconComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
