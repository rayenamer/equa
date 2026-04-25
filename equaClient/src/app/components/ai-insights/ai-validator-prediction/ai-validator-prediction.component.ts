import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ValidatorPrediction } from '../../../BlockChain/models/ai-insights.model';
import { ApiService } from '../../../services/api.service';

@Component({
    selector: 'app-ai-validator-prediction',
    standalone: true,
    imports: [CommonModule],
    templateUrl: './ai-validator-prediction.component.html',
    styleUrls: ['./ai-validator-prediction.component.scss']
})
export class AiValidatorPredictionComponent implements OnInit {
    prediction: ValidatorPrediction | null = null;
    loading = true;

    constructor(private apiService: ApiService) { }

    ngOnInit() {
        this.apiService.predictNextValidator().subscribe({
            next: (data) => {
                this.prediction = data;
                this.loading = false;
            },
            error: () => {
                this.loading = false;
            }
        });
    }
}

