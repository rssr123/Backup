import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RIPLComponent } from './ri-pl-listing.component';

describe('RIPLComponent', () => {
  let component: RIPLComponent;
  let fixture: ComponentFixture<RIPLComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [RIPLComponent]
    });
    fixture = TestBed.createComponent(RIPLComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
