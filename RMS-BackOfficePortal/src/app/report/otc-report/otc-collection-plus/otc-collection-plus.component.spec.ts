import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OtcCollectionPlusComponent } from './otc-collection-plus.component';

describe('OtcCollectionPlusComponent', () => {
  let component: OtcCollectionPlusComponent;
  let fixture: ComponentFixture<OtcCollectionPlusComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [OtcCollectionPlusComponent]
    });
    fixture = TestBed.createComponent(OtcCollectionPlusComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
