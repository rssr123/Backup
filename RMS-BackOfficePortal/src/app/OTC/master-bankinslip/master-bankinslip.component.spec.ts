import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MasterBankinslipComponent } from './master-bankinslip.component';

describe('MasterBankinslipComponent', () => {
  let component: MasterBankinslipComponent;
  let fixture: ComponentFixture<MasterBankinslipComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [MasterBankinslipComponent]
    });
    fixture = TestBed.createComponent(MasterBankinslipComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
