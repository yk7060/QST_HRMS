import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../app/auths/auth.service';


@Component({
  selector: 'app-register',
  standalone: true,
  templateUrl: './register.component.html',
  styleUrl: './register.component.css',
  imports: [CommonModule, FormsModule]
})
export class RegisterComponent {
  user = {
    username: '',
    email: '',
    password: '',
    role: ''
  };

  roles = ['HR', 'MANAGER', 'FINANCE', 'EMPLOYEE'];
  isLoading = false;
  errorMessage = '';

  constructor(private authService: AuthService, private router: Router) {}

  onSubmit() {
    this.isLoading = true;
    this.errorMessage = '';

    const token = this.authService.getToken();
    if (!token) {
      this.isLoading = false;
      this.errorMessage = 'Admin must be logged in.';
      return;
    }

    this.authService.register(this.user).subscribe({
      next: () => {
        this.isLoading = false;
        this.router.navigate(['/login']); // Navigate to login after success
      },
      error: (err) => {
        this.isLoading = false;
        this.errorMessage = err?.error?.message || 'Registration failed.';
      }
    });
  }
}
