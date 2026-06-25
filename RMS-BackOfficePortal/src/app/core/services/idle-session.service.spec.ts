import { TestBed } from '@angular/core/testing';

import { IdleSessionService } from './idle-session.service';

describe('IdleSessionService', () => {
  let service: IdleSessionService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(IdleSessionService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
