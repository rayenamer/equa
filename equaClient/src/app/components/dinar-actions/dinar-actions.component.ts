import { Component, EventEmitter, Output, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Html5QrcodeScanner, Html5QrcodeScanType } from 'html5-qrcode';

@Component({
    selector: 'app-dinar-actions',
    standalone: true,
    imports: [CommonModule, FormsModule],
    templateUrl: './dinar-actions.component.html',
    styleUrls: ['./dinar-actions.component.scss']
})
export class DinarActionsComponent implements OnDestroy {
    @Output() action = new EventEmitter<{ type: 'deposit' | 'withdraw', cardCode: string }>();

    cardCode: string = '';
    isScanning = false;
    private html5QrcodeScanner: Html5QrcodeScanner | null = null;

    onDeposit() {
        if (this.cardCode.trim().length > 0) {
            this.action.emit({ type: 'deposit', cardCode: this.cardCode.trim() });
            this.cardCode = '';
        }
    }

    startScan() {
        this.isScanning = true;
        setTimeout(() => {
            this.html5QrcodeScanner = new Html5QrcodeScanner(
                "reader",
                { fps: 10, qrbox: { width: 250, height: 250 }, supportedScanTypes: [Html5QrcodeScanType.SCAN_TYPE_CAMERA] },
                false
            );

            this.html5QrcodeScanner.render((decodedText: string) => {
                this.cardCode = decodedText;
                this.stopScan();
            }, (errorMessage: string) => {
                // Ignore empty scan errors
            });
        }, 100);
    }

    stopScan() {
        this.isScanning = false;
        if (this.html5QrcodeScanner) {
            this.html5QrcodeScanner.clear().catch(error => {
                console.error("Failed to clear html5QrcodeScanner.", error);
            });
            this.html5QrcodeScanner = null;
        }
    }

    ngOnDestroy() {
        this.stopScan();
    }

}
