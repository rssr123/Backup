import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UtListingDaysComponent } from './ut-listing-days.component';

describe('UtListingDaysComponent', () => {
  let component: UtListingDaysComponent;
  let fixture: ComponentFixture<UtListingDaysComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [UtListingDaysComponent]
    });
    fixture = TestBed.createComponent(UtListingDaysComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
