import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Dinar, DinarWallet } from '../models/dinar-wallet.model';

@Injectable({
  providedIn: 'root'
})
export class DinarWalletService {
  private baseUrl = `${environment.apiUrl}/dinar-wallets`;

  constructor(private http: HttpClient) {}

  createWallet(): Observable<DinarWallet> {
    return this.http.post<DinarWallet>(`${this.baseUrl}/create`, {});
  }

  deposit(walletId: string, amount: number): Observable<DinarWallet> {
    return this.http.post<DinarWallet>(`${this.baseUrl}/${encodeURIComponent(walletId)}/deposit/${amount}`, {});
  }

  getWallet(walletId: string): Observable<DinarWallet> {
    return this.http.get<DinarWallet>(`${this.baseUrl}/${encodeURIComponent(walletId)}`);
  }

  getWalletDinars(walletId: string): Observable<Dinar[]> {
    return this.http.get<Dinar[]>(`${this.baseUrl}/${encodeURIComponent(walletId)}/dinars`);
  }

  getNodeDinars(nodeId: number): Observable<Dinar[]> {
    return this.http.get<Dinar[]>(`${this.baseUrl}/node/${nodeId}/dinars`);
  }

  withdraw(walletId: string, amount: number): Observable<DinarWallet> {
    return this.http.post<DinarWallet>(`${this.baseUrl}/${encodeURIComponent(walletId)}/withdraw/${amount}`, {});
  }

  getMyWallet(): Observable<DinarWallet> {
    return this.http.get<DinarWallet>(`${this.baseUrl}/myWallet`);
  }
}
