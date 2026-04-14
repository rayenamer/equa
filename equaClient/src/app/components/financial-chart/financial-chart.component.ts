import { Component, Input, OnInit } from '@angular/core';
import { AgFinancialCharts } from 'ag-charts-angular';
import {
  AgFinancialChartOptions,
  FinancialChartModule,
  ModuleRegistry,
} from 'ag-charts-enterprise';

ModuleRegistry.registerModules([FinancialChartModule]);

@Component({
  selector: 'app-financial-chart',
  standalone: true,
  imports: [AgFinancialCharts],
  templateUrl: './financial-chart.component.html',
  styleUrls: ['./financial-chart.component.css'],
})
export class FinancialChartComponent implements OnInit {
  /** OHLCV data to display */
  @Input() data: {
    date: Date;
    open: number;
    high: number;
    low: number;
    close: number;
    volume: number;
  }[] = [];

  /** Chart title shown at the top */
  @Input() title: string = 'Financial Chart';

  /** Optional: pre-built annotations to render on the chart */
  @Input() annotations: any[] = [];

  public options: AgFinancialChartOptions = {};

  ngOnInit(): void {
    this.options = {
      data: this.data,
      title: { text: this.title },
      ...(this.annotations.length > 0 && {
        initialState: { annotations: this.annotations },
      }),
    };
  }
}
