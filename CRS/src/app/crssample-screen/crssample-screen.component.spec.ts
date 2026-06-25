import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CRSSampleScreenComponent } from './crssample-screen.component';

describe('CRSSampleScreenComponent', () => {
  let component: CRSSampleScreenComponent;
  let fixture: ComponentFixture<CRSSampleScreenComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [CRSSampleScreenComponent]
    });
    fixture = TestBed.createComponent(CRSSampleScreenComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
