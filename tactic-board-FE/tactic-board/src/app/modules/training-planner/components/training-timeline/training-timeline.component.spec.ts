import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TrainingTimelineComponent } from './training-timeline.component';

describe('TrainingTimelineComponent', () => {
  let component: TrainingTimelineComponent;
  let fixture: ComponentFixture<TrainingTimelineComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [TrainingTimelineComponent]
    });
    fixture = TestBed.createComponent(TrainingTimelineComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
