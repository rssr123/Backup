import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RIPLDetailComponent } from './ri-pl-detail.component';

describe('RIPLDetailComponent', () => {
  let component: RIPLDetailComponent;
  let fixture: ComponentFixture<RIPLDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [RIPLDetailComponent]
    });
    fixture = TestBed.createComponent(RIPLDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
