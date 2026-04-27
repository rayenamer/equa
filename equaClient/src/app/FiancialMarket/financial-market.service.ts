import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import {
    AssetResponseFinancial,
    AssetRequestFinancial,
    PriceHistoryResponseFinancial,
    PortfolioResponseFinancial,
    TransactionResponseFinancial,
    TradeRequestFinancial,
    TradeResponseFinancial,
    AssetMarketSummaryResponse
} from './models/financial-market.models';

@Injectable({
    providedIn: 'root'
})
export class FinancialMarketService {
    private apiUrl = 'http://localhost:8081/api/financial';

    constructor(private http: HttpClient) { }

    // Assets
    getAllAssets(): Observable<AssetResponseFinancial[]> {
        return this.http.get<AssetResponseFinancial[]>(`${this.apiUrl}/assets`);
    }

    getAssetById(id: number): Observable<AssetResponseFinancial> {
        return this.http.get<AssetResponseFinancial>(`${this.apiUrl}/assets/${id}`);
    }

    createAsset(req: AssetRequestFinancial): Observable<AssetResponseFinancial> {
        return this.http.post<AssetResponseFinancial>(`${this.apiUrl}/assets`, req);
    }

    getPriceHistory(assetId: number): Observable<PriceHistoryResponseFinancial[]> {
        return this.http.get<PriceHistoryResponseFinancial[]>(`${this.apiUrl}/assets/${assetId}/price-history`);
    }

    getAggregatedPriceHistory(): Observable<PriceHistoryResponseFinancial[]> {
        return this.http.get<PriceHistoryResponseFinancial[]>(`${this.apiUrl}/assets/aggregated`);
    }

    getMarketSummary(): Observable<AssetMarketSummaryResponse> {
        return this.http.get<AssetMarketSummaryResponse>(`${this.apiUrl}/assets/market-summary`);
    }

    // Portfolio
    getPortfolio(): Observable<PortfolioResponseFinancial> {
        return this.http.get<PortfolioResponseFinancial>(`${this.apiUrl}/portfolio`);
    }

    getTransactions(): Observable<TransactionResponseFinancial[]> {
        return this.http.get<TransactionResponseFinancial[]>(`${this.apiUrl}/transactions`);
    }

    // Trade
    buyAsset(req: TradeRequestFinancial): Observable<TradeResponseFinancial> {
        return this.http.post<TradeResponseFinancial>(`${this.apiUrl}/trade/buy`, req);
    }

    sellAsset(req: TradeRequestFinancial): Observable<TradeResponseFinancial> {
        return this.http.post<TradeResponseFinancial>(`${this.apiUrl}/trade/sell`, req);
    }
}
