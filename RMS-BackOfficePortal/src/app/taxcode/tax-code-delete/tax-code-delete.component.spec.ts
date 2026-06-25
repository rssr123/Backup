import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TaxCodeDeleteComponent } from './tax-code-delete.component';

describe('TaxCodeDeleteComponent', () => {
  let component: TaxCodeDeleteComponent;
  let fixture: ComponentFixture<TaxCodeDeleteComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [TaxCodeDeleteComponent]
    });
    fixture = TestBed.createComponent(TaxCodeDeleteComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
