import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AiNetworkHealthComponent } from '../../components/ai-insights/ai-network-health/ai-network-health.component';
import { AiValidatorPredictionComponent } from '../../components/ai-insights/ai-validator-prediction/ai-validator-prediction.component';
import { AiChatComponent } from '../../components/ai-insights/ai-chat/ai-chat.component';
import { AiAnalyzerComponent } from '../../components/ai-insights/ai-analyzer/ai-analyzer.component';

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

  runAnalysis() {
    this.isAnalyzing = true;
    this.analysisText = '';

    // Simulate API call to /analyze
    setTimeout(() => {
      this.isAnalyzing = false;
      this.analysisText = `--- ANALYSE RÉSEAU EQUA ---
Statut Global: OPTIMAL
Détection d'anomalies: Aucune (0%)
Performance des Validateurs: Constante (+2.4% vs 24h)
Recommandation: Le réseau est prêt pour une augmentation de charge de 15%. La répartition de la réputation est équilibrée.`;
    }, 2000);
  }
}
