import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ReprintreceiptComponent } from './reprintreceipt.component';

describe('ReprintreceiptComponent', () => {
  let component: ReprintreceiptComponent;
  let fixture: ComponentFixture<ReprintreceiptComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ReprintreceiptComponent]
    });
    fixture = TestBed.createComponent(ReprintreceiptComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
