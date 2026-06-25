import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MftReqFormEditComponent } from './mft-req-form-edit.component';

describe('MftReqFormEditComponent', () => {
  let component: MftReqFormEditComponent;
  let fixture: ComponentFixture<MftReqFormEditComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [MftReqFormEditComponent]
    });
    fixture = TestBed.createComponent(MftReqFormEditComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
