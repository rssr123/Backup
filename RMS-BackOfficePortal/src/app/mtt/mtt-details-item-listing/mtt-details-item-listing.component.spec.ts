import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MttDetailsItemListingComponent } from './mtt-details-item-listing.component';

describe('MttDetailsItemListingComponent', () => {
  let component: MttDetailsItemListingComponent;
  let fixture: ComponentFixture<MttDetailsItemListingComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [MttDetailsItemListingComponent]
    });
    fixture = TestBed.createComponent(MttDetailsItemListingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
