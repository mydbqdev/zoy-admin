import { Component } from '@angular/core';
import { DataService } from '../common/service/data.service';
import { TotalBookingDetailsModel } from '../common/models/dashboard.model';
 
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
  series: { name: string; value: number; label: string }[] = [];
  totalBookingDetails :TotalBookingDetailsModel= new TotalBookingDetailsModel();
  constructor(private dataService:DataService){
    this.dataService.getDashboardBookingDetails.subscribe(details=>{
      this.totalBookingDetails = details;
      this.getBookingDetails();
    });
  }

  getBookingDetails(): void {
      const totalBookings = this.totalBookingDetails.checked_in + this.totalBookingDetails.booked + this.totalBookingDetails.vacancy;

      const bookedPercentage = ((this.totalBookingDetails.booked / totalBookings) * 100).toFixed(2);
      const checkedInPercentage = ((this.totalBookingDetails.checked_in / totalBookings) * 100).toFixed(2);
      const vacancyPercentage = ((this.totalBookingDetails.vacancy / totalBookings) * 100).toFixed(2);

      this.series = [
        {
          name: 'Booked',
          value: this.totalBookingDetails.booked,
          label: bookedPercentage+'%'
        },
        {
          name: 'Checked-in',
          value: this.totalBookingDetails.checked_in,
          label: checkedInPercentage+'%'
        },
        {
          name: 'Vacancy',
          value: this.totalBookingDetails.vacancy,
          label: vacancyPercentage+'%'
        }
      ];
    }

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