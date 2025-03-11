import { createAction, props } from '@ngrx/store';
import { Update } from '@ngrx/entity';

export interface EditorElement {
  id: string;
  type: string;
  position: { x: number, y: number };
  properties?: any;
}

export const loadElements = createAction(
  '[Editor] Load Elements'
);

export const loadElementsSuccess = createAction(
  '[Editor] Load Elements Success',
  props<{ elements: EditorElement[] }>()
);

export const loadElementsFailure = createAction(
  '[Editor] Load Elements Failure',
  props<{ error: any }>()
);

export const addElement = createAction(
  '[Editor] Add Element',
  props<{ element: EditorElement }>()
);

export const updateElement = createAction(
  '[Editor] Update Element',
  props<{ element: Update<EditorElement> }>()
);

export const deleteElement = createAction(
  '[Editor] Delete Element',
  props<{ id: string }>()
);