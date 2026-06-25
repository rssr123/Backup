import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OTCCheckInComponent } from './otc-checkin.component';

describe('OTCCheckInComponent', () => {
  let component: OTCCheckInComponent;
  let fixture: ComponentFixture<OTCCheckInComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [OTCCheckInComponent]
    });
    fixture = TestBed.createComponent(OTCCheckInComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
