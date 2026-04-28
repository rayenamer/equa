import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
    selector: 'app-dinar-converter',
    standalone: true,
    imports: [CommonModule, FormsModule],
    templateUrl: './dinar-converter.component.html',
    styleUrl: './dinar-converter.component.scss'
})
export class DinarConverterComponent {
    @Input() maxAmount: number = 0;
    @Output() onConvertRequested = new EventEmitter<number>();

    amount: number | null = null;

    onAmountChange(val: any) {
        console.log('DinarConverter: amount changed', { val, maxAmount: this.maxAmount });
    }

    onConvert() {
        console.log('DinarConverter: onConvert()', { amount: this.amount, max: this.maxAmount });
        if (this.amount && this.amount > 0) {
            console.log('DinarConverter: emitting', this.amount);
            this.onConvertRequested.emit(Number(this.amount));
            this.amount = null;
        } else {
            console.warn('DinarConverter: conditions not met for conversion');
        }
    }
}
