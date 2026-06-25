import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FmsUpdateComponent } from './fms-update.component';

describe('FmsUpdateComponent', () => {
  let component: FmsUpdateComponent;
  let fixture: ComponentFixture<FmsUpdateComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [FmsUpdateComponent]
    });
    fixture = TestBed.createComponent(FmsUpdateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
