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
  showGifPicker = false;

  readonly availableGifs = [
    'https://media.tenor.com/JGp00a_5sjsAAAAM/the-office-michael-scott.gif',
    'https://media.tenor.com/kQA86PqyXZQAAAAj/small-dancing-white-cat-dance-funny.gif',
    'https://media.tenor.com/Bav2QWeveKgAAAAj/best-banana-cat.gif',
    'https://media.tenor.com/g485TTq4thoAAAAj/cat-cat-dance.gif',
    'https://media3.giphy.com/media/v1.Y2lkPTc5MGI3NjExeTYweW4waWN0YWs2Y3d3YjFqYmlvc3phM2NjNjgzdGZiNWN6cHFjNSZlcD12MV9naWZzX3NlYXJjaCZjdD1n/wJ2PzfofCXw1q/200.webp',
    'https://media0.giphy.com/media/v1.Y2lkPTc5MGI3NjExMWZ2bWVtbWE0YmsyMzNrODN5a2RvaHlmaHh2Y2c5YWc4OTQ1ZDFraSZlcD12MV9naWZzX3NlYXJjaCZjdD1n/BYoRqTmcgzHcL9TCy1/giphy.webp',
    'https://media4.giphy.com/media/v1.Y2lkPTc5MGI3NjExanBrd3pndHVvODJ3YnRpY2JoMDlzeHNqdWEzYWc2cjJsYTh6d3kwZCZlcD12MV9naWZzX3NlYXJjaCZjdD1n/JpG2A9P3dPHXaTYrwu/200.webp',
    'https://media1.giphy.com/media/v1.Y2lkPTc5MGI3NjExanBrd3pndHVvODJ3YnRpY2JoMDlzeHNqdWEzYWc2cjJsYTh6d3kwZCZlcD12MV9naWZzX3NlYXJjaCZjdD1n/xTiTnqUxyWbsAXq7Ju/giphy.webp',
    'https://media2.giphy.com/media/v1.Y2lkPTc5MGI3NjExbTEyeG8ycXlmOGR6MWtnOWNieTdnbHcwdGxid2F2ZXBuMm5xMTM0OCZlcD12MV9pbnRlcm5hbF9naWZfYnlfaWQmY3Q9Zw/12Eo7WogCAoj84/giphy.gif'
  ];

  selectGif(url: string): void {
    this.gifUrlChange.emit(url);
    this.showGifPicker = false;
  }

  clearGif(): void {
    this.gifUrlChange.emit('');
  }

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
