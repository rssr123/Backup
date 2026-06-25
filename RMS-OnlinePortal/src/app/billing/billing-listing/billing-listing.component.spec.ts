import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BillListingComponent } from './billing-listing.component';

describe('BillListingComponent', () => {
  let component: BillListingComponent;
  let fixture: ComponentFixture<BillListingComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [BillListingComponent]
    });
    fixture = TestBed.createComponent(BillListingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
