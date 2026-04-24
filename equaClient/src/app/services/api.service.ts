import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Project } from '../models/project.model';
import { WalletDTO, DinarWallet } from '../models/wallet.model';

@Injectable({
  providedIn: 'root'
})
export class ApiService {
  private apiUrl = 'http://localhost:8081/api';

  constructor(private http: HttpClient) {}

  getProjects(): Observable<Project[]> {
    return this.http.get<Project[]>(`${this.apiUrl}/projects`);
  }

  getProjectById(id: number): Observable<Project> {
    return this.http.get<Project>(`${this.apiUrl}/projects/${id}`);
  }

  // Dinar Wallet endpoints
  createDinarWallet(): Observable<DinarWallet> {
    return this.http.post<DinarWallet>(`${this.apiUrl}/dinar-wallets/create`, {});
  }

  getMyDinarWallet(): Observable<DinarWallet> {
    return this.http.get<DinarWallet>(`${this.apiUrl}/dinar-wallets/myWallet`);
  }

  depositDinar(walletId: string, amount: number): Observable<DinarWallet> {
    return this.http.post<DinarWallet>(`${this.apiUrl}/dinar-wallets/${walletId}/deposit/${amount}`, {});
  }

  withdrawDinar(walletId: string, amount: number): Observable<DinarWallet> {
    return this.http.post<DinarWallet>(`${this.apiUrl}/dinar-wallets/${walletId}/withdraw/${amount}`, {});
  }

  // Wallet (Equa) endpoints
  createWallet(): Observable<WalletDTO> {
    return this.http.post<WalletDTO>(`${this.apiUrl}/v1/wallets`, {});
  }

  getMyWallet(): Observable<WalletDTO> {
    return this.http.get<WalletDTO>(`${this.apiUrl}/v1/wallets/me`);
  }

  convertDinarsToEqua(amount: number): Observable<WalletDTO> {
    return this.http.post<WalletDTO>(`${this.apiUrl}/v1/wallets/convert?amount=${amount}`, {});
  }
}
