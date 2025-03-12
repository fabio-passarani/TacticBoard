// src/app/modules/editor-tactical/models/tactic-element.model.ts
export interface TacticElement {
  id: string;
  type: ElementType;
  x: number;
  y: number;
  rotation: number;
  scaleX: number;
  scaleY: number;
  properties: ElementProperties;
}

export enum ElementType {
  PLAYER = 'player',
  BALL = 'ball',
  CONE = 'cone',
  ARROW = 'arrow',
  LINE = 'line',
  TEXT = 'text'
}

export interface ElementProperties {
  label?: string;
  number?: number;
  color?: string;
  team?: 'home' | 'away' | 'neutral';
  isGoalkeeper?: boolean;
  customProps?: Record<string, any>;
}