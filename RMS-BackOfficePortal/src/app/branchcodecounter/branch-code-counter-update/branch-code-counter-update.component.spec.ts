import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BranchCodeCounterUpdateComponent } from './branch-code-counter-update.component';

describe('BranchCodeCounterUpdateComponent', () => {
  let component: BranchCodeCounterUpdateComponent;
  let fixture: ComponentFixture<BranchCodeCounterUpdateComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [BranchCodeCounterUpdateComponent]
    });
    fixture = TestBed.createComponent(BranchCodeCounterUpdateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
