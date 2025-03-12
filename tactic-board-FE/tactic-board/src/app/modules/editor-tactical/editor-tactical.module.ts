// src/app/modules/editor-tactical/editor-tactical.module.ts
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { EditorTacticalRoutingModule } from './editor-tactical-routing.module';
import { CanvasComponent } from './components/canvas/canvas.component';
import { TimelineComponent } from './components/timeline/timeline.component';
import { ElementLibraryComponent } from './components/element-library/element-library.component';
import { PlaybackControlsComponent } from './components/playback-controls/playback-controls.component';
import { EditorContainerComponent } from './components/editor-container/editor-container.component';

// Material Modules
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatListModule } from '@angular/material/list';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatSliderModule } from '@angular/material/slider';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatDividerModule } from '@angular/material/divider';
import { MatTabsModule } from '@angular/material/tabs';

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
    PlaybackControlsComponent,
    EditorContainerComponent
  ],
  imports: [
    CommonModule,
    EditorTacticalRoutingModule,
    FormsModule,
    ReactiveFormsModule,

    // Material
    MatButtonModule,
    MatIconModule,
    MatCardModule,
    MatToolbarModule,
    MatSidenavModule,
    MatListModule,
    MatExpansionModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatSliderModule,
    MatTooltipModule,
    MatDividerModule,
    MatTabsModule,

    // NgRx
    StoreModule.forFeature(fromEditor.editorFeatureKey, fromEditor.reducer),
    EffectsModule.forFeature([EditorEffects])
  ]
})
export class EditorTacticalModule { }