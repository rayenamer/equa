import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterOutlet, RouterLink, RouterLinkActive } from '@angular/router';
import { BlockchainSidebarComponent } from '../../components/organisms/blockchain-sidebar/blockchain-sidebar.component';
import { NavMenuItem } from '../../components/molecules/nav-menu/nav-menu.component';
import { BusinessModeService } from '../../services/business-mode.service';
import { Router } from '@angular/router';
import { ApiService } from '../../services/api.service';
import { FormsModule } from '@angular/forms';

@Component({
    selector: 'app-business-homepage',
    standalone: true,
    imports: [CommonModule, RouterOutlet, RouterLink, RouterLinkActive, BlockchainSidebarComponent, FormsModule],
    templateUrl: './homepage.component.html',
    styleUrl: './homepage.component.scss'
})
export class BusinessHomepageComponent implements OnInit {
    noBusiness = false;
    isLoading = true;
    newBusinessForm = {
        name: '',
        industry: '',
        registrationNumber: ''
    };

    constructor(
        public businessModeService: BusinessModeService,
        private router: Router,
        private apiService: ApiService
    ) { }

    ngOnInit(): void {
        this.apiService.getBusinesses().subscribe({
            next: (businesses) => {
                this.isLoading = false;
                if (!businesses || businesses.length === 0) {
                    this.noBusiness = true;
                }
            },
            error: () => {
                this.isLoading = false;
                this.noBusiness = true; // ensure fallback shows form on failures
            }
        });
    }

    createFirstBusiness(): void {
        if (!this.newBusinessForm.name) return;
        this.isLoading = true;
        this.apiService.createBusiness(this.newBusinessForm).subscribe({
            next: () => {
                this.noBusiness = false;
                this.isLoading = false;
                // Force reload of current component to propagate data
                const currentUrl = this.router.url;
                this.router.navigateByUrl('/', { skipLocationChange: true }).then(() => {
                    this.router.navigate([currentUrl]);
                });
            },
            error: () => this.isLoading = false
        });
    }

    navItems: NavMenuItem[] = [
        { label: 'Gérer', type: 'header' },
        { label: 'Informations', sectionId: '/business/info' },
        { label: 'Mouvements', sectionId: '/business/mouvements' },
        { label: 'Finance', sectionId: '/business/finance' },
        { label: 'Guide', type: 'header' },
        { label: 'Comment ça marche', sectionId: '/business/how-it-works' },
    ];

    /** Switch back to individual mode and navigate to user area */
    switchToIndividual(): void {
        this.businessModeService.setMode('individual');
        this.router.navigate(['/user/dashboard']);
    }
}
