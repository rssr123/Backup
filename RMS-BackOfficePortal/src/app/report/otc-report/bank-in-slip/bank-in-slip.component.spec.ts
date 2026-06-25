import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BankInSlipComponent } from './bank-in-slip.component';

describe('BankInSlipComponent', () => {
  let component: BankInSlipComponent;
  let fixture: ComponentFixture<BankInSlipComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [BankInSlipComponent]
    });
    fixture = TestBed.createComponent(BankInSlipComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
