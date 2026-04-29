import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink, Router, ActivatedRoute } from '@angular/router';
import { UiInputComponent } from '../../components/atoms/ui-input/ui-input.component';
import { UiButtonComponent } from '../../components/atoms/ui-button/ui-button.component';
import { AuthService } from '../services/auth.service';
import { SigninRequestDTO } from '../models/auth.model';
import { environment } from '../../../environments/environment';

declare global {
  interface Window {
    grecaptcha?: {
      ready: (callback: () => void) => void;
      execute: (siteKey: string, options: { action: string }) => Promise<string>;
    };
  }
}

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, UiInputComponent, UiButtonComponent],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent {
  credentials: SigninRequestDTO = {
    email: '',
    password: ''
  };

  recaptchaSiteKey = environment.recaptchaSiteKey;
  recaptchaEnabled = !!this.recaptchaSiteKey;

  loading = false;
  errorMessage = '';

  constructor(
    private authService: AuthService,
    private router: Router,
    private route: ActivatedRoute
  ) {
    this.handleGoogleCallback();
  }

  async onSubmit() {
    if (!this.credentials.email || !this.credentials.password) {
      this.errorMessage = 'Veuillez remplir tous les champs.';
      return;
    }

    this.loading = true;
    this.errorMessage = '';
    if (this.recaptchaEnabled) {
      try {
        this.credentials.recaptchaToken = await this.executeRecaptcha('signin');
      } catch {
        this.loading = false;
        this.errorMessage = 'reCAPTCHA indisponible. Reessayez dans quelques secondes.';
        return;
      }
    }

    this.authService.signin(this.credentials).subscribe({
      next: (response) => {
        this.loading = false;
        console.log('Login successful:', response);
        // Redirect to dashboard or home
        this.router.navigate(['/']);
      },
      error: (error) => {
        this.loading = false;
        console.error('Login failed:', error);
        this.errorMessage = error.error?.message || 'Erreur de connexion. Vérifiez vos identifiants.';
      }
    });
  }

  loginWithGoogle() {
    this.loading = true;
    this.errorMessage = '';
    this.authService.getGoogleLoginUrl('OBSERVER').subscribe({
      next: (googleUrl) => {
        window.location.href = googleUrl;
      },
      error: () => {
        this.loading = false;
        this.errorMessage = 'Impossible de démarrer la connexion Google.';
      }
    });
  }

  private handleGoogleCallback() {
    const token = this.route.snapshot.queryParamMap.get('token');
    const oauthError = this.route.snapshot.queryParamMap.get('error');

    if (oauthError) {
      this.errorMessage = 'La connexion Google a échoué. Veuillez réessayer.';
      return;
    }

    if (!token) return;

    this.loading = true;
    this.authService.signinWithGoogleToken(token).subscribe({
      next: () => {
        this.loading = false;
        this.router.navigate(['/']);
      },
      error: () => {
        this.loading = false;
        this.errorMessage = 'Connexion Google réussie, mais récupération du profil impossible.';
      }
    });
  }

  private async executeRecaptcha(action: string): Promise<string> {
    if (!this.recaptchaSiteKey) return '';
    await this.loadRecaptchaScript();

    for (let attempt = 0; attempt < 2; attempt++) {
      const grecaptcha = window.grecaptcha;
      if (!grecaptcha) throw new Error('grecaptcha not available');

      await new Promise<void>((resolve) => grecaptcha.ready(resolve));
      const token = await grecaptcha.execute(this.recaptchaSiteKey, { action });
      if (token && token !== 'browser-error') return token;
      await new Promise<void>((resolve) => setTimeout(resolve, 300));
    }

    throw new Error('recaptcha browser-error');
  }

  private loadRecaptchaScript(): Promise<void> {
    const existing = document.querySelector<HTMLScriptElement>('script[data-recaptcha-v3="true"]');
    if (existing) {
      return new Promise((resolve, reject) => {
        if ((window as any).grecaptcha) resolve();
        else {
          existing.addEventListener('load', () => resolve(), { once: true });
          existing.addEventListener('error', () => reject(new Error('recaptcha load error')), { once: true });
        }
      });
    }

    return new Promise((resolve, reject) => {
      const script = document.createElement('script');
      script.src = `https://www.google.com/recaptcha/api.js?render=${encodeURIComponent(this.recaptchaSiteKey)}`;
      script.async = true;
      script.defer = true;
      script.dataset['recaptchaV3'] = 'true';
      script.onload = () => resolve();
      script.onerror = () => {
        script.remove();
        const fallback = document.createElement('script');
        fallback.src = `https://www.recaptcha.net/recaptcha/api.js?render=${encodeURIComponent(this.recaptchaSiteKey)}`;
        fallback.async = true;
        fallback.defer = true;
        fallback.dataset['recaptchaV3'] = 'true';
        fallback.onload = () => resolve();
        fallback.onerror = () => reject(new Error('recaptcha load error'));
        document.head.appendChild(fallback);
      };
      document.head.appendChild(script);
    });
  }
}
