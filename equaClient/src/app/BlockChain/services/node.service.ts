import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Node } from '../models/node.model';

@Injectable({
  providedIn: 'root'
})
export class NodeService {
  private baseUrl = `${environment.apiUrl}/v1/nodes`;

  constructor(private http: HttpClient) {}

  getAllNodes(): Observable<Node[]> {
    return this.http.get<Node[]>(this.baseUrl);
  }

  getNodeById(id: number): Observable<Node> {
    return this.http.get<Node>(`${this.baseUrl}/${id}`);
  }

  createNode(request: Record<string, any>): Observable<Node> {
    return this.http.post<Node>(this.baseUrl, request);
  }

  searchNodes(filters: Record<string, any>): Observable<Node[]> {
    let params = new HttpParams();
    Object.entries(filters || {}).forEach(([key, value]) => {
      if (value !== undefined && value !== null && value !== '') {
        params = params.append(key, String(value));
      }
    });
    return this.http.get<Node[]>(`${this.baseUrl}/search`, { params });
  }

  getOnlineNodes(): Observable<Node[]> {
    return this.http.get<Node[]>(`${this.baseUrl}/online`);
  }

  getTopNodesByReputation(): Observable<Node[]> {
    return this.http.get<Node[]>(`${this.baseUrl}/top-reputation`);
  }

  updateNodeStatus(id: number, status: string): Observable<Node> {
    return this.http.patch<Node>(`${this.baseUrl}/${id}/status`, null, { params: { status } });
  }

  updateReputationScore(id: number, score: number): Observable<Node> {
    return this.http.patch<Node>(`${this.baseUrl}/${id}/reputation`, null, { params: { score: String(score) } });
  }

  updateLastSeen(id: number): Observable<Node> {
    return this.http.patch<Node>(`${this.baseUrl}/${id}/heartbeat`, null);
  }

  countNodesByStatus(status: string): Observable<number> {
    return this.http.get<number>(`${this.baseUrl}/count/status/${encodeURIComponent(status)}`);
  }

  deleteNode(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }

  existsByIpAddress(ipAddress: string): Observable<boolean> {
    return this.http.get<boolean>(`${this.baseUrl}/exists/ip/${encodeURIComponent(ipAddress)}`);
  }

  existsByPublicKey(publicKey: string): Observable<boolean> {
    return this.http.get<boolean>(`${this.baseUrl}/exists/public-key/${encodeURIComponent(publicKey)}`);
  }
}
