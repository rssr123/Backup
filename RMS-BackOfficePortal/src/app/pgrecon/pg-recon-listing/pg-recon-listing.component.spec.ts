import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PgReconListingComponent } from './pg-recon-listing.component';

describe('PgReconListingComponent', () => {
  let component: PgReconListingComponent;
  let fixture: ComponentFixture<PgReconListingComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [PgReconListingComponent]
    });
    fixture = TestBed.createComponent(PgReconListingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
