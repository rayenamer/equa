import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ValidatorPrediction } from '../../../models/ai-insights.model';

@Component({
    selector: 'app-ai-validator-prediction',
    standalone: true,
    imports: [CommonModule],
    templateUrl: './ai-validator-prediction.component.html',
    styleUrls: ['./ai-validator-prediction.component.scss']
})
export class AiValidatorPredictionComponent {
    @Input() prediction: ValidatorPrediction = {
        predictedNodeId: 104,
        location: 'New York, US',
        nodeType: 'VALIDATOR',
        reputation: 98.2,
        winProbability: 75,
        reasoning: 'Performances exceptionnelles et faible latence détectée.',
        allNodeOdds: [
            { nodeId: 104, location: 'New York, US', winChance: 75, reason: 'Latence < 10ms' },
            { nodeId: 102, location: 'Paris, FR', winChance: 15, reason: 'Uptime 99.9%' },
            { nodeId: 101, location: 'Tunis, TN', winChance: 10, reason: 'Stabilité réseau' }
        ]
    };
}
