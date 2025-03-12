// src/app/modules/editor-tactical/components/canvas/canvas.component.ts
import { Component, OnInit, ViewChild, ElementRef, AfterViewInit, OnDestroy, Input } from '@angular/core';
import { Store } from '@ngrx/store';
import { fabric } from 'fabric';
import { Subscription } from 'rxjs';
import { take } from 'rxjs/operators';
import * as EditorActions from '../../store/editor.actions';
import * as EditorSelectors from '../../store/editor.selectors';
import { TacticElement, ElementType } from '../../models/tactic-element.model';
import { v4 as uuidv4 } from 'uuid';
import '../../models/fabric-extensions';

@Component({
  selector: 'app-canvas',
  templateUrl: './canvas.component.html',
  styleUrls: ['./canvas.component.scss'],
  host: {'class': 'editor-container-host'}
})
export class CanvasComponent implements OnInit, AfterViewInit, OnDestroy {
  @ViewChild('canvas', { static: true }) canvasRef!: ElementRef<HTMLCanvasElement>;
  @Input() selectedElementType: ElementType | null = null;

  private canvas!: fabric.Canvas;
  private subscription = new Subscription();
  private fabricObjects: Map<string, fabric.Object> = new Map();

  currentTactic$ = this.store.select(EditorSelectors.selectCurrentTactic);
  tacticElements$ = this.store.select(EditorSelectors.selectTacticElements);
  isPlaying$ = this.store.select(EditorSelectors.selectIsPlaying);
  currentTime$ = this.store.select(EditorSelectors.selectCurrentTime);

  // Dimensioni del campo da calcio in pixel
  fieldWidth = 700;
  fieldHeight = 500;

  constructor(private store: Store) {}

  ngOnInit(): void {
    // Carica le tattiche all'inizializzazione
    this.store.dispatch(EditorActions.loadTactics());

    // Controlla se esiste già una tattica corrente, altrimenti carica la prima disponibile
    this.subscription.add(
      this.store.select(EditorSelectors.selectCurrentTacticId).subscribe(id => {
        if (!id) {
          this.store.select(EditorSelectors.selectAllTactics)
            .pipe(take(1))
            .subscribe(tactics => {
              if (tactics.length > 0) {
                this.store.dispatch(EditorActions.setCurrentTactic({ tacticId: tactics[0].id }));
              } else {
                // Crea una nuova tattica se non ne esistono
                this.store.dispatch(EditorActions.createTactic({
                  tactic: {
                    name: 'Nuova Tattica',
                    description: 'Tattica creata automaticamente',
                    elements: [],
                    animations: []
                  }
                }));
              }
            });
        }
      })
    );

    // Sottoscrizione agli elementi della tattica per aggiornarli sul canvas
    this.subscription.add(
      this.tacticElements$.subscribe(elements => {
        if (this.canvas && elements) {
          this.updateCanvasElements(elements);
        }
      })
    );

    // Aggiorna la posizione degli elementi durante la riproduzione dell'animazione
    this.subscription.add(
      this.currentTime$.subscribe(time => {
        // L'aggiornamento in base all'animazione verrà implementato qui
      })
    );
  }

  ngAfterViewInit(): void {
    // Inizializza il canvas Fabric.js
    this.canvas = new fabric.Canvas(this.canvasRef.nativeElement, {
      width: this.fieldWidth,
      height: this.fieldHeight,
      selection: true,
      preserveObjectStacking: true
    });

    // Disegna il campo da calcio
    this.drawSoccerField();

    // Imposta gli handler degli eventi del canvas
    this.setupCanvasEvents();
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
    this.canvas.dispose();
  }

