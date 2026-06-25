import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreditControlCaseListingComponent } from './credit-control-case-listing.component';

describe('CreditControlCaseComponent', () => {
  let component: CreditControlCaseListingComponent;
  let fixture: ComponentFixture<CreditControlCaseListingComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [CreditControlCaseListingComponent]
    });
    fixture = TestBed.createComponent(CreditControlCaseListingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
