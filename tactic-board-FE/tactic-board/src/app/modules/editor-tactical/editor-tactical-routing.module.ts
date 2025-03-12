// src/app/modules/editor-tactical/editor-tactical-routing.module.ts
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { EditorContainerComponent } from './components/editor-container/editor-container.component';

const routes: Routes = [
  {
    path: '',
    component: EditorContainerComponent
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class EditorTacticalRoutingModule { }