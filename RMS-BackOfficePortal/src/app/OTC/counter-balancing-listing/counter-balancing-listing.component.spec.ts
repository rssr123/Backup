import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CounterBalancingListingComponent } from './counter-balancing-listing.component';

describe('CounterBalancingListingComponent', () => {
  let component: CounterBalancingListingComponent;
  let fixture: ComponentFixture<CounterBalancingListingComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [CounterBalancingListingComponent]
    });
    fixture = TestBed.createComponent(CounterBalancingListingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
