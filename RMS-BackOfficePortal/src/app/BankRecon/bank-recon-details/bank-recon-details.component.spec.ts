import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BankReconDetailsComponent } from './bank-recon-details.component';

describe('BankReconDetailsComponent', () => {
  let component: BankReconDetailsComponent;
  let fixture: ComponentFixture<BankReconDetailsComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [BankReconDetailsComponent]
    });
    fixture = TestBed.createComponent(BankReconDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
