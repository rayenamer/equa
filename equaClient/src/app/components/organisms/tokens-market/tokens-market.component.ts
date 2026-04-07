import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { TokensGridComponent } from '../../tokens-grid/tokens-grid.component';

@Component({
  selector: 'app-tokens-market',
  standalone: true,
  imports: [TokensGridComponent],
  templateUrl: './tokens-market.component.html',
  styleUrl: './tokens-market.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class TokensMarketComponent {
  /** Section heading for token market block. */
  @Input() title = '';
}
