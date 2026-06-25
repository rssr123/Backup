import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MftReqFormAddComponent } from './mft-req-form-add.component';

describe('MftReqFormAddComponent', () => {
  let component: MftReqFormAddComponent;
  let fixture: ComponentFixture<MftReqFormAddComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [MftReqFormAddComponent]
    });
    fixture = TestBed.createComponent(MftReqFormAddComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
