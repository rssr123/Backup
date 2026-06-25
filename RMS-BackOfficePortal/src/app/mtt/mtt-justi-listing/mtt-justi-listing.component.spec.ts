import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MttJustiListingComponent } from './mtt-justi-listing.component';

describe('MttJustiListingComponent', () => {
  let component: MttJustiListingComponent;
  let fixture: ComponentFixture<MttJustiListingComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [MttJustiListingComponent]
    });
    fixture = TestBed.createComponent(MttJustiListingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
