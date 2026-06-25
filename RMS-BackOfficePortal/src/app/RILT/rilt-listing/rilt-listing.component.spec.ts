import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RiltListingComponent } from './rilt-listing.component';

describe('RiltListingComponent', () => {
  let component: RiltListingComponent;
  let fixture: ComponentFixture<RiltListingComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [RiltListingComponent]
    });
    fixture = TestBed.createComponent(RiltListingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
