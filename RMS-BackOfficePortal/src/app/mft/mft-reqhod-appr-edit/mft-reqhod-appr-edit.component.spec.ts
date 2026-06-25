import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MftReqhodApprEditComponent } from './mft-reqhod-appr-edit.component';

describe('MftReqhodApprEditComponent', () => {
  let component: MftReqhodApprEditComponent;
  let fixture: ComponentFixture<MftReqhodApprEditComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [MftReqhodApprEditComponent]
    });
    fixture = TestBed.createComponent(MftReqhodApprEditComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
