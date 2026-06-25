import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RefundSubmitBankinfoComponent } from './refund-submit-bankinfo.component';

describe('RefundSubmitBankinfoComponent', () => {
  let component: RefundSubmitBankinfoComponent;
  let fixture: ComponentFixture<RefundSubmitBankinfoComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [RefundSubmitBankinfoComponent]
    });
    fixture = TestBed.createComponent(RefundSubmitBankinfoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
