import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
    selector: 'app-node-stats',
    standalone: true,
    imports: [CommonModule],
    templateUrl: './node-stats.component.html',
    styleUrls: ['./node-stats.component.scss']
})
export class NodeStatsComponent {
    @Input() totalNodes: number = 124;
    @Input() onlineNodes: number = 118;
    @Input() avgReputation: number = 94.2;
    @Input() totalStake: string = '1.2M EQUA';
}
