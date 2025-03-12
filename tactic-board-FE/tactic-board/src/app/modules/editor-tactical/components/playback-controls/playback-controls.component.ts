// src/app/modules/editor-tactical/components/playback-controls/playback-controls.component.ts
import { Component, OnInit, OnDestroy } from '@angular/core';
import { Store } from '@ngrx/store';
import { Subscription, interval } from 'rxjs';
import { takeWhile } from 'rxjs/operators';
import * as EditorSelectors from '../../store/editor.selectors';
import * as EditorActions from '../../store/editor.actions';

@Component({
  selector: 'app-playback-controls',
  templateUrl: './playback-controls.component.html',
  styleUrls: ['./playback-controls.component.scss']
})
export class PlaybackControlsComponent implements OnInit, OnDestroy {
  currentAnimation$ = this.store.select(EditorSelectors.selectCurrentAnimation);
  isPlaying$ = this.store.select(EditorSelectors.selectIsPlaying);
  currentTime$ = this.store.select(EditorSelectors.selectCurrentTime);

  isPlaying = false;
  currentAnimationId: string | null = null;
  currentTime = 0;
  totalDuration = 5000; // Default 5 secondi

  private subscription = new Subscription();
  private playbackSubscription: Subscription | null = null;

  constructor(private store: Store) {}

  ngOnInit(): void {
    // Sottoscrizione all'animazione corrente
    this.subscription.add(
      this.currentAnimation$.subscribe(animation => {
        if (animation) {
          this.currentAnimationId = animation.id;
          this.totalDuration = animation.duration;
        } else {
          this.currentAnimationId = null;
        }
      })
    );

    // Sottoscrizione allo stato di riproduzione
    this.subscription.add(
      this.isPlaying$.subscribe(isPlaying => {
        this.isPlaying = isPlaying;

        if (isPlaying) {
          this.startPlayback();
        } else if (this.playbackSubscription) {
          this.playbackSubscription.unsubscribe();
          this.playbackSubscription = null;
        }
      })
    );

    // Sottoscrizione al tempo corrente
    this.subscription.add(
      this.currentTime$.subscribe(time => {
        this.currentTime = time;
      })
    );
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
    if (this.playbackSubscription) {
      this.playbackSubscription.unsubscribe();
    }
  }

  // Avvia la riproduzione dell'animazione
  play(): void {
    if (!this.currentAnimationId) return;

    this.store.dispatch(EditorActions.playAnimation({
      animationId: this.currentAnimationId
    }));
  }

  // Mette in pausa la riproduzione
  pause(): void {
    this.store.dispatch(EditorActions.pauseAnimation());
  }

  // Ferma completamente la riproduzione e torna all'inizio
  stop(): void {
    this.store.dispatch(EditorActions.stopAnimation());
  }

  // Gestisce l'avvio della riproduzione e l'aggiornamento del tempo
  private startPlayback(): void {
    if (this.playbackSubscription) {
      this.playbackSubscription.unsubscribe();
    }

    // Utilizziamo interval per aggiornare il tempo ogni 100ms
    this.playbackSubscription = interval(100)
      .pipe(
        takeWhile(() => this.isPlaying && this.currentTime < this.totalDuration)
      )
      .subscribe(() => {
        const newTime = Math.min(this.currentTime + 100, this.totalDuration);
        this.store.dispatch(EditorActions.setAnimationTime({ time: newTime }));

        // Se raggiungiamo la fine, fermiamo automaticamente
        if (newTime >= this.totalDuration) {
          this.store.dispatch(EditorActions.stopAnimation());
        }
      });
  }

  // Formatta il tempo in mm:ss.ms
  formatTime(ms: number): string {
    const seconds = Math.floor(ms / 1000);
    const minutes = Math.floor(seconds / 60);
    const remainingSeconds = seconds % 60;
    const remainingMs = ms % 1000;

    return `${minutes.toString().padStart(2, '0')}:${remainingSeconds.toString().padStart(2, '0')}.${Math.floor(remainingMs / 100)}`;
  }

  // Calcola la percentuale di progresso
  get progressPercentage(): number {
    return (this.currentTime / this.totalDuration) * 100;
  }
}

// src/app/modules/editor-tactical/components/playback-controls/playback-controls.component.html