  // Metodo per disegnare il campo da calcio
  private drawSoccerField(): void {
    // Sfondo verde del campo
    const field = new fabric.Rect({
      left: 0,
      top: 0,
      width: this.fieldWidth,
      height: this.fieldHeight,
      fill: '#2E7D32', // Verde scuro
      selectable: false,
      evented: false
    });
    this.canvas.add(field);

    // Bordo del campo (linea bianca)
    const border = new fabric.Rect({
      left: 20,
      top: 20,
      width: this.fieldWidth - 40,
      height: this.fieldHeight - 40,
      fill: 'transparent',
      stroke: 'white',
      strokeWidth: 2,
      selectable: false,
      evented: false
    });
    this.canvas.add(border);

    // Linea di metà campo
    const midLine = new fabric.Line([
      this.fieldWidth / 2, 20,
      this.fieldWidth / 2, this.fieldHeight - 20
    ], {
      stroke: 'white',
      strokeWidth: 2,
      selectable: false,
      evented: false
    });
    this.canvas.add(midLine);

    // Cerchio di metà campo
    const midCircle = new fabric.Circle({
      left: this.fieldWidth / 2 - 40,
      top: this.fieldHeight / 2 - 40,
      radius: 40,
      fill: 'transparent',
      stroke: 'white',
      strokeWidth: 2,
      selectable: false,
      evented: false
    });
    this.canvas.add(midCircle);

    // Area di rigore sinistra
    const leftPenaltyArea = new fabric.Rect({
      left: 20,
      top: (this.fieldHeight - 150) / 2,
      width: 80,
      height: 150,
      fill: 'transparent',
      stroke: 'white',
      strokeWidth: 2,
      selectable: false,
      evented: false
    });
    this.canvas.add(leftPenaltyArea);

    // Area di rigore destra
    const rightPenaltyArea = new fabric.Rect({
      left: this.fieldWidth - 100,
      top: (this.fieldHeight - 150) / 2,
      width: 80,
      height: 150,
      fill: 'transparent',
      stroke: 'white',
      strokeWidth: 2,
      selectable: false,
      evented: false
    });
    this.canvas.add(rightPenaltyArea);

    // Porta sinistra
    const leftGoal = new fabric.Rect({
      left: 10,
      top: (this.fieldHeight - 60) / 2,
      width: 10,
      height: 60,
      fill: 'transparent',
      stroke: 'white',
      strokeWidth: 2,
      selectable: false,
      evented: false
    });
    this.canvas.add(leftGoal);

    // Porta destra
    const rightGoal = new fabric.Rect({
      left: this.fieldWidth - 20,
      top: (this.fieldHeight - 60) / 2,
      width: 10,
      height: 60,
      fill: 'transparent',
      stroke: 'white',
      strokeWidth: 2,
      selectable: false,
      evented: false
    });
    this.canvas.add(rightGoal);

    // Aggiorna il canvas
    this.canvas.renderAll();
  }

  // Metodo per impostare gli handler degli eventi del canvas
  private setupCanvasEvents(): void {
    // Evento di click sul canvas per aggiungere elementi
    this.canvas.on('mouse:down', (options) => {
      if (this.selectedElementType && options.pointer) {
        const { x, y } = options.pointer;
        this.addElementToCanvas(this.selectedElementType, x, y);
        this.selectedElementType = null; // Reset della selezione dopo l'aggiunta
      }
    });

    // Evento di modifica degli oggetti
    this.canvas.on('object:modified', (options) => {
      const fabricObject = options.target;
      if (fabricObject && fabricObject.data) {
        const elementId = fabricObject.data.id;
        const element = this.getElementFromFabricObject(fabricObject);

        if (element) {
          this.store.dispatch(EditorActions.updateElement({ element }));
        }
      }
    });

    // Evento di selezione degli oggetti
    this.canvas.on('selection:created', (options) => {
      // Implementa se necessario
    });

    // Evento di deselezione degli oggetti
    this.canvas.on('selection:cleared', (options) => {
      // Implementa se necessario
    });
  }

  // Metodo per aggiungere un elemento al canvas e allo store
  addElementToCanvas(type: ElementType, x: number, y: number): void {
    const elementId = uuidv4();

    // Crea un oggetto Fabric in base al tipo
    let fabricObject: fabric.Object;

    switch (type) {
      case ElementType.PLAYER:
        fabricObject = this.createPlayer(x, y, { team: 'home', number: 10 });
        break;
      case ElementType.BALL:
        fabricObject = this.createBall(x, y);
        break;
      case ElementType.CONE:
        fabricObject = this.createCone(x, y);
        break;
      case ElementType.ARROW:
        fabricObject = this.createArrow(x, y);
        break;
      case ElementType.LINE:
        fabricObject = this.createLine(x, y);
        break;
      case ElementType.TEXT:
        fabricObject = this.createText(x, y);
        break;
      default:
        return;
    }

    // Aggiungi dati personalizzati all'oggetto Fabric
    fabricObject.data = {
      id: elementId,
      type: type
    };

    // Aggiungi l'oggetto al canvas
    this.canvas.add(fabricObject);
    this.fabricObjects.set(elementId, fabricObject);

    // Crea l'elemento tattico da aggiungere allo store
    const element: TacticElement = {
      id: elementId,
      type: type,
      x: x,
      y: y,
      rotation: 0,
      scaleX: 1,
      scaleY: 1,
      properties: this.getPropertiesFromFabricObject(fabricObject)
    };

    // Dispatcha l'azione per aggiungere l'elemento allo store
    this.store.dispatch(EditorActions.addElement({ element }));
  }

