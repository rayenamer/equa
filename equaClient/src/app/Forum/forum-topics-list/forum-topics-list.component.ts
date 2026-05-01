import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { UiButtonComponent } from '../../components/atoms/ui-button/ui-button.component';
import { UiInputComponent } from '../../components/atoms/ui-input/ui-input.component';
import { ForumTopic } from '../models/forum.models';

@Component({
  selector: 'app-forum-topics-list',
  standalone: true,
  imports: [CommonModule, UiInputComponent, UiButtonComponent],
  templateUrl: './forum-topics-list.component.html',
  styleUrl: './forum-topics-list.component.scss'
})
export class ForumTopicsListComponent {
  @Input() topics: ForumTopic[] = [];
  @Input() selectedTopicId: number | null = null;
  @Input() search = '';
  @Input() newTopicTitle = '';
  @Input() newTopicDescription = '';
  showAddTopic = false;

  @Output() searchChange = new EventEmitter<string>();
  @Output() newTopicTitleChange = new EventEmitter<string>();
  @Output() newTopicDescriptionChange = new EventEmitter<string>();
  @Output() createTopic = new EventEmitter<void>();
  @Output() topicSelected = new EventEmitter<ForumTopic>();
}
