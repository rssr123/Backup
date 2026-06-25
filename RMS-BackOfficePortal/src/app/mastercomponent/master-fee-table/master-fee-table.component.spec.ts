import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MasterFeeTableComponent } from './master-fee-table.component';

describe('MasterFeeTableComponent', () => {
  let component: MasterFeeTableComponent;
  let fixture: ComponentFixture<MasterFeeTableComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [MasterFeeTableComponent]
    });
    fixture = TestBed.createComponent(MasterFeeTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
