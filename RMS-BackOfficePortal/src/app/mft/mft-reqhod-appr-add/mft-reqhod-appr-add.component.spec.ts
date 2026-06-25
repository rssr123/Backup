import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MftReqhodApprAddComponent } from './mft-reqhod-appr-add.component';

describe('MftReqhodApprAddComponent', () => {
  let component: MftReqhodApprAddComponent;
  let fixture: ComponentFixture<MftReqhodApprAddComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [MftReqhodApprAddComponent]
    });
    fixture = TestBed.createComponent(MftReqhodApprAddComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
