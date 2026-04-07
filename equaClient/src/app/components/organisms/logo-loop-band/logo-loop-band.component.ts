import { ChangeDetectionStrategy, Component } from '@angular/core';
import { LogoLoopComponent } from '../../logo-loop/logo-loop.component';

@Component({
  selector: 'app-logo-loop-band',
  standalone: true,
  imports: [LogoLoopComponent],
  templateUrl: './logo-loop-band.component.html',
  styleUrl: './logo-loop-band.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class LogoLoopBandComponent {}
