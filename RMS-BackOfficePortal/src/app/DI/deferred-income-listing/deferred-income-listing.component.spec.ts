import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DeferredIncomeListingComponent } from './deferred-income-listing.component';

describe('DeferredIncomeListingComponent', () => {
  let component: DeferredIncomeListingComponent;
  let fixture: ComponentFixture<DeferredIncomeListingComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [DeferredIncomeListingComponent]
    });
    fixture = TestBed.createComponent(DeferredIncomeListingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
