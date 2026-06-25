import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BillingClassAddComponent } from './billing-class-add.component';

describe('BillingClassAddComponent', () => {
  let component: BillingClassAddComponent;
  let fixture: ComponentFixture<BillingClassAddComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [BillingClassAddComponent]
    });
    fixture = TestBed.createComponent(BillingClassAddComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
