import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MttListingComponent } from './mtt-listing.component';

describe('MttListingComponent', () => {
  let component: MttListingComponent;
  let fixture: ComponentFixture<MttListingComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [MttListingComponent]
    });
    fixture = TestBed.createComponent(MttListingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
