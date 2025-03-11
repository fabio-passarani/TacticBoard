import { isDevMode } from '@angular/core';
import {
  ActionReducerMap,
  MetaReducer
} from '@ngrx/store';

export interface State {
  // Definisci qui l'interfaccia dello stato globale
}

export const reducers: ActionReducerMap<State> = {
  // Aggiungi qui i tuoi reducers
};

export const metaReducers: MetaReducer<State>[] = isDevMode() ? [] : [];
