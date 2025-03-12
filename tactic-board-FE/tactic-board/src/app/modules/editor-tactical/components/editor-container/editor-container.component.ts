// src/app/modules/editor-tactical/components/editor-container/editor-container.component.ts
import { Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import * as EditorActions from '../../store/editor.actions';
import { ElementType } from '../../models/tactic-element.model';

@Component({
  selector: 'app-editor-container',
  templateUrl: './editor-container.component.html',
  styleUrls: ['./editor-container.component.scss']
})
export class EditorContainerComponent implements OnInit {
  selectedElementType: ElementType | null = null;

  constructor(private store: Store) { }

  ngOnInit(): void {
    // Carica le tattiche all'inizializzazione
    this.store.dispatch(EditorActions.loadTactics());
  }

  onElementSelected(type: ElementType): void {
    this.selectedElementType = type;
  }
}