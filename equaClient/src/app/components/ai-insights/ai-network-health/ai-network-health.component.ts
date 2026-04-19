import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HealthScore } from '../../../models/ai-insights.model';

@Component({
    selector: 'app-ai-network-health',
    standalone: true,
    imports: [CommonModule],
    templateUrl: './ai-network-health.component.html',
    styleUrls: ['./ai-network-health.component.scss']
})
export class AiNetworkHealthComponent {
    @Input() health: HealthScore = {
        decentralizationScore: 85,
        decentralizationExplanation: 'Bonne répartition géographique des nœuds.',
        activityScore: 92,
        activityExplanation: 'Volume de transactions élevé et stable.',
        nodeDiversityScore: 78,
        nodeDiversityExplanation: 'Besoin de plus de types de nœuds archives.',
        overallScore: 85,
        overallExplanation: 'Le réseau est robuste avec une marge de progression sur la diversité.'
    };

    getStatusColor(score: number) {
        if (score >= 90) return '#4ade80';
        if (score >= 70) return '#facc15';
        return '#f87171';
    }
}
