import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BillingTypeUpdateComponent } from './billing-type-update.component';

describe('BillingTypeUpdateComponent', () => {
  let component: BillingTypeUpdateComponent;
  let fixture: ComponentFixture<BillingTypeUpdateComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [BillingTypeUpdateComponent]
    });
    fixture = TestBed.createComponent(BillingTypeUpdateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
