import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ApiService } from '../../services/api.service';

@Component({
    selector: 'app-finance',
    standalone: true,
    imports: [CommonModule],
    templateUrl: './finance.component.html',
    styleUrl: './finance.component.scss'
})
export class FinanceComponent implements OnInit {
    activeTab: 'ratios' | 'etats' = 'ratios';

    ratiosLiquidite: any[] = [];
    ratiosSolvabilite: any[] = [];
    ratiosProfitabilite: any[] = [];

    constructor(private apiService: ApiService) { }

    ngOnInit() {
        this.apiService.getBusinesses().subscribe(businesses => {
            if (businesses && businesses.length > 0) {
                this.apiService.getFinanceRatios(businesses[0].id).subscribe((data: any) => {
                    this.ratiosLiquidite = data.ratiosLiquidite || [];
                    this.ratiosSolvabilite = data.ratiosSolvabilite || [];
                    this.ratiosProfitabilite = data.ratiosProfitabilite || [];
                });
            }
        });
    }

    setTab(tab: 'ratios' | 'etats') {
        this.activeTab = tab;
    }
}
