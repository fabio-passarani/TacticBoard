import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ExerciseCatalogComponent } from './exercise-catalog.component';

describe('ExerciseCatalogComponent', () => {
  let component: ExerciseCatalogComponent;
  let fixture: ComponentFixture<ExerciseCatalogComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ExerciseCatalogComponent]
    });
    fixture = TestBed.createComponent(ExerciseCatalogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
