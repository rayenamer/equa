import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';

@Component({
    selector: 'app-business-how-it-works',
    standalone: true,
    imports: [CommonModule, RouterLink],
    templateUrl: './how-it-works.component.html',
    styleUrl: './how-it-works.component.scss'
})
export class BusinessHowItWorksComponent {
    steps = [
        {
            number: '01',
            title: 'Gérez votre entreprise',
            body: 'Accédez à une vue complète de votre activité. Vos transactions effectuées via la plateforme EQUA sont enregistrées automatiquement comme mouvements comptables.',
            icon: 'M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6'
        },
        {
            number: '02',
            title: 'Classifiez vos mouvements',
            body: 'Les mouvements entrants et sortants non classifiés apparaissent dans une section dédiée. Assignez-leur un compte comptable et une catégorie analytique en un clic.',
            icon: 'M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2m-6 9l2 2 4-4'
        },
        {
            number: '03',
            title: 'Ajoutez manuellement',
            body: 'Enregistrez tout mouvement comptable manuellement : libellé, type (entrant/sortant), compte, catégorie et montant. Idéal pour les opérations en dehors de la plateforme.',
            icon: 'M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z'
        },
        {
            number: '04',
            title: 'Prenez des décisions éclairées',
            body: 'Consultez vos ratios financiers (liquidité, solvabilité, rentabilité) calculés automatiquement depuis vos mouvements. Votre compte de résultat est généré en temps réel.',
            icon: 'M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z'
        }
    ];
}
