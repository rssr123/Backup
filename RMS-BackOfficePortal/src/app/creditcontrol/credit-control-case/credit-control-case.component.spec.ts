import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreditControlCaseComponent } from './credit-control-case.component';

describe('CreditControlCaseComponent', () => {
  let component: CreditControlCaseComponent;
  let fixture: ComponentFixture<CreditControlCaseComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [CreditControlCaseComponent]
    });
    fixture = TestBed.createComponent(CreditControlCaseComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
