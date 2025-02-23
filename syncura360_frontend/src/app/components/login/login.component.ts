import { Component } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    CommonModule, 
    ReactiveFormsModule,
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
export class LoginComponent {
  loginForm: FormGroup;
  loading = false;
  errorMessage = '';

  constructor(private fb: FormBuilder, private router: Router) {
    this.loginForm = this.fb.group({
      username: ['', [Validators.required]],
      password: ['', [Validators.required]]
    });
  }

  passwordVisible: boolean = false;

  togglePassword() {
    this.passwordVisible = !this.passwordVisible;
  }

  onSubmit() {
    const username = this.loginForm.get('username')?.value;
    const password = this.loginForm.get('password')?.value;

    if (!username && !password) {
      this.errorMessage = 'Please enter your username and password';
      return;
    } else if (!username) {
      this.errorMessage = 'Please enter your username';
      return;
    } else if (!password) {
      this.errorMessage = 'Please enter your password';
      return;
    }

    this.loading = true;
    
    setTimeout(() => {
      this.loading = false;
      this.router.navigate(['/dashboard']); // Redirect to single dashboard component
    }, 1000);
  }

  navigateToRegister() {
    console.log("Navigating to register...");
    this.router.navigateByUrl('/register');  // ✅ Try using `navigateByUrl()`
  }
  

}