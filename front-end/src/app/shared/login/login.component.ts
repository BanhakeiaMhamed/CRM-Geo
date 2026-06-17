import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../Authentication/auth.service';
import { GeoLogoComponent } from '../geo-logo/geo-logo.component';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, GeoLogoComponent],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);
  private readonly fb = inject(FormBuilder);

  isSignUp = false;
  loading = false;
  errorMessage = '';

  form = this.fb.group({
    displayName: [''],
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(6)]]
  });

  async submit(): Promise<void> {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.loading = true;
    this.errorMessage = '';

    const { email, password, displayName } = this.form.getRawValue();

    try {
      if (this.isSignUp) {
        await this.authService.signUp(email!, password!, displayName ?? '');
      } else {
        await this.authService.signIn(email!, password!);
      }
      await this.router.navigate(['/home']);
    } catch (error: unknown) {
      this.errorMessage = error instanceof Error ? error.message : 'Erreur d\'authentification Firebase.';
    } finally {
      this.loading = false;
    }
  }

  toggleMode(): void {
    this.isSignUp = !this.isSignUp;
    this.errorMessage = '';
  }
}
