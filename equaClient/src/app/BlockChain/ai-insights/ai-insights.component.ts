import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AiNetworkHealthComponent } from '../../components/ai-insights/ai-network-health/ai-network-health.component';
import { AiValidatorPredictionComponent } from '../../components/ai-insights/ai-validator-prediction/ai-validator-prediction.component';
import { AiChatComponent } from '../../components/ai-insights/ai-chat/ai-chat.component';
import { AiAnalyzerComponent } from '../../components/ai-insights/ai-analyzer/ai-analyzer.component';
import { ApiService } from '../../services/api.service';


@Component({
  selector: 'app-ai-insights',
  standalone: true,
  imports: [
    CommonModule,
    AiNetworkHealthComponent,
    AiValidatorPredictionComponent,
    AiChatComponent,
    AiAnalyzerComponent
  ],
  templateUrl: './ai-insights.component.html',
  styleUrl: './ai-insights.component.scss'
})
export class AiInsightsComponent {
  analysisText = 'Cliquez sur le bouton pour lancer une analyse complète du réseau...';
  isAnalyzing = false;

  constructor(private apiService: ApiService) { }

  runAnalysis() {
    this.isAnalyzing = true;
    this.analysisText = '';

    this.apiService.analyzeBlockchain().subscribe({
      next: (response) => {
        this.isAnalyzing = false;
        this.analysisText = response.analysis;
      },
      error: (error) => {
        this.isAnalyzing = false;
        this.analysisText = 'Erreur lors de l\'analyse: ' + error.message;
      }
    });
  }
}

