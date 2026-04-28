import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../../services/api.service';

interface ChatMessage {
    role: 'user' | 'ai';
    content: string;
    timestamp: Date;
}

@Component({
    selector: 'app-ai-chat',
    standalone: true,
    imports: [CommonModule, FormsModule],
    templateUrl: './ai-chat.component.html',
    styleUrls: ['./ai-chat.component.scss']
})
export class AiChatComponent implements OnInit {

    messages: ChatMessage[] = [
        {
            role: 'ai',
            content: 'Bonjour ! Je suis l\'Assistant IA d\'EQUA. Comment puis-je vous aider à explorer la blockchain aujourd\'hui ?',
            timestamp: new Date()
        }
    ];
    userInput = '';
    isTyping = false;
    sessionId = '';

    constructor(private apiService: ApiService) { }

    ngOnInit() {
        this.sessionId = 'session-' + Math.random().toString(36).substring(2, 9);
    }


    sendMessage() {
        if (!this.userInput.trim()) return;

        const userMsg: ChatMessage = {
            role: 'user',
            content: this.userInput,
            timestamp: new Date()
        };
        this.messages.push(userMsg);
        const query = this.userInput;
        this.userInput = '';
        this.isTyping = true;

        this.apiService.chatWithBlockchain({
            sessionId: this.sessionId,
            message: query
        }).subscribe({
            next: (data) => {
                this.isTyping = false;
                const aiMsg: ChatMessage = {
                    role: 'ai',
                    content: data.response,
                    timestamp: new Date()
                };
                this.messages.push(aiMsg);
                this.scrollToBottom();
            },
            error: (err) => {
                this.isTyping = false;
                const aiMsg: ChatMessage = {
                    role: 'ai',
                    content: 'Désolé, je rencontre des difficultés techniques : ' + err.message,
                    timestamp: new Date()
                };
                this.messages.push(aiMsg);
                this.scrollToBottom();
            }
        });
    }

    clearChat() {
        this.apiService.clearChatSession(this.sessionId).subscribe({
            next: () => {
                this.messages = [this.messages[0]];
            }
        });
    }


    private scrollToBottom() {
        setTimeout(() => {
            const container = document.querySelector('.messages-container');
            if (container) container.scrollTop = container.scrollHeight;
        }, 100);
    }
}
