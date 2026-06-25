import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BibssListComponent } from './bibss-list.component';

describe('BibssListComponent', () => {
  let component: BibssListComponent;
  let fixture: ComponentFixture<BibssListComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [BibssListComponent]
    });
    fixture = TestBed.createComponent(BibssListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
