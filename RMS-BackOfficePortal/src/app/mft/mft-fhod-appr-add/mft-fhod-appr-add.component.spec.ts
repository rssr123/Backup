import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MftFhodApprAddComponent } from './mft-fhod-appr-add.component';

describe('MftFhodApprAddComponent', () => {
  let component: MftFhodApprAddComponent;
  let fixture: ComponentFixture<MftFhodApprAddComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [MftFhodApprAddComponent]
    });
    fixture = TestBed.createComponent(MftFhodApprAddComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
