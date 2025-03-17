import { Component, Inject } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [RouterLink, CommonModule],
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent { 


  constructor(
    @Inject(Router) private router: Router
  ) {}


  logoutAndRedirect() {
    localStorage.removeItem('token');
    localStorage.removeItem('role');
    this.router.navigate(['/']); 
  }

}
