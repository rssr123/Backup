import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RefundStatusDetailedComponent } from 'src/app/report/refund-status/refund-status.component';

describe('RefundStatusDetailedComponent', () => {
  let component: RefundStatusDetailedComponent;
  let fixture: ComponentFixture<RefundStatusDetailedComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [RefundStatusDetailedComponent]
    });
    fixture = TestBed.createComponent(RefundStatusDetailedComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
