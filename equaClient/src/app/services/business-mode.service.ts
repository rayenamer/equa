import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

export type AppMode = 'individual' | 'business';

@Injectable({
    providedIn: 'root'
})
export class BusinessModeService {
    private readonly STORAGE_KEY = 'appMode';
    private modeSubject = new BehaviorSubject<AppMode>(this.getStoredMode());
    public mode$ = this.modeSubject.asObservable();

    getMode(): AppMode {
        return this.modeSubject.value;
    }

    isBusinessMode(): boolean {
        return this.modeSubject.value === 'business';
    }

    /**
     * Toggle between individual and business mode.
     * TODO: Before switching to business mode, check if the user has a registered business.
     *   - If yes: switch to business mode normally.
     *   - If no: show a form/modal to register a new business (not yet implemented).
     */
    toggleMode(): void {
        const next: AppMode = this.modeSubject.value === 'individual' ? 'business' : 'individual';
        this.modeSubject.next(next);
        localStorage.setItem(this.STORAGE_KEY, next);
    }

    setMode(mode: AppMode): void {
        this.modeSubject.next(mode);
        localStorage.setItem(this.STORAGE_KEY, mode);
    }

    private getStoredMode(): AppMode {
        const stored = localStorage.getItem(this.STORAGE_KEY);
        return stored === 'business' ? 'business' : 'individual';
    }
}
