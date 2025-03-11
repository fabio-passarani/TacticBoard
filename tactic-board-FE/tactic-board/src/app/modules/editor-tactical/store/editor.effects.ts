import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { of } from 'rxjs';
import { catchError, map, switchMap } from 'rxjs/operators';
import * as EditorActions from './editor.actions';

@Injectable()
export class EditorEffects {
  constructor(private actions$: Actions) {}

  loadElements$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(EditorActions.loadElements),
      switchMap(() => {
        // Qui in futuro inserirai la chiamata al servizio
        return of([]).pipe(
          map(elements => EditorActions.loadElementsSuccess({ elements })),
          catchError(error => of(EditorActions.loadElementsFailure({ error })))
        );
      })
    );
  });
}