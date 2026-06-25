import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MasterBalancingComponent } from './master-balancing.component';

describe('MasterBalancingComponent', () => {
  let component: MasterBalancingComponent;
  let fixture: ComponentFixture<MasterBalancingComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [MasterBalancingComponent]
    });
    fixture = TestBed.createComponent(MasterBalancingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
