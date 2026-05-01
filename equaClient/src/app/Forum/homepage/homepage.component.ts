import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { ForumDiscussionComponent } from '../forum-discussion/forum-discussion.component';
import { ForumModerationComponent } from '../forum-moderation/forum-moderation.component';
import { ForumTopicsListComponent } from '../forum-topics-list/forum-topics-list.component';
import { ForumMessage, ForumModerationAction, ForumReport, ForumTopic } from '../models/forum.models';
import { ForumService } from '../services/forum.service';

@Component({
  selector: 'app-homepage',
  standalone: true,
  imports: [CommonModule, ForumTopicsListComponent, ForumDiscussionComponent, ForumModerationComponent],
  templateUrl: './homepage.component.html',
  styleUrl: './homepage.component.scss'
})
export class HomepageComponent {
  search = '';
  newTopicTitle = '';
  newTopicDescription = '';
  newMessage = '';
  gifUrl = '';
  reportReason = '';
  loading = false;
  errorMessage = '';
  moderationError = '';

  topics: ForumTopic[] = [];
  reports: ForumReport[] = [];
  selectedTopic: ForumTopic | null = null;

  constructor(private forumService: ForumService) {
    this.loadForumData();
  }

  get filteredTopics(): ForumTopic[] {
    const term = this.search.trim().toLowerCase();
    return this.topics.filter(topic => {
      if (!term) return true;
      return (
        topic.title.toLowerCase().includes(term) ||
        topic.description.toLowerCase().includes(term) ||
        topic.createdByUsername.toLowerCase().includes(term)
      );
    });
  }

  selectTopic(topic: ForumTopic): void {
    this.forumService.getTopicById(topic.topicId).subscribe({
      next: (topicDetails) => {
        this.selectedTopic = topicDetails;
        this.syncTopicInList(topicDetails);
      },
      error: () => {
        this.errorMessage = 'Unable to load messages for this topic.';
      }
    });
  }

  addMessage(): void {
    const value = this.newMessage.trim();
    const gif = this.normalizeGifUrl(this.gifUrl.trim());
    if ((!value && !gif) || !this.selectedTopic) return;
    this.forumService
      .addMessage(this.selectedTopic.topicId, {
        messageText: value,
        gifUrl: gif || undefined
      })
      .subscribe({
        next: () => {
          this.newMessage = '';
          this.gifUrl = '';
          this.selectTopic(this.selectedTopic as ForumTopic);
        },
        error: () => {
          this.errorMessage = 'Failed to publish the message.';
        }
      });
  }

  createTopic(): void {
    const title = this.newTopicTitle.trim();
    const description = this.newTopicDescription.trim();
    if (!title || !description) {
      this.errorMessage = 'Title and description are required to create a topic.';
      return;
    }
    this.forumService.createTopic({ title, description }).subscribe({
      next: (createdTopic) => {
        this.newTopicTitle = '';
        this.newTopicDescription = '';
        this.errorMessage = '';
        this.loadTopics();
        this.selectTopic(createdTopic);
      },
      error: (error: HttpErrorResponse) => {
        this.errorMessage = this.toUiErrorMessage(error, 'Unable to create topic.');
      }
    });
  }

  reportCurrentTopic(): void {
    if (!this.selectedTopic) return;
    const reason = this.reportReason.trim();
    if (!reason) {
      this.errorMessage = 'Please provide a report reason.';
      return;
    }
    this.forumService.reportTopic(this.selectedTopic.topicId, { reason }).subscribe({
      next: () => {
        this.reportReason = '';
        this.loadPendingReports();
      },
      error: (error: HttpErrorResponse) => {
        this.errorMessage = this.toUiErrorMessage(error, 'Unable to report this topic.');
      }
    });
  }

  reportMessage(messageId: number): void {
    const reason = this.reportReason.trim();
    if (!reason) {
      this.errorMessage = 'Please provide a report reason.';
      return;
    }
    this.forumService.reportMessage(messageId, { reason }).subscribe({
      next: () => {
        this.reportReason = '';
        this.loadPendingReports();
      },
      error: (error: HttpErrorResponse) => {
        this.errorMessage = this.toUiErrorMessage(error, 'Unable to report this message.');
      }
    });
  }

  moderateReport(report: ForumReport, action: ForumModerationAction): void {
    this.forumService.moderateReport(report.reportId, { action, note: `Action ${action}` }).subscribe({
      next: () => {
        this.loadPendingReports();
        this.loadTopics();
      },
      error: () => {
        this.moderationError = 'Moderation failed. Please verify ASSISTANT permissions.';
      }
    });
  }

  editCurrentTopic(): void {
    if (!this.selectedTopic) return;
    const nextTitle = window.prompt('New topic title', this.selectedTopic.title)?.trim();
    if (!nextTitle) return;
    const nextDescription = window.prompt('New topic description', this.selectedTopic.description)?.trim();
    if (!nextDescription) return;

    this.forumService
      .updateTopic(this.selectedTopic.topicId, { title: nextTitle, description: nextDescription })
      .subscribe({
        next: (updatedTopic) => {
          this.selectedTopic = updatedTopic;
          this.syncTopicInList(updatedTopic);
        },
        error: (error: HttpErrorResponse) => {
          this.errorMessage = this.toUiErrorMessage(error, 'Unable to update topic.');
        }
      });
  }

