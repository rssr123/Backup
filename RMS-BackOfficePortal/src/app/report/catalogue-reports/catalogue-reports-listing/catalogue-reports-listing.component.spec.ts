import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CatalogueReportsListingComponent } from './catalogue-reports-listing.component';

describe('CatalogueReportsListingComponent', () => {
  let component: CatalogueReportsListingComponent;
  let fixture: ComponentFixture<CatalogueReportsListingComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [CatalogueReportsListingComponent]
    });
    fixture = TestBed.createComponent(CatalogueReportsListingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
