import { Component} from '@angular/core';
import { singlearea } from './chart-data';
import * as shape from 'd3-shape';
@Component({
  selector: 'areachart-home-ngx',
  template: `<ngx-charts-area-chart
  [view]="[590, 315]"
    [results]="singlearea"
    [customColors]="colorScheme"
    [gradient]="gradient"
    [xAxis]="showXAxis"
    [yAxis]="showYAxis"
    [legend]="showLegend"
    [showGridLines]="showGridLines"
    [showXAxisLabel]="showXAxisLabel"
    [showYAxisLabel]="showYAxisLabel"
    [xAxisLabel]="xAxisLabel"
    [curve]="linearCurve"
    [yScaleMax]="yScaleMax"
    [yAxisLabel]="yAxisLabel">
</ngx-charts-area-chart>`
})
export class AreaNgxChartComponent {
  singlearea: any[];
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
    public colorScheme =  [{ 
        name: 'Stayed',
        value: '#45E7E7'
      }];
    public yScaleMax=6;
      public linearCurve=shape.curveLinear // shape.curveNatural;
     constructor() {
      Object.assign(this, { singlearea })
    }
}