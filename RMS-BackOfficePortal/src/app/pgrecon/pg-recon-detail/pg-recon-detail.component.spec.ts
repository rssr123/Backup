import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PgReconDetailComponent } from './pg-recon-detail.component';

describe('PgReconDetailComponent', () => {
  let component: PgReconDetailComponent;
  let fixture: ComponentFixture<PgReconDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [PgReconDetailComponent]
    });
    fixture = TestBed.createComponent(PgReconDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
