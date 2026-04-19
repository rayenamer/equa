import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';

interface Node {
    id: number;
    name: string;
    cooldown: number;
    maxCooldown: number;
    lastGuess: string;
    isGuessing: boolean;
    wins: number;
    isWinner: boolean;
}

interface Transaction {
    id: string;
    letter: string;
    timestamp: Date;
}

@Component({
    selector: 'app-consensus-explanation',
    standalone: true,
    imports: [CommonModule],
    templateUrl: './consensus-explanation.component.html',
    styleUrl: './consensus-explanation.component.scss'
})
export class ConsensusExplanationComponent implements OnInit, OnDestroy {
    nodes: Node[] = [
        { id: 1, name: 'Alpha Node', cooldown: 0, maxCooldown: 3000, lastGuess: '', isGuessing: false, wins: 0, isWinner: false },
        { id: 2, name: 'Beta Node', cooldown: 0, maxCooldown: 3000, lastGuess: '', isGuessing: false, wins: 0, isWinner: false },
        { id: 3, name: 'Gamma Node', cooldown: 0, maxCooldown: 3000, lastGuess: '', isGuessing: false, wins: 0, isWinner: false },
        { id: 4, name: 'Delta Node', cooldown: 0, maxCooldown: 3000, lastGuess: '', isGuessing: false, wins: 0, isWinner: false },
    ];

    targetLetter: string = 'E';
    alphabet: string = 'ABCDEF';

    pendingTransactions: Transaction[] = [];
    currentBlock: Transaction[] = [];
    finalizedBlocks: any[] = [];

    isRaceActive: boolean = false;
    winnerNodeId: number | null = null;
    blockCapacity: number = 5;
    blockCapacityList: number[] = [0, 1, 2, 3, 4]; // For template rendering

    private simulationInterval: any;

    ngOnInit() {
        this.blockCapacity = Math.floor(Math.random() * 8) + 3;
        this.blockCapacityList = Array.from({ length: this.blockCapacity }, (_, i) => i);
        this.startSimulation();
    }

    ngOnDestroy() {
        this.stopSimulation();
    }

    startSimulation() {
        if (this.simulationInterval) return;
        this.isRaceActive = true;
        this.simulationInterval = setInterval(() => this.tick(), 100);
    }

    stopSimulation() {
        if (this.simulationInterval) {
            clearInterval(this.simulationInterval);
            this.simulationInterval = null;
        }
        this.isRaceActive = false;
    }

    tick() {
        if (!this.isRaceActive) return;

        // Check if we need to generate a new transaction (target letter)
        if (this.pendingTransactions.length === 0) {
            this.createNewTransaction();
        }

        // Nodes process
        this.nodes.forEach(node => {
            if (node.cooldown > 0) {
                node.cooldown -= 100;
                node.isGuessing = false;
            } else if (!this.winnerNodeId) {
                // Ready to guess
                this.makeGuess(node);
            }
        });
    }

    createNewTransaction() {
        const randomLetter = this.alphabet[Math.floor(Math.random() * this.alphabet.length)];
        const tx: Transaction = {
            id: Math.random().toString(36).substring(7),
            letter: randomLetter,
            timestamp: new Date()
        };
        this.pendingTransactions.push(tx);
        this.targetLetter = tx.letter;
        this.winnerNodeId = null;
        this.nodes.forEach(n => n.isWinner = false);
    }

    makeGuess(node: Node) {
        node.isGuessing = true;
        const guess = this.alphabet[Math.floor(Math.random() * this.alphabet.length)];
        node.lastGuess = guess;

        if (guess === this.targetLetter) {
            // WINNER!
            this.winnerNodeId = node.id;
            node.wins++;
            node.isWinner = true;
            node.isGuessing = false;

            // Reward: Faster cooldown
            node.maxCooldown = Math.max(1000, node.maxCooldown - 200);

            // Move TX to block
            const tx = this.pendingTransactions.shift();
            if (tx) {
                this.currentBlock.push(tx);

                // Check if block is full
                if (this.currentBlock.length >= this.blockCapacity) {
                    this.finalizeBlock();
                }
            }

            // Reset others for next race
            setTimeout(() => {
                if (this.isRaceActive) {
                    this.createNewTransaction();
                    this.nodes.forEach(n => {
                        n.cooldown = n.maxCooldown;
                        n.lastGuess = '';
                    });
                }
            }, 2000);
        } else {
            // Wrong guess, start cooldown
            node.cooldown = node.maxCooldown;
            setTimeout(() => node.isGuessing = false, 500);
        }
    }

    finalizeBlock() {
        const blockHash = '0x' + Math.random().toString(16).substring(2, 10);
        (this.currentBlock as any).hash = blockHash;
        this.finalizedBlocks.unshift([...this.currentBlock] as any);
        this.currentBlock = [];
        if (this.finalizedBlocks.length > 5) {
            this.finalizedBlocks.pop();
        }
        // Randomize capacity for the next block (between 3 and 10)
        this.blockCapacity = Math.floor(Math.random() * 8) + 3;
        this.blockCapacityList = Array.from({ length: this.blockCapacity }, (_, i) => i);
    }

    toggleSimulation() {
        if (this.isRaceActive) this.stopSimulation();
        else this.startSimulation();
    }
}
