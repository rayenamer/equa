import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
    selector: 'app-ai-analyzer',
    standalone: true,
    imports: [CommonModule],
    templateUrl: './ai-analyzer.component.html',
    styleUrls: ['./ai-analyzer.component.scss']
})
export class AiAnalyzerComponent {
    @Input() analysisText: string = 'Analyse en cours...';
    @Input() isAnalyzing: boolean = false;
}
