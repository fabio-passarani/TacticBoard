import { createReducer, on } from '@ngrx/store';
import { EntityState, EntityAdapter, createEntityAdapter } from '@ngrx/entity';
import * as EditorActions from './editor.actions';
import { EditorElement } from './editor.actions';

export const editorFeatureKey = 'editor';

export interface EditorState extends EntityState<EditorElement> {
  loaded: boolean;
  loading: boolean;
  error: any;
}

export const adapter: EntityAdapter<EditorElement> = createEntityAdapter<EditorElement>();

export const initialState: EditorState = adapter.getInitialState({
  loaded: false,
  loading: false,
  error: null
});

export const reducer = createReducer(
  initialState,
  on(EditorActions.loadElements, state => ({
    ...state,
    loading: true
  })),
  on(EditorActions.loadElementsSuccess, (state, { elements }) =>
    adapter.setAll(elements, {
      ...state,
      loaded: true,
      loading: false
    })
  ),
  on(EditorActions.loadElementsFailure, (state, { error }) => ({
    ...state,
    error,
    loading: false
  })),
  on(EditorActions.addElement, (state, { element }) =>
    adapter.addOne(element, state)
  ),
  on(EditorActions.updateElement, (state, { element }) =>
    adapter.updateOne(element, state)
  ),
  on(EditorActions.deleteElement, (state, { id }) =>
    adapter.removeOne(id, state)
  )
);

export const {
  selectIds,
  selectEntities,
  selectAll,
  selectTotal,
} = adapter.getSelectors();