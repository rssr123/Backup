import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SssampleScreenComponent } from './sssample-screen.component';

describe('SssampleScreenComponent', () => {
  let component: SssampleScreenComponent;
  let fixture: ComponentFixture<SssampleScreenComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [SssampleScreenComponent]
    });
    fixture = TestBed.createComponent(SssampleScreenComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
