import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { AuthGuard } from './auth.guard';

describe('AuthGuard', () => {
  let authGuard: AuthGuard;
  let routerSpy = { navigate: jasmine.createSpy('navigate') };

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        AuthGuard,
        { provide: Router, useValue: routerSpy }
      ]
    });

    authGuard = TestBed.inject(AuthGuard);
  });

  it('should be created', () => {
    expect(authGuard).toBeTruthy();
  });

  it('should allow access when token is present', () => {
    spyOn(localStorage, 'getItem').and.returnValue('mockToken'); // Simulate token presence
    const mockRoute = {} as any; // minimal mock, update as needed
    expect(authGuard.canActivate(mockRoute)).toBeTrue();
    expect(routerSpy.navigate).not.toHaveBeenCalled();
  });

  it('should deny access and redirect to login when token is missing', () => {
    spyOn(localStorage, 'getItem').and.returnValue(null); // Simulate no token
    const mockRoute = {} as any; // minimal mock, update as needed
    expect(authGuard.canActivate(mockRoute)).toBeFalse();
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/login']);
  });
});
