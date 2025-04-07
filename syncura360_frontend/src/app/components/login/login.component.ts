import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { LoginService } from '../../services/login.service';
import { CommonModule } from '@angular/common';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { HttpClientModule } from '@angular/common/http';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    HttpClientModule,
    MatInputModule,
    MatButtonModule,
    MatCardModule,
    MatFormFieldModule,
    MatIconModule,
    RouterLink
  ],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {
  loginForm: FormGroup;
  loading = false;
  errorMessage = '';
  successMessage = '';

  constructor(private fb: FormBuilder, private router: Router, private loginService: LoginService) {
    this.loginForm = this.fb.group({
      username: ['', [Validators.required]],
      password: ['', [Validators.required]]
    });
  }

  ngOnInit() {
    if (typeof window !== 'undefined' && this.loginService.isAuthenticated()) {
      const role = localStorage.getItem('role');
      if (role === 'Nurse') {
        this.router.navigate(['/nurse']);
      } else if (role === 'Admin' || role === 'Super Admin') {
        this.router.navigate(['/dashboard']);
      } else if (role === 'Doctor') {
        this.router.navigate(['/doctor']);
      } else {
        this.router.navigate(['/']);
      }
    }
    }


    passwordVisible: boolean = false;

  togglePassword() {
    this.passwordVisible = !this.passwordVisible;
  }

  onSubmit() {
    if (this.loginForm.invalid) {
      this.errorMessage = 'Please enter both username and password';
      return;
    }

    this.loading = true;
    this.errorMessage = '';
    this.successMessage = '';
    const { username, password } = this.loginForm.value;

    this.loginService.login(username, password).subscribe({
      next: (response) => {
        this.loading = false;

        if (response.body?.message == "Authentication successful."){
          this.successMessage = 'Login Successful! Redirecting...';
          const role = response.body?.role;
          if (role === 'Nurse') {
            this.router.navigate(['/nurse']);
          } else if (role === 'Admin' || role === 'Super Admin') {
            this.router.navigate(['/dashboard']);
          } else if (role === 'Doctor') {
            this.router.navigate(['/doctor']);
          } else {
            this.router.navigate(['/']);
          }
        }


      },
      error: (error) => {
        this.loading = false;
        this.errorMessage = 'Login failed. Please try again.';
      }
    });
  }

  navigateToRegister() {
    this.router.navigate(['/register']);
  }
}
