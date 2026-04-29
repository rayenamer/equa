import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface UserDTO {
  id: number;
  username: string;
  email: string;
  userType: 'ASSISTANT' | 'OBSERVER' | string;
  createdAt: string;
  updatedAt: string;
}

export interface UserRolesDTO {
  userType: 'ASSISTANT' | 'OBSERVER' | string;
  permissions: string[];
}

export interface KycProfileDTO {
  nationalIdNumber: string;
  dateOfBirth: string;
  address: string;
  phoneNumber: string;
  kycSubmittedAt?: string;
  kycReviewNote?: string;
}

export type KycStatus = 'PENDING' | 'APPROVED' | 'REJECTED' | string;

export interface AuditLogDTO {
  logId: number;
  action: string;
  performedBy?: string;
  timestamp: string;
  eventId?: number | null;
  observerUserId?: number | null;
}

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private usersApiUrl = `${environment.apiUrl}/users`;
  private authContextApiUrl = `${environment.apiUrl}/v1/auth-context`;

  constructor(private http: HttpClient) {}

  getAllUsers(): Observable<UserDTO[]> {
    return this.http.get<UserDTO[]>(this.usersApiUrl);
  }

  getCurrentUserId(): Observable<number> {
    return this.http.get<number>(`${this.authContextApiUrl}/user-id`);
  }

  getUserById(id: number): Observable<UserDTO> {
    return this.http.get<UserDTO>(`${this.usersApiUrl}/${id}`);
  }

  updateUser(id: number, payload: UserDTO): Observable<UserDTO> {
    return this.http.put<UserDTO>(`${this.usersApiUrl}/${id}`, payload);
  }

  getUserRoles(id: number): Observable<UserRolesDTO> {
    return this.http.get<UserRolesDTO>(`${this.usersApiUrl}/${id}/roles`);
  }

  updateUserRoles(id: number, payload: UserRolesDTO): Observable<UserRolesDTO> {
    return this.http.put<UserRolesDTO>(`${this.usersApiUrl}/${id}/roles`, payload);
  }

  getRiskLevel(id: number): Observable<{ riskLevel: string }> {
    return this.http.get<{ riskLevel: string }>(`${this.usersApiUrl}/${id}/risk-level`);
  }

  getKycStatus(id: number): Observable<{ kycStatus: string }> {
    return this.http.get<{ kycStatus: string }>(`${this.usersApiUrl}/${id}/kyc-status`);
  }

  getMyKycProfile(): Observable<KycProfileDTO> {
    return this.http.get<KycProfileDTO>(`${this.usersApiUrl}/me/kyc-profile`);
  }

  createMyKycProfile(payload: KycProfileDTO): Observable<KycProfileDTO> {
    return this.http.post<KycProfileDTO>(`${this.usersApiUrl}/me/kyc-profile`, payload);
  }

  updateMyKycProfile(payload: KycProfileDTO): Observable<KycProfileDTO> {
    return this.http.put<KycProfileDTO>(`${this.usersApiUrl}/me/kyc-profile`, payload);
  }

  updateObserverKycStatus(id: number, kycStatus: KycStatus, note = ''): Observable<{ kycStatus: string }> {
    return this.http.put<{ kycStatus: string }>(`${this.usersApiUrl}/${id}/kyc-status`, { kycStatus, note });
  }

  getAuditLogsByObserver(observerUserId: number): Observable<AuditLogDTO[]> {
    return this.http.get<AuditLogDTO[]>(`${this.usersApiUrl}/observers/${observerUserId}/audit-logs`);
  }

  getAllAuditLogs(): Observable<AuditLogDTO[]> {
    return this.http.get<AuditLogDTO[]>(`${this.usersApiUrl}/audit-logs`);
  }
}
