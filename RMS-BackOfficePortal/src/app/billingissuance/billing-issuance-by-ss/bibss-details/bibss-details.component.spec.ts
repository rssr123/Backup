import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BibssDetailsComponent } from './bibss-details.component';

describe('BibssDetailsComponent', () => {
  let component: BibssDetailsComponent;
  let fixture: ComponentFixture<BibssDetailsComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [BibssDetailsComponent]
    });
    fixture = TestBed.createComponent(BibssDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
