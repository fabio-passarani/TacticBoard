// src/app/modules/editor-tactical/services/editor-utils.service.ts
import { Injectable } from '@angular/core';
import { ElementType, TacticElement } from '../models/tactic-element.model';
import { Animation } from '../models/animation.model';
import { v4 as uuidv4 } from 'uuid';

@Injectable({
  providedIn: 'root'
})
export class EditorUtilsService {

  constructor() { }

  /**
   * Genera un ID univoco
   */
  generateId(): string {
    return uuidv4();
  }

  /**
   * Crea un nuovo elemento tattico
   */
  createElement(type: ElementType, x: number, y: number, properties: any = {}): TacticElement {
    return {
      id: this.generateId(),
      type,
      x,
      y,
      rotation: 0,
      scaleX: 1,
      scaleY: 1,
      properties
    };
  }

  /**
   * Crea una nuova animazione
   */
  createAnimation(name: string = 'Nuova Animazione', duration: number = 5000): Animation {
    return {
      id: this.generateId(),
      name,
      duration,
      frames: []
    };
  }

  /**
   * Formatta il tempo in formato mm:ss.ms
   */
  formatTime(ms: number): string {
    const seconds = Math.floor(ms / 1000);
    const minutes = Math.floor(seconds / 60);
    const remainingSeconds = seconds % 60;
    const remainingMs = ms % 1000;

    return `${minutes.toString().padStart(2, '0')}:${remainingSeconds.toString().padStart(2, '0')}.${Math.floor(remainingMs / 100)}`;
  }

  /**
   * Clona un elemento tattico
   */
  cloneElement(element: TacticElement): TacticElement {
    return {
      ...element,
      id: this.generateId(),
      properties: { ...element.properties }
    };
  }

  /**
   * Calcola la distanza tra due punti
   */
  calculateDistance(x1: number, y1: number, x2: number, y2: number): number {
    return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
  }

  /**
   * Calcola l'angolo tra due punti (in radianti)
   */
  calculateAngle(x1: number, y1: number, x2: number, y2: number): number {
    return Math.atan2(y2 - y1, x2 - x1);
  }
}