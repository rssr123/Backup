import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MftFaApprAddComponent } from './mft-fa-appr-add.component';

describe('MftFaApprAddComponent', () => {
  let component: MftFaApprAddComponent;
  let fixture: ComponentFixture<MftFaApprAddComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [MftFaApprAddComponent]
    });
    fixture = TestBed.createComponent(MftFaApprAddComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
