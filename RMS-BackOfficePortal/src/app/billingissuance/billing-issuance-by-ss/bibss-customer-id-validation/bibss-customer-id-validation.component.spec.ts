import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BibssCustomerIdValidationComponent } from './bibss-customer-id-validation.component';

describe('BibssCustomerIdValidationComponent', () => {
  let component: BibssCustomerIdValidationComponent;
  let fixture: ComponentFixture<BibssCustomerIdValidationComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [BibssCustomerIdValidationComponent]
    });
    fixture = TestBed.createComponent(BibssCustomerIdValidationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
