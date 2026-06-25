import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ReceiptNoValidationComponent } from './receipt-no-validation.component';

describe('ReceiptNoValidationComponent', () => {
  let component: ReceiptNoValidationComponent;
  let fixture: ComponentFixture<ReceiptNoValidationComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ReceiptNoValidationComponent]
    });
    fixture = TestBed.createComponent(ReceiptNoValidationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
