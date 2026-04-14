import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LetterGlitchComponent } from '../../components/letter-glitch/letter-glitch.component';
import { StatCardComponent } from '../../components/molecules/stat-card/stat-card.component';
import { UiInputComponent } from '../../components/atoms/ui-input/ui-input.component';
import { UiButtonComponent } from '../../components/atoms/ui-button/ui-button.component';

@Component({
  selector: 'app-ai-insights',
  standalone: true,
  imports: [
    CommonModule,
    LetterGlitchComponent,
    StatCardComponent,
    UiInputComponent,
    UiButtonComponent
  ],
  templateUrl: './ai-insights.component.html',
  styleUrl: './ai-insights.component.scss'
})
export class AiInsightsComponent {
  aiStats = [
    { label: 'Score Santé Réseau', value: '94/100' },
    { label: 'Confiance Prédiction', value: '88%' },
    { label: 'Anomalies Détectées', value: '0' },
    { label: 'Contrats Analysés', value: '1,245' }
  ];

  messages = [
    { role: 'ai', text: 'Bonjour ! Je suis l\'IA EQUA. Comment puis-je vous aider avec l\'analyse de la blockchain aujourd\'hui ?' },
    { role: 'user', text: 'Peux-tu me donner le score de réputation du validateur Oracle ?' },
    { role: 'ai', text: 'Le validateur Oracle a un score de 99.2%. Il est actuellement très stable et performant.' }
  ];
}
