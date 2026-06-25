import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CourtOrderDetailsComponent } from './court-order-details.component';

describe('CourtOrderDetailsComponent', () => {
  let component: CourtOrderDetailsComponent;
  let fixture: ComponentFixture<CourtOrderDetailsComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [CourtOrderDetailsComponent]
    });
    fixture = TestBed.createComponent(CourtOrderDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
