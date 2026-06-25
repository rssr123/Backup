import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { AuthGuard } from './auth.guard';

describe('AuthGuard', () => {
  let guard: AuthGuard;
  let authService: AuthService;
  let router: Router;

  beforeEach(() => {
    TestBed.configureTestingModule({
      // Provide mock dependencies if required
      providers: [
        { provide: AuthService, useValue: {} },
        { provide: Router, useValue: {} }
      ]
    });
    authService = TestBed.inject(AuthService);
    router = TestBed.inject(Router);
    guard = new AuthGuard(authService, router);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  // Add more tests to test canActivate functionality
});
