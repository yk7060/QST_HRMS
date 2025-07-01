import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MessageService } from 'primeng/api';
import { ToastModule } from 'primeng/toast';
import { AuthService } from '../../app/auths/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, ToastModule],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
  providers: [MessageService]
})
export class LoginComponent {
  username = '';
  password = '';
  email = '';
  otp = '';
  newPassword = '';
  isAdmin = true;
  showForgot = false;
  showReset = false;

  constructor(
    private authService: AuthService,
    private router: Router,
    private messageService: MessageService
  ) {}

  login(): void {
    if (!this.username || !this.password) {
      this.messageService.add({
        severity: 'warn',
        summary: 'Validation',
        detail: 'Username and password are required'
      });
      return;
    }

    const loginMethod = this.isAdmin
      ? this.authService.adminLogin
      : this.authService.userLogin;

    loginMethod.call(this.authService, this.username, this.password).subscribe({
      next: () => {
        const role = this.authService.getUserRole();
        let route = '';

        if (role === 'ADMIN') {
          route = '/dashboard';
        } else if (role === 'HR') {
          route = '/hr-dashboard';
        } else if (role === 'EMPLOYEE') {
          route = '/employees-dashboard';
        } else if (role === 'MANAGER') {
          route = '/manager-dashboard';
        }

        this.router.navigate([route]);
      },
      error: (err) => {
        if (err?.error?.includes('reset the password')) {
          this.email = this.username;
          this.showForgot = false;
          this.showReset = true;
        }
      }
    });
  }

  toggleUserType(): void {
    this.isAdmin = !this.isAdmin;
  }

  sendOtp(): void {
    if (!this.email) {
      this.messageService.add({
        severity: 'warn',
        summary: 'Validation',
        detail: 'Email is required'
      });
      return;
    }

    this.authService.forgotPassword(this.email).subscribe({
      next: () => {
        this.messageService.add({
          severity: 'success',
          summary: 'OTP Sent',
          detail: 'OTP sent to your email'
        });
        this.showForgot = false;
        this.showReset = true;
      },
      error: () => {
        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail: 'Failed to send OTP'
        });
      }
    });
  }

  resetPassword(): void {
    if (!this.email || !this.otp || !this.newPassword) {
      this.messageService.add({
        severity: 'warn',
        summary: 'Validation',
        detail: 'All fields are required'
      });
      return;
    }

    this.authService.resetPassword(this.email, this.otp, this.newPassword).subscribe({
      next: () => {
        this.messageService.add({
          severity: 'success',
          summary: 'Success',
          detail: 'Password reset successful'
        });
        this.showReset = false;
      },
      error: () => {
        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail: 'Failed to reset password'
        });
      }
    });
  }
}
