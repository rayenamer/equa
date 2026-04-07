import { ChangeDetectionStrategy, Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UiButtonComponent } from '../../atoms/ui-button/ui-button.component';
import { UiInputComponent } from '../../atoms/ui-input/ui-input.component';

export interface FooterLinkGroup {
  title: string;
  links: { label: string; href: string }[];
}

@Component({
  selector: 'app-site-footer',
  standalone: true,
  imports: [CommonModule, UiButtonComponent, UiInputComponent],
  templateUrl: './site-footer.component.html',
  styleUrl: './site-footer.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class SiteFooterComponent {
  /** Brand label displayed in footer. */
  @Input() brand = 'EQUA';
  /** Footer tagline under brand. */
  @Input() tagline = 'Finance Without Barriers';
  /** Social links list. */
  @Input() socialLinks: { label: string; href: string; ariaLabel: string }[] = [];
  /** Footer link columns. */
  @Input() linkGroups: FooterLinkGroup[] = [];
  /** Newsletter section title. */
  @Input() newsletterTitle = 'Restez informe';
  /** Newsletter helper text. */
  @Input() newsletterText = '';
  /** Newsletter field value. */
  @Input() newsletterEmail = '';
  /** Copyright year. */
  @Input() currentYear = new Date().getFullYear();

  /** Emits newsletter email changes. */
  @Output() newsletterEmailChange = new EventEmitter<string>();
  /** Emits when the newsletter form is submitted. */
  @Output() newsletterSubmit = new EventEmitter<Event>();
}
