import { Component } from '@angular/core';
 
@Component({
  selector: 'doughnut',
  templateUrl: './doughnut.html'
})
export class DoughnutChartComponent {
  // Doughnut
  public doughnutChartLabels:string[] = ['Age 18 to 24', 'Age 25 to 35', 'Above 35+'];
  public demodoughnutChartData:number[][] = [[350, 450, 100],[250, 350, 150]];
  public doughnutChartType:string = 'doughnut';
 
  // events
  public chartClicked(e:any):void {
    console.log(e);
  }
 
  public chartHovered(e:any):void {
    console.log(e);
  }
}