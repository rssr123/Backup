import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OtcCollectionReceiptingComponent } from './otc-collection-receipting.component';

describe('OtcCollectionReceiptingComponent', () => {
  let component: OtcCollectionReceiptingComponent;
  let fixture: ComponentFixture<OtcCollectionReceiptingComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [OtcCollectionReceiptingComponent]
    });
    fixture = TestBed.createComponent(OtcCollectionReceiptingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
