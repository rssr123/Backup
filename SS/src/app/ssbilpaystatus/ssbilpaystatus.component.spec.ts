import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SsbilpaystatusComponent } from './ssbilpaystatus.component';

describe('SsbilpaystatusComponent', () => {
  let component: SsbilpaystatusComponent;
  let fixture: ComponentFixture<SsbilpaystatusComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [SsbilpaystatusComponent]
    });
    fixture = TestBed.createComponent(SsbilpaystatusComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
