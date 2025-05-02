import { TestBed } from '@angular/core/testing';
import { AuthGuard } from './auth.guard';
import { Router } from '@angular/router';
import { PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser, ɵPLATFORM_BROWSER_ID as PLATFORM_BROWSER_ID } from '@angular/common';

describe('AuthGuard', () => {
  let guard: AuthGuard;
  let routerSpy: jasmine.SpyObj<Router>;

  beforeEach(() => {
    routerSpy = jasmine.createSpyObj('Router', ['navigate']);

    TestBed.configureTestingModule({
      providers: [
        AuthGuard,
        { provide: Router, useValue: routerSpy },
        { provide: PLATFORM_ID, useValue: PLATFORM_BROWSER_ID }
      ]
    });

    guard = TestBed.inject(AuthGuard);
  });

  afterEach(() => {
    localStorage.clear();
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should allow activation if token exists and no roles are defined', () => {
    localStorage.setItem('token', 'test-token');
    const result = guard.canActivate({ data: {} });
    expect(result).toBeTrue();
    expect(routerSpy.navigate).not.toHaveBeenCalled();
  });

  it('should allow activation if token exists and role matches allowed roles', () => {
    localStorage.setItem('token', 'test-token');
    localStorage.setItem('role', 'Admin');
    const result = guard.canActivate({ data: { roles: ['Admin', 'User'] } });
    expect(result).toBeTrue();
    expect(routerSpy.navigate).not.toHaveBeenCalled();
  });

  it('should prevent activation if token does not exist', () => {
    const result = guard.canActivate({ data: {} });
    expect(result).toBeFalse();
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/']);
  });

  it('should prevent activation if role does not match allowed roles', () => {
    localStorage.setItem('token', 'test-token');
    localStorage.setItem('role', 'Guest');
    const result = guard.canActivate({ data: { roles: ['Admin', 'User'] } });
    expect(result).toBeFalse();
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/']);
  });

  it('should return false if not running in browser platform', () => {
    const nonBrowserGuard = new AuthGuard(routerSpy, {});
    spyOn(nonBrowserGuard as any, 'platformId').and.returnValue({});
    const result = nonBrowserGuard.canActivate({ data: {} });
    expect(result).toBeFalse();
    expect(routerSpy.navigate).not.toHaveBeenCalled();
  });
});
