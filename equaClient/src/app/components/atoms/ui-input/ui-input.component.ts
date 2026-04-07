import { ChangeDetectionStrategy, Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'app-ui-input',
  standalone: true,
  templateUrl: './ui-input.component.html',
  styleUrl: './ui-input.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class UiInputComponent {
  /** Native input type. */
  @Input() type: 'text' | 'email' | 'password' = 'text';
  /** Placeholder text shown when value is empty. */
  @Input() placeholder = '';
  /** Form control name attribute. */
  @Input() name = '';
  /** Current textual value. */
  @Input() value = '';
  /** Required flag passed to the native input. */
  @Input() required = false;

  /** Emits on every input value update. */
  @Output() valueChange = new EventEmitter<string>();

  onInput(event: Event): void {
    const nextValue = (event.target as HTMLInputElement).value;
    this.valueChange.emit(nextValue);
  }
}
