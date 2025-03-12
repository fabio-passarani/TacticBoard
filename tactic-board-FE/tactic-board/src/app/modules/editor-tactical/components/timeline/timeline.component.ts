// src/app/modules/editor-tactical/components/timeline/timeline.component.ts
import { Component, OnInit, OnDestroy } from '@angular/core';
import { Store } from '@ngrx/store';
import { Subscription } from 'rxjs';
import * as EditorSelectors from '../../store/editor.selectors';
import * as EditorActions from '../../store/editor.actions';
import { Animation, AnimationFrame } from '../../models/animation.model';
import { v4 as uuidv4 } from 'uuid';

@Component({
  selector: 'app-timeline',
  templateUrl: './timeline.component.html',
  styleUrls: ['./timeline.component.scss']
})
export class TimelineComponent implements OnInit, OnDestroy {
  // Make sure we're explicitly typing these observables
  currentTactic$ = this.store.select(EditorSelectors.selectCurrentTactic);
  animations$ = this.store.select(EditorSelectors.selectTacticAnimations);
  currentAnimation$ = this.store.select(EditorSelectors.selectCurrentAnimation);
  isPlaying$ = this.store.select(EditorSelectors.selectIsPlaying);
  currentTime$ = this.store.select(EditorSelectors.selectCurrentTime);

  selectedAnimationId: string | null = null;
  selectedTime = 0;
  totalDuration = 5000; // 5 secondi di default

  private subscription = new Subscription();

  constructor(private store: Store) {}

  ngOnInit(): void {
    // Gestisce la selezione dell'animazione corrente
    this.subscription.add(
      this.currentAnimation$.subscribe(animation => {
        if (animation) {
          this.selectedAnimationId = animation.id;
          this.totalDuration = animation.duration || 5000;
        } else {
          this.selectedAnimationId = null;
        }
      })
    );

    // Aggiorna il tempo selezionato in base alla riproduzione
    this.subscription.add(
      this.currentTime$.subscribe(time => {
        this.selectedTime = time;
      })
    );
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }

  // Crea una nuova animazione
  createAnimation(): void {
    const newAnimation: Animation = {
      id: uuidv4(),
      name: 'Nuova Animazione',
      duration: 5000,
      frames: []
    };

    this.store.dispatch(EditorActions.addAnimation({ animation: newAnimation }));
  }

  // Elimina l'animazione selezionata
  deleteAnimation(): void {
    if (this.selectedAnimationId) {
      this.store.dispatch(EditorActions.deleteAnimation({
        animationId: this.selectedAnimationId
      }));
      this.selectedAnimationId = null;
    }
  }

  // Aggiunge un keyframe all'animazione
  addKeyframe(elementId: string): void {
    if (!this.selectedAnimationId) return;

    const newFrame: AnimationFrame = {
      time: this.selectedTime,
      elementId,
      properties: {
        // Le proprietà saranno determinate dalla posizione dell'elemento in quel momento
        x: 0,
        y: 0
      }
    };

    this.store.dispatch(EditorActions.addAnimationFrame({
      animationId: this.selectedAnimationId,
      frame: newFrame
    }));
  }

  // Rimuove un keyframe dall'animazione
  deleteKeyframe(elementId: string, time: number): void {
    if (!this.selectedAnimationId) return;

    this.store.dispatch(EditorActions.deleteAnimationFrame({
      animationId: this.selectedAnimationId,
      frameTime: time,
      elementId
    }));
  }

  // Seleziona un'animazione
  selectAnimation(animationId: string): void {
    this.selectedAnimationId = animationId;
    this.store.dispatch(EditorActions.setAnimationTime({ time: 0 }));
  }

  // Imposta il tempo corrente

  setTime(event: any): void {
    // Angular Material 14+
    if (event.value !== undefined) {
      this.selectedTime = event.value;
    }
    // Angular Material 15+
    else if (event instanceof Event && event.target) {
      this.selectedTime = (event.target as HTMLInputElement).valueAsNumber;
    }
    let time = this.selectedTime;
    this.store.dispatch(EditorActions.setAnimationTime({ time }));
  }

  formatSliderTime(value: number): string {
    return this.formatTime(value);
  }

  // Formatta il tempo in mm:ss.ms
  formatTime(ms: number): string {
    const seconds = Math.floor(ms / 1000);
    const minutes = Math.floor(seconds / 60);
    const remainingSeconds = seconds % 60;
    const remainingMs = ms % 1000;

    return `${minutes.toString().padStart(2, '0')}:${remainingSeconds.toString().padStart(2, '0')}.${Math.floor(remainingMs / 100)}`;
  }
}