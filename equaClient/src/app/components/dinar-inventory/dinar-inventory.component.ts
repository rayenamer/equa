import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Dinar } from '../../models/dinar-wallet.model';

@Component({
    selector: 'app-dinar-inventory',
    standalone: true,
    imports: [CommonModule],
    templateUrl: './dinar-inventory.component.html',
    styleUrls: ['./dinar-inventory.component.scss']
})
export class DinarInventoryComponent {
    @Input() dinars: Dinar[] = [];
}
