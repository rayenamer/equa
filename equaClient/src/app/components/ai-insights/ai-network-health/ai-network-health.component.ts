import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HealthScore } from '../../../BlockChain/models/ai-insights.model';
import { ApiService } from '../../../services/api.service';


@Component({
    selector: 'app-ai-network-health',
    standalone: true,
    imports: [CommonModule],
    templateUrl: './ai-network-health.component.html',
    styleUrls: ['./ai-network-health.component.scss']
})
export class AiNetworkHealthComponent {
    health: HealthScore | null = null;
    loading = true;

    constructor(private apiService: ApiService) { }

    ngOnInit() {
        this.apiService.getHealthScore().subscribe({
            next: (data) => {
                this.health = data;
                this.loading = false;
            },
            error: () => {
                this.loading = false;
            }
        });
    }

    getStatusColor(score: number) {
        if (score >= 90) return '#4ade80';
        if (score >= 70) return '#facc15';
        return '#f87171';
    }
}

