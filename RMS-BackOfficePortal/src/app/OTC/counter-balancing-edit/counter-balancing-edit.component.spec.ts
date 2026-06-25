import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CounterBalancingEditComponent } from './counter-balancing-edit.component';

describe('CounterBalancingEditComponent', () => {
  let component: CounterBalancingEditComponent;
  let fixture: ComponentFixture<CounterBalancingEditComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [CounterBalancingEditComponent]
    });
    fixture = TestBed.createComponent(CounterBalancingEditComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
