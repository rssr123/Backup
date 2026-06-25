import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SsbilpayComponent } from './ssbilpay.component';

describe('SsbilpayComponent', () => {
  let component: SsbilpayComponent;
  let fixture: ComponentFixture<SsbilpayComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [SsbilpayComponent]
    });
    fixture = TestBed.createComponent(SsbilpayComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
