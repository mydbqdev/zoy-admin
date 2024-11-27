import { Component } from '@angular/core';
import { single } from './chart-data';
@Component({
  selector: 'barchart-ngx',
  template: `<ngx-charts-bar-vertical [view]="[420, 400]" [showGridLines]="showGridLines" [customColors]="colorScheme" [results]="single" [gradient]="gradient" [xAxis]="showXAxis"
  [yAxis]="showYAxis" [legend]="showLegend" [showXAxisLabel]="showXAxisLabel" [showYAxisLabel]="showYAxisLabel" [xAxisLabel]="xAxisLabel"
  [yAxisLabel]="yAxisLabel" (select)="onSelect($event)">
 </ngx-charts-bar-vertical>`
})
export class BarNgxChartComponent {
  view: any[] = [400, 300];
  single: any[];
  // options
  showGridLines=false;
  showXAxis = true;
  showYAxis = true;
  gradient = false;
  showLegend = false;
  showXAxisLabel = false;
  xAxisLabel = '';
  showYAxisLabel = true;
  yAxisLabel = 'In Thousand';

  colorScheme = [
        {"name": "01/11","value":'#F989E7'}, 
        {"name": "02/11","value":'#F989E7'},
        {"name": "03/11","value":'#F989E7'},
        {"name": "04/11","value":'#F989E7'}, 
        {"name": "05/11","value":'#F989E7'},
        {"name": "06/11","value":'#F989E7'},
        {"name": "07/11","value":'#F989E7'}
  ];


  constructor() {
    Object.assign(this, { single });
  }

  onSelect(event) {
    console.log(event);
  }
}