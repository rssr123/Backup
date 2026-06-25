import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DailyBalancingListingComponent } from './daily-balancing-listing.component';

describe('DailyBalancingListingComponent', () => {
  let component: DailyBalancingListingComponent;
  let fixture: ComponentFixture<DailyBalancingListingComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [DailyBalancingListingComponent]
    });
    fixture = TestBed.createComponent(DailyBalancingListingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
