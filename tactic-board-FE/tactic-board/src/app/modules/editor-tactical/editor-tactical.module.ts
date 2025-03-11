import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { EditorTacticalRoutingModule } from './editor-tactical-routing.module';
import { CanvasComponent } from './components/canvas/canvas.component';
import { TimelineComponent } from './components/timeline/timeline.component';
import { ElementLibraryComponent } from './components/element-library/element-library.component';
import { PlaybackControlsComponent } from './components/playback-controls/playback-controls.component';

// Material Modules
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';

// NgRx
import { StoreModule } from '@ngrx/store';
import { EffectsModule } from '@ngrx/effects';
import * as fromEditor from './store/editor.reducer';
import { EditorEffects } from './store/editor.effects';

@NgModule({
  declarations: [
    CanvasComponent,
    TimelineComponent,
    ElementLibraryComponent,
    PlaybackControlsComponent
  ],
  imports: [
    CommonModule,
    EditorTacticalRoutingModule,

    // Material
    MatButtonModule,
    MatIconModule,
    MatCardModule,

    // NgRx
    StoreModule.forFeature(fromEditor.editorFeatureKey, fromEditor.reducer),
    EffectsModule.forFeature([EditorEffects])
  ]
})
export class EditorTacticalModule { }
