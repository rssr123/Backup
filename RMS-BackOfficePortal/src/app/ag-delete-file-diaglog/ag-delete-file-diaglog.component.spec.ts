import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AgDeleteFileDiaglogComponent } from './ag-delete-file-diaglog.component';

describe('AgDeleteFileDiaglogComponent', () => {
  let component: AgDeleteFileDiaglogComponent;
  let fixture: ComponentFixture<AgDeleteFileDiaglogComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [AgDeleteFileDiaglogComponent]
    });
    fixture = TestBed.createComponent(AgDeleteFileDiaglogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
