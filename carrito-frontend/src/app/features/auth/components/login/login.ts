import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { AuthService } from '../../services/auth.service';
import { LoginModalService } from '../../../../shared/services/login-modal.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class Login implements OnInit {
  loginForm!: FormGroup;
  errorMessage: string = '';
  isLoading: boolean = false;
  isOpen: boolean = false;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private loginModalService: LoginModalService
  ) { }

  ngOnInit(): void {
    this.loginForm = this.fb.group({
      username: ['', [Validators.required]],
      password: ['', [Validators.required]]
    });

    this.loginModalService.isOpen$.subscribe(isOpen => {
      this.isOpen = isOpen;
      if (isOpen) {
        this.errorMessage = '';
        this.loginForm.reset();
      }
    });
  }

  onSubmit(): void {
    if (this.loginForm.invalid) {
      return;
    }

    this.isLoading = true;
    this.errorMessage = '';

    this.authService.login(this.loginForm.value).subscribe({
      next: () => {
        this.isLoading = false;
        this.loginModalService.notifyLoginSuccess();
      },
      error: (err) => {
        this.isLoading = false;
        this.errorMessage = err.error?.message || 'Usuario o contraseña incorrectos';
      }
    });
  }

  closeModal(): void {
    this.loginModalService.closeModal();
  }

  onBackdropClick(event: MouseEvent): void {
    if (event.target === event.currentTarget) {
      this.closeModal();
    }
  }
}
