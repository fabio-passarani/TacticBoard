// src/app/modules/editor-tactical/components/element-library/element-library.component.ts
import { Component, OnInit, Output, EventEmitter } from '@angular/core';
import { ElementType } from '../../models/tactic-element.model';

interface ElementOption {
  type: ElementType;
  name: string;
  icon: string;
  description: string;
}

@Component({
  selector: 'app-element-library',
  templateUrl: './element-library.component.html',
  styleUrls: ['./element-library.component.scss']
})
export class ElementLibraryComponent implements OnInit {
  @Output() elementSelected = new EventEmitter<ElementType>();

  elements: ElementOption[] = [
    {
      type: ElementType.PLAYER,
      name: 'Giocatore',
      icon: 'sports_handball',
      description: 'Aggiungi un giocatore al campo'
    },
    {
      type: ElementType.BALL,
      name: 'Pallone',
      icon: 'sports_soccer',
      description: 'Aggiungi un pallone al campo'
    },
    {
      type: ElementType.CONE,
      name: 'Cono',
      icon: 'change_history',
      description: 'Aggiungi un cono da allenamento'
    },
    {
      type: ElementType.ARROW,
      name: 'Freccia',
      icon: 'arrow_right_alt',
      description: 'Aggiungi una freccia per indicare movimento o direzione'
    },
    {
      type: ElementType.LINE,
      name: 'Linea',
      icon: 'remove',
      description: 'Aggiungi una linea per indicare distanze o zone'
    },
    {
      type: ElementType.TEXT,
      name: 'Testo',
      icon: 'text_fields',
      description: 'Aggiungi un testo per note o etichette'
    }
  ];

  categories = [
    {
      name: 'Giocatori',
      types: [ElementType.PLAYER]
    },
    {
      name: 'Oggetti',
      types: [ElementType.BALL, ElementType.CONE]
    },
    {
      name: 'Annotazioni',
      types: [ElementType.ARROW, ElementType.LINE, ElementType.TEXT]
    }
  ];

  constructor() { }

  ngOnInit(): void {
  }

  selectElement(type: ElementType): void {
    this.elementSelected.emit(type);
  }

  getElementsByCategory(category: string): ElementOption[] {
    const categoryObj = this.categories.find(c => c.name === category);
    if (!categoryObj) return [];

    return this.elements.filter(el => categoryObj.types.includes(el.type));
  }
}

// src/app/modules/editor-tactical/components/element-library/element-library.component.html