import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CounterCollectionComponent } from './counter-collection.component';

describe('CounterCollectionComponent', () => {
  let component: CounterCollectionComponent;
  let fixture: ComponentFixture<CounterCollectionComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [CounterCollectionComponent]
    });
    fixture = TestBed.createComponent(CounterCollectionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
