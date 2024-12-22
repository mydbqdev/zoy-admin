import { Component} from '@angular/core';
import { singleline } from './chart-data';
@Component({
  selector: 'linechart-home-ngx',
  template: `<ngx-charts-line-chart
  [view]="[590, 215]"
    [results]="singleline"
    [gradient]="gradient"
    [customColors]="colorScheme"
    [xAxis]="showXAxis"
    [yAxis]="showYAxis"
    [showGridLines]="showGridLines"
    [legend]="showLegend"
    [showXAxisLabel]="showXAxisLabel"
    [showYAxisLabel]="showYAxisLabel"
    [xAxisLabel]="xAxisLabel"
    [yScaleMax]="yScaleMax"
    [yAxisLabel]="yAxisLabel">
</ngx-charts-line-chart>`
})
export class LineNgxChartComponent {
    singleline: any[];
    public showXAxis = true;
    public showYAxis = true;
    public gradient = false;
    public showLegend = false;
    public showXAxisLabel = true;
    public xAxisLabel= "Days";
    public showYAxisLabel = true;
    public showGridLines=true;
    public yAxisLabel= "Revenue";
    public graphDataChart: any[];
    public colorScheme =  [{ 
      name: 'Revenue (INR)',
      value: '#0EC1C1'
    }];
    public yScaleMax=4000;
     constructor() {
      Object.assign(this, { singleline })
    }
}