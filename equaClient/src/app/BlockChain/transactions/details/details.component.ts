import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { UiButtonComponent } from '../../../components/atoms/ui-button/ui-button.component';

@Component({
  selector: 'app-details',
  standalone: true,
  imports: [CommonModule, RouterLink, UiButtonComponent],
  templateUrl: './details.component.html',
  styleUrl: './details.component.scss'
})
export class DetailsComponent {

}
