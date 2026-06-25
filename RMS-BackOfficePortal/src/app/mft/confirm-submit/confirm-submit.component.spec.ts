import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ConfirmSubmitComponent } from './confirm-submit.component';

describe('ConfirmSubmitComponent', () => {
  let component: ConfirmSubmitComponent;
  let fixture: ComponentFixture<ConfirmSubmitComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ConfirmSubmitComponent]
    });
    fixture = TestBed.createComponent(ConfirmSubmitComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
