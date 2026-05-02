import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ApiService } from '../../services/api.service';

@Component({
    selector: 'app-business-info',
    standalone: true,
    imports: [CommonModule],
    templateUrl: './info.component.html',
    styleUrl: './info.component.scss'
})
export class InfoComponent implements OnInit {
    businessData: any = null;
    isLoading = true;

    constructor(private apiService: ApiService) { }

    ngOnInit(): void {
        this.apiService.getBusinesses().subscribe({
            next: (businesses) => {
                if (businesses && businesses.length > 0) {
                    this.businessData = businesses[0];
                }
                this.isLoading = false;
            },
            error: () => {
                this.isLoading = false;
            }
        });
    }
}