  deleteCurrentTopic(): void {
    if (!this.selectedTopic) return;
    if (!window.confirm('Delete this topic?')) return;

    const topicId = this.selectedTopic.topicId;
    this.forumService.deleteTopic(topicId).subscribe({
      next: () => {
        this.topics = this.topics.filter(t => t.topicId !== topicId);
        this.selectedTopic = this.topics[0] ?? null;
        if (this.selectedTopic) {
          this.selectTopic(this.selectedTopic);
        }
      },
      error: (error: HttpErrorResponse) => {
        this.errorMessage = this.toUiErrorMessage(error, 'Unable to delete topic.');
      }
    });
  }

  editMessage(message: ForumMessage): void {
    const nextText = window.prompt('Edit message', message.messageText)?.trim();
    if (!nextText || !this.selectedTopic) return;

    this.forumService.updateMessage(message.messageId, { messageText: nextText }).subscribe({
      next: () => {
        this.selectTopic(this.selectedTopic as ForumTopic);
      },
      error: (error: HttpErrorResponse) => {
        this.errorMessage = this.toUiErrorMessage(error, 'Unable to update message.');
      }
    });
  }

  deleteMessage(message: ForumMessage): void {
    if (!this.selectedTopic) return;
    if (!window.confirm('Delete this message?')) return;

    this.forumService.deleteMessage(message.messageId).subscribe({
      next: () => {
        this.selectTopic(this.selectedTopic as ForumTopic);
      },
      error: (error: HttpErrorResponse) => {
        this.errorMessage = this.toUiErrorMessage(error, 'Unable to delete message.');
      }
    });
  }

  private loadForumData(): void {
    this.loading = true;
    this.errorMessage = '';
    this.loadTopics(() => {
      this.loading = false;
    });
    this.loadPendingReports();
  }

  private loadTopics(onComplete?: () => void): void {
    this.forumService.getAllTopics().subscribe({
      next: (topics) => {
        this.topics = topics;
        if (!this.selectedTopic && topics.length > 0) {
          this.selectTopic(topics[0]);
        } else if (this.selectedTopic) {
          const updated = topics.find(t => t.topicId === this.selectedTopic?.topicId);
          if (updated) this.selectedTopic = updated;
        }
      },
      error: () => {
        this.errorMessage = 'Unable to load topics.';
      },
      complete: () => {
        onComplete?.();
      }
    });
  }

  private loadPendingReports(): void {
    this.moderationError = '';
    this.forumService.getPendingReports().subscribe({
      next: (reports) => {
        this.reports = reports;
      },
      error: (error: HttpErrorResponse) => {
        if (error.status === 401 || error.status === 403) {
          // Pending reports are for moderators; hide this error for non-assistant users.
          this.reports = [];
          return;
        }
        this.moderationError = this.toUiErrorMessage(error, 'Unable to load pending reports.');
      }
    });
  }

  private syncTopicInList(topicDetails: ForumTopic): void {
    const index = this.topics.findIndex(t => t.topicId === topicDetails.topicId);
    if (index === -1) return;
    this.topics = [
      ...this.topics.slice(0, index),
      topicDetails,
      ...this.topics.slice(index + 1)
    ];
  }

  private toUiErrorMessage(error: HttpErrorResponse, fallback: string): string {
    if (error.status === 401) {
      return 'You must be logged in to perform this action.';
    }
    if (error.status === 403) {
      return 'This action is not allowed for your role.';
    }
    if (error.status === 400 && error.error?.message) {
      return String(error.error.message);
    }
    if (typeof error.error === 'string' && error.error.trim()) {
      return error.error;
    }
    return fallback;
  }

  private normalizeGifUrl(input: string): string {
    if (!input) return '';

    // Keep direct image links as-is.
    if (/\.(gif|webp|png|jpg|jpeg)(\?.*)?$/i.test(input)) {
      return input;
    }

    // Convert common Giphy page URL to direct media URL.
    // Example: https://giphy.com/gifs/funny-cat-abc123 -> https://media.giphy.com/media/abc123/giphy.gif
    const giphyMatch = input.match(/giphy\.com\/gifs\/[^/]*-([a-zA-Z0-9]+)(?:$|[/?#])/i);
    if (giphyMatch?.[1]) {
      return `https://media.giphy.com/media/${giphyMatch[1]}/giphy.gif`;
    }

    // Convert common Tenor page URL to direct gif URL.
    // Example: https://tenor.com/view/...-12345678 -> https://media.tenor.com/12345678/tenor.gif
    const tenorMatch = input.match(/tenor\.com\/view\/[^/]*-([a-zA-Z0-9]+)(?:$|[/?#])/i);
    if (tenorMatch?.[1]) {
      return `https://media.tenor.com/${tenorMatch[1]}/tenor.gif`;
    }

    return input;
  }
}
