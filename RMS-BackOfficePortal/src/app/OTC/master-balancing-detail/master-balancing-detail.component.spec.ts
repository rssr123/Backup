import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MasterBalancingDetailComponent } from './master-balancing-detail.component';

describe('MasterBalancingDetailComponent', () => {
  let component: MasterBalancingDetailComponent;
  let fixture: ComponentFixture<MasterBalancingDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [MasterBalancingDetailComponent]
    });
    fixture = TestBed.createComponent(MasterBalancingDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
