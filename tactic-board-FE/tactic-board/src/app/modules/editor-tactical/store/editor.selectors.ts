// src/app/modules/editor-tactical/store/editor.selectors.ts
import { createFeatureSelector, createSelector } from '@ngrx/store';
import * as fromEditor from './editor.reducer';
import { Tactic } from '../models/tactic.model';

export const selectEditorState = createFeatureSelector<fromEditor.EditorState>(
  fromEditor.editorFeatureKey
);

export const selectAllTactics = createSelector(
  selectEditorState,
  state => Object.values(state.tactics.entities).filter(Boolean) as Tactic[]
);

export const selectCurrentTacticId = createSelector(
  selectEditorState,
  state => state.currentTacticId
);

export const selectCurrentTactic = createSelector(
  selectEditorState,
  state => state.currentTacticId
    ? state.tactics.entities[state.currentTacticId]
    : null
);

export const selectTacticElements = createSelector(
  selectCurrentTactic,
  tactic => tactic ? tactic.elements : []
);

export const selectTacticAnimations = createSelector(
  selectCurrentTactic,
  tactic => tactic ? tactic.animations : []
);

export const selectPlaybackState = createSelector(
  selectEditorState,
  state => state.playback
);

export const selectIsPlaying = createSelector(
  selectPlaybackState,
  playback => playback.isPlaying
);

export const selectCurrentAnimationId = createSelector(
  selectPlaybackState,
  playback => playback.currentAnimationId
);

export const selectCurrentAnimation = createSelector(
  selectTacticAnimations,
  selectCurrentAnimationId,
  (animations, id) => id ? animations.find(a => a.id === id) : null
);

export const selectCurrentTime = createSelector(
  selectPlaybackState,
  playback => playback.currentTime
);

export const selectLoading = createSelector(
  selectEditorState,
  state => state.loading
);

export const selectError = createSelector(
  selectEditorState,
  state => state.error
);
