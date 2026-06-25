import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BillingTypeAddComponent } from './billing-type-add.component';

describe('BillingTypeAddComponent', () => {
  let component: BillingTypeAddComponent;
  let fixture: ComponentFixture<BillingTypeAddComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [BillingTypeAddComponent]
    });
    fixture = TestBed.createComponent(BillingTypeAddComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
