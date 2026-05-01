import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { ForumModerationAction, ForumReport } from '../models/forum.models';

@Component({
  selector: 'app-forum-moderation',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './forum-moderation.component.html',
  styleUrl: './forum-moderation.component.scss'
})
export class ForumModerationComponent {
  @Input() reports: ForumReport[] = [];
  @Output() moderate = new EventEmitter<{ report: ForumReport; action: ForumModerationAction }>();
}
