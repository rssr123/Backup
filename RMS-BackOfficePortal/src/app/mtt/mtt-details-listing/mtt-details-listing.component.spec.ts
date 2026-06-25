import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MttDetailsListingComponent } from './mtt-details-listing.component';

describe('MttDetailsListingComponent', () => {
  let component: MttDetailsListingComponent;
  let fixture: ComponentFixture<MttDetailsListingComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [MttDetailsListingComponent]
    });
    fixture = TestBed.createComponent(MttDetailsListingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
