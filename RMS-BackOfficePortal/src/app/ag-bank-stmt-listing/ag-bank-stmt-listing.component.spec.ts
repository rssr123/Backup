import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AgBankStmtListingComponent } from './ag-bank-stmt-listing.component';

describe('AgBankStmtListingComponent', () => {
  let component: AgBankStmtListingComponent;
  let fixture: ComponentFixture<AgBankStmtListingComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [AgBankStmtListingComponent]
    });
    fixture = TestBed.createComponent(AgBankStmtListingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
