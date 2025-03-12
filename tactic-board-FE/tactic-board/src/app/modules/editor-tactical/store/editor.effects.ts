// src/app/modules/editor-tactical/store/editor.effects.ts
import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { of } from 'rxjs';
import { catchError, map, switchMap, withLatestFrom } from 'rxjs/operators';
import { Store } from '@ngrx/store';
import * as EditorActions from './editor.actions';
import * as EditorSelectors from './editor.selectors';
import { TacticService } from '../services/tactic.service';

@Injectable()
export class EditorEffects {
  constructor(
    private actions$: Actions,
    private store: Store,
    private tacticService: TacticService
  ) {}

  loadTactics$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(EditorActions.loadTactics),
      switchMap(() =>
        this.tacticService.getTactics().pipe(
          map(tactics => EditorActions.loadTacticsSuccess({ tactics })),
          catchError(error => of(EditorActions.loadTacticsFailure({ error })))
        )
      )
    );
  });

  loadCurrentTactic$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(EditorActions.loadCurrentTactic),
      switchMap(({ tacticId }) =>
        this.tacticService.getTacticById(tacticId).pipe(
          map(tactic => tactic
            ? EditorActions.loadCurrentTacticSuccess({ tactic })
            : EditorActions.loadCurrentTacticFailure({
                error: `Tactic with id ${tacticId} not found`
              })
          ),
          catchError(error => of(EditorActions.loadCurrentTacticFailure({ error })))
        )
      )
    );
  });

  createTactic$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(EditorActions.createTactic),
      switchMap(({ tactic }) =>
        this.tacticService.createTactic(tactic).pipe(
          map(createdTactic => EditorActions.createTacticSuccess({ tactic: createdTactic })),
          catchError(error => of(EditorActions.createTacticFailure({ error })))
        )
      )
    );
  });

  updateTactic$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(EditorActions.updateTactic),
      switchMap(({ tactic }) =>
        this.tacticService.updateTactic(tactic).pipe(
          map(updatedTactic => EditorActions.updateTacticSuccess({ tactic: updatedTactic })),
          catchError(error => of(EditorActions.updateTacticFailure({ error })))
        )
      )
    );
  });

  deleteTactic$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(EditorActions.deleteTactic),
      switchMap(({ tacticId }) =>
        this.tacticService.deleteTactic(tacticId).pipe(
          map(success => success
            ? EditorActions.deleteTacticSuccess({ tacticId })
            : EditorActions.deleteTacticFailure({
                error: `Failed to delete tactic with id ${tacticId}`
              })
          ),
          catchError(error => of(EditorActions.deleteTacticFailure({ error })))
        )
      )
    );
  });

  // Salvataggio automatico delle modifiche alla tattica corrente
  updateElements$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(
        EditorActions.addElement,
        EditorActions.updateElement,
        EditorActions.deleteElement,
        EditorActions.addAnimation,
        EditorActions.updateAnimation,
        EditorActions.deleteAnimation,
        EditorActions.addAnimationFrame,
        EditorActions.updateAnimationFrame,
        EditorActions.deleteAnimationFrame
      ),
      withLatestFrom(
        this.store.select(EditorSelectors.selectCurrentTactic)
      ),
      switchMap(([action, currentTactic]) => {
        if (!currentTactic) return of({ type: 'NO_OP' });

        return this.tacticService.updateTactic(currentTactic).pipe(
          map(updatedTactic => EditorActions.updateTacticSuccess({
            tactic: updatedTactic
          })),
          catchError(error => of(EditorActions.updateTacticFailure({ error })))
        );
      })
    );
  });
}