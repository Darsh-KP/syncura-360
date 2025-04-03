import { Injectable, Inject, PLATFORM_ID } from '@angular/core';
import { CanActivate, Router } from '@angular/router';
import { isPlatformBrowser } from '@angular/common';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {

  constructor(
    private router: Router,
    @Inject(PLATFORM_ID) private platformId: object
  ) {
  }

  canActivate(route: any): boolean {
    if (isPlatformBrowser(this.platformId)) {
      const token = localStorage.getItem('token');
      const role = localStorage.getItem('role');
      const allowedRoles = route.data?.['roles'];

      if (token && (!allowedRoles || allowedRoles.includes(role))) {
        return true;
      }
      this.router.navigate(['/']);
      return false;
    }
    return false;
  }
}
