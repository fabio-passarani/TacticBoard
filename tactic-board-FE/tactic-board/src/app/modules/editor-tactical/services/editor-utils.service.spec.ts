import { TestBed } from '@angular/core/testing';

import { EditorUtilsService } from './editor-utils.service';

describe('EditorUtilsService', () => {
  let service: EditorUtilsService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(EditorUtilsService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
