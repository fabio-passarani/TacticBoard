// src/app/modules/editor-tactical/store/editor.actions.ts
import { createAction, props } from '@ngrx/store';
import { Tactic } from '../models/tactic.model';
import { TacticElement } from '../models/tactic-element.model';
import { Animation, AnimationFrame } from '../models/animation.model';

// Azioni per il caricamento delle tattiche
export const loadTactics = createAction(
  '[Editor] Load Tactics'
);

export const loadTacticsSuccess = createAction(
  '[Editor] Load Tactics Success',
  props<{ tactics: Tactic[] }>()
);

export const loadTacticsFailure = createAction(
  '[Editor] Load Tactics Failure',
  props<{ error: any }>()
);

// Azioni per la tattica corrente
export const setCurrentTactic = createAction(
  '[Editor] Set Current Tactic',
  props<{ tacticId: string }>()
);

export const loadCurrentTactic = createAction(
  '[Editor] Load Current Tactic',
  props<{ tacticId: string }>()
);

export const loadCurrentTacticSuccess = createAction(
  '[Editor] Load Current Tactic Success',
  props<{ tactic: Tactic }>()
);

export const loadCurrentTacticFailure = createAction(
  '[Editor] Load Current Tactic Failure',
  props<{ error: any }>()
);

export const createTactic = createAction(
  '[Editor] Create Tactic',
  props<{ tactic: Partial<Tactic> }>()
);

export const createTacticSuccess = createAction(
  '[Editor] Create Tactic Success',
  props<{ tactic: Tactic }>()
);

export const createTacticFailure = createAction(
  '[Editor] Create Tactic Failure',
  props<{ error: any }>()
);

export const updateTactic = createAction(
  '[Editor] Update Tactic',
  props<{ tactic: Tactic }>()
);

export const updateTacticSuccess = createAction(
  '[Editor] Update Tactic Success',
  props<{ tactic: Tactic }>()
);

export const updateTacticFailure = createAction(
  '[Editor] Update Tactic Failure',
  props<{ error: any }>()
);

export const deleteTactic = createAction(
  '[Editor] Delete Tactic',
  props<{ tacticId: string }>()
);

export const deleteTacticSuccess = createAction(
  '[Editor] Delete Tactic Success',
  props<{ tacticId: string }>()
);

export const deleteTacticFailure = createAction(
  '[Editor] Delete Tactic Failure',
  props<{ error: any }>()
);

// Azioni per gli elementi della tattica
export const addElement = createAction(
  '[Editor] Add Element',
  props<{ element: TacticElement }>()
);

export const updateElement = createAction(
  '[Editor] Update Element',
  props<{ element: TacticElement }>()
);

export const deleteElement = createAction(
  '[Editor] Delete Element',
  props<{ elementId: string }>()
);

// Azioni per le animazioni
export const addAnimation = createAction(
  '[Editor] Add Animation',
  props<{ animation: Animation }>()
);

export const updateAnimation = createAction(
  '[Editor] Update Animation',
  props<{ animation: Animation }>()
);

export const deleteAnimation = createAction(
  '[Editor] Delete Animation',
  props<{ animationId: string }>()
);

export const addAnimationFrame = createAction(
  '[Editor] Add Animation Frame',
  props<{ animationId: string, frame: AnimationFrame }>()
);

export const updateAnimationFrame = createAction(
  '[Editor] Update Animation Frame',
  props<{ animationId: string, frame: AnimationFrame }>()
);

export const deleteAnimationFrame = createAction(
  '[Editor] Delete Animation Frame',
  props<{ animationId: string, frameTime: number, elementId: string }>()
);

// Azioni per la riproduzione dell'animazione
export const playAnimation = createAction(
  '[Editor] Play Animation',
  props<{ animationId: string }>()
);

export const pauseAnimation = createAction(
  '[Editor] Pause Animation'
);

export const stopAnimation = createAction(
  '[Editor] Stop Animation'
);

export const setAnimationTime = createAction(
  '[Editor] Set Animation Time',
  props<{ time: number }>()
);