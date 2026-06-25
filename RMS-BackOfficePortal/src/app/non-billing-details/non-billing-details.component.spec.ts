import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NonBillingDetailsComponent } from './non-billing-details.component';

describe('NonBillingDetailsComponent', () => {
  let component: NonBillingDetailsComponent;
  let fixture: ComponentFixture<NonBillingDetailsComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [NonBillingDetailsComponent]
    });
    fixture = TestBed.createComponent(NonBillingDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
