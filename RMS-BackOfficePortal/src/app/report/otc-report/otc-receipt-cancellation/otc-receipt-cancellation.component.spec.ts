import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OtcReceiptCancellationComponent } from './otc-receipt-cancellation.component';

describe('OtcReceiptCancellationComponent', () => {
  let component: OtcReceiptCancellationComponent;
  let fixture: ComponentFixture<OtcReceiptCancellationComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [OtcReceiptCancellationComponent]
    });
    fixture = TestBed.createComponent(OtcReceiptCancellationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
