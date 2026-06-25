import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OtcReceiptScreenComponent } from './otc-receipt-screen.component';

describe('OtcReceiptScreenComponent', () => {
  let component: OtcReceiptScreenComponent;
  let fixture: ComponentFixture<OtcReceiptScreenComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [OtcReceiptScreenComponent]
    });
    fixture = TestBed.createComponent(OtcReceiptScreenComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
