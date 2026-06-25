import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BranchCodeCounterAddComponent } from './branch-code-counter-add.component';

describe('BranchCodeCounterAddComponent', () => {
  let component: BranchCodeCounterAddComponent;
  let fixture: ComponentFixture<BranchCodeCounterAddComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [BranchCodeCounterAddComponent]
    });
    fixture = TestBed.createComponent(BranchCodeCounterAddComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
