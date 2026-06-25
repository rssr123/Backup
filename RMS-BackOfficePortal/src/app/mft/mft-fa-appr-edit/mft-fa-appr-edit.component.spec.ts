import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MftFaApprEditComponent } from './mft-fa-appr-edit.component';

describe('MftFaApprEditComponent', () => {
  let component: MftFaApprEditComponent;
  let fixture: ComponentFixture<MftFaApprEditComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [MftFaApprEditComponent]
    });
    fixture = TestBed.createComponent(MftFaApprEditComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
