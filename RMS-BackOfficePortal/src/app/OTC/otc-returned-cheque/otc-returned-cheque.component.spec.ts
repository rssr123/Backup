import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OtcReturnedChequeComponent } from './otc-returned-cheque.component';

describe('OtcReturnedChequeComponent', () => {
  let component: OtcReturnedChequeComponent;
  let fixture: ComponentFixture<OtcReturnedChequeComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [OtcReturnedChequeComponent]
    });
    fixture = TestBed.createComponent(OtcReturnedChequeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
