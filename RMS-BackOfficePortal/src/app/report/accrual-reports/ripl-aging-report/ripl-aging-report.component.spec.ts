import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RIPLAgingReportComponent } from './ripl-aging-report.component';

describe('RIPLAgingReportComponent', () => {
  let component: RIPLAgingReportComponent;
  let fixture: ComponentFixture<RIPLAgingReportComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [RIPLAgingReportComponent]
    });
    fixture = TestBed.createComponent(RIPLAgingReportComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
