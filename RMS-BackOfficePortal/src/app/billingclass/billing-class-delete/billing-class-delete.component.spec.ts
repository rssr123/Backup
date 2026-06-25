import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BillingClassDeleteComponent } from './billing-class-delete.component';

describe('BillingClassDeleteComponent', () => {
  let component: BillingClassDeleteComponent;
  let fixture: ComponentFixture<BillingClassDeleteComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [BillingClassDeleteComponent]
    });
    fixture = TestBed.createComponent(BillingClassDeleteComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
