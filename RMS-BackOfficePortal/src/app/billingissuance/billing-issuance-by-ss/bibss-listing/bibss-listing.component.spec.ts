import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BibssListingComponent } from './bibss-listing.component';

describe('BibssListingComponent', () => {
  let component: BibssListingComponent;
  let fixture: ComponentFixture<BibssListingComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [BibssListingComponent]
    });
    fixture = TestBed.createComponent(BibssListingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
