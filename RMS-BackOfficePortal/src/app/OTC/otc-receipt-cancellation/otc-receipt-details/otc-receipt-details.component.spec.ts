import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OtcReceiptDetailsComponent } from './otc-receipt-details.component';

describe('OtcReceiptDetailsComponent', () => {
  let component: OtcReceiptDetailsComponent;
  let fixture: ComponentFixture<OtcReceiptDetailsComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [OtcReceiptDetailsComponent]
    });
    fixture = TestBed.createComponent(OtcReceiptDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
