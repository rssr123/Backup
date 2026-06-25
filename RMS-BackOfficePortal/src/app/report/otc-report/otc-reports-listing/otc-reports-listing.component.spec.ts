import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OtcReportsListingComponent } from './otc-reports-listing.component';

describe('OtcReportsListingComponent', () => {
  let component: OtcReportsListingComponent;
  let fixture: ComponentFixture<OtcReportsListingComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [OtcReportsListingComponent]
    });
    fixture = TestBed.createComponent(OtcReportsListingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
