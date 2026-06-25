import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CollectionReportsListingComponent } from './collection-reports-listing.component';

describe('CollectionReportsListingComponent', () => {
  let component: CollectionReportsListingComponent;
  let fixture: ComponentFixture<CollectionReportsListingComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [CollectionReportsListingComponent]
    });
    fixture = TestBed.createComponent(CollectionReportsListingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
