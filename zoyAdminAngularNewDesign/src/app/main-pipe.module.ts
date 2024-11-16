import { NgModule } from '@angular/core';
import { CommonModule } from "@angular/common";
import { PerformancePipe } from './common/helpers/filter.pipe';

@NgModule({
    declarations: [PerformancePipe], // <---
    imports: [CommonModule],
    exports: [PerformancePipe] // <---
})

export class MainPipe { }