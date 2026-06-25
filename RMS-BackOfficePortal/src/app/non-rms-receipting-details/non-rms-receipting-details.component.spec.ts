import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NonRmsReceiptingDetailsComponent } from './non-rms-receipting-details.component';

describe('NonRmsReceiptingDetailsComponent', () => {
  let component: NonRmsReceiptingDetailsComponent;
  let fixture: ComponentFixture<NonRmsReceiptingDetailsComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [NonRmsReceiptingDetailsComponent]
    });
    fixture = TestBed.createComponent(NonRmsReceiptingDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
