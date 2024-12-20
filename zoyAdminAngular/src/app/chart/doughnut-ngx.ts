import { Component } from '@angular/core';
 
@Component({
  selector: 'doughnut-ngx',
  template: `<ngx-charts-pie-chart
  [view]="[250, 250]"
  [customColors]="colorScheme"
  [results]="series"
  [legend]="false"
  [labels]="false"
  [doughnut]="true"
  [labelFormatting]="pieChartLabel.bind(this, series)"
  [animations]="false">
</ngx-charts-pie-chart>`
})
export class DoughnutNgxChartComponent {
    series = [
        {
          "name": "Booked",
          "value": 2000,
          "label": "20%"
        },
        {
          "name": "Checked-in",
          "value": 7000,
          "label": "70%"
        },
        {
          "name": "Vacancy",
          "value": 1000,
          "label": "10%"
        }
      ];
      // your color scheme
  colorScheme = [
      {"name": "Vacancy",
          "value":'#E1E4E8'}, 
          {
            "name": "Checked-in",
            "value":'#448EFC'},
            {
              "name": "Booked",
              "value":'#FF8743'}
    ];
    
      pieChartLabel(series: any[], name: string): string {
          const item = series.filter(data => data.name === name);
          if (item.length > 0) {
              return item[0].label;
          }
          return name;
      }
}