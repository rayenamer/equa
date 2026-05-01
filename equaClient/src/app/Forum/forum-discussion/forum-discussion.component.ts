import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { UiButtonComponent } from '../../components/atoms/ui-button/ui-button.component';
import { UiInputComponent } from '../../components/atoms/ui-input/ui-input.component';
import { ForumMessage, ForumTopic } from '../models/forum.models';

@Component({
  selector: 'app-forum-discussion',
  standalone: true,
  imports: [CommonModule, UiInputComponent, UiButtonComponent],
  templateUrl: './forum-discussion.component.html',
  styleUrl: './forum-discussion.component.scss'
})
export class ForumDiscussionComponent {
  @Input() selectedTopic: ForumTopic | null = null;
  @Input() newMessage = '';
  @Input() gifUrl = '';
  @Input() reportReason = '';
  showComposer = false;

  @Output() newMessageChange = new EventEmitter<string>();
  @Output() gifUrlChange = new EventEmitter<string>();
  @Output() reportReasonChange = new EventEmitter<string>();
  @Output() sendMessage = new EventEmitter<void>();
  @Output() reportTopic = new EventEmitter<void>();
  @Output() reportMessage = new EventEmitter<ForumMessage>();
  @Output() editTopic = new EventEmitter<void>();
  @Output() deleteTopic = new EventEmitter<void>();
  @Output() editMessage = new EventEmitter<ForumMessage>();
  @Output() deleteMessage = new EventEmitter<ForumMessage>();

  onGifError(event: Event): void {
    const img = event.target as HTMLImageElement | null;
    if (img) {
      img.style.display = 'none';
    }
  }
}
