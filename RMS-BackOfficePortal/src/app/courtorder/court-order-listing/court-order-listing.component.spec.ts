import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CourtOrderListingComponent } from './court-order-listing.component';

describe('CourtOrderListingComponent', () => {
  let component: CourtOrderListingComponent;
  let fixture: ComponentFixture<CourtOrderListingComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [CourtOrderListingComponent]
    });
    fixture = TestBed.createComponent(CourtOrderListingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
