import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Block } from '../models/block.model';

export interface BlockStats {
  totalBlocks?: number;
  averageSize?: number;
  latestBlockTime?: string;
  [key: string]: any;
}

@Injectable({
  providedIn: 'root'
})
export class BlockService {
  private baseUrl = `${environment.apiUrl}/v1/blocks`;

  constructor(private http: HttpClient) {}

  getAllBlocks(): Observable<Block[]> {
    return this.http.get<Block[]>(this.baseUrl);
  }

  getBlockById(id: number): Observable<Block> {
    return this.http.get<Block>(`${this.baseUrl}/${id}`);
  }

  getBlockByHash(blockHash: string): Observable<Block> {
    return this.http.get<Block>(`${this.baseUrl}/hash/${encodeURIComponent(blockHash)}`);
  }

  getLatestBlock(): Observable<Block> {
    return this.http.get<Block>(`${this.baseUrl}/latest`);
  }

  getGenesisBlock(): Observable<Block> {
    return this.http.get<Block>(`${this.baseUrl}/genesis`);
  }

  getBlockchainStats(): Observable<BlockStats> {
    return this.http.get<BlockStats>(`${this.baseUrl}/stats`);
  }

  blockExists(blockHash: string): Observable<boolean> {
    return this.http.get<boolean>(`${this.baseUrl}/exists/${encodeURIComponent(blockHash)}`);
  }

  searchBlocks(filters: Record<string, any>): Observable<Block[]> {
    return this.http.post<Block[]>(`${this.baseUrl}/search`, filters);
  }

  createBlock(request: Record<string, any>): Observable<Block> {
    return this.http.post<Block>(this.baseUrl, request);
  }
}
