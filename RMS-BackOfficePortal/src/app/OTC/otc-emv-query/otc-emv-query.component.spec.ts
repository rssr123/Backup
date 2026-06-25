import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OtcEmvQueryComponent } from './otc-emv-query.component';

describe('OtcEmvQueryComponent', () => {
  let component: OtcEmvQueryComponent;
  let fixture: ComponentFixture<OtcEmvQueryComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [OtcEmvQueryComponent]
    });
    fixture = TestBed.createComponent(OtcEmvQueryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
