import { TestBed } from '@angular/core/testing';

import { FormFilesService } from './form-files.service';

describe('FormFilesService', () => {
  let service: FormFilesService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(FormFilesService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
