import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { SplashCursorComponent } from './components/splash-cursor/splash-cursor.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, SplashCursorComponent],
  template: `
    <router-outlet></router-outlet>
    <app-splash-cursor />
  `,
  styles: []
})
export class AppComponent {
  title = 'EQUA - Finance Without Barriers';
}
