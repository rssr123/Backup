import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ReprintreceiptdetailsComponent } from './reprintreceiptdetails.component';

describe('ReprintreceiptdetailsComponent', () => {
  let component: ReprintreceiptdetailsComponent;
  let fixture: ComponentFixture<ReprintreceiptdetailsComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ReprintreceiptdetailsComponent]
    });
    fixture = TestBed.createComponent(ReprintreceiptdetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