  // Metodo per aggiornare gli elementi sul canvas in base allo stato dello store
  private updateCanvasElements(elements: TacticElement[]): void {
    // Rimuovi gli oggetti non più presenti nello stato
    const currentIds = elements.map(el => el.id);

    this.fabricObjects.forEach((obj, id) => {
      if (!currentIds.includes(id)) {
        this.canvas.remove(obj);
        this.fabricObjects.delete(id);
      }
    });

    // Aggiorna o aggiungi gli oggetti
    elements.forEach(element => {
      const fabricObject = this.fabricObjects.get(element.id);

      if (fabricObject) {
        // Aggiorna l'oggetto esistente
        this.updateFabricObject(fabricObject, element);
      } else {
        // Crea un nuovo oggetto
        this.createFabricObjectFromElement(element);
      }
    });

    // Aggiorna il canvas
    this.canvas.renderAll();
  }

  // Metodo per creare un oggetto Fabric da un elemento tattico
  private createFabricObjectFromElement(element: TacticElement): void {
    let fabricObject: fabric.Object;

    switch (element.type) {
      case ElementType.PLAYER:
        fabricObject = this.createPlayer(
          element.x,
          element.y,
          element.properties
        );
        break;
      case ElementType.BALL:
        fabricObject = this.createBall(element.x, element.y);
        break;
      case ElementType.CONE:
        fabricObject = this.createCone(element.x, element.y);
        break;
      case ElementType.ARROW:
        fabricObject = this.createArrow(element.x, element.y);
        break;
      case ElementType.LINE:
        fabricObject = this.createLine(element.x, element.y);
        break;
      case ElementType.TEXT:
        fabricObject = this.createText(
          element.x,
          element.y,
          element.properties.label
        );
        break;
      default:
        return;
    }

    // Imposta la rotazione e la scala
    fabricObject.set({
      angle: element.rotation,
      scaleX: element.scaleX,
      scaleY: element.scaleY
    });

    // Aggiungi dati personalizzati
    fabricObject.data = {
      id: element.id,
      type: element.type
    };

    // Aggiungi al canvas e alla mappa
    this.canvas.add(fabricObject);
    this.fabricObjects.set(element.id, fabricObject);
  }

  // Metodo per aggiornare un oggetto Fabric esistente
  private updateFabricObject(fabricObject: fabric.Object, element: TacticElement): void {
    fabricObject.set({
      left: element.x,
      top: element.y,
      angle: element.rotation,
      scaleX: element.scaleX,
      scaleY: element.scaleY
    });

    // Aggiorna le proprietà specifiche del tipo
    if (element.type === ElementType.PLAYER) {
      this.updatePlayerObject(fabricObject as fabric.Group, element.properties);
    } else if (element.type === ElementType.TEXT && element.properties.label) {
      (fabricObject as fabric.Text).set({ text: element.properties.label });
    }
  }

  // Crea un giocatore sul campo
  private createPlayer(x: number, y: number, props: any = {}): fabric.Group {
    const teamColor = props.team === 'home' ? '#ff0000' :
                      props.team === 'away' ? '#0000ff' : '#888888';

    // Cerchio del giocatore
    const circle = new fabric.Circle({
      radius: 15,
      fill: teamColor,
      stroke: 'white',
      strokeWidth: 2,
      originX: 'center',
      originY: 'center'
    });

    // Numero del giocatore
    const number = new fabric.Text(props.number?.toString() || '', {
      fontSize: 12,
      fill: 'white',
      fontWeight: 'bold',
      originX: 'center',
      originY: 'center'
    });

    // Gruppo per il giocatore
    const player = new fabric.Group([circle, number], {
      left: x,
      top: y,
      selectable: true,
      hasControls: true,
      hasBorders: true
    });

    return player;
  }

