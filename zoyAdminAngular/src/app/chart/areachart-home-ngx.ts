import { Component} from '@angular/core';
import { singleline } from './chart-data';
@Component({
  selector: 'areachart-home-ngx',
  template: `<ngx-charts-area-chart
  [view]="[590, 315]"
    [results]="singleline"
    [gradient]="gradient"
    [xAxis]="showXAxis"
    [yAxis]="showYAxis"
    [legend]="showLegend"
    [showGridLines]="showGridLines"
    [showXAxisLabel]="showXAxisLabel"
    [showYAxisLabel]="showYAxisLabel"
    [xAxisLabel]="xAxisLabel"
    [yAxisLabel]="yAxisLabel">
</ngx-charts-area-chart>`
})
export class AreaNgxChartComponent {
    singleline: any[];
    public showXAxis = true;
    public showYAxis = true;
    public gradient = true;
    public showLegend = false;
    public showGridLines=true;
    showLabels: boolean = true;
    public showXAxisLabel = true;
    public xAxisLabel= "No of Booking";
    public showYAxisLabel = true;
    public yAxisLabel= "No of Months stayed";
    public graphDataChart: any[];
    public colorScheme = {
      domain: ['blue', '#A10A28', '#C7B42C', '#AAAAAA']
    };
     constructor() {
      Object.assign(this, { singleline })
    }
}