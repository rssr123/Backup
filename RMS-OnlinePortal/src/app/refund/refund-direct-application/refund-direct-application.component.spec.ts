import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RefundDirectApplicationComponent } from './refund-direct-application.component';

describe('RefundDirectApplicationComponent', () => {
  let component: RefundDirectApplicationComponent;
  let fixture: ComponentFixture<RefundDirectApplicationComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [RefundDirectApplicationComponent]
    });
    fixture = TestBed.createComponent(RefundDirectApplicationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
