import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RmsDetailListingComponent } from './rms-detail-listing.component';

describe('RmsDetailListingComponent', () => {
  let component: RmsDetailListingComponent;
  let fixture: ComponentFixture<RmsDetailListingComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [RmsDetailListingComponent]
    });
    fixture = TestBed.createComponent(RmsDetailListingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
