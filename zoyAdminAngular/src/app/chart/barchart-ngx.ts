import { Component } from '@angular/core';
import { single } from './chart-data';
@Component({
  selector: 'barchart-ngx',
  template: `<ngx-charts-bar-vertical [view]="[700, 400]" [results]="single" [gradient]="gradient" [xAxis]="showXAxis"
  [yAxis]="showYAxis" [legend]="showLegend" [showXAxisLabel]="showXAxisLabel" [showYAxisLabel]="showYAxisLabel" [xAxisLabel]="xAxisLabel"
  [yAxisLabel]="yAxisLabel" (select)="onSelect($event)">
 </ngx-charts-bar-vertical>`
})
export class BarNgxChartComponent {
  view: any[] = [700, 400];
  single: any[];
  // options
  showXAxis = true;
  showYAxis = true;
  gradient = false;
  showLegend = true;
  showXAxisLabel = false;
  xAxisLabel = '';
  showYAxisLabel = false;
  yAxisLabel = '';

  colorScheme = {
    domain: ['#5AA454', '#A10A28', '#C7B42C', '#AAAAAA'],
  };

  constructor() {
    Object.assign(this, { single });
  }

  onSelect(event) {
    console.log(event);
  }
}