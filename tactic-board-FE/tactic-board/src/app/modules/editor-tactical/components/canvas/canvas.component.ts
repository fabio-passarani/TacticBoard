import { Component, OnInit, ViewChild, ElementRef, AfterViewInit } from '@angular/core';
import { Store } from '@ngrx/store';
import * as EditorActions from '../../store/editor.actions';
import * as EditorSelectors from '../../store/editor.selectors';
import { State } from 'src/app/reducers';

@Component({
  selector: 'app-canvas',
  templateUrl: './canvas.component.html',
  styleUrls: ['./canvas.component.scss']
})
export class CanvasComponent implements OnInit, AfterViewInit {
  @ViewChild('canvas', { static: true }) canvasRef!: ElementRef<HTMLCanvasElement>;
  private ctx!: CanvasRenderingContext2D;

  elements$ = this.store.select(EditorSelectors.selectAllElements);

  constructor(private store: Store<State>) {}

  ngOnInit(): void {
    // Carica gli elementi iniziali
    this.store.dispatch(EditorActions.loadElements());
  }

  ngAfterViewInit(): void {
    const context = this.canvasRef.nativeElement.getContext('2d');
    if (!context) {
      throw new Error('Failed to get 2D context');
    }
    this.ctx = context;
        this.setupCanvas();
  }

  private setupCanvas(): void {
    const canvas = this.canvasRef.nativeElement;
    // Imposta le dimensioni del canvas
    canvas.width = canvas.offsetWidth;
    canvas.height = canvas.offsetHeight;

    // Disegna il campo da calcio
    this.drawField();
  }

  private drawField(): void {
    // Implementa il disegno del campo da calcio
  }

  addElement(type: string, position: { x: number, y: number }): void {
    const element: EditorActions.EditorElement = {
      id: Date.now().toString(),
      type,
      position
    };

    this.store.dispatch(EditorActions.addElement({ element }));
  }
}
