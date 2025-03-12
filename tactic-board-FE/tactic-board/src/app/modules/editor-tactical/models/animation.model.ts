// src/app/modules/editor-tactical/models/animation.model.ts
export interface Animation {
  id: string;
  name: string;
  duration: number;
  frames: AnimationFrame[];
}

export interface AnimationFrame {
  time: number; // milliseconds from start
  elementId: string;
  properties: {
    x?: number;
    y?: number;
    rotation?: number;
    opacity?: number;
    visible?: boolean;
    customProps?: Record<string, any>;
  };
}