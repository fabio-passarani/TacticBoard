import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ExerciseFiltersComponent } from './exercise-filters.component';

describe('ExerciseFiltersComponent', () => {
  let component: ExerciseFiltersComponent;
  let fixture: ComponentFixture<ExerciseFiltersComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ExerciseFiltersComponent]
    });
    fixture = TestBed.createComponent(ExerciseFiltersComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
