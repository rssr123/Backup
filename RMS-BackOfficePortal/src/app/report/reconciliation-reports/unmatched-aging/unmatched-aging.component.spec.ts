import { ComponentFixture, TestBed } from '@angular/core/testing';
import { UnmatchedAgingComponent } from './unmatched-aging.component';

describe('UnmatchedAgingComponent', () => {
  let component: UnmatchedAgingComponent;
  let fixture: ComponentFixture<UnmatchedAgingComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [UnmatchedAgingComponent]
    });
    fixture = TestBed.createComponent(UnmatchedAgingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
