import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RefundRequestSelectSMEComponent } from './refund-request-select-sme.component';

describe('RefundRequestSelectSMEComponent', () => {
  let component: RefundRequestSelectSMEComponent;
  let fixture: ComponentFixture<RefundRequestSelectSMEComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [RefundRequestSelectSMEComponent]
    });
    fixture = TestBed.createComponent(RefundRequestSelectSMEComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
