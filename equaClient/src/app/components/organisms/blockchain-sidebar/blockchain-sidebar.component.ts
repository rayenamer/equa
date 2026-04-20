import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { NavMenuItem } from '../../molecules/nav-menu/nav-menu.component';

@Component({
    selector: 'app-blockchain-sidebar',
    standalone: true,
    imports: [CommonModule, RouterLink, RouterLinkActive],
    templateUrl: './blockchain-sidebar.component.html',
    styleUrl: './blockchain-sidebar.component.scss'
})
export class BlockchainSidebarComponent {
    @Input() navItems: NavMenuItem[] = [];
}
