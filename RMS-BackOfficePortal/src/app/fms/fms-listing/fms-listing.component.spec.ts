import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FmsListingComponent } from './fms-listing.component';

describe('FmsListingComponent', () => {
  let component: FmsListingComponent;
  let fixture: ComponentFixture<FmsListingComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [FmsListingComponent]
    });
    fixture = TestBed.createComponent(FmsListingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
