import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RICPAgingComponent } from './ricp-aging.component';

describe('RICPAgingComponent', () => {
  let component: RICPAgingComponent;
  let fixture: ComponentFixture<RICPAgingComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [RICPAgingComponent]
    });
    fixture = TestBed.createComponent(RICPAgingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
