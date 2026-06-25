import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OtcPageComponent } from './otc-page.component';

describe('OtcPageComponent', () => {
  let component: OtcPageComponent;
  let fixture: ComponentFixture<OtcPageComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [OtcPageComponent]
    });
    fixture = TestBed.createComponent(OtcPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
