import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
    selector: 'app-dinar-balance-card',
    standalone: true,
    imports: [CommonModule],
    templateUrl: './dinar-balance-card.component.html',
    styleUrls: ['./dinar-balance-card.component.scss']
})
export class DinarBalanceCardComponent {
    @Input() balance: number = 0;
    @Input() walletId: string = 'Non créé';
    @Input() dinarCount: number = 0;
    @Input() title: string = 'MON PORTEFEUILLE DINAR';
    @Input() currency: string = 'TND';
    @Input() subLabel: string = 'Unités en main';
}
