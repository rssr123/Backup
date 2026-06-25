import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreditControlCaseViewerComponent } from './credit-control-case-view.component';

describe('CreditControlCaseViewerComponent', () => {
  let component: CreditControlCaseViewerComponent;
  let fixture: ComponentFixture<CreditControlCaseViewerComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [CreditControlCaseViewerComponent]
    });
    fixture = TestBed.createComponent(CreditControlCaseViewerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
