import { Component } from '@angular/core';
import { single } from './chart-data';
@Component({
  selector: 'barchart-ngx',
  template: `<ngx-charts-bar-vertical [view]="[420, 400]" [barPadding]="barPadding" [showGridLines]="showGridLines" [customColors]="colorScheme" [results]="single" [gradient]="gradient" [xAxis]="showXAxis"
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
  barPadding=33;  /** 33 for 7 bar, 39 for 6 , 47 for 5,  10 for 12  */

  colorScheme = [
        {"name": "01/11","value":'#448EFC'}, 
        {"name": "02/11","value":'#448EFC'},
        {"name": "03/11","value":'#448EFC'},
        {"name": "04/11","value":'#448EFC'}, 
        {"name": "05/11","value":'#448EFC'},
        {"name": "06/11","value":'#448EFC'}, 
        {"name": "07/11","value":'#448EFC'}
  ];


  constructor() {
    Object.assign(this, { single });
  }

  onSelect(event) {
    console.log(event);
  }
}