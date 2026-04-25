import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DinarBalanceCardComponent } from '../dinar-balance-card/dinar-balance-card.component';

@Component({
    selector: 'app-wallet-card',
    standalone: true,
    imports: [CommonModule, DinarBalanceCardComponent],
    templateUrl: './wallet-card.component.html',
    styleUrl: './wallet-card.component.scss'
})
export class WalletCardComponent {
    @Input() title: string = '';
    @Input() exists: boolean = false;

    // Empty state inputs
    @Input() emptyTitle: string = '';
    @Input() emptyDesc: string = '';
    @Input() btnText: string = '';

    // Balance card inputs
    @Input() balance: number = 0;
    @Input() walletId: string = '';
    @Input() subValue: number = 0;
    @Input() currency: string = 'TND';
    @Input() subLabel: string = 'Unités en main';

    @Output() create = new EventEmitter<void>();

    onCreate() {
        this.create.emit();
    }
}
