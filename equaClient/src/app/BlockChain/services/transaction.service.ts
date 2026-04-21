import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Transaction, TransactionStatus } from '../models/transaction.model';

@Injectable({
  providedIn: 'root'
})
export class TransactionService {
  private baseUrl = `${environment.apiUrl}/v1/transactions`;

  constructor(private http: HttpClient) {}

  createTransaction(request: Record<string, any>): Observable<Transaction> {
    return this.http.post<Transaction>(this.baseUrl, request);
  }

  getTransactionById(id: number): Observable<Transaction> {
    return this.http.get<Transaction>(`${this.baseUrl}/${id}`);
  }

  getAllTransactions(): Observable<Transaction[]> {
    return this.http.get<Transaction[]>(this.baseUrl);
  }

  getByFromWallet(fromWallet: string): Observable<Transaction[]> {
    return this.http.get<Transaction[]>(`${this.baseUrl}/from/${encodeURIComponent(fromWallet)}`);
  }

  getByToWallet(toWallet: string): Observable<Transaction[]> {
    return this.http.get<Transaction[]>(`${this.baseUrl}/to/${encodeURIComponent(toWallet)}`);
  }

  getByWallet(wallet: string): Observable<Transaction[]> {
    return this.http.get<Transaction[]>(`${this.baseUrl}/wallet/${encodeURIComponent(wallet)}`);
  }

  getByDateRange(start: string, end: string): Observable<Transaction[]> {
    const params = new HttpParams().set('start', start).set('end', end);
    return this.http.get<Transaction[]>(`${this.baseUrl}/date-range`, { params });
  }

  getAllOrderedByTimestamp(): Observable<Transaction[]> {
    return this.http.get<Transaction[]>(`${this.baseUrl}/ordered/timestamp`);
  }

  getAllOrderedByAmount(): Observable<Transaction[]> {
    return this.http.get<Transaction[]>(`${this.baseUrl}/ordered/amount`);
  }

  getPendingTransactions(): Observable<Transaction[]> {
    return this.http.get<Transaction[]>(`${this.baseUrl}/pending`);
  }

  getByValidatorNodeId(nodeId: number): Observable<Transaction[]> {
    return this.http.get<Transaction[]>(`${this.baseUrl}/validator/${nodeId}`);
  }

  getCreatedAfter(date: string): Observable<Transaction[]> {
    return this.http.get<Transaction[]>(`${this.baseUrl}/created-after`, {
      params: new HttpParams().set('date', date)
    });
  }

  getHighValueTransactions(threshold: string): Observable<Transaction[]> {
    return this.http.get<Transaction[]>(`${this.baseUrl}/high-value`, {
      params: new HttpParams().set('threshold', threshold)
    });
  }

  getTransactionsBetweenWallets(wallet1: string, wallet2: string): Observable<Transaction[]> {
    return this.http.get<Transaction[]>(`${this.baseUrl}/between-wallets`, {
      params: new HttpParams().set('wallet1', wallet1).set('wallet2', wallet2)
    });
  }

  updateStatus(id: number, status: TransactionStatus): Observable<Transaction> {
    return this.http.patch<Transaction>(`${this.baseUrl}/${id}/status`, null, {
      params: new HttpParams().set('status', status)
    });
  }

  countByWallet(wallet: string): Observable<number> {
    return this.http.get<number>(`${this.baseUrl}/stats/count/wallet/${encodeURIComponent(wallet)}`);
  }

  getTotalSentByWallet(wallet: string): Observable<number> {
    return this.http.get<number>(`${this.baseUrl}/stats/sent/${encodeURIComponent(wallet)}`);
  }

  getTotalReceivedByWallet(wallet: string): Observable<number> {
    return this.http.get<number>(`${this.baseUrl}/stats/received/${encodeURIComponent(wallet)}`);
  }

  getTotalFeesCollected(): Observable<number> {
    return this.http.get<number>(`${this.baseUrl}/stats/fees`);
  }

  getAverageTransactionAmount(): Observable<number> {
    return this.http.get<number>(`${this.baseUrl}/stats/average-amount`);
  }

  getByStatus(status: TransactionStatus): Observable<Transaction[]> {
    return this.http.get<Transaction[]>(`${this.baseUrl}/status/${status}`);
  }

  countByStatus(status: TransactionStatus): Observable<number> {
    return this.http.get<number>(`${this.baseUrl}/stats/count/status/${status}`);
  }
}
