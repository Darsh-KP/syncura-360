import { TestBed } from '@angular/core/testing';

import { VisitMgmtService } from './visit-mgmt.service';

describe('VisitMgmtService', () => {
  let service: VisitMgmtService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(VisitMgmtService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
