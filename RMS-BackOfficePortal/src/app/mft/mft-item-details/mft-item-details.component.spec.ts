import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MftItemDetailsComponent } from './mft-item-details.component';

describe('MftItemDetailsComponent', () => {
  let component: MftItemDetailsComponent;
  let fixture: ComponentFixture<MftItemDetailsComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [MftItemDetailsComponent]
    });
    fixture = TestBed.createComponent(MftItemDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
