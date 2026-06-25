import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BranchCodeCounterDeleteComponent } from './branch-code-counter-delete.component';

describe('BranchCodeCounterDeleteComponent', () => {
  let component: BranchCodeCounterDeleteComponent;
  let fixture: ComponentFixture<BranchCodeCounterDeleteComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [BranchCodeCounterDeleteComponent]
    });
    fixture = TestBed.createComponent(BranchCodeCounterDeleteComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
