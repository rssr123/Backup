import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BillingClassUpdateComponent } from './billing-class-update.component';

describe('BillingClassUpdateComponent', () => {
  let component: BillingClassUpdateComponent;
  let fixture: ComponentFixture<BillingClassUpdateComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [BillingClassUpdateComponent]
    });
    fixture = TestBed.createComponent(BillingClassUpdateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
