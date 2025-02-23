import { Component } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
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
    ReactiveFormsModule,  // ✅ Import this to fix 'formGroup' error
    MatInputModule, 
    MatButtonModule, 
    MatCardModule, 
    MatFormFieldModule, // ✅ Required for Material form fields
    MatIconModule // ✅ Optional for icons
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
    if (this.loginForm.invalid) return;
    this.loading = true;
    
    setTimeout(() => {
      this.loading = false;
      const { username } = this.loginForm.value;
      
      if (username.includes('admin')) {
        this.router.navigate(['/admin-dashboard']);
      } else if (username.includes('doctor')) {
        this.router.navigate(['/doctor-dashboard']);
      } else if (username.includes('nurse')) {
        this.router.navigate(['/nurse-dashboard']);
      } else {
        this.errorMessage = 'Invalid credentials';
      }
    }, 1000);
  }
}
