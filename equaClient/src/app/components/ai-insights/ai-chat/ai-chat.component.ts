import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

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
export class AiChatComponent {
    messages: ChatMessage[] = [
        {
            role: 'ai',
            content: 'Bonjour ! Je suis l\'Assistant IA d\'EQUA. Comment puis-je vous aider à explorer la blockchain aujourd\'hui ?',
            timestamp: new Date()
        }
    ];
    userInput = '';
    isTyping = false;

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

        // Simulate API call to /chat
        setTimeout(() => {
            this.isTyping = false;
            const aiMsg: ChatMessage = {
                role: 'ai',
                content: this.generateMockResponse(query),
                timestamp: new Date()
            };
            this.messages.push(aiMsg);
            this.scrollToBottom();
        }, 1500);
    }

    clearChat() {
        this.messages = [this.messages[0]];
        // Simulate DELETE /chat/{sessionId}
    }

    private generateMockResponse(query: string): string {
        const q = query.toLowerCase();
        if (q.includes('santé') || q.includes('health')) return 'Le score de santé actuel est de 98%. Tous les validateurs sont opérationnels.';
        if (q.includes('bloc') || q.includes('block')) return 'Le dernier bloc miné est le #2,456,891. Il contient 45 transactions.';
        if (q.includes('dinar')) return 'Votre solde actuel est de 45,280 TND. Le taux de conversion EQUA est stable.';
        return 'D\'après mon analyse de la blockchain EQUA, le réseau maintient une stabilité élevée avec un temps de bloc moyen de 1.2s.';
    }

    private scrollToBottom() {
        setTimeout(() => {
            const container = document.querySelector('.messages-container');
            if (container) container.scrollTop = container.scrollHeight;
        }, 100);
    }
}
