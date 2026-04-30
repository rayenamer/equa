import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Project } from '../models/project.model';
import { DinarWallet, WalletDTO } from '../BlockChain/models/dinar-wallet.model';
import { Transaction, TransactionRequest } from '../BlockChain/models/transaction.model';
import { ChatRequest, ChatResponse, HealthScore, ValidatorPrediction } from '../BlockChain/models/ai-insights.model';

@Injectable({
  providedIn: 'root'
})
export class ApiService {
  private apiUrl = 'http://localhost:8081/api';

  constructor(private http: HttpClient) { }

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

  depositDinar(walletId: string, cardCode: string): Observable<DinarWallet> {
    return this.http.post<DinarWallet>(`${this.apiUrl}/dinar-wallets/${walletId}/deposit?cardCode=${encodeURIComponent(cardCode)}`, {});
  }

  withdrawDinar(walletId: string, amount: number | string): Observable<DinarWallet> {
    return this.http.post<DinarWallet>(`${this.apiUrl}/dinar-wallets/${walletId}/withdraw?amount=${amount}`, {});
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

  // Smart Contract endpoints
  processTransaction(request: TransactionRequest): Observable<Transaction> {
    return this.http.post<Transaction>(`${this.apiUrl}/v1/smartContract/process`, request);
  }

  analyzeBlockchain(): Observable<{ analysis: string }> {
    return this.http.get<{ analysis: string }>(`${this.apiUrl}/v1/smartContract/analyze`);
  }

  chatWithBlockchain(request: ChatRequest): Observable<ChatResponse> {
    return this.http.post<ChatResponse>(`${this.apiUrl}/v1/smartContract/chat`, request);
  }

  getHealthScore(): Observable<HealthScore> {
    return this.http.get<HealthScore>(`${this.apiUrl}/v1/smartContract/health`);
  }

  clearChatSession(sessionId: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/v1/smartContract/chat/${sessionId}`);
  }

  predictNextValidator(): Observable<ValidatorPrediction> {
    return this.http.get<ValidatorPrediction>(`${this.apiUrl}/v1/smartContract/predict`);
  }

  getCurrentRate(): Observable<number> {
    return this.http.get<number>(`${this.apiUrl}/v1/rates/current`);
  }
}

