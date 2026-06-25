import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FmsAddComponent } from './fms-add.component';

describe('FmsAddComponent', () => {
  let component: FmsAddComponent;
  let fixture: ComponentFixture<FmsAddComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [FmsAddComponent]
    });
    fixture = TestBed.createComponent(FmsAddComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
