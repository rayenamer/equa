import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FinancialMarketService, AiMarketInsights } from '../financial-market.service';

@Component({
    selector: 'app-ai-market-insights',
    standalone: true,
    imports: [CommonModule],
    templateUrl: './ai-insights.component.html',
    styleUrl: './ai-insights.component.scss'
})
export class AiInsightsComponent {
    insights: AiMarketInsights | null = null;
    isAnalyzing = false;
    errorText = '';

    constructor(private service: FinancialMarketService) { }

    runAnalysis() {
        this.isAnalyzing = true;
        this.insights = null;
        this.errorText = '';

        this.service.getMarketInsights().subscribe({
            next: (res) => {
                this.isAnalyzing = false;
                this.insights = res;
            },
            error: (err) => {
                this.isAnalyzing = false;
                this.errorText = 'Error generating insights: ' + err.message;
            }
        });
    }

    formatText(text: string): string {
        if (!text) return '';
        return text.replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>');
    }
}
