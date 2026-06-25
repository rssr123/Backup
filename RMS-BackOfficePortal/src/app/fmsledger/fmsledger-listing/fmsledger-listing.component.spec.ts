import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FmsledgerListingComponent } from './fmsledger-listing.component';

describe('FmsledgerListingComponent', () => {
  let component: FmsledgerListingComponent;
  let fixture: ComponentFixture<FmsledgerListingComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [FmsledgerListingComponent]
    });
    fixture = TestBed.createComponent(FmsledgerListingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
