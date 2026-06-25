import { ComponentFixture, TestBed } from '@angular/core/testing';

import { WhitelistScreenComponent } from './whitelist-screen.component';

describe('WhitelistScreenComponent', () => {
  let component: WhitelistScreenComponent;
  let fixture: ComponentFixture<WhitelistScreenComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [WhitelistScreenComponent]
    });
    fixture = TestBed.createComponent(WhitelistScreenComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
