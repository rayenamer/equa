import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterOutlet, RouterLink, RouterLinkActive } from '@angular/router';
import { BlockchainSidebarComponent } from '../../components/organisms/blockchain-sidebar/blockchain-sidebar.component';
import { NavMenuItem } from '../../components/molecules/nav-menu/nav-menu.component';
import { BusinessModeService } from '../../services/business-mode.service';
import { Router } from '@angular/router';

@Component({
    selector: 'app-business-homepage',
    standalone: true,
    imports: [CommonModule, RouterOutlet, RouterLink, RouterLinkActive, BlockchainSidebarComponent],
    templateUrl: './homepage.component.html',
    styleUrl: './homepage.component.scss'
})
export class BusinessHomepageComponent {
    constructor(
        public businessModeService: BusinessModeService,
        private router: Router
    ) { }

    navItems: NavMenuItem[] = [
        { label: 'Mouvements', sectionId: '/business/mouvements' },
        { label: 'Finance', sectionId: '/business/finance' }
    ];

    /** Switch back to individual mode and navigate to user area */
    switchToIndividual(): void {
        this.businessModeService.setMode('individual');
        this.router.navigate(['/user/dashboard']);
    }
}
