import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject, map, tap } from 'rxjs';
import { SignupRequestDTO, SigninRequestDTO, AuthResponseDTO, User } from '../models/auth.model';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = `${environment.apiUrl}/auth`;
  private authContextUrl = `${environment.apiUrl}/v1/auth-context/me`;
  private backendBaseUrl = environment.apiUrl.replace(/\/api\/?$/, '');
  private tokenKey = 'authToken';
  private userKey = 'currentUser';

  private currentUserSubject = new BehaviorSubject<User | null>(this.getUserFromStorage());
  public currentUser$ = this.currentUserSubject.asObservable();

  constructor(private http: HttpClient) {}

  signup(request: SignupRequestDTO): Observable<AuthResponseDTO> {
    return this.http.post<AuthResponseDTO>(`${this.apiUrl}/signup`, request).pipe(
      tap(response => this.handleAuthentication(response))
    );
  }

  signin(request: SigninRequestDTO): Observable<AuthResponseDTO> {
    return this.http.post<AuthResponseDTO>(`${this.apiUrl}/signin`, request).pipe(
      tap(response => this.handleAuthentication(response))
    );
  }

  forgotPassword(email: string): Observable<{ message: string }> {
    return this.http.post<{ message: string }>(`${this.apiUrl}/forgot-password`, { email });
  }

  resetPassword(token: string, newPassword: string): Observable<{ message: string }> {
    return this.http.post<{ message: string }>(`${this.apiUrl}/reset-password`, { token, newPassword });
  }

  changePassword(currentPassword: string, newPassword: string): Observable<{ message: string }> {
    return this.http.put<{ message: string }>(`${this.apiUrl}/change-password`, {
      currentPassword,
      newPassword
    });
  }

  disableAccount(): Observable<{ message: string }> {
    return this.http.post<{ message: string }>(`${this.apiUrl}/disable-account`, {});
  }

  getGoogleLoginUrl(role: 'OBSERVER' | 'ASSISTANT' = 'OBSERVER'): Observable<string> {
    return this.http
      .get<{ url: string }>(`${this.apiUrl}/oauth2/google-url`, { params: { role } })
      .pipe(map(response => this.resolveBackendUrl(response.url)));
  }

  signinWithGoogleToken(token: string): Observable<User> {
    localStorage.setItem(this.tokenKey, token);
    return this.http.get<any>(this.authContextUrl).pipe(
      map(profile => this.mapProfileToUser(profile)),
      tap(user => {
        localStorage.setItem(this.userKey, JSON.stringify(user));
        this.currentUserSubject.next(user);
      })
    );
  }

  logout(): void {
    localStorage.removeItem(this.tokenKey);
    localStorage.removeItem(this.userKey);
    this.currentUserSubject.next(null);
  }

  getToken(): string | null {
    return localStorage.getItem(this.tokenKey);
  }

  isAuthenticated(): boolean {
    return !!this.getToken();
  }

  getCurrentUser(): User | null {
    return this.currentUserSubject.value;
  }

  private mapProfileToUser(profile: any): User {
    const userType = profile?.permissions ? 'OBSERVER' : 'ASSISTANT';
    return {
      id: Number(profile?.id ?? 0),
      username: profile?.username ?? profile?.email ?? 'Google User',
      email: profile?.email ?? '',
      userType
    };
  }

  private resolveBackendUrl(url: string): string {
    if (!url) return this.backendBaseUrl;
    if (url.startsWith('http://') || url.startsWith('https://')) return url;
    const prefix = url.startsWith('/') ? '' : '/';
    return `${this.backendBaseUrl}${prefix}${url}`;
  }

  private handleAuthentication(response: AuthResponseDTO): void {
    localStorage.setItem(this.tokenKey, response.token);
    const user: User = {
      id: response.id,
      username: response.username,
      email: response.email,
      userType: response.userType
    };
    localStorage.setItem(this.userKey, JSON.stringify(user));
    this.currentUserSubject.next(user);
  }

  private getUserFromStorage(): User | null {
    const userStr = localStorage.getItem(this.userKey);
    return userStr ? JSON.parse(userStr) : null;
  }
}