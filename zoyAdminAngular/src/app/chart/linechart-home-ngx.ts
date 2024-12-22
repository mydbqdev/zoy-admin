import { Component} from '@angular/core';
import { singleline } from './chart-data';
@Component({
  selector: 'linechart-home-ngx',
  template: `<ngx-charts-line-chart
  [view]="[590, 215]"
    [results]="singleline"
    [gradient]="gradient"
    [xAxis]="showXAxis"
    [yAxis]="showYAxis"
    [showGridLines]="showGridLines"
    [legend]="showLegend"
    [showXAxisLabel]="showXAxisLabel"
    [showYAxisLabel]="showYAxisLabel"
    [xAxisLabel]="xAxisLabel"
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
    public colorScheme = {
      domain: ['#A10A28', '#A10A28', '#C7B42C', '#AAAAAA']
    };
     constructor() {
      Object.assign(this, { singleline })
    }
}