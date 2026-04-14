import { Component, OnInit, inject, PLATFORM_ID, ChangeDetectorRef } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { ChartModule } from 'primeng/chart';

@Component({
  selector: 'app-chart-lines-style',
  standalone: true,
  imports: [ChartModule],
  templateUrl: './chart-lines-style.component.html',
  styleUrls: ['./chart-lines-style.component.scss']
})
export class ChartLinesStyleComponent implements OnInit {
  data: any;
  options: any;

  platformId = inject(PLATFORM_ID);
  cd = inject(ChangeDetectorRef);

  ngOnInit() {
    this.initChart();
  }

  initChart() {
    if (isPlatformBrowser(this.platformId)) {
      const documentStyle = getComputedStyle(document.documentElement);
      const textColor = documentStyle.getPropertyValue('--p-text-color');
      const textColorSecondary = documentStyle.getPropertyValue('--p-text-muted-color');
      const surfaceBorder = documentStyle.getPropertyValue('--p-content-border-color');

      this.data = {
        labels: ['January', 'February', 'March', 'April', 'May', 'June', 'July'],
        datasets: [
          {
            label: 'First Dataset',
            data: [65, 59, 80, 81, 56, 55, 40],
            fill: false,
            tension: 0.4,
            borderColor: documentStyle.getPropertyValue('--p-cyan-500')
          },
          {
            label: 'Second Dataset',
            data: [28, 48, 40, 19, 86, 27, 90],
            fill: false,
            borderDash: [5, 5],
            tension: 0.4,
            borderColor: documentStyle.getPropertyValue('--p-orange-500')
          },
          {
            label: 'Third Dataset',
            data: [12, 51, 62, 33, 21, 62, 45],
            fill: true,
            borderColor: documentStyle.getPropertyValue('--p-gray-500'),
            tension: 0.4,
            backgroundColor: 'rgba(107, 114, 128, 0.2)'
          }
        ]
      };

      this.options = {
        maintainAspectRatio: false,
        aspectRatio: 0.6,
        plugins: {
          legend: {
            labels: {
              color: textColor
            }
          }
        },
        scales: {
          x: {
            ticks: { color: textColorSecondary },
            grid: { color: surfaceBorder }
          },
          y: {
            ticks: { color: textColorSecondary },
            grid: { color: surfaceBorder }
          }
        }
      };

      this.cd.markForCheck();
    }
  }
}