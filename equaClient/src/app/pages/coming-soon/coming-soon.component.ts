import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';

@Component({
    selector: 'app-coming-soon',
    standalone: true,
    imports: [CommonModule, RouterLink],
    templateUrl: './coming-soon.component.html',
    styleUrl: './coming-soon.component.scss'
})
export class ComingSoonComponent implements OnInit, OnDestroy {
    days: string = '00';
    hours: string = '00';
    minutes: string = '00';
    seconds: string = '00';
    private timer: any;

    features = [
        { title: 'Gouvernance DAO', icon: 'pi-users', desc: 'Votez sur les futures évolutions de l\'écosystème.' },
        { title: 'Récompenses de Staking', icon: 'pi-percentage', desc: 'Gagnez des intérêts en sécurisant le réseau EQUA.' },
        { title: 'Place de Marché NFT', icon: 'pi-images', desc: 'Tokenisez vos biens immobiliers et objets de collection.' }
    ];

    ngOnInit() {
        this.startCountdown();
    }

    ngOnDestroy() {
        if (this.timer) {
            clearInterval(this.timer);
        }
    }

    private startCountdown() {
        // Target: 2 weeks from now
        const targetDate = new Date();
        targetDate.setDate(targetDate.getDate() + 14);

        this.updateTime(targetDate);

        this.timer = setInterval(() => {
            this.updateTime(targetDate);
        }, 1000);
    }

    private updateTime(targetDate: Date) {
        const now = new Date().getTime();
        const distance = targetDate.getTime() - now;

        if (distance < 0) {
            this.days = this.hours = this.minutes = this.seconds = '00';
            if (this.timer) clearInterval(this.timer);
            return;
        }

        const d = Math.floor(distance / (1000 * 60 * 60 * 24));
        const h = Math.floor((distance % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
        const m = Math.floor((distance % (1000 * 60 * 60)) / (1000 * 60));
        const s = Math.floor((distance % (1000 * 60)) / 1000);

        this.days = d < 10 ? '0' + d : d.toString();
        this.hours = h < 10 ? '0' + h : h.toString();
        this.minutes = m < 10 ? '0' + m : m.toString();
        this.seconds = s < 10 ? '0' + s : s.toString();
    }

    onNotifySubmit(email: string) {
        if (email) {
            alert('Merci ! Nous vous tiendrons informé du lancement.');
        }
    }
}
