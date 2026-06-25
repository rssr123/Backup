import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MftFaFaEditComponent } from './mft-fa-fa-edit.component';

describe('MftFaFaEditComponent', () => {
  let component: MftFaFaEditComponent;
  let fixture: ComponentFixture<MftFaFaEditComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [MftFaFaEditComponent]
    });
    fixture = TestBed.createComponent(MftFaFaEditComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
