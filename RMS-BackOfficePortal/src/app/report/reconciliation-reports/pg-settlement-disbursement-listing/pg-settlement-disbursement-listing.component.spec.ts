import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PgSettlementDisbursementListingComponent } from './pg-settlement-disbursement-listing.component';

describe('PgSettlementDisbursementListingComponent', () => {
  let component: PgSettlementDisbursementListingComponent;
  let fixture: ComponentFixture<PgSettlementDisbursementListingComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [PgSettlementDisbursementListingComponent]
    });
    fixture = TestBed.createComponent(PgSettlementDisbursementListingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
