import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AnnotationToolsComponent } from './annotation-tools.component';

describe('AnnotationToolsComponent', () => {
  let component: AnnotationToolsComponent;
  let fixture: ComponentFixture<AnnotationToolsComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [AnnotationToolsComponent]
    });
    fixture = TestBed.createComponent(AnnotationToolsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
