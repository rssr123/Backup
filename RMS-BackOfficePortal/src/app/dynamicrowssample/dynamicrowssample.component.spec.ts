import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DynamicrowssampleComponent } from './dynamicrowssample.component';

describe('DynamicrowssampleComponent', () => {
  let component: DynamicrowssampleComponent;
  let fixture: ComponentFixture<DynamicrowssampleComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [DynamicrowssampleComponent]
    });
    fixture = TestBed.createComponent(DynamicrowssampleComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
