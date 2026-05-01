import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import {
  CreateForumMessageRequest,
  CreateForumReportRequest,
  CreateForumTopicRequest,
  ForumMessage,
  ForumReport,
  ForumTopic,
  ModerateForumReportRequest,
  UpdateForumMessageRequest,
  UpdateForumTopicRequest
} from '../models/forum.models';

@Injectable({
  providedIn: 'root'
})
export class ForumService {
  private readonly apiUrl = `${environment.apiUrl}/forum`;

  constructor(private http: HttpClient) {}

  getAllTopics(): Observable<ForumTopic[]> {
    return this.http.get<ForumTopic[]>(`${this.apiUrl}/topics`);
  }

  getTopicById(topicId: number): Observable<ForumTopic> {
    return this.http.get<ForumTopic>(`${this.apiUrl}/topics/${topicId}`);
  }

  createTopic(payload: CreateForumTopicRequest): Observable<ForumTopic> {
    return this.http.post<ForumTopic>(`${this.apiUrl}/topics`, payload);
  }

  updateTopic(topicId: number, payload: UpdateForumTopicRequest): Observable<ForumTopic> {
    return this.http.put<ForumTopic>(`${this.apiUrl}/topics/${topicId}`, payload);
  }

  deleteTopic(topicId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/topics/${topicId}`);
  }

  addMessage(topicId: number, payload: CreateForumMessageRequest): Observable<ForumMessage> {
    return this.http.post<ForumMessage>(`${this.apiUrl}/topics/${topicId}/messages`, payload);
  }

  updateMessage(messageId: number, payload: UpdateForumMessageRequest): Observable<ForumMessage> {
    return this.http.put<ForumMessage>(`${this.apiUrl}/messages/${messageId}`, payload);
  }

  deleteMessage(messageId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/messages/${messageId}`);
  }

  reportTopic(topicId: number, payload: CreateForumReportRequest): Observable<ForumReport> {
    return this.http.post<ForumReport>(`${this.apiUrl}/topics/${topicId}/reports`, payload);
  }

  reportMessage(messageId: number, payload: CreateForumReportRequest): Observable<ForumReport> {
    return this.http.post<ForumReport>(`${this.apiUrl}/messages/${messageId}/reports`, payload);
  }

  getPendingReports(): Observable<ForumReport[]> {
    return this.http.get<ForumReport[]>(`${this.apiUrl}/reports/pending`);
  }

  moderateReport(reportId: number, payload: ModerateForumReportRequest): Observable<ForumReport> {
    return this.http.post<ForumReport>(`${this.apiUrl}/reports/${reportId}/moderate`, payload);
  }
}