  // Aggiorna un oggetto giocatore esistente
  private updatePlayerObject(player: fabric.Group, props: any): void {
    const circle = player.getObjects()[0] as fabric.Circle;
    const number = player.getObjects()[1] as fabric.Text;

    if (props.team) {
      const teamColor = props.team === 'home' ? '#ff0000' :
                        props.team === 'away' ? '#0000ff' : '#888888';
      circle.set({ fill: teamColor });
    }

    if (props.number !== undefined) {
      number.set({ text: props.number.toString() });
    }
  }

  // Crea una palla
  private createBall(x: number, y: number): fabric.Circle {
    return new fabric.Circle({
      left: x,
      top: y,
      radius: 8,
      fill: 'white',
      stroke: 'black',
      strokeWidth: 1,
      originX: 'center',
      originY: 'center'
    });
  }

  // Crea un cono (per allenamenti)
  private createCone(x: number, y: number): fabric.Triangle {
    return new fabric.Triangle({
      left: x,
      top: y,
      width: 20,
      height: 20,
      fill: 'orange',
      originX: 'center',
      originY: 'center'
    });
  }

  // Crea una freccia
  private createArrow(x: number, y: number): fabric.Path {
    return new fabric.Path('M 0 0 L 50 0 L 45 -5 M 50 0 L 45 5', {
      left: x,
      top: y,
      stroke: 'black',
      strokeWidth: 2,
      fill: '',
      originX: 'left',
      originY: 'center'
    });
  }

  // Crea una linea
  private createLine(x: number, y: number): fabric.Line {
    return new fabric.Line([0, 0, 50, 0], {
      left: x,
      top: y,
      stroke: 'black',
      strokeWidth: 2
    });
  }

  // Crea un testo
  private createText(x: number, y: number, text: string = 'Testo'): fabric.Text {
    return new fabric.Text(text, {
      left: x,
      top: y,
      fontSize: 16,
      fill: 'black',
      backgroundColor: 'rgba(255,255,255,0.7)'
    });
  }

  // Estrai le proprietà da un oggetto Fabric
  private getPropertiesFromFabricObject(obj: fabric.Object): any {
    if (!obj) return {};

    if (obj.type === 'group') { // Player
      const group = obj as fabric.Group;
      const number = (group.getObjects()[1] as fabric.Text).text;
      const circle = group.getObjects()[0] as fabric.Circle;
      const color = circle.fill as string;

      let team = 'neutral';
      if (color === '#ff0000') team = 'home';
      if (color === '#0000ff') team = 'away';

      return {
        number: typeof number === 'string' ? parseInt(number) : 0,
        team,
        color
      };
    } else if (obj.type === 'text') { // Text
      return {
        label: (obj as fabric.Text).text
      };
    }

    return {};
  }

  // Estrai un elemento tattico da un oggetto Fabric
  private getElementFromFabricObject(obj: fabric.Object): TacticElement | null {
    if (!obj || !obj.data) return null;

    return {
      id: obj.data.id,
      type: obj.data.type,
      x: obj.left || 0,
      y: obj.top || 0,
      rotation: obj.angle || 0,
      scaleX: obj.scaleX || 1,
      scaleY: obj.scaleY || 1,
      properties: this.getPropertiesFromFabricObject(obj)
    };
  }

  // Metodi pubblici esposti al template

  // Imposta il tipo di elemento da aggiungere al prossimo click
  selectElement(type: ElementType): void {
    this.selectedElementType = type;
  }

  // Elimina l'elemento selezionato
  deleteSelectedElement(): void {
    const activeObject = this.canvas.getActiveObject();
    if (activeObject && activeObject.data) {
      const elementId = activeObject.data.id;
      this.store.dispatch(EditorActions.deleteElement({ elementId }));
    }
  }

  // Pulisce tutto il canvas (rimuove tutti gli elementi tranne il campo)
  clearCanvas(): void {
    this.tacticElements$.pipe(take(1)).subscribe(elements => {
      elements.forEach(element => {
        this.store.dispatch(EditorActions.deleteElement({ elementId: element.id }));
      });
    });
  }

  // Salva lo stato corrente del canvas
  saveCanvas(): void {
    // In una situazione reale, qui chiameremmo il backend
    console.log('Canvas saved');
  }

  // Esporta l'immagine del canvas
  exportCanvas(): void {
    const dataURL = this.canvas.toDataURL({
      format: 'png',
      quality: 1,
      multiplier: 1  // Aggiunto questo campo richiesto
    });

    const link = document.createElement('a');
    link.download = 'tactical-scheme.png';
    link.href = dataURL;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  }
}