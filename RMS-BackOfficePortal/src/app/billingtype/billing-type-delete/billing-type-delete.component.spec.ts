import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BillingTypeDeleteComponent } from './billing-type-delete.component';

describe('BillingTypeDeleteComponent', () => {
  let component: BillingTypeDeleteComponent;
  let fixture: ComponentFixture<BillingTypeDeleteComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [BillingTypeDeleteComponent]
    });
    fixture = TestBed.createComponent(BillingTypeDeleteComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
