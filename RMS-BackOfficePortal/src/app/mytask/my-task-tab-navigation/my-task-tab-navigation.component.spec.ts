import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MyTaskTabNavigationComponent } from './my-task-tab-navigation.component';

describe('MyTaskTabNavigationComponent', () => {
  let component: MyTaskTabNavigationComponent;
  let fixture: ComponentFixture<MyTaskTabNavigationComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [MyTaskTabNavigationComponent]
    });
    fixture = TestBed.createComponent(MyTaskTabNavigationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
