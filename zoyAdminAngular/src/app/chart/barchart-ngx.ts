import { Component } from '@angular/core';
import { DataService } from '../common/service/data.service';
@Component({
  selector: 'barchart-ngx',
  template: `<ngx-charts-bar-vertical [view]="[380, 400]" [barPadding]="barPadding" [showGridLines]="showGridLines" [customColors]="colorScheme" [results]="zoyRevenue" [gradient]="gradient" [xAxis]="showXAxis"
  [yAxis]="showYAxis" [legend]="showLegend" [showXAxisLabel]="showXAxisLabel" [showYAxisLabel]="showYAxisLabel" [xAxisLabel]="xAxisLabel"
  [yAxisLabel]="yAxisLabel">
 </ngx-charts-bar-vertical>`
})

export class BarNgxChartComponent {
  view: any[] = [380, 400];
  zoyRevenue: any[];
  // options
  showGridLines=false;
  showXAxis = true;
  showYAxis = true;
  gradient = false;
  showLegend = false;
  showXAxisLabel = true;
  xAxisLabel = 'Date (DD/MM)';
  showYAxisLabel = true;
  yAxisLabel = 'In Thousand';
  barPadding=30;  /** 33 for 7 bar, 39 for 6 , 47 for 5,  10 for 12  */
  colorScheme: { name: string; value: string }[] = [];
  zoyRevenuedata:{"date": string,"revenueInThousands": number}[]=[];
  constructor(private dataService:DataService) {
    this.dataService.getTotalRevenueDetails.subscribe(details=>{
      this.zoyRevenuedata = details;
      this.getTotalRevenueDetails();
    });
  }

  getTotalRevenueDetails(){
    this.zoyRevenue=[];
    this.colorScheme=[];
     
     this.zoyRevenue = this.zoyRevenuedata.map(item => {
          const date = new Date(item.date);
          const day = String(date.getDate()).padStart(2, '0');
          const month = String(date.getMonth() + 1).padStart(2, '0'); 
        
          return {
            name: day+"/"+month,
            value: item.revenueInThousands
          };
        });
        this.colorScheme = this.zoyRevenue.map(item => ({
          name: item.name,
          value: '#448EFC'
        }));
   
	}

  
}

