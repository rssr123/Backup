import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CrsstatusComponent } from './crsstatus.component';

describe('CrsstatusComponent', () => {
  let component: CrsstatusComponent;
  let fixture: ComponentFixture<CrsstatusComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [CrsstatusComponent]
    });
    fixture = TestBed.createComponent(CrsstatusComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
