import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MftFaFaRqtAddComponent } from './mft-fa-fa-rqt-add.component';

describe('MftFaFaRqtAddComponent', () => {
  let component: MftFaFaRqtAddComponent;
  let fixture: ComponentFixture<MftFaFaRqtAddComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [MftFaFaRqtAddComponent]
    });
    fixture = TestBed.createComponent(MftFaFaRqtAddComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
