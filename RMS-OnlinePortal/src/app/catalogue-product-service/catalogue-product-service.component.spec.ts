import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CatalogueProductServiceComponent } from './catalogue-product-service.component';

describe('CatalogueProductServiceComponent', () => {
  let component: CatalogueProductServiceComponent;
  let fixture: ComponentFixture<CatalogueProductServiceComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [CatalogueProductServiceComponent]
    });
    fixture = TestBed.createComponent(CatalogueProductServiceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
