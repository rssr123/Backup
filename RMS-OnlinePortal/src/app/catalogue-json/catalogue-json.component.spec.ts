import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CatalogueJsonComponent } from './catalogue-json.component';

describe('CatalogueJsonComponent', () => {
  let component: CatalogueJsonComponent;
  let fixture: ComponentFixture<CatalogueJsonComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [CatalogueJsonComponent]
    });
    fixture = TestBed.createComponent(CatalogueJsonComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
