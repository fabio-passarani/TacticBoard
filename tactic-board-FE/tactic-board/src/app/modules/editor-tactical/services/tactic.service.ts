// src/app/modules/editor-tactical/services/tactic.service.ts
import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { delay } from 'rxjs/operators';
import { Tactic } from '../models/tactic.model';
import { TacticElement, ElementType } from '../models/tactic-element.model';
import { Animation } from '../models/animation.model';
import { v4 as uuidv4 } from 'uuid'; // Nota: dovrai installare uuid con npm install uuid @types/uuid --save

@Injectable({
  providedIn: 'root'
})
export class TacticService {
  private tactics: Tactic[] = [
    {
      id: '1',
      name: 'Schema di base 4-3-3',
      description: 'Formazione standard 4-3-3 con varianti offensive',
      createdAt: new Date(),
      updatedAt: new Date(),
      elements: this.generateSampleElements('4-3-3'),
      animations: [],
      tags: ['4-3-3', 'offensive', 'base']
    },
    {
      id: '2',
      name: 'Schema difensivo 5-3-2',
      description: 'Formazione difensiva con 5 difensori',
      createdAt: new Date(),
      updatedAt: new Date(),
      elements: this.generateSampleElements('5-3-2'),
      animations: [],
      tags: ['5-3-2', 'defensive', 'compact']
    }
  ];

  constructor() { }

  getTactics(): Observable<Tactic[]> {
    // Simula una chiamata API con delay
    return of(this.tactics).pipe(delay(300));
  }

  getTacticById(id: string): Observable<Tactic | undefined> {
    const tactic = this.tactics.find(t => t.id === id);
    return of(tactic).pipe(delay(300));
  }

  createTactic(tactic: Partial<Tactic>): Observable<Tactic> {
    const newTactic: Tactic = {
      id: uuidv4(),
      name: tactic.name || 'Nuovo schema tattico',
      description: tactic.description,
      createdAt: new Date(),
      updatedAt: new Date(),
      elements: tactic.elements || [],
      animations: tactic.animations || [],
      tags: tactic.tags || []
    };

    this.tactics.push(newTactic);
    return of(newTactic).pipe(delay(300));
  }

  updateTactic(tactic: Tactic): Observable<Tactic> {
    const index = this.tactics.findIndex(t => t.id === tactic.id);
    if (index !== -1) {
      tactic.updatedAt = new Date();
      this.tactics[index] = { ...tactic };
      return of(this.tactics[index]).pipe(delay(300));
    }
    throw new Error('Tactic not found');
  }

  deleteTactic(id: string): Observable<boolean> {
    const index = this.tactics.findIndex(t => t.id === id);
    if (index !== -1) {
      this.tactics.splice(index, 1);
      return of(true).pipe(delay(300));
    }
    return of(false).pipe(delay(300));
  }

  // Metodi di supporto per generare elementi di esempio per le formazioni
  private generateSampleElements(formation: string): TacticElement[] {
    const elements: TacticElement[] = [
      // Aggiungi sempre il pallone
      {
        id: uuidv4(),
        type: ElementType.BALL,
        x: 350,
        y: 250,
        rotation: 0,
        scaleX: 1,
        scaleY: 1,
        properties: {}
      }
    ];

    // Aggiungi il portiere
    elements.push({
      id: uuidv4(),
      type: ElementType.PLAYER,
      x: 100,
      y: 250,
      rotation: 0,
      scaleX: 1,
      scaleY: 1,
      properties: {
        number: 1,
        team: 'home',
        isGoalkeeper: true,
        color: '#ff0000'
      }
    });

    // In base alla formazione, genera i giocatori di movimento
    switch (formation) {
      case '4-3-3':
        // Aggiungi 4 difensori
        for (let i = 0; i < 4; i++) {
          elements.push({
            id: uuidv4(),
            type: ElementType.PLAYER,
            x: 150,
            y: 100 + i * 100,
            rotation: 0,
            scaleX: 1,
            scaleY: 1,
            properties: {
              number: i + 2,
              team: 'home',
              color: '#ff0000'
            }
          });
        }

        // Aggiungi 3 centrocampisti
        for (let i = 0; i < 3; i++) {
          elements.push({
            id: uuidv4(),
            type: ElementType.PLAYER,
            x: 250,
            y: 125 + i * 125,
            rotation: 0,
            scaleX: 1,
            scaleY: 1,
            properties: {
              number: i + 6,
              team: 'home',
              color: '#ff0000'
            }
          });
        }

        // Aggiungi 3 attaccanti
        for (let i = 0; i < 3; i++) {
          elements.push({
            id: uuidv4(),
            type: ElementType.PLAYER,
            x: 350,
            y: 125 + i * 125,
            rotation: 0,
            scaleX: 1,
            scaleY: 1,
            properties: {
              number: i + 9,
              team: 'home',
              color: '#ff0000'
            }
          });
        }
        break;

      case '5-3-2':
        // Implementa la formazione 5-3-2
        // ... (simile all'implementazione del 4-3-3)
        break;

      default:
        // Default vuoto
        break;
    }

    return elements;
  }
}