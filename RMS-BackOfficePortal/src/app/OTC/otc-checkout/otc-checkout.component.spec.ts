import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OTCCheckOutComponent } from './otc-checkout.component';
import { OTCCheckInComponent } from '../otc-checkin/otc-checkin.component';

describe('OTCCheckOutComponent', () => {
  let component: OTCCheckOutComponent;
  let fixture: ComponentFixture<OTCCheckOutComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [OTCCheckOutComponent]
    });
    fixture = TestBed.createComponent(OTCCheckOutComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
