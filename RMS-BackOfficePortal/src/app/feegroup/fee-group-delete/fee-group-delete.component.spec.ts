import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FeeGroupDeleteComponent } from './fee-group-delete.component';

describe('FeeGroupDeleteComponent', () => {
  let component: FeeGroupDeleteComponent;
  let fixture: ComponentFixture<FeeGroupDeleteComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [FeeGroupDeleteComponent]
    });
    fixture = TestBed.createComponent(FeeGroupDeleteComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
