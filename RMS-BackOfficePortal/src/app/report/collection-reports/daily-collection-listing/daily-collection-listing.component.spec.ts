import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DailyCollectionListingComponent } from './daily-collection-listing.component';

describe('DailyCollectionListingComponent', () => {
  let component: DailyCollectionListingComponent;
  let fixture: ComponentFixture<DailyCollectionListingComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [DailyCollectionListingComponent]
    });
    fixture = TestBed.createComponent(DailyCollectionListingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
