import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UtListingMonthsComponent } from './ut-listing-months.component';

describe('UtListingMonthsComponent', () => {
  let component: UtListingMonthsComponent;
  let fixture: ComponentFixture<UtListingMonthsComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [UtListingMonthsComponent]
    });
    fixture = TestBed.createComponent(UtListingMonthsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
