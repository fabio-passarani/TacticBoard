import { createFeatureSelector, createSelector } from '@ngrx/store';
import * as fromEditor from './editor.reducer';

export const selectEditorState = createFeatureSelector<fromEditor.EditorState>(
  fromEditor.editorFeatureKey
);

export const selectAllElements = createSelector(
  selectEditorState,
  fromEditor.selectAll
);

export const selectElementEntities = createSelector(
  selectEditorState,
  fromEditor.selectEntities
);

export const selectEditorLoaded = createSelector(
  selectEditorState,
  state => state.loaded
);

export const selectEditorLoading = createSelector(
  selectEditorState,
  state => state.loading
);

export const selectEditorError = createSelector(
  selectEditorState,
  state => state.error
);