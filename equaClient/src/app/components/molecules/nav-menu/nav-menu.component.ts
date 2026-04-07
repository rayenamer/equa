import { ChangeDetectionStrategy, Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';

export interface NavMenuItem {
  label: string;
  sectionId: string;
}

@Component({
  selector: 'app-nav-menu',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './nav-menu.component.html',
  styleUrl: './nav-menu.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class NavMenuComponent {
  /** Navigation items rendered in order. */
  @Input() items: NavMenuItem[] = [];
  /** Controls mobile expanded state. */
  @Input() open = false;

  /** Emits with the destination section id when an item is clicked. */
  @Output() itemClick = new EventEmitter<string>();

  onItemClick(event: Event, sectionId: string): void {
    event.preventDefault();
    this.itemClick.emit(sectionId);
  }
}
