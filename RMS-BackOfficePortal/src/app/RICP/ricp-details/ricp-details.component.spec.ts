import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RicpDetailsComponent } from './ricp-details.component';

describe('RicpDetailsComponent', () => {
  let component: RicpDetailsComponent;
  let fixture: ComponentFixture<RicpDetailsComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [RicpDetailsComponent]
    });
    fixture = TestBed.createComponent(RicpDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
