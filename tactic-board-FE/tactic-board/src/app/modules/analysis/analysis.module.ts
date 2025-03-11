import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { AnalysisRoutingModule } from './analysis-routing.module';
import { VideoUploaderComponent } from './components/video-uploader/video-uploader.component';
import { VideoPlayerComponent } from './components/video-player/video-player.component';
import { AnnotationToolsComponent } from './components/annotation-tools/annotation-tools.component';


@NgModule({
  declarations: [
    VideoUploaderComponent,
    VideoPlayerComponent,
    AnnotationToolsComponent
  ],
  imports: [
    CommonModule,
    AnalysisRoutingModule
  ]
})
export class AnalysisModule { }
