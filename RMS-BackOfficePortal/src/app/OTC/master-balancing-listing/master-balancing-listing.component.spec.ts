import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MasterBalancingListingComponent } from './master-balancing-listing.component';

describe('MasterBalancingListingComponent', () => {
  let component: MasterBalancingListingComponent;
  let fixture: ComponentFixture<MasterBalancingListingComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [MasterBalancingListingComponent]
    });
    fixture = TestBed.createComponent(MasterBalancingListingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
