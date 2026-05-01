export interface ForumMessage {
  messageId: number;
  topicId: number;
  authorId: number;
  authorUsername: string;
  messageText: string;
  createdAt: string;
  gifUrl?: string | null;
  hidden: boolean;
}

export interface ForumTopic {
  topicId: number;
  title: string;
  description: string;
  createdById: number;
  createdByUsername: string;
  createdAt: string;
  updatedAt: string;
  hidden: boolean;
  messages: ForumMessage[];
}

export interface ForumReport {
  reportId: number;
  targetType: 'TOPIC' | 'MESSAGE';
  topicId?: number | null;
  messageId?: number | null;
  reporterId: number;
  reporterUsername: string;
  reason: string;
  status: 'PENDING' | 'RESOLVED' | 'REJECTED';
  autoHidden: boolean;
  createdAt: string;
  moderatorId?: number | null;
  moderatorUsername?: string | null;
  moderatedAt?: string | null;
  moderationNote?: string | null;
}

export type ForumModerationAction = 'HIDE' | 'IGNORE';

export interface CreateForumTopicRequest {
  title: string;
  description: string;
}

export interface CreateForumMessageRequest {
  messageText: string;
  gifUrl?: string;
}

export interface UpdateForumTopicRequest {
  title: string;
  description: string;
}

export interface UpdateForumMessageRequest {
  messageText: string;
}

export interface CreateForumReportRequest {
  reason: string;
}

export interface ModerateForumReportRequest {
  action: ForumModerationAction;
  note?: string;
}
