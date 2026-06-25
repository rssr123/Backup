import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MftFaFhodApprAddComponent } from './mft-fa-fhod-appr-add.component';

describe('MftFaFhodApprAddComponent', () => {
  let component: MftFaFhodApprAddComponent;
  let fixture: ComponentFixture<MftFaFhodApprAddComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [MftFaFhodApprAddComponent]
    });
    fixture = TestBed.createComponent(MftFaFhodApprAddComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
