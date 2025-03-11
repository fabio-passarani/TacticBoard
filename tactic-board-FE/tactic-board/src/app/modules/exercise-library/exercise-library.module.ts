import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { ExerciseLibraryRoutingModule } from './exercise-library-routing.module';
import { ExerciseCatalogComponent } from './components/exercise-catalog/exercise-catalog.component';
import { ExerciseFiltersComponent } from './components/exercise-filters/exercise-filters.component';
import { ExerciseFormComponent } from './components/exercise-form/exercise-form.component';

// Material Modules
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

@NgModule({
  declarations: [
    ExerciseCatalogComponent,
    ExerciseFiltersComponent,
    ExerciseFormComponent
  ],
  imports: [
    CommonModule,
    ExerciseLibraryRoutingModule,
    FormsModule,
    ReactiveFormsModule,
    
    // Material
    MatButtonModule,
    MatIconModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule
  ]
})
export class ExerciseLibraryModule { }
