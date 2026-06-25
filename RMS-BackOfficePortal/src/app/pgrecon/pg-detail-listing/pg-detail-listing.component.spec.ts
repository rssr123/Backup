import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PgDetailListingComponent } from './pg-detail-listing.component';

describe('PgDetailListingComponent', () => {
  let component: PgDetailListingComponent;
  let fixture: ComponentFixture<PgDetailListingComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [PgDetailListingComponent]
    });
    fixture = TestBed.createComponent(PgDetailListingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
