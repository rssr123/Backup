import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BillRegistrationComponent } from './billing-registration.component';

describe('BillRegistrationComponent', () => {
  let component: BillRegistrationComponent;
  let fixture: ComponentFixture<BillRegistrationComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [BillRegistrationComponent]
    });
    fixture = TestBed.createComponent(BillRegistrationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
