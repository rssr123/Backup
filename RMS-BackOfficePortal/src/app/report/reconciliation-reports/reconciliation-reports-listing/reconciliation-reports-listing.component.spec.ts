import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ReconciliationReportsListingComponent } from './reconciliation-reports-listing.component';

describe('ReconciliationReportsListingComponent', () => {
  let component: ReconciliationReportsListingComponent;
  let fixture: ComponentFixture<ReconciliationReportsListingComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ReconciliationReportsListingComponent]
    });
    fixture = TestBed.createComponent(ReconciliationReportsListingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
