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

  private getUserId(): number {
    // Priority 1: currentUser which strictly defines `id: number`
    const userStr = localStorage.getItem('currentUser');
    if (userStr) {
      try {
        const user = JSON.parse(userStr);
        if (user && user.id && !isNaN(Number(user.id))) return Number(user.id);
      } catch (e) { }
    }

    // Priority 2: Fallback to JWT decoding (handle non-numeric subs)
    const token = localStorage.getItem('authToken');
    if (token) {
      try {
        const payload = JSON.parse(atob(token.split('.')[1]));
        if (payload.id && !isNaN(Number(payload.id))) return Number(payload.id);
        if (payload.userId && !isNaN(Number(payload.userId))) return Number(payload.userId);

        const subInt = parseInt(payload.sub, 10);
        if (payload.sub && !isNaN(subInt)) return subInt;
      } catch (e) {
        console.error('Error decoding JWT token for userId', e);
      }
    }

    return 0; // Or throw error
  }

  private getSafeUserId(): number {
    const finalId = this.getUserId();
    if (isNaN(finalId) || !isFinite(finalId) || finalId === null || finalId === undefined) {
      console.warn("Extracted userId was invalid/NaN, defaulting to 0");
      return 0;
    }
    return finalId;
  }

  // --- Business Endpoints ---
  getBusinesses(): Observable<any[]> {
    const userId = this.getSafeUserId();
    if (userId === 0) return new Observable(subj => { subj.next([]); subj.complete(); });
    return this.http.get<any[]>(`${this.apiUrl}/v1/business/user/${userId}`);
  }

  createBusiness(business: any): Observable<any> {
    const userId = this.getSafeUserId();
    return this.http.post<any>(`${this.apiUrl}/v1/business/user/${userId}`, business);
  }

  getBusinessWallet(businessId: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/v1/business/${businessId}/wallet`);
  }

  createBusinessWallet(businessId: number): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/v1/business/${businessId}/wallet`, {});
  }

  getFinanceRatios(businessId: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/v1/business/${businessId}/finance`);
  }

  // --- Mouvement Endpoints ---
  getMouvements(businessId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/v1/mouvements/business/${businessId}`);
  }

  addMouvement(businessId: number, mouvement: any): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/v1/mouvements/business/${businessId}`, mouvement);
  }

  classifyMouvement(mouvementId: number, statut: string, compte: string, categorie: string): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/v1/mouvements/${mouvementId}/classify?statut=${encodeURIComponent(statut)}&compte=${encodeURIComponent(compte)}&categorie=${encodeURIComponent(categorie)}`, {});
  }

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

