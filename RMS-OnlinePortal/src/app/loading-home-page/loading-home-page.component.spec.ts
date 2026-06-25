import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LoadingHomePageComponent } from './loading-home-page.component';

describe('LoadingHomePageComponent', () => {
  let component: LoadingHomePageComponent;
  let fixture: ComponentFixture<LoadingHomePageComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [LoadingHomePageComponent]
    });
    fixture = TestBed.createComponent(LoadingHomePageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
