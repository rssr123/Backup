import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FmsledgerAddComponent } from './fmsledger-add.component';

describe('FmsledgerAddComponent', () => {
  let component: FmsledgerAddComponent;
  let fixture: ComponentFixture<FmsledgerAddComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [FmsledgerAddComponent]
    });
    fixture = TestBed.createComponent(FmsledgerAddComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
