import { TacticElement } from "./tactic-element.model";
import { Animation } from "./animation.model";

// src/app/modules/editor-tactical/models/tactic.model.ts
export interface Tactic {
  id: string;
  name: string;
  description?: string;
  createdAt: Date;
  updatedAt: Date;
  elements: TacticElement[];
  animations: Animation[];
  tags?: string[];
}