import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GhlComponent } from './ghl.component';

describe('GhlComponent', () => {
  let component: GhlComponent;
  let fixture: ComponentFixture<GhlComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [GhlComponent]
    });
    fixture = TestBed.createComponent(GhlComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
