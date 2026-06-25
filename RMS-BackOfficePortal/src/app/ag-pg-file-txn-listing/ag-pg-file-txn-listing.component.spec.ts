import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AgPgFileTxnListingComponent } from './ag-pg-file-txn-listing.component';

describe('AgPgFileTxnListingComponent', () => {
  let component: AgPgFileTxnListingComponent;
  let fixture: ComponentFixture<AgPgFileTxnListingComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [AgPgFileTxnListingComponent]
    });
    fixture = TestBed.createComponent(AgPgFileTxnListingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
