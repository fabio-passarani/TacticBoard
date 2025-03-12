// src/app/modules/editor-tactical/store/editor.reducer.ts
import { createReducer, on } from '@ngrx/store';
import { EntityState, EntityAdapter, createEntityAdapter } from '@ngrx/entity';
import * as EditorActions from './editor.actions';
import { Tactic } from '../models/tactic.model';
import { TacticElement } from '../models/tactic-element.model';
import { Animation } from '../models/animation.model';

export const editorFeatureKey = 'editor';

export interface EditorState {
  tactics: EntityState<Tactic>;
  currentTacticId: string | null;
  playback: {
    isPlaying: boolean;
    currentAnimationId: string | null;
    currentTime: number;
  };
  loading: boolean;
  error: any;
}

export const tacticAdapter: EntityAdapter<Tactic> = createEntityAdapter<Tactic>();

export const initialState: EditorState = {
  tactics: tacticAdapter.getInitialState(),
  currentTacticId: null,
  playback: {
    isPlaying: false,
    currentAnimationId: null,
    currentTime: 0
  },
  loading: false,
  error: null
};

export const reducer = createReducer(
  initialState,

  // Gestione del caricamento delle tattiche
  on(EditorActions.loadTactics, state => ({
    ...state,
    loading: true
  })),

  on(EditorActions.loadTacticsSuccess, (state, { tactics }) => ({
    ...state,
    tactics: tacticAdapter.setAll(tactics, state.tactics),
    loading: false
  })),

  on(EditorActions.loadTacticsFailure, (state, { error }) => ({
    ...state,
    error,
    loading: false
  })),

  // Gestione della tattica corrente
  on(EditorActions.setCurrentTactic, (state, { tacticId }) => ({
    ...state,
    currentTacticId: tacticId
  })),

  on(EditorActions.loadCurrentTactic, state => ({
    ...state,
    loading: true
  })),

  on(EditorActions.loadCurrentTacticSuccess, (state, { tactic }) => ({
    ...state,
    tactics: tacticAdapter.upsertOne(tactic, state.tactics),
    currentTacticId: tactic.id,
    loading: false
  })),

  on(EditorActions.loadCurrentTacticFailure, (state, { error }) => ({
    ...state,
    error,
    loading: false
  })),

  // Gestione della creazione di tattiche
  on(EditorActions.createTactic, state => ({
    ...state,
    loading: true
  })),

  on(EditorActions.createTacticSuccess, (state, { tactic }) => ({
    ...state,
    tactics: tacticAdapter.addOne(tactic, state.tactics),
    currentTacticId: tactic.id,
    loading: false
  })),

  on(EditorActions.createTacticFailure, (state, { error }) => ({
    ...state,
    error,
    loading: false
  })),

  // Gestione dell'aggiornamento di tattiche
  on(EditorActions.updateTactic, state => ({
    ...state,
    loading: true
  })),

  on(EditorActions.updateTacticSuccess, (state, { tactic }) => ({
    ...state,
    tactics: tacticAdapter.updateOne({ id: tactic.id, changes: tactic }, state.tactics),
    loading: false
  })),

  on(EditorActions.updateTacticFailure, (state, { error }) => ({
    ...state,
    error,
    loading: false
  })),

  // Gestione dell'eliminazione di tattiche
  on(EditorActions.deleteTactic, state => ({
    ...state,
    loading: true
  })),

  on(EditorActions.deleteTacticSuccess, (state, { tacticId }) => {
    const newState = {
      ...state,
      tactics: tacticAdapter.removeOne(tacticId, state.tactics),
      loading: false
    };

    // Se la tattica corrente è stata eliminata, resetta currentTacticId
    if (state.currentTacticId === tacticId) {
      newState.currentTacticId = null;
    }

    return newState;
  }),

  on(EditorActions.deleteTacticFailure, (state, { error }) => ({
    ...state,
    error,
    loading: false
  })),

  // Gestione degli elementi della tattica
  on(EditorActions.addElement, (state, { element }) => {
    if (!state.currentTacticId) return state;

    const currentTactic = state.tactics.entities[state.currentTacticId];
    if (!currentTactic) return state;

    const updatedTactic = {
      ...currentTactic,
      elements: [...currentTactic.elements, element]
    };

    return {
      ...state,
      tactics: tacticAdapter.updateOne({
        id: state.currentTacticId,
        changes: updatedTactic
      }, state.tactics)
    };
  }),

  on(EditorActions.updateElement, (state, { element }) => {
    if (!state.currentTacticId) return state;

    const currentTactic = state.tactics.entities[state.currentTacticId];
    if (!currentTactic) return state;

    const updatedElements = currentTactic.elements.map(el =>
      el.id === element.id ? element : el
    );

    const updatedTactic = {
      ...currentTactic,
      elements: updatedElements
    };

    return {
      ...state,
      tactics: tacticAdapter.updateOne({
        id: state.currentTacticId,
        changes: updatedTactic
      }, state.tactics)
    };
  }),

  on(EditorActions.deleteElement, (state, { elementId }) => {
    if (!state.currentTacticId) return state;

    const currentTactic = state.tactics.entities[state.currentTacticId];
    if (!currentTactic) return state;

    const updatedElements = currentTactic.elements.filter(el => el.id !== elementId);

    const updatedTactic = {
      ...currentTactic,
      elements: updatedElements
    };

    return {
      ...state,
      tactics: tacticAdapter.updateOne({
        id: state.currentTacticId,
        changes: updatedTactic
      }, state.tactics)
    };
  }),

  // Gestione delle animazioni e della riproduzione
  on(EditorActions.playAnimation, (state, { animationId }) => ({
    ...state,
    playback: {
      ...state.playback,
      isPlaying: true,
      currentAnimationId: animationId
    }
  })),

  on(EditorActions.pauseAnimation, (state) => ({
    ...state,
    playback: {
      ...state.playback,
      isPlaying: false
    }
  })),

  on(EditorActions.stopAnimation, (state) => ({
    ...state,
    playback: {
      ...state.playback,
      isPlaying: false,
      currentTime: 0
    }
  })),

  on(EditorActions.setAnimationTime, (state, { time }) => ({
    ...state,
    playback: {
      ...state.playback,
      currentTime: time
    }
  }))
);

// Selectors
export const getEditorState = (state: any) => state[editorFeatureKey];
