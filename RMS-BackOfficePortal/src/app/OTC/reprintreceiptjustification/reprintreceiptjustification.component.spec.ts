import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ReprintreceiptjustificationComponent } from './reprintreceiptjustification.component';

describe('ReprintreceiptjustificationComponent', () => {
  let component: ReprintreceiptjustificationComponent;
  let fixture: ComponentFixture<ReprintreceiptjustificationComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ReprintreceiptjustificationComponent]
    });
    fixture = TestBed.createComponent(ReprintreceiptjustificationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
