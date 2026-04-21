import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class SmartContractService {
  private baseUrl = `${environment.apiUrl}/v1/smartContract`;

  constructor(private http: HttpClient) {}

  processTransaction(request: Record<string, any>): Observable<any> {
    return this.http.post<any>(`${this.baseUrl}/process`, request);
  }

  getAllNodesWithBlockchain(): Observable<any[]> {
    return this.http.get<any[]>(this.baseUrl);
  }

  analyze(): Observable<Record<string, string>> {
    return this.http.get<Record<string, string>>(`${this.baseUrl}/analyze`);
  }

  chat(sessionId: string, message: string): Observable<Record<string, string>> {
    return this.http.post<Record<string, string>>(`${this.baseUrl}/chat`, { sessionId, message });
  }

  getHealthScore(): Observable<any> {
    return this.http.get<any>(`${this.baseUrl}/health`);
  }

  clearChat(sessionId: string): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/chat/${encodeURIComponent(sessionId)}`);
  }

  predictNextValidator(): Observable<any> {
    return this.http.get<any>(`${this.baseUrl}/predict`);
  }
}
