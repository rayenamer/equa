import { Component, EventEmitter, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
    selector: 'app-dinar-actions',
    standalone: true,
    imports: [CommonModule, FormsModule],
    templateUrl: './dinar-actions.component.html',
    styleUrls: ['./dinar-actions.component.scss']
})
export class DinarActionsComponent {
    @Output() action = new EventEmitter<{ type: 'deposit' | 'withdraw', amount: number }>();

    depositAmount: number | null = null;
    withdrawAmount: number | null = null;

    onDeposit() {
        if (this.depositAmount && this.depositAmount > 0) {
            this.action.emit({ type: 'deposit', amount: this.depositAmount });
            this.depositAmount = null;
        }
    }

    onWithdraw() {
        if (this.withdrawAmount && this.withdrawAmount > 0) {
            this.action.emit({ type: 'withdraw', amount: this.withdrawAmount });
            this.withdrawAmount = null;
        }
    }
}
