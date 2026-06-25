import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BranchCodeCounterListingComponent } from './branch-code-counter-listing.component';

describe('BranchCodeCounterListingComponent', () => {
  let component: BranchCodeCounterListingComponent;
  let fixture: ComponentFixture<BranchCodeCounterListingComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [BranchCodeCounterListingComponent]
    });
    fixture = TestBed.createComponent(BranchCodeCounterListingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
