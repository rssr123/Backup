import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MttDetailsPgListingComponent } from './mtt-details-pg-listing.component';

describe('MttDetailsPgListingComponent', () => {
  let component: MttDetailsPgListingComponent;
  let fixture: ComponentFixture<MttDetailsPgListingComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [MttDetailsPgListingComponent]
    });
    fixture = TestBed.createComponent(MttDetailsPgListingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
