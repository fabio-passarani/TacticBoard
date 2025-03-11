import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

const routes: Routes = [
  { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
  {
    path: 'editor',
    loadChildren: () => import('./modules/editor-tactical/editor-tactical.module').then(m => m.EditorTacticalModule)
  },
  {
    path: 'exercises',
    loadChildren: () => import('./modules/exercise-library/exercise-library.module').then(m => m.ExerciseLibraryModule)
  },
  {
    path: 'planner',
    loadChildren: () => import('./modules/training-planner/training-planner.module').then(m => m.TrainingPlannerModule)
  },
  {
    path: 'analysis',
    loadChildren: () => import('./modules/analysis/analysis.module').then(m => m.AnalysisModule)
  },
  {
    path: 'dashboard',
    loadChildren: () => import('./modules/dashboard/dashboard.module').then(m => m.DashboardModule)
  },
  { path: '**', redirectTo: 'dashboard' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
