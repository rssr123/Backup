import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OtcCollectionComponent } from './otc-collection.component';

describe('OtcCollectionComponent', () => {
  let component: OtcCollectionComponent;
  let fixture: ComponentFixture<OtcCollectionComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [OtcCollectionComponent]
    });
    fixture = TestBed.createComponent(OtcCollectionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
