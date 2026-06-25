import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RicpListingComponent } from './ricp-listing.component';

describe('RicpListingComponent', () => {
  let component: RicpListingComponent;
  let fixture: ComponentFixture<RicpListingComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [RicpListingComponent]
    });
    fixture = TestBed.createComponent(RicpListingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
