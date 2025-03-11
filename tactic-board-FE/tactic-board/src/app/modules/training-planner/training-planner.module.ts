import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { TrainingPlannerRoutingModule } from './training-planner-routing.module';
import { CalendarViewComponent } from './components/calendar-view/calendar-view.component';
import { TrainingTimelineComponent } from './components/training-timeline/training-timeline.component';
import { ExportOptionsComponent } from './components/export-options/export-options.component';


@NgModule({
  declarations: [
    CalendarViewComponent,
    TrainingTimelineComponent,
    ExportOptionsComponent
  ],
  imports: [
    CommonModule,
    TrainingPlannerRoutingModule
  ]
})
export class TrainingPlannerModule { }
