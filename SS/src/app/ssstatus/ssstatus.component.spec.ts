import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SsstatusComponent } from './ssstatus.component';

describe('SsstatusComponent', () => {
  let component: SsstatusComponent;
  let fixture: ComponentFixture<SsstatusComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [SsstatusComponent]
    });
    fixture = TestBed.createComponent(SsstatusComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
